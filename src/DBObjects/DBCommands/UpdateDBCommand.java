package DBObjects.DBCommands;

import DBException.*;
import DBObjects.DBCommands.CommandLists.CommandCondition;
import DBObjects.DBCommands.CommandLists.NameValueList;
import DBObjects.DBTable;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateDBCommand extends DBCommand {

    String nameValueString;
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
        //Configured in DBCommand parent class
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
        updateConditions.executeConditions(tableForCommand);
        tableForCommand.updateTable(updateNameValues);
    }

    @Override
    public String[] splitCommand(String commandString) {
        //get the NameValueList
        Pattern nameValuePattern = Pattern.compile("(?<=\\sset)\\s+.*\\s+(?=where\\s)", Pattern.CASE_INSENSITIVE);
        Matcher nameValueMatcher = nameValuePattern.matcher(commandString);
        nameValueMatcher.find();
        nameValueString = nameValueMatcher.group();
        commandString = commandString.replaceFirst(nameValueMatcher.pattern().pattern(), " ");
        return commandString.split("\\s+");
    }
}
