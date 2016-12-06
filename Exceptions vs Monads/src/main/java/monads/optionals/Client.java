package monads.optionals;

public class Client {

    private Server server;
    
    public Client(Server server) {
        this.server = server;
    }
    
    public String execute(String input) {
        return server.makeRequest(input)
                     .orElse("request failed");
    }
}
