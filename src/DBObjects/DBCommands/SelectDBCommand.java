package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectDBCommand extends DBCommand{

    String attributeString;
    WildAttributeList selectAttributes;
    CommandCondition selectConditions;

    public SelectDBCommand(String[] selectArgs) throws DBException{
        if (isEmptyCommand(selectArgs)){
            throw new InvalidCommandArgumentException("Select command has no arguments.");
        }
        if (selectArgs.length > 2){
            throw new InvalidCommandArgumentException("Select command has the incorrect form.");
        }
        commandString = selectArgs[0];
        //finds and removes the WildAttributeList
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        if (selectArgs.length == 2){
            listString = selectArgs[1];
        }
    }

    @Override
    public void prepareCommand() throws DBException {
        int currentToken = 0;
        prepareAttributes();
        String fromString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!fromString.equals("FROM")){
            throw new InvalidCommandArgumentException("Expected \"FROM\" string in select command");
        }
        String tableName = getNextToken(tokenizedCommand, currentToken++);
        //Configured in DBCommand parent class
        setupTable(tableName);
        if (currentToken != tokenizedCommand.length){
            String whereString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
            if (!whereString.equals("WHERE")){
                throw new InvalidCommandArgumentException("Expected \"WHERE\" string in select command.");
            }
            if (listString == null){
                listString = commandString.split("(?i)\\s+where\\s+")[1];
            }
            prepareConditions();
        }
    }

    public void prepareAttributes() throws DBException {
        selectAttributes = new WildAttributeList(attributeString);
        selectAttributes.parseList();
    }

    public void prepareConditions() throws DBException {
        selectConditions = new CommandCondition(listString);
        selectConditions.parseList();
    }

    @Override
    public void executeCommand() throws DBException {
        if (selectConditions != null){
            selectConditions.executeConditions(tableForCommand);
        }
        if (selectAttributes.getAllAttributes()){
            returnMessage = tableForCommand.printTable();
        }
        else{
            returnMessage = tableForCommand.printTable(selectAttributes.getAttributeList());
        }
    }

    @Override
    public String[] splitCommand(String commandString) throws DBException {
        //get attribute list:
        Pattern attributePattern = Pattern.compile("(?<=select).*(?=from\\s+)", Pattern.CASE_INSENSITIVE);
        Matcher attributeMatcher = attributePattern.matcher(commandString);
        if (!attributeMatcher.find()){
            throw new InvalidCommandArgumentException("Could not find attribute list in select statement.");
        }
        attributeString = attributeMatcher.group();
        commandString = attributeMatcher.replaceFirst(" ");
        return commandString.split("\\s+");
    }

    public static void test(){
        String[] test1 = new String[1];
        test1[0] = "select cool, from from table test1";
        try {
            SelectDBCommand selectTest = new SelectDBCommand(test1);
            selectTest.splitCommand(test1[0]);
            assert selectTest.attributeString.equals(" cool, from ");
            selectTest.splitCommand("select*from table test1");
            assert selectTest.attributeString.equals("*");
            selectTest.splitCommand("select a, b, c, d from table test1");
            assert selectTest.attributeString.equals(" a, b, c, d ");
        }
        catch (DBException dbe){}
        System.out.println("SelectDBCommand passed.");
    }

}
