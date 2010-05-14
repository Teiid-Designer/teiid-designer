/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit;

import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.common.util.WSDLServletUtil;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/**
 * Provides a context useful for editing VDBs.
 */
public interface VdbEditingContext extends IChangeNotifier {

    final String MANIFEST_MODEL_NAME = "MetaMatrix-VdbManifestModel.xmi"; //$NON-NLS-1$
    final String GENERATED_WSDL_NAME = WSDLServletUtil.GENERATED_WSDL_NAME;
    final String GENERATED_WSDL_FILENAME = WSDLServletUtil.GENERATED_WSDL_FILENAME;
    final String CLOSING = "closing"; //$NON-NLS-1$

    /**
     * Open the editing context and the VDB.
     */
    void open() throws VdbEditException, IOException;

    /**
     * Return whether the VDB is open.
     * @return true if the VDB is open, false otherwise.
     */
    boolean isOpen();

    /**
     * Save the current state of the VDB in its complete form.
     * This is equivalent to calling {@link #save(IProgressMonitor, boolean) save(IProgressMonitor,false)}.
     * @param monitor the progress monitor; may be null
     * @throws IOException if there is an error saving the resource
     */
    IStatus save( IProgressMonitor monitor );

    /**
     * Save the current state of the VDB, with a control specifying whether to only minimal
     * information should be saved.  This method provides an alternative to 
     * {@link #save(IProgressMonitor)} that instead persists only the minimal information
     * necessary (e.g., excluding indexes that can be regenerated).  
     * As a result, this method may complete in substantially less time than
     * a complete save.
     * @param monitor the progress monitor; may be null
     * @param minimal true if this method is to persist only that information which 
     * cannot be determined or rebuilt from other persisted information.
     * @throws IOException if there is an error saving the resource
     */
    IStatus save( IProgressMonitor monitor, boolean minimal );

    /**
     * Return whether there are any changes to the VDB since the last time it was saved.
     * @return true if the VDB has changes that must be saved to prevent information from
     * being lost, false otherwise.
     */
    boolean isSaveRequired();

    /**
     * Return whether saving in the full form is required to make a valid VDB.
     * @return true if {@link #save(IProgressMonitor, boolean) save(IProgressMonitor,true)}
     * needs to be called to make the VDB archive valid, or false otherwise.
     */
    boolean isSaveRequiredForValidVdb();

    /**
     * Check if the model in the vdb that this ModelReference represents is out of synch with
     * the coresspoinding model in the model workspace. 
     * @param model The model
     * @return true if the given model is out of sych with the workspace counterpart.
     * @since 4.3
     */
    boolean isStale(final ModelReference model);

    /**
     * Check if any models in the vdb are out of synch with
     * the coresspoinding model in the model workspace. 
     * @return true if there are any outofsynch models in the vdb.
     * @since 4.3
     */
    boolean isStale();

    /**
     * Mark this VDB as having changed and requiring save.
     */
    void setModified();

    /**
     * Close the editing context and the VDB.
     */
    void close() throws IOException;

    /**
     * Return the VirtualDatabase object, which is the root object of the manifest model.
     * @return the VirtualDatabase; never null
     */
    VirtualDatabase getVirtualDatabase();

    /**
     * Get the container into which the resources/models inside a vdb are loaded. 
     * @return the Container for the vdb, never null
     * @since 4.2
     */
    Container getVdbContainer();

    /**
     * Convenience method to remove and re-add to this VDB the supplied model.  If the specified model
     * does not already exist in this VDB then it will be added.  
     * @param monitor the progress monitor; may be null
     * @param pathInWorkspace the workspace-relative path to the model that is to be added to the VDB.
     * @return the reference to the added model; never null
     * @throws VdbEditException if the model specified by the supplied path cannot be found 
     * in the workspace or there are errors removing or re-adding the model.
     */
    ModelReference refreshModel(final IProgressMonitor monitor, final IPath pathInWorkspace) throws VdbEditException;

    /**
     * Convenience method to add to this VDB the supplied model. If addDependentModels is true,
     * the method will will recursively find all dependent models to the supplied model
     * and add them to this VDB. 
     * @param monitor the progress monitor; may be null
     * @param pathInWorkspace the workspace-relative path to the model that is to be added to the VDB.
     * @param addDependentModels if true, all dependent models will be add, otherwise only the specified
     * model will be added.
     * @return the array of newly added model references or an empty array if there are no new models
     * to add; never null
     * @throws VdbEditException if the model specified by the supplied path cannot be found 
     * in the workspace or there are errors adding a model.
     */
    ModelReference[] addModel(final IProgressMonitor monitor, final IPath pathInWorkspace, final boolean addDependentModels) throws VdbEditException;

    /**
     * Convenience method to remove from this VDB the model given by the supplied path. 
     * This method may update model dependencies, and it may actually remove other models
     * referenced only by the model being removed.  Calling this method is equivalent to 
     * calling VdbEditingContext.removeModel(IPath,true).
     * @param pathInWorkspace the workspace-relative path to the model that is to be added to the VDB.
     * @return the status of the removed model for the supplied path; never null
     */
    IStatus removeModel( final IPath pathInWorkspace );

    /**
     * Convenience method to remove from this VDB the model given by the supplied path. 
     * This method may update model dependencies, and it may actually remove other models
     * referenced only by the model being removed.
     * @param pathInWorkspace the workspace-relative path to the model that is to be added to the VDB.
     * @param deleteLocal indicates if the model file should be deleted
     * @return the status of the removed model for the supplied path; never null
     */
    IStatus removeModel( final IPath pathInWorkspace, boolean deleteLocal );

    /**
     * Convenience method to add the existing non-model file to this VDB.  An optional pathInArchive may be
     * specified to define the relative path of the entry within the archive file.  The pathInArchive 
     * must include the name of the file to be used as the name of the archive entry and may not be null.
     * @param monitor the progress monitor; may be null
     * @param nonModel the file to be added
     * @param pathInArchive the relative path to the non-model within the VDB archive; may not be null
     * @return the non-model references; never null
     * @throws VdbEditException if the file specified by the supplied file does not exist or there are errors adding the file.
     */
    NonModelReference addNonModel(final IProgressMonitor monitor, final File nonModel, final IPath pathInArchive) throws VdbEditException;

    /**
     * Convenience method to remove and re-add to this VDB the supplied non-model.  If the specified non-model
     * does not already exist in this VDB then it will be added.
     * @param monitor the progress monitor; may be null
     * @param nonModel the file to be refreshed
     * @param pathInArchive the relative path to the non-model within the VDB archive; may not be null
     * @return the non-model reference; never null
     * @throws VdbEditException if the file specified by the supplied file does not exist or there are errors
     * removing or re-adding the non-model
     */
    NonModelReference refreshNonModel(final IProgressMonitor monitor, final File nonModel, final IPath pathInArchive) throws VdbEditException;

    /**
     * Convenience method to remove from this VDB the non-model given by the supplied path. 
     * @param pathInArchive the relative path to the non-model within the VDB archive; may not be null
     * @return the status of the removed non-model for the supplied path; never null
     */
    IStatus removeNonModel( final IPath pathInArchive );

    /**
     * Return the array of index names available in this archive.
     * @return the array of index names; may be empty, but never null
     */
    String[] getIndexNames();

    /**
     * Get the stringified content of the index with the supplied name.  The name must match one of 
     * the names of the {@link #getIndexNames() available indexes}.
     * @param indexName the name of the index; may not be null and must be one of the strings returned
     * from {@link #getIndexNames()}
     * @return the content of the index; may be null if the index has no content
     */
    String getIndexContent( final String indexName );

    /**
     * Obtain the visibility of the specified ModelReference for this VDB. 
     * @param model the ModelReference of a model in this VDB.
     * @return true if the model is currently marked visible, false if it is hidden.  If the specified
     * ModelReference is not within this VDB, the method will return false.
     * @since 4.2
     */
    boolean isVisible( final ModelReference model );
    
    /**
     * Obtain the visibility for the file at the specified path in the VDB, if the file cannot be found 
     * then this returns a false. 
     * @param pathInVdb The path to the resource in the vdb
     * @return true if the model is currently marked visible, false if it is hidden, if the file cannot be found 
     * then this returns a false.
     * @since 4.2
     */
    boolean isVisible( final String pathInVdb);    

    /**
     * Set the visibility of the specified ModelReference for this VDB. 
     * @param model the ModelReference of a model in this VDB.
     * @param isVisible true if the model is to be marked visible, false if it is to be marked hidden.
     * @since 4.2
     */
    void setVisible( final ModelReference model, final boolean isVisible );

    /**
     * Return the interface that defines the WSDL generation options.
     * @return The interface VdbWsdlGenerationOptions
     */
    VdbWsdlGenerationOptions getVdbWsdlGenerationOptions();

    /**
     * Get paths to all the models, xsd, wsdl, DEF files in the vdb.  
     * @return an array of paths to models in the vdb
     * @since 4.2
     */
    String[] getResourcePaths();

    /**
     * Get the contents of the resource as an input stream given the path to the resource
     * in the vdb 
     * @param pathInVdb The path to the resource in the vdb
     * @return The inputstream with the contents of the resource.
     * @since 4.2
     */
    InputStream getResource(String pathInVdb);

    /**
     * Get the ModelRefernce contents of a model given the path to the model
     * in the vdb 
     * @param pathInVdb The path to the model in the vdb
     * @return The ModelReference to a model in the vdb
     * @since 4.2
     */
    ModelReference getModelReference(String pathInVdb);

    /**
     * Check if the vdb for this context contains a wsdl model file. 
     * @return true if the vdb has a wsdl model file. 
     * @since 4.2
     */
    boolean hasWsdl();

    /**
     * Get the vdb definition file contained in the vdb. If there is no
     * definition file in the vdb, a null is returned. 
     * @return The vdb definition file.
     * @since 4.3
     */
    File getVdbDefinitionFile();

    /**
     * Set whether validation preference settings will be adhered to
     * during VDB validation on save.  If set to true, preference
     * settings will be ignored and default severity levels will be
     * used for all validation rules.  If false, severity levels 
     * defined in the modeler preferences will be used.
     * @param serverValidation
     * @since 4.2
     */
    void setPerformServerValidation(boolean serverValidation);
    
    /**
     * Indicates if the VDB resource is readonly. 
     * @return <code>true</code>if readonly; <code>false</code> otherwise.
     * @since 4.4
     */
    boolean isReadOnly();
    
    /**
     * Adds the given listener to this notifier. Has no effect if an identical listener is already registered.
     * @param theListener the listener being registered
     */
    void addVetoableChangeListener(VetoableChangeListener listener);

    /**
     * Removes the given listener from this notifier. Has no effect if the listener is not registered.
     * @param theListener the listener being unregistered
     */
    void removeVetoableChangeListener(VetoableChangeListener listener);


    /** 
     * Return the execution options of this VDB
     * @since 5.0.2
     */
    Properties getExecutionProperties();
    
    /**
     * Set the execution property for this VDB
     * @param propertyName name of the property
     * @param propertyValue value of the property
     */
    void setExecutionProperty(String propertyName, String propertyValue);
    
    /** 
     * Add User artifact.  If there is already a user artifact with the same name, the existing
     * artifact will be replaced.
     *  @since 5.3.3
     *  @param userFile the supplied userFile to add to the vdb
     */
    public File addUserFile(final File userFile) throws VdbEditException;

    /** 
     * Remove User artifact with the given name.
     *  @since 5.3.3
     *  @param name the name of the artifact to remove
     */
    public void removeUserFileWithName(final String name) throws VdbEditException;
    
    /** 
     * Get User file names
     *  @since 5.3.3
     *  @return the collection of user file names
     */
    public Collection getUserFileNames( );

}
