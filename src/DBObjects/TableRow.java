package DBObjects;


import java.util.ArrayList;
import java.util.Arrays;

public class TableRow {
    public ArrayList<String> rowData;

    public TableRow(String[] rowData){
        this.rowData = new ArrayList<>(Arrays.asList(rowData));
    }
}
