package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AttributeList parses and processes attribute lists in commands. It
 * verifies each attribute is a valid name, not that the attribute exists in
 * the table.
 */
public class AttributeList extends CommandList{

    protected String[] attributeNames;
    protected List<TableAttribute> attributeList;

    /**
     * Default constructor for child classes.
     */
    protected AttributeList(){}

    /**
     * Constructor for AttributeList.
     * @param argString String of attributes to process.
     * @throws DBException Thrown if string isn't wrapped in parentheses.
     */
    public AttributeList(String argString) throws DBException{
        String attributeString = removeWhiteSpace(argString);
        attributeString = stripParentheses(attributeString);
        attributeNames = splitValues(attributeString);
        attributeList = new ArrayList<>();
    }

    /**
     * Removes all whitespace in an attribute list. No whitespace is permitted.
     * Undefined behaviour occurs if the list is delimited by spaces.
     * @param valueString String to remove spaces from.
     * @return Returns string with no spaces.
     */
    @Override
    protected String removeWhiteSpace(String valueString){
        StringBuilder valuesNoSpaces = new StringBuilder();
        for (int i = 0; i < valueString.length(); i++){
            if (!Character.isWhitespace(valueString.charAt(i))){
                valuesNoSpaces.append(valueString.charAt(i));
            }
        }
        return valuesNoSpaces.toString();
    }

    /**
     * Creates an array of Strings from a string split on commas.
     * @param argString List string from the initial command.
     * @return Returns an array of strings with each element representing an attribute.
     */
    @Override
    protected String[] splitValues(String argString){
        return argString.split(",");
    }

    /**
     * Gets table attributes that were generates.
     * @return Returns an ArrayList or table attributes.
     */
    public ArrayList<TableAttribute> getAttributeList(){
        return (ArrayList<TableAttribute>) attributeList;
    }

    /**
     * Converts the arguments into a list that can be acted upon.
     * @return Returns true if processing succeeds.
     * @throws DBException Thrown if processing fails or there are no
     * attributes to process.
     */
    public boolean processList() throws DBException {
        if (isListEmpty(attributeNames)){return false;}
        convertStringToList();
        return true;
    }

    /**
     * Adds values to an ArrayList of table attributes.
     * @throws DBException Thrown if attribute names contain special characters.
     */
    @Override
    protected void convertStringToList() throws DBException {
        for (String attribute : attributeNames){
            if (isNameValid(attribute)){
                TableAttribute attributeToAdd = new TableAttribute(attribute);
                attributeList.add(attributeToAdd);
            }
            else{
                throw new InvalidCommandArgumentException("Attribute name was not valid.");
            }
        }
    }

    /**
     * Testing for AttributeList
     */
    public static void test() {
        String testAttributes1 = "( one, two, three )", testAttributes2 = "(one,two,three)";
        String testAttributes3 = "( one,two,three )", testAttributes4 = " ";

        try{
            AttributeList attList1 = new AttributeList(testAttributes1);
            AttributeList attList2 = new AttributeList(testAttributes2);
            AttributeList attList3 = new AttributeList(testAttributes3);
            AttributeList attList4 = new AttributeList(testAttributes4);

            assert !attList1.isListEmpty(attList1.attributeNames);
            assert attList1.stripParentheses(attList1.stringifyArray(attList1.attributeNames)).equals("one,two,three");
            assert attList1.processList();

            assert !attList2.isListEmpty(attList2.attributeNames);
            assert attList2.stripParentheses(attList2.stringifyArray(attList2.attributeNames)).equals("one,two,three");
            assert attList2.processList();

            assert !attList3.isListEmpty(attList3.attributeNames);
            assert attList3.stripParentheses(attList3.stringifyArray(attList3.attributeNames)).equals("one,two,three");
            assert attList3.processList();

            assert attList4.isListEmpty(attList4.attributeNames);
            assert !attList4.processList();
        }
        catch (DBException de){
            System.out.println("Error testing AttributeList."); }
        DBTest.passMessage("AttributeList passed.");
    }
}
