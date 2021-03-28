/**
 * Table attribute class. Contains information about a tables attributes.
 */

package DBObjects;

public class TableAttribute extends DBObject implements DBTableObject {

    /**
     * Constructs a table attribute with a given name.
     * @param attributeName Name of the attribute.
     */
    public TableAttribute(String attributeName){
        super(attributeName);
    }

    /**
     * Determines if two attributes are equal.
     * @param attributeToCheck Attribute to compare against current attribute.
     * @return Return true if two attributes have the same name.
     */
    public boolean equals(TableAttribute attributeToCheck){
        return attributeToCheck.objectName.equals(this.objectName);
    }

    @Override
    /**
     * Returns the name of an attribute.
     */
    public String toString() {
        return objectName;
    }
}
