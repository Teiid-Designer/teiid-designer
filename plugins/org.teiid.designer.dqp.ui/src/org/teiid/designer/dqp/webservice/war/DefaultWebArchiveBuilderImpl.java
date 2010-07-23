/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;
import org.teiid.core.util.StringUtil;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.webservice.gen.BasicWsdlGenerator;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.modeler.webservice.util.AntTasks;

/**
 * This is the default implementation of a WebArchiveBuilder.
 * 
 * @since 7.1
 */
public class DefaultWebArchiveBuilderImpl implements WebArchiveBuilder {

    private IPath webServicePluginPath = null;
    private List<String> ports = new ArrayList<String>();
    private Map<String, String> operationToProcedureMap = new HashMap<String, String>();
    private String wsdlFilename = StringUtil.Constants.EMPTY_STRING;

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

    public IStatus validateContextName( String contextName ) {

        try {
            final String URL_BASE = "http://www.teiid.org/"; //$NON-NLS-1$       
            final URLValidator urlValidator = new URLValidator();

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
     * @see com.metamatrix.modeler.dataservices.lds.WebArchiveBuilder#createWebArchive(java.io.InputStream, java.util.Map)
     * @since 7.1
     */
    public IStatus createWebArchive( Properties properties,
                                     IProgressMonitor monitor ) {

        try {

            // Get and validate the context name.
            final String contextName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
            IStatus status = validateContextName(contextName);

            if (IStatus.ERROR == status.getSeverity()) {

                throw new Exception(status.getException());
            }

            // Get the build directory and create it if it doesn't already exist.
            final String webServicePluginPath = getWSPluginInstallPath().toOSString();
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
            // Create the classes directory.
            final String webInfClassesDirectoryName = webInfDirectoryName + File.separator + "classes"; //$NON-NLS-1$
            final File webInfClassesDirectory = new File(webInfClassesDirectoryName);
            webInfClassesDirectory.mkdir();
            monitor.worked(10);

            monitor.subTask(TASK_CREATING_WSDL_FILE);
            // Create WSDL file
            generateWsdl(properties, webInfClassesDirectory);

            monitor.subTask(TASK_COPYING_FILES);
            // Copy the Web files.
            getWebFiles(contextDirectory, webInfDirectory);
            // Replace the variables in the web.xml file.
            replaceWebXmlVariables(webInfDirectoryName, properties, contextName);
            // Replace the variables in the jbossws-cxf.xml file.
            replaceJBossWSCXFVariables(webInfDirectoryName, properties);
            // Create properties file and write to classes root.
            createPropertiesFile(webInfClassesDirectory, properties);
            // Create and compile Provider files (one per port).
            createProviderJavaClasses(webInfClassesDirectory, properties);

            monitor.worked(10);

            monitor.subTask(TASK_CREATING_WAR_ARCHIVE);
            // ZIP everything in the context directory into the new WAR file.
            final String warFileName = tempDirectoryName + File.separator + contextName + ".war"; //$NON-NLS-1$
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
        if (!fileName.endsWith("/") && !fileName.endsWith("\\")) { //$NON-NLS-1$ //$NON-NLS-2$

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
    private void getWebFiles( File contextDirectory,
                              File webInfDirectory ) throws Exception {

        // Copy all of the Web files
        final String webLibPath = getWebLibDirectoryPath();
        final String webAppsDirectoryName = webLibPath + File.separator + "webapps"; //$NON-NLS-1$
        final File webAppsDirectory = new File(webAppsDirectoryName);
        FileUtils.copyRecursively(webAppsDirectory, contextDirectory, null, false);
    }

    /**
     * Replace the variables in the web.xml file with their appropriate values.
     * 
     * @param webInfDirectoryName
     * @param properties
     * @param contextName
     * @since 7.1
     */
    protected void replaceWebXmlVariables( String webInfDirectoryName,
                                           Properties properties,
                                           String contextName ) {

        // Replace variables in the web.xml file.
        File webXmlFile = new File(webInfDirectoryName + File.separator + "web.xml"); //$NON-NLS-1$

        AntTasks.replace(webXmlFile, "${warname}", contextName); //$NON-NLS-1$
        AntTasks.replace(webXmlFile, "${servlets}", createServletTags(getPorts())); //$NON-NLS-1$

    }

    /**
     * Replace the variables in the jbossws-cxf.xml file with their appropriate values.
     * 
     * @param webInfDirectoryName
     * @param properties
     * @since 7.1
     */
    protected void replaceJBossWSCXFVariables( String webInfDirectoryName,
                                               Properties properties ) {

        // Replace variables in the jbossws-cxf.xml file.
        File jbossWSCxfXMLFile = new File(webInfDirectoryName + File.separator + "jbossws-cxf.xml"); //$NON-NLS-1$

        AntTasks.replace(jbossWSCxfXMLFile, "${jaxws.endpoint}", createEndpointTags(properties, getPorts())); //$NON-NLS-1$

    }

    /**
     * Create the teiidsoap.properties file.
     * 
     * @param webInfClassesDirectory
     * @param properties
     * @since 7.1
     */
    protected void createPropertiesFile( File webInfClassesDirectory,
                                         Properties properties ) throws IOException {

        // Create teiidsoap.properties file
        File teiisSoapProperties = new File(webInfClassesDirectory + File.separator + "teiidsoap.properties"); //$NON-NLS-1$
        String jndiValue = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME);

        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            // Create file
            fstream = new FileWriter(teiisSoapProperties);
            out = new BufferedWriter(fstream);
            Iterator it = operationToProcedureMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                out.write(pairs.getKey() + "=" + pairs.getValue() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.write(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME + "=" + jndiValue); //$NON-NLS-1$
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
     * @since 7.1
     */
    protected void createProviderJavaClasses( File webInfClassesDirectory,
                                              Properties properties ) throws IOException {

        String pathToProviders = "/org" + File.separator + "teiid" + File.separator + "soap" + File.separator + "provider"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String pathToPlugin = "/org" + File.separator + "teiid" + File.separator + "soap"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        final String tns = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WSDL_TNS);
        String vdbFileName = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_VDB_FILE_NAME);
        File vdbFile = new File(FileUtils.getFilenameWithoutExtension(vdbFileName));

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
            AntTasks.replace(providerJavaFile, "${serviceName}", vdbFile.getName()); //$NON-NLS-1$
            AntTasks.replace(providerJavaFile, "${wsdlFileName}", this.wsdlFilename); //$NON-NLS-1$
        }

        File template = new File(webInfClassesDirectory.getCanonicalPath() + pathToProviders + File.separator
                                 + "ProviderTemplate.java"); //$NON-NLS-1$

        File soapPlugin = new File(webInfClassesDirectory.getCanonicalPath() + pathToPlugin + File.separator + "SoapPlugin.java"); //$NON-NLS-1$

        File teiidProvider = new File(webInfClassesDirectory.getCanonicalPath() + pathToProviders + File.separator
                                      + "TeiidWSProvider.java"); //$NON-NLS-1$

        template.delete();
        // Compile classes
        JavaCompiler compilerTool = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compilerTool.getStandardFileManager(null, null, null);

        List<File> sourcePath = Arrays.asList(webInfClassesDirectory);
        List<String> compilerOptions = Arrays.asList("-g"); //$NON-NLS-1$ Add debug option
        fileManager.setLocation(StandardLocation.SOURCE_PATH, sourcePath);
        // prepare the source files to compile
        List<File> sourceFileList = new ArrayList<File>();
        sourceFileList.add(soapPlugin);
        sourceFileList.add(teiidProvider);
        for (File providerClass : portProviders) {
            sourceFileList.add(providerClass);
        }

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
        CompilationTask task = compilerTool.getTask(null, fileManager, null, compilerOptions, null, compilationUnits);
        task.call();
        fileManager.close();
    }

    /**
     * Return the path to the directory containing the lds.jar file
     * 
     * @return
     * @throws Exception
     * @since 7.1
     */
    private String getWebLibDirectoryPath() throws Exception {
        final String webServicePluginPath = getWSPluginInstallPath().toOSString();
        final String webServiceLibFolder = webServicePluginPath + File.separator + "war_resources"; //$NON-NLS-1$
        if (new File(webServiceLibFolder).exists()) {
            return webServiceLibFolder;
        }
        final String msg = WebServicePlugin.Util.getString("DefaultWebArchiveBuilderImpl.web_lib_directory_does_not_exist", webServiceLibFolder); //$NON-NLS-1$
        WebServicePlugin.Util.log(IStatus.ERROR, msg);
        throw new FileNotFoundException(msg);
    }

    public IPath getWSPluginInstallPath() throws IOException {
        if (this.webServicePluginPath == null) {
            URL url = FileLocator.find(WebServicePlugin.getInstance().getBundle(), new Path(""), null); //$NON-NLS-1$
            url = FileLocator.toFileURL(url);
            this.webServicePluginPath = new Path(url.getFile());
        }

        return (IPath)this.webServicePluginPath.clone();
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

    public void generateWsdl( Properties properties,
                              File classesFolder ) throws IOException {

        BasicWsdlGenerator wsdlGenerator = new BasicWsdlGenerator();
        ModelResource wsModel = null;
        final String contextName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
        final String host = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_HOST);
        final String port = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WAR_PORT);
        final String tns = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WSDL_TNS);
        final ArrayList<ModelResource> modelsArrayList = (ArrayList<ModelResource>)properties.get(WebArchiveBuilderConstants.PROPERTY_VDB_WS_MODELS);
        String webServiceName = contextName;
        for (ModelResource webServiceModel : modelsArrayList) {
            try {
                wsModel = webServiceModel;
                wsdlGenerator.addWebServiceModel(webServiceModel.getEmfResource());
                IResource[] iResources = WorkspaceResourceFinderUtil.getDependentResources(webServiceModel.getResource());
                for (IResource iResource : iResources) {
                    if (ModelIdentifier.isSchemaModel(iResource)) {
                        // Copy the XSD file to the classes folder
                        FileUtils.copy(new File(iResource.getLocation().toOSString()), classesFolder, true);
                        wsdlGenerator.addXsdModel(importSchema(iResource.getLocation().toOSString()), iResource.getLocation());
                    }
                }
            } catch (ModelWorkspaceException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        wsdlGenerator.setName(webServiceName);
        wsdlGenerator.setTargetNamespace(tns);
        wsdlGenerator.setUrlRootForReferences(StringUtil.Constants.EMPTY_STRING);
        wsdlGenerator.setUrlSuffixForReferences(StringUtil.Constants.EMPTY_STRING);
        wsdlGenerator.setUrlForWsdlService("http://" + host + ":" + port + "/" + contextName + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final IStatus status = wsdlGenerator.generate(new NullProgressMonitor());

        // nothing more to do if an error is expected
        if (status.getSeverity() == IStatus.ERROR) {
            throw new RuntimeException("Unable to generate WSDL"); //$NON-NLS-1$
        }

        String fileName = webServiceName + ".wsdl"; //$NON-NLS-1$ 
        wsdlFilename = fileName;
        try {
            // Create our WSDL file and write to it
            OutputStream stream = new FileOutputStream(new File(classesFolder, fileName));
            wsdlGenerator.write(stream);
            // Get an iFile instance to refresh our workspace
            IFile iFile = wsModel.getModelProject().getProject().getFile(fileName);
            iFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            setPorts(wsdlGenerator.getPorts());
            setOperationToProcedureMap(wsdlGenerator.getOperationToProcedureMap());

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (CoreException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public XSDSchema importSchema( String path ) {
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

    private String createEndpointTags( Properties properties,
                                       List<String> tags ) {

        final String contextName = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME);
        final String tns = (String)properties.get(WebArchiveBuilderConstants.PROPERTY_WSDL_TNS);

        StringBuffer endpointTags = new StringBuffer();
        String startJaxwsEndpoint = "\t<jaxws:endpoint id="; //$NON-NLS-1$
        String endJaxwsEndpoint = "\t</jaxws:endpoint>\n"; //$NON-NLS-1$
        String serviceName = " serviceName=\"s:"; //$NON-NLS-1$
        String implementor = "\n\t\timplementor=\"org.teiid.soap.provider."; //$NON-NLS-1$
        String wsdlLocation = " \n\t\twsdlLocation=\"classpath:"; //$NON-NLS-1$
        String namespace = " xmlns:s=\""; //$NON-NLS-1$
        String address = " \n\t\taddress=\""; //$NON-NLS-1$

        for (String port : tags) {
            endpointTags.append(startJaxwsEndpoint).append("\"").append(port).append("\"").append(serviceName); //$NON-NLS-1$ //$NON-NLS-2$
            endpointTags.append(contextName).append("\""); //$NON-NLS-1$ 
            endpointTags.append(implementor).append(port).append("\"").append(wsdlLocation).append(wsdlFilename).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
            endpointTags.append(namespace).append(tns).append("\"").append(address).append("/"); //$NON-NLS-1$ //$NON-NLS-2$
            endpointTags.append(contextName).append("/").append(port).append("\">\n").append(endJaxwsEndpoint); //$NON-NLS-1$ //$NON-NLS-2$ 
        }

        return endpointTags.toString();
    }
}
