package DBObjects;

import DBException.*;
import DBObjects.DBCommands.DBCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void performStatement(String commandString) throws DBException {
        commandString = removeSemicolon(commandString);
        commandToProcess = separateLists(commandString);
        String firstToken = getFirstToken(commandToProcess[0]);
        if (!DBCommand.isValidCommand(firstToken)){
            throw new DBInvalidCommandException("An invalid command was entered: " + firstToken);
        }
        DBCommand sqlDBCommand = DBCommand.generateCommand(firstToken, commandToProcess);
        sqlDBCommand.processCommand(workingDatabase);
        workingDatabase = sqlDBCommand.getWorkingDatabase();
    }

    private String getFirstToken(String mainCommand){
        Pattern tokenPattern = Pattern.compile("\\s*[a-zA-Z0-9]+(\\s+|\\*)", Pattern.CASE_INSENSITIVE);
        Matcher tokenMatcher = tokenPattern.matcher(mainCommand);
        tokenMatcher.find();
        String commandName = tokenMatcher.group();
        System.out.println(commandName);
        commandName = commandName.replaceAll("\\s", "");
        System.out.println(commandName);
        //return mainCommand.split("(\\s+|\\s*\\*)")[0];
        return commandName;
    }

    private String removeSemicolon(String statement) throws DBException {
        if (statement.charAt(statement.length() - 1) != ';'){
            throw new DBNonTerminatingException("String did not end with a semicolon.");
        }
        return statement.substring(0, statement.length() - 1);
    }

    private String[] separateLists(String statement){
        return statement.split("(?=\\()", 2);
    }

    public static void test(){
        String statement1 = "create new table test1;", statement2 = "insert into test1 values ('ab', 1234);";
        DBStatement test1 = new DBStatement(null);
        try {
            assert test1.removeSemicolon(statement1).equals("create new table test1");
            statement1 = test1.removeSemicolon(statement1);
            assert test1.removeSemicolon(statement2).equals("insert into test1 values ('ab', 1234)");
            statement2 = test1.removeSemicolon(statement2);
            assert test1.separateLists(statement1).length == 1;
            assert test1.separateLists(statement2).length == 2;
            assert test1.getFirstToken(statement1).equals("create");
            assert test1.getFirstToken(statement2).equals("insert");
        }
        catch (DBException de){

        }
        System.out.println("DBStatement passed.");
    }

}
