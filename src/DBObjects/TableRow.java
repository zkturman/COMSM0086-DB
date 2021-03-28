/**
 * TableRow class handles the creation, editing, and removal of
 * table rows from a table.
 */
package DBObjects;


import DBException.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TableRow extends DBObject implements DBTableObject {

    private ArrayList<String> rowData;

    /**
     * Constructor for a new row. Table rows do not have object names.
     */
    public TableRow(){
        rowData = new ArrayList<>();
    }

    /**
     * Constructor for a new row from an array of strings. Mainly used
     * when inserting values into a table.
     * @param rowData Array of values that represent row data.
     */
    public TableRow(String[] rowData){
        this.rowData = new ArrayList<>(Arrays.asList(rowData));
    }

    /**
     * Constructor for a row from a tab-delimited string of data. Used to
     * instantiate table rows from file.
     * @param tabbedRowData Tab-delimited string.
     */
    public TableRow(String tabbedRowData){
        String[] rowAry = tabbedRowData.split("\\t");
        this.rowData = new ArrayList<>(Arrays.asList(rowAry));
    }

    /**
     * Gets the size of a current table row.
     * @return Size of the current table row.
     */
    public int getSize(){
        return rowData.size();
    }

    /**
     * Return the specified value of a table row.
     * @param index Index to return.
     * @return Specified value of a table row.
     */
    public String getValue(int index){
        return rowData.get(index);
    }

    /**
     * Prepends an id to the table row.
     * @param idValue The ID of the table row.
     */
    public void addIdValue(int idValue){
        String idString = String.valueOf(idValue);
        rowData.add(0, idString);
    }

    /**
     * Appends a specified value to the end of a table row.
     * @param value Value to be appended to the table row.
     */
    public void appendCell(String value){
        rowData.add(value);
    }

    /**
     * Adds a new, null value at the end of the row.
     */
    public void appendCell(){
        rowData.add("''");
    }

    /**
     * Update a specified value in the table row.
     * @param value The value to add to the row.
     * @param index The index at which to add the value.
     * @throws DBException Thrown if index is greater than the number of values in the row.
     */
    public void updateValue(String value, int index) throws DBException {
        if (index >= rowData.size()){
            throw new DBOutOfRangeException("Attempted to edit a cell outside the range of data.");
        }
        rowData.set(index, value);
    }

    /**
     * Removes a value from a table row. Does not replace the value.
     * This is used when removing a column from the table.
     * @param index The index of the value to remove.
     */
    public void removeValue(int index){
        rowData.remove(index);
    }

    /**
     * Returns the value of a table row at the specified index.
     * @param index Index of the row to return.
     * @return Returns the specified value String from the row.
     */
    public String printValue(int index){
        return rowData.get(index) + "\t";
    }

    /**
     * Returns a table row as a tab-delimited string.
     * @return Tab-delimited string of row data.
     */
    @Override
    public String toString(){
        StringBuilder returnString = new StringBuilder();
        for(String row : rowData){
            returnString.append(row).append("\t");
        }
        return returnString.toString();
    }
}
