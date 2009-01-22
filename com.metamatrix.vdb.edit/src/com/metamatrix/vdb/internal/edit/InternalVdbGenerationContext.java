/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.vdb.edit.VdbGenerationContext;


/** 
 * @since 5.0
 */
public interface InternalVdbGenerationContext extends VdbGenerationContext {
    
    final String MATERIALIZED_VIEW_TABLE_MAPPINGS = "MaterializedViewVirtToPhysMappings"; //$NON-NLS-1$
    final String WSDL_GENERATION_OPTIONS = "WsdlGenerationOptions"; //$NON-NLS-1$
    
    /**
     * Return the VdbContext instance associated with this generation context 
     * @return the VdbContext
     * @since 5.0
     */
    VdbContextImpl getVdbContext();

    /**
     * Set the VdbContext instance to be associated with this generation context 
     * @param theVdbContext; may not be null
     * @since 5.0
     */
    void setVdbContext(final VdbContextImpl theVdbContext);
    
    /**
     * Return the ResourceSet instance associated with this generation context 
     * @return the resource set
     * @since 5.0
     */
    ResourceSet getResourceSet();
    
    /**
     * Set the ResourceSet instance to be associated with this generation context 
     * @param theResourceSet; may not be null
     * @since 5.0
     */
    void setResourceSet(final ResourceSet theResourceSet);
    
//    /**
//     * Return the list of any "internal" resources contributed by the VdbContext 
//     * itself or by any previously called artifact generators.  These resources
//     * will not exist in any ResourceSet or be found in a workspace project but
//     * instead are created during the save operation on a VdbContext.  The list
//     * is modifiable so that any additions to it will be accessible by subsequent 
//     * artifact generators.
//     * @return list of resources
//     * @since 5.0
//     */
//    List getInternalResources();
    
    /**
     * Returns the data associated with the specified key. The data could be 
     * contributed by the VdbContext itself or by any previously called artifact 
     * generators. 
     * @param theKey the key whose data is being requested
     * @return the data or <code>null</code> if none found
     * @since 5.0
     */
    Object getData(String theKey);
    
    /**
     * Returns the data map used by the generation context to store information
     * for internal artifact generators 
     * @return
     * @since 5.0
     */
    Map getDataMap();
    
    /**
     * Dispose of the VdbGenerationContext and clean up any associated state
     * @since 5.0
     */
    void dispose();
    
}
