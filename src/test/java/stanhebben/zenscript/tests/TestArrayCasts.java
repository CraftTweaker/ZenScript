package stanhebben.zenscript.tests;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestHelper;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenConstructor;

import static stanhebben.zenscript.TestAssertions.assertMany;

public class TestArrayCasts {

    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
        TestHelper.registry.registerNativeClass(A.class);
        TestHelper.registry.registerNativeClass(B.class);
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

    @Test
    public void testCastArrayArray_B_A() {
        TestHelper.run("import root.tests.B; import root.tests.A; var x = [B('1'), B('2'), B('3')] as B[]; var y = x as A[]; for i in y print(i);");
        assertMany("1", "2", "3");
    }

    @Test
    public void testCastArray2List() {
        TestHelper.run("var x = ['1', '2', '3'] as string[]; var y = x as [string]; for i in y print(i);");
        assertMany("1", "2", "3");
    }

    @Test
    public void testCastList2Array() {
        TestHelper.run("var x = ['1', '2', '3'] as [string]; var y = x as string[]; for i in y print(i);");
        assertMany("1", "2", "3");
    }

    @Test
    public void testCastArray2List_A_String() {
        TestHelper.run("import root.tests.B; var x = [B('1'), B('2'), B('3')] as B[]; var y = x as [string]; for i in y print(i);");
        assertMany("1", "2", "3");
    }

    @Test
    public void testCastArray2List_A_B() {
        TestHelper.run("import root.tests.B; import root.tests.A; var x = [B('1'), B('2'), B('3')] as B[]; var y = x as [A]; for i in y print(i);");
        assertMany("1", "2", "3");
    }

    @Test
    public void testCastListList_B_A() {
        TestHelper.run("import root.tests.B; import root.tests.A; var x = [B('1'), B('2'), B('3')] as [B]; var y = x as [A]; for i in y print(i);");
        assertMany("1", "2", "3");
    }

    @Test
    public void testCastListArray_B_A() {
        TestHelper.run("import root.tests.B; import root.tests.A; var x = [B('1'), B('2'), B('3')] as [B]; var y = x as A[]; for i in y print(i);");
        assertMany("1", "2", "3");
    }


    @ZenClass("tests.A")
    public static class A {
        private final String s;

        @ZenConstructor
        public A(String s) {
            this.s = s;
        }

        @Override
        @ZenCaster()
        public String toString() {
            return s;
        }

        @ZenCaster
        public B asB() {
            return new B(s);
        }
    }

    @ZenClass("tests.B")
    public static class B {
        final String s;

        @ZenConstructor
        public B(String s) {
            this.s = s;
        }

        @Override
        @ZenCaster()
        public String toString() {
            return s;
        }

        @ZenCaster
        public A asA() {
            return new A(s);
        }
    }

}
