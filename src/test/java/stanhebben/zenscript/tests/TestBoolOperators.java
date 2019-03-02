package stanhebben.zenscript.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanhebben.zenscript.TestHelper;

import static stanhebben.zenscript.TestAssertions.assertMany;

@SuppressWarnings("WeakerAccess")
public class TestBoolOperators {
    
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }
    
    @BeforeEach
    public void beforeEch() {
        TestHelper.beforeEach();
    }
    
    @Test
    public void TestAnd() {
        TestHelper.run("var tests = [true & false, false & false, false & true, true & true] as bool[]; for t in tests print(t);");
        
        assertMany("false", "false", "false", "true");
    }
    
    @Test
    public void TestOr() {
        TestHelper.run("var tests = [true | false, false | false, false | true, true | true] as bool[]; for t in tests print(t);");
        
        assertMany("true", "false", "true", "true");
    }
    
    @Test
    public void TestAndAnd() {
        TestHelper.run("var tests = [true && false, false && false, false && true, true && true] as bool[]; for t in tests print(t);");
        
        assertMany("false", "false", "false", "true");
    }
    
    @Test
    public void TestOrOr() {
        TestHelper.run("var tests = [true || false, false || false, false || true, true || true] as bool[]; for t in tests print(t);");
        
        assertMany("true", "false", "true", "true");
    }
    
    @Test
    public void TestAndAndOrder() {
        TestHelper.run("function methodTrue() as bool {print('methodTrue'); return true;} function methodFalse() as bool {print('methodFalse'); return false;} function printFin(a as bool) as void {print(a); print('Fin');}" + "printFin(methodTrue() && methodTrue()); printFin(methodTrue() && methodFalse()); printFin(methodFalse() && methodTrue()); printFin(methodFalse() && methodFalse()); ");
        
        assertMany("methodTrue", "methodTrue", "true", "Fin", "methodTrue", "methodFalse", "false", "Fin", "methodFalse", "false", "Fin", "methodFalse", "false", "Fin");
    }
    
    @Test
    public void TestOrOrOrder() {
        TestHelper.run("function methodTrue() as bool {print('methodTrue'); return true;} function methodFalse() as bool {print('methodFalse'); return false;} function printFin(a as bool) as void {print(a); print('Fin');}" + "printFin(methodTrue() || methodTrue()); printFin(methodTrue() || methodFalse()); printFin(methodFalse() || methodTrue()); printFin(methodFalse() || methodFalse()); ");
        
        assertMany("methodTrue", "true", "Fin", "methodTrue", "true", "Fin", "methodFalse", "methodTrue", "true", "Fin", "methodFalse", "methodFalse", "false", "Fin");
        
        
    }
    
    @Test
    public void TestAndOrder() {
        TestHelper.run("function methodTrue() as bool {print('methodTrue'); return true;} function methodFalse() as bool {print('methodFalse'); return false;} function printFin(a as bool) as void {print(a); print('Fin');}" + "printFin(methodTrue() & methodTrue()); printFin(methodTrue() & methodFalse()); printFin(methodFalse() & methodTrue()); printFin(methodFalse() & methodFalse());");
        
        assertMany("methodTrue", "methodTrue", "true", "Fin", "methodTrue", "methodFalse", "false", "Fin", "methodFalse", "methodTrue", "false", "Fin", "methodFalse", "methodFalse", "false", "Fin");
    }
    
    @Test
    public void TestOrOrder() {
        TestHelper.run("function methodTrue() as bool {print('methodTrue'); return true;} function methodFalse() as bool {print('methodFalse'); return false;} function printFin(a as bool) as void {print(a); print('Fin');}" + "printFin(methodTrue() | methodTrue()); printFin(methodTrue() | methodFalse()); printFin(methodFalse() | methodTrue()); printFin(methodFalse() | methodFalse());");
        
        assertMany("methodTrue", "methodTrue", "true", "Fin", "methodTrue", "methodFalse", "true", "Fin", "methodFalse", "methodTrue", "true", "Fin", "methodFalse", "methodFalse", "false", "Fin");
        
        
    }
    
    @Test
    public void TestConditional() {
        TestHelper.run("print(true ? 'a' : 'b'); print(false ? 'a' : 'b');" + "print(true ? 'a' : 10.0D); print(false ? 'a' : 10.0D);" + "print((true ? 10.0D : '11') + 1); print((false ? 10.0D : '11') + 1);");
        
        assertMany("a", "b", "a", "10.0", "11.0", "12.0");
    }
}
