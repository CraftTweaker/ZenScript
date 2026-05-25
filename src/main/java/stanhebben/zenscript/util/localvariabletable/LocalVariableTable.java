package stanhebben.zenscript.util.localvariabletable;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentClass;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LocalVariableTable {

    private final IEnvironmentClass envClass;
    private final LinkedList<List<LocalVariable>> scopeStack = new LinkedList<>();

    private final List<LocalVariable> variables = new ArrayList<>();

    public LocalVariableTable(IEnvironmentClass environment) {
        this.envClass = environment;
    }


    public void beginScope() {
        scopeStack.push(new ArrayList<>());
    }


    public void endScope(Label end) {
        if (scopeStack.isEmpty()) {
            envClass.error("End of variable scope without begin");
            return;
        }

        List<LocalVariable> stack = scopeStack.pop();

        for (LocalVariable variable : stack) {
            variable.setEndLabel(end);
        }

        variables.addAll(stack);

    }

    public void ensureFirstLabel(MethodOutput output, ZenPosition position) {
        if (output.lastLabel() == null) {
            if (position != null) {
                output.position(position);
            } else {
                output.label(new Label());
            }
        }
    }

    public void endMethod(MethodOutput output) {
        Label lastLabel = new Label();
        output.label(lastLabel);

        endScope(lastLabel);
    }

    public void put(LocalVariable variable) {
        if (scopeStack.isEmpty()) {
            envClass.error("Adding local variable without begin scope");
            return;
        }
        scopeStack.peek().add(variable);
    }


    public void writeLocalVariables(MethodOutput output) {
        if (!scopeStack.isEmpty()) {
            envClass.error("There are unclosed scopes before write local variables, some variables may be missing");
        }
        for (LocalVariable variable : variables) {
            variable.writeTo(output);
        }
    }


}
