package monads.optionals;

import java.io.IOException;
import java.util.Optional;

public class Server {

    private DataAccess dataAccess;
    
    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Optional<String> makeRequest(String input) {
        try {
            return Optional.of(dataAccess.readFile(input));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
