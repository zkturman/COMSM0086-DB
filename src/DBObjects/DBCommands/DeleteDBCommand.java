package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.CommandCondition;
import DBObjects.DBTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class DeleteDBCommand extends DBCommand {

    CommandCondition deleteConditions;

    public DeleteDBCommand(String[] deleteArgs) throws DBException {
        super(deleteArgs);
        if (!commandHasArguments(deleteArgs)){
            throw new InvalidCommandArgumentException("Delete command has no arguments.");
        }
        commandString = deleteArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        if (deleteArgs.length ==2){
            listString = deleteArgs[1];
        }
        if (deleteArgs.length > 2){
            throw new InvalidCommandArgumentException("Delete command has the incorrect form.");
        }
    }

    @Override
    public void prepareCommand() throws DBException {
        int currentToken = 0;
        String fromString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!fromString.equals("FROM")){
            throw new InvalidCommandArgumentException("Expected \"FROM\" string in delete command.");
        }
        String tableName = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        //Configured in DBCommand parent class
        setupTable(tableName);
        String whereString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!whereString.equals("WHERE")){
            throw new InvalidCommandArgumentException("Expected \"WHERE\" string in select command.");
        }
        if (currentToken != tokenizedCommand.length && listString == null){
            listString = commandString.split("(?i)\\s+where\\s+")[1];
            tokenizedCommand = Arrays.copyOfRange(tokenizedCommand, 0, currentToken);
        }
        if (currentToken == tokenizedCommand.length && listString != null) {
            prepareConditions();
        }
        else{
            throw new InvalidCommandArgumentException("Update conditions were of the incorrect form.");
        }
        prepareConditions();
    }

    public void prepareConditions() throws DBException {
        deleteConditions = new CommandCondition(listString);
        deleteConditions.parseList();
    }

    @Override
    public void executeCommand() throws DBException {
        deleteConditions.executeConditions(tableForCommand);
        tableForCommand.deleteRows();
    }

    @Override
    public String[] removeCommandName(String[] tokenizedCommand) {
       return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }
}
