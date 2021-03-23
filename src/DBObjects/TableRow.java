package DBObjects;


import java.util.ArrayList;
import java.util.Arrays;

public class TableRow extends DBObject implements DBTableObject{
    public ArrayList<String> rowData;

    public TableRow(String[] rowData){
        this.rowData = new ArrayList<>(Arrays.asList(rowData));
    }

    public TableRow(String tabbedRowData){
        String[] rowAry = tabbedRowData.split("(\\t(?<!(\\t)\\t)|(?!(\\t)\\t)\\t)");
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

    public void addCell(){
        rowData.add("\t");
    }

    public void removeValue(int index){
        rowData.remove(index);
    }
}
