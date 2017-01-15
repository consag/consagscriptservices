package nl.consag.deploy.supporting;

import nl.consag.deploy.supporting.DeployConstants;
import nl.consag.deploy.supporting.Logging;

public class consagException extends Throwable{
    @SuppressWarnings("compatibility:-158058141262712740")
    private static final long serialVersionUID = 1L;
    private String location =DeployConstants.UNKNOWN;
    private String errCode =DeployConstants.UNKNOWN;
    private String errMsg =DeployConstants.UNKNOWN;

    public consagException(String location, String errCode, String errMsg) {
        this.location =location;
        this.errCode =errCode;
        this.errMsg = errMsg;
        Logging.LogEntry("exceptions.log", DeployConstants.FATAL, location, errCode +" - " + errMsg);
        System.err.println();

    }
    
    public String toString() {
        return location + ": " + errCode + " - " + errMsg;
    }
}
