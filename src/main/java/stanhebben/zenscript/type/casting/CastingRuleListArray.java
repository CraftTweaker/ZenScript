package stanhebben.zenscript.type.casting;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.MethodOutput;

import java.util.*;

/**
 * @author Stan
 */
public class CastingRuleListArray implements ICastingRule {

    private final ICastingRule base;
    private final ZenTypeArrayList from;
    private final ZenTypeArrayBasic to;

    public CastingRuleListArray(ICastingRule base, ZenTypeArrayList from, ZenTypeArrayBasic to) {
        this.base = base;
        this.from = from;
        this.to = to;
    }

    @Override
    public void compile(IEnvironmentMethod method) {

        final MethodOutput methodOutput = method.getOutput();
        methodOutput.iConst0();
        final int localCounter = methodOutput.local(int.class);
        methodOutput.storeInt(localCounter);

        final int localArray = methodOutput.local(to.toASMType());
        methodOutput.dup();
        methodOutput.invokeInterface(Collection.class, "size", int.class);
        methodOutput.newArray(to.getBaseType().toASMType());
        methodOutput.storeObject(localArray);

        final Label start = new Label();
        final Label end = new Label();

        methodOutput.label(start);
        methodOutput.dup();
        methodOutput.dup();
        methodOutput.invokeInterface(Collection.class, "size", int.class);
        methodOutput.loadInt(localCounter);

        methodOutput.ifICmpLE(end);
        methodOutput.loadInt(localCounter);

        methodOutput.invokeInterface(List.class, "get", Object.class, int.class);
        if(base != null)
            base.compile(method);
        methodOutput.loadObject(localArray);
        methodOutput.swap();
        methodOutput.loadInt(localCounter);
        methodOutput.swap();
        methodOutput.arrayStore(to.getBaseType().toASMType());

        methodOutput.iinc(localCounter);
        methodOutput.goTo(start);
        methodOutput.label(end);
        methodOutput.loadObject(localArray);
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
