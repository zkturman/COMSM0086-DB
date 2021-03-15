package DBObjects;
import java.io.*;

public class Database extends DBObject{
    public Database(String databaseName){
        super(databaseName);
    }

    public void createObject(){
        System.out.println("we're trying to create a database");
        //try here for nullpointerexecption
        File dbToCreate = new File(objectName);
        try {
            if (!dbToCreate.mkdir()) {
                System.out.println("Failed to create database");
            }
        }
        catch(SecurityException se){
            se.printStackTrace();
        }
    }

    public boolean dbObjectExists(){
        File databaseFolder = new File(objectName);
        return (databaseFolder.exists() && databaseFolder.isDirectory());
    }
}
