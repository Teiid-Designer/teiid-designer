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

package com.metamatrix.modeler.core.resource;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.id.ObjectID;


/** 
 * @since 4.3
 */
public interface EObjectCacheHolder {

    /**
     * Returns the eProxy value to which this cache maps the specified key.  
     * Returns <tt>null</tt> if the cache contains no mapping for this key or
     * if the EResource is current loaded in which case there are no eProxy instances
     * associated with it.  
     * @param key key whose associated value is to be returned.
     * @return the value to which this cache maps the specified key, or
     *         <tt>null</tt> if the map contains no mapping for this key.
     */
    EObject getEObject(ObjectID key);

}
