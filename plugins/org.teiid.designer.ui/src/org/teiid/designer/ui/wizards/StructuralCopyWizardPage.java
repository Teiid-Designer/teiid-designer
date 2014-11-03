/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.widget.INodeDescendantsDeselectionHandler;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
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
	@Override
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
	
	
	private ModelResource getSelectedResource() {
        ModelResource selectedResource = null;
        if ( selection != null && SelectionUtilities.isSingleSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IFile ) {
                if ( ModelUtilities.isModelFile((IFile) obj) ) {
                    try {
                        selectedResource = ModelUtil.getModelResource((IFile) obj, false);
                        if ( ! selectedResource.getPrimaryMetamodelDescriptor().equals(this.metamodelDescriptor) ) {
                            selectedResource = null;
                        }
                    } catch (ModelWorkspaceException e) {
                        // no need to log, just launch the dialog empty
                    } 
                }
            }
        }
        
        return selectedResource;
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
	
	@Override
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
    
    public boolean doGenerateDefaultSQL() {
        return panel.doGenerateDefaultSQL();
    }
    
    private void warnIfUnsupportedModelInfo() {
        ModelResource mr = getSelectedResource();
        if( mr == null ) return;
        
    	// Get assistants
    	Collection<ModelObjectExtensionAssistant> assistants = new ArrayList<ModelObjectExtensionAssistant>();
    	
        for (String namespacePrefix : ExtensionPlugin.getInstance().getRegistry().getAllNamespacePrefixes()) {
        	ModelExtensionAssistant assistant = ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(namespacePrefix);
            if (assistant instanceof ModelObjectExtensionAssistant) {
            	assistants.add((ModelObjectExtensionAssistant)assistant);
            }
        }
    	
        boolean doWarn = false;
		try {
			// Iterate through model's eObjects and bail 
			Iterator<?> iter = mr.getEmfResource().getAllContents();
			doWarn = false;
			
			while (iter.hasNext()) {
			    final EObject eObj = (EObject) iter.next();
			    
			    for( ModelObjectExtensionAssistant nextAss : assistants ) {
			        if( nextAss.supportsMyNamespace(eObj)) {
			        	Properties props = nextAss.getOverriddenValues(eObj);
			        	if( props != null && !props.isEmpty() ) doWarn = true;;
			        	
			        	if( doWarn ) break;
			        }
			    }
			    if( doWarn ) {
			    	break;
			    }
			}
		} catch (ModelWorkspaceException e) {
			UiConstants.Util.log(e);
		} catch (Exception e) {
			UiConstants.Util.log(e);
		}

        if( doWarn ) {
			MessageDialog.openWarning(this.getShell(), UiConstants.Util.getString("StructuralCopyWizardPage.modelContainsExtensionPropertiesWarningTitle"),  //$NON-NLS-1$
					UiConstants.Util.getString("StructuralCopyWizardPage.modelContainsExtensionPropertiesWarningMsg", mr.getItemName()));  //$NON-NLS-1$
        }
    }

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if( visible ) {
			warnIfUnsupportedModelInfo();
		}
	}

}//end StructuralCopyWizardPage
