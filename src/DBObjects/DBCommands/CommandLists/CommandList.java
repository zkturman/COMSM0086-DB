package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.DBObject;

/**
 * CommandList contains basic methods for core functionality of list processing.
 * This class also contains abstract methods for required class functions.
 */
public abstract class CommandList extends DBObject {

    /**
     * Default constructor CommandList for child classes.
     */
    protected CommandList(){}

    /**
     * Determines if the tokenized list has any values.
     * @param argumentList Array of tokens to evaluate
     * @return Returns true if there are zero elements in the array.
     */
    protected boolean isListEmpty(String[] argumentList){
        return argumentList.length == 0;
    }

    /**
     * Recombines a tokenized list.
     * @param stringAry Tokenized list
     * @return String version of tokenized list with all characters preserved.
     */
    protected String stringifyArray(String[] stringAry){
        return String.join("", stringAry);
    }

    /**
     * Evaluates a parenthetical string and removes its parentheses.
     * @param argumentStr String to check
     * @return Argument string with no outer parentheses.
     * @throws DBException Thrown if the string isn't wrapped in parentheses or is zero length.
     */
    protected String stripParentheses(String argumentStr) throws DBException {
        checkArgumentLength(argumentStr);
        checkParens(argumentStr);
        argumentStr = removeWrappingChars(argumentStr);
        return argumentStr;
    }

    /**
     * Determines if the argument is empty.
     * @param argument String to check
     * @throws DBException Thrown if argument is of zero length.
     */
    protected void checkArgumentLength(String argument) throws DBException {
        if (argument.length() == 0){
            throw new InvalidCommandArgumentException("There was an empty attribute somehow.");
        }
    }

    /**
     * Evaluates whether an argument is correctly wrapped in parentheses.
     * @param argument String to check
     * @throws DBException Thrown if argument is not wrapped in parentheses.
     */
    protected void checkParens(String argument) throws DBException {
        if (wrappedInParens(argument)){
            throw new InvalidCommandArgumentException("List did not end and begin with parentheses.");
        }
    }

    /**
     * Determines if a string is began and ended with parentheses.
     * @param argument String to check
     * @return Returns true if the first character is '(' and the final
     * character is ')'
     */
    protected boolean wrappedInParens(String argument){
        return argument.charAt(0) != '(' || argument.charAt(argument.length() - 1) != ')';
    }

    /**
     * Removes wrapping characters from a string, such as parentheses or quotes.
     * @param value String to alter.
     * @return Value string with no wrapping characters.
     */
    protected String removeWrappingChars(String value){
        return value.substring(1, value.length() - 1);
    }

    /**
     * Removes whitespace from list so it can be delimited cleanly.
     * @param listString List of values to affect.
     * @return Returns the list without whitespace.
     */
    protected abstract String removeWhiteSpace(String listString);

    /**
     * Determines if a value is valid. It must be either a string, boolean,
     * float, or integer.
     * @param value String to check
     * @return Returns true if the value is an appropriate type.
     */
    protected boolean isValidValue(String value){
        return isStringLiteral(value) || isFloatLiteral(value) ||
                isBooleanLiteral(value) || isIntegerLiteral(value);
    }

    /**
     * Determines if a given value is a string literal. It must be wrapped in single quotes,
     * and no internal characters can be '\'' or '\t'.
     * @param value String to check.
     * @return Returns true if the value is a string literal.
     */
    protected boolean isStringLiteral(String value){
        if (wrappedInQuotes(value)){
            return false;
        }
        value = removeWrappingChars(value);
        return validStringContent(value);
    }

    /**
     * Determines if a string literal is wrapped in single quotes.
     * @param value String to check
     * @return Returns true if the first and last characters are '\''
     */
    protected boolean wrappedInQuotes(String value){
        return value.charAt(0) != '\'' || value.charAt(value.length() - 1) != '\'';
    }

    /**
     * Confirms a string literal has no inappropriate characters.
     * @param value String to check
     * @return Returns true if the string is valid.
     */
    protected boolean validStringContent(String value){
        for (int i = 0; i < value.length(); i++){
            if (invalidLiteralChars(value, i)){
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if a character in a string literal is invalid.
     * @param value String to check.
     * @param index Index in string to check.
     * @return Returns true if the character is '\'' or '\t'
     */
    protected boolean invalidLiteralChars(String value, int index){
        return value.charAt(index) == '\'' || value.charAt(index) == '\t';
    }

    /**
     * Determines if a string represents a boolean literal. This function is case-sensitve.
     * @param value String to check
     * @return Returns true if the string is either "true" or "false"
     */
    protected boolean isBooleanLiteral(String value){
        if (value.equals("true")){
            return true;
        }
        return value.equals("false");
    }

    /**
     * Determines if a string represents a float literal.
     * @param value Value to check
     * @return Returns true if the float is comprised of two integers separated by a period.
     */
    protected boolean isFloatLiteral(String value){
        String[] floatAry = value.split("\\.");
        if (invalidFloatSplit(floatAry)){
            return false;
        }
        return parseFloatSplit(floatAry);
    }

    /**
     *  Determines if a float has exactly one period.
     * @param floatParts Resulting array after the float is split on '.'
     * @return Returns true if there are exactly two elements in floatParts
     */
    protected boolean invalidFloatSplit(String[] floatParts){
        return floatParts.length != 2;
    }

    /**
     * Determines if each string in the array is representative of an integer.
     * @param floatParts Parts of a float split on it's decimal.
     * @return Returns true if each part can be parsed as an integer.
     */
    protected boolean parseFloatSplit(String[] floatParts){
        try{
            Integer.parseInt(floatParts[0]);
            Integer.parseInt(floatParts[1]);
        }
        catch (NumberFormatException nfe){
            return false;
        }
        return true;
    }

    /**
     * Determines if a string is representative of an integer.
     * @param value String to check
     * @return Returns true if the string can be parsed as an integer.
     */
    protected boolean isIntegerLiteral(String value){
        try{
            Integer.parseInt(value);
        }
        catch (NumberFormatException nfe){
            return false;
        }
        return true;
    }

    /**
     * Parses the list string and initialises appropriate classes for commands to act
     * on the specific list.
     * @return Returns true if no errors were encountered when processing.
     * @throws DBException Thrown if the list is incorrectly formatted.
     */
    public abstract boolean processList() throws DBException;

    /**
     * Converts the string list into a list.
     * @throws DBException Thrown if conversion fails.
     */
    protected abstract void convertStringToList() throws DBException;

    /**
     * Splits the list into individual values. This is required for all lists.
     * @param argString List string from the initial command.
     * @return Returns an array of strings where each element is a value in the list.
     * @throws DBException Thrown if the list is incorrectly formatted.
     */
    protected abstract String[] splitValues(String argString) throws DBException;

    /**
     * Determines if the attributeName is valid.
     * @param attributeName String to check
     * @throws DBException Thrown if the attribute name contains special characters.
     */
    protected void checkAttributeValid(String attributeName) throws DBException {
        if (!isNameValid(attributeName)){
            throw new InvalidCommandArgumentException("Invalid attribute name used for comparison.");
        }
    }

    /**
     * Determines if a comparison value is valid.
     * @param compareValue String to check.
     * @throws DBException Thrown if the the comparison value not a valid value.
     */
    protected void checkValueValid(String compareValue)throws DBException {
        if (!isValidValue(compareValue)){
            throw new InvalidCommandArgumentException("Invalid value used for comparison.");
        }
    }
}
