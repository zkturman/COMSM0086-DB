package DBObjects;

import DBObjects.DBCommands.Command;

public class DBStatement {
    Database workingDatabase = null;
    public String[] commandToProcess;

    public Database getWorkingDatabase() {
        return workingDatabase;
    }

    public void setWorkingDatabase(Database workingDatabase) {
        this.workingDatabase = workingDatabase;
    }

    public DBStatement(Database workingDatabase){
        this.workingDatabase = workingDatabase;
    }

    public void performStatement(String commandString){
        if (commandString.charAt(commandString.length() - 1) != ';'){
            System.out.println("the argument didn't terminate in a semicolon");
            return;
        }
        commandString = commandString.substring(0, commandString.length() - 1);
        commandToProcess = commandString.split(" ");
        for (int i = 0; i < commandToProcess.length; i++){
            System.out.println(commandToProcess[i]);
        }
        Command sqlCommand = Command.isValidCommand(commandToProcess);
        if (sqlCommand != null){
            System.out.println("this was a good command: " + sqlCommand.SQLCommand);
            sqlCommand.processCommand(workingDatabase);
            workingDatabase = sqlCommand.getWorkingDatabase();
        }
        else {
            System.out.println("invalid command was used");
        }
    }

}
