/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
