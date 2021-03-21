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
        return objectName.matches(".+[a-zA-Z0-9]");
    }
    public void createObject(){
        System.out.println("we're trying to create the parent DBObject");
    }
    public void dropObject() { System.out.println("we're trying to drop the parent DBObject");}
    public boolean dbObjectExists(){
        return false;
    }
}
