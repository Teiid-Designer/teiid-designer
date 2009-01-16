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

package com.metamatrix.modeler.core;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * ExternalResourceSet is the interface for all extensions of
 * ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET. Implementations
 * define a org.eclipse.emf.ecore.resource.ResourceSet that is
 * external to the model container but may be needed by the container
 * for resolution of resource references.
 */
public interface ExternalResourceSet {

    ResourceSet getResourceSet();

}
