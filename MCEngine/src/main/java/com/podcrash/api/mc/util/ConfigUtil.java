package com.podcrash.api.mc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigUtil {

    public static Properties readPropertiesFile(ClassLoader loader, String fileName) {
        Properties prop = new Properties();
        InputStream stream = loader.getResourceAsStream(fileName);
        if(stream != null) {
            try {
                prop.load(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return prop;
    }
}
