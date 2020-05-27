package stanhebben.zenscript.tests;

import org.junit.jupiter.api.*;
import stanhebben.zenscript.*;
import stanhebben.zenscript.annotations.*;

import java.util.*;

import static stanhebben.zenscript.TestAssertions.assertMany;

public class TestLambdas {
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
        TestHelper.registry.registerNativeClass(FunInterface.class);
        TestHelper.registry.registerNativeClass(FunInterfaceWithDefaultMethod.class);
        TestHelper.registry.registerNativeClass(FunInterfaceWithStaticMethod.class);
        TestHelper.registry.registerNativeClass(FunInterfaceWithZenMethod.class);
        TestHelper.registry.registerGlobal("applyFun", TestHelper.registry.getStaticFunction(TestLambdas.class, "applyFun", FunInterface.class));
    }
    
    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach();
    }
    
    @Test
    public void Test_FunctionExpression_Capture() {
        TestHelper.run("val x = 10; print(applyFun(function(a){return a + x;}));");
        assertMany("11");
    }
    
    @Test
    public void Test_FunctionExpressionToLambdaWrapping() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var fun = function(i as int) as int {return i + 1;};");
        joiner.add("print(applyFun(fun));");
        TestHelper.run(joiner.toString());
        assertMany("2");
    }
    
    @Test
    public void Test_FunctionExpressionToLambdaWrapping_2() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var fun as function(int)int = function(i as int) as int {return i + 1;};");
        joiner.add("print(applyFun(fun));");
        TestHelper.run(joiner.toString());
        assertMany("2");
    }
    
    @Test
    public void Test_FunctionExpressionCallInterfceMethod() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("import tests.fun.FunInterfaceWithZenMethod;");
        joiner.add("var fun as FunInterfaceWithZenMethod = function(i) {return i + 1;};");
        joiner.add("print(fun.apply(1));");
        TestHelper.run(joiner.toString());
        assertMany("2");
    }
    
    @Test
    public void Test_FunctionExpressionCallDefaultMethod() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("import tests.fun.FunInterfaceWithDefaultMethod;");
        joiner.add("var fun as FunInterfaceWithDefaultMethod = function(i) {return i + 1;};");
        joiner.add("print(fun.isEven(1));");
        TestHelper.run(joiner.toString());
        assertMany("true");
    }
    
    @Test
    public void Test_FunctionExpressionCallStaticMethod() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("import tests.fun.FunInterfaceWithStaticMethod;");
        joiner.add("print(FunInterfaceWithStaticMethod.doSomethingStatic());");
        TestHelper.run(joiner.toString());
        assertMany("true");
    }
    
    @Test
    public void Test_FunctionExpressionAccessStaticMember() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("import tests.fun.FunInterfaceWithStaticMethod;");
        joiner.add("print(FunInterfaceWithStaticMethod.staticField);");
        TestHelper.run(joiner.toString());
        assertMany("true");
    }
    
    @ZenClass("tests.fun.FunInterface")
    @FunctionalInterface
    public interface FunInterface {
        int apply(int a);
    }
    
    @ZenClass("tests.fun.FunInterfaceWithZenMethod")
    @FunctionalInterface
    public interface FunInterfaceWithZenMethod {
        @ZenMethod
        int apply(int a);
    }
    
    @ZenClass("tests.fun.FunInterfaceWithDefaultMethod")
    @FunctionalInterface
    public interface FunInterfaceWithDefaultMethod {
        @ZenMethod
        int apply(int a);
    
        @ZenMethod
        default boolean isEven(int a) {
            return apply(a) % 2 == 0;
        }
    }
    
    @ZenClass("tests.fun.FunInterfaceWithStaticMethod")
    @FunctionalInterface
    public interface FunInterfaceWithStaticMethod {
        @ZenMethod
        int apply(int a);
        
        @ZenMethod
        static boolean doSomethingStatic() {
            return true;
        }
        
        @ZenProperty
        boolean staticField = true;
    }
    
    public static int applyFun(FunInterface i) {
        return i.apply(1);
    }
}
