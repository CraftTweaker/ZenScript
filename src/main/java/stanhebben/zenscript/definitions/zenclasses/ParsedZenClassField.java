package stanhebben.zenscript.definitions.zenclasses;

import org.objectweb.asm.*;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.*;
import stanhebben.zenscript.util.MethodOutput;

import static stanhebben.zenscript.ZenTokener.*;

public class ParsedZenClassField {

    public final boolean isStatic;
    public final ParsedExpression initializer;
    public final String name;
    private final String ownerName;
    public ZenType type;

    public ParsedZenClassField(boolean isStatic, ZenType type, ParsedExpression initializer, String name, String ownerName) {

        this.isStatic = isStatic;
        this.type = type;
        this.initializer = initializer;
        this.name = name;
        this.ownerName = ownerName;
    }

    public static ParsedZenClassField parse(ZenTokener parser, EnvironmentScript classEnvironment, boolean isStatic, String ownerName) {
        String name = parser.required(T_ID, "Identifier expected").getValue();
        ZenType type = ZenType.ANY;
        if(parser.optional(T_AS) != null) {
            type = ZenType.read(parser, classEnvironment);
        }
        ParsedExpression initializer = null;
        if(parser.optional(T_ASSIGN) != null) {
            initializer = ParsedExpression.read(parser, classEnvironment);
        }
        parser.required(T_SEMICOLON, "; expected");


        return new ParsedZenClassField(isStatic, type, initializer, name, ownerName);

    }

    void addMethodsToMember(ZenNativeMember zenNativeMember) {
        zenNativeMember.setGetter(new ZenClassFieldMethod(false));
        zenNativeMember.setSetter(new ZenClassFieldMethod(true));
    }

    public boolean hasInitializer() {
        return initializer != null;
    }

    public void visit(ClassWriter newClass) {
        newClass.visitField(!isStatic ? Opcodes.ACC_PUBLIC : (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC), name, type.toASMType().getDescriptor(), null, null).visitEnd();
    }

    public void writeAll(IEnvironmentMethod clinitEnvironment, ClassWriter newClass, MethodOutput clinit, String className) {
        final Expression expression = hasInitializer() ? initializer.compile(clinitEnvironment, type).eval(clinitEnvironment) : null;
        if(type == ZenType.ANY && expression != null)
            type = expression.getType();
        visit(newClass);

        if(expression != null) {
            expression.compile(true, clinitEnvironment);
            clinit.putStaticField(className, name, type.toASMType().getDescriptor());
        }
    }

    private final class ZenClassFieldMethod implements IJavaMethod {

        private final boolean isSetter;


        private ZenClassFieldMethod(boolean isSetter) {

            this.isSetter = isSetter;
        }

        @Override
        public boolean isStatic() {
            return ParsedZenClassField.this.isStatic;
        }

        @Override
        public boolean accepts(int numArguments) {
            return numArguments == (isSetter ? 1 : 0);
        }

        @Override
        public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
            return accepts(arguments.length) && (!isSetter || arguments[0].getType().canCastImplicit(ParsedZenClassField.this.type, environment));
        }

        @Override
        public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
            return accepts(environment, arguments) ? JavaMethod.PRIORITY_LOW : JavaMethod.PRIORITY_INVALID;
        }

        @Override
        public void invokeVirtual(MethodOutput output) {
            if(isStatic())
                if(isSetter)
                    output.putStaticField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
                else
                    output.getStaticField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
            else {
                if(isSetter)
                    output.putField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
                else
                    output.getField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
            }
        }

        @Override
        public void invokeStatic(MethodOutput output) {
            if(!isStatic())
                throw new IllegalArgumentException("Cannot invoke nonstatic method from a static context");
            if (isSetter)
                output.putStaticField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
            else
                output.getStaticField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
        }

        @Override
        public ZenType[] getParameterTypes() {
            return isSetter ? new ZenType[]{type} : new ZenType[0];
        }

        @Override
        public ZenType getReturnType() {
            return isSetter ? ZenType.VOID : ParsedZenClassField.this.type;
        }

        @Override
        public boolean isVarargs() {
            return false;
        }
    
        @Override
        public String getErrorDescription() {
            return "INTERNAL METHOD";
        }
    }
}
