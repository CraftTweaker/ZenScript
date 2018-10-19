package stanhebben.zenscript.expression;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.*;

public class ExpressionAndAnd extends Expression {

    private final Expression a;
    private final Expression b;

    public ExpressionAndAnd(ZenPosition position, Expression a, Expression b) {
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
        // if not a: return false
        // if not b: return false
        // return true

        MethodOutput output = environment.getOutput();

        final Label onFalse = new Label();
        final Label end = new Label();
        a.compile(true, environment);
        output.ifEQ(onFalse);
        b.compile(true, environment);
        output.ifEQ(onFalse);
        output.iConst1();
        output.goTo(end);
        output.label(onFalse);
        output.iConst0();
        output.label(end);

        if(!result) {
            output.pop();
        }
    }

    @Override
    public void compileIf(Label onElse, IEnvironmentMethod environment) {
        a.compile(true, environment);
        environment.getOutput().ifEQ(onElse);
        b.compile(true, environment);
        environment.getOutput().ifEQ(onElse);
    }
}
