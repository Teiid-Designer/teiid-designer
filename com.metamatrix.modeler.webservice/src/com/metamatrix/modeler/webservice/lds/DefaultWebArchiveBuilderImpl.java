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

package com.metamatrix.modeler.webservice.lds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.validator.UrlValidator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import com.metamatrix.common.xml.XmlUtil;
import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.modeler.webservice.util.AntTasks;

/**
 * This is the default implementation of a WebArchiveBuilder.
 * 
 * @since 4.4
 */
public class DefaultWebArchiveBuilderImpl implements WebArchiveBuilder {

    // =============================================================
    // Constants
    // =============================================================
    private static final String I18N_PREFIX = "WebArchiveBuilder."; //$NON-NLS-1$
    private static final String TASK_CREATE_DIRECTORIES = getString("taskCreateDirectory"); //$NON-NLS-1$
    private static final String TASK_COPYING_FILES = getString("taskCopyingFiles"); //$NON-NLS-1$
    private static final String TASK_CREATING_WAR_ARCHIVE = getString("taskCreatingWarArchive"); //$NON-NLS-1$
    private static final String TASK_COPYING_WAR_FILE = getString("taskCopyingWarFile"); //$NON-NLS-1$
    private static final String TASK_CREATING_EMBEDDED_JAR_FILE = getString("taskCreatingEmbeddedJar"); //$NON-NLS-1$
    private static final String TASK_CLEANUP = getString("taskCleanup"); //$NON-NLS-1$

    /**
     * This constructor is package protected, so that only the factory can call it.
     * 
     * @since 4.4
     */
    public DefaultWebArchiveBuilderImpl() {
    }

    /**
     * @see com.metamatrix.modeler.dataservices.lds.WebArchiveBuilder#validateContextName(java.lang.String)
     * @since 4.4
     */
    public IStatus validateContextName( String contextName ) {

        try {
            final String URL_BASE = "http://www.metamatrix.com/"; //$NON-NLS-1$       
            final UrlValidator urlValidator = new UrlValidator();

            // Check for invalid characters
            String[] invalidChars = new String[] {"/" //$NON-NLS-1$
                , "\\" //$NON-NLS-1$
                , StringUtil.Constants.SPACE};
            final String invalidChar = validateInvalidCharactersInContextName(contextName, invalidChars);

            // Perform validation
            if (invalidChar != null) {

                String msg = null;
                if (StringUtil.Constants.SPACE.equals(invalidChar)) {
                    msg = getString("ContextNameValidationFailed_InvalidSpace"); //$NON-NLS-1$
                } else {
                    final Object[] params = new Object[] {invalidChar};
                    msg = getString("ContextNameValidationFailed_InvalidCharacter", params); //$NON-NLS-1$
                }

                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID,
                                  WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, msg, null);
            } else if (StringUtil.Constants.EMPTY_STRING.equals(contextName)) {

                final String msg = getString("ContextNameValidationFailed_Empty"); //$NON-NLS-1$        
                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID,
                                  WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, msg, null);
            } else if (!urlValidator.isValid(URL_BASE + contextName)) {

                final String msg = getString("ContextNameValidationFailed"); //$NON-NLS-1$        
                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID,
                                  WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, msg, null);
            } else {

                final String msg = getString("ContextNameValidationSucceeded"); //$NON-NLS-1$        
                return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID,
                                  WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_SUCCEEDED, msg, null);
            }
        } catch (Exception e) {

            final String msg = getString("ContextNameValidationFailed"); //$NON-NLS-1$        
            return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID,
                              WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, msg, e);
        }
    }

    /**
     * @see com.metamatrix.modeler.dataservices.lds.WebArchiveBuilder#createWebArchive(java.io.InputStream, java.util.Map)
     * @since 4.4
     */
    public IStatus createWebArchive( Map properties,
                                     IProgressMonitor monitor ) {

        try {

            // Get and valdiate the context name.
            final String contextName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
            IStatus status = validateContextName(contextName);

            if (IStatus.ERROR == status.getSeverity()) {

                throw new Exception(status.getException());
            }

            // Get the build directory and create it if it doesn't already exist.
            final String webServicePluginPath = PluginUtilities.getPluginProjectLocation("com.metamatrix.modeler.webservice", true); //$NON-NLS-1$
            final String buildDirectoryName = webServicePluginPath + File.separator + WebArchiveBuilderConstants.BUILD_DIR;
            File buildDirectory = new File(buildDirectoryName);
            buildDirectory.mkdir();

            monitor.subTask(TASK_CREATE_DIRECTORIES);
            // Get the work directory, create it (in case it is not there) and try to empty it.
            final String workDirectoryName = buildDirectoryName + File.separator + WebArchiveBuilderConstants.WORK_DIR;
            File workDirectory = new File(workDirectoryName);
            workDirectory.mkdir();
            FileUtils.removeChildrenRecursively(workDirectory);
            // Create the temporary directory.
            final TempDirectory tempDir = TempDirectory.getTempDirectory(workDirectoryName);
            final String tempDirectoryName = tempDir.getPath();
            // Create context directory.
            final String contextDirectoryName = tempDirectoryName + File.separator + contextName;
            final File contextDirectory = new File(contextDirectoryName);
            contextDirectory.mkdir();
            // Create the images directory.
            final String imagesDirectoryName = contextDirectoryName + File.separator + "images"; //$NON-NLS-1$
            final File imagesDirectory = new File(imagesDirectoryName);
            imagesDirectory.mkdir();
            // Create the WEB-INF directory.
            final String webInfDirectoryName = contextDirectoryName + File.separator + "WEB-INF"; //$NON-NLS-1$
            final File webInfDirectory = new File(webInfDirectoryName);
            webInfDirectory.mkdir();
            // Create the lib directory.
            final String webInfLibDirectoryName = webInfDirectoryName + File.separator + "lib"; //$NON-NLS-1$
            final File webInfLibDirectory = new File(webInfLibDirectoryName);
            // Create the lib directory.
            final String webInfClassesDirectoryName = webInfDirectoryName + File.separator + "classes"; //$NON-NLS-1$
            final File webInfClassesDirectory = new File(webInfClassesDirectoryName);
            webInfClassesDirectory.mkdir();
            webInfLibDirectory.mkdir();
            monitor.worked(10);

            monitor.subTask(TASK_COPYING_FILES);
            // Copy the Web files.
            getWebFiles(contextDirectory, imagesDirectory, webInfDirectory);
            // Copy these files into the WEB-INF/lib
            getLibFiles(webInfLibDirectory);
            // Replace the variables in the web.xml file.
            replaceVariables(webInfDirectoryName, properties, contextName);
            // Get the external files.
            getExternalFiles(webInfLibDirectoryName);
            copyLicenseFilesToClassesDirectory(webInfClassesDirectoryName, properties);
            monitor.worked(10);

            // Create JAR file containing the MMQuery (Embedded) files.
            monitor.subTask(TASK_CREATING_EMBEDDED_JAR_FILE);
            createEmbeddedJar(buildDirectoryName, tempDirectoryName, properties, monitor);
            final String embeddedJarFileName = File.separator + WebArchiveBuilderConstants.EMBEDDED_JAR_FILE_NAME;
            FileUtils.copyFile(tempDirectoryName, webInfLibDirectoryName, embeddedJarFileName);
            monitor.worked(5);

            monitor.subTask(TASK_CREATING_WAR_ARCHIVE);
            // ZIP everything in the context directory into the new WAR file.
            final String warFileName = tempDirectoryName + File.separator + contextName + ".war"; //$NON-NLS-1$
            AntTasks.zip(contextDirectoryName, warFileName);
            monitor.worked(20);

            // Get the target directory. If it doesnt exist, create it.
            final String newWarFileDir = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_FILE_SAVE_LOCATION);
            File newWarDir = new File(newWarFileDir);
            if (!newWarDir.exists()) {
                newWarDir.mkdir();
            }

            monitor.subTask(TASK_COPYING_WAR_FILE);
            // Move the temporary WAR file to its destination.
            final File warFile = new File(warFileName);
            final String newWarFileName = getFileName((String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_FILE_SAVE_LOCATION),
                                                      contextName + ".war"); //$NON-NLS-1$
            File newWarFile = new File(newWarFileName);
            if (newWarFile.exists()) {
                if (!newWarFile.delete()) {
                    String msg = getString("WebArchiveCreationFailed_CouldNotDeleteExistingWARFile"); //$NON-NLS-1$
                    throw new Exception(msg);
                }
            }
            if (!warFile.renameTo(newWarFile)) {
                String msg = getString("WebArchiveCreationFailed_CouldNotSaveWARFile"); //$NON-NLS-1$
                throw new Exception(msg);
            }
            // Delete the temp directory.
            monitor.subTask(TASK_CLEANUP);
            tempDir.remove();
            monitor.worked(5);
        } catch (Exception e) {

            final String msg = getString("WebArchiveCreationFailed"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID,
                                              WebArchiveBuilderConstants.STATUS_CODE_WAR_FILE_CREATION_FAILED, msg, e);

            // Log the error.
            WebServicePlugin.Util.log(status);

            return status;
        }

        // Creating the WAR file was successful, so the license file must be valid.
        final String msg = getString("WebArchiveCreationSucceeded"); //$NON-NLS-1$
        final IStatus status = new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID,
                                          WebArchiveBuilderConstants.STATUS_CODE_WAR_FILE_CREATION_SUCCEEDED, msg, null);

        return status;
    }

    public boolean targetWarFileExists( Map properties ) {
        boolean fileExists = false;

        // Get and valdiate the context name.
        final String contextName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
        IStatus status = validateContextName(contextName);

        if (IStatus.ERROR == status.getSeverity()) {
            fileExists = true;
            return fileExists;
        }

        final String newWarFileName = getFileName((String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_FILE_SAVE_LOCATION),
                                                  contextName + ".war"); //$NON-NLS-1$

        fileExists = (new File(newWarFileName)).exists();

        return fileExists;
    }

    protected static String getString( final String id ) {
        return WebServicePlugin.Util.getString(I18N_PREFIX + id);
    }

    protected static String getString( final String id,
                                       final Object[] params ) {
        return WebServicePlugin.Util.getString(I18N_PREFIX + id, params);
    }

    /**
     * This method validates if any of the specified invalid characters are in the input context then a null value will be
     * returned. Note: the characters are in String format.
     * 
     * @param contextName
     * @param chars
     * @return
     * @since 4.4
     */
    private String validateInvalidCharactersInContextName( String contextName,
                                                           String[] invalidChars ) {

        int numChars = invalidChars.length;
        for (int charCounter = 0; charCounter < numChars; charCounter++) {
            final String invalidChar = invalidChars[charCounter];

            if (contextName.indexOf(invalidChar) >= 0) {
                return invalidChar;
            }
        }

        return null;
    }

    /**
     * Retruens a file name for the specified path and name.
     * 
     * @param path
     * @param name
     * @return
     * @since 4.4
     */
    private String getFileName( String path,
                                String name ) {

        String fileName = path;
        if (!fileName.endsWith("/") && !fileName.endsWith("\\")) { //$NON-NLS-1$ //$NON-NLS-2$

            fileName = fileName + File.separator;
        }
        fileName = fileName + name;

        return fileName;
    }

    /**
     * Copies Web files into the WAR build directory stucture.
     * 
     * @param contextDirectory
     * @param imagesDirectory
     * @param webInfDirectory
     * @since 4.4
     */
    private void getWebFiles( File contextDirectory,
                              File imagesDirectory,
                              File webInfDirectory ) throws Exception {

        // Copy all of the Web files
        final String webLibPath = getWebLibDirectoryPath();
        final String webAppsDirectoryName = webLibPath + File.separator + "webapps"; //$NON-NLS-1$
        final File webAppsDirectory = new File(webAppsDirectoryName);
        FileUtils.copyDirectoryContentsRecursively(webAppsDirectory, contextDirectory);
    }

    /**
     * Replace the variables in the web.xml file with their appropriate values.
     * 
     * @param webInfDirectoryName
     * @param properties
     * @param contextName
     * @since 4.4
     */
    protected void replaceVariables( String webInfDirectoryName,
                                     Map properties,
                                     String contextName ) {

        // Replace variables in the web.xml file.
        File webXmlFile = new File(webInfDirectoryName + File.separator + "web.xml"); //$NON-NLS-1$

        /*
         * here we process any additional properties defined by the caller that should be placed on the
         * URL for the connection to the Query instance embedded in the Web service WAR file.
         */
        Properties addlProps = (Properties)properties.get(WebArchiveBuilderConstants.PROPERTY_ADDITIONAL_PROPERTIES);

        String addlPropsString = StringUtil.Constants.EMPTY_STRING;

        if (addlProps != null) {
            addlPropsString = createAddlPropertiesString(addlProps);
        }

        String vdbFileName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_VDB_FILE_NAME);
        File vdbFile = new File(FileUtils.getFilenameWithoutExtension(vdbFileName));
        AntTasks.replace(webXmlFile, "${vdb.name}", vdbFile.getName()); //$NON-NLS-1$

        AntTasks.replace(webXmlFile, "${mmquery.additional.properties}", addlPropsString); //$NON-NLS-1$

        String logFilePath = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_LOG_FILE_PATH);
        AntTasks.replace(webXmlFile, "${log.file.path}", logFilePath); //$NON-NLS-1$            

        AntTasks.replace(webXmlFile, "${log.file.prefix}", contextName); //$NON-NLS-1$ 
    }

    /**
     * takes a Properties object (that CANNOT be null) and turns it into a String that can be 'tacked' on to a MM Server URL as
     * additional properties to be passed to create a connection. This method will also escape the string so that it is suitable
     * to be a text 'value' of an element in an xml document.
     * 
     * @param props NOT NULL properties object
     * @return a String of the format key=value;key=value
     */
    protected String createAddlPropertiesString( final Properties props ) {
        final StringBuffer buf = new StringBuffer();
        for (Iterator i = props.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            if (entry.getValue() instanceof String) {
                // get only the string properties, because a non-string property could not have been set on the url.
                buf.append(entry.getKey()).append('=').append(entry.getValue()).append(';');
            }
        }

        String urlString = buf.toString();

        /*
         * now we must escape any special characters in this string to ensure that it is suitable to be the text value
         * of an element in an XML document.
         */
        urlString = XmlUtil.escapeCharacterData(urlString);

        return urlString;
    }

    /**
     * Copy the lib files into the WAR directory structure.
     * 
     * @param webInfLibDirectory
     * @throws Exception
     * @since 4.4
     */
    private void getLibFiles( File webInfLibDirectory ) throws Exception {
        throw new RuntimeException("War file generation needs to re-written"); //$NON-NLS-1$
    }

    /**
     * Return the path to the directory containing the lds.jar file
     * 
     * @return
     * @throws Exception
     * @since 4.4
     */
    private String getWebLibDirectoryPath() throws Exception {
        final String mmWebServicePluginPath = PluginUtilities.getPluginProjectLocation("com.metamatrix.modeler.webservice", true); //$NON-NLS-1$
        final String mmWebServiceLibFolder = mmWebServicePluginPath + File.separator + "dimension_war_resources"; //$NON-NLS-1$
        if (new File(mmWebServiceLibFolder).exists()) {
            return mmWebServiceLibFolder;
        }
        final String msg = WebServicePlugin.Util.getString("DefaultWebArchiveBuilderImpl.web_lib_directory_does_not_exist", mmWebServiceLibFolder); //$NON-NLS-1$
        WebServicePlugin.Util.log(IStatus.ERROR, msg);
        throw new FileNotFoundException(msg);
    }

    /**
     * Return the path to the directory containing the user defined function file, FunctionDefinitions.xmi
     * 
     * @return
     * @throws Exception
     * @since 4.4
     */
    private String getUdfConfigDirectoryPath() throws Exception {
        final String mmWebServicePluginPath = PluginUtilities.getPluginProjectLocation("com.metamatrix.modeler.webservice", true); //$NON-NLS-1$
        final IPath mmWebServicePluginDirPath = new Path(mmWebServicePluginPath);

        // Look for the FunctionDefinitions.xmi file in the development environment location first
        IPath functionsFilePath = mmWebServicePluginDirPath.removeLastSegments(2).append("/config/FunctionDefinitions.xmi"); //$NON-NLS-1$

        // Check if the file exists
        File functionsFile = functionsFilePath.toFile();
        if (functionsFile != null && functionsFile.exists()) {
            return functionsFile.getParentFile().getCanonicalPath();
        }

        // Look for the FunctionDefinitions.xmi file in the installation location
        functionsFilePath = mmWebServicePluginDirPath.removeLastSegments(3).append("/config/FunctionDefinitions.xmi"); //$NON-NLS-1$

        // Check if the file exists
        functionsFile = functionsFilePath.toFile();
        if (functionsFile != null && functionsFile.exists()) {
            return functionsFile.getParentFile().getCanonicalPath();
        }

        return null;
    }

    /**
     * Creates the Embedded JAR file.
     * 
     * @param buildeDirectoryName
     * @param workDirectoryName
     * @param properties
     * @throws Exception
     * @since 4.4
     */
    private void createEmbeddedJar( String buildeDirectoryName,
                                    String workDirectoryName,
                                    Map properties,
                                    IProgressMonitor monitor ) throws Exception {

        String orginVdbFileName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_VDB_FILE_NAME);
        File orginVdbFile = new File(orginVdbFileName);

        final String vdbName = FileUtils.getFilenameWithoutExtension(orginVdbFile.getName());

        // Create the Embedded directories. We put all embedded resources under a directory qualified by the VDB name
        // to reduce the chance of inter-vdb resource mixups.
        final File embeddedJarRootDirectory = new File(workDirectoryName + File.separator
                                                       + WebArchiveBuilderConstants.EMBEDDED_DIR);
        final File vdbContextualEmbeddedDir = new File(embeddedJarRootDirectory.getPath() + File.separator + vdbName);
        final File embeddedConfigDirectory = new File(vdbContextualEmbeddedDir.getPath() + File.separator
                                                      + WebArchiveBuilderConstants.EMBEDDED_CONFIG_DIR);
        final File embeddedExtensionsDirectory = new File(vdbContextualEmbeddedDir.getPath() + File.separator
                                                          + WebArchiveBuilderConstants.EMBEDDED_EXTENSIONS_DIR);
        final File embeddedJdbcDirectory = new File(vdbContextualEmbeddedDir.getPath() + File.separator
                                                    + WebArchiveBuilderConstants.EMBEDDED_JDBC_DIR);
        final File embeddedLibDirectory = new File(vdbContextualEmbeddedDir.getPath() + File.separator
                                                   + WebArchiveBuilderConstants.EMBEDDED_LIB_DIR);
        final File embeddedLogsDirectory = new File(vdbContextualEmbeddedDir.getPath() + File.separator
                                                    + WebArchiveBuilderConstants.EMBEDDED_LOGS_DIR);

        embeddedJarRootDirectory.mkdir();
        vdbContextualEmbeddedDir.mkdir();
        embeddedConfigDirectory.mkdir();
        embeddedExtensionsDirectory.mkdir();
        embeddedJdbcDirectory.mkdir();
        embeddedLibDirectory.mkdir();
        embeddedLogsDirectory.mkdir();

        // Copy the following files from the Modeler kit.
        String dqpPluginPath = PluginUtilities.getPluginProjectLocation("com.metamatrix.modeler.dqp", true); //$NON-NLS-1$        
        final IPath dqpStateLocation = WebServicePlugin.getInstance().getStateLocation().removeLastSegments(1).append("com.metamatrix.modeler.dqp");//$NON-NLS-1$
        String mmqueryApiPluginPath = PluginUtilities.getPluginProjectLocation("com.metamatrix.mmquery.api", true); //$NON-NLS-1$
        monitor.worked(5);
        final IPath configFilePath = new Path(dqpPluginPath);

        FileUtils.copyFile(configFilePath.toOSString(),
                           "configuration.xml", embeddedConfigDirectory.getPath(), "ServerConfig.xml"); //$NON-NLS-1$ //$NON-NLS-2$

        // Copy extension JAR files from the /DqpExtensions directory into the /embedded/extensions directory

        IPath dqpExtensionPath = dqpStateLocation.append("DqpExtensions"); //$NON-NLS-1$
        File[] jarFiles = FileUtils.findAllFilesInDirectoryHavingExtension(dqpExtensionPath.toOSString(), ".jar"); //$NON-NLS-1$
        int numJarFiles = jarFiles.length;
        for (int jarFileCtr = 0; jarFileCtr < numJarFiles; jarFileCtr++) {
            File jarFile = jarFiles[jarFileCtr];
            FileUtils.copyFile(dqpExtensionPath.toOSString(), embeddedExtensionsDirectory.getPath(), jarFile.getName());
        }

        monitor.worked(5);
        // Copy the user defined function file, FunctionDefinitions.xmi, into the /embedded/config directory
        String udfFileNameValue = StringUtil.Constants.EMPTY_STRING;
        String udfClasspathValue = StringUtil.Constants.EMPTY_STRING;
        String udfConfigDirectoryPath = getUdfConfigDirectoryPath();
        if (udfConfigDirectoryPath != null) {
            // If the /config directory is found, copy the UDF model file into the /embedded/config directory
            FileUtils.copyFile(udfConfigDirectoryPath, embeddedConfigDirectory.getPath(), "FunctionDefinitions.xmi"); //$NON-NLS-1$

            // Copy any UDF extension jar files from the /config directory into the /embedded/extensions directory
            jarFiles = FileUtils.findAllFilesInDirectoryHavingExtension(udfConfigDirectoryPath, ".jar"); //$NON-NLS-1$
            numJarFiles = jarFiles.length;
            for (int jarFileCtr = 0; jarFileCtr < numJarFiles; jarFileCtr++) {
                File jarFile = jarFiles[jarFileCtr];
                FileUtils.copyFile(udfConfigDirectoryPath, embeddedExtensionsDirectory.getPath(), jarFile.getName());
            }

            // Set the property value for the user defined function file
            udfFileNameValue = "./config/FunctionDefinitions.xmi"; //$NON-NLS-1$

            // Create the concatenated string of UDF extension jars
            if (numJarFiles > 0) {
                StringBuffer sb = new StringBuffer();
                for (int jarFileCtr = 0; jarFileCtr < numJarFiles; jarFileCtr++) {
                    sb.append("extensionjar:"); //$NON-NLS-1$
                    sb.append(jarFiles[jarFileCtr].getName());
                    sb.append(';');
                }
                // Remove the trailing ';'
                sb.deleteCharAt(sb.length() - 1);
                udfClasspathValue = sb.toString();
            }
        }
        monitor.worked(5);
        FileUtils.copyFile(mmqueryApiPluginPath + "/dqp", embeddedLibDirectory.getPath(), "mmquery.jar"); //$NON-NLS-1$ //$NON-NLS-2$
        FileUtils.copyFile(dqpPluginPath, embeddedLibDirectory.getPath(), "metamatrixpki.keystore"); //$NON-NLS-1$
        FileUtils.copyFile(dqpPluginPath, embeddedLibDirectory.getPath(), "System.vdb"); //$NON-NLS-1$ 

        // Copy VDB into the config directory

        String destVdbFileName = embeddedConfigDirectory.getPath() + File.separator + orginVdbFile.getName();
        FileUtils.copy(orginVdbFileName, destVdbFileName);

        // Copy embedded.properties file.
        FileUtils.copyFile(buildeDirectoryName,
                           vdbContextualEmbeddedDir.getPath(),
                           WebArchiveBuilderConstants.EMBEDDED_PROPERTIES_FILE_NAME);

        // Read password key.
        String passwordFile = dqpPluginPath + File.separator + "dqp.key"; //$NON-NLS-1$
        BufferedReader reader = new BufferedReader(new FileReader(passwordFile));
        String dqpKey = reader.readLine();
        reader.close();
        monitor.worked(5);
        // Replace variables in the Embedded properties file.
        File embeddedPropertiesFile = new File(vdbContextualEmbeddedDir.getPath() + File.separator
                                               + WebArchiveBuilderConstants.EMBEDDED_PROPERTIES_FILE_NAME);

        AntTasks.replace(embeddedPropertiesFile, "${dqp.key}", dqpKey); //$NON-NLS-1$
        AntTasks.replace(embeddedPropertiesFile, "${vdb.file.name}", orginVdbFile.getName()); //$NON-NLS-1$
        AntTasks.replace(embeddedPropertiesFile, "${dqp.udf.file}", udfFileNameValue); //$NON-NLS-1$
        AntTasks.replace(embeddedPropertiesFile, "${dqp.udf.extensionjar}", udfClasspathValue); //$NON-NLS-1$

        // ZIP everything in the context directory into a JAR file.
        final String embeddedJarFileName = workDirectoryName + File.separator + WebArchiveBuilderConstants.EMBEDDED_JAR_FILE_NAME;
        AntTasks.zip(embeddedJarRootDirectory.getPath(), embeddedJarFileName);
        monitor.worked(30);

    }

    /**
     * Gets the external JAR files.
     * 
     * @param libDirectoryName
     * @throws Exception
     * @since 4.4
     */
    private void getExternalFiles( String libDirectoryName ) throws Exception {

        // Copy external JAR files.
        String pluginsDirectory = PluginUtilities.getPluginsLocation(true);
        FileUtils.copyFile(pluginsDirectory, libDirectoryName, "org.eclipse.core.runtime_3.1.0.jar"); //$NON-NLS-1$
        FileUtils.copyFile(pluginsDirectory, libDirectoryName, "org.eclipse.core.runtime.compatibility_3.1.0.jar"); //$NON-NLS-1$
        FileUtils.copyFile(pluginsDirectory, libDirectoryName, "org.eclipse.core.resources_3.1.0.jar"); //$NON-NLS-1$        
        FileUtils.copyFile(pluginsDirectory, libDirectoryName, "org.eclipse.osgi_3.1.0.jar"); //$NON-NLS-1$
        FileUtils.copyFile(pluginsDirectory, libDirectoryName, "org.eclipse.osgi.services_3.1.0.jar"); //$NON-NLS-1$
        FileUtils.copyFile(pluginsDirectory, libDirectoryName, "org.eclipse.osgi.util_3.1.0.jar"); //$NON-NLS-1$                    

        String jdomDir = PluginUtilities.getPluginProjectLocation("org.jdom", true); //$NON-NLS-1$
        FileUtils.copyFile(jdomDir, libDirectoryName, "jdom.jar"); //$NON-NLS-1$  

        String oswegoDir = PluginUtilities.getPluginProjectLocation("edu.oswego.util.concurrent", true); //$NON-NLS-1$
        FileUtils.copyFile(oswegoDir, libDirectoryName, "concurrent.jar"); //$NON-NLS-1$    
    }

    private void copyLicenseFilesToClassesDirectory( String classesDirName,
                                                     Map properties ) throws Exception {
        // Copy the license file to the web-inf/classes directory of the war file.
        final String path = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_LICENSE_FILE_PATH);
        FileUtils.copyFile(path, classesDirName, WebArchiveBuilderConstants.LICENSE_NAME);
        FileUtils.copyFile(path, classesDirName, WebArchiveBuilderConstants.LICENSE_CERT_NAME);
    }

}
