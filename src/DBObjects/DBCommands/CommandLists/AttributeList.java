package DBObjects.DBCommands.CommandLists;

import DBException.DatabaseException;
import DBException.InvalidCommandArgumentException;
import DBObjects.TableAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttributeList extends CommandList{

    private List<TableAttribute> attributeList;

    public ArrayList<TableAttribute> getAttributeList(){
        return (ArrayList<TableAttribute>) attributeList;
    }

    public AttributeList(String[] listArgs){
        this.listArgs = listArgs;
        attributeList = new ArrayList<>();
    }

    public boolean parseList() throws DatabaseException {
        if (listArgs.length == 0){
            return false; //there are no attributes to process
        }
        String attributeStr = String.join("", listArgs);
        if (attributeStr.charAt(0) != '(' || attributeStr.charAt(attributeStr.length() - 1) != ')'){
            throw new InvalidCommandArgumentException(); //message --> attribute list does start and end with parentheses
        }

        //attributes should be delimited with commas only here, so check that all are valid when split on ','
        attributeStr = attributeStr.substring(1, attributeStr.length() - 1);
        listArgs = attributeStr.split(",");
        return true;

    }

    public void convertStringToList() throws DatabaseException{
        for (int i = 0; i < listArgs.length; i++){
            String attributeName = listArgs[i];
            if (isNameValid(attributeName)){
                TableAttribute attributeToAdd = new TableAttribute(attributeName);
                attributeList.add(attributeToAdd); //add check to make sure this is actually a table
            }
            else{
                throw new InvalidCommandArgumentException();
            }
        }
    }
}
