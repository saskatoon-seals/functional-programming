package errorcodes;

public class Client {

    private Server server;
    
    public Client(Server server) {
        this.server = server;
    }
    
    public String execute(String result) {
        StringBuilder resultBuilder = new StringBuilder(result);
        
        if (server.makeRequest(resultBuilder) == OperationStatus.REQUEST_FAILED) {
            return "request failed";
        }
        
        return resultBuilder.toString();
    }
}
