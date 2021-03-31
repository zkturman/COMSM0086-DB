package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.ValueList;

/**
 * InsertDBCommand class handles inserting new rows into database tables.
 */
public class InsertDBCommand extends DBCommand {

    private ValueList valuesToInsert;

    /**
     * Constructor for the InsertDBCommand. Gets list and command strings from a pre-
     * processed command. Insert commands but has a parenthetical list.
     * @param insertArgs Pre-processed command from DBStatement.
     * @throws DBException Thrown if the pre-processed command doesn't have two elements.
     */
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

    /**
     *  Parses and prepares list and table objects for the insert command.
     * @throws DBException Thrown if the formatting of the tokenized command is incorrect.
     */
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

    /**
     * Parses a list of values to be inserted into a table. Also processes the list
     * so it can be easily added to the table.
     * @param valueList Comma-delimited string of values to add to the table.
     * @throws DBException Thrown if the list is empty
     */
    protected void prepareValueList(String valueList) throws DBException {
        valuesToInsert = new ValueList(valueList);
        if (!valuesToInsert.parseList()){
            throw new InvalidCommandArgumentException("Insert expects values to insert.");
        }
    }

    /**
     * Inserts a new row into a table.
     * @throws DBException Thrown if the table row contains a different number of
     * values than the number of attributes in the table.
     */
    protected void executeCommand() throws DBException {
        tableForCommand.insertTableRow(valuesToInsert.getValueList());
    }
}
