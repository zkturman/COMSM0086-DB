package DBObjects.DBCommands;

import DBException.DatabaseException;
import DBException.InvalidCommandArgumentException;
import DBException.NotUsingDatabaseExeception;
import DBObjects.*;

import java.util.Arrays;

public class DropCommand extends DropCreateCommand {
    DBObject objectToDrop;

    public DropCommand(String[] dropArgs){
        super(dropArgs);
    }

    public void parseCommand() throws DatabaseException {
        super.parseCommand();

        objectToDrop = initDBObject(structureType, followingSQLCommands[1]);
    }

    public void evaluateStructureArgs(int structureType, String[] stringToProcess) throws DatabaseException{
        super.evaluateStructureArgs(structureType, stringToProcess);

        if (stringToProcess.length > 1){
            throw new InvalidCommandArgumentException(); //message --> too many arguments given
        }
    }

    public void interpretCommand() throws DatabaseException{
        objectToDrop.dropObject();
    }
}
