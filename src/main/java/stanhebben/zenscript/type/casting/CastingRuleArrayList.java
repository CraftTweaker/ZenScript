package stanhebben.zenscript.type.casting;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.MethodOutput;

import java.util.*;

import static stanhebben.zenscript.util.ZenTypeUtil.internal;

/**
 * @author Stan
 */
public class CastingRuleArrayList implements ICastingRule {

    private final ICastingRule base;
    private final ZenTypeArrayBasic from;
    private final ZenTypeArrayList to;

    public CastingRuleArrayList(ICastingRule base, ZenTypeArrayBasic from, ZenTypeArrayList to) {
        this.base = base;
        this.from = from;
        this.to = to;
    }

    @Override
    public void compile(IEnvironmentMethod method) {
        if(from.toJavaClass().getComponentType().isPrimitive())
            throw new IllegalArgumentException("Cannot convert primitive Array to List!");
        final MethodOutput methodOutput = method.getOutput();
        methodOutput.iConst0();
        final int localCounter = methodOutput.local(int.class);
        methodOutput.storeInt(localCounter);

        final int localList = methodOutput.local(ArrayList.class);
        methodOutput.newObject(ArrayList.class);
        methodOutput.dup();
        methodOutput.invokeSpecial("java/util/ArrayList", "<init>", "()V");
        methodOutput.storeObject(localList);

        final Label start = new Label();
        final Label end = new Label();

        methodOutput.label(start);
        methodOutput.dup();
        methodOutput.dup();
        methodOutput.arrayLength();
        methodOutput.loadInt(localCounter);

        methodOutput.ifICmpLE(end);
        methodOutput.loadInt(localCounter);

        methodOutput.arrayLoad(from.getBaseType().toASMType());
        if(base != null)
            base.compile(method);
        methodOutput.loadObject(localList);
        methodOutput.swap();
        methodOutput.invokeInterface(Collection.class, "add", boolean.class, Object.class);
        methodOutput.pop();
        methodOutput.iinc(localCounter);
        methodOutput.goTo(start);
        methodOutput.label(end);
        methodOutput.pop();
        methodOutput.pop();
        methodOutput.loadObject(localList);
    }

    @Override
    public ZenType getInputType() {
        return from;
    }

    @Override
    public ZenType getResultingType() {
        return to;
    }
}
