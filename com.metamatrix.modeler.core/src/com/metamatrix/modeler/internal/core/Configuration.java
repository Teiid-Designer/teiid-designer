/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.core;

import java.util.ArrayList;
import java.util.List;

/**
 * This Configuration class represents the runtime configuration of this plugin.  When run within
 * Eclipse, this class is populated by the descriptor information.
 */
public class Configuration {
    
    private final List metamodelDescriptors;
    private final List resourceDescriptors;
    private final List validationDescriptors;    
    private final List associationProviderDescriptors;
    //private final List modelContainerInitializerDescriptors;
    private final List mappingAdapterDescriptors;
    private final List datatypeManagerDescriptors;
    private final List externalResourceDescriptors;
    private final List externalResourceSetDescriptors;
    private final List invocationFactoryHelpers;
    private final List resourceLoadOptions;

    /**
     * Construct an instance of Configuration.
     */
    public Configuration() {
        this.metamodelDescriptors = new ArrayList();
        this.resourceDescriptors = new ArrayList();
        this.validationDescriptors = new ArrayList();  
        //this.modelContainerInitializerDescriptors = new ArrayList();
        this.associationProviderDescriptors = new ArrayList();
        this.mappingAdapterDescriptors = new ArrayList();
        this.datatypeManagerDescriptors = new ArrayList();
        this.externalResourceDescriptors = new ArrayList();
        this.externalResourceSetDescriptors = new ArrayList();
        this.invocationFactoryHelpers = new ArrayList();
        this.resourceLoadOptions = new ArrayList();
    }

    /**
     * Return a list of {@link com.metamatrix.modeler.core.ExternalResourceDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.
     * @return
     */
    public List getExternalResourceDescriptors() {
        return externalResourceDescriptors;
    }


    /**
     * Return a list of {@link com.metamatrix.modeler.core.ExternalResourceSetDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET.
     * @return
     */
    public List getExternalResourceSetDescriptors() {
        return externalResourceSetDescriptors;
    }

    /**
     * Return a list of {@link com.metamatrix.modeler.core.ExtensionDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.DATATYPE_MANAGER.
     * @return
     */
    public List getDatatypeManagerDescriptors() {
        return datatypeManagerDescriptors;
    }

    /**
     * Return a list of {@link com.metamatrix.modeler.core.metamodel.MetamodelDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.METAMODEL.
     * @return
     */
    public List getMetamodelDescriptors() {
        return metamodelDescriptors;
    }

    /**
     * Return a list of {@link com.metamatrix.modeler.core.container.ResourceDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.RESOURCE_FACTORY.
     * @return
     */
    public List getResourceDescriptors() {
        return resourceDescriptors;
    }

    /**
     * Return a list of {@link com.metamatrix.modeler.core.ValidationDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.VALIDATION.
     * @return
     */
    public List getValidationDescriptors() {
        return validationDescriptors;
    }    

    /**
     * Return a list of {@link com.metamatrix.modeler.core.ExtensionDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER.
     * @return
     */
    public List getAssociationProviderDescriptors() {
        return associationProviderDescriptors;
    }    

//  /**
//     * Return a list of {@link com.metamatrix.modeler.core.ExtensionDescriptor}
//     * instances for all extensions of the ModelerCore.EXTENSION_POINT.MODEL_CONTAINER_INITIALIZER.
//     * @return
//     */
//    public List getModelContainerInitializerDescriptors() {
//        return modelContainerInitializerDescriptors;
//    }    
        
    /**
     * Return a list of {@link com.metamatrix.modeler.core.MappingAdapterDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.MODEL_OBJECT_RESOLVER.
     * @return
     */
    public List getMappingAdapterDescriptors() {
        return mappingAdapterDescriptors;
    }    

    /**
     * Return a list of {@link com.metamatrix.modeler.core.ExtensionDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.INVOCATION_FACTORY_HELPER.
     * @return
     */
    public List getInvocationFactoryHelpers() {
        return invocationFactoryHelpers;
    }

    /**
     * Return a list of {@link com.metamatrix.modeler.core.ExtensionDescriptor}
     * instances for all extensions of the ModelerCore.EXTENSION_POINT.RESOURCE_LOAD_OPTIONS.
     * @return
     */
    public List getResourceLoadOptions() {
        return resourceLoadOptions;
    }

}
