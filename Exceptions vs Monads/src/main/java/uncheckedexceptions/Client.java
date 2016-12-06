package uncheckedexceptions;

public class Client {

    private Server server;
    
    public Client(Server server) {
        this.server = server;
    }
    
    /*
     * DRAWBACK:
     * 
     * Because compiler doesn't warn the developer, he might forget to catch ServerException
     * In that case, execute method would terminate and no result would be returned.
     */
    public String execute(String input) {
        try {
            return server.makeRequest(input);
        } catch (ServerException e) {
            return "request failed";
        }
    }
}
