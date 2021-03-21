package DBObjects.DBCommands;

import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBObjects.*;

import java.util.Arrays;

public class DropDBCommand extends DropCreateDBCommand {

    public DropDBCommand(String[] dropArgs) throws DBException{
        super(dropArgs);
    }

    @Override
    public String[] removeCommandName(String[] tokenizedCommand) {
        return Arrays.copyOfRange(tokenizedCommand, 1, tokenizedCommand.length);
    }

    @Override
    public void setupListVars(String[] commandArgs) throws DBException {
        if (commandArgs.length > 1){
            throw new InvalidCommandArgumentException("Drop command has unexpected arguments.");
        }
    }

    public void executeCommand() throws DBException {
        objectToChange.dropObject();
    }

    @Override
    public void prepareList(String listString) {}
}
