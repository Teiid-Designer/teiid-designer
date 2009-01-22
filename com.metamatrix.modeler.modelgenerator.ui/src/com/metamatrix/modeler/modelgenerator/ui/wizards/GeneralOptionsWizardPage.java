/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.modeler.modelgenerator.ui.ModelerModelGeneratorUiPlugin;
import com.metamatrix.modeler.modelgenerator.ui.PluginConstants;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalOptions;
import com.metamatrix.ui.internal.util.WizardUtil;

/**
 * GeneralOptionsWizardPage
 */
public class GeneralOptionsWizardPage extends WizardPage implements ModelGeneratorUiConstants {
    //////////////////////////////////////////////////////////////////////////////////////
    // Static variables
    //////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // Instance variables
    ////////////////////////////////////////////////////////////////////////////////

    private GeneralOptionsWizardPanel panel;
    private GeneratorManagerOptions generatorMgrOptions;
        
    ////////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Construct an instance of GeneralOptions WizardPage.
     * @param pageName
     * @param mgrOptions the generator manager options
     */
    public GeneralOptionsWizardPage(String pageName, GeneratorManagerOptions mgrOptions) {
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
        panel = new GeneralOptionsWizardPanel(parent, this, this.generatorMgrOptions);
        super.setControl(panel);
        validatePage();
    }
    
    /**
     * Check whether the page is valid to continue.  There must be at least one feature checked
     * in the tree.
     */
    public void validatePage() {
        // Check panel to see if it is valid
        if(!this.generatorMgrOptions.hasValidGeneratorOptions()) {
            WizardUtil.setPageComplete(this, this.generatorMgrOptions.getGeneratorOptionsStatusMessage(), IMessageProvider.ERROR); 
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
            // Set initial selections from preferences
            IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore(); 
            Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
            
            //==============================================================
            // GENERAL OPTIONS
            //==============================================================
            
            //---------------------------------------
            // Relational Column Datatype
            //---------------------------------------
            String datatypeStr = prefStore.getString(PluginConstants.Prefs.ModelGenerator.RELATIONAL_COLUMN_TYPE);
            EObject relationalColType = null;
            try {
                relationalColType = ModelerCore.getWorkspaceDatatypeManager().findDatatype(datatypeStr);
            } catch (ModelerCoreException e) {
                Util.log(e);
            }
            // Set generator Option
            options.setDefaultRelationalColumnType(relationalColType);
            
            //---------------------------------------
            // Relational Column Default length
            //---------------------------------------
            int defaultLength = prefStore.getInt(PluginConstants.Prefs.ModelGenerator.DEFAULT_STRING_LENGTH);
            // Set generator Option
            options.setGeneratedStringTypeColumnDefaultLength(defaultLength);

            //---------------------------------------
            // Package Usage Option
            //---------------------------------------
            int usageOption = prefStore.getInt(PluginConstants.Prefs.ModelGenerator.PACKAGE_USAGE);
            // Set generator Option

            if(usageOption==Uml2RelationalOptions.PackageUsage.FLATTEN) {
                options.setPackageUsage(Uml2RelationalOptions.PackageUsage.FLATTEN_LITERAL);
            } else if(usageOption==Uml2RelationalOptions.PackageUsage.IGNORE) {
                options.setPackageUsage(Uml2RelationalOptions.PackageUsage.IGNORE_LITERAL);
            }

            //---------------------------------------
            // Reachability Constraint Option
            //---------------------------------------
            int reachConstr = prefStore.getInt(PluginConstants.Prefs.ModelGenerator.REACHABILITY_CONTRAINT);
            // Set generator Option
            options.setReachabilityConstraint(reachConstr);

            //---------------------------------------
            // Class Ignored Stereotypes List
            //---------------------------------------
            String listString = prefStore.getString(PluginConstants.Prefs.ModelGenerator.CLASS_IGNORED_STEREOTYPES);
            if(listString!=null) {
                String[] array = parseList(listString);
                // Set generator Option
                options.setClassStereotypesToIgnore(Arrays.asList(array));
            }
        }
    }
    
    /**
     * Parses the comma separated string into an array of strings
     * 
     * @return list
     */
    private static String[] parseList(String listString) {
        List list = new ArrayList(10);
        StringTokenizer tokenizer = new StringTokenizer(listString, ","); //$NON-NLS-1$
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            list.add(token);
        }
        return (String[])list.toArray(new String[list.size()]);
    }
}
