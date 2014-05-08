/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.properties.extension;

import static org.teiid.designer.ui.UiConstants.PLUGIN_ID;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.designer.core.util.VdbHelper;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;

/**
 * Chooser Dialog for selecting a VdbFile from the Workspace - either 
 *  1) selects a udf jar from the udf jar folder (lib)
 *  2) selects a 'user file' from the user files folder (otherFiles)
 */
public class ChooseVdbFileFromWorkspaceDialog extends ElementTreeSelectionDialog {

    /**
     * Constructor
     * @param shell the Shell
     * @param title Dialog Title
     * @param message Dialog Message
     * @param project the current project, for scoping the selections
     * @param vdbFolder type of folder being analysed
     */
    ChooseVdbFileFromWorkspaceDialog( final Shell shell,
                      final String title,
                      final String message,
                      final IProject project,
                      final VdbFolders vdbFolder ) {
        super(shell, new ModelExplorerLabelProvider(), new ChooseFileDialogContentProvider(project, vdbFolder));
        setTitle(title);
        setMessage(message);
        setAllowMultiple(false);
        setInput(new IProject[] {project});
        
        setValidator(new ISelectionStatusValidator() {

            @Override
            public IStatus validate( final Object[] selection ) {
                if (selection.length == 1 && selection[0] instanceof IFile) return new Status(IStatus.OK, PLUGIN_ID, null);
                return new Status(IStatus.ERROR, PLUGIN_ID, null);
            }
        });
    }
    
}

/*
 * Workspace File Chooser Dialog - Content provider
 */
final class ChooseFileDialogContentProvider implements ITreeContentProvider {
    
    private IProject[] projects;
    private VdbFolders vdbFolder;

    /**
     * @param project the supplied project
     * @param vdbFolder type of folder
     */
    public ChooseFileDialogContentProvider(IProject project, VdbFolders vdbFolder) {
        this.projects = new IProject[]{project};
        this.vdbFolder = vdbFolder;
    }
    
    @Override
    public final void dispose() {
    }

    @Override
    public final IResource[] getChildren( final Object element ) {
        IContainer container = (IContainer)element;
        final List<IResource> children = new ArrayList<IResource>();
        try {
            for (final IResource resource : container.members())
                if ((resource instanceof IContainer && hasChildren(resource))
                    || (resource instanceof IFile && validFile((IFile)resource))) children.add(resource);
        } catch (final CoreException error) {
            throw CoreModelerPlugin.toRuntimeException(error);
        }
        return children.toArray(new IResource[children.size()]);
    }

    @Override
    public Object[] getElements( final Object inputElement ) {
        return projects;
    }

    @Override
    public boolean hasChildren( final Object element ) {
        if(element instanceof IFile) return false;
        IContainer container = (IContainer)element;
        // only return the appropriate folder contents
        if(container instanceof IFolder && vdbFolder != null) {
            if(VdbFolders.UDF.equals(vdbFolder) && !VdbFolders.UDF.getReadFolder().equals(container.getName())) {
                return false;
            }
        }
        try {
            for (final IResource resource : container.members())
                if (resource instanceof IContainer) {
                    if (hasChildren(resource)) return true;
                } else if (validFile((IFile)resource)) return true;
        } catch (final CoreException error) {
            throw CoreModelerPlugin.toRuntimeException(error);
        }
        return false;
    }

    @Override
    public final Object getParent( final Object element ) {
        return ((IResource)element).getParent();
    }
    
    boolean validFile( final IFile file ) {
        String ext = file.getFileExtension();
        if (ext == null) return false;
        ext = ext.toLowerCase();
        if(VdbFolders.UDF.equals(vdbFolder)) {
            return VdbHelper.JAR_EXT.equals(ext);
        } else {
            // Other files

            // Deny jar files as they are udf
            if (VdbHelper.JAR_EXT.equals(ext))
                return false;

            // Allow xsd files even though they are model files
            // as they are being moved to the other files section of vdb
            if (ModelUtil.isXsdFile(file))
                return true;

            return !ModelUtil.isModelFile(file);
        }
    }
    
    @Override
    public void inputChanged( final Viewer viewer,
                              final Object oldInput,
                              final Object newInput ) {
    }

}
