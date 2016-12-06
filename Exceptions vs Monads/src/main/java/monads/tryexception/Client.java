package monads.tryexception;

//https://github.com/zacharyvoase/try
public class Client {

    private Server server;
    
    public Client(Server server) {
        this.server = server;
    }
    
    public String execute(String input) {
        return server.makeRequest(input)
                     .orElse("request failed");
    }
    
    public String executeWithTry(String input) {
        return server.makeRequestWithTry(input)
                     .getResult()
                     .orElse("request failed");
    }
}
