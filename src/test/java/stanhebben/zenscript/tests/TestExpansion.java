package stanhebben.zenscript.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestAssertions;
import stanhebben.zenscript.TestHelper;

import java.util.StringJoiner;

public class TestExpansion {
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach();
    }

    @Test
    public void testExpansion() {
        StringJoiner joiner = new StringJoiner("\n")
                .add("$expand string$wrap(prefix as string, suffix as string) as string {")
                .add("    return prefix ~ this ~ suffix;")
                .add("}")
                .add("print(\"test\".wrap(\"[\", \"]\"));");
        TestHelper.run(joiner.toString());
        TestAssertions.assertMany("[test]");
    }

    @Test
    public void testExpansionDefaultArguments() {
        StringJoiner joiner = new StringJoiner("\n")
                .add("$expand string$wrap(prefix as string = \"(\", suffix as string = \")\") as string {")
                .add("    return prefix ~ this ~ suffix;")
                .add("}")
                .add("print(\"test\".wrap(\"[\", \"]\"));")
                .add("print(\"test\".wrap());");
        TestHelper.run(joiner.toString());
        TestAssertions.assertMany("[test]", "(test)");
    }
}
