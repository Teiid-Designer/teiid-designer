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

package com.metamatrix.modeler.modelgenerator.processor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;

/**
 * MultiDatatypeFinder
 */
public class MultiDatatypeFinder implements DatatypeFinder {

    private final List datatypeFinders;

    /**
     * Construct an instance of MultiDatatypeFinder.
     * 
     */
    public MultiDatatypeFinder( final List datatypeFinders ) {
        super();
        ArgCheck.isNotNull(datatypeFinders);
        ArgCheck.isPositive(datatypeFinders.size());
        this.datatypeFinders = datatypeFinders;
    }

    /**
     * Go through the finders until the first one is found ...
     * @see com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder#findDatatype(java.lang.String)
     */
    public EObject findDatatype(final String name) throws CoreException {
        final Iterator iter = this.datatypeFinders.iterator();
        while (iter.hasNext()) {
            final DatatypeFinder finder = (DatatypeFinder)iter.next();
            final EObject result = finder.findDatatype(name);
            if ( result != null ) {
                return result;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder#findDatatype(org.eclipse.emf.common.util.URI)
     */
    public EObject findDatatype(URI uri) throws CoreException {
        final Iterator iter = this.datatypeFinders.iterator();
        while (iter.hasNext()) {
            final DatatypeFinder finder = (DatatypeFinder)iter.next();
            final EObject result = finder.findDatatype(uri);
            if ( result != null ) {
                return result;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder#findAllDatatypes(java.lang.String)
     */
    public List findAllDatatypes(String name) throws CoreException {
        final List results = new LinkedList();
        final Iterator iter = this.datatypeFinders.iterator();
        while (iter.hasNext()) {
            final DatatypeFinder finder = (DatatypeFinder)iter.next();
            final List result = finder.findAllDatatypes(name);
            final Iterator resultIter = result.iterator();
            while (resultIter.hasNext()) {
                final EObject object = (EObject)resultIter.next();
                if ( !results.contains(object) ) {
                    results.add(object);
                }
            }
        }
        return results;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder#findAllDatatypes(org.eclipse.emf.common.util.URI)
     */
    public List findAllDatatypes(URI uri) throws CoreException {
        final List results = new LinkedList();
        final Iterator iter = this.datatypeFinders.iterator();
        while (iter.hasNext()) {
            final DatatypeFinder finder = (DatatypeFinder)iter.next();
            final List result = finder.findAllDatatypes(uri);
            final Iterator resultIter = result.iterator();
            while (resultIter.hasNext()) {
                final EObject object = (EObject)resultIter.next();
                if ( !results.contains(object) ) {
                    results.add(object);
                }
            }
        }
        return results;
    }

}
