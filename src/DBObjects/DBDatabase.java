package DBObjects;
import DBException.DBException;
import DBException.DBServerException;

import java.io.*;
import java.nio.file.Files;

public class DBDatabase extends DBObject{
    public DBDatabase(String databaseName){
        super(databaseName);
    }

    public void createObject() throws DBException {
        //try here for nullpointerexecption
        File dbToCreate = new File(objectName);
        try {
            if (!dbToCreate.mkdir()) {
                throw new DBServerException("Failed to create database");
            }
        }
        catch(SecurityException se){
            se.printStackTrace();
        }
    }

    public void dropObject() throws DBException {
        File dbToDrop = new File(objectName);
        File[] tablesToDrop = dbToDrop.listFiles();
        try{
            if (tablesToDrop != null) {
                for (int i = 0; i < tablesToDrop.length; i++) {
                    if (!tablesToDrop[i].delete()) {
                        throw new IOException();
                        //throw new DBServerException("Failed to drop table in database.");
                    }
                }
            }
            if (!dbToDrop.delete()){
                throw new DBServerException("Failed to delete database.");
            }
        }
        catch (SecurityException se){
            throw new DBServerException("Failed to delete database.");
        }
        catch (IOException ioe){
            ioe.getCause();
            ioe.printStackTrace();
        }
    }

    public boolean dbObjectExists(){
        File databaseFolder = new File(objectName);
        return (databaseFolder.exists() && databaseFolder.isDirectory());
    }
}
