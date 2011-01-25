/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.AbstractModelerAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.IActionWorker;
import com.metamatrix.ui.actions.WorkerProblem;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * ModelObjectAction
 */
public abstract class ModelObjectAction extends AbstractModelerAction {
    private IActionWorker actionWorker;
    /**
     * Construct an instance of ModelObjectAction.
     * @param thePlugin
     */
    public ModelObjectAction(AbstractUiPlugin thePlugin) {
        super(thePlugin);
    }

    /**
     * Construct an instance of ModelObjectAction.
     * @param thePlugin
     * @param theStyle
     */
    public ModelObjectAction(AbstractUiPlugin thePlugin, int theStyle) {
        super(thePlugin, theStyle);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        if( this.actionWorker != null ) {
            if( actionWorker.execute() ) {
                if( actionWorker.getEnableAfterExecute() )
                    setEnabled(actionWorker.setEnabledState());
            } else if( actionWorker.getWorkerProblem() != null ) {
                WorkerProblem problem = actionWorker.getWorkerProblem();
                Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                MessageDialog.openError(shell, problem.getTitle(), problem.getMessage());
            }
        }
    }
    
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        if (this.actionWorker != null && ModelUtilities.isAllModelProjectMembers(theSelection)) {
            setEnabled(actionWorker.selectionChanged(theSelection));
        } else {
            // make sure action is disabled
            if (isEnabled()) {
                setEnabled(false);
            }
        }
    }
    /**
     * This method provides modeler actions a quick way to determine whether the selected EObjects
     * are in a model that is read-only.  This is needed for enable/disable when selection changes.
     * @param theSelection
     * @return <code>true</code> if at least one of the selected EObjects or models is readonly;
     * <code>false</code> otherwise.
     */
    protected boolean isReadOnly() {
        boolean isreadonly = true;
        List allEObjects = SelectionUtilities.getSelectedEObjects(getSelection());

        if (!allEObjects.isEmpty()) {
            isreadonly = false;
            Iterator iter = allEObjects.iterator();

            while( iter.hasNext() ) {
                if( ModelObjectUtilities.isReadOnly((EObject)iter.next()) ) {
                    isreadonly = true; 
                    break;
                }
            }
        } else {
            List selectedObjs = SelectionUtilities.getSelectedObjects(getSelection());
            
            if (!selectedObjs.isEmpty()) {
                isreadonly = false;
                
                for (int size = selectedObjs.size(), i = 0; i < size; i++) {
                    Object obj = selectedObjs.get(i);
                    
                    if ((obj instanceof IResource) && ModelUtil.isIResourceReadOnly(((IResource) obj))) { 
                        isreadonly = true;
                        break;
                    } else if (!(obj instanceof IResource)) {
                        isreadonly = true;
                        break;
                    }
                }
            }
        }
        
        return isreadonly;
    }
    
    protected boolean areEObjectsSelected() {
        return !SelectionUtilities.getSelectedEObjects(getSelection()).isEmpty();
    }
    
    
    protected boolean isEObjectSelected() {
        ISelection sel = getSelection();
        return SelectionUtilities.isSingleSelection(sel) && (SelectionUtilities.getSelectedEObject(sel) != null) ;
    }
    
    /**
     * This method provides each action the ability to force an editor to be opened before the doRun() is
     * executed.  This, of course, should be preceded in the the process of action enabling by the isReadOnly()
     * call. Most edit actions on model objects would return "true".
     * @return requiresEditorForRun
     */
    abstract protected boolean requiresEditorForRun();
    
    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     * This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        if( requiresEditorForRun() ) {
            List allSelectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            if( allSelectedEObjects != null &&  !allSelectedEObjects.isEmpty() ) {
                EObject eObject = (EObject)allSelectedEObjects.get(0);
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObject);
                if( mr != null ) {
                    // Defect 19537 - to properly rename new objects in tree, need to call new activate() method which allows
                    // forcing the initial active part to be STILL active after activating the model editor
                    ModelEditorManager.activate(mr, true, true);
                }
            }
        }
        return true;
    }
    
    protected boolean canLegallyEditResource() {
		boolean canEdit = false;
		List allEObjects = SelectionUtilities.getSelectedEObjects(getSelection());

		if( allEObjects != null && !allEObjects.isEmpty() ) {
            canEdit = true;
		} else {
            // make sure only models selected and that they are licensed
            List selectedObjs = SelectionUtilities.getSelectedObjects(getSelection());
            
            if (!selectedObjs.isEmpty()) {
                canEdit = true;
            
                for (int size = selectedObjs.size(), i = 0; i < size; i++) {
                    Object obj = selectedObjs.get(i);
                    
                    if ((obj instanceof IResource) && ModelUtilities.isModelFile((IResource)obj)) {
                        
                        try {
                            if (ModelUtil.getModelResource(((IFile)obj), false) == null) {
                                canEdit = false;
                                break;
                            }
                        } catch (ModelWorkspaceException theException) {
                            canEdit = false;
                            getPluginUtils().log(theException);
                            break;
                        }
                    } else {
                        canEdit = false;
                        break;
                    }
                }
            }
        } 
        
		return canEdit;
    }

    public IActionWorker getActionWorker() {
        return this.actionWorker;
    }
    public void setActionWorker(IActionWorker actionWorker) {
        this.actionWorker = actionWorker;
    }
}
