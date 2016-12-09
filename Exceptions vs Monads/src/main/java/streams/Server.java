package streams;

import java.util.Arrays;
import java.util.stream.Stream;

import io.meat.Try;

public class Server {

    private DataAccess dataAccess;
    
    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    
    //APIs:
   
    //Instead of Try<List<String>>: there's no distinction between empty list and "no list" in 
    //my domain model.
    public Stream<Try<String>> makeRequest(String... inputs) {
        return Arrays.stream(inputs)
                     .map(this::readFile);
    }
    
    //Helper methods:
    
    private Try<String> readFile(String input) {
        return Try.attemptChecked(() -> dataAccess.readFile(input));
    }
}
