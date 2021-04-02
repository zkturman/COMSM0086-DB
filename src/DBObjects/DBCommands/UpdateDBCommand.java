package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UpdateDBCommand is responsible for updating rows in a table with new values.
 */
public class UpdateDBCommand extends DBCommand {

    private String nameValueString;
    private NameValueList updateNameValues;
    private CommandCondition updateConditions;

    /**
     * Constructor for an UpdateDBCommand. Tokenizes the command string and
     * configures parenthetical conditions. Non-parenthetical conditions
     * are handled separately during parsing.
     * @param updateArgs Pre-processed command string.
     * @throws DBException Thrown if preprocessed string contains more than two elements.
     */
    protected UpdateDBCommand(String[] updateArgs) throws DBException{
        isEmptyCommand(updateArgs);
        commandString = updateArgs[0];
        //Finds and removes the NameValueList
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        if (updateArgs.length == 2){
            listString = updateArgs[1];
        }
        if (updateArgs.length > 2){
            throw new InvalidCommandArgumentException("Update command has the incorrect form.");
        }
    }

    /**
     * Parses an UPDATE command.
     * @throws DBException Throws an error if the tokens do not match what is expected.
     */
    @Override
    protected void prepareCommand() throws DBException {
        int currentToken = 0;

        String tableName = getNextToken(tokenizedCommand, currentToken++);
        setupTable(tableName);

        String setString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(setString, "SET");

        prepareNameValues();

        String whereString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(whereString, "WHERE");
        //prepareConditions handles null and inappropriate lists
        if (currentToken != tokenizedCommand.length && listString == null){
            listString = commandString.split("(?i)\\s+where\\s+")[1];
        }

        prepareConditions();
    }

    /**
     * Instantiates a NameValueList object and gets corresponding attributes and values.
     * @throws DBException Thrown if values or attributes are incorrectly formatted.
     */
    protected void prepareNameValues() throws DBException {
        updateNameValues = new NameValueList(nameValueString);
        updateNameValues.processList();
    }

    /**
     * Handles conditions of the update command. Conditions are required for UPDATE.
     * @throws DBException Thrown conditions are setup incorrectly or there are no conditions.
     */
    protected void prepareConditions() throws DBException {
        if (listString == null){
            throw new InvalidCommandArgumentException("Update command expects condition.");
        }
        updateConditions = new CommandCondition(listString);
        updateConditions.processList();
    }

    /**
     * Updates rows in a table.
     * @throws DBException Thrown if table writing or reading fails.
     */
    @Override
    protected void executeCommand() throws DBException {
        updateConditions.executeConditions(tableForCommand);
        tableForCommand.updateTable(updateNameValues);
    }

    /**
     * Splits a command string and tokenizes it. This override also removes
     * a name value list embedded within the command.
     * @param commandString String to split.
     * @return Returns a tokenized array excluding embedded name-value pairs.
     */
    @Override
    protected String[] splitCommand(String commandString) throws DBException {
        //get the NameValueList
        Pattern nameValuePattern = Pattern.compile("(?<=\\sset)\\s+.*\\s+(?=where\\s)", Pattern.CASE_INSENSITIVE);
        Matcher nameValueMatcher = nameValuePattern.matcher(commandString);
        if (!nameValueMatcher.find()){
            throw new InvalidCommandArgumentException("Name value list is required for update command.");
        }
        nameValueString = nameValueMatcher.group();
        commandString = commandString.replaceFirst(nameValueMatcher.pattern().pattern(), " ");
        return commandString.split("\\s+");
    }
}
