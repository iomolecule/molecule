package org.molecule.config;

public class ConfigurationTypeException extends ConfigurationException{

    public ConfigurationTypeException() {
    }

    public ConfigurationTypeException(String message) {
        super(message);
    }

    public ConfigurationTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationTypeException(Throwable cause) {
        super(cause);
    }

    public ConfigurationTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
