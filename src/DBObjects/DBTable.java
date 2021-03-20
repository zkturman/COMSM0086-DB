package DBObjects;

import DBException.DBObjectDoesNotExistException;
import DBException.DBException;

import java.io.*;
import java.util.ArrayList;

public class DBTable extends DBObject {
    String attributePath;
    String tablePath;
    Database owningDatabase;
    public ArrayList<TableAttribute> tableAttributes;
    public ArrayList<TableRow> tableRows;

    public Database getOwningDatabase() {
        return owningDatabase;
    }

    public void setOwningDatabase(Database owningDatabase) {
        this.owningDatabase = owningDatabase;
    }

    public void setTableAttributes(ArrayList<TableAttribute> tableAttributes) {
        this.tableAttributes = tableAttributes;
    }

    public void setTableFilePaths() {
        attributePath = createPath("txt");
        tablePath = createPath("tsv");
    }

    public String createPath(String extension){
        return owningDatabase.getObjectName() + File.separator + objectName + "." + extension;
    }

    public DBTable(String tableName){
        super(tableName);
        tableAttributes = new ArrayList<TableAttribute>();
        tableRows = new ArrayList<TableRow>();
    }

    private File returnDBFile(String fileName, String fileExtension){
        File fileToOpen = new File(fileName + "." + fileExtension);
        if(!fileToOpen.exists()){
            System.out.println("This file doesn't exist, throw an error.");
        }
        return fileToOpen;
    }

    public void addAttribute(TableAttribute newAttribute){
        tableAttributes.add(newAttribute);
    }
    public void addRow(TableRow newRow){ tableRows.add(newRow); }
    public void addRow(String[] newRow){
        TableRow row = new TableRow(newRow);
        addRow(row);
    }

    public void createObject(){
        tablePath = createPath("tsv");
        attributePath = createPath("txt");
        this.createNewFile(tablePath);
        this.createNewFile(attributePath);
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

    public void loadAttributeFile(File attributeFile) throws DBException {
        try {
            FileReader reader = new FileReader((attributePath));
            BufferedReader buffReader = new BufferedReader(reader);
            String attributeLine = buffReader.readLine();
            while(attributeLine != null){
                tableAttributes.add(new TableAttribute(attributeLine));
                attributeLine = buffReader.readLine();
            }
        }
        catch(IOException ioe){
            System.out.println();
        }
    }

    public void appendAttribute(TableAttribute attributeToAppend) throws DBException {
        File attributeFile = new File(attributePath);
        if (!attributeFile.exists()){
            throw new DBObjectDoesNotExistException();
        }
        loadAttributeFile(attributeFile);
        tableAttributes.add(attributeToAppend);
        writeAttributeFile(attributeFile);
    };

    public void removeAttribute(TableAttribute attributeToRemove) throws DBException {
        File attributeFile = new File(attributePath);
        if (!attributeFile.exists()){
            throw new DBObjectDoesNotExistException();
        }
        loadAttributeFile(attributeFile);
        for (int i = 0; i < tableAttributes.size(); i++){
            if (tableAttributes.get(i).equals(attributeToRemove)){
                tableAttributes.remove(i);
            }
        }
        writeAttributeFile(attributeFile);
    };

    public void defineAttributeFile(){
        File attributeFile = new File(attributePath);
        if (attributeFile.exists() && tableAttributes.size() > 0){
            try {
                FileWriter writer = new FileWriter(attributePath);
                BufferedWriter buffWriter = new BufferedWriter(writer);
                for (int i = 0; i < tableAttributes.size(); i++) {
                    buffWriter.write(tableAttributes.get(i).objectName);
                    buffWriter.newLine();
                    buffWriter.flush();
                }
                buffWriter.close();
            }
            catch (IOException ioe){
                System.out.println("We weren't able to write the attribute file");
                ioe.printStackTrace();
            }
        }
        else{
            System.out.println("the attribute does file does not exist... quitting.");
        }
    }

    private void writeAttributeFile(File attributeFile){
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

    public void insertValues(){};
}
