package DBObjects.DBCommands.CommandLists;

import DBException.DBException;
import DBObjects.DBCommands.CommandLists.ValueList;

public class NameValueList extends ValueList {
    public NameValueList(String listString) throws DBException {
        super(listString);
    }
    public boolean parseList(){return true;};
}
