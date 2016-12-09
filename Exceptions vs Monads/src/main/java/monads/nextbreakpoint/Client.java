package monads.nextbreakpoint;

public class Client {

    private Server server;
    
    public Client(Server server) {
        this.server = server;
    }
        
    public String executeWithTry(String input) {
        return server.makeRequestWithTry(input)
                     .value()
                     .orElse("request failed");
    }
}
