package checkedexceptions;

import java.io.IOException;

public class Server {

    private DataAccess dataAccess;
    
    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String makeRequest(String input) throws ServerException {        
        try {
            return dataAccess.readFile(input);
        } catch (IOException e) {
            throw new ServerException(e);
        }    
    }
}
