package checkedexceptions;

public class Client {

    private Server server;
    
    public Client(Server server) {
        this.server = server;
    }
    
    public String execute(String input) {
        try {
            return server.makeRequest(input);
        } catch (ServerException e) {
            return "request failed";
        }
    }
}
