package DBObjects.DBCommands.CommandLists;

import DBObjects.DBCommands.CommandLists.ValueList;

public class NameValueList extends ValueList {
    public NameValueList(String[] listArgs){
        super(listArgs);
    }
    public boolean parseList(){return true;};
}
