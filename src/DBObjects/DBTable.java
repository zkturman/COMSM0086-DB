package DBObjects;

import java.io.*;
import java.util.ArrayList;

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

    public DBTable(String tableName){
        super(tableName);
        tableAttributes = new ArrayList<>();
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
        this.tablePath = owningDatabase.getObjectName() + File.separator + objectName + ".tsv";
        this.attributePath = owningDatabase.getObjectName() + File.separator + objectName + ".txt";
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

    public void defineAttributeFile(){
        File attributeFile = new File(attributePath);
        if (attributeFile.exists() && tableAttributes.size() > 0){
            try {
                FileWriter writer = new FileWriter(attributePath);
                BufferedWriter buffWriter = new BufferedWriter(writer);
                for (int i = 0; i < tableAttributes.size(); i++) {
                    buffWriter.write(tableAttributes.get(i).objectName);
                }
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
