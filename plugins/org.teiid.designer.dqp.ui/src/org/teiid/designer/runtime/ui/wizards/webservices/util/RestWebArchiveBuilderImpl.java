/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.wizards.webservices.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.TempDirectory;
import org.teiid.designer.DesignerSPIPlugin;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.ui.wizards.webservices.WarDeploymentInfoPanel;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.webservice.WebServicePlugin;
import org.teiid.designer.webservice.lib.WebServiceLibConstants;
import org.teiid.designer.webservice.lib.WebServiceLibPlugin;
import org.teiid.designer.webservice.util.AntTasks;


/**
 * This is the default implementation of a WebArchiveBuilder.
 * 
 * @since 8.0
 */
public class RestWebArchiveBuilderImpl implements WebArchiveBuilder, WebServiceLibConstants, StringConstants {

    private IPath webServicePluginPath = null;
    private List<String> models = new ArrayList<String>();
    private Map<String, List<RestProcedure>> modelMapOfProcedures = new HashMap<String, List<RestProcedure>>();
    File webXmlFile = null;
    public static String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$

    // =============================================================
    // Constants
    // =============================================================
    private static final String I18N_PREFIX = "WebArchiveBuilder."; //$NON-NLS-1$
    private static final String TASK_CREATE_DIRECTORIES = getString("taskCreateDirectory"); //$NON-NLS-1$
    private static final String TASK_COPYING_FILES = getString("taskCopyingFiles"); //$NON-NLS-1$
    private static final String TASK_CREATING_WAR_ARCHIVE = getString("taskCreatingWarArchive"); //$NON-NLS-1$
    private static final String TASK_COPYING_WAR_FILE = getString("taskCopyingWarFile"); //$NON-NLS-1$
    private static final String TASK_CLEANUP = getString("taskCleanup"); //$NON-NLS-1$
    private static final String JNDI_PREFIX = "java:"; //$NON-NLS-1$

    /**
     * This constructor is package protected, so that only the factory can call it.
     * 
     * @since 7.4
     */
    protected RestWebArchiveBuilderImpl() {
    }

    /**
     * @return modelMapOfProcedures
     */
    public Map<String, List<RestProcedure>> getModelMapOfProcedures() {
        return modelMapOfProcedures;
    }

    /**
     * @param modelMapOfProcedures Sets modelMapOfProcedures to the specified value.
     */
    public void setModelMapOfProcedures( Map<String, List<RestProcedure>> modelMapOfProcedures ) {
        this.modelMapOfProcedures = modelMapOfProcedures;
    }

    @Override
    public IStatus validateContextName( String contextName ) {

        try {
            final String URL_BASE = "http://www.teiid.org/"; //$NON-NLS-1$       
            final URLValidator urlValidator = new URLValidator();

            // Check for invalid characters
            String[] invalidChars = new String[] {"/" //$NON-NLS-1$
                , "\\" //$NON-NLS-1$
                , CoreStringUtil.Constants.SPACE};
            final String invalidChar = validateInvalidCharactersInContextName(contextName, invalidChars);

            // Perform validation
            if (invalidChar != null) {

                String msg = null;
                if (CoreStringUtil.Constants.SPACE.equals(invalidChar)) {
                    msg = getString("ContextNameValidationFailed_InvalidSpace"); //$NON-NLS-1$
                } else {
                    final Object[] params = new Object[] {invalidChar};
                    msg = getString("ContextNameValidationFailed_InvalidCharacter", params); //$NON-NLS-1$
                }

                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID,
                                  WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, msg, null);
            } else if (CoreStringUtil.Constants.EMPTY_STRING.equals(contextName)) {

                final String msg = getString("ContextNameValidationFailed_Empty"); //$NON-NLS-1$        
                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID,
                                  WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, msg, null);
            } else if (!urlValidator.isValidValue(URL_BASE + contextName)) {

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
     * @see org.teiid.designer.runtime.ui.wizards.webservices.util.WebArchiveBuilder#createWebArchive(Properties, IProgressMonitor)
     * @since 7.4
     */
    @Override
    public IStatus createWebArchive( Properties properties,
                                     IProgressMonitor monitor ) {

        try {

            // Get and validate the context name.
            final String contextName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
            IStatus status = validateContextName(contextName);

            if (IStatus.ERROR == status.getSeverity()) {

                throw new Exception(status.getException());
            }

            modelMapOfProcedures = (Map<String, List<RestProcedure>>)properties.get(WebArchiveBuilderConstants.PROPERTY_VDB_REST_PROCEDURES);
            setModels(modelMapOfProcedures);

            // Get the build directory and create it if it doesn't already
            // exist.
            final String webServicePluginPath = WebServiceLibPlugin.getDefault().getInstallPath().toOSString();
            final String buildDirectoryName = webServicePluginPath + File.separator + WebArchiveBuilderConstants.REST_BUILD_DIR;
            
            File buildDirectory = new File(buildDirectoryName);
            buildDirectory.mkdir();

            monitor.subTask(TASK_CREATE_DIRECTORIES);
            // Get the work directory, create it (in case it is not there) and
            // try to empty it.
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
            // Create the WEB-INF directory.
            final String webInfDirectoryName = contextDirectoryName + File.separator + WEB_INF;
            final File webInfDirectory = new File(webInfDirectoryName);
            webInfDirectory.mkdir();
            // Create the classes directory.
            final String webInfClassesDirectoryName = webInfDirectoryName + File.separator + CLASSES;
            // Create the classes directory.
            final String webInfLibDirectoryName = webInfDirectoryName + File.separator + LIB;
            final File webInfClassesDirectory = new File(webInfClassesDirectoryName);
            final File webInfLibDirectory = new File(webInfLibDirectoryName);
            webInfLibDirectory.mkdir();
            webInfClassesDirectory.mkdir();
            monitor.worked(10);

            monitor.subTask(TASK_COPYING_FILES);
            // Copy the Web files.
            getWebFiles(contextDirectory, webInfDirectory);

            // Encapsulate the security properties in order to ensure they are valid
            SecurityCredentials securityCredentials = new SecurityCredentials(properties);
            if (securityCredentials.hasType(WarDeploymentInfoPanel.BASIC)) {
                // Replace the variables in the jboss-web.xml file.
                replaceJBossWebXmlVariables(webInfDirectoryName, securityCredentials.getSecurityRealm());
            }

            // Replace the variables in the web.xml file.
            replaceWebXmlVariables(webInfDirectoryName, securityCredentials, contextName);
            // Create properties file and write to classes root.
            createPropertiesFile(webInfClassesDirectory, properties);
            // Create and compile Provider files (one per port).
            createResourceJavaClasses(contextDirectory, webInfLibDirectory, webInfClassesDirectory, properties);

            monitor.worked(10);

            monitor.subTask(TASK_CREATING_WAR_ARCHIVE);
            // ZIP everything in the context directory into the new WAR file.
            final String warFileName = tempDirectoryName + File.separator + contextName + DOT_WAR;
            AntTasks.zip(contextDirectoryName, warFileName);
            monitor.worked(20);

            // Get the target directory. If it doesn't exist, create it.
            final String newWarFileDir = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_FILE_SAVE_LOCATION);
            File newWarDir = new File(newWarFileDir);
            if (!newWarDir.exists()) {
                newWarDir.mkdir();
            }

            monitor.subTask(TASK_COPYING_WAR_FILE);
            // Move the temporary WAR file to its destination.
            final File warFile = new File(warFileName);
            final String newWarFileName = getFileName((String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_FILE_SAVE_LOCATION),
                                                      contextName + DOT_WAR);
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

        // Creating the WAR file was successful
        final String msg = getString("WebArchiveCreationSucceeded"); //$NON-NLS-1$
        final IStatus status = new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID,
                                          WebArchiveBuilderConstants.STATUS_CODE_WAR_FILE_CREATION_SUCCEEDED, msg, null);

        return status;
    }

    @Override
    public boolean targetWarFileExists( Properties properties ) {
        boolean fileExists = false;

        // Get and validate the context name.
        final String contextName = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
        IStatus status = validateContextName(contextName);

        if (IStatus.ERROR == status.getSeverity()) {
            fileExists = true;
            return fileExists;
        }

        final String newWarFileName = getFileName((String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_FILE_SAVE_LOCATION),
                                                  contextName + DOT_WAR);

        fileExists = (new File(newWarFileName)).exists();

        return fileExists;
    }

    private static String getString( final String id ) {
        return WebServicePlugin.Util.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
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
     * @since 7.4
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
     * Returns a file name for the specified path and name.
     * 
     * @param path
     * @param name
     * @return
     * @since 7.4
     */
    private String getFileName( String path,
                                String name ) {

        String fileName = path;
        if (!fileName.endsWith(FORWARD_SLASH) && !fileName.endsWith(DOUBLE_BACK_SLASH)) {

            fileName = fileName + File.separator;
        }
        fileName = fileName + name;

        return fileName;
    }

    /**
     * Copies Web files into the WAR build directory structure.
     * 
     * @param contextDirectory
     * @param webInfDirectory
     * @since 7.4
     */
    private void getWebFiles( File contextDirectory, File webInfDirectory ) throws Exception {
        // Copy all of the Web files
        final String webLibPath = WebServiceLibPlugin.getDefault().getRestWebLibDirectoryPath();
        final String webAppsDirectoryName = webLibPath + File.separator + WEBAPPS;
        final File webAppsDirectory = new File(webAppsDirectoryName);
        FileUtils.copyRecursively(webAppsDirectory, contextDirectory, null, false);
    }
    
    /**
     * Replace the variables in the web.xml file with their appropriate values.
     * 
     * @param webInfDirectoryName
     * @param properties
     * @param contextName
     * @since 8.2
     */
    private void replaceJBossWebXmlVariables( String webInfDirectoryName, String securityDomain) {

        // Replace variables in the jboss-web.xml file.
        File jbossWebXmlFile = new File(webInfDirectoryName + File.separator + JBOSS_WEB_XML);

        String securityDomainNode = "<security-domain>java:/jaas/" + securityDomain + "</security-domain>"; //$NON-NLS-1$ //$NON-NLS-2$

        AntTasks.replace(jbossWebXmlFile,
                "<!--<security-domain>java:/jaas/teiid-security</security-domain>-->", securityDomainNode); //$NON-NLS-1$
        
    }

    private String getUrlPatterns() {
    	StringBuilder patternStringBuilder = new StringBuilder();
		for (String model:models){
			patternStringBuilder.append("<url-pattern>/").append(model).append("/*</url-pattern>").append(NEWLINE);
		}
		return patternStringBuilder.toString();
	}

	/**
     * Replace the variables in the web.xml file with their appropriate values.
     * 
     * @param webInfDirectoryName
     * @param properties
     * @param contextName
     *
     * @throws Exception
     *
     * @since 7.4
     */
    private void replaceWebXmlVariables( String webInfDirectoryName,
                                           SecurityCredentials securityCredentials,
                                           String contextName ) throws Exception {

        // Replace variables in the web.xml file.
        webXmlFile = new File(webInfDirectoryName + File.separator + WEB_XML);
        
        String urlPatterns = getUrlPatterns();
        AntTasks.replace(webXmlFile, "${urlPattern}", urlPatterns); //$NON-NLS-1$

        // Update for Basic Auth if HTTPBasic security is selected
        if (securityCredentials.hasType(WarDeploymentInfoPanel.BASIC)) {
            AntTasks.replace(webXmlFile, "<!--<security-constraint>", "\t<security-constraint>"); //$NON-NLS-1$ //$NON-NLS-2$
            AntTasks.replace(webXmlFile, "${realmName}", securityCredentials.getSecurityRealm()); //$NON-NLS-1$
            AntTasks.replace(webXmlFile, "${roleName}", securityCredentials.getSecurityRole()); //$NON-NLS-1$
            AntTasks.replace(webXmlFile, "</login-config>-->", "</login-config>"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        AntTasks.replace(webXmlFile, "${warname}", contextName); //$NON-NLS-1$
        AntTasks.replace(webXmlFile, "${contextName}", contextName); //$NON-NLS-1$
    }

    /**
     * Create the teiidrest.properties file.
     * 
     * @param webInfClassesDirectory
     * @param properties
     * @since 7.4
     */
    private void createPropertiesFile( File webInfClassesDirectory,
                                         Properties properties ) throws IOException {

        // Create teiidrest.properties file
        File teiidRestProperties = new File(webInfClassesDirectory + File.separator +TEIID_REST_PROPS);
        String jndiValue = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME);

        if (jndiValue != null && !jndiValue.startsWith(JNDI_PREFIX)) {
            jndiValue = JNDI_PREFIX + FORWARD_SLASH + jndiValue;
        }
        
        ITeiidServerVersion teiidVersion = ModelerCore.getTeiidServerVersion();
        
        String version = teiidVersion.toString();
        
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            // Create file
            fstream = new FileWriter(teiidRestProperties);
            out = new BufferedWriter(fstream);
            out.write(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME + EQUALS + jndiValue + NEWLINE);
            out.write(WebArchiveBuilderConstants.PROPERTY_TEIID_VERSION + EQUALS + version);
        } finally {
            // Close the output stream
            out.close();
        }
    }

    /**
     * Create the Resource (one for each model) and Application java classes
     * 
     * @param webInfClassesDirectory
     * @param properties
     * @throws Exception
     * @since 7.4
     */
    private void createResourceJavaClasses(   File contextDirectory,
    										  File webInfLibDirectory,
                                              File webInfClassesDirectory,
                                              Properties properties) throws Exception {

        String pathToResource = "/org" + File.separator + "teiid" + File.separator + "rest" + File.separator + "services"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String pathToPlugin = "/org" + File.separator + "teiid" + File.separator + "rest"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        String teiidProviderJavaFile= null;
        
        String vdbFileName = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_VDB_FILE_NAME);
        String context = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
        String vdbName = vdbFileName.substring(vdbFileName.lastIndexOf(File.separator ));
        vdbName=vdbName.substring(1,vdbName.lastIndexOf(".")) + " VDB";
        
        List<File> resources = new ArrayList<File>();
        StringBuilder singletonSb = new StringBuilder();
        for (String resource : getModels()) {
            String resourceJavaFilePath = webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator + resource
                                          + ".java"; //$NON-NLS-1$
            FileUtils.copy(webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator + "ResourceTemplate.java", //$NON-NLS-1$
                           resourceJavaFilePath,
                           true);
            File indexHtml = new File(contextDirectory.getCanonicalPath() + File.separator + "index.html");
            File resourceJavaFile = new File(resourceJavaFilePath);
            resources.add(resourceJavaFile);

            ITeiidServerVersion version = ModelerCore.getTeiidServerVersion();
            boolean greaterThan82 = version.isGreaterThan(TeiidServerVersion.Version.TEIID_8_2.get());

            if (greaterThan82) {
            	teiidProviderJavaFile = "TeiidRSProviderPost"; //$NON-NLS-1$
            }else{
            	teiidProviderJavaFile = "TeiidRSProviderPre"; //$NON-NLS-1$
            }
            
            AntTasks.replace(resourceJavaFile, "${TeiidRSProvider}", "org.teiid.rest.services." + teiidProviderJavaFile); //$NON-NLS-1$ //$NON-NLS-2$
            AntTasks.replace(resourceJavaFile, "${className}", resource); //$NON-NLS-1$
            AntTasks.replace(resourceJavaFile, "${modelName}", "org.teiid.rest.services." + resource); //$NON-NLS-1$ //$NON-NLS-2$
            AntTasks.replace(resourceJavaFile, "${path}", "@Path( \"/" + resource + "\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            AntTasks.replace(resourceJavaFile, "${api}", "@Api( value=\"/" + resource + "\", description=\"REST operations\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            String methods = generateMethods(resource);
            AntTasks.replace(resourceJavaFile, "${httpMethods}", methods); //$NON-NLS-1$
            AntTasks.replace(indexHtml, "${title}", vdbName); //$NON-NLS-1$
            AntTasks.replace(indexHtml, "${context}", context); //$NON-NLS-1$

            singletonSb.append(NEWLINE + "singletons.add(new org.teiid.rest.services." + resource + "());"); //$NON-NLS-1$ //$NON-NLS-2$

        }

        File template = new File(webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator
                                 + "ResourceTemplate.java"); //$NON-NLS-1$

        File restPlugin = new File(webInfClassesDirectory.getCanonicalPath() + pathToPlugin + File.separator + "RestPlugin.java"); //$NON-NLS-1$
        
        File teiidProvider = new File(webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator
                                      + teiidProviderJavaFile + ".java"); //$NON-NLS-1$

        File teiidRestApplication = new File(webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator
                                             + "TeiidRestApplication.java"); //$NON-NLS-1$

        AntTasks.replace(teiidRestApplication, "${resources}", singletonSb.toString()); //$NON-NLS-1$

        template.delete();
        String spiPath = DesignerSPIPlugin.getPluginPath();
        File spiFile = new File(spiPath);
        String runtimePath = ModelerCore.getTeiidRuntimePath();
        File runtimeFile = new File(runtimePath);
        // Compile classes
        JavaCompiler compilerTool = ToolProvider.getSystemJavaCompiler();
        if (compilerTool != null) {
            StandardJavaFileManager fileManager = compilerTool.getStandardFileManager(null, null, null);

            String pathToJar1 = webInfLibDirectory.getCanonicalPath() + File.separator + JACKSON_CORE_JAR;
            String pathToJar2 = webInfLibDirectory.getCanonicalPath() + File.separator + JACKSON_JAXRS_JAR;
            String pathToJar3 = webInfLibDirectory.getCanonicalPath() + File.separator + JSON_JAR;
            String pathToJar4 = webInfLibDirectory.getCanonicalPath() + File.separator + JAXRS_API_JAR;
            String pathToJar5 = webInfLibDirectory.getCanonicalPath() + File.separator + SWAGGER_ANNOTATIONS_JAR;
            String pathToJar6 = webInfLibDirectory.getCanonicalPath() + File.separator + SWAGGER_CORE_JAR;
            String pathToJar7 = webInfLibDirectory.getCanonicalPath() + File.separator + SWAGGER_JAXRS_JAR;
            String pathToJar8 = webInfLibDirectory.getCanonicalPath() + File.separator + SERVLET_API;
            String pathToJar9 = webInfLibDirectory.getCanonicalPath() + File.separator + REFLECTIONS;
            String pathToJar10 = webInfLibDirectory.getCanonicalPath() + File.separator + SCALA_LIBRARY;
            String pathToJar11 = webInfLibDirectory.getCanonicalPath() + File.separator + JACKSON_CORE_JAR;
            String pathToJar12 = webInfLibDirectory.getCanonicalPath() + File.separator + JACKSON_DATABIND_JAR;
            
            FileUtils.copy(spiFile, webInfLibDirectory, true);
            FileUtils.copy(runtimeFile, webInfLibDirectory, true);

            List<File> classPaths = new ArrayList<File>();
            classPaths.add(new File(pathToJar1));
            classPaths.add(new File(pathToJar2));
            classPaths.add(new File(pathToJar3));
            classPaths.add(new File(pathToJar4));
            classPaths.add(new File(pathToJar5));
            classPaths.add(new File(pathToJar6));
            classPaths.add(new File(pathToJar7));
            classPaths.add(new File(pathToJar8));
            classPaths.add(new File(pathToJar9));
            classPaths.add(new File(pathToJar10));
            classPaths.add(new File(pathToJar11));
            classPaths.add(new File(pathToJar12));

            classPaths.add(runtimeFile);
            classPaths.add(spiFile);

            fileManager.setLocation(StandardLocation.CLASS_PATH, classPaths);

            List<File> sourceFileList = new ArrayList<File>();
            // prepare the source files to compile
            for (File resourceClass : resources) {
                sourceFileList.add(resourceClass);
            }

            sourceFileList.add(restPlugin);
            sourceFileList.add(teiidProvider);
            sourceFileList.add(teiidRestApplication);

            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
            /*Create a diagnostic controller, which holds the compilation problems*/
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            ArrayList<String> options = new ArrayList<String>();

            CompilationTask task = compilerTool.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
            task.call();
            List<Diagnostic<? extends JavaFileObject>> diagnosticList = diagnostics.getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticList) {
                diagnostic.getKind();
                if (diagnostic.getKind().equals(Kind.ERROR)) {
                    throw new Exception(diagnostic.getMessage(null));
                }
            }
            fileManager.close();

            Boolean includeJars = (Boolean)properties.get(WebArchiveBuilderConstants.PROPERTY_INCLUDE_RESTEASY_JARS);

            // Delete RESTEasy and dependent jars if the user elected not to include them. Some jars are required even without
            // the RESTEasy jars. We will need to save those to a temp folder and then copy them back after removing the RESTEasy jars.
            if (!includeJars) {
            	File tempFolder = new File(webInfClassesDirectory+"/tmp"); //$NON-NLS-1$
                File jsonJar = new File(pathToJar4);
            //    File saxonJar = new File(pathToJar6);
                List<File> jarsToAdd = new ArrayList<File>();
            //    jarsToAdd.add(saxonJar);
                jarsToAdd.add(jsonJar);
                jarsToAdd.add(spiFile);
                jarsToAdd.add(runtimeFile);
                Iterator iter = jarsToAdd.iterator();
                
                while (iter.hasNext()){
                	FileUtils.copy((File)iter.next(), tempFolder, true);
                }

                FileUtils.removeChildrenRecursively(webInfLibDirectory);
                jarsToAdd = FileUtils.getFilesForPattern(tempFolder.getCanonicalPath(), EMPTY_STRING, ".jar"); //$NON-NLS-1$
                iter = jarsToAdd.iterator();
                while (iter.hasNext()){
                	FileUtils.copy((File)iter.next(), webInfLibDirectory, true);
                }
                
                tempFolder.delete();

            }

        }
    }

    private String generateMethods( final String name ) {
        StringBuilder sb = new StringBuilder();

        ArrayList procedureList = (ArrayList)modelMapOfProcedures.get(name);
        Iterator<RestProcedure> procedureIter = procedureList.iterator();
        while (procedureIter.hasNext()) {
            RestProcedure restProcedure = procedureIter.next();

            if (restProcedure.getProducesAnnotation() != null
                && restProcedure.getProducesAnnotation().contains("MediaType.APPLICATION_JSON")) { //$NON-NLS-1$
                createJSONMethod(sb, restProcedure);
            } else {
                createXMLMethod(sb, restProcedure);
            }

        }

        return sb.toString();

    }

    /**
     * @param sb
     * @param restProcedure
     */
    private void createXMLMethod( StringBuilder sb,
                                  RestProcedure restProcedure ) {
        commonRestMethodLogic(sb, restProcedure, "", true); //$NON-NLS-1$
        if (restProcedure.getConsumesAnnotation() != null && !restProcedure.getConsumesAnnotation().isEmpty()) {
            sb.append("\tparameterMap = getInputs(is);" + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Gen return and execute
        sb.append("\treturn teiidProvider.execute(\"" + restProcedure.getFullyQualifiedProcedureName() + "\", parameterMap, \"" + restProcedure.getCharSet() + "\", properties);" + NEWLINE //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-4
                  + "}" + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param sb
     * @param restProcedure
     */
    private void createJSONMethod( StringBuilder sb,
                                   RestProcedure restProcedure ) {
        commonRestMethodLogic(sb, restProcedure, "json", false); //$NON-NLS-1$
        if (restProcedure.getConsumesAnnotation() != null && !restProcedure.getConsumesAnnotation().isEmpty()) {
            sb.append("\tparameterMap = getJSONInputs(is, \""+restProcedure.getCharSet()+ "\");" + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        // Gen return and execute
        sb.append("\tString result = convertStreamToString(teiidProvider.execute(\"" + restProcedure.getFullyQualifiedProcedureName() + "\", parameterMap, \"" + restProcedure.getCharSet() + "\", properties), \"" + restProcedure.getCharSet() + "\");" + NEWLINE //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                  + "\t"); //$NON-NLS-1$
        sb.append("\tString json = convertXMLToJSON(result);" + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("\treturn json;" + NEWLINE + "\t" + "}" + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    }

    /**
     * @param sb
     * @param restProcedure
     */
    private void commonRestMethodLogic( StringBuilder sb,
                                        RestProcedure restProcedure,
                                        String methodAppendString,
                                        boolean isXml) {
        sb.append("@" + restProcedure.getRestMethod().toUpperCase() + NEWLINE + "\t"); //$NON-NLS-1$//$NON-NLS-2$
        String uri = methodAppendString == "" ? restProcedure.getUri() : methodAppendString + "/" + restProcedure.getUri(); //$NON-NLS-1$ //$NON-NLS-2$
        
        sb.append("@Path( \"/" + uri + "\" )" + NEWLINE + "\t"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        if (restProcedure.getConsumesAnnotation() != null && !restProcedure.getConsumesAnnotation().isEmpty()) {
            sb.append(restProcedure.getConsumesAnnotation() + NEWLINE + "\t"); //$NON-NLS-1$
        }
        if (restProcedure.getProducesAnnotation() != null && !restProcedure.getProducesAnnotation().isEmpty()) {
            sb.append(restProcedure.getProducesAnnotation() + NEWLINE + "\t"); //$NON-NLS-1$
        }
        
        // Gen method signature
        if (isXml){
        	sb.append("@ApiOperation(value=\"(XML) " + restProcedure.getProcedureName() + ": " + restProcedure.getDescription() + "\",response=InputStream.class)" + NEWLINE + "\t"); //$NON-NLS-1$
        	sb.append("public InputStream " + restProcedure.getProcedureName() + methodAppendString + "( "); //$NON-NLS-1$ //$NON-NLS-2$
        }else{
        	sb.append("@ApiOperation(value=\"(JSON) " + restProcedure.getProcedureName() + ": " + restProcedure.getDescription() + "\",response=String.class)" + NEWLINE + "\t");
        	sb.append("public String " + restProcedure.getProcedureName() + methodAppendString + "( "); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        // Check for URI parameters and add as @PathParams
        Collection<String> pathParams = WarArchiveUtil.getPathParameters(uri);
        int pathParamCount = 0;
        for (String param : pathParams) {
            pathParamCount++;
            sb.append("@ApiParam( \"" + param + "\" ) "); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("@PathParam( \"" + param + "\" ) String " + param); //$NON-NLS-1$ //$NON-NLS-2$
            if (pathParamCount < pathParams.size()) {
                sb.append(", "); //$NON-NLS-1$
            }
        }
        
        //Now check for header parameters
        LinkedList<String> headerParamList = new LinkedList<String>();
        if (hasHeaders(restProcedure)){
        	int headerParamCount = 0;
        	headerParamList = restProcedure.getHeaderParameterList();
            for (String param : headerParamList) {
            	headerParamCount++;
                sb.append("@HeaderParam( \"" + param + "\" ) String " + param); //$NON-NLS-1$ //$NON-NLS-2$
                if (headerParamCount < headerParamList.size()) {
                    sb.append(", "); //$NON-NLS-1$
                }
            }
            //Add a comma if we expect to add query parameters in the next block of code
            if (hasQueryParameters(restProcedure)) sb.append(", ");  //$NON-NLS-1$
        }
        
        //Now check for query parameters
        LinkedList<String> queryParamList = new LinkedList<String>();
        if (pathParamCount==0 && hasQueryParameters(restProcedure)){
        	int queryParamCount = 0;
        	queryParamList = restProcedure.getQueryParameterList();
            for (String param : queryParamList) {
            	queryParamCount++;
                sb.append("@QueryParam( \"" + param + "\" ) String " + param); //$NON-NLS-1$ //$NON-NLS-2$
                if (queryParamCount < queryParamList.size()) {
                    sb.append(", "); //$NON-NLS-1$
                }
            }
            	
        }
        if (restProcedure.getConsumesAnnotation() != null && !restProcedure.getConsumesAnnotation().isEmpty()) {
            if (pathParams.size() > 0 || headerParamList.size() > 0 || queryParamList.size() > 0 ) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(" InputStream is ) { " + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            sb.append(" ) { " + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Gen setting of parameter(s)
        sb.append("\tMap<String, String> parameterMap = getParameterMap();" + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        //We will always consider header parameters first, so the supporting procedure knows the correct order
        if (headerParamList.size() > 0) {
            for (String param : headerParamList) {
                sb.append("\tparameterMap.put(\"" + param + "\", " + param + ");" + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
        }
        if (pathParams.size() > 0) {
            for (String param : pathParams) {
                sb.append("\tparameterMap.put(\"" + param + "\", " + param + ");" + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
        }
        if (queryParamList.size() > 0) {
            for (String param : queryParamList) {
                sb.append("\tparameterMap.put(\"" + param + "\", " + param + ");" + NEWLINE + "\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
        }
    }
    
    /** 
     * @param restProcedure
     *  @return
     */
    private boolean hasQueryParameters(RestProcedure restProcedure) {
    	return restProcedure.getQueryParameterList() != null && restProcedure.getQueryParameterList().size()>0;
    }
    
    /**
    * @param restProcedure
    * @return
    */
    private boolean hasHeaders(RestProcedure restProcedure) {
    	return restProcedure.getHeaderParameterList() != null && restProcedure.getHeaderParameterList().size()>0;
    }

    class URLValidator implements Serializable {
        /**
         */
        private static final long serialVersionUID = -4756137226908808631L;

        public boolean isValidValue( Object value ) {
            if (value instanceof String) {
                try {
                    new URL(value.toString());
                } catch (MalformedURLException e) {
                    return false;
                }
                return true;
            } else if (value instanceof URL) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @param modelToProcedureMap Map of models with procedures
     */
    public void setModels( Map<String, List<RestProcedure>> modelToProcedureMap ) {
        Set<String> modelNameSet = modelToProcedureMap.keySet();
        this.models = new ArrayList<String>(modelNameSet);
    }

    /**
     * @return models
     */
    public List<String> getModels() {
        return models;
    }
}