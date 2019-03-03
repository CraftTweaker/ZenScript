package stanhebben.zenscript;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.impl.GenericCompileEnvironment;
import stanhebben.zenscript.impl.GenericRegistry;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class TestHelper {
    
    public static List<String> prints = new LinkedList<>();
    public static GenericRegistry registry;
    public static TestErrorLogger logger;
    public static GenericCompileEnvironment compileEnvironment;
    
    private TestHelper() {
    }
    
    public static void beforeEach() {
        prints.clear();
        logger.clear();
        ZenModule.classes.clear();
        ZenModule.loadedClasses.clear();
    }
    
    public static void setupEnvironment() {
        compileEnvironment = new GenericCompileEnvironment();
        logger = new TestErrorLogger();
        registry = new GenericRegistry(compileEnvironment, logger);
        registry.registerGlobal("print", registry.getStaticFunction(TestHelper.class, "print", String.class));
    }
    
    public static void print(String s) {
        prints.add(s);
    }
    
    public static void run(String content) {
        run(content, false, true, false);
    }
    
    public static void run(String content, boolean allowWarnings, boolean allowInformation, boolean allowErrors) {
        try {
            ZenModule module = ZenModule.compileScriptString(content, "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null) {
                runnable.run();
            }
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        boolean worked = true;
        if(!allowInformation && !logger.listInfo.isEmpty()) {
            for(String s : logger.listInfo)
                System.err.println(s);
            worked = false;
        }
        if(!allowWarnings && !logger.listWarning.isEmpty()) {
            for(String s : logger.listWarning)
                System.err.println(s);
            worked = false;
        }
        if(!allowErrors && !logger.listError.isEmpty()) {
            for(String s : logger.listError)
                System.err.println(s);
            worked = false;
        }
        Assertions.assertTrue(worked);
    }
}
