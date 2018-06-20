package stanhebben.zenscript.dump;

import java.util.*;

public interface IDumpConvertable {
    
    /**
     * Gets a List of dumpable objects that can easily serialized into JSON
     */
    default List<? extends IDumpable> asDumpedObject() {return Collections.emptyList();}
}
