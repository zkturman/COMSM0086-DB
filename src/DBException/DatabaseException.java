package DBException;
import DBObjects.DBCommands.*;
import DBObjects.*;

public class DatabaseException extends Exception{

    DBObject databaseObject;
    Command command;

    public DatabaseException()
    {
    }

    public DatabaseException(Command errorCommand, DBObject errorObject){
        databaseObject = errorObject;
        command = errorCommand;
    }

    public String toString(){
        return "Ran into issue performing " + command + " on " + databaseObject + ".";
    }

    //Database object already exits
    //
    //
}
