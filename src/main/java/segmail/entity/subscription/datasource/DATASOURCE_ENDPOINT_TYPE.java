/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource;

/**
 *
 * @author LeeKiatHaw
 */
public enum DATASOURCE_ENDPOINT_TYPE {
    
    MYSQL("MYSQL", "MySQL", "mysql://",3306)
    //,HTTP("HTTP", "Http", "http://",80)
    //,HTTPS("HTTPS", "SSL/TLS", "https://",443)
    ;
    public final String name;
    
    public final String label;
    
    public final String prefix;
    
    public final int port;
    
    private DATASOURCE_ENDPOINT_TYPE(String name, String label, String prefix, int port){
        this.name = name;
        this.label = label;
        this.prefix = prefix;
        this.port = port;
    }
    
    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }

    public String getLabel() {
        return label;
    }
    
    public static DATASOURCE_ENDPOINT_TYPE defaultValue() {
        return MYSQL;
    }
}
