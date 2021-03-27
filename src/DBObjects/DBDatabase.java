/**
 *
 */

package DBObjects;

import DBException.*;
import java.io.*;

public class DBDatabase extends DBObject{
    public DBDatabase(String databaseName){
        super(databaseName);
    }

    public void createObject() throws DBException {
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
        if (tablesToDrop != null){
            deleteDirectory(tablesToDrop);
        }
        deleteFile(dbToDrop);
    }

    public boolean dbObjectExists(){
        File databaseFolder = new File(objectName);
        return (databaseFolder.exists() && databaseFolder.isDirectory());
    }

    private void deleteDirectory(File[] directoryToDelete) throws DBException {
        for (File file : directoryToDelete) {
            deleteFile(file);
        }
    }

    private void deleteFile(File fileToDelete) throws DBException {
        try {
            if (!fileToDelete.delete()) {
                throw new DBServerException("Failed to database object.");
            }
        }
       catch (SecurityException se){
            throw new DBServerException("Failed to delete database.");
        }
    }
}
