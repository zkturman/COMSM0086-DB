package DBObjects.DBCommands;

import DBObjects.*;
import DBException.*;
import DBObjects.DBCommands.CommandLists.AttributeList;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateCommand extends DropCreateCommand {
    DBObject objectToCreate;
    ArrayList<TableAttribute> attributesToCreate;
    AttributeList attributesToParse;


    public CreateCommand(String [] createArgs){
        super(createArgs);
        attributesToCreate = new ArrayList<>();
    }

    public void parseCommand() throws DatabaseException{
        //make sure create has arguments
        super.parseCommand();
        objectToCreate = initDBObject(structureType, followingSQLCommands[1]);
        if (objectToCreate instanceof DBTable){
            ((DBTable) objectToCreate).setTableFilePaths ();
            ((DBTable) objectToCreate).setTableAttributes(attributesToCreate); //this seems fishy
        }
    }

    public void evaluateStructureArgs(StructureType type, String[] stringToProcess) throws DatabaseException {
        super.evaluateStructureArgs(type, stringToProcess);

        //check if this is a database and it contains more than the db name
        if (stringToProcess.length > 2 && type == StructureType.DATABASE){
            throw new InvalidCommandArgumentException(); //message --> there are too many arguments for a database
        }

        if (structureType == StructureType.TABLE){
            stringToProcess = Arrays.copyOfRange(stringToProcess, 1, stringToProcess.length);
            processCreateAttributes(stringToProcess);
        }
    }

    public void processCreateAttributes(String[] attributeList) throws DatabaseException{
        attributesToParse = new AttributeList(attributeList);
        if (attributesToParse.parseList()) {
            attributesToParse.convertStringToList();
        }
    }

    public void interpretCommand() throws DatabaseException{
        objectToCreate.createObject();
        if (objectToCreate instanceof DBTable){
            ((DBTable) objectToCreate).setTableAttributes(attributesToParse.getAttributeList());
            ((DBTable) objectToCreate).defineAttributeFile();
        }
    }
}
