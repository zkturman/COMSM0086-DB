package DBException;
import DBObjects.DBCommands.*;
import DBObjects.*;

public class DBException extends Exception{

   String errorMessage;

    public DBException()
    {
    }

    public DBException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String toString(){
        return errorMessage;
    }

    //Database object already exits
    //
    //
}
