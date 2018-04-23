package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionNothing extends Expression {
    
    
    public ExpressionNothing(ZenPosition position) {
        super(position);
    }
    
    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
    
    }
    
    @Override
    public ZenType getType() {
        return ZenType.NULL;
    }
}
