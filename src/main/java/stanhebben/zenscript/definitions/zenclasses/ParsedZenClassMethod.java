package stanhebben.zenscript.definitions.zenclasses;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.parser.expression.ParsedExpressionVariable;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.statements.StatementReturn;
import stanhebben.zenscript.symbols.SymbolArgument;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAny;
import stanhebben.zenscript.type.ZenTypeZenClass;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.localvariabletable.LocalVariable;
import stanhebben.zenscript.util.localvariabletable.LocalVariableTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static stanhebben.zenscript.ZenTokener.*;

public class ParsedZenClassMethod {
    
    
    final ParsedFunction method;
    final String className;
    private ZenTypeZenClass owner;
    
    public ParsedZenClassMethod(ParsedFunction parse, String className) {
        
        this.method = parse;
        this.className = className;
    }
    
    public static ParsedZenClassMethod parse(ZenTokener parser, EnvironmentScript classEnvironment, String className) {
        Token tName = parser.required(ZenTokener.T_ID, "identifier expected");
        
        // function (argname [as type], argname [as type], ...) [as type] {
        // ...contents... }
        parser.required(T_BROPEN, "( expected");
        
        List<ParsedFunctionArgument> arguments = new ArrayList<>();
        if(parser.optional(T_BRCLOSE) == null) {
            Token argName = parser.required(T_ID, "identifier expected");
            ZenType type = ZenTypeAny.INSTANCE;
            ParsedExpression expression = null;
            boolean hasDefaultArgument = false;
            if(parser.optional(T_AS) != null) {
                type = ZenType.read(parser, classEnvironment);
            }
            
            if(parser.optional(T_ASSIGN) != null) {
                expression = ParsedExpression.read(parser, classEnvironment);
                if(expression instanceof ParsedExpressionVariable) {
                    throw new ParseException(parser.getFile(), parser.getLine(), parser.getLineOffset(), "Variables are not allowed in default arguments");
                }
                hasDefaultArgument = true;
            }
            
            arguments.add(new ParsedFunctionArgument(argName.getValue(), type, expression));
            
            while(parser.optional(T_COMMA) != null) {
                Token argName2 = parser.required(T_ID, "identifier expected");
                ZenType type2 = ZenTypeAny.INSTANCE;
                ParsedExpression expression2 = null;
                if(parser.optional(T_AS) != null) {
                    type2 = ZenType.read(parser, classEnvironment);
                }
                
                if(parser.optional(T_ASSIGN) != null) {
                    expression2 = ParsedExpression.read(parser, classEnvironment);
                    if(expression2 instanceof ParsedExpressionVariable) {
                        throw new ParseException(parser.getFile(), parser.getLine(), parser.getLineOffset(), "Variables are not allowed in default arguments");
                    }
                    hasDefaultArgument = true;
                } else if(hasDefaultArgument) {
                    throw new ParseException(parser.getFile(), parser.getLine(), parser.getLineOffset(), "Parameter " + argName2.getValue() + " requires a default argument");
                }
                
                arguments.add(new ParsedFunctionArgument(argName2.getValue(), type2, expression2));
            }
            
            parser.required(T_BRCLOSE, ") expected");
        }
        
        ZenType type = ZenTypeAny.INSTANCE;
        if(parser.optional(T_AS) != null) {
            type = ZenType.read(parser, classEnvironment);
        }
        
        parser.required(T_AOPEN, "{ expected");
        
        Statement[] statements;
        if(parser.optional(T_ACLOSE) != null) {
            statements = new Statement[0];
        } else {
            ArrayList<Statement> statementsAL = new ArrayList<>();
            
            while(parser.optional(T_ACLOSE) == null) {
                statementsAL.add(Statement.read(parser, classEnvironment, type));
            }
            statements = statementsAL.toArray(new Statement[statementsAL.size()]);
        }
        
        
        return new ParsedZenClassMethod(new ParsedFunction(tName.getPosition(), tName.getValue(), arguments, type, statements), className);
    }
    
    public void addToMember(ZenNativeMember zenNativeMember) {
        zenNativeMember.addMethod(new ZenClassMethod());
    }
    
    public void writeAll(ClassVisitor newClass, IEnvironmentClass environmentNewClass) {
        String description = method.getSignature();
        MethodOutput methodOutput = new MethodOutput(newClass, Opcodes.ACC_PUBLIC, method.getName(), description, null, null);
        IEnvironmentMethod methodEnvironment = new EnvironmentMethod(methodOutput, environmentNewClass);
        
        LocalVariableTable localVariableTable = methodEnvironment.getLocalVariableTable();
        localVariableTable.beginScope();
        localVariableTable.put(LocalVariable.thisRef(owner.toASMType()));
        List<ParsedFunctionArgument> arguments = method.getArguments();
        for(int i = 0, j = 0; i < arguments.size(); ) {
            ParsedFunctionArgument argument = arguments.get(i);
            SymbolArgument symbolArgument = new SymbolArgument(++i + j, argument.getType());
            methodEnvironment.putValue(argument.getName(), symbolArgument, method.getPosition());
            localVariableTable.put(LocalVariable.parameter(argument.getName(), symbolArgument));
            if(argument.getType().isLarge())
                ++j;
        }
        methodOutput.start();
        Statement[] statements = method.getStatements();
        for(Statement statement : statements) {
            statement.compile(methodEnvironment);
        }
        
        localVariableTable.ensureFirstLabel(methodOutput, method.getPosition());
        if(method.getReturnType() != ZenType.VOID) {
            if(statements.length != 0 && statements[statements.length - 1] instanceof StatementReturn) {
                if(((StatementReturn) statements[statements.length - 1]).getExpression() != null) {
                    method.getReturnType().defaultValue(method.getPosition()).compile(true, methodEnvironment);
                    methodOutput.returnType(method.getReturnType().toASMType());
                }
            } else {
                method.getReturnType().defaultValue(method.getPosition()).compile(true, methodEnvironment);
                methodOutput.returnType(method.getReturnType().toASMType());
            }
        } else if(statements.length == 0 || !(statements[statements.length - 1] instanceof StatementReturn)) {
            methodOutput.ret();
        }
        localVariableTable.endMethod(methodOutput);
        localVariableTable.writeLocalVariables(methodOutput);
        methodOutput.end();
    }
    
    public void setOwnerClassType(ZenTypeZenClass type) {
        this.owner = type;
    }
    
    public class ZenClassMethod implements IJavaMethod {
        
        @Override
        public boolean isStatic() {
            return false;
        }
        
        @Override
        public boolean accepts(int numArguments) {
            int defaultArguments = method.countDefaultArguments();
            if(defaultArguments == 0) {
                return method.getArgumentTypes().length == numArguments;
            } else {
                return numArguments + defaultArguments >= method.getArgumentTypes().length;
            }
        }
        
        @Override
        public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
            return accepts(arguments.length) && IntStream.range(0, arguments.length).allMatch(i -> arguments[i].getType().canCastImplicit(method.getArgumentTypes()[i], environment));
        }
        
        @Override
        public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
            return matchesExact(arguments) ? JavaMethod.PRIORITY_HIGH : accepts(environment, arguments) ? JavaMethod.PRIORITY_LOW : JavaMethod.PRIORITY_INVALID;
        }
        
        private boolean matchesExact(Expression... arguments) {
            if(!accepts(arguments.length))
                return false;
            for(int i = 0; i < arguments.length; i++) {
                if(arguments[i].getType().toJavaClass() != method.getArgumentTypes()[i].toJavaClass())
                    return false;
            }
            return true;
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
        
        @Override
        public String getErrorDescription() {
            final StringBuilder builder = new StringBuilder(method.getName()).append("(");
            
            for(ZenType zenType : method.getArgumentTypes()) {
                builder.append(zenType.toString()).append(", ");
            }
            
            final int length = builder.length();
            builder.delete(length - 2, length);
            
            return builder.append(")").toString();
        }
        
        public String getOwner() {
            return className;
        }
        
        public ParsedFunction getFunction() {
            return method;
        }
    }
}
