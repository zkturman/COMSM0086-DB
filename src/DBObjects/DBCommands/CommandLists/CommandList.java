package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.DBObject;

public abstract class CommandList extends DBObject {

    protected CommandList(){}

    protected boolean isListEmpty(String[] argumentList){
        if (argumentList.length == 0){
            return true;
        }
        return false;
    }

    protected String stringifyArray(String[] stringAry){
        return String.join("", stringAry);
    }

    protected String stripParentheses(String argumentStr) throws DBException {
        if (argumentStr.length() == 0){
            throw new InvalidCommandArgumentException("There was an empty attribute somehow.");
        }
        if (argumentStr.charAt(0) != '(' || argumentStr.charAt(argumentStr.length() - 1) != ')'){
            throw new InvalidCommandArgumentException("List did not end and begin with parentheses.");
        }
        argumentStr = argumentStr.substring(1, argumentStr.length() - 1);
        return argumentStr;
    }

    protected abstract String removeWhiteSpace(String valueString);

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
        return isIntegerLiteral(value);
    }

    protected boolean isStringLiteral(String value){
        if (value.charAt(0) != '\'' || value.charAt(value.length() - 1) != '\''){
            return false;
        }
        value = value.substring(1, value.length() - 1);
        for (int i = 0; i < value.length(); i++){
            if (value.charAt(i) == '\'' || value.charAt(i) == '\t'){
                return false;
            }
        }
        return true;
    }

    protected boolean isBooleanLiteral(String value){
        if (value.toUpperCase().equals("TRUE")){
            return true;
        }
        return value.toUpperCase().equals("FALSE");
    }

    protected boolean isFloatLiteral(String value){
        String[] floatAry = value.split("\\.");
        if (floatAry.length != 2){
            return false;
        }
        try{
            Integer.parseInt(floatAry[0]);
            Integer.parseInt(floatAry[1]);
        }
        catch (NumberFormatException nfe){
            return false;
        }
        return true;
    }

    protected boolean isIntegerLiteral(String value){
        try{
            Integer.parseInt(value);
        }
        catch (NumberFormatException nfe){
            return false;
        }
        return true;
    }

    public abstract boolean parseList() throws DBException;
    protected abstract void convertStringToList() throws DBException;
    protected abstract String[] splitValues(String argString) throws DBException;
}
