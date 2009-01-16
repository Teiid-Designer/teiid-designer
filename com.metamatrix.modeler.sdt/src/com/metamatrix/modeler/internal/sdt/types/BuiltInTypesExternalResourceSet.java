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
package com.metamatrix.modeler.internal.sdt.types;

import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.modeler.core.ExternalResourceDescriptor;
import com.metamatrix.modeler.core.ExternalResourceSet;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.internal.core.ExternalResourceDescriptorImpl;
import com.metamatrix.modeler.internal.core.ExternalResourceLoader;
import com.metamatrix.modeler.sdt.ModelerSdtPlugin;

public class BuiltInTypesExternalResourceSet implements ExternalResourceSet {

    /** Defines the expected name of the built-in datatype archive file */
    public static final String DATATYPES_ZIP_FILE_NAME = DatatypeConstants.DATATYPES_ZIP_FILE_NAME;

    /** Defines the expected name of the built-in datatype model file */
    public static final String DATATYPES_MODEL_FILE_NAME = DatatypeConstants.DATATYPES_MODEL_FILE_NAME;

    /** Defines the expected built-in datatypes URI */
    public static final String BUILTIN_DATATYPES_URI = DatatypeConstants.BUILTIN_DATATYPES_URI;

    private static final String BUILTIN_DATATYPES_CONTAINER_NAME = "builtInDatatypesContainer"; //$NON-NLS-1$

    private Container builtInTypesContainer;

    /**
     * @see com.metamatrix.modeler.core.ExternalResourceSet#getResourceSet()
     */
    public ResourceSet getResourceSet() {
        if (this.builtInTypesContainer == null) {
            Container container = this.createContainer(BUILTIN_DATATYPES_CONTAINER_NAME);
            this.loadContainer(container);
            this.builtInTypesContainer = container;
        }

        return this.builtInTypesContainer;
    }

    protected Container createContainer( final String containerName ) {
        Container container = null;
        try {
            container = ModelerCore.createEmptyContainer(containerName);
            // Add delegate resource set required by the built-in datatypes model
            // when resolving XMLSchema entity references
            ExternalResourceSet xsdResourceSet = new XsdExternalResourceSet();
            container.addExternalResourceSet(xsdResourceSet.getResourceSet());
        } catch (Throwable t) {
            ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                      t,
                                      ModelerSdtPlugin.Util.getString("BuiltInTypesExternalResourceSet.Error_creating_container_for_the_resource_1", DATATYPES_MODEL_FILE_NAME)); //$NON-NLS-1$
        }
        if (container == null) {
            ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                      ModelerSdtPlugin.Util.getString("BuiltInTypesExternalResourceSet.Error_creating_container_for_the_resource_2", DATATYPES_MODEL_FILE_NAME)); //$NON-NLS-1$
        }
        return container;
    }

    protected void loadContainer( final Container container ) {
        if (container != null) {
            final ExternalResourceDescriptor descriptor = this.getExternalResourceDescriptor();
            final ExternalResourceLoader loader = new ExternalResourceLoader();
            try {
                loader.load(descriptor, container);
            } catch (Throwable t) {
                ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                          t,
                                          ModelerSdtPlugin.Util.getString("BuiltInTypesExternalResourceSet.Error_loading_external_resource_into_the_built-in_datatypes_container_3", descriptor.getResourceName())); //$NON-NLS-1$
            }
        }
    }

    protected ExternalResourceDescriptor getExternalResourceDescriptor() {
        final ExternalResourceDescriptorImpl descriptor = new ExternalResourceDescriptorImpl();

        // Define the name of the resource to load, "builtInDataTypes.xsd"
        descriptor.setResourceName(DATATYPES_MODEL_FILE_NAME);

        // Define the internal URI to use when retrieving this resource,
        // "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
        descriptor.setInternalUri(BUILTIN_DATATYPES_URI);

        // Define the resource location in terms of the declaring plugin location on the a file system
        String resourceURL = null;
        try {
            final URL installURL = ModelerSdtPlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
            resourceURL = FileLocator.toFileURL(new URL(installURL, DATATYPES_ZIP_FILE_NAME)).getFile();
            if (resourceURL == null || resourceURL.length() == 0) {
                ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                          ModelerSdtPlugin.Util.getString("BuiltInTypesExternalResourceSet.Unable_to_create_an_absolute_path_to_the_resource_4", DATATYPES_ZIP_FILE_NAME)); //$NON-NLS-1$
            } else {
                descriptor.setResourceUrl(resourceURL);
            }
        } catch (Throwable t) {
            ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                      t,
                                      ModelerSdtPlugin.Util.getString("BuiltInTypesExternalResourceSet.Error_creating_local_URL_for_5", DATATYPES_ZIP_FILE_NAME)); //$NON-NLS-1$
        }

        // Define the temporary working directory location in terms of the declaring plugin location on the a file system
        String tempDirPath = null;
        try {
            tempDirPath = ModelerSdtPlugin.getDefault().getStateLocation().toOSString();
            if (tempDirPath == null || tempDirPath.length() == 0) {
                ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                          ModelerSdtPlugin.Util.getString("BuiltInTypesExternalResourceSet.Unable_to_create_an_absolute_path_to_the_data_directory_for_resource_6", descriptor.getResourceUrl())); //$NON-NLS-1$
            } else {
                descriptor.setTempDirectoryPath(tempDirPath);
            }
        } catch (Throwable t) {
            ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                      ModelerSdtPlugin.Util.getString("BuiltInTypesExternalResourceSet.Error_creating_the_absolute_path_to_the_data_directory_for_resource_7", descriptor.getResourceUrl())); //$NON-NLS-1$
        }

        return descriptor;
    }

}
