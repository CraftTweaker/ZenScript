package stanhebben.zenscript.definitions;

import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;

/**
 * @author Stanneke
 */
public class ParsedFunctionArgument {

    private final String name;
    private final ZenType type;
    private final ParsedExpression defaultExpression;

    public ParsedFunctionArgument(String name, ZenType type, ParsedExpression defaultExpression) {
        this.name = name;
        this.type = type;
        this.defaultExpression = defaultExpression;
    }

    @Deprecated
    public ParsedFunctionArgument(String name, ZenType type) {
        this(name, type, null);
    }

    public String getName() {
        return name;
    }

    public ZenType getType() {
        return type;
    }

    public ParsedExpression getDefaultExpression() {
        return defaultExpression;
    }
}
