/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/** 
 * @since 5.0
 */
public class ListAndMapUtil {

    /**
     * Simple List to HashMap creator 
     * @param list
     * @return
     * @since 5.0
     */
    public static HashMap createMapFromList( List list ) {
        
        HashMap hmap = new HashMap( list.size() );
        Iterator iter = list.iterator();
        
        while( iter.hasNext() ) {
            Object oTemp = iter.next();
            hmap.put( oTemp, oTemp );
        }

        return hmap;
    }

}
