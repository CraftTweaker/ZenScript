package stanhebben.zenscript.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestAssertions;
import stanhebben.zenscript.TestHelper;

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
    public void testDefaultArguments() {
        TestHelper.run("function test(a as int, b as int = 2, c as int = 3) {print(a + b + c);} test(1); test(2, 4); test(10, 10, 7);");
        TestAssertions.assertMany("6", "9", "27");
    }
}
