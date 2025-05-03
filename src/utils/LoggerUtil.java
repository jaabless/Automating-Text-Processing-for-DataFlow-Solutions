package utils;

import java.util.logging.*;

public class LoggerUtil {
    private static final Logger logger = Logger.getLogger("TextProcessorLogger");

    static {
        try {
            FileHandler fh = new FileHandler("app.log", true);
            fh.setFormatter(new SimpleFormatter());
            ConsoleHandler ch = new ConsoleHandler();  // ✅ New console handler
            ch.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.addHandler(ch);
        } catch (Exception e) {
            LoggerUtil.logError("Logging setup failed", e);
        }

    }

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logError(String message, Throwable t) {
        logger.log(Level.SEVERE, message, t);
    }
}