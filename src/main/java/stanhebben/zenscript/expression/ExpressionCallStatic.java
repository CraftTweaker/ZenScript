package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ArrayUtil;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.Pair;
import stanhebben.zenscript.util.ZenPosition;

import java.util.Collections;
import java.util.List;

/**
 * @author Stanneke
 */
public class ExpressionCallStatic extends Expression {
    
    private final IJavaMethod method;
    private Expression[] arguments;
    private final IEnvironmentGlobal environmentGlobal;
    private final List<Pair<ZenType, ParsedExpression>> filledDefaultValues;
    
    public ExpressionCallStatic(ZenPosition position, IEnvironmentGlobal environment, IJavaMethod method, List<Pair<ZenType, ParsedExpression>> filledDefaultValues, Expression... arguments) {
        super(position);
        
        this.method = method;
        this.environmentGlobal = environment;
        this.arguments = arguments;
        this.filledDefaultValues = filledDefaultValues;
    }

    public ExpressionCallStatic(ZenPosition position, IEnvironmentGlobal environment, IJavaMethod method, Expression... arguments) {
        this(position, environment, method, Collections.emptyList(), arguments);
    }
    
    @Override
    public ZenType getType() {
        return method.getReturnType();
    }
    
    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        for (Pair<ZenType, ParsedExpression> filledDefaultValue : filledDefaultValues) {
            arguments = ArrayUtil.add(arguments, filledDefaultValue.getValue().compileKey(environment, filledDefaultValue.getKey()));
        }
        Expression[] rematchedArguments = JavaMethod.rematch(getPosition(), method, environmentGlobal, arguments);
        
        for(Expression argument : rematchedArguments) {
            argument.compile(true, environment);
        }
        
        method.invokeStatic(output);
        
        if(method.getReturnType() != ZenType.VOID && !result) {
            output.pop(method.getReturnType().isLarge());
        }
    }
}
