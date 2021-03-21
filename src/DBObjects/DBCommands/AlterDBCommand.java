package DBObjects.DBCommands;

import DBException.*;
import DBObjects.*;

import java.util.Arrays;

public class AlterDBCommand extends DBCommand {
    DBTable tableToAlter;
    TableAttribute attributeToAlter;
    AlterType alterType;
    public AlterDBCommand(String[] alterArgs) throws DBException{
        super(alterArgs);
        if (!commandHasArguments(alterArgs)){
            throw new InvalidCommandArgumentException("Alter command has no arguments");
        }
        commandString = alterArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        if (alterArgs.length > 1) {
            throw new InvalidCommandArgumentException("Alter command has unexpected structure.");
        }
    }

    public void prepareCommand() throws DBException {
        int currentToken = 0;
        String tableString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!tableString.equals("TABLE")){
            throw new InvalidCommandArgumentException("Expected \"TABLE\" string in alter command.");
        }
        String tableName = getNextToken(tokenizedCommand, currentToken++);
        setupTable(tableName);
        String alterationType = getNextToken(tokenizedCommand, currentToken++);
        if (!convertStringToAlterType(alterationType)){
            throw new InvalidCommandArgumentException("Invalid alteration type provided.");
        }
        String attributeName = getNextToken(tokenizedCommand, currentToken++);
        setupAttribute(attributeName);
    }

    private void setupAttribute(String attributeName) throws DBException {
        if (!isNameValid(attributeName)){
            throw new InvalidCommandArgumentException("Attribute name is invalid.");
        }
        attributeToAlter = new TableAttribute(attributeName);
    }

    public void setupTable(String tableName) throws DBException {
        if (tableName.length() == 0) {
            throw new InvalidCommandArgumentException("No table name given.");
        }
        if (!isNameValid(tableName)) {
            throw new InvalidCommandArgumentException("Table name contains special characters.");
        }
        if (workingDatabase == null){
            throw new NotUsingDBException("No working database has been selected.");
        }
        tableToAlter = new DBTable(tableName, workingDatabase);
    }

    public void executeCommand() throws DBException {
        if (alterType == AlterType.ADD){
            tableToAlter.appendAttribute(attributeToAlter);
        }
        else if (alterType == AlterType.DROP){
            tableToAlter.removeAttribute(attributeToAlter);
        }
        else {
            throw new DBInvalidAlterType("Invalid alteration type was provided.");
        }
    }

    @Override
    public String[] splitCommand(String commandString) throws DBException {
        return commandString.split("\\s+");
    }

    @Override
    public String getNextToken(String[] tokenAry, int index) throws DBException {
        if (index > tokenAry.length){
            throw new InvalidCommandArgumentException("Alter command was incomplete.");
        }
        return tokenAry[index];
    }

    @Override
    public String[] removeCommandName(String[] tokenizedCommand) {
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }

    private boolean convertStringToAlterType(String alterString){
        switch(alterString.toUpperCase()){
            case "ADD":
                alterType = AlterType.ADD;
                return true;
            case "DROP":
                alterType = AlterType.DROP;
                return true;
            default:
                return false;
        }
    }
}
