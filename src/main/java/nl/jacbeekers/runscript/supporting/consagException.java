package nl.jacbeekers.runscript.supporting;

public class consagException extends Throwable{
    private String location = scriptConstants.UNKNOWN;
    private String errCode = scriptConstants.UNKNOWN;
    private String errMsg = scriptConstants.UNKNOWN;

    public consagException(String location, String errCode, String errMsg) {
        this.location =location;
        this.errCode =errCode;
        this.errMsg = errMsg;
        Logging.LogEntry("exceptions.log", scriptConstants.FATAL, location, errCode +" - " + errMsg);
        System.err.println();

    }
    
    public String toString() {
        return location + ": " + errCode + " - " + errMsg;
    }
}
