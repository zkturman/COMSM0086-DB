package DBException;

public class InvalidCommandArgumentException extends DBException {
    public InvalidCommandArgumentException(String errorMessage){
        super(errorMessage);
    }
}
