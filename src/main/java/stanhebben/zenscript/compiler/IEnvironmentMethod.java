package stanhebben.zenscript.compiler;

import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.localvariabletable.LocalVariableTable;

/**
 * @author Stan
 */
public interface IEnvironmentMethod extends IEnvironmentClass {
    
    MethodOutput getOutput();

    int getLocal(SymbolLocal variable);
    int getLocal(SymbolLocal variable, boolean create);


    LocalVariableTable getLocalVariableTable();

}
