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

package com.metamatrix.modeler.internal.dqp.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.common.config.api.ConnectorArchive;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.api.ExtensionModule;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.common.config.util.InvalidConfigurationElementException;
import com.metamatrix.common.config.xml.XMLConfigurationImportExportUtility;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.config.DqpExtensionsHandler;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;

/**
 * Business Object which is used by the connector import wizard in Designer.
 * Currently, the user can import from either a .cdk or .caf file.  When the import file
 * is set via the 'setConnectorFile' method, all available data is imported from it (connector
 * types, connectors and extension jars).
 */
public class ExtensionJarImportHelper implements DqpUiConstants {


    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExtensionJarImportHelper.class);

    // Connector File type Options
    private static final int UNKNOWN_FILE = -1;
    private static final int CDK_FILE = 0;
    private static final int CAF_FILE = 1;

    private static final String JAR_EXT = ".jar"; //$NON-NLS-1$

	private File connectorFile;
	private int connectorFileType = UNKNOWN_FILE;

	// Collection of already-existing connector and udf jars in workspace config
	private Collection<String> existingWorkpaceExtJarNames;

	// Collections of import file jars
	private Collection<File> allImportFileExtJars;
	private Collection<ExtensionModule> allImportCAFExtJars;
	// Collection of required jarNames
	private Collection<String> requiredExtJars;

	// Map of Connector and Connector Types to integer status
	private Map<String, String> allJarAndPathMap;
	private Map<String, IStatus> allJarAndStatusMap;

	/**
     * Helper method to get string from the i18n.properties file
     * @param id the i18n string key
     * @return the associated string
     * @since 5.5.3
     */
    private static String getString(final String id) {
        return UTIL.getStringOrKey(I18N_PREFIX + id);
    }
    private static String getString(final String id,final Object param1) {
        return UTIL.getString(I18N_PREFIX + id,param1);
    }
	// ===========================================================================================================================
    // Constructor
    // ===========================================================================================================================

    /**
     * Constructor
     * @since 5.5.3
     */
	public ExtensionJarImportHelper ( ) {
		// Init empty collections on construction
		initHelper();
	}

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

	/**
     * Get the list of all required extension jar names
     * @return all required jar names for the connector types and connectors (no duplicates); never null.
     * @since 5.5.3
     */
    public Collection<String> getRequiredExtensionJarNames() {
    	return this.requiredExtJars;
    }

    /**
     * Set the list of all required extension jar names
     * @param rqdJarNames all required jar names for current selections.
     * @since 5.5.3
     */
    public void setRequiredExtensionJarNames(Collection<String> rqdJarNames) {
    	this.requiredExtJars = rqdJarNames;

    	// if path is not set, try to set it to default
    	for(String jarName : this.requiredExtJars) {
    		String jarPath = this.allJarAndPathMap.get(jarName);
    		if(jarPath==null || jarPath.trim().length()==0) {
        		this.setRequiredJarPathToDefault(jarName);
    		}
    	}
    }

    /**
     * Determine if overwrite of any workspace config jars is allowed.  The specific case where overwrite
     * is currently *not* allowed is for caf import.  For caf import, the caf jars must be used.
     * @param jarName
     * @return <code>true</code> if any workspace jars can be overwritten.
     * @since 5.5.3
     */
    public boolean canOverwriteWorkspaceConfigJars( ) {
    	boolean allowOverwrite = true;

    	if(this.connectorFileType==CAF_FILE) {
    		allowOverwrite = false;
    	}

    	return allowOverwrite;
    }

    /**
     * Determine if overwrite of the supplied jar is allowed.  The specific case where overwrite
     * is currently *not* allowed is for caf import.  For caf import, the caf jar must be used.
     * @param jarName
     * @return <code>true</code> if the existing workspace jar is allowed to be overwritten.
     * @since 5.5.3
     */
    public boolean canOverwriteWorkspaceConfigJar(String jarName) {
    	boolean allowOverwrite = true;

    	// If no jars can be overwritten, short circuit
    	if(!canOverwriteWorkspaceConfigJars()) {
    		allowOverwrite = false;
    	// Check individual jar
    	} else if(this.allJarAndPathMap.containsKey(jarName)&&this.allJarAndPathMap.get(jarName).equalsIgnoreCase(ConnectorImportHelper.USE_CAF_JAR)) {
	    		allowOverwrite=false;
    	}
    	return allowOverwrite;
    }

    /**
     * Get the list of all required extension jar names that are not yet mapped.
     * @return all required but unmapped jar names for the connector types and connectors (no duplicates); never null.
     * @since 5.5.3
     */
    public Collection<String> getUnmappedRequiredExtensionJarNames() {
    	Collection<String> unmappedJars = new ArrayList<String>();
    	// Iterate the list of required jar names
    	for(Iterator<String> jIter = this.requiredExtJars.iterator(); jIter.hasNext();) {
    		String jarName = jIter.next();
    		// connector_patch.jar not required to be mapped, leave it out of unmapped list
    		if(!DqpExtensionsHandler.CONNECTOR_PATCH_JAR.equals(jarName)) {
        		String jarPath = this.allJarAndPathMap.get(jarName);
        		if(jarPath==null || jarPath.trim().length()==0) {
        			unmappedJars.add(jarName);
        		}
    		}
    	}
    	return unmappedJars;
    }

    /**
     * Get the required extension jar status
     * @param jarName the name of the jar whose import status is being requested
     * @return the import status of the specified jar file
     * @since 5.5.3
     */
    public IStatus getRequiredExtensionJarStatus( String jarName ) {
    	if(jarName==null || jarName.trim().length()==0) {
        	throw new IllegalArgumentException();
    	}
    	if(!this.requiredExtJars.contains(jarName)) {
    		throw new IllegalArgumentException();
    	}
    	return this.allJarAndStatusMap.get(jarName);
    }

    /**
     * Get a collection of IStatus for all the required extension jars
     * @return the collection of IStatus of the of all required jars
     * @since 5.5.3
     */
    public Collection<IStatus> getAllRequiredExtensionJarStatus() {
    	Collection<IStatus> statusList = new ArrayList<IStatus>(this.requiredExtJars.size());
    	for(Iterator<String> iter = this.requiredExtJars.iterator(); iter.hasNext();) {
    		String jarName = iter.next();
    		IStatus status = this.allJarAndStatusMap.get(jarName);
    		statusList.add(status);
    	}
    	return statusList;
    }

    /**
     * Check whether the supplied jarName already exists in the list of
     * workspace extension jars.
     * @param jarName the name of the extension jar being checked
     * @return <code>true</code> if an extension jar with the specified name already exists in the current configuration
     * @since 5.5.3
     */
    public boolean jarExistsInConfiguration( String jarName ) {
    	boolean jarExistsInConfig = false;
    	for(Iterator<String> jIter = this.existingWorkpaceExtJarNames.iterator(); jIter.hasNext(); ) {
    		String workspaceJarName = jIter.next();
    		if(workspaceJarName.equalsIgnoreCase(jarName)) {
    			jarExistsInConfig = true;
    			break;
    		}
    	}
        return jarExistsInConfig;
    }

    /**
     * Check whether the supplied jarName is available with the import.
     * @param jarName the name of the extension jar being checked
     * @return <code>true</code> if an extension jar with the specified name is available with the importFile selection.
     * @since 5.5.3
     */
    public boolean jarExistsInImport( String jarName ) {
    	boolean jarExistsInImport = false;
    	// The available imported jars different, depending on caf or cdk
    	if(this.connectorFileType==CDK_FILE) {
    		for(Iterator<File> iter = this.allImportFileExtJars.iterator(); iter.hasNext();) {
    			File theFile = iter.next();
    			if(theFile.getName().equalsIgnoreCase(jarName)) {
    				jarExistsInImport=true;
    				break;
    			}
    		}
    	} else if(this.connectorFileType==CAF_FILE) {
    		for(Iterator<ExtensionModule> iter = this.allImportCAFExtJars.iterator(); iter.hasNext();) {
    			ExtensionModule module = iter.next();
    			if(module.getFullName().equalsIgnoreCase(jarName)) {
    				jarExistsInImport=true;
    				break;
    			}
    		}
    	}
        return jarExistsInImport;
    }

    /**
     * Get the CAF jar from the import.  If not found or this is not a CAF import,
     * null will be returned;
     * @param jarName the name of the extension jar
     * @return the CAF jar ExtensionModule, or null if not found or not caf import.
     * @since 5.5.3
     */
    public ExtensionModule getCAFJarFromImport( String jarName ) {
    	ExtensionModule retModule = null;
    	for(Iterator<ExtensionModule> iter = this.allImportCAFExtJars.iterator(); iter.hasNext();) {
    		ExtensionModule module = iter.next();
			if(module.getFullName().equalsIgnoreCase(jarName)) {
				retModule=module;
				break;
			}
    	}
    	return retModule;
    }

    /**
     * Get the jar File from the import.  If not found or this is not a CDK import,
     * null will be returned;
     * @param jarName the name of the extension jar
     * @return the jar File, or null if not found or not cdk import.
     * @since 5.5.3
     */
    public File getCDKJarFromImport( String jarName ) {
    	File retFile = null;
    	for(Iterator<File> iter = this.allImportFileExtJars.iterator(); iter.hasNext();) {
    		File file = iter.next();
			if(file.getName().equalsIgnoreCase(jarName)) {
				retFile=file;
				break;
			}
    	}
    	return retFile;
    }
    /**
     * Set the connector file to import.  Invoking this method will trigger the import of all
     * available connector types, connectors and extension jars from the import file.
     * @param fileName the full path to the cdk or caf file
     * @return status of the operation.
     * @since 5.5.3
     */
    public IStatus setConnectorFile(final String fileName ) {
    	ArgCheck.isNotNull(fileName);
    	ArgCheck.isNotEmpty(fileName);

		// Init this object if the connector file is reset
    	initHelper();

		// Load the list of existing connector and udf jar names
		this.loadExistingWorkpaceJarNames();

    	// ------------------------------------------------------------
    	// Check the existence of the file and ensure it is readable
    	// ------------------------------------------------------------
        File fileToImport = new File(fileName);
        // Check if file exists
        if(!fileToImport.exists()) {
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("importFileNotFound.msg"),null); //$NON-NLS-1$
        // If file exists, test whether the file is readable
        } else if(!fileToImport.canRead()) {
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("importFileNotReadable.msg"),null); //$NON-NLS-1$
        }

        // Set the new import file
        this.connectorFile = fileToImport;

        // Determine .cdk or .caf
        if ( this.connectorFile.getName().toLowerCase().endsWith(DqpUiConstants.CDK_FILE_EXTENSION) ) {
            this.connectorFileType = CDK_FILE;
        }else if ( this.connectorFile.getName().toLowerCase().endsWith(DqpUiConstants.CAF_FILE_EXTENSION) ) {
            this.connectorFileType = CAF_FILE;
        } else {
        	this.connectorFileType = UNKNOWN_FILE;
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("importFileUnknownType.msg"),null); //$NON-NLS-1$
        }

        // --------------------------------------------------------------------
    	// File exists, now import all jars that can be obtained from the File
    	// --------------------------------------------------------------------
    	IStatus importStatus = loadAllImportFileExtJars();

    	// Initialize all jar defaults
    	initAllJarDefaultPathAndStatus();

    	// Return final status of connector type and connector import
    	return importStatus;
    }

    /**
     * Get the current connector file type being imported. (CDK_FILE or CAF_FILE)
     * @return the type of connector file (CDK_FILE or CAF_FILE)
     * @since 5.5.3
     */
    public int getConnectorFileType( ) {
    	return this.connectorFileType;
    }

   	/**
   	 * Set the Extension jar path
     * @param jarName the name of the jar
     * @param jarPath the path of the jar to use when importing
     * @return the status
     * @since 5.5.3
     */
    public IStatus setRequiredJarPath( String jarName, String jarPath ) {
    	if(jarName==null || jarName.trim().length()==0) {
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("emptyJarNameError.msg"),null); //$NON-NLS-1$
    	}
    	if(!this.requiredExtJars.contains(jarName)) {
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("jarNotRequiredError.msg"),null); //$NON-NLS-1$
    	}

    	// If request to "USE EXISTING JAR" check if it exists in workspaceConfig first.
    	if(ConnectorImportHelper.USE_WSCONFIG_JAR.equalsIgnoreCase(jarPath) && !jarExistsInConfiguration(jarName)) {
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("jarDoesNotExistInConfig.msg"),null); //$NON-NLS-1$
    	}
    	this.allJarAndPathMap.put(jarName, jarPath);

        IStatus status = null;

        // Path set to null
        if( jarPath==null || jarPath.trim().length()==0 ) {
    		if(!DqpExtensionsHandler.CONNECTOR_PATCH_JAR.equalsIgnoreCase(jarName)) {
    			String msg = getString("reqdJarPathNotSet.msg",jarName); //$NON-NLS-1$
                status = new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,msg,null);
    		} else {
                status = new Status(IStatus.OK,DqpUiConstants.PLUGIN_ID,IStatus.OK,getString("connPatchJarPathNotSet.msg"),null); //$NON-NLS-1$
    		}
        // Path non-null, but overwrite of config is chosen
        } else if(jarExistsInConfiguration(jarName) && !ConnectorImportHelper.USE_WSCONFIG_JAR.equalsIgnoreCase(jarPath)) {
        	String msg = getString("configJarOverwriteSelected.msg",jarName); //$NON-NLS-1$
            status = new Status(IStatus.WARNING,DqpUiConstants.PLUGIN_ID,IStatus.WARNING,msg,null);
        // All other situations ok
        } else {
        	String message = null;
        	// The CAF jar is being used
    		if(ConnectorImportHelper.USE_CAF_JAR.equals(jarPath)) {
    			message = getString("reqdJarImportCAFJarUsed.msg",jarName); //$NON-NLS-1$
    		} else {
    			message = getString("reqdJarSpecifiedPathUsed.msg",jarName); //$NON-NLS-1$
    		}
            status = new Status(IStatus.OK,DqpUiConstants.PLUGIN_ID,IStatus.OK,message,null);
    	}
		allJarAndStatusMap.put(jarName, status);

		String msg = getString("jarPathSuccess.msg",jarName); //$NON-NLS-1$
        return new Status(IStatus.OK,DqpUiConstants.PLUGIN_ID,IStatus.OK,msg,null);
    }

    public String getRequiredJarPath( String jarName ) throws IllegalArgumentException {
    	if(jarName==null || jarName.trim().length()==0) {
        	throw new IllegalArgumentException();
    	}
    	if(!this.requiredExtJars.contains(jarName)) {
    		throw new IllegalArgumentException();
    	}
    	return this.allJarAndPathMap.get(jarName);
    }

   	/**
   	 * Set the Extension jar path to the default
     * @param jarName the name of the jar
     * @return the status
     * @since 5.5.3
     */
    public IStatus setRequiredJarPathToDefault( String jarName) {
    	if(jarName==null || jarName.trim().length()==0) {
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("emptyJarNameError.msg"),null); //$NON-NLS-1$
    	}
    	if(!this.requiredExtJars.contains(jarName)) {
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("jarNotRequiredError.msg"),null); //$NON-NLS-1$
    	}

    	this.setJarToDefaultPathAndStatus(jarName);

		String msg = getString("jarPathSuccess.msg",jarName); //$NON-NLS-1$
        return new Status(IStatus.OK,DqpUiConstants.PLUGIN_ID,IStatus.OK,msg,null);
    }

    /**
     * Specify that the existing jar for the supplied jarName should be used, rather than overriden.
     * @param jarName the name of the jar that will use the version currently in the configuration
     * @throws IllegalArgumentException if a jar with the specified name does not exist in the current configuration
     * @since 5.5.3
     */
    public IStatus setUseExistingJar( String jarName ) {
    	return this.setRequiredJarPath(jarName, ConnectorImportHelper.USE_WSCONFIG_JAR);
    }

    // ===========================================================================================================================
    // Private Methods
    // ===========================================================================================================================

    /**
     * Load all available extension jars, based on the type of connectors file being imported.  For a caf file,
     * the ext jars are contained within the caf itself.  For a cdk file, look in the directory location of the
     * cdk file and grab any available jars (based on required jars list)
     * @return the status of the operation.
     * @since 5.5.3
     */
    private IStatus loadAllImportFileExtJars() {
    	this.allImportFileExtJars = new ArrayList<File>();
    	this.allImportCAFExtJars = new ArrayList<ExtensionModule>();

		// Get the available Extension Modules
        try {
        	// CDK File
        	if(this.connectorFileType==CDK_FILE) {
        		// Get the contents of the connector file directory
        		final File[] extensionContents = this.connectorFile.getParentFile().listFiles();

        		// Locate any jars in the cdk directory and make them available.
                if ( extensionContents != null ) {
                    for ( int i=0 ; i<extensionContents.length ; ++i ) {
                        // see if the file in the installation folder already exists in the state location
                        if ( extensionContents[i].isFile() ) {
                            // Determine .cdk or .caf
                            if ( extensionContents[i].getName().toLowerCase().endsWith(JAR_EXT) ) {
                            	allImportFileExtJars.add(extensionContents[i]);
                            }
                        }
                    }
                }

        	// CAF File
        	} else if(this.connectorFileType==CAF_FILE) {
                // Open the caf file
                FileInputStream stream = null;
        		try {
        			stream = new FileInputStream(this.connectorFile);
        		} catch (FileNotFoundException e) {
                	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("importFileNotFound.msg"),e); //$NON-NLS-1$
        		}
        		// instantiate the import export utilities
                XMLConfigurationImportExportUtility util = new XMLConfigurationImportExportUtility();

                ConnectorArchive archive = util.importConnectorArchive(stream, new BasicConfigurationObjectEditor());
                ConnectorBindingType[] types = archive.getConnectorTypes();
                for(int iCt=0; iCt<types.length; iCt++) {
                    ExtensionModule[] modules = archive.getExtensionModules(types[iCt]);
                    for(int i=0; i<modules.length; i++) {
                        if(!this.allImportCAFExtJars.contains(modules[i])) {
                        	allImportCAFExtJars.add(modules[i]);
                        }
                    }
                }
        	}
		} catch (InvalidConfigurationElementException e) {
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("importFileInvalidConfigError.msg"),e); //$NON-NLS-1$
		} catch (IOException e) {
        	return new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,getString("importFileIOError.msg"),e); //$NON-NLS-1$
		}

		// Type Import was successful
        return new Status(IStatus.OK,DqpUiConstants.PLUGIN_ID,IStatus.OK,getString("importFileAvailable.msg"),null); //$NON-NLS-1$
    }

    /**
     * Initialize the import file - specific variables
     * @since 5.5.3
     */
    private void initHelper() {
    	this.connectorFile = null;
    	this.connectorFileType = UNKNOWN_FILE;

		this.allImportFileExtJars = Collections.emptyList();
		this.allImportCAFExtJars = Collections.emptyList();
		this.requiredExtJars = Collections.emptyList();

		this.allJarAndPathMap = Collections.emptyMap();
		this.allJarAndStatusMap = Collections.emptyMap();
    }

    /**
     * Load the collection of existing extension jar names that are already available
     * in the workspace configuration.
     * @since 5.5.3
     */
    private void loadExistingWorkpaceJarNames() {
    	this.existingWorkpaceExtJarNames = new ArrayList<String>();

    	// Add all Connector jarNames
    	Collection<String> allConnJarNames = DqpPlugin.getInstance().getExtensionsHandler().getAllConnectorJars();
    	this.existingWorkpaceExtJarNames.addAll(allConnJarNames);

    	// Add all Udf jarNames
    	Collection<String> allUDFJarNames = DqpPlugin.getInstance().getExtensionsHandler().getUdfJarNames();
    	this.existingWorkpaceExtJarNames.addAll(allUDFJarNames);
    }

    /**
     * Get all the available jarNames from the importFile.
     * @return the list of all ext jarNames available from the import file selection.
     * @since 5.5.3
     */
    public Collection<String> getAllImportFileJarNames () {
    	Collection<String> importJarNames = new ArrayList<String>();

    	// The available imported jars different, depending on caf or cdk
    	if(this.connectorFileType==CDK_FILE) {
    		for(Iterator<File> iter = this.allImportFileExtJars.iterator(); iter.hasNext();) {
    			File theFile = iter.next();
    			importJarNames.add(theFile.getName());
    		}
    	} else if(this.connectorFileType==CAF_FILE) {
    		for(Iterator<ExtensionModule> iter = this.allImportCAFExtJars.iterator(); iter.hasNext();) {
    			ExtensionModule module = iter.next();
    			importJarNames.add(module.getFullName());
    		}
    	}
    	return importJarNames;
    }

    /**
     * Get all the existing ext jarNames in the workspace config.
     * @return the list of all ext jarNames available in the workspace config.
     * @since 5.5.3
     */
    public Collection<String> getAllWorkspaceConfigJarNames () {
    	return existingWorkpaceExtJarNames;
    }

    /**
     * Get path to specify for the supplied import file jarName.
     * @return the path for the specified jarName.
     * @since 5.5.3
     */
    private String getPathForImportFileJar (String jarName) {
    	String path = null;

    	// The available imported jars different, depending on caf or cdk
    	if(this.connectorFileType==CDK_FILE) {
    		for(Iterator<File> iter = this.allImportFileExtJars.iterator(); iter.hasNext();) {
    			File theFile = iter.next();
    			if(theFile.getName().equalsIgnoreCase(jarName)) {
    				path = theFile.getAbsolutePath();
    				// Trim off the fileName
    				path = path.substring(0,path.lastIndexOf(jarName));
    				break;
    			}
    		}
    	} else if(this.connectorFileType==CAF_FILE) {
    		for(Iterator<ExtensionModule> iter = this.allImportCAFExtJars.iterator(); iter.hasNext();) {
    			ExtensionModule module = iter.next();
    			if(module.getFullName().equalsIgnoreCase(jarName)) {
    				path = ConnectorImportHelper.USE_CAF_JAR;
    				break;
    			}
    		}
    	}
    	return path;
    }

    /**
     * Get the list of all possible jarNames, combination of the workspace config jars
     * and the import jars
     * @return the collection of all possible jarNames
     * @since 5.5.3
     */
    private Collection<String> getAllPossibleJarNames() {
    	Collection<String> allPossible = new ArrayList<String>();
    	for(Iterator<String> jIter = this.getAllImportFileJarNames().iterator(); jIter.hasNext();) {
    		String jName = jIter.next();
    		if(!allPossible.contains(jName)) {
    			allPossible.add(jName);
    		}
    	}
    	for(Iterator<String> jIter = this.getAllWorkspaceConfigJarNames().iterator(); jIter.hasNext();) {
    		String jName = jIter.next();
    		if(!allPossible.contains(jName)) {
    			allPossible.add(jName);
    		}
    	}
    	return allPossible;
    }

    /**
     * Initialize all possible jar default path and status
     * whether the jar was found in the import and whether it already exists in the workspace
     * configuration
     * @param jarName the required jarName
     * @since 5.5.3
     */
    private void initAllJarDefaultPathAndStatus() {
    	this.allJarAndPathMap = new HashMap<String, String>();
    	this.allJarAndStatusMap = new HashMap<String, IStatus>();
    	for(Iterator<String> jIter = this.getAllPossibleJarNames().iterator(); jIter.hasNext();) {
    		String jarName = jIter.next();
    		setJarToDefaultPathAndStatus(jarName);
    	}
    }

    /**
     * Set the default path and status for the required jar.  The defaults are based upon
     * whether the jar was found in the import and whether it already exists in the workspace
     * configuration
     * @param jarName the required jarName
     * @since 5.5.3
     */
    private void setJarToDefaultPathAndStatus(String jarName) {
    	// Check the incoming jarName
    	if(jarName==null || jarName.trim().length()==0) {
    		throw new IllegalArgumentException(getString("emptyJarNameError.msg")); //$NON-NLS-1$
    	}

    	// Set the required jar status, based on whether it already exists in workspace
        Collection<String> importFileJarNames = getAllImportFileJarNames();
        Collection<String> workspaceConfigJarNames = getAllWorkspaceConfigJarNames();

        // ---------------------------------------------------
    	// Set the default path and status
        // ---------------------------------------------------

        // Jar not found in import
        if(!importFileJarNames.contains(jarName)) {
        	// Jar does not exist in workspace config, default path is null.
        	if(!workspaceConfigJarNames.contains(jarName)) {
        		allJarAndPathMap.put(jarName,null);
        		// connector_patch.jar is allowed to not be present
        		IStatus status = null;
        		if(!DqpExtensionsHandler.CONNECTOR_PATCH_JAR.equalsIgnoreCase(jarName)) {
        			String msg = getString("reqdJarPathNotSet.msg",jarName); //$NON-NLS-1$
                    status = new Status(IStatus.ERROR,DqpUiConstants.PLUGIN_ID,IStatus.ERROR,msg,null);
        		} else {
                    status = new Status(IStatus.OK,DqpUiConstants.PLUGIN_ID,IStatus.OK,getString("connPatchJarPathNotSet.msg"),null); //$NON-NLS-1$
        		}
        		allJarAndStatusMap.put(jarName, status);
        	// Jar exists in workspace config, default path is 'useWSJar'
        	} else {
        		allJarAndPathMap.put(jarName,ConnectorImportHelper.USE_WSCONFIG_JAR);
        		String msg = getString("reqdJarPathUseWSConfig.msg",jarName); //$NON-NLS-1$
                IStatus status = new Status(IStatus.OK,DqpUiConstants.PLUGIN_ID,IStatus.OK,msg,null);
                allJarAndStatusMap.put(jarName, status);
        	}
        // Jar is found in import
        } else {
        	// No conflicting jars in workspaceConfig
        	if(!workspaceConfigJarNames.contains(jarName)) {
        		String importJarPath = getPathForImportFileJar(jarName);
        		String message = null;
        		if(ConnectorImportHelper.USE_CAF_JAR.equals(importJarPath)) {
        			message = getString("reqdJarImportCAFJarUsed.msg",jarName); //$NON-NLS-1$
        		} else {
        			message = getString("reqdJarImportCDKJarUsed.msg",jarName); //$NON-NLS-1$
        		}
        		allJarAndPathMap.put(jarName, importJarPath);
                IStatus status = new Status(IStatus.OK,DqpUiConstants.PLUGIN_ID,IStatus.OK,message,null);
        		allJarAndStatusMap.put(jarName, status);
        	// There is an existing workspace config jar
        	} else {
        		// CAF import - default is to use the caf jar
        		if(this.connectorFileType==CAF_FILE) {
        			allJarAndPathMap.put(jarName, ConnectorImportHelper.USE_CAF_JAR);
        			String msg = getString("reqdJarImportCAFJarUsedWSConflict.msg",jarName); //$NON-NLS-1$
                    IStatus status = new Status(IStatus.WARNING,DqpUiConstants.PLUGIN_ID,IStatus.WARNING,msg,null);
        			allJarAndStatusMap.put(jarName, status);
        		// CDK import - default is to use the workspace config jar.
        		} else if(this.connectorFileType==CDK_FILE){
        			allJarAndPathMap.put(jarName, ConnectorImportHelper.USE_WSCONFIG_JAR);
        			String msg = getString("reqdJarImportCDKJarExistsWSConflict.msg",jarName); //$NON-NLS-1$
                    IStatus status = new Status(IStatus.WARNING,DqpUiConstants.PLUGIN_ID,IStatus.WARNING,msg,null);
        			allJarAndStatusMap.put(jarName, status);
        		}
        	}
        }
    }

}
