/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.datasource;

import eds.component.data.IncompleteDataException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.datasource.ListDataMapping;
import segmail.entity.subscription.datasource.ListDatasource;
import segmail.entity.subscription.datasource.ListDatasourceObject;
import segmail.entity.subscription.datasource.synchronize.ListDatasourceObjectWrapper;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class DatasourceQueryService {
    
    public final int DEFAULT_MAX_DISTINCT_VALUES = 100;
    
    @EJB SubscriptionService subService;
    
    /**
     * Currently only MySQL implementation
     * 
     * @param ld
     * @param mappings
     * @param orderByField
     * @param keyList List of subscribers key values (ld.getKEY_FIELD() to find. Null or empty to disregard this as a criteria.
     * @param start -1 to disregard this as a criteria, and max will also be disregarded.
     * @param max -1 to disregard this as a criteria, and start will also be disregarded.
     * @return 
     * @throws java.sql.SQLException 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ListDatasourceObject> getRemoteSubscribers(
            ListDatasource ld, 
            List<ListDataMapping> mappings, 
            String orderByField, 
            List<String> keyList,
            int start, 
            int max) throws SQLException, IncompleteDataException {
        Connection conn = DatasourceConnectionFactory.getMySQLConnection(ld.getSERVER_NAME(), ld.getDB_NAME(), ld.getUSERNAME(), ld.getPASSWORD());
        try {
            
            //Construct the field list
            String fieldString = "";
            for(ListDataMapping mapping : mappings) {
                if(fieldString.length() > 0)
                    fieldString += ",";
                fieldString += mapping.getFOREIGN_NAME();
            }
            
            //Construct query
            String queryString = "";
            queryString += "SELECT " + fieldString;
            queryString += " FROM " + ld.getTABLE_NAME();
            
            //WHERE criteria
            queryString += " WHERE 1=1";
            if(keyList != null && !keyList.isEmpty()) {
                String inList = "";
                for(String in : keyList) {
                    if(inList.length() > 0)
                        inList += ",";
                    inList += "'"+in+"'";
                }
                queryString += " AND "+ld.getKEY_FIELD()+" IN (" + inList + ")";
            }
            
            if(ld.isUSE_STATUS_FIELD()) {
                queryString += " AND "+ld.getSTATUS_FIELD()+" = '"+ld.getSTATUS_FIELD_VALUE()+"'";
            }
            
            queryString += " ORDER BY " + orderByField + " ASC";
            
            PreparedStatement stmt = null;
            if(start >= 0 && max >= 0) {
                queryString += " LIMIT ?, ?";
                stmt = conn.prepareStatement(queryString);
                stmt.setInt(1, start);
                stmt.setInt(2, max);
            } else {
                stmt = conn.prepareStatement(queryString);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            List<ListDatasourceObject> results = new ArrayList<>();
            while(rs.next()) {
                ListDatasourceObject result = new ListDatasourceObject();
                for(int i=0; i<mappings.size(); i++) {
                    ListDataMapping mapping = mappings.get(i);
                    String localKey = mapping.getKEY_NAME();
                    String foreignName = mapping.getFOREIGN_NAME();
                    switch(FIELD_TYPE.valueOf(mapping.getTYPE())) {
                        case EMAIL: result.addValue(localKey, rs.getString(foreignName));
                                    result.setEmail(rs.getString(foreignName));
                                    break;
                        case TEXT : 
                        default   : result.addValue(localKey, rs.getString(foreignName));
                                    break;
                    }
                }
                results.add(result);
            }
            
            conn.close();
            return results;
            
        } catch (SQLException ex) {
            Logger.getLogger(DatasourceQueryService.class.getName()).log(Level.SEVERE, null, ex);
            conn.close();
            throw ex;
        } finally {
            conn.close();
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ListDatasourceObjectWrapper> getRemoteSubscriberWrappers(
            ListDatasource ld, 
            List<ListDataMapping> mappings, 
            String orderByField, 
            List<String> keyList,
            int start, 
            int max) throws SQLException, IncompleteDataException {
        List<ListDatasourceObject> rawObjects = getRemoteSubscribers(ld, mappings, orderByField, keyList, start, max);
        
        List<ListDatasourceObjectWrapper> wrappers = new ArrayList<>();
        for(ListDatasourceObject rawObj : rawObjects) {
            ListDatasourceObjectWrapper newWrapper = new ListDatasourceObjectWrapper(rawObj);
            wrappers.add(newWrapper);
        }
        
        return wrappers;
    }
    
    public List<String> getRemoteTableColumns(ListDatasource ld) throws IncompleteDataException, SQLException {
        if(ld.getTABLE_NAME() == null || ld.getTABLE_NAME().isEmpty())
            throw new IncompleteDataException("Table name is missing.");
        
        List<String> results = new ArrayList<>();
        Connection conn = DatasourceConnectionFactory.getConnection(ld);
        
        try {
            String queryString = "DESCRIBE "+ld.getTABLE_NAME();
            ResultSet rs = conn.prepareStatement(queryString).executeQuery();
            
            while(rs.next()) {
                String fieldName = rs.getString("Field");
                results.add(fieldName);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DatasourceQueryService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            conn.close();
        }
        return results;
    }
    
    public List<String> getDistinctValues(ListDatasource ld, String fieldName) throws IncompleteDataException, SQLException {
        
        if(fieldName == null || fieldName.isEmpty())
            throw new IncompleteDataException("No column name set for mapping.");

        if(ld.getTABLE_NAME() == null || ld.getTABLE_NAME().isEmpty())
            throw new IncompleteDataException("Table name is missing.");

        List<String> results = new ArrayList<>();
        
        Connection conn = DatasourceConnectionFactory.getConnection(ld);
        try {
            String queryString = "SELECT DISTINCT "+fieldName;
            queryString += " FROM " + ld.getTABLE_NAME();
            queryString += " LIMIT ?, ?";
            
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement(queryString);
            stmt.setInt(1, 0);
            stmt.setInt(2, DEFAULT_MAX_DISTINCT_VALUES);
            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                results.add(rs.getString(fieldName));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DatasourceQueryService.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally {
            conn.close();
        }
        return results;
    }
    
}
