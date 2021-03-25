package DBObjects;

import DBException.DBException;

public abstract class DBObject {
    String objectName;

    public String getObjectName() {
        return objectName;
    }

    public DBObject(){}
    public DBObject(String objectName){
        this.objectName = objectName;
    }
    public boolean isNameValid(String objectName) {
        return objectName.matches(".+[a-zA-Z0-9]");
    }
    public void createObject() throws DBException {}
    public void dropObject() throws  DBException {}
    public boolean dbObjectExists(){return false;}
}
