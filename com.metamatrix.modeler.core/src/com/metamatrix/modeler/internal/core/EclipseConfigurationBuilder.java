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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.core.ExtensionDescriptor;
import com.metamatrix.modeler.core.ExternalResourceDescriptor;
import com.metamatrix.modeler.core.MappingAdapterDescriptor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.internal.core.container.ResourceDescriptorImpl;
import com.metamatrix.modeler.internal.core.metamodel.MetamodelDescriptorImpl;
import com.metamatrix.modeler.internal.core.metamodel.MetamodelRootClassDescriptorImpl;

/**
 * EclipseConfigurationBuilder
 */
public class EclipseConfigurationBuilder {

    /**
     * @param config
     */
    public static void build( Configuration config ) {
        // Add all of the metamodel descriptors ...
        final boolean addAspects = true;
        final List descriptors = readMetamodelDescriptors(addAspects);
        config.getMetamodelDescriptors().addAll(descriptors);

        // Add all of the resource descriptors ...
        final List resourceDescriptors = readResourceDescriptors();
        config.getResourceDescriptors().addAll(resourceDescriptors);

        // Add all of the validation descriptors ...
        final List validationDescriptors = readValidationDescriptors();
        config.getValidationDescriptors().addAll(validationDescriptors);

        // Add all of the association provider descriptors ...
        final List associationProviderDescriptors = readAssociationProviderDescriptors();
        config.getAssociationProviderDescriptors().addAll(associationProviderDescriptors);

        // // Add all of the model container initializer descriptors ...
        // final List modelContainerInitializerDescriptors = readModelContainerInitializerDescriptors();
        // config.getModelContainerInitializerDescriptors().addAll(modelContainerInitializerDescriptors);

        // Add all of the model object resolvers ...
        final List modelObjectResolverDescriptors = readModelObjectResolverDescriptors();
        config.getMappingAdapterDescriptors().addAll(modelObjectResolverDescriptors);

        // Add all of the datatype manager descriptors ...
        final List datatypeManagerDescriptors = readDatatypeManagerDescriptors();
        config.getDatatypeManagerDescriptors().addAll(datatypeManagerDescriptors);

        // Add all of the external resource descriptors ...
        final List externalResourceDescriptors = readExternalResourceDescriptors();
        config.getExternalResourceDescriptors().addAll(externalResourceDescriptors);

        // Add all of the external resource set descriptors ...
        final List externalResourceSetDescriptors = readExternalResourceSetDescriptors();
        config.getExternalResourceSetDescriptors().addAll(externalResourceSetDescriptors);

        // Add all of the invocation factory helper descriptors ...
        final List invocationFactoryHelperDescriptors = readInvocationFactoryHelperDescriptors();
        config.getInvocationFactoryHelpers().addAll(invocationFactoryHelperDescriptors);

        // Add all of the invocation factory helper descriptors ...
        final List resourceLoadOptionDescriptors = readResourceLoadOptionDescriptors();
        config.getResourceLoadOptions().addAll(resourceLoadOptionDescriptors);
    }

    /**
     * Scan the plugin registry and return a {@link MappingAdapterDescriptor} instance for each model object resolver found in the
     * plugin registry
     * 
     * @return list of {@link MappingAdapterDescriptor} instances that are representative of what can be found in the plugin
     *         registry
     */
    public static List readModelObjectResolverDescriptors() {
        final List result = new ArrayList();
        final IExtension[] extensions = PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.EOBJECT_MATCHER_FACTORY.UNIQUE_ID);
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            final ExtensionDescriptor descriptor = createModelObjectResolverDescriptor(extension);
            result.add(descriptor);
        }
        return result;
    }

    /**
     * Scan the plugin registry and return a {@link ExtensionDescriptor} instance for each datatype manager extension found in the
     * plugin registry
     * 
     * @return list of {@link ExtensionDescriptor} instances that are representative of what can be found in the plugin registry
     */
    public static List readDatatypeManagerDescriptors() {
        final List result = new ArrayList();
        final IExtension[] extensions = PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.DATATYPE_MANAGER.UNIQUE_ID);
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            final ExtensionDescriptor descriptor = createDatatypeManagerDescriptor(extension);
            result.add(descriptor);
        }
        return result;
    }

    /**
     * Scan the plugin registry and return a {@link ExtensionDescriptor} instance for each external resource set extension found
     * in the plugin registry
     * 
     * @return list of {@link ExtensionDescriptor} instances that are representative of what can be found in the plugin registry
     */
    public static List readExternalResourceSetDescriptors() {
        final List result = new ArrayList();
        final IExtension[] extensions = PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET.UNIQUE_ID);
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            final ExtensionDescriptor descriptor = createExternalResourceSetDescriptor(extension);
            result.add(descriptor);
        }
        return result;
    }

    /**
     * Scan the plugin registry and return a {@link ExternalResourceDescriptor} instance for each external resource extension
     * found in the plugin registry
     * 
     * @return list of {@link ExternalResourceDescriptor} instances that are representative of what can be found in the plugin
     *         registry
     */
    public static List readExternalResourceDescriptors() {
        final List result = new ArrayList();
        final IExtension[] extensions = PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.UNIQUE_ID);
        for (int i = 0; i < extensions.length; i++) {
            IExtension extension = extensions[i];
            ExternalResourceDescriptor descriptor = createExternalResourceDescriptor(extension);
            result.add(descriptor);
        }
        // Sort the ExternalResourceDescriptor based on their priority values -
        // the external resource with the highest priority value will be loaded
        // first while the resource with the lowest priority value will be loaded last.
        Collections.sort(result, new ExternalResourceDescriptorComparator());

        return result;
    }

    /**
     * Scan the plugin registry and return a {@link ExtensionDescriptor} instance for each metamodel found in the plugin registry
     * 
     * @return list of {@link ExtensionDescriptor} instances that are representative of what can be found in the plugin registry
     */
    public static List readAssociationProviderDescriptors() {
        final List result = new ArrayList();
        final IExtension[] extensions = PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER.UNIQUE_ID);
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            final ExtensionDescriptor descriptor = createAssociationProviderDescriptor(extension);
            result.add(descriptor);
        }
        return result;
    }

    /**
     * Scan the plugin registry and return a {@link ValidationDescriptor} instance for each metamodel found in the plugin registry
     * 
     * @return list of {@link ValidationDescriptor} instances that are representative of what can be found in the plugin registry
     */
    public static List readValidationDescriptors() {
        final List result = new ArrayList();
        final IExtension[] extensions = PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.VALIDATION.UNIQUE_ID);
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            result.addAll(createValidationDescriptor(extension));
        }
        return result;
    }

    /**
     * Scan the plugin registry and return a {@link ExtensionDescriptor} instance for each InvocationFactoryHelper extension found
     * in the plugin registry
     * 
     * @return list of {@link ExtensionDescriptor} instances that are representative of what can be found in the plugin registry
     */
    public static List readInvocationFactoryHelperDescriptors() {
        final List result = new ArrayList();
        final IExtension[] extensions = PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.INVOCATION_FACTORY_HELPER.UNIQUE_ID);
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            final ExtensionDescriptor descriptor = createInvocationFactoryHelperDescriptor(extension);
            result.add(descriptor);
        }
        return result;
    }

    /**
     * Scan the plugin registry and return a {@link ExtensionDescriptor} instance for each resource load options extension found
     * in the plugin registry
     * 
     * @return list of {@link ExtensionDescriptor} instances that are representative of what can be found in the plugin registry
     */
    public static List readResourceLoadOptionDescriptors() {
        final List result = new ArrayList();
        final IExtension[] extensions = PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.RESOURCE_LOAD_OPTIONS.UNIQUE_ID);
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            final ExtensionDescriptor descriptor = createResourceLoadOptionDescriptor(extension);
            result.add(descriptor);
        }
        return result;
    }

    /**
     * Scan the plugin registry and return a {@link ResourceDescriptor} instance for each metamodel found in the plugin registry
     * 
     * @return list of {@link ResourceDescriptor} instances that are representative of what can be found in the plugin registry
     */
    public static List readResourceDescriptors() {
        final List result = new ArrayList();

        // Find all of the ResourceFactory extensions ...
        final IExtensionPoint resourceFactoryExtension = Platform.getExtensionRegistry().getExtensionPoint(ModelerCore.PLUGIN_ID,
                                                                                                           ModelerCore.EXTENSION_POINT.RESOURCE_FACTORY.ID);
        if (resourceFactoryExtension == null) {
            final String extensionPointID = ModelerCore.EXTENSION_POINT.RESOURCE_FACTORY.UNIQUE_ID;
            Assertion.isNotNull(resourceFactoryExtension,
                                ModelerCore.Util.getString("EclipseConfigurationBuilder.Extension_point_not_defined", //$NON-NLS-1$
                                                           extensionPointID));
        }

        // Go through all the extensions and build descriptors ...
        final IExtension[] factoryExtensions = resourceFactoryExtension.getExtensions();
        for (int i = 0; i < factoryExtensions.length; i++) {
            final IExtension factoryExtension = factoryExtensions[i];
            final String factoryExtensionID = factoryExtension.getUniqueIdentifier();

            // Get the info about each factory ...
            boolean error = false;
            final ResourceDescriptorImpl descriptor = new ResourceDescriptorImpl(factoryExtensionID);
            List fileExtensions = descriptor.getExtensions();
            List protocols = descriptor.getProtocols();
            final IConfigurationElement[] elements = factoryExtension.getConfigurationElements();
            for (int j = 0; j < elements.length; j++) {
                final IConfigurationElement iConfigurationElement = elements[j];
                if (iConfigurationElement.getName().equals(ModelerCore.EXTENSION_POINT.RESOURCE_FACTORY.ELEMENTS.FILE_EXTENSION)) {
                    final String value = iConfigurationElement.getValue();
                    if (value != null && value.trim().length() != 0) {
                        fileExtensions.add(value);
                    } else {
                        if (ModelerCore.DEBUG) {
                            ModelerCore.Util.log(IStatus.WARNING,
                                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.Ignoring_null_or_zero-length_file_extension_value_on_extension", factoryExtensionID)); //$NON-NLS-1$
                        }
                    }
                }
                if (iConfigurationElement.getName().equals(ModelerCore.EXTENSION_POINT.RESOURCE_FACTORY.ELEMENTS.PROTOCOL)) {
                    final String value = iConfigurationElement.getValue();
                    if (value != null && value.trim().length() != 0) {
                        protocols.add(value);
                    } else {
                        if (ModelerCore.DEBUG) {
                            ModelerCore.Util.log(IStatus.WARNING,
                                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.Ignoring_null_or_zero-length_protocol_value_on_extension", factoryExtensionID)); //$NON-NLS-1$
                        }
                    }
                }
                if (iConfigurationElement.getName().equals(ModelerCore.EXTENSION_POINT.RESOURCE_FACTORY.ELEMENTS.CLASS)) {
                    final String className = iConfigurationElement.getAttribute(ModelerCore.EXTENSION_POINT.RESOURCE_FACTORY.ATTRIBUTES.NAME);
                    if (className != null && className.trim().length() != 0) {
                        final Bundle bundle = Platform.getBundle(factoryExtension.getNamespaceIdentifier());
                        descriptor.setResourceFactoryClass(className, bundle);
                    } else {
                        if (ModelerCore.DEBUG) {
                            ModelerCore.Util.log(IStatus.ERROR,
                                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.A_class_name_must_be_specified_on_extension", factoryExtensionID)); //$NON-NLS-1$
                            error = true;
                        }
                    }
                }
            }

            if (!error) {
                result.add(descriptor);
            }
        }

        return result;
    }

    /**
     * Scan the plugin registry and return a {@link MetamodelDescriptor} instance for each metamodel found in the plugin registry
     * 
     * @param addAspects flag specifying whether to add aspects ...
     * @return list of {@link MetamodelDescriptor} instances that are representative of what can be found in the plugin registry
     */
    public static List readMetamodelDescriptors( final boolean addAspects ) {
        final List result = new ArrayList();
        final IExtension[] extensions = PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.METAMODEL.UNIQUE_ID);
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            final MetamodelDescriptor descriptor = createMetamodelDescriptor(extension);
            result.add(descriptor);
        }

        // Add aspects ...
        if (addAspects) {
            final List aspectExtensions = getMetamodelAspectExtensions();
            for (Iterator iter = aspectExtensions.iterator(); iter.hasNext();) {
                final IExtension extension = (IExtension)iter.next();
                // Iterate over the descriptors ...
                final Iterator descIter = result.iterator();
                while (descIter.hasNext()) {
                    final MetamodelDescriptor descriptor = (MetamodelDescriptor)descIter.next();
                    addMetamodelAspect(descriptor, extension);
                }
            }
        }

        return result;
    }

    /**
     * Return a {@link AssociationDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER</code> extension point.
     * 
     * @param extension
     */
    public static ExtensionDescriptor createAssociationProviderDescriptor( final IExtension extension ) {
        if (extension == null) {
            ArgCheck.isNotNull(extension,
                               ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        final String uniqueId = extension.getExtensionPointUniqueIdentifier();
        if (!ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER.UNIQUE_ID.equals(uniqueId)) {
            ArgCheck.isTrue(ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER.UNIQUE_ID.equals(uniqueId),
                            ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_must_be_an_extension_for_the_point", //$NON-NLS-1$
                                                       ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER.UNIQUE_ID,
                                                       uniqueId));
        }

        final Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
        String extensionId = extension.getSimpleIdentifier();
        String className = null;

        final IConfigurationElement[] elems = extension.getConfigurationElements();
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER.ELEMENTS.PROVIDER_CLASS)) {
                className = elem.getAttribute(ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER.ATTRIBUTES.NAME);
            }
        }

        // Create the ExtensionDescriptor instance
        final ExtensionDescriptorImpl descriptor = new ExtensionDescriptorImpl(extensionId, className, bundle);
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            final Object[] params = new Object[] {extensionId, className, bundle};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Created_new_ExtensionDescriptorImpl_instance_id,_classname,_bundle}_1", params)); //$NON-NLS-1$
        }
        return descriptor;
    }

    /**
     * Return a {@link MappingAdapterDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.MODEL_OBJECT_RESOLVER</code> extension point.
     * 
     * @param extension
     */
    public static MappingAdapterDescriptor createModelObjectResolverDescriptor( final IExtension extension ) {
        if (extension == null) {
            ArgCheck.isNotNull(extension,
                               ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        final String uniqueId = extension.getExtensionPointUniqueIdentifier();
        if (!ModelerCore.EXTENSION_POINT.EOBJECT_MATCHER_FACTORY.UNIQUE_ID.equals(uniqueId)) {
            ArgCheck.isTrue(ModelerCore.EXTENSION_POINT.EOBJECT_MATCHER_FACTORY.UNIQUE_ID.equals(uniqueId),
                            ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_must_be_an_extension_for_the_point", //$NON-NLS-1$
                                                       ModelerCore.EXTENSION_POINT.EOBJECT_MATCHER_FACTORY.UNIQUE_ID,
                                                       uniqueId));
        }

        final Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
        String extensionId = extension.getSimpleIdentifier();
        String className = null;

        final IConfigurationElement[] elems = extension.getConfigurationElements();
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.EOBJECT_MATCHER_FACTORY.ELEMENTS.CLASS)) {
                className = elem.getAttribute(ModelerCore.EXTENSION_POINT.EOBJECT_MATCHER_FACTORY.ATTRIBUTES.NAME);
            }
        }

        // Create the ExtensionDescriptor instance
        final MappingAdapterDescriptorImpl descriptor = new MappingAdapterDescriptorImpl(extensionId, className, bundle);
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            final Object[] params = new Object[] {extensionId, className, bundle};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Created_new_ModelObjectResolverDescriptorImpl_instance", params)); //$NON-NLS-1$
        }
        return descriptor;

    }

    /**
     * Return a {@link ExtensionDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.DATATYPE_MANAGER</code> extension point.
     * 
     * @param extension
     */
    public static ExtensionDescriptor createDatatypeManagerDescriptor( final IExtension extension ) {
        if (extension == null) {
            ArgCheck.isNotNull(extension,
                               ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        final String uniqueId = extension.getExtensionPointUniqueIdentifier();
        if (!ModelerCore.EXTENSION_POINT.DATATYPE_MANAGER.UNIQUE_ID.equals(uniqueId)) {
            ArgCheck.isTrue(ModelerCore.EXTENSION_POINT.DATATYPE_MANAGER.UNIQUE_ID.equals(uniqueId),
                            ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_must_be_an_extension_for_the_point", //$NON-NLS-1$
                                                       ModelerCore.EXTENSION_POINT.DATATYPE_MANAGER.UNIQUE_ID,
                                                       uniqueId));
        }

        final Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
        String extensionId = extension.getSimpleIdentifier();
        String className = null;

        final IConfigurationElement[] elems = extension.getConfigurationElements();
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.DATATYPE_MANAGER.ELEMENTS.CLASS)) {
                className = elem.getAttribute(ModelerCore.EXTENSION_POINT.DATATYPE_MANAGER.ATTRIBUTES.NAME);
            }
        }

        // Create the ExtensionDescriptor instance
        final ExtensionDescriptorImpl descriptor = new ExtensionDescriptorImpl(extensionId, className, bundle);
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            final Object[] params = new Object[] {extensionId, className, bundle};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Created_new_ExtensionDescriptorImpl_instance_1", params)); //$NON-NLS-1$
        }
        return descriptor;

    }

    /**
     * Return a {@link ExtensionDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.DATATYPE_MANAGER</code> extension point.
     * 
     * @param extension
     */
    public static ExtensionDescriptor createInvocationFactoryHelperDescriptor( final IExtension extension ) {
        if (extension == null) {
            ArgCheck.isNotNull(extension,
                               ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        final String uniqueId = extension.getExtensionPointUniqueIdentifier();
        if (!ModelerCore.EXTENSION_POINT.INVOCATION_FACTORY_HELPER.UNIQUE_ID.equals(uniqueId)) {
            ArgCheck.isTrue(ModelerCore.EXTENSION_POINT.INVOCATION_FACTORY_HELPER.UNIQUE_ID.equals(uniqueId),
                            ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_must_be_an_extension_for_the_point", //$NON-NLS-1$
                                                       ModelerCore.EXTENSION_POINT.INVOCATION_FACTORY_HELPER.UNIQUE_ID,
                                                       uniqueId));
        }

        final Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
        String extensionId = extension.getSimpleIdentifier();
        String className = null;

        final IConfigurationElement[] elems = extension.getConfigurationElements();
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.INVOCATION_FACTORY_HELPER.ELEMENTS.CLASS)) {
                className = elem.getAttribute(ModelerCore.EXTENSION_POINT.INVOCATION_FACTORY_HELPER.ATTRIBUTES.NAME);
            }
        }

        // Create the ExtensionDescriptor instance
        final ExtensionDescriptorImpl descriptor = new ExtensionDescriptorImpl(extensionId, className, bundle);
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            final Object[] params = new Object[] {extensionId, className, bundle};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Created_new_ExtensionDescriptorImpl_instance_1", params)); //$NON-NLS-1$
        }
        return descriptor;

    }

    /**
     * Return a {@link ExtensionDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.RESOURCE_LOAD_OPTIONS</code> extension point.
     * 
     * @param extension
     */
    public static ExtensionDescriptor createResourceLoadOptionDescriptor( final IExtension extension ) {
        if (extension == null) {
            ArgCheck.isNotNull(extension,
                               ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        final String uniqueId = extension.getExtensionPointUniqueIdentifier();
        if (!ModelerCore.EXTENSION_POINT.RESOURCE_LOAD_OPTIONS.UNIQUE_ID.equals(uniqueId)) {
            ArgCheck.isTrue(ModelerCore.EXTENSION_POINT.RESOURCE_LOAD_OPTIONS.UNIQUE_ID.equals(uniqueId),
                            ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_must_be_an_extension_for_the_point", //$NON-NLS-1$
                                                       ModelerCore.EXTENSION_POINT.RESOURCE_LOAD_OPTIONS.UNIQUE_ID,
                                                       uniqueId));
        }

        final Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
        String extensionId = extension.getSimpleIdentifier();
        String className = null;

        final IConfigurationElement[] elems = extension.getConfigurationElements();
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.RESOURCE_LOAD_OPTIONS.ELEMENTS.CLASS)) {
                className = elem.getAttribute(ModelerCore.EXTENSION_POINT.RESOURCE_LOAD_OPTIONS.ATTRIBUTES.NAME);
            }
        }

        // Create the ExtensionDescriptor instance
        final ExtensionDescriptorImpl descriptor = new ExtensionDescriptorImpl(extensionId, className, bundle);
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            final Object[] params = new Object[] {extensionId, className, bundle};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Created_new_ExtensionDescriptorImpl_instance_1", params)); //$NON-NLS-1$
        }
        return descriptor;

    }

    /**
     * Return a {@link ExtensionDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET</code> extension point.
     * 
     * @param extension
     */
    public static ExtensionDescriptor createExternalResourceSetDescriptor( final IExtension extension ) {
        if (extension == null) {
            ArgCheck.isNotNull(extension,
                               ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        final String uniqueId = extension.getExtensionPointUniqueIdentifier();
        if (!ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET.UNIQUE_ID.equals(uniqueId)) {
            ArgCheck.isTrue(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET.UNIQUE_ID.equals(uniqueId),
                            ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_must_be_an_extension_for_the_point", //$NON-NLS-1$
                                                       ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET.UNIQUE_ID,
                                                       uniqueId));
        }

        final Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
        final Properties properties = new Properties();
        String extensionId = extension.getSimpleIdentifier();
        String className = null;

        final IConfigurationElement[] elems = extension.getConfigurationElements();
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET.ELEMENTS.CLASS)) {
                className = elem.getAttribute(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET.ATTRIBUTES.NAME);
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE_SET.ELEMENTS.PROPERTIES)) {
                final String[] propNames = elem.getAttributeNames();
                for (int i = 0; i < propNames.length; i++) {
                    final String name = propNames[i];
                    final String value = elem.getAttribute(name);
                    properties.setProperty(name, value);
                }
            }
        }

        // Create the ExternalResourceSetDescriptor instance
        final ExternalResourceSetDescriptorImpl descriptor = new ExternalResourceSetDescriptorImpl(extensionId, className, bundle);
        descriptor.setProperties(properties);
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            final Object[] params = new Object[] {extensionId, className, bundle};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Created_new_ExternalResourceSetDescriptorImpl_instance__id,_classname,_bundle_1", params)); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Set_ExternalResourceSetDescriptorImpl_properties_to_2", properties)); //$NON-NLS-1$
        }
        return descriptor;

    }

    /**
     * Return a {@link ExternalResourceDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE</code> extension point.
     * 
     * @param extension
     */
    public static ExternalResourceDescriptor createExternalResourceDescriptor( final IExtension extension ) {
        if (extension == null) {
            ArgCheck.isNotNull(extension,
                               ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        final String uniqueId = extension.getExtensionPointUniqueIdentifier();
        if (!ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.UNIQUE_ID.equals(uniqueId)) {
            ArgCheck.isTrue(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.UNIQUE_ID.equals(uniqueId),
                            ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_must_be_an_extension_for_the_point", //$NON-NLS-1$
                                                       ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.UNIQUE_ID,
                                                       uniqueId));
        }

        // Create the ExternalResourceDescriptor instance
        final ExternalResourceDescriptorImpl descriptor = new ExternalResourceDescriptorImpl();
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Created_new_ExternalResourceDescriptorImpl_instance_1")); //$NON-NLS-1$
        }

        // Set the plugin ID
        final String pluginId = extension.getNamespaceIdentifier();
        descriptor.setPluginID(pluginId);
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_plugin_ID_to_2", pluginId)); //$NON-NLS-1$
        }

        // Set the extension ID
        descriptor.setExtensionID(extension.getSimpleIdentifier());
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_extension_ID_to_3", extension.getSimpleIdentifier())); //$NON-NLS-1$
        }

        final Properties properties = new Properties();
        final IConfigurationElement[] elems = extension.getConfigurationElements();
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            final String elemValue = elem.getValue();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.ELEMENTS.RESOURCE_NAME) && elemValue != null) {
                descriptor.setResourceName(elemValue);
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_external_resource_name_to_4", elemValue)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.ELEMENTS.RESOURCE_URL) && elemValue != null) {
                descriptor.setResourceUrl(elemValue);
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_external_resource_URL_to_5", elemValue)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.ELEMENTS.INTERNAL_URI) && elemValue != null) {
                descriptor.setInternalUri(elemValue);
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_external_resource_URI_to_6", elemValue)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.ELEMENTS.PRIORITY) && elemValue != null) {
                descriptor.setPriority(Integer.parseInt(elemValue));
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_external_resource_priority_to_7", elemValue)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.ELEMENTS.PROPERTIES)) {
                final String[] propNames = elem.getAttributeNames();
                for (int i = 0; i < propNames.length; i++) {
                    final String name = propNames[i];
                    final String value = elem.getAttribute(name);
                    properties.setProperty(name, value);
                }
                descriptor.setProperties(properties);
            }
        }

        // Check for required descriptor values
        if (descriptor.getResourceUrl() == null || descriptor.getResourceUrl().length() == 0) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.Error_the_external_resource_URL_was_not_defined_in_the_extension_1", extension.getSimpleIdentifier())); //$NON-NLS-1$
        }
        if (descriptor.getResourceName() == null || descriptor.getResourceName().length() == 0) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.Error_the_external_resource_name_was_not_defined_in_the_extension_2", extension.getSimpleIdentifier())); //$NON-NLS-1$
        }

        // Define the resource location in terms of the declaring plugin location on the a file system
        URL resourceURL = null;
        String resource = null;
        final Bundle bundle = Platform.getBundle(pluginId);
        try {
            final URL installURL = bundle.getEntry("/"); //$NON-NLS-1$
            resourceURL = bundle.getResource(descriptor.getResourceUrl());

            if (resourceURL == null) {
                resource = FileLocator.toFileURL(new URL(installURL, descriptor.getResourceUrl())).getFile();
                if (resource == null || resource.length() == 0) {
                    ModelerCore.Util.log(IStatus.ERROR,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.Unable_to_create_an_absolute_path_to_the_resource_1", descriptor.getResourceUrl())); //$NON-NLS-1$
                    if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                        ModelerCore.Util.log(IStatus.INFO,
                                             ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_external_resource_URL_to_5", descriptor.getResourceUrl())); //$NON-NLS-1$
                    }
                }
            } else {
                resource = resourceURL.toString();
            }

            if (resource != null) {
                descriptor.setResourceUrl(resource);
            }
        } catch (Throwable t) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 t,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.Error_creating_local_URL_for_2", descriptor.getResourceUrl())); //$NON-NLS-1$
        }

        // Define the temporary working directory location in terms of the declaring plugin location on the a file system
        String tempDirPath = null;
        try {
            tempDirPath = Platform.getStateLocation(bundle).toOSString();
            if (tempDirPath == null || tempDirPath.length() == 0) {
                ModelerCore.Util.log(IStatus.ERROR,
                                     ModelerCore.Util.getString("EclipseConfigurationBuilder.Unable_to_create_an_absolute_path_to_the_data_directory_for_resource_1", descriptor.getResourceUrl())); //$NON-NLS-1$
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_temporary_directory_path_to_1", tempDirPath)); //$NON-NLS-1$
                }
            } else {
                descriptor.setTempDirectoryPath(tempDirPath);
            }
        } catch (Throwable t) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.Error_creating_the_absolute_path_to_the_data_directory_for_resource_2", descriptor.getResourceUrl())); //$NON-NLS-1$
        }

        return descriptor;

    }

    /**
     * Return a {@link ValidationDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.VALIDATION</code> extension point.
     * 
     * @param extension
     */
    public static List createValidationDescriptor( final IExtension extension ) {
        if (extension == null) {
            ArgCheck.isNotNull(extension,
                               ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        final String uniqueId = extension.getExtensionPointUniqueIdentifier();
        if (!ModelerCore.EXTENSION_POINT.VALIDATION.UNIQUE_ID.equals(uniqueId)) {
            ArgCheck.isTrue(ModelerCore.EXTENSION_POINT.VALIDATION.UNIQUE_ID.equals(uniqueId),
                            ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_must_be_an_extension_for_the_point", //$NON-NLS-1$
                                                       ModelerCore.EXTENSION_POINT.VALIDATION.UNIQUE_ID,
                                                       uniqueId));
        }

        if (ModelerCore.DEBUG_VALIDATION) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Created_new_ValidationDescriptorImpl_instance_1")); //$NON-NLS-1$
        }

        if (ModelerCore.DEBUG_VALIDATION) {
            final Object[] params = new Object[] {extension.getSimpleIdentifier()};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_extension_ID_to", params)); //$NON-NLS-1$
        }

        final IConfigurationElement[] elems = extension.getConfigurationElements();
        List descriptors = new ArrayList(elems.length);
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.VALIDATION.ELEMENTS.PREFERENCE)) {
                final String prefName = elem.getAttribute(ModelerCore.EXTENSION_POINT.VALIDATION.ATTRIBUTES.NAME);
                final String prefLabel = elem.getAttribute(ModelerCore.EXTENSION_POINT.VALIDATION.ATTRIBUTES.LABEL);
                final String prefCategory = elem.getAttribute(ModelerCore.EXTENSION_POINT.VALIDATION.ATTRIBUTES.CATEGORY);
                final String prefToolTip = elem.getAttribute(ModelerCore.EXTENSION_POINT.VALIDATION.ATTRIBUTES.TOOL_TIP);
                final String defaultValue = elem.getAttribute(ModelerCore.EXTENSION_POINT.VALIDATION.ATTRIBUTES.DEFAULT);

                if (prefName == null) {
                    continue;
                }
                // Create the MetamodelDescriptor instance
                final ValidationDescriptorImpl descriptor = new ValidationDescriptorImpl();
                // Set the extension ID
                descriptor.setExtensionID(extension.getSimpleIdentifier());
                descriptor.setPrefernceName(prefName);
                descriptor.setPrefernceLabel(prefLabel);
                descriptor.setPrefernceCategory(prefCategory);
                descriptor.setPrefernceToolTip(prefToolTip);
                descriptor.setDefaultOption(defaultValue);
                if (ModelerCore.DEBUG_VALIDATION) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_NAME_to", new Object[] {prefName})); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_LABEL_to", new Object[] {prefLabel})); //$NON-NLS-1$
                    if (prefCategory != null) {
                        ModelerCore.Util.log(IStatus.INFO,
                                             ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_CATEGORY_to", new Object[] {prefCategory})); //$NON-NLS-1$
                    }
                    if (prefToolTip != null) {
                        ModelerCore.Util.log(IStatus.INFO,
                                             ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_TOOLTIP_to", new Object[] {prefToolTip})); //$NON-NLS-1$
                    }
                    if (defaultValue != null) {
                        ModelerCore.Util.log(IStatus.INFO,
                                             ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Set_the_DEFAULT_OPTION_to_1") + new Object[] {defaultValue}); //$NON-NLS-1$
                    }
                }

                descriptors.add(descriptor);
            }
        }

        return descriptors;
    }

    /**
     * Return a {@link MetamodelDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.METAMODEL</code> extension point.
     * 
     * @param extension
     */
    public static MetamodelDescriptor createMetamodelDescriptor( final IExtension extension ) {
        if (extension == null) {
            ArgCheck.isNotNull(extension,
                               ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        final String uniqueId = extension.getExtensionPointUniqueIdentifier();
        if (!ModelerCore.EXTENSION_POINT.METAMODEL.UNIQUE_ID.equals(uniqueId)) {
            ArgCheck.isTrue(ModelerCore.EXTENSION_POINT.METAMODEL.UNIQUE_ID.equals(uniqueId),
                            ModelerCore.Util.getString("EclipseConfigurationBuilder.The_IExtension_must_be_an_extension_for_the_point", //$NON-NLS-1$
                                                       ModelerCore.EXTENSION_POINT.METAMODEL.UNIQUE_ID,
                                                       uniqueId));
        }

        final Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
        final Properties properties = new Properties();

        // Create the MetamodelDescriptor instance
        String namespaceURI = null;
        String ePackageClassName = null;
        final IConfigurationElement[] elems = extension.getConfigurationElements();
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            final String elemValue = elem.getValue();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.URI) && elemValue != null) {
                namespaceURI = elemValue;
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.PACKAGE_CLASS)) {
                ePackageClassName = elem.getAttribute(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.NAME);
            }
        }
        final MetamodelDescriptorImpl descriptor = new MetamodelDescriptorImpl(namespaceURI, ePackageClassName, bundle);
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Created_new_MetamodelDescriptorImpl_instance_1")); //$NON-NLS-1$

            Object[] params = new Object[] {namespaceURI};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_URI_to", params)); //$NON-NLS-1$

            params = new Object[] {ePackageClassName, bundle};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_EPackage_class_name_to,_and_the_bundle_to", params)); //$NON-NLS-1$
        }

        // Set the extension ID
        descriptor.setExtensionID(extension.getSimpleIdentifier());
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            final Object[] params = new Object[] {extension.getSimpleIdentifier()};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_extension_ID_to", params)); //$NON-NLS-1$
        }

        // Set the metamodel display name to initially be the extension name
        descriptor.setDisplayName(extension.getLabel());
        if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
            final Object[] params = new Object[] {extension.getLabel()};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_name_to", params)); //$NON-NLS-1$
        }

        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            final String elemValue = elem.getValue();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.ALTERNATE_URI) && elemValue != null) {
                descriptor.addAlternateNamespaceURI(elemValue);
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    final Object[] params = new Object[] {elemValue};
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Adding_alternate_URI_1", params)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.DISPLAY_NAME) && elemValue != null) {
                descriptor.setDisplayName(elemValue);
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.FILE_EXTENSION) && elemValue != null) {
                descriptor.setFileExtension(elemValue);
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    final Object[] params = new Object[] {elemValue};
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Setting_file_extension_to_1", params)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.ADAPTER_CLASS)) {
                final String className = elem.getAttribute(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.NAME);
                descriptor.addAdapterFactoryBundle(className, bundle);
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    final Object[] params = new Object[] {className, bundle};
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Set_the_AdapterFactory_class_name_to,_and_the_bundle_to", params)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.ALLOWABLE_MODEL_TYPE) && elemValue != null) {
                descriptor.addAllowableModelType(elemValue);
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    final Object[] params = new Object[] {elemValue};
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Adding_model_type_1", params)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.ROOT_ENTITY_CLASS)) {
                final String className = elem.getAttribute(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.NAME);
                final String maxOccurs = elem.getAttribute(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.ROOT_ENTITY_MAX_OCCURS);

                final MetamodelRootClassDescriptorImpl rootDescriptor = new MetamodelRootClassDescriptorImpl(uniqueId, className,
                                                                                                             bundle);
                rootDescriptor.setMaxOccurs(maxOccurs);
                descriptor.addRootClassDescriptor(rootDescriptor);

                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    final Object[] params = new Object[] {className, bundle};
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Added_root_entity_class_name,_and_bundle_1", params)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.PROPERTIES)) {
                final String[] propNames = elem.getAttributeNames();
                for (int i = 0; i < propNames.length; i++) {
                    final String name = propNames[i];
                    final String value = elem.getAttribute(name);
                    properties.setProperty(name, value);
                }
                descriptor.setProperties(properties);
                if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                    final Object[] params = new Object[] {properties};
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.Set_metamodel_properties_to_2", params)); //$NON-NLS-1$
                }
            } else if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.INITIALIZERS)) {
                final IConfigurationElement[] initializers = elem.getChildren();
                for (int i = 0; i < initializers.length; ++i) {
                    final IConfigurationElement initializer = initializers[i];
                    final String initializerTagName = initializer.getName();
                    if (initializerTagName.equals(ModelerCore.EXTENSION_POINT.METAMODEL.ELEMENTS.INITIALIZER)) {
                        // Get the attributes ...
                        final String name = initializer.getAttribute(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.NAME);
                        final String desc = initializer.getAttribute(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.DESCRIPTION);
                        final String className = initializer.getAttribute(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.CLASS);
                        descriptor.addModelInitializer(name, desc, className, bundle);
                    }
                }
            }
        }

        return descriptor;

    }

    private static List getMetamodelAspectExtensions() {
        // Add to the UML diagram MetamodelAspect extensions
        final List extensions = new ArrayList();
        addExtensionsToList(PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.UNIQUE_ID), extensions);

        // Add to the item provider MetamodelAspect extensions
        addExtensionsToList(PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.ITEM_PROVIDER_ASPECT.UNIQUE_ID), extensions);

        // Add to the dependency MetamodelAspect extensions
        addExtensionsToList(PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.DEPENDENCY_ASPECT.UNIQUE_ID), extensions);

        // Add to the validation MetamodelAspect extensions
        addExtensionsToList(PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.VALIDATION_ASPECT.UNIQUE_ID), extensions);

        // Add to the feature constraint MetamodelAspect extensions
        addExtensionsToList(PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.FEATURE_CONSTRAINT_ASPECT.UNIQUE_ID),
                            extensions);

        // Add to the sql MetamodelAspect extensions
        addExtensionsToList(PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.SQL_ASPECT.UNIQUE_ID), extensions);

        // Add to the relationship MetamodelAspect extensions
        addExtensionsToList(PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.RELATIONSHIP_ASPECT.UNIQUE_ID), extensions);

        // Add to the imports MetamodelAspect extensions
        addExtensionsToList(PluginUtilities.getExtensions(ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.UNIQUE_ID), extensions);

        return extensions;
    }

    private static void addExtensionsToList( final IExtension[] extensions,
                                             final List extensionList ) {
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            if (!extensionList.contains(extension)) {
                extensionList.add(extension);
            }
        }
    }

    /**
     * Return a {@link MetamodelDescriptor} instance for the specified {@link IExtension} representing an extension to the
     * <code>ModelerCore.EXTENSION_POINT.METAMODEL</code> extension point.
     * 
     * @param extension
     */
    private static void addMetamodelAspect( final MetamodelDescriptor descriptor,
                                            final IExtension extension ) {
        // If the IExtension is a valid MetamodelAspect extension ...
        if (validMetamodelAspect(descriptor, extension)) {
            final Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());

            // Get the extension point ID for the aspect (e.g. "com.metamatrix.modeler.core.umlDiagramAspect")
            final String uniqueExtensionPointID = extension.getExtensionPointUniqueIdentifier();

            // Extract information from the extension ...
            final IConfigurationElement[] elems = extension.getConfigurationElements();
            for (int j = 0; j < elems.length; j++) {
                final IConfigurationElement elem = elems[j];
                final String elemName = elem.getName();
                if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL_ASPECT.ELEMENTS.FACTORY_CLASS)) {
                    final String className = elem.getAttribute(ModelerCore.EXTENSION_POINT.METAMODEL_ASPECT.ATTRIBUTES.NAME);
                    ((MetamodelDescriptorImpl)descriptor).addAspectFactoryBundle(uniqueExtensionPointID, className, bundle);
                    if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                        final Object[] params = new Object[] {className, bundle, uniqueExtensionPointID};
                        ModelerCore.Util.log(IStatus.INFO,
                                             ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Added_the_MetamodelAspect,_bundle,_extension_point_ID", params)); //$NON-NLS-1$
                    }
                }
            }
        }
    }

    /**
     * Returns true if the specified IExtension represents MetamodelAspect extension applicable to this MetamodelDescriptor
     */
    private static boolean validMetamodelAspect( final MetamodelDescriptor descriptor,
                                                 final IExtension extension ) {
        if (descriptor == null || extension == null) {
            return false;
        }

        // Get the extension point ID for the aspect (e.g. "com.metamatrix.modeler.core.umlDiagramAspect")
        final String uniqueExtensionPointID = extension.getExtensionPointUniqueIdentifier();

        // Check if the extension is not a known metamodel aspect extension ...
        if (!uniqueExtensionPointID.equals(ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.UNIQUE_ID)
            && !uniqueExtensionPointID.equals(ModelerCore.EXTENSION_POINT.ITEM_PROVIDER_ASPECT.UNIQUE_ID)
            && !uniqueExtensionPointID.equals(ModelerCore.EXTENSION_POINT.DEPENDENCY_ASPECT.UNIQUE_ID)
            && !uniqueExtensionPointID.equals(ModelerCore.EXTENSION_POINT.VALIDATION_ASPECT.UNIQUE_ID)
            && !uniqueExtensionPointID.equals(ModelerCore.EXTENSION_POINT.SQL_ASPECT.UNIQUE_ID)
            && !uniqueExtensionPointID.equals(ModelerCore.EXTENSION_POINT.RELATIONSHIP_ASPECT.UNIQUE_ID)
            && !uniqueExtensionPointID.equals(ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.UNIQUE_ID)
            && !uniqueExtensionPointID.equals(ModelerCore.EXTENSION_POINT.FEATURE_CONSTRAINT_ASPECT.UNIQUE_ID)) {
            return false;
        }

        // Extract information from the extension ...
        final IConfigurationElement[] elems = extension.getConfigurationElements();
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            final String elemValue = elem.getValue();
            if (elemName.equals(ModelerCore.EXTENSION_POINT.METAMODEL_ASPECT.ELEMENTS.METAMODEL_REF_ID) && elemValue != null) {
                final String aspectMetamodelID = elemValue;
                // If this aspect refers to the same metamodel as this descriptor, return true
                if (aspectMetamodelID.equals(descriptor.getExtensionID())) {
                    if (ModelerCore.DEBUG_MODELER_CORE_INIT) {
                        final Object[] params = new Object[] {descriptor.getExtensionID(), uniqueExtensionPointID};
                        ModelerCore.Util.log(IStatus.INFO,
                                             ModelerCore.Util.getString("EclipseConfigurationBuilder.DEBUG.__Found_valid_MetamodelAspect_IExtension_for_the_metamodel.__The_extension_point_ID", params)); //$NON-NLS-1$
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sort ExternalResourceDescriptor instances
     */
    static class ExternalResourceDescriptorComparator implements Comparator {
        public int compare( Object obj1,
                            Object obj2 ) {
            if (obj1 == null && obj2 == null) {
                return 0;
            } else if (obj1 == null && obj2 != null) {
                return 1;
            } else if (obj1 != null && obj2 == null) {
                return -1;
            }
            ExternalResourceDescriptor descriptor1 = (ExternalResourceDescriptor)obj1;
            ExternalResourceDescriptor descriptor2 = (ExternalResourceDescriptor)obj2;
            int value1 = descriptor1.getPriority();
            int value2 = descriptor2.getPriority();
            if (value1 < value2) {
                return 1;
            } else if (value1 > value2) {
                return -1;
            }
            return 0;
        }
    }
}
