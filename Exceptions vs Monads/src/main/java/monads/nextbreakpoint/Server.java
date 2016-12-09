package monads.nextbreakpoint;

import java.io.IOException;
import java.util.function.Function;

import com.nextbreakpoint.Try;

public class Server {

    private DataAccess dataAccess;
    
    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /*
     * Will return:
     * public static <R> Try<R, Exception> of(Callable<R> callable)
     * 
     * So exception can be only of a type Exception!
     * 
     * Type String in Try<String> is inferred from the type of input
     */
    public Try<String, IOException> makeRequestWithTry(String input) {
        return Try.of(() -> dataAccess.readFile(input))
                  .mapper(exceptionMapper());
    }
    
    //DRAWBACK: Exception type is unknown in advance because lazy evaluation, so mapper is needed.
    private static Function<Exception, IOException> exceptionMapper() {
        return e -> (e instanceof IOException) ? (IOException) e : new IOException("IO Error", e);
    }
}
