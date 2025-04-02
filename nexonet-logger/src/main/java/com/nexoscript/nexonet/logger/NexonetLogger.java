package com.nexoscript.nexonet.logger;

public class NexonetLogger {
    private final boolean logging;

    public NexonetLogger(boolean logging) {
        this.logging = logging;
    }

    public void log(LoggingType loggingType, String message) {
        this.log(loggingType, message, true);
    }

    public void log(LoggingType loggingType, String message, boolean newLine) {
        if (logging) {
            StringBuilder builder = new StringBuilder();
            switch (loggingType) {
                case INFO -> builder.append("\u001B[0;37m");
                case WARN -> builder.append("\u001B[0;33m");
                case ERROR -> builder.append("\u001B[0;31m");
            }
            builder.append("[").append(loggingType.name()).append("] ").append(message).append("\u001B[0m");
            if (newLine) {
                builder.append("\n");
            }
            System.out.print(builder);
        }
    }
}