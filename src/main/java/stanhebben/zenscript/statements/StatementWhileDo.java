package stanhebben.zenscript.statements;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.iterator.IteratorWhileDo;
import stanhebben.zenscript.util.*;

import java.util.*;


public class StatementWhileDo extends Statement {
    
    private final Statement body;
    private final ParsedExpression condition;
    
    public StatementWhileDo(ZenPosition position, Statement body, ParsedExpression condition) {
        super(position);
        this.body = body;
        this.condition = condition;
        
    }
    
    @Override
    public void compile(IEnvironmentMethod environment) {
        MethodOutput output = environment.getOutput();
        output.position(getPosition());
        
        final IteratorWhileDo iterator = new IteratorWhileDo(condition, environment);
        int[] locals = new int[0];
        iterator.compileStart(locals);
        
        Label repeat = new Label();
        Label exit = new Label();
        
        output.label(repeat);
        iterator.compilePreIterate(locals, exit);
    
        //Allows for break statements, sets the exit label!
        for (Statement statement : body.getSubStatements()) {
            if (statement instanceof StatementBreak)
                ((StatementBreak) statement).setExit(exit);
        }
        
        body.compile(environment);
        iterator.compilePostIterate(locals, exit, repeat);
        output.label(exit);
        iterator.compileEnd();
    }
    
    @Override
    public List<Statement> getSubStatements() {
        List<Statement> out = new ArrayList<>();
        out.add(this);
        out.addAll(body.getSubStatements());
        return out;
    }
}
