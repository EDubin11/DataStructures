//left to do: need to do undo logic. make lambda in deletedocument method. 
//also need to work on the two stack popping in last undo method. and deal with monster methods
//problem: need to be able to delete the doc when calling it in putdocument, whether in method or in a fucntion, and not add to stack a command twice
package edu.yu.cs.com1320.project.stage2.impl;

import java.io.*;
import java.util.Arrays;
import java.util.function.Function;
import java.net.URI;
import java.util.*;
import java.net.*;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;
import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.impl.*;


public class DocumentStoreImpl implements DocumentStore{

    HashTableImpl<URI, Document> hshTable;
    StackImpl<Command> theStack;

    
    // enum DocumentFormat{
    //     TXT,BINARY
    // }


    public DocumentStoreImpl(){
        hshTable = new HashTableImpl<URI, Document>();
        theStack = new StackImpl<Command>();

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
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException{//need to add command to stack for byte
        //see if its binary or txt and then read it diff ways 
        if(uri == null || uri.toASCIIString().isBlank()){
            throw new IllegalArgumentException();
        }
        // int Hashspot = hashFunctionDS(uri);
        if (input == null){//its a delete
            if (hshTable.get(uri) == null){
                deleteDocument(uri);
                //hshTable.put(uri,new DocumentImpl(uri, null));//just do put(uri,null)
                //if this is delete and uri.get is null, then nothing to do so return 0 and thats it
                return 0;
            }else{
                Document n = (Document)hshTable.get(uri);//may not need to parse as document
                int nulltoUseHsh = n.hashCode();
                URI d = uri;
                // if (deleteDocument(uri)){
                //     Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
                //         this.hshTable.put(uriToUse, n);//need to look over unsure abt
                //         return true;
                //     };
                //     Command toPushOn = new Command(uri, undoDel);
                //     this.theStack.push(toPushOn);
                // }else{
                //     //this is a no-op command bc because deleete didint work
                //     Function<URI,Boolean> undoNothing = (URI j) ->  {
                //         int f = 5;
                //         return true;
                //     };
                //     Command toPushOn = new Command(uri, undoNothing);
                // }
                // Function<URI,Boolean> undoDel= uri->  {
                //     this.hshTable.put(uri, n);
                // };
                // Command toPushOn = new Command(uri, undoDel);
                // this.theStack.push(toPushOn);
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
                Function<URI,Boolean> undoAdd = (URI uriToUse) ->  {
                    //deleteDocumentForUse(uriToUse);//need to look over unsure abt
                    this.hshTable.put(uriToUse, null);
                    return true;
                };
                Command toPushOn = new Command(uri, undoAdd);
                //System.out.println("pushed command new doc");
                this.theStack.push(toPushOn);
                return 0;
            }else{
                Document m = (Document)hshTable.get(uri);//maybe all wrong, shouldnt be a need to make new document if one already exists, just change that one
                int toUseHsh = m.hashCode();
                hshTable.put(uri,new DocumentImpl(uri, text));//just change this to put(uri,text). nevermind
                Function<URI,Boolean> undoChange = (URI uriToUse) ->  {
                    this.hshTable.put(uriToUse, m);//need to look over unsure abt
                    return true;
                };
                Command toPushOn = new Command(uri, undoChange);
                //System.out.println("pushed command changed doc");
                this.theStack.push(toPushOn);
                return toUseHsh;
            }
        }else{
            byte[] arrayByte = new byte[input.available()];
            input.read(arrayByte);
            if (hshTable.get(uri) == null){
                hshTable.put(uri,new DocumentImpl(uri, arrayByte));
                Function<URI,Boolean> undoAdd = (URI uriToUse) ->  {
                    // deleteDocumentForUse(uriToUse);//need to look over unsure abt
                    this.hshTable.put(uriToUse, null);
                    return true;
                };
                Command toPushOn = new Command(uri, undoAdd);
                this.theStack.push(toPushOn);
                return 0;
            }else{
                Document a = (Document)hshTable.get(uri);
                int toUseByteHash = a.hashCode();
                hshTable.put(uri,new DocumentImpl(uri, arrayByte));//dont need to create new.nevermind i think
                Function<URI,Boolean> undoChange = (URI uriToUse) ->  {
                    this.hshTable.put(uriToUse, a);//need to look over unsure abt
                    return true;
                };
                Command toPushOn = new Command(uri, undoChange);
                this.theStack.push(toPushOn);
                return toUseByteHash;
            }
        }
    }

     /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri){
        return ((Document)this.hshTable.get(uri));//do i need to parse this????CRYSTAL
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
        if (this.hshTable.get(uri) == null){
            Function<URI,Boolean> undoNothing = (URI j) ->  {
                int f = 5;
                return true;
            };
            Command toPushOn = new Command(uri, undoNothing);
            return false;
        }
        Document doc = hshTable.get(uri);
        // int hashed = hashFunctionDS(uri);
        // while ( != null)
        // hshTable.put(uri, null);
        //instead do: 
        this.hshTable.put(uri,null);
        Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
            this.hshTable.put(uriToUse, doc);//need to look over unsure abt
            return true;
        };
        Command toPushOn = new Command(uri, undoDel);
        this.theStack.push(toPushOn);

        // hshTable.deleteThisTotally(uri);
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
    public void undo() throws IllegalStateException{
        if (this.theStack.size() == 0 || this.theStack == null){
            throw new IllegalStateException();
        }
        Command toDo = this.theStack.pop();
        if (toDo == null){
            throw new IllegalStateException();
        }
        toDo.undo();
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
        StackImpl <Command> newStack = new StackImpl<Command>();
        // Command toUse = this.theStack.peek();
        while (this.theStack.size() > 0){
            Command toUse = this.theStack.pop();
            // if (toUse == null){
            //     throw new IllegalStateException();
            // }
            if (toUse.getUri().equals(uri)){
                //System.out.println(toUse.getUri());
                toUse.undo();
                //System.out.println("undone on uri");
                h++;
                break;


                


        //IF UNDO CALLED ON A URI and its further into the stack, pop everything off befre it one at a time, in between pushing them 
        //onto new stack, and then take off the command undoing, hold it. then take stuff off the new stack, popping it all off. 
        //one by one, push them onto the original command stack, until done. then execute the undo
        //OR: do this- cycel through until . next is the command, then execute undo on that command. then have current.next be equal to current.next.next, and the executed command.next = null. nevermind.
            }else{
                //Command toRePush = this.theStack.pop();
                newStack.push(toUse);
            }

            // if 
            // Command toRePush = this.theStack.pop();
            // newStack.push(toRePush);



        }
        while (newStack.size() > 0){
            Command toRePushonReal = newStack.pop();
            this.theStack.push(toRePushonReal);
        }
        if (h == 0){
            throw new IllegalStateException();
        }
        // throw new IllegalStateException();


        // if its a delete:
        // Function<URI,Boolean> undoDel=(URI uri, Document doc) ->  {
        //     this.hshTable.put(uri, doc);
        // }
        // if its a put on a new uri/doc: 
        // Function<URI,Boolean> undoPutNew=(URI uri, Document doc) ->  {
        //     this.deleteDocument(uri);
        // }
        // if its a put on an edited uri/doc:
        // Function<URI,Boolean> undoPutOld=(URI uri, Document doc) ->  {
        //     this.hshTable.put(uri, doc);
        // }
        //need to  remeber to be adding and deleting from command stack 
        //cycle through command stack 
        //if being called an undo on certain uri, then cycle through and then whatever it hits 
        //also, in my put doc logic i need to account for if its new uri and doc or overwriting old doc in terms of setting up undo funciton
        
        //IF UNDO CALLED ON A URI and its further into the stack, pop everything off befre it one at a time, in between pushing them 
        //onto new stack, and then take off the command undoing, hold it. then take stuff off the new stack, popping it all off. 
        //one by one, push them onto the original command stack, until done. then execute the undo

    }
}
