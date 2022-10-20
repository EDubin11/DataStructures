package edu.yu.cs.com1320.project.impl;

import java.util.Arrays;

import edu.yu.cs.com1320.project.*;
//import edu.yu.cs.com1320.project.stage1.Document;

import java.net.URI;
import java.util.*;
import java.net.*;

public class HashTableImpl<Key,Value> implements HashTable<Key,Value>{
    

    private class Node<Key,Value>{
        //Document doc;
        Node next;
        // URI uri;
        Key key;
        Value value;
        
        Node(Key k, Value v){
            // this.doc = value;
            this.key = k;
            this.value = v;
            this.next = null;
        }
    }
    private Node[] table;

    
    //Document[] table;
    
    

    public HashTableImpl(){
        this.table = new Node[5];
    }
    
    //Cannot create a generic array of HashTableImpl<Key,Value>.Node<?,?>Java(16777751)

    public Value get(Key k){
        if (k == null){
            return null;
        }
        int index = this.hashFunction(k);
        Node current = this.table[index];
        Value toReturnOnMethod;
        if (current == null){
            return null;
        }
        while (current!= null){
            if (k == current.key){
                toReturnOnMethod = (Value)current.value;
                return toReturnOnMethod;
                // break;//go over this
            }
            current = current.next;
        }
        return null;
        // if (current.key == k){
        //     toReturnOnMethod = (Value)current.value;
        // }
        // return toReturnOnMethod;
        
        // if (current != null){//need to find out if should get last node in this bucket
        //     return (Value)current.value;
        // }
        // return null;
    }

    // public Value get(Key k){
    //     int index = this.hashFunction(k);
    //     Entry current = this.table[index];
    //     if(current != null){
    //     return (Value)current.value;
    //     }
    //     return null;
    // }

    // public Value put(Key k, Value v){
    //     int index = this.hashFunction(k);
    //     Entry old = this.table[index];
    //     Entry<Key,Value> putEntry  = new Entry<Key,Value>(k,v);
    //     this.table[index] = putEntry;
    //     if(old != null){
    //     return (Value)old.value;
    //     }
    //     return null;
    // }

    /**
     * @param k the key at which to store the value
     * @param v the value to store.
     * To delete an entry, put a null value.
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */
    public Value put(Key k, Value v){
        if (k == null){
            return null;
        }
        int index = this.hashFunction(k);
        Node old = this.table[index];
        Node <Key,Value> newNode = new Node(k,v);
        //Value toReturnIfUse;
        Value toReturnOnMethod;
        if (old == null){
            this.table[index] = newNode;
            return null;
        }
        while (old.next != null){
            if (k == old.key){
                toReturnOnMethod = (Value)old.value;
                old.value = v;
                return toReturnOnMethod;
                // break;//go over this
            }
            old = old.next;
        }
        if (old.key == k){
            toReturnOnMethod = (Value)old.value;
            old.value = v;
            return toReturnOnMethod;
        }
        old.next = newNode;
        // while (old.next != null){
        //     old = old.next;
        // }
        return null;
        
        
        
        
        
        // for (Node a: this.table){
        //     while (a.next != null){
        //         if (a.key == k){
        //             toReturnIfUse = (Value)a.value;
        //         }
        //     }
        //     //may not need outside loop, can prob do a better way to put this is rights spot. 
        //     //if use hashcode, i will know index and can just go through that index 
        // }//makes no sense, just use index
        //also need to go and add it onto the linked list at that array spot

        //mew way:
        // if(this.table[index] != null){
        //     for (Node f: this.table[index]){
        //         if (f.next == null){
        //             toReturnIfUse = (Value)f.value;///think this should work
        //         }
        //     }
        // }else{
        //     this.table[index] = newNode;
        // }

        
        // if (this.table[index] == null){
        //     this.table[index] = newNode;
        // } 
        //need to go through array and nodes in each array to see if value of any of them exists and get the value which is the document 
        
    }

    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % 5;
    }


    private boolean checkIfKeyExists(Key k){
        int index = this.hashFunction(k);
        Node current = this.table[index];
        Value toReturnOnMethod;
        if (current == null){
            return false;
        }
        while (current!= null){
            if (k == current.key){
                return true;
                // break;//go over this
            }
            current = current.next;
        }
        return false;
    }

    // private void reSizeTable(int NewSize){
        
    // }

    //Document[] table = new Document[]; //wrong
    // the key gonna have to be URI, bc that way two docs can have same uri 
    //check the linkedlist if it contains the element already (use contains() method), if so ask on piazza what to do
}


// int bucket = hashFunction (value) ;
// HashEntry current = elementData [bucket] ;
// while (current != null) (
// if (current.data == value){
//     return true;
// }
// current = current.next;
// return false;