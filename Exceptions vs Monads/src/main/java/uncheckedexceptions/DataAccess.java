package uncheckedexceptions;

public class DataAccess {
    
    /*
     * This function has 2 types of output:
     *   1. return value
     *   2. side effect
     */
    public String readFile(String input) {
        if (!input.equals("ready for reading")) {
            throw new DataAccessException();
        }
        
        return "something";
    }    
}
