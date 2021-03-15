package DBObjects.DBCommands;

import DBObjects.*;
import DBException.*;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateCommand extends DropCreateCommand {
    DBObject objectToCreate;
    ArrayList<TableAttribute> attributesToCreate;


    public CreateCommand(String [] createArgs){
        super(createArgs);
        attributesToCreate = new ArrayList<>();
    }

    public void parseCommand() throws DatabaseException{
        //make sure create has arguments
        super.parseCommand();
        objectToCreate = initDBObject(structureType, followingSQLCommands[1]);
        if (objectToCreate instanceof DBTable){
            ((DBTable) objectToCreate).setTableFilePaths();
            ((DBTable) objectToCreate).setTableAttributes(attributesToCreate);
        }
    }

    public void evaluateStructureArgs(int type, String[] stringToProcess) throws DatabaseException {
        super.evaluateStructureArgs(type, stringToProcess);

        //check if this is a database and it contains more than the db name
        if (stringToProcess.length > 2 && type == 0){
            throw new InvalidCommandArgumentException(); //message --> there are too many arguments for a database
        }

        if (structureType == 1){
            processCreateAttributes(stringToProcess);
        }
    }

    //maybe a class to handle this as this code is repeated in other commands. or put this in command
    public void processCreateAttributes(String[] attributeList) throws DatabaseException{
        if (attributeList.length <= 1){
            return; //there are no attributes to process
        }
        //remove the table name
        attributeList = Arrays.copyOfRange(attributeList, 1, attributeList.length);
        String attributeStr = String.join("", attributeList);
        if (attributeStr.charAt(0) != '(' || attributeStr.charAt(attributeStr.length() - 1) != ')'){
            throw new InvalidCommandArgumentException(); //message --> attribute list does start and end with parentheses
        }

        //attributes should be delimited with commas only here, so check that all are valid when split on ','
        attributeStr = attributeStr.substring(1, attributeStr.length() - 1);
        attributeList = attributeStr.split(",");
        for (int i = 0; i < attributeList.length; i++){
            createTableAttributes(attributeList[i]);
        }
    }

    public void createTableAttributes(String attributeName) throws DatabaseException{
        if (isNameValid(attributeName)){
            TableAttribute attributeToAdd = new TableAttribute(attributeName);
            attributesToCreate.add(attributeToAdd); //add check to make sure this is actually a table
        }
        else{
            throw new InvalidCommandArgumentException();
        }
    }

    public void interpretCommand() throws DatabaseException{
        objectToCreate.createObject();
        if (objectToCreate instanceof DBTable){
            ((DBTable) objectToCreate).defineAttributeFile();
        }
    }
}
