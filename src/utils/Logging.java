/***********************************************************************************************************************
 * Logging Class: Records any errors which occur during the execution of the application as well as the admin login
 * activities, and stores the records inside the logs folder inside the application's root directory.
 **********************************************************************************************************************/
package utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {
    private static final Logger logger = Logger.getLogger(Logging.class.getName());

    static {
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) logDir.mkdirs();
            FileHandler fileHandler = new FileHandler("logs/system_logs.%g.log", 1024 * 1024, 10, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("\n********* Failed to initialize logger *********\n");
        }
    }


    public static void logException(Exception e, String context) {
        logger.log(Level.SEVERE, "[" + context + "] " + e.getMessage(), e);
    }


    public static void logActivity(String activityMessage) {
        logger.log(Level.INFO, activityMessage);
    }
}
