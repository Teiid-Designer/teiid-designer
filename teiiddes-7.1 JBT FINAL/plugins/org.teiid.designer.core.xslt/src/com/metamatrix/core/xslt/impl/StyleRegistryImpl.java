/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
