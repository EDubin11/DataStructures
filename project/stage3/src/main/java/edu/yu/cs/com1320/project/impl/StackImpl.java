package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T>{//i think done

    


    private class Node<T>{
        Node next;
        T data;

        Node(T dt){
            this.data = dt;
            this.next = null;
        }

    }
    private Node top;
    private StackImpl<T> stack;

    public StackImpl(){//make sure this is right 
        this.stack = null;//make it equal to starter head node
        this.top = null;

    }

     /**
     * @param element object to add to the Stack
     */
    public void push(T element){//think done
        Node toPush = new Node(element);
        toPush.data = element;
        toPush.next = null;

        if (this.top == null){
            this.top = toPush;
        }else{
            // Node toIterate = this.head;
            // while(head.next != null){
            //     toIterate = toIterate.next;
            // }
            // toIterate.next = toPush;
            // this all wrong. not stack funcitonaltiy 


            // initialize data into temp data field
 
        // put top reference into temp link
        toPush.next = this.top;
 
        // update top reference
        this.top = toPush;
        }


    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    public T pop(){//i think done
        if (this.top == null){
            return null;
        }

        T stuff = (T)this.top.data;
        this.top = this.top.next;
        return stuff;


    }

    /**
     *
     * @return the element at the top of the stack without removing it
     */
    public T peek(){
        if (this.top == null){
            return null;
        }
        return (T)this.top.data;
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    public int size(){//ex: size is 3. top not null so 1. then next not null so 2. then next not null so 3. done
        if (this.top == null){
            return 0;
        }
        Node toAddUp = this.top;
        int adding = 1;
        while(toAddUp.next != null){
            toAddUp = toAddUp.next;
            adding ++;   
        }
        return adding;

         // Node toIterate = this.head;
            // while(head.next != null){
            //     toIterate = toIterate.next;
            // }
            // toIterate.next = toPush;
            // this all wrong. not stack funcitonaltiy 


    }

    // @Override
    // public void push(Object element) {
    //     // TODO Auto-generated method stub
        
    // }

    // @Override
    // public void push(Object element) {
    //     // TODO Auto-generated method stub
        
    // }
    
}
