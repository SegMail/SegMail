/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TreeAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Stack;

/**
 *
 * @author vincent.a.lee
 */
public class TreeBuilder {
    
    /**
     * Give it a collection of branches and it gives you a full-grown tree.
     * Returns an empty list if given an empty list
     * 
     * @param allBranches
     * @return list of all roots
     */
    public static List<TreeNode> buildTree(List<TreeBranch> allBranches) {
        if(allBranches == null || allBranches.isEmpty())
            return new ArrayList<TreeNode>();
        
        TreeBranch peek = allBranches.get(0);
        switch(peek.getTraversalMode()){
            case PARENT_ONLY    : return buildTreeByParent(allBranches);
            default             : throw new RuntimeException("No traversal mode set for TreeBranch \""+peek.getClass().getName()+"\"");
        }
    }
    
    public static List<TreeNode> buildTreeByParent(List<TreeBranch> allBranches) {
        if(allBranches == null || allBranches.isEmpty())
            return new ArrayList<TreeNode>();
        
        List<TreeBranch> copyOfBranches = new ArrayList<TreeBranch>(allBranches); //To refrain from modifying the original instance
        
        List<TreeNode> allRoots = new ArrayList<TreeNode>(); //This will be the returned list of all roots
        List<TreeNode> visited = new ArrayList<TreeNode>(); //This will be used to check if the node has been constructed
        
        Iterator<TreeBranch> i = copyOfBranches.iterator();
        
        //loop through list
        while(i.hasNext()){
            TreeBranch inspected = i.next();
            
            //check if visited. if visited, skip to next TreeBranch
            if(visited.contains(inspected)) continue;
            
            //loop through parents
            TreeBranch inspectedParent = inspected;
            TreeBranch inspectedChild = null; //the previous child node
            TreeNode newParentNode = null; //the node that is going to be added as a root
            while( inspectedParent != null && copyOfBranches.contains(inspectedParent)){
                //mark as visited
                newParentNode = new TreeNode();
                newParentNode.setRoot(inspectedParent);
                //check if the visited node has
                if(!visited.contains(newParentNode)){
                    visited.add(newParentNode);
                }
                //how to add the inspected as a child of inspected parent?
                if(inspectedChild != null){
                    //newParentNode.addChild(inspectedChild);
                }
                
                //get next parent & change child pointer
                inspectedChild = inspectedParent;
                inspectedParent = (TreeBranch) inspectedParent.getParent();
            }
            //after traversing to the point of no parent, the inspectedChild should be added as a root
            allRoots.add(newParentNode);
        }
        return allRoots;
        
    }
    
    public static List<TreeNode> buildTreeByParentBruteForce(List<TreeBranch> allBranches){
        /**
         * 1. Loop through each TreeBranch in the list allBranches.
         * 2. Construct the TreeNode for this TreeBranch first - inspectedTreeBranch and add them to a TreeNode list.
         * 3. Loop through the list again to search for all TreeNode which parent is this TreeNode.
         * 4. If inspectedTreeBranch is the parent of this TreeBranch, then add it as the child of inpectedTreeBranch
         * 
         */
        List<TreeBranch> copyOfBranches = new ArrayList<TreeBranch>(allBranches); //To refrain from modifying the original instance
        final List<TreeNode> inspectedNodes = new ArrayList<TreeNode>(); //Immutable list that will be used in comparing parents
        List<TreeNode> finalNodes = new ArrayList<TreeNode>(); //Final list of nodes that will be returned
        // 1. Loop through each TreeBranch in the list allBranches.
        for(TreeBranch inspectedTreeBranch : copyOfBranches){
            //2. Construct the TreeNode for this TreeBranch first - inspectedTreeBranch.
            TreeNode treeNode = new TreeNode();
            treeNode.setRoot(inspectedTreeBranch);
            inspectedNodes.add(treeNode);
            finalNodes.add(treeNode);
        }
        
        //3. Loop through the list again to search for all TreeBranch which parent is this TreeBranch.
        for(TreeNode parentTreeNode : inspectedNodes){
            TreeBranch checkParent = parentTreeNode.getRoot();
            //4. If inspectedTreeBranch is the parent of this TreeBranch, then add it as the child of inpectedTreeBranch
            //Use an Iterator now, because we are going to modify the list while iterating through it to avoid ConcurrentModificationException
            ListIterator<TreeNode> finalNodeIterator = finalNodes.listIterator();
            while(finalNodeIterator.hasNext()){
                TreeNode childTreeNode = finalNodeIterator.next();
                TreeBranch child = childTreeNode.getRoot();
                
                if(child.getParent() != null
                        && child.getParent().equals(checkParent)){
                    //link the child and parent
                    parentTreeNode.addChild(childTreeNode);
                    childTreeNode.setParent(parentTreeNode);
                    //remove them from the finalNodes list
                    finalNodeIterator.remove();
                }
            }
            
        }
        
        return finalNodes;
    }
}
