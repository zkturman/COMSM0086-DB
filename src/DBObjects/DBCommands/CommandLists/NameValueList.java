package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.*;

import java.util.ArrayList;

import static DBObjects.DBCommands.DBCommand.isNameValid;

public class NameValueList extends ValueList {
    ArrayList<TableAttribute> attributesToChange;
    ArrayList<String> valuesForChange;
    String[] nameValueContents;

    public ArrayList<TableAttribute> getAttributesToChange() {
        return attributesToChange;
    }

    public ArrayList<String> getValuesForChange() {
        return valuesForChange;
    }

    public NameValueList(String listString) throws DBException {
        String nameValueString = removeWhiteSpace(listString);
        nameValueContents = splitValues(nameValueString);
        attributesToChange = new ArrayList<>();
        valuesForChange = new ArrayList<>();
    }

    public boolean parseList() throws DBException{
        if (isListEmpty(nameValueContents)){
            throw new InvalidCommandArgumentException("Name-value lists cannot be empty.");
        }
        convertStringToList();
        return true;
    }

    @Override
    public void convertStringToList() throws DBException {
        for (String pair : nameValueContents){
            int equalIndex = findEqualSign(pair);
            String attributeName = pair.substring(0, equalIndex);
            String valueString = pair.substring(equalIndex + 1, pair.length());
            if (!isNameValid(attributeName)){
                throw new InvalidCommandArgumentException("Attribute name contained special characters.");
            }
            TableAttribute attribute = new TableAttribute(attributeName);
            attributesToChange.add(attribute);
            if (!isValidValue(valueString)){
                throw new InvalidCommandArgumentException("Value is not of the correct form.");
            }
            valuesForChange.add(valueString);
        }
    }

    /**
     * Returns the index of the first equal sign '=' encountered that isn't within single quotes '\''
     * @param nameValuePair String of the form attributeName=valueName with optional spaces surrounding the
     *                      equal sign.
     * @return index of equal sign.
     * @throws DBException Thrown if an equal sign wasn't found or a string literal was encountered first.
     */
    public int findEqualSign(String nameValuePair) throws DBException {
        int i = 0;
        boolean found = false, quote = false;
        while (!found && !quote && i < nameValuePair.length()){
            char c = nameValuePair.charAt(i++);
            found = isExpectedChar(c, '=');
            quote = isExpectedChar(c, '\'');
        }
        checkHasEquals(found);
        return i - 1;
    }

    private boolean isExpectedChar(char actualChar, char expectedChar){
        return actualChar == expectedChar;
    }

    private void checkHasEquals(boolean hasEquals) throws DBException {
        if (!hasEquals){
            throw new InvalidCommandArgumentException("Name value pair should contain an equal sign.");
        }
    }

    public static void test(){
        String test1 = "test=blah", test2 = "test = blah", test3 = "test = 'test ='";
        String test4 = "=blah";
        try{
            NameValueList testList = new NameValueList(test1);
            assert testList.findEqualSign(test1) == 4;
            assert testList.findEqualSign(test2) == 5;
            assert testList.findEqualSign(test3) == 5;
            assert testList.findEqualSign(test4) == 0;
        }
        catch (DBException dbe){}
    }
}
