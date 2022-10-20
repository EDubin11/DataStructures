//need to go over the two undo methods, see if should take out the size == 0 part, might be wrong and should just apply a no-op 

package edu.yu.cs.com1320.project.stage5.impl;

import java.io.*;
import java.util.function.Function;

import org.apache.maven.surefire.api.runorder.ThreadedExecutionScheduler;

import java.net.URI;
import java.util.*;
import java.net.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.*;


public class DocumentStoreImpl implements DocumentStore{
 
    // private HashTableImpl<URI, Document> hshTable;
    private StackImpl<Undoable> theStack;
    private TrieImpl<URI> theTrie;//may need to change this to <URI> 
    private int docCounter;
    private MinHeapImpl<URI> theHeap;
    private BTreeImpl<URI,Document> theBTree;
    private int maxDocCount;
    private int maxDocBytes;
    private int currentDocBytes;
    private File myDir;


    
    // enum DocumentFormat{
    //     TXT,BINARY
    // }


    // private class DocumentComparator<Value>{
    //     DocumentComparator<Document> docComparator = (Document a, Document b, String j) -> (a.word_count(j) - b.word_count(j));

    // }
    
    public DocumentStoreImpl(){
        // this.hshTable = new HashTableImpl<URI, Document>();
        this.theBTree = new BTreeImpl<>();
        this.theStack = new StackImpl<>();
        this.theTrie = new TrieImpl<>();
        this.docCounter = 0;
        this.theHeap = new MinHeapImpl<>(this.theBTree);
        this.maxDocCount = Integer.MAX_VALUE;
        this.maxDocBytes = Integer.MAX_VALUE;
        this.currentDocBytes = 0;
        this.myDir = new File(System.getProperty("user.dir"));
        this.theBTree.setPersistenceManager(new DocumentPersistenceManager(myDir));

        


        //work on this
    }

    public DocumentStoreImpl(File baseDir){
        // this.hshTable = new HashTableImpl<URI, Document>();
        this.theBTree = new BTreeImpl<>();
        this.theStack = new StackImpl<>();
        this.theTrie = new TrieImpl<>();
        this.docCounter = 0;
        this.theHeap = new MinHeapImpl<>(this.theBTree);
        this.maxDocCount = Integer.MAX_VALUE;
        this.maxDocBytes = Integer.MAX_VALUE;
        this.currentDocBytes = 0;
        if (baseDir == null){
            this.myDir = new File(System.getProperty("user.dir"));
        }else{
            this.myDir = baseDir;
        }
        this.theBTree.setPersistenceManager(new DocumentPersistenceManager(myDir));


        


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
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException{//need to add undo support for all non-deletes in this method
        //see if its binary or txt and then read it diff ways 
        if(uri == null || uri.toASCIIString().isBlank()){
            throw new IllegalArgumentException();
        }
        if (input == null){//its a delete
            if (this.theBTree.get(uri) == null){
                deleteDocument(uri);
                return 0;
            }else{
                Document n = (Document)this.theBTree.get(uri);//may not need to parse as document
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
            String text = new String(input.readAllBytes());
            if (text.getBytes().length > this.maxDocBytes){
                throw new IllegalArgumentException();
            }
            this.nullPutMakeSureEnoughCountAndSpaceTXT(text);//wrong, only works if before it its null, otherwise need to delete old bytes and not update count
            if (this.theBTree.get(uri) == null){
                this.nullPutMakeSureEnoughCountAndSpaceTXT(text);
                Document q = new DocumentImpl(uri, text, null);
                this.theBTree.put(uri,q);
                q.setLastUseTime(System.nanoTime());
                this.theHeap.insert(q.getKey());
                //this.theHeap.reHeapify(q);
                addDocBytes(q);
                this.docCounter++;
                // this.theHeap.insert(q);
                //hshTable.put(uri, q);
                // this.theBTree.put(uri,q);
                this.triePutWithoutCommandAdding(q.getKey(), q);
                Function<URI,Boolean> undoAdd = (URI uriToUse) ->  {
                    //deleteDocumentForUse(uriToUse);//need to look over unsure abt
                    //this.hshTable.put(uriToUse, null);
                    // this.theBTree.put(uri, null);
                    this.docCounter--;
                    this.subtractDocBytes(q);
                    this.deleteDocFromHeap(q);
                    this.deleteDocumentFromTrie(q);
                    this.theBTree.put(uri, null);
                    return true;
                };
                GenericCommand toPushOn = new GenericCommand(uri, undoAdd);
                //System.out.println("pushed command new doc");
                this.theStack.push(toPushOn);
                // this.docCounter++;
                return 0;
            }else{//fix 
                // if ()
                Document m = (Document)this.theBTree.get(uri);//maybe all wrong, shouldnt be a need to make new document if one already exists, just change that one
                if (this.isItInMemory(uri, m)){
                    this.makeSureEnoughSpaceOverrideWithTXT(m, text);
                    //this.subtractDocBytes(m);
                    this.deleteDocFromHeap(m);
                    int toUseHsh = m.hashCode();
                    Document doc = new DocumentImpl(uri, text, null);
                    this.addDocBytes(doc);
                    doc.setLastUseTime(System.nanoTime());
                    this.theHeap.insert(doc.getKey());
                    //this.theHeap.reHeapify(doc);
                    //hshTable.put(uri,doc);//just change this to put(uri,text). nevermind
                    this.theBTree.put(uri, doc);
                    deleteDocumentFromTrie(m);
                    triePutWithoutCommandAdding(doc.getKey(),doc);
                    Function<URI,Boolean> undoChange = (URI uriToUse) ->  {//need to have this update docbytes annd heap also
                        if (this.checkIfTooManyBytes(m)){
                            throw new IllegalArgumentException();
                        }
                        //this.hshTable.put(uriToUse, m);//need to look over unsure abt
                        this.theBTree.put(uriToUse, m);
                        this.makeSureEnoughSpaceOverrideWithTXT(doc, text);
                        //this.subtractDocBytes(doc);
                        this.addDocBytes(m);
                        m.setLastUseTime(System.nanoTime());
                        this.theHeap.insert(m.getKey());
                        this.deleteDocFromHeap(doc);
                        this.deleteDocumentFromTrie(doc);
                        this.triePutWithoutCommandAdding(m.getKey(), m);
                        return true;
                    };
                    GenericCommand toPushOn = new GenericCommand(uri, undoChange);
                    //System.out.println("pushed command changed doc");
                    this.theStack.push(toPushOn);
                    return toUseHsh;
                }else{
                    int toUseHsh = m.hashCode();
                    Document doc = new DocumentImpl(uri, text, null);
                    this.nullPutMakeSureEnoughCountAndSpaceTXT(text);
                    this.addDocBytes(doc);
                    this.docCounter++;
                    doc.setLastUseTime(System.nanoTime());
                    this.theHeap.insert(doc.getKey());
                    //this.theHeap.reHeapify(doc);
                    //hshTable.put(uri,doc);//just change this to put(uri,text). nevermind
                    this.theBTree.put(uri, doc);
                    this.deleteDocumentFromTrie(m);
                    triePutWithoutCommandAdding(doc.getKey(),doc);
                    // this.nullPutMakeSureEnoughCountAndSpaceTXT(text);
                    Function<URI,Boolean> undoChange = (URI uriToUse) ->  {//need to have this update docbytes annd heap also
                        // if (this.checkIfTooManyBytes(m)){
                        //     throw new IllegalArgumentException();
                        // }
                        //this.hshTable.put(uriToUse, m);//need to look over unsure abt
                        this.theBTree.put(uriToUse, m);
                        try {
                            this.theBTree.moveToDisk(uriToUse);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                       // this.makeSureEnoughSpaceOverrideWithTXT(doc, text);
                        this.subtractDocBytes(doc);
                        this.docCounter--;
                        //this.addDocBytes(m);
                       // m.setLastUseTime(System.nanoTime());
                       // this.theHeap.insert(m.getKey());
                        this.deleteDocFromHeap(doc);
                        this.deleteDocumentFromTrie(doc);
                        this.triePutWithoutCommandAdding(m.getKey(), m);
                        return true;
                    };
                    GenericCommand toPushOn = new GenericCommand(uri, undoChange);
                    //System.out.println("pushed command changed doc");
                    this.theStack.push(toPushOn);
                    return toUseHsh;
                }
                // this.makeSureEnoughSpaceOverrideWithTXT(m, text);
                // this.subtractDocBytes(m);
                // this.deleteDocFromHeap(m);
                // int toUseHsh = m.hashCode();
                // Document doc = new DocumentImpl(uri, text, null);
                // this.addDocBytes(doc);
                // doc.setLastUseTime(System.nanoTime());
                // this.theHeap.insert(doc.getKey());
                // //this.theHeap.reHeapify(doc);
                // //hshTable.put(uri,doc);//just change this to put(uri,text). nevermind
                // this.theBTree.put(uri, doc);
                // deleteDocumentFromTrie(m);
                // triePutWithoutCommandAdding(doc.getKey(),doc);
                // Function<URI,Boolean> undoChange = (URI uriToUse) ->  {//need to have this update docbytes annd heap also
                //     if (this.checkIfTooManyBytes(m)){
                //         throw new IllegalArgumentException();
                //     }
                //     //this.hshTable.put(uriToUse, m);//need to look over unsure abt
                //     this.theBTree.put(uriToUse, m);
                //     this.makeSureEnoughSpaceOverrideWithTXT(doc, text);
                //     this.subtractDocBytes(doc);
                //     this.addDocBytes(m);
                //     m.setLastUseTime(System.nanoTime());
                //     this.theHeap.insert(m.getKey());
                //     this.deleteDocFromHeap(doc);
                //     this.deleteDocumentFromTrie(doc);
                //     this.triePutWithoutCommandAdding(m.getKey(), m);
                //     return true;
                // };
                // GenericCommand toPushOn = new GenericCommand(uri, undoChange);
                // //System.out.println("pushed command changed doc");
                // this.theStack.push(toPushOn);
                // return toUseHsh;
            }
        }else{//NEED TO DEAL WITH ALL OF THE ADDING WITH HEAP IN HERE. AND IN ALL DELETE METHODS
            byte[] arrayByte = new byte[input.available()];
            if (arrayByte.length > this.maxDocBytes){
                throw new IllegalArgumentException();
            }
            //nullPutMakeSureEnoughCountAndSpaceBinary(arrayByte);
            input.read(arrayByte);
            if (this.theBTree.get(uri) == null){
                nullPutMakeSureEnoughCountAndSpaceBinary(arrayByte);
                Document p = new DocumentImpl(uri, arrayByte);
                //hshTable.put(uri,p);
                this.theBTree.put(uri, p);
                p.setLastUseTime(System.nanoTime());
                this.theHeap.insert(p.getKey());
                //this.theHeap.reHeapify(p);
                this.addDocBytes(p);
                Function<URI,Boolean> undoAdd = (URI uriToUse) ->  {
                    // deleteDocumentForUse(uriToUse);//need to look over unsure abt
                    //this.hshTable.put(uriToUse, null);
                    this.theBTree.put(uriToUse, null);
                    this.docCounter--;
                    this.subtractDocBytes(p);
                    this.deleteDocFromHeap(p);
                    return true;
                };
                GenericCommand toPushOn = new GenericCommand(uri, undoAdd);
                this.theStack.push(toPushOn);
                this.docCounter++;
                return 0;
            }else{
                Document a = (Document)this.theBTree.get(uri);
                if (this.isItInMemory(uri,a)){
                    this.makeSureEnoughSpaceOverrideWithBinary(a, arrayByte);
                    //this.subtractDocBytes(a);
                    this.deleteDocFromHeap(a);
                    int toUseByteHash = a.hashCode();
                    Document dc = new DocumentImpl(uri, arrayByte);
                    this.theBTree.put(uri, dc);
                    this.addDocBytes(dc);
                    dc.setLastUseTime(System.nanoTime());
                    this.theHeap.insert(dc.getKey());
                    this.deleteDocumentFromTrie(a);
                    // this.theBTree.put(uri, dc);

                    Function<URI,Boolean> undoChange = (URI uriToUse) ->  {
                        if (this.checkIfTooManyBytes(a)){
                            throw new IllegalArgumentException();
                        }
                        if (dc.getDocumentTxt() == null){
                            this.makeSureEnoughSpaceOverrideWithBinary(dc, a.getDocumentBinaryData());
                        }else{
                            this.makeSureEnoughSpaceOverrideWithTXT(dc, a.getDocumentTxt());
                            this.triePutWithoutCommandAdding(a.getKey(), a);
                        }
                        this.theBTree.put(uriToUse, a);
                        // this.makeSureEnoughSpaceOverrideWithBinary(dc, a.getDocumentBinaryData());
                        //this.subtractDocBytes(u);
                        this.addDocBytes(a);
                        this.deleteDocFromHeap(dc);
                        a.setLastUseTime(System.nanoTime());
                        this.theHeap.insert(a.getKey());
                        //this.hshTable.put(uriToUse, a);//need to look over unsure abt
                        // this.theBTree.put(uriToUse, a);
                        return true;
                    };
                    GenericCommand toPushOn = new GenericCommand(uri, undoChange);
                    this.theStack.push(toPushOn);
                    return toUseByteHash;
                }else{
                    this.nullPutMakeSureEnoughCountAndSpaceBinary(arrayByte);
                    int toUseByteHash = a.hashCode();
                    Document dc = new DocumentImpl(uri, arrayByte);
                    this.theBTree.put(uri, dc);
                    this.addDocBytes(dc);
                    dc.setLastUseTime(System.nanoTime());
                    this.theHeap.insert(dc.getKey());
                    this.deleteDocumentFromTrie(a);
                    // this.theBTree.put(uri, dc);
                    Function<URI,Boolean> undoChange = (URI uriToUse) ->  {
                        if (this.checkIfTooManyBytes(a)){
                            throw new IllegalArgumentException();
                        }
                        if (a.getDocumentTxt() == null){
                            this.makeSureEnoughSpaceOverrideWithBinary(dc, a.getDocumentBinaryData());
                        }else{
                            this.makeSureEnoughSpaceOverrideWithTXT(dc, a.getDocumentTxt());
                            this.triePutWithoutCommandAdding(a.getKey(), a);
                        }
                        this.theBTree.put(uriToUse, a);
                        try {
                            this.theBTree.moveToDisk(uriToUse);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // this.makeSureEnoughSpaceOverrideWithBinary(u, a.getDocumentBinaryData());
                        // this.subtractDocBytes(u);
                        //this.addDocBytes(a);
                        //a.setLastUseTime(System.nanoTime());
                        //this.theHeap.insert(a.getKey());
                        this.deleteDocFromHeap(dc);
                        //this.hshTable.put(uriToUse, a);//need to look over unsure abt
                        // this.theBTree.put(uriToUse, a);
                        
                        // try {
                        //     this.theBTree.moveToDisk(uriToUse);
                        // } catch (Exception e) {
                        //     // TODO Auto-generated catch block
                        //     e.printStackTrace();
                        // }
                        return true;
                    };
                    GenericCommand toPushOn = new GenericCommand(uri, undoChange);
                    this.theStack.push(toPushOn);
                    return toUseByteHash;


                }

                // this.makeSureEnoughSpaceOverrideWithBinary(a, arrayByte);
                // int toUseByteHash = a.hashCode();
                // this.deleteDocFromHeap(a);
                // this.subtractDocBytes(a);
                // Document u = new DocumentImpl(uri, arrayByte);
                // this.addDocBytes(u);
                // //hshTable.put(uri,u);//dont need to create new.nevermind i think
                // this.theBTree.put(uri, u);
                // u.setLastUseTime(System.nanoTime());
                // this.theHeap.insert(u.getKey());
                // //this.theHeap.reHeapify(u);
                // Function<URI,Boolean> undoChange = (URI uriToUse) ->  {
                //     if (this.checkIfTooManyBytes(a)){
                //         throw new IllegalArgumentException();
                //     }
                //     this.makeSureEnoughSpaceOverrideWithBinary(u, a.getDocumentBinaryData());
                //     this.subtractDocBytes(u);
                //     this.addDocBytes(a);
                //     a.setLastUseTime(System.nanoTime());
                //     this.theHeap.insert(a.getKey());
                //     this.deleteDocFromHeap(u);
                //     //this.hshTable.put(uriToUse, a);//need to look over unsure abt
                //     this.theBTree.put(uriToUse, a);
                //     return true;
                // };
                // GenericCommand toPushOn = new GenericCommand(uri, undoChange);
                // this.theStack.push(toPushOn);
                // return toUseByteHash;
            }
        }
    }

     /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri){
        Document toReturn = (Document)this.theBTree.get(uri);//do i need to parse this????CRYSTAL
        if (toReturn == null){
            return toReturn;
        }
        if (!this.isItInMemory(uri, toReturn)){
            if (toReturn.getDocumentTxt() == null){
                this.nullPutMakeSureEnoughCountAndSpaceBinary(toReturn.getDocumentBinaryData());
                this.theBTree.put(uri, toReturn);
                this.docCounter++;
                this.addDocBytes(toReturn);
                toReturn.setLastUseTime(System.nanoTime());
                this.theHeap.insert(uri);
                // this.theBTree.put(uri, toReturn);
            }else{
                this.nullPutMakeSureEnoughCountAndSpaceTXT(toReturn.getDocumentTxt());
                this.theBTree.put(uri, toReturn);
                this.docCounter++;
                this.addDocBytes(toReturn);
                toReturn.setLastUseTime(System.nanoTime());
                this.theHeap.insert(uri);
                // this.theBTree.put(uri, toReturn);
            }
        }else{
            toReturn.setLastUseTime(System.nanoTime());
            this.theHeap.reHeapify(toReturn.getKey());
        }
        // if (toReturn == null){
        //     return toReturn;
        // }
        // toReturn.setLastUseTime(System.nanoTime());
        // this.theHeap.reHeapify(toReturn.getKey());
        return toReturn;
    }


    // private boolean deleteDocumentForUse(URI uri){
    //     if (this.hshTable.get(uri) == null){
    //         return false;
    //     }
    //     this.hshTable.put(uri, null);
    //     return true;
    // }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean deleteDocument(URI uri){//need to look over. also look at the put method in hshtable and see iof can get the boolean to return so know if deletred or not in order to strcutuer command and no op
        //need to put in all functionality with the trie and the undos. 
        if (this.theBTree.get(uri) == null){
            Function<URI,Boolean> undoNothing = (URI j) ->  {
                int f = 5;
                return true;
            };
            GenericCommand toPushOn = new GenericCommand(uri, undoNothing);
            this.theStack.push(toPushOn);
            return false;
        }
        //Boolean wasItInMemory;
        GenericCommand command;
        Document doc = this.theBTree.get(uri);
        Boolean wasDocInMemory = this.isItInMemory(uri, doc);
        // if (this.isItInMemory(uri, doc)){
        //     wasItInMemory = true;
        // }
        if (doc.getDocumentTxt() != null){
            command = deleteDocumentFromTrie(doc, wasDocInMemory);//this needs to be changed, to have it putting it back in the heap also.
        }else{
           // jdeowqhi
            Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
                if (this.checkIfTooManyBytes(doc)){
                    throw new IllegalArgumentException();
                }
                if (wasDocInMemory){
                    this.nullPutMakeSureEnoughCountAndSpaceBinary(doc.getDocumentBinaryData());
                    this.theBTree.put(uriToUse, doc);
                    this.docCounter++;
                    doc.setLastUseTime(System.nanoTime());
                    this.addDocBytes(doc);
                    this.theHeap.insert(doc.getKey());
                    // this.theBTree.put(uriToUse, doc);
                }else{
                    this.theBTree.put(uriToUse, doc);
                    try {
                        this.theBTree.moveToDisk(uriToUse);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // this.nullPutMakeSureEnoughCountAndSpaceBinary(doc.getDocumentBinaryData());
                // this.docCounter++;
                // doc.setLastUseTime(System.nanoTime());
                // this.addDocBytes(doc);
                // this.theHeap.insert(doc.getKey());
                // this.theBTree.put(uriToUse, doc);//need to look over unsure abt
                // for (String word: m.getWords()){//just put this all in the deleteAll method
                //     this.theTrie.put(word,m);
                // }
                return true;
            };
            command = new GenericCommand<>(doc.getKey(), undoDel);
            //need to deal with deleting here and making new command which just puts onto non trie things
        }
        //GenericCommand<URI> command = deleteDocumentFromTrie(doc);//this needs to be changed, to have it putting it back in the heap also.

        

        // int hashed = hashFunctionDS(uri);
        // while ( != null)
       // hshTable.put(uri, null);
        // this.theBTree.put(uri, null);

        this.theStack.push(command);
        // int toSubtractBytes;
        // if (doc.getDocumentTxt() == null){
        //     toSubtractBytes = doc.getDocumentBinaryData().length;
        // }else{
        //     toSubtractBytes = doc.getDocumentTxt().getBytes().length;
        // }
        if (wasDocInMemory){
            this.subtractDocBytes(doc);
            this.docCounter--;
            this.deleteDocFromHeap(doc);
        }
        this.theBTree.put(uri, null);
        // this.subtractDocBytes(doc);
        //this.currentDocBytes = this.currentDocBytes - toSubtractBytes;
        //doc.setLastUseTime();//need to put in the right time to get it to be the min in the stack, than reheapify on it, then call remove on the heap to knock it off
        // this.deleteDocFromHeap(doc);
        // hshTable.deleteThisTotally(uri);
        // this.docCounter--;
        return true;
    }

    // private int hashFunctionDS(URI uri){
    //     return (uri.hashCode() & 0x7fffffff) % this.hshTable.length;//maybe just made instacen variable that gets changed when resize array 
    // }

    //need tpo build stack. should do nested class of command and then itll have a next and previous and of course the action 
    //need to figure out how to store the function 
    

    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    public void undo() throws IllegalStateException{//i think done. check the casts and instanceOf if should have<Value> or <URI> or not
        if (this.theStack.size() == 0 || this.theStack == null){
            throw new IllegalStateException();
        }
        


        Undoable toDo = this.theStack.pop();
        if (toDo == null){
            throw new IllegalStateException();
        }
        if (toDo instanceof CommandSet){
            CommandSet toUndoAll = (CommandSet)toDo;
            if (toUndoAll.size() == 0){
                this.undo();
            }else{
                toUndoAll.undoAll();
            }
            // toUndoAll.undoAll();
        }else{
            GenericCommand<URI> toUndoThis = (GenericCommand)toDo;
            toUndoThis.undo();
        }
        //toDo.undo();
        // Function<URI,Boolean> toApply = toDo.undo; 
        // toApply.apply(toDO.uri);
        
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException{
        if (uri == null){
            throw new IllegalArgumentException();
        }
        if (this.theStack.size() == 0 || this.theStack == null){
            throw new IllegalStateException();
        }
        int h = 0;
        StackImpl <Undoable> newStack = new StackImpl<Undoable>();
        // Command toUse = this.theStack.peek();
        while (this.theStack.size() > 0){
            Undoable toUse = this.theStack.pop();
            //if (this.theStack.pop().class == GenericCommand.class){}
            if (toUse instanceof CommandSet){
                CommandSet toUse1 = (CommandSet)toUse;
                if (toUse1.size() == 0){
                    this.undo(uri);
                }else{
                    // toUse1.undoAll();
                    if (toUse1.containsTarget(uri)){
                        //toUse = (CommandSet)toUse;
                        Boolean k = toUse1.undo(uri);
                        //System.out.println("undone on uri");
                        newStack.push(toUse1);
                        h++;
                        break;
                    }else{
                        newStack.push(toUse1);
                    }
                }
            }else{
                GenericCommand toUse2 = (GenericCommand)toUse;
                URI ur = (URI)toUse2.getTarget();
                if (ur.equals(uri)){
                    toUse2.undo();
                    h++;
                    break;
                }else{
                    newStack.push(toUse2);
                }
            }
        }

                



        
        while (newStack.size() > 0){
            Undoable toRePushonReal = newStack.pop();
            this.theStack.push(toRePushonReal);
        }
        if (h == 0){
            throw new IllegalStateException();
        }


    }

    public List<Document> search(String keyword){
        //lowercase
        keyword = keyword.toLowerCase();
        String theWord = keyword;
        // List<Document> results = this.theTrie.getAllSorted(theWord, (doca,docb) -> {
        //     if(doca.wordCount(theWord) - docb.wordCount(theWord) > 0 ){
        //         return -1;
        //     }else if(doca.wordCount(theWord) - docb.wordCount(theWord) < 0) {
        //         return 1;
        //     }else{
        //         return 0;
        //     }
        // });

        List<URI> docss = this.theTrie.getAllSorted(theWord, (uria,urib) -> {
            Document doc1 = this.theBTree.get(uria);
            if (!this.isItInMemory(uria, doc1)){
                try {
                    this.theBTree.moveToDisk(uria);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Document doc2 = this.theBTree.get(urib);
            if (!this.isItInMemory(urib, doc2)){
                try {
                    this.theBTree.moveToDisk(urib);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(doc1.wordCount(theWord) - doc2.wordCount(theWord) > 0 ){
                return -1;
            }else if(doc1.wordCount(theWord) - doc2.wordCount(theWord) < 0) {
                return 1;
            }else{
                return 0;
            }
            
        });
        Set<Document> gottenDocs = new HashSet<Document>();
        for(URI doc : docss){
            Document j = this.theBTree.get(doc);
            if (j != null) {
                gottenDocs.add(j);
            }
        }//now that theyve been pulled from memory, need to deal with memory Limitations
        this.dealWithMemoryLimitsOnSearches(gottenDocs);
        List<Document> results = new ArrayList<>(gottenDocs);
        return results;
    }

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> searchByPrefix(String keywordPrefix){
        keywordPrefix = keywordPrefix.toLowerCase();
        String theWord = keywordPrefix;
        List<URI> results = this.theTrie.getAllWithPrefixSorted(theWord, (URI uria, URI urib) -> {
            Document doca = this.theBTree.get(uria);
            Document docb = this.theBTree.get(urib);
            int numberOfTimes = 0;
            if (doca == null){
                numberOfTimes = 0;
            }else{
                for (String word : doca.getWords()) {
                    int h = doca.wordCount(word);
                    if (word.startsWith(theWord)){
                        numberOfTimes += h;
                    }
                }
            }
            int numberOfTimesB = 0;
            if (docb == null){
                numberOfTimesB = 0;
            }else{
                for (String word : docb.getWords()) {
                    int s = docb.wordCount(word);
                    if (word.startsWith(theWord)){
                        numberOfTimesB += s;
                    }
                }
            }
            if(numberOfTimes - numberOfTimesB > 0 ){
                return -1;
            }else if(numberOfTimes - numberOfTimesB < 0) {
                return 1;
            }else{
                return 0;
            }
        });
        Set<Document> gottenDocs = new HashSet<Document>();
        for (URI resultURI: results) {
            Document j = this.theBTree.get(resultURI);
            if (j != null) {
                gottenDocs.add(j);
            }
        }
        if (results == null) {
            return new ArrayList<Document>();
        }else{
            // for (Document w: gottenDocs) {
            //     w.setLastUseTime(System.nanoTime());
            //     this.theHeap.reHeapify(w.getKey());
            // }
            if (gottenDocs.size() == 0 || gottenDocs == null){
                return new ArrayList<Document>();
            }
            this.dealWithMemoryLimitsOnSearches(gottenDocs);
            List <Document> returnDocs = new ArrayList<>(gottenDocs);
            return returnDocs;
        }
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAll(String keyword){//need to deal with deleting from the heap 
        keyword = keyword.toLowerCase();
        String theWord = keyword;
        // List<Document> results = this.theTrie.getAllSorted(theWord, (doca,docb) -> {
        //     if(doca.wordCount(theWord) - docb.wordCount(theWord) > 0 ){
        //         return -1;
        //     }else if(doca.wordCount(theWord) - docb.wordCount(theWord) < 0) {
        //         return 1;
        //     }else{
        //         return 0;
        //     }
        // });

        List<URI> uris = this.theTrie.getAllSorted(theWord, (uria,urib) -> {
            Document doc1 = this.theBTree.get(uria);
            Document doc2 = this.theBTree.get(urib);
            if(doc1.wordCount(theWord) - doc2.wordCount(theWord) > 0 ){
                return -1;
            }else if(doc1.wordCount(theWord) - doc2.wordCount(theWord) < 0) {
                return 1;
            }else{
                return 0;
            }
        });
        Set<Document> gottenDocs = new HashSet<Document>();
        for (URI delUri: uris) {
            Document j = this.theBTree.get(delUri);
            if (j != null) {
                gottenDocs.add(j);
            }
        }

        Set<URI> removed = new HashSet<URI>();
        //Set<Document> theDocs = new HashSet<>(results);//this may be wrong. can have same documents twice, but cant have URI twice
       // Set<GenericCommand<URI>> setOfCommands= new HashSet<GenericCommand<URI>>();
       CommandSet<URI> cmdSet= new CommandSet<>();
        // Function<URI,Boolean> undoNothing = (URI j) ->  {
        //     int f = 5;
        //     return true;
        // };
        // GenericCommand toPushOnFake = new GenericCommand(null, undoNothing);
        // cmdSet.addCommand(toPushOnFake);
        for (Document toDel: gottenDocs){
            URI uri = toDel.getKey();
            Boolean wasIt = this.isItInMemory(uri, toDel);
            //this.hshTable.put(uri,null);
            // this.theBTree.put(uri,null);
            this.docCounter--;
            this.subtractDocBytes(toDel);
            this.deleteDocFromHeap(toDel);
            this.theBTree.put(uri,null);


            GenericCommand<URI> cmd = this.deleteDocumentFromTrie(toDel, wasIt);

            //CommandSet<URI> result = new CommandSet<>();
            // for (String word: toDel.getWords()){//just put this all in the deleteAll method
            //     List<Document> docss = this.theTrie.getAllSorted(word, (doca,docb) -> {
            //         if(doca.wordCount(word) - docb.wordCount(word) > 0 ){
            //             return -1;
            //         }else if(doca.wordCount(word) - docb.wordCount(word) < 0) {
            //             return 1;
            //         }else{
            //             return 0;
            //         }
            //     });
            //     this.theTrie.delete(word,toDel);
            // }
        // Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
        //     this.hshTable.put(uriToUse, toDel);
        //     this.docCounter++;//need to look over unsure abt
        //     for (String word: toDel.getWords()){//just put this all in the deleteAll method
        //         this.theTrie.put(word,toDel);
        //     }
        //     return true;
        // };


            // Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
            //     if (this.checkIfTooManyBytes(toDel)){
            //         throw new IllegalArgumentException();
            //     }
            //     //this.hshTable.put(uriToUse, toDel);//need to look over unsure abt
            //     this.theBTree.put(uriToUse,toDel);
            //     for (String word: toDel.getWords()){//just put this all in the deleteAll method
            //         this.theTrie.put(word, toDel.getKey());
            //     }
            //     this.nullPutMakeSureEnoughCountAndSpaceTXT(toDel.getDocumentTxt());
            //     toDel.setLastUseTime(System.nanoTime());
            //     this.theHeap.insert(toDel.getKey());
            //     this.addDocBytes(toDel);
            //     this.docCounter++;
            //     return true;
            // };

            // GenericCommand<URI> cmd = new GenericCommand<URI>(toDel.getKey(), undoDel);
                

            removed.add(uri);
            cmdSet.addCommand(cmd);
            //this.theStack.push(setOfCommands);
        }
        this.theStack.push(cmdSet);
        //gonna have to add commandSet to commandstack for all of these deletes.
        return removed;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAllWithPrefix(String keywordPrefix){//need to deal with deleting from the heap 
        Set<URI> resultURISet = new HashSet<URI>();
        CommandSet<URI> cmdSet= new CommandSet<>();
        if (keywordPrefix == null || keywordPrefix.isEmpty() || keywordPrefix.equals("")){
            return resultURISet;
        }
        keywordPrefix = keywordPrefix.toLowerCase();
        String theWord = keywordPrefix;
        //Set<Document> deleted = this.theTrie.getAllWithPrefixSorted(theWord, )
        List<URI> results = this.theTrie.getAllWithPrefixSorted(theWord, (uria,urib) -> {
            Document doca = this.theBTree.get(uria);
            Document docb = this.theBTree.get(urib);
            int numberOfTimes = 0;
            for (String word : doca.getWords()) {
                int h = doca.wordCount(word);
                if (word.startsWith(theWord)){
                    numberOfTimes += h;
                }
            }
            int numberOfTimesB = 0;
            for (String word : docb.getWords()) {
                int s = docb.wordCount(word);
                if (word.startsWith(theWord)){
                    numberOfTimesB += s;
                }
            }
            if(numberOfTimes - numberOfTimesB > 0 ){
                return -1;
            }else if(numberOfTimes - numberOfTimesB < 0) {
                return 1;
            }else{
                return 0;
            }
        });
        Set<Document> gottenDocs = new HashSet<Document>();
        for (URI resultURI: results) {
            Document j = this.theBTree.get(resultURI);
            if (j != null) {
                gottenDocs.add(j);
            }
        }
        for (Document toDel : gottenDocs){
            resultURISet.add(toDel.getKey());
            if (isItInMemory(toDel.getKey(), toDel)){
                URI uri = toDel.getKey();
                // this.hshTable.put(uri,null);
                //  this.theBTree.put(uri, null);
                 this.deleteDocFromHeap(toDel);
                 this.theBTree.put(uri, null);
                 this.subtractDocBytes(toDel);
                 this.docCounter--;
                 GenericCommand<URI> cmd = this.deleteDocumentFromTrie(toDel, true);
                 cmdSet.addCommand(cmd);
            }else{
                URI uri = toDel.getKey();
                // this.hshTable.put(uri,null);
                 this.theBTree.put(uri, null);
                 GenericCommand<URI> cmd = this.deleteDocumentFromTrie(toDel, false);
                 cmdSet.addCommand(cmd);
            }
        }


        // Set<URI> deleted = this.theTrie.deleteAllWithPrefix(theWord);
        // Set<Document> deletedDocs = new HashSet<Document>();
        // for (URI delUri: deleted) {
        //     deletedDocs.add(this.theBTree.get(delUri));
        // }
        // List<Document> prefixDocs = this.searchByPrefix(keywordPrefix);
        // //Set<URI> result = new HashSet<URI>();
        // // CommandSet<URI> cmdSet= new CommandSet<>();
        // Set<Document> theDocs = new HashSet<>(prefixDocs);
        // //Set<GenericCommand<URI>> setOfCommands= new HashSet<GenericCommand<URI>>();
        // for (Document toDel: deletedDocs){
        //     // if (wasItInMemory){
        //     //     this.nullPutMakeSureEnoughCountAndSpaceTXT(m.getDocumentTxt());
        //     //     this.docCounter++;
        //     //     this.addDocBytes(m);
        //     //     m.setLastUseTime(System.nanoTime());
        //     //     this.theHeap.insert(uriToUse);
        //     //     this.theBTree.put(uriToUse, m);
        //     // }else{
        //     //     this.theBTree.put(uriToUse, m);
        //     //     this.theBTree.moveToDisk(uriToUse);
        //     // }
        //     URI uri = toDel.getKey();
        //    // this.hshTable.put(uri,null);
        //     this.theBTree.put(uri, null);
        //     this.deleteDocFromHeap(toDel);
        //     this.subtractDocBytes(toDel);
        //     this.docCounter--;

        //     //GenericCommand<URI> command = this.deleteDocumentFromTrie(toDel);


        //     CommandSet<URI> newresult = new CommandSet<>();


        //     result.add(uri);
        //     Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
        //         //need to put it back onto diff spots if undoing a deleteAll, so make sure to account for that
        //         if (this.checkIfTooManyBytes(toDel)){
        //             throw new IllegalArgumentException();
        //         }
        //         this.hshTable.put(uriToUse, toDel);//need to look over unsure abt
        //         for (String word: toDel.getWords()){//just put this all in the deleteAll method
        //             this.theTrie.put(word, toDel);
        //         }
        //         this.nullPutMakeSureEnoughCountAndSpaceTXT(toDel.getDocumentTxt());
        //         toDel.setLastUseTime(System.nanoTime());
        //         //need to put it back onto diff spots if undoing a deleteAll, so make sure to account for that
        //         this.theHeap.insert(toDel);
        //         this.addDocBytes(toDel);
        //         //need to put it back onto diff spots if undoing a deleteAll, so make sure to account for that
        //         this.docCounter++;
        //         return true;
        //     };

        //     GenericCommand<URI> toPushOn = new GenericCommand(uri, undoDel);
        //     cmdSet.addCommand(toPushOn);
        // }
        // if (cmdSet.isEmpty()){
        //     URI tempUri = new URI("http://mmsfghwis,khzdaoldi.hfaseweikukghre2uighdewloi.jdqwlihfduw3eirandomiuldzh,n,");
        //     Function<URI,Boolean> fakeNoothing = (URI uriToUse) ->  {
        //         int i = 5;
        //         i = i + 3;
        //         return true;
        //     };
        //     GenericCommand<URI> nothing = new GenericCommand(tempUri, fakeNoothing);
        //     cmdSet.addCommand(nothing);


        // }
        this.theStack.push(cmdSet);
        return resultURISet;
    }

    private GenericCommand<URI> deleteDocumentFromTrie(Document m){
        CommandSet<URI> result = new CommandSet<>();
         for (String word: m.getWords()){//just put this all in the deleteAll method
            // List<URI> docss = this.theTrie.getAllSorted(word, (uria,urib) -> {
            //     Document doc1 = this.theBTree.get(uria);
            //     Document doc2 = this.theBTree.get(urib);
            //     if(doc1.wordCount(word) - doc2.wordCount(word) > 0 ){
            //         return -1;
            //     }else if(doc1.wordCount(word) - doc2.wordCount(word) < 0) {
            //         return 1;
            //     }else{
            //         return 0;
            //     }
            // });

            // docss.remove(m);
            // this.theTrie.put(word,null);
            // for (Document a: docss){
            //     this.theTrie.put(word, a);
            // }
            this.theTrie.delete(word,m.getKey());
        }
        Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
            if (this.checkIfTooManyBytes(m)){
                throw new IllegalArgumentException();
            }
            this.nullPutMakeSureEnoughCountAndSpaceTXT(m.getDocumentTxt());
            this.docCounter++;
            this.addDocBytes(m);
            m.setLastUseTime(System.nanoTime());
            this.theHeap.insert(uriToUse);
            //it depedns if it was deleted out of disk or out of memory before.
            //if out of memory, stam put it in the heap//if out of disk it was deleted, put it into btree then call movetodisk on it
            //this.hshTable.put(uriToUse, m);//need to look over unsure abt
            for (String word: m.getWords()){//just put this all in the deleteAll method
                this.theTrie.put(word,m.getKey());
            }
            return true;
        };
        GenericCommand<URI> cmd = new GenericCommand<>(m.getKey(), undoDel);
        return cmd;
    }


    private GenericCommand<URI> deleteDocumentFromTrie(Document m, Boolean wasItInMemory){
        CommandSet<URI> result = new CommandSet<>();
         for (String word: m.getWords()){//just put this all in the deleteAll method
            // List<URI> docss = this.theTrie.getAllSorted(word, (uria,urib) -> {
            //     Document doc1 = this.theBTree.get(uria);
            //     Document doc2 = this.theBTree.get(urib);
            //     if(doc1.wordCount(word) - doc2.wordCount(word) > 0 ){
            //         return -1;
            //     }else if(doc1.wordCount(word) - doc2.wordCount(word) < 0) {
            //         return 1;
            //     }else{
            //         return 0;
            //     }
            // });

            // docss.remove(m);
            // this.theTrie.put(word,null);
            // for (Document a: docss){
            //     this.theTrie.put(word, a);
            // }
            this.theTrie.delete(word,m.getKey());
        }
        Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
            if (this.checkIfTooManyBytes(m)){
                throw new IllegalArgumentException();
            }
            if (wasItInMemory){
                this.nullPutMakeSureEnoughCountAndSpaceTXT(m.getDocumentTxt());
                this.theBTree.put(uriToUse, m);
                this.docCounter++;
                this.addDocBytes(m);
                m.setLastUseTime(System.nanoTime());
                this.theHeap.insert(uriToUse);
                // this.theBTree.put(uriToUse, m);
            }else{
                this.theBTree.put(uriToUse, m);
                try {
                    this.theBTree.moveToDisk(uriToUse);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // this.nullPutMakeSureEnoughCountAndSpaceTXT(m.getDocumentTxt());
            // this.docCounter++;
            // this.addDocBytes(m);
            // m.setLastUseTime(System.nanoTime());
            // this.theHeap.insert(uriToUse);
            //it depedns if it was deleted out of disk or out of memory before.
            //if out of memory, stam put it in the heap//if out of disk it was deleted, put it into btree then call movetodisk on it
            //this.hshTable.put(uriToUse, m);//need to look over unsure abt
            for (String word: m.getWords()){//just put this all in the deleteAll method
                this.theTrie.put(word,m.getKey());
            }
            return true;
        };
        GenericCommand<URI> cmd = new GenericCommand<>(m.getKey(), undoDel);
        return cmd;
    }


    private void deleteDocFromStack(Document doc){
        StackImpl <Undoable> newStack = new StackImpl<Undoable>();
        // Command toUse = this.theStack.peek();
        while (this.theStack.size() > 0){
            Undoable toUse = this.theStack.pop();
            //if (this.theStack.pop().class == GenericCommand.class){}
            if (toUse instanceof CommandSet){
                CommandSet toUse1 = (CommandSet)toUse;
                if (toUse1.containsTarget(doc.getKey())){
                    Iterator<GenericCommand<URI>> it = toUse1.iterator();
                    while (it.hasNext()){
                        GenericCommand gc = it.next();
                        if (gc.getTarget().equals(doc.getKey())){//NEED TO LOOK OVER. MAY BE ==
                            it.remove();
                        }
                    }
                    newStack.push(toUse1);
                }else{
                    newStack.push(toUse1);
                }
            }else{
                GenericCommand toUse2 = (GenericCommand)toUse;
                URI ur = (URI)toUse2.getTarget();
                if (ur.equals(doc.getKey())){
                    int l = 5;
                }else{
                    newStack.push(toUse2);
                }
            }
        }
        while (newStack.size() > 0){
            Undoable toRePushonReal = newStack.pop();
            this.theStack.push(toRePushonReal);
        }
    }


    private void triePutWithoutCommandAdding(URI uri, Document doc){
        for (String word: doc.getWords()){//just put this all in the deleteAll method
            this.theTrie.put(word,uri);
        }
    }


    private int prefixAppearsInDoc(Document doc, String prefix){
        int numberOfTimes = 0;
        for (String word : doc.getWords()) {
            int h = doc.wordCount(word);
            if (word.startsWith(prefix)){
                numberOfTimes += (h);
            }
        }
        return numberOfTimes;
    }
    

    
    public void setMaxDocumentCount(int limit) {
        //int temp = this.maxDocCount;
        this.maxDocCount = limit;
        while (!(this.maxDocCount - this.docCounter >= 0)){//NEED TO PUT THIS FOR BINARY DOCUMENT ALSO!!!!
            //it stays in BTree on disk and stays in stack 
            //gets deleted from trie and heap
            URI forNow = (URI)theHeap.remove();
            //deleteDocFromStack(forNow);
            Document doc = (Document)this.theBTree.get(forNow);
            //deleteDocumentFromTrie(doc);//it stays in trie now
            //this.hshTable.put(forNow.getKey(), null);
            try {
                this.theBTree.moveToDisk(forNow);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            docCounter--;
            this.subtractDocBytes(doc);
        }
    }

    
    public void setMaxDocumentBytes(int limit) {
        this.maxDocBytes = limit;
        while (!(this.maxDocBytes - this.currentDocBytes >= 0)){//NEED TO PUT THIS FOR BINARY DOCUMENT ALSO!!!! may be better to put this eariler in doc
            URI forNow = (URI)theHeap.remove();
            //deleteDocFromStack(forNow);
            //deleteDocumentFromTrie(forNow);
            // this.hshTable.put(forNow.getKey(), null);
            Document doc2 = this.theBTree.get(forNow);
            try {
                this.theBTree.moveToDisk(forNow);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            docCounter--;
            this.subtractDocBytes(doc2);
        }
    }


    private void nullPutMakeSureEnoughCountAndSpaceTXT(String text){
        while (!(this.maxDocCount - this.docCounter >= 1)){//NEED TO PUT THIS FOR BINARY DOCUMENT ALSO!!!!
            URI forNow = (URI)theHeap.remove();
            //deleteDocFromStack(forNow);
            //deleteDocumentFromTrie(forNow);
           // this.hshTable.put(forNow.getKey(), null);
            Document doc2 = this.theBTree.get(forNow);
            try {
                this.theBTree.moveToDisk(forNow);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            docCounter--;
            this.subtractDocBytes(doc2);
        }
        while (!(this.maxDocBytes - this.currentDocBytes >= text.getBytes().length)){//NEED TO PUT THIS FOR BINARY DOCUMENT ALSO!!!! may be better to put this eariler in doc
            URI forNow = (URI)theHeap.remove();
            //deleteDocFromStack(forNow);
            //deleteDocumentFromTrie(forNow);
            //this.hshTable.put(forNow.getKey(), null);
            Document doc2 = this.theBTree.get(forNow);
            try {
                this.theBTree.moveToDisk(forNow);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            docCounter--;
            this.subtractDocBytes(doc2);
        }
    }


    private void nullPutMakeSureEnoughCountAndSpaceBinary(byte[] arrayByte){
        while (!(maxDocCount - docCounter >= 1)){//NEED TO PUT THIS FOR BINARY DOCUMENT ALSO!!!!
            URI forNow = (URI)theHeap.remove();
            //deleteDocFromStack(forNow);
            //deleteDocumentFromTrie(forNow);
            //this.hshTable.put(forNow.getKey(), null);
            Document doc2 = this.theBTree.get(forNow);
            try {
                this.theBTree.moveToDisk(forNow);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            docCounter--;
            //this.subtractDocBytes(forNow);
            this.subtractDocBytes(doc2);

        }
        while (!(maxDocBytes - currentDocBytes >= arrayByte.length)){//NEED TO PUT THIS FOR BINARY DOCUMENT ALSO!!!! may be better to put this eariler in doc
            //Document forNow = (Document)theHeap.remove();
            URI forNow = (URI)theHeap.remove();
            //deleteDocFromStack(forNow);
            //deleteDocumentFromTrie(forNow);
            //this.hshTable.put(forNow.getKey(), null);
            //this.subtractDocBytes(forNow);
            Document doc2 = this.theBTree.get(forNow);
            try {
                this.theBTree.moveToDisk(forNow);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            docCounter--;
            this.subtractDocBytes(doc2);
        }
    }


    private void makeSureEnoughSpaceOverrideWithTXT(Document oldDoc, String newText){
        //need to take old doc, get its bytes
        if (oldDoc.getDocumentTxt() == null){
            int oldDocBytes = oldDoc.getDocumentBinaryData().length;
            this.currentDocBytes = this.currentDocBytes - oldDocBytes;
            nullPutMakeSureEnoughCountAndSpaceTXT(newText);
        }else{
            int oldDocWordBytes = oldDoc.getDocumentTxt().getBytes().length;
            this.currentDocBytes = this.currentDocBytes - oldDocWordBytes;
            nullPutMakeSureEnoughCountAndSpaceTXT(newText);
        }

    }
    

    private void makeSureEnoughSpaceOverrideWithBinary(Document oldDoc, byte[] newArrayByte){
        //need to take old doc, get its bytes
        if (oldDoc.getDocumentTxt() == null){
            int oldDocBytes = oldDoc.getDocumentBinaryData().length;
            this.currentDocBytes = this.currentDocBytes - oldDocBytes;
            nullPutMakeSureEnoughCountAndSpaceBinary(newArrayByte);
        }else{
            int oldDocWordBytes = oldDoc.getDocumentTxt().getBytes().length;
            this.currentDocBytes = this.currentDocBytes - oldDocWordBytes;
            nullPutMakeSureEnoughCountAndSpaceBinary(newArrayByte);        
        }
    }


    private void deleteDocFromHeap(Document doc){
        doc.setLastUseTime(0);
        this.theHeap.reHeapify(doc.getKey());
        this.theHeap.remove();
    }


    private void addDocBytes(Document newDoc){
        if (newDoc.getDocumentTxt() == null){
            int newDocBytes = newDoc.getDocumentBinaryData().length;
            this.currentDocBytes = this.currentDocBytes + newDocBytes;
            //nullPutMakeSureEnoughCountAndSpaceBinary(newArrayByte);
        }else{
            int newDocBytes = newDoc.getDocumentTxt().getBytes().length;
            this.currentDocBytes = this.currentDocBytes + newDocBytes;
            //nullPutMakeSureEnoughCountAndSpaceBinary(newArrayByte);        
        }
    }

    private void subtractDocBytes(Document oldDoc){
        if (oldDoc.getDocumentTxt() == null){
            int oldDocBytes = oldDoc.getDocumentBinaryData().length;
            this.currentDocBytes = this.currentDocBytes - oldDocBytes;
            //nullPutMakeSureEnoughCountAndSpaceBinary(newArrayByte);
        }else{
            int oldDocBytes = oldDoc.getDocumentTxt().getBytes().length;
            this.currentDocBytes = this.currentDocBytes - oldDocBytes;
            //nullPutMakeSureEnoughCountAndSpaceBinary(newArrayByte);        
        }
    }

    private boolean checkIfTooManyBytes(Document nowUndoingAndPutting){
        if (nowUndoingAndPutting.getDocumentTxt() == null){
            int putDocBytes = nowUndoingAndPutting.getDocumentBinaryData().length;
            if (this.maxDocBytes < putDocBytes){
                return true;
            }
            //nullPutMakeSureEnoughCountAndSpaceBinary(newArrayByte);
        }else{
            int putDocBytes = nowUndoingAndPutting.getDocumentTxt().getBytes().length;
            if (this.maxDocBytes < putDocBytes){
                return true;
            }
            //nullPutMakeSureEnoughCountAndSpaceBinary(newArrayByte);        
        }
        return false;
    }

    private boolean isItInMemory(URI uri, Document doc){
        Set<URI> uriSet = new HashSet<URI>();
        int i = 5;
        while(i == 5){
            try{
                URI currentURI = this.theHeap.remove();
                uriSet.add(currentURI);
            }catch(Exception e){
                break;
            }
        }
        for (URI j: uriSet){
            this.theHeap.insert(j);
        }
        if (uriSet.contains(uri)){
            return true;
        }else{
            return false;
        }
    }

    private void dealWithMemoryLimitsOnSearches(Set<Document> theDocs){
        for (Document w: theDocs){
            if (theDocs == null || theDocs.size() == 0){
                return;
            }
            if (isItInMemory(w.getKey(), w)){
                w.setLastUseTime(System.nanoTime());
                this.theHeap.reHeapify(w.getKey());
            }else{
                //this.triePutWithoutCommandAdding(w.getKey(), w); // dont need, its already in  trie
                this.theBTree.put(w.getKey(), w);
                w.setLastUseTime(System.nanoTime());
                this.theHeap.insert(w.getKey());
                this.nullPutMakeSureEnoughCountAndSpaceTXT(w.getDocumentTxt());
                this.docCounter++;
                this.addDocBytes(w);
            }
            // w.setLastUseTime(System.nanoTime());
            // this.theHeap.reHeapify(w.getKey());
        }

    }
    // if its not in memory, then Ive just moved it back into memory by calling getall. 
    //so, should get this docs bytes and increase counter. then make sure counter and bytescounter are under control. then go vaiter
    // when deleting bc of memeory overflow, its stays in trie, stack and gets written to disk, only kicked out of heap



}