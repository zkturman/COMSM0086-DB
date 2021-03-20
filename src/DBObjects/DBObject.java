package DBObjects;

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
        if (objectName.matches(".*[a-zA-Z0-9]")) {
            return true;
        } else {
            return false;
        }
    }

    public void createObject(){
        System.out.println("we're trying to create the parent DBObject");
    }
    public void dropObject() { System.out.println("we're trying to drop the parent DBObject");}

    public boolean dbObjectExists(){
        return false;
    }
}
