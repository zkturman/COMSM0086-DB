package DBObjects.DBCommands.CommandLists;

import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBObjects.DBTest;
import DBObjects.TableAttribute;

import java.util.ArrayList;
import java.util.List;

public class AttributeList extends CommandList{

    private String[] attributeNames;
    private List<TableAttribute> attributeList;

    public ArrayList<TableAttribute> getAttributeList(){
        return (ArrayList<TableAttribute>) attributeList;
    }

    public AttributeList(String[] listArgs){
        this.listArgs = listArgs;
        attributeList = new ArrayList<>();
    }

    public boolean parseList() throws DBException {
        if (isListEmpty(listArgs)){return false;}
        String attributeStr = stripParentheses(stringifyArray(listArgs));
        attributeNames = attributeStr.split(",");
        convertStringToList();
        return true;
    }

    protected void convertStringToList() throws DBException {
        for (String i : attributeNames){
            String attribute = i;
            if (isNameValid(attribute)){
                TableAttribute attributeToAdd = new TableAttribute(attribute);
                attributeList.add(attributeToAdd); //add check to make sure this is actually a table
            }
            else{
                throw new InvalidCommandArgumentException();
            }
        }
    }

    public static void test() {
        String testAttributes1 = "( one, two, three )", testAttributes2 = "(one,two,three)";
        String testAttributes3 = "( one,two,three )", testAttributes4 = " ";
        AttributeList attList1 = new AttributeList(testAttributes1.split(" "));
        AttributeList attList2 = new AttributeList(testAttributes2.split(" "));
        AttributeList attList3 = new AttributeList(testAttributes3.split(" "));
        AttributeList attList4 = new AttributeList(testAttributes4.split(" "));

        try{
            assert !attList1.isListEmpty(attList1.listArgs);
            assert attList1.stripParentheses(attList1.stringifyArray(attList1.listArgs)).equals("one,two,three");
            assert attList1.parseList();

            assert !attList2.isListEmpty(attList2.listArgs);
            assert attList2.stripParentheses(attList2.stringifyArray(attList2.listArgs)).equals("one,two,three");
            assert attList2.parseList();

            assert !attList3.isListEmpty(attList3.listArgs);
            assert attList3.stripParentheses(attList3.stringifyArray(attList3.listArgs)).equals("one,two,three");
            assert attList3.parseList();

            assert attList4.isListEmpty(attList4.listArgs);
            assert !attList4.parseList();
        }
        catch (DBException de){}
        DBTest.passMessage("AttributeList passed.");
    }
}
