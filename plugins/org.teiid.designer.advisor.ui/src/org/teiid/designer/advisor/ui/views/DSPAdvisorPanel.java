/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.actions.AdvisorActionFactory;

import com.metamatrix.modeler.internal.ui.forms.FormUtil;

/**
 * 
 */
public class DSPAdvisorPanel extends ManagedForm 
	implements AdvisorUiConstants.Images, AdvisorUiConstants.Groups { // IPropertyChangeListener,

    FormToolkit toolkit;

    private ScrolledForm parentForm;
    //private DSPStatusSection statusSection;


    /**
     * @since 4.3
     */
    public DSPAdvisorPanel( Composite parent ) {
        super(parent);

        this.parentForm = this.getForm();

        this.parentForm.setAlwaysShowScrollBars(true);
        initGUI();
    }

    private void initGUI() {
        this.parentForm.setLayout(new GridLayout(1, true));
        GridData gd = new GridData(GridData.FILL_BOTH);
        this.parentForm.setLayoutData(gd);

        // addProjectComboSelector(this);

        this.toolkit = getToolkit();
        
        Color bkgdColor = toolkit.getColors().getBackground();

        parentForm.setBackground(bkgdColor);
        // parentForm = toolkit.createForm(this);

        this.parentForm.setText("All Modeling Actions..."); //DSPAdvisorI18n.TeiidProjectAdvisor);

        this.parentForm.setLayout(new GridLayout());
        // parentForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData formGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        //formGD.verticalAlignment = SWT.BEGINNING;
        this.parentForm.setLayoutData(formGD);

        FormUtil.tweakColors(toolkit, parentForm.getDisplay());
        this.parentForm.setBackground(bkgdColor);
        
        Form form = this.parentForm.getForm();
        
        contributeToMenu(form.getMenuManager());

        new AdvisorGuidesSection(toolkit, parentForm.getBody());
        
        new DSPCheatSheetSection(toolkit, parentForm.getBody());

        Composite body = parentForm.getBody();
		GridLayout gl = new GridLayout(2, false);
		body.setLayout(gl);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		body.setLayoutData(gd);

    }

    @Override
    public FormToolkit getToolkit() {
        if (this.toolkit == null) {
            Display display = parentForm.getDisplay();
            if (AdvisorUiPlugin.getDefault() != null) {
                this.toolkit = AdvisorUiPlugin.getDefault().getFormToolkit(display);
            } else {
                this.toolkit = new FormToolkit(display);
            }
        }

        return this.toolkit;
    }

    private void contributeToMenu( IMenuManager menuMgr ) {
    	AdvisorActionFactory.addActionsLibraryToMenu(menuMgr);
        menuMgr.update(true);
    }
    
}
