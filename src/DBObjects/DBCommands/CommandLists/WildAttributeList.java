package DBObjects.DBCommands.CommandLists;

import DBException.DBException;

public class WildAttributeList extends AttributeList{
    public WildAttributeList(String listString) throws DBException {
        super(listString);
    }
    public boolean parseList(){return true;};
}
