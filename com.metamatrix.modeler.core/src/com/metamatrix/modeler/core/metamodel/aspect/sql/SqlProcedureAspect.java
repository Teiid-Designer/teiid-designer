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
 * SqlProcedureAspect
 */
public interface SqlProcedureAspect extends
                                   SqlAspect {

    // These are mapping types for which the procedure can be atarget.
    interface MAPPINGS {

        public static final int SQL_TRANSFORM = 1;
    }

    /**
     * Check if table represents a procedure in a virtual model
     * 
     * @return true if the procedure is virtual
     */
    boolean isVirtual(EObject eObject);

    /**
     * Check if this aspect represents a procedure that is a funtion.
     * 
     * @param eObject
     *            The <code>EObject</code> for procedure object
     * @return true if the procedure is a function, else false.
     */
    boolean isFunction(EObject eObject);

    /**
     * Get a list of parameters for the given procedure
     * 
     * @param eObject
     *            The <code>EObject</code> for procedure object
     * @return a list of parameters on the procedure
     */
    List getParameters(EObject eObject);

    /**
     * Get the resultSet on a given procedure.
     * 
     * @param eObject
     *            The <code>EObject</code> for procedure object
     * @return The ResultSet on the procedure
     */
    Object getResult(EObject eObject);

    /**
     * Returns an integer {@link com.metamatrix.metamodels.relational.ProcedureUpdateCount value} indicating the number of updates
     * the user has specified will occur within the supplied procedure.
     * 
     * @param eObject
     *            A physical stored procedure.
     * @return The update count value.
     * @since 5.5.3
     */
    int getUpdateCount(EObject eObject);

    /**
     * Check if the procedure is mappable for the given mapping type.
     * 
     * @param eObject
     *            The <code>EObject</code> whose mapability is checked
     * @param mappinType
     *            The mapping type being checked.
     * @return true if mappable else false
     */
    boolean isMappable(EObject eObject,
                       int mappingType);

    /**
     * Check if the procedure is can be a transformation source for the given target.
     * 
     * @param source
     *            The <code>EObject</code> intends to be a transformation source
     * @param target
     *            The <code>EObject</code> that is the target of the transformation
     * @return true if can be a source else false
     */
    boolean canBeTransformationSource(EObject source,
                                      EObject target);

    /**
     * Check if the procedure can accept the given transformation source.
     * 
     * @param target
     *            The <code>EObject</code> that is the target of the transformation
     * @param source
     *            The <code>EObject</code> intends to be a transformation source
     * @return true if can be a source else false
     */
    boolean canAcceptTransformationSource(EObject target,
                                          EObject source);
}
