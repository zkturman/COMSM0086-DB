package DBException;

import DBObjects.DBCommands.DBCommand;

public class DBConditionFormException extends DBException {
    public DBConditionFormException(String errorMessage){
        super(errorMessage);
    }
}
