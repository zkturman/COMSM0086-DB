package DBObjects;

import DBException.*;

/**
 * Database object parent class. Contains the default constructor for all database objects,
 * and validates the name of all database objects.
 */
public class DBObject {
    protected String objectName;

    /**
     *Instantiates a database object with the given name.
     * @param objectName Name of the database object.
     */
    public DBObject(String objectName){
        this.objectName = objectName;
    }

    /**
     * Default constructor for a DBObject. Does not instantiate the object name.
     */
    protected DBObject(){}

    /**
     * Returns the name of the database object.
     * @return Returns the name of a database object.
     */
    public String getObjectName() {
        return objectName;
    }


    /**
     * Verifies database object names are alphanumeric.
     * @param objectName Name of the object to check.
     * @return Returns true if the object name is alphanumeric.
     */
    public static boolean isNameValid(String objectName) {
        return objectName.matches(".+[a-zA-Z0-9]");
    }

    /**
     * Creates a database object.
     * @throws DBException Thrown in override classes.
     */
    public void createObject() throws DBException {}

    /**
     * Deletes a database object.
     * @throws DBException Thrown in override classes.
     */
    public void dropObject() throws  DBException {}
}
