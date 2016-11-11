/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.jdg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.transformation.materialization.MaterializedModelManager;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

public class PojoWizardPage_3 extends AbstractWizardPage implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(PojoWizardPage_3.class);
	private static final String TITLE = getString("title"); //$NON-NLS-1$

	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}
   
    private boolean synchronizing;
    
    private MaterializedModelManager manager;

	public PojoWizardPage_3(MaterializedModelManager generator) {
		super(PojoWizardPage_3.class.getSimpleName(), TITLE);
		this.manager = generator;
	}

	@Override
	public void createControl(Composite parent) {
		// Create page

		final Composite hostPanel = new Composite(parent, SWT.NONE);
		hostPanel.setLayout(new GridLayout(1, false));
		hostPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Create page
		DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel);
		hostPanel.setLayout(new GridLayout(1, false));
		hostPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Composite mainPanel = scrolledComposite.getPanel();
		mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainPanel.setLayout(new GridLayout(1, false));

		scrolledComposite.sizeScrolledPanel();

		setControl(hostPanel);

		setMessage(getString("initialMessage")); //$NON-NLS-1$

		setPageComplete(true);
	}


	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			synchronizeUI();
		}
	}
	
    void synchronizeUI(){
    	synchronizing = true;
        
    	// TODO:
                
        synchronizing = false;
    }
}