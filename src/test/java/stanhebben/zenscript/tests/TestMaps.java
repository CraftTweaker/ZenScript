package stanhebben.zenscript.tests;

import org.junit.jupiter.api.*;
import stanhebben.zenscript.*;

import java.util.*;

import static stanhebben.zenscript.TestAssertions.assertMany;

public class TestMaps {
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
        TestHelper.registry.registerNativeClass(TestLambdas.FunInterface.class);
        TestHelper.registry.registerNativeClass(TestLambdas.FunInterfaceWithDefaultMethod.class);
        TestHelper.registry.registerNativeClass(TestLambdas.FunInterfaceWithStaticMethod.class);
        TestHelper.registry.registerNativeClass(TestLambdas.FunInterfaceWithZenMethod.class);
        TestHelper.registry.registerGlobal("applyFun", TestHelper.registry.getStaticFunction(TestLambdas.class, "applyFun", TestLambdas.FunInterface.class));
    }
    
    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach();
    }
    
    @Test
    public void Test_NestedMap_Unboxing() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var outerMap = {'outer': {inner: 10.0D}} as double[string][string];");
        joiner.add("for outerName, innerMap in outerMap {");
        joiner.add("    for innerName, dValue in innerMap {");
        joiner.add("        var x = dValue;");
        joiner.add("        print('[' ~ outerName ~ '][' ~ innerName ~ '][' ~ dValue ~']');");
        joiner.add("    }");
        joiner.add("}");
        TestHelper.run(joiner.toString());
        assertMany("[outer][inner][10.0]");
    }
}
