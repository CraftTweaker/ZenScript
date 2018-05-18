package stanhebben.zenscript.cli;

import stanhebben.zenscript.*;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.impl.*;
import stanhebben.zenscript.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class CLIEntryPoint {
    
    public static void main(String[] args) {
        lint(new String[]{"print(\"test\");\n" + "if (true) print('test2' + <ore:iron>);\n" + "test.magic();\n" + "\n"});
        
        if(true)
            return;
        
        if(args.length == 0) {
            printHelp();
            return;
        }
        
        switch(args[0]) {
            case "help":
                printHelp();
                return;
            case "lint":
                lint(Arrays.copyOfRange(args, 1, args.length));
                return;
            case "compile":
                compile(Arrays.copyOfRange(args, 1, args.length));
                return;
        }
        
        System.out.println("No correct input given, run 'zs help' for help");
    }
    
    public static void printHelp() {
        System.out.println("Commads:\n" + "\thelp\n" + "\tlint\n" + "\tcompile\n");
    }
    
    public static void lint(String[] args) {
        if(args.length < 1) {
            System.err.println("Too little params");
            return;
        }
        
        System.out.println("Loading scripts");
        // CRT_LOADING_STARTED_EVENT_EVENT_LIST.publish(new CrTLoadingStartedEvent(loaderName, isSyntaxCommand, networkSide));
        // preprocessorManager.clean();
        
        Set<String> executed = new HashSet<>();
        boolean loadSuccessful = true;
        
        // List<ScriptFile> scriptFiles = collectScriptFiles(isSyntaxCommand);
        
        // preprocessor magic
        /*for(ScriptFile scriptFile : scriptFiles) {
            scriptFile.addAll(preprocessorManager.checkFileForPreprocessors(scriptFile));
        }
    
        scriptFiles.sort(PreprocessorManager.SCRIPT_FILE_COMPARATOR);
*/
        
        Map<String, byte[]> classes = new HashMap<>();
        
        GenericCompileEnvironment compileEnvo = new GenericCompileEnvironment();
        GenericErrorLogger errorLogger = new GenericErrorLogger(System.out);
        GenericRegistry registry = new GenericRegistry(compileEnvo, errorLogger);
        IEnvironmentGlobal environmentGlobal = new GenericGlobalEnvironment(classes, registry);
        registry.registerGlobal("print", registry.getStaticFunction(CLIEntryPoint.class, "print", String.class));
        
        try {
            ZenTokener parser = new ZenTokener(args[0], environmentGlobal.getEnvironment(), "fallback_name", false);
            ZenParsedFile zenParsedFile = new ZenParsedFile("fallback_name", "TempClass", parser, environmentGlobal);
            
            ZenModule.compileScripts("TempClass", Collections.singletonList(zenParsedFile), environmentGlobal, true);
            
            
            /* ZenModule module = new ZenModule(classes, Thread.currentThread().getContextClassLoader());

            
           
            Runnable runnable = module.getMain();
            runnable.run();
            
            */
        } catch(ParseException e) {
            System.out.println("in " + e.getFile() + " in line " + e.getLine() + " at pos " + e.getLineOffset() + ". Reason: " + e.getExplanation());
        } catch(IOException e) {
            
            e.printStackTrace();
        }
    }
    
    public static void compile(String[] args) {
    
    }
    
    public static void print(String s) {
        System.out.println(s);
    }
}
