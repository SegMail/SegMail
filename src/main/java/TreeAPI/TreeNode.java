/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TreeAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * A TreeNode is a atomic unit of a Tree. It is an empty shell that contains the
 * root object and its children. Some data structures might not contain the mechanism
 * of storing children, eg. adjacency lists, and the TreeBuilder API can be used
 * to build the tree.
 * 
 * It can be its own root - any TreeNode
 * is a root.
 * 
 * - Do I need an Iterator?
 * @author vincent.a.lee
 */
public class TreeNode<T extends TreeBranch> {
    
    private T root;
    private TreeNode parent;
    private List<TreeNode> children = new ArrayList<TreeNode>();

    public T getRoot() {
        return root;
    }

    public void setRoot(T root) {
        this.root = root;
    }
    
    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }
    
    public void addChild(TreeNode child){
        children.add(child);
    }
    
}
