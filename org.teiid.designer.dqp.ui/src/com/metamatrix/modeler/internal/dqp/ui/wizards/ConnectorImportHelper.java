/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ServerAdmin;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

/**
 * Business Object which is used by the connector import wizard in Designer. Currently, the user can import from either a .cdk or
 * .caf file. When the import file is set via the 'setConnectorFile' method, all available data is imported from it (connector
 * types, connectors and extension jars).
 */
public class ConnectorImportHelper implements DqpUiConstants {

    /**
     * IStatus codes used for imported types and bindings.
     * 
     * @since 5.5.3
     */
    private interface StatusCodes {
        /**
         * Indicates the imported connector type or connector is new and does not currently exist.
         * 
         * @since 5.5.3
         */
        int NEW = 100;

        /**
         * Indicates the imported connector type or connector will replace the same-named existing connector type or binding in
         * the configuration.
         * 
         * @since 5.5.3
         */
        int UPDATE = 101;
    }

    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ConnectorImportHelper.class);

    // Import Selection Options
    private static final int SELECTED_FOR_IMPORT = 0;
    private static final int NOT_SELECTED_FOR_IMPORT = 1;

    // Connector File type Options
    public static final int UNKNOWN_FILE = -1;
    public static final int CDK_FILE = 0;
    public static final int CAF_FILE = 1;

    // Constant for designation of "Use existing Jar"
    public static final String USE_WSCONFIG_JAR = "<useExisting>"; //$NON-NLS-1$
    public static final String USE_CAF_JAR = "<use caf jar>"; //$NON-NLS-1$

    private File connectorFile;
    private int connectorFileType = UNKNOWN_FILE;

    private boolean initDefaultSelections = true;

    // Collections of import file connector and connector types
    private Collection<ConnectorType> allImportFileConnectorTypes;
    private Collection<ConnectorBinding> allImportFileConnectors;
    private Collection<String> requiredExtJars;

    // Map of Connector and Connector Types to integer status
    private Map<ConnectorType, Integer> allImportFileConnTypeAndStatusMap;
    private Map<ConnectorBinding, Integer> allImportFileConnAndStatusMap;

    // Map of Standard workspace Connector Type name to connector type
    private Map<String, ConnectorType> workspaceConfigStandardTypeMap;

    /**
     * Helper method to get string from the i18n.properties file
     * 
     * @param id the i18n string key
     * @return the associated string
     * @since 5.5.3
     */
    static String getString( final String id ) {
        return UTIL.getStringOrKey(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object param1,
                                     final Object param2 ) {
        return UTIL.getString(I18N_PREFIX + id, param1, param2);
    }

    /**
     * Constructor
     * 
     * @since 5.5.3
     */
    public ConnectorImportHelper() {
        // Init empty collections on construction
        this.allImportFileConnectorTypes = Collections.emptyList();
        this.allImportFileConnectors = Collections.emptyList();
        this.requiredExtJars = Collections.emptyList();

        this.allImportFileConnTypeAndStatusMap = Collections.emptyMap();
        this.allImportFileConnAndStatusMap = Collections.emptyMap();

        this.workspaceConfigStandardTypeMap = Collections.emptyMap();
    }

    /**
     * Test whether the supplied connector type already exists in the workspace config. This is a simple name match. The status
     * will go further to check compatibility.
     * 
     * @param connType
     * @return <code>true</code> if a matching connector type already exists in the configuration
     * @since 5.5.3
     */
    public boolean existsInWorkspaceConfig( ConnectorType connType ) {
        boolean existsInConfig = false;
        Collection<ConnectorType> workspaceTypes = getAllWorkspaceConfigConnectorTypes();
        for (Iterator<ConnectorType> wIter = workspaceTypes.iterator(); wIter.hasNext();) {
            ConnectorType wType = wIter.next();
            if (connType.getFullName().equalsIgnoreCase(wType.getFullName())) {
                existsInConfig = true;
                break;
            }
        }
        return existsInConfig;
    }

    /**
     * Test whether the supplied connector already exists in the workspace config
     * 
     * @param connector
     * @return <code>true</code> if a matching connector already exists in the configuration
     * @since 5.5.3
     */
    public boolean existsInWorkspaceConfig( ConnectorBinding connector ) {
        boolean existsInConfig = false;
        Collection<ConnectorBinding> workspaceConns = getAllWorkspaceConfigConnectors();
        for (Iterator<ConnectorBinding> wIter = workspaceConns.iterator(); wIter.hasNext();) {
            ConnectorBinding wConn = wIter.next();
            if (connector.getName().equalsIgnoreCase(wConn.getName())) {
                existsInConfig = true;
                break;
            }
        }
        return existsInConfig;
    }

    /**
     * Get the list of all required extension jar names
     * 
     * @return all required jar names for the connector types and connectors (no duplicates); never null.
     * @since 5.5.3
     */
    public Collection<String> getAllRequiredExtensionJarNames() {
        return this.requiredExtJars;
    }

    /**
     * Get the list of all required extension jar names that are not yet mapped.
     * 
     * @return all required but unmapped jar names for the connector types and connectors (no duplicates); never null.
     * @since 5.5.3
     */
    public Collection<String> getUnmappedExtensionJarNames() {
        return this.extJarHelper.getUnmappedRequiredExtensionJarNames();
    }

    /**
     * Get the list of all connector types that require the supplied jarName
     * 
     * @param extJarName the extension jar name
     * @limitToSelected if 'true' only the types that are selected for import are considered.
     * @return all connector types that require the supplied jarName; never null.
     * @since 5.5.3
     */
    public Collection<ConnectorType> getConnectorTypesRequiringExtensionJarName( String extJarName,
                                                                                        boolean limitToSelected ) {
        Collection<ConnectorType> connectorTypes = new ArrayList<ConnectorType>();

        // Iterate the connector types
        for (Iterator<ConnectorType> ctIter = this.allImportFileConnectorTypes.iterator(); ctIter.hasNext();) {
            ConnectorType cType = ctIter.next();
            Collection<String> rqdJars = ModelerDqpUtils.getRequiredExtensionJarNames(cType);
            // Check if supplied jarName is in the required list
            if (rqdJars.contains(extJarName)) {
                if (limitToSelected && isSelectedForImport(cType)) {
                    connectorTypes.add(cType);
                } else if (!limitToSelected) {
                    connectorTypes.add(cType);
                }
            }

        }
        return connectorTypes;
    }

    /**
     * Get the list of all connectors that require the supplied jarName
     * 
     * @param extJarName the extension jar name
     * @limitToSelected if 'true' only the connectors that are selected for import are considered.
     * @return all connectors that require the supplied jarName; never null.
     * @since 5.5.3
     */
    public Collection<ConnectorBinding> getConnectorsRequiringExtensionJarName( String extJarName,
                                                                                boolean limitToSelected ) {
        Collection<ConnectorBinding> connectors = new ArrayList<ConnectorBinding>();

        // Iterate the connectors
        for (Iterator<ConnectorBinding> cIter = this.allImportFileConnectors.iterator(); cIter.hasNext();) {
            ConnectorBinding conn = cIter.next();
            Collection<String> rqdJars = ModelerDqpUtils.getRequiredExtensionJarNames(conn);
            // Check if supplied jarName is in the required list
            if (rqdJars.contains(extJarName)) {
                if (limitToSelected && isSelectedForImport(conn)) {
                    connectors.add(conn);
                } else if (!limitToSelected) {
                    connectors.add(conn);
                }
            }

        }
        return connectors;
    }

    /**
     * Get the collection of all connector types in the import file
     * 
     * @return the connector types available to be imported from the connector file; never null.
     * @since 5.5.3
     */
    public Collection<ConnectorType> getAllImportFileConnectorTypes() {
        if (this.allImportFileConnectorTypes != null) {
            return this.allImportFileConnectorTypes;
        }

        return Collections.emptySet();
    }

    /**
     * Get the collection of all connector types in the import file that are non-standard. The type check is a simple name match.
     * Cannot overwrite standard types.
     * 
     * @return the non-standard connector types available to be imported from the connector file; never null.
     * @since 5.5.3
     */
    public Collection<ConnectorType> getAllNonStandardImportFileConnectorTypes() {
        Collection<ConnectorType> nonStandardImportTypes = new ArrayList<ConnectorType>();
        Set<String> wsStandardBindingNames = this.workspaceConfigStandardTypeMap.keySet();
        if (this.allImportFileConnectorTypes != null) {
            for (Iterator<ConnectorType> iter = this.allImportFileConnectorTypes.iterator(); iter.hasNext();) {
                ConnectorType connType = iter.next();
                // Check import type name vs list of standard ws bindings
                String importTypeName = connType.getFullName();
                if (!wsStandardBindingNames.contains(importTypeName)) {
                    nonStandardImportTypes.add(connType);
                }
            }
        }

        return nonStandardImportTypes;
    }

    /**
     * Get the collection of all connectors in the import file
     * 
     * @return the connectors available to be imported from the connector file; never null.
     * @since 5.5.3
     */
    public Collection<ConnectorBinding> getAllImportFileConnectors() {
        if (this.allImportFileConnectors != null) {
            return this.allImportFileConnectors;
        }

        return Collections.emptySet();
    }

    /**
     * Get the collection of connector types from the import file that have been selected for import.
     * 
     * @return the connector types available from the connector file and selected for import; never null.
     * @since 5.5.3
     */
    public Collection<ConnectorType> getSelectedImportFileConnectorTypes() {
        Collection<ConnectorType> selectedTypes = new ArrayList<ConnectorType>();
        for (Iterator<ConnectorType> keyIter = allImportFileConnTypeAndStatusMap.keySet().iterator(); keyIter.hasNext();) {
            ConnectorType connType = keyIter.next();
            int connTypeStatus = allImportFileConnTypeAndStatusMap.get(connType).intValue();

            if (connTypeStatus == SELECTED_FOR_IMPORT) {
                selectedTypes.add(connType);
            }
        }

        return selectedTypes;
    }

    /**
     * Get the collection of connectors from the import file that have been selected for import.
     * 
     * @return the components available from the connector file and selected for import; never null.
     * @since 5.5.3
     */
    public Collection<ConnectorBinding> getSelectedImportFileConnectors() {
        Collection<ConnectorBinding> selectedConnectors = new ArrayList<ConnectorBinding>();
        for (Iterator<ConnectorBinding> keyIter = allImportFileConnAndStatusMap.keySet().iterator(); keyIter.hasNext();) {
            ConnectorBinding connector = keyIter.next();
            int connStatus = allImportFileConnAndStatusMap.get(connector).intValue();

            if (connStatus == SELECTED_FOR_IMPORT) {
                selectedConnectors.add(connector);
            }
        }

        return selectedConnectors;
    }

    /**
     * Get the collection of connectors from the import file whose type is selected for import.
     * 
     * @return the connectors available to be imported based on all the component types being imported; never null.
     * @since 5.5.3
     */
    public Collection<ConnectorBinding> getImportFileConnectorsForSelectedTypes() {
        Collection<ConnectorBinding> availableConnectors = new ArrayList<ConnectorBinding>();

        // Get the selected Connector Types
        Collection<ConnectorType> selectedTypes = getSelectedImportFileConnectorTypes();

        // Iterate the list of all connectors. Include only those that have one of the selected types.
        for (Iterator<ConnectorBinding> cIter = this.allImportFileConnectors.iterator(); cIter.hasNext();) {
            ConnectorBinding conn = cIter.next();
            if (isConnectorTypeMatch(conn, selectedTypes)) {
                availableConnectors.add(conn);
            }
        }
        return availableConnectors;
    }

    /**
     * Determine if the supplied connector has a type which matches any of the types in the supplied type list.
     * 
     * @param conn the connector whose type to check
     * @param connTypes the type list to check the connector type against.
     * @return <code>true</code> if the connector type matches any of the supplied types
     * @since 5.5.3
     */
    private boolean isConnectorTypeMatch( ConnectorBinding conn,
                                          Collection<ConnectorType> connTypes ) {
        boolean isTypeMatch = false;

        // connector type name
        String connTypeName = conn.getComponentTypeID().getName();

        // Iterate thru the supplied connector types. If the type of the supplied connector
        // matches any of the types in the list, it is a match
        for (Iterator<ConnectorType> ctIter = connTypes.iterator(); ctIter.hasNext();) {
            ConnectorType cType = ctIter.next();
            if (cType.getName().equals(connTypeName)) {
                isTypeMatch = true;
                break;
            }
        }
        return isTypeMatch;
    }

    /**
     * Get the status of the provided connector type
     * 
     * @param connType the connector type whose import status is being requested
     * @return the import status of the specified connector type; never null.
     * @since 5.5.3
     */
    public IStatus getStatus( ConnectorType connType ) {

        String typeName = connType.getFullName();
        // Make sure connType is in the map. If not found, this is an error.
        if (!this.allImportFileConnTypeAndStatusMap.containsKey(connType)) {
            String msg = getString("connTypeNotFoundInFile.msg", typeName, typeName); //$NON-NLS-1$
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR, msg, null);
        }

        // Handle the unselected types
        if (!isSelectedForImport(connType)) {
            // Incoming type name matches workspace standard type.
            if (nameMatchesWorkspaceStandardType(typeName)) {
                String msg = getString("connTypeNotSelectedForImportMatchesWSStandard.msg"); //$NON-NLS-1$
                return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
            }
            // Name does not match any in the workspace - always OK to import
            if (!nameMatchesWorkspaceType(typeName)) {
                String msg = getString("connTypeNotSelectedForImport.msg", typeName, typeName); //$NON-NLS-1$
                return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
            }

            // Import type will replace workspace type.
            String msg = getString("connTypeNotSelectedForImportWillReplaceWSType.msg", typeName, typeName); //$NON-NLS-1$
            return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
        }

        // Handle the selected types
        if (existsInWorkspaceConfig(connType)) {
            // Incoming type name matches workspace standard type.
            if (nameMatchesWorkspaceStandardType(typeName)) {
                String msg = getString("connTypeSelectedForImportMatchesWSStandard.msg", typeName, typeName); //$NON-NLS-1$
                return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR, msg, null);
            }

            // Import type is update of workspace type.
            String msg = getString("connTypeSelectedForImportWillReplaceWSType.msg", typeName, typeName); //$NON-NLS-1$
            return new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, StatusCodes.UPDATE, msg, null);
        }
        String msg = getString("connTypeSelectedForImport.msg", typeName, typeName); //$NON-NLS-1$
        return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, StatusCodes.NEW, msg, null);
    }

    /**
     * Get the status of the provided connector
     * 
     * @param connector the connector whose import status is being requested
     * @return the import status of the specified connector
     * @since 5.5.3
     */
    public IStatus getStatus( ConnectorBinding connector ) {

        String connName = connector.getFullName();
        // Make sure connector is in the map
        if (!this.allImportFileConnAndStatusMap.containsKey(connector)) {
            String msg = getString("connNotFoundInFile.msg", connName, connName); //$NON-NLS-1$
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR, msg, null);
        }

        // Handle status for the unselected connectors
        if (!isSelectedForImport(connector)) {
            // Name does not match any in the workspace - always OK to import
            if (!nameMatchesWorkspaceConn(connName)) {
                String msg = getString("connNotSelectedForImport.msg", connName, connName); //$NON-NLS-1$
                return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
            }

            // Import connector will replace workspace connector when selected
            String msg = getString("connNotSelectedForImportWillReplaceWSConn.msg", connName, connName); //$NON-NLS-1$
            return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
        }

        // Handle Status for selected connectors

        // Name does not match any in the workspace - always OK to import
        if (!nameMatchesWorkspaceConn(connName)) {
            String msg = getString("connSelectedForImport.msg", connName, connName); //$NON-NLS-1$
            return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, StatusCodes.NEW, msg, null);
        }

        // Import type will replace existing workspace connector.
        ConnectorType importType = getImportFileType(connector.getComponentTypeID().getFullName());
        String msg = null;

        if (isSelectedForImport(importType)) {
            msg = getString("connSelectedForImportWillReplaceWSConnTypeImported.msg", connName, connName); //$NON-NLS-1$
        } else {
            msg = getString("connSelectedForImportWillReplaceWSConnTypeNotImported.msg", connName, connName); //$NON-NLS-1$
        }

        return new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, StatusCodes.UPDATE, msg, null);
    }

    /**
     * @param typeName the name of the type from the import file being requested
     * @return the type (never <code>null</code>)
     */
    private ConnectorType getImportFileType( String typeName ) {
        ConnectorType result = null;

        for (ConnectorType type : this.allImportFileConnectorTypes) {
            if (type.getFullName().equals(typeName)) {
                result = type;
                break;
            }
        }

        // should never happen since binding types are always included in import file
        assert (result != null);
        return result;
    }

    /**
     * Get a collection of IStatus objects for all of the Connector Types selected for import.
     * 
     * @return the collection of IStatus objects; never null
     * @since 5.5.3
     */
    public Collection<IStatus> getAllSelectedConnectorTypeStatus() {
        Collection<ConnectorType> selectedImportTypes = getSelectedImportFileConnectorTypes();
        Collection<IStatus> allStatus = new ArrayList<IStatus>(selectedImportTypes.size());

        for (Iterator<ConnectorType> iter = selectedImportTypes.iterator(); iter.hasNext();) {
            ConnectorType type = iter.next();
            allStatus.add(getStatus(type));
        }

        return allStatus;
    }

    /**
     * Get a collection of IStatus objects for all of the Connectors selected for import.
     * 
     * @return the collection of IStatus objects; never null
     * @since 5.5.3
     */
    public Collection<IStatus> getAllSelectedConnectorStatus() {
        Collection<ConnectorBinding> selectedImportConns = getSelectedImportFileConnectors();
        Collection<IStatus> allStatus = new ArrayList<IStatus>(selectedImportConns.size());

        for (Iterator<ConnectorBinding> iter = selectedImportConns.iterator(); iter.hasNext();) {
            ConnectorBinding conn = iter.next();
            allStatus.add(getStatus(conn));
        }

        return allStatus;
    }

    /**
     * Get the Extension jar path for the supplied connector. The path returned is a semi-colon separated list of the jar names.
     * 
     * @param connector the connector
     * @return the extension jar path for the connector
     * @since 5.5.3
     */
    public String getExtensionJarPath( ConnectorBinding connector ) {
        StringBuffer sb = new StringBuffer();
        Collection<String> extJars = ModelerDqpUtils.getRequiredExtensionJarNames(connector);
        Iterator<String> iter = extJars.iterator();
        while (iter.hasNext()) {
            String jarName = iter.next();
            sb.append(jarName);
            if (iter.hasNext()) {
                sb.append(";"); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }

    /**
     * Get the Extension jar path for the supplied connector type. The path returned is a semi-colon separated list of the jar
     * names.
     * 
     * @param connType the connector type
     * @return the extension jar path for the connector type
     * @since 5.5.3
     */
    public String getExtensionJarPath( ConnectorType connType ) {
        StringBuffer sb = new StringBuffer();
        Iterator<String> iter = ModelerDqpUtils.getRequiredExtensionJarNames(connType).iterator();
        while (iter.hasNext()) {
            String jarName = iter.next();
            sb.append(jarName);
            if (iter.hasNext()) {
                sb.append(';');
            }
        }
        return sb.toString();
    }

    /**
     * Get the required extension jar status
     * 
     * @param jarName the name of the jar whose import status is being requested
     * @return the import status of the specified jar file
     * @since 5.5.3
     */
    public IStatus getRequiredExtensionJarStatus( String jarName ) {
        return this.extJarHelper.getRequiredExtensionJarStatus(jarName);
    }

    /**
     * Get a collection of IStatus for all the required extension jars
     * 
     * @return the collection of IStatus of the of all required jars
     * @since 5.5.3
     */
    public Collection<IStatus> getAllRequiredExtensionJarStatus() {
        return this.extJarHelper.getAllRequiredExtensionJarStatus();
    }

    /**
     * @return the overall status of the import. This is a MultiStatus which is made up of status of all connector types and
     *         connector selected for import, and all required jar statuses.
     * @return the overall status for the import.
     * @since 5.5.3
     */
    public IStatus getImportStatus() {
        Collection<IStatus> typeStatusList = this.getAllSelectedConnectorTypeStatus();
        Collection<IStatus> connStatusList = this.getAllSelectedConnectorStatus();
        Collection<IStatus> jarStatusList = this.getAllRequiredExtensionJarStatus();

        Collection<IStatus> totalList = new ArrayList<IStatus>(typeStatusList.size() + connStatusList.size()
                                                               + jarStatusList.size());
        totalList.addAll(typeStatusList);
        totalList.addAll(connStatusList);
        totalList.addAll(jarStatusList);

        // If there is at least one status, return multi-status
        if (!totalList.isEmpty()) {
            return new MultiStatus(DqpUiConstants.PLUGIN_ID, 0, totalList.toArray(new IStatus[totalList.size()]),
                                   getString("connectorImportStatus.msg"), null); //$NON-NLS-1$
        }

        // No status objects means that nothing has been selected for import
        return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR,
                          getString("connectorImportStatusError.msg"), null); //$NON-NLS-1$
    }

    /**
     * Check whether the supplied Connector Type is selected for import.
     * 
     * @param connType the connector type being checked
     * @return <code>true</code> if the connector type is selected for import
     * @since 5.5.3
     */
    public boolean isSelectedForImport( ConnectorType connType ) {
        if (!this.allImportFileConnTypeAndStatusMap.containsKey(connType)) {
            return false;
        }
        int importStatus = this.allImportFileConnTypeAndStatusMap.get(connType).intValue();
        if (importStatus == SELECTED_FOR_IMPORT) {
            return true;
        }
        return false;
    }

    /**
     * Check whether the supplied Connector Type is a standard type.
     * 
     * @param connType the connector type being checked
     * @return <code>true</code> if the connector type is a standard type.
     * @since 5.5.3
     */
    public boolean isStandardType( ConnectorType connType ) {
        if (!this.allImportFileConnTypeAndStatusMap.containsKey(connType)) {
            return false;
        }
        return getAdmin().isStandardComponentType(connType);
    }

    /**
     * Check whether the supplied Connector is selected for import.
     * 
     * @param conn the connector type being checked
     * @return <code>true</code> if the connector is selected for import
     * @since 5.5.3
     */
    public boolean isSelectedForImport( ConnectorBinding conn ) {
        if (!this.allImportFileConnAndStatusMap.containsKey(conn)) {
            return false;
        }
        int importStatus = this.allImportFileConnAndStatusMap.get(conn).intValue();
        if (importStatus == SELECTED_FOR_IMPORT) {
            return true;
        }
        return false;
    }

    /**
     * Check whether the supplied jarName already exists in the list of workspace extension jars.
     * 
     * @param jarName the name of the extension jar being checked
     * @return <code>true</code> if an extension jar with the specified name already exists in the current configuration
     * @since 5.5.3
     */
    public boolean jarExistsInConfiguration( String jarName ) {
        return this.extJarHelper.jarExistsInConfiguration(jarName);
    }

    /**
     * Check whether overwrite of the existing config jar is allowed
     * 
     * @param jarName the name of the jar
     * @return <code>true</code> if overwrite of the configuration jar is allowed.
     * @since 5.5.3
     */
    public boolean canOverwriteWorkspaceConfigJar( String jarName ) {
        return this.extJarHelper.canOverwriteWorkspaceConfigJar(jarName);
    }

    /**
     * Check whether overwrite of the existing config jars is allowed
     * 
     * @return <code>true</code> if overwrite of the configuration jars is allowed.
     * @since 5.5.3
     */
    public boolean canOverwriteWorkspaceConfigJars() {
        return this.extJarHelper.canOverwriteWorkspaceConfigJars();
    }

    /**
     * Check whether the supplied jarName is available with the import.
     * 
     * @param jarName the name of the extension jar being checked
     * @return <code>true</code> if an extension jar with the specified name is available with the importFile selection.
     * @since 5.5.3
     */
    public boolean jarExistsInImport( String jarName ) {
        return this.extJarHelper.jarExistsInImport(jarName);
    }

    /**
     * Get the CAF jar from the import. If not found or this is not a CAF import, null will be returned;
     * 
     * @param jarName the name of the extension jar
     * @return the CAF jar ExtensionModule, or null if not found or not caf import.
     * @since 5.5.3
     */
    public ExtensionModule getCAFJarFromImport( String jarName ) {
        return this.extJarHelper.getCAFJarFromImport(jarName);
    }

    /**
     * Get the jar File from the import. If not found or this is not a CDK import, null will be returned;
     * 
     * @param jarName the name of the extension jar
     * @return the jar File, or null if not found or not cdk import.
     * @since 5.5.3
     */
    public File getCDKJarFromImport( String jarName ) {
        return this.extJarHelper.getCDKJarFromImport(jarName);
    }

    /**
     * Change the import status for the supplied ConnectorType
     * 
     * @param connectorType the connector type
     * @param shouldImport a flag indicating if the specified connector type should be imported
     * @since 5.5.3
     */
    public void setImportStatus( ConnectorType connectorType,
                                 boolean shouldImport ) {
        if (!this.allImportFileConnTypeAndStatusMap.containsKey(connectorType)) {
            throw new IllegalArgumentException();
        }
        int importStatus = NOT_SELECTED_FOR_IMPORT;
        if (shouldImport) {
            importStatus = SELECTED_FOR_IMPORT;
        }
        this.allImportFileConnTypeAndStatusMap.put(connectorType, new Integer(importStatus));

        // If a type has been deselected, must also deselect any imported types (unless the type
        // already exists in the workspace).
        if (!shouldImport && !existsInWorkspaceConfig(connectorType)) {
            for (Iterator<ConnectorBinding> iter = this.allImportFileConnAndStatusMap.keySet().iterator(); iter.hasNext();) {
                ConnectorBinding conn = iter.next();
                ComponentTypeID compType = conn.getComponentTypeID();
                if (compType.getFullName().equalsIgnoreCase(connectorType.getFullName())) {
                    this.allImportFileConnAndStatusMap.put(conn, new Integer(NOT_SELECTED_FOR_IMPORT));
                }
            }
        }
        // Update the required ext jars
        updateRequiredExtJars();
    }

    /**
     * Change the import status for the supplied Connector
     * 
     * @param connector the connector
     * @param shouldImport a flag indicating if the specified connector should be imported
     * @since 5.5.3
     */
    public void setImportStatus( ConnectorBinding connector,
                                 boolean shouldImport ) {
        if (!this.allImportFileConnAndStatusMap.containsKey(connector)) {
            throw new IllegalArgumentException();
        }
        int importStatus = NOT_SELECTED_FOR_IMPORT;
        if (shouldImport) {
            importStatus = SELECTED_FOR_IMPORT;
        }
        this.allImportFileConnAndStatusMap.put(connector, new Integer(importStatus));

        // Update the required ext jars
        updateRequiredExtJars();
    }

    /**
     * Set the initialize default selections flag. The default value is 'true'. When 'true', the initial import selections will be
     * made for connector types, connectors and extension jars, based upon the imported entities and the entities that already
     * exist in the workspace config. When 'false', no pre-selections will be made - all imported entities will be set to 'not
     * import'.
     * 
     * @param fileName the full path to the cdk or caf file
     * @return status of the operation.
     * @since 5.5.3
     */
    public void setInitDefaultSelectionFlag( boolean initSelections ) {
        this.initDefaultSelections = initSelections;
    }

    /**
     * Set the connector file to import. Invoking this method will trigger the import of all available connector types, connectors
     * and extension jars from the import file.
     * 
     * @param fileName the full path to the cdk or caf file
     * @return status of the operation.
     * @since 5.5.3
     */
    public IStatus setConnectorFile( final String fileName ) {
        ArgCheck.isNotNull(fileName);
        ArgCheck.isNotEmpty(fileName);

        // Init this object if the connector file is reset
        initHelper();

        // ------------------------------------------------------------
        // Check the existence of the file and ensure it is readable
        // ------------------------------------------------------------
        File fileToImport = new File(fileName);
        // Check if file exists
        if (!fileToImport.exists()) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR, getString("importFileNotFound.msg"), null); //$NON-NLS-1$
            // If file exists, test whether the file is readable
        } else if (!fileToImport.canRead()) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR,
                              getString("importFileNotReadable.msg"), null); //$NON-NLS-1$
        }

        // Set the new import file
        this.connectorFile = fileToImport;

        // Determine .cdk or .caf
        if (this.connectorFile.getName().toLowerCase().endsWith(DqpUiConstants.CDK_FILE_EXTENSION)) {
            this.connectorFileType = CDK_FILE;
        } else if (this.connectorFile.getName().toLowerCase().endsWith(DqpUiConstants.CAF_FILE_EXTENSION)) {
            this.connectorFileType = CAF_FILE;
        } else {
            this.connectorFileType = UNKNOWN_FILE;
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR,
                              getString("importFileUnknownType.msg"), null); //$NON-NLS-1$
        }

        // --------------------------------------------------------------------
        // File exists, now import all info that can be obtained from the File
        // --------------------------------------------------------------------
        IStatus importStatus = loadAllImportFileData();

        importStatus = this.extJarHelper.setConnectorFile(fileName);

        // For CAF import, the file must contain at least one non-standard type
        if (this.connectorFileType == CAF_FILE) {
            Collection<ConnectorType> nonStdTypes = getAllNonStandardImportFileConnectorTypes();
            if (nonStdTypes.isEmpty()) {
                return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR,
                                  getString("importFileCAFHasAllStandard.msg"), null); //$NON-NLS-1$
            }
        }

        // initialize the default import selections
        if (initDefaultSelections) {
            initSelections();
        }
        // Return final status of connector type and connector import
        return importStatus;
    }

    /**
     * Get the current connector file type being imported. (CDK_FILE or CAF_FILE)
     * 
     * @return the type of connector file (CDK_FILE or CAF_FILE)
     * @since 5.5.3
     */
    public int getConnectorFileType() {
        return this.connectorFileType;
    }

    /**
     * Set the Extension jar path
     * 
     * @param jarName the name of the jar
     * @param jarPath the path of the jar to use when importing
     * @return the status
     * @since 5.5.3
     */
    public IStatus setRequiredJarPath( String jarName,
                                       String jarPath ) {
        return this.extJarHelper.setRequiredJarPath(jarName, jarPath);
    }

    /**
     * Set the Extension jar path
     * 
     * @param jarName the name of the jar
     * @param jarPath the path of the jar to use when importing
     * @return the status
     * @since 5.5.3
     */
    public IStatus resetRequiredJarPathToDefault( String jarName ) {
        return this.extJarHelper.setRequiredJarPathToDefault(jarName);
    }

    /**
     * Get the current path to the required jar
     * 
     * @param jarName the name of the jar
     * @return the current path of the jar.
     * @since 5.5.3
     */
    public String getRequiredJarPath( String jarName ) throws IllegalArgumentException {
        return this.extJarHelper.getRequiredJarPath(jarName);
    }

    /**
     * Specify that the existing jar for the supplied jarName should be used, rather than overriden.
     * 
     * @param jarName the name of the jar that will use the version currently in the configuration
     * @throws IllegalArgumentException if a jar with the specified name does not exist in the current configuration
     * @since 5.5.3
     */
    public IStatus setUseExistingJar( String jarName ) {
        return this.extJarHelper.setUseExistingJar(jarName);
    }

    /**
     * Get the workspace configuration manager (for checking incoming bindings against workspace)
     * 
     * @return the ConfigurationManager
     * @since 5.5.3
     */
    private ServerAdmin getConfigurationManager() {
        return DqpPlugin.getInstance().getAdmin();
    }

    /**
     * Get the collection of Connector Types which exist in the workspace configuration.
     * 
     * @return the connector types which exist in the workspace configuration
     * @since 5.5.3
     */
    public Collection<ConnectorType> getAllWorkspaceConfigConnectorTypes() {
        // Collection<ConnectorType> types = getConfigurationManager().getConnectorTypes();
        // for(Iterator iter = types.iterator(); iter.hasNext();) {
        // ConnectorType type = (ConnectorType)iter.next();
        // boolean isStandard = getConfigurationManager().isStandardComponentType(type);
        // }
        // return types;
        return getConfigurationManager().getConnectorTypes();
    }

    /**
     * Get the collection of Connectors whick exist in the workspace configuration.
     * 
     * @return the connectors which exist in the workspace configuration.
     * @since 5.5.3
     */
    public Collection<ConnectorBinding> getAllWorkspaceConfigConnectors() {
        return getConfigurationManager().getConnectorBindings();
    }

    /**
     * Load all available data from the (previously set) connectors file, cdk or caf
     * 
     * @return the status of the import.
     * @since 5.5.3
     */
    private IStatus loadAllImportFileData() {

        // Load the Connector types. Method handles both cdk and caf file import.
        IStatus importStatus = loadAllImportFileConnectorTypes();

        // if connector type import was successful, import the connectors (currently only cdk)
        if (importStatus.getCode() == IStatus.OK && this.connectorFileType == CDK_FILE) {
            importStatus = loadAllImportFileConnectors();
        }

        return importStatus;
    }

    /**
     * Load the collection of all connector types from the (previously set) connectors file.
     * 
     * @return the status of the operation.
     * @since 5.5.3
     */
    private IStatus loadAllImportFileConnectorTypes() {
        // Attempt the import
        FileInputStream in = null;
        try {
            in = new FileInputStream(this.connectorFile);
        } catch (FileNotFoundException e) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR, getString("importFileNotFound.msg"), e); //$NON-NLS-1$
        }

        // load all of the available Connector Types
        IStatus status = loadAllImportFileConnectorTypes(in, this.connectorFileType);
        if (status.getCode() != IStatus.OK) {
            return status;
        }

        // Init the import selection map (Selection Status initialized to false)
        this.allImportFileConnTypeAndStatusMap = new HashMap<ConnectorType, Integer>();
        for (Iterator<ConnectorType> ctIter = allImportFileConnectorTypes.iterator(); ctIter.hasNext();) {
            ConnectorType connType = ctIter.next();
            this.allImportFileConnTypeAndStatusMap.put(connType, new Integer(NOT_SELECTED_FOR_IMPORT));
        }
        // Import was successful
        return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, getString("importFileAvailable.msg"), null); //$NON-NLS-1$
    }

    /**
     * Load all connector types from the supplied input stream.
     * 
     * @param stream the FileInputStream
     * @param connFileType the type of input file (caf or cdk)
     * @return the status of the operation.
     * @since 5.5.3
     */
    private IStatus loadAllImportFileConnectorTypes( FileInputStream stream,
                                                     int connFileType ) {
        this.allImportFileConnectorTypes = null;

        // instantiate the import export utilities
        XMLConfigurationImportExportUtility util = new XMLConfigurationImportExportUtility();

        // Get the available Connector Types from the file
        allImportFileConnectorTypes = null;
        try {
            // CDK File
            if (this.connectorFileType == CDK_FILE) {
                allImportFileConnectorTypes = util.importComponentTypes(stream, new BasicConfigurationObjectEditor());
                // CAF File
            } else if (this.connectorFileType == CAF_FILE) {
                ConnectorArchive archive = util.importConnectorArchive(stream, new BasicConfigurationObjectEditor());
                ConnectorType[] cTypes = archive.getConnectorTypes();
                allImportFileConnectorTypes = new ArrayList<ConnectorType>(cTypes.length);
                for (int i = 0; i < cTypes.length; i++) {
                    allImportFileConnectorTypes.add(cTypes[i]);
                }
            }
        } catch (InvalidConfigurationElementException e) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR,
                              getString("importFileInvalidConfigError.msg"), e); //$NON-NLS-1$
        } catch (IOException e) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR, getString("importFileIOError.msg"), e); //$NON-NLS-1$
        }

        // Type Import was successful
        return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, getString("importFileAvailable.msg"), null); //$NON-NLS-1$
    }

    /**
     * Load the collection of all connectors from the (previously set) connectors file.
     * 
     * @return the status of the operation.
     * @since 5.5.3
     */
    private IStatus loadAllImportFileConnectors() {
        // Attempt the import
        FileInputStream in = null;
        try {
            in = new FileInputStream(this.connectorFile);
        } catch (FileNotFoundException e) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR, getString("importFileNotFound.msg"), e); //$NON-NLS-1$
        }

        // instantiate the import export utilities
        ConfigurationObjectEditor coe = new BasicConfigurationObjectEditor(false);
        XMLConfigurationImportExportUtility io = new XMLConfigurationImportExportUtility();

        // Get the available Connectors from the file
        allImportFileConnectors = null;
        try {
            allImportFileConnectors = io.importConnectorBindings(in, coe);
        } catch (ConfigObjectsNotResolvableException e) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR,
                              getString("importFileObjsNotResolvableError.msg"), e); //$NON-NLS-1$
        } catch (InvalidConfigurationElementException e) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR,
                              getString("importFileInvalidConfigError.msg"), e); //$NON-NLS-1$
        } catch (IOException e) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.ERROR, getString("importFileIOError.msg"), e); //$NON-NLS-1$
        }

        // Init the import selection map (Selection Status initialized to false)
        this.allImportFileConnAndStatusMap = new HashMap();
        for (Iterator cIter = allImportFileConnectors.iterator(); cIter.hasNext();) {
            ConnectorBinding conn = (ConnectorBinding)cIter.next();
            this.allImportFileConnAndStatusMap.put(conn, new Integer(NOT_SELECTED_FOR_IMPORT));
        }

        // Import was successful
        return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, getString("importFileAvailable.msg"), null); //$NON-NLS-1$
    }

    /**
     * Initialize the import file - specific variables
     * 
     * @since 5.5.3
     */
    private void initHelper() {
        this.connectorFile = null;
        this.connectorFileType = UNKNOWN_FILE;

        this.allImportFileConnectorTypes = Collections.emptyList();
        this.allImportFileConnectors = Collections.emptyList();

        this.allImportFileConnTypeAndStatusMap = Collections.emptyMap();
        this.allImportFileConnAndStatusMap = Collections.emptyMap();

        initWorkspaceStandardTypesMap();
    }

    /**
     * Initialize a map of the workspace standard types
     * 
     * @since 5.5.3
     */
    private void initWorkspaceStandardTypesMap() {
        this.workspaceConfigStandardTypeMap = new HashMap();
        // Get all connector types in workspace, iterate to find standard type
        Collection<ConnectorType> types = getAdmin().getConnectorTypes();
        for (Iterator iter = types.iterator(); iter.hasNext();) {
            ConnectorType type = (ConnectorType)iter.next();
            boolean isStandard = getAdmin().isStandardComponentType(type);
            if (isStandard) {
                this.workspaceConfigStandardTypeMap.put(type.getFullName(), type);
            }
        }
    }

    /**
     * Check whether the supplied name matches any of the standard workspace type names.
     * 
     * @param name the supplied name
     * @return <code>true</code> if the supplied name matches the name of a workspace standard type.
     * @since 5.5.3
     */
    private boolean nameMatchesWorkspaceStandardType( String name ) {
        boolean matches = false;
        Set wsStandardTypeNames = this.workspaceConfigStandardTypeMap.keySet();
        if (wsStandardTypeNames.contains(name)) {
            matches = true;
        }
        return matches;
    }

    /**
     * Check whether the supplied name matches any of the workspace type names.
     * 
     * @param name the supplied name
     * @return <code>true</code> if the supplied name matches the name of a workspace type.
     * @since 5.5.3
     */
    private boolean nameMatchesWorkspaceType( String name ) {
        boolean matches = false;
        Collection<ConnectorType> types = getConfigurationManager().getConnectorTypes();
        for (Iterator iter = types.iterator(); iter.hasNext();) {
            ConnectorType type = (ConnectorType)iter.next();
            if (type.getFullName().equalsIgnoreCase(name)) {
                matches = true;
                break;
            }
        }
        return matches;
    }

    /**
     * Check whether the supplied name matches any of the workspace connector names.
     * 
     * @param name the supplied name
     * @return <code>true</code> if the supplied name matches the name of a workspace connector.
     * @since 5.5.3
     */
    private boolean nameMatchesWorkspaceConn( String name ) {
        boolean matches = false;
        Collection<ConnectorBinding> conns = getConfigurationManager().getConnectorBindings();
        for (Iterator iter = conns.iterator(); iter.hasNext();) {
            ConnectorBinding conn = (ConnectorBinding)iter.next();
            if (conn.getFullName().equalsIgnoreCase(name)) {
                matches = true;
                break;
            }
        }
        return matches;
    }

    /**
     * Get all the available jarNames from the importFile.
     * 
     * @return the list of all ext jarNames available from the import file selection.
     * @since 5.5.3
     */
    public Collection getAllImportFileJarNames() {
        return this.extJarHelper.getAllImportFileJarNames();
    }

    /**
     * Get all the existing ext jarNames in the workspace config.
     * 
     * @return the list of all ext jarNames available in the workspace config.
     * @since 5.5.3
     */
    public Collection getAllWorkspaceConfigJarNames() {
        return this.extJarHelper.getAllWorkspaceConfigJarNames();
    }

    /**
     * Init the default selections for the entities being imported. Any connector types which do not already exist in the
     * workspace config will be marked for import. Then the connectors of the selected connector types will be marked for import,
     * if they do not already exist. Finally, the required jars which are available and do not already exist will be marked for
     * import.
     * 
     * @since 5.5.3
     */
    private void initSelections() {

        // Iterate the connector types, if no conflicts, mark for import
        for (Iterator ctIter = this.allImportFileConnectorTypes.iterator(); ctIter.hasNext();) {
            ConnectorType cType = (ConnectorType)ctIter.next();
            if (!existsInWorkspaceConfig(cType)) {
                this.allImportFileConnTypeAndStatusMap.put(cType, new Integer(SELECTED_FOR_IMPORT));
            } else {
                this.allImportFileConnTypeAndStatusMap.put(cType, new Integer(NOT_SELECTED_FOR_IMPORT));
            }
        }

        // Iterate the connectors and mark as appropriate. If there is a name conflict with
        // an existing workspace connector, do not mark for import.
        // - first, mark everything as 'No import'
        for (Iterator cIter = this.allImportFileConnectors.iterator(); cIter.hasNext();) {
            ConnectorBinding conn = (ConnectorBinding)cIter.next();
            if (!existsInWorkspaceConfig(conn)) {
                this.allImportFileConnAndStatusMap.put(conn, new Integer(SELECTED_FOR_IMPORT));
            } else {
                this.allImportFileConnAndStatusMap.put(conn, new Integer(NOT_SELECTED_FOR_IMPORT));
            }
        }

        // Set the required list of jars - based on selected types and connectors
        updateRequiredExtJars();
    }

    /**
     * Reset the list of required jarNames, based on the selected connector types and connectors.
     * 
     * @since 5.5.3
     */
    private void updateRequiredExtJars() {
        requiredExtJars = new ArrayList();

        // Iterate the connector types and gather up the ext jars
        for (Iterator ctIter = this.allImportFileConnectorTypes.iterator(); ctIter.hasNext();) {
            ConnectorType cType = (ConnectorType)ctIter.next();
            if (isSelectedForImport(cType)) {
                Collection cTypeModules = ModelerDqpUtils.getRequiredExtensionJarNames(cType);
                for (Iterator jarIter = cTypeModules.iterator(); jarIter.hasNext();) {
                    String jarName = (String)jarIter.next();
                    if (!requiredExtJars.contains(jarName)) {
                        requiredExtJars.add(jarName);
                    }
                }
            }
        }

        // Iterate the connectors and gather up the ext jars - not required to be same as types
        for (Iterator cIter = this.allImportFileConnectors.iterator(); cIter.hasNext();) {
            ConnectorBinding conn = (ConnectorBinding)cIter.next();
            if (isSelectedForImport(conn)) {
                Collection connModules = ModelerDqpUtils.getRequiredExtensionJarNames(conn);
                for (Iterator jarIter = connModules.iterator(); jarIter.hasNext();) {
                    String jarName = (String)jarIter.next();
                    if (!requiredExtJars.contains(jarName)) {
                        requiredExtJars.add(jarName);
                    }
                }
            }
        }
        // Set the required jars on the jar helper
        this.extJarHelper.setRequiredExtensionJarNames(requiredExtJars);

    }

    private IStatus importConnectorTypes( List<ConnectorType> types,
                                          Set<String> copiedJars,
                                          IProgressMonitor monitor ) {
        ConfigurationManager configMgr = getConfigurationManager();
        int numTypes = types.size();
        int numTypesProcessed = 0;

        monitor.subTask(getString("importConnectorTypesTaskName")); //$NON-NLS-1$

        // process types and their required jars
        for (int i = 0; i < numTypes; ++i) {
            ConnectorType newType = types.get(i);

            // process jars first so that if a jar cannot be copied we won't add the type
            JarCopyMgr copyMgr = new JarCopyMgr(ModelerDqpUtils.getRequiredExtensionJarNames(newType));
            IStatus jarStatus = copyMgr.copy(copiedJars);

            if (jarStatus.isOK()) {
                ConnectorType existingType = null;
                Collection existingBindings = null;
                boolean existingTypeRemoved = false;

                try {
                    int statusCode = getStatus(newType).getCode();

                    // save existing type and bindings in case we have to recover
                    if (statusCode == StatusCodes.UPDATE) {
                        existingType = (ConnectorType)configMgr.getComponentType(newType.getName());
                        existingBindings = configMgr.getBindingsForType(existingType.getID());

                        // removing type removes all bindings associated with that type
                        configMgr.removeConnectorType(existingType);
                        existingTypeRemoved = true;
                    }

                    // add new type
                    configMgr.addConnectorType(newType);

                    // increment work completed
                    monitor.worked(1);
                    ++numTypesProcessed;

                    // don't need the copy manager anymore so have it get rid of it's resources
                    copyMgr.flush();

                    // if user canceled don't process the remaining types
                    if (monitor.isCanceled()) {
                        // if we didn't process all the types stop import
                        if (numTypesProcessed != numTypes) {
                            String msg = null;

                            if (numTypesProcessed == 0) {
                                msg = getString("importCanceledNoTypesImported"); //$NON-NLS-1$
                            } else {
                                msg = getString("importCanceledSomeTypesImported", numTypesProcessed, numTypes); //$NON-NLS-1$
                            }

                            return new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                        }
                    }
                } catch (Throwable t) {
                    UTIL.log(t);

                    // adding new type failed and there was no existing type being overwritten
                    if (existingType == null) {
                        String msg = null;

                        if (numTypesProcessed == 0) {
                            msg = UTIL.getString("importNewTypeFailedNoTypesImported", newType.getFullName()); //$NON-NLS-1$
                        } else {
                            msg = UTIL.getString(I18N_PREFIX + "importNewTypeFailedSomeTypesImported", //$NON-NLS-1$
                                                 newType.getFullName(),
                                                 numTypesProcessed,
                                                 numTypes);
                        }

                        return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                    }

                    // copy original jars back
                    IStatus undoStatus = copyMgr.undo();

                    // restore existing type since adding new type failed
                    if (existingTypeRemoved) {
                        try {
                            // restore existing type
                            configMgr.addConnectorType(existingType);

                            // restore existing bindings
                            if ((existingBindings != null) && !existingBindings.isEmpty()) {
                                for (Iterator itr = existingBindings.iterator(); itr.hasNext();) {
                                    configMgr.addBinding((ConnectorBinding)itr.next());
                                }
                            }

                            String msg = null;

                            // problem copying original jars back to the configuration but the type and bindings were restored
                            if (undoStatus.getSeverity() == IStatus.ERROR) {
                                if (numTypesProcessed == 0) {
                                    msg = UTIL.getString(I18N_PREFIX + "restoredTypeNotJarsNoTypesImported", //$NON-NLS-1$
                                                         newType.getFullName(),
                                                         copyMgr.getTempDirPath());
                                } else {
                                    Object[] params = new Object[] {newType.getFullName(), copyMgr.getTempDirPath(),
                                        numTypesProcessed, numTypes};
                                    msg = UTIL.getString(I18N_PREFIX + "restoredTypeNotJarsSomeTypesImported", params); //$NON-NLS-1$
                                }
                            } else {
                                // the original type, bindings, and jars were restored
                                if (numTypesProcessed == 0) {
                                    // restoring old type was successful
                                    msg = UTIL.getString(I18N_PREFIX + "restoredTypeNoTypesImported", //$NON-NLS-1$
                                                         newType.getFullName());
                                } else {
                                    // restoring old type was successful
                                    msg = UTIL.getString(I18N_PREFIX + "restoredTypeSomeTypesImported", //$NON-NLS-1$
                                                         newType.getFullName(),
                                                         numTypesProcessed,
                                                         numTypes);
                                }
                            }

                            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                        } catch (Exception e) {
                            // could not restore old type
                            String msg = null;

                            if (undoStatus.getSeverity() == IStatus.ERROR) {
                                if (numTypesProcessed == 0) {
                                    msg = UTIL.getString(I18N_PREFIX + "errorRestoringTypeJarsNoTypesImported", //$NON-NLS-1$
                                                         newType.getFullName(),
                                                         copyMgr.getTempDirPath());
                                } else {
                                    Object[] params = new Object[] {newType.getFullName(), copyMgr.getTempDirPath(),
                                        numTypesProcessed, numTypes};
                                    msg = UTIL.getString(I18N_PREFIX + "errorRestoringTypeJarsSomeTypesImported", params); //$NON-NLS-1$
                                }
                            } else {
                                if (numTypesProcessed == 0) {
                                    msg = UTIL.getString(I18N_PREFIX + "errorRestoringTypeNoTypesImported", //$NON-NLS-1$
                                                         newType.getFullName());
                                } else {
                                    Object[] params = new Object[] {newType.getFullName(), numTypesProcessed, numTypes};
                                    msg = UTIL.getString(I18N_PREFIX + "errorRestoringTypeSomeTypesImported", params); //$NON-NLS-1$
                                }
                            }

                            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, e);
                        }
                    }

                    // could not add new type because removing existing type failed
                    String msg = null;

                    if (undoStatus.getSeverity() == IStatus.ERROR) {
                        if (numTypesProcessed == 0) {
                            msg = UTIL.getString(I18N_PREFIX + "errorRemovingExistingTypeJarsNoTypesImported", //$NON-NLS-1$
                                                 newType.getFullName(),
                                                 copyMgr.getTempDirPath());
                        } else {
                            Object[] params = new Object[] {newType.getFullName(), copyMgr.getTempDirPath(), numTypesProcessed,
                                numTypes};
                            msg = UTIL.getString(I18N_PREFIX + "errorRemovingExistingTypeJarsSomeTypesImported", params); //$NON-NLS-1$
                        }
                    } else {
                        if (numTypesProcessed == 0) {
                            msg = UTIL.getString(I18N_PREFIX + "errorRemovingExistingTypeNoTypesImported", //$NON-NLS-1$
                                                 newType.getFullName());
                        } else {
                            Object[] params = new Object[] {newType.getFullName(), numTypesProcessed, numTypes};
                            msg = UTIL.getString(I18N_PREFIX + "errorRemovingExistingTypeSomeTypesImported", params); //$NON-NLS-1$
                        }
                    }

                    return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                }
            } else {
                // there was a problem copying original jars to temporary location
                UTIL.log(jarStatus);

                // restore original jars
                IStatus status = copyMgr.undo();
                String msg = null;

                if (status.getSeverity() == IStatus.ERROR) {
                    UTIL.log(status);

                    if (numTypesProcessed == 0) {
                        msg = UTIL.getString(I18N_PREFIX + "copyRestoreTypeJarsFailedNoTypesImported", //$NON-NLS-1$
                                             newType.getFullName(),
                                             copyMgr.getTempDirPath());
                    } else {
                        Object[] params = new Object[] {newType.getFullName(), copyMgr.getTempDirPath(), numTypesProcessed,
                            numTypes};
                        msg = UTIL.getString(I18N_PREFIX + "copyRestoreTypeJarsFailedSomeTypesImported", params); //$NON-NLS-1$
                    }
                } else {
                    if (numTypesProcessed == 0) {
                        msg = UTIL.getString(I18N_PREFIX + "copyTypeJarsFailedNoTypesImported", //$NON-NLS-1$
                                             newType.getFullName());
                    } else {
                        msg = UTIL.getString(I18N_PREFIX + "copyTypeJarsFailedSomeTypesImported", //$NON-NLS-1$
                                             newType.getFullName(),
                                             numTypesProcessed,
                                             numTypes);
                    }
                }

                return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
            }
        }

        // everything imported successfully
        return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, getString("allTypesImported"), null); //$NON-NLS-1$
    }

    private IStatus importConnectorBindings( List<ConnectorBinding> bindings,
                                             Set<String> copiedJars,
                                             IProgressMonitor monitor ) {
        ConfigurationManager configMgr = getConfigurationManager();
        int numBindings = bindings.size();
        int numBindingsProcessed = 0;

        monitor.subTask(getString("importConnectorsTaskName")); //$NON-NLS-1$

        // process bindings and their required jars
        for (int i = 0; i < numBindings; ++i) {
            ConnectorBinding newBinding = bindings.get(i);

            // process jars first so that if a jar cannot be copied we won't add the binding
            JarCopyMgr copyMgr = new JarCopyMgr(ModelerDqpUtils.getRequiredExtensionJarNames(newBinding));
            IStatus jarStatus = copyMgr.copy(copiedJars);

            if (jarStatus.isOK()) {
                ConnectorBinding existingBinding = null;
                boolean existingBindingRemoved = false;

                // now add the binding
                try {
                    int statusCode = getStatus(newBinding).getCode();

                    // save existing binding in case we have to recover (status is not equals or incompatible)
                    if (statusCode == StatusCodes.UPDATE) {
                        existingBinding = configMgr.getBinding(newBinding.getName());
                        configMgr.removeBinding(existingBinding);
                        existingBindingRemoved = true;
                    }

                    // add new binding
                    configMgr.addBinding(newBinding);

                    // increment work completed
                    monitor.worked(1);
                    ++numBindingsProcessed;

                    // don't need the copy manager anymore so have it get rid of it's resources
                    copyMgr.flush();

                    if (monitor.isCanceled()) {
                        // if we didn't process all the bindings stop import
                        if (numBindingsProcessed != numBindings) {
                            String msg = null;

                            if (numBindingsProcessed == 0) {
                                msg = getString("importCanceledNoBindingsImported"); //$NON-NLS-1$
                            } else {
                                msg = getString("importCanceledSomeBindingsImported", numBindingsProcessed, numBindings); //$NON-NLS-1$
                            }

                            return new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                        }
                    }
                } catch (Throwable t) {
                    UTIL.log(t);

                    // adding new binding failed and there was no existing binding being overwritten
                    if (existingBinding == null) {
                        String msg = null;

                        if (numBindingsProcessed == 0) {
                            msg = UTIL.getString(I18N_PREFIX + "importNewBindingFailedNoTypesImported", newBinding.getFullName()); //$NON-NLS-1$
                        } else {
                            msg = UTIL.getString(I18N_PREFIX + "importNewBindingFailedSomeBindingsImported", //$NON-NLS-1$
                                                 newBinding.getFullName(),
                                                 numBindingsProcessed,
                                                 numBindings);
                        }

                        return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                    }

                    // copy original jars back
                    IStatus undoStatus = copyMgr.undo();

                    // restore existing binding since adding new binding failed
                    if (existingBindingRemoved) {
                        try {
                            // restore existing binding
                            configMgr.addBinding(existingBinding);

                            String msg = null;

                            // problem copying original jars back to the configuration but the original binding was restored
                            if (undoStatus.getSeverity() == IStatus.ERROR) {
                                if (numBindingsProcessed == 0) {
                                    msg = UTIL.getString(I18N_PREFIX + "restoredBindingNotJarsNoBindingsImported", //$NON-NLS-1$
                                                         newBinding.getFullName(),
                                                         copyMgr.getTempDirPath());
                                } else {
                                    Object[] params = new Object[] {newBinding.getFullName(), copyMgr.getTempDirPath(),
                                        numBindingsProcessed, numBindings};
                                    msg = UTIL.getString(I18N_PREFIX + "restoredBindingNotJarsSomeBindingsImported", params); //$NON-NLS-1$
                                }
                            } else {
                                // the original bindings and jars were restored
                                if (numBindingsProcessed == 0) {
                                    // restoring old binding was successful
                                    msg = UTIL.getString(I18N_PREFIX + "restoredBindingNoBindingsImported", //$NON-NLS-1$
                                                         newBinding.getFullName());
                                } else {
                                    // restoring old binding was successful
                                    msg = UTIL.getString(I18N_PREFIX + "restoredBindingSomeBindingsImported", //$NON-NLS-1$
                                                         newBinding.getFullName(),
                                                         numBindingsProcessed,
                                                         numBindings);
                                }
                            }

                            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                        } catch (Exception e) {
                            // could not restore old binding
                            String msg = null;

                            if (undoStatus.getSeverity() == IStatus.ERROR) {
                                if (numBindingsProcessed == 0) {
                                    msg = UTIL.getString(I18N_PREFIX + "errorRestoringBindingJarsNoBindingsImported", //$NON-NLS-1$
                                                         newBinding.getFullName(),
                                                         copyMgr.getTempDirPath());
                                } else {
                                    Object[] params = new Object[] {newBinding.getFullName(), copyMgr.getTempDirPath(),
                                        numBindingsProcessed, numBindings};
                                    msg = UTIL.getString(I18N_PREFIX + "errorRestoringBindingJarsSomeBindingsImported", params); //$NON-NLS-1$
                                }
                            } else {
                                if (numBindingsProcessed == 0) {
                                    msg = UTIL.getString(I18N_PREFIX + "errorRestoringBindingNoBindingsImported", //$NON-NLS-1$
                                                         newBinding.getFullName());
                                } else {
                                    Object[] params = new Object[] {newBinding.getFullName(), numBindingsProcessed, numBindings};
                                    msg = UTIL.getString(I18N_PREFIX + "errorRestoringBindingSomeBindingsImported", params); //$NON-NLS-1$
                                }
                            }

                            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, e);
                        }
                    }

                    // could not add new binding because removing existing binding failed
                    String msg = null;

                    if (undoStatus.getSeverity() == IStatus.ERROR) {
                        if (numBindingsProcessed == 0) {
                            msg = UTIL.getString(I18N_PREFIX + "errorRemovingExistingBindingJarsNoBindingsImported", //$NON-NLS-1$
                                                 newBinding.getFullName(),
                                                 copyMgr.getTempDirPath());
                        } else {
                            Object[] params = new Object[] {newBinding.getFullName(), copyMgr.getTempDirPath(),
                                numBindingsProcessed, numBindings};
                            msg = UTIL.getString(I18N_PREFIX + "errorRemovingExistingBindingJarsSomeBindingsImported", params); //$NON-NLS-1$
                        }
                    } else {
                        if (numBindingsProcessed == 0) {
                            msg = UTIL.getString(I18N_PREFIX + "errorRemovingExistingBindingNoBindingsImported", //$NON-NLS-1$
                                                 newBinding.getFullName());
                        } else {
                            Object[] params = new Object[] {newBinding.getFullName(), numBindingsProcessed, numBindings};
                            msg = UTIL.getString(I18N_PREFIX + "errorRemovingExistingBindingSomeBindingsImported", params); //$NON-NLS-1$
                        }
                    }

                    return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                }
            } else {
                // there was a problem copying original jars to temporary location
                UTIL.log(jarStatus);

                // restore original jars
                IStatus status = copyMgr.undo();
                String msg = null;

                if (status.getSeverity() == IStatus.ERROR) {
                    UTIL.log(status);

                    if (numBindingsProcessed == 0) {
                        msg = UTIL.getString(I18N_PREFIX + "copyRestoreBindingJarsFailedNoBindingsImported", //$NON-NLS-1$
                                             newBinding.getFullName(),
                                             copyMgr.getTempDirPath());
                    } else {
                        Object[] params = new Object[] {newBinding.getFullName(), copyMgr.getTempDirPath(), numBindingsProcessed,
                            numBindings};
                        msg = UTIL.getString(I18N_PREFIX + "copyRestoreBindingJarsFailedSomeBindingsImported", params); //$NON-NLS-1$
                    }
                } else {
                    if (numBindingsProcessed == 0) {
                        msg = UTIL.getString(I18N_PREFIX + "copyBindingJarsFailedNoBindingsImported", //$NON-NLS-1$
                                             newBinding.getFullName());
                    } else {
                        msg = UTIL.getString(I18N_PREFIX + "copyBindingJarsFailedSomeBindingsImported", //$NON-NLS-1$
                                             newBinding.getFullName(),
                                             numBindingsProcessed,
                                             numBindings);
                    }
                }

                return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
            }
        }

        // everything imported successfully
        return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, getString("allBindingsImported"), null); //$NON-NLS-1$
    }

    public IStatus performImport( IProgressMonitor monitor ) {
        // make sure it is OK to go ahead with import
        assert (getImportStatus().getSeverity() != IStatus.ERROR);

        Set<String> copiedJars = new HashSet<String>();
        List<ConnectorType> types = new ArrayList<ConnectorType>(getSelectedImportFileConnectorTypes());
        List<ConnectorBinding> bindings = new ArrayList<ConnectorBinding>(getSelectedImportFileConnectors());

        // set the total work in the monitor
        monitor.beginTask(getString("importTaskName"), types.size() + bindings.size()); //$NON-NLS-1$
        monitor.setTaskName(getString("importTaskName")); //$NON-NLS-1$

        IStatus status = importConnectorTypes(types, copiedJars, monitor);

        if (status.getSeverity() != IStatus.ERROR) {
            if (!monitor.isCanceled()) {
                status = importConnectorBindings(bindings, copiedJars, monitor);

                if (status.getSeverity() != IStatus.ERROR) {
                    return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, getString("importSuccess"), null); //$NON-NLS-1$
                }
            }
        }

        return status;
    }

    /**
     * The <code>JarCopyMgr</code> class manages the copying of jar files to the configuration.
     * 
     * @since 5.5.3
     */
    private class JarCopyMgr {

        private final Collection<String> jarNames;

        private TempDirectory tempDir;

        public JarCopyMgr( Collection<String> jarNames ) {
            this.jarNames = jarNames;
        }

        /**
         * Copies the jars being overwritten to a temporary location that can be used later to
         * 
         * @param copiedJars a list of jars that have already been copied to the configuration's extension module directory
         * @return the status of the copy
         * @since 5.5.3
         */
        public IStatus copy( Set<String> copiedJars ) {
            DqpExtensionsHandler extensionMgr = DqpPlugin.getInstance().getExtensionsHandler();

            for (Iterator<String> itr = this.jarNames.iterator(); itr.hasNext();) {
                String jarName = itr.next();
                String path = getRequiredJarPath(jarName);
                // If this is unmapped "connector_patch.jar", do not process
                if (DqpExtensionsHandler.CONNECTOR_PATCH_JAR.equals(jarName) && path == null) {
                    continue;
                }
                // only copy if not using an existing jar or if the jar has already been copied
                if (!path.equals(USE_WSCONFIG_JAR) && !copiedJars.contains(jarName)) {
                    // check to see if we're overwriting
                    if (jarExistsInConfiguration(jarName)) {
                        // make sure there is a temporary directory
                        if (this.tempDir == null) {
                            this.tempDir = TempDirectory.getTempDirectory(null);
                        }

                        // copy old jar so that we can get it back later
                        try {
                            extensionMgr.copyExtensionModule(jarName, this.tempDir.getPath());
                        } catch (Exception e) {
                            UTIL.log(e);
                            String msg = UTIL.getString(I18N_PREFIX + "JarCopyMgr.problemCopyingJar.msg", //$NON-NLS-1$
                                                        jarName,
                                                        this.tempDir.getPath());
                            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, e);
                        }
                    }

                    // add jar to configuration
                    try {
                        if (path.equals(USE_CAF_JAR)) {
                            // there was a problem adding jar from caf
                            if (!extensionMgr.addConnectorJars(this, new ExtensionModule[] {getCAFJarFromImport(jarName)})) {
                                String msg = UTIL.getString(I18N_PREFIX + "JarCopyMgr.problemAddingCafJar.msg", jarName); //$NON-NLS-1$
                                return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                            }
                        } else if (!extensionMgr.addConnectorJar(this, new File(path, jarName))) {
                            // there was a problem adding jar
                            String msg = UTIL.getString(I18N_PREFIX + "JarCopyMgr.problemAddingJar.msg", jarName, path); //$NON-NLS-1$
                            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                        }
                    } catch (Throwable t) {
                        UTIL.log(t);
                        String msg = UTIL.getString(I18N_PREFIX + "JarCopyMgr.problemAddingJar.msg", jarName, path); //$NON-NLS-1$
                        return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, t);
                    }

                    // jar copied to temporary location (if necessary) and new connector jar added to configuration successfully
                    copiedJars.add(jarName);
                }
            }

            return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, getString("JarCopyMgr.copySuccess.msg"), null); //$NON-NLS-1$
        }

        /**
         * Deletes the temporary directory holding the original jar files that were overwritten.
         * 
         * @since 5.5.3
         */
        public void flush() {
            if (this.tempDir != null) {
                this.tempDir.remove();
            }
        }

        /**
         * @return the path of the temporary directory
         * @since 5.5.3
         */
        public String getTempDirPath() {
            return this.tempDir.getPath();
        }

        /**
         * Writes the original jar files back to the extensions directory.
         * 
         * @return the status of the undo
         * @since 5.5.3
         */
        public IStatus undo() {
            if (this.tempDir != null) {
                DqpExtensionsHandler extensionMgr = DqpPlugin.getInstance().getExtensionsHandler();
                File dir = new File(this.tempDir.getPath());
                boolean allCopied = true;

                // copy back all the original jars that have been overwritten back into the configuration
                for (File oldJarFile : dir.listFiles()) {
                    if (!extensionMgr.addConnectorJar(dir, oldJarFile)) {
                        // problem putting back old jar
                        allCopied = false;
                    }
                }

                if (allCopied) {
                    flush();
                } else {
                    String msg = UTIL.getString(I18N_PREFIX + "JarCopyMgr.undoFailed.msg", this.jarNames); //$NON-NLS-1$
                    new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
                }
            }

            return new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK, getString("JarCopyMgr.undoSuccess.msg"), null); //$NON-NLS-1$
        }
    }
}
