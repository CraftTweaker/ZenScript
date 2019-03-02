package stanhebben.zenscript;

import org.junit.jupiter.api.Test;
import stanhebben.zenscript.impl.GenericCompileEnvironment;
import stanhebben.zenscript.impl.GenericErrorLogger;
import stanhebben.zenscript.impl.GenericRegistry;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class TestHelper {
    
    public static List<String> prints = new LinkedList<>();
    public static GenericRegistry registry;
    public static GenericErrorLogger logger;
    public static GenericCompileEnvironment compileEnvironment;
    
    private TestHelper() {
    }
    
    public static void beforeEach() {
        prints.clear();
        ZenModule.classes.clear();
        ZenModule.loadedClasses.clear();
    }
    
    public static void setupEnvironment() {
        compileEnvironment = new GenericCompileEnvironment();
        logger = new GenericErrorLogger(System.out);
        registry = new GenericRegistry(compileEnvironment, logger);
        registry.registerGlobal("print", registry.getStaticFunction(TestHelper.class, "print", String.class));
    }
    
    public static void print(String s) {
        prints.add(s);
    }
    
    public static void run(String content) {
        try {
            ZenModule module = ZenModule.compileScriptString(content, "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null) {
                runnable.run();
            }
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
    }
}
