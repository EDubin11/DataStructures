// //left to do: need to do undo logic. make lambda in deletedocument method. 
// //also need to work on the two stack popping in last undo method. and deal with monster methods
// //problem: need to be able to delete the doc when calling it in putdocument, whether in method or in a fucntion, and not add to stack a command twice

// //need to go through all the methods, dealing with new undos and making sure everything matches up and makes sense. 
// //the actual undo methods iyH I dont think will be so bad. just making sure the stack is right is hard 


// package edu.yu.cs.com1320.project.stage3.impl;

// import java.io.*;
// import java.util.function.Function;

// import org.apache.maven.surefire.api.runorder.ThreadedExecutionScheduler;

// import java.net.URI;
// import java.util.*;
// import java.net.*;
// import edu.yu.cs.com1320.project.stage3.Document;
// import edu.yu.cs.com1320.project.stage3.DocumentStore;
// import edu.yu.cs.com1320.project.GenericCommand;
// import edu.yu.cs.com1320.project.CommandSet;
// import edu.yu.cs.com1320.project.Undoable;
// import edu.yu.cs.com1320.project.impl.*;


// public class DocumentStoreImpl implements DocumentStore{

//     private HashTableImpl<URI, Document> hshTable;
//     private StackImpl<Undoable> theStack;
//     private TrieImpl<Document> theTrie;

    
//     // enum DocumentFormat{
//     //     TXT,BINARY
//     // }


//     // private class DocumentComparator<Value>{
//     //     DocumentComparator<Document> docComparator = (Document a, Document b, String j) -> (a.word_count(j) - b.word_count(j));

//     // }
    
//     public DocumentStoreImpl(){
//         hshTable = new HashTableImpl<URI, Document>();
//         theStack = new StackImpl<Undoable>();
//         theTrie = new TrieImpl<Document>();


//         //work on this
//     }
    
//     /**
//      * @param input the document being put
//      * @param uri unique identifier for the document
//      * @param format indicates which type of document format is being passed
//      * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. 
//      * If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
//      * @throws IOException if there is an issue reading input
//      * @throws IllegalArgumentException if uri or format are null
//      */
//     public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException{//need to add undo support for all non-deletes in this method
//         //see if its binary or txt and then read it diff ways 
//         if(uri == null || uri.toASCIIString().isBlank()){
//             throw new IllegalArgumentException();
//         }
//         // int Hashspot = hashFunctionDS(uri);
//         if (input == null){//its a delete
//             if (hshTable.get(uri) == null){
//                 deleteDocument(uri);
//                 //hshTable.put(uri,new DocumentImpl(uri, null));//just do put(uri,null)
//                 //if this is delete and uri.get is null, then nothing to do so return 0 and thats it
//                 return 0;
//             }else{
//                 Document n = (Document)hshTable.get(uri);//may not need to parse as document
//                 int nulltoUseHsh = n.hashCode();
//                 URI d = uri;
//                 // if (deleteDocument(uri)){
//                 //     Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
//                 //         this.hshTable.put(uriToUse, n);//need to look over unsure abt
//                 //         return true;
//                 //     };
//                 //     Command toPushOn = new Command(uri, undoDel);
//                 //     this.theStack.push(toPushOn);
//                 // }else{
//                 //     //this is a no-op command bc because deleete didint work
//                 //     Function<URI,Boolean> undoNothing = (URI j) ->  {
//                 //         int f = 5;
//                 //         return true;
//                 //     };
//                 //     Command toPushOn = new Command(uri, undoNothing);
//                 // }
//                 // Function<URI,Boolean> undoDel= uri->  {
//                 //     this.hshTable.put(uri, n);
//                 // };
//                 // Command toPushOn = new Command(uri, undoDel);
//                 // this.theStack.push(toPushOn);
//                 deleteDocument(uri);
//                 return nulltoUseHsh;
//             }
//         }
//         if (format == null){
//             throw new IllegalArgumentException();
//         }
//         //NOTE: MUST ACCOUNT FOR LINE ABT DELTE IN INSTRICTIONS
//         if (format == DocumentFormat.TXT){
//             // if (input == null){//this is dealing with delete. must also do with the binary if statement 
//             //     if (hshTable.get(uri) == null){
//             //         //hshTable.put(uri,new DocumentImpl(uri, null));//just do put(uri,null)
//             //         //if this is delete and uri.get is null, then nothing to do so return 0 and thats it
//             //         return 0;
//             //     }else{
//             //         Document n = (Document)hshTable.get(uri);//may not need to parse as document
//             //         int nulltoUseHsh = n.hashCode();
//             //         deleteDocument(uri);
//             //         return nulltoUseHsh;
//             //     }
//             // }//this null input stream is done I think
//             String text = new String(input.readAllBytes());
//             if (hshTable.get(uri) == null){
//                 Document q = new DocumentImpl(uri, text);
//                 hshTable.put(uri, q);
//                 for (String word: q.getWords()){
//                     this.theTrie.put(word, q);
//                 }
//                 Function<URI,Boolean> undoAdd = (URI uriToUse) ->  {
//                     //deleteDocumentForUse(uriToUse);//need to look over unsure abt
//                     this.hshTable.put(uriToUse, null);
//                     //this.deleteDocumentFromTrie(q);

//                     //CommandSet<URI> result = new CommandSet<>();
//                     for (String word: q.getWords()){//just put this all in the deleteAll method
//                         List<Document> docss = this.theTrie.getAllSorted(word, (doca,docb) -> {
//                             if(doca.wordCount(word) - docb.wordCount(word) > 0 ){
//                                 return -1;
//                             }else if(doca.wordCount(word) - docb.wordCount(word) < 0) {
//                                 return 1;
//                             }else{
//                                 return 0;
//                             }
//                         });
//                         // docss.remove(q);
//                         // this.theTrie.put(word,null);
//                         // for (Document a: docss){
//                         //     this.theTrie.put(word, a);
//                         // }
//                         this.theTrie.delete(word,q);
//                     }

//                     return true;
//                 };
//                 GenericCommand toPushOn = new GenericCommand(uri, undoAdd);
//                 //System.out.println("pushed command new doc");
//                 this.theStack.push(toPushOn);
//                 return 0;
//             }else{//fix 
//                 Document m = (Document)hshTable.get(uri);//maybe all wrong, shouldnt be a need to make new document if one already exists, just change that one
//                 int toUseHsh = m.hashCode();
//                 Document doc = new DocumentImpl(uri, text);
//                 hshTable.put(uri,doc);//just change this to put(uri,text). nevermind
//                 for (String word: m.getWords()){//just put this all in the deleteAll method
//                     List<Document> docss = this.theTrie.getAllSorted(word, (doca,docb) -> {
//                         if(doca.wordCount(word) - docb.wordCount(word) > 0 ){
//                             return -1;
//                         }else if(doca.wordCount(word) - docb.wordCount(word) < 0) {
//                             return 1;
//                         }else{
//                             return 0;
//                         }
//                     });
//                     // docss.remove(m);
//                     // this.theTrie.put(word,null);
//                     // for (Document a: docss){
//                     //     this.theTrie.put(word, a);
//                     // }
//                     this.theTrie.delete(word,m);
//                 }
//                 for (String word: doc.getWords()){//just put this all in the deleteAll method
//                     this.theTrie.put(word,doc);
//                 }
//                 Function<URI,Boolean> undoChange = (URI uriToUse) ->  {
//                     this.hshTable.put(uriToUse, m);//need to look over unsure abt
//                     //this.deleteDocumentFromTrie(m);

//                     CommandSet<URI> result = new CommandSet<>();
//                     for (String word: doc.getWords()){//just put this all in the deleteAll method
//                         List<Document> docss = this.theTrie.getAllSorted(word, (doca,docb) -> {
//                             if(doca.wordCount(word) - docb.wordCount(word) > 0 ){
//                                 return -1;
//                             }else if(doca.wordCount(word) - docb.wordCount(word) < 0) {
//                                 return 1;
//                             }else{
//                                 return 0;
//                             }
//                         });
//                         docss.remove(doc);
//                         this.theTrie.put(word,null);
//                         for (Document a: docss){
//                             this.theTrie.put(word, a);
//                         }
//                     }


//                     for (String word: m.getWords()){
//                         this.theTrie.put(word, m);
//                     }
//                     return true;
//                 };
//                 // for (String word: m.getWords()){
//                 //         this.theTrie.put(word, m);
//                 //     }
//                 GenericCommand toPushOn = new GenericCommand(uri, undoChange);
//                 //System.out.println("pushed command changed doc");
//                 this.theStack.push(toPushOn);
//                 return toUseHsh;
//             }
//         }else{
//             byte[] arrayByte = new byte[input.available()];
//             input.read(arrayByte);
//             if (hshTable.get(uri) == null){
//                 hshTable.put(uri,new DocumentImpl(uri, arrayByte));
//                 Function<URI,Boolean> undoAdd = (URI uriToUse) ->  {
//                     // deleteDocumentForUse(uriToUse);//need to look over unsure abt
//                     this.hshTable.put(uriToUse, null);
//                     return true;
//                 };
//                 GenericCommand toPushOn = new GenericCommand(uri, undoAdd);
//                 this.theStack.push(toPushOn);
//                 return 0;
//             }else{
//                 Document a = (Document)hshTable.get(uri);
//                 int toUseByteHash = a.hashCode();
//                 hshTable.put(uri,new DocumentImpl(uri, arrayByte));//dont need to create new.nevermind i think
//                 Function<URI,Boolean> undoChange = (URI uriToUse) ->  {
//                     this.hshTable.put(uriToUse, a);//need to look over unsure abt
//                     return true;
//                 };
//                 GenericCommand toPushOn = new GenericCommand(uri, undoChange);
//                 this.theStack.push(toPushOn);
//                 return toUseByteHash;
//             }
//         }
//     }

//      /**
//      * @param uri the unique identifier of the document to get
//      * @return the given document
//      */
//     public Document getDocument(URI uri){
//         return ((Document)this.hshTable.get(uri));//do i need to parse this????CRYSTAL
//     }


//     // private boolean deleteDocumentForUse(URI uri){
//     //     if (this.hshTable.get(uri) == null){
//     //         return false;
//     //     }
//     //     this.hshTable.put(uri, null);
//     //     return true;
//     // }

//     /**
//      * @param uri the unique identifier of the document to delete
//      * @return true if the document is deleted, false if no document exists with that URI
//      */
//     public boolean deleteDocument(URI uri){//need to look over. also look at the put method in hshtable and see iof can get the boolean to return so know if deletred or not in order to strcutuer command and no op
//         //need to put in all functionality with the trie and the undos. 
//         if (this.hshTable.get(uri) == null){
//             Function<URI,Boolean> undoNothing = (URI j) ->  {
//                 int f = 5;
//                 return true;
//             };
//             GenericCommand toPushOn = new GenericCommand(uri, undoNothing);
//             return false;
//         }
//         Document doc = hshTable.get(uri);
//         GenericCommand<URI> command = deleteDocumentFromTrie(doc);

        

//         // int hashed = hashFunctionDS(uri);
//         // while ( != null)
//         hshTable.put(uri, null);
//         //instead do: 
//         //this.hshTable.put(uri,null);
//         // for (String word: doc.getWords()){//just put this all in the deleteAll method
//         //     List<Document> docss = this.theTrie.getAllSorted(word, (doca,docb) -> {
//         //         if(doca.wordCount(word) - docb.wordCount(word) > 0 ){
//         //             return -1;
//         //         }else if(doca.wordCount(word) - docb.wordCount(word) < 0) {
//         //             return 1;
//         //         }else{
//         //             return 0;
//         //         }
//         //     });
//         //     docss.remove(doc);
//         //     this.theTrie.put(word,null);
//         //     for (Document a: docss){
//         //         this.theTrie.put(word, a);
//         //     }
//         //     // this.theTrie.put(word, docss);
//         // }//need to make this include adding new commands for every action i do to a commandSet in this loop.
//         // the undo of this would be semi easy, just adding the doc back on to all of the words it has. 
//         // Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
//         //     this.hshTable.put(uriToUse, doc);//need to look over unsure abt
//         //     return true;
//         // };
//         // GenericCommand toPushOn = new GenericCommand(uri, undoDel);
//         this.theStack.push(command);

//         // hshTable.deleteThisTotally(uri);
//         return true;
//     }

//     // private int hashFunctionDS(URI uri){
//     //     return (uri.hashCode() & 0x7fffffff) % this.hshTable.length;//maybe just made instacen variable that gets changed when resize array 
//     // }

//     //need tpo build stack. should do nested class of command and then itll have a next and previous and of course the action 
//     //need to figure out how to store the function 
    

//     /**
//      * undo the last put or delete command
//      * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
//      */
//     public void undo() throws IllegalStateException{//i think done. check the casts and instanceOf if should have<Value> or <URI> or not
//         if (this.theStack.size() == 0 || this.theStack == null){
//             throw new IllegalStateException();
//         }
        


//         Undoable toDo = this.theStack.pop();
//         if (toDo == null){
//             throw new IllegalStateException();
//         }
//         if (toDo instanceof CommandSet){
//             CommandSet toUndoAll = (CommandSet)toDo;
//             toUndoAll.undoAll();
//         }else{
//             GenericCommand<URI> toUndoThis = (GenericCommand)toDo;
//             toUndoThis.undo();
//         }
//         //toDo.undo();
//         // Function<URI,Boolean> toApply = toDo.undo; 
//         // toApply.apply(toDO.uri);
        
//     }

//     /**
//      * undo the last put or delete that was done with the given URI as its key
//      * @param uri
//      * @throws IllegalStateException if there are no actions on the command stack for the given URI
//      */
//     public void undo(URI uri) throws IllegalStateException{
//         if (uri == null){
//             throw new IllegalArgumentException();
//         }
//         if (this.theStack.size() == 0 || this.theStack == null){
//             throw new IllegalStateException();
//         }
//         int h = 0;
//         StackImpl <Undoable> newStack = new StackImpl<Undoable>();
//         // Command toUse = this.theStack.peek();
//         while (this.theStack.size() > 0){
//             Undoable toUse = this.theStack.pop();
//             //if (this.theStack.pop().class == GenericCommand.class){}
//             if (toUse instanceof CommandSet){
//                 CommandSet toUse1 = (CommandSet)toUse;
//                 if (toUse1.containsTarget(uri)){
//                     //toUse = (CommandSet)toUse;
//                     Boolean k = toUse1.undo(uri);
//                 //System.out.println("undone on uri");
//                 h++;
//                 break;
//                 }else{
//                     newStack.push(toUse1);
//                 }
//             }else{
//                 GenericCommand toUse2 = (GenericCommand)toUse;
//                 URI ur = (URI)toUse2.getTarget();
//                 if (ur.equals(uri)){
//                     toUse2.undo();
//                     h++;
//                     break;
//                 }else{
//                     newStack.push(toUse2);
//                 }
//             }
//         }
//         //     // if (toUse == null){
//         //     //     throw new IllegalStateException();
//         //     // }
//         //     if (toUse.getUri().equals(uri)){
//         //         //System.out.println(toUse.getUri());
//         //         toUse.undo();
//         //         //System.out.println("undone on uri");
//         //         h++;
//         //         break;


                


//         // //IF UNDO CALLED ON A URI and its further into the stack, pop everything off befre it one at a time, in between pushing them 
//         // //onto new stack, and then take off the command undoing, hold it. then take stuff off the new stack, popping it all off. 
//         // //one by one, push them onto the original command stack, until done. then execute the undo
//         // //OR: do this- cycel through until . next is the command, then execute undo on that command. then have current.next be equal to current.next.next, and the executed command.next = null. nevermind.
//         //     }else{
//         //         //Command toRePush = this.theStack.pop();
//         //         newStack.push(toUse);
//         //     }

//             // if 
//             // Command toRePush = this.theStack.pop();
//             // newStack.push(toRePush);



        
//         while (newStack.size() > 0){
//             Undoable toRePushonReal = newStack.pop();
//             this.theStack.push(toRePushonReal);
//         }
//         if (h == 0){
//             throw new IllegalStateException();
//         }
//         // throw new IllegalStateException();


//         // if its a delete:
//         // Function<URI,Boolean> undoDel=(URI uri, Document doc) ->  {
//         //     this.hshTable.put(uri, doc);
//         // }
//         // if its a put on a new uri/doc: 
//         // Function<URI,Boolean> undoPutNew=(URI uri, Document doc) ->  {
//         //     this.deleteDocument(uri);
//         // }
//         // if its a put on an edited uri/doc:
//         // Function<URI,Boolean> undoPutOld=(URI uri, Document doc) ->  {
//         //     this.hshTable.put(uri, doc);
//         // }
//         //need to  remeber to be adding and deleting from command stack 
//         //cycle through command stack 
//         //if being called an undo on certain uri, then cycle through and then whatever it hits 
//         //also, in my put doc logic i need to account for if its new uri and doc or overwriting old doc in terms of setting up undo funciton
        
//         //IF UNDO CALLED ON A URI and its further into the stack, pop everything off befre it one at a time, in between pushing them 
//         //onto new stack, and then take off the command undoing, hold it. then take stuff off the new stack, popping it all off. 
//         //one by one, push them onto the original command stack, until done. then execute the undo

//     }

//     public List<Document> search(String keyword){
//         //lowercase
//         keyword = keyword.toLowerCase();
//         String theWord = keyword;
//         return this.theTrie.getAllSorted(theWord, (doca,docb) -> {
//             if(doca.wordCount(theWord) - docb.wordCount(theWord) > 0 ){
//                 return -1;
//             }else if(doca.wordCount(theWord) - docb.wordCount(theWord) < 0) {
//                 return 1;
//             }else{
//                 return 0;
//             }
//         });

//     }

//     /**
//      * Retrieve all documents whose text starts with the given prefix
//      * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
//      * Search is CASE INSENSITIVE.
//      * @param keywordPrefix
//      * @return a List of the matches. If there are no matches, return an empty list.
//      */
//     public List<Document> searchByPrefix(String keywordPrefix){
//         keywordPrefix = keywordPrefix.toLowerCase();
//         String theWord = keywordPrefix;
//         List<Document> results = this.theTrie.getAllWithPrefixSorted(theWord, (doca,docb) -> {
//             int numberOfTimes = 0;
//             for (String word : doca.getWords()) {
//                 int h = doca.wordCount(word);
//                 if (word.startsWith(theWord)){
//                     numberOfTimes += h;
//                 }
//             }
//             int numberOfTimesB = 0;
//             for (String word : docb.getWords()) {
//                 int s = docb.wordCount(word);
//                 if (word.startsWith(theWord)){
//                     numberOfTimesB += s;
//                 }
//             }
//             if(numberOfTimes - numberOfTimesB > 0 ){
//                 return -1;
//             }else if(numberOfTimes - numberOfTimesB < 0) {
//                 return 1;
//             }else{
//                 return 0;
//             }
//         });
//         if (results == null) {
//             return new ArrayList<Document>();
//         }else{
//             return results;
//         }
//     }

//     /**
//      * Completely remove any trace of any document which contains the given keyword
//      * @param keyword
//      * @return a Set of URIs of the documents that were deleted.
//      */
//     public Set<URI> deleteAll(String keyword){//need to fix comparator
//         keyword = keyword.toLowerCase();
//         String theWord = keyword;
//         List<Document> results = this.theTrie.getAllSorted(theWord, (doca,docb) -> {
//             if(doca.wordCount(theWord) - docb.wordCount(theWord) > 0 ){
//                 return -1;
//             }else if(doca.wordCount(theWord) - docb.wordCount(theWord) < 0) {
//                 return 1;
//             }else{
//                 return 0;
//             }
//         });
//         Set<URI> removed = new HashSet<URI>();
//         Set<Document> theDocs = new HashSet<>(results);//this may be wrong. can have same documents twice, but cant have URI twice
//        // Set<GenericCommand<URI>> setOfCommands= new HashSet<GenericCommand<URI>>();
//        CommandSet<URI> cmdSet= new CommandSet<>();
//         for (Document toDel: theDocs){
//             URI uri = toDel.getKey();
//             this.hshTable.put(uri,null);
//             //GenericCommand<URI> commandtoAdd = this.deleteDocumentFromTrie(toDel);

//             //CommandSet<URI> result = new CommandSet<>();
//          for (String word: toDel.getWords()){//just put this all in the deleteAll method
//             List<Document> docss = this.theTrie.getAllSorted(word, (doca,docb) -> {
//                 if(doca.wordCount(word) - docb.wordCount(word) > 0 ){
//                     return -1;
//                 }else if(doca.wordCount(word) - docb.wordCount(word) < 0) {
//                     return 1;
//                 }else{
//                     return 0;
//                 }
//             });
//             // docss.remove(toDel);
//             // this.theTrie.put(word,null);
//             // for (Document a: docss){
//             //     this.theTrie.put(word, a);
//             // }
//             this.theTrie.delete(word,toDel);
//         }
//         Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
//             this.hshTable.put(uriToUse, toDel);//need to look over unsure abt
//             for (String word: toDel.getWords()){//just put this all in the deleteAll method
//                 this.theTrie.put(word,toDel);
//             }
//             return true;
//         };
//         GenericCommand<URI> cmd = new GenericCommand<URI>(toDel.getKey(), undoDel);
            

//             removed.add(uri);
//             // Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
//             //     this.hshTable.put(uriToUse, toDel);//need to look over unsure abt
//             //     return true;
//             // };
//             // GenericCommand<URI> toPushOn = new GenericCommand<>(uri, undoDel);
//             // cmdSet.addCommand(toPushOn);
//             cmdSet.addCommand(cmd);
//             //this.theStack.push(setOfCommands);
//         }
//         this.theStack.push(cmdSet);
//         //gonna have to add commandSet to commandstack for all of these deletes.
//         return removed;
//     }

//     /**
//      * Completely remove any trace of any document which contains a word that has the given prefix
//      * Search is CASE INSENSITIVE.
//      * @param keywordPrefix
//      * @return a Set of URIs of the documents that were deleted.
//      */
//     public Set<URI> deleteAllWithPrefix(String keywordPrefix){
//         Set<URI> result = new HashSet<URI>();
//         if (keywordPrefix == null || keywordPrefix.isEmpty() || keywordPrefix.equals("")){
//             return result;
//         }
//         keywordPrefix = keywordPrefix.toLowerCase();
//         String theWord = keywordPrefix;
//         Set<Document> deleted = this.theTrie.deleteAllWithPrefix(theWord);
//         // or could do: this.searchByPrefix(theWord);
//         // List<Document> docs = this.theTrie.getAllWithPrefixSorted(theWord, (doca,docb) -> {
//         //     int numberOfTimes = 0;
//         //     for (String word : doca.getWords()) {
//         //         int h = doca.wordCount(word);
//         //         if (word.startsWith(theWord)){
//         //             numberOfTimes += h;
//         //         }
//         //     }
//         //     int numberOfTimesB = 0;
//         //     for (String word : docb.getWords()) {
//         //         int s = docb.wordCount(word);
//         //         if (word.startsWith(theWord)){
//         //             numberOfTimesB += s;
//         //         }
//         //     }
//         //     if(numberOfTimes - numberOfTimesB > 0 ){
//         //         return -1;
//         //     }else if(numberOfTimes - numberOfTimesB < 0) {
//         //         return 1;
//         //     }else{
//         //         return 0;
//         //     }
//         // });
//         List<Document> prefixDocs = this.searchByPrefix(keywordPrefix);
//         //Set<URI> result = new HashSet<URI>();
//         CommandSet<URI> cmdSet= new CommandSet<>();
//         Set<Document> theDocs = new HashSet<>(prefixDocs);
//         //Set<GenericCommand<URI>> setOfCommands= new HashSet<GenericCommand<URI>>();
//         for (Document toDel: deleted){
//             URI uri = toDel.getKey();
//             this.hshTable.put(uri,null);
//             //GenericCommand<URI> command = this.deleteDocumentFromTrie(toDel);


//         CommandSet<URI> newresult = new CommandSet<>();
//         //  for (String word: toDel.getWords()){//just put this all in the deleteAll method
//         //     List<Document> docss = this.theTrie.getAllSorted(word, (doca,docb) -> {
//         //         if(doca.wordCount(word) - docb.wordCount(word) > 0 ){
//         //             return -1;
//         //         }else if(doca.wordCount(word) - docb.wordCount(word) < 0) {
//         //             return 1;
//         //         }else{
//         //             return 0;
//         //         }
//         //     });
//         //     this.theTrie.delete(word,toDel);
//         //     // List<Document> tester = this.theTrie.getAllSorted("this");
//         //     //  System.out.println(tester);
//         // //    docss.remove(toDel);
//         // //    this.theTrie.put(word,null);
//         // //    for (Document a: docss){
//         // //        this.theTrie.put(word, a);
//         // //    }
//         // }
//         // Function<URI,Boolean> undoDels = (URI uriToUse) ->  {
//         //     this.hshTable.put(uriToUse, toDel);//need to look over unsure abt
//         //     for (String word: toDel.getWords()){//just put this all in the deleteAll method
//         //         this.theTrie.put(word, toDel);
//         //     }
//         //     return true;
//         // };
//         // GenericCommand<URI> cmd = new GenericCommand<URI>(toDel.getKey(), undoDels);


//         result.add(uri);
//         Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
//             this.hshTable.put(uriToUse, toDel);//need to look over unsure abt
//             for (String word: toDel.getWords()){//just put this all in the deleteAll method
//                 this.theTrie.put(word, toDel);
//             }
//             return true;
//         };

//         GenericCommand<URI> toPushOn = new GenericCommand(uri, undoDel);
//         cmdSet.addCommand(toPushOn);
//         //this.theStack.push(setOfCommands);
//         }
//         this.theStack.push(cmdSet);

//         // for (Document doc : docs){
//         //     this.hshTable.put(doc.getKey(), null);

//         //     // Boolean a = deleteDocument(doc.getKey());
//         //     // if (a){
//         //     //     result.add(doc.getKey());
//         //     // }
//         // }
//         return result;
//     }

//     private GenericCommand<URI> deleteDocumentFromTrie(Document m){
//         CommandSet<URI> result = new CommandSet<>();
//          for (String word: m.getWords()){//just put this all in the deleteAll method
//             List<Document> docss = this.theTrie.getAllSorted(word, (doca,docb) -> {
//                 if(doca.wordCount(word) - docb.wordCount(word) > 0 ){
//                     return -1;
//                 }else if(doca.wordCount(word) - docb.wordCount(word) < 0) {
//                     return 1;
//                 }else{
//                     return 0;
//                 }
//             });

//             // docss.remove(m);
//             // this.theTrie.put(word,null);
//             // for (Document a: docss){
//             //     this.theTrie.put(word, a);
//             // }
//             this.theTrie.delete(word,m);
//         }
//         Function<URI,Boolean> undoDel = (URI uriToUse) ->  {
//             this.hshTable.put(uriToUse, m);//need to look over unsure abt
//             for (String word: m.getWords()){//just put this all in the deleteAll method
//                 this.theTrie.put(word,m);
//             }
//             return true;
//         };
//         GenericCommand<URI> cmd = new GenericCommand<>(m.getKey(), undoDel);
//         return cmd;
//     }

//     private int triePutWithoutCommandAdding(URI uri, Document doc){
//         for (String word: doc.getWords()){//just put this all in the deleteAll method
//             this.theTrie.put(word,doc);
//         }
//         return 1;
//     }

//     private int prefixAppearsInDoc(Document doc, String prefix){
//         int numberOfTimes = 0;
//         for (String word : doc.getWords()) {
//             int h = doc.wordCount(word);
//             if (word.startsWith(prefix)){
//                 numberOfTimes += (h);
//             }
//         }
//         return numberOfTimes;
//     }
//     // private int comparator(Document a, Document b, String word){
//     //     return a.wordCount(word) - b.wordCount(word);
//     // }


// }
