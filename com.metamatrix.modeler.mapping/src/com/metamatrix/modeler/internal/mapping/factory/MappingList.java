/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;

/**
 * MappingList is a Pair of Mapping and EList.
 */
class MappingList {

    public Mapping mapping;
    public EList list;
    public HashMap hmap;

    /**
     * Construct an instance of MappingList.
     */
    public MappingList(Mapping mapping, EList list) {
        this.mapping = mapping;
        this.list = list;
        
        hmap = new HashMap( list.size() );
        Iterator iter = list.iterator();
        
        while( iter.hasNext() ) {
            
            Object oTemp = iter.next();
            hmap.put( oTemp, oTemp );
        }

    }

    public boolean contains( EObject theTreeNode ) {
        boolean result = false;
        if ( hmap.get( theTreeNode ) != null ) {
            result = true;
        }
        return result;
    }
}
