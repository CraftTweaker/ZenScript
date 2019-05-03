package stanhebben.zenscript.expression;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.symbols.SymbolGlobalValue;
import stanhebben.zenscript.type.ZenType;

public class ExpressionGlobalSet extends Expression {

    private final SymbolGlobalValue global;
    private Expression newVal;

    public ExpressionGlobalSet(SymbolGlobalValue value, Expression newVal) {
        super(value.getPosition());
        this.global = value;
        this.newVal = newVal;
    }

    @Override
    public ZenType getType() {
        return ZenType.VOID;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        newVal.compile(true, environment);
        if (getType().toASMType().equals(Type.getType(Object.class))) {
            environment.getOutput().checkCast(getType().getSignature());
        }
        environment.getOutput().putStaticField(getOwner(), getName(), getASMDescriptor());
    }

    public String getOwner() {
        return global.getOwner();
    }

    public String getName() {
        return global.getName();
    }

    public String getASMDescriptor() {
        return global.getASMDescriptor();
    }

}
