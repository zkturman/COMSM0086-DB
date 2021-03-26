package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.ValueList;

public class InsertDBCommand extends DBCommand {

    ValueList valuesToInsert;

    public InsertDBCommand(String[] insertArgs) throws DBException{
        isEmptyCommand(insertArgs);
        if (insertArgs.length != 2){
            throw new InvalidCommandArgumentException("Insert argument did not have the expected structure.");
        }
        commandString = insertArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        listString = insertArgs[1];
    }

    public void prepareCommand() throws DBException {
        int currentToken = 0;
        String intoString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(intoString, "INTO");
        String tableName = getNextToken(tokenizedCommand, currentToken++);
        setupTable(tableName);
        String valuesString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(valuesString, "VALUES");
        checkCommandEnded(currentToken);
        prepareValueList(listString);
    }

    public void prepareValueList(String valueList) throws DBException {
        valuesToInsert = new ValueList(valueList);
        if (!valuesToInsert.parseList()){
            throw new InvalidCommandArgumentException("Insert expects values to insert.");
        }
    }

    public void executeCommand() throws DBException {
        tableForCommand.insertTableRow(valuesToInsert.getValueList());
    }


}
