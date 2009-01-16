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

package com.metamatrix.core.xslt.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.metamatrix.core.xslt.Style;
import com.metamatrix.core.xslt.StyleRegistry;

/**
 * StyleRegistry
 */
public class StyleRegistryImpl implements StyleRegistry {

    public final List styles;

    /**
     * Construct an instance of StyleRegistry.
     * 
     */
    public StyleRegistryImpl() {
        super();
        this.styles = new LinkedList();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ddl.StyleRegistry#getStyle(java.lang.String)
     */
    public Style getStyle(String name) {
        final Iterator iter = styles.iterator();
        while (iter.hasNext()) {
            final Style style = (Style)iter.next();
            if ( style.getName().equals(name) ) {
                return style;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ddl.StyleRegistry#getStyles()
     */
    public Collection getStyles() {
        return styles;
    }

}
