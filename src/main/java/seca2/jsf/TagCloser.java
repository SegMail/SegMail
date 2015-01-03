/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf;

import java.io.IOException;
import java.util.Stack;
import javax.faces.context.ResponseWriter;

/**
 * Utility class to help manage tags in a stack
 * 
 * @author LeeKiatHaw
 */
public class TagCloser {
    
    private Stack<String> tagStack = new Stack();
    
    public void openTag(String tag){
        tagStack.push(tag);
    }
    
    public String closeTag(){
        return tagStack.pop();
    }
    
    public void closeAllRemainingTags(ResponseWriter writer) throws IOException{
        while(!this.tagStack.empty()){
            writer.endElement(this.closeTag());
        }
    }
}
