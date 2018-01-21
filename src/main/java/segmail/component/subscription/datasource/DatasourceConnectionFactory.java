/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.datasource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import eds.component.data.IncompleteDataException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import segmail.entity.subscription.datasource.DATASOURCE_ENDPOINT_TYPE;
import segmail.entity.subscription.datasource.ListDatasource;

/**
 *
 * @author LeeKiatHaw
 */
public class DatasourceConnectionFactory {
    
    public static Connection getMySQLConnection(
            String serverName, 
            String dbName, 
            String username, 
            String password) throws SQLException, IncompleteDataException {
        if(serverName == null || serverName.isEmpty())
            throw new IncompleteDataException("Server Name is missing.");
        if(dbName == null || dbName.isEmpty())
            throw new IncompleteDataException("DB Name is missing.");
        if(username == null || username.isEmpty())
            throw new IncompleteDataException("Username is missing.");
        if(password == null || password.isEmpty())
            throw new IncompleteDataException("Password is missing.");
        
        MysqlDataSource mysqlDS = new MysqlDataSource();
        mysqlDS.setUser(username);
        mysqlDS.setPassword(password);
        mysqlDS.setServerName(serverName);
        mysqlDS.setDatabaseName(dbName);
        
        return mysqlDS.getConnection();
    }
    
    public static Connection getConnection(ListDatasource ld) throws IncompleteDataException, SQLException {
        DATASOURCE_ENDPOINT_TYPE type = DATASOURCE_ENDPOINT_TYPE.valueOf(ld.getENDPOINT_TYPE());
        
        switch(type) {
            case    MYSQL : {
                try {
                    return getMySQLConnection(ld.getSERVER_NAME(),ld.getDB_NAME(),ld.getUSERNAME(),ld.getPASSWORD());
                } catch (SQLException ex) {
                    Logger.getLogger(DatasourceConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
                    throw ex;
                }
            }
            default     : break;
        }
        
        return null;
    }
}
