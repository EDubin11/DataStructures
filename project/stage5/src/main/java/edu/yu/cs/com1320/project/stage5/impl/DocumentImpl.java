// quesrtions: how to read txt document when im in documentImpl. wont that mess it up 
// also, inoput stream how to call on this file

package edu.yu.cs.com1320.project.stage5.impl;

import java.io.*;
import java.net.URI;
//import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.*;
import edu.yu.cs.com1320.project.stage5.Document; 


public class DocumentImpl implements Document{ 
    private URI uri;
    private String text;
    private byte[] binaryData;
    private Map<String, Integer> wordsInMap;
    private long lastUseTime;
    
    
    public DocumentImpl (URI uri, String txt, Map<String, Integer> wordMap){
        if (uri == null || uri.toASCIIString().isBlank()|| txt == null || txt.isEmpty()){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.text = txt;
        this.binaryData = null;
        if (wordMap == null){
            this.wordsInMap = makeWordsMap(txt);
        }else{
            this.wordsInMap = wordMap;
        }
        
        //setLastUseTime(System.nanoTime());

    }
    
    
    public DocumentImpl (URI uri, byte[] binarydata){
        if (uri == null || binarydata == null|| binarydata.length == 0){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binarydata;
        this.text = null;
        this.wordsInMap = null;
        //setLastUseTime(System.nanoTime());
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
        return (j.hashCode() == this.hashCode());
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
            words[i] = words[i].toLowerCase();
            if (words[i].isEmpty() || words[i].equals(" ") || words[i].equals("-") || words[i].equals("_")){
                int o =7;
            }else{
                theWords.add(words[i]);
            }
        }
        for (String word: theWords){
            if (wordsMap.keySet().contains(word)){
                int a = wordsMap.get(word);
                wordsMap.put(word, a + 1);
            }else{
                wordsMap.put(word,1);
            }
        }
        return wordsMap;
    }

    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    public long getLastUseTime(){
        return this.lastUseTime;
    }

    
    public void setLastUseTime(long timeInNanoseconds){
        this.lastUseTime = timeInNanoseconds;
    }


    @Override
    public int compareTo(Document o) {//look over. may need to flip it
        if (this.getLastUseTime() > o.getLastUseTime()){
            return 1;
        }
        if (this.getLastUseTime() < o.getLastUseTime()){
            return -1;
        }
        return 0;
    }


    
    /**
     * @return a copy of the word to count map so it can be serialized
     */
    public Map<String,Integer> getWordMap(){
        HashMap<String,Integer> copy = new HashMap<String,Integer>();
        for(Map.Entry<String,Integer> entry : this.wordsInMap.entrySet()){
            copy.put(entry.getKey(),entry.getValue());
        }
        return copy;
    }

    /**
     * This must set the word to count map during deserialization
     * @param wordMap
     */
    public void setWordMap(Map<String,Integer> wordMap){
        this.wordsInMap = wordMap;
    }
    
}