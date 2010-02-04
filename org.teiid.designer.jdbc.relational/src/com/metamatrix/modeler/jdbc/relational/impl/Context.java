/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.log.Logger;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;

/**
 * Context
 */
public interface Context {
    
    public void setVerboseLogging( boolean verbose );
    public boolean isVerbose();
    public Logger getLogger();

    public Resource getResource();
    public ModelContents getModelContents();
    public JdbcDatabase getJdbcDatabase(); 
    public JdbcImportSettings getJdbcImportSettings(); 
    public IProgressMonitor getProgressMonitor();
    public List getErrors();
    public List getWarnings();

    /**
     * Find an existing EObject in the model with the supplied path.  
     * @param pathInModel the path of the EObject in the model; may not be null
     * @return the model object with the corresponding path; may be null only if no
     * such object was found at the supplied location in the model
     */
    public EObject findObject( IPath pathInModel );
    
    public EObject addNewObject( IPath pathInModel, EObject obj );
}
