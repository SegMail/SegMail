/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TreeAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * A TreeNode is a atomic unit of a Tree. It can be its own root - any TreeNode
 * is a root. 
 * 
 * - Do I need an Iterator?
 * @author vincent.a.lee
 */
public class TreeNode<T> {
    
    private T root;
    private List<T> children = new ArrayList<T>();

    public T getRoot() {
        return root;
    }

    public void setRoot(T root) {
        this.root = root;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
    
    public void addChild(T child){
        this.children.add(child);
    }
    
}
