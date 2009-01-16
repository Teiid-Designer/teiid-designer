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

package com.metamatrix.modeler.compare.selector;

import java.util.List;

import org.eclipse.emf.common.util.URI;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * ModelSelector
 */
public interface ModelSelector {
    
    public void open();
    
    public String getLabel();
    
    public void setLabel( String label );
    
    public List getRootObjects() throws ModelerCoreException;
    
    public URI getUri();
    
    public ModelAnnotation getModelAnnotation() throws ModelWorkspaceException;
    
    public void close();
    
    public ModelHelper getModelHelper() throws ModelWorkspaceException;
    
    public ModelContents getModelContents() throws ModelWorkspaceException;

    public void addRootObjects(List newRoots) throws ModelerCoreException;

    public void addRootObjects(List newRoots, int startingIndex) throws ModelerCoreException;

    public void rebuildModelImports() throws ModelerCoreException;

}
