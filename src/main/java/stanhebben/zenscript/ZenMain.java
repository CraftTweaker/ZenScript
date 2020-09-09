package stanhebben.zenscript;

import stanhebben.zenscript.impl.GenericCompileEnvironment;
import stanhebben.zenscript.impl.GenericErrorLogger;
import stanhebben.zenscript.impl.GenericRegistry;

import java.util.*;

public class ZenMain {
    public static void println(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {
        final IZenCompileEnvironment environment = new GenericCompileEnvironment();
        final IZenErrorLogger errorLogger = new GenericErrorLogger(System.err);
        final GenericRegistry genericRegistry = new GenericRegistry(environment, errorLogger);

        genericRegistry.registerGlobal("println", genericRegistry.getStaticFunction(ZenMain.class, "println", String.class));

        try {
            final StringJoiner builder = new StringJoiner("\n");
            builder.add("var myTestFun as function(string)void = function(s as string) as void {println(s);};");
            builder.add("myTestFun('Hello World');");
            builder.add("function myAcceptFun(consumer as function(string)void) as void {");
            builder.add("   consumer('hello from inside myAcceptFun');");
            builder.add("}");
            builder.add("myAcceptFun(myTestFun);");
            builder.add("myAcceptFun(function(s as string) as void {println('Another thing: ' ~ s);});");
            
            //final String script = "println('hello');";
            final String script = builder.toString();
            ZenModule module = ZenModule.compileScriptString(script, "test.zs", environment, ZenMain.class.getClassLoader());
            Runnable runnable = module.getMain();
            if(runnable != null) {
                runnable.run();
            }
        } catch(Throwable ex) {
            genericRegistry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
        }
    }
}
