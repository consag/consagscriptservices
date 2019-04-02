package nl.jacbeekers.runscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.concurrent.TimeUnit;

import nl.jacbeekers.runscript.supporting.scriptSupport;
import nl.jacbeekers.runscript.supporting.scriptConstants;
import nl.jacbeekers.runscript.supporting.Logging;

/**
 * @author Jac. Beekers @ consag consultancy services b.v.
 * @version 20190401.0
 * @since   December 2015
 *
 * 20160827.0 : Introduced logURL
 * 
 */
public class RunScript {
    private String version = "20190401.0";

    private String className = "RunScript";
    private String logFileName = scriptConstants.NOT_INITIALIZED;
//    private String context = scriptConstants.DEFAULT;
    private String startDate = scriptConstants.NOT_INITIALIZED;
    private int logLevel = 5;
    private int logEntries = 0;

    private static String cmdName = scriptConstants.NOT_INITIALIZED;
    private String baseDir = scriptConstants.DEFAULT_BASESCRIPTDIR;
    private String deployScriptDir = scriptConstants.DEFAULT_SCRIPTDIR;
    private String jettylogurl = scriptConstants.DEFAULT_JETTYLOGURL;

    private String resultCode = scriptConstants.NOT_INITIALIZED;
    private String resultMessage = scriptConstants.OK;
    private String errorMessage = scriptConstants.NOERRORS;

    private ArrayList<String> providedArgs = new ArrayList<>();
    private String sTimeout = scriptConstants.NOT_FOUND;
    int timeout=0;


    RunScript(String scriptName) {
        cmdName = scriptName;
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat(scriptConstants.DATEFORMAT_FOR_LOGFILENAME);
        this.startDate = sdf.format(started);
        logFileName = startDate + "." + className + "." + scriptName;
    }

    RunScript(String scriptName, ArrayList<String> providedArgs) {
        cmdName = scriptName;
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat(scriptConstants.DATEFORMAT_FOR_LOGFILENAME);
        this.startDate = sdf.format(started);
        logFileName = startDate + "." + className + "." + scriptName;
        this.providedArgs = providedArgs;
    }

    public void run() {
        String myName = "run";
        String myArea = "init";
        String logMessage = scriptConstants.NOT_INITIALIZED;
        String rc = scriptConstants.OK;

        String s = null;

        log(myName, scriptConstants.DEBUG, myArea, "Running command >" + deployScriptDir +"/" + cmdName + "<...");

        myArea = "ProcessBuilder";
        ProcessBuilder pb = new ProcessBuilder();
        String completeCommand =deployScriptDir +"/" +cmdName;
        log(myName, scriptConstants.DEBUG, myArea, "Running command >" + completeCommand + "<...");
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
            log(myName, scriptConstants.DEBUG, myArea, "Process is ProcessBuilder start...");
            File tmpFile = File.createTempFile(scriptConstants.TMPFILE_PREFIX, scriptConstants.TMPFILE_SUFFIX);
            tmpFile.deleteOnExit();
            
            pb.redirectErrorStream(true);
            pb.redirectOutput(tmpFile);
            
            Process p = pb.start();
            log(myName, scriptConstants.DEBUG, myArea, "Process instantiated with ProcessBuilder start.");

            BufferedReader stdInput = new BufferedReader(new FileReader(tmpFile));
        //   BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command

            myArea="try";
            log(myName, scriptConstants.DEBUG, myArea, "Entering wait mode...");
            //            p.waitFor();
            rc =waitForOrKill(p,TimeUnit.MINUTES.toMillis(timeout));
            log(myName, scriptConstants.DEBUG, myArea, "waitForOrKill returned >" +rc +"<.");
            if(scriptConstants.STOP.equals(rc)) {
                setError("CNSG-RNSC-0004", "CNSG-RNSC-0004: Timeout occurred running script >" +cmdName +"<.");
                log(myName, scriptConstants.ERROR, myArea, getErrorMessage());
                return;
            } 
            myArea = "script output";
            logMessage = "Here is the output of the command:";
            log(myName, scriptConstants.INFO, myArea, logMessage);
            String errMessage= scriptConstants.NOERRORS;
            
            while ((s = stdInput.readLine()) != null) {
                if(s.contains(scriptConstants.ERROR) || s.contains(scriptConstants.FATAL)) {
                    errMessage=s;
                } else {
                    if(s.contains(scriptConstants.WARNING) && scriptConstants.NOERRORS.equals(errMessage)) {
                        errMessage=s;
                        }
                    }
                log(myName, scriptConstants.INFO, myArea, s);
            }

            if (p.exitValue() == 0) {
                log(myName, scriptConstants.DEBUG, myArea, "Process completed successfully.");
                setResult(scriptConstants.OK, scriptConstants.NOERRORS);
            } else {
                log(myName, scriptConstants.ERROR, myArea, "Process completed with an error =>" +p.exitValue() +"<.");
                setError("CNSG-RNSC-0003",
                     "CNSG-RNSC-0003: Script encountered error >" + p.exitValue() +
                     "<. Last error message from script >" + errMessage + "<.");
            }


            // read any errors from the attempted command
/*            myArea = "script error output";
            logMessage = "Here is the standard error of the command (if any):";
            log(myName, scriptConstants.INFO, myArea, logMessage);
            while ((s = stdError.readLine()) != null) {
                logMessage = s;
                log(myName, scriptConstants.INFO, myArea, logMessage);
            }
  */          
            log(myName, scriptConstants.DEBUG, myArea, "Try completed.");
        } 
        catch (IOException e) {
            log(myName, scriptConstants.ERROR, myArea, "IO Exception: " +e.toString());
            setError("CNSG-RNSC-0001", "CNSG-RNSC-0001: " + e.toString());
            //           System.out.println("CNSG-RNSC-0001" +"-"+ "Process error: " +e.toString());
        } 
       /* catch (InterruptedException e) {
            myArea = "Exception handling";
            setError("CNSG-RNSC-0005", "CNSF-RNSC-0005: Script got interrupted: " + e.toString());
            log(myName, scriptConstants.ERROR, myArea, "Process got interrupted: " + e.toString());
        }
*/
        myArea = "Finalizing";
        log(myName, scriptConstants.DEBUG, myArea, "Command >" + cmdName + "< exiting.");

    }

    public void start() {
        String myName = "start";
        String myArea = "init";
        log(myName, scriptConstants.DEBUG, myArea, "Starting command >" + cmdName + "<.");

        log(myName, scriptConstants.DEBUG, myArea, "Getting properties...");
        getProperties();
        log(myName, scriptConstants.DEBUG, myArea, "Properties done.");
        myArea="run";
        log(myName, scriptConstants.DEBUG, myArea, "Calling run...");
        this.run();
        log(myName, scriptConstants.DEBUG, myArea, "After run. Start method completed.");

    }

    private void getProperties() {
        String myName="getProperties";
        String myArea="init";

        log(myName, scriptConstants.INFO, myArea, "current directory is >" + System.getProperty("user.dir") +"<.");
        scriptSupport scriptSupport = new scriptSupport();
        baseDir = scriptSupport.getPropertyValue(scriptConstants.KEY_BASESCRIPTDIRECTORY);
        if (scriptConstants.NOT_FOUND.equals(baseDir))
            log(myName, scriptConstants.INFO, myArea, "Property >" + scriptConstants.KEY_BASESCRIPTDIRECTORY + "< not found. Using default (" +scriptConstants.DEFAULT_BASESCRIPTDIR +").");
            baseDir = scriptConstants.DEFAULT_BASESCRIPTDIR;
        deployScriptDir = scriptSupport.getPropertyValue(scriptConstants.KEY_SCRIPTDIR);
        if (scriptConstants.NOT_FOUND.equals(deployScriptDir))
            log(myName, scriptConstants.INFO, myArea, "Property >" + scriptConstants.KEY_SCRIPTDIR + "< not found. Using default (" +scriptConstants.DEFAULT_SCRIPTDIR +").");
            deployScriptDir = scriptConstants.DEFAULT_SCRIPTDIR;
        if (!deployScriptDir.startsWith("/"))
            deployScriptDir = baseDir + "/" + deployScriptDir;
        sTimeout =scriptSupport.getPropertyValue(scriptConstants.KEY_DEFAULT_SCRIPT_TIMEOUT);
        if(sTimeout.startsWith(scriptConstants.NOT_FOUND)) {
            log(myName, scriptConstants.INFO, myArea, "Property >" + scriptConstants.KEY_DEFAULT_SCRIPT_TIMEOUT + "< not found. Using default (" +Integer.toString(scriptConstants.DEFAULT_TIMEOUT_SCRIPT_MINUTES) +").");
            timeout = scriptConstants.DEFAULT_TIMEOUT_SCRIPT_MINUTES;
        }
        else {
            timeout =Integer.parseInt(sTimeout);
        }

        jettylogurl = scriptSupport.getPropertyValue(scriptConstants.KEY_JETTYLOGURL);
        if (scriptConstants.NOT_FOUND.equals(jettylogurl))
            log(myName, scriptConstants.INFO, myArea, "Property >" + scriptConstants.KEY_JETTYLOGURL + "< not found. Using default (" +scriptConstants.DEFAULT_JETTYLOGURL +").");
            jettylogurl = scriptConstants.DEFAULT_JETTYLOGURL;

    }

    private void setResultCode(int i) {
        if (i == 0) {
            resultCode = scriptConstants.OK;
        } else {
            resultCode = scriptConstants.ERROR;
        }
    }

    private void setError(String errCode, String errMsg) {
        setResultCode(errCode);
        setErrorMessage(errMsg);
        setResultMessage(scriptConstants.ERROR);
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
     * @return Script error message (if any). Defaults to scriptConstants.NOERRORS (No errors encountered)
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
        if (scriptConstants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }
        logEntries++;
            if (logEntries == 1) {
                Logging.LogEntry(logFileName, className, scriptConstants.INFO, "Version info",
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

        logLevel = scriptConstants.logLevel.indexOf(level.toUpperCase());
        if (logLevel < 0) {
            log(myName, scriptConstants.WARNING, myArea,
                "Wrong log level >" + level + "< specified. Defaulting to level 5.");
            logLevel = 5;
        }

        log(myName, scriptConstants.INFO, myArea,
            "Log level has been set to >" + level + "< which is level >" + getIntLogLevel() + "<.");
    }

    /**
     * @return
     */
    public String getLogLevel() {
        return scriptConstants.logLevel.get(getIntLogLevel());
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
            SimpleDateFormat sdf = new SimpleDateFormat(scriptConstants.DATEFORMAT_FOR_LOGFILENAME);
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
                log(logFileName, myName, scriptConstants.DEBUG, myArea, "Interrupt after waitFor");

            }
            synchronized (this) {
                notifyAll();
                finished =true;
            }
        }
        public synchronized String waitForOrKill(long millis) {
            String myName="waitForOrKill";
            String myArea="running";
            String rc= scriptConstants.OK;
            
            if(!finished) {
                try {
                    wait(millis);
                } catch (InterruptedException e) {
                    myArea="interrupt";
                    log(logFileName, myName, scriptConstants.DEBUG, myArea, "Interrupt exception wait: " +e.toString());
                }
                if (finished) {
                    log(logFileName, myName, scriptConstants.DEBUG, myArea, "Script >" +cmdName +"< completed within >" + millis/1000/60 + "< minutes.");
                } else {
                    log(logFileName, myName, scriptConstants.DEBUG, myArea, "Timeout occurred. Will destroy " + cmdName +"<.");
                    process.destroy();
                    rc= scriptConstants.STOP;
                }
            }
            return rc;
        }

        private void log(String file, String location, String level, String area, String msg) {
            
                Logging.LogEntry(file, location, level, area, msg);
            
        }
    }
}
