package DBObjects;

public class TableAttribute extends DBObject {
    public TableAttribute(String attributeName){
        super(attributeName);
    }
    public boolean equals(TableAttribute attributeToCheck){
        return attributeToCheck.objectName.equals(this.objectName);
    }
}
