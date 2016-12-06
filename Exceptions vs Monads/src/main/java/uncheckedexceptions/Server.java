package uncheckedexceptions;

public class Server {

    private DataAccess dataAccess;
    
    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /*
     * BENEFIT: 
     * 
     * A lot cleaner implementation when catching and re-throwing exception isn't needed
     */
    public String makeRequest(String input) {        
        return dataAccess.readFile(input);    
    }
}
