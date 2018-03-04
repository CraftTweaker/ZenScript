package stanhebben.zenscript;


import org.junit.jupiter.api.*;
import stanhebben.zenscript.impl.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    
    @BeforeEach
    public void clearPrints() {
        prints.clear();
    }
    
    @Test
    public void testCompile() {
        try {
            ZenModule module = ZenModule.compileScriptString("var x = 0; x += 5;", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
    }
    
    @Test
    public void testPrint() {
        try {
            ZenModule module = ZenModule.compileScriptString("var x = 0; x += 5; print(x);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("5", prints.get(0));
    }
    
    
    @Test
    public void testLoop() {
        try {
            ZenModule module = ZenModule.compileScriptString("for i in 0 .. 10 { print(i); } for i in 10 to 20 {print(i); }", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        for(int i = 0; i < 20; i++) {
            assertEquals(i, Integer.parseInt(prints.get(i)));
        }
    }
    
    @Test
    public void testArrays() {
        try {
            ZenModule module = ZenModule.compileScriptString("val stringArray = [\"Hello\",\"World\",\"I\",\"am\"] as string[];\n\nprint(stringArray[0]);\n\n\n\nval stringArray1 = [\"Hello\",\"World\"] as string[];\nval stringArray2 = [\"I\",\"am\"] as string[];\nval stringArray3 = [\"a\",\"beautiful\"] as string[];\nval stringArrayAll = [stringArray1,stringArray2,stringArray3,[\"Butterfly\",\"!\"]] as string[][];\n\nprint(stringArrayAll[0][1]);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("Hello", prints.get(0));
        assertEquals("World", prints.get(1));
    }
    
    @Test
    public void testArrayLoop() {
        try {
            ZenModule module = ZenModule.compileScriptString("val stringArray = [\"Hello\",\"World\",\"I\",\"am\"] as string[];\n\nprint(stringArray[0]);\n\n\n\nval stringArray1 = [\"Hello\",\"World\"] as string[];\nval stringArray2 = [\"I\",\"am\"] as string[];\nval stringArray3 = [\"a\",\"beautiful\"] as string[];\nval stringArrayAll = [stringArray1,stringArray2,stringArray3,[\"Butterfly\",\"!\"]] as string[][];\n\nprint(stringArrayAll[0][1]); for item in stringArray {print(item); }", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("Hello", prints.get(0));
        assertEquals("World", prints.get(1));
        assertEquals("Hello", prints.get(2));
        assertEquals("World", prints.get(3));
        assertEquals("I", prints.get(4));
        assertEquals("am", prints.get(5));
    }
    
    @Test
    public void testArrayAddition() {
        try {
            ZenModule module = ZenModule.compileScriptString("var strArr = [\"hello\"] as string[]; strArr += \"world\"; for item in strArr {print(item);}", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("hello", prints.get(0));
        assertEquals("world", prints.get(1));
    }
    
    @Test
    public void testMaps() {
        try {
            ZenModule module = ZenModule.compileScriptString("val assocWithStrings = {\n    //you can use \"\" if you want\n    \"one\" : \"1\",\n\n    //but you don't have to\n    two : \"2\"\n} as string[string];\n\n//You can either use the memberGetter\nprint(assocWithStrings.one);\n\n//Or the standard index Getter\nprint(assocWithStrings[\"two\"]);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("1", prints.get(0));
        assertEquals("2", prints.get(1));
    }
    
    @Test
    public void testCalculations() {
        try {
            ZenModule module = ZenModule.compileScriptString("print(\"Hello\" ~ \" \" ~ \"World\"); if(3+1 == 2*2) {print(\"Used a calculation!\");}", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("Hello World", prints.get(0));
        assertEquals("Used a calculation!", prints.get(1));
    }
    
    @Test
    public void testConditionals() {
        try {
            ZenModule module = ZenModule.compileScriptString("val a = 0 as int; if(a==0){print(\"NumVal\");}  val b = 1; val c = 5; if(b+c==6){print(\"Num1!\");} if(b*c==5){print(\"Num2!\");} if(b/c==0.2){print(\"Num3!\");}  val d = \"Hello\"; val e = \"World\"; val f = d~e; if(d==\"Hello\" | e == \"Hello\"){print(\"OR1!\");} if(d==\"Hello\" | e == \"World\"){print(\"OR2!\");}  if(d==\"Hello\" ^ e == \"Hello\"){print(\"XOR1!\");} if(d==\"Hello\" ^ e == \"World\"){print(\"XOR2!\");}  if(d==\"Hello\" & e == \"Hello\"){print(\"AND1!\");} if(d==\"Hello\" & e == \"World\"){print(\"AND2!\");}", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("NumVal", prints.get(0));
        assertEquals("Num1!", prints.get(1));
        assertEquals("Num2!", prints.get(2));
        assertEquals("Num3!", prints.get(3));
        assertEquals("OR1!", prints.get(4));
        assertEquals("OR2!", prints.get(5));
        assertEquals("XOR1!", prints.get(6));
        assertEquals("AND2!", prints.get(7));
    }
    
    @Test
    public void testFunctions() {
        try {
            ZenModule module = ZenModule.compileScriptString("tens();  realTens(\"Hello World!\");  function tens(){     realTens(\"a\"); }  function realTens(a as string){     for i in 1 to 11{         print(a);     } }", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("a", prints.get(0));
        assertEquals("a", prints.get(1));
        assertEquals("a", prints.get(2));
        assertEquals("a", prints.get(3));
        assertEquals("a", prints.get(4));
        assertEquals("a", prints.get(5));
        assertEquals("a", prints.get(6));
        assertEquals("a", prints.get(7));
        assertEquals("a", prints.get(8));
        assertEquals("a", prints.get(9));
        assertEquals("Hello World!", prints.get(10));
        assertEquals("Hello World!", prints.get(11));
        assertEquals("Hello World!", prints.get(12));
        assertEquals("Hello World!", prints.get(13));
        assertEquals("Hello World!", prints.get(14));
        assertEquals("Hello World!", prints.get(15));
        assertEquals("Hello World!", prints.get(16));
        assertEquals("Hello World!", prints.get(17));
        assertEquals("Hello World!", prints.get(18));
        assertEquals("Hello World!", prints.get(19));
    }
    
    @Test
    public void testFunctionsTwo() {
        try {
            ZenModule module = ZenModule.compileScriptString("val result = add(1,99); print(result);  print(add(2,64));  function add(a as int,b as int) as int{     return a+b; }", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("100", prints.get(0));
        assertEquals("66", prints.get(1));
        
    }
    
    
    @Test
    public void testContains(){
        try {
            ZenModule module = ZenModule.compileScriptString("var checkthisString = \"Checking\" as string; var checkforthisString = \"ing\" as string; if (checkthisString in checkforthisString) { print(\"Yes\"); } else { print(\"No\"); }", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
    
        assertEquals("Yes", prints.get(0));
    
    }
    
    public static void print(String value) {
        prints.add(value);
    }
    
    
}
