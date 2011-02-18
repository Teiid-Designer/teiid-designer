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
 * SqlUniqueKeyAspect
 */
public interface SqlUniqueKeyAspect extends SqlColumnSetAspect {

    /**
     * Get a foreign keys <code>EObject</code> this unique key references
     * @param eObject The <code>EObject</code> for which foreign keys are obtained 
     * @return a <code>EObject</code> for the foreign keys
     */
    List getForeignKeys(EObject eObject);
}
