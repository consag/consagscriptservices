package nl.consag.deploy.supporting;

/**
 * @author Jac. Beekers @ consag consultancy services b.v.
 * @version 20151206.0
 * @since   December 2015
 *
 *
 */

import java.io.IOException;
import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploySupport {
    public DeploySupport() {
        super();
    }
    
    public String getPropertyValue(String propKey) {
    Properties prop = new Properties();
    String rc=DeployConstants.OK;
    String keyVal =DeployConstants.NOT_FOUND;
    
            InputStream input = null;

            try {
                input =Logging.class.getClassLoader().getResourceAsStream(DeployConstants.CONSAGDEPLOY_PROPERTIES);
                //    input = new FileInputStream(DeployConstants.CONSAGDEPLOY_PROPERTIES);
                if (input == null) {
                                        System.err.println("Sorry, unable to find " + DeployConstants.CONSAGDEPLOY_PROPERTIES);
                                        rc=DeployConstants.NOT_FOUND + " - CNSG-DPLY-WARNING: Properties file >" + DeployConstants.CONSAGDEPLOY_PROPERTIES +"< not found. Using defaults.";
                                        return rc;
                                }

                    // load a properties file
                    prop.load(input);

                    keyVal=prop.getProperty(propKey,DeployConstants.NOT_FOUND);

            } catch (IOException ex) {
                    ex.toString();
            } finally {
                    if (input != null) {
                            try {
                                    input.close();
                            } catch (IOException e) {
                                   System.err.println("Error reading properies file >" + DeployConstants.CONSAGDEPLOY_PROPERTIES +"<. Error: " + e.toString());
                            }
                    }
            }
            
    return keyVal;
    }

}
