package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBObject;
import DBObjects.DBTable;
import DBObjects.Database;

public abstract class DropCreateDBCommand extends DBCommand {
    DBObject objectToChange;


    protected DropCreateDBCommand(String[] dropCreateArgs) throws DBException{
        if (!commandHasArguments(dropCreateArgs)){
            throw new DBException("Create or Drop command has no arguments.");
        }
        commandString = dropCreateArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        setupListVars(dropCreateArgs);
    }

    public void prepareCommand() throws DBException {
        int currentToken = 0;
        String specifiedStructure = getNextToken(tokenizedCommand, currentToken++);
        determineStructureType(specifiedStructure, workingDatabase);
        //evaluateStructureArgs(structureType);
        String structureName = getNextToken(tokenizedCommand, currentToken++);
        if (!isNameValid(structureName)){
            throw new DBInvalidObjectName("Invalid object name provided in command.");
        }
        objectToChange = initDBObject(structureType, structureName);
        if (listString != null){
            prepareList(listString);
        }
    }

    public DBObject initDBObject(StructureType type, String objectName) throws DBException {
        if (type == StructureType.DATABASE){
            return new Database(objectName);
        }
        else if (type == StructureType.TABLE){
            if (workingDatabase == null){
                throw new NotUsingDBException("No working database has been selected.");
            }
            DBTable tableToChange = new DBTable(objectName, workingDatabase);
            return tableToChange;
        }
        else {
            throw new InvalidCommandArgumentException("No appropriate structure type is specified.");
        }
    }

    //can probably move this to command
    public String[] splitCommand(String commandString) throws DBException {
        return commandString.split("\\s+");
    }

    public String getNextToken(String[] tokenAry, int index) throws DBException{
        if (index > tokenAry.length - 1){
            throw new InvalidCommandArgumentException("Command expected more arguments.");
        }
        return tokenAry[index];
    }

    public abstract String[] removeCommandName(String[] tokenizedCommand);
    public abstract void setupListVars(String[] commandArgs) throws DBException;
    public abstract void executeCommand() throws DBException;
    public abstract void prepareList(String listString) throws DBException;

}
