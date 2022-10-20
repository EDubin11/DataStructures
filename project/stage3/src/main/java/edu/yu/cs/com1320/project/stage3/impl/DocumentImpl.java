// quesrtions: how to read txt document when im in documentImpl. wont that mess it up 
// also, inoput stream how to call on this file

package edu.yu.cs.com1320.project.stage3.impl;

import java.io.*;
import java.net.URI;
//import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.*;
import edu.yu.cs.com1320.project.stage3.Document; 


public class DocumentImpl implements Document{
    private URI uri;
    private String text;
    private byte[] binaryData;
    private HashMap<String, Integer> wordsInMap;
    
    
    public DocumentImpl (URI uri, String txt){
        if (uri == null || uri.toASCIIString().isBlank()|| txt == null || txt.isEmpty()){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.text = txt;
        this.wordsInMap = makeWordsMap(txt);

    }
    
    
    public DocumentImpl (URI uri, byte[] binarydata){
        if (uri == null || binarydata == null|| binarydata.length == 0){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binarydata;
        this.wordsInMap = null;
        // String text = new String(binaryData, StandardCharsets.UTF_8);
        // this.wordsInMap = makeWordsMap(text);
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


    @Override
    public int wordCount(String word) {
        if (word == null){
            throw new IllegalArgumentException();
        }
        word = word.toLowerCase();
        if (this.binaryData != null) {
            return 0;
        }
        if (this.wordsInMap.get(word) == null){
            return 0;
        }else{
            return this.wordsInMap.get(word);
        }
        // TODO Auto-generated method stub
        //return 0;
    }


    @Override
    public Set<String> getWords() {
        if (this.binaryData != null) {
            Set<String> wordss = new HashSet<>();
            return wordss;
        }
        if (this.wordsInMap.keySet() == null){
            return new HashSet<String>();
        }else{
            return this.wordsInMap.keySet();
        }
        // TODO Auto-generated method stub
        //return null;
    }

    private HashMap<String, Integer> makeWordsMap(String txt){
        HashMap<String, Integer> wordsMap = new HashMap<String, Integer>();
        Set<String> theWords = new HashSet<String>();
        String[] words = txt.split(" ");
        for (int i = 0; i < words.length; i ++){
            words[i] = words[i].replaceAll("[^a-zA-Z0-9]", "");
           //words[i] = words[i].replaceAll("[^\\d.]", "");
            words[i] = words[i].toLowerCase();
            // if (wordsMap.keySet().contains(words[i])){
            //     int a = wordsMap.get(words[i]);
            //     wordsMap.put(words[i], a + 1);
            // }else{
            //     wordsMap.put(words[i], 1);
            // }
            if (words[i].isEmpty() || words[i].equals(" ") || words[i].equals("-") || words[i].equals("_")){
                int o =7;
            }else{
                theWords.add(words[i]);
            }
        }
        for (String word: theWords){
            // word = word.replaceAll("[^a-zA-Z0-9]", "");
            // word = word.replaceAll("[^\\d.]", "");
            // word = word.toLowerCase();
            //make them all lowercase 
            if (wordsMap.keySet().contains(word)){
                int a = wordsMap.get(word);
                wordsMap.put(word, a + 1);
            }else{
                wordsMap.put(word,1);
            }
        }
        // for (String word : words){
        //     //word = word.replaceAll("[^a-zA-Z0-9]", "");
        //     if (wordsMap.keySet().contains(word)){
        //         int a = wordsMap.get(word);
        //         wordsMap.put(word, a + 1);
        //     }else{
        //         wordsMap.put(word,1);
        //     }
        // }
        return wordsMap;

    }
    
}