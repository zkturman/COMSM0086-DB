package DBObjects;


import DBException.DBException;
import DBException.DBOutOfRangeException;

import java.util.ArrayList;
import java.util.Arrays;

public class TableRow extends DBObject implements DBTableObject {
    public ArrayList<String> rowData;

    public TableRow(String[] rowData){
        this.rowData = new ArrayList<>(Arrays.asList(rowData));
    }

    public TableRow(String tabbedRowData){
        String[] rowAry = tabbedRowData.split("\\t");
        this.rowData = new ArrayList<>(Arrays.asList(rowAry));
    }

    public String toString(){
        String returnString = "";
        for(String row : rowData){
            returnString += row + "\t";
        }
        return returnString;
    }

    public String printValue(int index){
        return rowData.get(index) + "\t";
    }

    public int getSize(){
        return rowData.size();
    }

    public String getValue(int index){
        return rowData.get(index);
    }

    public void addIdValue(int idValue){
        String idString = String.valueOf(idValue);
        rowData.add(0, idString);
    }

    public void appendCell(){
        rowData.add("\'\'");
    }

    public void appendRow(TableRow rowToAppend){
        for (int i = 0; i < rowToAppend.getSize(); i++){
            rowData.add(rowToAppend.getValue(i));
        }
    }

    public void removeValue(int index){
        rowData.remove(index);
    }
    public void updateValue(String value, int index) throws DBException {
        if (index >= rowData.size()){
            throw new DBOutOfRangeException("Attempted to edit a cell outside the range of data.");
        }
        rowData.set(index, value);
    }
}
