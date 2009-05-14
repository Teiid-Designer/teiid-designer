/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;


/** 
 * Interface exposing methods for internal development purposes only.
 * @since 4.3
 */
public interface InternalVdbEditingContext extends VdbEditingContext {

    public static final String PRODUCER_NAME = ModelerCore.ILicense.PRODUCER_NAME; 

    public static final String PRODUCER_VERSION = ModelerCore.ILicense.VERSION; 

    public static final String VDB_CONTAINER_NAME = "VDB Container"; //$NON-NLS-1$
    
    public static final String WSDL_DEFAULT_TARGET_NAMESPACE_URI_PREFIX = "http://com.metamatrix/"; //$NON-NLS-1$
    public static final String WSDL_DEFAULT_NAMESPACE_URI = WsdlPackage.eNS_URI;    // per WS-I

    /**
     * Close the editing context and the VDB but do not remove the TempDirectory instance 
     * from the context so that it can be reused.
     */
    void close(boolean reuseTempDir, boolean fireStateChangedEvent, boolean allowVeto) throws IOException;
    
    /**
     * Set a boolean indicating if the models inside the vdb should be loaded into 
     * the internal resource set on open. 
     */
    void setLoadModelsOnOpen(boolean loadModelsOnOpen);    

    /**
     * Get the directory where the vdb contents are extracted when the vdb editing
     * context is opned. 
     * @return The directory containing vdb contents.
     * @since 4.3
     */
    File getVdbContentsFolder();
    
    /**
     * Get the path to the vdb for this editing context.  
     * @return The IPath to the vdb.
     * @since 4.3
     */
    IPath getPathToVdb();    

    /**
     * Return the resource for the specified VDB ModelRefernce or 
     * null if a resource cannot be found for this reference
     * @param modelRef The ModelReference instance in the VDB
     * @return The resource
     * @since 4.3
     */
    Resource getInternalResource(ModelReference modelRef);

    /**
     * Return the ModelReference for the specified VDB resource or 
     * null if a model reference cannot be found for this resource
     * @param internalResource The resource instance in the VDB
     * @return The ModelReference .
     * @since 4.3
     */
    ModelReference getModelReference(Resource internalResource);

//    /**
//     * Get the {@link org.eclipse.emf.common.util.URI} of the resource internal to the modelcontainer.
//     * @param modelPath The path to the model in the vdb.
//     * @return The URI of the internal resource.
//     * @since 4.3
//     */
//    URI getInternalResourceUri(final String modelPath);
//    
//    /**
//     * Return the ModelReference instance corresponding to the input resource URI or null
//     * if no ModelReference can be found in the VDB reference by that URI. 
//     * @param internalResourceUri The URI of the internal resource. 
//     * @return The modelreference of the model inside the vdb.
//     * @since 4.3
//     */
//    ModelReference getModelReference(URI internalResourceUri);

    /**
     * Notify all registered {@link IChangeListener}s that the state has changed.
     * @since 5.0
     */
    void fireStateChanged();
}
