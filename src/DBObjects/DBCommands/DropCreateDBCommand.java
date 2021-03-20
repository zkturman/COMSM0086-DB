package DBObjects.DBCommands;

import DBException.DBException;
import DBException.InvalidCommandArgumentException;
import DBException.NotUsingDBExeception;
import DBObjects.DBObject;
import DBObjects.DBTable;
import DBObjects.Database;

import java.util.Arrays;

public class DropCreateDBCommand extends DBCommand {


    public DropCreateDBCommand(String[] createArgs) {
        super(createArgs);
    }

    public void prepareCommand() throws DBException {
        if (!commandHasArguments()){
            throw new DBException(this, null);
        }
        String specifiedStructure = followingSQLCommands[0].toUpperCase();
        if (!determinedStructureType(specifiedStructure, workingDatabase)) {
            throw new InvalidCommandArgumentException();
        }
        String[] argumentList = Arrays.copyOfRange(followingSQLCommands, 1, followingSQLCommands.length);
        evaluateStructureArgs(structureType, argumentList);
    }

    public void executeCommand() throws DBException {};

    public void evaluateStructureArgs(StructureType type, String[] stringToProcess) throws DBException {
        if (stringToProcess.length == 0) {
            throw new InvalidCommandArgumentException(); //message --> no object name given
        }

        if (!isNameValid(stringToProcess[0])) {
            throw new InvalidCommandArgumentException(); //message --> name contains special chars
        }

    }

    public DBObject initDBObject(StructureType type, String objectName) throws DBException {
        if (type == StructureType.DATABASE){
            return new Database(objectName);
        }
        else if (type == StructureType.TABLE){
            if (workingDatabase == null){
                throw new NotUsingDBExeception();
            }
            DBTable tableToDrop = new DBTable(objectName);
            tableToDrop.setOwningDatabase(workingDatabase);
            tableToDrop.setTableFilePaths();
            return tableToDrop;
        }
        else {
            throw new InvalidCommandArgumentException();
        }
    }

    public String[] splitCommand(String commandString) throws DBException {
        return new String[0];
    }
}
