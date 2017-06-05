/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datasources.ui.sources;

import org.eclipse.core.runtime.Status;
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
import org.teiid.designer.datasources.ui.UiPlugin;
import org.teiid.designer.ui.forms.FormUtil;

public class DataSourcesPanel extends ManagedForm  {
    FormToolkit toolkit;

    private ScrolledForm parentForm;
    private DataSourcesSection sourcesSection;
    private DefaultServerSection serverSection;
//    
//    private AdvisorHyperLinkListener linkListener;
//
//    private StatusActionHandler actionHandler;

    /**
     * @since 4.3
     */
    public DataSourcesPanel( Composite parent , GlobalConnectionManager manager) {
        super(parent);
        
        this.parentForm = this.getForm();

        initGUI(manager);

    }

    private void initGUI(GlobalConnectionManager manager) {
        this.parentForm.setLayout(new GridLayout(1, true));
        GridData gd = new GridData(GridData.FILL_BOTH);
        this.parentForm.setLayoutData(gd);

        this.toolkit = getToolkit();
        Color bkgdColor = toolkit.getColors().getBackground();
        parentForm.setBackground(bkgdColor);

        //this.parentForm.setText(Messages.TeiidActionsManager);

        this.parentForm.setLayout(new GridLayout());

        this.parentForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        FormUtil.tweakColors(toolkit, parentForm.getDisplay());
        this.parentForm.setBackground(bkgdColor);
        
        Form form = this.parentForm.getForm();
        
//        contributeToMenu(form.getMenuManager());

//        this.linkListener = new AdvisorHyperLinkListener(this.getForm(), this.toolkit, this.actionHandler);

        Composite body = parentForm.getBody();
		//int nColumns = 2;
		GridLayout gl = new GridLayout(2, false);
		body.setLayout(gl);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		body.setLayoutData(gd);

		sourcesSection = new DataSourcesSection(toolkit, body);
		
		serverSection = new DefaultServerSection(toolkit, body, sourcesSection);
    }

    @Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
//		AdvisorUiPlugin.getStatusManager().removeListener(this);
	}

	@Override
    public FormToolkit getToolkit() {
        if (this.toolkit == null) {
            Display display = parentForm.getDisplay();
            if (UiPlugin.getDefault() != null) {
                this.toolkit = UiPlugin.getDefault().getFormToolkit(display);
            } else {
                this.toolkit = new FormToolkit(display);
            }
        }

        return this.toolkit;
    }

    /**
     * This private method handles updating the various status widgets in the summary table Items with problems get an X checkbox,
     * items without get a green checked checkbox. We also set the overall status for the summary section header and description.
     * 
     * @param status
     * @since 4.3
     */
    public void updateStatus( Status theStatus ) {

    }



//    private void contributeToMenu( IMenuManager menuMgr ) {
//    	AdvisorActionFactory.addActionsLibraryToMenu(menuMgr);
//        menuMgr.update(true);
//    }
}