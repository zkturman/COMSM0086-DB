package DBObjects.DBCommands;
import DBObjects.*;
import DBException.*;

import java.util.Arrays;

public abstract class DBCommand extends DBObject {
    public String commandString;
    public String[] tokenizedCommand;
    public String listString;
    protected StructureType structureType;
    DBDatabase workingDatabase;
    protected String returnMessage;
    DBTable tableForCommand;

    public String getReturnMessage() {
        return returnMessage;
    }

    public DBDatabase getWorkingDatabase() {
        return workingDatabase;
    }

    public void setWorkingDatabase(DBDatabase dbToSet){
        workingDatabase = dbToSet;
    }

    protected  DBCommand(){}

    public DBCommand(String[] commandArray) throws DBException{}

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

    public boolean processCommand(DBDatabase currentDB) throws DBException{
        workingDatabase = currentDB;
        prepareCommand();
        executeCommand();
        return true;
    }

    public void determineStructureType(String specifiedType, DBDatabase currentDB) throws DBException {
        specifiedType = specifiedType.toUpperCase();
        switch (specifiedType){
            case "TABLE":
                if (currentDB == null){
                    throw new NotUsingDBException("No working database is selected.");
                }
                structureType = StructureType.TABLE;
                return;
            case "DATABASE":
                structureType = StructureType.DATABASE;
                return;
            default:
                throw new InvalidCommandArgumentException("No valid structure was selected.");
        }
    }

    public void setupTable(String tableName) throws DBException {
        if (tableName.length() == 0) {
            throw new InvalidCommandArgumentException("No table name provided.");
        }
        if (!isNameValid(tableName)) {
            throw new InvalidCommandArgumentException("Table name was invalid.");
        }
        if (workingDatabase == null){
            throw new NotUsingDBException("No working database has been selected.");
        }
        tableForCommand = new DBTable(tableName, workingDatabase);
        tableForCommand.loadTableFile();
    }

    public boolean commandHasArguments(String[] commandAry) throws DBException{
        if (commandAry.length < 1) {
            throw new InvalidCommandArgumentException("Command has no arguments.");
        }
        return true;
    }

    public abstract void prepareCommand() throws DBException;
    public abstract void executeCommand() throws DBException;

    protected String[] splitCommand(String commandString) throws DBException {
        return commandString.split("\\s+");
    }

    protected String getNextToken(String[] tokenAry, int index) throws DBException{
        if (index > tokenAry.length - 1){
            throw new InvalidCommandArgumentException("Command has an inappropriate number of arguments.");
        }
        return tokenAry[index];
    }

    protected String[] removeCommandName(String[] tokenizedCommand) {
        int startIndex = 1;
        if (tokenizedCommand[0].equals("")){
            startIndex = 2;
        }
        return Arrays.copyOfRange(tokenizedCommand, startIndex, tokenizedCommand.length);
    }

    public static void test(){
    }
}
