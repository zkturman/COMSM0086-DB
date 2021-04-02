package DBObjects;

import DBException.*;
import java.io.*;

/**
 *Handles the creation and deletion of databases. Databases are implemented as folders.
 */
public class DBDatabase extends DBObject{

    /**
     * Creates a new database object. The database name will serve as the directory name.
     * @param databaseName Name of the new DBDatabase.
     * @throws DBException Thrown if root directory creation fails.
     */
    public DBDatabase(String databaseName) throws DBException {
        createRootDirectory();
        this.objectName = "DBRoot" + File.separator + databaseName;

    }

    /**
     * Creates root directory for all databases if directory doesn't exist.
     * @throws DBException Thrown if root directory creation fails.
     */
    private void createRootDirectory() throws DBException {
        File rootToCreate = new File("DBRoot");
        if (!rootToCreate.exists()){
            createDirectory(rootToCreate);
        }
    }

    /**
     * Creates a new database by creating a directory.
     * @throws DBException Thrown if creation fails.
     */
    public void createObject() throws DBException {
        File dbToCreate = new File(objectName);
        createDirectory(dbToCreate);
    }

    /**
     * Attempts to create a new directory for databases.
     * @param directoryToMake The directory to create.
     * @throws DBException Thrown if creation fails.
     */
    private void createDirectory(File directoryToMake) throws DBException {
        try {
            if (!directoryToMake.mkdir()) {
                throw new DBServerException("Failed to create directory.");
            }
        }
        catch(SecurityException se){
            throw new DBServerException("Failed to create directory.");
        }
    }

    /**
     * Deletes a database by removing all tables and the database itself.
     * If deletion fails, a partial deletion may have occurred.
     * @throws DBException Thrown if any objects are unable to be deleted.
     */
    public void dropObject() throws DBException {
        File dbToDrop = new File(objectName);
        File[] tablesToDrop = dbToDrop.listFiles();
        if (tablesToDrop != null){
            deleteDirectory(tablesToDrop);
        }
        deleteFile(dbToDrop);
    }

    /**
     * Checks if a database exists.
     * @return Returns true if the database exists and is a directory.
     */
    public boolean dbObjectExists(){
        File databaseFolder = new File(objectName);
        return (databaseFolder.exists() && databaseFolder.isDirectory());
    }

    /**
     * Deletes all files in a given directory.
     * @param directoryToDelete Deletes an array of File objects.
     * @throws DBException Thrown if a file is unable to be deleted.
     */
    private void deleteDirectory(File[] directoryToDelete) throws DBException {
        for (File file : directoryToDelete) {
            deleteFile(file);
        }
    }

    /**
     * Deletes a file from a database.
     * @param fileToDelete File that will  be deleted.
     * @throws DBException Thrown if the file is unable to be deleted.
     */
    private void deleteFile(File fileToDelete) throws DBException {
        try {
            if (!fileToDelete.delete()) {
                throw new DBServerException("Failed to delete database object.");
            }
        }
       catch (SecurityException se){
            throw new DBServerException("Failed to delete database object.");
        }
    }
}
