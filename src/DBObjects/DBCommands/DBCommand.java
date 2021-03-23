package DBObjects.DBCommands;
import DBException.DBObjectDoesNotExistException;
import DBObjects.*;
import DBException.*;

import java.util.Arrays;

public abstract class DBCommand extends DBObject {
    public String commandString;
    public String[] tokenizedCommand;
    public String listString;
    protected StructureType structureType;
    Database workingDatabase;

    public Database getWorkingDatabase() {
        return workingDatabase;
    }

    public void setWorkingDatabase(Database dbToSet){
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
            case "DELETE":
            case "JOIN":
            default:
                return null;
        }
    }

    public boolean processCommand(Database currentDB) throws DBException{
        workingDatabase = currentDB;
        prepareCommand();
        executeCommand();
        return true;
    }

    public void determineStructureType(String specifiedType, Database currentDB) throws DBException {
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

    public boolean commandHasArguments(String[] commandAry) throws DBException{
        if (commandAry.length < 1) {
            throw new InvalidCommandArgumentException("Command has no arguments.");
        }
        return true;
    }

    public abstract void prepareCommand() throws DBException;
    public abstract void executeCommand() throws DBException;
    public abstract String[] splitCommand(String commandString) throws DBException;
    public abstract String getNextToken(String[] tokenAry, int index) throws DBException;
    public abstract String[] removeCommandName(String[] tokenizedCommand);
    public static void test(){
    }
}
