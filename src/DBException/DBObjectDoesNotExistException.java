package DBException;

public class DBObjectDoesNotExistException extends DBException {
    public DBObjectDoesNotExistException(String errorString){
        super(errorString);
    }
}
