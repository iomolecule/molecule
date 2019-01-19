package org.molecule.mods.main;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.cfg4j.provider.ConfigurationProvider;
import org.junit.Test;
import org.molecule.commons.Constants;
import org.molecule.system.Fn;
import org.molecule.system.InOutParam;
import org.molecule.system.LifecycleException;
import org.molecule.system.Param;
import org.molecule.system.services.FnBus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static org.molecule.config.ConfigUtil.newClasspathConfigSource;
import static org.molecule.config.ConfigUtil.newConfigurationProvider;
import static org.molecule.util.CollectionUtils.SET;

@Slf4j
public class FnBusTests {

    @Test
    public void testFnBusSimple() throws LifecycleException {
        FnBus fnBus = new DefaultFnBus(getFns(),getEventBus(),getMessageConfigProvider());

        fnBus.start();

        fnBus.forEach(fn -> {
            log.info("Fn {} registered...",fn.getURI());
        });

        Param in = new InOutParam();
        Param out = fnBus.apply(in);

        log.info("Out : {}",out);
        fnBus.stop();
    }

    @Test
    public void testFnBusSimple2() throws URISyntaxException, LifecycleException {
        FnBus fnBus = new DefaultFnBus(getFns(),getEventBus(),getMessageConfigProvider());

        fnBus.start();

        fnBus.forEach(fn -> {
            log.info("Fn {} registered...",fn.getURI());
        });

        Param in = new InOutParam();
        in = in.plus(Constants.FUNCTION_TO_INVOKE,new URI("function://fn1"));
        Param out = fnBus.apply(in);

        log.info("Out : {}",out);
        fnBus.stop();
    }

    @Test
    public void testFnBusInvalidFn() throws URISyntaxException, LifecycleException {
        FnBus fnBus = new DefaultFnBus(getFns(),getEventBus(),getMessageConfigProvider());

        fnBus.start();

        fnBus.forEach(fn -> {
            log.info("Fn {} registered...",fn.getURI());
        });

        Param in = new InOutParam();
        in = in.plus(Constants.FUNCTION_TO_INVOKE,new URI("function://fn4"));
        Param out = fnBus.apply(in);

        log.info("Out : {}",out);
        fnBus.stop();
    }

    @Test
    public void testFnBusExceptionFn()throws URISyntaxException, LifecycleException {
        FnBus fnBus = new DefaultFnBus(getFns(),getEventBus(),getMessageConfigProvider());

        fnBus.start();

        fnBus.forEach(fn -> {
            log.info("Fn {} registered...",fn.getURI());
        });

        Param in = new InOutParam();
        in = in.plus(Constants.FUNCTION_TO_INVOKE,new URI("function://throw-exception-fn"));
        Param out = fnBus.apply(in);

        log.info("Out : {}",out);
        fnBus.stop();
    }

    @Test
    public void testFnBusExceptionWithMessageFn()throws URISyntaxException, LifecycleException {
        FnBus fnBus = new DefaultFnBus(getFns(),getEventBus(),getMessageConfigProvider());

        fnBus.start();

        fnBus.forEach(fn -> {
            log.info("Fn {} registered...",fn.getURI());
        });

        Param in = new InOutParam();
        in = in.plus(Constants.FUNCTION_TO_INVOKE,new URI("function://throw-exception-fn2"));
        Param out = fnBus.apply(in);

        log.info("Out : {}",out);
        fnBus.stop();
    }

    @Test
    public void testFnBusAddFn()throws URISyntaxException, LifecycleException {
        FnBus fnBus = new DefaultFnBus(getFns(),getEventBus(),getMessageConfigProvider());

        fnBus.start();

        fnBus.forEach(fn -> {
            log.info("Fn {} registered...",fn.getURI());
        });

        Param in = new InOutParam();
        in = in.plus(Constants.FUNCTION_TO_INVOKE,new URI("function://add-fn"));
        in = in.plus("a",500);
        in = in.plus("b",200);
        Param out = fnBus.apply(in);

        log.info("Out : {}",out);
        fnBus.stop();
    }

    private Set<Fn> getFns() {
        return SET(Fn.class,new Fn1(),new Fn2(),new ThrowExceptionFn(),new ThrowExceptionFn2(),new AddFn());
    }

    private EventBus getEventBus(){
        return new EventBus("test");
    }


    private ConfigurationProvider getMessageConfigProvider(){
        return newConfigurationProvider("",newClasspathConfigSource("test","fn-bus-test.json"));
    }
}

@Slf4j
class Fn1 implements Fn{

    @Override
    public URI getURI() {
        try {
            return new URI("function://fn1");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Param apply(Param param) {
        log.info("apply called");
        param = param.plus("response","inside fn1");
        return param;
    }
}

@Slf4j
class Fn2 implements Fn{

    @Override
    public URI getURI() {
        try {
            return new URI("function://fn2");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Param apply(Param param) {
        log.info("apply called");
        param = param.plus("response","inside fn2");
        return param;
    }
}

@Slf4j
class ThrowExceptionFn implements Fn{

    @Override
    public URI getURI() {
        try {
            return new URI("function://throw-exception-fn");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Param apply(Param param) {

        log.info("About to throw runtime exception...");

        throw new RuntimeException("Some random exception..");

    }
}

@Slf4j
class ThrowExceptionFn2 implements Fn{

    @Override
    public URI getURI() {
        try {
            return new URI("function://throw-exception-fn2");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Param apply(Param param) {

        log.info("About to throw runtime exception...");

        throw new RuntimeException("throw-exception-fn2-message");

    }
}

@Slf4j
class AddFn implements Fn{

    @Override
    public URI getURI() {
        try {
            return new URI("function://add-fn");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Param apply(Param param) {

        Integer a = (Integer)param.get("a");
        Integer b = (Integer)param.get("b");

        Integer c = a + b;

        param = param.plus("c",c);

        return param;

    }
}
