/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.modeler.compare.PropertyDifference;
import com.metamatrix.modeler.compare.ui.tree.DifferenceReportsPanel;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.ui.internal.util.WizardUtil;

/**
 * RefreshOptionsWizardPage
 */
public class RefreshOptionsWizardPage extends WizardPage implements ModelGeneratorUiConstants {
	//////////////////////////////////////////////////////////////////////////////////////
	// Static variables
	//////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	// Instance variables
	////////////////////////////////////////////////////////////////////////////////

	private DifferenceReportsPanel panel;
    private IModelGeneratorManager modelGeneratorMgr;
    private ModelResource modelResource;
    private List diffReports;
    boolean isVisible = false;
    	
	////////////////////////////////////////////////////////////////////////////////
	// Constructors
	////////////////////////////////////////////////////////////////////////////////
	/**
     * Construct an instance of TransformationLinkWizardPage.
     * @param pageName
     */
    public RefreshOptionsWizardPage(String pageName,IModelGeneratorManager modelGeneratorMgr,
                                    ModelResource modelResource) {
        super(pageName);
        this.modelGeneratorMgr = modelGeneratorMgr;
        this.modelResource = modelResource;
        setTitle(Util.getString("RefreshOptionsWizardPage.title")); //$NON-NLS-1$
        setDescription(Util.getString("RefreshOptionsWizardPage.description")); //$NON-NLS-1$
    }

    ////////////////////////////////////////////////////////////////////////////////
	// Instance methods
	////////////////////////////////////////////////////////////////////////////////
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        // Create the Difference Reports Panel
        String treeTitle = Util.getString("RefreshOptionsWizardPage.treeTitle"); //$NON-NLS-1$
        String tableTitle = Util.getString("RefreshOptionsWizardPage.diffDescriptorTitle"); //$NON-NLS-1$

        boolean enableProperySelection = true;       
        boolean showCheckboxes = true;

        panel = new DifferenceReportsPanel(parent, treeTitle, tableTitle, enableProperySelection, showCheckboxes);
        
        TableViewer tableViewer = panel.getTableViewer();
        if(tableViewer instanceof CheckboxTableViewer) {
            ((CheckboxTableViewer)tableViewer).addCheckStateListener(new ICheckStateListener() {
                public void checkStateChanged(CheckStateChangedEvent theEvent) {
                    Object checkedObject = theEvent.getElement();
                    boolean isChecked = theEvent.getChecked();
                    if(checkedObject instanceof PropertyDifference) {
                        PropertyDifference propDiff = (PropertyDifference)checkedObject;
                        propDiff.setSkip(!isChecked);
                    }
                }
            });
        }
        
        super.setControl(panel);
        validatePage();
    }
    
    @Override
    public void setVisible(boolean isVisible) {
        this.isVisible=isVisible;
        if(isVisible) {
            IProgressMonitor monitor = null;
            this.diffReports = this.modelGeneratorMgr.getDifferenceReports(this.modelResource,monitor);
            this.panel.setDifferenceReports(this.diffReports);
        }
        super.setVisible(isVisible);
    }
    
    /**
     * Check whether the page is visible.
     * @return 'true' if the page is Visible, 'false' if not.
     */
    public boolean isVisible() {
        return this.isVisible;
    }
    
    /**
     * Check whether the page is valid to continue.  There must be at least one feature checked
     * in the tree.
     */
    public void validatePage() {
        WizardUtil.setPageComplete(this); 
        // Check panel to see if it is valid
//        if(!panel.isValid()) {
//            WizardUtil.setPageComplete(this, panel.getValidationMessage(), WizardPage.ERROR); 
//        } else {
//            WizardUtil.setPageComplete(this); 
//        }
    }
    
}
