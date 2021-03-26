/**
 * @(#)
 */

package DBObjects;

import DBException.*;
import DBObjects.DBCommands.CommandLists.NameValueList;
import java.io.*;
import java.util.ArrayList;

public class DBTable extends DBObject implements DBTableObject {
    private String attributePath;
    private String tablePath;
    private DBDatabase owningDatabase;
    private ArrayList<TableAttribute> tableAttributes;
    private ArrayList<TableRow> tableRows;
    private TableAttribute joinAttribute;

    public DBTable(String tableName, DBDatabase owningDatabase){
        super(tableName);
        this.owningDatabase = owningDatabase;
        setTableFilePaths();
        tableAttributes = new ArrayList<>();
        tableAttributes.add(new TableAttribute("id"));
        tableRows = new ArrayList<>();
    }

    protected DBTable(String tableName){
        super(tableName);
        tableAttributes = new ArrayList<>();
        tableAttributes.add(new TableAttribute("id"));
        tableRows = new ArrayList<>();
    }

    private void setTableFilePaths() {
        attributePath = createPath("txt");
        tablePath = createPath("tsv");
    }



    //used in command / externally
    public TableRow getTableRow(int index){
        return tableRows.get(index);
    }
    public void removeTableRow(int index){
        tableRows.remove(index);
    }
    public int getNumRows(){
        return tableRows.size();
    }
    public ArrayList<TableRow> getTableRows() {
        return tableRows;
    }

    public int getAttributeIndex(String attributeName) throws DBException{
        for (int i = 0; i < tableAttributes.size(); i++){
            if (tableAttributes.get(i).getObjectName().equals(attributeName)){
                return i;
            }
        }
        throw new DBObjectDoesNotExistException("Could not find attribute in table.");
    }
    public void setTableAttributes(ArrayList<TableAttribute> tableAttributes) throws DBException{
        this.tableAttributes.addAll(tableAttributes);
        defineFileData(attributePath, this.tableAttributes);
    }
    public ArrayList<TableAttribute> getTableAttributes() {
        return tableAttributes;
    }
    public void setJoinAttribute(TableAttribute joinAttribute) throws DBException {
        loadAttributeFile();
        boolean found = false;
        for (TableAttribute attribute : tableAttributes){
            if (attribute.equals(joinAttribute)){
                found = true;
            }
        }
        if (!found){
            throw new DBObjectDoesNotExistException("Could not find attribute to join in table.");
        }
        this.joinAttribute = joinAttribute;
    }
    public TableAttribute getJoinAttribute() {
        return joinAttribute;
    }




    /**
     * Creates a new persistent table in the working database.
     * @throws DBException Thrown if the file cannot be newly created.
     */
    public void createObject() throws DBException {
        this.createNewFile(tablePath);
        this.createNewFile(attributePath);
    }

    /**
     * Creates a new file with a given filename in the current database's directly.
     * @param fileName Filename to create.
     * @throws DBException Thrown if the file already exists or the file couldn't be created.
     */
    private void createNewFile(String fileName) throws DBException {
        File fileToCreate = new File(fileName);
        if (fileToCreate.exists()){
            throw new DBServerException("Table already exists.");
        }
        try {
            if (!fileToCreate.createNewFile()){
                throw new DBServerException("Server not ableto create new file.");
            }
        }
        catch (IOException ioe){
            throw new DBServerException("Server not able to create new file.");
        }
    }

    /**
     * Deletes the table from the current directory.
     * @throws DBException Thrown if table files couldn't be deleted.
     */
    public void dropObject() throws DBException {
        File tableToDrop = new File(tablePath);
        File attributesToDrop = new File(attributePath);
        try{
            if (!tableToDrop.delete()){
                throw new DBServerException("Could not delete table. Does it exist?");
            }
            if (!attributesToDrop.delete()){
                throw new DBServerException("Could not delete attribute definitions. Does the table exist?");
            }
        }
        catch (SecurityException se){
            se.printStackTrace();
        }
    }



    public void appendAttribute(TableAttribute attributeToAppend) throws DBException {
        loadAttributeFile();
        tableAttributes.add(attributeToAppend);
        defineFileData(attributePath, tableAttributes);
        for (TableRow row : tableRows){
            row.appendCell();
        }
        defineFileData(tablePath, tableRows);
    };

    public void removeAttribute(TableAttribute attributeToRemove) throws DBException {
        loadAttributeFile();
        int i;
        boolean foundColumn = false;
        for (i = 0; i < tableAttributes.size(); i++){
            if (tableAttributes.get(i).equals(attributeToRemove)){
                tableAttributes.remove(i);
                foundColumn = true;
                for (TableRow row : tableRows){
                    row.removeValue(i);
                }
            }
        }
        if (!foundColumn){
            throw new DBInvalidObjectName("Attribute with this name not present.");
        }
        defineFileData(attributePath, tableAttributes);
        defineFileData(tablePath, tableRows);
    }

    /**
     *
     * @param newRow
     * @throws DBException
     */
    public void insertTableRow(TableRow newRow) throws DBException {
        newRow.addIdValue(tableRows.size() + 1);
        if (newRow.getSize() != getNumAttributes()){
            throw new DBInvalidValueWidthException("Number of values didn't match number of attributes.");
        }
        tableRows.add(newRow);
        defineFileData(tablePath, tableRows);
    }

    /**
     * Returns the number of attributes stored for the table.
     * @return Number of attributes in the table.
     * @throws DBException Thrown if attributes are not able to be loaded.
     */
    private int getNumAttributes() throws DBException{
        loadAttributeFile();
        return tableAttributes.size();
    }

    /**
     * Updates rows of a table with new values.
     * @param updateNameValues List of attributes with new columns to update.
     * @throws DBException Thrown if unable to load or write table data.
     */
    public void updateTable(NameValueList updateNameValues) throws DBException {
        ArrayList<TableRow> rowUpdates = new ArrayList<>(tableRows);
        loadTableFile();
        ArrayList<TableAttribute> attributesToUpdate = updateNameValues.getAttributesToChange();
        ArrayList<String> valuesForUpdates = updateNameValues.getValuesForChange();

        for (int i = 0; i < attributesToUpdate.size(); i++){
            TableAttribute attribute = attributesToUpdate.get(i);
            int attributeIndex = getAttributeIndex(attribute.getObjectName());
            String valueToUse = valuesForUpdates.get(i);
            for (TableRow row : rowUpdates){
                row.updateValue(valueToUse, attributeIndex);
            }
        }
        for (int i = 0; i < tableRows.size(); i++){
            for (TableRow updatedRow : rowUpdates){
                if (tableRows.get(i).getValue(0).equals(updatedRow.getValue(0))){
                    tableRows.set(i, updatedRow);
                }
            }
        }
        defineFileData(tablePath, tableRows);
    }

    /**
     * Removes rows that are current stored in a database file.
     * @throws DBException Thrown if table cannot be loaded or written.
     */
    public void deleteRows() throws DBException {
        ArrayList<TableRow> rowUpdates = new ArrayList<>(tableRows);
        loadTableFile();
        for (int i = 0; i < tableRows.size(); i++){
            String rowId = tableRows.get(i).getValue(0);
            for (TableRow updateRow : rowUpdates){
                if (rowId.equals(updateRow.getValue(0))){
                    tableRows.remove(i);
                    i--;
                }
            }
        }
        defineFileData(tablePath, tableRows);
    }

     /**
     * Returns a new DBTable object that is an inner join of two tables.
     * This table includes all rows where a primary key from one table
     * matched the secondary key of another. The two tables must have configured
     * joining attributes.
     * @param primaryTable Table on which to join.
     * @param secondaryTable Table by which to join.
     * @return DBTable object with row and attribute information from the two tables.
     * This object is not capable of storing to memory.
     * @throws DBException Thrown if joining attributes are non-existent in their tables.
     */
    public static DBTable joinTables (DBTable primaryTable, DBTable secondaryTable) throws DBException {
        DBTable jointTable = new DBTable("JointTable");
        jointTable.populateJointAttributes(primaryTable);
        jointTable.populateJointAttributes(secondaryTable);
        jointTable.populateJointTableRows(primaryTable, secondaryTable);
        return jointTable;
    }

    /**
     * Appends attributes from a table into the current table. This does not include attributes named 'id.'
     * @param joiningTable Table from which to get attributes.
     */
    private void populateJointAttributes(DBTable joiningTable) {
        ArrayList<TableAttribute> joiningAttributes = joiningTable.getTableAttributes();

        TableAttribute joinAttribute;
        String jointAttributeName, attributeName;

        for (TableAttribute attribute : joiningAttributes){
            attributeName = attribute.getObjectName();
            if (!attributeName.equals("id")) {
                jointAttributeName = joiningTable.getObjectName() + "." + attributeName;
                joinAttribute = new TableAttribute(jointAttributeName);
                tableAttributes.add(joinAttribute);
            }
        }
    }

    /**
     * Creates an inner join of two tables, joining all rows where the primary and secondary keys match.
     * @param primaryTable Primary table on which to join.
     * @param joiningTable Secondary table by which to join.
     * @throws DBException Thrown if the joining attribute does not exist.
     */
    private void populateJointTableRows(DBTable primaryTable, DBTable joiningTable) throws DBException {
        ArrayList<TableRow> primaryRows = primaryTable.getTableRows();
        ArrayList<TableRow> secondaryRows = joiningTable.getTableRows();

        TableAttribute primaryKey = primaryTable.getJoinAttribute();
        TableAttribute secondaryKey = joiningTable.getJoinAttribute();

        int primaryIndex = primaryTable.getAttributeIndex(primaryKey.getObjectName());
        int secondaryIndex = joiningTable.getAttributeIndex(secondaryKey.getObjectName());
        int jointIndex = 0;

        for (TableRow primaryRow : primaryRows) {
            String primaryValue = primaryRow.getValue(primaryIndex);
            for (TableRow secondaryRow : secondaryRows) {
                String secondaryValue = secondaryRow.getValue(secondaryIndex);
                if (primaryValue.equals(secondaryValue)) {
                    buildRows(primaryRow, jointIndex);
                    buildRows(secondaryRow, jointIndex);
                    jointIndex ++;
                }
            }
        }
    }

    /**
     * Builds a row by appending the contents of a given row to a specified row of the table.
     * @param rowToAdd Table row to append
     * @param index Index to append row.
     */
    private void buildRows(TableRow rowToAdd, int index){
        if (index == tableRows.size()){
            TableRow newRow = new TableRow();
            newRow.addIdValue(index + 1);
            tableRows.add(newRow);
        }
        TableRow partialRow = tableRows.get(index);
        //start at 1 to exclude row ids
        for (int i = 1; i < rowToAdd.getSize(); i++){
            partialRow.buildCell(rowToAdd.getValue(i));
        }
    }

    /**
     * Returns a string representing the data of a table for select attributes. String is
     * formatted for so it can be printed.
     * @param customAttributes A custom list of TableAttributes that can be used to limit
     *                         which attributes are used to build the string.
     * @return Returns a tab-delimited string of table information.
     * @throws DBException Thrown if table data could not be loaded or custom attributes
     * were missing.
     */
    public String printTable(ArrayList<TableAttribute> customAttributes) throws DBException {
        StringBuilder returnString = new StringBuilder();
        if (tableRows == null){
            loadTableFile();
        }
        if (tableAttributes.size() == 1){
            loadAttributeFile();
        }
        for (TableAttribute attribute : customAttributes){
            returnString.append(attribute.getObjectName()).append("\t");
        }
        returnString.append(System.lineSeparator());
        for (TableRow row : tableRows){
            for (TableAttribute customAttribute : customAttributes) {
                int attributeIndex = getAttributeIndex(customAttribute.getObjectName());
                returnString.append(row.printValue(attributeIndex));
            }
            returnString.append(System.lineSeparator());
        }
        return returnString.toString();
    }

    /**
     *Returns a string representing all attributes and data for a table. All attributes are
     * included in the order they are stored.
     * @return Returns a tab-delimited string of table information.
     * @throws DBException Thrown if table data could not be loaded.
     */
    public String printTable() throws DBException {
        if (tableAttributes.size() == 1){
            loadAttributeFile();
        }
        return printTable(tableAttributes);
    }

    /**
     * Writes table and attribute information based on the given filepath.
     * @param filepath Filepath to write data
     * @param dataToWrite List of items to write to file. List members must have a toString method.
     * @throws DBException Thrown if file does not exist or could not write to file.
     */
    private void defineFileData(String filepath, ArrayList<? extends DBObject> dataToWrite) throws DBException{
        File fileToDefine = new File(filepath);
        checkFileExits(fileToDefine);
        try {
            FileWriter writer = new FileWriter(fileToDefine);
            BufferedWriter buffWriter = new BufferedWriter(writer);
            for (DBObject dbObject : dataToWrite) {
                buffWriter.write(dbObject.toString());
                buffWriter.newLine();
                buffWriter.flush();
            }
            buffWriter.close();
        }
        catch (IOException ioe){
            throw new DBServerException("Internal error, unable to define attributes.");
        }
    }

    /**
     * Reloads all rows in a table that have been previously stored.
     * @throws DBException Thrown if the table does not exist or couldn't be loaded.
     */
    public void loadTableFile() throws DBException{
        tableRows.clear();
        File tableFile = new File(tablePath);
        checkFileExits(tableFile);
        try {
            FileReader reader = new FileReader((tablePath));
            BufferedReader buffReader = new BufferedReader(reader);
            String tableLine = buffReader.readLine();
            while(tableLine != null){
                tableRows.add(new TableRow(tableLine));
                tableLine = buffReader.readLine();
            }
            buffReader.close();
        }
        catch(IOException ioe){
            throw new DBServerException("Could not load table definitions.");
        }
    }

    /**
     * Reloads all attributes in a table that have been previously stored.
     * @throws DBException Thrown if the attributes do not exist or couldn't be loaded.
     */
    public void loadAttributeFile() throws DBException {
        tableAttributes.clear();
        File attributeFile = new File(attributePath);
        checkFileExits(attributeFile);
        try {
            FileReader reader = new FileReader((attributeFile));
            BufferedReader buffReader = new BufferedReader(reader);
            String attributeLine = buffReader.readLine();
            while(attributeLine != null){
                tableAttributes.add(new TableAttribute(attributeLine));
                attributeLine = buffReader.readLine();
            }
            buffReader.close();
        }
        catch(IOException ioe){
            throw new DBServerException("Could not load attribute data.");
        }
    }

    private void checkFileExits(File fileToCheck) throws DBServerException {
        if (!fileToCheck.exists()){
            throw new DBServerException("Could not find attribute definitions.");
        }
    }

    /**
     * Creates a filepath with a given extension based on the current database and table name.
     * @param extension The file extension for the file (e.g. txt). No decimal separator required.
     * @return Returns a string filepath.
     */
    private String createPath(String extension){
        return owningDatabase.getObjectName() + File.separator + objectName + "." + extension;
    }
}
