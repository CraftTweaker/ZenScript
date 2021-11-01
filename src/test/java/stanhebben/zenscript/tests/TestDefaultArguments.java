package stanhebben.zenscript.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestAssertions;
import stanhebben.zenscript.TestHelper;

import java.util.StringJoiner;

public class TestDefaultArguments {
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach();
    }

    @Test
    public void testFunctionDefaultArguments() {
        TestHelper.run("function test(a as int, b as int = 2, c as int = 3) {print(a + b + c);} test(1); test(2, 4); test(10, 10, 7);");
        TestAssertions.assertMany("6", "9", "27");
    }

    @Test
    public void testZenClassDefaultArguments() {
        StringJoiner joiner = new StringJoiner("\n")
                .add("zenClass Test {")
                .add("    zenConstructor() {}")
                .add("    function foo(a as int, b as int = 2, c as int = 3) as void {print(a + b + c);}")
                .add("}")
                .add("Test().foo(1);")
                .add("Test().foo(2, 3);")
                .add("Test().foo(4, 5, 6);");
        TestHelper.run(joiner.toString());
        TestAssertions.assertMany("6", "8", "15");
    }
}
