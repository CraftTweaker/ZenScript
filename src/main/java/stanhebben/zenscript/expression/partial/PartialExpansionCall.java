package stanhebben.zenscript.expression.partial;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

import java.util.Arrays;
import java.util.List;

/**
 * @author youyihj
 */
public class PartialExpansionCall extends PartialStaticGenerated {
    private final Expression instance;

    public PartialExpansionCall(ZenPosition position, String owner, String method, String signature, List<ParsedFunctionArgument> arguments, ZenType returnType, Expression instance) {
        super(position, owner, method, signature, arguments, returnType);
        this.instance = instance;
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        Expression[] expressions = Arrays.copyOf(new Expression[] {instance}, values.length + 1);
        System.arraycopy(values, 0, expressions, 1, values.length);
        return super.call(position, environment, expressions);
    }
}
