package DBObjects.DBCommands;
import DBException.DBObjectDoesNotExistException;
import DBObjects.*;
import DBException.*;

import java.util.Arrays;

public abstract class DBCommand extends DBObject {
    public String commandString;
    public String[] tokenizedCommand;
    public String listString;
    public String[] tokenizedList;
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
        switch (commandName) {
            case "CREATE":
                return new CreateDBCommand(commandArgs);
            case "USE":
                return new UseDBCommand(commandArgs);
            case "DROP":
                return new DropCreateDBCommand(commandArgs);
            case "ALTER":
                return new AlterDBCommand(commandArgs);
            case "INSERT":
                return new InsertDBCommand(commandArgs);
            case "SELECT":
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

    public boolean determinedStructureType(String specifiedType, Database currentDB) throws DBException {
        switch (specifiedType){
            case "TABLE":
                if (currentDB == null){
                    throw new DBObjectDoesNotExistException();
                }
                structureType = StructureType.TABLE;
                break;
            case "DATABASE":
                structureType = StructureType.DATABASE;
                break;
            default:
                System.out.println("Invalid create command parameters.");
                return false;
        }
        return true;
    }

    public boolean commandHasArguments(String[] commandAry) throws DBException{
        if (commandAry.length == 0) {
            throw new InvalidCommandArgumentException("Command had no arguments.");
        }
        return true;
    }

    public abstract void prepareCommand() throws DBException;
    public abstract void executeCommand() throws DBException;
    public abstract String[] splitCommand(String commandString) throws DBException;
    public static void test(){
    }
}
