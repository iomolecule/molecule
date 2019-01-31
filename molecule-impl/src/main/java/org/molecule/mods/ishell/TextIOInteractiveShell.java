package org.molecule.mods.ishell;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.molecule.system.Operation;
import org.molecule.system.Shell;
import org.molecule.system.services.DomainService;
import org.molecule.system.services.FnBus;
import org.mvel2.MVEL;

import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

@Slf4j
class TextIOInteractiveShell implements Shell{

    private DomainService domainService;
    private TextIO textIO;
    private String defaultPrompt = "> ";
    private String currentDomain = "";

    private Stack<String> domainStack = new Stack<>();
    //private EvictingQueue<String> commandHistory = EvictingQueue.create(50);

    private Pattern pattern = Pattern.compile(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    //private Pattern pattern = Pattern.compile(" (?=(?:[^\"]*\\[(.\\s\\d)*\\])*[^\"]*$)");
    //private Pattern pattern = Pattern.compile(" (?=(?:[^\"]*\\[[^\"]*\\])*[^\"]*$)");
    //private Pattern pattern = Pattern.compile(" ");
    private Splitter splitter = Splitter.on(pattern).trimResults().omitEmptyStrings();
    private FnBus fnBus;


    @Inject
    TextIOInteractiveShell(DomainService domainService, FnBus fnBus){
        this.domainService = domainService;
        this.fnBus = fnBus;
    }

    @Override
    public void start(String[] args) {
        log.debug("Starting Interactive shell...");
        textIO =    TextIoFactory.getTextIO();
        String commandName = "";
        while(!hasToExit(commandName) ){
            //textIO.newStringInputReader().

            commandName = textIO.newStringInputReader().read(getPrompt(domainStack,currentDomain,defaultPrompt));

            executeCommand(commandName);

            //TextTerminal<?> textTerminal = textIO.getTextTerminal();
            //textTerminal.printf("You have entered command %s \n",commandName);
        }

    }



    private boolean hasToExit(String commandName) {
        boolean retVal = false;
        if(commandName.equalsIgnoreCase("exit")
                || commandName.equalsIgnoreCase("quit")
                || commandName.equalsIgnoreCase("shutdown")){
            retVal = true;
        }
        return retVal;
    }

    private void executeCommand(String commandLine) {

        TextTerminal<?> textTerminal = textIO.getTextTerminal();

        List<String> input = splitter.splitToList(commandLine);
        String commandName = input.get(0);
        List<String> args = input.subList(1,input.size());

        if(commandName.equalsIgnoreCase("domains")){
            listDomains(textTerminal,args);
        }else if(commandName.equalsIgnoreCase("ops")){
            listOps(textTerminal,args);
        }else if(commandName.equalsIgnoreCase("cd")) {
            changeDomain(textTerminal,args);
        }else if(commandName.equalsIgnoreCase("pwd")){
            printCurrentDomain(textTerminal,args);
        }else if(commandName.equalsIgnoreCase("exec")){
            executeOperation(textTerminal,args);
        }else if(hasToExit(commandName)){
            exit(textTerminal,args);
        }else{
            textTerminal.printf("Unknown Command %s",commandName);
            textTerminal.resetLine();
        }
    }

    private void executeOperation(TextTerminal<?> textTerminal, List<String> args) {
        if(args == null || args.isEmpty()){
            return;
        }
        String operationName = args.get(0);
        String operationParams = null;
        if(args.size() > 1){
            operationParams = args.get(1);
        }

        log.info("Operation {}",operationName);
        log.info("Params {}",operationParams);

        String fullyQualifiedDomainName = getFullyQualifiedDomain(domainStack,currentDomain);

        if(!domainService.isValidOperationAt(fullyQualifiedDomainName,operationName)){
            textTerminal.printf("No such operation '%s' found under '%s'",operationName,getPrompt(domainStack,currentDomain,""));
        }else{
            if(operationParams != null) {
                //log.info("PREQUOTE IN {}", operationParams);
                //String opParams = quoteParams(operationParams);
                String opParams = unQuoteParams(operationParams);
                //log.info("MVEL IN {}", opParams);

                Object expressionObj = MVEL.eval(opParams);
                textTerminal.printf("Params %s, %s", expressionObj.getClass(), expressionObj);
                Operation operation = domainService.getOperationAt(fullyQualifiedDomainName,operationName);
                URI functionURI = operation.getFunctionURI();
                textTerminal.printf("About to invoke function %s",functionURI);
            }
        }
        //textTerminal.printf("Operation Name %s, Args %s",operationName,operationParams);

        textTerminal.resetLine();
    }

    private String unQuoteParams(String operationParams) {
        if(operationParams.startsWith("\"") && operationParams.endsWith("\"")){
            operationParams = operationParams.substring(1,operationParams.length()-1);
        }
        return operationParams;
    }

    private String quoteParams(String operationParams) {
        StringBuffer paramBuffer = new StringBuffer();

        paramBuffer.append("\"");

        paramBuffer.append(operationParams);

        paramBuffer.append("\"");

        return paramBuffer.toString();
    }

    private void exit(TextTerminal<?> textTerminal, List<String> args) {

    }

    private void printCurrentDomain(TextTerminal<?> textTerminal, List<String> args) {
        textTerminal.printf("%s",getPrompt(domainStack,currentDomain,""));
        textTerminal.resetLine();
    }

    private void changeDomain(TextTerminal<?> textTerminal, List<String> args) {
        String domainToChange = args.get(0);

        List<String> domainNamesAt = domainService.getDomainNamesAt(getFullyQualifiedDomain(domainStack,currentDomain));

        if(isNavigationToPrevious(domainToChange)){
            if(domainStack.size() > 0){
                currentDomain = domainStack.pop();
            }// if not we are already in the root so nothing to do
        }else if(isNavigationToRoot(domainToChange)){
            domainStack.clear();
            currentDomain = "";
        }else if(domainNamesAt.contains(domainToChange)){
            domainStack.push(currentDomain);
            currentDomain = domainToChange;
        }else{
            textTerminal.printf("no such domain %s",domainToChange);
            textTerminal.resetLine();
        }
    }

    private boolean isNavigationToRoot(String domainToChange) {
        return domainToChange.equalsIgnoreCase("/");
    }

    private boolean isNavigationToPrevious(String domainToChange) {
        return domainToChange.equalsIgnoreCase("..");
    }

    private String getPrompt(Stack<String> domainStack, String currentDomain,String postFix) {
        StringBuffer pathPrompt = new StringBuffer();
        for(int i=0;i<domainStack.size();i++){
            String domStr = domainStack.get(i);
            if(!domStr.isEmpty()){
                pathPrompt.append(domStr);
            }
            if(i < domainStack.size()) {
                pathPrompt.append("/");
            }
        }
        if(currentDomain.isEmpty()){
            pathPrompt.append("/");
        }else {
            pathPrompt.append(currentDomain);
        }
        pathPrompt.append(postFix);
        return pathPrompt.toString();
    }

    private String getFullyQualifiedDomain(Stack<String> domainStack, String currentDomain) {
        StringBuffer fullyQualifiedDomain = new StringBuffer();
        for(int i=0;i<domainStack.size();i++){
            String domStr = domainStack.get(i);
            if(!domStr.isEmpty()){
                fullyQualifiedDomain.append(domStr);
                if(i < domainStack.size()) {
                    fullyQualifiedDomain.append(".");
                }
            }
        }

        if(!currentDomain.isEmpty()){
            fullyQualifiedDomain.append(currentDomain);
        }

        return fullyQualifiedDomain.toString();
    }

    private void listOps(TextTerminal textTerminal, List<String> args) {
        String fullyQualifiedDomain = getFullyQualifiedDomain(domainStack, currentDomain);
        //log.info("FCN {}",fullyQualifiedDomain);
        List<String> operationsAt = domainService.getOperationsAt(fullyQualifiedDomain);
        textTerminal.printf("%s",operationsAt);
        textTerminal.resetLine();
    }

    private void listDomains(TextTerminal<?> textTerminal, List<String> args) {
        String fullyQualifiedDomain = getFullyQualifiedDomain(domainStack, currentDomain);
        //log.info("FCN {}",fullyQualifiedDomain);
        List<String> domainNamesAt = domainService.getDomainNamesAt(fullyQualifiedDomain);
        textTerminal.printf("%s",domainNamesAt);
        textTerminal.resetLine();
    }

    @Override
    public void stop() {
        log.debug("Stopping Interactive shell...");
        if(textIO != null){
            domainStack.clear();
            textIO.dispose();
        }

    }
}
