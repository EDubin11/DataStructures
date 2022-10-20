package edu.yu.cs.com1320.project.stage5.impl;

import java.net.URI;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import edu.yu.cs.com1320.project.stage5.Document;


public class DocumentPersistenceManagerTest {
    @Test
    public void testPersistence() throws Exception {
        BTree<URI,Document> tree = new BTreeImpl();
        DocumentPersistenceManager manager = new DocumentPersistenceManager(new File("commandSci/thp"));
        tree.setPersistenceManager(manager);
    URI uri1;
    URI uri2;
    String two = "doc2";
    uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
    uri2 = new URI("http://www.test2");
    // public void URIMaker() throws Exception{
    //     uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
    //     uri2 = new URI("http://www.test2");
    // }
    // uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
    // uri2 = new URI("http://www.test2");
    Document doc = new DocumentImpl(uri1, "first doc", null);
    Document doc2 = new DocumentImpl(uri2, two.getBytes());
    tree.put(uri1, doc2);
    tree.moveToDisk(uri1);
    Document h = tree.get(uri1);
    assert(doc2.equals(h));
    URI tempUri = new URI("http://mmsfghwis,khzdaoldi.hfaseweikukghre2uighdewloi.jdqwlihfduw3eirandomiuldzh,n,");
    
    
    }
    


}
