package stanhebben.zenscript.definitions;

import stanhebben.zenscript.ZenParsedFile;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialExpansionCall;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.util.ZenPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class ParsedExpansion {
    private final ParsedFunction function;
    private final ZenType type;
    private final String owner;

    public ParsedExpansion(ParsedFunction function, ZenType type, ZenParsedFile owner) {
        this.function = function;
        this.type = type;
        this.owner = owner.getClassName();
    }

    public static ParsedExpansion parse(ZenTokener parser, IEnvironmentGlobal environment, ZenParsedFile owner) {
        parser.next();
        Token expandKeyword = parser.next();
        if (expandKeyword.getType() != ZenTokener.T_ID || !expandKeyword.getValue().equals("expand")) {
            throw new ParseException(expandKeyword, "expand expected");
        }
        ZenType type = ZenType.read(parser, environment);
        Token token = parser.peek();
        if (token.getType() != ZenTokener.T_DOLLAR) {
            throw new ParseException(token, "$ expected");
        }
        List<ParsedFunctionArgument> arguments = new ArrayList<>();
        arguments.add(new ParsedFunctionArgument("this", type, null));
        ParsedFunction function = ParsedFunction.parse(parser, environment, arguments);

        return new ParsedExpansion(function, type, owner);
    }

    public String getName() {
        return function.getName();
    }

    public String getCompileName() {
        String typeName;
        if (type instanceof ZenTypeArrayBasic) {
            typeName = "array$" + ((ZenTypeArrayBasic) type).getBaseType().toJavaClass().getSimpleName();
        } else {
            typeName = type.toJavaClass().getSimpleName();
        }
        return "expand$" + typeName + "$" + getName();
    }

    public ParsedFunction getFunction() {
        return function;
    }

    public ZenType getType() {
        return type;
    }

    public String getOwner() {
        return owner;
    }

    public IPartialExpression instance(ZenPosition position, Expression value) {
        return new PartialExpansionCall(position, owner, getCompileName(), function.getSignature(), function.getArguments(), function.getReturnType(), value);
    }
}
