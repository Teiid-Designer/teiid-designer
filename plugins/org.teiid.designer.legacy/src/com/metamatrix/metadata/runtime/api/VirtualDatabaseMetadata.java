/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;

import java.util.Collection;
import java.util.List;

/**
 * A VirtualDatabaseMetadata represent a virtual database in the repository. It provides a set of methods to retrive the
 * information in the virtual database.
 */
public interface VirtualDatabaseMetadata {

    /**
     * Returns <code>true</code> if the metadata for the groups and elements have been loaded. The loading of the details are only
     * used when the metadata tree is beind displayed. So to cut down on the overhead when it's never been asked for, the details
     * are not loaded when only the models are needed by query processing.
     * 
     * @return
     * @since 4.2
     */
    public boolean isModelDetailsLoaded();

    /**
     * Obtain a collection of ProcedureID's for the specified modelID.
     * 
     * @param modelID is the id for the Model
     * @return Collection of ProcedureID's
     * @throws VirtualDatabaseException if an error occurs while trying to read the data.
     */
    public Collection getProcedures( ModelID modelID ) throws VirtualDatabaseException;

    /**
     * Return the models that are used for presentation purposes. This means the models indicate they are visible.
     * 
     * @return
     * @throws VirtualDatabaseException
     * @since 4.2
     */
    public Collection getDisplayableModels() throws VirtualDatabaseException;

    /**
     * Return all models that are defined in the VDB regardless of type or visibility.
     * 
     * @return
     * @throws VirtualDatabaseException
     * @since 4.2
     */
    public Collection getAllModels() throws VirtualDatabaseException;

    /**
     * Return model for the specified name
     * 
     * @return
     * @throws VirtualDatabaseException
     * @since 4.2
     */
    public Model getModel( String name ) throws VirtualDatabaseException;

    /**
     * Returns the visibility for a resource path.
     * 
     * @param resourcePath
     * @return <code>true</code> if the resource is visible.
     * @since 4.2
     */
    public boolean isVisible( String resourcePath ) throws VirtualDatabaseException;

    /**
     * Return an <b>ordered</b> list of ElementID's for the specified groupID.
     * 
     * @param groupID is the group for which the elements are to be obtained.
     * @return List of Element's.
     * @throws VirtualDatabaseException if an error occurs while trying to read the data.
     */
    public List getElementsInGroup( GroupID groupID ) throws VirtualDatabaseException;

    /**
     * Returns the <code>VirtualDatabase</code> for which this metadata object represents.
     * 
     * @return VirtualDatabase
     * @throws VirtualDatabaseException if an error occurs while trying to read the data.
     */
    public VirtualDatabase getVirtualDatabase() throws VirtualDatabaseException;

    /**
     * Returns the VirtualDatabaseID that identifies the VirtualDatabaseMetadata.
     * 
     * @return VirtualDatabaseID
     */
    public VirtualDatabaseID getVirtualDatabaseID();

    /*
     * Return a list all elements' full name in this VDB
     */
    public List getALLPaths( Collection models ) throws VirtualDatabaseException;

    /**
     * Obtain a collection of GroupID's for the specified modelID.
     * 
     * @param modelID is the id for the Model
     * @return Collection of ProcedureID's
     * @throws VirtualDatabaseException if an error occurs while trying to read the data.
     */
    public Collection getGroupsInModel( ModelID modelID ) throws VirtualDatabaseException;

}
