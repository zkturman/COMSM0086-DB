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
    DBTable tableToUpdate;
    NameValueList updateNameValues;
    CommandCondition updateConditions;

    protected UpdateDBCommand(String[] updateArgs) throws DBException{
        super(updateArgs);
        if (!commandHasArguments(updateArgs)){
            throw new InvalidCommandArgumentException("Update command has no arguments.");
        }
        if (updateArgs.length != 2){
            throw new InvalidCommandArgumentException("Update command has the incorrect form.");
        }
        commandString = updateArgs[0];
        //Finds and removes the NameValueList
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
        listString = updateArgs[1];
    }

    @Override
    public void prepareCommand() throws DBException {
        int currentToken = 0;
        String tableName;
        String setString;
        prepareNameValues();
        String whereString = getNextToken(tokenizedCommand, currentToken++).toUpperCase();
        if (!whereString.equals("WHERE")){
            throw new InvalidCommandArgumentException("Expected \"WHERE\" string in select command.");
        }
        prepareConditions();
    }

    public void prepareNameValues() throws DBException {
        updateNameValues = new NameValueList(nameValueString);
    }

    public void prepareConditions() throws DBException {
        updateConditions = new CommandCondition(listString);
        updateConditions.parseList();
    }

    @Override
    public void executeCommand() throws DBException {

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

    @Override
    public String[] removeCommandName(String[] tokenizedCommand) {
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }
}
