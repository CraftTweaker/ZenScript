package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionZSClass extends Expression {
    
    private final ZenTypeFrigginClass type;
    
    public ExpressionZSClass(ZenPosition position, ZenTypeFrigginClass type) {
        super(position);
        this.type = type;
    }
    
    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
    
    }
    
    @Override
    public ZenType getType() {
        return type;
    }
}
