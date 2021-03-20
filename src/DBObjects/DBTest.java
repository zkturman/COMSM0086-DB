package DBObjects;

import DBObjects.DBCommands.CommandLists.AttributeList;
import DBObjects.DBCommands.CommandLists.CommandCondition;
import DBObjects.DBCommands.CommandLists.ValueList;
import DBObjects.DBObject;

public class DBTest extends DBObject {

    public static void main (String[] args){
        AttributeList.test();
        ValueList.test();
        DBStatement.test();
    }
    public static void passMessage(String completeMessage){
        System.out.println(completeMessage);
    }
}
