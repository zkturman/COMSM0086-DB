package DBObjects.DBCommands;

import DBException.*;
import DBObjects.*;

/**
 * JoinDBCommand performs an inner join on two tables. A return string of all rows
 * where the attribute from one table's values matches the other will be set.
 * The return string can be printed to represent the table.
 */
public class JoinDBCommand extends DBCommand {

    private DBTable tableToJoin;

    /**
     * Constructor for the JoinDBCommand. Takes a pre-processed string
     * of arguments. There are no parenthetical lists for a JOIN command.
     * @param joinArgs Pre-processed string for the command.
     * @throws DBException Thrown if the command is empty or if parenthetical
     * lists terminated the initial command.
     */
    protected JoinDBCommand(String[] joinArgs) throws DBException {
        isEmptyCommand(joinArgs);
        if (joinArgs.length != 1){
            throw new InvalidCommandArgumentException("Join command has the incorrect form.");
        }
        commandString = joinArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
    }

    /**
     * Parses and initialises objects for a JOIN command.
     * @throws DBException Thrown if the tokenized command is set up incorrectly.
     */
    @Override
    protected void prepareCommand() throws DBException {
        int currentToken = 0;

        String primaryTable = getNextToken(tokenizedCommand, currentToken++);
        setupTable(primaryTable, DBJoinTableType.PRIMARY);

        String tableAndString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(tableAndString, "AND");

        String secondaryTable = getNextToken(tokenizedCommand, currentToken++);
        setupTable(secondaryTable, DBJoinTableType.SECONDARY);

        String onString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(onString, "ON");

        String primaryAttributeString = getNextToken(tokenizedCommand, currentToken++);
        if (!isNameValid(primaryAttributeString)){
            throw new InvalidCommandArgumentException("Primary attribute name was not valid.");
        }
        tableForCommand.setJoinAttribute(new TableAttribute(primaryAttributeString));

        String attributeAndString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(attributeAndString, "AND");

        String secondaryAttributeString = getNextToken(tokenizedCommand,currentToken++);
        if (!isNameValid(secondaryAttributeString)){
            throw new InvalidCommandArgumentException("Secondary attribute name was not valid.");
        }
        tableToJoin.setJoinAttribute(new TableAttribute(secondaryAttributeString));

        checkCommandEnded(currentToken);
    }

    /**
     * Sets up the primary and secondary tables for joining depending on
     * the join table type.
     * @param tableName Table to initialise.
     * @param type Primary or Secondary table.
     * @throws DBException Thrown if the working database is null.
     */
    protected void setupTable(String tableName, DBJoinTableType type) throws DBException {
        if (type == DBJoinTableType.PRIMARY){
            super.setupTable(tableName);
        }
        else {
            setupSecondaryTable(tableName);
        }
    }

    /**
     * Sets up the secondary table for joining. functionally the same as
     * setupTable aside from the field it sets.
     * @param tableName Table to create.
     * @throws DBException Thrown if workingDatabase is null.
     */
    private void setupSecondaryTable(String tableName) throws DBException{
        if (tableName.length() == 0) {
            throw new InvalidCommandArgumentException("No table name provided.");
        }
        if (!isNameValid(tableName)) {
            throw new InvalidCommandArgumentException("Table name was invalid.");
        }

        tableToJoin = new DBTable(tableName, workingDatabase);
        tableToJoin.loadTableFile();
    }

    /**
     * Joins two tables on the selected attributes. Creates a return string that
     * will be used as the return message of the command.
     * message to be printed.
     * @throws DBException Thrown if table files cannot be loaded or attributes do not exist.
     */
    @Override
    protected void executeCommand() throws DBException {
        DBTable jointTable = DBTable.joinTables(tableForCommand, tableToJoin);
        returnMessage = jointTable.printTable();
    }
}
