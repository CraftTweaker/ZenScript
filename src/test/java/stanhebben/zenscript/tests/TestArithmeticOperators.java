package stanhebben.zenscript.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestHelper;

import static stanhebben.zenscript.TestAssertions.assertMany;

@SuppressWarnings("WeakerAccess")
public class TestArithmeticOperators {
    
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }
    
    @BeforeEach
    public void beforeEch() {
        TestHelper.beforeEach();
    }

    @Test
    public void testBrackets() {
        TestHelper.run("print(((1+2)*3) - 5); print((1*(2+3)) - 5);");

        assertMany("4", "0");
    }

    @Test
    public void testD2LCast_Function() {
        TestHelper.run("val x = 10 as long; function pLong(lo as long) as void {print(lo);} pLong((1.8 * x) as long);");

        assertMany("18");
    }
}
