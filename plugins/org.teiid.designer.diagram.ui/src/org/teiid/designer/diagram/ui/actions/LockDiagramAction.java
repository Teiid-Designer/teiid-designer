/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.diagram.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *
 */
public class LockDiagramAction extends DiagramAction {

	DiagramEditor editor = null;
	
	/**
	 * 
	 */
	public LockDiagramAction(DiagramEditor editor) {
		super(SWT.TOGGLE);
		this.editor = editor;
		setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.UNLOCKED_IMAGE));
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        setEnabled(true);
        
        updateUi();
    }
    
    private void updateUi() {
        ModelResource mr = this.editor.getCurrentModelResource();
        if( mr != null ) {
	        boolean currentValue = ModelUtilities.isModelDiagramLocked(mr);
	        if( currentValue ) {
	        	setToolTipText(DiagramUiConstants.Util.getString("LockDiagramAction.unlockTooltip")); //$NON-NLS-1$
	        	setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.LOCKED_IMAGE));
	        } else {
	        	setToolTipText(DiagramUiConstants.Util.getString("LockDiagramAction.lockTooltip")); //$NON-NLS-1$
	        	setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.UNLOCKED_IMAGE));
	        }
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        // Get current DiagramEditor
    	ModelResource mr = this.editor.getCurrentModelResource();
    	boolean currentValue = ModelUtilities.isModelDiagramLocked(mr);
    	if( currentValue ) {
    		ModelUtilities.unlockModelDiagrams(mr);
    	} else {
    		ModelUtilities.lockModelDiagrams(mr);
    	}
    	
        try {
        	ModelEditor modelEditor = ModelEditorManager.getModelEditorForFile((IFile)mr.getUnderlyingResource(), false);
        	if( modelEditor != null ) {
        		modelEditor.doSave(new NullProgressMonitor());
        	}
        } catch (final Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
        
        UiUtil.runInSwtThread(new Runnable() {
			@Override
			public void run() {
				editor.doRefreshDiagram();
			}
		}, true);
    	
    	updateUi();
    }
}
