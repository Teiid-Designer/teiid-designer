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

package com.metamatrix.modeler.core.refactor;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;


/**
 * ReferenceUpdator
 * @since 4.2
 */
public interface ReferenceUpdator {

    /**
     * Update the EObject by resetting references by looking up oldToNewObjects map, the
     * references may be eObjects or values derived from eObjects in the map.
     * @param eObject The EObject to update
     * @param oldToNewObjects The Map of old to new objects, this info is needed to update
     * the EObject
     * @since 4.2
     */
    void updateEObject(final EObject eObject, final Map oldToNewObjects);
}
