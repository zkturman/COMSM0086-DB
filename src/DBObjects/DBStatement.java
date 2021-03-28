/**
 * DBStatement class. Used to receive statements from the server. Passes errors and return messages
 * from commands to the server. Also does initial processing of commands to ensure the correct command
 * is interpreted.
 */

package DBObjects;

import DBException.*;
import DBObjects.DBCommands.DBCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBStatement {
    private DBDatabase workingDatabase;
    private String returnMessage;

    /**
     * Constructor for a DBStatement. Instantiates a new statement and holds any return messages.
     * @param workingDatabase Current working database for commands.
     */
    public DBStatement(DBDatabase workingDatabase){
        this.workingDatabase = workingDatabase;
    }

    /**
     * Returns the current working database. USE command can update this.
     * @return The working database object.
     */
    public DBDatabase getWorkingDatabase() {
        return workingDatabase;
    }

    /**
     * Returns any messages created from the database query.
     * @return Message generated from SQL query.
     */
    public String getReturnMessage() {
        return returnMessage;
    }

    /**
     * Performs a SQL query and generates parsing errors. Evaluates queries differently depending on
     * the initial command (i.e. the first token).
     * @param commandString SQL query to perform.
     * @throws DBException Thrown if an error is encounter when parsing a SQL query.
     */
    public void performStatement(String commandString) throws DBException {
        checkCommandSize(commandString);
        commandString = removeSemicolon(commandString);
        String[] commandToProcess = separateLists(commandString);
        String firstToken = getFirstToken(commandToProcess[0]);
        if (!DBCommand.isValidCommand(firstToken)){
            throw new DBInvalidCommandException("An invalid command was entered: " + firstToken);
        }
        DBCommand sqlDBCommand = DBCommand.generateCommand(firstToken, commandToProcess);
        sqlDBCommand.processCommand(workingDatabase);
        workingDatabase = sqlDBCommand.getWorkingDatabase();
        returnMessage = sqlDBCommand.getReturnMessage();
    }

    /**
     * Confirms the command string has content.
     * @param commandString String to check.
     * @throws DBException Thrown if the string is null or is zero length.
     */
    private void checkCommandSize(String commandString) throws DBException{
        if (commandString == null || commandString.length() == 0){
            throw new DBInvalidCommandException("Command was empty.");
        }
    }

    /**
     * Finds the first token in an incoming command to see if it contains a valid command type.
     * @param mainCommand SQL query to evaluate.
     * @return The first word in a strings without spaces.
     */
    private String getFirstToken(String mainCommand) throws DBException {
        Pattern tokenPattern = Pattern.compile("\\s*[a-zA-Z0-9]+(\\s+|(?=\\*))", Pattern.CASE_INSENSITIVE);
        Matcher tokenMatcher = tokenPattern.matcher(mainCommand);
        if (!tokenMatcher.find()){
            throw new DBInvalidCommandException("An incorrect command was attempted.");
        }
        String commandName = tokenMatcher.group();
        commandName = commandName.replaceAll("\\s", "");
        return commandName;
    }

    /**
     * Removes the terminating semicolon of a SQL statement.
     * @param statement SQL statement to be processed.
     * @return SQL statement without terminating semicolon.
     * @throws DBException If there is no terminating semicolon.
     */
    private String removeSemicolon(String statement) throws DBException {
        int newEnd = statement.length() - 1;
        if (statement.charAt(newEnd) != ';'){
            throw new DBNonTerminatingException("String did not end with a semicolon.");
        }
        return statement.substring(0, newEnd);
    }

    /**
     * Separates terminating parenthetical lists from a statement. Used for initial processing, but does not find all
     * lists in a statement. If it is not wrapped with parentheses, it is handled in specific command processing.
     * @param statement SQL statement to be evaluated
     * @return Array of strings of size two where the 0th index is the main SQL
     * command and 1st index is a terminating parenthetical list.
     */
    private String[] separateLists(String statement){
        return statement.split("(?=\\()", 2);
    }

    /**
     * Used to test functionality of DBStatement
     */
    public static void test(){
        String statement1 = "create new table test1;", statement2 = "insert into test1 values ('ab', 1234);";
        DBStatement test1 = new DBStatement(null);
        try {
            assert test1.removeSemicolon(statement1).equals("create new table test1");
            statement1 = test1.removeSemicolon(statement1);
            assert test1.removeSemicolon(statement2).equals("insert into test1 values ('ab', 1234)");
            statement2 = test1.removeSemicolon(statement2);
            assert test1.separateLists(statement1).length == 1;
            assert test1.separateLists(statement2).length == 2;
            assert test1.getFirstToken(statement1).equals("create");
            assert test1.getFirstToken(statement2).equals("insert");
        }
        catch (DBException de){
            System.out.println("DBStatement threw an error during testing.");
        }
        System.out.println("DBStatement passed.");
    }

}
