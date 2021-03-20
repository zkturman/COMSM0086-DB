package DBException;

public class DBInvalidCommandException extends DBException {
    public DBInvalidCommandException(String errorMessage){
        super(errorMessage);
    }
}
