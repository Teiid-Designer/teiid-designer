/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.vdb.edit.VdbWsdlGenerationOptions;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

/**
 * @since 4.3
 */
public class FakeVdbEditingContext implements InternalVdbEditingContext {

    ManifestFactory factory = ManifestFactory.eINSTANCE;
    VirtualDatabase database = factory.createVirtualDatabase();
    boolean saveRequired = false;
    File defnFile = null;
    boolean isStale = false;
    Properties executionProps = new Properties();

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#open()
     * @since 4.3
     */
    public void open() {
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isOpen()
     * @since 4.3
     */
    public boolean isOpen() {
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#save(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    public IStatus save( IProgressMonitor monitor ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     * @since 4.3
     */
    public IStatus save( IProgressMonitor monitor,
                         boolean minimal ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isSaveRequired()
     * @since 4.3
     */
    public boolean isSaveRequired() {
        return this.saveRequired;
    }

    public void setSaveRequired( boolean saveReq ) {
        this.saveRequired = saveReq;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isSaveRequiredForValidVdb()
     * @since 4.3
     */
    public boolean isSaveRequiredForValidVdb() {
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isStale(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 4.3
     */
    public boolean isStale( ModelReference model ) {
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isStale()
     * @since 4.3
     */
    public boolean isStale() {
        return this.isStale;
    }

    public void setStale( boolean stale ) {
        this.isStale = stale;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#setModified()
     * @since 4.3
     */
    public void setModified() {
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#close()
     * @since 4.3
     */
    public void close() {
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getVirtualDatabase()
     * @since 4.3
     */
    public VirtualDatabase getVirtualDatabase() {
        return this.database;
    }

    /**
     * @param database The database to set.
     * @since 4.3
     */
    public void setDatabase( VirtualDatabase database ) {
        this.database = database;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getVdbContainer()
     * @since 4.3
     */
    public Container getVdbContainer() {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#addModel(org.eclipse.core.runtime.IProgressMonitor,
     *      org.eclipse.core.runtime.IPath, boolean)
     * @since 4.3
     */
    public ModelReference[] addModel( IProgressMonitor progressMonitor,
                                      IPath pathInWorkspace,
                                      boolean addDependentModels ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#removeModel(org.eclipse.core.runtime.IPath)
     * @since 4.3
     */
    public IStatus removeModel( IPath pathInWorkspace ) {
        return null;
    }

    public IStatus removeModel( IPath pathInWorkspace,
                                boolean deleteLocal ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#refreshModel(org.eclipse.core.runtime.IProgressMonitor,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.3
     */
    public ModelReference refreshModel( IProgressMonitor progressMonitor,
                                        IPath pathInWorkspace ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getIndexNames()
     * @since 4.3
     */
    public String[] getIndexNames() {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getIndexContent(java.lang.String)
     * @since 4.3
     */
    public String getIndexContent( String indexName ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isVisible(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 4.3
     */
    public boolean isVisible( ModelReference model ) {
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isVisible(java.lang.String)
     * @since 4.3
     */
    public boolean isVisible( String pathInVdb ) {
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#setVisible(com.metamatrix.vdb.edit.manifest.ModelReference, boolean)
     * @since 4.3
     */
    public void setVisible( ModelReference model,
                            boolean isVisible ) {
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getVdbWsdlGenerationOptions()
     * @since 4.3
     */
    public VdbWsdlGenerationOptions getVdbWsdlGenerationOptions() {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getResourcePaths()
     * @since 4.3
     */
    public String[] getResourcePaths() {
        return new String[0];
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getResource(java.lang.String)
     * @since 4.3
     */
    public InputStream getResource( String pathInVdb ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getModelReference(java.lang.String)
     * @since 4.3
     */
    public ModelReference getModelReference( String pathInVdb ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#hasWsdl()
     * @since 4.3
     */
    public boolean hasWsdl() {
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getVdbDefinitionFile()
     * @since 4.3
     */
    public File getVdbDefinitionFile() {
        return this.defnFile;
    }

    /**
     * @param hasDefn The hasDefn to set.
     * @since 4.3
     */
    public void setDefnFile( File defnFile ) {
        this.defnFile = defnFile;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#setPerformServerValidation(boolean)
     * @since 4.3
     */
    public void setPerformServerValidation( boolean serverValidation ) {
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#addNonModel(org.eclipse.core.runtime.IProgressMonitor, java.io.File,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.3
     */
    public NonModelReference addNonModel( IProgressMonitor progressMonitor,
                                          File nonModel,
                                          IPath pathInVdb ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#refreshNonModel(org.eclipse.core.runtime.IProgressMonitor, java.io.File,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.3
     */
    public NonModelReference refreshNonModel( IProgressMonitor theMonitor,
                                              File theNonModel,
                                              IPath thePathInArchive ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#removeNonModel(org.eclipse.core.runtime.IPath)
     * @since 4.3
     */
    public IStatus removeNonModel( IPath pathInWorkspace ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#getPathToVdb()
     * @since 4.3
     */
    public IPath getPathToVdb() {
        return new Path("FakeVdb.vdb"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isReadOnly()
     * @since 4.3
     */
    public boolean isReadOnly() {
        return !getPathToVdb().toFile().canWrite();
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#getVdbContentsFolder()
     * @since 4.3
     */
    public File getVdbContentsFolder() {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#setLoadModelsOnOpen(boolean)
     * @since 4.3
     */
    public void setLoadModelsOnOpen( boolean loadModelsOnOpen ) {
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener( IChangeListener theListener ) {
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener( IChangeListener theListener ) {
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#close(boolean, boolean)
     * @since 4.3
     */
    public void close( boolean reuseTempDir,
                       boolean fireStateChangedEvent,
                       boolean allowVeto ) {
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#getInternalResource(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 4.3
     */
    public Resource getInternalResource( ModelReference modelRef ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#getModelReference(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public ModelReference getModelReference( Resource internalResource ) {
        return null;
    }

    public void addVetoableChangeListener( VetoableChangeListener listener ) {
    }

    public void removeVetoableChangeListener( VetoableChangeListener listener ) {
    }

    public void fireStateChanged() {
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getInternalResourcePath(org.eclipse.core.runtime.IPath)
     * @since 5.0
     */
    public IPath getInternalResourcePath( IPath pathInVdb ) {
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getInternalResourceUri(org.eclipse.core.runtime.IPath)
     * @since 5.0
     */
    public URI getInternalResourceUri( IPath pathInVdb ) {
        return null;
    }

    public void setExecutionProperty( String name,
                                      String value ) {
        executionProps.setProperty(name, value);
    }

    public Properties getExecutionProperties() {
        return executionProps;
    }

    public File addUserFile( final File userFile ) {
        return null;
    }

    public void removeUserFileWithName( final String name ) {

    }

    public Collection getUserFileNames() {
        return Collections.EMPTY_LIST;
    }
}
