package DBObjects.DBCommands;
import DBObjects.*;

import DBException.*;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateCommand extends Command {
    DBObject objectToCreate;
    ArrayList<TableAttribute> attributesToCreate;


    public CreateCommand(String [] createArgs){
        super(createArgs);
    }
    public boolean processCommand(Database currentDB) {
        workingDatabase = currentDB;
        //parseCommand
        //parsing is going to verify statement is correct and create objects in preparation for storage

        //interpretCommand
        //create objects after verifying they are correct and store objects

        try {
            parseCommand();
            interpretCommand();
        }
        catch (DatabaseException dbe){
            System.out.println("We couldn't parse the message...");
            dbe.printStackTrace();
            return false;
        }
        //does this need to be boolean??
        return true;
    }

    public void parseCommand() throws DatabaseException{
        //make sure create has arguments
        super.parseCommand();

        String specifiedStructure = followingSQLCommands[0].toUpperCase();

        //make sure we can create the specified database object --> table or database
        //create database object here
        //set structureType here
        if (!determinedStructureType(specifiedStructure, workingDatabase)){ throw new InvalidCommandArgumentException(); }

        //make sure following arguments for create statement are valid
        String[] argumentList = Arrays.copyOfRange(followingSQLCommands, 1, followingSQLCommands.length);
        processCreateObject(structureType, argumentList);
        objectToCreate = createNewObject(structureType, followingSQLCommands[1]);
    }

    public void processCreateObject(int type, String[] stringToProcess) throws DatabaseException {

        //check if nothing follows the structure type
        if (stringToProcess.length == 0){
            throw new InvalidCommandArgumentException(); //message --> no object name given
        }

        //check if this is a database and it contains more than the db name
        if (stringToProcess.length > 2 && type == 0){
            throw new InvalidCommandArgumentException(); //message --> there are too many arguments for a database
        }

        if (!isNameValid(stringToProcess[0])){
            throw new InvalidCommandArgumentException(); //message --> the object name is invalid
        }

        if (structureType == 1){
            processCreateAttributes(stringToProcess);
        }
    }

    public DBObject createNewObject(int type, String objectName) throws DatabaseException{

        //create database if that is specified
        if (type == 0) {
            return new Database(objectName);
        }

        //create table if that is specified
        if (type == 1) {
            if (workingDatabase == null) {
                throw new NotUsingDatabaseExeception();
            }
            else {
                DBTable newTable = new DBTable(objectName);
                newTable.setOwningDatabase(workingDatabase);
                return newTable;
                //do we have attributes? process this-->
            }
        }
        else {
            throw new DatabaseException();
        }
    }

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
        attributeStr = attributeStr.substring(1, attributeList.length - 1);
        attributeList = attributeStr.split(",");
        for (int i = 0; i < attributeList.length; i++){
            createTableAttributes(attributeList[i]);
        }
    }

    public void createTableAttributes(String attributeName) throws DatabaseException{
        if (isNameValid(attributeName)){
            TableAttribute attributeToAdd = new TableAttribute(attributeName);
            ((DBTable) objectToCreate).addAttribute(attributeToAdd); //add check to make sure this is actually a table
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


    public void testCreate(){

    }


}
