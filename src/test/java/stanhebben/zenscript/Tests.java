package stanhebben.zenscript;


import org.junit.jupiter.api.*;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.impl.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tests {
    
    public static List<String> prints = new LinkedList<>();
    
    
     public static GenericRegistry registry;
     public static GenericErrorLogger logger;
     public static GenericCompileEnvironment compileEnvironment;
    
    @BeforeAll
    public static void setupEnvironment() {
        compileEnvironment = new GenericCompileEnvironment();
        logger = new GenericErrorLogger(System.out);
        registry = new GenericRegistry(compileEnvironment, logger);
        registry.registerGlobal("print", registry.getStaticFunction(Tests.class, "print", String.class));
    }
    
    @Test
    public void testCompile() {
        try {
            ZenModule module = ZenModule.compileScriptString("print(\"test\");", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: " + "test.zs" + ": " + ex.getMessage(), ex);
        }
        
        assertTrue(prints.get(0).equals("tests"));
        prints.clear();
    }
    
    
    public static void print(String value) {
        prints.add(value);
    }
    
    
}
