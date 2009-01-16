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
 * SqlVdbAspect is used to get the different properties on a VDB archive for runtime metadata.
 */
public interface SqlVdbAspect extends SqlAspect {
    
    /**
     * Return the version of the VDB archive
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getVersion(EObject eObject);
    
    /**
     * Return the identifier for the VDB archive
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getIdentifier(EObject eObject);
    
    /**
     * Return the description for the VDB archive
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getDescription(EObject eObject);
    
    /**
     * Return the name of the VDB archive producer
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getProducerName(EObject eObject);
    
    /**
     * Return the version of the VDB archive producer
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getProducerVersion(EObject eObject);
    
    /**
     * Return the name of the provider
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getProvider(EObject eObject);
    
    /**
     * Return the time the VDB archive was last changed
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getTimeLastChanged(EObject eObject);
    
    /**
     * Return the time the VDB archive was last re-indexed
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getTimeLastProduced(EObject eObject);
    
    /**
     * Return the list of model identifiers for the VDB archive
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    List getModelIDs(EObject eObject);

}
