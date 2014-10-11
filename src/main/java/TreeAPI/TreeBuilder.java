/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TreeAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author vincent.a.lee
 */
public class TreeBuilder {
    
    /**
     * Give it a collection of branches and it gives you a full-grown tree.
     * 
     * @param allBranches
     * @return list of all roots
     */
    public static List<TreeNode<T>> buildTree(List<TreeBranch<T>> allBranches) {
        
    }
    
    public static List<TreeNode> buildTreeByParent(List<TreeBranch> allBranches) {
        List<TreeBranch> copyOfBranches = new ArrayList<TreeBranch>(allBranches);
        
        List<TreeNode> allRoots = new ArrayList<TreeNode>(); //This will be the returned list of all roots
        
        Iterator<TreeBranch> i = copyOfBranches.iterator();
        TreeNode root; //As we traverse through the list, we will change root.
        
        while(i.hasNext()){
            TreeBranch branch = i.next();
            TreeBranch parent = (TreeBranch) branch.getParent();
            
            //If no parent, then this is a root
            if(parent == null){
                
            }
        }
        
    }
}
