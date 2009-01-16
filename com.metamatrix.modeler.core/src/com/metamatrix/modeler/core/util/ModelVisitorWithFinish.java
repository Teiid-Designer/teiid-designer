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

package com.metamatrix.modeler.core.util;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * ModelVisitorWithFinish
 */
public interface ModelVisitorWithFinish extends ModelVisitor {
    
    /**
     * Called after the object and it's contents have all been visited.  Implementing this interface
     * allows the visitor to do additional logic after all children have been visited.
     * @param item the item to visit; never null
     * @return true if the children of <code>item</code> should be visited, or false if they should not.
     * @throws ModelerCoreException if the visit fails for some reason
     */
    public void postVisit( EObject object) throws ModelerCoreException;


}
