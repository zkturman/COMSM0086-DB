package DBObjects.DBCommands;

import DBException.DatabaseException;
import DBException.InvalidCommandArgumentException;
import DBObjects.*;

import java.util.Arrays;
import java.util.Locale;

public class AlterCommand extends Command {
    DBTable tableToAlter;
    TableAttribute attributeToAlter;
    AlterType alterType;
    public AlterCommand(String[] alterArgs){
        super(alterArgs);
    }

    public void parseCommand() throws DatabaseException{
        if (!commandHasArguments()){
            throw new DatabaseException(this, null);
        }
        String specifiedStructure = followingSQLCommands[0].toUpperCase();
        if (!specifiedStructure.equals("TABLE")){
            throw new InvalidCommandArgumentException();
        }
        String[] argumentList = Arrays.copyOfRange(followingSQLCommands, 1, followingSQLCommands.length);
        evaluateStructureArgs(1, argumentList);
    }

    public void evaluateStructureArgs(int structureType, String[] stringToProcess) throws DatabaseException{

        if (stringToProcess.length == 0) {
            throw new InvalidCommandArgumentException(); //message --> no table name given
        }
        if (!isNameValid(stringToProcess[0])) {
            throw new InvalidCommandArgumentException(); //message --> table name contains special chars
        }
        tableToAlter = new DBTable(stringToProcess[0]);
        tableToAlter.setOwningDatabase(workingDatabase);
        tableToAlter.setTableFilePaths();
        if (stringToProcess.length <= 1){
            throw new InvalidCommandArgumentException(); //message --> no alteration type
        }
        stringToProcess = Arrays.copyOfRange(stringToProcess, 1, stringToProcess.length);
        if (!convertStringToAlterType(stringToProcess[0])){
            throw new InvalidCommandArgumentException(); //message --> alteration type is incorrect
        }
        if (stringToProcess.length <= 1){
            throw new InvalidCommandArgumentException(); //message --> no attribute name was given
        }
        stringToProcess = Arrays.copyOfRange(stringToProcess, 1, stringToProcess.length);
        if (!isNameValid(stringToProcess[0])){
            throw new InvalidCommandArgumentException(); //message --> attribute name was invalid
        }
        attributeToAlter = new TableAttribute(stringToProcess[0]);
    }
    public void interpretCommand() throws DatabaseException {
        if (alterType == AlterType.ADD){
            tableToAlter.appendAttribute(attributeToAlter);
        }
        else if (alterType == AlterType.DROP){
            tableToAlter.removeAttribute(attributeToAlter);
        }
        else {
            throw new DatabaseException(); //message --> invalid alter type
        }
    }

    public boolean convertStringToAlterType(String alterString){
        switch(alterString.toUpperCase()){
            case "ADD":
                alterType = AlterType.ADD;
                return true;
            case "DROP":
                alterType = AlterType.DROP;
                return true;
            default:
                return false;
        }
    }
}
