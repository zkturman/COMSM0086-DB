package DBObjects.DBCommands;

import DBObjects.*;
import DBException.*;
import java.util.Arrays;

/**
 * DBCommand parent class. Contains common functionality for all database commands and
 * contains abstract functions that must be implemented by other commands. This object
 * is the only direct class accessed by the DBStatement class.
 */
public abstract class DBCommand extends DBObject {
    protected String commandString;
    protected String[] tokenizedCommand;
    protected String listString;
    protected StructureType structureType;
    protected DBDatabase workingDatabase;
    protected String returnMessage;
    protected DBTable tableForCommand;


    /**
     * Default constructor for a DBCommand. Used by child classes.
     */
    protected  DBCommand(){}

    /**
     * Gets the return message from a command.
     * @return Returns any return messags from a command.
     */
    public String getReturnMessage() {
        return returnMessage;
    }

    /**
     * Gets the current working database for the command.
     * @return Returns the current database.
     */
    public DBDatabase getWorkingDatabase() {
        return workingDatabase;
    }

    /**
     * Determines if a command name is a accepted command name.
     * @param commandName Potential name of a command.
     * @return Returns true if the command name is valid.
     */
    public static boolean isValidCommand(String commandName){
        String commandType = commandName.toUpperCase();
        switch (commandType){
            case "CREATE":
            case "USE":
            case "DROP":
            case "ALTER":
            case "INSERT":
            case "SELECT":
            case "UPDATE":
            case "DELETE":
            case "JOIN":
                return true;
            default:
                return false;
        }
    }

    /**
     * Generates a command based on the command name.
     * @param commandName Name of the command to be generated.
     * @param commandArgs Tokenized command to be used by the command.
     * @return Returns a DBCommand object to be executed.
     * @throws DBException Thrown if the command name doesn't match an existing command.
     */
    public static DBCommand generateCommand(String commandName, String[] commandArgs) throws DBException{
        commandName = commandName.toUpperCase();
        switch (commandName) {
            case "CREATE":
                return new CreateDBCommand(commandArgs);
            case "USE":
                return new UseDBCommand(commandArgs);
            case "DROP":
                return new DropDBCommand(commandArgs);
            case "ALTER":
                return new AlterDBCommand(commandArgs);
            case "INSERT":
                return new InsertDBCommand(commandArgs);
            case "SELECT":
                return new SelectDBCommand(commandArgs);
            case "UPDATE":
                return new UpdateDBCommand(commandArgs);
            case "DELETE":
                return new DeleteDBCommand(commandArgs);
            case "JOIN":
                return new JoinDBCommand(commandArgs);
            default:
                throw new DBInvalidCommandException("An invalid command was entered.");
        }
    }

    /**
     * Processes the command with the current database.
     * @param currentDB The database for which the command will be executed.
     * @throws DBException Thrown if an error occurs when processing the command.
     */
    public void processCommand(DBDatabase currentDB) throws DBException {
        workingDatabase = currentDB;
        prepareCommand();
        executeCommand();
    }

    /**
     * Prepares the command for execution by parsing and building the relevant database objects.
     * @throws DBException Thrown if parsing or object building fails.
     */
    protected abstract void prepareCommand() throws DBException;

    /**
     * Executes the command. Behaviour is different for each command.
     * @throws DBException Thrown if execution fails.
     */
    protected abstract void executeCommand() throws DBException;

    /**
     * Determines and updates the structure type of a command based on the type specified in the query.
     * @param specifiedType Structure specified in the database query.
     * @param currentDB The current working database. Working database is required if TABLE is specified.
     * @throws DBException Thrown if an invalid structure is specified or table is specified
     * and no working database is specified.
     */
    protected void determineStructureType(String specifiedType, DBDatabase currentDB) throws DBException {
        specifiedType = specifiedType.toUpperCase();
        switch (specifiedType){
            case "TABLE":
                checkWorkingDB(currentDB);
                structureType = StructureType.TABLE;
                return;
            case "DATABASE":
                structureType = StructureType.DATABASE;
                return;
            default:
                throw new InvalidCommandArgumentException("No valid structure was selected.");
        }
    }

    /**
     * Determines if a tokenized command has any strings in it.
     * @param commandAry Tokenized command.
     * @throws DBException Thrown if there are no values in the array.
     */
    protected void isEmptyCommand(String[] commandAry) throws DBException{
        if (commandAry.length < 1) {
            throw new InvalidCommandArgumentException("Command has no arguments.");
        }
    }

    /**
     * Splits a command string on spaces. This will break the string into tokens
     * on any number of spaces.
     * @param commandString String to split.
     * @return Tokenized command.
     * @throws DBException Thrown in override methods.
     */
    protected String[] splitCommand(String commandString) throws DBException {
        return commandString.split("\\s+");
    }

    /**
     * Removes the command name from a command. The command name is always the first token in the string.
     * @param tokenizedCommand All tokens in a command.
     * @return Following command tokens.
     */
    protected String[] removeCommandName(String[] tokenizedCommand) {
        int startIndex = 1;
        if (tokenizedCommand[0].equals("")){
            startIndex = 2;
        }
        return Arrays.copyOfRange(tokenizedCommand, startIndex, tokenizedCommand.length);
    }

    /**
     * Gets the next token from a tokenized command.
     * @param tokenAry Tokenized command.
     * @param index Index of the tokenized command to return.
     * @return Returns a string of the next tokenized command.
     * @throws DBException Thrown if the given index is greater than or equal to the number of tokens.
     */
    protected String getNextToken(String[] tokenAry, int index) throws DBException{
        if (index >= tokenAry.length){
            throw new InvalidCommandArgumentException("Command has an inappropriate number of arguments.");
        }
        return tokenAry[index];
    }

    /**
     * Determines if the end of a tokenized command has been reached.
     * @param tokenIndex Index of tokenized command to check.
     * @throws DBException Thrown if the end of the command hasn't been reached.
     */
    protected void checkCommandEnded(int tokenIndex) throws DBException {
        if (tokenIndex != tokenizedCommand.length){
            throw new InvalidCommandArgumentException("Command did not have the correct structure.");

        }
    }

    /**
     * Determines if a given string matches an expected key word in a command.
     * @param tokenString String to compare against the expected string.
     * @param expectedString String that is expected in the command.
     * @throws DBException Thrown if the token string doesn't match the expected string.
     */
    protected void compareStrings(String tokenString, String expectedString) throws DBException{
        if (!tokenString.equals(expectedString)){
            throw new DBServerException("String " + tokenString + " did not match the expected "
                    + expectedString + ".");
        }
    }

    /**
     * Used to prepare a DBTable object for a command.
     * @param tableName Name of the table object.
     * @throws DBException Thrown if table name is invalid or no working
     * database is being used.
     */
    protected void setupTable(String tableName) throws DBException {
        if (tableName.length() == 0) {
            throw new InvalidCommandArgumentException("No table name provided.");
        }
        if (!isNameValid(tableName)) {
            throw new InvalidCommandArgumentException("Table name was invalid.");
        }
        checkWorkingDB(workingDatabase);
        tableForCommand = new DBTable(tableName, workingDatabase);
        tableForCommand.loadTableFile();
    }

    /**
     * Checks if a working database has been selected.
     * @param workingDatabase Object to evaluate.
     * @throws DBException Returns null if the working database is null.
     */
    protected void checkWorkingDB(DBDatabase workingDatabase) throws DBException {
        if (workingDatabase == null){
            throw new NotUsingDBException("No working database has been selected.");
        }
    }
}
