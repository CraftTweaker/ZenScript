package stanhebben.zenscript.definitions;

import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.parser.expression.ParsedExpressionVariable;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.ZenPosition;

import java.util.*;

import static stanhebben.zenscript.ZenTokener.*;

/**
 * @author Stanneke
 */
public class ParsedFunction {

    private final ZenPosition position;
    private final String name;
    private final List<ParsedFunctionArgument> arguments;
    private final ZenType returnType;
    private final Statement[] statements;
    private final String signature;

    public ParsedFunction(ZenPosition position, String name, List<ParsedFunctionArgument> arguments, ZenType returnType, Statement[] statements) {
        this.position = position;
        this.name = name;
        this.arguments = arguments;
        this.returnType = returnType;
        this.statements = statements;

        StringBuilder sig = new StringBuilder();
        sig.append("(");
        for(ParsedFunctionArgument argument : arguments) {
            sig.append(argument.getType().getSignature());
        }
        sig.append(")");
        sig.append(returnType.getSignature());
        signature = sig.toString();
    }

    public static ParsedFunction parse(ZenTokener parser, IEnvironmentGlobal environment) {
        return ParsedFunction.parse(parser, environment, new ArrayList<>());
    }

    static ParsedFunction parse(ZenTokener parser, IEnvironmentGlobal environment, List<ParsedFunctionArgument> arguments) {
        parser.next();
        Token tName = parser.required(ZenTokener.T_ID, "identifier expected");

        // function (argname [as type], argname [as type], ...) [as type] {
        // ...contents... }
        parser.required(T_BROPEN, "( expected");

        if(parser.optional(T_BRCLOSE) == null) {
            Token argName = parser.required(T_ID, "identifier expected");
            ZenType type = ZenTypeAny.INSTANCE;
            ParsedExpression expression = null;
            boolean hasDefaultArgument = false;
            if(parser.optional(T_AS) != null) {
                type = ZenType.read(parser, environment);
            }

            if (parser.optional(T_ASSIGN) != null) {
                expression = ParsedExpression.read(parser, environment);
                if (expression instanceof ParsedExpressionVariable) {
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
                    type2 = ZenType.read(parser, environment);
                }

                if (parser.optional(T_ASSIGN) != null) {
                    expression2 = ParsedExpression.read(parser, environment);
                    if (expression2 instanceof ParsedExpressionVariable) {
                        throw new ParseException(parser.getFile(), parser.getLine(), parser.getLineOffset(), "Variables are not allowed in default arguments");
                    }
                    hasDefaultArgument = true;
                } else if (hasDefaultArgument) {
                    throw new ParseException(parser.getFile(), parser.getLine(), parser.getLineOffset(), "Parameter " + argName2.getValue() + " requires a default argument");
                }

                arguments.add(new ParsedFunctionArgument(argName2.getValue(), type2, expression2));
            }

            parser.required(T_BRCLOSE, ") expected");
        }

        ZenType type = ZenTypeAny.INSTANCE;
        if(parser.optional(T_AS) != null) {
            type = ZenType.read(parser, environment);
        }

        parser.required(T_AOPEN, "{ expected");

        Statement[] statements;
        if(parser.optional(T_ACLOSE) != null) {
            statements = new Statement[0];
        } else {
            ArrayList<Statement> statementsAL = new ArrayList<>();

            while(parser.optional(T_ACLOSE) == null) {
                statementsAL.add(Statement.read(parser, environment, type));
            }
            statements = statementsAL.toArray(new Statement[statementsAL.size()]);
        }

        return new ParsedFunction(tName.getPosition(), tName.getValue(), arguments, type, statements);
    }

    public ZenPosition getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public ZenType getReturnType() {
        return returnType;
    }

    public List<ParsedFunctionArgument> getArguments() {
        return arguments;
    }

    public int countDefaultArguments() {
        return ((int) arguments.stream().map(ParsedFunctionArgument::getDefaultExpression).filter(Objects::nonNull).count());
    }

    public ZenType[] getArgumentTypes() {
        ZenType[] result = new ZenType[arguments.size()];
        for(int i = 0; i < arguments.size(); i++) {
            result[i] = arguments.get(i).getType();
        }
        return result;
    }

    public String getDefaultParameterFieldName(int number) {
        StringBuilder builder = new StringBuilder("method_default_parameter_");
        builder.append(name);
        for (ZenType argumentType : this.getArgumentTypes()) {
            builder.append(argumentType.toASMType().getDescriptor());
        }
        builder.append("_").append(number);
        String name = builder.toString();
        name = name.replace("[", "array_")
                .replace("/", "_")
                .replace(";", "");
        return name;
    }

    public Statement[] getStatements() {
        return statements;
    }
}
