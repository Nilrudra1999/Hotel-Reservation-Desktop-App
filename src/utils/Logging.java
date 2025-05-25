/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Logging utility Class: This class uses the logging class along with a file handler to log exceptions and other app
 * wide activities onto a single logs folder. Each log file is set to maximum size which stores a certain number of
 * logging activities, before changing to the next file marked sequentially starting at 0. "logs0.log"
 **********************************************************************************************************************/
package utils;

import java.io.File;
import java.io.IOException;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



public class Logging {
    private static Logging instance;
    private static final Logger logger = Logger.getLogger(Logging.class.getName());

    public Logging() throws IOException {
        File logDir = new File("logs");
        if (!logDir.exists()) logDir.mkdirs();
        FileHandler fileHandler = new FileHandler("logs/system_logs.%g.log", 1024 * 1024, 10, true);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
    }


    public static Logging getInstance() {
        try {
            if (instance == null) instance = new Logging();
        } catch (IOException e) { e.printStackTrace(); }
        return instance;
    }


    public void logException(Exception e, String message) {
        logger.log(Level.SEVERE, message + "\n" + e.getMessage(), e);
    }
    public void logActivity(String message) { logger.log(Level.INFO, message + "\n"); }
}
