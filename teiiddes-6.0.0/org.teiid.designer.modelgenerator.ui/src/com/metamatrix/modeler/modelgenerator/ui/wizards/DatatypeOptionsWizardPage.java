/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.ui.internal.util.WizardUtil;

/**
 * DatatypeOptionsWizardPage
 */
public class DatatypeOptionsWizardPage extends WizardPage implements ModelGeneratorUiConstants {
    //////////////////////////////////////////////////////////////////////////////////////
    // Static variables
    //////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // Instance variables
    ////////////////////////////////////////////////////////////////////////////////

    private DatatypeOptionsWizardPanel panel;
    private GeneratorManagerOptions generatorMgrOptions;
        
    ////////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Construct an instance of DatatypeOptions WizardPage.
     * @param pageName
     * @param mgrOptions the generator manager options
     */
    public DatatypeOptionsWizardPage(String pageName, GeneratorManagerOptions mgrOptions) {
        super(pageName);
        this.generatorMgrOptions = mgrOptions;
        setTitle(Util.getString("GenerationOptionsWizardPage.title")); //$NON-NLS-1$
        setDescription(Util.getString("GenerationOptionsWizardPage.description")); //$NON-NLS-1$
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Instance methods
    ////////////////////////////////////////////////////////////////////////////////
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        panel = new DatatypeOptionsWizardPanel(parent, this, this.generatorMgrOptions);
        super.setControl(panel);
        validatePage();
    }
    
    /**
     * Check whether the page is valid to continue.  There must be at least one feature checked
     * in the tree.
     */
    public void validatePage() {
        // Check panel to see if it is valid
        if(!this.generatorMgrOptions.hasValidDatatypeSelections()) {
            WizardUtil.setPageComplete(this, this.generatorMgrOptions.getDatatypeSelectionStatusMessage(), IMessageProvider.ERROR); 
        } else {
            WizardUtil.setPageComplete(this); 
        }
    }
    
    /**
     * Method that the wizard can call to populate the  Uml2RelationalOptions with missing values if the
     * finish button is pressed before this page is activated.
     */
    public void preFinish() {
        if( panel == null ) {
            // No action
        }
    }
}
