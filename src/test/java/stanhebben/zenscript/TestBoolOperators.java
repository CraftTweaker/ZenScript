package stanhebben.zenscript;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.impl.GenericCompileEnvironment;
import stanhebben.zenscript.impl.GenericErrorLogger;
import stanhebben.zenscript.impl.GenericRegistry;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBoolOperators {
    
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
        registry.registerGlobal("print", registry.getStaticFunction(TestBoolOperators.class, "print", String.class));
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
    public void TestAnd() {
        try {
            ZenModule module = ZenModule.compileScriptString("var tests = [true & false, false & false, false & true, true & true] as bool[]; for t in tests print(t);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        assertEquals("false", prints.get(0));
        assertEquals("false", prints.get(1));
        assertEquals("false", prints.get(2));
        assertEquals("true", prints.get(3));
    }
    
    @Test
    public void TestOr() {
        try {
            ZenModule module = ZenModule.compileScriptString("var tests = [true | false, false | false, false | true, true | true] as bool[]; for t in tests print(t);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        assertEquals("true", prints.get(0));
        assertEquals("false", prints.get(1));
        assertEquals("true", prints.get(2));
        assertEquals("true", prints.get(3));
    }
    
    @Test
    public void TestAndAnd() {
        try {
            ZenModule module = ZenModule.compileScriptString("var tests = [true && false, false && false, false && true, true && true] as bool[]; for t in tests print(t);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        assertEquals("false", prints.get(0));
        assertEquals("false", prints.get(1));
        assertEquals("false", prints.get(2));
        assertEquals("true", prints.get(3));
    }
    
    @Test
    public void TestOrOr() {
        try {
            ZenModule module = ZenModule.compileScriptString("var tests = [true || false, false || false, false || true, true || true] as bool[]; for t in tests print(t);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        assertEquals("true", prints.get(0));
        assertEquals("false", prints.get(1));
        assertEquals("true", prints.get(2));
        assertEquals("true", prints.get(3));
    }
    
    @Test
    public void TestAndAndOrder() {
        try {
            ZenModule module = ZenModule.compileScriptString("function methodTrue() as bool {print('methodTrue'); return true;} function methodFalse() as bool {print('methodFalse'); return false;} function printFin(a as bool) as void {print(a); print('Fin');}" +
                    "printFin(methodTrue() && methodTrue()); printFin(methodTrue() && methodFalse()); printFin(methodFalse() && methodTrue()); printFin(methodFalse() && methodFalse()); ",
                    "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        assertMany("methodTrue", "methodTrue", "true", "Fin",
                "methodTrue", "methodFalse", "false", "Fin",
                "methodFalse", "false", "Fin",
                "methodFalse", "false", "Fin");
    }
    
    @Test
    public void TestOrOrOrder() {
        try {
            ZenModule module = ZenModule.compileScriptString("function methodTrue() as bool {print('methodTrue'); return true;} function methodFalse() as bool {print('methodFalse'); return false;} function printFin(a as bool) as void {print(a); print('Fin');}" +
                            "printFin(methodTrue() || methodTrue()); printFin(methodTrue() || methodFalse()); printFin(methodFalse() || methodTrue()); printFin(methodFalse() || methodFalse()); ",
                    "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        assertMany("methodTrue", "true", "Fin",
                "methodTrue", "true", "Fin",
                "methodFalse", "methodTrue", "true", "Fin",
                "methodFalse", "methodFalse", "false", "Fin");
        
        
    }
    
    @Test
    public void TestAndOrder() {
        try {
            ZenModule module = ZenModule.compileScriptString("function methodTrue() as bool {print('methodTrue'); return true;} function methodFalse() as bool {print('methodFalse'); return false;} function printFin(a as bool) as void {print(a); print('Fin');}" +
                            "printFin(methodTrue() & methodTrue()); printFin(methodTrue() & methodFalse()); printFin(methodFalse() & methodTrue()); printFin(methodFalse() & methodFalse()); ",
                    "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        assertMany("methodTrue", "methodTrue", "true", "Fin",
                "methodTrue", "methodFalse", "false", "Fin",
                "methodFalse", "methodTrue", "false", "Fin",
                "methodFalse", "methodFalse", "false", "Fin");
    }
    
    @Test
    public void TestOrOrder() {
        try {
            ZenModule module = ZenModule.compileScriptString("function methodTrue() as bool {print('methodTrue'); return true;} function methodFalse() as bool {print('methodFalse'); return false;} function printFin(a as bool) as void {print(a); print('Fin');}" +
                            "printFin(methodTrue() | methodTrue()); printFin(methodTrue() | methodFalse()); printFin(methodFalse() | methodTrue()); printFin(methodFalse() | methodFalse()); ",
                    "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        assertMany("methodTrue", "methodTrue", "true", "Fin",
                "methodTrue", "methodFalse", "true", "Fin",
                "methodFalse", "methodTrue", "true", "Fin",
                "methodFalse", "methodFalse", "false", "Fin");
        
        
    }
    
    
    private static void assertMany(String... lines) {
        for(int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], prints.get(i));
        }
    }
}
