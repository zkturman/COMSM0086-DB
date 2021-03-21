package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.ValueList;
import DBObjects.DBTable;

import java.util.Arrays;
import java.util.Locale;

public class InsertDBCommand extends DBCommand {

    DBTable tableToInsert;
    ValueList valuesToInsert;

    public InsertDBCommand(String[] insertArgs) throws DBException{
        super(insertArgs);
        if (!commandHasArguments(insertArgs)){
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
        setupTable(tableName);
        String valuesString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!valuesString.equals("VALUES")){
            throw new InvalidCommandArgumentException("Expected \"VALUES\" string in insert command.");
        }
        if (tokenizedCommand.length != currentToken){
           throw new InvalidCommandArgumentException("Value command did not have the correct structure.");
        }
        prepareValueList(listString);
    }

    public void setupTable(String tableName)throws DBException {
        if (tableName.length() == 0) {
            throw new InvalidCommandArgumentException("No table name provided.");
        }
        if (!isNameValid(tableName)) {
            throw new InvalidCommandArgumentException("Table name was invalid.");
        }
        if (workingDatabase == null){
            throw new NotUsingDBException("No working database has been selected.");
        }
        tableToInsert = new DBTable(tableName, workingDatabase);
    }

    public void prepareValueList(String valueList) throws DBException {
        valuesToInsert = new ValueList(valueList);
        if (!valuesToInsert.parseList()){
            throw new InvalidCommandArgumentException("Insert expects values to insert.");
        }
    }

    public void executeCommand() throws DBException {
        tableToInsert.insertRow(valuesToInsert.getValueList());
    }

    @Override
    public String[] splitCommand(String commandString) throws DBException {
        return commandString.split("\\s+");
    }

    @Override
    public String getNextToken(String[] tokenAry, int index) throws DBException {
        if (index > tokenAry.length){
            throw new InvalidCommandArgumentException("Insert command is incomplete.");
        }
        return tokenAry[index];
    }

    @Override
    public String[] removeCommandName(String[] tokenizedCommand) {
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }
}
