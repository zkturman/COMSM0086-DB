package DBObjects.DBCommands;

import DBException.DatabaseException;
import DBException.InvalidCommandArgumentException;
import DBException.NotUsingDatabaseExeception;
import DBObjects.DBObject;
import DBObjects.DBTable;
import DBObjects.Database;

import javax.xml.crypto.Data;
import java.util.Arrays;

public class DropCreateCommand extends Command {


    public DropCreateCommand(String[] createArgs) {
        super(createArgs);
    }

    public void parseCommand() throws DatabaseException {
        if (!commandHasArguments()){
            throw new DatabaseException(this, null);
        }
        String specifiedStructure = followingSQLCommands[0].toUpperCase();
        if (!determinedStructureType(specifiedStructure, workingDatabase)) {
            throw new InvalidCommandArgumentException();
        }
        String[] argumentList = Arrays.copyOfRange(followingSQLCommands, 1, followingSQLCommands.length);
        evaluateStructureArgs(structureType, argumentList);
    }

    public void interpretCommand() throws DatabaseException{};

    public void evaluateStructureArgs(StructureType type, String[] stringToProcess) throws DatabaseException {
        if (stringToProcess.length == 0) {
            throw new InvalidCommandArgumentException(); //message --> no object name given
        }

        if (!isNameValid(stringToProcess[0])) {
            throw new InvalidCommandArgumentException(); //message --> name contains special chars
        }

    }

    public DBObject initDBObject(StructureType type, String objectName) throws DatabaseException {
        if (type == StructureType.DATABASE){
            return new Database(objectName);
        }
        else if (type == StructureType.TABLE){
            if (workingDatabase == null){
                throw new NotUsingDatabaseExeception();
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
}
