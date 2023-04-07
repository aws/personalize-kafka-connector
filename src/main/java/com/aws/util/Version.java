package com.aws.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/************
 *  This is used for providing version for Connector
 */
public class Version {

    private static final Logger log = LoggerFactory.getLogger(Version.class);

    public static final String version;

    static {
        String versionProperty = "unknown";

        try {
            Properties prop = new Properties();
            prop.load(Version.class.getResourceAsStream(Constants.CONNECTOR_VERSION_PROPERTIES_FILE_NAME));
            System.out.println(prop);
            versionProperty = prop.getProperty(Constants.CONNECTOR_VERSION_PROPERTY_NAME, versionProperty).trim();
        } catch (Exception e) {
            log.warn("Error in loading connector version");
            versionProperty = "unknown";
        }
        version = versionProperty;
    }


    public static String getVersion() {
        return version;
    }

}
