/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.resources.IResource;

/** A simple interface to filter out resources.
 * @author PForhan
 */
public interface ResourceFilter {
    public boolean accept(IResource res);
    
    // Class constants:
    public static final ResourceFilter ACCEPT_ALL = new ResourceFilter() {
        public boolean accept(IResource res) {
            return true;
        }
    };
}
