package DBObjects.DBCommands;

import DBException.*;

import java.util.Arrays;

public class JoinDBCommand extends DBCommand {

    protected JoinDBCommand(String[] joinArgs) throws DBException {
        super(joinArgs);
        if (!commandHasArguments(joinArgs)){
            throw new InvalidCommandArgumentException("Join command has no arguments.");
        }
        if (joinArgs.length != 1){
            throw new InvalidCommandArgumentException("Join command has the incorrect form.");
        }
        commandString = joinArgs[0];
        tokenizedCommand = splitCommand(commandString);
        tokenizedCommand = removeCommandName(tokenizedCommand);
    }
    @Override
    public void prepareCommand() throws DBException {
        String tableName1;
        String andString;
        String tableName2;
        String onString;
        String attributeString1;
        String andString2;
        String attributeString2;
    }

    @Override
    public void executeCommand() throws DBException {

    }

    @Override
    public String[] splitCommand(String commandString) throws DBException {
        return commandString.split("\\s+");
    }

    @Override
    public String getNextToken(String[] tokenAry, int index) throws DBException {
        if (index > tokenAry.length){
            throw new InvalidCommandArgumentException("Join has the incorrect number of arguments.");
        }
        return tokenAry[index];
    }

    @Override
    public String[] removeCommandName(String[] tokenizedCommand) {
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }
}
