package DBObjects.DBCommands;

import DBException.*;
import DBObjects.*;

/**
 * UseDBCommand is responsible for setting the working database for future commands.
 * The database is stored in memory and passed to DBServer. It is updated during each
 * DBStatement, but is only ever modified within this class.
 */
public class UseDBCommand extends DBCommand {
    DBDatabase databaseToUse;

    /**
     * Constructor for UseDBCommand. Responsible for setting the working database.
     * @param useArgs Pre-processed string for command.
     * @throws DBException Thrown if there are no arguments in the command.
     */
    protected UseDBCommand(String[] useArgs) throws DBException{
        isEmptyCommand(useArgs);
        commandString = useArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
    }

    /**
     * Parses command string and initialises a DBDatabase.
     * @throws DBException Thrown if database name is invalid or more than one
     * argument is provided.
     */
    protected void prepareCommand() throws DBException {
        int currentIndex = 0;
        String databaseName = getNextToken(tokenizedCommand, currentIndex++);
        if (!isNameValid(databaseName)){
            throw new InvalidCommandArgumentException("Database did not have a valid name.");
        }
        databaseToUse = new DBDatabase(databaseName);
        checkCommandEnded(currentIndex);
    }

    /**
     * Sets the working database to the specified one.
     * @throws DBException Throws an error if the database does not exist.
     */
    protected void executeCommand() throws DBException {
        if (!databaseToUse.dbObjectExists()){
            throw new DBObjectDoesNotExistException("Could not find database.");
        }
        workingDatabase = databaseToUse;
    }
}
