/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.dialogs;

import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.Messages;
import org.teiid.designer.advisor.ui.actions.AdvisorActionFactory;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.viewsupport.DesignerProperties;
import org.teiid.designer.vdb.ui.VdbUiConstants;


public class DefineSourceDialog  extends TitleAreaDialog implements IChangeListener, AdvisorUiConstants {

	private static final String PREFIX = I18nUtil.getPropertyPrefix(DefineSourceDialog.class);

	static String getString(String key) {
		return VdbUiConstants.Util.getString(PREFIX + key);
	}

	/*
			COMMAND_IDS.IMPORT_FLAT_FILE,
			COMMAND_IDS.IMPORT_XML_FILE,
			COMMAND_IDS.IMPORT_XML_FILE_URL,
			COMMAND_IDS.IMPORT_JDBC,
			COMMAND_IDS.IMPORT_SALESFORCE,
			COMMAND_IDS.IMPORT_WSDL_TO_SOURCE,
	 */
	private Button importFlatFileButton, importXmlFileButton, importXmlFileUrlButton, importJdbcButton, importSalesforceButton, importWsdlButton;
	private Button launchImporterButton;

	DesignerProperties designerProperties;

	/**
	 * @param parentShell 
	 * @since 5.5.3
	 */
	public DefineSourceDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * @param parentShell 
	 * @param properties 
	 * @since 5.5.3
	 */
	public DefineSourceDialog(Shell parentShell, Properties properties) {
		this(parentShell);
		this.designerProperties = (DesignerProperties) properties;
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 * @since 5.5.3
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Create Data Source Model"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		Control buttonBar = super.createButtonBar(parent);
		getButton(OK).setEnabled(false);

		// set the first selection so that initial validation state is set
		// (doing it here since the selection handler uses OK
		// button)

		return buttonBar;
	}
	
	@Override
	protected void okPressed() {
		launchSelectedImporter(false);
		super.okPressed();
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@SuppressWarnings("unused")
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite pnlOuter = (Composite) super.createDialogArea(parent);
		Composite panel = new Composite(pnlOuter, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		panel.setLayout(gridLayout);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// set title
		setTitle(Messages.DefineDataSourceModels);
		setMessage(Messages.DefineDataSourceModelsMessage);

		this.launchImporterButton = new Button(panel, SWT.NONE);//WidgetFactory.createRadioButton(panel, COMMAND_LABELS.IMPORT_JDBC , SWT.NONE, 1, true);
		this.launchImporterButton.setText(Messages.ClickToLaunchSelectedImporter);
        this.launchImporterButton.setToolTipText(COMMAND_DESC.IMPORT_JDBC);
        this.launchImporterButton.setLayoutData(new GridData());
        this.launchImporterButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	launchSelectedImporter(true);
            }
        });
		
		IMPORT_OPTIONS_WIDGETS: {
	        Group theGroup = WidgetFactory.createGroup(panel, Messages.ImportSourceOptions, SWT.NONE, 1, 1);
	    	theGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        
	        this.importJdbcButton = WidgetFactory.createRadioButton(theGroup, COMMAND_LABELS.IMPORT_JDBC , SWT.NONE, 1, true);
	        this.importJdbcButton.setToolTipText(COMMAND_DESC.IMPORT_JDBC);

	        this.importFlatFileButton = WidgetFactory.createRadioButton(theGroup, COMMAND_LABELS.IMPORT_FLAT_FILE , SWT.NONE, 1, false);
	        this.importFlatFileButton.setToolTipText(COMMAND_DESC.IMPORT_FLAT_FILE);

	        this.importXmlFileButton = WidgetFactory.createRadioButton(theGroup, COMMAND_LABELS.IMPORT_XML_FILE , SWT.NONE, 1, false);
	        this.importXmlFileButton.setToolTipText(COMMAND_DESC.IMPORT_XML_FILE);
	        
	        this.importXmlFileUrlButton = WidgetFactory.createRadioButton(theGroup, COMMAND_LABELS.IMPORT_XML_FILE_URL , SWT.NONE, 1, false);
	        this.importXmlFileUrlButton.setToolTipText(COMMAND_DESC.IMPORT_XML_FILE_URL);
	        
	        this.importWsdlButton = WidgetFactory.createRadioButton(theGroup, COMMAND_LABELS.IMPORT_WSDL_TO_SOURCE , SWT.FILL, 1, false);
	        this.importWsdlButton.setToolTipText(COMMAND_DESC.IMPORT_WSDL_TO_SOURCE);
	        
	        this.importSalesforceButton = WidgetFactory.createRadioButton(theGroup, COMMAND_LABELS.IMPORT_SALESFORCE , SWT.NONE, 1, false);
	        this.importSalesforceButton.setToolTipText(COMMAND_DESC.IMPORT_SALESFORCE);
		}

		return panel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		if (this.designerProperties != null) {


			updateState();
		}
		return control;
	}

	/**
	 * @see org.teiid.core.designer.event.IChangeListener#stateChanged(org.teiid.core.designer.event.IChangeNotifier)
	 * @since 5.5.3
	 */
	@Override
	public void stateChanged(IChangeNotifier theSource) {
		updateState();
	}

	private void updateState() {
		IStatus status = Status.OK_STATUS;

		if (status.getSeverity() == IStatus.ERROR) {
			getButton(OK).setEnabled(false);
			setErrorMessage(status.getMessage());
		} else {
			getButton(OK).setEnabled(true);
			setErrorMessage(null);
			setMessage(Messages.ClickOkToFinish);
		}
	}
	
	private void launchSelectedImporter(boolean sync) {
		if( this.importJdbcButton.getSelection() ) {
			AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_JDBC, designerProperties, sync);
		} else if( this.importFlatFileButton.getSelection()) {
			AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_FLAT_FILE, designerProperties, sync);
		} else if( this.importXmlFileButton.getSelection()) {
			AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_XML_FILE, designerProperties, sync);
		} else if( this.importXmlFileUrlButton.getSelection()) {
			AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_XML_FILE_URL, designerProperties, sync);
		} else if( this.importWsdlButton.getSelection()) {
			AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE, designerProperties, sync);
		} else if( this.importSalesforceButton.getSelection()) {
			AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_SALESFORCE, designerProperties, sync);
		}
	}
	
}