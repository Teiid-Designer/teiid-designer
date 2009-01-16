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
