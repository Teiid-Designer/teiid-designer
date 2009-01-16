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
