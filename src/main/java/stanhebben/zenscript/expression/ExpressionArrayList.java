package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.*;

import java.util.*;

public class ExpressionArrayList extends Expression {


    private final Expression[] contents;
    private final ZenTypeArrayList type;

    public ExpressionArrayList(ZenPosition position, ZenTypeArrayList type, Expression[] contents) {
        super(position);
        this.contents = contents;
        this.type = type;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if(!result) return;
        final MethodOutput methodOutput = environment.getOutput();
        methodOutput.newObject(ArrayList.class);
        methodOutput.dup();
        methodOutput.invokeSpecial("java/util/ArrayList", "<init>", "()V");

        for(Expression content : contents) {
            methodOutput.dup();
            content.cast(getPosition(), environment, ZenTypeUtil.checkPrimitive(content.getType())).compile(true, environment);
            methodOutput.invokeInterface(Collection.class, "add", boolean.class, Object.class);
            methodOutput.pop();
        }
    }

    @Override
    public ZenType getType() {
        return type;
    }
}
