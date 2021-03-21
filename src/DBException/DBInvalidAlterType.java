package DBException;

public class DBInvalidAlterType extends InvalidCommandArgumentException{
    public DBInvalidAlterType(String errorMessage){
        super(errorMessage);
    }
}
