package stanhebben.zenscript;

import stanhebben.zenscript.util.ZenPosition;

public interface IZenLogger {
    
    /**
     * Called when an error is detected.
     *
     * @param message error message
     */
    void error(String message);
    /**
     * Called when an error is detected.
     *
     * @param e exception to throw
     * @param message error message
     */
    void error(String message, Throwable e);
    
    
    /**
     * Called when a warning is generated.
     *
     * @param message warning message
     */
    void warning(String message);
    
    /**
     * Called to generate a info.
     *
     * @param message info message
     */
    void info(String message);
}
