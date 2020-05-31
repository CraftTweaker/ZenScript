package stanhebben.zenscript.tests;


import org.junit.jupiter.api.*;
import stanhebben.zenscript.*;
import stanhebben.zenscript.annotations.*;

import java.util.*;

import static stanhebben.zenscript.TestAssertions.*;

public class TestFunctionTypes {
    
    @BeforeAll
    public static void setupEnvironment() {
        TestHelper.setupEnvironment();
    }
    
    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach();
    }
    
    @Test
    public void TestFunctionTypeWithArray() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var myTestFun as function(string[])void = function(strings as string[]) as void {");
        joiner.add("    for s in strings print(s);");
        joiner.add("};");
        joiner.add("");
        joiner.add("myTestFun(['a', 'b', 'c']);");
        TestHelper.run(joiner.toString());
        assertMany("a", "b", "c");
    }
    
    @Test
    public void TestFunctionTypeWithNestedArray() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var myTestFun as function(string[][])void = function(strings as string[][]) as void {");
        joiner.add("    for arr in strings for s in arr print(s);");
        joiner.add("};");
        joiner.add("");
        joiner.add("myTestFun([['a', 'b'], ['c']]);");
        TestHelper.run(joiner.toString());
        assertMany("a", "b", "c");
    }
    
    @Test
    public void TestFunctionTypeWithList() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var myTestFun as function([string])void = function(strings as [string]) as void {");
        joiner.add("    for s in strings print(s);");
        joiner.add("};");
        joiner.add("");
        joiner.add("myTestFun(['a', 'b', 'c']);");
        TestHelper.run(joiner.toString());
        assertMany("a", "b", "c");
    }
    
    @Test
    public void TestFunctionTypeWithNestedList() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var myTestFun as function([[string]])void = function(strings as [[string]]) as void {");
        joiner.add("    for arr in strings for s in arr print(s);");
        joiner.add("};");
        joiner.add("");
        joiner.add("myTestFun([['a', 'b'], ['c']]);");
        TestHelper.run(joiner.toString());
        assertMany("a", "b", "c");
    }
    
    @Test
    public void TestFunctionTypeWithAssocArray() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var myTestFun as function(string[string])void = function(strings as string[string]) as void {");
        joiner.add("    for key, value in strings print(key ~ ' <-> ' ~ value);");
        joiner.add("};");
        joiner.add("");
        joiner.add("myTestFun({'a': 'a', 'b' : 'b', 'c': 'c'});");
        TestHelper.run(joiner.toString());
        assertMany("a <-> a", "b <-> b", "c <-> c");
    }
    
    @Test
    public void TestFunctionTypeWithFunctionType() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var myTestFun as function(function(string)string)void = function(fun as function(string)string) as void {");
        joiner.add("    print(fun('a'));");
        joiner.add("};");
        joiner.add("");
        joiner.add("myTestFun(function(s as string) as string {return s + s;});");
        joiner.add("myTestFun(function(s as string) as string {return s + s;});");
        TestHelper.run(joiner.toString());
        assertMany("aa", "aa");
    }
    
    
    @Test
    public void TestFunctionTypeWithHigherOrderFunctionType() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("var myStringConverterConverter as function(function(string)string)string = function (converter as function(string)string) as string {");
        joiner.add("	return converter('a');");
        joiner.add("};");
        joiner.add("");
        joiner.add("var myStringConverterConverterConsumer as function(function(function(string)string)string)void = function(converterConverter as function(function(string)string)string) as void {");
        joiner.add("	print(converterConverter(function(s as string) as string {return s ~ s;}));");
        joiner.add("};");
        joiner.add("");
        joiner.add("myStringConverterConverterConsumer(myStringConverterConverter);");
        
        TestHelper.run(joiner.toString());
        assertMany("aa");
    }
    
    @Test
    public void TestFunctionTypeWithNativeType() {
        TestHelper.registry.registerNativeClass(MyNativeType.class);
        
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("import test.my.NativeType;");
        joiner.add("var myTestFun as function(NativeType)void = function(nat as NativeType) as void {");
        joiner.add("    nat.print();");
        joiner.add("};");
        joiner.add("");
        joiner.add("myTestFun(NativeType.create('a'));");
        TestHelper.run(joiner.toString());
        assertMany("a");
    }
    
    
    @ZenClass("test.my.NativeType")
    public static final class MyNativeType {
    
        private final String s;
    
        public MyNativeType(String s ) {
            this.s = s;
        }
        
        @ZenMethod
        public static MyNativeType create(String s) {
            return new MyNativeType(s);
        }
        
        @ZenMethod
        public void print() {
            TestHelper.print(s);
        }
    }
}
