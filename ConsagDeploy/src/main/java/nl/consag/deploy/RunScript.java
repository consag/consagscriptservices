package nl.consag.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.concurrent.TimeUnit;

import nl.consag.deploy.supporting.DeployConstants;
import nl.consag.deploy.supporting.DeploySupport;
import nl.consag.deploy.supporting.Logging;

/**
 * @author Jac. Beekers @ consag consultancy services b.v.
 * @version 20160828.0
 * @since   December 2015
 *
 * 20160827.0 : Introduced logURL
 * 
 */
public class RunScript {
    private String version = "20160828.0";

    private String className = "RunScript";
    private String logFileName = DeployConstants.NOT_INITIALIZED;
    private String context = DeployConstants.DEFAULT;
    private String startDate = DeployConstants.NOT_INITIALIZED;
    private int logLevel = 5;
    private int logEntries = 0;

    private static String cmdName = DeployConstants.NOT_INITIALIZED;
    private String baseDir = DeployConstants.DEFAULT_BASEDEPLOYDIR;
    private String deployScriptDir = DeployConstants.DEFAULT_DEPLOYSCRIPTDIR;
    private String jettylogurl = DeployConstants.DEFAULT_JETTYLOGURL;

    private String resultCode = DeployConstants.NOT_INITIALIZED;
    private String resultMessage = DeployConstants.OK;
    private String errorMessage = DeployConstants.NOERRORS;

    private ArrayList<String> providedArgs = new ArrayList<>();
    private String sTimeout = DeployConstants.NOT_FOUND;
    int timeout=0;


    RunScript(String scriptName) {
        cmdName = scriptName;
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DeployConstants.DATEFORMAT_FOR_LOGFILENAME);
        this.startDate = sdf.format(started);
        logFileName = startDate + "." + className + "." + scriptName;
    }

    RunScript(String scriptName, ArrayList<String> providedArgs) {
        cmdName = scriptName;
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DeployConstants.DATEFORMAT_FOR_LOGFILENAME);
        this.startDate = sdf.format(started);
        logFileName = startDate + "." + className + "." + scriptName;
        this.providedArgs = providedArgs;
    }

    public void run() {
        String myName = "run";
        String myArea = "init";
        String logMessage = DeployConstants.NOT_INITIALIZED;
        String rc =DeployConstants.OK;

        String s = null;

        log(myName, DeployConstants.DEBUG, myArea, "Running command >" + deployScriptDir +"/" + cmdName + "<...");

        myArea = "ProcessBuilder";
        ProcessBuilder pb = new ProcessBuilder();
        String completeCommand =deployScriptDir +"/" +cmdName;
        log(myName, DeployConstants.DEBUG, myArea, "Running command >" + completeCommand + "<...");
        ArrayList<String> values = new ArrayList<>();
        values.add(completeCommand);
        if (providedArgs.size() > 0) {
            values.addAll(1, providedArgs);
        }
        pb.command(values);

//        Map<String, String> env = pb.environment();
        //             env.put("VAR1", "myValue");
        //             env.remove("OTHERVAR");
        //             env.put("VAR2", env.get("VAR1") + "suffix");
        pb.directory(new File(baseDir));
        try {
            myArea="Process";
            log(myName, DeployConstants.DEBUG, myArea, "Process is ProcessBuilder start...");
            File tmpFile = File.createTempFile(DeployConstants.TMPFILE_PREFIX, DeployConstants.TMPFILE_SUFFIX);
            tmpFile.deleteOnExit();
            
            pb.redirectErrorStream(true);
            pb.redirectOutput(tmpFile);
            
            Process p = pb.start();
            log(myName, DeployConstants.DEBUG, myArea, "Process instantiated with ProcessBuilder start.");

            BufferedReader stdInput = new BufferedReader(new FileReader(tmpFile));
        //   BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command

            myArea="try";
            log(myName, DeployConstants.DEBUG, myArea, "Entering wait mode...");
            //            p.waitFor();
            rc =waitForOrKill(p,TimeUnit.MINUTES.toMillis(timeout));
            log(myName, DeployConstants.DEBUG, myArea, "waitForOrKill returned >" +rc +"<.");
            if(DeployConstants.STOP.equals(rc)) {
                setError("CNSG-RNSC-0004", "CNSG-RNSC-0004: Timeout occurred running script >" +cmdName +"<.");
                log(myName, DeployConstants.ERROR, myArea, getErrorMessage());
                return;
            } 
            myArea = "script output";
            logMessage = "Here is the output of the command:";
            log(myName, DeployConstants.INFO, myArea, logMessage);
            String errMessage=DeployConstants.NOERRORS;
            
            while ((s = stdInput.readLine()) != null) {
                if(s.contains(DeployConstants.ERROR) || s.contains(DeployConstants.FATAL)) {
                    errMessage=s;
                } else {
                    if(s.contains(DeployConstants.WARNING) && DeployConstants.NOERRORS.equals(errMessage)) {
                        errMessage=s;
                        }
                    }
                log(myName, DeployConstants.INFO, myArea, s);
            }

            if (p.exitValue() == 0) {
                log(myName, DeployConstants.DEBUG, myArea, "Process completed successfully.");
                setResult(DeployConstants.OK, DeployConstants.NOERRORS);
            } else {
                log(myName, DeployConstants.ERROR, myArea, "Process completed with an error =>" +p.exitValue() +"<.");
                setError("CNSG-RNSC-0003",
                     "CNSG-RNSC-0003: Script encountered error >" + p.exitValue() +
                     "<. Last error message from script >" + errMessage + "<.");
            }


            // read any errors from the attempted command
/*            myArea = "script error output";
            logMessage = "Here is the standard error of the command (if any):";
            log(myName, DeployConstants.INFO, myArea, logMessage);
            while ((s = stdError.readLine()) != null) {
                logMessage = s;
                log(myName, DeployConstants.INFO, myArea, logMessage);
            }
  */          
            log(myName, DeployConstants.DEBUG, myArea, "Try completed.");
        } 
        catch (IOException e) {
            log(myName, DeployConstants.ERROR, myArea, "IO Exception: " +e.toString());
            setError("CNSG-RNSC-0001", "CNSG-RNSC-0001: " + e.toString());
            //           System.out.println("CNSG-RNSC-0001" +"-"+ "Process error: " +e.toString());
        } 
       /* catch (InterruptedException e) {
            myArea = "Exception handling";
            setError("CNSG-RNSC-0005", "CNSF-RNSC-0005: Script got interrupted: " + e.toString());
            log(myName, DeployConstants.ERROR, myArea, "Process got interrupted: " + e.toString());
        }
*/
        myArea = "Finalizing";
        log(myName, DeployConstants.DEBUG, myArea, "Command >" + cmdName + "< exiting.");

    }

    public void start() {
        String myName = "start";
        String myArea = "init";
        log(myName, DeployConstants.DEBUG, myArea, "Starting command >" + cmdName + "<.");

        log(myName, DeployConstants.DEBUG, myArea, "Getting properties...");
        getProperties();
        log(myName, DeployConstants.DEBUG, myArea, "Properties done.");
        myArea="run";
        log(myName, DeployConstants.DEBUG, myArea, "Calling run...");
        this.run();
        log(myName, DeployConstants.DEBUG, myArea, "After run. Start method completed.");

    }

    private void getProperties() {
        String myName="getProperties";
        String myArea="init";

        DeploySupport depSupport = new DeploySupport();
        baseDir = depSupport.getPropertyValue(DeployConstants.KEY_BASEDEPLOYDIR);
        if (DeployConstants.NOT_FOUND.equals(baseDir))
            baseDir = DeployConstants.DEFAULT_BASEDEPLOYDIR;
        deployScriptDir = depSupport.getPropertyValue(DeployConstants.KEY_DEPLOYSCRIPTDIR);
        if (DeployConstants.NOT_FOUND.equals(deployScriptDir))
            deployScriptDir = DeployConstants.DEFAULT_DEPLOYSCRIPTDIR;
        if (!deployScriptDir.startsWith("/"))
            deployScriptDir = baseDir + "/" + deployScriptDir;
        sTimeout =depSupport.getPropertyValue(DeployConstants.KEY_DEFAULT_SCRIPT_TIMEOUT);
        if(sTimeout.startsWith(DeployConstants.NOT_FOUND)) {
            log(myName, DeployConstants.INFO, myArea, "Property >" +DeployConstants.KEY_DEFAULT_SCRIPT_TIMEOUT + "< not found. Using default (" +Integer.toString(DeployConstants.DEFAULT_TIMEOUT_SCRIPT_MINUTES));
            timeout = DeployConstants.DEFAULT_TIMEOUT_SCRIPT_MINUTES;
        }
        else {
            timeout =Integer.parseInt(sTimeout);
        }

        jettylogurl = depSupport.getPropertyValue(DeployConstants.KEY_JETTYLOGURL);
        if (DeployConstants.NOT_FOUND.equals(jettylogurl))
            jettylogurl = DeployConstants.DEFAULT_JETTYLOGURL;

    }

    private void setResultCode(int i) {
        if (i == 0) {
            resultCode = DeployConstants.OK;
        } else {
            resultCode = DeployConstants.ERROR;
        }
    }

    private void setError(String errCode, String errMsg) {
        setResultCode(errCode);
        setErrorMessage(errMsg);
        setResultMessage(DeployConstants.ERROR);
    }

    private void setResult(String code, String msg) {
        setResultCode(code);
        setResultMessage(msg);
    }

    private void setResultCode(String errCode) {
        resultCode = errCode;
    }

    /**
     * @return Script result code
     */
    public String getResultCode() {
        return resultCode;
    }

    private void setErrorMessage(String errMsg) {
        errorMessage = errMsg;
    }

    /**
     * @return Script error message (if any). Defaults to DeployConstants.NOERRORS (No errors encountered)
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    private void setResultMessage(String msg) {
        resultMessage = msg;
    }

    /**
     * @return Script result message
     */
    public String getResultMessage() {
        return resultMessage;
    }

    private void log(String name, String level, String location, String logText) {
        if (DeployConstants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }
        logEntries++;
            if (logEntries == 1) {
                Logging.LogEntry(logFileName, className, DeployConstants.INFO, "Version info",
                                 "Version: " + getVersion() + " hashCode: " + Integer.toString(hashCode()));
            }
            Logging.LogEntry(logFileName, name, level, location, logText);
     }

    /**
     * @return
     */
    public String getLogFileName() {
        return logFileName + ".log";
    }
    public String getLogURL() {
        return jettylogurl + "/" + getLogFileName();
    }

    /**
     * @param level
     */
    public void setLogLevel(String level) {
        String myName = "setLogLevel";
        String myArea = "determineLevel";

        logLevel = DeployConstants.logLevel.indexOf(level.toUpperCase());
        if (logLevel < 0) {
            log(myName, DeployConstants.WARNING, myArea,
                "Wrong log level >" + level + "< specified. Defaulting to level 5.");
            logLevel = 5;
        }

        log(myName, DeployConstants.INFO, myArea,
            "Log level has been set to >" + level + "< which is level >" + getIntLogLevel() + "<.");
    }

    /**
     * @return
     */
    public String getLogLevel() {
        return DeployConstants.logLevel.get(getIntLogLevel());
    }

    /**
     * @return
     */
    public Integer getIntLogLevel() {
        return logLevel;
    }

    /*
    * @since 20151204.0
    *
    */
    public String getVersion() {
        return version;
    }

    public static String waitForOrKill(Process self, long numberOfMillis) {
        ProcessRunner runnable =new ProcessRunner(self);
        Thread thread =new Thread(runnable);
        thread.start();
        return runnable.waitForOrKill(numberOfMillis);
    }

    protected static class ProcessRunner implements Runnable {
        //Taken from
        //http://stackoverflow.com/questions/1247390/java-native-process-timeout
        //Note: not using Java8 yet
        private static final String className="ProcessRunner";
        Process process;
        private boolean finished =false;
        private static  String logFileName="ProcessRunner.log";
        
        public ProcessRunner(Process process) {
            this.process =process;
            java.util.Date started = new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat(DeployConstants.DATEFORMAT_FOR_LOGFILENAME);
            logFileName = sdf.format(started) + "." + className;
        }
        public void run() {
            String myName="ProcessRunner.run";
            String myArea="run";
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // Ignore
                myArea="interrupt";
                log(logFileName, myName, DeployConstants.DEBUG, myArea, "Interrupt after waitFor");

            }
            synchronized (this) {
                notifyAll();
                finished =true;
            }
        }
        public synchronized String waitForOrKill(long millis) {
            String myName="waitForOrKill";
            String myArea="running";
            String rc=DeployConstants.OK;
            
            if(!finished) {
                try {
                    wait(millis);
                } catch (InterruptedException e) {
                    myArea="interrupt";
                    log(logFileName, myName, DeployConstants.DEBUG, myArea, "Interrupt exception wait: " +e.toString());
                }
                if (finished) {
                    log(logFileName, myName, DeployConstants.DEBUG, myArea, "Script >" +cmdName +"< completed within >" + millis/1000/60 + "< minutes.");
                } else {
                    log(logFileName, myName, DeployConstants.DEBUG, myArea, "Timeout occurred. Will destroy " + cmdName +"<.");
                    process.destroy();
                    rc=DeployConstants.STOP;
                }
            }
            return rc;
        }

        private void log(String file, String location, String level, String area, String msg) {
            
                Logging.LogEntry(file, location, level, area, msg);
            
        }
    }
}
