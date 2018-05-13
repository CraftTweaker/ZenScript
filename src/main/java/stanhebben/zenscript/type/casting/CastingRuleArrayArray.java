package stanhebben.zenscript.type.casting;

import org.objectweb.asm.*;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.MethodOutput;

/**
 * @author Stan
 */
public class CastingRuleArrayArray implements ICastingRule {

    private final ICastingRule base;
    private final ZenTypeArrayBasic from;
    private final ZenTypeArrayBasic to;

    public CastingRuleArrayArray(ICastingRule base, ZenTypeArrayBasic from, ZenTypeArrayBasic to) {
        this.base = base;
        this.from = from;
        this.to = to;
    }

    @Override
    public void compile(final IEnvironmentMethod method) {
        final MethodOutput output = method.getOutput();
        final Label end = new Label();
        final Label start = new Label();

        final Type toType = to.getBaseType().toASMType();

        final int result = output.local(to.toASMType());

        output.dup();
        output.arrayLength();
        output.newArray(toType);
        output.storeObject(result);

        final int counter = output.local(int.class);
        output.iConst0();
        output.storeInt(counter);

        output.label(start);
        output.dup();
        output.dup();
        output.arrayLength();
        output.loadInt(counter);
        output.dupX1();
        output.ifICmpGE(end);

        output.arrayLoad(from.getBaseType().toASMType());
        if (base != null)
            base.compile(method);

        output.loadObject(result);
        output.swap();
        output.loadInt(counter);
        output.swap();
        output.arrayStore(toType);
        output.iinc(counter);

        output.goTo(start);
        output.label(end);
        output.loadObject(result);
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
