package DBObjects.DBCommands;

import DBException.*;
import DBObjects.*;

public class AlterDBCommand extends DBCommand {
    private TableAttribute attributeToAlter;
    private AlterType alterType;

    public AlterDBCommand(String[] alterArgs) throws DBException{
        isEmptyCommand(alterArgs);
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
        compareStrings(tableString, "TABLE");
        String tableName = getNextToken(tokenizedCommand, currentToken++);
        setupTable(tableName);
        String alterationType = getNextToken(tokenizedCommand, currentToken++);
        if (!convertStringToAlterType(alterationType)){
            throw new InvalidCommandArgumentException("Invalid alteration type provided.");
        }
        String attributeName = getNextToken(tokenizedCommand, currentToken++);
        setupAttribute(attributeName);
        checkCommandEnded(currentToken);
    }

    private void setupAttribute(String attributeName) throws DBException {
        if (!isNameValid(attributeName)){
            throw new InvalidCommandArgumentException("Attribute name is invalid.");
        }
        attributeToAlter = new TableAttribute(attributeName);
    }

    public void executeCommand() throws DBException {
        switch(alterType){
            case ADD:
                tableForCommand.appendAttribute(attributeToAlter);
                break;
            case DROP:
                tableForCommand.removeAttribute(attributeToAlter);
                break;
            default:
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
