/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.properties.extension;

import java.io.File;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.util.VdbHelper;
import org.teiid.designer.core.util.VdbHelper.FileFilter;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.ui.actions.CopyFilesAndFoldersOperation;
import org.teiid.designer.ui.wizards.FolderUtil;

/**
 *  Methods for selecting Vdb Files (udf jars or other) from the fileSystem or workspace
 */
public class VdbFileDialogUtil implements StringConstants {

    /**
     * Allow the user total control over when type of file they want
     * to select, eg. file system or workspace.
     *
     * @param shell
     * @param project
     * @param udfJar
     *
     * @return path of selected file
     */
    private static String chooseOptionsSelectFile(Shell shell, IProject project, VdbFolders vdbFolder) {
        String selectedFile;
        boolean[] choices = showOptionsDialog(shell, vdbFolder, false);
        boolean selectFromWorkspace = false;
        boolean copyToWorkspace = false;
        if (choices != null) {
            selectFromWorkspace = choices[0];
            copyToWorkspace = choices[1];
        } else {
            return EMPTY_STRING;
        }

        // Show Workspace selection dialog
        if (selectFromWorkspace) {
            selectedFile = chooseFileFromWorkspace(shell, project, vdbFolder);
            // Show FileSystem selection dialog
        } else {
            selectedFile = chooseFileFromFileSystem(shell, project, vdbFolder, copyToWorkspace);
        }
        return selectedFile;
    }

    /**
     * Select a File for the VDB, scoped to the provided project.  The user is given the option of selecting
     * the file from the workspace(if any are present) or the file system.
     *
     * @param shell the Shell
     * @param project the VDB project
     * @param vdbFolder type of folder requires to be selected from or null
     * @return the filePath string
     */
    public static String selectFile(Shell shell, IProject project, VdbFolders vdbFolder) {

        String selectedFile = null;
        // Determine if the desired folder is under the project
        IContainer folder = VdbHelper.getFolder(project, vdbFolder.getReadFolder());

        // If the project does not contain folder
        // -- present FileSystem dialog
        if (folder == null) {
            // Udf jar selection: go to file system (must copy)
            if (VdbFolders.UDF.equals(vdbFolder)) {
                selectedFile = chooseFileFromFileSystem(shell, project, vdbFolder, true);
                // OtherFiles selection: show choice dialog, to determine if the file needs to be copied to workspace
            } else {
                boolean[] choices = showOptionsDialog(shell, vdbFolder, true);
                boolean copyToWorkspace = false;
                if (choices != null) {
                    copyToWorkspace = choices[1];
                } else {
                    return ""; //$NON-NLS-1$
                }
                selectedFile = chooseFileFromFileSystem(shell, project, vdbFolder, copyToWorkspace);
            }
            // If the project contains folder (and at least one file in it)
            // -- Allow file selection from folder, or FileSystem
        } else {
            // Determine if folder has at least one file in it.
            // -- if not, the workspace option is not available
            if (VdbHelper.folderContainsOneOrMoreFile(folder, vdbFolder.getExtension())) {
                selectedFile = chooseOptionsSelectFile(shell, project, vdbFolder);
                // Workspace folder has no files.
            } else {
                // For 'otherFiles' user has option to copyToWorkspace - show options dialog
                // For 'udfJar' the file must be copied to the workspace
                boolean copyToWorkspace = true;
                if (! VdbFolders.UDF.equals(vdbFolder)) {
                    boolean[] choices = showOptionsDialog(shell, vdbFolder, true);
                    if (choices != null) {
                        copyToWorkspace = choices[1];
                    } else {
                        return EMPTY_STRING;
                    }
                }
                selectedFile = chooseFileFromFileSystem(shell, project, vdbFolder, copyToWorkspace);
            }
        }
        return selectedFile;
    }

    /*
     * Show the file vs workspace selection options dialog, and return the dialog selections.
     * If the dialog was cancelled, returns null
     * @param vdbFolder type of vdb folder
     * @param disableWorkspaceOption 'true' disables the option of selecting from workspace
     * @return returns boolean array
     *     - 1) 'true' if workspace selection, 'false' if fileSystem
     *     - 2) 'true' if copyToWorkspace, 'false' if not. 
     */
    private static boolean[] showOptionsDialog(Shell shell, VdbFolders vdbFolder, boolean disableWorkspaceOption) {
        boolean selectFromWorkspace = false;
        boolean copyToWorkspace = false;
        
        // Show dialog for user choice
        String title = EMPTY_STRING;
        StringBuffer message = new StringBuffer();
        if (VdbFolders.UDF.equals(vdbFolder)) {
            title = Messages.workspaceOrFileSystemDialogUdfTitle;
            message.append(Messages.workspaceOrFileSystemDialogUdfMessage);
        } else {
            title = Messages.workspaceOrFileSystemDialogFileTitle;
            message.append(Messages.workspaceOrFileSystemDialogFileMessage);
        }

        if(disableWorkspaceOption) {
            message.append("\n"+Messages.workspaceOptionIsDisabledMessage); //$NON-NLS-1$
        }

        ChooseVdbFileOptionsDialog optionsDialog = new ChooseVdbFileOptionsDialog(
                                                                                  shell, title, message.toString(),
                                                                                  vdbFolder, disableWorkspaceOption);
        
        int returnCode = optionsDialog.open();
        
        if(returnCode == Window.OK) {
            selectFromWorkspace = optionsDialog.selectFromWorkspaceSelected();
            copyToWorkspace = optionsDialog.copyToWorkspaceSelected();
            return new boolean[] {selectFromWorkspace,copyToWorkspace};
        } 
        return null; 
    }
    
    /*
     * Show dialog to select desired type of file from the FileSystem
     * @param shell the shell
     * @param project the project
     * @param vdbFolder
     * @param copyToWorkspace 'true' if copying the file into workspace folder
     * @return the selected file name
     */
    private static String chooseFileFromFileSystem(Shell shell, IProject project, VdbFolders vdbFolder, boolean copyToWorkspace) {
        String fileResult = null;

        final FileDialog dlg = new FileDialog(shell);
        FileFilter fileFilter = vdbFolder == null ? VdbHelper.ALL_FILE_FILTER : vdbFolder.getFileFilter();
        dlg.setFilterExtensions(new String[] { fileFilter.getFilter() });
        dlg.setFilterNames(new String[] { fileFilter.getName() });

        String fileFullName = dlg.open();
        if (fileFullName == null)
            return EMPTY_STRING;

        File theFile = new File(fileFullName);
        String fileShortName = theFile.getName();

        // Make sure the selected file is a jar file for the udfJar option.  If not, show error dialog and return
        if (fileShortName != null && VdbFolders.UDF.equals(vdbFolder) && !fileShortName.endsWith(VdbHelper.JAR_EXT)) {
            MessageDialog.openError(shell, Messages.selectedFileNotAJarDialogTitle, Messages.selectedFileNotAJarDialogMessage);
            return EMPTY_STRING;
        }

        // If the appropriate folder does not exist, create it.
        String folderName = vdbFolder.getWriteFolder();
        IContainer folder = VdbHelper.getFolder(project, folderName);
        if (folder == null && copyToWorkspace) {
            FolderUtil.createFolder(shell, project, folderName);
            folder = VdbHelper.getFolder(project, folderName);
        }

        // Now copy the selected file into the folder
        if (folder != null && copyToWorkspace) {
            String[] files = new String[] {fileFullName};
            CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(shell);
            operation.copyFiles(files, folder);
        }

        // If this is a 'userFile' and not copied to workspace - use full path
        if (!VdbFolders.UDF.equals(vdbFolder) && !copyToWorkspace) {
            fileResult = fileFullName;
            // Now lookup the file from the folder, and return the path
        } else {
            fileResult = VdbHelper.getFileRelativePath(folder, fileShortName);
        }

        return fileResult;
    }

    /*
     * Show dialog to select a file from the workspace
     *
     * @return the selected file name from the workspace
     */
    private static String chooseFileFromWorkspace(Shell shell, IProject project, VdbFolders vdbFolder) {
        String fileName = null;
        String title = null;
        String message = null;
        if (VdbFolders.UDF.equals(vdbFolder)) {
            title = Messages.chooseFileFromWorkspaceDialogUdfTitle;
            message = Messages.chooseFileFromWorkspaceDialogUdfMessage;
        } else {
            title = Messages.chooseFileFromWorkspaceDialogFileTitle;
            message = Messages.chooseFileFromWorkspaceDialogFileMessage;
        }
        final ChooseVdbFileFromWorkspaceDialog dlg = new ChooseVdbFileFromWorkspaceDialog(
                                                                                          shell, title, message,
                                                                                          project, vdbFolder);
        
        final IResource choice = dlg.open() == Window.OK ? (IResource)dlg.getFirstResult() : null;
        if (choice != null) fileName = choice.getProjectRelativePath().toString();
        
        return fileName;
    }
    
}
