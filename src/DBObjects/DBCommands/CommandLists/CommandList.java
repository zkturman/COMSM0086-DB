package DBObjects.DBCommands.CommandLists;

import DBException.DatabaseException;
import DBObjects.DBObject;
import DBObjects.Database;

public abstract class CommandList extends DBObject {
    protected String[] listArgs;
    public abstract boolean parseList() throws DatabaseException;
    public abstract void convertStringToList() throws DatabaseException;

}
