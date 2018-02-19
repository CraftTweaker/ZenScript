package stanhebben.zenscript;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.impl.*;

import java.io.*;
import java.util.*;

import static stanhebben.zenscript.ZenModule.compileScripts;

public class MainTest {
    
    public static final String fileName = "stringfile";
    
    public static void main(String[] args) throws IOException {
        GenericCompileEnvironment compileEnvironment = new GenericCompileEnvironment();
        GenericErrorLogger logger = new GenericErrorLogger(System.out);
        GenericRegistry registry = new GenericRegistry(compileEnvironment, logger);
        registry.registerGlobal("print", registry.getStaticFunction(GenericFunctions.class, "print", String.class));
        Map<String, byte[]> classes = new HashMap<>();
        IEnvironmentGlobal environmentGlobal = registry.makeGlobalEnvironment(classes);
        
        
        ZenTokener parser = new ZenTokener(new FileReader("script.zs"), registry.getCompileEnvironment(), fileName, false);
        ZenParsedFile zenParsedFile = new ZenParsedFile(fileName, fileName, parser, environmentGlobal);
        
        
        try {
            // Stops if the compile is disabled
            ZenModule.compileScripts(fileName, Collections.singletonList(zenParsedFile), environmentGlobal, false);
            
            ZenModule module = new ZenModule(classes, MainTest.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: " + fileName + ": " + ex.getMessage(), ex);
        }
        
    }
}
