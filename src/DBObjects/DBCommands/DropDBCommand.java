package DBObjects.DBCommands;

import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBObjects.*;

public class DropDBCommand extends DropCreateDBCommand {
    DBObject objectToDrop;

    public DropDBCommand(String[] dropArgs){
        super(dropArgs);
    }

    public void prepareCommand() throws DBException {
        super.prepareCommand();

        objectToDrop = initDBObject(structureType, followingSQLCommands[1]);
    }

    public void evaluateStructureArgs(StructureType structureType, String[] stringToProcess) throws DBException {
        super.evaluateStructureArgs(structureType, stringToProcess);

        if (stringToProcess.length > 1){
            throw new InvalidCommandArgumentException(); //message --> too many arguments given
        }
    }

    public void executeCommand() throws DBException {
        objectToDrop.dropObject();
    }
}
