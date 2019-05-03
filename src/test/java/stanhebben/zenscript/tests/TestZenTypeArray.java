package stanhebben.zenscript.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestHelper;

import static stanhebben.zenscript.TestAssertions.assertMany;

public class TestZenTypeArray {

    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }

    @BeforeEach
    public void beforeEch() {
        TestHelper.beforeEach();
    }

    @Test
    public void testSub() {
        TestHelper.run("val list = ['a', 'b', 'c'] as [string];print(list[1]); print(list - 'b'); print(list[1]);");
        assertMany("b", "true", "c");
    }

    @Test
    public void testRemove() {
        TestHelper.run("val list = ['a', 'b', 'c'] as [string];print(list[1]); print(list.remove('b')); print(list[1]);");
        assertMany("b", "true", "c");
    }

    @Test
    public void testSueNoRetValue() {
        TestHelper.run("val list = ['a', 'b', 'c'] as [string];print(list[1]); list - 'b'; print(list[1]);");
        assertMany("b", "c");
    }

    @Test
    public void testRemoveNoRetValue() {
        TestHelper.run("val list = ['a', 'b', 'c'] as [string];print(list[1]); list.remove('b'); print(list[1]);");
        assertMany("b", "c");
    }
}
