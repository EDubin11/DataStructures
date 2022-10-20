// package edu.yu.cs.com1320.project.impl;

// //import java.util.Arrays;

// import edu.yu.cs.com1320.project.*;
// //import edu.yu.cs.com1320.project.stage1.Document;

// import java.net.URI;
// import java.util.*;
// import java.net.*;

// public class HashTableImpl<Key,Value> implements HashTable<Key,Value>{


//     private class Node<Key,Value>{
//         //Document doc;
//         Node next;
//         // URI uri;
//         Key key;
//         Value value;
        
//         Node(Key k, Value v){
//             // this.doc = value;
//             this.key = k;
//             this.value = v;
//             this.next = null;
//         }
//     }
//     private Node[] table;
//     private int tableSize;
    
//     //Document[] table;
    
    

//     public HashTableImpl(){
//         this.table = new Node[5];
//         this.tableSize = 5;
//     }
    
//     //Cannot create a generic array of HashTableImpl<Key,Value>.Node<?,?>Java(16777751)

//     public Value get(Key k){
//         if (k == null){
//             return null;
//         }
//         int index = this.hashFunction(k);
//         Node current = this.table[index];
//         Value toReturnOnMethod;
//         if (current == null){
//             return null;
//         }
//         while (current!= null){
//             if (k == current.key){
//                 toReturnOnMethod = (Value)current.value;
//                 return toReturnOnMethod;
//                 // break;//go over this
//             }
//             current = current.next;
//         }
//         return null;
//         // if (current.key == k){
//         //     toReturnOnMethod = (Value)current.value;
//         // }
//         // return toReturnOnMethod;
        
//         // if (current != null){//need to find out if should get last node in this bucket
//         //     return (Value)current.value;
//         // }
//         // return null;
//     }

//     // public Value get(Key k){
//     //     int index = this.hashFunction(k);
//     //     Entry current = this.table[index];
//     //     if(current != null){
//     //     return (Value)current.value;
//     //     }
//     //     return null;
//     // }

//     // public Value put(Key k, Value v){
//     //     int index = this.hashFunction(k);
//     //     Entry old = this.table[index];
//     //     Entry<Key,Value> putEntry  = new Entry<Key,Value>(k,v);
//     //     this.table[index] = putEntry;
//     //     if(old != null){
//     //     return (Value)old.value;
//     //     }
//     //     return null;
//     // }

//     /**
//      * @param k the key at which to store the value
//      * @param v the value to store.
//      * To delete an entry, put a null value.
//      * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
//      */
//     public Value put(Key k, Value v){
//         if (this.countNodes() > .75 * this.table.length){
//             reSizeTable(this.table.length * 2);
//         }
//         if (k == null){
//             return null;
//         }
//         // if (v == null){
//         //     deleteThisTotally(k);
//         // }// look over
//         int index = this.hashFunction(k);
//         Node old = this.table[index];
//         Value toReturnOnMethod;

//         if (v == null){
//             if (old == null){
//                 return null;
//             }
//             while (old.next != null){
//                 if (k == old.key){
//                     toReturnOnMethod = (Value)old.value;
//                     deleteThisTotally(k);
//                     return toReturnOnMethod;
//                     // break;//go over this
//                 }
//                 old = old.next;
//             }
//             if (old.key == k){
//                 toReturnOnMethod = (Value)old.value;
//                 deleteThisTotally(k);
//                 return toReturnOnMethod;
//             }
//             return null;
//         }else{
//             Node <Key,Value> newNode = new Node(k,v);
//             //Value toReturnIfUse;
//             // Value toReturnOnMethod;
//             if (old == null){
//                 this.table[index] = newNode;
//                 return null;
//             }
//             while (old.next != null){
//                 if (k == old.key){
//                     toReturnOnMethod = (Value)old.value;
//                     old.value = v;
//                     return toReturnOnMethod;
//                     // break;//go over this
//                 }
//                 old = old.next;
//             }
//             if (old.key == k){
//                 toReturnOnMethod = (Value)old.value;
//                 old.value = v;
//                 return toReturnOnMethod;
//             }
//             old.next = newNode;
//             // while (old.next != null){
//             //     old = old.next;
//             // }
//             return null;

//         }

        
        
        
        
        
        
//         // for (Node a: this.table){
//         //     while (a.next != null){
//         //         if (a.key == k){
//         //             toReturnIfUse = (Value)a.value;
//         //         }
//         //     }
//         //     //may not need outside loop, can prob do a better way to put this is rights spot. 
//         //     //if use hashcode, i will know index and can just go through that index 
//         // }//makes no sense, just use index
//         //also need to go and add it onto the linked list at that array spot

//         //mew way:
//         // if(this.table[index] != null){
//         //     for (Node f: this.table[index]){
//         //         if (f.next == null){
//         //             toReturnIfUse = (Value)f.value;///think this should work
//         //         }
//         //     }
//         // }else{
//         //     this.table[index] = newNode;
//         // }

        
//         // if (this.table[index] == null){
//         //     this.table[index] = newNode;
//         // } 
//         //need to go through array and nodes in each array to see if value of any of them exists and get the value which is the document 
        
//     }

//     private int hashFunction(Key key){
//         return (key.hashCode() & 0x7fffffff) % this.table.length;
//     }

//     private int hashFunctionResize(Key key, int newLength){
//         return (key.hashCode() & 0x7fffffff) % newLength;
//     }
    


//     private boolean checkIfKeyExists(Key k){
//         int index = this.hashFunction(k);
//         Node current = this.table[index];
//         Value toReturnOnMethod;
//         if (current == null){
//             return false;
//         }
//         while (current!= null){
//             if (k == current.key){
//                 return true;
//                 // break;//go over this
//             }
//             current = current.next;
//         }
//         return false;
//     }

//     private boolean deleteThisTotally(Key k){// i think done. issue is its not visible. maybe do that if put is called with null value, then calls this method
//         int index = this.hashFunction(k);
//         Node current = this.table[index];
//         if (current == null){
//             return false;
//         }
//         if (current.key == k){
//             if (current.next != null){
//                 this.table[index] = current.next;
//                 return true;
//             }else{
//                 this.table[index] = null;
//                 return true;
//             }
//         }
//         while (current.next != null){
//             if (current.next.key == k){
//                 //System.out.println("made it here");
//                 Node toDelete = current.next;
//                 Node other = toDelete.next;
//                 current.next = other;
//                 toDelete.next = null;
//                 toDelete.key = null;
//                 toDelete.value = null;
//                 return true;
//             }
//             current = current.next;
//         }
        
//         return false;


//     }

//     private void reSizeTable(int newSize){//needs to be fixed. right now has j being idex and then also its next. 
//         //System.out.println("resizing");
//         Node[] newTable = new Node[newSize];
//         for (Node j: this.table){
//             while (j != null){
//                 int index = hashFunctionResize((Key)j.key, newSize);
//                 Node<Key,Value> toPutIn = new Node((Key)j.key, (Value)j.value);
//                 Node old = newTable[index];
//                 if (old == null){
//                     newTable[index] = toPutIn;
//                 }else{
//                     while (old.next != null){
//                         //System.out.println(old.next);
//                         old = old.next;
//                     }
//                     old.next = toPutIn; 
//                 }
//                 j = j.next; 
//             }





//             // while (j != null){
//             //     // newTable

//             //     int index = hashFunctionResize((Key)j.key, newSize);
//             //     Node old = newTable[index];
//             //     // Node j;
//             //     //Value toReturnIfUse;
//             //     Value toReturnOnMethod;
//             //     if (old == null){
//             //         newTable[index] = j;
//             //         // return null;
//             //     }else{
//             //         while (old.next != null){
//             //             // if (k == old.key){
//             //             //     toReturnOnMethod = (Value)old.value;
//             //             //     old.value = v;
//             //             //     return toReturnOnMethod;
//             //             //     // break;//go over this
//             //             // }
//             //             old = old.next;
//             //         }
//             //         // if (old.key == k){
//             //         //     toReturnOnMethod = (Value)old.value;
//             //         //     old.value = v;
//             //         //     return toReturnOnMethod;
//             //         // }
//             //         old.next = j; 
//             //     }
//             //     // while (old.next != null){
//             //     //     // if (k == old.key){
//             //     //     //     toReturnOnMethod = (Value)old.value;
//             //     //     //     old.value = v;
//             //     //     //     return toReturnOnMethod;
//             //     //     //     // break;//go over this
//             //     //     // }
//             //     //     old = old.next;
//             //     // }
//             //     // // if (old.key == k){
//             //     // //     toReturnOnMethod = (Value)old.value;
//             //     // //     old.value = v;
//             //     // //     return toReturnOnMethod;
//             //     // // }
//             //     // old.next = j;
//             //     // while (old.next != null){
//             //     //     old = old.next;
//             //     // }
//             //     // return null;
//             // }
//         }
//         this.table = newTable;
//         //System.out.println("resized");
//     }

//     private int countNodes(){
//         int counter = 0;
//         for (Node i: this.table){
//             while (i != null){
//                 counter++;
//                 i = i.next;
//             }
//         }
//             // if (i != null){
//             //     while (i.next != null){
//             //         counter++;
//             //         i = i.next;
//             //     }
//             // }
//         return counter;
//     }

//     //Document[] table = new Document[]; //wrong
//     // the key gonna have to be URI, bc that way two docs can have same uri 
//     //check the linkedlist if it contains the element already (use contains() method), if so ask on piazza what to do
// }


// // int bucket = hashFunction (value) ;
// // HashEntry current = elementData [bucket] ;
// // while (current != null) (
// // if (current.data == value){
// //     return true;
// // }
// // current = current.next;
// // return false;
