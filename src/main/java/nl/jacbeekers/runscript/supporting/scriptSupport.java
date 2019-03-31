package nl.jacbeekers.runscript.supporting;

/**
 * @author Jac. Beekers @ consag consultancy services b.v.
 * @version 20151206.0
 * @since   December 2015
 *
 *
 */

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

public class scriptSupport {
    public scriptSupport() {
        super();
    }
    
    public String getPropertyValue(String propKey) {
    Properties prop = new Properties();
    String rc=scriptConstants.OK;
    String keyVal =scriptConstants.NOT_FOUND;
    
            InputStream input = null;

            try {
                input = Logging.class.getClassLoader().getResourceAsStream(scriptConstants.RUNSCRIPT_PROPERTIES);
                //    input = new FileInputStream(scriptConstants.RUNSCRIPT_PROPERTIES);
                if (input == null) {
                                        System.err.println("Sorry, unable to find " + scriptConstants.RUNSCRIPT_PROPERTIES);
                                        rc=scriptConstants.NOT_FOUND + " - CNSG-DPLY-WARNING: Properties file >" + scriptConstants.RUNSCRIPT_PROPERTIES +"< not found. Using defaults.";
                                        return rc;
                                }

                    // load a properties file
                    prop.load(input);

                    keyVal=prop.getProperty(propKey,scriptConstants.NOT_FOUND);

            } catch (IOException ex) {
                    ex.toString();
            } finally {
                    if (input != null) {
                            try {
                                    input.close();
                            } catch (IOException e) {
                                   System.err.println("Error reading properies file >" + scriptConstants.RUNSCRIPT_PROPERTIES +"<. Error: " + e.toString());
                            }
                    }
            }
            
    return keyVal;
    }

}
