package DBObjects.DBCommands;

import DBObjects.*;
import DBException.*;
import DBObjects.DBCommands.CommandLists.AttributeList;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateDBCommand extends DropCreateDBCommand {
    DBObject objectToCreate;
    ArrayList<TableAttribute> attributesToCreate;
    AttributeList attributesToParse;


    public CreateDBCommand(String [] createArgs){
        super(createArgs);
        attributesToCreate = new ArrayList<>();
    }

    public void prepareCommand() throws DBException {
        //make sure create has arguments
        super.prepareCommand();
        objectToCreate = initDBObject(structureType, followingSQLCommands[1]);
        if (objectToCreate instanceof DBTable){
            ((DBTable) objectToCreate).setTableFilePaths ();
            ((DBTable) objectToCreate).setTableAttributes(attributesToCreate); //this seems fishy
        }
    }

    public void evaluateStructureArgs(StructureType type, String[] stringToProcess) throws DBException {
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

    public void processCreateAttributes(String[] attributeList) throws DBException {
        attributesToParse = new AttributeList(attributeList);
        attributesToParse.parseList();
    }

    public void executeCommand() throws DBException {
        objectToCreate.createObject();
        if (objectToCreate instanceof DBTable){
            ((DBTable) objectToCreate).setTableAttributes(attributesToParse.getAttributeList());
            ((DBTable) objectToCreate).defineAttributeFile();
        }
    }
}
