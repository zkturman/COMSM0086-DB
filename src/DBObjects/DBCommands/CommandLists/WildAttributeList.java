package DBObjects.DBCommands.CommandLists;

import DBException.DBException;
import DBObjects.TableAttribute;

import java.util.ArrayList;


public class WildAttributeList extends AttributeList{

    private boolean allAttributes = false;

    public boolean getAllAttributes(){
        return allAttributes;
    }

    public WildAttributeList(String argString) throws DBException {
        String attributeString = removeWhiteSpace(argString);
        attributeNames = splitValues(attributeString);
        attributeList = new ArrayList<>();
    }

    public void convertStringToList() throws DBException {
        if (attributeNames.length == 1 && attributeNames[0].equals("*")){
            allAttributes = true;
        }
        else{
            super.convertStringToList();
        }
    }

    public static void test(){
        try {
            WildAttributeList test1 = new WildAttributeList("*");
            WildAttributeList test2 = new WildAttributeList("  *  ");
            WildAttributeList test3 = new WildAttributeList("*, test");
            WildAttributeList test4 = new WildAttributeList("a, b, c");
            assert test1.parseList() == true;
            assert test2.parseList() == true;
            assert test3.parseList() == true;
            assert test4.parseList() == true;
        }
        catch (DBException dbe){}
        System.out.println("WildAttributeList passed.");
    }
}
