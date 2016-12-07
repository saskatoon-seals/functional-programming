package streams;

import java.util.Optional;
import java.util.stream.Collectors;

import io.meat.Try;

public class Client {

    private Server server;
    
    public Client(Server server) {
        this.server = server;
    }
    
    //APIs:
        
    public String executeWithHandlingCollection(String... inputs) {
        return Optional.ofNullable(getResult(inputs))
                       .orElse("request failed");
    }
    
    public String executeWithHandlingIndividualValues(String... inputs) {
        return server.makeRequest(inputs)
                     .map(result -> result.getResult()
                                          .orElse("request failed")) 
                     .collect(Collectors.joining(", "));
    }
    
    //Helper methods:
    
    private String getResult(String... inputs) {
        return server.makeRequest(inputs)
                     .map(Try::getResult) 
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .collect(Collectors.joining(", "));
    }
}
