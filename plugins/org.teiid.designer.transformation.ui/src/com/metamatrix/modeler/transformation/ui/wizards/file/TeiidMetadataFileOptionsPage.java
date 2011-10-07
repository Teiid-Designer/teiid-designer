/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.file;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public class TeiidMetadataFileOptionsPage  extends AbstractWizardPage
	implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataFileOptionsPage.class);
	private static final String TITLE = getString("title"); //$NON-NLS-1$
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
	TeiidMetadataImportWizard parentWizard;
	
	private Button csvOptionButton, xmlOptionButton;

	public TeiidMetadataFileOptionsPage(TeiidMetadataImportWizard parentWizard) {
		super(TeiidMetadataFileOptionsPage.class.getSimpleName(), TITLE);
		this.parentWizard = parentWizard;
	}

	@Override
	public void createControl(Composite parent) {
        // Create page
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        
        mainPanel.setLayout(new GridLayout());
        mainPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        setControl(mainPanel);
        // Add widgets to page
        
        
        Group mainGroup = WidgetFactory.createGroup(mainPanel, getString("formatOptions"), SWT.BORDER); //$NON-NLS-1$
        mainGroup.setLayout(new GridLayout());
        mainGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        this.csvOptionButton = WidgetFactory.createRadioButton(mainGroup, getString("flatFileCSVDataFormat"), true); //$NON-NLS-1$
        this.csvOptionButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	optionButtonSelected();
            }
        });
        this.xmlOptionButton = WidgetFactory.createRadioButton(mainGroup, getString("xmlFileDataFormat")); //$NON-NLS-1$
        this.xmlOptionButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	optionButtonSelected();
            }
        });
        
        setMessage(getString("message")); //$NON-NLS-1$
        
        setPageComplete(true);
	}
	
	void optionButtonSelected() {
		this.parentWizard.setFileOption(this.csvOptionButton.getSelection());
	}
	
}
