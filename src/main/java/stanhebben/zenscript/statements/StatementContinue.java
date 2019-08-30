package stanhebben.zenscript.statements;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.util.ZenPosition;

public class StatementContinue extends Statement {

    private Label exit;

    public StatementContinue(ZenPosition position) {
        super(position);
    }
    
    public void setExit(Label exit) {
        this.exit = exit;
    }
    
    @Override
    public void compile(IEnvironmentMethod environment) {
        if(exit != null)
            environment.getOutput().goTo(exit);
        else
            environment.error(getPosition(), "Skipping continue statement as it has no proper label. Only use them in loops!");
    }
}
