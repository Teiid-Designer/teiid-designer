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

import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.ui.internal.util.WizardUtil;

/**
 * CustomPropertyOptionsWizardPage
 */
public class CustomPropertyOptionsWizardPage extends WizardPage implements ModelGeneratorUiConstants {
    //////////////////////////////////////////////////////////////////////////////////////
    // Static variables
    //////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // Instance variables
    ////////////////////////////////////////////////////////////////////////////////

    private CustomPropertyOptionsWizardPanel panel;
    private GeneratorManagerOptions generatorMgrOptions;
        
    ////////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Construct an instance of CustomPropertyOptions WizardPage.
     * @param pageName
     * @param mgrOptions the generator manager options
     */
    public CustomPropertyOptionsWizardPage(String pageName, GeneratorManagerOptions mgrOptions) {
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
        panel = new CustomPropertyOptionsWizardPanel(parent, this.generatorMgrOptions);
        super.setControl(panel);
        validatePage();
    }
    
    /**
     * Get the Column Custom Properties Map from the Generation Options Panel.
     * @return the custom Properties Map
     */
    public Map getColumnCustomPropsMap() {
        Map resultMap = null;
        if(this.panel!=null) {
            resultMap = this.panel.getColumnCustomPropsMap();
        }
        return resultMap;
    }
    
    /**
     * Get the Table Custom Properties Map from the Generation Options Panel.
     * @return the custom Properties Map
     */
    public Map getTableCustomPropsMap() {
        Map resultMap = null;
        if(this.panel!=null) {
            resultMap = this.panel.getTableCustomPropsMap();
        }
        return resultMap;
    }
    
    /**
     * Check whether the page is valid to continue.  Alway valid, since no inputs are required.
     */
    public void validatePage() {
        WizardUtil.setPageComplete(this); 
    }

}
