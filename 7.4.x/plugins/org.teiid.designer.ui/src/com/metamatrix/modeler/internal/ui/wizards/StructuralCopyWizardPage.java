/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.widget.INodeDescendantsDeselectionHandler;

public class StructuralCopyWizardPage extends WizardPage implements UiConstants,
		INodeDescendantsDeselectionHandler {
	//////////////////////////////////////////////////////////////////////////////////////
	// Static variables
	//////////////////////////////////////////////////////////////////////////////////////
	private static final int DESCENDANT_NODES_DESELECTION_YES = 1;
	private static final int DESCENDANT_NODES_DESELECTION_NO = 2;
	private static final int DESCENDANT_NODES_DESELECTION_PROMPT = 3;
	private static int deselectionInstruction = DESCENDANT_NODES_DESELECTION_PROMPT;
			
	//////////////////////////////////////////////////////////////////////////////////////
	// Instance variables
	//////////////////////////////////////////////////////////////////////////////////////
	private ISelection selection;
	private TreeViewerWizardPanel panel;
	private MetamodelDescriptor metamodelDescriptor;
	protected boolean targetIsVirtual;
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor for StructuralCopyWizardPage.
	 */
	public StructuralCopyWizardPage(ISelection selection, 
			MetamodelDescriptor metamodelDescriptor, boolean targetIsVirtual) {
		super("specifyStructuralCopyPage"); //$NON-NLS-1$
		setTitle(Util.getString("StructuralCopyWizardPage.title")); //$NON-NLS-1$
		setDescription(Util.getString("StructuralCopyWizardPage.description")); //$NON-NLS-1$
		this.selection = selection;
		this.metamodelDescriptor = metamodelDescriptor;
		this.targetIsVirtual = targetIsVirtual;
		setPageComplete(false);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// Instance methods
	//////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
        
        ModelResource selectedResource = null;
        if ( selection != null && SelectionUtilities.isSingleSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IFile ) {
                if ( ModelUtilities.isModelFile((IFile) obj) ) {
                    try {
                        selectedResource = ModelUtil.getModelResource((IFile) obj, false);
                        if ( ! selectedResource.getPrimaryMetamodelDescriptor().equals(this.metamodelDescriptor) ) {
                            selectedResource = null;
                        } else {
                        	setPageComplete(true);
                        }
                    } catch (ModelWorkspaceException e) {
                        // no need to log, just launch the dialog empty
                    } 
                }
            }
        }
        
		panel = new TreeViewerWizardPanel(parent, this, metamodelDescriptor, 
				selectedResource, targetIsVirtual);
		setControl(panel);
	}
	
	/**
	 * Return CheckboxTreeViewer, which contains tree structure representing features of 
	 * the existing model, along with indication of which are selected for copy
	 * 
	 * @return CheckboxTreeViewer containing tree structure representing features of
	 *             	the existing model, along with indication of which are selected for
	 * 			  	copy
	 */
	public TreeViewer getViewer() {
		return panel.getViewer();
	}
	
	public IStructuralCopyTreePopulator getTreePopulator() {
		return panel.getTreePopulator();
	}
	
	public boolean deselectDescendants(Object node) {
		boolean deselect;
		switch (StructuralCopyWizardPage.deselectionInstruction) {
			case StructuralCopyWizardPage.DESCENDANT_NODES_DESELECTION_YES:
				deselect = true;
				break;
			case StructuralCopyWizardPage.DESCENDANT_NODES_DESELECTION_NO:
				deselect = false;
				break;
			default:
				DeselectDescendantsDialog dialog = new DeselectDescendantsDialog(
						getShell());
				int response = dialog.open();
				boolean alwaysUseResponse = dialog.alwaysUseResponse();
				if (response == SWT.YES) {
					deselect = true;
					if (alwaysUseResponse) {
						StructuralCopyWizardPage.deselectionInstruction = 
								StructuralCopyWizardPage.DESCENDANT_NODES_DESELECTION_YES;
					} 
				} else {
					deselect = false;
					if (alwaysUseResponse) {
						StructuralCopyWizardPage.deselectionInstruction = 
								StructuralCopyWizardPage.DESCENDANT_NODES_DESELECTION_NO;
					}
				}
		}
		return deselect;
	}

    /** Tells whether the user has explicitly indiciated that they wish to 
     *  copy the entire selected model, rather than select pieces of it.
     * 
     * @return true if the user wishes to copy the entire model.
     */
    public boolean isCopyEntireModel() {
        return panel.isCopyEntireModel();
    }
    
    public boolean isCopyAllDescriptions() {
        return panel.isCopyAllDescriptions();
    }

}//end StructuralCopyWizardPage
