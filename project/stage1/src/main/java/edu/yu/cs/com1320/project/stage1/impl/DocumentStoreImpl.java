package edu.yu.cs.com1320.project.stage1.impl;

import java.io.*;
import java.util.Arrays;
import java.net.URI;
import java.util.*;
import java.net.*;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import edu.yu.cs.com1320.project.impl.*;


public class DocumentStoreImpl implements DocumentStore{

    HashTableImpl<URI, Document> hshTable;

    
    // enum DocumentFormat{
    //     TXT,BINARY
    // }


    public DocumentStoreImpl(){
        hshTable = new HashTableImpl<URI, Document>();

        //work on this
    }
    
    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. 
     * If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException{
        //see if its binary or txt and then read it diff ways 
        if(uri == null || uri.toASCIIString().isBlank()){
            throw new IllegalArgumentException();
        }
        int Hashspot = hashFunctionDS(uri);

        if (input == null){
            if (hshTable.get(uri) == null){
                //hshTable.put(uri,new DocumentImpl(uri, null));//just do put(uri,null)
                //if this is delete and uri.get is null, then nothing to do so return 0 and thats it
                return 0;
            }else{
                Document n = (Document)hshTable.get(uri);//may not need to parse as document
                int nulltoUseHsh = n.hashCode();
                deleteDocument(uri);
                return nulltoUseHsh;
            }
        }
        if (format == null){
            throw new IllegalArgumentException();
        }

        //NOTE: MUST ACCOUNT FOR LINE ABT DELTE IN INSTRICTIONS
        if (format == DocumentFormat.TXT){
            // if (input == null){//this is dealing with delete. must also do with the binary if statement 
            //     if (hshTable.get(uri) == null){
            //         //hshTable.put(uri,new DocumentImpl(uri, null));//just do put(uri,null)
            //         //if this is delete and uri.get is null, then nothing to do so return 0 and thats it
            //         return 0;
            //     }else{
            //         Document n = (Document)hshTable.get(uri);//may not need to parse as document
            //         int nulltoUseHsh = n.hashCode();
            //         deleteDocument(uri);
            //         return nulltoUseHsh;
            //     }
            // }//this null input stream is done I think


            String text = new String(input.readAllBytes());
            if (hshTable.get(uri) == null){
                hshTable.put(uri,new DocumentImpl(uri, text));
                return 0;
            }else{
                Document m = (Document)hshTable.get(uri);//maybe all wrong, shouldnt be a need to make new document if one already exists, just change that one
                // URI toGetHash = m.getKey();
                // int hash = hashFunctionDS(toGetHash);
                //need to do return hashcode for all of these
                int toUseHsh = m.hashCode();
                hshTable.put(uri,new DocumentImpl(uri, text));//just change this to put(uri,text). nevermind
                return toUseHsh;
            }
        }else{
            byte[] arrayByte = new byte[input.available()];
            input.read(arrayByte);
            if (hshTable.get(uri) == null){
                hshTable.put(uri,new DocumentImpl(uri, arrayByte));
                return 0;
            }else{
                Document a = (Document)hshTable.get(uri);
                // URI toGetHash = m.getKey();
                // int hash = hashFunctionDS(toGetHash);
                int toUseByteHash = a.hashCode();
                hshTable.put(uri,new DocumentImpl(uri, arrayByte));//dont need to create new.nevermind i think
                return toUseByteHash;
            }
        }
            // if (input == null){//i dont think i need to do thios. shouldve been taken care of above

            // }

            
            
        
        // return 0;
        // if (hshTable.get(uri)==null){
        //     // hshTable.put(,uri,new DocumentImpl(uri, text));
        //     return 0;//may be totally extra
        // }
    }

     /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri){
        return ((Document)this.hshTable.get(uri));//do i need to parse this????CRYSTAL
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean deleteDocument(URI uri){
        if (hshTable.get(uri) == null){
            return false;
        }
        hshTable.put(uri, null);
        return true;
    }

    private int hashFunctionDS(URI uri){
        return (uri.hashCode() & 0x7fffffff) % 5;
    }

    //need tpo build stack. should do nested class of command and then itll have a next and previous and of course the action 
    //need to figure out how to store the function 
    
}
