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
 * @since 4.3
 */
public interface SqlResultSetAspect extends
                                   SqlColumnSetAspect {

    /**
     * Return the procedure that produces this result set. 
     * @param eObject The <code>EObject</code> for which the procedure is obtained 
     * @return the procedure that produces this result set.
     * @since 5.0.2
     */
    Object getProcedure(EObject eObject);
}
