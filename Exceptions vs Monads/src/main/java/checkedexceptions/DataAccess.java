package checkedexceptions;

import java.io.IOException;

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
