package stanhebben.zenscript.dump;

public class DumpUtils {
    
    
    public static String getClassNameFromPath(String path){
        int lastChar = path.lastIndexOf('.');
        if (lastChar < 0) {
            lastChar = path.lastIndexOf('\\');
        }
        
        if (lastChar < 0) {
            lastChar = path.lastIndexOf('/');
        }
        
        if (lastChar < 0) {
            return path;
        }
    
        return path.substring(lastChar + 1, path.length());
    }
    
}
