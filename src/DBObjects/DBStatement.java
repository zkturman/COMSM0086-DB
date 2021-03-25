package DBObjects;

import DBException.*;
import DBObjects.DBCommands.DBCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBStatement {
    private DBDatabase workingDatabase = null;
    private String returnMessage;
    private String[] commandToProcess;

    public DBDatabase getWorkingDatabase() {
        return workingDatabase;
    }

    public void setWorkingDatabase(DBDatabase workingDatabase) {
        this.workingDatabase = workingDatabase;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public DBStatement(DBDatabase workingDatabase){
        this.workingDatabase = workingDatabase;
    }

    public void performStatement(String commandString) throws DBException {
        commandString = removeSemicolon(commandString);
        commandToProcess = separateLists(commandString);
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
     * Finds the first token in an incoming command to see if it contains a valid command type.
     * @param mainCommand SQL query to evaluate.
     * @return The first word in a strings without spaces.
     */
    private String getFirstToken(String mainCommand){
        Pattern tokenPattern = Pattern.compile("\\s*[a-zA-Z0-9]+(\\s+|\\*)", Pattern.CASE_INSENSITIVE);
        Matcher tokenMatcher = tokenPattern.matcher(mainCommand);
        tokenMatcher.find();
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
        if (statement.charAt(statement.length() - 1) != ';'){
            throw new DBNonTerminatingException("String did not end with a semicolon.");
        }
        return statement.substring(0, statement.length() - 1);
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

        }
        System.out.println("DBStatement passed.");
    }

}
