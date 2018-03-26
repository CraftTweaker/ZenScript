package stanhebben.zenscript.statements;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.util.ZenPosition;

import java.util.*;

public class StatementBreak extends Statement {
    
    private Label exit;
    
    public StatementBreak(ZenPosition position) {
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
            environment.error(getPosition(), "Break Statement without proper label, report to the author!");
    }
}
