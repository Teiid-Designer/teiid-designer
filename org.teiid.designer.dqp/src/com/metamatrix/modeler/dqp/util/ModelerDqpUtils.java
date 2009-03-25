/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.api.ConnectorBindingType.Attributes;
import com.metamatrix.common.object.PropertyDefinition;
import com.metamatrix.common.object.PropertyType;
import com.metamatrix.common.util.crypto.CryptoException;
import com.metamatrix.common.util.crypto.CryptoUtil;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ModelSourceProperty;

/**
 * @since 4.3
 */
public final class ModelerDqpUtils {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ModelerDqpUtils.class);

    /**
     * Max length of a binding name.
     * 
     * @since 4.3
     */
    public static final int BINDING_NAME_MAX_LENGTH = 255;

    /**
     * {@link IStatus} code for an empty or <code>null</code> binding name.
     * 
     * @since 4.3
     */
    public static final int BINDING_NAME_EMPTY_ERROR = 100;

    /**
     * {@link IStatus} code for binding name with an invalid character.
     * 
     * @since 4.3
     */
    public static final int BINDING_NAME_INVALID_CHAR_ERROR = 200;

    /**
     * {@link IStatus} code for a binding name that is too long.
     * 
     * @since 4.3
     */
    public static final int BINDING_NAME_MAX_LENGTH_ERROR = 300;

    /**
     * {@link IStatus} code for a binding name that begins or ends with a space or has consecutive spaces.
     * 
     * @since 4.3
     */
    public static final int BINDING_NAME_WHITESPACE_ERROR = 400;

    private static final String CONNECTOR_CLASSPATH = "ConnectorClassPath"; //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Don't allow construction.
     */
    private ModelerDqpUtils() {
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param msgKey the properties file key
     * @param params the optional message data parameters
     * @return the error status object with the localized message
     * @since 6.0.0
     */
    public static IStatus createErrorStatus( String msgKey,
                                             String... params ) {
        String msg = (params == null) ? DqpPlugin.Util.getString(msgKey) : DqpPlugin.Util.getString(msgKey, (Object[])params);
        return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, msg);
    }

    /**
     * Obtains the connector type for the specified connector binding.
     * 
     * @param theBinding the non-<code>null</code> binding whose connector type is being requested
     * @return the binding or <code>null</code>
     * @since 4.3
     */
    public static ComponentType getConnectorType( ConnectorBinding theBinding ) {
        return DqpPlugin.getInstance().getConfigurationManager().getConnectorType(theBinding.getComponentTypeID());
    }

    /**
     * Obtains a connector binding name based on a <code>ModelInfo</code> object.
     * 
     * @param theModelInfo the model info object which will be used to construct a name
     * @return the name
     * @since 4.3
     */
    public static String createNewBindingName( ModelInfo theModelInfo ) {
        return createNewBindingName(theModelInfo.getName());
    }

    /**
     * Obtains a connector binding name based on a model name.
     * 
     * @param model the model name
     * @return the binding name
     * @since 5.5.3
     */
    public static String createNewBindingName( String name ) {
        return DqpPlugin.Util.getString(PREFIX + "newConnectorName", name); //$NON-NLS-1$
    }

    /**
     * Encrypts the value.
     * 
     * @param theValue the value being encrypted
     * @return the encrypted value
     * @since 5.0
     */
    public static String encryptValue( String theValue ) {
        String result = null;
        try {
            if ((theValue != null) && (theValue.length() > 0)) {
                result = CryptoUtil.getEncryptor().encrypt(theValue);
            }
        } catch (CryptoException theException) {
            DqpPlugin.Util.log(theException);
        }
        return result;
    }

    /**
     * Obtains all the {@link ConnectorBindingType}s loaded in the current configuration.
     * 
     * @return a map keyed by type name with a value of the type (never <code>null</code>)
     * @since 4.3
     */
    public static Map getConnectorTypes() {
        Map result = new HashMap();
        Collection types = DqpPlugin.getInstance().getConfigurationManager().getConnectorTypes();

        for (Iterator iter = types.iterator(); iter.hasNext();) {
            Object type = iter.next();
            result.put(((ConnectorBindingType)type).getID().getName(), type);
        }

        return result;
    }

    /**
     * Obtains the first <code>ConnectorBinding</code> assigned to the specified model using the specified VDB definition.
     * 
     * @param theModel the model whose first binding is being requested
     * @param theDefn the VDB definition used to find bindings
     * @return the binding or <code>null</code>
     * @throws IllegalArgumentException if an input parameter is <code>null</code>
     * @since 4.3
     */
    public static ConnectorBinding getFirstConnectorBinding( ModelInfo theModel,
                                                             VDBDefn theDefn ) {
        ArgCheck.isNotNull(theModel);
        ArgCheck.isNotNull(theDefn);

        ConnectorBinding result = null;

        if (theModel.requiresConnectorBinding()) {
            Collection bindingNames = theModel.getConnectorBindingNames();

            if ((bindingNames != null) && !bindingNames.isEmpty()) {
                String cbname = (String)bindingNames.iterator().next();

                ConnectorBinding binding = theDefn.getConnectorBindingByName(cbname);
                if (binding != null) {
                    result = binding;
                }
            }
        }

        return result;
    }

    /**
     * Obtains the <code>ModelSource</code> for the specified model using the given context.
     * 
     * @param theContext the context used to find the import source
     * @param theModel the model whose import source is being requested
     * @return the import source or <code>null</code> if not found
     * @throws IllegalArgumentException if any input parameter is <code>null</code>
     * @since 4.3
     */
    public static ModelSource getModelImportSource( VdbEditingContext theContext,
                                                    ModelInfo theModel ) {
        ArgCheck.isNotNull(theContext);
        ArgCheck.isNotNull(theModel);

        ModelSource result = null;
        List modelRefs = theContext.getVirtualDatabase().getModels();

        // no need to check if modelRefs is null as ELists are never null
        if (!modelRefs.isEmpty()) {
            for (int size = modelRefs.size(), i = 0; i < size; ++i) {
                ModelReference modelRef = (ModelReference)modelRefs.get(i);

                if (FileUtils.getFilenameWithoutExtension(modelRef.getName()).equals(theModel.getName())) {
                    result = modelRef.getModelSource();
                    break;
                }
            }
        }

        return result;
    }

    public static ModelSource getModelImportSource( VdbContextEditor theContext,
                                                    ModelInfo theModel ) {
        ArgCheck.isNotNull(theContext);
        ArgCheck.isNotNull(theModel);

        ModelSource result = null;
        List modelRefs = theContext.getVirtualDatabase().getModels();

        // no need to check if modelRefs is null as ELists are never null
        if (!modelRefs.isEmpty()) {
            for (int size = modelRefs.size(), i = 0; i < size; ++i) {
                ModelReference modelRef = (ModelReference)modelRefs.get(i);

                if (FileUtils.getFilenameWithoutExtension(modelRef.getName()).equals(theModel.getName())) {
                    result = modelRef.getModelSource();
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Get a <code>Map</code> of property name to values for JDBC connection properties stored on a model reference on the vdb
     * manifest model.
     * 
     * @param theModelRef the model reference whose connection properties are being requested
     * @return a map of JDBC connection properties (never <code>null</code> but maybe empty)
     * @since 5.0
     */
    public static Map getModelJdbcProperties( ModelReference theModelRef ) {
        Map result = null;
        ModelSource mdlSource = theModelRef.getModelSource();

        if ((theModelRef.getModelType() != ModelType.PHYSICAL_LITERAL) || (mdlSource == null)) {
            result = Collections.EMPTY_MAP;
        } else {
            result = new HashMap();
            Collection properties = mdlSource.getProperties();

            for (Iterator itr = properties.iterator(); itr.hasNext();) {
                ModelSourceProperty sourceProperty = (ModelSourceProperty)itr.next();
                String propertyName = sourceProperty.getName();
                String propertyValue = sourceProperty.getValue();

                if (!StringUtil.isEmpty(propertyName) && !StringUtil.isEmpty(propertyValue)) {
                    if (propertyName.equalsIgnoreCase(JDBCConnectionPropertyNames.JDBC_IMPORT_DRIVER_CLASS)) {
                        result.put(JDBCConnectionPropertyNames.JDBC_IMPORT_DRIVER_CLASS, propertyValue);
                    } else if (propertyName.equalsIgnoreCase(JDBCConnectionPropertyNames.JDBC_IMPORT_URL)) {
                        result.put(JDBCConnectionPropertyNames.JDBC_IMPORT_URL, propertyValue);
                    } else if (propertyName.equalsIgnoreCase(JDBCConnectionPropertyNames.JDBC_IMPORT_USERNAME)) {
                        result.put(JDBCConnectionPropertyNames.JDBC_IMPORT_USERNAME, propertyValue);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Obtains the {@link ComponentTypeDefn} with the specified name from the specified type.
     * 
     * @param theId the ID of the type
     * @param thePropertyName the property whose type definition is being requested
     * @return the type definitions or <code>null</code> if none exist
     * @since 5.0
     */
    public static ComponentTypeDefn getComponentTypeDefinition( ComponentTypeID theId,
                                                                String thePropertyName ) {
        ConfigurationManager configMgr = DqpPlugin.getInstance().getConfigurationManager();
        ConfigurationModelContainer container = configMgr.getDefaultConfig().getCMContainerImpl();
        return container.getComponentTypeDefinition(theId, thePropertyName);
    }

    /**
     * Get the collection of extension jar names that are required by the supplied connector (may not be <code>null</code>)
     * 
     * @param conn the connector whose required jar names are being requested
     * @return the required jar names (never <code>null</code>)
     * @since 6.0.0
     */
    public static Collection<String> getRequiredExtensionJarNames( ConnectorBinding conn ) {
        return getJarNames(conn.getProperty(CONNECTOR_CLASSPATH));
    }

    /**
     * Get the collection of extension jar names that are required by the supplied connector type
     * 
     * @param connectorBindingType the connector type whose required jar names are being requested (may not be <code>null</code>)
     * @return the required jar names  (never <code>null</code>)
     * @since 6.0.0
     */
    public static Collection<String> getRequiredExtensionJarNames( ComponentType connectorBindingType ) {
        return getJarNames(connectorBindingType.getDefaultValue(CONNECTOR_CLASSPATH));
    }
    
    private static Collection<String> getJarNames(String classPath) {
        Collection<String> jarNames = null;

        if (classPath == null) {
            jarNames = Collections.emptyList();
        } else {
            jarNames = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(classPath, ";"); //$NON-NLS-1$

            while (st.hasMoreTokens()) {
                String path = st.nextToken();
                int idx = path.indexOf(Attributes.MM_JAR_PROTOCOL);

                if (idx != -1) {
                    String jarFile = path.substring(idx + Attributes.MM_JAR_PROTOCOL.length() + 1);
                    jarNames.add(jarFile);
                }
            }
        }

        return jarNames;
    }

    /**
     * Indicates if the specified name would be a unique name in the current configuration. Another method is provided to check
     * uniqueness in both the configuration and in the VDB.
     * 
     * @param theProposedName the proposed new binding name
     * @return <code>true</code> if unique; <code>false</code> otherwise.
     * @since 4.3
     * @see #isUniqueBindingName(String, VDBDefn)
     */
    public static boolean isUniqueBindingName( String theProposedName ) {
        return (DqpPlugin.getInstance().getConfigurationManager().getBinding(theProposedName) == null);
    }

    /**
     * Indicates if the specified name would be a unique name in the current configuration and in the bindings established in the
     * <code>VDBDefn</code>.
     * 
     * @param theProposedName the proposed new binding name
     * @return <code>true</code> if unique; <code>false</code> otherwise.
     * @since 4.3
     */
    public static boolean isUniqueBindingName( String theProposedName,
                                               VDBDefn theDefn ) {
        return (isUniqueBindingName(theProposedName) && (theDefn.getConnectorBindingByName(theProposedName) == null));
    }

    /**
     * Indicates if the specified name is of proper length and is composed of valid characters for a connector binding name.
     * 
     * @param theProposedName the name being validated
     * @return the validation status
     * @since 4.3
     */
    public static IStatus isValidBindingName( String theProposedName ) {
        int severity = IStatus.OK;
        int code = IStatus.OK;
        String msg = ""; //$NON-NLS-1$

        if ((theProposedName == null) || (theProposedName.length() == 0)) {
            // binding name can't be empty or null
            severity = IStatus.ERROR;
            code = BINDING_NAME_EMPTY_ERROR;
            msg = DqpPlugin.Util.getStringOrKey(PREFIX + "bindingNameEmptyError"); //$NON-NLS-1$
        } else if (theProposedName.startsWith(" ") || theProposedName.endsWith(" ") || (theProposedName.indexOf("  ") != -1)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            severity = IStatus.ERROR;
            code = BINDING_NAME_WHITESPACE_ERROR;
            msg = DqpPlugin.Util.getStringOrKey(PREFIX + "bindingNameWhitespaceError"); //$NON-NLS-1$
        } else {
            int length = theProposedName.length();

            if (length <= BINDING_NAME_MAX_LENGTH) {
                // make sure all characters are valid
                for (int i = 0; i < length; ++i) {
                    char c = theProposedName.charAt(i);

                    if (!Character.isLetterOrDigit(c) && !Character.isSpaceChar(c) && (c != '_')) {
                        severity = IStatus.ERROR;
                        code = BINDING_NAME_INVALID_CHAR_ERROR;
                        msg = DqpPlugin.Util.getString(PREFIX + "bindingNameInvalidCharError", //$NON-NLS-1$
                                                       new Object[] {Character.toString(c), Integer.toString(i + 1)});
                        break;
                    }
                }
            } else {
                // binding name too long
                severity = IStatus.ERROR;
                code = BINDING_NAME_MAX_LENGTH_ERROR;
                msg = DqpPlugin.Util.getString(PREFIX + "bindingNameMaxLengthError", BINDING_NAME_MAX_LENGTH); //$NON-NLS-1$
            }
        }

        return new Status(severity, DqpPlugin.PLUGIN_ID, code, msg, null);
    }

    /**
     * Indicates if the specified {@link ConnectorBindingType} is a valid connector type for the Modeler DQP.
     * 
     * @param theConnectorType the connector type being checked
     * @return <code>true</code> if valid; <code>false</code> otherwise.
     * @throws NullPointerException if theConnectorType is <code>null</code>
     * @since 4.3
     */
    public static boolean isValidConnectorType( ConnectorBindingType theConnectorType ) {
        boolean result = false;

        // need to filter out:
        // (1) not deployable types
        // (2) types whose parents are not in the connector product type (this filters out DesignTimeCatalog)
        if (theConnectorType.isDeployable()
            && (theConnectorType.getParentComponentTypeID().getName().equals(ConnectorBindingType.CONNECTOR_PROD_TYPEID.getName()))) {
            result = true;
        }

        return result;
    }

    public static boolean setConnectorBindingPassword( ConnectorBinding theBinding,
                                                       String thePassword ) throws Exception {
        boolean result = false;
        Collection defns = getConnectorType(theBinding).getComponentTypeDefinitions();
        Object propId = null;

        if ((defns != null) && !defns.isEmpty()) {
            Iterator itr = defns.iterator();

            while (itr.hasNext()) {
                ComponentTypeDefn defn = (ComponentTypeDefn)itr.next();

                if (defn.getPropertyDefinition().isMasked()) {
                    propId = defn.getPropertyDefinition().getName();
                    break;
                }
            }
        }

        if (propId != null) {
            result = setPropertyValue(theBinding, propId, thePassword);
        }

        return result;
    }

    /**
     * Sets a connector binding property.
     * 
     * @param theBinding the binding whose property is being set.
     * @param thePropertyId the property ID
     * @param theNewValue the new property value
     * @return <code>true</code> if the property was set; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean setPropertyValue( ConnectorBinding theBinding,
                                            Object thePropertyId,
                                            Object theNewValue ) throws Exception {
        boolean result = false;
        ComponentType type = getConnectorType(theBinding);

        if (type != null) {
            Assertion.isInstanceOf(thePropertyId, String.class, thePropertyId.getClass().getName());
            Assertion.isInstanceOf(theNewValue, String.class, theNewValue.getClass().getName());
            Assertion.isInstanceOf(type.getID(), ComponentTypeID.class, type.getID().getClass().getName());

            result = true;
            String value = (String)theNewValue;
            ComponentTypeDefn typeDefn = type.getComponentTypeDefinition((String)thePropertyId);

            // if not found search parents for it
            if (typeDefn == null) {
                typeDefn = getComponentTypeDefinition((ComponentTypeID)type.getID(), (String)thePropertyId);
            }

            if (typeDefn == null) {
                result = false;
            } else {
                PropertyDefinition propDefn = typeDefn.getPropertyDefinition();
                PropertyType propType = typeDefn.getPropertyType();

                // validate proposed new value
                if (propDefn.isMasked()) {
                    value = ModelerDqpUtils.encryptValue(value);

                    if (value == null) {
                        value = ""; //$NON-NLS-1$
                    }
                } else {
                    result = propType.isValidValue(value);

                    if (propDefn.hasAllowedValues() && propDefn.isConstrainedToAllowedValues()) {
                        List values = propDefn.getAllowedValues();

                        if (values != null && !values.isEmpty()) {
                            result = false;
                            for (int size = values.size(), i = 0; i < size; ++i) {
                                if (value.equals(values.get(i))) {
                                    result = true;
                                    break;
                                }
                            }
                        } else {
                            // if there are no allowed values, this is an illegal state for the property defn
                            // but we need to allow the user to continue - just log it.
                            Object[] msgArray = new Object[] {propDefn.getName(), type.getName()};
                            DqpPlugin.Util.log(IStatus.WARNING,
                                               DqpPlugin.Util.getString("ModelerDqpUtils.noAllowedValuesWarning", //$NON-NLS-1$
                                                                        msgArray));
                            result = true;
                        }
                    }
                }
            }

            // if proposed value is valid set the property
            if (result) {
                DqpPlugin.getInstance().getConfigurationManager().setConnectorPropertyValue(theBinding, (String)thePropertyId, value);
            }
        }

        return result;
    }
}
