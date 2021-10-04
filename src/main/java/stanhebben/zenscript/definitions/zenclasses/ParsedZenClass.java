package stanhebben.zenscript.definitions.zenclasses;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionThis;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.SymbolType;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeZenClass;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

import java.util.*;

import static stanhebben.zenscript.ZenTokener.*;

public class ParsedZenClass {
    
    public final ZenPosition position;
    public final String name;
    public final String className;
    public final ZenTypeZenClass type;
    private final EnvironmentScript classEnvironment;
    private final List<ParsedClassConstructor> constructors = new LinkedList<>();
    private final List<ParsedZenClassField> statics = new LinkedList<>();
    private final List<ParsedZenClassField> nonStatics = new LinkedList<>();
    private final List<ParsedZenClassMethod> methods = new LinkedList<>();
    private final Map<String, ZenNativeMember> members = new LinkedHashMap<>();
    public Class thisClass;
    
    public ParsedZenClass(ZenPosition position, String name, String className, EnvironmentScript classEnvironment) {
        this.position = position;
        
        this.name = name;
        this.className = className;
        this.classEnvironment = classEnvironment;
        this.type = new ZenTypeZenClass(this);
    }
    
    
    public static ParsedZenClass parse(ZenTokener parser, IEnvironmentGlobal environmentGlobal) {
        
        EnvironmentScript classEnvironment = new EnvironmentScript(environmentGlobal);
        parser.next();
        
        final Token id = parser.required(T_ID, "ClassName required");
        parser.required(T_AOPEN, "{ expected");
        
        final String name = id.getValue();
        final ZenPosition position = id.getPosition();
        ParsedZenClass classTemplate = new ParsedZenClass(position, name, environmentGlobal.makeClassNameWithMiddleName(position.getFile().getClassName() + "_" + name + "_"), classEnvironment);
        classEnvironment.putValue(name, new SymbolType(classTemplate.type), classTemplate.position);
        
        Token keyword;
        while((keyword = parser.optional(T_VAL, T_VAR, T_STATIC, T_ZEN_CONSTRUCTOR, T_FUNCTION)) != null) {
            final int type = keyword.getType();
            switch(type) {
                case T_VAL:
                case T_VAR:
                case T_STATIC:
                    classTemplate.addField(ParsedZenClassField.parse(parser, classEnvironment, type == T_STATIC, classTemplate.className));
                    break;
                case T_ZEN_CONSTRUCTOR:
                    classTemplate.addConstructor(ParsedClassConstructor.parse(parser, classEnvironment));
                    break;
                case T_FUNCTION:
                    classTemplate.addMethod(ParsedZenClassMethod.parse(parser, classEnvironment, classTemplate.className));
            }
        }
        parser.required(T_ACLOSE, "} expected");
        return classTemplate;
    }
    
    private void addMethod(ParsedZenClassMethod parsedMethod) {
        ParsedFunction method = parsedMethod.method;
        methods.add(parsedMethod);
        if(!members.containsKey(method.getName())) {
            members.put(method.getName(), new ZenNativeMember());
            classEnvironment.putValue(method.getName(), position1 -> new ExpressionThis(position1, type).getMember(position1, classEnvironment, method.getName()), position);
        }
        parsedMethod.addToMember(members.get(method.getName()));
        for (int i = 0; i < method.getArguments().size(); i++) {
            ParsedFunctionArgument argument = method.getArguments().get(i);
            if (argument.getDefaultExpression() != null) {
                addField(new ParsedZenClassField(true, argument.getType(), argument.getDefaultExpression(), method.getDefaultParameterFieldName(i), className));
            }
        }
    }
    
    private void addConstructor(ParsedClassConstructor parsedClassConstructor) {
        constructors.add(parsedClassConstructor);
    }
    
    private void addField(ParsedZenClassField parsedZenClassField) {
        final String fieldName = parsedZenClassField.name;
        if(!members.containsKey(fieldName))
            members.put(fieldName, new ZenNativeMember());
        parsedZenClassField.addMethodsToMember(members.get(fieldName));
        if(parsedZenClassField.isStatic) {
            statics.add(parsedZenClassField);
            classEnvironment.putValue(fieldName, position1 -> type.getStaticMember(position1, classEnvironment, fieldName), position);
        } else {
            nonStatics.add(parsedZenClassField);
            classEnvironment.putValue(fieldName, position1 -> new ExpressionThis(position1, type).getMember(position1, classEnvironment, fieldName), position);
        }
        
        
    }
    
    public void writeClass(IEnvironmentGlobal environmentGlobal) {
        final ClassWriter newClass = new ZenClassWriter(ClassWriter.COMPUTE_FRAMES);
        newClass.visitSource(position.getFileName(), null);
        newClass.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", new String[0]);
        
        final EnvironmentClass environmentNewClass = new EnvironmentClass(newClass, classEnvironment);
        environmentNewClass.putValue("this", position1 -> new ExpressionThis(position1, type), position);
        
        writeStatics(newClass, environmentNewClass);
        visitNonStatics(newClass);
        
        
        writeConstructors(newClass, environmentNewClass);
        writeMethods(newClass, environmentNewClass);
        newClass.visitEnd();
        
        //ZS ASM STUFF
        byte[] thisClassArray = newClass.toByteArray();
        environmentGlobal.putClass(className, thisClassArray);
        thisClass = new ClassLoader() {
            private Class<?> find(byte[] array) {
                return defineClass(className, array, 0, array.length);
            }
        }.find(thisClassArray);
    }
    
    private void visitNonStatics(ClassWriter newClass) {
        for(ParsedZenClassField nonStatic : nonStatics) {
            nonStatic.visit(newClass);
        }
    }
    
    private void writeStatics(ClassWriter newClass, EnvironmentClass environmentNewClass) {
        if(!statics.isEmpty()) {
            final MethodOutput clinit = new MethodOutput(newClass, Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC, "<clinit>", "()V", null, null);
            final EnvironmentMethod clinitEnvironment = new EnvironmentMethod(clinit, environmentNewClass);
            clinit.start();
            for(ParsedZenClassField aStatic : statics)
                aStatic.writeAll(clinitEnvironment, newClass, clinit, className);
            clinit.ret();
            clinit.end();
        }
    }
    
    private void writeConstructors(ClassWriter newClass, EnvironmentClass environmentNewClass) {
        for(ParsedClassConstructor constructor : constructors)
            constructor.writeAll(environmentNewClass, newClass, nonStatics, className, position);
    }
    
    private void writeMethods(ClassWriter newClass, EnvironmentClass environmentNewClass) {
        for(ParsedZenClassMethod parsedMethod : methods)
            parsedMethod.writeAll(newClass, environmentNewClass);
    }
    
    public ZenType[] predictCallTypes(int numArguments) {
        for(ParsedClassConstructor con : constructors) {
            if(con.types.length == numArguments)
                return con.types;
        }
        return new ZenType[0];
    }
    
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression[] arguments) {
        for(ParsedClassConstructor constructor : constructors) {
            if(constructor.canAccept(arguments, environment))
                return constructor.call(position, arguments, type);
        }
        environment.error("Could not find constructor for " + name + "with " + arguments.length + " arguments.");
        return new ExpressionInvalid(position);
    }
    
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name, boolean isStatic) {
        if(members.containsKey(name))
            return isStatic
                    ? members.get(name).instance(position, environment)
                    : members.get(name).instance(position, environment, value);
        environment.error("Could not find " + (isStatic ? "static " : "") + "member " + name);
        return new ExpressionInvalid(position);
    }
}
