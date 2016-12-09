package monads.nextbreakpoint;

import java.io.IOException;

//Let's assume we don't have a control over this class
public class DataAccess {
    
    /*
     * This function has 2 types of output:
     *   1. return value
     *   2. side effect
     */
    public String readFile(String input) throws IOException {
        if (!input.equals("ready for reading")) {
            throw new IOException();
        }
        
        return "something";
    }    
}
