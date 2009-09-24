/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.xslt;

import java.util.Collection;

/**
 * StyleRegistry
 */
public interface StyleRegistry {
    
    /**
     * Find the first style in the registry with the supplied name
     * @param name the name of the style
     * @return the {@link Style} with a matching name, or null if no
     * such Style object could be found
     */
    public Style getStyle( String name );
    
    /**
     * Return the collection of {@link Style} instances.  This collection
     * is mutable.
     * @return the list of {@link Style} instances; never null
     */
    public Collection getStyles();
    
}
