package edu.cs.com1320.project.impl;
import java.io.*;
import java.util.Arrays;
import java.util.*;
import java.net.*;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import edu.yu.cs.com1320.project.stage1.impl.*;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


public class DocStoreimplTest{
    @Test 
    public void dsimpl() throws URISyntaxException, IOException{
        DocumentStore hsh = new DocumentStoreImpl();
        URI a = new URI("http/wha");
        URI b = new URI("http/whb");
        URI c = new URI("http/whc");
        URI d = new URI("http/whd");
        URI e = new URI("http/whe");
        URI f = new URI("http/whf");
        URI j = new URI("http/whj");

        Document j1 = new DocumentImpl(a,"string 1");
        Document j2 = new DocumentImpl(b,"string 2");
        Document j3 = new DocumentImpl(c,"string 3");
        Document j4 = new DocumentImpl(d,"string 4");
        Document j5 = new DocumentImpl(e,"string 5");
        Document j6 = new DocumentImpl(f,"string 6");
        Document j7 = new DocumentImpl(e, "null");


        InputStream stream = new ByteArrayInputStream("string 1".getBytes(Charset.forName("UTF-8")));
        InputStream stream2 = new ByteArrayInputStream("string 2".getBytes(Charset.forName("UTF-8")));
        InputStream stream3= new ByteArrayInputStream("string 3".getBytes(Charset.forName("UTF-8")));
        InputStream stream4 = new ByteArrayInputStream("string 4".getBytes(Charset.forName("UTF-8")));
        InputStream stream5 = new ByteArrayInputStream("string 5".getBytes(Charset.forName("UTF-8")));
        InputStream stream6 = new ByteArrayInputStream("string 6".getBytes(Charset.forName("UTF-8")));
        InputStream stream7 = new ByteArrayInputStream("string 7".getBytes(Charset.forName("UTF-8")));
        InputStream stream8 = new ByteArrayInputStream("string 8".getBytes(Charset.forName("UTF-8")));

        
       hsh.putDocument(stream2,b, DocumentFormat.TXT);

        hsh.putDocument(stream,a, DocumentFormat.TXT);
        hsh.putDocument(stream3,c, DocumentFormat.TXT);
        hsh.putDocument(stream4,d, DocumentFormat.TXT);
        hsh.putDocument(stream5,e, DocumentFormat.TXT);
        hsh.putDocument(stream6,f, DocumentFormat.TXT);
        hsh.putDocument(stream8,j, DocumentFormat.TXT);

        System.out.println(hsh.getDocument(a));
        System.out.println(hsh.getDocument(b));
        hsh.putDocument(stream7,a, DocumentFormat.TXT);

        System.out.println("1st" + hsh.getDocument(a));
        System.out.println("2nd" + j1);

        System.out.println(hsh.getDocument(b));
        System.out.println(hsh.getDocument(c));
        System.out.println(hsh.getDocument(d));
        System.out.println(hsh.getDocument(e));
        System.out.println(hsh.getDocument(f));




        hsh.deleteDocument(a);

        System.out.println(a);

        









        // Integer f = 9;
        // Integer v = 8;
        // Integer p = 4;
        // hsh.put(f, v);
        // hsh.put(f,p);

        System.out.println(hsh.getDocument(a));

        
        // System.out.println("hi");
    }
    // public static void main(String[] args){
    //     hsh();
    //  }

    
}