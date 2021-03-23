package DBObjects.DBCommands;

import DBException.DBObjectDoesNotExistException;
import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBObjects.Database;

import java.util.Arrays;

public class UseDBCommand extends DBCommand {
    Database databaseToUse;

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
        databaseToUse = new Database(databaseName);
    }
    public void executeCommand() throws DBException {
        if (!databaseToUse.dbObjectExists()){
            throw new DBObjectDoesNotExistException("Could not find database.");
        }
        //databaseToUse.loadTables
        workingDatabase = databaseToUse;
    }

    public String[] splitCommand(String commandString){
        return commandString.split("\\s+");
    }

    @Override
    public String getNextToken(String[] tokenAry, int index) throws DBException {
        if (index > tokenAry.length){
            throw new InvalidCommandArgumentException("Command did not have the correct number of arguments.");
        }
        return tokenAry[index];
    }

    @Override
    public String[] removeCommandName(String[] tokenizedCommand) {
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }

    public static void test(){

    }
}
