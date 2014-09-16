/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.component.data;

import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author vincent.a.lee
 */
public class Package {
    private Stack<String> stack = new Stack<String>();
    
    public void push(String childPackage){
        stack.push(childPackage);
    }
    
    public String pop(){
        return stack.pop();
    }

    public Stack<String> getStack() {
        return stack;
    }

    public void setStack(Stack<String> stack) {
        this.stack = stack;
    }
    
    @Override
    public String toString() {
        String output = "";
        Stack<String> copy = (Stack<String>) stack.clone();
        
        //Printing must be FIFO, not LIFO
        Iterator i = copy.iterator();
        boolean first = true;
        while(i.hasNext()){
            if(first){
                output += i.next();
                first = false;
            }
            else output += "."+i.next();
        }
        
        return output;
    }

    @Override
    protected Package clone() throws CloneNotSupportedException {
        Stack<String> copy = (Stack<String>) stack.clone();
        
        Package newPackage = new Package();
        newPackage.setStack(copy);
        return newPackage;
    }
    
    
}
