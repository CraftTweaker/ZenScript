package stanhebben.zenscript.expression;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionOrOr extends Expression {
    
    private final Expression a;
    private final Expression b;
    
    public ExpressionOrOr(ZenPosition position, Expression a, Expression b) {
        super(position);
        
        this.a = a;
        this.b = b;
    }
    
    @Override
    public ZenType getType() {
        return a.getType();
    }
    
    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        final MethodOutput output = environment.getOutput();
        
        final Label onTrue = new Label();
        final Label end = new Label();
        
        a.compile(true, environment);
        output.ifNE(onTrue);
        b.compile(true, environment);
        output.ifNE(onTrue);
        output.iConst0();
        output.goTo(end);
        output.label(onTrue);
        output.iConst1();
        output.label(end);
        
        if(!result)
            output.pop();
        
    }
}
