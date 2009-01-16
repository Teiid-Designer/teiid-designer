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
