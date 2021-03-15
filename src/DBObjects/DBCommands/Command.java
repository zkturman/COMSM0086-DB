package DBObjects.DBCommands;
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


            default:
                return null;
        }
    }

    public boolean processCommand(Database currentDB) {
        return true;
    }

    public void parseCommand() throws DatabaseException {
        if (!commandHasArguments()){
            throw new DatabaseException(this, null);
        }
    }
    public void interpretCommand() throws DatabaseException{}

    public boolean determinedStructureType(String specifiedType, Database currentDB){
        switch (specifiedType){
            case "TABLE":
                if (currentDB == null){
                    System.out.println("no database is selected");
                    return false;
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
