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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.dqp.webservice.war.objects.RestProcedure;
import org.teiid.designer.dqp.webservice.war.util.WarArchiveUtil;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.modeler.webservice.util.AntTasks;

/**
 * This is the default implementation of a WebArchiveBuilder.
 * 
 * @since 7.4
 */
public class RestWebArchiveBuilderImpl implements WebArchiveBuilder {

    private IPath webServicePluginPath = null;
    private List<String> models = new ArrayList<String>();
    private Map<String, List<RestProcedure>> modelMapOfProcedures = new HashMap<String, List<RestProcedure>>();
    File webXmlFile = null;
    public static String newline = System.getProperty("line.separator"); //$NON-NLS-1$

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
            final String webServicePluginPath = getWSPluginInstallPath().toOSString();
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
            final String webInfDirectoryName = contextDirectoryName + File.separator + "WEB-INF"; //$NON-NLS-1$
            final File webInfDirectory = new File(webInfDirectoryName);
            webInfDirectory.mkdir();
            // Create the classes directory.
            final String webInfClassesDirectoryName = webInfDirectoryName + File.separator + "classes"; //$NON-NLS-1$
            // Create the classes directory.
            final String webInfLibDirectoryName = webInfDirectoryName + File.separator + "lib"; //$NON-NLS-1$
            final File webInfClassesDirectory = new File(webInfClassesDirectoryName);
            final File webInfLibDirectory = new File(webInfLibDirectoryName);
            webInfLibDirectory.mkdir();
            webInfClassesDirectory.mkdir();
            monitor.worked(10);

            monitor.subTask(TASK_COPYING_FILES);
            // Copy the Web files.
            getWebFiles(contextDirectory, webInfDirectory);

            // Replace the variables in the web.xml file.
            replaceWebXmlVariables(webInfDirectoryName, properties, contextName);
            // Create properties file and write to classes root.
            createPropertiesFile(webInfClassesDirectory, properties);
            // Create and compile Provider files (one per port).
            createResourceJavaClasses(webInfLibDirectory, webInfClassesDirectory, properties);

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
     * @since 7.4
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
     * @since 7.4
     */
    protected void replaceWebXmlVariables( String webInfDirectoryName,
                                           Properties properties,
                                           String contextName ) {

        // Replace variables in the web.xml file.
        webXmlFile = new File(webInfDirectoryName + File.separator + "web.xml"); //$NON-NLS-1$

        AntTasks.replace(webXmlFile, "${contextName}", contextName); //$NON-NLS-1$
    }

    /**
     * Create the teiidrest.properties file.
     * 
     * @param webInfClassesDirectory
     * @param properties
     * @since 7.4
     */
    protected void createPropertiesFile( File webInfClassesDirectory,
                                         Properties properties ) throws IOException {

        // Create teiidsoap.properties file
        File teiidRestProperties = new File(webInfClassesDirectory + File.separator + "teiidrest.properties"); //$NON-NLS-1$
        String jndiValue = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME);

        if (jndiValue != null && !jndiValue.startsWith(JNDI_PREFIX)) {
            jndiValue = JNDI_PREFIX + jndiValue;
        }

        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            // Create file
            fstream = new FileWriter(teiidRestProperties);
            out = new BufferedWriter(fstream);
            out.write(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME + "=" + jndiValue); //$NON-NLS-1$
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
    protected void createResourceJavaClasses( File webInfLibDirectory,
                                              File webInfClassesDirectory,
                                              Properties properties ) throws Exception {

        String pathToResource = "/org" + File.separator + "teiid" + File.separator + "rest" + File.separator + "services"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String pathToPlugin = "/org" + File.separator + "teiid" + File.separator + "rest"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

        List<File> resources = new ArrayList<File>();
        StringBuilder singletonSb = new StringBuilder();
        for (String resource : getModels()) {
            String resourceJavaFilePath = webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator + resource
                                          + ".java"; //$NON-NLS-1$
            FileUtils.copy(webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator + "ResourceTemplate.java", //$NON-NLS-1$
                           resourceJavaFilePath,
                           true);
            File resourceJavaFile = new File(resourceJavaFilePath);
            resources.add(resourceJavaFile);
            AntTasks.replace(resourceJavaFile, "${className}", resource); //$NON-NLS-1$
            AntTasks.replace(resourceJavaFile, "${modelName}", "org.teiid.rest.services." + resource); //$NON-NLS-1$ //$NON-NLS-2$
            AntTasks.replace(resourceJavaFile, "${path}", "@Path( \"" + resource + "\" )"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            String methods = generateMethods(resource);
            AntTasks.replace(resourceJavaFile, "${httpMethods}", methods); //$NON-NLS-1$

            singletonSb.append(newline + "singletons.add(new org.teiid.rest.services." + resource + "());"); //$NON-NLS-1$ //$NON-NLS-2$

        }

        File template = new File(webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator
                                 + "ResourceTemplate.java"); //$NON-NLS-1$

        File restPlugin = new File(webInfClassesDirectory.getCanonicalPath() + pathToPlugin + File.separator + "RestPlugin.java"); //$NON-NLS-1$

        File teiidProvider = new File(webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator
                                      + "TeiidRSProvider.java"); //$NON-NLS-1$

        File teiidRestApplication = new File(webInfClassesDirectory.getCanonicalPath() + pathToResource + File.separator
                                             + "TeiidRestApplication.java"); //$NON-NLS-1$

        AntTasks.replace(teiidRestApplication, "${resources}", singletonSb.toString()); //$NON-NLS-1$

        template.delete();
        // Compile classes
        JavaCompiler compilerTool = ToolProvider.getSystemJavaCompiler();
        if (compilerTool != null) {
            StandardJavaFileManager fileManager = compilerTool.getStandardFileManager(null, null, null);

            String pathToJar1 = webInfLibDirectory.getCanonicalPath() + File.separator + "jackson-core-asl-1.8.3.jar"; //$NON-NLS-1$
            String pathToJar2 = webInfLibDirectory.getCanonicalPath() + File.separator + "jackson-jaxrs-1.8.3.jar"; //$NON-NLS-1$
            String pathToJar3 = webInfLibDirectory.getCanonicalPath() + File.separator + "jackson-mapper-asl-1.8.3.jar"; //$NON-NLS-1$
            String pathToJar4 = webInfLibDirectory.getCanonicalPath() + File.separator + "json-1.0.jar"; //$NON-NLS-1$
            String pathToJar5 = webInfLibDirectory.getCanonicalPath() + File.separator + "jaxrs-api-2.2.0.GA.jar"; //$NON-NLS-1$

            File jar1 = new File(pathToJar1);
            File jar2 = new File(pathToJar2);
            File jar3 = new File(pathToJar3);
            File jar4 = new File(pathToJar4);
            File jar5 = new File(pathToJar5);
            List<File> classPaths = Arrays.asList(jar1, jar2, jar3, jar4, jar5);
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

            Boolean includeJars = (Boolean)properties.get(WebArchiveBuilderConstants.PROPERTY_INCLUDE_RESTEASY_JARS);

            // Delete RESTEasy and dependent jars if the user elected not to include them
            if (!includeJars) {

                FileUtils.removeChildrenRecursively(webInfLibDirectory);

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
        commonRestMethodLogic(sb, restProcedure, ""); //$NON-NLS-1$
        if (restProcedure.getConsumesAnnotation() != null && !restProcedure.getConsumesAnnotation().isEmpty()) {
            sb.append("\tparameterMap = getInputs(is);" + newline + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Gen return and execute
        sb.append("\treturn teiidProvider.execute(\"" + restProcedure.getFullyQualifiedProcedureName() + "\", parameterMap);" + newline //$NON-NLS-1$ //$NON-NLS-2$
                  + "}" + newline + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param sb
     * @param restProcedure
     */
    private void createJSONMethod( StringBuilder sb,
                                   RestProcedure restProcedure ) {
        commonRestMethodLogic(sb, restProcedure, "json"); //$NON-NLS-1$
        if (restProcedure.getConsumesAnnotation() != null && !restProcedure.getConsumesAnnotation().isEmpty()) {
            sb.append("\tparameterMap = getJSONInputs(is);" + newline + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Gen return and execute
        sb.append("\tString result = teiidProvider.execute(\"" + restProcedure.getFullyQualifiedProcedureName() + "\", parameterMap);" + newline //$NON-NLS-1$ //$NON-NLS-2$
                  + "\t"); //$NON-NLS-1$
        sb.append("\tString json = convertXMLToJSON(result);" + newline + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("\treturn json;" + newline + "\t" + "}" + newline + "\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    }

    /**
     * @param sb
     * @param restProcedure
     */
    private void commonRestMethodLogic( StringBuilder sb,
                                        RestProcedure restProcedure,
                                        String methodAppendString ) {
        sb.append("@" + restProcedure.getRestMethod().toUpperCase() + newline + "\t"); //$NON-NLS-1$//$NON-NLS-2$
        String uri = methodAppendString == "" ? restProcedure.getUri() : methodAppendString + "/" + restProcedure.getUri(); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("@Path( \"" + uri + "\" )" + newline + "\t"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        if (restProcedure.getConsumesAnnotation() != null && !restProcedure.getConsumesAnnotation().isEmpty()) {
            sb.append(restProcedure.getConsumesAnnotation() + newline + "\t"); //$NON-NLS-1$
        }
        if (restProcedure.getProducesAnnotation() != null && !restProcedure.getProducesAnnotation().isEmpty()) {
            sb.append(restProcedure.getProducesAnnotation() + newline + "\t"); //$NON-NLS-1$
        }
        // Gen method signature
        sb.append("public String " + restProcedure.getProcedureName() + methodAppendString + "( "); //$NON-NLS-1$ //$NON-NLS-2$
        // Check for URI parameters and add as @PathParams
        Collection<String> pathParams = WarArchiveUtil.getPathParameters(uri);
        int pathParamCount = 0;
        for (String param : pathParams) {
            pathParamCount++;
            sb.append("@PathParam( \"" + param + "\" ) String " + param); //$NON-NLS-1$ //$NON-NLS-2$
            if (pathParamCount < pathParams.size()) {
                sb.append(", "); //$NON-NLS-1$
            }
        }
        if (restProcedure.getConsumesAnnotation() != null && !restProcedure.getConsumesAnnotation().isEmpty()) {
            if (pathParams.size() > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(" InputStream is ) { " + newline + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            sb.append(" ) { " + newline + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Gen setting of parameter(s)
        sb.append("\tparameterMap.clear();" + newline + "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        if (pathParams.size() > 0) {
            for (String param : pathParams) {
                sb.append("\tparameterMap.put(\"" + param + "\", " + param + ");" + newline + "\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
        }
    }

    /**
     * Return the path to the lib directory
     * 
     * @return
     * @throws Exception
     * @since 7.4
     */
    private String getWebLibDirectoryPath() throws Exception {
        final String webServicePluginPath = getWSPluginInstallPath().toOSString();
        final String webServiceLibFolder = webServicePluginPath + File.separator + "rest_war_resources"; //$NON-NLS-1$
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

    /**
     * @param models Sets models to the specified value. These names are used for the dynamic generation of resource java files.
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
