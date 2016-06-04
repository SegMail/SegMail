/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
public interface FormListEntity  {
    
    /**
     * Load a list of entities
     */
    public void loadList();
    
    /**
     * Load the selected entity identified by an entity ID.
     * 
     * @param entityId 
     */
    public void loadSelectedEntity(long entityId);
    
    /**
     * Load the selected entity identified by an entity key.
     * 
     * @param entityKey 
     */
    public void loadSelectedEntity(String entityKey);
}
