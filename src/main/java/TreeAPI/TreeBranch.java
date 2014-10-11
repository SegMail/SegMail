/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TreeAPI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
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
