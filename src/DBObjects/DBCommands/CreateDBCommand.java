package DBObjects.DBCommands;

import DBObjects.*;
import DBException.*;
import DBObjects.DBCommands.CommandLists.AttributeList;

/**
 *CreateDBCommand is responsible for creating tables and databases.
 */
public class CreateDBCommand extends DropCreateDBCommand {
    AttributeList attributesToParse;

    /**
     * Constructor for a CreateDBCommand. Initialises fields for the command.
     * @param createArgs Pre-processed command string. The list is separate from the bulk
     *                   of the command.
     * @throws DBException Thrown if instantiation fails or preprocessed string doesn't
     * match expectations.
     */
    public CreateDBCommand(String [] createArgs) throws DBException {
        super(createArgs);
    }

    /**
     * If there is a list present in the tokenized command, this function will
     * set up the appropriate fields.
     * @param createArgs Tokenized command.
     * @throws DBException Thrown if there are more than two elements in the pre-processed command.
     */
    @Override
    public void setupListVars(String[] createArgs) throws DBException {
        if (createArgs.length > 1){
            listString = createArgs[1];
        }
        if (createArgs.length > 2){
            throw new InvalidCommandArgumentException("Create argument did not have the expected structure.");
        }
    }

    /**
     * Executes a CREATE command, creating the table or database.
     * @throws DBException Thrown if creation fails.
     */
    public void executeCommand() throws DBException {
        objectToChange.createObject();
        if (objectToChange instanceof DBTable){
            if (attributesToParse != null){
                ((DBTable) objectToChange).setTableAttributes(attributesToParse.getAttributeList());
            }
        }
    }

    /**
     * Prepares a list of attributes for if creating a table object.
     */
    @Override
    public void prepareList(String listString) throws DBException {
        attributesToParse = new AttributeList(listString);
        attributesToParse.processList();
    }
}
