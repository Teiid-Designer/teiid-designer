/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.jdbc.JdbcImportSettings;
import org.teiid.designer.jdbc.metadata.JdbcDatabase;
import org.teiid.designer.jdbc.relational.impl.Context;


/**
 * ContextImpl
 *
 * @since 8.0
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
    @Override
	public EObject findObject( final IPath pathInModel ) {
        CoreArgCheck.isNotNull(pathInModel);

        // First, attempt to find the object in the map (which means we have already found it) ...
        final EObject obj = (EObject)eObjectByIPath.get(pathInModel);
        return obj;
    }

    @Override
	public EObject addNewObject( final IPath path,
                                 final EObject obj ) {
        return (EObject)eObjectByIPath.put(path, obj);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.Context#getResource()
     */
    @Override
	public Resource getResource() {
        return this.resource;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.Context#getModelContents()
     */
    @Override
	public ModelContents getModelContents() {
        return this.modelContents;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.Context#getJdbcDatabase()
     */
    @Override
	public JdbcDatabase getJdbcDatabase() {
        return this.jdbcDatabase;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.Context#getJdbcImportSettings()
     */
    @Override
	public JdbcImportSettings getJdbcImportSettings() {
        return this.importSettings;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.Context#getProgressMonitor()
     */
    @Override
	public IProgressMonitor getProgressMonitor() {
        return this.monitor;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.Context#getErrors()
     */
    @Override
	public List getErrors() {
        return this.errors;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.Context#getWarnings()
     */
    @Override
	public List getWarnings() {
        return this.warnings;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.Context#setVerboseLogging(boolean)
     */
    @Override
	public void setVerboseLogging( final boolean verbose ) {
        this.verbose = verbose;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.Context#isVerbose()
     */
    @Override
	public boolean isVerbose() {
        return verbose;
    }
}
