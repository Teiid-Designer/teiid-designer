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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.util.XSDParser;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.TempDirectory;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.util.JndiUtil;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.runtime.ui.wizards.webservices.WarDeploymentInfoPanel;
import org.teiid.designer.ui.util.ErrorHandler;
import org.teiid.designer.vdb.ui.util.VdbResourceFinder;
import org.teiid.designer.webservice.WebServicePlugin;
import org.teiid.designer.webservice.gen.BasicWsdlGenerator;
import org.teiid.designer.webservice.lib.WebServiceLibConstants;
import org.teiid.designer.webservice.lib.WebServiceLibPlugin;
import org.teiid.designer.webservice.util.AntTasks;

/**
 * This is the default implementation of a WebArchiveBuilder.
 * 
 * @since 8.0
 */
public class DefaultWebArchiveBuilderImpl implements WebArchiveBuilder, WebServiceLibConstants, StringConstants {

    private List<String> ports = new ArrayList<String>();
    private Map<String, String> operationToProcedureMap = new HashMap<String, String>();
    private String wsdlFilename = CoreStringUtil.Constants.EMPTY_STRING;

    // =============================================================
    // Constants
    // =============================================================
    private static final String I18N_PREFIX = "WebArchiveBuilder."; //$NON-NLS-1$
    private static final String TASK_CREATE_DIRECTORIES = getString("taskCreateDirectory"); //$NON-NLS-1$
    private static final String TASK_COPYING_FILES = getString("taskCopyingFiles"); //$NON-NLS-1$
    private static final String TASK_CREATING_WSDL_FILE = getString("taskCreatingWSDLFile"); //$NON-NLS-1$
    private static final String TASK_CREATING_WAR_ARCHIVE = getString("taskCreatingWarArchive"); //$NON-NLS-1$
    private static final String TASK_COPYING_WAR_FILE = getString("taskCopyingWarFile"); //$NON-NLS-1$
    private static final String TASK_CLEANUP = getString("taskCleanup"); //$NON-NLS-1$

    /**
     * This constructor is package protected, so that only the factory can call it.
     * 
     * @since 7.1
     */
    public DefaultWebArchiveBuilderImpl() {
    }

    /**
     * @param ports Sets ports to the specified value. These names are used for the dynamic generation of servlets in the web.xml,
     *        the jbossws-cxf.xml endpoints and the wsprovider java files.
     */
    public void setPorts( List<String> ports ) {
        this.ports = ports;
    }

    /**
     * @return ports
     */
    public List<String> getPorts() {
        return ports;
    }

    /**
     * @return operationToProcedureMap
     */
    public Map<String, String> getOperationToProcedureMap() {
        return operationToProcedureMap;
    }

	/**
     * @param operationToProcedureMap Sets operationToProcedureMap to the specified value.
     */
    public void setOperationToProcedureMap( Map<String, String> operationToProcedureMap ) {
        this.operationToProcedureMap = operationToProcedureMap;
    }

    @Override
    public IStatus validateContextName( String contextName ) {

        try {
            final String URL_BASE = "http://www.teiid.org/"; //$NON-NLS-1$       
            final URLValidator urlValidator = new URLValidator();

            // Check for invalid characters
            String[] invalidChars = new String[] {FORWARD_SLASH
                , DOUBLE_BACK_SLASH
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
     * @see WebArchiveBuilder#createWebArchive(Properties, IProgressMonitor)
     * @since 7.1
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

            // Get the build directory and create it if it doesn't already
            // exist.
            final String webServicePluginPath = WebServiceLibPlugin.getDefault().getInstallPath().toOSString();
            final String buildDirectoryName = webServicePluginPath + File.separator + WebArchiveBuilderConstants.BUILD_DIR;
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
            // Create the images directory.
            final String imagesDirectoryName = contextDirectoryName + File.separator + IMAGES;
            final File imagesDirectory = new File(imagesDirectoryName);
            imagesDirectory.mkdir();
            // Create the WEB-INF directory.
            final String webInfDirectoryName = contextDirectoryName + File.separator + WEB_INF;
            final File webInfDirectory = new File(webInfDirectoryName);
            webInfDirectory.mkdir();
           //  Create the WEB-INF/wsdl directory.
            final String webInfWsdlDirectoryName = webInfDirectoryName + File.separator + WebServiceLibConstants.WSDL;
            final File webInfWsdlDirectory = new File(webInfWsdlDirectoryName);
            webInfWsdlDirectory.mkdir();
            // Create the classes directory.
            final String webInfClassesDirectoryName = webInfDirectoryName + File.separator + CLASSES;
            // Create the WEB-INF/lib directory.
            final String webInfLibDirectoryName = webInfDirectoryName + File.separator + LIB;
            final File webInfClassesDirectory = new File(webInfClassesDirectoryName);
            final File webInfLibDirectory = new File(webInfLibDirectoryName);
            webInfLibDirectory.mkdir();
            webInfClassesDirectory.mkdir();
            monitor.worked(10);

            monitor.subTask(TASK_CREATING_WSDL_FILE);
            // Create WSDL file
            boolean containsGlobalDataTypes = generateWsdl(properties, webInfWsdlDirectory);

            monitor.subTask(TASK_COPYING_FILES);
            // Copy the Web files.
            getWebFiles(contextDirectory, webInfDirectory, containsGlobalDataTypes);

            // Encapsulate the security properties in order to ensure they are valid
            SecurityCredentials securityCredentials = new SecurityCredentials(properties);
            if (securityCredentials.hasType(WarDeploymentInfoPanel.BASIC)) {
                // Replace the variables in the jboss-web.xml file.
                replaceJBossWebXmlVariables(webInfDirectoryName, securityCredentials.getSecurityRealm());
            }

            // Replace the variables in the web.xml file.
            replaceWebXmlVariables(webInfDirectoryName, securityCredentials, contextName);
            // Replace the variables in the jbossws-cxf.xml file.
            replaceJBossWSCXFVariables(webInfDirectoryName, properties, securityCredentials);
            // Create properties file and write to classes root.
            createPropertiesFile(webInfClassesDirectory, properties);
            // Create and compile Provider files (one per port).
            createProviderJavaClasses(webInfLibDirectory, webInfClassesDirectory, properties, securityCredentials);

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
     * @since 7.1
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
     * @since 7.1
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
     * @since 7.1
     */
    private void getWebFiles( File contextDirectory, File webInfDirectory,
                              boolean containsGlobalDataTypes) throws Exception {

        // Copy all of the Web files
        final String webLibPath = WebServiceLibPlugin.getDefault().getSoapWebLibDirectoryPath();
        final String webAppsDirectoryName = webLibPath + File.separator + WEBAPPS;
        final File webAppsDirectory = new File(webAppsDirectoryName);
        FileUtils.copyRecursively(webAppsDirectory, contextDirectory, null, false);
        
        //Delete builtInDataType.xsd if not applicable to this service
        if (!containsGlobalDataTypes){
        	File builtInDataTypeSchemaFile = new File(webInfDirectory.getPath() + "/wsdl/" + DatatypeConstants.DATATYPES_MODEL_FILE_NAME); //$NON-NLS-1$
        	builtInDataTypeSchemaFile.delete();
        }
       
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
     * @since 7.1
     */
    private void replaceWebXmlVariables( String webInfDirectoryName,
                                           SecurityCredentials securityCredentials,
                                           String contextName ) throws Exception {

        // Replace variables in the web.xml file.
        File webXmlFile = new File(webInfDirectoryName + File.separator + WEB_XML);

        // Update for Basic Auth if HTTPBasic security is selected
        if (securityCredentials.hasType(WarDeploymentInfoPanel.BASIC)) {
            AntTasks.replace(webXmlFile, "<!--<security-constraint>", "\t<security-constraint>"); //$NON-NLS-1$ //$NON-NLS-2$
            AntTasks.replace(webXmlFile, "${realmName}", securityCredentials.getSecurityRealm()); //$NON-NLS-1$
            AntTasks.replace(webXmlFile, "${roleName}", securityCredentials.getSecurityRole()); //$NON-NLS-1$
            AntTasks.replace(webXmlFile, "</login-config>-->", "</login-config>"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            AntTasks.replace(webXmlFile, "<!--${basic_auth}-->", CoreStringUtil.Constants.EMPTY_STRING); //$NON-NLS-1$
        }

        AntTasks.replace(webXmlFile, "${warname}", contextName); //$NON-NLS-1$
        AntTasks.replace(webXmlFile, "${servlets}", createServletTags(getPorts())); //$NON-NLS-1$
    }

    /**
     * Replace the variables in the web.xml file with their appropriate values.
     * 
     * @param webInfDirectoryName
     * @param securityDomain
     *
     * @since 7.1
     */
    private void replaceJBossWebXmlVariables( String webInfDirectoryName,
                                                String securityDomain ) {

        // Replace variables in the jboss-web.xml file.
        File jbossWebXmlFile = new File(webInfDirectoryName + File.separator + JBOSS_WEB_XML);

        String securityDomainNode = "<security-domain>java:/jaas/" + securityDomain + "</security-domain>"; //$NON-NLS-1$ //$NON-NLS-2$

        AntTasks.replace(jbossWebXmlFile,
                         "<!--<security-domain>java:/jaas/teiid-security</security-domain>-->", securityDomainNode); //$NON-NLS-1$
    }

    /**
     * Replace the variables in the jbossws-cxf.xml file with their appropriate values.
     * 
     * @param webInfDirectoryName
     * @param properties
     * @param securityCredentials
     * @since 7.1
     */
    private void replaceJBossWSCXFVariables( String webInfDirectoryName,
                                               Properties properties, SecurityCredentials securityCredentials ) {

        // Replace variables in the jbossws-cxf.xml file.
        File jbossWSCxfXMLFile = new File(webInfDirectoryName + File.separator + JBOSS_WEB_CXF);

        AntTasks.replace(jbossWSCxfXMLFile, "${jaxws.endpoint}", createEndpointTags(properties, securityCredentials, getPorts())); //$NON-NLS-1$

        if (securityCredentials.hasType(WarDeploymentInfoPanel.WSSE)) {
            AntTasks.replace(jbossWSCxfXMLFile, "<!--<bean ", "<bean "); //$NON-NLS-1$ //$NON-NLS-2$
            AntTasks.replace(jbossWSCxfXMLFile, "</bean>-->", "</bean>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

    /**
     * Create the teiidsoap.properties file.
     * 
     * @param webInfClassesDirectory
     * @param properties
     * @since 7.1
     */
    private void createPropertiesFile( File webInfClassesDirectory,
                                         Properties properties ) throws IOException {

        // Create teiidsoap.properties file
        File teiisSoapProperties = new File(webInfClassesDirectory + File.separator + TEIID_SOAP_PROPS);
        String jndiValue = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME);

        if (jndiValue != null) {
            jndiValue = JndiUtil.addJavaPrefix(jndiValue);
        }

        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            // Create file
            fstream = new FileWriter(teiisSoapProperties);
            out = new BufferedWriter(fstream);
            Iterator it = operationToProcedureMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                out.write(pairs.getKey() + EQUALS + pairs.getValue() + NEW_LINE);
            }
            out.write(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME + EQUALS + jndiValue);
        } finally {
            // Close the output stream
            out.close();
        }
    }

    /**
     * Create the WSProvider java classes (one for each port)
     *
     * @param webInfClassesDirectory
     * @param properties
     * @param securityCredentials
     * @since 7.1
     */
	private void createProviderJavaClasses( File webInfLibDirectory,
	                                          File webInfClassesDirectory,
	                                          Properties properties,
	                                          SecurityCredentials securityCredentials ) throws Exception {
	
	    String pathToProviders = "/org" + File.separator + "teiid" + File.separator + "soap" + File.separator + "provider"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	    String pathToCallback = "/org" + File.separator + "teiid" + File.separator + "soap" + File.separator + "wsse"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	    String pathToPlugin = "/org" + File.separator + "teiid" + File.separator + "soap"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
	    final String tns = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WSDL_TNS);
	    String context = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
	    
	    
	    List<File> portProviders = new ArrayList<File>();
	    for (String port : getPorts()) {
	        String providerJavaFilePath = webInfClassesDirectory.getCanonicalPath() + pathToProviders + File.separator + port
	                                      + ".java"; //$NON-NLS-1$
	        FileUtils.copy(webInfClassesDirectory.getCanonicalPath() + pathToProviders + File.separator + "ProviderTemplate.java", //$NON-NLS-1$
	                       providerJavaFilePath,
	                       true);
	        File providerJavaFile = new File(providerJavaFilePath);
	        portProviders.add(providerJavaFile);
	        AntTasks.replace(providerJavaFile, "${className}", port); //$NON-NLS-1$
	        AntTasks.replace(providerJavaFile, "${targetNamespace}", tns); //$NON-NLS-1$
	        AntTasks.replace(providerJavaFile, "${portName}", port); //$NON-NLS-1$
	        AntTasks.replace(providerJavaFile, "${serviceName}", context); //$NON-NLS-1$
	        AntTasks.replace(providerJavaFile, "${wsdlFileName}", this.wsdlFilename); //$NON-NLS-1$
	    }
	
	    File template = new File(webInfClassesDirectory.getCanonicalPath() + pathToProviders + File.separator
	                             + "ProviderTemplate.java"); //$NON-NLS-1$
	
	    File soapPlugin = new File(webInfClassesDirectory.getCanonicalPath() + pathToPlugin + File.separator + "SoapPlugin.java"); //$NON-NLS-1$
	
	    File teiidProvider = new File(webInfClassesDirectory.getCanonicalPath() + pathToProviders + File.separator
	                                  + "TeiidWSProvider.java"); //$NON-NLS-1$
	
	    File usernameCallback = new File(webInfClassesDirectory.getCanonicalPath() + pathToCallback + File.separator
	                                     + "UsernamePasswordCallback.java"); //$NON-NLS-1$
	
	    if (securityCredentials.hasType(WarDeploymentInfoPanel.WSSE)) {
	        AntTasks.replace(usernameCallback,
	                         "${username}", (String)properties.get(WebArchiveBuilderConstants.PROPERTY_SECURITY_USERNAME)); //$NON-NLS-1$
	        AntTasks.replace(usernameCallback,
	                         "${password}", (String)properties.get(WebArchiveBuilderConstants.PROPERTY_SECURITY_PASSWORD)); //$NON-NLS-1$
	    }
	
	    template.delete();
	    // Compile classes
	    JavaCompiler compilerTool = ToolProvider.getSystemJavaCompiler();
	    if (compilerTool != null) {
	        StandardJavaFileManager fileManager = compilerTool.getStandardFileManager(null, null, null);
	
	        String pathToWSSEJar = webInfLibDirectory.getCanonicalPath() + File.separator + "wss4j.jar"; //$NON-NLS-1$
	
	        File wsseJar = new File(pathToWSSEJar);
	        List<File> classPaths = Arrays.asList(wsseJar);
	        fileManager.setLocation(StandardLocation.CLASS_PATH, classPaths);
	
	        // prepare the source files to compile
	        List<File> sourceFileList = new ArrayList<File>();
	        sourceFileList.add(soapPlugin);
	        sourceFileList.add(teiidProvider);
	        if (securityCredentials.hasType(WarDeploymentInfoPanel.WSSE)) {
	            sourceFileList.add(usernameCallback);
	        }
	        for (File providerClass : portProviders) {
	            sourceFileList.add(providerClass);
	        }
	
	        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
	        /*Create a diagnostic controller, which holds the compilation problems*/
	        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	        CompilationTask task = compilerTool.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
	        task.call();
	        List<Diagnostic<? extends JavaFileObject>> diagnosticList = diagnostics.getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticList) {
                diagnostic.getKind();
                if (diagnostic.getKind().equals(Kind.ERROR)) {
                    throw new Exception(diagnostic.getMessage(null));
                }
            }
	        
	        fileManager.close();
	
	        // Cleanup wsse.jar. Only needed for dynamic compilation.
	        wsseJar.delete();
	        webInfLibDirectory.delete();
	    }
	}

    private boolean isUseMTOM( Properties properties ) {
        Object useMotem = properties.get(WebArchiveBuilderConstants.PROPERTY_USE_MTOM);
        return useMotem == null ? false : useMotem.equals(Boolean.TRUE);
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
     * @param properties
     * @param webinfWsdlFolder
     * @return status of wsdl generation
     * @throws IOException
     * @throws CoreException
     */
    public boolean generateWsdl( Properties properties,
                              File webinfWsdlFolder) throws IOException, CoreException {

        BasicWsdlGenerator wsdlGenerator = new BasicWsdlGenerator();
        Resource wsModel = null;
        final String contextName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
        final String host = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_HOST);
        final String port = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_PORT);
        final String tns = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WSDL_TNS);

        String webServiceName = contextName;

        String vdbFileName = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_VDB_FILE_NAME);
        IPath vdbPath = new Path(vdbFileName);
        IFile vdbFile = ModelerCore.getWorkspace().getRoot().getFile(vdbPath);
        boolean hasGlobalDataType = false;
        
    	VdbResourceFinder vdbResourceFinder = new VdbResourceFinder(vdbFile);
        
        for (Resource webServiceModel : vdbResourceFinder.getWebServiceResources() ) {
            try {
                wsModel = webServiceModel;
                wsdlGenerator.addWebServiceModel(wsModel);
                ArrayList<Resource> dependentSchemas = new ArrayList<Resource>();
                Resource[] resources = vdbResourceFinder.getDependentResources(wsModel);
                vdbResourceFinder.getAllDependentSchemas(resources, dependentSchemas);
                for (Resource resource : dependentSchemas) {
                	
                    if (! ModelUtil.isXsdFile(resource))
                        continue;

                    String fullFilePath = resource.getURI().path();
                    String fileNameWithExtension = new Path(fullFilePath).lastSegment();
                    IPath fileLocationPath = new Path(fullFilePath).removeLastSegments(1);

                    // Copy the XSD file to the classes folder
                    XSDSchema xsdSchema = importSchema(fullFilePath);

                    // Check for an import of the global data types schema
                    if (containsGlobalDataTypeImport(xsdSchema)) {
                        hasGlobalDataType = true;
                        // Copy iResource to classesFolder and WEB-INF/wsdl
                        File xsd = new File(fullFilePath);
                        FileUtils.copy(xsd, webinfWsdlFolder, true);
                        // Get handle to new file in wsdl folder
                        File xsdCopy = new File(webinfWsdlFolder.getPath() + FORWARD_SLASH + fileNameWithExtension);
                        // Replace the schemaLocation of the global data
                        // types schema import with the relative path to xsd
                        AntTasks.replace(xsdCopy,
                                         "schemaLocation=\"http://www.metamatrix.com/metamodels/SimpleDatatypes-instance\"", //$NON-NLS-1$
                                         "schemaLocation=\"builtInDataTypes.xsd\""); //$NON-NLS-1$ 
                    } else {
                        File schemaFile = new File(fullFilePath);
                        FileUtils.copy(schemaFile, webinfWsdlFolder, true);
                    }

                    wsdlGenerator.addXsdModel(xsdSchema, fileLocationPath);
                }
            } catch ( Exception e ) {
            	vdbResourceFinder.dispose();
            	throw ErrorHandler.toCoreException(e);
            }
        }
        
        vdbResourceFinder.dispose();
        
        wsdlGenerator.setName(webServiceName);
        wsdlGenerator.setTargetNamespace(tns);
        wsdlGenerator.setUrlRootForReferences(CoreStringUtil.Constants.EMPTY_STRING);
        wsdlGenerator.setUrlSuffixForReferences(CoreStringUtil.Constants.EMPTY_STRING);
        wsdlGenerator.setUrlForWsdlService("http://" + host + ":" + port + FORWARD_SLASH + contextName + FORWARD_SLASH); //$NON-NLS-1$ //$NON-NLS-2$
        final IStatus status = wsdlGenerator.generate(new NullProgressMonitor());

        // nothing more to do if an error is expected
        if (status.getSeverity() == IStatus.ERROR) {
            throw new RuntimeException("Unable to generate WSDL"); //$NON-NLS-1$
        }

        String fileName = webServiceName + ".wsdl"; //$NON-NLS-1$ 
        wsdlFilename = fileName;
        try {
            // Create our WSDL file and write to it
            OutputStream stream = new FileOutputStream(new File(webinfWsdlFolder, fileName));
            wsdlGenerator.write(stream);
            // Get an iFile instance to refresh our workspace
            IFile iFile = vdbFile;
            iFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            setPorts(wsdlGenerator.getPorts());
            setOperationToProcedureMap(wsdlGenerator.getOperationToProcedureMap());

        } catch (Exception e) {
            throw ErrorHandler.toCoreException(e);
        }

        return hasGlobalDataType;

    }

    private boolean containsGlobalDataTypeImport( final XSDSchema schema ) {
        boolean containsImport = false;
        if (schema != null) {
            final List<XSDSchemaContent> contents = schema.getContents();
            for (int i = 0; i < contents.size(); i++) {
                final Object content = contents.get(i);
                if (content instanceof XSDImport) {
                    if (DatatypeConstants.BUILTIN_DATATYPES_URI.equals(((XSDImport)content).getNamespace())) {
                        containsImport = true;
                        break;
                    }
                }
            }

        }
        return containsImport;
    }

    private XSDSchema importSchema( String path ) {
        XSDParser parser = new XSDParser(null);
        parser.parse(path);
        XSDSchema schema = parser.getSchema();
        schema.setSchemaLocation(path);
        return schema;
    }

    private String createServletTags( List<String> tags ) {
        StringBuffer servletTags = new StringBuffer();
        String startServlet = "\t<servlet>\n"; //$NON-NLS-1$
        String endServlet = "\t</servlet>\n"; //$NON-NLS-1$
        String startServletName = "\t\t<servlet-name>"; //$NON-NLS-1$
        String endServletName = "</servlet-name>\n"; //$NON-NLS-1$
        String startServletClass = "\t\t<servlet-class>org.teiid.soap.provider."; //$NON-NLS-1$
        String endServletClass = "</servlet-class>\n\t\t<load-on-startup>1</load-on-startup>\n"; //$NON-NLS-1$
        String startServletMapping = "\t<servlet-mapping>\n"; //$NON-NLS-1$
        String endServletMapping = "\t</servlet-mapping>\n"; //$NON-NLS-1$
        String startUrlPattern = "\t\t<url-pattern>/"; //$NON-NLS-1$
        String endUrlPattern = "</url-pattern>\n"; //$NON-NLS-1$

        for (String port : tags) {
            servletTags.append(startServlet).append(startServletName).append(port).append(endServletName);
            servletTags.append(startServletClass).append(port).append(endServletClass).append(endServlet);
            servletTags.append(startServletMapping).append(startServletName).append(port).append(endServletName);
            servletTags.append(startUrlPattern).append(port).append(endUrlPattern).append(endServletMapping);
        }

        return servletTags.toString();
    }

    private String createEndpointTags( Properties properties, SecurityCredentials securityCredentials,
                                                                 List<String> tags ) {

        final String contextName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
        final String tns = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WSDL_TNS);

        StringBuffer endpointTags = new StringBuffer();
        String startJaxwsEndpoint = "\t<jaxws:endpoint id="; //$NON-NLS-1$
        String endJaxwsEndpoint = "\t</jaxws:endpoint>\n"; //$NON-NLS-1$
        String serviceName = " serviceName=\"s:"; //$NON-NLS-1$
        String implementor = "\n\t\timplementor=\"org.teiid.soap.provider."; //$NON-NLS-1$
        String wsdlLocation = " \n\t\twsdlLocation=\"WEB-INF/wsdl/"; //$NON-NLS-1$
        String namespace = " xmlns:s=\""; //$NON-NLS-1$
        String wsseInterceptor1 = "\t\t<jaxws:inInterceptors>\n\t\t\t<ref bean=\"UsernameToken_Request\" />\n\t\t\t<bean "; //$NON-NLS-1$
        String wsseInterceptor2 = "class=\"org.apache.cxf.binding.soap.saaj.SAAJInInterceptor\" />\n\t\t</jaxws:inInterceptors>\n"; //$NON-NLS-1$
        String useMTOM = "\t\t<jaxws:properties>\n\t\t\t<entry key=\"mtom-enabled\" value=\"true\"/>\n\t\t</jaxws:properties>\n"; //$NON-NLS-1$

        for (String port : tags) {
            endpointTags.append(startJaxwsEndpoint).append("\"").append(port).append("\"").append(serviceName); //$NON-NLS-1$ //$NON-NLS-2$
            endpointTags.append(contextName).append("\""); //$NON-NLS-1$ 
            endpointTags.append(implementor).append(port).append("\"").append(wsdlLocation).append(wsdlFilename).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
            endpointTags.append(namespace).append(tns).append("\">\n"); //$NON-NLS-1$
            if (securityCredentials.hasType(WarDeploymentInfoPanel.WSSE)) {
                endpointTags.append(wsseInterceptor1).append(wsseInterceptor2);
            }
            if (isUseMTOM(properties)) {
                endpointTags.append(useMTOM);
            }
            endpointTags.append(endJaxwsEndpoint);
        }

        return endpointTags.toString();
    }
}
