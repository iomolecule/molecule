package org.molecule.mods.ishell;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.molecule.system.Shell;
import org.molecule.system.services.DomainService;

import javax.inject.Inject;
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

    private Pattern pattern = Pattern.compile(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    private Splitter splitter = Splitter.on(pattern).trimResults().omitEmptyStrings();


    @Inject
    TextIOInteractiveShell(DomainService domainService){
        this.domainService = domainService;
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
        }else if(hasToExit(commandName)){
            exit(textTerminal,args);
        }else{
            textTerminal.printf("Unknown Command %s",commandName);
            textTerminal.resetLine();
        }
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
        log.info("FCN {}",fullyQualifiedDomain);
        List<String> operationsAt = domainService.getOperationsAt(fullyQualifiedDomain);
        textTerminal.printf("%s",operationsAt);
        textTerminal.resetLine();
    }

    private void listDomains(TextTerminal<?> textTerminal, List<String> args) {
        String fullyQualifiedDomain = getFullyQualifiedDomain(domainStack, currentDomain);
        log.info("FCN {}",fullyQualifiedDomain);
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
