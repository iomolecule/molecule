package org.molecule.mods.main;

import lombok.extern.slf4j.Slf4j;
import org.cfg4j.source.ConfigurationSource;
import org.molecule.system.ExceptionHandler;

@Slf4j
class DefaultExceptionHandler implements ExceptionHandler {


    private ConfigurationSource configurationSource;

    DefaultExceptionHandler(ConfigurationSource messageConfigurationSource){
        this.configurationSource = messageConfigurationSource;
    }

    @Override
    public void handle(Throwable throwable) {

        String message = throwable.getMessage();
        log.debug("Error Message {}",message);
        log.debug("Handling exception {} with message config source {}",throwable,configurationSource);

    }
}
