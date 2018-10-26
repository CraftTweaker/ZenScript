package stanhebben.zenscript;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.impl.GenericCompileEnvironment;
import stanhebben.zenscript.impl.GenericErrorLogger;
import stanhebben.zenscript.impl.GenericRegistry;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFunctions {
    
    public static List<String> prints = new LinkedList<>();
    
    
    public static GenericRegistry registry;
    public static GenericErrorLogger logger;
    public static GenericCompileEnvironment compileEnvironment;
    
    @SuppressWarnings("unused")
    public static void print(String value) {
        prints.add(value);
    }
    
    @BeforeAll
    public static void setupEnvironment() {
        compileEnvironment = new GenericCompileEnvironment();
        logger = new GenericErrorLogger(System.out);
        registry = new GenericRegistry(compileEnvironment, logger);
        registry.registerGlobal("print", registry.getStaticFunction(TestFunctions.class, "print", String.class));
    }
    
    private static void assertMany(String... lines) {
        for(int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], prints.get(i));
        }
    }
    
    @BeforeEach
    public void clearPrints() {
        prints.clear();
    }
    
    @BeforeEach
    public void clearClasses() {
        ZenModule.classes.clear();
        ZenModule.loadedClasses.clear();
    }
    
    @Test
    public void Test_NestedFunctions() {
        try {
            ZenModule module = ZenModule.compileScriptString("tens();  realTens(\"Hello World!\"); function tens(){     realTens(\"a\"); }  function realTens(a as string){     for i in 1 to 11{         print(a);     } }", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        String[] expectedValues = new String[20];
        Arrays.fill(expectedValues, 0, 10, "a");
        Arrays.fill(expectedValues, 10, 20, "Hello World!");
        assertMany(expectedValues);
    }
    
    @Test
    public void Test_FunctionDeclaredAfterCall() {
        try {
            ZenModule module = ZenModule.compileScriptString("val result = add(1,99); print(result);  print(add(2,64));  function add(a as int,b as int) as int{     return a+b; }", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertMany("100", "66");
        
    }
    
    @Test
    public void Test_FunctionLargeType() {
        try {
            ZenModule module = ZenModule.compileScriptString("function test(a as double, b as bool) as double {return b ? a : 10.0D;} print(test(3.0D, true)); print(test(3.0D, false));", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        assertMany("3.0", "10.0");
    }
}
