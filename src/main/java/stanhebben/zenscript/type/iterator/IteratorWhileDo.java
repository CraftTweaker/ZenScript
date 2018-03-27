package stanhebben.zenscript.type.iterator;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.MethodOutput;

public class IteratorWhileDo implements IZenIterator {
    
    private ParsedExpression condition;
    private MethodOutput output;
    private IEnvironmentMethod environment;
    
    public IteratorWhileDo(ParsedExpression condition, IEnvironmentMethod environment) {
        this.condition = condition;
        this.output = environment.getOutput();
        this.environment = environment;
    }
    
    @Override
    public void compileStart(int[] locals) {
        //Nothing required since we don't need to call an entrySet or something
    }
    
    @Override
    public void compilePreIterate(int[] locals, Label exit) {
        condition.compile(environment, ZenType.BOOL).eval(environment).cast(condition.getPosition(), environment, ZenType.BOOL).compile(true, environment);
        output.ifEQ(exit);
    }
    
    @Override
    public void compilePostIterate(int[] locals, Label exit, Label repeat) {
        output.goTo(repeat);
    }
    
    @Override
    public void compileEnd() {
        //Nothing required since we don't need to close anything.
    }
    
    @Override
    public ZenType getType(int i) {
        return ZenType.VOID;
    }
}
