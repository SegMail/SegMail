/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TreeAPI;

/**
 * A TreeBranch tells you which nodes to join together in order to build a tree.
 * 
 * 
 * @author KH
 */
public interface TreeBranch<T> {
    
    public enum TRAVERSAL_MODE{
        PARENT_ONLY,
        CHILDREN_ONLY,
        PARENT_AND_CHILDREN
    }
    
    public T getParent();
    
    public Iterable<T> getChildren();
    
    public TRAVERSAL_MODE getTraversalMode();
}
