package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.FileWriter;

import com.google.gson.Gson;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> { 
    private File dir;
    private String strDir;

    public DocumentPersistenceManager(File baseDir){// i think done
        if (baseDir == null){
            final String dir = System.getProperty("user.dir");
            File myDir = new File(dir);
            myDir.mkdirs();
            this.dir = myDir;
        }else{
            if (baseDir.exists() || baseDir.mkdirs()){
                this.dir = baseDir;
            }
        }
        this.strDir = this.dir.toString() + "/";
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        File file = new File(uri.getPath());
        File s = new File(strDir + file + ".json");
        s.getParentFile().mkdirs();
        // String parent = s.getParent();
        // File pf = new File(parent);
        // pf.mkdirs();
        // s.createNewFile();
        //new File(strDir).mkdirs();
        s.createNewFile();
        FileWriter serializingTo = new FileWriter(s);
        Gson gson = new Gson();
        String json = gson.toJson(val);  
        serializingTo.write(json);
        serializingTo.close();   
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        String pathStr = strDir + uri.getPath() + ".json";
        Path filePath = Path.of(pathStr);
        
        // File myFile = new File(pathStr);
        // Scanner myScanner = new Scanner(myFile);
        // String toDeserialize = Files.readString(filePath);
        String toDo = new String(Files.readAllBytes(filePath));
        Gson gson = new Gson();
        Document theDoc = gson.fromJson(toDo, DocumentImpl.class);
        theDoc.setLastUseTime(System.nanoTime());
        return theDoc;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        File file = new File(strDir + uri + ".json");
        if (file.delete()){ 
            return true;
        }else{
            return false;
        }
    }
}
