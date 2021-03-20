package DBObjects.DBCommands.CommandLists;

import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBObjects.DBObject;

public abstract class CommandList extends DBObject {
    protected String[] listArgs;
    public abstract boolean parseList() throws DBException;
    protected abstract void convertStringToList() throws DBException;
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
        if (argumentStr.charAt(0) != '(' || argumentStr.charAt(argumentStr.length() - 1) != ')'){
            throw new InvalidCommandArgumentException("List did not end and begin with parentheses.");
        }
        argumentStr = argumentStr.substring(1, argumentStr.length() - 1);
        return argumentStr;
    }
    public static void test(){
    };
}
