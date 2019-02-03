package org.molecule.mods.main;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.molecule.commons.Constants;
import org.molecule.config.ConfigurationSource;
import org.molecule.system.*;
import org.molecule.system.services.FnBus;
import org.molecule.util.FnUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static org.molecule.commons.Constants.*;
import static org.molecule.util.CollectionUtils.LIST;
import static org.molecule.util.StringUtils.format;

@Slf4j
class DefaultFnBus implements FnBus{

    private Set<Fn> fns;
    private Set<List<Fn>> fnsSets;
    private Set<Function<Param,Param>> functionSet;

    private EventBus eventBus;

    private URI uri;

    private Map<URI,Fn> fnMap;

    private boolean started;

    private ConfigurationSource messageConfigProvider;

    private static List<ParamDeclaration> errorOutParamDeclaration = LIST(ParamDeclaration.class,
            new DefaultParamDeclaration(STATUS,String.class,true),
            new DefaultParamDeclaration(REASON,String.class,true));



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

    DefaultFnBus(Set<Fn> fnSet,
                 Set<List<Fn>> fnSets,
                 Set<Function<Param,Param>> functionSet,
                 EventBus eventBus,
                 ConfigurationSource messageConfigProvider){
        checkArgument(fnSet != null,"Set of Fns cannot be null!");
        checkArgument(eventBus != null, "EventBus cannot be null!");
        this.fns = fnSet;
        this.fnsSets = fnSets;
        this.functionSet = functionSet;
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

        Collection<Fn> fnCollection = fnMap.values();
        fnCollection.forEach(fnConsumer);
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
            //log.info("Function To Invoke {}",param.get(FUNCTION_TO_INVOKE));
            return handleFunctionCall((URI)param.get(FUNCTION_TO_INVOKE),param);
        }else{
            return handleError(ERROR_NO_FUNCTION_TO_INVOKE_SPECIFIED,param);
        }
    }

    private Param handleError(String errorCode, Param param) {
        log.debug("Error on Function Call {}",errorCode);
        Param outParam = param.plus(STATUS,FAILED);
        if(messageConfigProvider.isValid(errorCode)) {
            outParam = outParam.plus(REASON,
                    format(messageConfigProvider.get(errorCode, String.class, errorCode), outParam.asMap()));
        }else{
            outParam = outParam.plus(REASON,errorCode);
        }

        outParam = FnUtils.mapOutParams(outParam, errorOutParamDeclaration, Constants.OUT_PARAMS);

        return outParam;
    }

    private Param handleFunctionCall(URI funURI,
                                     Param param) {
        //log.info("Checking FunURI {}",funURI);
        if(fnMap.containsKey(funURI)){
            Fn fnToInvoke = fnMap.get(funURI);
            try {
                //verify the incoming params and check if any mandatory params are missing or
                // type mismatching from what is expected
                param = FnUtils.verifyInParams(param,fnToInvoke.getInDeclarations());
                Param outParam = fnToInvoke.apply(param);
                //log.info("Out Param {}",outParam);
                //verify whether the out going parameters are valid and provided as promised by the fn
                //any mismatch or missing parameters against the one declared by the Fn a runtime exception is thrown
                //log.info("Out Params {}",fnToInvoke.getOutDeclarations());
                outParam = FnUtils.mapOutParams(outParam,fnToInvoke.getOutDeclarations(), Constants.OUT_PARAMS);
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

            if(functionSet != null && !functionSet.isEmpty()){
                List<Fn> listOfFns = getListOfFnsFromFunctions(functionSet);

                for (Fn fn : listOfFns) {
                    log.info("Processing Fn {}",fn.getURI());
                    fnMap.put(fn.getURI(),fn);
                }

            }
        }
    }

    private List<Fn> getListOfFnsFromFunctions(Set<Function<Param, Param>> functionLists) {
        List<Fn> listOfFns = new ArrayList<>();

            for (Function<Param, Param> paramParamFunction : functionLists) {
                try {
                    Fn fn = transferFunctionToFn(paramParamFunction);
                    listOfFns.add(fn);
                }catch(Exception e){
                    String message = String.format("Error %s occured while processing Fn Class %s, so ignoring the function from being registered!",e.getMessage(),paramParamFunction.getClass());
                    log.warn(message);
                }
            }


        return listOfFns;

    }

    private Fn transferFunctionToFn(Function<Param, Param> function) {
        URI uri = null;
        //String doc = null;
        List<ParamDeclaration> inParams = null;
        List<ParamDeclaration> outParams = null;
        Class fnClass = function.getClass();
        try {
            uri = FnUtils.getURI(fnClass);
            try {
                inParams = FnUtils.getInParams(fnClass);
            }catch(Exception ex){

            }

            try {
                outParams = FnUtils.getOutParams(fnClass);
            }catch(Exception e){

            }
            //doc = FnUtils.getDoc(fnClass);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        Fn newFn = new URIFn(uri,function,inParams,outParams);

        return newFn;

    }
}
