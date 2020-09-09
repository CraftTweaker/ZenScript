package stanhebben.zenscript.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestHelper;

import static stanhebben.zenscript.TestAssertions.assertMany;

@SuppressWarnings("WeakerAccess")
public class TestClasses {
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }
    
    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach();
    }
    
    @Test
    public void testWhole() {
        TestHelper.run("zenClass name {\n" + "\tstatic myStatic as string = \"value\";\n" + "\tstatic otherStatic as string = \"value\";\n" + "\n" + "\tval nonStatic as string = \"123\";\n" + "\tval nonStaticTwo as string;\n" + "\n" + "\tzenConstructor(parameter as string, parameter2 as string) {\n" + "\t\tprint(\"TETETE\");\n" + "\t\tprint(parameter);\n" + "\t\tnonStaticTwo = parameter2;\n" + "\t}\n" + "\n" + "\tzenConstructor(parameter as string) {\n" + "\t\tprint(\"FFFFFF\");\n" + "\t}\n" + "\n" + "\tfunction myMethod(arg as string, arg1 as string) as string {\n" + "\t\treturn \"value\" + arg ~ arg1;\n" + "\t}\n" + "\n" + "}\n" + "\n" + "var test = name(\"NOPE\");\n" + "test = name(\"nope\", \"noper\");\n" + "print(test.myMethod(\"one\", \"two\"));\n" + "print(name.myStatic);\n" + "print(name(\"parameter1\", \"parameter2\").nonStatic);\n" + "val ttt = name(\"t\");\n" + "ttt.myStatic = \"1\";\n" + "print(ttt.myStatic);\n" + "ttt.nonStatic = \"0\";\n" + "print(ttt.nonStatic);\n" + "print(name(\"MYParam1\", \"MyPAram2\").nonStaticTwo);");
        
        assertMany("FFFFFF", "TETETE", "nope", "valueonetwo", "value", "TETETE", "parameter1", "123", "FFFFFF", "1", "0", "TETETE", "MYParam1", "MyPAram2");
    }
    
    @Test
    public void testConstructorCall() {
        TestHelper.run("zenClass name { zenConstructor(a as int, b as int) {print(a); print(b);}} name(10, 20);");
        
        assertMany("10", "20");
    }
    
    @Test
    public void testConstructorCallLarge() {
        TestHelper.run("zenClass name { zenConstructor(a as long, b as long) {print(a); print(b);}} name(10, 20);");
        
        assertMany("10", "20");
    }
    
    @Test
    public void testMethod_empty() {
        TestHelper.run("zenClass name {zenConstructor() {} function method() {}} name().method();");
    }
    
    @Test
    public void testMethod() {
        TestHelper.run("zenClass name {zenConstructor() {} function method(a as int, b as int) {print(a); print(b);}} name().method(10, 20);");
        
        assertMany("10", "20");
    }
    
    @Test
    public void testMethod_large() {
        TestHelper.run("zenClass name {zenConstructor() {} function method(a as long, b as long) {print(a); print(b);}} name().method(10, 20);");
        
        assertMany("10", "20");
    }
    
    @Test
    public void testMethod_overloading() {
        TestHelper.run("zenClass name {zenConstructor() {} \n"
                + "function method(a as int) {print('a: ' ~ a);} \n"
                + "function method(a as int, b as int) {print('b: ' ~ a ~ b);}} \n"
                + "name().method(30); \n"
                + "name().method(10, 20);");
        assertMany("a: 30", "b: 1020");
    }
}
