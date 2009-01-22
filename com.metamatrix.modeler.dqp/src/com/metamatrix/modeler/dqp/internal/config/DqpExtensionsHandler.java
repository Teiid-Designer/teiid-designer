/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ExtensionModule;
import com.metamatrix.common.config.api.ConnectorBindingType.Attributes;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.config.ExtensionModuleChangeEvent;
import com.metamatrix.modeler.dqp.config.IExtensionModuleChangeListener;

/**
 * @since 4.3
 */
public class DqpExtensionsHandler /* extends DqpConfigHandler */{

	/**
	 * The name of the placeholder patch jar used in a connector classpath. A
	 * jar of this name does not have to exist.
	 * 
	 * @since 5.5.3
	 */
	public static final String CONNECTOR_PATCH_JAR = "connector_patch.jar"; //$NON-NLS-1$

	private List<IExtensionModuleChangeListener> listeners;

	private IPath dqpExtensionsFolderPath;

	private UdfJarMapperManager udfJarMapperManager;

	public DqpExtensionsHandler() {
		this.listeners = new ArrayList<IExtensionModuleChangeListener>();
		this.dqpExtensionsFolderPath = DqpPath.getRuntimeExtensionsPath();
		this.udfJarMapperManager = new UdfJarMapperManager(this.dqpExtensionsFolderPath, this);
		this.udfJarMapperManager.load();
	}

	/**
	 * NOTE: This method is for testing purposes only !!!!
	 */
	protected UdfJarMapperManager getUdfJarMapperManager() {
		return this.udfJarMapperManager;
	}

	/**
	 * Copies the specified file to the extensions folder. If an existing
	 * extension module with that name exists it is overwritten.
	 * 
	 * @param source
	 *            the source who is adding the jar
	 * @param jarFile
	 *            the jar being added
	 * @return <code>true</code> if jar was added successfully
	 */
	public boolean addConnectorJar(Object source, File jarFile) {
		// assert it is a jar file here
		boolean success = true;
		boolean changed = false;

		try {
			copyFileToExtensionsFolder(jarFile);
			changed = true;
		} catch (Exception exception) {
			success = false;
			String msg = DqpPlugin.Util.getString(I18nUtil
					.getPropertyPrefix(DqpExtensionsHandler.class)
					+ "problemAddingExtensionModule", jarFile.getName()); //$NON-NLS-1$
			DqpPlugin.Util.log(IStatus.ERROR, msg);
		}

		if (changed) {
			ExtensionModuleChangeEvent event = new ExtensionModuleChangeEvent(
					source, ExtensionModuleChangeEvent.Type.ADDED_FROM_CAF,
					jarFile.getName());
			fireExtensionModuleChangeEvent(event);
		}

		return success;
	}
	
	private void copyFileToExtensionsFolder(File file) throws Exception {
		FileUtils.copyFile(file.getParent(), this.dqpExtensionsFolderPath.toOSString(), file.getName());
	}

	/**
	 * Adds the specified jar files. If one or more jar files are added a
	 * {@link ExtensionModuleChangeEvent} is fired.
	 * 
	 * @param source
	 *            the source of the delete
	 * @param jarFiles
	 *            the jar files being added
	 * @return <code>true</code> if <strong>ALL</strong> jars were successfully
	 *         added
	 * @since 5.5.3
	 */
	public boolean addUdfJars(Object source, File[] jarFiles) {
		boolean success = true;
		boolean changed = false;
		Collection<File> affectedObjects = new ArrayList<File>(jarFiles.length);
		List<String> jarNames = new ArrayList<String>();

		for (File file : jarFiles) {
			try {
				copyFileToExtensionsFolder(file);
				changed = true;
				jarNames.add(file.getName());
				affectedObjects.add(file);
			} catch (Exception exception) {
				success = false;
				String msg = DqpPlugin.Util.getString(I18nUtil
						.getPropertyPrefix(DqpExtensionsHandler.class)
						+ "problemAddingExtensionModule", file.getName()); //$NON-NLS-1$
				DqpPlugin.Util.log(IStatus.ERROR, msg);
			}
		}

		if (changed) {
			this.udfJarMapperManager.addJars(jarNames);

			ExtensionModuleChangeEvent event = new ExtensionModuleChangeEvent(
					source, ExtensionModuleChangeEvent.Type.ADDED_FOR_UDF,
					affectedObjects);
			fireExtensionModuleChangeEvent(event);
		}

		return success;
	}

	/**
	 * Adds the specified jar files. If one or more jar files are added a
	 * {@link ExtensionModuleChangeEvent} is fired.
	 * 
	 * @param source
	 *            the source of the delete
	 * @param jarFiles
	 *            the jar files being added
	 * @return <code>true</code> if <strong>ALL</strong> jars were successfully
	 *         added
	 * @since 5.5.3
	 */
	public boolean addConnectorJars(Object source, ExtensionModule[] extModules) {
		boolean success = true;
		boolean changed = false;
		Collection<String> affectedObjects = new ArrayList<String>(
				extModules.length);

		for (ExtensionModule extModule : extModules) {
			try {
				writeCAFJarFileToExtensionFolder(extModule);
				changed = true;
				affectedObjects.add(extModule.getFullName());
			} catch (IOException exception) {
				success = false;
				String msg = DqpPlugin.Util
						.getString(
								I18nUtil
										.getPropertyPrefix(DqpExtensionsHandler.class)
										+ "problemAddingExtensionModule", extModule.getFullName()); //$NON-NLS-1$
				DqpPlugin.Util.log(IStatus.ERROR, msg);
			}
		}

		if (changed) {
			ExtensionModuleChangeEvent event = new ExtensionModuleChangeEvent(
					source, ExtensionModuleChangeEvent.Type.ADDED_FROM_CAF,
					affectedObjects);
			fireExtensionModuleChangeEvent(event);
		}

		return success;
	}

    private void writeCAFJarFileToExtensionFolder(ExtensionModule extModules)throws IOException {
        IPath jarPath = this.dqpExtensionsFolderPath.append(extModules.getFullName());
        FileOutputStream out = new FileOutputStream(jarPath.toFile());
        out.write(extModules.getFileContents()) ;
        out.close();
    }

    /**
	 * Deletes the specified jar files. If one or more jar files are deleted
	 * {@link ExtensionModuleChangeEvent} is fired.
	 * 
	 * @param source
	 *            the source of the delete
	 * @param jarFiles
	 *            the jar files being deleted
	 * @return <code>true</code> if <strong>ALL</strong> jars were successfully
	 *         deleted
	 * @since 5.5.3
	 */
	public boolean deleteUdfJarFiles(Object source, File[] jarFiles) {
		boolean success = true;
		boolean changed = false;
		Collection<File> affectedObjects = new ArrayList<File>(jarFiles.length);
		List<String> jarNames = new ArrayList<String>();

		for (File file : jarFiles) {
			boolean isConnJar = isConnectorJar(file);

			boolean deleted = false;

			if (!isConnJar && file.exists()) {
				deleted = file.delete();
			}

			if (deleted || isConnJar) {
				changed = true;
				jarNames.add(file.getName());
				affectedObjects.add(file);
			} else {
				success = false;
				String msg = DqpPlugin.Util.getString(I18nUtil
						.getPropertyPrefix(DqpExtensionsHandler.class)
						+ "problemDeletingExtensionModule", file.getName()); //$NON-NLS-1$
				DqpPlugin.Util.log(IStatus.ERROR, msg);
			}
		}

		if (changed) {
			this.udfJarMapperManager.removeJars(jarNames);
			if (!affectedObjects.isEmpty()) {
				ExtensionModuleChangeEvent event = new ExtensionModuleChangeEvent(
						source,
						ExtensionModuleChangeEvent.Type.DELETED_FROM_UDF,
						affectedObjects);
				fireExtensionModuleChangeEvent(event);
			}
		}

		return success;
	}

	/**
	 * Deletes the specified jar files. If one or more jar files are deleted
	 * {@link ExtensionModuleChangeEvent} is fired.
	 * 
	 * @param source
	 *            the source of the delete
	 * @param jarFiles
	 *            the jar files being deleted
	 * @return <code>true</code> if <strong>ALL</strong> jars were successfully
	 *         deleted
	 * @since 5.5.3
	 */
	public boolean deleteConnectorJars(Object source, File[] jarFiles) {
		boolean success = true;
		boolean changed = false;
		Collection<File> affectedObjects = new ArrayList<File>(jarFiles.length);

		for (File file : jarFiles) {
			boolean isUdfJar = isUdfJar(file);
			boolean deleted = false;

			if (!isUdfJar) {
				deleted = file.delete();
			}

			if (deleted) {
				changed = true;
				affectedObjects.add(file);
			} else if (!isUdfJar) {
				success = false;
				String msg = DqpPlugin.Util.getString(I18nUtil
						.getPropertyPrefix(DqpExtensionsHandler.class)
						+ "problemDeletingExtensionModule", file.getName()); //$NON-NLS-1$
				DqpPlugin.Util.log(IStatus.ERROR, msg);
			}
		}

		if (changed) {
			ExtensionModuleChangeEvent event = new ExtensionModuleChangeEvent(
					source, ExtensionModuleChangeEvent.Type.DELETED,
					affectedObjects);
			fireExtensionModuleChangeEvent(event);
		}

		return success;
	}

	public boolean isUdfJar(File jarFile) {
		return isUdfJar(jarFile.getName());
	}

	public boolean isUdfJar(String jarFileName) {
		return this.udfJarMapperManager.isUdfJar(jarFileName);
	}

	public boolean isConnectorJar(File jarFile) {
		return isConnectorJar(jarFile.getName());
	}

	public boolean isConnectorJar(String jarFileName) {
		return getAllConnectorJars().contains(jarFileName);
	}

	public List<String> getAllConnectorJars() {
		HashSet<String> theJars = new HashSet<String>();

		List<String> bindingJars = getConnectorBindingJars();
		List<String> typeJars = getConnectorTypeJars();
		theJars.addAll(bindingJars);
		theJars.addAll(typeJars);

		return new ArrayList<String>(theJars);
	}

	public List<String> getConnectorTypeJars() {
		Collection<ComponentType> types = getConfigurationManager().getConnectorTypes();

		List<String> theJars = new ArrayList<String>();

		for (ComponentType type : types) {
			String[] jarNames = getConnectorTypeExtensionModules(type);

			for (String jarName : jarNames) {
				if (!theJars.contains(jarName)) {
					// connector_patch.jar not required, only add it if it
					// really exists in extensions
					if (CONNECTOR_PATCH_JAR.equals(jarName)
							&& !this.extensionModuleExists(CONNECTOR_PATCH_JAR)) {
						continue;
					}
					theJars.add(jarName);
				}
			}
		}

		return theJars;
	}

	/**
	 * Get list of extension modules needed by this connector type.
	 * 
	 * @see com.metamatrix.common.config.api.ConnectorBindingType#getExtensionModules()
	 * @since 4.3.2
	 */
	public String[] getConnectorTypeExtensionModules(ComponentType type) {
		ArrayList<String> modules = new ArrayList<String>();
		String classPath = type.getDefaultValue(Attributes.CONNECTOR_CLASSPATH);
		if (classPath != null) {
			StringTokenizer st = new StringTokenizer(classPath, ";"); //$NON-NLS-1$
			while (st.hasMoreTokens()) {
				String path = st.nextToken();
				int idx = path.indexOf(Attributes.MM_JAR_PROTOCOL);
				if (idx != -1) {
					String jarFile = path.substring(idx
							+ Attributes.MM_JAR_PROTOCOL.length() + 1);
					modules.add(jarFile);
				}
			}
		}
		return modules.toArray(new String[modules.size()]);
	}

	/**
	 * Get list of extension modules needed by this connector type.
	 * 
	 * @see com.metamatrix.common.config.api.ConnectorBindingType#getExtensionModules()
	 * @since 4.3.2
	 */
	public String[] getConnectorBindingExtensionModules(ConnectorBinding binding) {
		ArrayList<String> modules = new ArrayList<String>();
		String classPath = binding.getProperty(Attributes.CONNECTOR_CLASSPATH);
		if (classPath != null) {
			StringTokenizer st = new StringTokenizer(classPath, ";"); //$NON-NLS-1$
			while (st.hasMoreTokens()) {
				String path = st.nextToken();
				int idx = path.indexOf(Attributes.MM_JAR_PROTOCOL);
				if (idx != -1) {
					String jarFile = path.substring(idx 
							+ Attributes.MM_JAR_PROTOCOL.length() + 1);
					modules.add(jarFile);
				}
			}
		}
		return modules.toArray(new String[modules.size()]);
	}

	public List<String> getConnectorBindingJars() {
		Collection<ConnectorBinding> bindings = getConfigurationManager().getConnectorBindings();

		List<String> theJars = new ArrayList<String>();

		for (ConnectorBinding binding : bindings) {

			String[] jarNames = getConnectorBindingExtensionModules(binding);

			for (String jarName : jarNames) {
				if (!theJars.contains(jarName)) {
					// connector_patch.jar not required, only add it if it
					// really exists in extensions
					if (CONNECTOR_PATCH_JAR.equals(jarName)
							&& !this.extensionModuleExists(CONNECTOR_PATCH_JAR)) {
						continue;
					}
					theJars.add(jarName);
				}
			}
		}

		return theJars;
	}
	
	private ConfigurationManager getConfigurationManager() {
		return DqpPlugin.getInstance().getConfigurationManager();
	}

	public List<File> getUdfJarFiles() {
		return this.udfJarMapperManager.getUdfJarFiles();
	}

	public List<String> getUdfJarNames() {
		List<String> udfJarNames = new ArrayList<String>(5);

		Collection<File> files = getUdfJarFiles();
		for (File file : files) {
			String fName = file.getName();
			if (!udfJarNames.contains(fName)) {
				udfJarNames.add(fName);
			}
		}
		return udfJarNames;
	}

    public List<File> getConnectorJarFiles() {
		File extDir = this.dqpExtensionsFolderPath.toFile();

		if (extDir.exists() && extDir.isDirectory()) {
			File[] extDirContents = extDir.listFiles();

			if (extDirContents != null) {
			    return Arrays.asList(extDirContents);
			}
		}

		return new ArrayList<File>(0);
    }

	/**
	 * @param moduleName
	 *            the name of the extension module being checked
	 * @return <code>true</code> if an extension module with the specified name
	 *         exists in the configuration
	 * @since 5.5.3
	 */
	public boolean extensionModuleExists(String moduleName) {
		IPath extensionModule = this.dqpExtensionsFolderPath.append(moduleName);
		return extensionModule.toFile().exists();
	}

	public IPath getDqpExtensionsFolderPath() {
		return this.dqpExtensionsFolderPath;
	}

	/**
	 * Copies the extension module with the specified name to the destination
	 * directory. If the file exists it will be overwritten.
	 * 
	 * @param moduleName
	 *            the name of the extension modules to copy
	 * @param destinationDirectory
	 *            the location to copy the file
	 * @throws Exception if there is a problem doing the copy
	 * @throws IllegalArgumentException
	 *             if an extension module does not exist to copy
	 * @throws IllegalArgumentException
	 *             if the destination directory does not exist
	 */
	public void copyExtensionModule(String moduleName,
			String destinationDirectory) throws Exception {
		copyExtensionModules(Collections.singletonList(moduleName), destinationDirectory);
	}

	/**
	 * Copies the extension modules with the specified names to the destination
	 * directory. If a file exists it will be overwritten.
	 * 
	 * @param moduleNames
	 *            the names of the extension modules to be copied
	 * @param destinationDirectory
	 *            the location to copy the files
	 * @throws IllegalArgumentException
	 *             if the destination directory does not exist
	 * @throws Exception if there is a problem doing the copy
	 */
	public void copyExtensionModules(Collection<String> moduleNames,
			String destinationDirectory) throws Exception {
		if (!new File(destinationDirectory).exists()) {
			String msg = DqpPlugin.Util.getString(I18nUtil
					.getPropertyPrefix(DqpExtensionsHandler.class)
					+ "missingDestinationDirectory", destinationDirectory); //$NON-NLS-1$
			DqpPlugin.Util.log(IStatus.ERROR, msg);
			throw new IllegalArgumentException(msg);
		}

		for (String moduleName : moduleNames) {
			IPath extensionModule = this.dqpExtensionsFolderPath
					.append(moduleName);

			// make sure an extension module with that name exists
			if (!extensionModule.toFile().exists()) {
				String msg = DqpPlugin.Util.getString(I18nUtil
						.getPropertyPrefix(DqpExtensionsHandler.class)
						+ "missingExtensionsModule", moduleName); //$NON-NLS-1$
				DqpPlugin.Util.log(IStatus.ERROR, msg);
				throw new IllegalArgumentException(msg);
			}

			// do the copy
			FileUtils.copyFile(this.dqpExtensionsFolderPath.toOSString(), destinationDirectory, moduleName);
		}
	}

	/**
	 * Registers the specified listener to receive a notification when the
	 * extension module folder changes.
	 * 
	 * @param listener
	 *            the listener being registered
	 * @since 5.5.3
	 */
	public void addChangeListener(IExtensionModuleChangeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Unregisters the specified listener so that they no longer receive a
	 * notification when the extension module folder changes.
	 * 
	 * @param listener
	 *            the listener being unregistered
	 * @since 5.5.3
	 */
	public void removeChangeListener(IExtensionModuleChangeListener listener) {
		this.listeners.remove(listener);
	}

	private void fireExtensionModuleChangeEvent(ExtensionModuleChangeEvent event) {
		for (IExtensionModuleChangeListener listener : this.listeners) {
			listener.extensionModulesChanged(event);
		}
	}
}
