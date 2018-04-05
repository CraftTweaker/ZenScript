package stanhebben.zenscript.type;

import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.*;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.definitions.*;
import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.expression.partial.*;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;

import java.lang.reflect.Field;
import java.util.Objects;

public class ZenTypeFrigginClass extends ZenType {
    
    public final ParsedFrigginClass parsedFrigginClass;
    
    public ZenTypeFrigginClass(ParsedFrigginClass parsedFrigginClass) {
        
        this.parsedFrigginClass = parsedFrigginClass;
    }
    
    @Override
    public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
        throw new UnsupportedOperationException("Friggin Classes cannot be unaried!");
    }
    
    @Override
    public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
        throw new UnsupportedOperationException("Classes have no binary operators");
    }
    
    @Override
    public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
        throw new UnsupportedOperationException("Classes don't have ternary operators");
    }
    
    @Override
    public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
        throw new UnsupportedOperationException("Why would you compare friggin classes?");
    }
    
    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
        return parsedFrigginClass.getMember(position, environment, value, name, false);
    }
    
    @Override
    public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
        return parsedFrigginClass.getMember(position, environment, null, name, true);
    }
    
    @Override
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
        for(ParsedClassConstructor constructor : parsedFrigginClass.constructors) {
            if(constructor.canAccept(arguments, environment))
                return constructor.call(position, environment, receiver, arguments, this);
                //return new ExpressionCallVirtual(position, environment, JavaMethod.get(environment.getEnvironment().getTypeRegistry(), toJavaClass(), parsedFrigginClass.name, constructor.parameterClasses()), receiver, arguments);
        }
        environment.error("Could not find constructor for " + parsedFrigginClass.name + "with " + arguments.length + " arguments.");
        return new ExpressionInvalid(position);
    }
    
    @Override
    public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
    
    }
    
    @Override
    public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
        return null;
    }
    
    @Override
    public Class toJavaClass() {
        return parsedFrigginClass.thisClass;
    }
    
    @Override
    public Type toASMType() {
        return Type.getType(toJavaClass());
    }
    
    @Override
    public int getNumberType() {
        return 0;
    }
    
    @Override
    public String getSignature() {
        return toASMType().getDescriptor();
    }
    
    @Override
    public boolean isPointer() {
        return false;
    }
    
    @Override
    public String getAnyClassName(IEnvironmentGlobal environment) {
        return parsedFrigginClass.className;
    }
    
    @Override
    public String getName() {
        return parsedFrigginClass.className;
    }
    
    @Override
    public Expression defaultValue(ZenPosition position) {
        return new ExpressionNull(position);
    }
    
    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return parsedFrigginClass.predictCallTypes(numArguments);
    }
}
