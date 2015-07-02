/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.program.orgchart;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import talent.entity.organization.BusinessUnit;
import talent.entity.organization.Role;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormCreateNewRole")
public class FormCreateNewRole {
    
    private Role role;
    private BusinessUnit unit;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public BusinessUnit getUnit() {
        return unit;
    }

    public void setUnit(BusinessUnit unit) {
        this.unit = unit;
    }
    
    
}
