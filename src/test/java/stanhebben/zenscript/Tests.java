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
    
    @SuppressWarnings("unused")
    public static void print(String value) {
        prints.add(value);
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
            ZenModule module = ZenModule.compileScriptString("print(\"Hello\" ~ \" \" ~ \"World\"); if(3+1 == 2*2) {print(\"Used a calculation!\");} print(0x7fffffffffffffff);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
            
            
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("Hello World", prints.get(0));
        assertEquals("Used a calculation!", prints.get(1));
        assertEquals("9223372036854775807", prints.get(2));
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
    public void testContains() {
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
    
    @Test
    public void testWhile() {
        try {
            ZenModule module = ZenModule.compileScriptString("var i = 0; while i < 10 {print(i); i += 1;} print(\"After loop: \" + i); while (i > 0) {if i == 5 break; print(i); i -= 1;} print(\"After loop 2: \" + i);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        
        for(int i = 0; i < 10; i++) {
            assertEquals(Integer.toString(i), prints.get(i));
        }
        assertEquals("After loop: 10", prints.get(10));
        for(int i = 10; i > 5; i--) {
            assertEquals(Integer.toString(i), prints.get(21 - i));
        }
        assertEquals("After loop 2: 5", prints.get(16));
    }
    
    @Test
    public void testClasses() {
        try {
            ZenModule module = ZenModule.compileScriptString("frigginClass name {\n" + "\tstatic myStatic as string = \"value\";\n" + "\tstatic otherStatic as string = \"value\";\n" + "\n" + "\tval nonStatic as string = \"123\";\n" + "\tval nonStaticTwo as string;\n" + "\n" + "\tfrigginConstructor(parameter as string, parameter2 as string) {\n" + "\t\tprint(\"TETETE\");\n" + "\t\tprint(parameter);\n" + "\t\tnonStaticTwo = parameter2;\n" + "\t}\n" + "\n" + "\tfrigginConstructor(parameter as string) {\n" + "\t\tprint(\"FFFFFF\");\n" + "\t}\n" + "\n" + "\tfunction myMethod(arg as string, arg1 as string) as string {\n" + "\t\treturn \"value\" + arg ~ arg1;\n" + "\t}\n" + "\n" + "}\n" + "\n" + "var test = name(\"NOPE\");\n" + "test = name(\"nope\", \"noper\");\n" + "print(test.myMethod(\"one\", \"two\"));\n" + "print(name.myStatic);\n" + "print(name(\"parameter1\", \"parameter2\").nonStatic);\n" + "val ttt = name(\"t\");\n" + "ttt.myStatic = \"1\";\n" + "print(ttt.myStatic);\n" + "ttt.nonStatic = \"0\";\n" + "print(ttt.nonStatic);\n" + "print(name(\"MYParam1\", \"MyPAram2\").nonStaticTwo);", "test.zs", compileEnvironment, Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null)
                runnable.run();
        } catch(Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
        assertEquals("FFFFFF", prints.get(0));
        assertEquals("TETETE", prints.get(1));
        assertEquals("nope", prints.get(2));
        assertEquals("valueonetwo", prints.get(3));
        assertEquals("value", prints.get(4));
        assertEquals("TETETE", prints.get(5));
        assertEquals("parameter1", prints.get(6));
        assertEquals("123", prints.get(7));
        assertEquals("FFFFFF", prints.get(8));
        assertEquals("1", prints.get(9));
        assertEquals("0", prints.get(10));
        assertEquals("TETETE", prints.get(11));
        assertEquals("MYParam1", prints.get(12));
        assertEquals("MyPAram2", prints.get(13));
    }
    
    
}
