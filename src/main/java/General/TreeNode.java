/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package General;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author KH
 */
public class TreeNode<T> {
    
    private T element;
    private int BREADTH = 0;
    private int DEPTH = 0;
    
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }
    
    /**
     * Returns a list of all elements in the tree in a Depth-First-Search order.
     * 
     * @return List of all elements under tree
     */
    public List<T> DFS(){
        List<T> results = new ArrayList<T>();
        results.add(element); //Adds its own element first
        
        for(TreeNode<T> child:children){
            results.addAll(child.DFS());
        }
        
        return results;
    }
    
    /**
     * Returns a list of all elements in the tree in a Breadth-First-Search order.
     * @return 
     */
    public List<T> BFS(){
        List<T> results = new ArrayList<T>();
        Queue<TreeNode<T>> nextQueue = new LinkedList<TreeNode<T>>();
        
        results.add(element); //Adds its own element first
        
        for(TreeNode<T> child:children){
            nextQueue.add(child);
        }
        
        for(TreeNode<T> next:nextQueue){
            results.addAll(next.BFS());
        }
        
        return results;
    }
    
}
