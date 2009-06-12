/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.container;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ChangeNotifier;

import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.transaction.UndoableListener;
import com.metamatrix.modeler.core.transaction.UnitOfWorkProvider;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * @since 3.1
 */
public interface Container extends ResourceSet {

    /**
     * The identifiers for all extensions referenced within ModelerCore
     */
    public static class OPTIONS {
        // Set whether all model changes will be processed through transactions (value type is java.lang.Boolean)
        public static final String ENABLE_TRANSACTIONS = "com.metamatrix.modeler.core.container.enableTxns"; //$NON-NLS-1$
    }
    
    //############################################################################################################################
	//# Methods                                                                                                                  #
	//############################################################################################################################
    /**
     * Obtain the component from which Notifications are fired and which manages
     * the listeners on the container and the objects managed by the container.
     * @return the Notification source for this container; never null
     * @since 3.1
     */
    ChangeNotifier getChangeNotifier();

    /**
     * Obtain the component that can find objects by primary key.
     * @return the finder for this container; never null
     * @since 3.1
     */
    EObjectFinder getEObjectFinder();

    /**
     * Obtain the component that can find {@link org.eclipse.emf.ecore.resource.Resource} objects in the Container.
     * @return the resource finder for this container; never null
     * @since 4.3
     */
    ResourceFinder getResourceFinder();

    /**
     * Set the component used to find objects by primary key.
     * @param the finder to use for this container; may not be null
     * @since 4.3
     */
    void setEObjectFinder(EObjectFinder finder);

    /**
     * Set the component used to find {@link org.eclipse.emf.ecore.resource.Resource} objects in the Container.
     * @param the resource finder to use for this container; may not be null
     * @since 4.3
     */
    void setResourceFinder(ResourceFinder finder);

    /**
     * Obtain the name of this container
     * @since 3.1
     */
    String getName();
    
    /**
     * Returns a resource for the URI that either already exists, is new but represents an existing file,
     * or is new and represents a brand-new file.
     * @param uri the URI for the resource; may not be null
     * @return the Resource for the URI; never null, since a Resource (and maybe the underlying file) 
     * is created if one does not exist.
     * @throws ModelerCoreException if the resource cannot be loaded or created
     */
    Resource getOrCreateResource( final URI uri ) throws ModelerCoreException;
        
    /**
     * Sets the name of the container.  This method should generally not be called by clients. 
     * @param name The name for the container
     * @throws IllegalArgumentException if the name is not valid
     * @throws IllegalStateException if the container has been started.
     * @since 3.1
     */
    void setName(String newName);
    
    /**
     * Shutdown this container, which will no longer be usable once this method is called.
     * @since 3.1
     */
    void shutdown() throws ModelerCoreException;
    
    /**
     * Start this container.
     * @since 3.1
     */
    void start() throws ModelerCoreException;

    /**
     * Returns the EmfTransactionProvider.
     * @return UnitOfWorkProvider
     * @since 3.1
     */
    UnitOfWorkProvider getEmfTransactionProvider();
    
    /**
     * Returns the MetamodelRegistry.
     * @return MetamodelRegistry
     * @since 3.1
     */
    MetamodelRegistry getMetamodelRegistry();
    
    /**
     * Sets the MetamodelRegistry.
     * @param registry The MetamodelRegistry to set
     * @since 3.1
     */
    void setMetamodelRegistry(MetamodelRegistry registry);
    
    /**
     * Makes this container aware of the resource descriptor.
     * @param resourceDescriptor the ResourceDescriptor
     */
    void addResourceDescriptor(ResourceDescriptor resourceDescriptor) throws ModelerCoreException;
    
    /**
     * Add the given listener to the emfTransactionProvider's UndoableListener list
     * @param listener
     */
    void addUndoableEditListener(UndoableListener listener);
    
    /**
     * Remove the given listener from the emfTransactionProvider's UndoableListener list
     * @param listener
     */
    void removeUndoableEditListener(UndoableListener listener);
    
    /**
     * Add a ResourceSet to be used for resolution of a resource URI.  The
     * specified ResourceSet will be treated as read-only and will never be
     * used to load a resource for the URI being checked.
     * @param resourceSet
     */
    void addExternalResourceSet(ResourceSet resourceSet);
    
    /**
     * Return the array of external {@link org.eclipse.emf.ecore.resource.ResourceSet}
     * instances registered with this container.
     */
    ResourceSet[] getExternalResourceSets();
    
    /**
     * Return this container's datatype manager. 
     * @return the datatype manager; never null
     * @since 4.2
     */
    DatatypeManager getDatatypeManager();

    /**
     * Return the configuration options associated with this container
     * @param options
     * @since 4.3
     */
    Map getOptions();
    
    /**
     * Set the configuration options for this container 
     * @param options
     * @since 4.3
     */
    void setOptions(Map options);

}
