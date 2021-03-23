package DBObjects;

import DBObjects.DBCommands.CommandLists.*;
import DBObjects.DBCommands.SelectDBCommand;
import DBObjects.DBObject;

public class DBTest extends DBObject {

    public static void main (String[] args){
        AttributeList.test();
        ValueList.test();
        DBStatement.test();
        SelectDBCommand.test();
        WildAttributeList.test();
        CommandCondition.test();
        DBExpression.test();
    }
    public static void passMessage(String completeMessage){
        System.out.println(completeMessage);
    }
}
