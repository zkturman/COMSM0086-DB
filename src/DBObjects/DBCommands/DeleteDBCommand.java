package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.CommandCondition;

/**
 * DeleteDBCommand is responsible for deleting rows from a table.
 */
public class DeleteDBCommand extends DBCommand {

    private CommandCondition deleteConditions;

    /**
     * Constructor for a DeleteDBCommand. Tokenizes the command string and looks for
     * a parenthetical condition list. Non-parenthetical conditions are handled elsewhere.
     * @param deleteArgs Pre-processed command string.
     * @throws DBException Thrown if preprocessed string contains more than two elements.
     */
    public DeleteDBCommand(String[] deleteArgs) throws DBException {
        isEmptyCommand(deleteArgs);
        commandString = deleteArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        if (deleteArgs.length == 2){
            listString = deleteArgs[1];
        }
        if (deleteArgs.length > 2){
            throw new InvalidCommandArgumentException("Delete command has the incorrect form.");
        }
    }

    /**
     * Parses and sets up objects to handle row deletion in a table.
     * @throws DBException Thrown if the command is not formatted as expected.
     */
    @Override
    public void prepareCommand() throws DBException {
        int currentToken = 0;

        String fromString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(fromString, "FROM");

        String tableName = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        setupTable(tableName);

        String whereString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(whereString, "WHERE");
        if (currentToken != tokenizedCommand.length && listString == null){
            listString = commandString.split("(?i)\\s+where\\s+")[1];
        }

        prepareConditions();
    }

    /**
     * Evaluates conditions for the command. Conditions are required.
     * @throws DBException Thrown if conditions don't exist or are incorrectly formatted.
     */
    public void prepareConditions() throws DBException {
        if (listString == null){
            throw new InvalidCommandArgumentException("Delete command expects condition.");
        }
        deleteConditions = new CommandCondition(listString);
        deleteConditions.parseList();
    }

    /**
     * Deletes specified rows from a table.
     * @throws DBException Thrown if conditions are incorrectly formatted or table writing fails.
     */
    @Override
    public void executeCommand() throws DBException {
        deleteConditions.executeConditions(tableForCommand);
        tableForCommand.deleteRows();
    }
}
