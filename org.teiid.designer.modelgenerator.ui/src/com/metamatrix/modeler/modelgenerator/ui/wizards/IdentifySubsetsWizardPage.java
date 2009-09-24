/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.EventObject;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.ui.internal.util.WizardUtil;

/**
 * IdentifySubsetsWizardPage - this Wizard Page is used to select the subset of
 * UML features to use in generating a new, Virtual Relational Model
 */
public class IdentifySubsetsWizardPage extends WizardPage implements EventObjectListener,
                                                                     ModelGeneratorUiConstants,
                                                                     StringUtil.Constants {
	//////////////////////////////////////////////////////////////////////////////////////
	// Static variables
	//////////////////////////////////////////////////////////////////////////////////////
    private static final String NO_SELECTIONS = Util.getString("IdentifySubsetsWizardPage.noSelections.text"); //$NON-NLS-1$

	////////////////////////////////////////////////////////////////////////////////
	// Instance variables
	////////////////////////////////////////////////////////////////////////////////
    private ModelWorkspaceSelections umlInputSelections;
    private CheckboxTreePanel treePanel;
    private IModelGeneratorManager modelGeneratorMgr;
    private List initialSelections;

	////////////////////////////////////////////////////////////////////////////////
	// Constructors
	////////////////////////////////////////////////////////////////////////////////
	/**
     * Construct an instance of IdentifySubsetsWizardPage.
     * @param pageName
     */
    public IdentifySubsetsWizardPage(String pageName, String title, String descr, 
                                      IModelGeneratorManager modelGeneratorMgr) {
        super(pageName);
        this.modelGeneratorMgr = modelGeneratorMgr;
        this.umlInputSelections = this.modelGeneratorMgr.getUmlInputSelections();
        setTitle(title); 
        setDescription(descr); 
    }

    ////////////////////////////////////////////////////////////////////////////////
	// Instance methods
	////////////////////////////////////////////////////////////////////////////////
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        String title = Util.getString("IdentifySubsetsWizardPage.selectionTree.title"); //$NON-NLS-1$
    	this.treePanel = new CheckboxTreePanel(parent,title,this.umlInputSelections);
        if(this.initialSelections!=null) {
            setTreeSelections(this.initialSelections);
        }
        // Add listener for checkbox changes
        treePanel.addEventListener(this);
        super.setControl(treePanel);
        validatePage();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // EventObjectListener Interface
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Method that handles Events from the CheckboxTreePanel.  This just validates the
     * page whenever the checkboxTree selections have changed.  
     * @param e the EventObject
     */
    public void processEvent(EventObject e) {
        validatePage();
    }
    
    /**
     * Set the Checkbox Tree Selections
     * @param selectedObjs the selected tree objects
     */
    public void setTreeSelections(final List selectedObjs) {
        this.initialSelections = selectedObjs;
        if(this.treePanel!=null) {
            this.treePanel.setTreeSelections(selectedObjs); 
        }
    }
    
    /**
     * Check whether the page is valid to continue.  There must be at least one feature checked
     * in the tree.
     */
    private void validatePage() {
        boolean hasValidSelections = this.modelGeneratorMgr.hasValidSourceSelections();
        // If no selections, page is not complete
        if(!hasValidSelections) {
            WizardUtil.setPageComplete(this, NO_SELECTIONS, IMessageProvider.ERROR); 
        } else {
            boolean hasUmlSelections = ((ModelGeneratorManager)this.modelGeneratorMgr).hasUmlModelSelections();
            if( hasUmlSelections ) {
                WizardUtil.setPageComplete(this);
            } else {
                WizardUtil.setPageComplete(this, NO_SELECTIONS, IMessageProvider.ERROR); 
            }
        }
    }
    
}
