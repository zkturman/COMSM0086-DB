package DBObjects.DBCommands;

import DBException.*;
import DBObjects.*;

public class UseDBCommand extends DBCommand {
    DBDatabase databaseToUse;

    public UseDBCommand(String[] commandArgs) throws DBException{
        if (commandArgs.length != 1){
            throw new InvalidCommandArgumentException("Use command doesn't have any arguments.");
        }
        commandString = commandArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
    }

    public void prepareCommand() throws DBException {
        String databaseName = getNextToken(tokenizedCommand, 0);
        if (!isNameValid(databaseName)){
            throw new InvalidCommandArgumentException("Database did not have a valid name.");
        }
        databaseToUse = new DBDatabase(databaseName);
    }
    public void executeCommand() throws DBException {
        if (!databaseToUse.dbObjectExists()){
            throw new DBObjectDoesNotExistException("Could not find database.");
        }
        workingDatabase = databaseToUse;
    }

    public static void test(){

    }
}
