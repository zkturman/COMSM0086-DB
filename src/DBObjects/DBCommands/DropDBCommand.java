package DBObjects.DBCommands;

import DBException.*;

/**
 * DropDBCommand handles deleting tables and databases.
 */
public class DropDBCommand extends DropCreateDBCommand {

    /**
     * Constructor for DropDBCommand. Uses parent constructor.
     * @param dropArgs Pre-processed command string.
     * @throws DBException Thrown if pre-processed string is unexpected.
     */
    public DropDBCommand(String[] dropArgs) throws DBException{
        super(dropArgs);
    }

    /**
     * Determines if a list has been passed to a drop command. Drop
     * commands do not take lists as arguments.
     * @param commandArgs Tokenized command.
     * @throws DBException Thrown if a list argument exists for the command.
     */
    @Override
    public void setupListVars(String[] commandArgs) throws DBException {
        if (commandArgs.length > 1){
            throw new InvalidCommandArgumentException("Drop command has unexpected arguments.");
        }
    }

    /**
     * Executes the drop command.
     * @throws DBException Thrown if the object is not able to be deleted.
     */
    public void executeCommand() throws DBException {
        objectToChange.dropObject();
    }

    /**
     * Abstract class. There are no lists for drop commands.
     * @param listString String of values to be processed.
     */
    @Override
    public void prepareList(String listString) {}
}
