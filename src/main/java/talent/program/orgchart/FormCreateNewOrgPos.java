/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.program.orgchart;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import talent.entity.organization.BusinessUnit;
import talent.entity.organization.Position;
import talent.entity.talent.Employee;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormCreateNewOrgPos")
public class FormCreateNewOrgPos {
    
    private Position role;
    private BusinessUnit unit;
    private Employee holder;
    
    public void createNewOrgPos(){
        
    }

    public Position getRole() {
        return role;
    }

    public void setRole(Position role) {
        this.role = role;
    }

    public BusinessUnit getUnit() {
        return unit;
    }

    public void setUnit(BusinessUnit unit) {
        this.unit = unit;
    }

    public Employee getHolder() {
        return holder;
    }

    public void setHolder(Employee holder) {
        this.holder = holder;
    }
    
    
}
