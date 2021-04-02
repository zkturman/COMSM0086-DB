package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SelectDBCommand handles generating a return string that represents the contents
 * of a table. The string can contain a combination of rows and attributes depending
 * on the command's form.
 */
public class SelectDBCommand extends DBCommand{

    private String attributeString;
    private WildAttributeList selectAttributes;
    private CommandCondition selectConditions;

    /**
     * Constructor for a SelectDBCommand that takes a pre-processed string.
     * The arguments contain optional parenthetical conditions. Non-parenthetical
     * conditions and attributes are handled separately.
     * @param selectArgs Pre-processed command string.
     * @throws DBException Thrown if the command is empty
     */
    protected SelectDBCommand(String[] selectArgs) throws DBException{
        isEmptyCommand(selectArgs);
        if (selectArgs.length > 2){
            throw new InvalidCommandArgumentException("Select command has the incorrect form.");
        }
        commandString = selectArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        if (selectArgs.length == 2){
            listString = selectArgs[1];
        }
    }

    /**
     * Parse and initialises objects for a SELECT command. Handles
     * the WildAttributeList and Conditions of the SELECT command.
     * @throws DBException Thrown if the tokenized command is incorrectly formatted.
     */
    @Override
    protected void prepareCommand() throws DBException {
        int currentToken = 0;
        prepareAttributes();
        String fromString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(fromString, "FROM");
        String tableName = getNextToken(tokenizedCommand, currentToken++);
        setupTable(tableName);
        if (currentToken != tokenizedCommand.length){
            String whereString = getNextToken(tokenizedCommand, currentToken).toUpperCase();
            compareStrings(whereString, "WHERE");
            if (listString == null){
                listString = commandString.split("(?i)\\s+where\\s+")[1];
            }
            prepareConditions();
        }
    }

    /**
     * Parses and configures objects to handles the attributes of
     * a SELECT command.
     * @throws DBException Thrown if the list is incorrectly formatted.
     */
    protected void prepareAttributes() throws DBException {
        selectAttributes = new WildAttributeList(attributeString);
        selectAttributes.processList();
    }

    /**
     * Prepares conditions for a SELECT command. Conditions are
     * optional for this command.
     * @throws DBException Thrown if conditions are incorrectly formatted.
     */
    protected void prepareConditions() throws DBException {
        selectConditions = new CommandCondition(listString);
        selectConditions.processList();
    }

    /**
     * Creates a return string to print table data. The return string can be
     * for all attributes in a table or a custom selection.
     * @throws DBException Thrown if conditions for a
     */
    @Override
    protected void executeCommand() throws DBException {
        if (selectConditions != null){
            selectConditions.executeConditions(tableForCommand);
        }
        if (selectAttributes.getAllAttributes()){
            returnMessage = tableForCommand.printTable();
        }
        else{
            returnMessage = tableForCommand.printTable(selectAttributes.getAttributeList());
        }
    }

    /**
     * In addition to splitting the command on white spaces, the string also
     * finds the attribute list between SELECT and FROM terms.
     * @param commandString String to split.
     * @return Tokenized command, excluding the attribute list.
     * @throws DBException Thrown if there is no attribute list in the command.
     */
    @Override
    protected String[] splitCommand(String commandString) throws DBException {
        //get attribute list:
        Pattern attributePattern = Pattern.compile("(?<=select).*(?=from\\s+)", Pattern.CASE_INSENSITIVE);
        Matcher attributeMatcher = attributePattern.matcher(commandString);
        if (!attributeMatcher.find()){
            throw new InvalidCommandArgumentException("Could not find attribute list in select statement.");
        }
        attributeString = attributeMatcher.group();
        commandString = attributeMatcher.replaceFirst(" ");
        return commandString.split("\\s+");
    }

    public static void test(){
        String[] test1 = new String[1];
        test1[0] = "select cool, from from table test1";
        try {
            SelectDBCommand selectTest = new SelectDBCommand(test1);
            selectTest.splitCommand(test1[0]);
            assert selectTest.attributeString.equals(" cool, from ");
            selectTest.splitCommand("select*from table test1");
            assert selectTest.attributeString.equals("*");
            selectTest.splitCommand("select a, b, c, d from table test1");
            assert selectTest.attributeString.equals(" a, b, c, d ");
        }
        catch (DBException dbe){
            System.out.println("SelectDBCommand tests threw an error.");
        }
        System.out.println("SelectDBCommand passed.");
    }
}
