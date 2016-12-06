package uncheckedexceptions;

public class ServerException extends RuntimeException {

    public ServerException(Throwable cause) {
        super(cause);
    }
}
