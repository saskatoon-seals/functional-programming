package errorcodes;

public class Server {

    private DataAccess dataAccess;
    
    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public OperationStatus makeRequest(StringBuilder result) {        
        if (dataAccess.readFile(result) != OperationStatus.SUCCESS) {
            return OperationStatus.REQUEST_FAILED;
        }
        
        return OperationStatus.SUCCESS;        
    }
}
