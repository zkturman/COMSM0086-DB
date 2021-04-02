package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.*;

/**
 * DBExpression handles comparing a table row against an expression from a condition. A condition
 * can be made up of many expressions, and each one is compared against the row. The final value of
 * the chained expressions determines whether the row will be filtered from the DBTable object.
 */
public class DBExpression extends CommandCondition {

    private final String operator;
    private final String value1;
    private final String value2;
    private DBTable referenceTable;
    private TableRow rowToEvaluate;

    /**
     * Constructor for a DBExpression.
     * @param value1 First value to be evaluated. Can be "1", "0", or an attribute name.
     * @param value2 Second value to be evaluated. Can be "1", "0", or a valid value.
     * @param operator Operator to determine how expression is evaluated.
     */
    protected DBExpression(String value1, String value2, String operator){
        this.value1 = value1;
        this.value2 = value2;
        this.operator = operator;
    }

    /**
     * Performs an operation for a condition.
     * @param tableToFilter Table from which a row will be evaluated against the expression.
     * @param rowNumber Table row to evaluate.
     * @return Returns "1" if the row meets the expressions requirements, "0" otherwise.
     * @throws DBException Thrown if the expression cannot be performed on the row.
     */
    protected String performOperation(DBTable tableToFilter, int rowNumber) throws DBException{
        this.referenceTable = tableToFilter;
        rowToEvaluate = tableToFilter.getTableRow(rowNumber);
        switch (operator){
            case "&":
            case "+":
                return performBoolean(value1, value2, operator);
            case "~":
            case "=":
            case "!":
            case "<":
            case ">":
            case "£":
            case "@":
                return performComparison(value1, value2, operator);
            default:
                throw new InvalidCommandArgumentException("Could not perform operation.");
        }
    }

    /**
     * Performs a boolean operation on two string booleans. Handles AND and OR operations.
     * @param booleanLeft Left boolean value to evaluate.
     * @param booleanRight Right boolean value to evaluate.
     * @param operator Boolean operator to apply.
     * @return Returns "1" if the operation is true, "0" otherwise.
     * @throws DBException Thrown if an invalid operator or boolean value was used.
     */
    private String performBoolean(String booleanLeft, String booleanRight  , String operator) throws DBException {
        checkBooleanValues(booleanLeft);
        checkBooleanValues(booleanRight);
        switch (operator){
            case "&":
                return performAnd(booleanLeft, booleanRight);
            case "+":
                return performOr(booleanLeft, booleanRight);
            default:
                throw new InvalidCommandArgumentException("Could not perform operation.");
        }
    }

    /**
     * Determines if a boolean value is valid. A value is valid only if it
     * is equal to "1" or "0".
     * @param booleanValue String to evaluate.
     * @throws DBException Thrown if value is neither "1" nor "0".
     */
    private void checkBooleanValues(String booleanValue) throws DBException {
        if (!booleanValue.equals("1") && !booleanValue.equals("0")){
            throw new InvalidCommandArgumentException("Could not perform operation.");
        }
    }

    /**
     * Determines if AND statement evaluates to true.
     * @param booleanLeft Left boolean value from operation.
     * @param booleanRight Right boolean value from operation.
     * @return Returns "1" if either booleanLeft and booleanRight equals "1", "0" otherwise.
     */
    private String performAnd(String booleanLeft, String booleanRight){
        if (booleanLeft.equals("1") && booleanRight.equals("1")){
            return "1";
        }
        return "0";
    }

    /**
     * Determines if OR statement evaluates to true.
     * @param booleanLeft Left boolean value from operation.
     * @param booleanRight Right boolean value from operation.
     * @return Returns "1" if either booleanLeft or booleanRight equals "1", "0" otherwise.
     */
    private String performOr(String booleanLeft, String booleanRight){
        if (booleanLeft.equals("1") || booleanRight.equals("1")){
            return "1";
        }
        return "0";
    }

    /**
     * Performs comparison operations for command conditions.
     * @param attributeName Name of attribute to derive attribute value.
     * @param compareValue Value from condition for comparison
     * @param operator The type of comparison
     * @return Returns "1" if the comparison is true, "0" otherwise.
     * @throws DBException Thrown if operation cannot be performed.
     */
    private String performComparison(String attributeName, String compareValue, String operator) throws DBException {
        checkAttributeValid(attributeName);
        checkValueValid(compareValue);
        int attributeIndex = referenceTable.getAttributeIndex(attributeName);
        String attributeValue = rowToEvaluate.getValue(attributeIndex);
        switch (operator){
            case "~":
                return performLike(attributeValue, compareValue);
            case "=":
                return performEquals(attributeValue, compareValue);
            case "!":
                return performNotEquals(attributeValue, compareValue);
            case "<":
                return performLessThan(attributeValue, compareValue);
            case ">":
                return performGreaterThan(attributeValue, compareValue);
            case "£":
                return performLessEquals(attributeValue, compareValue);
            case "@":
                return performGreaterEquals(attributeValue, compareValue);
            default:
                throw new InvalidCommandArgumentException("Unrecognized operator.");
        }
    }

    /**
     * Determines if an attribute value is like a value from a condition string. Like
     * is defined as the the attribute value containing the comparison value.
     * @param attributeValue Value for the corresponding attribute found in a table.
     * @param compareValue Value to compare from the condition string.
     * @return Returns "1" if attributeValue contains compareValue, "0" otherwise.
     * @throws DBException Thrown if either attributeValue or compareValue are not string literals.
     */
    private String performLike(String attributeValue, String compareValue) throws DBException {
        checkLikeValid(attributeValue, compareValue);
        compareValue = removeSingleQuotes(compareValue);
        if (attributeValue.contains(compareValue)){
            return "1";
        }
        return "0";
    }

    /**
     * Determines if both string for LIKE operation are string literals. String literals are
     * required for the LIKE operation.
     * @param attributeValue Value for the corresponding attribute found in a table.
     * @param compareValue Value to compare from the condition string.
     * @throws DBException Thrown if either attributeValue or compareValue are not string literals.
     */
    private void checkLikeValid(String attributeValue, String compareValue) throws DBException {
        if (!isStringLiteral(attributeValue) || !isStringLiteral(compareValue)){
            throw new InvalidCommandArgumentException("LIKE operator requires string literals");
        }
    }

    /**
     * Removes wrapping single quotes from a string literal.
     * @param stringLiteral String to strip.
     * @return Returns string without single quotes.
     * @throws DBException Thrown if string length is less than two.
     */
    private String removeSingleQuotes(String stringLiteral) throws DBException {
        if (stringLiteral.length() < 2){
            throw new InvalidCommandArgumentException("String literal did have wrapping single quotes.");
        }
        return removeWrappingChars(stringLiteral);
    }

    /**
     * Determines if an attribute value is equal to a value from a condition string.
     * @param attributeValue Value for the corresponding attribute found in a table.
     * @param compareValue Value to compare from the condition string.
     * @return Returns "1" if attributeValue == compareValue, "0" otherwise.
     */
    private String performEquals(String attributeValue, String compareValue){
        if (attributeValue.equals(compareValue)){
            return "1";
        }
        return "0";
    }

    /**
     * Determines if an attribute value is not equal to a value from a condition string.
     * @param attributeValue Value for the corresponding attribute found in a table.
     * @param compareValue Value to compare from the condition string.
     * @return Returns "1" if attributeValue != compareValue, "0" otherwise.
     */
    private String performNotEquals(String attributeValue, String compareValue){
        if (!attributeValue.equals(compareValue)){
            return "1";
        }
        return "0";
    }

    /**
     * Attempts to determine if the attribute value is less than the compare value.
     * @param attributeValue Value for the corresponding attribute found in a table.
     * @param compareValue Value to compare from the condition string.
     * @return Returns "1" if attributeValue < compare value, "0" otherwise
     * @throws DBException Thrown if either value is not a number.
     */
    private String performLessThan(String attributeValue, String compareValue) throws DBException {
        Float attributeNum = convertValueToFloat(attributeValue);
        Float compareNum = convertValueToFloat(compareValue);
        if (attributeNum < compareNum){
            return "1";
        }
        return "0";
    }

    /**
     * Attempts to determine if the attribute value is greater than the compare value.
     * @param attributeValue Value for the corresponding attribute found in a table.
     * @param compareValue Value to compare from the condition string.
     * @return Returns "1" if attributeValue > compare value, "0" otherwise
     * @throws DBException Thrown if either value is not a number.
     */
    private String performGreaterThan(String attributeValue, String compareValue) throws DBException {
        Float attributeNum = convertValueToFloat(attributeValue);
        Float compareNum = convertValueToFloat(compareValue);
        if (attributeNum > compareNum){
            return "1";
        }
        return "0";
    }

    /**
     * Attempts to determine if the attribute value is less than or equal to the compare value.
     * @param attributeValue Value for the corresponding attribute found in a table.
     * @param compareValue Value to compare from the condition string.
     * @return Returns "1" if attributeValue <= compare value, "0" otherwise
     * @throws DBException Thrown if either value is not a number.
     */
    private String performLessEquals(String attributeValue, String compareValue) throws DBException {
        Float attributeNum = convertValueToFloat(attributeValue);
        Float compareNum = convertValueToFloat(compareValue);
        if (attributeNum <= compareNum){
            return "1";
        }
        return "0";
    }

    /**
     * Attempts to determine if the attribute value is greater than or equal to the compare value.
     * @param attributeValue Value for the corresponding attribute found in a table.
     * @param compareValue Value to compare from the condition string.
     * @return Returns "1" if attributeValue >= compare value, "0" otherwise.
     * @throws DBException Thrown if either value is not a number.
     */
    private String performGreaterEquals(String attributeValue, String compareValue) throws DBException {
        Float attributeNum = convertValueToFloat(attributeValue);
        Float compareNum = convertValueToFloat(compareValue);
        if (attributeNum >= compareNum){
            return "1";
        }
        return "0";
    }

    /**
     * Attempt to convert a string representation of a number to a float.
     * @param value String to parse.
     * @return String as a float.
     * @throws DBException Thrown if the string is neither a float nor an integer.
     */
    private Float convertValueToFloat(String value) throws DBException{
        if (isFloatLiteral(value) || isIntegerLiteral(value)){
            return Float.parseFloat(value);
        }
        throw new InvalidCommandArgumentException("Cannot use non-numbers for inequality expressions.");
    }

    /**
     * Testing for DBExpression.
     */
    public static void test()  {
        DBExpression test = new DBExpression("1", "1", "&");
        try{
            assert test.performAnd("1", "1").equals("1");
            assert test.performAnd("1", "0").equals("0");
            assert test.performAnd("0", "0").equals("0");
            assert test.performAnd("0", "1").equals("0");
            assert test.performOr("1", "1").equals("1");
            assert test.performOr("1", "0").equals("1");
            assert test.performOr("0", "1").equals("1");
            assert test.performOr("0", "0").equals("0");
            assert test.performLike("'table'", "'tab'").equals("1");
            assert test.performLike("'table'", "'cat'").equals("0");
            assert test.performEquals("table", "table").equals("1");
            assert test.performEquals("12342", "1222").equals("0");
            assert test.performNotEquals("123423", "1234121").equals("1");
            assert test.performNotEquals("table", "table").equals("0");
            assert test.performLessThan("123", "124").equals("1");
            assert test.performLessThan("123", "122").equals("0");
            assert test.performLessThan("123", "123").equals("0");
            assert test.performLessEquals("123", "124").equals("1");
            assert test.performLessEquals("123", "122").equals("0");
            assert test.performLessEquals("123", "123").equals("1");
            assert test.performGreaterThan("123", "124").equals("0");
            assert test.performGreaterThan("123", "122").equals("1");
            assert test.performGreaterThan("123", "123").equals("0");
            assert test.performGreaterEquals("123", "124").equals("0");
            assert test.performGreaterEquals("123", "122").equals("1");
            assert test.performGreaterEquals("123", "123").equals("1");
        }
        catch (DBException dbe){
            System.out.println("Error in DBExpression testing.");
        }
        System.out.println("DBExpression passed.");
    }
}
