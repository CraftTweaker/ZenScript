package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.Pair;
import stanhebben.zenscript.util.ZenPosition;

import java.util.List;

public class ExpressionFunctionCall extends Expression {
    
    private final Expression[] values;
    private final ZenType returnType;
    private final Expression receiver;
    private final String className;
    private final String descriptor;
    private final List<Pair<ZenType, ParsedExpression>> filledDefaultValues;
    
    

    
    public ExpressionFunctionCall(ZenPosition position, Expression[] values, List<Pair<ZenType, ParsedExpression>> filledDefaultValues, ZenType returnType, Expression receiver, String className, String descriptor) {
        super(position);
        this.values = values;
        this.filledDefaultValues = filledDefaultValues;
        this.returnType = returnType;
        this.receiver = receiver;
        this.className = className;
        this.descriptor = descriptor;
    }
    
    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        receiver.compile(true, environment);
        for (Expression value : values) {
            value.compile(true, environment);
        }
        for (Pair<ZenType, ParsedExpression> filledDefaultValue : filledDefaultValues) {
            filledDefaultValue.getValue().compileKey(environment, filledDefaultValue.getKey()).compile(true, environment);
        }
        environment.getOutput().invokeInterface(className, "accept", descriptor);
        if(returnType != ZenType.VOID && !result) {
            environment.getOutput().pop(returnType.isLarge());
        }
    }
    
    @Override
    public ZenType getType() {
        return returnType;
    }
}
