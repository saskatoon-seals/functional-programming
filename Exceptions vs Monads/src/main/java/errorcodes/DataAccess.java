package errorcodes;

public class DataAccess {

    /*
     * The worst thing here is, that I need to use an output parameter.
     * Another option would be to create a class that has both string and status code variables.
     */
    public OperationStatus readFile(StringBuilder result) {
        if (!result.toString().equals("ready for reading")) {
            return OperationStatus.READ_FAILED;
        }
        
        result.delete(0, result.length());
        result.append("something");
        
        return OperationStatus.SUCCESS;
    }

    
}
