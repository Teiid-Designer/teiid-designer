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

package com.metamatrix.ui.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * TreeNodeMap
 */
public class TreeNodeMap {

    private HashMap hmap;

    /**
     * Construct an instance of TreeNodeMap.
     */
    public TreeNodeMap( List list ) {
       
        // Create a map with key = the tree node; value = index of the entry in the list
        hmap = new HashMap( list.size() );
        int ix = -1;
        Iterator iter = list.iterator();
        
        while( iter.hasNext() ) {
            
            Object oTemp = iter.next();
            hmap.put( oTemp, new Integer( ++ix ) );
        }

    }

    public boolean contains( Object theTreeNode ) {
        boolean result = false;
        if ( hmap.get( theTreeNode ) != null ) {
            result = true;
        }
        return result;
    }

    public int indexOf( Object theTreeNode ) {
        int result = -1;
        Integer i = (Integer)hmap.get( theTreeNode );
        
        if ( i != null ) {
            result = i.intValue();
        }
        return result;
    }
    
    public Object get( Object theTreeNode ) {
        return hmap.get( theTreeNode );
    }

}
