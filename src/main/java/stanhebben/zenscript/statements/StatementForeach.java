package stanhebben.zenscript.statements;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.*;
import stanhebben.zenscript.util.localvariabletable.LocalVariable;
import stanhebben.zenscript.util.localvariabletable.LocalVariableTable;

import java.util.*;

public class StatementForeach extends Statement {

    private final String[] varnames;
    private final ParsedExpression list;
    private final Statement body;

    public StatementForeach(ZenPosition position, String[] varnames, ParsedExpression list, Statement body) {
        super(position);

        this.varnames = varnames;
        this.list = list;
        this.body = body;
    }

    @Override
    public void compile(IEnvironmentMethod environment) {
        Expression cList = list.compile(environment, ZenType.ANYARRAY).eval(environment);
        ZenType listType = cList.getType();

        IZenIterator iterator = listType.makeIterator(varnames.length, environment);
        if(iterator == null) {
            environment.error(getPosition(), "No iterator with " + varnames.length + " variables");
            return;
        }

        MethodOutput methodOutput = environment.getOutput();
        Label label = environment.getOutput().position(getPosition());

        IEnvironmentMethod local = new EnvironmentScope(environment);
        LocalVariableTable localVariableTable = local.getLocalVariableTable();
        localVariableTable.beginScope();
        int[] localVariables = new int[varnames.length];
        for(int i = 0; i < localVariables.length; i++) {
            SymbolLocal localVar = new SymbolLocal(iterator.getType(i), true);
            local.putValue(varnames[i], localVar, getPosition());
            localVariableTable.put(LocalVariable.localVariable(varnames[i], localVar, environment, label));
            localVariables[i] = local.getLocal(localVar);
        }

        cList.compile(true, environment);
        iterator.compileStart(localVariables);

        Label repeat = new Label();
        Label exit = new Label();
        Label postIterate = new Label();
    
        //Allows for break statements, sets the exit label!
        for (Statement statement : body.getSubStatements()) {
            if (statement instanceof StatementBreak)
                ((StatementBreak) statement).setExit(exit);
            else if (statement instanceof StatementContinue)
                ((StatementContinue) statement).setExit(postIterate);
        }
        
        methodOutput.label(repeat);
        iterator.compilePreIterate(localVariables, exit);
        body.compile(local);
        methodOutput.label(postIterate);
        iterator.compilePostIterate(localVariables, exit, repeat);
        methodOutput.label(exit);
        iterator.compileEnd();
        localVariableTable.endScope(environment.getOutput().lastLabel());
    }
    
    @Override
    public List<Statement> getSubStatements() {
        List<Statement> out = new ArrayList<>();
        out.add(this);
        out.addAll(body.getSubStatements());
        return out;
    }
}
