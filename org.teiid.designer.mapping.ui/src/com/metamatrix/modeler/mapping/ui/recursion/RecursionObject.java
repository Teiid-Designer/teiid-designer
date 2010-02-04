/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.recursion;

import java.util.Iterator;
import java.util.List;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.RecursionErrorMode;

/**
 * RecursionObject
 */
public class RecursionObject {



    private MappingClass mcMappingClass;

   /**
     * Construct an instance of RecursionObject.
     * 
     */
    public RecursionObject( MappingClass mcMappingClass ) {
//        System.out.println("[RecursionObject.ctor]"); //$NON-NLS-1$
//        if ( mcMappingClass == null ) {            
//            System.out.println("[RecursionObject.ctor] mcMappingClass is NULL"); //$NON-NLS-1$
//        }
        this.mcMappingClass = mcMappingClass;
    }
         
    public MappingClass getMappingClass() {
        return mcMappingClass;     
    }

    public boolean isRecursive() {
        return mcMappingClass.isRecursive();     
    }

    public void setRecursive( boolean b ) {
        // Only set if changed
        if( b != isRecursive() )
            mcMappingClass.setRecursive( b );     
    }

    public boolean isRecursionAllowed() {
        return mcMappingClass.isRecursionAllowed();     
    }

    public void setRecursionAllowed( boolean b ) {
        // Only set if changed
        if( b != isRecursionAllowed() )
            mcMappingClass.setRecursionAllowed( b );     
    }


    public void setRecursionLimitErrorMode( String value ) {                
        mcMappingClass.setRecursionLimitErrorMode( RecursionErrorMode.get( value ) );
    }

    public String getRecursionLimitErrorMode() {                

        return mcMappingClass.getRecursionLimitErrorMode().getName();     
    }

    public String getRecursionCriteria() {
        return mcMappingClass.getRecursionCriteria();
    }
    
    public void setRecursionCriteria( String sCriteria ) {
        mcMappingClass.setRecursionCriteria( sCriteria );
    }

    public int getRecursionLimit() {
        return mcMappingClass.getRecursionLimit();
    }
    
    public void setRecursionLimit( int iLimit ) {
        // Only set if changed
        if( iLimit != getRecursionLimit() )
            mcMappingClass.setRecursionLimit( iLimit );
    }


    public String[] getValidErrorModeValues() {
        List lstValues = RecursionErrorMode.VALUES;
                
        String[] saValues = new String[ lstValues.size() ];
        int iCounter = 0;
        Iterator it = lstValues.iterator();
        
        while ( it.hasNext() ) {
            RecursionErrorMode remTemp = (RecursionErrorMode)it.next();
            saValues[ iCounter++ ] = remTemp.getName();
        }
        
        return saValues;
    }

}
