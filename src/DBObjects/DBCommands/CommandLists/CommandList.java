package DBObjects.DBCommands.CommandLists;

import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBObjects.DBObject;

public abstract class CommandList extends DBObject {
    public abstract boolean parseList() throws DBException;
    protected abstract void convertStringToList() throws DBException;
    protected abstract String[] splitValues(String argString) throws DBException;

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
    public static void test(){
    };
}
