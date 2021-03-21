package DBException;

public class NotUsingDBException extends DBException {
    public NotUsingDBException(String errorMessage){
        super(errorMessage);
    }
}
