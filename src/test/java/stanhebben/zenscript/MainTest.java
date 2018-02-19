package stanhebben.zenscript;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.impl.*;

import java.io.*;
import java.util.*;

import static stanhebben.zenscript.ZenModule.compileScripts;

public class MainTest {
    
    
    public static void main(String[] args) throws IOException {
        //Create a compile environment needed for the registry
        GenericCompileEnvironment compileEnvironment = new GenericCompileEnvironment();
        //Creates a logger needed for the registry
        GenericErrorLogger logger = new GenericErrorLogger(System.out);
        //Creates the IZenRegistry, needed to store all the ZenClass' and Symbols
        GenericRegistry registry = new GenericRegistry(compileEnvironment, logger);
        //Registers A print function as a global method
        registry.registerGlobal("print", registry.getStaticFunction(GenericFunctions.class, "print", String.class));
        //Creates a IEnvironmentGlobal needed to compile the scripts
        Map<String, byte[]> classes = new HashMap<>();
        IEnvironmentGlobal environmentGlobal = registry.makeGlobalEnvironment(classes);
    
        //Loads the script file
        File file = new File("script.zs");
        String fileName = file.getName();
        FileReader reader = new FileReader(file);
        //Creates a ZenTokener and ZenParsedFile for the file
        ZenTokener parser = new ZenTokener(reader, registry.getCompileEnvironment(), fileName, false);
        ZenParsedFile zenParsedFile = new ZenParsedFile(fileName, fileName, parser, environmentGlobal);
        
        
        try {
            //Compiles and runs the script
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
