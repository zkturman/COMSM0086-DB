package DBObjects.DBCommands.CommandLists;

import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBObjects.DBTest;
import DBObjects.TableRow;

import java.util.ArrayList;
import java.util.List;

public class ValueList extends CommandList{

    private String[] valueContents;
    private TableRow valueList;

    protected ValueList(){}

    public ValueList(String argString) throws DBException {
        String valueString = removeWhiteSpace(argString);
        valueString = stripParentheses(valueString);
        valueContents = splitValues(valueString);
    }

    public TableRow getValueList(){
        return valueList;
    }

    public boolean parseList() throws DBException {
        if (isListEmpty(valueContents)){
            return false;
        }
        convertStringToList();
        return true;
    }

    /**
     * Splits the values of a list on any commas that are not within single quotation marks '\''
     * @param valueString String to split
     * @return Array of strings containing separate values.
     * @throws DBException if the list ends with a comma
     */
    protected String[] splitValues(String valueString) throws DBException {
        //need the inverse of split on this ((\'.*?\'|[^\',\s]+))
        //here it is ,(?=(?:[^\']*\'[^\']*\')*[^\']*$)
        int startIndex = 0, endIndex = 0, i;
        ArrayList<String> splitList = new ArrayList<>();
        boolean inQuotes = false;
        for (i = 0; i < valueString.length(); ++i){
            endIndex = i;
            if (valueString.charAt(i) == ',' && !inQuotes){
                splitList.add(valueString.substring(startIndex, endIndex));
                startIndex = i + 1;
            }
            if (valueString.charAt(i) == '\''){
                inQuotes = !inQuotes;
            }
        }
        if (endIndex < startIndex){
            throw new InvalidCommandArgumentException("Invalid comma separators in value list.");
        }
        splitList.add(valueString.substring(startIndex, endIndex + 1));
        String[] returnAry = new String[splitList.size()];
        return splitList.toArray(returnAry);
    }

    /**
     * Converts the values into a TableRow
     * @throws DBException if any values in the list are none of string, boolean, float, or integer literals
     */
    public void convertStringToList() throws DBException {
        for (String valueStr : valueContents) {
            if (!isValidValue(valueStr)) {
                throw new InvalidCommandArgumentException("Value " + valueStr + " is not an appropriate type.");
            }
        }
        valueList = new TableRow(valueContents);
    }

    /**
     * Removes whites space from a list except whitespace found in single quotation marks '\''
     * @param valueString String to remove whitespace from
     * @return Input string without whitespace
     */
    protected String removeWhiteSpace(String valueString){
        boolean inQuotes = false;
        StringBuilder valuesNoSpaces = new StringBuilder();
        for (int i = 0; i < valueString.length(); i++){
            if (valueString.charAt(i) == '\''){
                inQuotes = !inQuotes;
            }
            if (!Character.isWhitespace(valueString.charAt(i)) || inQuotes){
                valuesNoSpaces.append(valueString.charAt(i));
            }
        }
        return valuesNoSpaces.toString();
    }

    public static void test(){
        String test1 = "('abc,  defg', true, 1.2345, 12345 )";
        testValues();
        try{
            ValueList test = new ValueList(test1);
            test.removeWhiteSpace(test1);
            test.stripParentheses(test1);
        }
        catch (DBException de){
            System.out.println("encountered dbe");
        }
        DBTest.passMessage("ValueList passed.");
    }

    public static void testValues(){
        String test1 = "('abcdefg', true, 1.2345, 12345 )";
        try {
            ValueList test = new ValueList(test1);
            assert test.isStringLiteral("'abcdefg'");
            assert test.isStringLiteral("'abc123'");
            assert test.isStringLiteral("'ab12!@'");
            assert test.isStringLiteral("'ab'");
            assert test.isStringLiteral("'ab\n\r'");
            assert !test.isStringLiteral("'ab\n\t'");
            assert test.isStringLiteral("'ab cd'");
            assert !test.isBooleanLiteral("abcd");
            assert !test.isBooleanLiteral("");
            assert test.isBooleanLiteral("true");
            assert test.isBooleanLiteral("TRUE");
            assert test.isBooleanLiteral("False");
            assert test.isBooleanLiteral("FALSE");
            assert test.isFloatLiteral("1.5");
            assert test.isFloatLiteral("1.555555");
            assert test.isFloatLiteral("-1.5");
            assert !test.isFloatLiteral("1.a");
            assert !test.isFloatLiteral("1.");
            assert !test.isFloatLiteral(".1");
            assert !test.isFloatLiteral("");
            assert test.isIntegerLiteral("1");
            assert test.isIntegerLiteral("1234");
            assert !test.isIntegerLiteral("asd");
            assert test.isIntegerLiteral("-1234");
            assert !test.isIntegerLiteral("");
            assert !test.isIntegerLiteral("1a");
            assert !test.isIntegerLiteral("\t");
            assert test.isValidValue("'abcd'");
            assert !test.isValidValue("'abcd");
            assert !test.isValidValue("'abc\t'");
            assert test.isValidValue("true");
            assert test.isValidValue("false");
            assert test.isValidValue("1.234");
            assert test.isValidValue("-12.34");
            assert !test.isValidValue("1.aa");
            assert test.isValidValue("11");
            assert test.isValidValue("-11");
            assert !test.isValidValue("1a");
        }
        catch (DBException de){}

    }
}
