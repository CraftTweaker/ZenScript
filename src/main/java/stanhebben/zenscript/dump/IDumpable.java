package stanhebben.zenscript.dump;

import java.util.*;

public interface IDumpable {
    
    /**
     * Gets a List of dumpable objects that can easily serialized into JSON
     */
    default List<? extends IDumpedObject> asDumpedObject() {return Collections.emptyList();}
}
