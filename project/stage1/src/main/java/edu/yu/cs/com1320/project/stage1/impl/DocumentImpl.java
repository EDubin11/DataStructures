// quesrtions: how to read txt document when im in documentImpl. wont that mess it up 
// also, inoput stream how to call on this file

package edu.yu.cs.com1320.project.stage1.impl;

import java.util.Arrays;


import java.io.*;
import java.net.URI;
import java.util.*;
import java.net.*;
import edu.yu.cs.com1320.project.stage1.Document; 


public class DocumentImpl implements Document{
    private URI uri;
    private String text;
    private byte[] binaryData;
    
    
    public DocumentImpl (URI uri, String txt){
        if (uri == null || uri.toASCIIString().isBlank()|| txt == null || txt.isEmpty()){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.text = txt;
    }
    
    
    public DocumentImpl (URI uri, byte[] binarydata){
        if (uri == null || binarydata == null|| binarydata.length == 0){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binarydata;
    }
    
    public String getDocumentTxt(){
        return this.text;
        //depends on the type of document. its a txt doc
        // if (this.)

        // String content = Files.readString(this.getKey(), StandardCharsets.US_ASCII);//prob wrong but just needs minor fixing up

    }

    public byte[] getDocumentBinaryData(){
        if (this.binaryData == null){
            return null;
        }
       byte[] bytes = Arrays.copyOf(this.binaryData, this.binaryData.length);
        return bytes;
        // FileInputStream fl = new FileInputStream(this);
 
        // // Now creating byte array of same length as file
        // byte[] arr = new byte[(int)this.length()];
 
        // // Reading file content to byte array
        // // using standard read() method
        // fl.read(arr);
 
        // // lastly closing an instance of file input stream
        // // to avoid memory leakage
        // fl.close();
 
        // // Returning above byte array
        // return arr;

    }

    public URI getKey(){
        return this.uri;
    }

    @Override
    public int hashCode() {
     int result = uri.hashCode();
     result = 31 * result + (text != null ? text.hashCode() : 0);
     result = 31 * result + Arrays.hashCode(binaryData);
     return result;
    }

    @Override 
    public boolean equals(Object other){
        Document j = (Document)other;
        return (j.hashCode() == other.hashCode());
    }
    
}