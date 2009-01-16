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
