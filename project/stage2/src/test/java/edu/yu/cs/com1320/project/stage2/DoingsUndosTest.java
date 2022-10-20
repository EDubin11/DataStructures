package edu.yu.cs.com1320.project.stage2;

import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.stage2.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class DoingsUndosTest {

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;

     //variables to hold possible values for doc3
     private URI uri3;
     private String txt3;

      //variables to hold possible values for doc4
    private URI uri4;
    private String txt4;

     //variables to hold possible values for doc5
     private URI uri5;
     private String txt5;

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "This is the text of doc1, in plain text. No fancy file format - just plain old String";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text for doc2. A plain old String.";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "Text for doc3. A plain old String.";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "Text for doc4. A plain old String.";

        //init possible values for doc5
        this.uri5 = new URI("http://edu.yu.cs/com1320/project/doc5");
        this.txt5 = "Text for doc5. A plain old String.";





    }

    @Test
    public void testPutNewVersionOfDocumentTxt() throws IOException {
        //put the first version
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
        assertEquals(this.txt1,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");

        //put the second version, testing both return value of put and see if it gets the correct text
        returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(Utils.calculateHashCode(this.uri1, this.txt1,null) == returned,"should return hashcode of old text");
        assertEquals(this.txt2,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");
    }


    @Test
    public void testPutandDoUndoToGetOldVersion() throws Exception {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
        assertEquals(this.txt1,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");
        try {
            store.undo(this.uri1);
        } catch (IllegalStateException e) {
            System.out.println("didnt Worked");
        }
        Document n = store.getDocument(this.uri1);
        assertNull(n,"should be null");


        try {
            store.undo(this.uri1);
            System.out.println("oh no");
        } catch (IllegalStateException e) {
            System.out.println("Worked!");
        }
        


        // int returneddoc2 = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        // assertTrue(returned == 0);
        // assertEquals(this.txt2,store.getDocument(this.uri2).getDocumentTxt(),"failed to return correct text");


        // int returneddpc3 = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        // assertTrue(returned == 0);
        // assertEquals(this.txt3,store.getDocument(this.uri3).getDocumentTxt(),"failed to return correct text");


        // int returnedoc4 = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        // assertTrue(returned == 0);
        // assertEquals(this.txt4,store.getDocument(this.uri4).getDocumentTxt(),"failed to return correct text");

        // int returneddoc5 = store.putDocument(new ByteArrayInputStream(this.txt5.getBytes()),this.uri5, DocumentStore.DocumentFormat.TXT);
        // assertTrue(returned == 0);
        // assertEquals(this.txt5,store.getDocument(this.uri5).getDocumentTxt(),"failed to return correct text");




        int returned1 = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned1 == 0);
        //assertTrue(Utils.calculateHashCode(this.uri1, this.txt1,null) == returned1,"should return hashcode of old text");
        assertEquals(this.txt2,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");

        store.undo(this.uri1);
        Document h = store.getDocument(this.uri1);
        assertNull(h);
        //assertEquals(this.txt1,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");
        //System.out.println(store.getStackSize());
        try {
            store.undo(this.uri1);
        } catch (IllegalStateException e) {
            System.out.println("Worked");
        }
         n = store.getDocument(this.uri1);
        assertNull(n,"should be null");
        //assertEquals(this.txt1,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");
        try {
            store.undo(this.uri1);
            System.out.println("oy");
        } catch (IllegalStateException e) {
            System.out.println("Workewd now");
        }
        try {
            store.undo(this.uri1);
            System.out.println("oy");
        } catch (IllegalStateException e) {
            System.out.println("good");
            // throw new IllegalStateException("Worked");
        }
        // store.undo();


    }
}
