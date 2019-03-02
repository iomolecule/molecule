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

package com.iomolecule.mods.httpshell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.google.common.io.CharStreams;
import com.iomolecule.shell.http.MediaType;
import com.iomolecule.shell.http.annotations.SupportedMediaTypes;
import com.iomolecule.system.*;
import com.iomolecule.system.services.DomainService;
import com.iomolecule.system.services.FnBus;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static com.iomolecule.commons.Constants.*;
import static com.iomolecule.util.JSONUtils.OBJECT_MAPPER;

@Slf4j
class HttpHandler implements io.undertow.server.HttpHandler{

    private DomainService domainService;
    private FnBus fnBus;
    private LifecycleManager rootLifecycleManager;
    private Set<String> supportedMediaTypes;


    @Inject
    HttpHandler(DomainService domainService, FnBus fnBus, LifecycleManager lifecycleManager, @SupportedMediaTypes Set<String> supportedMediaTypes){
        Objects.requireNonNull(domainService,"domainService");
        Objects.requireNonNull(fnBus,"fnBus");
        Objects.requireNonNull(lifecycleManager,"lifecyclemanager");
        Objects.requireNonNull(supportedMediaTypes,"supported media types");
        this.domainService = domainService;
        this.fnBus = fnBus;
        this.rootLifecycleManager = lifecycleManager;
        this.supportedMediaTypes = supportedMediaTypes;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        HttpString requestMethod = httpServerExchange.getRequestMethod();

        String requestURI = httpServerExchange.getRequestURI();

        HeaderMap requestHeaders = httpServerExchange.getRequestHeaders();

        String requestURL = httpServerExchange.getRequestURL();

        Map<String, Deque<String>> queryParameters = httpServerExchange.getQueryParameters();

        log.debug("Request Details...");
        log.debug("Method : {} ",requestMethod);
        log.debug("URI : {}",requestURI);
        log.debug("URL : {}",requestURL);
        log.debug("Headers : {}",requestHeaders);
        log.debug("Query Params : {}",queryParameters);

        if(isSupportedMediaTypes(httpServerExchange)){

            try {


                Param inParam = mapRequestToParam(httpServerExchange);
                Param outParam = fnBus.apply(inParam);

                if(outParam.hasOutParams()){

                    sendResponse(httpServerExchange,outParam);
                }else{
                    respondWithCode(httpServerExchange,StatusCodes.NO_CONTENT);
                }
            }catch(InvalidOperationException e){
                respondWithCode(httpServerExchange,StatusCodes.NOT_FOUND);
            }catch(JsonProcessingException e){
                respondWithCode(httpServerExchange,StatusCodes.INTERNAL_SERVER_ERROR,errorToString(e));
            }

        }else{
            respondWithCode(httpServerExchange, StatusCodes.UNSUPPORTED_MEDIA_TYPE,StatusCodes.UNSUPPORTED_MEDIA_TYPE_STRING);
        }

        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE,"text/plain");
        httpServerExchange.getResponseSender().send("Hello from iomolecule!!");

    }

    private String errorToString(Exception e) throws JsonProcessingException {
        Map<String,Object> message = new HashMap<>();

        message.put(STATUS,FAILED);
        message.put(REASON,e.getMessage());
        message.put(EXCEPTION,e);
        String jsonString = OBJECT_MAPPER.writeValueAsString(message);
        return jsonString;
    }

    private void sendResponse(HttpServerExchange httpServerExchange, Param outParam) throws JsonProcessingException {
        Map<String, Object> outValues = outParam.outParams();

        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE,MediaType.APPLICATION_JSON);

        String outValue = OBJECT_MAPPER.writeValueAsString(outValues);

        httpServerExchange.getResponseSender().send(outValue);
    }

    private Param mapRequestToParam(HttpServerExchange httpServerExchange) throws IOException, InvalidOperationException {

        String requestURI = httpServerExchange.getRequestURI();

        Map<String, Deque<String>> queryParameters = httpServerExchange.getQueryParameters();

        HttpString requestMethod = httpServerExchange.getRequestMethod();

        String requestBody = null;

        Map requestMap = new HashMap();


        String operationPath = toDomainOperation(requestURI,requestMethod,queryParameters);

        Operation operation = domainService.getOperation(operationPath);

        Param outParam = null;

        if(operation.getName().equals("__no_op__")){
            //no operation present for the given path so throw and exception here
            String message = String.format("operation %s is invalid for path %s",requestMethod,requestURI);
            throw new InvalidOperationException(message);
        }else{

            if(requestMethod.equalToString("POST") || requestMethod.equalToString("PUT")){
                requestBody = readBody(httpServerExchange);
                Map map = OBJECT_MAPPER.convertValue(requestBody, Map.class);
                requestMap.putAll(map);
            }

            requestMap = populateQueryParams(queryParameters,requestMap);

            requestMap.put(FUNCTION_TO_INVOKE,operation.getFunctionURI());

            outParam = new InOutParam(PersistentHashMap.create(requestMap));

        }


        return outParam;
    }

    private Map populateQueryParams(Map<String, Deque<String>> queryParameters, Map requestMap) {

        queryParameters.forEach((key,vallist)->{
            if(vallist.size() > 1){
                List<String> values = new ArrayList<>();
                for (String item : vallist) {
                    values.add(item);
                }
                requestMap.put(key,values);
            }else{
                if(!vallist.isEmpty()){
                    requestMap.put(key,vallist.getFirst());
                }
            }

        });

        return requestMap;
    }

    private String toDomainOperation(String requestURI, HttpString requestMethod, Map<String, Deque<String>> queryParameters) {

        String newRequestURI = requestURI.substring(1,requestURI.length());

        newRequestURI = newRequestURI.replaceAll("/",".");

        String opName = null;
        if(queryParameters.containsKey("op")){
            Deque<String> ops = queryParameters.get("op");
            opName = ops.getFirst();
        }else{
            opName = requestMethod.toString().toLowerCase();
        }
        newRequestURI = String.format("%s.%s",newRequestURI,opName);

        return newRequestURI;
    }

    private boolean isSupportedMediaTypes(HttpServerExchange httpServerExchange) {
        boolean retVal = false;

        HeaderValues acceptHeader = httpServerExchange.getRequestHeaders().get(Headers.ACCEPT);

        if(canAcceptAll(acceptHeader)){
            retVal = true;
        }else {
            for (String supportedMediaType : supportedMediaTypes) {
                if(acceptHeader.contains(supportedMediaType)){
                    retVal = true;
                    break;
                }
            }

        }


        return retVal;
    }

    private boolean canAcceptAll(HeaderValues acceptHeader) {
        boolean retVal = false;

        Iterator<String> iterator = acceptHeader.iterator();

        while(iterator.hasNext()){
            String next = iterator.next();
            String[] splitAcceptedMediaTypes = next.split(",");
            for (String splitAcceptedMediaType : splitAcceptedMediaTypes) {
                if(splitAcceptedMediaType.startsWith(MediaType.ACCEPT_ALL)){
                    retVal = true;
                    break;
                }
            }
        }
        return retVal;
    }

    private void respondWithCode(HttpServerExchange httpServerExchange, int statusCode, String message) {
        httpServerExchange.setStatusCode(statusCode);
        if(message != null) {
            httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE,"text/plain");
            httpServerExchange.getResponseSender().send(message);
        }else{
            httpServerExchange.endExchange();
        }
    }

    private void respondWithCode(HttpServerExchange httpServerExchange, int statusCode) {
        respondWithCode(httpServerExchange,statusCode,null);
    }

    private String readBody(HttpServerExchange exchange) throws IOException {
        InputStream requestStream = exchange.getInputStream();
        InputStreamReader streamReader = new InputStreamReader(requestStream);

        try {
            String requestString = CharStreams.toString(streamReader);
            return requestString;
        }finally{
            try {
                streamReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }

}
