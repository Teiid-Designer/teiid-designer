/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.rest;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

public class TeiidRestImporterModelDefinitionPage extends AbstractWizardPage
		implements UiConstants, InternalUiConstants.Widgets,
		CoreStringUtil.Constants {

	// ===========================================================================================================================
	// Constants

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidRestImporterModelDefinitionPage.class);

	private static final String TITLE = getString("title"); //$NON-NLS-1$
	private static final String REST_TITLE = getString("restTitle"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}

	private TeiidMetadataImportInfo info;

	boolean creatingControl = false;

	boolean synchronizing = false;

	boolean processingChecks = false;

	Properties designerProperties;

	boolean controlComplete = false;
	boolean visibleCompleted = false;

	ModelsDefinitionSection modelsDefinitionSection;

	/**
	 * Constructor
	 * 
	 * @since 4.0
	 * @param info
	 *            the import info object
	 */
	public TeiidRestImporterModelDefinitionPage(TeiidMetadataImportInfo info) {
		this(null, info);
	}

	/**
	 * @since 4.0
	 */
	public TeiidRestImporterModelDefinitionPage(Object selection, TeiidMetadataImportInfo info) {
		super(TeiidRestImporterModelDefinitionPage.class.getSimpleName(), TITLE);
		// Set page incomplete initially
		this.info = info;
		setPageComplete(false);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(Images.IMPORT_TEIID_METADATA));
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

        modelsDefinitionSection = new ModelsDefinitionSection(this, this.info, mainPanel);

		scrolledComposite.sizeScrolledPanel();
		
		setControl(hostPanel);

		setMessage(INITIAL_MESSAGE);
		controlComplete = true;
	}

	public void setDesignerProperties(Properties properties) {
		this.designerProperties = properties;
	}




	void synchronizeUI() {
		this.modelsDefinitionSection.synchronizeUi();
	}


	private boolean validatePage() {

		// Validate the models section
		if (!this.modelsDefinitionSection.validatePage()) {
			return false;
		}

		setThisPageComplete(EMPTY_STRING, NONE);

		return true;
	}

	protected void setThisPageComplete(String message, int severity) {
		WizardUtil.setPageComplete(this, message, severity);
	}



	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			this.setTitle(REST_TITLE);

			synchronizeUI();
			validatePage();

			visibleCompleted = true;
		}
	}
}
