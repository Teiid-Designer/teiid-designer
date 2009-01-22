/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import org.eclipse.emf.ecore.EObject;

/**
 * SqlForeignKeyAspect
 */
public interface SqlForeignKeyAspect extends SqlColumnSetAspect {
    
    /**
     * Get a unique key <code>EObject</code> this foreign key referen
     * @param eObject The <code>EObject</code> for which unique key is obtained 
     * @return a <code>EObject</code> for the unique key
     */
    Object getUniqueKey(EObject eObject);
}
