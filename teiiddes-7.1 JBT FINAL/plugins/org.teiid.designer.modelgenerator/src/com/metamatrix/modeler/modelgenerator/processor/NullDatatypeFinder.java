/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.processor;

import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * NullDatatypeFinder
 */
public class NullDatatypeFinder implements DatatypeFinder {

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findDatatype(java.lang.String)
     */
    public EObject findDatatype( String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findDatatype(org.eclipse.emf.common.util.URI)
     */
    public EObject findDatatype( URI uri ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findAllDatatypes(java.lang.String)
     */
    public List findAllDatatypes( String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findAllDatatypes(org.eclipse.emf.common.util.URI)
     */
    public List findAllDatatypes( URI uri ) {
        return null;
    }

}
