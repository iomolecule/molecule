/*
 * Copyright 2019 Vijayakumar Mohan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iomolecule.mods.main;

import com.google.common.eventbus.EventBus;
import com.google.inject.Injector;
import com.iomolecule.system.*;
import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.services.FnInterceptionService;
import com.iomolecule.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import com.iomolecule.commons.Constants;
import com.iomolecule.config.MsgConfigSource;
import com.iomolecule.system.services.FnBus;
import com.iomolecule.util.FnUtils;
import com.iomolecule.util.JSONUtils;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.iomolecule.commons.Constants.*;
import static com.iomolecule.util.CollectionUtils.LIST;
import static com.iomolecule.util.StringUtils.format;

@Slf4j
class DefaultFnBus implements FnBus{

    private Set<Fn> fns;
    private Set<List<Fn>> fnsSets;
    private Set<Function<Param,Param>> functionSet;

    private Set<Object> methodFnProviders;
    private Set<Object> fnProviders;

    private EventBus eventBus;

    private URI uri;

    private Map<URI,Fn> fnMap;


    private boolean started;

    private MsgConfigSource messageConfigProvider;

    private static List<ParamDeclaration> errorOutParamDeclaration = LIST(ParamDeclaration.class,
            new DefaultParamDeclaration(STATUS,String.class,true),
            new DefaultParamDeclaration(REASON,String.class,true));


    private FnInterceptionService fnInterceptionService;

    private Injector injector;


    DefaultFnBus(Set<Fn> fnSet,
                 EventBus eventBus,
                 MsgConfigSource messageConfigProvider,FnInterceptionService fnInterceptionService){
        checkArgument(fnSet != null,"Set of Fns cannot be null!");
        checkArgument(eventBus != null, "EventBus cannot be null!");
        this.fns = fnSet;
        this.eventBus = eventBus;
        this.messageConfigProvider = messageConfigProvider;
        this.fnInterceptionService = fnInterceptionService;

        try {
            uri = new URI(String.format("%s://%s", FUNCTION_SCHEME, FNBUS_NAME));
        }catch(Exception e){
            throw new RuntimeException(e);
        }


    }

    DefaultFnBus(Set<Fn> fnSet,
                 Set<List<Fn>> fnSets,
                 EventBus eventBus,
                 MsgConfigSource messageConfigProvider,FnInterceptionService fnInterceptionService){
        checkArgument(fnSet != null,"Set of Fns cannot be null!");
        checkArgument(eventBus != null, "EventBus cannot be null!");
        this.fns = fnSet;
        this.fnsSets = fnSets;
        this.eventBus = eventBus;
        this.messageConfigProvider = messageConfigProvider;
        this.fnInterceptionService = fnInterceptionService;
        try {
            uri = new URI(String.format("%s://%s", FUNCTION_SCHEME, FNBUS_NAME));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    DefaultFnBus(Injector injector, Set<Fn> fnSet,
                 Set<List<Fn>> fnSets,
                 Set<Function<Param,Param>> functionSet,
                 Set<Object> methodFnProviders,
                 Set<Object> fnProviderClasses,
                 EventBus eventBus,
                 MsgConfigSource messageConfigProvider, FnInterceptionService fnInterceptionService){
        checkArgument(fnSet != null,"Set of Fns cannot be null!");
        checkArgument(eventBus != null, "EventBus cannot be null!");
        this.fns = fnSet;
        this.fnsSets = fnSets;
        this.functionSet = functionSet;
        this.eventBus = eventBus;
        this.messageConfigProvider = messageConfigProvider;
        this.fnInterceptionService = fnInterceptionService;
        this.methodFnProviders = methodFnProviders;
        this.fnProviders = fnProviderClasses;
        this.injector = injector;
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

            if(fnInterceptionService != null){
                fnInterceptionService.stop();
            }
        }
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public Param apply(Param param) {
        if(param.containsKey(FUNCTION_TO_INVOKE)){
            //log.debug("Function To Invoke {}",param.get(FUNCTION_TO_INVOKE));
            return handleFunctionCall((URI)param.get(FUNCTION_TO_INVOKE),param);
        }else{
            return handleError(ERROR_NO_FUNCTION_TO_INVOKE_SPECIFIED,param);
        }
    }

    private Param handleError(String errorCode, Param param) {
        log.debug("Error on Function Call {}",errorCode);
        Param outParam = param.plus(STATUS,FAILED);
        String messageJsonPointer = JSONUtils.toJSONPointer(errorCode);

        if(messageConfigProvider.isValid(messageJsonPointer)) {
            outParam = outParam.plus(REASON,
                    format(messageConfigProvider.get(messageJsonPointer, String.class, errorCode), outParam.asMap()));
        }else{
            outParam = outParam.plus(REASON,errorCode);
        }

        outParam = FnUtils.mapOutParams(outParam, errorOutParamDeclaration, Constants.OUT_PARAMS);

        return outParam;
    }

    private Param handleFunctionCall(URI funURI,
                                     Param param) {
        //log.debug("Checking FunURI {}",funURI);
        if(fnMap.containsKey(funURI)){
            Fn fnToInvoke = fnMap.get(funURI);
            try {
                //verify the incoming params and check if any mandatory params are missing or
                // type mismatching from what is expected
                // param = FnUtils.verifyInParams(param,fnToInvoke.getInDeclarations());

                param = invokeInterceptorsBefore(fnToInvoke,param);

                Param outParam = fnToInvoke.apply(param);

                outParam = invokeInterceptorsAfter(fnToInvoke,outParam);

                //log.debug("Out Param {}",outParam);
                //verify whether the out going parameters are valid and provided as promised by the fn
                //any mismatch or missing parameters against the one declared by the Fn a runtime exception is thrown
                //log.debug("Out Params {}",fnToInvoke.getOutDeclarations());
                //outParam = FnUtils.mapOutParams(outParam,fnToInvoke.getOutDeclarations(), Constants.OUT_PARAMS);
                return outParam;
            }catch(Exception e){
                e.printStackTrace();
                return handleException(e,param);
            }
        }else{
            return handleError(ERROR_NO_VALID_FUNCTION_REGISTERED_FOR_URI,param);
        }
    }

    private Param invokeInterceptorsAfter(Fn fnToInvoke, Param outParam) {
        return fnInterceptionService.interceptAfter(fnToInvoke,outParam);
    }

    private Param invokeInterceptorsBefore(Fn fnToInvoke, Param param) {
        return fnInterceptionService.interceptBefore(fnToInvoke,param);
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
                    log.debug("Processing Fn {}",fn.getURI());
                    fnMap.put(fn.getURI(),fn);
                }

            }

            if(methodFnProviders != null && !methodFnProviders.isEmpty()){

                List<Fn> listOfFns = getListOfFnsFromMethodFnProviders(methodFnProviders);

                for (Fn fn : listOfFns) {
                    log.debug("Processing Fn {}",fn.getURI());

                    fnMap.put(fn.getURI(),fn);
                }

            }

            if(fnProviders != null && !fnProviders.isEmpty()){
                List<Fn> listOfFns = getListOfFnsFromFnProviders(fnProviders);

                for (Fn fn : listOfFns) {
                    log.debug("Processing Fn {}",fn.getURI());

                    fnMap.put(fn.getURI(),fn);
                }

            }

            //start the interception service
            if(fnInterceptionService != null){
                fnInterceptionService.start();
            }
        }
    }

    private List<Fn> getListOfFnsFromFnProviders(Set<Object> fnProviders) {
        List<Fn> fnList = new ArrayList<>();

        for (Object fnProvider : fnProviders) {

            List<Fn> fnsFromObject = getFnsFromObject(fnProvider);

            //Object instance = injector.getInstance(fnProvider);
            fnList.addAll(fnsFromObject);
        }

        return fnList;
    }

    private List<Fn> getListOfFnsFromMethodFnProviders(Set<Object> methodFnProviders) {
        List<Fn> fnList = new ArrayList<>();

        for (Object methodFnProvider : methodFnProviders) {
            List<Method> listOfFnProviderMethods = ReflectionUtils.getListOfFnProviderMethods(methodFnProvider, Id.class);
            for (Method providerMethod : listOfFnProviderMethods) {
                Fn methodFn = methodToFn(methodFnProvider,providerMethod);
                fnList.add(methodFn);
            }

        }

        return fnList;
    }

    private List<Fn> getFnsFromObject(Object obj){
        List<Fn> fnList = new ArrayList<>();
        List<Method> listOfFnProviderMethods = ReflectionUtils.getListOfFnProviderMethods(obj, Id.class);
        for (Method providerMethod : listOfFnProviderMethods) {
            Fn methodFn = methodToFn(obj,providerMethod);
            fnList.add(methodFn);
        }
        return fnList;
    }

    private Fn methodToFn(Object providerObject,Method providerMethod) {

        Id idAnnotation = providerMethod.getAnnotation(Id.class);

        List<ParamDeclaration> argumentsInfo = ReflectionUtils.getArgumentInfo(providerObject, providerMethod);
        ParamDeclaration returnInfo = ReflectionUtils.getReturnInfo(providerObject, providerMethod);

        MethodFn methodFn = new MethodFn(providerObject,providerMethod,idAnnotation.value(),argumentsInfo,returnInfo);

        return methodFn;
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
