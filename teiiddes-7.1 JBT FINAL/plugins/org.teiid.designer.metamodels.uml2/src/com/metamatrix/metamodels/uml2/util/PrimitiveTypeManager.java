/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.util;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.LiteralBoolean;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralNull;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.ValueSpecification;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.ExternalResourceDescriptor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.internal.core.ExternalResourceDescriptorImpl;
import com.metamatrix.modeler.internal.core.ExternalResourceLoader;

/**
 * PrimitiveTypeManager is a singleton used for managing UML primitive types. A primitive type is a data type implemented by the
 * underlying infrastructure and made available for modeling.
 */
public class PrimitiveTypeManager {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(PrimitiveTypeManager.class);

    /** Defines the expected name of the primitive types model file */
    public static final String UML_PRIMITIVE_TYPES_MODEL_FILE_NAME = "primitiveTypes.xmi"; //$NON-NLS-1$

    /** Defines the expected name of theprimitive types archive file */
    public static final String UML_PRIMITIVE_TYPES_ZIP_FILE_NAME = "primitiveTypes.zip"; //$NON-NLS-1$

    /** Defines the expected primitive types internal URI */
    public static final String UML_PRIMITIVE_TYPES_INTERNAL_URI = "http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"; //$NON-NLS-1$

    /** Defines the URI for the primitive types model */
    public static final URI UML_PRIMITIVE_TYPES_URI = URI.createURI(UML_PRIMITIVE_TYPES_INTERNAL_URI);

    /** Defines the names of the predefined primitive types */
    public static final String INTEGER_PRIMITIVE_TYPE = "Integer"; //$NON-NLS-1$

    public static final String BOOLEAN_PRIMITIVE_TYPE = "Boolean"; //$NON-NLS-1$

    public static final String STRING_PRIMITIVE_TYPE = "String"; //$NON-NLS-1$

    public static final String UNLIMITED_NATURAL_PRIMITIVE_TYPE = "UnlimitedNatural"; //$NON-NLS-1$

    // The singleton instance must be defined physically below the UML_PRIMITIVE_TYPES_URI constants since that constant is used
    // in the initialization of the instance. If not, an exception will be thrown that aborts static class initialization, thus
    // preventing the URI constant from ever being initialized. Absolutely no feedback is given by the JDK when this happens
    // other than the fact that the constant's value will be null.
    public static final PrimitiveTypeManager INSTANCE = new PrimitiveTypeManager();

    private static final String MESSAGE_1 = getString("Error_retrieving_model_container_reference_1"); //$NON-NLS-1$

    private static final String MESSAGE_2 = getString("Error_retrieving_model_container_reference_2"); //$NON-NLS-1$

    private static final String MESSAGE_ID_1 = "Error_creating_container_for_the_resource_0_1"; //$NON-NLS-1$

    private static final String MESSAGE_ID_2 = "Error_retrieving_model_container_reference_0_2"; //$NON-NLS-1$

    private static final String MESSAGE_ID_3 = "Error_loading_external_resource_into_container_0_for_resource_1_3"; //$NON-NLS-1$

    private static final String MESSAGE_ID_4 = "Unable_to_create_an_absolute_path_to_the_resource_0_4"; //$NON-NLS-1$

    private static final String MESSAGE_ID_5 = "Error_creating_local_URL_for_0_5"; //$NON-NLS-1$

    private static final String MESSAGE_ID_6 = "Unable_to_create_an_absolute_path_to_the_data_directory_for_resource_0_6"; //$NON-NLS-1$

    private static final String MESSAGE_ID_7 = "Error_creating_the_absolute_path_to_the_data_directory_for_resource_0_7"; //$NON-NLS-1$

    /**
     * @since 4.1
     */
    private static String getString( final String id ) {
        return Uml2Plugin.Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.1
     */
    private static String getString( final String id,
                                     final Object parameter ) {
        return Uml2Plugin.Util.getString(I18N_PREFIX + id, parameter);
    }

    /**
     * @since 4.1
     */
    private static String getString( final String id,
                                     final Object parameter1,
                                     final Object parameter2 ) {
        return Uml2Plugin.Util.getString(I18N_PREFIX + id, parameter1, parameter2);
    }

    /** Reference to the EMF resource for the primitive types model */
    private Resource primitiveTypesResource;

    /** Map of primitive type name to PrimitiveType instance */
    private Map nameToType = new HashMap();

    private PrimitiveTypeManager() {
        init();
    }

    /**
     * @since 4.1
     */
    public ValueSpecification createValueSpecification( final PrimitiveType type,
                                                        final String value ) {
        CoreArgCheck.isNotNull(type);
        // If the value is "null" assume it is meant to represent null, i.e., the absence of a value.
        if (value == null || value.length() == 0 || value.equalsIgnoreCase("null")) { //$NON-NLS-1$
            LiteralNull valueSpec = UMLFactory.eINSTANCE.createLiteralNull();
            return valueSpec;
        }
        // The value is a specification of an integer value.
        else if (INTEGER_PRIMITIVE_TYPE.equals(type.getName())) {
            LiteralInteger valueSpec = UMLFactory.eINSTANCE.createLiteralInteger();
            valueSpec.setValue(Integer.parseInt(value));
            return valueSpec;
        }
        // The value is a specification of a boolean value.
        else if (BOOLEAN_PRIMITIVE_TYPE.equals(type.getName())) {
            LiteralBoolean valueSpec = UMLFactory.eINSTANCE.createLiteralBoolean();
            valueSpec.setValue(Boolean.valueOf(value).booleanValue());
            return valueSpec;
        }
        // The value is a specification of an unlimited natural value.
        else if (UNLIMITED_NATURAL_PRIMITIVE_TYPE.equals(type.getName())) {
            LiteralUnlimitedNatural valueSpec = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
            valueSpec.setValue(Integer.parseInt(value));
            return valueSpec;
        }
        // Else treat the value as a string value.
        else {
            LiteralString valueSpec = UMLFactory.eINSTANCE.createLiteralString();
            valueSpec.setValue(value);
            return valueSpec;
        }
    }

    /**
     * Return the {@link org.eclipse.uml2.PrimitiveType}instance with the specified case-insensitive name. Only the predefined
     * primitive types are available through this manager. This method will return null for any name that is not one of these
     * predefined types.
     * 
     * @param name
     * @return
     */
    public PrimitiveType getPrimitiveType( final String name ) {
        if (hasPrimitiveType(name)) {
            return (PrimitiveType)nameToType.get(name.toLowerCase());
        }
        return null;
    }

    /**
     * Return the collection of all {@link org.eclipse.uml2.PrimitiveType} instances known by this manager.
     * 
     * @param name
     * @return
     */
    public Collection getAllPrimitiveTypes() {
        return Collections.unmodifiableCollection(nameToType.values());
    }

    /**
     * Return true if a {@link org.eclipse.uml2.PrimitiveType}instance already exists with the specified name, otherwise return
     * false.
     * 
     * @param name
     * @return
     */
    public boolean hasPrimitiveType( final String name ) {
        CoreArgCheck.isNotZeroLength(name);
        return nameToType.get(name.toLowerCase()) != null;
    }

    /**
     * Return the corresponding XSD built-in datatype for the pre-defined primitive type, if one exists, else return null.
     * 
     * @param name
     * @return
     */
    public EObject getBuiltInTypeForPrimitiveType( final PrimitiveType type ) { // NO_UCD
        CoreArgCheck.isNotNull(type);
        final String typeName = type.getName();
        if (this.hasPrimitiveType(typeName)) {
            try {
                return ModelerCore.getDatatypeManager(type, true).getBuiltInDatatype(typeName);
            } catch (ModelerCoreException e) {
                Uml2Plugin.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
        return null;
    }

    protected void init() {
        // Create a descriptor defining the primitive types external resource
        final ExternalResourceDescriptor descriptor = getExternalResourceDescriptor();

        // // Create an empty container to hold the external resource
        // final Container container = createContainer(UML_PRIMITIVE_TYPES_CONTAINER_NAME);

        // Use the model container to hold the external resource
        final Container container = getModelContainer();

        // Load the external resource into the container
        if (container.getResource(UML_PRIMITIVE_TYPES_URI, false) != null) {
            this.primitiveTypesResource = container.getResource(UML_PRIMITIVE_TYPES_URI, false);
        } else {
            this.primitiveTypesResource = loadContainer(descriptor, container);
        }

        // Populate the HashMap with the primitive types found in the external resource
        if (this.primitiveTypesResource != null) {
            for (Iterator iter = this.primitiveTypesResource.getAllContents(); iter.hasNext();) {
                EObject eObject = (EObject)iter.next();
                if (eObject instanceof PrimitiveType) {
                    PrimitiveType pt = (PrimitiveType)eObject;
                    this.nameToType.put(pt.getName().toLowerCase(), pt);
                }
            }
        }
    }

    protected static Container getModelContainer() {
        Container container = null;
        try {
            container = ModelerCore.getModelContainer();
        } catch (Throwable t) {
            Uml2Plugin.Util.log(IStatus.ERROR, t, MESSAGE_1);
        }
        if (container == null) {
            Uml2Plugin.Util.log(IStatus.ERROR, MESSAGE_2);
        }
        return container;
    }

    protected static Container createContainer( final String containerName ) {
        Container container = null;
        try {
            container = ModelerCore.createEmptyContainer(containerName);
        } catch (Throwable t) {
            Uml2Plugin.Util.log(IStatus.ERROR, t, getString(MESSAGE_ID_1, UML_PRIMITIVE_TYPES_MODEL_FILE_NAME));
        }
        if (container == null) {
            Uml2Plugin.Util.log(IStatus.ERROR, getString(MESSAGE_ID_2, UML_PRIMITIVE_TYPES_MODEL_FILE_NAME));
        }
        return container;
    }

    protected static Resource loadContainer( final ExternalResourceDescriptor descriptor,
                                             final Container container ) {
        Resource resource = null;
        if (container != null) {
            final ExternalResourceLoader loader = new ExternalResourceLoader();
            try {
                resource = loader.load(descriptor, container);
            } catch (Throwable t) {
                Uml2Plugin.Util.log(IStatus.ERROR, t, getString(MESSAGE_ID_3, container.getName(), descriptor.getResourceName()));
            }
        }
        return resource;
    }

    protected static ExternalResourceDescriptor getExternalResourceDescriptor() {
        final ExternalResourceDescriptorImpl descriptor = new ExternalResourceDescriptorImpl();

        // Set the plugin and extension IDs
        descriptor.setPluginID(Uml2Plugin.PLUGIN_ID);
        descriptor.setExtensionID(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.ID);

        // Define the name of the resource to load, "primitiveTypes.xmi"
        descriptor.setResourceName(UML_PRIMITIVE_TYPES_MODEL_FILE_NAME);

        // Define the internal URI to use when retrieving this resource,
        // "http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"
        descriptor.setInternalUri(UML_PRIMITIVE_TYPES_INTERNAL_URI);

        // Define the resource location in terms of the declaring plugin location on the a file system
        String resourceURL = null;
        try {
            final URL installURL = FileLocator.resolve(Uml2Plugin.getDefault().getBundle().getEntry("/")); //$NON-NLS-1$
            resourceURL = FileLocator.toFileURL(new URL(installURL, UML_PRIMITIVE_TYPES_ZIP_FILE_NAME)).getFile();
            if (resourceURL == null || resourceURL.length() == 0) {
                Uml2Plugin.Util.log(IStatus.ERROR, getString(MESSAGE_ID_4, UML_PRIMITIVE_TYPES_ZIP_FILE_NAME));
            } else {
                descriptor.setResourceUrl(resourceURL);
            }
        } catch (Throwable t) {
            Uml2Plugin.Util.log(IStatus.ERROR, t, getString(MESSAGE_ID_5, UML_PRIMITIVE_TYPES_ZIP_FILE_NAME));
        }

        // Define the temporary working directory location in terms of the declaring plugin location on the a file system
        String tempDirPath = null;
        try {
            tempDirPath = Uml2Plugin.getDefault().getStateLocation().toOSString();
            if (tempDirPath == null || tempDirPath.length() == 0) {
                Uml2Plugin.Util.log(IStatus.ERROR, getString(MESSAGE_ID_6, descriptor.getResourceUrl()));
            } else {
                descriptor.setTempDirectoryPath(tempDirPath);
            }
        } catch (Throwable t) {
            Uml2Plugin.Util.log(IStatus.ERROR, t, getString(MESSAGE_ID_7, UML_PRIMITIVE_TYPES_ZIP_FILE_NAME));
        }

        return descriptor;
    }

}
