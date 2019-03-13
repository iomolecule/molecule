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

package com.iomolecule.mods.cmdshell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iomolecule.commons.Constants;
import com.iomolecule.system.*;
import com.iomolecule.system.services.DomainService;
import com.iomolecule.system.services.FnBus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.iomolecule.util.JSONUtils.OBJECT_MAPPER;

@Slf4j
class CmdShell implements Shell, Runnable{


    private DomainService domainService;
    private FnBus fnBus;

    @CommandLine.Option(names={"-c","--command"},
            description = "Fully qualified command including domain path E.g. /domain1/domain2/operation"
            ,required = true)
    private String command;

    @CommandLine.Parameters
    private List<String> params;

    @CommandLine.Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;


    @Inject
    CmdShell(DomainService domainService, FnBus fnBus){
        this.domainService = domainService;
        this.fnBus = fnBus;
    }

    @Override
    public void start(String[] args) {
        log.debug("Starting with Args {}",args);

        CommandLine.run(this,args);


        /*try {



        }catch(CommandLine.MissingParameterException e){
            List<CommandLine.Model.ArgSpec> missing = e.getMissing();

            for (CommandLine.Model.ArgSpec argSpec : missing) {
                System.out.println(argSpec);
            }

        }*/

    }

    @Override
    public void stop() {
        log.debug("Stop ...");
    }

    @Override
    public void run() {

        if(domainService.isValidOperation(command)){
            Operation operation = domainService.getOperation(command);
            Param inParam = new InOutParam();
            inParam = inParam.plus(Constants.FUNCTION_TO_INVOKE,operation.getFunctionURI());
            inParam = fillParams(inParam,params);
            Param outParam = fnBus.apply(inParam);
            if(outParam.hasOutParams() || outParam.containsKey(Constants.STATUS)){
                prettyPrintOutput(outParam);
            }
        }else{
            System.out.println(String.format("Operation %s is invalid for domain %s",command,"/"));
        }
    }

    private Param fillParams(Param inParam, List<String> args) {
        if(args == null || args.isEmpty()){
            return inParam;
        }
        String operationParams = null;
        operationParams = args.get(0);

        log.debug("Params {}",operationParams);

        if(operationParams != null) {
            //log.debug("PREQUOTE IN {}", operationParams);
            //String opParams = quoteParams(operationParams);
            //String opParams = unQuoteParams(operationParams);
            //log.debug("MVEL IN {}", opParams);

            try {
                Object expressionObj = MVEL.eval(operationParams);
                //printf("Params %s, %s", expressionObj.getClass(), expressionObj);
                if (expressionObj instanceof Map) {
                    Map paramMap = (Map) expressionObj;
                    Set keySet = paramMap.keySet();

                    for (Object key : keySet) {
                        Object value = paramMap.get(key);
                        inParam = inParam.plus((String) key, value);
                    }


                } else {
                    String message = String.format("Unsupported command param data structure %s, only Map expressions are supported at present", expressionObj.getClass());
                    throw new RuntimeException(message);
                }
            }catch(Exception e){
                //if we get an exception when evaluating the expression
                //as an MVEL expression, we can pass the argument as is to let the function handle as is
                inParam = inParam.plus(Constants.IN_PARAMS,operationParams);
            }
        }

        return inParam;
    }

    private void prettyPrintOutput(Param outParam) {
        Map<String, Object> outParamMap = outParam.outParams();
        try {
            String outputString = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(outParamMap);
            System.out.println(outputString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

@Data
class MoleculeCmd{


}