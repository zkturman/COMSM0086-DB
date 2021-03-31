package DBObjects.DBCommands;

import DBException.*;
import DBObjects.*;

/**
 * DropCreateDBCommand handles commands that involve creating or dropping database objects.
 * Because the commands are similar, the bulk of their functionality is found in this class.
 */
public abstract class DropCreateDBCommand extends DBCommand {
    DBObject objectToChange;

    /**
     * Constructor for a DropCreateDBCommand. Instantiates some necessary variables.
     * @param dropCreateArgs Tokenized command to be executed.
     * @throws DBException Thrown if command processing fails.
     */
    protected DropCreateDBCommand(String[] dropCreateArgs) throws DBException{
        isEmptyCommand(dropCreateArgs);
        commandString = dropCreateArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        setupListVars(dropCreateArgs);
    }

    /**
     * Parses and initialises database objects necessary for executing a command.
     * @throws DBException Thrown if the structure of the command is incorrect.
     */
    protected void prepareCommand() throws DBException {
        int currentToken = 0;
        String specifiedStructure = getNextToken(tokenizedCommand, currentToken++);
        determineStructureType(specifiedStructure, workingDatabase);
        //evaluateStructureArgs(structureType);
        String structureName = getNextToken(tokenizedCommand, currentToken);
        if (!isNameValid(structureName)){
            throw new DBInvalidObjectName("Invalid object name provided in command.");
        }
        objectToChange = initDBObject(structureType, structureName);
        if (listString != null){
            prepareList(listString);
        }
    }

    /**
     * Initialises the database object to be dropped or created.
     * @param type The structure type, previously validated.
     * @param objectName Name of object to initialise.
     * @return Returns the initialised DBObject.
     * @throws DBException Thrown if initialisation fails.
     */
    private DBObject initDBObject(StructureType type, String objectName) throws DBException {
        if (type == StructureType.DATABASE){
            return new DBDatabase(objectName);
        }
        return new DBTable(objectName, workingDatabase);
    }

    /**
     Setups up variables related to list arguments for drop and create.
     * @param commandArgs Tokenized command.
     * @throws DBException Thrown if setting up the list fails.
     */
    protected abstract void setupListVars(String[] commandArgs) throws DBException;

    /**
     * Executes the drop or create command.
     * @throws DBException Thrown if the execution of the command fail.
     */
    protected abstract void executeCommand() throws DBException;

    /**
     * Parses and initialises lists for create and drop commands.
     * @param listString String of values to be processed.
     * @throws DBException Thrown if the list is formatted incorrectly.
     */
    protected abstract void prepareList(String listString) throws DBException;
}
