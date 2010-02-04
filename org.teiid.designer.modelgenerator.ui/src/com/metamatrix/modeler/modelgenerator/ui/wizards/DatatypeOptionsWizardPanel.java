/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;

/**
 * DatatypeOptionsWizardPanel.  This panel contains the datatype options.
 */
public class DatatypeOptionsWizardPanel extends Composite 
        implements ModelGeneratorUiConstants, StringUtil.Constants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private DatatypeOptionsWizardPage wizardPage;

    private GeneratorManagerOptions generatorMgrOptions;  

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public DatatypeOptionsWizardPanel(Composite parent, DatatypeOptionsWizardPage page,
                                       GeneratorManagerOptions generatorMgrOptions) {
        super(parent, SWT.NULL);
        this.wizardPage = page;
        this.generatorMgrOptions = generatorMgrOptions;
        
        initialize();
        
        wizardPage.validatePage();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the Panel
     */
    private void initialize() {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        this.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        String treeTitle = Util.getString("DatatypeOptionsWizardPanel.title"); //$NON-NLS-1$
        CheckboxTreePanel treePanel = new CheckboxTreePanel( this, treeTitle, generatorMgrOptions.getDatatypeSelections()); 
        treePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
    }
        
}//end DatatypeOptionsWizardPanel
