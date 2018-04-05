package stanhebben.zenscript.definitions;

import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.symbols.SymbolArgument;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.*;

import java.util.*;
import java.util.stream.IntStream;

import static stanhebben.zenscript.ZenTokener.*;

public class ParsedClassConstructor {
    
    protected final ZenType[] types;
    protected final String[] names;
    protected final List<Statement> statements;
    
    public ParsedClassConstructor(List<ZenType> types, List<String> names, List<Statement> statements) {
        this.types = types.toArray(new ZenType[types.size()]);
        this.names = names.toArray(new String[names.size()]);
        this.statements = statements;
    }
    
    
    static ParsedClassConstructor parse(ZenTokener parser, IEnvironmentGlobal environment) {
        parser.required(T_BROPEN, "( Needed");
        //Map<String, ZenType> parameters = new HashMap<>();
        
        List<ZenType> types = new LinkedList<>();
        List<String> names = new LinkedList<>();
        
        while(parser.optional(T_BRCLOSE) == null) {
            String name = parser.required(T_ID, "Parameter identifier required").getValue();
            ZenType type = ZenType.ANY;
            if(parser.optional(T_AS) != null)
                type = ZenType.read(parser, environment);
            if(names.contains(name))
                environment.error("Constructor parameter already present: " + name);
            types.add(type);
            names.add(name);
            parser.optional(T_COMMA);
        }
        parser.required(T_AOPEN, "{ required");
        List<Statement> statements = new ArrayList<>();
        while(parser.optional(T_ACLOSE) == null) {
            statements.add(Statement.read(parser, environment, null));
        }
        return new ParsedClassConstructor(types, names, statements);
    }
    
    public String getDescription() {
        StringBuilder builder = new StringBuilder("(");
        for(ZenType type : types) {
            builder.append(type.toASMType().getDescriptor());
        }
        return builder.append(")V").toString();
    }
    
    public void writeConstructor(IEnvironmentMethod environmentMethod) {
        for(Statement statement : statements) {
            statement.compile(environmentMethod);
        }
    }
    
    public boolean canAccept(Expression[] arguments, IEnvironmentGlobal environment) {
        if(arguments.length != types.length)
            return false;
        return IntStream.range(0, arguments.length).allMatch(i -> arguments[i].getType().canCastImplicit(types[i], environment));
    }
    
    public Class[] parameterClasses() {
        Class[] out = new Class[types.length];
        for(int i = 0; i < types.length; i++)
            out[i] = types[i].toJavaClass();
        return out;
    }
    
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression[] arguments, ZenTypeFrigginClass type) {
        if(arguments.length != this.types.length)
            throw new IllegalArgumentException(String.format("Expected %d arguments, received %d", types.length, arguments.length));
        
        return new Expression(position) {
            @Override
            public void compile(boolean result, IEnvironmentMethod environment) {
                MethodOutput output = environment.getOutput();
                output.newObject(type.getName());
                output.dup();
                for(Expression ex : arguments) {
                    ex.compile(result, environment);
                }
                output.invokeSpecial(type.getName(), "<init>", getDescription());
            }
            
            @Override
            public ZenType getType() {
                return type;
            }
        };
    }
    
    public void injectParameters(IEnvironmentMethod environmentMethod, ZenPosition position) {
        for(int i = 0; i < types.length; i++) {
            environmentMethod.putValue(names[i], new SymbolArgument(i+1, types[i]), position);
        }
    }
}
