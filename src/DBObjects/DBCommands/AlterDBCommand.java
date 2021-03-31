package DBObjects.DBCommands;

import DBException.*;
import DBObjects.*;

/**
 * AlterDBCommand handles appending new attributes or deleting existing ones.
 * Attribute values in table rows are removed during deletion.
 */
public class AlterDBCommand extends DBCommand {
    private TableAttribute attributeToAlter;
    private AlterType alterType;

    /**
     * Constructor for AlterDBCommand.
     * @param alterArgs Preprocessed string for alter command.
     * @throws DBException Thrown if arguments contain a parenthetical list or
     * if the command is empty.
     */
    protected AlterDBCommand(String[] alterArgs) throws DBException{
        isEmptyCommand(alterArgs);
        commandString = alterArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        if (alterArgs.length > 1) {
            throw new InvalidCommandArgumentException("Alter command has unexpected structure.");
        }
    }

    /**
     * Parses the alter command and sets up the table and
     * attribute to be modified.
     * @throws DBException Thrown if arguments for command are incorrect.
     */
    protected void prepareCommand() throws DBException {
        int currentToken = 0;
        String tableString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        compareStrings(tableString, "TABLE");

        String tableName = getNextToken(tokenizedCommand, currentToken++);
        setupTable(tableName);

        String alterationType = getNextToken(tokenizedCommand, currentToken++);
        convertStringToAlterType(alterationType);

        String attributeName = getNextToken(tokenizedCommand, currentToken++);
        setupAttribute(attributeName);

        checkCommandEnded(currentToken);
    }

    /**
     * Configures the attribute name of the alter command.
     * @param attributeName Name of the attribute for the command.
     * @throws DBException Thrown if the attribute name contains non-alphanumeric
     * characters.
     */
    private void setupAttribute(String attributeName) throws DBException {
        if (!isNameValid(attributeName)){
            throw new InvalidCommandArgumentException("Attribute name is invalid.");
        }
        attributeToAlter = new TableAttribute(attributeName);
    }

    /**
     * Executes the alter command to remove or add a column to a table.
     * @throws DBException Thrown if alteration fails or the commands alter type isn't set.
     */
    protected void executeCommand() throws DBException {
        switch(alterType){
            case ADD:
                tableForCommand.appendAttribute(attributeToAlter);
                break;
            case DROP:
                tableForCommand.removeAttribute(attributeToAlter);
                break;
            default:
                throw new DBInvalidAlterType("Invalid alteration type was provided.");
        }
    }

    /**
     * Configures what type of alter command is being performed. Not case sensitive.
     * @param alterString Expected alter type from the tokenized command.
     * @throws DBException Thrown if alterString is not ADD or DROP.
     */
    private void convertStringToAlterType(String alterString) throws DBException{
        switch(alterString.toUpperCase()){
            case "ADD":
                alterType = AlterType.ADD;
                break;
            case "DROP":
                alterType = AlterType.DROP;
                break;
            default:
                throw new InvalidCommandArgumentException("Invalid alteration type provided.");
        }
    }
}
