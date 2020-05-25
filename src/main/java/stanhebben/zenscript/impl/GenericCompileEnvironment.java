package stanhebben.zenscript.impl;

import stanhebben.zenscript.*;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;

import java.util.List;

public class GenericCompileEnvironment implements IZenCompileEnvironment {
    
    private IZenRegistry registry;
    
    public GenericCompileEnvironment() {
    }
    
    @Override
    public IZenErrorLogger getErrorLogger() {
        return registry.getErrorLogger();
    }
    
    @Override
    public IZenSymbol getGlobal(String name) {
        final IZenSymbol symbol = registry.getGlobals().get(name);
        if(symbol != null) {
            return symbol;
        }
        return registry.getRoot().get(name);
    }
    
    @Override
    public IZenSymbol getBracketed(IEnvironmentGlobal environment, List<Token> tokens) {
        return registry.resolveBracket(environment, tokens);
    }
    
    @Override
    public TypeRegistry getTypeRegistry() {
        return registry.getTypes();
    }
    
    @Override
    public TypeExpansion getExpansion(String type) {
        return registry.getExpansions().get(type);
    }
    
    public IZenRegistry getRegistry() {
        return registry;
    }
    
    public void setRegistry(IZenRegistry registry) {
        this.registry = registry;
    }
}
