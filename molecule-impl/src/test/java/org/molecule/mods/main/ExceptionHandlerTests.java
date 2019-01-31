package org.molecule.mods.main;

import org.junit.Test;
import org.molecule.config.ConfigurationSource;
import org.molecule.config.InputStreamConfigurationSource;
import org.molecule.system.ExceptionHandler;

public class ExceptionHandlerTests {

    @Test
    public void testSimpleExceptions(){

        ConfigurationSource source = new InputStreamConfigurationSource(false,false,
                getClass().getResourceAsStream(""));
        ExceptionHandler exceptionHandler = new DefaultExceptionHandler(source);

        int x = 0;
        int y = 40;

        try {
            int z = y / x;
        }catch(Exception e){
            exceptionHandler.handle(e);
        }


    }
}
