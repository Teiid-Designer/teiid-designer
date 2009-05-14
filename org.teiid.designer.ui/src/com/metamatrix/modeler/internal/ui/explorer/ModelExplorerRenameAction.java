/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.explorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.views.navigator.ResourceNavigatorRenameAction;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.actions.TreeViewerRenameAction;
import com.metamatrix.modeler.internal.ui.refactor.actions.RenameRefactorAction;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.DelegatableAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * ModelExplorerRenameAction is a specialization of ResourceNavigatorRenameAction that also
 * handles in-line renaming of EObjects.  In addition, it prevents renaming of ModelResources
 * that are open in a ModelEditor.
 */
public class ModelExplorerRenameAction extends ResourceNavigatorRenameAction implements ISelectionProvider,
                                                                                        UiConstants {
    //============================================================================================================================
    // Constants
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelExplorerRenameAction.class);
    
    private static final String RENAME_NOT_SUPPORTED_MESSAGE = getString("renameNotSupportedMessage"); //$NON-NLS-1$
    
    //============================================================================================================================
    // Static Methods
    
    /**<p>
     * </p>
     * @since 4.0
     */
    private static String getString(final Object id) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    //============================================================================================================================
    // Variables

    private TreeViewerRenameAction inlineRenameAction;
    private IActionDelegate delRefactorRename; 
    private IAction actRefactorRename; 

    /**
     * Construct an instance of ModelExplorerRenameAction.
     * @param shell
     * @param treeViewer
     */
    public ModelExplorerRenameAction(Shell shell, TreeViewer treeViewer) {
        super(shell, treeViewer);
        // create the TreeViewerRenameAction for inline editing of EObject names
        inlineRenameAction = new TreeViewerRenameAction();
        inlineRenameAction.setTreeViewer(treeViewer, (ILabelProvider) treeViewer.getLabelProvider());
    }

    /* Overridden to handle EObjects and model files.
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        Object selection = getStructuredSelection().getFirstElement();
        if ( selection instanceof EObject ) {
            // set the selection on the TreeViewerRenameAction and run it
            inlineRenameAction.selectionChanged(new SelectionChangedEvent(this, getStructuredSelection()));
            inlineRenameAction.run();
            
        } else if ((selection instanceof IResource) && ModelerCore.hasModelNature(((IResource)selection).getProject())) { 
            if (selection instanceof IFile) {
                final IFile file = (IFile)selection;

                if (ModelUtilities.isModelFile(file)) {
                    renameModelResource(file);
                } else if ( file.getFileExtension() != null
                          && ModelerCore.VDB_FILE_EXTENSION.endsWith(file.getFileExtension().toLowerCase())) {
                    WidgetUtil.showError(RENAME_NOT_SUPPORTED_MESSAGE);            
                } else {
                    super.run();
                }
            } else if (selection instanceof IProject) {
                renameModelResource((IResource)selection);
            } else if (selection instanceof IFolder) {
                renameModelResource((IResource)selection);
            } else {
                super.run();
            }
        } else {
            super.run();
        }
    }

    /*
     *  jhTODO: reimplement this method so that it delegates to the refactor rename action
     */
    private void renameModelResource(IResource theResource) {

        //swjTODO: enable rename when it is supported, or replace with refactor.
        getRenameActionDelegate().selectionChanged( actRefactorRename, getSelection() );
                
        if ( actRefactorRename != null && actRefactorRename.isEnabled() ) {            
            actRefactorRename.run();
        }  
            
    }

    private IActionDelegate getRenameActionDelegate() {
        
        IWorkbenchWindow window 
            = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
            
        if ( actRefactorRename == null ) {
            delRefactorRename = new RenameRefactorAction();
            
            actRefactorRename = new DelegatableAction( delRefactorRename, window );
            actRefactorRename.setEnabled(false);
        }
        return delRefactorRename;      
        
    }


    /* (non-Javadoc)
     * jhTODO: reimplement this method so that it delegates to the refactor rename action also
     * @see org.eclipse.ui.actions.SelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection(IStructuredSelection selection) {
        boolean result = SelectionUtilities.isSingleSelection(selection);
        
        if (result) {
            EObject selectedEObject = SelectionUtilities.getSelectedEObject(selection);

            if (selectedEObject != null) {
                inlineRenameAction.selectionChanged(new SelectionChangedEvent(this, selection));
                result = inlineRenameAction.isEnabled();
            } else {
                getRenameActionDelegate().selectionChanged(actRefactorRename, selection);
                result = actRefactorRename.isEnabled();
            }
        }
        
        return result;        
    }
    
    // ====================================================
    // ISelectionProvider methods

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        return getStructuredSelection();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        throw new RuntimeException("ModelExplorerRenameAction.addSelectionChangedListener is not supported");   //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) { 
        throw new RuntimeException("ModelExplorerRenameAction.removeSelectionChangedListener is not supported");   //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        throw new RuntimeException("ModelExplorerRenameAction.setSelection is not supported");   //$NON-NLS-1$
    }

}
