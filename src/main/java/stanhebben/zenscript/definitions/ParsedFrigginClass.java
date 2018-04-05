package stanhebben.zenscript.definitions;

import org.objectweb.asm.*;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.statements.*;
import stanhebben.zenscript.symbols.*;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.type.natives.*;
import stanhebben.zenscript.util.*;

import java.util.*;
import java.util.stream.IntStream;

import static stanhebben.zenscript.ZenTokener.*;
import static stanhebben.zenscript.util.ZenTypeUtil.internal;

public class ParsedFrigginClass {
    
    
    public final ZenPosition position;
    public final String name;
    public final String className;
    public final ZenTypeFrigginClass type;
    public final List<ParsedClassConstructor> constructors;
    private final Map<String, Pair<ZenType, Statement>> staticFields;
    private final Map<String, Pair<ZenType, Statement>> nonStaticFields;
    private final Map<String, ZenNativeMember> members = new HashMap<>();
    private final List<ParsedFunction> methods = new ArrayList<>();
    public Class<?> thisClass = Object.class;
    
    private ParsedFrigginClass(ZenPosition position, String name, IEnvironmentGlobal environment, List<ParsedClassConstructor> constructors, Map<String, Pair<ZenType, Statement>> staticFields, Map<String, Pair<ZenType, Statement>> nonStaticFields) {
        this.position = position;
        this.name = name;
        this.className = environment.makeClassName();
        this.constructors = constructors;
        this.staticFields = staticFields;
        this.nonStaticFields = nonStaticFields;
        this.type = new ZenTypeFrigginClass(this);
        
        for(Map.Entry<String, Pair<ZenType, Statement>> statics : staticFields.entrySet()) {
            if(!members.containsKey(statics.getKey()))
                members.put(statics.getKey(), new ZenNativeMember());
            ZenNativeMember member = members.get(statics.getKey());
            member.setGetter(new ZenClassFieldMethod(true, false, statics.getKey(), statics.getValue().getKey()));
            member.setSetter(new ZenClassFieldMethod(true, true, statics.getKey(), statics.getValue().getKey()));
        }
        
        for(Map.Entry<String, Pair<ZenType, Statement>> nonStatics : nonStaticFields.entrySet()) {
            if(!members.containsKey(nonStatics.getKey()))
                members.put(nonStatics.getKey(), new ZenNativeMember());
            ZenNativeMember member = members.get(nonStatics.getKey());
            member.setGetter(new ZenClassFieldMethod(false, false, nonStatics.getKey(), nonStatics.getValue().getKey()));
            member.setSetter(new ZenClassFieldMethod(false, true, nonStatics.getKey(), nonStatics.getValue().getKey()));
        }
        
        environment.putValue(name, new SymbolType(type), position);
    }
    
    
    public static ParsedFrigginClass createFrigginClass(ZenTokener parser, IEnvironmentGlobal environment) {
        parser.next();
        
        Token id = parser.required(T_ID, "ClassName required");
        parser.required(T_AOPEN, "You need to start the class, don't ya?");
        Map<String, Pair<ZenType, Statement>> statics = new HashMap<>();
        while(parser.optional(T_STATIC) != null) {
            String name = parser.required(T_ID, "Static variable identifier required").getValue();
            ZenType type = ZenType.ANY;
            if(parser.optional(T_AS) != null) {
                type = ZenType.read(parser, environment);
            }
            parser.required(T_ASSIGN, "'=' expected");
            statics.put(name, new Pair<>(type, Statement.read(parser, environment, type)));
        }
        Map<String, Pair<ZenType, Statement>> nonStatics = new HashMap<>();
        while(parser.optional(T_VAL) != null) {
            String name = parser.required(T_ID, "Nonstatic variable identifier required").getValue();
            ZenType type = ZenType.ANY;
            if(parser.optional(T_AS) != null) {
                type = ZenType.read(parser, environment);
            }
            Statement statement = null;
            if(parser.optional(T_ASSIGN) == null) {
                parser.required(T_SEMICOLON, "; expected");
            } else {
                statement = Statement.read(parser, environment, type);
            }
            nonStatics.put(name, new Pair<>(type, statement));
        }
        List<ParsedClassConstructor> constructors = new ArrayList<>();
        while(parser.optional(T_FRIGGIN_CONSTRUCTOR) != null) {
            constructors.add(ParsedClassConstructor.parse(parser, environment));
        }
        
        
        ParsedFrigginClass classTemplate = new ParsedFrigginClass(id.getPosition(), id.getValue(), environment, constructors, statics, nonStatics);
        while(parser.peek().getType() == T_FUNCTION) {
            classTemplate.addMethod(ParsedFunction.parse(parser, environment));
        }
        
        parser.required(T_ACLOSE, "You need to close the class, don't ya?");
        
        return classTemplate;
    }
    
    
    public void writeClass(IEnvironmentGlobal environmentGlobal) {
        ClassWriter newClass = new ZenClassWriter(ClassWriter.COMPUTE_FRAMES);
        newClass.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", new String[0]);
        
        EnvironmentClass environmentNewClass = new EnvironmentClass(newClass, environmentGlobal);
        
        if(!staticFields.isEmpty()) {
            MethodOutput clinit = new MethodOutput(newClass, Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            EnvironmentMethod clinitEnvironment = new EnvironmentMethod(clinit, environmentNewClass);
            clinit.start();
            for(Map.Entry<String, Pair<ZenType, Statement>> statics : staticFields.entrySet()) {
                
                String fieldName = statics.getKey();
                
                String descriptor = statics.getValue().getKey().toASMType().getDescriptor();
                Statement statement = statics.getValue().getValue();
                clinitEnvironment.putValue(fieldName, position1 -> type.getMember(position1, environmentGlobal, null, fieldName), position);
                
                newClass.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, fieldName, descriptor, null, null).visitEnd();
                statement.compile(clinitEnvironment, true);
                clinit.putStaticField(className, fieldName, descriptor);
            }
            
            clinit.ret();
            clinit.end();
        }
        
        if(!nonStaticFields.isEmpty()) {
            for(Map.Entry<String, Pair<ZenType, Statement>> nonStatic : nonStaticFields.entrySet()) {
                String fieldName = nonStatic.getKey();
                String descriptor = nonStatic.getValue().getKey().toASMType().getDescriptor();
                newClass.visitField(Opcodes.ACC_PUBLIC, fieldName, descriptor, null, null).visitEnd();
            }
        }
        environmentNewClass.putValue("thisFrigginClass", ExpressionThis::new, position);
        for(Map.Entry<String, Pair<ZenType, Statement>> nonStatic : nonStaticFields.entrySet()) {
            String fieldName = nonStatic.getKey();
            environmentNewClass.putValue(fieldName, position1 -> type.getMember(position1, environmentGlobal, new ExpressionThis(position1), fieldName), position);
        }
        for(ParsedClassConstructor constructor : constructors) {
            
            MethodOutput initVisitor = new MethodOutput(newClass, Opcodes.ACC_PUBLIC, "<init>", constructor.getDescription(), null, null);
            IEnvironmentMethod environmentMethod = new EnvironmentMethod(initVisitor, environmentNewClass);
            constructor.injectParameters(environmentMethod, position);
            initVisitor.start();
            
            initVisitor.loadObject(0);
            initVisitor.invokeSpecial(internal(Object.class), "<init>", "()V");
            
            
            initNonStaticFields(environmentMethod, initVisitor, className);
            constructor.writeConstructor(environmentMethod);
            initVisitor.ret();
            initVisitor.end();
        }
        
        createMethods(newClass, environmentNewClass);
        
        
        newClass.visitEnd();
        byte[] thisClassArray = newClass.toByteArray();
        environmentGlobal.putClass(className, thisClassArray);
        thisClass = new ClassLoader() {
            private Class<?> find() {
                return defineClass(className, thisClassArray, 0, thisClassArray.length);
            }
        }.find();
    }
    
    
    private void initNonStaticFields(IEnvironmentMethod environmentMethod, MethodOutput initVisitor, String classname) {
        for(Map.Entry<String, Pair<ZenType, Statement>> nonStatic : nonStaticFields.entrySet()) {
            initVisitor.loadObject(0);
            Statement statement = nonStatic.getValue().getValue();
            if(statement == null)
                continue;
            String fieldName = nonStatic.getKey();
            String descriptor = nonStatic.getValue().getKey().toASMType().getDescriptor();
            
            statement.compile(environmentMethod, true);
            initVisitor.putField(classname, fieldName, descriptor);
        }
    }
    
    private void createMethods(ClassWriter newClass, EnvironmentClass environmentNewClass) {
        for(ParsedFunction method : methods) {
            String description = method.getSignature();
            MethodOutput methodOutput = new MethodOutput(newClass, Opcodes.ACC_PUBLIC, method.getName(), description, null, null);
            IEnvironmentMethod methodEnvironment = new EnvironmentMethod(methodOutput, environmentNewClass);
            
            List<ParsedFunctionArgument> arguments = method.getArguments();
            for(int i = 0; i < arguments.size(); ) {
                ParsedFunctionArgument argument = arguments.get(i);
                methodEnvironment.putValue(argument.getName(), new SymbolArgument(++i, argument.getType()), method.getPosition());
            }
            methodOutput.start();
            Statement[] statements = method.getStatements();
            for(Statement statement : statements) {
                statement.compile(methodEnvironment);
            }
            
            if(method.getReturnType() != ZenType.VOID) {
                if(statements[statements.length - 1] instanceof StatementReturn) {
                    if(((StatementReturn) statements[statements.length - 1]).getExpression() != null) {
                        method.getReturnType().defaultValue(method.getPosition()).compile(true, methodEnvironment);
                        methodOutput.returnType(method.getReturnType().toASMType());
                    }
                } else {
                    method.getReturnType().defaultValue(method.getPosition()).compile(true, methodEnvironment);
                    methodOutput.returnType(method.getReturnType().toASMType());
                }
            } else if(!(statements[statements.length - 1] instanceof StatementReturn)) {
                methodOutput.ret();
            }
            methodOutput.end();
        }
    }
    
    private void addMethod(ParsedFunction method) {
        if(!members.containsKey(method.getName()))
            members.put(method.getName(), new ZenNativeMember());
        members.get(method.getName()).addMethod(new ZenClassMethod(method));
        methods.add(method);
    }
    
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name, boolean isStatic) {
        if(members.containsKey(name))
            return members.get(name).instance(position, environment, value);
        environment.error("Could not find " + (isStatic ? "static " : "") + "member " + name);
        return new ExpressionInvalid(position);
    }
    
    public ZenType[] predictCallTypes(int numArguments) {
        for(ParsedClassConstructor con : constructors) {
            if(con.types.length == numArguments)
                return con.types;
        }
        return new ZenType[0];
    }
    
    class ZenClassMethod implements IJavaMethod {
        
        private final ParsedFunction method;
        
        ZenClassMethod(ParsedFunction method) {
            this.method = method;
        }
        
        @Override
        public boolean isStatic() {
            return false;
        }
        
        @Override
        public boolean accepts(int numArguments) {
            return method.getArgumentTypes().length == numArguments;
        }
        
        @Override
        public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
            return accepts(arguments.length) && IntStream.range(0, arguments.length).allMatch(i -> arguments[i].getType().canCastImplicit(method.getArgumentTypes()[i], environment));
        }
        
        @Override
        public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
            return 0;
        }
        
        @Override
        public void invokeVirtual(MethodOutput output) {
            output.invokeVirtual(className, method.getName(), method.getSignature());
        }
        
        @Override
        public void invokeStatic(MethodOutput output) {
            throw new UnsupportedOperationException("Cannot statically invoke a virtual method");
        }
        
        @Override
        public ZenType[] getParameterTypes() {
            return method.getArgumentTypes();
        }
        
        @Override
        public ZenType getReturnType() {
            return method.getReturnType();
        }
        
        @Override
        public boolean isVarargs() {
            return false;
        }
    }
    
    class ZenClassFieldMethod implements IJavaMethod {
        
        private final boolean isSetter;
        //Is the FIELD static
        private final boolean isStatic;
        private final String name;
        private final ZenType type;
        
        ZenClassFieldMethod(boolean isStatic, boolean isSetter, String name, ZenType type) {
            
            this.isStatic = isStatic;
            this.isSetter = isSetter;
            this.name = name;
            this.type = type;
        }
        
        @Override
        public boolean isStatic() {
            return isStatic;
        }
        
        @Override
        public boolean accepts(int numArguments) {
            return numArguments == (isSetter ? 1 : 0);
        }
        
        @Override
        public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
            return accepts(arguments.length) && (!isSetter || arguments[0].getType().canCastImplicit(type, environment));
        }
        
        @Override
        public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
            return 0;
        }
        
        @Override
        public void invokeVirtual(MethodOutput output) {
            if(isStatic) {
                if(isSetter)
                    output.putStaticField(className, name, type.getSignature());
                else
                    output.getStaticField(className, name, type.getSignature());
            } else {
                if(isSetter)
                    output.putField(className, name, type.getSignature());
                else
                    output.getField(className, name, type.getSignature());
            }
        }
        
        @Override
        public void invokeStatic(MethodOutput output) {
            if(!isStatic)
                throw new IllegalArgumentException("Cannot invoke nonstatic method from a static context");
            output.getStaticField(className, name, type.getSignature());
        }
        
        @Override
        public ZenType[] getParameterTypes() {
            return isSetter ? new ZenType[]{type} : new ZenType[0];
        }
        
        @Override
        public ZenType getReturnType() {
            return isSetter ? ZenType.VOID : type;
        }
        
        @Override
        public boolean isVarargs() {
            return false;
        }
    }
    
    class ExpressionThis extends Expression {
        
        ExpressionThis(ZenPosition position) {
            super(position);
        }
        
        @Override
        public void compile(boolean result, IEnvironmentMethod environment) {
            if(result)
                environment.getOutput().loadObject(0);
        }
        
        @Override
        public ZenType getType() {
            return type;
        }
    }
}
