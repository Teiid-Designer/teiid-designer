/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
