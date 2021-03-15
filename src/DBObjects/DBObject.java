package DBObjects;

public class DBObject {
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
            System.out.println("the name doesn't contain non-alphanums");
            return true;
        } else {
            System.out.println("the name contained alphanums");
            return false;
        }
    }

    public void createObject(){
        System.out.println("we're trying to create the parent DBObject");
    }

    public boolean dbObjectExists(){
        return false;
    }
}
