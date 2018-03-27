package stanhebben.zenscript.compiler;

import org.objectweb.asm.ClassWriter;

/**
 * In some cases visitMaxs threw ClassNotFound Exceptions due to different class loaders/path
 * Using this one should fix that.
 * Issue was mostly found in MCF due to a class loader override that didn't affect ASM classes.
 */
public class ZenClassWriter extends ClassWriter {
    
    public ZenClassWriter(int flags) {
        super(flags);
    }
}
