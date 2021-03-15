package DBObjects;
import java.io.*;
import java.util.List;

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

    public void dropObject(){
        System.out.println("we're trying to drop a database");
        File dbToDrop = new File(objectName);
        File[] tablesToDrop = dbToDrop.listFiles();
        try{
            for(int i = 0; i < tablesToDrop.length; i++){
                if (!tablesToDrop[i].delete()){
                    System.out.println("Failed to drop database tables");
                }
            }
            if (!dbToDrop.delete()){
                System.out.println("Failed to drop database");
            }
        }
        catch (SecurityException se){
            se.printStackTrace();
        }
    }

    public boolean dbObjectExists(){
        File databaseFolder = new File(objectName);
        return (databaseFolder.exists() && databaseFolder.isDirectory());
    }
}
