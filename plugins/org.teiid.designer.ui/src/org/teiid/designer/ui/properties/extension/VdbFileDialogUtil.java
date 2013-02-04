/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.properties.extension;

import java.io.File;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.core.util.VdbHelper;
import org.teiid.designer.ui.actions.CopyFilesAndFoldersOperation;
import org.teiid.designer.ui.wizards.FolderUtil;

/**
 *  Methods for selecting Vdb Files (udf jars or other) from the fileSystem or workspace
 */
public class VdbFileDialogUtil {
    
    /**
     * Select a File for the VDB, scoped to the provided project.  The user is given the option of selecting
     * the file from the workspace(if any are present) or the file system.
     * @param shell the Shell
     * @param proj the VDB project
     * @param udfJar 'true' if UDF jar selection is desired, otherwise 'false' for other files
     * @return the filePath string
     */
    public static String selectUdfOrFile(Shell shell, IProject proj, boolean udfJar) {
        // Determine if the desired folder is under the project
        String folderName = udfJar ? VdbHelper.UDF_FOLDER : VdbHelper.OTHER_FILES_FOLDER; 
        IFolder folder = VdbHelper.getFolder(proj,folderName);
        
        String selectedFile = null;
        // If the project does not contain folder
        // -- present FileSystem dialog
        if(folder==null) {
            // Udf jar selection: go to file system (must copy)
            if(udfJar) {
                selectedFile = chooseFileFromFileSystem(shell,proj,udfJar,true);
            // OtherFiles selection: show choice dialog, to determine if the file needs to be copied to workspace
            } else {
                boolean[] choices = showOptionsDialog(shell,udfJar,true); 
                boolean copyToWorkspace = false;
                if(choices!=null) {
                    copyToWorkspace = choices[1];
                } else {
                    return "";  //$NON-NLS-1$
                }
                selectedFile = chooseFileFromFileSystem(shell,proj,udfJar,copyToWorkspace);
            }
        // If the project contains folder (and at least one file in it)
        // -- Allow file selection from folder, or FileSystem
        } else {
            // Determine if folder has at least one file in it.
            // -- if not, the workspace option is not available
            if(VdbHelper.folderContainsOneOrMoreFile(folder,udfJar)) {
                boolean[] choices = showOptionsDialog(shell,udfJar,false); 
                boolean selectFromWorkspace = false;
                boolean copyToWorkspace = false;
                if(choices!=null) {
                    selectFromWorkspace = choices[0];
                    copyToWorkspace = choices[1];
                } else {
                    return "";  //$NON-NLS-1$
                }
                
                // Show Workspace selection dialog
                if(selectFromWorkspace) {
                    selectedFile = chooseFileFromWorkspace(shell,proj,udfJar);
                // Show FileSystem selection dialog
                } else {
                    selectedFile = chooseFileFromFileSystem(shell,proj,udfJar,copyToWorkspace);
                }
            // Workspace folder has no files.
            } else {
                // For 'otherFiles' user has option to copyToWorkspace - show options dialog
                // For 'udfJar' the file must be copied to the workspace
                boolean copyToWorkspace = true;
                if(!udfJar) {
                    boolean[] choices = showOptionsDialog(shell,udfJar,true); 
                    if(choices!=null) {
                        copyToWorkspace = choices[1];
                    } else {
                        return "";  //$NON-NLS-1$
                    }
                }
                selectedFile = chooseFileFromFileSystem(shell,proj,udfJar,copyToWorkspace);
            }
        }
        return selectedFile;
    }
    
    /*
     * Show the file vs workspace selection options dialog, and return the dialog selections.
     * If the dialog was cancelled, returns null
     * @param udfJar this is udf jar option
     * @param disableWorkspaceOption 'true' disables the option of selecting from workspace
     * @return returns boolean array
     *     - 1) 'true' if workspace selection, 'false' if fileSystem
     *     - 2) 'true' if copyToWorkspace, 'false' if not. 
     */
    private static boolean[] showOptionsDialog(Shell shell, boolean udfJarOption, boolean disableWorkspaceOption) {
        boolean selectFromWorkspace = false;
        boolean copyToWorkspace = false;
        
        // Show dialog for user choice
        String title = getChooseVdbFileDialogTitle(udfJarOption);
        String message = getChooseVdbFileDialogMessage(udfJarOption,disableWorkspaceOption);
        ChooseVdbFileOptionsDialog optionsDialog = new ChooseVdbFileOptionsDialog(shell,title,message,udfJarOption,disableWorkspaceOption);
        
        int returnCode = optionsDialog.open();
        
        if(returnCode == Window.OK) {
            selectFromWorkspace = optionsDialog.selectFromWorkspaceSelected();
            copyToWorkspace = optionsDialog.copyToWorkspaceSelected();
            return new boolean[] {selectFromWorkspace,copyToWorkspace};
        } 
        return null; 
    }
    
    /*
     * Get the Dialog title based on whether selecting udf jar or other files
     * @param udfJar 'true' for udf jar selection
     * @return the dialog title
     */
    private static String getChooseVdbFileDialogTitle(boolean udfJar) {
        if(udfJar) {
            return Messages.workspaceOrFileSystemDialogUdfTitle;
        }
        return Messages.workspaceOrFileSystemDialogFileTitle;
    }
    
    /*
     * Get the Dialog message based on whether selecting udf jar or other files, and
     * add a note if workspace selection is disabled
     * @param udfJar 'true' for udf jar selection, 'false' for other files
     * @param disableWorkspaceOption 'true' if workspace option is not available
     * @return the dialog message
     */
    private static String getChooseVdbFileDialogMessage(boolean udfJar, boolean disableWorkspaceOption) {
        StringBuffer sb = new StringBuffer();
        if(udfJar) {
            sb.append(Messages.workspaceOrFileSystemDialogUdfMessage);
        } else {
            sb.append(Messages.workspaceOrFileSystemDialogFileMessage);
        }
        if(disableWorkspaceOption) {
            sb.append("\n"+Messages.workspaceOptionIsDisabledMessage); //$NON-NLS-1$
        }
        return sb.toString();
    }
    
    /*
     * Show dialog to select desired type of file from the FileSystem
     * @param shell the shell
     * @param project the project
     * @param udfJar 'true' if selecting udfJars, 'false' otherwise
     * @param copyToWorkspace 'true' if copying the file into workspace folder
     * @return the selected file name
     */
    private static String chooseFileFromFileSystem(Shell shell, IProject project, boolean udfJar, boolean copyToWorkspace) {
        String fileResult = null;

        final FileDialog dlg = new FileDialog(shell);
        if(udfJar) {
            dlg.setFilterExtensions(new String[] {"*.jar"}); //$NON-NLS-1$
            dlg.setFilterNames(new String[] {"jar"}); //$NON-NLS-1$ 
        } else {
            dlg.setFilterExtensions(new String[] {"*.*"}); //$NON-NLS-1$
            dlg.setFilterNames(new String[] {"all files"}); //$NON-NLS-1$ 
        }

        String fileFullName = dlg.open();
        if(fileFullName!=null) {
            File theFile = new File(fileFullName);
            String fileShortName = theFile.getName();
           
            // Make sure the selected file is a jar file for the udfJar option.  If not, show error dialog and return
            if(fileShortName!=null && udfJar && !fileShortName.endsWith(".jar")) {  //$NON-NLS-1$
                MessageDialog.openError(shell,Messages.selectedFileNotAJarDialogTitle,Messages.selectedFileNotAJarDialogMessage);
                return ""; //$NON-NLS-1$
            }
            String folderName = null;
            if(udfJar) {
                folderName = VdbHelper.UDF_FOLDER;
            } else {
                folderName = VdbHelper.OTHER_FILES_FOLDER;
            }
            // If the appropriate folder does not exist, create it.
            IFolder folder = VdbHelper.getFolder(project,folderName);
            if(folder==null && copyToWorkspace) {
                FolderUtil.createFolder(shell, project, folderName);
                folder = VdbHelper.getFolder(project,folderName);
            }

            // Now copy the selected jar into the lib folder
            if(folder!=null && copyToWorkspace) {
                String[] files = new String[] {fileFullName};
                CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(shell);
                operation.copyFiles(files, folder);
            }

            // If this is a 'userFile' and not copied to workspace - use full path
            if(!udfJar && !copyToWorkspace) {
                fileResult = fileFullName;
            // Now lookup the file from the folder, and return the path
            } else {
                fileResult = VdbHelper.getFileRelativePath(folder, fileShortName);
            }
        } else {
            fileResult = ""; //$NON-NLS-1$
        }
        return fileResult;
    }

    /*
     * Show dialog to select a file from the workspace
     * @param cellEditorWindow the window control
     * @return the selected file name from the workspace
     */
    private static String chooseFileFromWorkspace(Shell shell, IProject project, boolean udfJar) {
        String fileName = null;
        String title = null;
        String message = null;
        if(udfJar) {
            title = Messages.chooseFileFromWorkspaceDialogUdfTitle;
            message = Messages.chooseFileFromWorkspaceDialogUdfMessage;
        } else {
            title = Messages.chooseFileFromWorkspaceDialogFileTitle;
            message = Messages.chooseFileFromWorkspaceDialogFileMessage;
        }
        final ChooseVdbFileFromWorkspaceDialog dlg = new ChooseVdbFileFromWorkspaceDialog(shell,title,message,project,udfJar);
        
        final IResource choice = dlg.open() == Window.OK ? (IResource)dlg.getFirstResult() : null;
        if (choice != null) fileName = choice.getProjectRelativePath().toString();
        
        return fileName;
    }
    
}
