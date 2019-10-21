package stanhebben.zenscript.tests;

import org.junit.jupiter.api.*;
import stanhebben.zenscript.*;
import stanhebben.zenscript.annotations.*;
import stanhebben.zenscript.expression.*;

import static stanhebben.zenscript.TestAssertions.assertMany;

public class TestLambdas {
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
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
    
    @ZenClass("tests.fun.FunInterface")
    @FunctionalInterface
    public interface FunInterface {
        int apply(int a);
    }
    
    public static int applyFun(FunInterface i) {
        return i.apply(1);
    }
}
