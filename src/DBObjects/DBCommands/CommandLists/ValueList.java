package DBObjects.DBCommands.CommandLists;

import DBException.DatabaseException;
import DBException.InvalidCommandArgumentException;

import java.util.ArrayList;
import java.util.List;

public class ValueList extends CommandList{

    private List<String> valueList;
    public ValueList(String[] listArgs){
        this.listArgs = listArgs;
        valueList = new ArrayList<>();
    }

    public ArrayList<String> getValueList(){
        return (ArrayList<String>) valueList;
    }

    public boolean parseList() throws DatabaseException{
        if (listArgs.length == 0){
            return false;
        }
        String valueStr = String.join("", listArgs);
        if(valueStr.charAt(0) != '(' || valueStr.charAt(valueStr.length() - 1) != ')'){
            throw new InvalidCommandArgumentException(); //message --> value list does not start and end with parentheses
        }
        valueStr = valueStr.substring(1, valueStr.length() - 1);
        listArgs = valueStr.split(",");
        return true;
    }

    public void convertStringToList() throws DatabaseException {
        for (int i = 0; i < listArgs.length; i++){
            if (isValidValue(listArgs[i])){
                valueList.add(listArgs[i]);
            }
            else{
                throw new InvalidCommandArgumentException(); //message --> value is not of appropriate type
            }
        }
    }

    protected boolean isValidValue(String value){
        if (isStringLiteral(value)){
            return true;
        }
        if (isBooleanLiteral(value)){
            return true;
        }
        if (isFloatLiteral(value)){
            return true;
        }
        if (isIntegerLiteral(value)){
            return true;
        }
        return false;
    }

    protected boolean isStringLiteral(String value){
        return true;
    }

    protected boolean isBooleanLiteral(String value){
        return true;
    }

    protected boolean isFloatLiteral(String value){
        return true;
    }

    protected boolean isIntegerLiteral(String value){
        return true;
    }

}
