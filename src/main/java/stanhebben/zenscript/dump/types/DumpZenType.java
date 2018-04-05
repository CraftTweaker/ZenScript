package stanhebben.zenscript.dump.types;

public class DumpZenType extends DumpClassBase {
    private Class<?> clazz;
    
    public DumpZenType(Class<?> clazz, String zsAliasPath) {
        super(clazz.getCanonicalName(), zsAliasPath);
        
        this.clazz = clazz;
    }
    
    public Class<?> getClazz() {
        return clazz;
    }
}
