package org.molecule.mods.main;

import org.cfg4j.source.ConfigurationSource;
import org.junit.Test;
import org.molecule.system.ExceptionHandler;

import static org.molecule.config.ConfigUtil.newClasspathConfigSource;

public class ExceptionHandlerTests {

    @Test
    public void testSimpleExceptions(){

        ConfigurationSource source = newClasspathConfigSource("test","exception-config.json");
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
