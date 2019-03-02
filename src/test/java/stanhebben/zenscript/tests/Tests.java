package stanhebben.zenscript.tests;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestHelper;

import static stanhebben.zenscript.TestAssertions.assertCalculated;
import static stanhebben.zenscript.TestAssertions.assertMany;
import static stanhebben.zenscript.TestAssertions.assertOne;

@SuppressWarnings("WeakerAccess")
public class Tests {
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }
    
    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach();
    }
    
    @Test
    public void testCompile() {
        TestHelper.run("var x = 0; x += 5;");
    }
    
    @Test
    public void testPrint() {
        TestHelper.run("var x = 0; x += 5; print(x);");
        assertOne("5", 0);
    }
    
    @Test
    public void testLoop() {
        TestHelper.run("for i in 0 .. 10 { print(i); } for i in 10 to 20 {print(i); }");
        
        assertCalculated(Integer::toString, 20);
    }
    
    @Test
    public void testArrays() {
        TestHelper.run("val stringArray = [\"Hello\",\"World\",\"I\",\"am\"] as string[];\n\nprint(stringArray[0]);\n\n\n\nval stringArray1 = [\"Hello\",\"World\"] as string[];\nval stringArray2 = [\"I\",\"am\"] as string[];\nval stringArray3 = [\"a\",\"beautiful\"] as string[];\nval stringArrayAll = [stringArray1,stringArray2,stringArray3,[\"Butterfly\",\"!\"]] as string[][];\n\nprint(stringArrayAll[0][1]);");
        assertOne("Hello", 0);
        assertOne("World", 1);
    }
    
    @Test
    public void testArrayLoop() {
        TestHelper.run("val stringArray = [\"Hello\",\"World\",\"I\",\"am\"] as string[];\n\nprint(stringArray[0]);\n\n\n\nval stringArray1 = [\"Hello\",\"World\"] as string[];\nval stringArray2 = [\"I\",\"am\"] as string[];\nval stringArray3 = [\"a\",\"beautiful\"] as string[];\nval stringArrayAll = [stringArray1,stringArray2,stringArray3,[\"Butterfly\",\"!\"]] as string[][];\n\nprint(stringArrayAll[0][1]); for item in stringArray {print(item); }");
        
        assertMany("Hello", "World", "Hello", "World", "I", "am");
    }
    
    @Test
    public void testArrayAddition() {
        TestHelper.run("var strArr = [\"hello\"] as string[]; strArr += \"world\"; for item in strArr {print(item);}");
        
        assertMany("hello", "world");
    }
    
    @Test
    public void testMaps() {
        TestHelper.run("val assocWithStrings = {\n    //you can use \"\" if you want\n    \"one\" : \"1\",\n\n    //but you don't have to\n    two : \"2\"\n} as string[string];\n\n//You can either use the memberGetter\nprint(assocWithStrings.one);\n\n//Or the standard index Getter\nprint(assocWithStrings[\"two\"]);");
        
        assertMany("1", "2");
    }
    
    @Test
    public void testCalculations() {
        TestHelper.run("print(\"Hello\" ~ \" \" ~ \"World\"); if(3+1 == 2*2) {print(\"Used a calculation!\");} print(0x7fffffffffffffff);");
        
        assertOne("Hello World", 0);
        assertOne("Used a calculation!", 1);
        assertOne("9223372036854775807", 2);
    }
    
    @Test
    public void testConditionals() {
        TestHelper.run("val a = 0 as int; if(a==0){print(\"NumVal\");}  val b = 1; val c = 5; if(b+c==6){print(\"Num1!\");} if(b*c==5){print(\"Num2!\");} if(b/c==0.2){print(\"Num3!\");}  val d = \"Hello\"; val e = \"World\"; val f = d~e; if(d==\"Hello\" | e == \"Hello\"){print(\"OR1!\");} if(d==\"Hello\" | e == \"World\"){print(\"OR2!\");}  if(d==\"Hello\" ^ e == \"Hello\"){print(\"XOR1!\");} if(d==\"Hello\" ^ e == \"World\"){print(\"XOR2!\");}  if(d==\"Hello\" & e == \"Hello\"){print(\"AND1!\");} if(d==\"Hello\" & e == \"World\"){print(\"AND2!\");}");
        
        assertOne("NumVal", 0);
        assertOne("Num1!", 1);
        assertOne("Num2!", 2);
        assertOne("Num3!", 3);
        assertOne("OR1!", 4);
        assertOne("OR2!", 5);
        assertOne("XOR1!", 6);
        assertOne("AND2!", 7);
    }
    
    @Test
    public void testContains() {
        TestHelper.run("var checkThisString = \"Checking\" as string; var checkForThisString = \"ing\" as string; if (checkThisString in checkForThisString) { print(\"Yes\"); } else { print(\"No\"); }");
        
        assertOne("Yes", 0);
        
    }
    
    @Test
    public void testWhile() {
        TestHelper.run("var i = 0; while i < 10 {print(i); i += 1;} print(\"After loop: \" + i); while (i > 0) {if i == 5 break; print(i); i -= 1;} print(\"After loop 2: \" + i);");
        
        assertCalculated(Integer::toString, 10);
        
        
        assertOne("After loop: 10", 10);
        for(int i = 10; i > 5; i--)
            assertOne(Integer.toString(i), 21 - i);
        assertOne("After loop 2: 5", 16);
    }
    
    @Test
    public void testClasses() {
        TestHelper.run("zenClass name {\n" + "\tstatic myStatic as string = \"value\";\n" + "\tstatic otherStatic as string = \"value\";\n" + "\n" + "\tval nonStatic as string = \"123\";\n" + "\tval nonStaticTwo as string;\n" + "\n" + "\tzenConstructor(parameter as string, parameter2 as string) {\n" + "\t\tprint(\"TETETE\");\n" + "\t\tprint(parameter);\n" + "\t\tnonStaticTwo = parameter2;\n" + "\t}\n" + "\n" + "\tzenConstructor(parameter as string) {\n" + "\t\tprint(\"FFFFFF\");\n" + "\t}\n" + "\n" + "\tfunction myMethod(arg as string, arg1 as string) as string {\n" + "\t\treturn \"value\" + arg ~ arg1;\n" + "\t}\n" + "\n" + "}\n" + "\n" + "var test = name(\"NOPE\");\n" + "test = name(\"nope\", \"noper\");\n" + "print(test.myMethod(\"one\", \"two\"));\n" + "print(name.myStatic);\n" + "print(name(\"parameter1\", \"parameter2\").nonStatic);\n" + "val ttt = name(\"t\");\n" + "ttt.myStatic = \"1\";\n" + "print(ttt.myStatic);\n" + "ttt.nonStatic = \"0\";\n" + "print(ttt.nonStatic);\n" + "print(name(\"MYParam1\", \"MyPAram2\").nonStaticTwo);");
        
        assertMany("FFFFFF", "TETETE", "nope", "valueonetwo", "value", "TETETE", "parameter1", "123", "FFFFFF", "1", "0", "TETETE", "MYParam1", "MyPAram2");
    }
    
    
}
