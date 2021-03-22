package DBObjects.DBCommands.CommandLists;

import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBObjects.DBTest;
import DBObjects.TableAttribute;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AttributeList extends CommandList{

    protected String[] attributeNames;
    protected List<TableAttribute> attributeList;

    public ArrayList<TableAttribute> getAttributeList(){
        return (ArrayList<TableAttribute>) attributeList;
    }

    protected AttributeList(){}
    public AttributeList(String argString) throws DBException{
        String attributeString = removeWhiteSpace(argString);
        attributeString = stripParentheses(attributeString);
        attributeNames = splitValues(attributeString);
        attributeList = new ArrayList<>();
    }

    public boolean parseList() throws DBException {
        if (isListEmpty(attributeNames)){return false;}
        convertStringToList();
        return true;
    }

    protected void convertStringToList() throws DBException {
        for (String attribute : attributeNames){
            if (isNameValid(attribute)){
                TableAttribute attributeToAdd = new TableAttribute(attribute);
                attributeList.add(attributeToAdd); //add check to make sure this is actually a table
            }
            else{
                throw new InvalidCommandArgumentException("Attribute name was not valid.");
            }
        }
    }

    @Override
    protected String[] splitValues(String argString) throws DBException {
        return argString.split(",");
    }

    protected String removeWhiteSpace(String valueString){
        String valuesNoSpaces = "";
        for (int i = 0; i < valueString.length(); i++){
            if (!Character.isWhitespace(valueString.charAt(i))){
                valuesNoSpaces += valueString.charAt(i);
            }
        }
        return valuesNoSpaces;
    }

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
            assert attList1.parseList();

            assert !attList2.isListEmpty(attList2.attributeNames);
            assert attList2.stripParentheses(attList2.stringifyArray(attList2.attributeNames)).equals("one,two,three");
            assert attList2.parseList();

            assert !attList3.isListEmpty(attList3.attributeNames);
            assert attList3.stripParentheses(attList3.stringifyArray(attList3.attributeNames)).equals("one,two,three");
            assert attList3.parseList();

            assert attList4.isListEmpty(attList4.attributeNames);
            assert !attList4.parseList();
        }
        catch (DBException de){}
        DBTest.passMessage("AttributeList passed.");
    }
}
