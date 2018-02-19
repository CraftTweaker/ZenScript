package stanhebben.zenscript;

import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.impl.IBracketHandler;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.*;
import stanhebben.zenscript.util.Pair;

import java.util.*;

public interface IZenRegistry {
    
    void registerGlobal(String name, IZenSymbol symbol);
    
    void registerExpansion(Class<?> cls);
    
    IZenSymbol resolveBracket(IEnvironmentGlobal environment, List<Token> tokens);
    
    IEnvironmentGlobal makeGlobalEnvironment(Map<String, byte[]> classes);
    
    IZenCompileEnvironment getCompileEnvironment();
    
    Map<String, IZenSymbol> getGlobals();
    
    Set<Pair<Integer, IBracketHandler>> getBracketHandlers();
    
    TypeRegistry getTypes();
    
    SymbolPackage getRoot();
    
    Map<String, TypeExpansion> getExpansions();
    
    void setCompileEnvironment(IZenCompileEnvironment compileEnvironment);
    
    void setGlobals(Map<String, IZenSymbol> globals);
    
    void setBracketHandlers(Set<Pair<Integer, IBracketHandler>> bracketHandlers);
    
    void setTypes(TypeRegistry types);
    
    void setRoot(SymbolPackage root);
    
    void setExpansions(Map<String, TypeExpansion> expansions);
    
    IZenErrorLogger getErrorLogger();
    
    void setErrorLogger(IZenErrorLogger errorLogger);
    
}
