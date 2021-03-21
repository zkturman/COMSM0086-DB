package DBObjects;

public class TableAttribute extends DBObject implements DBTableObject {
    public TableAttribute(String attributeName){
        super(attributeName);
    }
    public boolean equals(TableAttribute attributeToCheck){
        return attributeToCheck.objectName.equals(this.objectName);
    }

    @Override
    public String toString() {
        return objectName;
    }
}
