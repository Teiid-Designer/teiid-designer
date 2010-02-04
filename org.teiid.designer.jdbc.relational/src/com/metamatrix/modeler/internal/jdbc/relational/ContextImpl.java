/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.relational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.relational.impl.Context;

/**
 * ContextImpl
 */
public class ContextImpl implements Context {
    private final Resource resource;
    private final ModelContents modelContents;
    private final JdbcDatabase jdbcDatabase;
    private final JdbcImportSettings importSettings;
    private final IProgressMonitor monitor;
    private final List errors = new ArrayList();
    private final List warnings = new ArrayList();
    private final Map eObjectByIPath = new HashMap();
    private boolean verbose;

    public ContextImpl( final Resource resource,
                        final ModelContents contents,
                        final JdbcDatabase jdbcDatabase,
                        final JdbcImportSettings settings,
                        final IProgressMonitor monitor ) {
        this.resource = resource;
        this.modelContents = contents;
        this.jdbcDatabase = jdbcDatabase;
        this.importSettings = settings;
        this.monitor = monitor != null ? monitor : new NullProgressMonitor();
    }

    /**
     * Find an existing EObject in the model with the supplied path.
     * 
     * @param pathInModel the path of the EObject in the model; may not be null
     * @return the model object with the corresponding path; may be null only if no such object was found in the model
     */
    public EObject findObject( final IPath pathInModel ) {
        ArgCheck.isNotNull(pathInModel);

        // First, attempt to find the object in the map (which means we have already found it) ...
        final EObject obj = (EObject)eObjectByIPath.get(pathInModel);
        return obj;
    }

    public EObject addNewObject( final IPath path,
                                 final EObject obj ) {
        return (EObject)eObjectByIPath.put(path, obj);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.Context#getResource()
     */
    public Resource getResource() {
        return this.resource;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.Context#getModelContents()
     */
    public ModelContents getModelContents() {
        return this.modelContents;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.Context#getJdbcDatabase()
     */
    public JdbcDatabase getJdbcDatabase() {
        return this.jdbcDatabase;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.Context#getJdbcImportSettings()
     */
    public JdbcImportSettings getJdbcImportSettings() {
        return this.importSettings;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.Context#getProgressMonitor()
     */
    public IProgressMonitor getProgressMonitor() {
        return this.monitor;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.Context#getErrors()
     */
    public List getErrors() {
        return this.errors;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.Context#getWarnings()
     */
    public List getWarnings() {
        return this.warnings;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.Context#setVerboseLogging(boolean)
     */
    public void setVerboseLogging( final boolean verbose ) {
        this.verbose = verbose;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.Context#isVerbose()
     */
    public boolean isVerbose() {
        return verbose;
    }
}
