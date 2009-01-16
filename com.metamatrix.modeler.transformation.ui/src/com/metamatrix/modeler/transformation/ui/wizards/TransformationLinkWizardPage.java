/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.transformation.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.wizards.IStructuralCopyTreePopulator;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.widget.INodeDescendantsDeselectionHandler;
import com.metamatrix.ui.internal.widget.InheritanceCheckboxTreeViewer;

/**
 * TransformationLinkWizardPage
 */
public class TransformationLinkWizardPage extends WizardPage implements UiConstants,
		INodeDescendantsDeselectionHandler {

    ////////////////////////////////////////////////////////////////////////////////
	// Instance variables
	////////////////////////////////////////////////////////////////////////////////

    private ISelection selection;
	private MetamodelDescriptor metamodelDescriptor;
	private TransformationCheckboxTreeViewerWizardPanel panel;
	protected boolean targetIsVirtual;
	
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
    public void createControl(Composite parent) {

        ModelResource selectedResource = null;
        if ( selection != null && SelectionUtilities.isSingleSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IFile ) {
                if ( ModelUtilities.isModelFile((IFile) obj) ) {
                    try {
                        selectedResource = ModelUtilities.getModelResource((IFile) obj, false);
                        if ( ! selectedResource.getPrimaryMetamodelDescriptor().equals(this.metamodelDescriptor) ) {
                            selectedResource = null;
                        }
                    } catch (ModelWorkspaceException e) {
                        // no need to log, just launch the dialog empty
                    } 
                }
            }
        }

    	panel = new TransformationCheckboxTreeViewerWizardPanel(parent, this, this, 
    			metamodelDescriptor, selectedResource, targetIsVirtual);
        super.setControl(panel);
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

    public InheritanceCheckboxTreeViewer getViewer() {
    	return panel.getViewer();
    }
    
    public IStructuralCopyTreePopulator getTreePopulator() {
    	return panel.getTreePopulator();
    }
    
    /** 
     * @see com.metamatrix.ui.internal.widget.INodeDescendantsDeselectionHandler#deselectDescendants(java.lang.Object)
     * @since 4.2
     */
    public boolean deselectDescendants(Object theNode) {
        return true;
	}

}
