package org.molecule.mods.main;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.molecule.config.ConfigurationSource;
import org.molecule.system.Fn;
import org.molecule.system.LifecycleException;
import org.molecule.system.Param;
import org.molecule.system.ParamDeclaration;
import org.molecule.system.services.FnBus;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static org.molecule.commons.Constants.*;
import static org.molecule.util.StringUtils.format;

@Slf4j
class DefaultFnBus implements FnBus{

    private Set<Fn> fns;
    private Set<List<Fn>> fnsSets;

    private EventBus eventBus;

    private URI uri;

    private Map<URI,Fn> fnMap;

    private boolean started;

    private ConfigurationSource messageConfigProvider;

    DefaultFnBus(Set<Fn> fnSet,
                 EventBus eventBus,
                 ConfigurationSource messageConfigProvider){
        checkArgument(fnSet != null,"Set of Fns cannot be null!");
        checkArgument(eventBus != null, "EventBus cannot be null!");
        this.fns = fnSet;
        this.eventBus = eventBus;
        this.messageConfigProvider = messageConfigProvider;

        try {
            uri = new URI(String.format("%s://%s", FUNCTION_SCHEME, FNBUS_NAME));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    DefaultFnBus(Set<Fn> fnSet,
                 Set<List<Fn>> fnSets,
                 EventBus eventBus,
                 ConfigurationSource messageConfigProvider){
        checkArgument(fnSet != null,"Set of Fns cannot be null!");
        checkArgument(eventBus != null, "EventBus cannot be null!");
        this.fns = fnSet;
        this.fnsSets = fnSets;
        this.eventBus = eventBus;
        this.messageConfigProvider = messageConfigProvider;

        try {
            uri = new URI(String.format("%s://%s", FUNCTION_SCHEME, FNBUS_NAME));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void forEach(Consumer<Fn> fnConsumer) {
        fns.forEach(fnConsumer);
    }

    @Override
    public boolean hasFnForURI(URI uri) {
        boolean retVal = false;
        for (Fn fn : fns) {
            if(fn.getURI().equals(uri)){
                retVal = true;
                break;
            }
        }

        return retVal;
    }

    @Override
    public void start() throws LifecycleException {
        if(!isInitialized()){
            initialize();
        }
    }

    @Override
    public void stop() {
        if(!started){
           //do the stopping
            fnMap.clear();
            //fns.clear();
        }
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public Param apply(Param param) {
        if(param.containsKey(FUNCTION_TO_INVOKE)){
            return handleFunctionCall((URI)param.get(FUNCTION_TO_INVOKE),param);
        }else{
            return handleError(ERROR_NO_FUNCTION_TO_INVOKE_SPECIFIED,param);
        }
    }

    private Param handleError(String errorCode, Param param) {
        log.info("Error on Function Call {}",errorCode);
        Param outParam = param.plus(STATUS,FAILED);
        if(messageConfigProvider.isValid(errorCode)) {
            outParam = outParam.plus(REASON,
                    format(messageConfigProvider.get(errorCode, String.class, errorCode), outParam.asMap()));
        }else{
            outParam = outParam.plus(REASON,errorCode);
        }
        return outParam;
    }

    private Param handleFunctionCall(URI funURI, Param param) {
        if(fnMap.containsKey(funURI)){
            Fn fnToInvoke = fnMap.get(funURI);
            try {
                Param outParam = fnToInvoke.apply(param);
                //List<ParamDeclaration> outDeclarations = fnToInvoke.getOutDeclarations();
                //outParam = outParam.plus(OUT_PARAMS,outDeclarations);
                return outParam;
            }catch(Exception e){
                return handleException(e,param);
            }
        }else{
            return handleError(ERROR_NO_VALID_FUNCTION_REGISTERED_FOR_URI,param);
        }
    }

    private Param handleException(Exception exception, Param param) {
        String message = exception.getMessage();
        try{
            message = messageConfigProvider.get(message,String.class,message);
        }catch(Exception ex){
            //if no mapped message is found just ignore and send the message in the exception
        }
        Param outParam = param.plus(STATUS,FAILED);
        outParam = outParam.plus(REASON,message);
        outParam = outParam.plus(EXCEPTION,exception);
        return outParam;
    }

    private boolean isInitialized(){
        return fnMap != null;
    }

    private synchronized void initialize(){
        if(!isInitialized()){
            fnMap = new HashMap<>();
            //initialize the internal function map here
            for (Fn fn : fns) {
                fnMap.put(fn.getURI(),fn);
            }

            if(fnsSets != null && !fnsSets.isEmpty()){
                for (List<Fn> fnsSet : fnsSets) {
                    for (Fn fn : fnsSet) {
                        fnMap.put(fn.getURI(),fn);
                    }

                }

            }
        }
    }
}
