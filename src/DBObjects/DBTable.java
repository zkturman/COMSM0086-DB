package DBObjects;

import DBException.*;
import DBObjects.DBCommands.CommandLists.NameValueList;

import java.io.*;
import java.util.ArrayList;

public class DBTable extends DBObject implements DBTableObject {
    String attributePath;
    String tablePath;
    DBDatabase owningDatabase;
    private ArrayList<TableAttribute> tableAttributes;
    private ArrayList<TableRow> tableRows;
    private TableAttribute joinAttribute;

    public DBDatabase getOwningDatabase() {
        return owningDatabase;
    }

    public void setOwningDatabase(DBDatabase owningDatabase) {
        this.owningDatabase = owningDatabase;
    }

    public TableAttribute getJoinAttribute() {
        return joinAttribute;
    }

    public void setJoinAttribute(TableAttribute joinAttribute) throws DBException {
        tableAttributes.clear();
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

    public ArrayList<TableRow> getTableRows() {
        return tableRows;
    }

    public ArrayList<TableAttribute> getTableAttributes() {
        return tableAttributes;
    }

    public void setTableAttributes(ArrayList<TableAttribute> tableAttributes) throws DBException{
        this.tableAttributes.addAll(tableAttributes);
        defineFileData(attributePath, this.tableAttributes);
    }

    public void setTableFilePaths() {
        attributePath = createPath("txt");
        tablePath = createPath("tsv");
    }

    public DBTable(String tableName, DBDatabase owningDatabase){
        super(tableName);
        this.owningDatabase = owningDatabase;
        setTableFilePaths();
        tableAttributes = new ArrayList<TableAttribute>();
        tableAttributes.add(new TableAttribute("id"));
        tableRows = new ArrayList<TableRow>();
    }

    public void insertRow(TableRow newRow) throws DBException {
        newRow.addIdValue(tableRows.size() + 1);
        if (newRow.getSize() != getNumAttributes()){
            throw new DBInvalidValueWidthException("Number of values didn't match number of attributes.");
        }
        tableRows.add(newRow);
        defineFileData(tablePath, tableRows);
    }

    public int getNumAttributes() throws DBException{
        tableAttributes.clear();
        loadAttributeFile();
        return tableAttributes.size();
    }

    public int getAttributeIndex(String attributeName) throws DBException{
        for (int i = 0; i < tableAttributes.size(); i++){
            if (tableAttributes.get(i).getObjectName().equals(attributeName)){
                return i;
            }
        }
        throw new DBObjectDoesNotExistException("Could not find attribute in table.");
    }

    public String getAttributeValue(String attributeName) throws DBException{
        int index = getAttributeIndex(attributeName);
        return tableAttributes.get(index).getObjectName();
    }

    public void removeTableRow(int index){
        tableRows.remove(index);
    }

    public TableRow getTableRow(int index){
        return tableRows.get(index);
    }

    public int getNumRows(){
        return tableRows.size();
    }

    public void createObject() throws DBException {
        this.createNewFile(tablePath);
        this.createNewFile(attributePath);
    }

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

    @Override
    public boolean dbObjectExists() {
        return false;
    }

    public void createNewFile(String fileName) throws DBException {
        File fileToCreate = new File(fileName);
        if (fileToCreate.exists()){
            throw new DBServerException("Table already exists.");
        }
        try {
            fileToCreate.createNewFile();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
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

    public void loadAttributeFile() throws DBException {
        tableAttributes.clear();
        File attributeFile = new File(attributePath);
        if (!attributeFile.exists()){
            throw new DBServerException("Could not find attribute definitions.");
        }
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

    public void defineFileData(String filepath, ArrayList<? extends DBObject> dataToWrite) throws DBException{
        File attributeFile = new File(filepath);
        if (attributeFile.exists()){
            try {
                FileWriter writer = new FileWriter(attributeFile);
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
        else{
            throw new DBServerException("Could not write attributes for table.");
        }
    }

    public void loadTableFile() throws DBException{
        File tableFile = new File(tablePath);
        if (!tableFile.exists()){
            throw new DBServerException("Table definitions could be be found");
        }
        try {
            FileReader reader = new FileReader((tablePath));
            BufferedReader buffReader = new BufferedReader(reader);
            String tableLine = buffReader.readLine();
            while(tableLine != null){
                tableRows.add(new TableRow(tableLine));
                tableLine = buffReader.readLine();
            }
        }
        catch(IOException ioe){
            throw new DBServerException("Could not load table definitions.");
        }
    }

    public String createPath(String extension){
        return owningDatabase.getObjectName() + File.separator + objectName + "." + extension;
    }

    public void updateTable(NameValueList updateNameValues) throws DBException {
        ArrayList<TableRow> rowUpdates = new ArrayList<>(tableRows);
        tableRows.clear();
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

    public void deleteRows() throws DBException {
        ArrayList<TableRow> rowUpdates = new ArrayList<>(tableRows);
        tableRows.clear();
        loadTableFile();
        for (int i = 0; i < tableRows.size(); i++){
            for (TableRow updateRow : rowUpdates){
                String rowId = tableRows.get(i).getValue(0);
                if (rowId.equals(updateRow.getValue(0))){
                    tableRows.remove(i);
                    i--;
                }
            }
        }
        defineFileData(tablePath, tableRows);
    }

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
            for (int i = 0; i < customAttributes.size(); i++){
                int attributeIndex = getAttributeIndex(customAttributes.get(i).getObjectName());
                returnString.append(row.printValue(attributeIndex));
            }
            returnString.append(System.lineSeparator());
        }
        return returnString.toString();
    }

    public String printTable() throws DBException {
        if (tableAttributes.size() == 1){
            loadAttributeFile();
        }
        return printTable(tableAttributes);
    }

    public String joinTables(DBTable tableToJoin) throws DBException {
        ArrayList<TableRow> joiningRows = tableToJoin.getTableRows();
        ArrayList<TableAttribute> secondaryAttributes = tableToJoin.getTableAttributes();
        TableAttribute secondaryKey = tableToJoin.getJoinAttribute();
        int primaryIndex = getAttributeIndex(joinAttribute.getObjectName());
        int secondaryIndex = tableToJoin.getAttributeIndex(secondaryKey.getObjectName());
        //get all columns
        for (TableAttribute attribute : secondaryAttributes){
            tableAttributes.add(attribute);
        }
        for (int i = 0; i < tableRows.size(); i++){
            TableRow primaryRow = tableRows.get(i);
            String primaryValue = primaryRow.getValue(primaryIndex);
            for (int j = 0; i < joiningRows.size(); i++){
                TableRow secondaryRow = joiningRows.get(j);
                String secondaryValue = secondaryRow.getValue(secondaryIndex);
                if (primaryValue.equals(secondaryValue)){
                    primaryRow.appendRow(secondaryRow);
                }
            }
        }
        return printTable();
    }

    public static void test() {

    }
}
