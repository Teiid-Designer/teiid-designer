/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
