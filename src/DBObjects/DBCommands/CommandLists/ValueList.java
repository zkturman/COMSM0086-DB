package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.*;
import java.util.ArrayList;

/**
 * ValueList class is responsible for parsing a value list, primarily for INSERT commands.
 * The list is converted into a new TableRow. Insertion is handled by the DBTable class.
 */
public class ValueList extends CommandList{

    private String[] valueContents;
    private TableRow valueList;

    /**
     * Default ValueList constructor for child classes.
     */
    protected ValueList(){}

    /**
     * Constructor for a ValueList that takes a comma-delimited list of values.
     * @param argString Comma-delimited list of values.
     * @throws DBException Thrown if not wrapped in parentheses or commas are
     * incorrectly formatted.
     */
    public ValueList(String argString) throws DBException {
        String valueString = removeWhiteSpace(argString);
        valueString = stripParentheses(valueString);
        valueContents = splitValues(valueString);
    }

    /**
     * Returns the Table Row created when parsing the list.
     * @return ValueList's table row.
     */
    public TableRow getValueList(){
        return valueList;
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
            char c = valueString.charAt(i);
            if (c == '\''){
                inQuotes = !inQuotes;
            }
            if (shouldKeepChar(c, inQuotes)){
                valuesNoSpaces.append(c);
            }
        }
        return valuesNoSpaces.toString();
    }

    /**
     * Determines if a character should be kept when removing whitespace from
     * a value list.
     * @param charToCheck Current character in string.
     * @param inQuotes If the current character is within single quotes.
     * @return Returns true if the character is in quotes or not whitespace.
     */
    private boolean shouldKeepChar(char charToCheck, boolean inQuotes){
        return !Character.isWhitespace(charToCheck) || inQuotes;
    }

    /**
     * Splits the values of a list on any commas that are not within single quotation marks '\''
     * @param valueString String to split
     * @return Array of strings containing separate values.
     * @throws DBException Thrown if the list ends with a comma
     */
    protected String[] splitValues(String valueString) throws DBException {
        int startIndex = 0, endIndex = 0, i;
        ArrayList<String> splitList = new ArrayList<>();
        boolean inQuotes = false;
        for (i = 0; i < valueString.length(); ++i){
            endIndex = i;
            char c = valueString.charAt(i);
            if (shouldSplit(c, inQuotes)){
                splitList.add(valueString.substring(startIndex, endIndex));
                startIndex = i + 1;
            }
            inQuotes = setInQuotes(c, inQuotes);
        }
        checkListEnd(startIndex, endIndex);
        splitList.add(valueString.substring(startIndex, endIndex + 1));
        String[] returnAry = new String[splitList.size()];
        return splitList.toArray(returnAry);
    }

    private boolean shouldSplit(char charToCheck, boolean inQuotes){
        return charToCheck == ',' && !inQuotes;
    }

    /**
     * If the current character in a string is a single quote, this returns the opposite of
     * in quotes. This behaviour allows inQuote to be inverted each time a quote is encountered
     * and thus finds the beginning and end of string literals.
     * @param charToCheck Current character in string.
     * @param inQuotes Variable to determine if the character is within quotes.
     * @return If a single quote hasn't been encounter, it returns inQuotes. Otherwise, it returns NOT inQuotes.
     */
    private boolean setInQuotes(char charToCheck, boolean inQuotes){
        if (charToCheck == '\''){
            inQuotes = !inQuotes;
        }
        return inQuotes;
    }

    /**
     * Determines if the list terminated correctly, particularly if there was a terminating comma.
     * @param lastWordStart The last starting index of a value in the list.
     * @param lastWordEnd The last character in a value string.
     * @throws DBException Thrown if lastWordStart is greater than lastWordEnd.
     */
    private void checkListEnd(int lastWordStart, int lastWordEnd) throws DBException {
        if (lastWordEnd < lastWordStart){
            throw new InvalidCommandArgumentException("Invalid comma separators in value list.");
        }
    }

    /**
     * Parses list string and converts it to a new Table Row.
     * @return True if the processing succeeds
     * @throws DBException Thrown if processing fails.
     */
    public boolean processList() throws DBException {
        if (isListEmpty(valueContents)){
            return false;
        }
        convertStringToList();
        return true;
    }

    /**
     * Converts the values into a TableRow
     * @throws DBException if any values in the list are none of string, boolean, float, or integer literals
     */
    protected void convertStringToList() throws DBException {
        for (String valueStr : valueContents) {
            if (!isValidValue(valueStr)) {
                throw new InvalidCommandArgumentException("Value " + valueStr + " is not an appropriate type.");
            }
        }
        valueList = new TableRow(valueContents);
    }

    /**
     * Testing for value list processing.
     */
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

    /**
     * Testing for value identification.
     */
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
        catch (DBException de){
            System.out.println("Error in ValueList testing.");
        }

    }
}
