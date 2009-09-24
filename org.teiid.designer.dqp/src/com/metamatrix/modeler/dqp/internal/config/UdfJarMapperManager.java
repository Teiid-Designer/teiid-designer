/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;


/**
 * Class which provides file and content management for a properties file
 * designed to maintain a comma separated list of jar files specified by a user as user defined function jars.
 * @since 5.0
 */
public class UdfJarMapperManager {

    private IPath extensionsFolderPath;

    private DqpExtensionsHandler extHandler;

    private List<String> udfJarFileNames;

    /**
     * Constructs a new <code>UdfJarMapperManager</code> class which provides file and content management for a properties file
     * designed to maintain a comma separated list of jar files specified by a user as user defined function jars.
     * @since 5.0
     */
    public UdfJarMapperManager(IPath extFolderPath, DqpExtensionsHandler handler) {
        super();
        this.extHandler = handler;
        this.extensionsFolderPath = extFolderPath;
        udfJarFileNames = new ArrayList<String>();
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    // ---------------------------------------------------------------------------------------------------------------------------

    /**
     * Returns true if input jar name is mapped to the UDF model
     * @param jarName
     * @return
     * @since 5.0
     */
    public boolean isUdfJar(String jarName) {
        return udfJarFileNames.contains(jarName);
    }

    /**
     * Returns true if input jar file mapped to the UDF model
     * @param jarFileName - the jar <code>File</code>
     * @return
     * @since 5.0
     */
    public boolean isUdfJar(File jarFileName) {
        return udfJarFileNames.contains(jarFileName.getName());
    }

    /**
     *
     * Returns a list of UDF mapped jar files
     * @return list of UDF jar files
     * @since 5.0
     */
    public List<File> getUdfJarFiles() {
        List<File> jarFiles = new ArrayList<File>(udfJarFileNames.size());
        for( String str: udfJarFileNames ) {
            IPath udfJarMapperFilePath = extensionsFolderPath.append(str);
            jarFiles.add(udfJarMapperFilePath.toFile());
        }

        return jarFiles;
    }

    /**
     * Adds a list of jar names to the cached name list and saves the mapper file to persist the change.
     * @param newJarNames the list of jars to add
     * @since 5.0
     */
    public void addJars(List<String> newJarNames) {
        for( String newJarName: newJarNames ) {
            if( !udfJarFileNames.contains(newJarName) ) {
                udfJarFileNames.add(newJarName);
            }
        }

        save();
    }

    /**
     * Removes a list of jar names from the cached name list and saves the mapper file to persist the change.
     * @param oldJarNames the list of jars to remove
     * @since 5.0
     */
    public void removeJars(List<String> oldJarNames) {
        udfJarFileNames.removeAll(oldJarNames);

        save();
    }

    /**
     * Public method required to load the UDF jars AFTER the DqpExtensionHandler is finished constructing.
     *
     * @since 5.0
     */
    public void load() {
        initFile();
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    // PRIVATE METHODS
    // ---------------------------------------------------------------------------------------------------------------------------

    private File getUdfJarMapperFile() {
        IPath udfJarMapperFilePath = extensionsFolderPath.append(DqpPlugin.UDF_JAR_MAPPER_FILE_NAME);

        return udfJarMapperFilePath.toFile();
    }

    // Should only be called by the constructor.
    private void initFile() {

        File mapperFile = getUdfJarMapperFile();
        if( !mapperFile.exists() ) {
            setNonConnectorJarNames();

            writeFile(mapperFile, getFileText());
        } else {

            try {
                extractUdfJarFileNames();
            } catch (FileNotFoundException theException) {
                DqpPlugin.Util.log(IStatus.ERROR, theException,
                                   DqpPlugin.Util.getString("UdfJarMapperManager.mapperFileDoesNotExist", mapperFile.getPath())); //$NON-NLS-1$
            }
        }
    }

    private boolean isJarFile(File file) {
        return FileUtil.isArchiveFileName(file.getName(), false);
    }

    private void setNonConnectorJarNames() {
        List<String> connectorJarNames = extHandler.getAllConnectorJars();


        List<String> udfJarNames = new ArrayList<String>();

        File extFolder = extensionsFolderPath.toFile();
        if( extFolder.exists() ) {
            File[] extensionsFolderChildren = extFolder.listFiles();
            if( extensionsFolderChildren.length > 0 ) {
                for( File theJar: extensionsFolderChildren ) {
                    if( isJarFile(theJar) && !connectorJarNames.contains(theJar.getName()) && !DqpExtensionsHandler.CONNECTOR_PATCH_JAR.equals(theJar.getName()) ) {
                        udfJarNames.add(theJar.getName());
                    }
                }
            }
        }

        udfJarFileNames = udfJarNames;
    }

    private String getFileText() {
        StringBuffer sb = new StringBuffer();
        sb.append(DqpPlugin.Util.getString("UdfJarMapperManager.fileHeader")).append('\n').append('\n').append(getMappedUdfString()); //$NON-NLS-1$

        return sb.toString();
    }

    private String getMappedUdfString() {
        StringBuffer sb = new StringBuffer();
        sb.append(DqpPlugin.Util.getString("UdfJarMapperManager.modelPrefix") + '='); //$NON-NLS-1$
        int i=0;
        for( String str: udfJarFileNames ) {
            if( i> 0 ) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(str);
            i++;
        }
        return sb.toString();
    }

    private void extractUdfJarFileNames() throws FileNotFoundException {
        Scanner scanner = new Scanner(getUdfJarMapperFile());

        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            if( str.indexOf('=') > -1 ) {
                parseUdfJars(str);
            }
        }
    }

    private void parseUdfJars(String line) {
        Scanner lineScanner = new Scanner(line);

        // Skip the "FunctionDefinitions.xmi="
        lineScanner.findInLine("="); //$NON-NLS-1$

        lineScanner.useDelimiter("\\s*,\\s*"); //$NON-NLS-1$

        while (lineScanner.hasNext()) {
            String nextJarName = lineScanner.next();
            udfJarFileNames.add(nextJarName);
        }
    }


    private void save() {
        writeFile(getUdfJarMapperFile(), getFileText());
    }

    private void writeFile(File mapperFile, String fullText) {
        if(mapperFile!=null) {

            FileWriter fw=null;
            BufferedWriter out=null;
            PrintWriter pw=null;
            try {
                fw=new FileWriter(mapperFile);
                out = new BufferedWriter(fw);
                pw=new PrintWriter(out);

                pw.write(fullText);

            } catch(Exception e){
                String msg = DqpPlugin.Util.getString("UdfJarMapperManager.problemWritingMsg") + ' ' + mapperFile.getPath(); //$NON-NLS-1$
                DqpPlugin.Util.log(IStatus.ERROR, e, msg);
            } finally {
                pw.close();
                try{
                    out.close();
                }catch(java.io.IOException e){}
                try{
                    fw.close();
                }catch(java.io.IOException e){}
            }
        }
    }



}
