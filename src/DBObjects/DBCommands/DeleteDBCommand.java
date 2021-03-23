package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.CommandCondition;
import DBObjects.DBTable;

import java.util.ArrayList;
import java.util.Arrays;

public class DeleteDBCommand extends DBCommand {

    DBTable tableToDelete;
    CommandCondition deleteConditions;

    public DeleteDBCommand(String[] deleteArgs) throws DBException {
        super(deleteArgs);
        if (!commandHasArguments(deleteArgs)){
            throw new InvalidCommandArgumentException("Delete command has no arguments.");
        }
        if (deleteArgs.length != 2){
            throw new InvalidCommandArgumentException("Delete command has the incorrect form.");
        }
        commandString = deleteArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
    }

    @Override
    public void prepareCommand() throws DBException {
        int currentToken = 0;
        String fromString;
        String tableName;
        String whereString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!whereString.equals("WHERE")){
            throw new InvalidCommandArgumentException("Expected \"WHERE\" string in select command.");
        }
        prepareConditions();
    }

    public void prepareConditions() throws DBException {
        deleteConditions = new CommandCondition(listString);
        deleteConditions.parseList();
    }

    @Override
    public void executeCommand() throws DBException {

    }

    @Override
    public String[] splitCommand(String commandString) throws DBException {
        return commandString.split("\\s+");
    }

    @Override
    public String getNextToken(String[] tokenAry, int index) throws DBException {
        if (index >= tokenAry.length){
            throw new InvalidCommandArgumentException("Delete command has the incorrect number of arguments");
        }
        return tokenAry[index];
    }

    @Override
    public String[] removeCommandName(String[] tokenizedCommand) {
       return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }
}
