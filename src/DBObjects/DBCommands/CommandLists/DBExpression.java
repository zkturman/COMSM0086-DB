package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.DBObject;
import DBObjects.DBTable;
import DBObjects.TableAttribute;
import DBObjects.TableRow;

import java.util.Locale;

import static DBObjects.DBCommands.DBCommand.isNameValid;

public class DBExpression extends CommandCondition {

    String operator;
    String value1;
    String value2;
    DBTable referenceTable;
    TableRow rowToEvaluate;

    protected DBExpression(){}

    public DBExpression(String value1, String value2, String operator){
        this.value1 = value1;
        this.value2 = value2;
        this.operator = operator;
    }

    public String performOperation(DBTable tableToFilter, int rowNumber) throws DBException{
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

    public String performBoolean(String booleanLeft, String booleanRight  , String operator) throws DBException {
        int binNum1, binNum2;
        if (!booleanLeft.equals("1") && !booleanLeft.equals("0")){
            throw new InvalidCommandArgumentException("Could not perform operation.");
        }
        if (!booleanRight.equals("1") && !booleanRight.equals("0")){
            throw new InvalidCommandArgumentException("Could not perform operation.");
        }
        binNum1 = Integer.parseInt(booleanLeft);
        binNum2 = Integer.parseInt(booleanRight);
        switch (operator){
            case "&":
                return performAnd(binNum1, binNum2);
            case "+":
                return performOr(binNum1, binNum2);
            default:
                throw new InvalidCommandArgumentException("Could not perform operation.");
        }
    }

    public String performAnd(int binary1, int binary2){
        if (binary1 == 1 && binary2 == 1){
            return "1";
        }
        return "0";
    }

    public String performOr(int binary1, int binary2){
        if (binary1 == 1 || binary2 == 1){
            return "1";
        }
        return "0";
    }

    public String performComparison(String attributeName, String compareValue, String operator) throws DBException {
        if (!isNameValid(attributeName)){
            throw new InvalidCommandArgumentException("Invalid attribute name used for comparison.");
        }
        if (!isValidValue(compareValue)){
            throw new InvalidCommandArgumentException("Invalid value used for comparison.");
        }
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

    public String performLike(String attributeValue, String compareValue) throws DBException {
        if (!isStringLiteral(attributeValue) || !isStringLiteral(compareValue)){
            throw new InvalidCommandArgumentException("LIKE operator requires string literals");
        }
        compareValue = removeSingleQuotes(compareValue);
        if (attributeValue.contains(compareValue)){
            return "1";
        }
        return "0";
    }

    private String removeSingleQuotes(String stringLiteral) throws DBException {
        if (stringLiteral.length() < 2){
            throw new InvalidCommandArgumentException("String literal did have wrapping single quotes.");
        }
        return stringLiteral.substring(1, stringLiteral.length() - 1);
    }

    public String performEquals(String attributeValue, String compareValue){
        if (attributeValue.equals(compareValue)){
            return "1";
        }
        return "0";
    }

    public String performNotEquals(String attributeValue, String compareValue){
        if (!attributeValue.equals(compareValue)){
            return "1";
        }
        return "0";
    }

    public String performLessThan(String attributeValue, String compareValue) throws DBException {
        Float attributeNum = convertValueToFloat(attributeValue);
        Float compareNum = convertValueToFloat(compareValue);
        if (attributeNum < compareNum){
            return "1";
        }
        return "0";
    }

    public String performGreaterThan(String attributeValue, String compareValue) throws DBException {
        Float attributeNum = convertValueToFloat(attributeValue);
        Float compareNum = convertValueToFloat(compareValue);
        if (attributeNum > compareNum){
            return "1";
        }
        return "0";
    }

    public String performLessEquals(String attributeValue, String compareValue) throws DBException {
        Float attributeNum = convertValueToFloat(attributeValue);
        Float compareNum = convertValueToFloat(compareValue);
        if (attributeNum <= compareNum){
            return "1";
        }
        return "0";
    }

    public String performGreaterEquals(String attributeValue, String compareValue) throws DBException {
        Float attributeNum = convertValueToFloat(attributeValue);
        Float compareNum = convertValueToFloat(compareValue);
        if (attributeNum >= compareNum){
            return "1";
        }
        return "0";
    }

    public Float convertValueToFloat(String value) throws DBException{
        if (isFloatLiteral(value) || isIntegerLiteral(value)){
            return Float.parseFloat(value);
        }
        throw new InvalidCommandArgumentException("Cannot use non-numbers for inequality expressions.");
    }

    public static void test()  {
        DBExpression test = new DBExpression();
        try{
            assert test.performAnd(1, 1).equals("1");
            assert test.performAnd(1, 0).equals("0");
            assert test.performAnd(0, 0).equals("0");
            assert test.performAnd(0, 1).equals("0");
            assert test.performOr(1, 1).equals("1");
            assert test.performOr(1, 0).equals("1");
            assert test.performOr(0, 0).equals("0");
            assert test.performOr(0, 1).equals("1");
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
        catch (DBException dbe){}
        System.out.println("DBExpression passed.");
    }
}
