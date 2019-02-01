package org.molecule.mods.ishell;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.*;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.molecule.commons.Constants;
import org.molecule.system.*;
import org.molecule.system.services.DomainService;
import org.molecule.system.services.FnBus;
import org.mvel2.MVEL;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import static org.molecule.mods.ishell.IShellConstants.EXIT_SYSTEM;

@Slf4j
class JLineInteractiveShell implements Shell{

    static final String SHELL_STACK = "org.molecule.mods.ishell.jline.shell-stack";
    static final String ROOT_DOMAIN = "";

    private String[] commandsList;

    private DomainService domainService;
    static String defaultPrompt = "> ";
    private Completer domainOpCompleter;
    //private String currentDomain = "";


    private Stack<String> domainStack;
    //private EvictingQueue<String> commandHistory = EvictingQueue.create(50);

    private Pattern pattern = Pattern.compile(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    //private Pattern pattern = Pattern.compile(" (?=(?:[^\"]*\\[(.\\s\\d)*\\])*[^\"]*$)");
    //private Pattern pattern = Pattern.compile(" (?=(?:[^\"]*\\[[^\"]*\\])*[^\"]*$)");
    //private Pattern pattern = Pattern.compile(" ");
    private Splitter splitter = Splitter.on(pattern).trimResults().omitEmptyStrings();
    private FnBus fnBus;

    @Inject
    JLineInteractiveShell(DomainService domainService, FnBus fnBus,@Named(JLineInteractiveShell.SHELL_STACK) Stack<String> domainStack){
        commandsList = new String[]{"help","domains","ops","cd","pwd","exec","exit"};
        this.domainService = domainService;
        this.fnBus = fnBus;
        this.domainStack = domainStack;
        this.domainStack.push(ROOT_DOMAIN);
        this.domainOpCompleter = new DomainOperationsCompleter(domainService,domainStack);
    }

    @Override
    public void start(String[] args) {
        AnsiConsole.systemInstall(); // needed to support ansi on Windows cmd
        //printWelcomeMessage();
        LineReaderBuilder readerBuilder = LineReaderBuilder.builder();
        List<Completer> completors = new LinkedList<Completer>();

        //completors.add(new StringsCompleter(commandsList));
        completors.add(domainOpCompleter);
        readerBuilder.completer(new ArgumentCompleter(completors));

        LineReader reader = readerBuilder.build();

        String line;
        PrintWriter out = new PrintWriter(System.out);

        boolean active = true;

        while (active && (line = readLine(reader, defaultPrompt)) != null) {

            List<String> input = splitter.splitToList(line);
            if(input != null && !input.isEmpty()) {
                String command = input.get(0);
                List<String> commandArguments = new ArrayList();
                if(input.size() > 1){
                    commandArguments = input.subList(1,input.size());
                }
                active = executeCommandOnFnBus(command,commandArguments);
            }
            /*if ("help".equals(line)) {
                printHelp();
            } else if ("domains".equals(line)) {
                AttributedStringBuilder a = new AttributedStringBuilder()
                        .append("You have selected ")
                        .append("domains", AttributedStyle.BOLD.foreground(AttributedStyle.RED))
                        .append("!");

                System.out.println(a.toAnsi());
            } else if ("action2".equals(line)) {
                AttributedStringBuilder a = new AttributedStringBuilder()
                        .append("You have selected ")
                        .append("ops", AttributedStyle.BOLD.foreground(AttributedStyle.RED))
                        .append("!");

                System.out.println(a.toAnsi());
            } else if ("exit".equals(line)) {
                System.out.println("Exiting application");
                return;
            } else {
                System.out
                        .println("Invalid command, For assistance press TAB or type \"help\" then hit ENTER.");
            }*/
        }


    }

    private boolean executeCommandOnFnBus(String command,List<String> args) {
        boolean shouldContinue = true;
        try {
            shouldContinue = executeCommandOnRootDomain(command, args);
        }catch(InvalidOperationException e){
            try {
                executeCommandOnCurrentDomain(command, args);
            }catch(InvalidOperationException ex){
                System.out
                        .println("Invalid command, For assistance press TAB or type \"help\" then hit ENTER.");
            }
        }
        return shouldContinue;
    }

    private void executeCommandOnCurrentDomain(String command, List<String> args) throws InvalidOperationException {
        String fullyQualifiedCurrentDomain = getFullyQualifiedDomain(domainStack);
        List<String> rootOperationList = domainService.getOperationsAt(fullyQualifiedCurrentDomain);
        if(rootOperationList.contains(command)){
            Operation operation = domainService.getOperation(command);
            Param inParam = new InOutParam();
            inParam = inParam.plus(Constants.FUNCTION_TO_INVOKE, operation.getFunctionURI());
            Param outParam = fnBus.apply(inParam);
            log.info("Received Out {}",outParam);
        }else{
            throw new InvalidOperationException(String.format("Operation %s is invalid for domain %s",command,fullyQualifiedCurrentDomain));
        }
    }

    private boolean executeCommandOnRootDomain(String command,List<String> args) throws InvalidOperationException {
        boolean retVal = true;
        List<String> rootOperationList = domainService.getOperationsAt("");
        if(rootOperationList.contains(command)){
            Operation operation = domainService.getOperation(command);
            Param inParam = new InOutParam();
            inParam = inParam.plus(Constants.FUNCTION_TO_INVOKE, operation.getFunctionURI());
            inParam = inParam.plus(Constants.IN_PARAMS,args);
            Param outParam = fnBus.apply(inParam);
            if(outParam.containsKey(EXIT_SYSTEM)){
                retVal = (Boolean)outParam.get(EXIT_SYSTEM);
            }
            //log.info("Received Out {}",outParam);
        }else{
            throw new InvalidOperationException(String.format("Operation %s is invalid for domain %s",command,"/"));
        }
        return retVal;
    }

    /*private void printHelp() {
        int helpFormattingWidth = 15;
        System.out.println(getFormattedHelpMessage(helpFormattingWidth,"help","Show help"));

        System.out.println(getFormattedHelpMessage(helpFormattingWidth,"domains","List all valid accessible domains within the current active domain"));
        System.out.println(getFormattedHelpMessage(helpFormattingWidth,"ops","List all valid accessible operations within the current active domain"));
        System.out.println(getFormattedHelpMessage(helpFormattingWidth,"cd","Change to the specified domain"));
        System.out.println(getFormattedHelpMessage(helpFormattingWidth,"pwd","Prints the present working domain"));
        System.out.println(getFormattedHelpMessage(helpFormattingWidth,"exec","Executes the specified operation withing the current active domain"));
        System.out.println(getFormattedHelpMessage(helpFormattingWidth,"exit","Exits the system"));
        System.out.println(getFormattedHelpMessage(helpFormattingWidth,"shutdown","Shutdown the system"));
        System.out.println(getFormattedHelpMessage(helpFormattingWidth,"quit","Exits the system"));

    }*/


    @Override
    public void stop() {
        AnsiConsole.systemUninstall();

    }
    private String getPrompt_working(Stack<String> domainStack, String currentDomain,String postFix) {
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

    static String getPrompt(Stack<String> domainStack,String postFix) {
        StringBuffer pathPrompt = new StringBuffer();
        for(int i=0;i<domainStack.size();i++){

            String domStr = domainStack.get(i);

            if(!domStr.isEmpty()){
                pathPrompt.append(domStr);
                if((i+1)<domainStack.size()){
                    pathPrompt.append("/");
                }
            }else{
                pathPrompt.append("/");
            }
        }

        pathPrompt.append(postFix);
        return pathPrompt.toString();
    }

    private String readLine(LineReader reader, String postFix) {
        try {

            String line = reader.readLine(getPrompt(domainStack,postFix));
            return line.trim();
        }
        catch (UserInterruptException e) {
            // e.g. ^C
            return null;
        }
        catch (EndOfFileException e) {
            // e.g. ^D
            return null;
        }
    }

    /*private boolean executeCommand(String commandLine) {

        boolean retVal = true;
        List<String> input = splitter.splitToList(commandLine);
        if(input != null && !input.isEmpty()) {
            String commandName = input.get(0);
            List<String> args = input.subList(1, input.size());

            if (commandName.equalsIgnoreCase("domains")) {
                listDomains(args);
            } else if (commandName.equalsIgnoreCase("ops")) {
                listOps(args);
            } else if (commandName.equalsIgnoreCase("cd")) {
                changeDomain(args);
            } else if (commandName.equalsIgnoreCase("pwd")) {
                printCurrentDomain(args);
            } else if (commandName.equalsIgnoreCase("exec")) {
                executeOperation(args);
            } else if (hasToExit(commandName)) {
                exit(args);
                retVal = false; //on exit get out of the command prompt looping
            } else if (commandName.equalsIgnoreCase("help")) {
                printHelp();
            } else {
                System.out
                        .println("Invalid command, For assistance press TAB or type \"help\" then hit ENTER.");
            }
        }

        return retVal;
    }*/

    private boolean hasToExit(String commandName) {
        boolean retVal = false;
        if(commandName.equalsIgnoreCase("exit")
                || commandName.equalsIgnoreCase("quit")
                || commandName.equalsIgnoreCase("shutdown")){
            retVal = true;
        }
        return retVal;
    }

    private void executeOperation(List<String> args) {
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

        String fullyQualifiedDomainName = getFullyQualifiedDomain(domainStack);

        if(!domainService.isValidOperationAt(fullyQualifiedDomainName,operationName)){
            printf("No such operation '%s' found under '%s'",operationName,getPrompt(domainStack,""));
        }else{
            if(operationParams != null) {
                //log.info("PREQUOTE IN {}", operationParams);
                //String opParams = quoteParams(operationParams);
                String opParams = unQuoteParams(operationParams);
                //log.info("MVEL IN {}", opParams);

                Object expressionObj = MVEL.eval(opParams);
                printf("Params %s, %s", expressionObj.getClass(), expressionObj);
                Operation operation = domainService.getOperationAt(fullyQualifiedDomainName,operationName);
                URI functionURI = operation.getFunctionURI();
                printf("About to invoke function %s",functionURI);
            }
        }
        //textTerminal.printf("Operation Name %s, Args %s",operationName,operationParams);


    }

    private void printf(String formattingText, Object... args) {
        System.out.println(String.format(formattingText,args));
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

    private void exit( List<String> args) {
        printf("Bye! Bye!...");
    }

    private void printCurrentDomain(List<String> args) {
        printf("%s",getPrompt(domainStack,""));
    }

    private void changeDomain(List<String> args) {
        String domainToChange = args.get(0);

        List<String> domainNamesAt = domainService.getDomainNamesAt(getFullyQualifiedDomain(domainStack));

        if(isNavigationToPrevious(domainToChange)){
            if(domainStack.size() > 0){
                domainStack.pop();
            }// if not we are already in the root so nothing to do
        }else if(isNavigationToRoot(domainToChange)){
            domainStack.clear();
            domainStack.push(ROOT_DOMAIN);
            //currentDomain = "";
        }else if(domainNamesAt.contains(domainToChange)){
            //domainStack.push(currentDomain);
            //currentDomain = domainToChange;
            domainStack.push(domainToChange);
        }else{
            printf("no such domain %s",domainToChange);
        }
    }

    private boolean isNavigationToRoot(String domainToChange) {
        return domainToChange.equalsIgnoreCase("/");
    }

    private boolean isNavigationToPrevious(String domainToChange) {
        return domainToChange.equalsIgnoreCase("..");
    }


    private String getFullyQualifiedDomain_working(Stack<String> domainStack, String currentDomain) {
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

        //if(!currentDomain.isEmpty()){
        //    fullyQualifiedDomain.append(currentDomain);
        //}

        return fullyQualifiedDomain.toString();
    }

    static String getFullyQualifiedDomain(Stack<String> domainStack) {
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

        //if(!currentDomain.isEmpty()){
        //    fullyQualifiedDomain.append(currentDomain);
        //}

        return fullyQualifiedDomain.toString();
    }

    private void listOps(List<String> args) {
        String fullyQualifiedDomain = getFullyQualifiedDomain(domainStack);
        //log.info("FCN {}",fullyQualifiedDomain);
        List<String> operationsAt = domainService.getOperationsAt(fullyQualifiedDomain);
        printf("%s",operationsAt);
    }

    private void listDomains(List<String> args) {
        String fullyQualifiedDomain = getFullyQualifiedDomain(domainStack);
        //log.info("FCN {}",fullyQualifiedDomain);
        List<String> domainNamesAt = domainService.getDomainNamesAt(fullyQualifiedDomain);
        printf("%s",domainNamesAt);
    }

}
