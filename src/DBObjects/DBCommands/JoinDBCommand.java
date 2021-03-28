package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBTable;
import DBObjects.TableAttribute;

import java.util.Arrays;

public class JoinDBCommand extends DBCommand {

    DBTable tableToJoin;
    TableAttribute primaryAttribute;
    TableAttribute secondaryAttribute;

    protected JoinDBCommand(String[] joinArgs) throws DBException {
        isEmptyCommand(joinArgs);
        if (joinArgs.length != 1){
            throw new InvalidCommandArgumentException("Join command has the incorrect form.");
        }
        commandString = joinArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
    }

    @Override
    public void prepareCommand() throws DBException {
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
        primaryAttribute = new TableAttribute(primaryAttributeString);
        tableForCommand.setJoinAttribute(primaryAttribute);

        String attributeAndString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(attributeAndString, "AND");

        String secondaryAttributeString = getNextToken(tokenizedCommand,currentToken++);
        if (!isNameValid(secondaryAttributeString)){
            throw new InvalidCommandArgumentException("Secondary attribute name was not valid.");
        }
        secondaryAttribute = new TableAttribute(secondaryAttributeString);
        tableToJoin.setJoinAttribute(secondaryAttribute);

        checkCommandEnded(currentToken);
    }

    public void setupTable(String tableName, DBJoinTableType type) throws DBException {
        if (type == DBJoinTableType.PRIMARY){
            super.setupTable(tableName);
        }
        else {
            if (tableName.length() == 0) {
                throw new InvalidCommandArgumentException("No table name provided.");
            }
            if (!isNameValid(tableName)) {
                throw new InvalidCommandArgumentException("Table name was invalid.");
            }
            if (workingDatabase == null) {
                throw new NotUsingDBException("No working database has been selected.");
            }
            tableToJoin = new DBTable(tableName, workingDatabase);
            tableToJoin.loadTableFile();
        }
    }

    @Override
    public void executeCommand() throws DBException {
        DBTable jointTable = DBTable.joinTables(tableForCommand, tableToJoin);
        returnMessage = jointTable.printTable();
    }

    @Override
    public String[] splitCommand(String commandString) {
        return commandString.split("\\s+");
    }

    @Override
    /**
     *
     */
    public String[] removeCommandName(String[] tokenizedCommand) {
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }
}
