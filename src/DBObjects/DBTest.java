package DBObjects;

import DBObjects.DBCommands.CommandLists.*;
import DBObjects.DBCommands.SelectDBCommand;

/**
 * Test class for parsing and interpreting.
 * Focuses on assertion testing.
 */
public class DBTest extends DBObject {

    /**
     * Used to check processing of certain classes.
     * @param args Args for main command. Not used.
     */
    public static void main (String[] args){
        AttributeList.test();
        ValueList.test();
        DBStatement.test();
        SelectDBCommand.test();
        WildAttributeList.test();
        CommandCondition.test();
        DBExpression.test();
        NameValueList.test();
    }

    /**
     * Used to process testing messages.
     * @param completeMessage Message to print.
     */
    public static void passMessage(String completeMessage){
        System.out.println(completeMessage);
    }
}
