package DBObjects.DBCommands;

import DBException.DBObjectDoesNotExistException;
import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBObjects.Database;

public class UseDBCommand extends DBCommand {
    Database databaseToUse;

    public UseDBCommand(String[] commandArgs) throws DBException{
        if (commandArgs.length != 1){
            throw new InvalidCommandArgumentException("Use command doesn't have any arguments.");
        }
        commandString = commandArgs[0];
        tokenizedCommand = splitCommand(commandString);
    }

    public void prepareCommand() throws DBException {
        String databaseName = tokenizedCommand[0];
        if (!isNameValid(databaseName)){
            throw new InvalidCommandArgumentException("Database did not have a valid name.");
        }
        databaseToUse = new Database(databaseName);
    }
    public void executeCommand() throws DBException {
        if (!databaseToUse.dbObjectExists()){
            throw new DBObjectDoesNotExistException();
        }
        //databaseToUse.loadTables
        workingDatabase = databaseToUse;
    }

    public String[] splitCommand(String commandString){
        return commandString.split("\\s+");
    }

    public static void test(){

    }
}
