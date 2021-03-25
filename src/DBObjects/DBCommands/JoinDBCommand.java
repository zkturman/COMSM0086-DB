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
        if (isEmptyCommand(joinArgs)){
            throw new InvalidCommandArgumentException("Join command has no arguments.");
        }
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
        if (!tableAndString.equals("AND")){
            throw new InvalidCommandArgumentException("Expected \"AND\" string between tables in join command.");
        }
        String secondaryTable = getNextToken(tokenizedCommand, currentToken++);
        setupTable(secondaryTable, DBJoinTableType.SECONDARY);
        String onString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!onString.equals("ON")){
            throw new InvalidCommandArgumentException("Expected \"ON\" string in join command.");
        }
        String primaryAttributeString = getNextToken(tokenizedCommand, currentToken++);
        if (!isNameValid(primaryAttributeString)){
            throw new InvalidCommandArgumentException("Primary attribute name was not valid.");
        }
        primaryAttribute = new TableAttribute(primaryAttributeString);
        tableForCommand.setJoinAttribute(primaryAttribute);
        String attributeAndString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!attributeAndString.equals("AND")){
            throw new InvalidCommandArgumentException("Expected \"AND\" string between attributes in join command.");
        }
        String secondaryAttributeString = getNextToken(tokenizedCommand,currentToken++);
        if (!isNameValid(secondaryAttributeString)){
            throw new InvalidCommandArgumentException("Secondary attribute name was not valid.");
        }
        secondaryAttribute = new TableAttribute(secondaryAttributeString);
        tableToJoin.setJoinAttribute(secondaryAttribute);
        if (currentToken != tokenizedCommand.length){
            throw new InvalidCommandArgumentException("Join command was not the expected number of arguments.");
        }
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
    public String[] removeCommandName(String[] tokenizedCommand) {
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }
}
