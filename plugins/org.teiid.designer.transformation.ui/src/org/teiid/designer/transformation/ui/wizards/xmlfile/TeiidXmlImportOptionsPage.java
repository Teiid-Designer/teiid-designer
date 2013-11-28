/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.xmlfile;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;


/**
 * @since 8.0
 */
public class TeiidXmlImportOptionsPage extends AbstractWizardPage implements
		UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidXmlImportOptionsPage.class);
	private static final String TITLE = getString("title"); //$NON-NLS-1$

	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}

	private Button xmlFileOptionButton, xmlUrlOptionButton;
	
	private final TeiidMetadataImportInfo fileInfo;
	
	private Properties designerProperties;
	
	private boolean synchronizing = false;

	public TeiidXmlImportOptionsPage(TeiidMetadataImportInfo fileInfo) {
		super(TeiidXmlImportOptionsPage.class.getSimpleName(), TITLE);
		this.fileInfo = fileInfo;
	}

	@Override
	public void createControl(Composite parent) {
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout());
		GridData mpGD = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		mpGD.widthHint = 400;
		mainPanel.setLayoutData(mpGD);
		//mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);
		// Add widgets to page
		Group descriptionGroup = WidgetFactory.createGroup(mainPanel, getString("description"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$

        Text descriptionText = new Text(descriptionGroup,  SWT.WRAP | SWT.READ_ONLY);
        GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.heightHint = 120;
        gd.widthHint = 300;
        descriptionText.setLayoutData(gd);
        descriptionText.setText(getString("descriptionMessage")); //$NON-NLS-1$
        descriptionText.setBackground(mainPanel.getBackground());
        descriptionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		

		Group mainGroup = WidgetFactory.createGroup(mainPanel,getString("xmlOptionsGroup"), SWT.BORDER); //$NON-NLS-1$
		mainGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.xmlFileOptionButton = WidgetFactory.createRadioButton(mainGroup,getString("localXmlFileOption"), true); //$NON-NLS-1$
		this.xmlFileOptionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if( !synchronizing ) {
					optionButtonSelected();
				}
			}
		});
		this.xmlUrlOptionButton = WidgetFactory.createRadioButton(mainGroup,getString("urlXmlFileOption")); //$NON-NLS-1$
		this.xmlUrlOptionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if( !synchronizing ) {
					optionButtonSelected();
				}
			}
		});

		setMessage(getString("message")); //$NON-NLS-1$

		setPageComplete(true);
	}

	void optionButtonSelected() {
		if( this.xmlFileOptionButton.getSelection() ) {
			fileInfo.setFileMode(TeiidMetadataImportInfo.FILE_MODE_TEIID_XML_FILE);
		} else {
			fileInfo.setFileMode(TeiidMetadataImportInfo.FILE_MODE_TEIID_XML_URL);
		}
	}
	
	void synchronizeUI() {
		synchronizing = true;
		
		synchronizing = false;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			if( this.designerProperties != null ) {
				boolean value = DesignerPropertiesUtil.isImportXmlRemote(this.designerProperties);
				if( value ) {
					this.xmlFileOptionButton.setSelection(false);
					this.xmlUrlOptionButton.setSelection(true);
					optionButtonSelected();
				}
			}
		}
	}
	
    public void setDesignerProperties( Properties properties ) {
        this.designerProperties = properties;
    }
}
