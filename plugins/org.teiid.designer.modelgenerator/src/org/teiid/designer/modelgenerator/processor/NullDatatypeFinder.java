/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.processor;

import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * NullDatatypeFinder
 */
public class NullDatatypeFinder implements DatatypeFinder {

    /**
     * @See org.teiid.designer.modelgenerator.DatatypeFinder#findDatatype(java.lang.String)
     */
    public EObject findDatatype( String name ) {
        return null;
    }

    /**
     * @See org.teiid.designer.modelgenerator.DatatypeFinder#findDatatype(org.eclipse.emf.common.util.URI)
     */
    public EObject findDatatype( URI uri ) {
        return null;
    }

    /**
     * @See org.teiid.designer.modelgenerator.DatatypeFinder#findAllDatatypes(java.lang.String)
     */
    public List findAllDatatypes( String name ) {
        return null;
    }

    /**
     * @See org.teiid.designer.modelgenerator.DatatypeFinder#findAllDatatypes(org.eclipse.emf.common.util.URI)
     */
    public List findAllDatatypes( URI uri ) {
        return null;
    }

}
