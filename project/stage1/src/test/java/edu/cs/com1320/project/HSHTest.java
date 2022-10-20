 package edu.cs.com1320.project;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.*;


public class HSHTest{
    @Test 
    public void hsh(){
        HashTable hsh = new HashTableImpl<>();
        Integer f = 9;
        Integer v = 8;
        Integer p = 4;
        Integer a = 1;
        Integer b = 18937;
        Integer w = 432;
        Integer abc = 123;
        Integer d = 32;
        Integer toUse = 20;
        Integer y = 473;
        Integer x = 84;



        hsh.put(f, v);
        hsh.put(d,p);
        hsh.put(b,w);
        hsh.put(a,p);
        hsh.put(abc,toUse);
        hsh.put(y,x);





        System.out.println(hsh.get(f));
        System.out.println(hsh.get(d));
        System.out.println(hsh.get(b));
        System.out.println(hsh.get(a));
        System.out.println(hsh.get(abc));
        System.out.println(hsh.get(y));






        // System.out.println("hi");
    }
    // public static void main(String[] args){
    //     hsh();
    //  }

    
}