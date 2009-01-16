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

import java.util.Properties;

import org.eclipse.emf.ecore.EObject;

/**
 * SqlModelAspect is used to get the model source information 
 * for runtime metadata.
 */
public interface SqlModelSourceAspect extends SqlAspect {
    
    /**
     * Return Properties object for the model source
     * @param eObject The <code>EObject</code> to retrieve model source properties
     * @return Properties 
     */
    Properties getProperties(EObject eObject);

}
