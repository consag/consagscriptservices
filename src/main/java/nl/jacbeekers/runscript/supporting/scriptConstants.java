package nl.jacbeekers.runscript.supporting;

/**
 * @author Jac. Beekers @ consag consultancy services b.v.
 * @version 20151206.0
 * @since   December 2015
 * 
 * 
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class scriptConstants {

    public static final String YES = "Yes";
        public static final String Y = "Y";
        public static final String NO = "No";
        public static final String N = "N";
        public static final String STOP = "STOP";
        public static final String OK = "OK";
        public static final String TRUE = "true";
        public static final String FALSE ="false";
        public static final String FAILED = "Failed";
        
        public static final String NOT_FOUND = "NOTFOUND";
        public static final String NOT_PROVIDED = "NotProvided";
        public static final String DEFAULT = "default";
        public static final String UNKNOWN = "unknown";
        public static final String NOT_IMPLEMENTED = "Not yet implemented.";
        public static final String NOT_INITIALIZED = "Not initialized";
        public static final String NONE = "None";
        public static final String LATEST = "LATEST";
        public static final String FITCONVERTDT = "FITCONVERTDT";
        public static final String SRC = "SRC";
        public static final String TGT ="TGT";
        public static final String NOPRIMKEY ="NoPrimaryKey";
        public static final String NOT_COMPARED ="Not compared";
        public static final String NO_DIFFERENCES ="No differences";
        public static final String FILTER_INDICATOR="filter";
        public static final String OPERATOR_EQUALS ="=";
        public static final String PARAM_DELIMITER ="Delimiter=";
        
        public static final String INCOMING ="incoming";
        public static final String OUTGOING ="outgoing";
        public static final String TESTDATA ="testdata";
        public static final String DEPLOYMENT="deployment";
        public static final String TEMP ="temp";
        public static final String ENVIRONMENT ="Environment";

        public static final String LOGICAL_BASE_DIR = "base";
        public static final String LOGICAL_SCRIPT_DIR ="scripts";
        
        public static final String NO_ERRORS ="No errors";

        // Delimiters
        public static final String LOG_FILE_DELIMITER = ";";
        public static final String INPUT_FILE_DELIMITER = ":";
        public static final String QUERY_DELIMITER = ",";
        public static final String STATEMENT_DELIMITER = ";";
        public static final String COLUMN_DELIMITER =",";
        public static final String FIELD_DELIMITER =";";
        public static final String APPPROP_DELIMITER =".";
        
        // Location of config files
        public static final String CONFIGDIRECTORY="config/";

        // Logical to physical directory mapping
        public static final String DIRECTORY_PROPERTIES = CONFIGDIRECTORY + "directory.properties";
            // Siebel connectivity
        public static final String SIEBEL_PROPERTIES =CONFIGDIRECTORY + "siebelconnection.properties";
        public static final String FILEOPERATION_PROPERTIES =CONFIGDIRECTORY + "fileoperation.properties";
        public static final String APPLICATION_PROPERTIES =CONFIGDIRECTORY + "application.properties";
        public static final String DATABASE_PROPERTIES =CONFIGDIRECTORY + "database.properties";
        public static final String JDBC_PROPERTIES =CONFIGDIRECTORY + "jdbc.properties";
        public static final String POWERCENTER_PROPERTIES =CONFIGDIRECTORY + "powercenter.properties";
        public static final String APPWSH_PROPERTIES =CONFIGDIRECTORY + "appwsh.properties";
        public static final String WSH_PROPERTIES =CONFIGDIRECTORY + "wsh.properties";
        public static final String DAC_PROPERTIES =CONFIGDIRECTORY + "dac.properties";
        public static final String ENVIRONMENT_PROPERTIES =CONFIGDIRECTORY + "environment.properties";
        public static final String INFA_PROCESS_PROPERTIES =CONFIGDIRECTORY + "infaprocess.properties";
        public static final String RUNSCRIPT_PROPERTIES = "runscript.properties";
        public static final String ACTION_PROPERTIES ="action.properties";
        public static final String DEFAULT_APPLICATION ="IDQ";

                // Log levels
        public static final String VERBOSE = "VERBOSE";
        public static final String DEBUG ="DEBUG";
        public static final String INFO ="INFO";
        public static final String WARNING ="WARNING";
        public static final String ERROR = "ERROR";
        public static final String FATAL ="FATAL";
        public static final List<String> logLevel = Collections.unmodifiableList(Arrays.asList(FATAL,ERROR,WARNING,INFO,DEBUG,VERBOSE));
        
        // default timestamp format used by Excel and Siebel
        public static final String DEFAULT_TIMESTAMP_FORMAT ="yyyy-MM-dd HH:mm:ss";
                
        // Scheduler constants
        public static final String SCHEDULER_PROPERTIES = CONFIGDIRECTORY + "scheduler_client.properties";
        public static final int MISFIRE_INSTRUCTION_FIRE_NOW = 1;
        public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT = 2;
        public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT = 3;
        public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT = 4;
        public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT = 5;
        public static final List<String> QUARTZ_MISFIRE_INSTRUCTIONS = Collections.unmodifiableList(Arrays.asList(
            "0 is unknown"
            ,"1 - MISFIRE_INSTRUCTION_FIRE_NOW"
            ,"2 - MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT"
            ,"3 - MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT"
            ,"4 - MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT"
            ,"5 - MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT"
            ));
        
        // Scheduler Time units
        public static final String SCHEDULER_HOUR = "hour";
        public static final String SCHEDULER_DAY ="day";
        public static final String SCHEDULER_WEEK ="week";
        public static final String SCHEDULER_MONTH ="month";
        public static final String SCHEDULER_YEAR ="year";
        public static final List<String> schedulerUnits = Collections.unmodifiableList(Arrays.asList(SCHEDULER_HOUR
                                                                                                     ,SCHEDULER_DAY
                                                                                                     ,SCHEDULER_WEEK
                                                                                                     ));
        public static final List<Integer> schedulerUnitInHours = Collections.unmodifiableList(Arrays.asList(1
                                                                                                     ,24
                                                                                                     ,7*24
                                                                                                     ));

        // Messages
        public static final String ENGLISH = "ENU";
        public static final String DUTCH = "NLD";
        private static final String[] MESSAGE_LANGUAGES = new String[] {
            scriptConstants.ENGLISH, scriptConstants.DUTCH
        };
        public static final String NOERRORS="No errors encountered";
        
        // Indicators
        public static final String ALL="All";
        public static final String FIRST_ONLY="FirstOnly";
        
        public static final String ARTIFACT_RESPONSE_TYPE ="application/json";
        
        public static final String SCRIPT_EXTENSION_LINUX =".sh";
        public static final String SCRIPT_EXTENSION_WINDOWS =".cmd";
        
        public static final String DATEFORMAT_FOR_LOGFILENAME ="yyyyMMddHHmmss";
        public static final String DATEFORMAT_FOR_LOGENTRY ="yyyy-MM-dd HH:mm:ss";

        /*
         * Keys for the properties file runscript.properties
         */
        public static final String KEY_BASESCRIPTDIRECTORY ="basescriptdirectory";
        public static final String DEFAULT_BASESCRIPTDIR =".";
        public static final String KEY_LOGDIR ="logdirectory";
        public static final String DEFAULT_LOGDIR ="./log";
        public static final String KEY_SCRIPTDIR ="scriptdirectory";
        public static final String DEFAULT_SCRIPTDIR ="scripts";
        public static final String KEY_JETTYLOGURL ="jettylogurl";
        /* Assume Jetty (or any other webserver has contextPath /logs mapped to script log directory
         */
        public static final String DEFAULT_JETTYLOGURL ="http://localhost:8080/logs";
        
        public static final String KEY_DEFAULT_SCRIPT_TIMEOUT="timeout";
        public static final int DEFAULT_TIMEOUT_SCRIPT_MINUTES=5;

        public static final String INFA_DEFAULT_TARGET_ENVIRONMENT ="DEV";
        public static final String INFA_DEFAULT_TARGET_CONNECTION ="conn";
        
        public static final String TMPFILE_PREFIX="consag.";
        public static final String TMPFILE_SUFFIX=".tmp";
    
}
