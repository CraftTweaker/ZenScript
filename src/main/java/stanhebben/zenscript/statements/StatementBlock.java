package stanhebben.zenscript.statements;

import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.util.ZenPosition;

import java.util.*;

public class StatementBlock extends Statement {

    private final List<Statement> statements;

    public StatementBlock(ZenPosition position, List<Statement> statements) {
        super(position);

        this.statements = statements;
    }
    
    @Override
    public void compile(IEnvironmentMethod environment) {
        environment.getOutput().position(getPosition());
        IEnvironmentMethod local = new EnvironmentScope(environment);
        for(Statement statement : statements) {
            statement.compile(local);
            if(statement.isReturn()) {
                return;
            }
        }
    }
    
    @Override
    public void compile(IEnvironmentMethod environment, boolean forced) {
        environment.getOutput().position(getPosition());
        IEnvironmentMethod local = new EnvironmentScope(environment);
        for(Statement statement : statements) {
            statement.compile(local, forced);
            if(statement.isReturn()) {
                return;
            }
        }
    }
    
    @Override
    public List<Statement> getSubStatements() {
        List<Statement> out = new ArrayList<>();
        out.add(this);
        for (Statement statement : statements)
            out.addAll(statement.getSubStatements());
        return out;
    }
}
