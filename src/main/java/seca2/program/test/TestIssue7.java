/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory;

/**
 *
 * @author LeeKiatHaw
 */
public class TestIssue7 {
    
    public static void main(String[] args){
        DefaultIdentifierGeneratorFactory factory = new DefaultIdentifierGeneratorFactory();
        Class clazz = factory.getIdentifierGeneratorClass( "org.hibernate.id.MultipleHiLoPerTableGenerator" );
        try {
            IdentifierGenerator identifierGenerator = ( IdentifierGenerator ) clazz.newInstance();
            factory.createIdentifierGenerator("org.hibernate.id.MultipleHiLoPerTableGenerator", null, null);
            System.out.println(identifierGenerator.toString());
        } catch (InstantiationException ex) {
            System.out.println(ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
