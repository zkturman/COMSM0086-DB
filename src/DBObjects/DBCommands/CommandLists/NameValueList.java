package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.*;
import java.util.ArrayList;

/**
 * NameValueList parses a list of name value pairs. Names are attributes that needs to
 * be updated in a table. Values are the values which need to be added to those attributes.
 */
public class NameValueList extends ValueList {

    private final ArrayList<TableAttribute> attributesToChange;
    private final ArrayList<String> valuesForChange;
    private final String[] nameValueContents;

    /**
     * Constructor for a NameValueList.
     * @param listString List of name-value pairs.
     * @throws DBException Thrown if the list ends with a comma.
     */
    public NameValueList(String listString) throws DBException {
        String nameValueString = removeWhiteSpace(listString);
        nameValueContents = splitValues(nameValueString);
        attributesToChange = new ArrayList<>();
        valuesForChange = new ArrayList<>();
    }

    /**
     * Gets attributes that need updates.
     * @return Returns an ArrayList of attribute.
     */
    public ArrayList<TableAttribute> getAttributesToChange() {
        return attributesToChange;
    }

    /**
     * Gets the values that should be updated.
     * @return Returns an ArrayList of values.
     */
    public ArrayList<String> getValuesForChange() {
        return valuesForChange;
    }

    /**
     * Checks if the list is empty, and then converts the string to a list.
     * @return True if processing completes.
     * @throws DBException Thrown if the list is empty or incorrectly formatted.
     */
    @Override
    public boolean processList() throws DBException{
        if (isListEmpty(nameValueContents)){
            throw new InvalidCommandArgumentException("Name-value lists cannot be empty.");
        }
        convertStringToList();
        return true;
    }

    /**
     * Adds attributes and values to lists for each name value pair.
     * @throws DBException Thrown if an attribute name or value is invalid.
     */
    @Override
    protected void convertStringToList() throws DBException {
        for (String pair : nameValueContents){
            int equalIndex = findEqualSign(pair);
            String attributeName = pair.substring(0, equalIndex);
            String valueString = pair.substring(equalIndex + 1);

            checkAttributeValid(attributeName);
            TableAttribute attribute = new TableAttribute(attributeName);
            attributesToChange.add(attribute);

            checkValueValid(valueString);
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
    private int findEqualSign(String nameValuePair) throws DBException {
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

    /**
     * Determines if a character matches an expected character.
     * @param actualChar Current character when iterating.
     * @param expectedChar Expected character.
     * @return Returns true if the two characters match.
     */
    private boolean isExpectedChar(char actualChar, char expectedChar){
        return actualChar == expectedChar;
    }

    /**
     * Determines if a name value pair has an equal sign. All pairs should have this.
     * @param hasEquals Boolean to reflect if name value pair has an equal sign.
     * @throws DBException Thrown if hasEquals is false.
     */
    private void checkHasEquals(boolean hasEquals) throws DBException {
        if (!hasEquals){
            throw new InvalidCommandArgumentException("Name value pair should contain an equal sign.");
        }
    }

    /**
     * Testing for NameValueList
     */
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
        catch (DBException dbe){
            System.out.println("Error testing NameValueList.");
        }
    }
}
