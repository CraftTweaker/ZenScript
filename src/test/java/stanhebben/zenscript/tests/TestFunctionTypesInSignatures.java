package stanhebben.zenscript.tests;

import org.junit.jupiter.api.*;
import stanhebben.zenscript.*;

import java.util.*;

import static stanhebben.zenscript.TestAssertions.*;

public class TestFunctionTypesInSignatures {
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }
    
    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach();
    }
    
    @Test
    public void TestFunctionExpressionInStaticFunction() {
        TestHelper.run("var myTestFun as function(string)void = function(s as string) as void {print(s);};\n" + "myTestFun('Hello World');\n" + "function myAcceptFun(consumer as function(string)void) as void {\n" + "   consumer('hello from inside myAcceptFun');\n" + "}\n" + "myAcceptFun(myTestFun);\n" + "myAcceptFun(function(s as string) as void {print('Another thing: ' ~ s);});");
        assertMany("Hello World", "hello from inside myAcceptFun", "Another thing: hello from inside myAcceptFun");
    }
    
    @Test
    public void TestFunctionExpressionInClassSignature() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("zenClass myClass {");
        joiner.add("    var consumer as function(string)void;");
        joiner.add("    zenConstructor(consumer as function(string)void) {");
        joiner.add("        this.consumer = consumer;");
        joiner.add("    }");
        joiner.add("    function acceptString(x as string) as void {");
        joiner.add("        consumer(x);");
        joiner.add("    }");
        joiner.add("}");
        joiner.add("myClass(function(x as string) as void {print('inside fun: ' ~ x);}).acceptString('abc');");
        
        TestHelper.run(joiner.toString());
        assertMany("inside fun: abc");
    }
    
    @Test
    public void TestFuncSupplier() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("global funcCreator as function(int)function()string = function(i as int) as function() string {");
        joiner.add("    return function() as string {return 'Hello, Nr. ' ~ i;};");
        joiner.add("};");
        joiner.add("print(funcCreator(10)());");
        joiner.add("print(funcCreator(42)());");
        
        TestHelper.run(joiner.toString());
        assertMany("Hello, Nr. 10", "Hello, Nr. 42");
    }
}
