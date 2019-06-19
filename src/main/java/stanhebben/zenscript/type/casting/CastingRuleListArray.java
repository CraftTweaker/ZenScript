package stanhebben.zenscript.type.casting;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.util.MethodOutput;

import java.util.Collection;
import java.util.List;

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
        if (base == null)
            compileNoConversion(method.getOutput());
        else
            compileWithConversion(method);
    }

    private void compileWithConversion(IEnvironmentMethod method) {
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
        methodOutput.checkCast(from.getBaseType().toASMType().getInternalName());
        base.compile(method);
        methodOutput.loadObject(localArray);
        methodOutput.swap();
        methodOutput.loadInt(localCounter);
        methodOutput.swap();
        methodOutput.arrayStore(to.getBaseType().toASMType());

        methodOutput.iinc(localCounter);
        methodOutput.goTo(start);
        methodOutput.label(end);

        methodOutput.pop();
        methodOutput.pop();
        methodOutput.loadObject(localArray);
    }


    private void compileNoConversion(MethodOutput methodOutput) {
        methodOutput.iConst0();
        methodOutput.newArray(to.getBaseType().toASMType());

        methodOutput.invokeInterface(Collection.class, "toArray", Object[].class, Object[].class);
        methodOutput.checkCast(to.toJavaClass());
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
