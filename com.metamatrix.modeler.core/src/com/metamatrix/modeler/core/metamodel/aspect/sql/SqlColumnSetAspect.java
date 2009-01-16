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

package com.metamatrix.modeler.core.metamodel.aspect.sql;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * This aspect represents all metamodelentities that reference columns, i.e: UniqueKeys,
 * ForeignKeys, Indexes, AccessPatterns, Procedures.
 */
public interface SqlColumnSetAspect extends SqlAspect {
    
    /**
     * Get a list of <code>EObject</code>s for the columns referenced by this 
     * aspect.
     * @param eObject The <code>EObject</code> for which columns are obtained 
     * @return a list of <code>EObject</code>s
     */
    List getColumns(EObject eObject);

    /**
     * Returns the type of entity this aspect represents
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.COLUMN_SET_TYPES
     * @return int value representing the aspect type.
     */
    int getColumnSetType();
}
