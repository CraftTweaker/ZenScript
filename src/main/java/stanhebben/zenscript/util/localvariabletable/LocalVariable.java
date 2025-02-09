package stanhebben.zenscript.util.localvariabletable;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.symbols.SymbolArgument;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.util.MethodOutput;

import java.util.function.IntSupplier;

public class LocalVariable {
    private final String name;
    private final Type type;

    private final IntSupplier idxSupplier;

    private Label start;
    private Label end;

    private LocalVariable(String name, Type type, IntSupplier idxSupplier, Label start) {
        this.name = name;
        this.type = type;
        this.idxSupplier = idxSupplier;
        this.start = start;
    }

    public void setEndLabel(Label end) {
        this.end = end;
    }

    public void writeTo(MethodOutput output) {
        if (start == null) {
            start = output.firstLabel();
        }
        output.localVariable(name, type, idxSupplier.getAsInt(), start, end);
    }


    public static LocalVariable localVariable(String name, SymbolLocal symbol, IEnvironmentMethod env, Label pos) {
        return new LocalVariable(name, symbol.getType().toASMType(), () -> env.getLocal(symbol, false), pos);
    }

    public static LocalVariable thisRef(Type type) {
        return new LocalVariable("this", type, () -> 0, null);
    }

    public static LocalVariable parameter(String name, SymbolArgument symbol) {
        return new LocalVariable(name, symbol.getType().toASMType(), symbol::getId, null);
    }
}
