package DBObjects;

import java.io.*;
import java.util.ArrayList;
import java.util.jar.Attributes;

public class DBTable extends DBObject {
    String attributePath;
    String tablePath;
    Database owningDatabase;
    public ArrayList<TableAttribute> tableAttributes;

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

    public void createObject(){
        System.out.println("we're trying to create a table");
        tablePath = createPath("tsv");
        attributePath = createPath("txt");
        this.createNewFile(tablePath);
        this.createNewFile(attributePath);
    }

    public void createNewFile(String fileName){
        System.out.println("We tried to create this file " + fileName);
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

    public void defineAttributeFile(){
        File attributeFile = new File(attributePath);
        System.out.println("this is the attribute file: " + attributePath);
        if (attributeFile.exists() && tableAttributes.size() > 0){
            try {
                FileWriter writer = new FileWriter(attributePath);
                BufferedWriter buffWriter = new BufferedWriter(writer);
                for (int i = 0; i < tableAttributes.size(); i++) {
                    System.out.println("We should have written " + tableAttributes.get(i).objectName);
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
}
