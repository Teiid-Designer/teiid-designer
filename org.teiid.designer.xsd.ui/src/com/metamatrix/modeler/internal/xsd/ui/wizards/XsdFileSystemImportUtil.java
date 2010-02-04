/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.internal.core.xml.xsd.XsdHeader;
import com.metamatrix.internal.core.xml.xsd.XsdHeaderReader;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.viewsupport.JobUtils;
import com.metamatrix.ui.internal.widget.ListMessageDialog;


/** 
 * @since 5.5
 */
public class XsdFileSystemImportUtil {
    private static final String I18N_PREFIX         = "XsdFileSystemImportUtil"; //$NON-NLS-1$
    private static final String SEPARATOR           = "."; //$NON-NLS-1$
    private static final String FILE_SEPARATOR      = File.separator;
    
    /**
     * Import XSD schemas from the given files.  
     * @param xsdFiles the given XSD files
     * @param addDependentXsds whether to import dependent XSD schemas
     * @param destinationFullPath the full path in the workspace where to import the XSDs
     * @param container the container that contains the wizard page
     * @param overwriteQuery implementation of IOverwriteQuery
     * @param createContainerStructure whether to create container structure
     * @param overwriteExistingResources whether to overwite existing resources
     * @return
     * @since 5.5
     */
    public static boolean importXsds(List xsdFiles, boolean addDependentXsds, IPath destinationFullPath, IWizardContainer container, IOverwriteQuery overwriteQuery, boolean createContainerStructure, boolean overwriteExistingResources) {
        return importXsds(xsdFiles, addDependentXsds, destinationFullPath, container, overwriteQuery, createContainerStructure, overwriteExistingResources, Collections.EMPTY_MAP);
    }
    
    /**
     * Import XSD schemas from the given files.  
     * @param xsdFiles the given XSD files
     * @param addDependentXsds whether to import dependent XSD schemas
     * @param destinationFullPath the full path in the workspace where to import the XSDs
     * @param container the container that contains the wizard page
     * @param overwriteQuery implementation of IOverwriteQuery
     * @param createContainerStructure whether to create container structure
     * @param overwriteExistingResources whether to overwite existing resources
     * @param Map fileToUserInfo optional property for each file to be imported. This is useful for the XSD that has dependent XSDs with URL (HTTP/HTTPS) locations
     * @return
     * @since 5.5
     */
    public static boolean importXsds(List xsdFiles, boolean addDependentXsds, IPath destinationFullPath, IWizardContainer container, IOverwriteQuery overwriteQuery, boolean createContainerStructure, boolean overwriteExistingResources, Map fileToUserInfo) {
        Shell shell = container.getShell();
        if (xsdFiles.size() > 0) {
            if( addDependentXsds ) {
                // we need to get the dependent models (Files) and add those too!!!
                Map depFilesImportToPaths = new HashMap();
                List depFiles = new ArrayList(getDependentXsdFiles(xsdFiles, depFilesImportToPaths, fileToUserInfo));
                boolean OK_TO_CONTINUE = false; 
                if( depFiles.isEmpty() ) {
                    OK_TO_CONTINUE = true;
                } else {
                    OK_TO_CONTINUE = ListMessageDialog.openQuestion(
                              shell, 
                              getString("addingDependentXsds.title"), //$NON-NLS-1$
                              null, 
                              getString("addingDependentXsds.message"),  //$NON-NLS-1$
                              new ArrayList(depFilesImportToPaths.keySet()), null);
                }
                if( OK_TO_CONTINUE ) {
                    List allFiles = new ArrayList(xsdFiles.size() + depFiles.size());
                    allFiles.addAll(xsdFiles);
                    allFiles.addAll(depFiles);
                    
                    
                    // Now we need to insure that the folder structure is complete for all xsd's
                    // Note that we'll have to find the top-most parent to make this happen?
                    File topLevelSourceDirectory = getTopLevelCommonFolder(allFiles);

                    addMissingFoldersToFileList(topLevelSourceDirectory, allFiles);
                    
                    return importResources(allFiles, container, overwriteQuery, topLevelSourceDirectory, destinationFullPath, depFilesImportToPaths, createContainerStructure, overwriteExistingResources);
                }
                
                return false;
            }
            
            File topLevelSourceDirectory = getTopLevelCommonFolder(xsdFiles);
            return importResources(xsdFiles, container, overwriteQuery, topLevelSourceDirectory, destinationFullPath, null, createContainerStructure, overwriteExistingResources);
        }

        MessageDialog.openInformation(shell,
                DataTransferMessages.DataTransfer_information,
                DataTransferMessages.FileImport_noneSelected);

        return false;
    }
    
    private static Collection getDependentXsdFiles(Collection selectedFiles, Map depFilesImportToPaths, Map fileToUserInfo) {
        Collection modifiableList = new ArrayList();
        
        for(Iterator iter = selectedFiles.iterator(); iter.hasNext();) {
            addDependentXsdFiles((File)iter.next(), modifiableList, selectedFiles, depFilesImportToPaths, fileToUserInfo);
        }
        return modifiableList;
    }

    private static void addDependentXsdFiles(File xsdFile, Collection modifiableList, Collection originalList,  Map depFilesImportToPaths, Map fileToUserInfo) {
        XsdHeader header = null;
        
        try {
            header = XsdHeaderReader.readHeader(xsdFile);
        } catch (MetaMatrixCoreException theException) {
            ModelerXsdUiConstants.Util.log(theException);
        }
        
        if (header != null) {
            // Add all the imported schema locations
            String[] locations = header.getImportSchemaLocations();
            addDepFilesForLocations(locations, xsdFile, modifiableList, originalList, depFilesImportToPaths, fileToUserInfo);
            // Add all the included schema locations
            locations = header.getIncludeSchemaLocations();
            addDepFilesForLocations(locations, xsdFile, modifiableList, originalList, depFilesImportToPaths, fileToUserInfo);
        }
    }
    
    private static void addDepFilesForLocations(String[] locations, File xsdFile, Collection modifiableList, Collection originalList,  Map depFilesImportToPaths, Map fileToUserInfo) {
        for (int i = 0; i != locations.length; ++i) {
            final String location = locations[i];
            String absolutePath = getAbsolutePath(xsdFile,location);
            File f = new File(absolutePath);
            if (f.exists() && !listContainsFileByPath(modifiableList, f) && !listContainsFileByPath(originalList, f)) {
                // Add to list of dependent XSDs
                modifiableList.add(f);
                depFilesImportToPaths.put(location, absolutePath);
                addDependentXsdFiles(f, modifiableList, originalList, depFilesImportToPaths, fileToUserInfo);
            }else {
                //check if it is url
                try {
                    URL url = new URL(absolutePath);
                    String filePath = url.getPath();
                    if(filePath.startsWith("/")) {//$NON-NLS-1$
                        filePath = filePath.substring(1);
                    }
                    String userName = null;
                    String password = null;
                    boolean verifyHostname = true;
                    Object userInfo[] = (Object[])fileToUserInfo.get(xsdFile);
                    if(userInfo != null) {
                        userName = (String)userInfo[0];
                        password = (String)userInfo[1];
                        verifyHostname = ((Boolean)userInfo[2]).booleanValue();
                    }
                    f = URLHelper.createFileFromUrl(url, filePath, userName, password, verifyHostname);
                    // Add to list of dependent XSDs
                    modifiableList.add(f);
                    depFilesImportToPaths.put(location, f.getAbsolutePath());
                    addDependentXsdFiles(f, modifiableList, originalList, depFilesImportToPaths, fileToUserInfo);
                }catch(MalformedURLException e) {
                    //not URL
                }catch(IOException ioe) {
                    ModelerXsdUiConstants.Util.log(ioe);
                }
            }
        }
    }
    
    /**
     * This method returns a File (i.e. Folder) which represents the top-most common folder required to contain the full
     * path/directory structure for the xsd schema files contained in the input allFiles Collection. 
     * @param allFiles
     * @return
     * @since 5.5
     */
    private static File getTopLevelCommonFolder(Collection allFiles) {
        // We need to walk through each file's segment
        // If we find one segment (i.e. folder) that's different or if there's no more folders (i.e. at the file)
        // then we've found the root.  If it is different than the the "location" selected by the user, we need to change this somehow.
        
        // Let's break these up into Tokens
        String nextSegment = null;
        File nextFile = null;
        int minSegCount = 99;
        Collection segmentsList = new ArrayList();
        int minPathId = 0;
        for( Iterator iter = allFiles.iterator(); iter.hasNext(); ) {
            nextFile = (File)iter.next();
            Collection segments = new ArrayList();
            for (final StringTokenizer textIter = new StringTokenizer(nextFile.getAbsolutePath(), FILE_SEPARATOR); textIter.hasMoreTokens();) {
                nextSegment = textIter.nextToken().trim();
                segments.add(nextSegment);
            }
            segmentsList.add(segments.toArray());
            if( segments.size() < minSegCount ) {
                minSegCount = segments.size();
                minPathId = segmentsList.size()-1;
            }
            
        }
        
        // Now that we have them broken up in segments, and have a minSegCount 
        
        Object[] segmentsArray = segmentsList.toArray();
        Object[] pathArray = null;
        
        Object[] minPathArray = (Object[])segmentsArray[minPathId];
        
        for(int j=0; j<minSegCount; j++ ) {
            String baseSeg = (String)minPathArray[j];
            String compareSeg = null;
            for( int i=1; i<segmentsArray.length; i++ ) {
                pathArray = (Object[])segmentsArray[i];
                compareSeg = (String)pathArray[j];
                if( ! baseSeg.equalsIgnoreCase(compareSeg)) {
                    // We've found a path that is different, so we bail
                    // and get the segments prior to this, and 
                    String topLevelFolder = null;
                    for( int k=0; k<j-1; k++ ) {
                        if(k==0)
                            topLevelFolder = minPathArray[k] + FILE_SEPARATOR;
                        else
                            topLevelFolder = topLevelFolder + minPathArray[k] + FILE_SEPARATOR;
                    }
                    topLevelFolder = topLevelFolder + minPathArray[j-1];
                    return new File(topLevelFolder);
                }
            }
        }
        
        String topLevelFolder = null;
        for( int k=0; k<minPathArray.length-1; k++ ) {
            if(k==0)
                topLevelFolder = minPathArray[k] + FILE_SEPARATOR;
            else
                topLevelFolder = topLevelFolder + minPathArray[k] + FILE_SEPARATOR;
        }
        if( minPathArray.length > 2 ) {
            topLevelFolder = topLevelFolder + minPathArray[minPathArray.length-1];
        }
        return new File(topLevelFolder);
    }
    

    
    /*
     * This method needs to create/add any folders required that between each xsd file and the topLevelFolder
     */
    private static void addMissingFoldersToFileList(File topLevelFolder, Collection allFiles) {
        // For each xsd, get it's path/name
        // walk backwards from the modelName and create a folder at each level up to the topLevelFolder
        File nextFile = null;
        String nextSegment;
        
        // Tokenize the top level segment
        Collection topLevelSegments = new ArrayList();
        for (final StringTokenizer textIter = new StringTokenizer(topLevelFolder.getAbsolutePath(), FILE_SEPARATOR); textIter.hasMoreTokens();) {
            nextSegment = textIter.nextToken().trim();
            topLevelSegments.add(nextSegment);
        }
        Object[] topSegArray = topLevelSegments.toArray();
        int nBaseSegs = topSegArray.length;
        
        Collection missingFolders = new ArrayList();
        
        for( Iterator iter = allFiles.iterator(); iter.hasNext(); ) {
            nextFile = (File)iter.next();
            Collection segments = new ArrayList();
            for (final StringTokenizer textIter = new StringTokenizer(nextFile.getAbsolutePath(), FILE_SEPARATOR); textIter.hasMoreTokens();) {
                nextSegment = textIter.nextToken().trim();
                segments.add(nextSegment);
            }
            // if the segment count for a file is greater than the segment count for the top level folder
            // then we need to create folders up to nSegs-1
            String thisPath = topLevelFolder.getAbsolutePath();
            File existingFolder = null;
            if( segments.size() > nBaseSegs+1 ) {
                Object[] thisSegArray = segments.toArray();
                // Now that we have both the base array and this array
                for( int i=nBaseSegs; i<thisSegArray.length-1; i++ ) {
                    thisPath =  thisPath + FILE_SEPARATOR + thisSegArray[i];
                }
                existingFolder = new File(thisPath);
                if( !existingFolder.exists() && !listContainsFileByPath(missingFolders, existingFolder)) {
                    //System.out.println(" ADDING MISSING FOLDER = " + existingFolder);
                    missingFolders.add(existingFolder);
                }
            }
        }

        if( !missingFolders.isEmpty() ) {
            Collection files = new ArrayList(allFiles);
            allFiles.clear();
            allFiles.addAll(missingFolders);
            allFiles.addAll(files);
        }
    }
       
    private static boolean listContainsFileByPath(Collection files, File file) {
        for( Iterator iter = files.iterator(); iter.hasNext(); ) {
            File nextFile = (File)iter.next();
            if( nextFile.getAbsolutePath().equals(file.getAbsolutePath())) {
                return true;
            }
        }
        
        return false;
    }
    
    private static String getAbsolutePath(final File base, final String relativePath) {
        URI baseLocation = URI.createFileURI(base.getAbsolutePath());
        URI relLocation  = URI.createURI(relativePath, false);
        if (baseLocation.isHierarchical() && !baseLocation.isRelative() && relLocation.isRelative()) {
            relLocation = relLocation.resolve(baseLocation);
        }
        if( relLocation.isFile() ) {
            return relLocation.toFileString();
        }
        
        return URI.decode(relLocation.toString());
    }
    
    /**
     * Import the XSD resources into the workspace.
     * @param fileSystemObjects
     * @param container
     * @param overwriteQuery
     * @param topLevelSourceDirectory
     * @param destinationFullPath
     * @param depFilesImportToPaths
     * @param createContainerStructure
     * @param overwriteExistingResources
     * @return
     * @since 5.5
     */
    public static boolean importResources(List fileSystemObjects, IWizardContainer container, IOverwriteQuery overwriteQuery, File topLevelSourceDirectory, IPath destinationFullPath, Map depFilesImportToPaths, boolean createContainerStructure, boolean overwriteExistingResources) {
        ImportOperation operation = new ImportOperation(destinationFullPath,
                topLevelSourceDirectory, FileSystemStructureProvider.INSTANCE,
                overwriteQuery, fileSystemObjects);
        
        operation.setContext(container.getShell());
        
        // Let's cache the auto-build and reset after.
        boolean autoBuildOn = ResourcesPlugin.getWorkspace().isAutoBuilding();
        if( autoBuildOn ) {
            JobUtils.setAutoBuild(false);
        }
        
        operation.setCreateContainerStructure(createContainerStructure);
        operation.setOverwriteResources(overwriteExistingResources);
        boolean importResult = executeImportOperation(operation, container);
        // We need to reset these files to "Not Modified"
        Collection emfResources = new HashSet();
        for( Iterator iter = fileSystemObjects.iterator(); iter.hasNext(); ) {
            Object nextObj = iter.next();
            if( nextObj instanceof File  && !((File)nextObj).isDirectory()) {
                // ------------------------------------------------------
                // Defect 24934 - Need to cache the target XSD File, so we can create the proper "Import" statement for each dependent xsd file
                // Must use original source SCHEMA files ... NOT ModelResource file paths
                File targetXSDFile = (File)nextObj;
                String name = targetXSDFile.getName();
                // do something here if .xsd
                if( name.indexOf(".xsd") > -1 ) { //$NON-NLS-1$
                    Resource[] resources = null;
                    
                    try {
                        resources = ModelWorkspaceManager.getModelWorkspaceManager().getModelContainer().getResourceFinder().findByName(name, false, false);
                        if( resources != null && resources.length == 1) {
                            ModelResource mr = ModelUtilities.getModelResource(resources[0], true);
                            if( mr != null ) {
                                emfResources.add(mr.getEmfResource());
                            }
                            
                            //reset import locations to relative paths
                            if(depFilesImportToPaths != null && !depFilesImportToPaths.isEmpty()
                                            &&resources[0].getContents().size() == 1){
                                
                                Object content = resources[0].getContents().get(0);
                                if(content instanceof XSDSchema) {
                                    boolean importChanged = false;
                                    Iterator iter1 = ((XSDSchema)content).getContents().iterator();
                                    while(iter1.hasNext()) {
                                        content = iter1.next();
                                        if(content instanceof XSDImport) {
                                            XSDImport xsdImport = ((XSDImport)content); 
                                            String xsdLocation = xsdImport.getSchemaLocation();
                                            String absoluteImportLocation = (String)depFilesImportToPaths.get(xsdLocation);
                                            // ------------------------------------------------------
                                            // Defect 24934 - ON LINUX, the getAbsolutePath() returns a path with a duplicate folder path if there is a 
                                            // symbolic link in the path name.
                                            // Fix is to use getPath() instead.
                                            
                                            String topLevelDirectoryPath = topLevelSourceDirectory.getPath();
                                            if( !Platform.getOS().equals(Platform.WS_WIN32)) {
                                                if( topLevelDirectoryPath.charAt(0) != '/') {
                                                    topLevelDirectoryPath = '/' + topLevelDirectoryPath;
                                                }
                                            }
                                            // ------------------------------------------------------
                                            // Defect 24934 - Use the target XSD file for the base path (instead of XSD Model Resource)
                                            IPath projectRelativeBaseXSDFilePath = new Path(targetXSDFile.getPath().substring(topLevelDirectoryPath.length() + 1));
                                            
                                            if(absoluteImportLocation != null) {
                                                // Get the 
                                                IPath projectRelativeImportedXsdFilePath = new Path(absoluteImportLocation.substring(topLevelDirectoryPath.length() + 1));
                                                
                                                String relativeXsdLocation = ModelUtil.getRelativePath(projectRelativeImportedXsdFilePath, projectRelativeBaseXSDFilePath);
                                                
                                                if(!xsdLocation.equals(relativeXsdLocation)) {
                                                    xsdImport.setSchemaLocation(relativeXsdLocation);
                                                    importChanged = true;
                                                }
                                            }
                                        }
                                    }
                                    if(importChanged) {
                                        mr.save(null, false);
                                    }
                                }
                            }
                        }
                    } catch (CoreException theException) {
                    }          
                }
            }
        }
        
        Iterator iter = emfResources.iterator();
        while(iter.hasNext()) {
            ((Resource)iter.next()).setModified(false);
        }
        
        if( autoBuildOn ) {
            JobUtils.setAutoBuild(true);
        }
        
        return importResult;
    }
    
    /**
     *  Execute the passed import operation.  Answer a boolean indicating success.
     */
    private static boolean executeImportOperation(ImportOperation op, IWizardContainer container) {
        try {
            container.run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable exception = e.getTargetException();
            String message = exception.getMessage();
            //Some system exceptions have no message
            if (message == null)
                message = NLS.bind(IDEWorkbenchMessages.WizardDataTransfer_exceptionMessage, exception);
            MessageDialog.openError(container.getShell(),
                                    IDEWorkbenchMessages.WizardExportPage_internalErrorTitle, message);
            return false;
        }

        IStatus status = op.getStatus();
        if (!status.isOK()) {
            ErrorDialog
                    .openError(container.getShell(), DataTransferMessages.FileImport_importProblems,
                            null, // no special message
                            status);
            return false;
        }

        return true;
    }
    
    private static String getString(final String id) {
        return ModelerXsdUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id);
    }
}
