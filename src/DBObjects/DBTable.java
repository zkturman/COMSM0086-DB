package DBObjects;

import DBException.*;

import java.io.*;
import java.util.ArrayList;

public class DBTable extends DBObject {
    String attributePath;
    String tablePath;
    Database owningDatabase;
    private ArrayList<TableAttribute> tableAttributes;
    private ArrayList<TableRow> tableRows;

    public Database getOwningDatabase() {
        return owningDatabase;
    }

    public void setOwningDatabase(Database owningDatabase) {
        this.owningDatabase = owningDatabase;
    }

    public void setTableAttributes(ArrayList<TableAttribute> tableAttributes) throws DBException{
        this.tableAttributes.addAll(tableAttributes);
        for (TableAttribute att: tableAttributes){
            System.out.println(att.toString());
        }
        defineFileData(attributePath, this.tableAttributes);
    }

    public void setTableFilePaths() {
        attributePath = createPath("txt");
        tablePath = createPath("tsv");
    }

    public DBTable(String tableName, Database owningDatabase){
        super(tableName);
        this.owningDatabase = owningDatabase;
        setTableFilePaths();
        tableAttributes = new ArrayList<TableAttribute>();
        tableAttributes.add(new TableAttribute("id"));
        tableRows = new ArrayList<TableRow>();
    }

    public void insertRow(TableRow newRow) throws DBException {
        loadTableFile();
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
    public void createObject(){
        this.createNewFile(tablePath);
        this.createNewFile(attributePath);
    }

    public void dropObject(){
        File tableToDrop = new File(tablePath);
        File attributesToDrop = new File(attributePath);
        try{
            if (!tableToDrop.delete()){
                System.out.println("Could not delete table file");
            }
            if (!attributesToDrop.delete()){
                System.out.println("Could not delete attribute file");
            }
        }
        catch (SecurityException se){
            se.printStackTrace();
        }
    }

    public void createNewFile(String fileName){
        File fileToCreate = new File(fileName);
        if (fileToCreate.exists()){
            System.out.println("throw error that file already exists");
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
        updateAttributeFile();
    };

    public void removeAttribute(TableAttribute attributeToRemove) throws DBException {
        loadAttributeFile();
        loadTableFile();
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
        updateAttributeFile();
        defineFileData(tablePath, tableRows);
    };
    private void updateAttributeFile() throws DBException{
        File attributeFile = new File(attributePath);
        if (!attributeFile.exists()){
            throw new DBObjectDoesNotExistException();
        }
        try{
            FileWriter writer = new FileWriter(attributeFile);
            BufferedWriter buffWriter = new BufferedWriter(writer);
            for (int i = 0; i < tableAttributes.size(); i++) {
                buffWriter.write(tableAttributes.get(i).objectName);
                buffWriter.newLine();
                buffWriter.flush();
            }
            buffWriter.close();
        }
        catch (IOException ioe){
            System.out.println("You couldn't append a column");
        }
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
            System.out.println();
        }
    }

    public void defineFileData(String filepath, ArrayList<? extends DBObject> dataToWrite) throws DBException{
        File attributeFile = new File(filepath);
        if (attributeFile.exists() && dataToWrite.size() > 0){
            try {
                FileWriter writer = new FileWriter(attributeFile);
                BufferedWriter buffWriter = new BufferedWriter(writer);
                for (DBObject dbObject : dataToWrite) {
                    buffWriter.write(dbObject.toString());
                    System.out.println(dbObject.toString());
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
            System.out.println();
        }
    }

    private File returnDBFile(String fileName, String fileExtension){
        File fileToOpen = new File(fileName + "." + fileExtension);
        if(!fileToOpen.exists()){
            System.out.println("This file doesn't exist, throw an error.");
        }
        return fileToOpen;
    }

    public String createPath(String extension){
        return owningDatabase.getObjectName() + File.separator + objectName + "." + extension;
    }

    public static void test() {

    }
}
