package DBObjects.DBCommands.CommandLists;

import DBException.DBException;
import java.util.ArrayList;

/**
 * WildAttributeList handles loading attributes for printed tables.
 * If only an asterisk is provide, then all attributes will be loaded.
 * Otherwise, functionality from AttributeList is used.
 */
public class WildAttributeList extends AttributeList{

    private boolean allAttributes = false;

    /**
     * Constructor for WildAttributeList. This does not remove wrapping parentheses.
     * @param argString List of attributes to load.
     */
    public WildAttributeList(String argString) {
        String attributeString = removeWhiteSpace(argString);
        attributeNames = splitValues(attributeString);
        attributeList = new ArrayList<>();
    }

    /**
     * Gets value to determine if all attributes should be loaded.
     * @return Returns true if "*" was indicated for attributes.
     */
    public boolean getAllAttributes(){
        return allAttributes;
    }

    /**
     * If only an asterisk was provided in the list, all attributes will be loaded.
     * Otherwise only specified attributes will be processed.
     * @throws DBException Thrown if attribute names contain special characters.
     */
    protected void convertStringToList() throws DBException {
        if (attributeNames.length == 1 && attributeNames[0].equals("*")){
            allAttributes = true;
        }
        else{
            super.convertStringToList();
        }
    }

    /**
     * Testing for WildAttributeList.
     */
    public static void test(){
        try {
            WildAttributeList test1 = new WildAttributeList("*");
            WildAttributeList test2 = new WildAttributeList("  *  ");
            WildAttributeList test3 = new WildAttributeList("*, test");
            WildAttributeList test4 = new WildAttributeList("a, b, c");
            assert test1.processList();
            assert test2.processList();
            assert test3.processList();
            assert test4.processList();
        }
        catch (DBException dbe){
            System.out.println("Error testing WildAttributeList.");
        }
        System.out.println("WildAttributeList passed.");
    }
}
