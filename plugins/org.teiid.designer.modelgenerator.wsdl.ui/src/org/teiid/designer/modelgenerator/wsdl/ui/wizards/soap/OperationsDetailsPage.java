/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.provider.XSDSemanticItemProviderAdapterFactory;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.util.ModelGeneratorWsdlUiUtil;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.WSDLImportWizardManager;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.SchemaTreeModel.SchemaNode;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels.ColumnsInfoPanel;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels.ElementsInfoPanel;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels.RequestSchemaContentsGroup;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels.ResponseSchemaContentsGroup;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels.WrapperProcedurePanel;
import org.teiid.designer.query.proc.wsdl.IWsdlConstants.ProcedureType;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlTextViewer;
import org.teiid.designer.ui.common.graphics.ColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

/**
 * @since 8.0
 */
public class OperationsDetailsPage extends AbstractWizardPage implements
	IChangeListener, ModelGeneratorWsdlUiConstants {

	/** <code>IDialogSetting</code>s key for saved dialog height. */
	private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog width. */
	private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog X position. */
	private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog Y position. */
	private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

	/** The import manager. */
	WSDLImportWizardManager importManager;

	// ========== UI COMPONENTS =========================

	/** The Operations Combo Selector */
	Combo operationsCombo;

	private XSDSemanticItemProviderAdapterFactory semanticAdapterFactory;
	private AdapterFactoryLabelProvider schemaLabelProvider;
	private SchemaTreeContentProvider schemaContentProvider;

	/** This keeps track of the root object of the model. */
	protected XSDSchema xsdSchema;
	// protected XSD xsdSchema1;

	TabFolder tabFolder;

	TabItem requestTab;
	Text requestProcedureNameText;
	TextViewer requestSqlTextViewer;
	IDocument requestSqlDocument;
	
	RequestSchemaContentsGroup requestBodySchemaContentsGroup;
	ElementsInfoPanel requestBodyElementsInfoPanel;
	RequestSchemaContentsGroup requestHeaderSchemaContentsGroup;
	ElementsInfoPanel requestHeaderElementsInfoPanel;
	TabItem requestHeaderTab;
	Composite requestHeaderStackPanel;
	StackLayout requestHeaderStackLayout;
	Composite disabledRequestHeaderPanel;
	SashForm requestHeaderSplitter;

	TabItem responseTab;
	Text responseProcedureNameText;
	TextViewer responseSqlTextViewer;
	IDocument responseSqlDocument;
	
	ResponseSchemaContentsGroup responseBodySchemaContentsGroup;
	ColumnsInfoPanel responseBodyColumnsInfoPanel;
	ResponseSchemaContentsGroup responseHeaderSchemaContentsGroup;
	ColumnsInfoPanel responseHeaderColumnsInfoPanel;
	TabItem responseHeaderTab;
	Composite responseHeaderStackPanel;
	StackLayout responseHeaderStackLayout;
	Composite disabledResponseHeaderPanel;
	SashForm responseHeaderSplitter;

	TabFolder wrapperTab;
	WrapperProcedurePanel wrapperPanel;
	Button overwriteExistingCB;

	private ProcedureGenerator procedureGenerator;
	
	ImportWsdlSchemaHandler schemaHandler;

	// ==================================================
	public OperationsDetailsPage(WSDLImportWizardManager theImportManager) {
		super(OperationsDetailsPage.class.getSimpleName(), Messages.ProcedureDefinition);
		this.importManager = theImportManager;
		this.importManager.setSelectedOperations(new ArrayList());
		setImageDescriptor(ModelGeneratorWsdlUiUtil.getImageDescriptor(Images.NEW_MODEL_BANNER));

		semanticAdapterFactory = new XSDSemanticItemProviderAdapterFactory();
		schemaLabelProvider = new SchemaTreeLabelProvider(semanticAdapterFactory);
		schemaContentProvider = new SchemaTreeContentProvider(semanticAdapterFactory);
		schemaHandler = new ImportWsdlSchemaHandler(theImportManager, this);
		this.importManager.addChangeListener(this);
	}

	public ProcedureGenerator getProcedureGenerator() {
		return this.procedureGenerator;
	}

	private void notifyOperationChanged(Operation operation) {
		this.procedureGenerator = importManager.getProcedureGenerator(operation);

		this.wrapperPanel.notifyOperationChanged(operation);

		this.requestProcedureNameText.setText(this.procedureGenerator.getRequestProcedureName());
		this.responseProcedureNameText.setText(this.procedureGenerator.getResponseProcedureName());

		// Now update the column info panels
		this.requestBodyElementsInfoPanel.setProcedureInfo(this.procedureGenerator.getRequestInfo());
		this.responseBodyColumnsInfoPanel.setProcedureInfo(this.procedureGenerator.getResponseInfo());

		this.requestHeaderElementsInfoPanel.setProcedureInfo(this.procedureGenerator.getRequestInfo());
		this.responseHeaderColumnsInfoPanel.setProcedureInfo(this.procedureGenerator.getResponseInfo());
		
		this.overwriteExistingCB.setSelection(this.procedureGenerator.doOverwriteExistingProcedures());
		this.overwriteExistingCB.setEnabled(this.importManager.viewModelExists());

		updateSqlText(ProcedureType.BOTH);
		updateSchemaTree(ProcedureType.BOTH);
		
		updateStatus();
	}

	public void notifyColumnDataChanged() {
		if (!responseBodyColumnsInfoPanel.getRootPathText().getText().equals("")) { //$NON-NLS-1$
            this.schemaHandler.getResponseSchemaTreeModel().setRootPath(
                    responseBodyColumnsInfoPanel.getRootPathText().getText());
			this.getProcedureGenerator().getResponseInfo().setRootPath(responseBodyColumnsInfoPanel.getRootPathText().getText());
		}
		this.requestBodyElementsInfoPanel.refresh();
		this.requestHeaderElementsInfoPanel.refresh();
		this.responseBodyColumnsInfoPanel.refresh();
		this.responseHeaderColumnsInfoPanel.refresh();
		
		updateSqlText(ProcedureType.BOTH);
		this.wrapperPanel.notifyOperationChanged(this.getProcedureGenerator().getOperation());

		updateStatus();
	}
	
	public void notifyRootTextColumnDataChanged() {
        this.schemaHandler.getResponseSchemaTreeModel().setRootPath(
                responseBodyColumnsInfoPanel.getRootPathText().getText());
	
		updateStatus();
	}

	public void updateStatus() {
		this.importManager.notifyChanged();
//		setPageStatus();
	}

	public WSDLImportWizardManager getImportManager() {
		return this.importManager;
	}
	
	public IContentProvider getSchemaContentProvider() {
		return this.schemaContentProvider;
	}
	
	public ILabelProvider getSchemaLabelProvider() {
		return this.schemaLabelProvider;
	}
	
	public ImportWsdlSchemaHandler getSchemaHandler() {
		return this.schemaHandler;
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 * @since 4.2
	 */
	@Override
	public void createControl(Composite theParent) {
		Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE,
				GridData.FILL_BOTH);
		GridLayout layout = new GridLayout(2, false);
		pnlMain.setLayout(layout);
		setControl(pnlMain);

		createOperationsSelectionPanel(pnlMain);

		createTabbedDetailsPanel(pnlMain);
	}

	@SuppressWarnings("unused")
	private void createOperationsSelectionPanel(Composite parent) {
		Group operationsGroup = WidgetFactory.createGroup(parent, Messages.Operations, GridData.FILL_BOTH, 2, 2);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		operationsGroup.setLayoutData(gd);

		ACTION_COMBO: {
			operationsCombo = new Combo(operationsGroup, SWT.NONE | SWT.READ_ONLY);
			operationsCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			gd.horizontalSpan = 2;
			operationsCombo.setLayoutData(gd);

			operationsCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent ev) {
					selectComboItem(operationsCombo.getSelectionIndex());
				}
			});
		}

		overwriteExistingCB = new Button(operationsGroup, SWT.CHECK);
		overwriteExistingCB.setText(Messages.OverwriteExistingProcedures);
		gd.horizontalSpan = 2;
		overwriteExistingCB.setLayoutData(gd);
		overwriteExistingCB.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleOverwriteSelected();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		this.overwriteExistingCB.setEnabled(this.importManager.viewModelExists());
		
	}

	private List<String> getOperationsNameList() {
		List<String> nameList = new ArrayList<String>();
		for (Operation op : this.importManager.getSelectedOperations()) {
			nameList.add(op.getName());
		}
		return nameList;
	}

	private void createTabbedDetailsPanel(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		createRequestTab(tabFolder);
		createResponseTab(tabFolder);
		createWrapperTab(tabFolder);
	}

	private void createRequestTab(TabFolder tabFolder) {
		Composite panel = WidgetFactory.createPanel(tabFolder);
		this.requestTab = new TabItem(tabFolder, SWT.NONE);
		this.requestTab.setControl(panel);
		this.requestTab.setText(Messages.Request);

		panel.setLayout(new GridLayout(2, false));

		Composite namePanel = WidgetFactory.createPanel(panel);
		namePanel.setLayout(new GridLayout(2, false));
		GridData namePanelGD = new GridData(GridData.FILL_HORIZONTAL);
		namePanelGD.horizontalSpan = 2;
		namePanel.setLayoutData(namePanelGD);

		Label procedureNameLabel = new Label(namePanel, SWT.NONE);
		procedureNameLabel.setText(Messages.GeneratedProcedureName);

		requestProcedureNameText = new Text(namePanel, SWT.BORDER | SWT.SINGLE);
		WidgetUtil.colorizeWidget(requestProcedureNameText, WidgetUtil.TEXT_COLOR_BLUE, true);
		requestProcedureNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		requestProcedureNameText.setEditable(false);

		createTabbedRequestPanel(panel);

		createRequestSqlGroup(panel);
	}

	private void createRequestBodySplitter(Composite parent) {
		SashForm splitter = new SashForm(parent, SWT.HORIZONTAL);
		GridData gid = new GridData();
		gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
		gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
		splitter.setLayoutData(gid);

		requestBodySchemaContentsGroup = new RequestSchemaContentsGroup(splitter, ProcedureInfo.TYPE_BODY, this);
		requestBodyElementsInfoPanel = new ElementsInfoPanel(splitter, SWT.NONE,ProcedureInfo.TYPE_BODY, this);
		requestBodySchemaContentsGroup.setElementsInfoPanel(requestBodyElementsInfoPanel);

		splitter.setWeights(new int[] { 60, 40 });
	}
	
	private void createTabbedRequestPanel(Composite parent) {
		TabFolder requestTabFolder = new TabFolder(parent, SWT.LEFT | SWT.BORDER);
		requestTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		createRequestBodyTab(requestTabFolder);
		createRequestHeaderTab(requestTabFolder);
	}
	
	private void createRequestBodyTab(TabFolder tabFolder) {
		Composite panel = WidgetFactory.createPanel(tabFolder);
		TabItem requestBodyTab = new TabItem(tabFolder, SWT.NONE);
		requestBodyTab.setControl(panel);
		requestBodyTab.setText(Messages.Body_upper_case);
		
		createRequestBodySplitter(panel);
	}
	
	private void createRequestHeaderTab(TabFolder tabFolder) {
		Composite panel = WidgetFactory.createPanel(tabFolder);
		requestHeaderTab = new TabItem(tabFolder, SWT.NONE);
		requestHeaderTab.setControl(panel);
		requestHeaderTab.setText(Messages.Header_upper_case);

    	requestHeaderStackPanel = new Composite(panel, SWT.NONE | SWT.FILL);
    	requestHeaderStackLayout = new StackLayout();
    	requestHeaderStackLayout.marginWidth = 0;
    	requestHeaderStackLayout.marginHeight = 0;
    	requestHeaderStackPanel.setLayout(requestHeaderStackLayout);
    	requestHeaderStackPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createRequestHeaderSplitter(requestHeaderStackPanel);
		createRequestHeaderDisabledPanel(requestHeaderStackPanel);
		
		this.requestHeaderStackLayout.topControl = requestHeaderStackPanel;
	}
	
	private void createRequestHeaderDisabledPanel(Composite parent) {
		disabledRequestHeaderPanel = WidgetFactory.createPanel(parent, SWT.NONE);
		disabledRequestHeaderPanel.setLayout(new GridLayout(1, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		disabledRequestHeaderPanel.setLayoutData(gd);
		Label label = new Label(disabledRequestHeaderPanel, SWT.NONE);
		label.setText(Messages.NoHeaderMessage);
	}
	
	private void createRequestHeaderSplitter(Composite parent) {
		requestHeaderSplitter = new SashForm(parent, SWT.HORIZONTAL);
		GridData gid = new GridData();
		gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
		gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
		requestHeaderSplitter.setLayoutData(gid);

		requestHeaderSchemaContentsGroup = new RequestSchemaContentsGroup(requestHeaderSplitter, ProcedureInfo.TYPE_HEADER, this);
		requestHeaderElementsInfoPanel = new ElementsInfoPanel(requestHeaderSplitter, SWT.NONE,ProcedureInfo.TYPE_HEADER, this);
		requestHeaderSchemaContentsGroup.setElementsInfoPanel(requestHeaderElementsInfoPanel);

		requestHeaderSplitter.setWeights(new int[] { 60, 40 });
	}

	private void createRequestSqlGroup(Composite parent) {
		Group group = WidgetFactory.createGroup(parent,Messages.GeneratedSQLStatement, SWT.NONE, 2);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		ColorManager colorManager = new ColorManager();
		int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

		requestSqlTextViewer = new SqlTextViewer(group, new VerticalRuler(0), styles, colorManager);
		requestSqlDocument = new Document();
		requestSqlTextViewer.setInput(requestSqlDocument);
		requestSqlTextViewer.setEditable(false);
		WidgetUtil.colorizeWidget(requestSqlTextViewer.getTextWidget(), WidgetUtil.TEXT_COLOR_DEFAULT, true);
		requestSqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
		requestSqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createResponseTab(TabFolder tabFolder) {
		Composite panel = WidgetFactory.createPanel(tabFolder);
		this.responseTab = new TabItem(tabFolder, SWT.NONE);
		this.responseTab.setControl(panel);
		this.responseTab.setText(Messages.Response);

		panel.setLayout(new GridLayout(2, false));

		Composite namePanel = WidgetFactory.createPanel(panel);
		namePanel.setLayout(new GridLayout(2, false));
		GridData namePanelGD = new GridData(GridData.FILL_HORIZONTAL);
		namePanelGD.horizontalSpan = 2;
		namePanel.setLayoutData(namePanelGD);

		Label procedureNameLabel = new Label(namePanel, SWT.NONE);
		procedureNameLabel.setText(Messages.GeneratedProcedureName);

		responseProcedureNameText = new Text(namePanel, SWT.BORDER | SWT.SINGLE);
		WidgetUtil.colorizeWidget(responseProcedureNameText, WidgetUtil.TEXT_COLOR_BLUE, true);
		responseProcedureNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		responseProcedureNameText.setEditable(false);

		createTabbedReponsePanel(panel);

		createResponseSqlGroup(panel);
	}

	private void createResponseBodySplitter(Composite parent) {
		SashForm splitter = new SashForm(parent, SWT.HORIZONTAL);
		GridData gid = new GridData();
		gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
		gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
		splitter.setLayoutData(gid);

		responseBodySchemaContentsGroup = new ResponseSchemaContentsGroup(splitter, ProcedureInfo.TYPE_BODY, this);
		responseBodyColumnsInfoPanel = new ColumnsInfoPanel(splitter, SWT.NONE, ProcedureInfo.TYPE_BODY, this);
		responseBodySchemaContentsGroup.setColumnsInfoPanel(responseBodyColumnsInfoPanel);

		splitter.setWeights(new int[] { 40, 60 });
	}
	
	private void createResponseHeaderSplitter(Composite parent) {
		responseHeaderSplitter = new SashForm(parent, SWT.HORIZONTAL);
		GridData gid = new GridData();
		gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
		gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
		responseHeaderSplitter.setLayoutData(gid);

		responseHeaderSchemaContentsGroup = new ResponseSchemaContentsGroup(responseHeaderSplitter, ProcedureInfo.TYPE_HEADER, this);
		responseHeaderColumnsInfoPanel = new ColumnsInfoPanel(responseHeaderSplitter, SWT.NONE, ProcedureInfo.TYPE_HEADER, this);
		responseHeaderSchemaContentsGroup.setColumnsInfoPanel(responseHeaderColumnsInfoPanel);

		responseHeaderSplitter.setWeights(new int[] { 40, 60 });
	}
	
	
	private void createTabbedReponsePanel(Composite parent) {
		TabFolder responseTabFolder = new TabFolder(parent, SWT.LEFT | SWT.BORDER);
		responseTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		createResponseBodyTab(responseTabFolder);
		createResponseHeaderTab(responseTabFolder);
	}
	
	private void createResponseBodyTab(TabFolder tabFolder) {
		Composite panel = WidgetFactory.createPanel(tabFolder);
		TabItem requestBodyTab = new TabItem(tabFolder, SWT.NONE);
		requestBodyTab.setControl(panel);
		requestBodyTab.setText(Messages.Body_upper_case);
		
		createResponseBodySplitter(panel);
	}
	
	private void createResponseHeaderTab(TabFolder tabFolder) {
		Composite panel = WidgetFactory.createPanel(tabFolder);
		responseHeaderTab = new TabItem(tabFolder, SWT.NONE);
		responseHeaderTab.setControl(panel);
		responseHeaderTab.setText(Messages.Header_upper_case);

    	responseHeaderStackPanel = new Composite(panel, SWT.NONE | SWT.FILL);
    	responseHeaderStackLayout = new StackLayout();
    	responseHeaderStackLayout.marginWidth = 0;
    	responseHeaderStackLayout.marginHeight = 0;
    	responseHeaderStackPanel.setLayout(responseHeaderStackLayout);
    	responseHeaderStackPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createResponseHeaderSplitter(responseHeaderStackPanel);
		createResponseHeaderDisabledPanel(responseHeaderStackPanel);
		
		this.responseHeaderStackLayout.topControl = responseHeaderStackPanel;
	}

	private void createResponseHeaderDisabledPanel(Composite parent) {
		disabledResponseHeaderPanel = WidgetFactory.createPanel(parent, SWT.NONE);
		disabledResponseHeaderPanel.setLayout(new GridLayout(1, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		disabledResponseHeaderPanel.setLayoutData(gd);
		Label label = new Label(disabledResponseHeaderPanel, SWT.NONE);
		label.setText(Messages.NoHeaderMessage);
	}
	
	private void createResponseSqlGroup(Composite parent) {
		Group group = WidgetFactory.createGroup(parent, Messages.GeneratedSQLStatement, SWT.NONE, 2);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		ColorManager colorManager = new ColorManager();
		int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP
				| SWT.FULL_SELECTION;

		responseSqlTextViewer = new SqlTextViewer(group, new VerticalRuler(0),
				styles, colorManager);
		responseSqlDocument = new Document();
		responseSqlTextViewer.setInput(responseSqlDocument);
		responseSqlTextViewer.setEditable(false);
		responseSqlTextViewer.getTextWidget().setBackground(
				Display.getCurrent().getSystemColor(
						SWT.COLOR_WIDGET_LIGHT_SHADOW));
		responseSqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
		responseSqlTextViewer.getControl().setLayoutData(
				new GridData(GridData.FILL_BOTH));
	}

	private void createWrapperTab(TabFolder tabFolder) {
		Composite panel = WidgetFactory.createPanel(tabFolder);
		this.responseTab = new TabItem(tabFolder, SWT.NONE);
		this.responseTab.setControl(panel);
		this.responseTab.setText(Messages.WrapperProcedure);

		panel.setLayout(new GridLayout(1, false));

		wrapperPanel = new WrapperProcedurePanel(panel, this);
	}

	private void selectComboItem(int selectionIndex) {
		if (selectionIndex >= 0) {
			operationsCombo.select(selectionIndex);
			String operationName = operationsCombo.getItem(selectionIndex);

			for (Operation op : this.importManager.getSelectedOperations()) {
				if (op.getName().equalsIgnoreCase(operationName)) {
					notifyOperationChanged(op);
					break;
				}
			}
		}
	}
	
	private void handleOverwriteSelected() {
		// For the selected operation, set the procedure generator's value
		if( this.getProcedureGenerator() != null ) {
			this.getProcedureGenerator().setOverwriteExistingProcedures(this.overwriteExistingCB.getSelection());
			notifyOperationChanged(this.getProcedureGenerator().getOperation());
		}
	}

	void updateSqlText(ProcedureType type) {
		if (this.procedureGenerator != null) {
		    
		    switch (type) {
		        case REQUEST:
		            requestSqlTextViewer.getDocument().set(this.procedureGenerator.getRequestInfo().getSqlString(new Properties()));
		            break;
		        case RESPONSE:
		            responseSqlTextViewer.getDocument().set(this.procedureGenerator.getResponseInfo().getSqlString(new Properties()));
		            break;
		        case BOTH:
				requestSqlTextViewer.getDocument().set(this.procedureGenerator.getRequestInfo().getSqlString(new Properties()));
				responseSqlTextViewer.getDocument().set(this.procedureGenerator.getResponseInfo().getSqlString(new Properties()));
			}
		}
	}

	void updateSchemaTree(ProcedureType type) {
	    SchemaNodeWrapper nodeInput;
	    
		switch (type) {
		    case REQUEST:
		        nodeInput = new SchemaNodeWrapper(getSchemaForSelectedOperation(ProcedureType.REQUEST));
			    requestBodySchemaContentsGroup.setInput(nodeInput);
			    requestHeaderSchemaContentsGroup.setInput(nodeInput);
			    break;
		    case RESPONSE:
		        nodeInput = new SchemaNodeWrapper(getSchemaForSelectedOperation(ProcedureType.RESPONSE));
		        responseBodySchemaContentsGroup.setInput(nodeInput);
		        responseHeaderSchemaContentsGroup.setInput(nodeInput);
		        break;
		    case BOTH:
		        nodeInput = new SchemaNodeWrapper(getSchemaForSelectedOperation(ProcedureType.REQUEST));
		        requestBodySchemaContentsGroup.setInput(nodeInput);
		        requestHeaderSchemaContentsGroup.setInput(nodeInput);
		        nodeInput = new SchemaNodeWrapper(getSchemaForSelectedOperation(ProcedureType.RESPONSE));
		        responseBodySchemaContentsGroup.setInput(nodeInput);
		        responseHeaderSchemaContentsGroup.setInput(nodeInput);
		}
	}
	
	private List<SchemaNode> getSchemaForSelectedOperation(ProcedureType type) {
		return this.schemaHandler.getSchemaForSelectedOperation(type, this.procedureGenerator);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
	 * @since 4.2
	 */
	@Override
	public void dispose() {
		saveState();
	}

	/**
	 * Override to replace the NewModelWizard settings with the section devoted
	 * to the Web Service Model Wizard.
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
	 * @since 4.2
	 */
	@Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();

        if (settings != null) {
            // get the right section of the NewModelWizard settings
            IDialogSettings temp = settings.getSection(DIALOG_SETTINGS_SECTION);

            if (temp == null) {
                settings = settings.addNewSection(DIALOG_SETTINGS_SECTION);
            } else {
                settings = temp;
            }
        }

        return settings;
    }

	/**
	 * Persists dialog size and position.
	 * 
	 * @since 4.2
	 */
	private void saveState() {
		IDialogSettings settings = getDialogSettings();

		if (settings != null) {
			Shell shell = getContainer().getShell();

			if (shell != null) {
				Rectangle r = shell.getBounds();
				settings.put(DIALOG_X, r.x);
				settings.put(DIALOG_Y, r.y);
				settings.put(DIALOG_WIDTH, r.width);
				settings.put(DIALOG_HEIGHT, r.height);
			}
		}
	}

	/**
	 * Sets the wizard page status message.
	 * 
	 * @since 4.2
	 */
	void setPageStatus() {
		
		IStatus generatorStatus = this.importManager.getValidator().getProcedureStatus(this.procedureGenerator);
		if( generatorStatus == null ) {
			return;
		}
		if (generatorStatus.isOK() || generatorStatus.getSeverity() < IStatus.ERROR) {
			this.setErrorMessage(null);
			WizardUtil.setPageComplete(this);
			// NOW CHECK THE REMAINING OPERATIONS
			IStatus operationsStatus = this.importManager.getValidator().getOperationsStatus();
			String finalMessage = this.importManager.getValidator().getPrimaryMessage(operationsStatus);
			if( operationsStatus.getSeverity() > IStatus.WARNING ) {
				WizardUtil.setPageComplete(this, finalMessage, WizardUtil.getMessageSeverity(operationsStatus.getSeverity()));
				this.setErrorMessage(finalMessage);
				this.setPageComplete(false);
			} else if( operationsStatus.getSeverity() == IStatus.WARNING ) {
				this.setMessage(finalMessage, WizardUtil.getMessageSeverity(IStatus.WARNING));
			}
		} else {
			String finalMessage = this.importManager.getValidator().getPrimaryMessage(generatorStatus);
			WizardUtil.setPageComplete(this, finalMessage, WizardUtil.getMessageSeverity(generatorStatus.getSeverity()));
			this.setErrorMessage(finalMessage);
			this.setPageComplete(false);
		}

		getContainer().updateButtons();
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (isVisible) {
			WidgetUtil.setComboItems(operationsCombo, getOperationsNameList(),
					null, true);

			selectComboItem(0);
		
    		this.wrapperPanel.setVisible();
    		this.overwriteExistingCB.setEnabled(this.importManager.viewModelExists());
    		
    		boolean includeHeader = this.importManager.isMessageServiceMode();
    		if( includeHeader ) {
    			this.requestHeaderStackLayout.topControl = requestHeaderSplitter;
    			this.responseHeaderStackLayout.topControl = responseHeaderSplitter;
    		} else {
    			this.requestHeaderStackLayout.topControl = disabledRequestHeaderPanel;
    			this.responseHeaderStackLayout.topControl = disabledResponseHeaderPanel;
    		}
    		this.requestHeaderStackPanel.layout();
    		this.responseHeaderStackPanel.layout();
    		this.requestHeaderTab.getControl().setEnabled(includeHeader);
    		this.responseHeaderTab.getControl().setEnabled(includeHeader);
		}
		setPageStatus();
	}
    
    public void updateDesignerProperties() {

    }
    
    
	/* (non-Javadoc)
	 * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
	 */
	@Override
	public void stateChanged(IChangeNotifier theSource) {
		setPageStatus();
	}

	Object[] getNodeChildren(Object element) {
		return new Object[0];
	}

	boolean getNodeHasChildren(Object element) {
		return false;
	}

	Image getNodeImage(Object element) {
		return null;
	}

	String getNodeName(Object element) {
		return "<name>"; //$NON-NLS-1$
	}

	Object getNodeParent(Object element) {
		return null;
	}

	public String createRequestColumn(int requestType) {
		if( requestType == ProcedureInfo.TYPE_BODY ) {
			return this.requestBodySchemaContentsGroup.createRequestColumn();
		}
        return this.requestHeaderSchemaContentsGroup.createRequestColumn();
	}

	public String createResponseColumn(int responseType) {
		if( responseType == ProcedureInfo.TYPE_BODY ) {
			return this.responseBodySchemaContentsGroup.createResponseColumn();
        }
        return this.responseHeaderSchemaContentsGroup.createResponseColumn();
	}

	class OperationsListProvider extends LabelProvider implements
			ITreeContentProvider {
		private final Image OPERATION_ICON_IMG = ModelGeneratorWsdlUiUtil
				.getImage(Images.OPERATION_ICON);

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getChildren(final Object node) {
			if (node instanceof ArrayList) {
				ArrayList theList = ((ArrayList) node);

				return theList.toArray();
			}
			return CoreStringUtil.Constants.EMPTY_STRING_ARRAY;
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object getParent(final Object node) {
			return null;
		}

		@Override
		public boolean hasChildren(final Object node) {
			return false;
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {
		}

		@Override
		public Image getImage(final Object node) {
			if (node instanceof Operation) {
				return OPERATION_ICON_IMG;
			}
			return null;
		}

		@Override
		public String getText(final Object node) {
			if (node instanceof Operation) {
				return ((Operation) node).getName();
			}
			return "unknownElement"; //$NON-NLS-1$
		}
	}
}
