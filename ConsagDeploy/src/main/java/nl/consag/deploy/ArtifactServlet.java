package nl.consag.deploy;
/**
 * @author Jac. Beekers @ consag consultancy services b.v.
 * @version 20160827.0
 * @since   December 2015
 *
 * 20160827.0 : Introduced logURL
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.*;
import javax.servlet.http.*;


import nl.consag.deploy.supporting.DeployConstants;

import nl.consag.deploy.supporting.consagException;

import org.json.JSONException;
import org.json.JSONObject;

public class ArtifactServlet extends HttpServlet {
    
    @SuppressWarnings("compatibility:-9073628297096402944")
    private static final long serialVersionUID = 3459314780608223535L;
    private String logFileName = DeployConstants.NOT_INITIALIZED;
    private String artifactType = DeployConstants.NOT_INITIALIZED;
    private String action = DeployConstants.NOT_INITIALIZED;
    private String resultCode =DeployConstants.OK;
    private String resultMessage =DeployConstants.NOT_INITIALIZED;
    private String errorMessage =DeployConstants.NOERRORS;
    
    public static final String version ="20160827.0";
    private static int logLevel=3;
    private String application =DeployConstants.DEFAULT_APPLICATION;
    private String logURL = DeployConstants.NOT_INITIALIZED;


    /**
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**Process the HTTP doGet request.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Type of artifact

        String osName =System.getProperty("os.name");
        String scriptExtension =DeployConstants.NOT_FOUND;
        Map keyVals=null;
                            
        try {
            setApplication(request.getParameter("application"));
            setArtifactType(request.getParameter("type"));
            setAction(request.getParameter("action"));
        } catch (Exception e) {
            log(e.toString());
            setError("CNSG-DPLY-ERROR-0004","Exception occurred: " +e.toString());
            try {
                writeResponse(response);
            } catch (consagException c) {
                throw new IOException(c.toString());
            }
            return;
        }
        
        if(osName.startsWith("Windows")) {
            scriptExtension =DeployConstants.SCRIPT_EXTENSION_WINDOWS;
        } else {
            scriptExtension =DeployConstants.SCRIPT_EXTENSION_LINUX;
        }
        
        String scriptName=DeployConstants.NOT_INITIALIZED;
        
        List<String> allowed =getAllowedActions(getApplication());
        if(!DeployConstants.NOT_FOUND.equals(allowed.get(0))) {
         if(!allowed.contains(getAction())) {
            String errCode="CNSG-DPLY-ERROR-0001";
            String err="Invalid type-action combination >" + getArtifactType() +"-" + getAction() + "< for application >" + getApplication() +"<.";
            setError(errCode,err);
            try {
                writeResponse(response);
            } catch (consagException c) {
                throw new IOException(c.toString());
            }
         }
        }
        
        scriptName=getArtifactType() +getAction() + scriptExtension;
        
        ArrayList<String> params = new ArrayList<>();
        keyVals =request.getParameterMap();
        for(Object key: keyVals.keySet()) {
            params.add("-" +key.toString());
            String[] val =(String[])keyVals.get(key);   
            params.add(val[0]);
        }
        
        RunScript rs = new RunScript (scriptName, params);
        rs.start();
        setLogFileName(rs.getLogFileName());
        setLogURL(rs.getLogURL());

        if(DeployConstants.OK.equals(rs.getResultCode())) {
            setResult(DeployConstants.OK, rs.getResultMessage());
        } else {
            setError("CNSG-DPLY-ERROR-0003","Script failed: " +rs.getErrorMessage()
                     +". Log file: " + getLogFileName());
        }
        rs=null;

        try {
            writeResponse(response);
        } catch (consagException c) {
            throw new IOException(c.toString());
        }

    }

    private List<String> getAllowedActions(String app) {

        String allowedActions =DeployConstants.NOT_FOUND;
        List<String> listedActions = new ArrayList<String>();
        
        try {
        File file = new File(DeployConstants.ACTION_PROPERTIES);
        FileInputStream fileInput = new FileInputStream(file);
        Properties actionProp = new Properties();
        actionProp.load(fileInput);
        fileInput.close();
        
        allowedActions = actionProp.getProperty(app+".ALLOWEDACTIONS", DeployConstants.NOT_FOUND);
        } catch (FileNotFoundException e) {
            allowedActions = DeployConstants.NOT_FOUND;
            } catch (IOException e) {
                allowedActions = DeployConstants.NOT_FOUND;
            }
        
        listedActions = Arrays.asList(allowedActions.split(","));
        
        return listedActions;

    }

    private void setArtifactType(String type) {
        if(type == null) 
            this.artifactType =DeployConstants.NOT_PROVIDED;
        else
            this.artifactType =type;
    }
    private void setApplication(String app) {
        if(app == null)
            this.application =DeployConstants.DEFAULT_APPLICATION;
        else
            this.application =app;
    }
    
    private String getApplication() {
        return this.application;
    }

    /**
     * @return artifactType
     */
    public String getArtifactType() {
        return artifactType;
    }

    private void setAction(String action) {
        if(action == null) 
            this.action=DeployConstants.NOT_PROVIDED; 
        else
            this.action =action;
    }

    /**
     * @return action
     */
    public String getAction() {
        return action;
    }

    private void setResultCode(String code) {
        resultCode =code;
    }
    private String getResultCode() {
        return resultCode;
    }

    private void setResultMessage(String msg) {
        resultMessage =msg;
    }
    private String getResultMessage() {
        return resultMessage;
    }
    private void setErrorMessage(String err) {
        errorMessage =err;
    }

    /**
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    private void writeResponse(HttpServletResponse response) throws consagException {
        String myName="writeResponse";
        
        response.setContentType(DeployConstants.ARTIFACT_RESPONSE_TYPE);
        response.addHeader("Content-Type", DeployConstants.ARTIFACT_RESPONSE_TYPE);
        PrintWriter out;
        try {
            out = response.getWriter();
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("application", getApplication());
            jsonResponse.put("type", getArtifactType());
            jsonResponse.put("action", getAction());
            jsonResponse.put("resultCode", getResultCode());
            jsonResponse.put("resultMessage", getResultMessage());
            jsonResponse.put("errorMessage", getErrorMessage());
            jsonResponse.put("logfilename", getLogFileName());
            jsonResponse.put("logurl", getLogURL());
            if(getResultCode().equals("CNSG-DPLY-ERROR-0001")) {
                //output allowed actions
                jsonResponse.put("allowedactions", getAllowedActions(getApplication()).toString());
            }
            
            jsonResponse.write(out);
            out.close();
        } catch (IOException e) {
            setError("CNSG-DPLY-ERROR-0005","Script failed. Error =>" +e.toString() +"<.");
            throw servletException(myName, getResultCode(), getErrorMessage());
        } catch (JSONException e) {
            setError("CNSG-DPLY-ERROR-0006","JSON exception occurred. Error =>" +e.toString() +"<.");
            throw servletException(myName, getResultCode(), getErrorMessage());
            
        }

    }

    private void setError(String errCode, String err) {
        setResultCode(errCode);
        setErrorMessage(err);
        setResultMessage(DeployConstants.ERROR);
    }

    private void setResult(String code, String msg) {
        setResultCode(code);
        setErrorMessage(DeployConstants.NOERRORS);
        setResultMessage(msg);
    }

    /**
     * @return Version info
     */
    public static String getVersion() {
        return version;
    }

    private consagException servletException(String where, String errCode, String msg) {
        consagException e =new consagException(where, errCode, msg);
        
        return e;
    }

    private static void outMsg(String msg) {
        System.out.println(msg);
    }
    private static void errMsg(String msg) {
        System.err.println(msg);
    }

    private String getLogFileName() {
        return logFileName;
    }

    private void setLogFileName(String fileName) {
        logFileName = fileName;
    }
    private void setLogURL(String logUrl) {
        logURL = logUrl;
    }
    private String getLogURL() {
        return logURL;
    }
}

