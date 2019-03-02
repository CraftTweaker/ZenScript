package stanhebben.zenscript;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;
import stanhebben.zenscript.impl.GenericCompileEnvironment;
import stanhebben.zenscript.impl.GenericErrorLogger;
import stanhebben.zenscript.impl.GenericRegistry;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("WeakerAccess")
public class TestZenProperty {

    private static List<String> prints = new LinkedList<>();


    private static GenericRegistry registry;
    private static GenericErrorLogger logger;
    private static GenericCompileEnvironment compileEnvironment;

    @SuppressWarnings("unused")
    public static void print(String value) {
        prints.add(value);
    }

    @BeforeAll
    public static void setupEnvironment() {
        compileEnvironment = new GenericCompileEnvironment();
        logger = new GenericErrorLogger(System.out);
        registry = new GenericRegistry(compileEnvironment, logger);
        registry.registerGlobal("print", registry.getStaticFunction(TestZenProperty.class, "print", String.class));
        registry.registerNativeClass(PropertyTester.class);
        registry.registerGlobal("testerInstance", registry.getStaticFunction(PropertyTester.class, "getInstance"));
    }

    private static void assertMany(String... lines) {
        for (int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], prints.get(i));
        }
    }

    @BeforeEach
    public void clearPrints() {
        prints.clear();
    }

    @BeforeEach
    public void clearClasses() {
        ZenModule.classes.clear();
        ZenModule.loadedClasses.clear();
    }

    @BeforeEach
    public void resetTester() {
        PropertyTester.instance = null;
    }

    @Test
    public void testSet_NoParameters() {
        ModuleCreator.run("testerInstance().a = 'abc';", compileEnvironment, registry);
        Assert.assertNotNull(PropertyTester.instance);
        Assert.assertEquals("abc", PropertyTester.getInstance().a);
    }

    @Test
    public void testSetMethod_NoParameters() {
        ModuleCreator.run("testerInstance().setA('abc');", compileEnvironment, registry);
        Assert.assertNotNull(PropertyTester.instance);
        Assert.assertEquals("abc", PropertyTester.getInstance().a);
    }

    @Test
    public void testGet_NoParameters() {
        PropertyTester.getInstance().a = "someValue";
        ModuleCreator.run("print(testerInstance().a);", compileEnvironment, registry);
        assertMany("someValue");
    }

    @Test
    public void testGetMethod_NoParameters() {
        PropertyTester.getInstance().a = "someValue";
        ModuleCreator.run("print(testerInstance().getA());", compileEnvironment, registry);
        assertMany("someValue");
    }

    @Test
    public void testSet_Methods() {
        ModuleCreator.run("testerInstance().b = 'abc';", compileEnvironment, registry);

        Assert.assertNotNull(PropertyTester.instance);
        Assert.assertEquals("abc", PropertyTester.getInstance().b);
        assertMany("setB");
    }

    @Test
    public void testSetMethod_Methods() {
        ModuleCreator.run("testerInstance().setB('abc');", compileEnvironment, registry);

        Assert.assertNotNull(PropertyTester.instance);
        Assert.assertEquals("abc", PropertyTester.getInstance().b);
        assertMany("setB");
    }

    @Test
    public void testGet_Methods() {
        PropertyTester.getInstance().b = "someValue";
        ModuleCreator.run("print(testerInstance().b);", compileEnvironment, registry);

        assertMany("getB", "someValue");
    }

    @Test
    public void testGetMethod_Methods() {
        PropertyTester.getInstance().b = "someValue";
        ModuleCreator.run("print(testerInstance().getB());", compileEnvironment, registry);

        assertMany("getB", "someValue");
    }

    @Test
    public void testSet_MethodReRouting() {
        ModuleCreator.run("testerInstance().c = 'abc';", compileEnvironment, registry);

        Assert.assertNotNull(PropertyTester.instance);
        Assert.assertEquals("abc", PropertyTester.getInstance().c);
        assertMany("setC2");
    }

    @Test
    public void testGet_MethodReRouting() {
        PropertyTester.getInstance().c = "someValue";
        ModuleCreator.run("print(testerInstance().c);", compileEnvironment, registry);

        assertMany("getC2", "someValue");
    }


    //No ZenRegister because registered natively
    @ZenClass("tests.internal.PropertyTester")
    @SuppressWarnings("unused")
    public static final class PropertyTester {


        public static PropertyTester instance;
        @ZenProperty
        public String a;

        @ZenProperty
        public String b;

        @ZenProperty(getter = "getC2", setter = "setC2")
        public String c;


        @ZenMethod
        public static PropertyTester getInstance() {
            return instance != null ? instance : (instance = new PropertyTester());
        }

        @ZenMethod
        public String getB() {
            print("getB");
            return b;
        }

        @ZenMethod
        public void setB(String b) {
            print("setB");
            this.b = b;
        }

        public String getC2() {
            print("getC2");
            return c;
        }

        public void setC2(String c) {
            print("setC2");
            this.c = c;
        }
    }
}
