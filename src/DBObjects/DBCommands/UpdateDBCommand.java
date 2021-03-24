package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.CommandCondition;
import DBObjects.DBCommands.CommandLists.NameValueList;
import DBObjects.DBTable;

import javax.print.attribute.standard.NumberOfInterveningJobs;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateDBCommand extends DBCommand {

    String nameValueString;
    DBTable tableToUpdate;
    NameValueList updateNameValues;
    CommandCondition updateConditions;

    protected UpdateDBCommand(String[] updateArgs) throws DBException{
        super(updateArgs);
        if (!commandHasArguments(updateArgs)){
            throw new InvalidCommandArgumentException("Update command has no arguments.");
        }
        commandString = updateArgs[0];
        //Finds and removes the NameValueList
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        if (updateArgs.length == 2){
            listString = updateArgs[1];
        }
        if (updateArgs.length > 2){
            throw new InvalidCommandArgumentException("Update command has the incorrect form.");
        }
    }

    @Override
    public void prepareCommand() throws DBException {
        int currentToken = 0;
        String tableName = getNextToken(tokenizedCommand, currentToken++);
        setupTable(tableName);
        String setString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!setString.equals("SET")){
            throw new InvalidCommandArgumentException("Expected \"SET\" string in update command");
        }
        prepareNameValues();
        String whereString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!whereString.equals("WHERE")){
            throw new InvalidCommandArgumentException("Expected \"WHERE\" string in select command.");
        }
        if (currentToken != tokenizedCommand.length && listString == null){
            listString = commandString.split("\\s+where\\s+")[1];
            tokenizedCommand = Arrays.copyOfRange(tokenizedCommand, 0, currentToken);
        }
        if (currentToken == tokenizedCommand.length && listString != null) {
            prepareConditions();
        }
        else{
            throw new InvalidCommandArgumentException("Update conditions were of the incorrect form.");
        }
    }

    private void setupTable(String tableName) throws DBException {
        if (tableName.length() == 0){
            throw new InvalidCommandArgumentException("No table name was provided."); //is this possible?
        }
        if (!isNameValid(tableName)){
            throw new InvalidCommandArgumentException("Table name contained unexpected characters.");
        }
        if (workingDatabase == null){
            throw new NotUsingDBException("No working database has been selected.");
        }
        tableToUpdate = new DBTable(tableName, workingDatabase);
        tableToUpdate.loadTableFile();
    }

    public void prepareNameValues() throws DBException {
        updateNameValues = new NameValueList(nameValueString);
        updateNameValues.parseList();
    }

    public void prepareConditions() throws DBException {
        if (listString == null){
            throw new InvalidCommandArgumentException("Update command expects condition.");
        }
        updateConditions = new CommandCondition(listString);
        updateConditions.parseList();
    }

    @Override
    public void executeCommand() throws DBException {
        updateConditions.executeConditions(tableToUpdate);
        tableToUpdate.updateTable(updateNameValues);
    }

    @Override
    public String[] splitCommand(String commandString) throws DBException {
        //get the NameValueList
        Pattern nameValuePattern = Pattern.compile("(?<=\\sset)\\s+.*\\s+(?=where\\s)", Pattern.CASE_INSENSITIVE);
        Matcher nameValueMatcher = nameValuePattern.matcher(commandString);
        nameValueMatcher.find();
        nameValueString = nameValueMatcher.group();
        commandString = commandString.replaceFirst(nameValueMatcher.pattern().pattern(), " ");
        return commandString.split("\\s+");
    }

    @Override
    public String getNextToken(String[] tokenAry, int index) throws DBException {
        if (index >= tokenAry.length){
            throw new InvalidCommandArgumentException("Update command was missing the correct number of arguments.");
        }
        return tokenAry[index];
    }
}
