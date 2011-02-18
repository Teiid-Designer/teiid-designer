/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.ui.internal.util.WizardUtil;

/**
 * RelationshipOptionsWizardPage
 */
public class RelationshipOptionsWizardPage extends WizardPage implements ModelGeneratorUiConstants {
    //////////////////////////////////////////////////////////////////////////////////////
    // Static variables
    //////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // Instance variables
    ////////////////////////////////////////////////////////////////////////////////

    private RelationshipOptionsWizardPanel panel;
    private GeneratorManagerOptions generatorMgrOptions;
    private IResource targetResource;
    private IPath targetFilePath;
        
    ////////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Construct an instance of RelationshipOptions WizardPage.
     * @param pageName
     * @param mgrOptions the generator manager options
     * @param targetRes the target resource
     * @param targetFilePath the target FilePath
     */
    public RelationshipOptionsWizardPage(String pageName, GeneratorManagerOptions mgrOptions,
                                          IResource targetRes, IPath targetFilePath) {
        super(pageName);
        this.generatorMgrOptions = mgrOptions;
        this.targetResource = targetRes;
        this.targetFilePath = targetFilePath;
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
        panel = new RelationshipOptionsWizardPanel(parent, this, this.generatorMgrOptions);
        panel.setTargetResource(this.targetResource);
        panel.setTargetRelationalFilePath(this.targetFilePath);
        super.setControl(panel);
        validatePage();
    }
    
    /**
     * Check whether the page is valid to continue.  There must be at least one feature checked
     * in the tree.
     */
    public void validatePage() {
        // Check panel to see if it is valid
        if(!this.generatorMgrOptions.hasValidRelationshipOptions()) {
            WizardUtil.setPageComplete(this, this.generatorMgrOptions.getRelationshipOptionsStatusMessage(), IMessageProvider.ERROR); 
        } else {
            WizardUtil.setPageComplete(this); 
        }
    }

    public void nextPressed() {
        panel.activateRelationshipModel();
    }
    
}
