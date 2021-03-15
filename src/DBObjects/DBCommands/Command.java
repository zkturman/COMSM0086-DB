package DBObjects.DBCommands;
import DBException.DBObjectDoesNotExistException;
import DBObjects.*;

import DBException.DatabaseException;

import java.util.Arrays;

public class Command extends DBObject {
    public String SQLCommand;
    String[] followingSQLCommands;
    protected int structureType;
    Database workingDatabase;

    public Database getWorkingDatabase() {
        return workingDatabase;
    }

    public void setWorkingDatabase(Database dbToSet){
        workingDatabase = dbToSet;
    }

    public Command(){ }

    public Command(String[] commandArray){
        SQLCommand = commandArray[0];
        followingSQLCommands = Arrays.copyOfRange(commandArray, 1, commandArray.length);
    }

    public static Command isValidCommand(String[] commandString){
        String commandType = commandString[0].toUpperCase();
        switch (commandType){
            case "CREATE":
                return new CreateCommand(commandString);
            case "USE":
                return new UseCommand(commandString);
            case "DROP":
                return new DropCommand(commandString);


            default:
                return null;
        }
    }

    public boolean processCommand(Database currentDB) {
        workingDatabase = currentDB;
        try {
            parseCommand();
            interpretCommand();
        }
        catch (DatabaseException dbe){
            System.out.println("We couldn't parse the message...");
            dbe.printStackTrace();
            return false;
        }
        //does this need to be boolean??
        return true;
    }

    public void parseCommand() throws DatabaseException {
        if (!commandHasArguments()){
            throw new DatabaseException(this, null);
        }
    }
    public void interpretCommand() throws DatabaseException{}

    public boolean determinedStructureType(String specifiedType, Database currentDB) throws DatabaseException{
        switch (specifiedType){
            case "TABLE":
                if (currentDB == null){
                    throw new DBObjectDoesNotExistException();
                }
                structureType = 1;
                break;
            case "DATABASE":
                structureType = 0;
                break;
            default:
                System.out.println("Invalid create command parameters.");
                return false;
        }
        return true;
    }

    //maybe can delete this if we're checking in statement
    public boolean doesCommandTerminate(){
        if (followingSQLCommands[followingSQLCommands.length - 1] != ";") {
            System.out.println("The create command doesn't end in a semicolon.");
            return false;
        }
        //remove semicolon
        followingSQLCommands = Arrays.copyOfRange(followingSQLCommands, 0, followingSQLCommands.length - 1);
        return true;
    }

    public boolean commandHasArguments(){
        if (followingSQLCommands.length == 0){
            System.out.println("We should throw an error here because the following commands didn't exist.");
            return false;
        }
        return true;
    }
}
