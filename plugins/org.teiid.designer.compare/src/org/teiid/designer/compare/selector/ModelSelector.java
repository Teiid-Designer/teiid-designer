/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.selector;

import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelAnnotation;


/**
 * ModelSelector
 *
 * @since 8.0
 */
public interface ModelSelector {
    
    public void open();
    
    public String getLabel();
    
    public void setLabel( String label );
    
    public List<EObject> getRootObjects() throws ModelerCoreException;
    
    public URI getUri();
    
    public ModelAnnotation getModelAnnotation() throws ModelWorkspaceException;
    
    public void close();
    
    public ModelHelper getModelHelper() throws ModelWorkspaceException;
    
    public ModelContents getModelContents() throws ModelWorkspaceException;

    public void addRootObjects(List newRoots) throws ModelerCoreException;

    public void addRootObjects(List newRoots, int startingIndex) throws ModelerCoreException;

    public void rebuildModelImports() throws ModelerCoreException;

}
