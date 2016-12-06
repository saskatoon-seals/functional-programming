package monads.tryexception;

import java.util.Optional;

import io.meat.Try;

public class Server {

    private DataAccess dataAccess;
    
    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Optional<String> makeRequest(String input) {
        return makeRequestWithTry(input)
                .getResult();
    }
    
    /*
     * BENEFITS:
     * 
     *   1. Elegant (no try/catch, no side-effects)
     *   2. As soon client wants to process the result, it's forced to handle the exception with
     *     .orElse(..)
     */
    public Try<String> makeRequestWithTry(String input) {
        return Try.attemptChecked(() -> dataAccess.readFile(input));
    }
}
