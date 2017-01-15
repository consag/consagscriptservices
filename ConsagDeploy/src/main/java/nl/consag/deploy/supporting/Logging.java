package nl.consag.deploy.supporting;

/**
 * @author Jac. Beekers @ consag consultancy services b.v.
 * @version 20151206.0
 * @since   December 2015
 * 
 * 
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Logging {
	  static private  String logFile = DeployConstants.DEFAULT;
//          private static InformaticaFixtureMessages msg = new InformaticaFixtureMessages();
          static private  String logMsg = DeployConstants.NOT_INITIALIZED;
    static private String logDir =DeployConstants.DEFAULT_LOGDIR;


    /**
     * @param nameLog
     * @param logLevel
     * @param location
     * @param logText
     */
    public static void  LogEntry (String nameLog, String logLevel, String location, String logText) {
        LogEntry(nameLog,DeployConstants.OK,logLevel,location,logText);
	  }
	  
    public static void LogEntry (String nameLog, String methodName, String logLevel, String location, String logText) {
	      getProperties();
              logFile = getLogDir() +"/" + nameLog + ".log";
	      
	      try {
	      FileWriter fLog = new FileWriter( logFile, true);
	      BufferedWriter out = new BufferedWriter(fLog);
	      Date date = new Date();
	      SimpleDateFormat sdf = new SimpleDateFormat(DeployConstants.DATEFORMAT_FOR_LOGENTRY);
	      String formattedDate = sdf.format(date);
//	      try {
//	      logMsg = msg.getMessage(logText, DeployConstants.ENGLISH);
//	      if(DeployConstants.NOT_FOUND.equals(logMsg)) {
	          logMsg=logText;
//	      }
//	       } catch (Exception e) {
//	          out.write("\n" + formattedDate + DeployConstants.LOG_FILE_DELIMITER + nameLog + DeployConstants.LOG_FILE_DELIMITER +
//	                DeployConstants.ERROR + DeployConstants.LOG_FILE_DELIMITER + location + DeployConstants.LOG_FILE_DELIMITER
//	                    +"Error accessing HashMap - error =>" + e.toString() + "<.");
//	      //                    e.printStackTrace();
//	      }
	      out.write("\n" + formattedDate + DeployConstants.LOG_FILE_DELIMITER 
	                + methodName + DeployConstants.LOG_FILE_DELIMITER 
	                + logLevel + DeployConstants.LOG_FILE_DELIMITER 
	                + location + DeployConstants.LOG_FILE_DELIMITER 
	                 +logMsg);
	      out.close();
	      }
	      catch (IOException e) {
	      System.err.println("Error writing to log >" + logFile +"<. Error: " + e.getMessage());
         //     throw(e);
	      }

	  }
          
    private static void getProperties() {
    Properties prop = new Properties();
            InputStream input = null;

            try {
                input =Logging.class.getClassLoader().getResourceAsStream(DeployConstants.CONSAGDEPLOY_PROPERTIES);
                if (input == null) {
                                    setLogDir(DeployConstants.DEFAULT_LOGDIR);
                                }
                else {
                    prop.load(input);
                    setLogDir(prop.getProperty(DeployConstants.KEY_LOGDIR,DeployConstants.DEFAULT_LOGDIR));
                }
            } catch (IOException ex) {
                    ex.toString();
            } finally {
                    if (input != null) {
                            try {
                                    input.close();
                            } catch (IOException e) {
                                   System.err.println("Error reading properies file >" + DeployConstants.CONSAGDEPLOY_PROPERTIES +"<. Error: " + e.toString());
                                setLogDir(DeployConstants.DEFAULT_LOGDIR);
                            }
                    }
            }
    }

    private static void setLogDir(String dir) {
        logDir=dir;
    }

    private static String getLogDir() {
        return logDir;
    }
}
