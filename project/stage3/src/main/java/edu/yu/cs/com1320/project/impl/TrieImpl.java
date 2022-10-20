//notes: still need to deal with al lundo stuff in terms of generic commands and undoing changes to tries
//also need to decide how gonna implement the put method. could have it adding a value or changing the value of the
//cons of adding is helpful with adding new doc put into the store. cons of changing  is to set it to null. or could have 2 parralel methods, changing the names and doing diff things. 
//go through and make sure no duplicates in retruned things, if a list 

package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.stage3.Document;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class TrieImpl<Value> implements Trie<Value>{


    private static final int alphabetSize = 256; // extended ASCII
    private Node root; // root of trie
    private Set<String> allWords = new HashSet<String>();

    private class Node<Value>
    {
        private List<Value> values = new ArrayList<Value>();
        //private String key;
        private Node<Value>[] links = new Node[TrieImpl.alphabetSize];
    }

    // private class DocumentComparator<Value>{
    //     //Comparator<Human> comparator = (h1, h2) -> h1.getName().compareTo(h2.getName());
    //     DocumentComparator<Document> docComparator = (Document a, Document b, String j) -> (a.word_count(j) - b.word_count(j));

    // }
    

    // private class Node<Key,Value>{
    //     //Document doc;
    //     Node[] next;
    //     // URI uri;
    //     //Key key;
    //     Value value;
        
    //     Node(){
    //         // this.doc = value;
    //         //this.key = k;
    //         this.value = ;
    //         this.next = null;
    //     }
    // }
    public TrieImpl(){
        this.root = new Node();
        //this.values = new ArrayList<Value>();
        
    }
    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    public void put(String key, Value val){
        //deleteAll the value from this key
        // if (val == null)
        // {
        //     this.deleteAll(key);
        // }
        // else
        //{
        key = key.toLowerCase();
        List<Value> values = new ArrayList<Value>();
        if (val != null){
            values.add(val);
            this.root = put(this.root, key, values, 0);
            this.allWords.add(key);
        }else{
            this.deleteAll(key);
        }
            // values.add(val);
            // this.root = put(this.root, key, values, 0);
        //}
    }

    private Node put(Node x, String key, List<Value> vals, int d)
    {
        //create a new node
        if (x == null)
        {
            x = new Node();
            //x.key = key;
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length())
        {
            if (vals != null){
                List<Value> values = x.values;
                values.addAll(vals);
                if (values.size() > 0 && values != null){
                    Set<Value> set = new HashSet<>(values);
                    List<Value> alVals = new ArrayList<>(set);
                    x.values = alVals;
                }else{
                    x.values = values;
                }
                //x.values.addAll(vals);
                // x.values = values;
            }else{
                //x.values = new ArrayList<Value>();
            }
            // x.values = vals;
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, vals, d + 1);
        return x;
    }


    // private List<Value> getAllSorted(String key)
    // {
    //     key = key.toLowerCase();
    //     Node x = this.get(this.root, key, 0);
    //     if (x == null)
    //     {
    //         return null;
    //     }
    //     //return (List<Value>)Arrays.asList((Value)x.val);
    //     return x.values;
    // }

    private Node get(Node x, String key, int d)
    {
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }




    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    public List<Value> getAllSorted(String key, Comparator<Value> comparator){
        key = key.toLowerCase();
        List<Value> values = getAllSorted(key);
        if (values == null || values.size() == 0){
            return new ArrayList<Value>();
        }
        Set<Value> stuff = new HashSet<Value>(values);
        List<Value> result = new ArrayList<Value>(stuff);
        Collections.sort(result, comparator);
        return result;
        
    }

    private List<Value> getAllSorted(String key)
    {
        key = key.toLowerCase();
        Node x = this.get(this.root, key, 0);
        if (x == null)
        {
            return new ArrayList<Value>();
        }
        //return (List<Value>)Arrays.asList((Value)x.val);
        return x.values;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        prefix = prefix.toLowerCase();


        List<Value> sortedValues = getAllSorted(prefix);
        //new ArrayList<Value>();
        //return result;
        for (String wrd: this.allWords){
            if (wrd.startsWith(prefix)){
                List<Value> vall =this.getAllSorted(wrd);
                sortedValues.addAll(vall);
            }
        }
        if (sortedValues == null || sortedValues.isEmpty()){
            return new ArrayList<Value>();
        }
        Set<Value> setsorted = new HashSet<Value>(sortedValues);
        List<Value> listresult = new ArrayList<Value>(setsorted);
        Collections.sort(listresult, comparator);
        return listresult;

       
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAllWithPrefix(String prefix){//this is just strighgt wrong. dont do the last character myself. itll do it itself. need to find that node and set it as null and all values in it as null
        if (prefix == null || prefix.equals("")){
            return new HashSet<Value>();
        }
        prefix = prefix.toLowerCase();
        List<Value> sortedValues = new ArrayList<Value>();
        Set<Value> values = new HashSet<Value>();
        Set<String> sa = new HashSet<>();
        for (String wrd: this.allWords){
            if (wrd.startsWith(prefix) ){
                List<Value> vall =this.getAllSorted(wrd);
                sortedValues.addAll(vall);
                sa.add(wrd);
            }
        }
        //return sortedValues;
        for (String a: sa){
            Set q = deleteAll(a);
            values.addAll(q);
        }
        return values;
       
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAll(String key){//not sure if works. dont know if actually deletes
        if (key == null || key.equals("")){
            return new HashSet<Value>();
        }
        key = key.toLowerCase();
        List<Value> valuesToDelete = this.getAllSorted(key);
        if (valuesToDelete.isEmpty() || valuesToDelete.size() == 0){
            return new HashSet<Value>();
        }
        Set <Value> toDelete = new HashSet<Value>(valuesToDelete);
        this.root = deleteAll(this.root, key, 0);
        return toDelete;

    }

    private Node deleteAll(Node x, String key, int d)
    {
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {

            x.values = null;
            x.values = new ArrayList<Value>();
        }
        //continue down the trie to the target node
        else
        {
            char c = key.charAt(d);
            x.links[c] = this.deleteAll(x.links[c], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.values != null)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty	
        for (int c = 0; c <TrieImpl.alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    public Value delete(String key, Value val){// i think done
        if (key == null || key.equals("")){
            return null;
        }
        key = key.toLowerCase();
        List<Value> valuesToDelete = this.getAllSorted(key);
        if (!valuesToDelete.contains(val)){
            return null;
        }else{
            valuesToDelete.remove(val);
        }
        deleteAll(key);
        put(this.root, key, valuesToDelete, 0);
        System.out.println(getAllSorted(key));
        return val;
    }

    // private List<Value> collectDownForPrefix (Node x, String key, List<Value> values){//need to look over
    //     if (x == null){
    //         return values;
    //     }
    //     if (x.values != null){
    //         values.addAll(x.values);
    //     }
    //     if (x.links == null){
    //         return null;
    //     }
    //     for (Node z: x.links){
    //         collectDownForPrefix(z,z.key, values);
    //     }
    //     return values;
    // }

    private void collect(Node<Value> x,StringBuilder prefix,Queue<String> results){
        //if this node has a value, add it’s key to the queue
        if (x.values != null) {
            //add a string made up of the chars from
            //root to this node to the result set
            results.add(prefix.toString());
            }
            //visit each non-null child/link
            for (char c = 0; c < TrieImpl.alphabetSize; c++) {
                if(x.links[c]!=null){
                    //add child's char to the string
                    prefix.append(c);
                    this.collect(x.links[c], prefix , results);
                    //remove the child's char to prepare for next iteration
                    prefix.deleteCharAt(prefix.length() - 1);
                    //this.collect(x.links[c], new StringBuilder(c) , results);
                }
            }
            //System.out.println(results.toString());
        }


}