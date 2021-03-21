package DBObjects.DBCommands;

import DBObjects.*;
import DBException.*;
import DBObjects.DBCommands.CommandLists.AttributeList;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateDBCommand extends DropCreateDBCommand {
    AttributeList attributesToParse;

    public CreateDBCommand(String [] createArgs) throws DBException {
        super(createArgs);
    }

    @Override
    public String[] removeCommandName(String[] tokenizedCommand){
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }

    @Override
    public void setupListVars(String[] createArgs) throws DBException {
        if (createArgs.length > 1){
            listString = createArgs[1];
        }
        if (createArgs.length > 2){
            throw new InvalidCommandArgumentException("Create argument did not have the expected structure.");
        }
    }

    public void executeCommand() throws DBException {
        objectToChange.createObject();
        if (objectToChange instanceof DBTable){
            ((DBTable) objectToChange).setTableAttributes(attributesToParse.getAttributeList());
            ((DBTable) objectToChange).defineAttributeFile();
        }
    }

    @Override
    public void prepareList(String listString) throws DBException{
        attributesToParse = new AttributeList(listString);
        attributesToParse.parseList();
    }
}
