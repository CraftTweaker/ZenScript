package stanhebben.zenscript.tests;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestHelper;

import static stanhebben.zenscript.TestAssertions.assertMany;

public class TestArrayCasts {
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }
    
    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach();
    }
    
    @Test
    public void testCastAtDeclaration() {
        TestHelper.run("var x = [1.0f, 2.0f, 3.0f] as double[]; for i in x print(i);");
        assertMany("1.0", "2.0", "3.0");
    }
    
    @Test
    public void testCastAtDeclaration2() {
        TestHelper.run("var x = [0x001, 0x002, 0x003] as double[]; for i in x print(i);");
        assertMany("1.0", "2.0", "3.0");
    }
    
    @Test
    public void testCastAfterDeclaration() {
        TestHelper.run("var x = [1.0f, 2.0f, 3.0f] as float[]; var y = x as double[]; for i in y print(i);");
        assertMany("1.0", "2.0", "3.0");
    }
    
    @Test
    public void testCastAfterDeclaration_nonLarge_large() {
        TestHelper.run("var x = [0x001, 0x002, 0x003] as int[]; var y = x as double[]; for i in y print(i);");
        assertMany("1.0", "2.0", "3.0");
    }
    
    @Test
    public void testCastAfterDeclaration_nonLarge_nonLarge() {
        TestHelper.run("var x = [0x001, 0x002, 0x003] as int[]; var y = x as float[]; for i in y print(i);");
        assertMany("1.0", "2.0", "3.0");
    }
    
    @Test
    public void testCastAfterDeclaration_large_nonLarge() {
        TestHelper.run("var x = [1.0D, 2.0D, 3.0D] as double[]; var y = x as int[]; for i in y print(i);");
        assertMany("1", "2", "3");
    }
    
    @Test
    public void testCastAfterDeclaration_large_large() {
        TestHelper.run("var x = [1.0D, 2.0D, 3.0D] as double[]; var y = x as long[]; for i in y print(i);");
        assertMany("1", "2", "3");
    }
}
