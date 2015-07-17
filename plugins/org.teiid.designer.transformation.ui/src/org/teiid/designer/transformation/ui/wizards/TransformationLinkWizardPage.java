/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.widget.INodeDescendantsDeselectionHandler;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.wizards.IStructuralCopyTreePopulator;


/**
 * TransformationLinkWizardPage
 *
 * @since 8.0
 */
public class TransformationLinkWizardPage extends WizardPage implements UiConstants,
		INodeDescendantsDeselectionHandler {

    ////////////////////////////////////////////////////////////////////////////////
	// Instance variables
	////////////////////////////////////////////////////////////////////////////////

    private ISelection selection;
	private MetamodelDescriptor metamodelDescriptor;
	private TransformationTreeViewerWizardPanel panel;
	protected boolean targetIsVirtual;
	protected boolean sourceIsPhysical;
	
	////////////////////////////////////////////////////////////////////////////////
	// Constructors
	////////////////////////////////////////////////////////////////////////////////
	/**
     * Construct an instance of TransformationLinkWizardPage.
     * @param pageName
     */
    public TransformationLinkWizardPage(String pageName, ISelection selection,
    		MetamodelDescriptor metamodelDescriptor, boolean targetIsVirtual) {
        super(pageName);
        this.selection = selection;
        this.metamodelDescriptor = metamodelDescriptor;
        this.targetIsVirtual = targetIsVirtual;
        setTitle(Util.getString("TransformationLinkWizardPage.title")); //$NON-NLS-1$
        setDescription(Util.getString("TransformationLinkWizardPage.description")); //$NON-NLS-1$
    }

    ////////////////////////////////////////////////////////////////////////////////
	// Instance methods
	////////////////////////////////////////////////////////////////////////////////
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
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
                        }
                    } catch (ModelWorkspaceException e) {
                        // no need to log, just launch the dialog empty
                    } 
                }
            }
        }
        
        if( selectedResource != null ) {
        	sourceIsPhysical = ModelIdentifier.isPhysicalModelType(selectedResource);
        }

    	panel = new TransformationTreeViewerWizardPanel(parent, this,
    			metamodelDescriptor, selectedResource, sourceIsPhysical, targetIsVirtual );
        super.setControl(panel);
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
    
    public boolean isClearSupportsUpdate() {
        return panel.isClearSupportsUpdate();
    }
    
    public boolean isCopyAllDescriptions() {
        return panel.isCopyAllDescriptions();
    }

    /** Tells whether the user has explicitly indiciated that they wish to 
     *  copy the entire selected model, rather than select pieces of it.
     * 
     * @return true if the user wishes to copy the entire model.
     */
    public boolean isCopyEntireModel() {
        return panel.isCopyEntireModel();
    }

    public TreeViewer getViewer() {
    	return panel.getViewer();
    }
    
    public IStructuralCopyTreePopulator getTreePopulator() {
    	return panel.getTreePopulator();
    }
    
    /** 
     * @see org.teiid.designer.ui.common.widget.INodeDescendantsDeselectionHandler#deselectDescendants(java.lang.Object)
     * @since 4.2
     */
    @Override
	public boolean deselectDescendants(Object theNode) {
        return true;
	}
    
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if( visible ) {
			ModelUtilities.warnIfUnsupportedModelInfoWontBeCopied(getSelectedResource());
		}
	}

}
