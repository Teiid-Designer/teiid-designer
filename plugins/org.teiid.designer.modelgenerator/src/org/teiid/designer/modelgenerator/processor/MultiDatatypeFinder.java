/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.processor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;

/**
 * MultiDatatypeFinder
 *
 * @since 8.0
 */
public class MultiDatatypeFinder implements DatatypeFinder {

    private final List datatypeFinders;

    /**
     * Construct an instance of MultiDatatypeFinder.
     * 
     */
    public MultiDatatypeFinder( final List datatypeFinders ) {
        super();
        CoreArgCheck.isNotNull(datatypeFinders);
        CoreArgCheck.isPositive(datatypeFinders.size());
        this.datatypeFinders = datatypeFinders;
    }

    /**
     * Go through the finders until the first one is found ...
     * @see org.teiid.designer.modelgenerator.processor.DatatypeFinder#findDatatype(java.lang.String)
     */
    @Override
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
     * @see org.teiid.designer.modelgenerator.processor.DatatypeFinder#findDatatype(org.eclipse.emf.common.util.URI)
     */
    @Override
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
     * @see org.teiid.designer.modelgenerator.processor.DatatypeFinder#findAllDatatypes(java.lang.String)
     */
    @Override
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
     * @see org.teiid.designer.modelgenerator.processor.DatatypeFinder#findAllDatatypes(org.eclipse.emf.common.util.URI)
     */
    @Override
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
