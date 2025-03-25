package dat.utils;

import dat.exceptions.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
    public static String getPropertyValue(String propName, String resourceName) {
        // REMEMBER TO BUILD WITH MAVEN FIRST. Read the property file if not deployed (else read system vars instead)
        // Read from ressources/config.properties or from pom.xml depending on the ressourceName
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName)) {
            Properties prop = new Properties();
            prop.load(is);

            String value = prop.getProperty(propName);
            if (value != null) {
                return value.trim();
            } else {
                throw new ApiException(500, String.format("Property %s not found in %s", propName, resourceName));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new ApiException(500, String.format("Could not read property %s. Did you remember to build the project with MAVEN?", propName));
        }
    }
}