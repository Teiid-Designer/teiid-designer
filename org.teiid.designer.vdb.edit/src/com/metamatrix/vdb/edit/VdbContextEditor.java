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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.NonModelReference;

/**
 * Provides a editor for the VDB context
 */
public interface VdbContextEditor extends VdbContext, IChangeNotifier {

    /**
     * Return the {@link org.eclipse.emf.ecore.resource.ResourceSet} instance associated
     * with this editor.
     */
    ResourceSet getVdbResourceSet();
    
    /**
     * Set the description on this VDB.
     * @param the description; may be null
     */
    void setDescription(String description);
    
    /**
     * Set the VdbContextValidator instance to be used for model validation
     * when a VDB is saved.
     * @param theValidator
     * @since 5.0
     */
    void setVdbContextValidator(VdbContextValidator theValidator);
    
    /**
     * Add the specified artifact generators to the editor.  Artifact generators, executed when 
     * a VDB is saved, contribute additional files to the resultant VDB file for use by downstream 
     * applications.  Possible artifact generators include a materialized view model, wsdl file, 
     * or runtime index file generators.
     * @param theGenerator the VdbArtifactGenerator; may not be null
     * @since 4.3
     */
    void addArtifactGenerator(VdbArtifactGenerator theGenerator);

    /**
     * Convenience method to add to this VDB the supplied model. If addDependentModels is true,
     * the method will will recursively find all dependent models to the supplied model
     * and add them to VDB's manifest.  The newly added models will not appear in the VDB archive 
     * file until a save is executed.
     * @param monitor the progress monitor; may be null
     * @param theModel the file to be added; may not be null
     * @param pathInArchive the path to use when archiving the model; may not be null
     * @param addDependentModels if true, all dependent models will be add, otherwise only the specified
     * model will be added.
     * @return the array of newly added model references or an empty array if there are no new models
     * to add; never null
     * @throws VdbEditException if there are errors adding the model.
     */
    ModelReference[] addModel(IProgressMonitor monitor, File theModel, String pathInArchive, boolean addDependentModels) throws VdbEditException;

    /**
     * Convenience method to remove from this VDB the model associated with the ModelReference.
     * The removed model will not disappear from the VDB archive file until a save is executed.
     * @param monitor the progress monitor; may be null
     * @param theReference the ModelReference for the model being removed; may not be null
     * @return the status of the removed model for the supplied path; never null
     * @throws VdbEditException if there are errors removing the model.
     */
    IStatus removeModel(IProgressMonitor monitor, ModelReference theReference) throws VdbEditException;

    /**
     * Convenience method to add the existing non-model file to this VDB.  An optional pathInArchive may be
     * specified to define the relative path of the entry within the archive file.  The pathInArchive 
     * must include the name of the file to be used as the name of the archive entry and may not be null.
     * @param monitor the progress monitor; may be null
     * @param theNonModel the file to be added; may not be null
     * @param pathInArchive the path to use when archiving the non-model file
     * @return the non-model references; never null
     * @throws VdbEditException if there are errors adding the non-model.
     */
    NonModelReference addNonModel(IProgressMonitor monitor, File theNonModel, String pathInArchive) throws VdbEditException;

    /**
     * Convenience method to remove from this VDB the non-model associated with the NonModelReference.
     * The removed non-model will not disappear from the VDB archive file until a save is executed.
     * @param monitor the progress monitor; may be null
     * @param theReference the NonModelReference for the file being removed; may not be null
     * @return the status of the removed non-model for the supplied path; never null
     * @throws VdbEditException if there are errors removing the non-model.
     */
    IStatus removeNonModel(IProgressMonitor monitor, NonModelReference theReference) throws VdbEditException;
    
    /**
     * Return true if there are model changes 
     * @return
     * @since 5.0
     */
    boolean isSaveRequired();
    
    /**
     * Sets that the context needs to be saved regardless of whether there are model changes. 
     * @since 5.0
     */
    void setSaveIsRequired();

    /**
     * Save the current state of the VDB in its complete form.
     * @param theMonitor the progress monitor; may be null
     * @throws VdbEditException if there is an error saving the resource
     * @return the status of the save operation
     */
    IStatus save(IProgressMonitor theMonitor) throws VdbEditException;
    
    /**
     * Adds the given listener to this notifier. Has no effect if an identical listener is already registered.
     * @param theListener the listener being registered
     */
    void addVetoableChangeListener(VetoableChangeListener theListener);

    /**
     * Removes the given listener from this notifier. Has no effect if the listener is not registered.
     * @param theListener the listener being unregistered
     */
    void removeVetoableChangeListener(VetoableChangeListener theListener);
    
    /**
     * Dispose of the VDB editor and clean up any associated state
     * @since 5.0
     */
    void dispose();
    
    /**
     * Set the execution property for this VDB
     * @param propertyName name of the property
     * @param propertyValue value of the property
     */
    void setExecutionProperty(String propertyName, String propertyValue);

}
