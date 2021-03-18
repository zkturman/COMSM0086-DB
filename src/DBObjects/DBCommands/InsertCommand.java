package DBObjects.DBCommands;

import DBException.DatabaseException;
import DBException.InvalidCommandArgumentException;
import DBObjects.DBCommands.CommandLists.ValueList;
import DBObjects.DBTable;

import java.util.Arrays;
import java.util.Locale;

public class InsertCommand extends Command {

    DBTable tableToInsert;
    ValueList valuesToInsert;

    public InsertCommand(String[] insertArgs){
        super(insertArgs);
    }

    public void parseCommand() throws DatabaseException {
        if (!commandHasArguments()){
            throw new InvalidCommandArgumentException();
        }
        String intoString = followingSQLCommands[0].toUpperCase(Locale.ROOT);
        if (!intoString.equals("INTO")){
            throw new InvalidCommandArgumentException();
        }
        String[] argumentList = Arrays.copyOfRange(followingSQLCommands, 1, followingSQLCommands.length);
        evaluateStructureArgs(argumentList);
    }

    public void evaluateStructureArgs(String[] stringToProcess)throws DatabaseException{
        if (stringToProcess.length == 0){
            throw new InvalidCommandArgumentException(); //message --> no table name given
        }
        if (!isNameValid(stringToProcess[0])){
            throw new InvalidCommandArgumentException(); //message --> invalid tablename
        }
        tableToInsert = new DBTable(stringToProcess[0]);
        tableToInsert.setOwningDatabase(workingDatabase);
        tableToInsert.setTableFilePaths();
        if (stringToProcess.length <= 1){
            throw new InvalidCommandArgumentException(); //message --> no VALUES string
        }
        stringToProcess = Arrays.copyOfRange(stringToProcess, 1, stringToProcess.length);
        if (!stringToProcess[0].equals("VALUES")){
            throw new InvalidCommandArgumentException(); //message --> expected VALUES string
        }
        if (stringToProcess.length <= 1){
            throw new InvalidCommandArgumentException(); //
        }
        stringToProcess = Arrays.copyOfRange(stringToProcess, 1, stringToProcess.length);
        processInsertValues(stringToProcess);
    }

    public void processInsertValues(String[] valueList) throws DatabaseException{
        valuesToInsert = new ValueList(valueList);
        if (valuesToInsert.parseList()){
            valuesToInsert.convertStringToList();
        }
    }

    public void interpretCommand() throws DatabaseException {
        tableToInsert.insertValues();
    }
}
