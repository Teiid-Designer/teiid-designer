/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.processor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * BuiltInDatatypeFinder
 */
public class BuiltInDatatypeFinder implements DatatypeFinder {
    
    public static final BuiltInDatatypeFinder INSTANCE = new BuiltInDatatypeFinder();
    
    /**
     * Construct an instance of BuiltInDatatypeFinder.
     * 
     */
    public BuiltInDatatypeFinder() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findDatatype(java.lang.String)
     */
    public EObject findDatatype(final String name) throws CoreException {
        final DatatypeManager dtMgr = ModelerCore.getWorkspaceDatatypeManager();    // only care about built-ins!
        if ( name == null ) {
            return dtMgr.getAnySimpleType();
        }
        final EObject datatype = dtMgr.getBuiltInDatatype(name);
        return datatype;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findDatatype(org.eclipse.emf.common.util.URI)
     */
    public EObject findDatatype(final URI uri) throws CoreException {
        final DatatypeManager dtMgr = ModelerCore.getWorkspaceDatatypeManager();    // only care about built-ins!
        if ( uri == null ) {
            return dtMgr.getAnySimpleType();
        }
        final EObject datatype = dtMgr.findDatatype(uri.toString());
        return datatype;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findAllDatatypes(java.lang.String)
     */
    public List findAllDatatypes(final String name) throws CoreException {
        final EObject datatype = findDatatype(name);
        if ( datatype != null ) {
            final List result = new LinkedList();
            result.add(datatype);
            return result;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findAllDatatypes(org.eclipse.emf.common.util.URI)
     */
    public List findAllDatatypes(final URI uri) throws CoreException {
        final EObject datatype = findDatatype(uri);
        if ( datatype != null ) {
            final List result = new LinkedList();
            result.add(datatype);
            return result;
        }
        return Collections.EMPTY_LIST;
    }

}
