package stanhebben.zenscript;

public class ModuleCreator {

    public static boolean run(String content, IZenCompileEnvironment compileEnvironment, IZenRegistry registry) {
        try {
            ZenModule module = ZenModule.compileScriptString(content, "test.zs", compileEnvironment, org.junit.jupiter.api.Test.class.getClassLoader());
            Runnable runnable = module.getMain();
            if (runnable != null) {
                runnable.run();
            }
            return true;
        } catch (Throwable ex) {
            registry.getErrorLogger().error("Error executing: test.zs: " + ex.getMessage(), ex);
            return false;
        }
    }
}
