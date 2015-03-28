/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.component.navigation;

import TreeAPI.TreeNode;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import eds.entity.navigation.MenuItem;

/**
 *
 * @author vincent.a.lee
 */
@SessionScoped
public class MenuContainer implements Serializable {
    
    private List<TreeNode> menuItemList;
    private TreeNode<MenuItem> menuRoot;
    private List<MenuItem> allMenuItems;

    public List<TreeNode> getMenuItemList() {
        return menuItemList;
    }

    public void setMenuItemList(List<TreeNode> menuItemList) {
        this.menuItemList = menuItemList;
    }

    public TreeNode<MenuItem> getMenuRoot() {
        return menuRoot;
    }

    public void setMenuRoot(TreeNode<MenuItem> menuRoot) {
        this.menuRoot = menuRoot;
    }

    public List<MenuItem> getAllMenuItems() {
        return allMenuItems;
    }

    public void setAllMenuItems(List<MenuItem> allMenuItems) {
        this.allMenuItems = allMenuItems;
    }
    
    
}
