package DBException;

import DBObjects.DBCommands.DBCommand;

public class DBCommandFormException extends DBException {
    public DBCommandFormException(String errorMessage){
        super(errorMessage);
    }
}
