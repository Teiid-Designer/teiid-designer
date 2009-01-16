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
