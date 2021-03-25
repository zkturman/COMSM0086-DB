package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.ValueList;

public class InsertDBCommand extends DBCommand {

    ValueList valuesToInsert;

    public InsertDBCommand(String[] insertArgs) throws DBException{
        if (isEmptyCommand(insertArgs)){
            throw new InvalidCommandArgumentException("Insert command has no arguments.");
        }
        commandString = insertArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        if (insertArgs.length == 1){
            throw new InvalidCommandArgumentException("Insert command expects a list of values.");
        }
        listString = insertArgs[1];
        if (insertArgs.length > 2){
            throw new InvalidCommandArgumentException("Insert argument did not have the expected structure.");
        }
    }

    public void prepareCommand() throws DBException {
        int currentToken = 0;
        String intoString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!intoString.equals("INTO")){
            throw new InvalidCommandArgumentException("Expected \"INTO\" string in insert command.");
        }
        String tableName = getNextToken(tokenizedCommand, currentToken++);
        //Configured in DBCommand parent class
        setupTable(tableName);
        String valuesString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!valuesString.equals("VALUES")){
            throw new InvalidCommandArgumentException("Expected \"VALUES\" string in insert command.");
        }
        if (tokenizedCommand.length != currentToken){
           throw new InvalidCommandArgumentException("Insert command did not have the correct structure.");
        }
        prepareValueList(listString);
    }

    public void prepareValueList(String valueList) throws DBException {
        valuesToInsert = new ValueList(valueList);
        if (!valuesToInsert.parseList()){
            throw new InvalidCommandArgumentException("Insert expects values to insert.");
        }
    }

    public void executeCommand() throws DBException {
        tableForCommand.insertRow(valuesToInsert.getValueList());
    }
}
