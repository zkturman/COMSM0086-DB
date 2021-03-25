package DBObjects.DBCommands;

import DBException.*;
import DBObjects.*;

public class AlterDBCommand extends DBCommand {
    TableAttribute attributeToAlter;
    AlterType alterType;
    public AlterDBCommand(String[] alterArgs) throws DBException{
        if (isEmptyCommand(alterArgs)){
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
        //Configured in DBCommand parent class
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

    public void executeCommand() throws DBException {
        if (alterType == AlterType.ADD){
            tableForCommand.appendAttribute(attributeToAlter);
        }
        else if (alterType == AlterType.DROP){
            tableForCommand.removeAttribute(attributeToAlter);
        }
        else {
            throw new DBInvalidAlterType("Invalid alteration type was provided.");
        }
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
