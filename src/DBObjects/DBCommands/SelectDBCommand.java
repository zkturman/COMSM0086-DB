package DBObjects.DBCommands;

import DBException.*;
import DBObjects.*;
import DBObjects.DBCommands.CommandLists.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectDBCommand extends DBCommand{

    String attributeString;
    DBTable tableToRead;
    WildAttributeList selectAttributes;
    CommandCondition selectConditions;

    public SelectDBCommand(String[] selectArgs) throws DBException{
        super(selectArgs);
        if (!commandHasArguments(selectArgs)){
            throw new InvalidCommandArgumentException("Select command has no arguments.");
        }
        if (selectArgs.length > 2){
            throw new InvalidCommandArgumentException("Select command has the incorrect form.");
        }
        commandString = selectArgs[0];
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
        setupTable(tableName);
        if (shouldCheckConditions()){
            String whereString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
            if (!whereString.equals("WHERE")){
                throw new InvalidCommandArgumentException("Expected \"WHERE\" string in select command.");
            }
            prepareConditions();
        }
        if (tokenizedCommand.length != currentToken){
            listString = commandString.split("\\s+where\\s+")[1];
            prepareConditions();
        }
    }

    public void prepareAttributes() throws DBException {
        selectAttributes = new WildAttributeList(attributeString);
        selectAttributes.parseList();
    }

    public void setupTable(String tableName) throws DBException {
        if (tableName.length() == 0) {
            throw new InvalidCommandArgumentException("No table name provided.");
        }
        if (!isNameValid(tableName)) {
            throw new InvalidCommandArgumentException("Table name was invalid.");
        }
        if (workingDatabase == null){
            throw new NotUsingDBException("No working database has been selected.");
        }
        tableToRead = new DBTable(tableName, workingDatabase);
        tableToRead.loadTableFile();
    }

    public void setupSingleExpression() throws DBException {

    }

    public boolean shouldCheckConditions(){
        return listString != null;
    }

    public void prepareConditions() throws DBException {
        selectConditions = new CommandCondition(listString);
        selectConditions.parseList();
    }

    @Override
    public void executeCommand() throws DBException {
        if (selectConditions != null){
            selectConditions.executeConditions(tableToRead);
        }
        if (selectAttributes.getAllAttributes()){
            tableToRead.printTable();
        }
        tableToRead.printTable(selectAttributes.getAttributeList());
    }

    @Override
    public String[] splitCommand(String commandString) throws DBException {
        //get attribute list:
        Pattern attributePattern = Pattern.compile("(?<=select).*(?=from)");
        Matcher attributeMatcher = attributePattern.matcher(commandString);
        attributeMatcher.find();
        attributeString = attributeMatcher.group();
        commandString = commandString.replaceFirst(attributeMatcher.pattern().pattern(), " ");
        return commandString.split("\\s+");
    }

    @Override
    public String getNextToken(String[] tokenAry, int index) throws DBException {
        if (index >= tokenAry.length){
            throw new InvalidCommandArgumentException("Select command was missing the correct number of arguments.");
        }
        return tokenAry[index];
    }

    @Override
    public String[] removeCommandName(String[] tokenizedCommand) {
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
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
