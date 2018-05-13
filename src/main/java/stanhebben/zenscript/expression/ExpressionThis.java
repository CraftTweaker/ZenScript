package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionThis extends Expression {

    private final ZenType type;

    public ExpressionThis(ZenPosition position, ZenType type) {
        super(position);
        this.type = type;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if(result)
            environment.getOutput().loadObject(0);
    }

    @Override
    public ZenType getType() {
        return type;
    }
}
