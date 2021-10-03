package stanhebben.zenscript.expression.partial;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolZenStaticMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.Pair;
import stanhebben.zenscript.util.ZenPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Stan
 */
public class PartialStaticGenerated implements IPartialExpression {

    private final ZenPosition position;
    private final String owner;
    private final String method;
    private final String signature;
    private final List<ParsedFunctionArgument> arguments;
    private final ZenType returnType;

    public PartialStaticGenerated(ZenPosition position, String owner, String method, String signature, List<ParsedFunctionArgument> arguments, ZenType returnType) {
        this.position = position;
        this.owner = owner;
        this.method = method;
        this.signature = signature;
        this.arguments = arguments;
        this.returnType = returnType;
    }

    @Override
    public Expression eval(IEnvironmentGlobal environment) {
        environment.error(position, "cannot use a function as value");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
        environment.error(position, "cannot assign to a function");
        return new ExpressionInvalid(position);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        environment.error(position, "functions don't have members");
        return new ExpressionInvalid(position);
    }

    @Override
    public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
        List<ParsedFunctionArgument> filledDefaultExpressions = this.arguments.subList(values.length, this.arguments.size());
        if(filledDefaultExpressions.stream().anyMatch(Objects::isNull)) {
            environment.error(position, "invalid number of arguments");
            return new ExpressionInvalid(position);
        }

        Expression[] arguments = new Expression[values.length];
        for(int i = 0; i < values.length; i++) {
            arguments[i] = values[i].cast(position, environment, this.arguments.get(i).getType());
        }
        List<Pair<ZenType, ParsedExpression>> collect = new ArrayList<>();
        for (ParsedFunctionArgument filledDefaultExpression : filledDefaultExpressions) {
            collect.add(new Pair<>(filledDefaultExpression.getType(), filledDefaultExpression.getDefaultExpression()));
        }

        return new ExpressionCallStatic(position, environment, JavaMethod.getStatic(owner, method, returnType, getArgumentTypes()), collect, arguments);
    }

    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return Arrays.copyOf(getArgumentTypes(), numArguments);
    }

    @Override
    public IZenSymbol toSymbol() {
        return new SymbolZenStaticMethod(owner, method, signature, arguments, returnType);
    }

    @Override
    public ZenType getType() {
        return returnType;
    }

    @Override
    public ZenType toType(IEnvironmentGlobal environment) {
        environment.error(position, "not a valid type");
        return ZenType.ANY;
    }

    private ZenType[] getArgumentTypes() {
        return this.arguments.stream().map(ParsedFunctionArgument::getType).toArray(ZenType[]::new);
    }
}
