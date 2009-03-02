/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
