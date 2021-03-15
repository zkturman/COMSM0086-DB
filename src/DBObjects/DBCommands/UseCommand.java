package DBObjects.DBCommands;

import DBException.DBObjectDoesNotExistException;
import DBException.DatabaseException;
import DBException.InvalidCommandArgumentException;
import DBObjects.Database;

import java.util.Arrays;

public class UseCommand extends Command {
    Database databaseToUse;

    public UseCommand(String [] useArgs){
        super(useArgs);
    }

    public void parseCommand() throws DatabaseException{
        if (followingSQLCommands.length != 1){
            System.out.println("There are an incorrect number of arguments for USE");
            throw new InvalidCommandArgumentException();
        }
        String databaseName = followingSQLCommands[0];
        if (!isNameValid(databaseName)){
            System.out.println("Database did not have a correct name.");
            throw new InvalidCommandArgumentException();
        }
        databaseToUse = new Database(databaseName);
    }
    public void interpretCommand() throws DatabaseException{
        if (!databaseToUse.dbObjectExists()){
            throw new DBObjectDoesNotExistException();
        }
        //databaseToUse.loadTables
        workingDatabase = databaseToUse;
        System.out.println("we set the database in the useCommand");
    }
}
