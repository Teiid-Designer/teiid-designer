/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels.ColumnsInfoPanel;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels.ElementsInfoPanel;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.WSDLImportWizardManager;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlTextViewer;
import com.metamatrix.ui.graphics.ColorManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;
import com.metamatrix.ui.tree.AbstractTreeContentProvider;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDElementDeclarationImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.eclipse.xsd.provider.XSDSemanticItemProviderAdapterFactory;
import org.eclipse.xsd.util.XSDParser;

public class OperationsDetailsPage extends AbstractWizardPage implements
		ModelGeneratorWsdlUiConstants {

	/** <code>IDialogSetting</code>s key for saved dialog height. */
	private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog width. */
	private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog X position. */
	private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog Y position. */
	private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

	private static final int REQUEST = ProcedureInfo.REQUEST;
	private static final int RESPONSE = ProcedureInfo.RESPONSE;
	private static final int BOTH = 2;

	/** The import manager. */
	WSDLImportWizardManager importManager;

	// ========== UI COMPONENTS =========================

	/** The checkbox treeViewer */
	private TreeViewer treeViewer;
	private Tree tree;

	private XSDSemanticItemProviderAdapterFactory semanticAdapterFactory;

	/** This keeps track of the root object of the model. */
	protected XSDSchema xsdSchema;
	// protected XSD xsdSchema1;

	Button generateWrapperProcedure;

	TabFolder tabFolder;

	TabItem requestTab;
	Text selectedRequestOperationText;
	Text requestProcedureNameText;
	TreeViewer requestXmlTreeViewer;
	TextViewer requestSqlTextViewer;
	IDocument requestSqlDocument;
	Action requestCreateElementAction, requestSetRootPathAction;
	Button requestAddElementButton;
	ElementsInfoPanel requestElementsInfoPanel;

	TabItem responseTab;
	Text selectedResponseOperationText;
	Text responseProcedureNameText;
	TreeViewer responseXmlTreeViewer;
	TextViewer responseSqlTextViewer;
	IDocument responseSqlDocument;
	Action responseCreateElementAction, responseSetRootPathAction;
	Button responseAddElementButton;
	ColumnsInfoPanel responseElementsInfoPanel;

	private ProcedureGenerator procedureGenerator;

	// ==================================================
	public OperationsDetailsPage(WSDLImportWizardManager theImportManager) {
		super(OperationsDetailsPage.class.getSimpleName(),
				Messages.ProcedureDefinition);
		this.importManager = theImportManager;
		this.importManager.setSelectedOperations(new ArrayList());
		setImageDescriptor(ModelGeneratorWsdlUiUtil
				.getImageDescriptor(Images.NEW_MODEL_BANNER));
	}

	public ProcedureGenerator getProcedureGenerator() {
		return this.procedureGenerator;
	}

	private void notifyOperationChanged(Operation operation) {
		this.procedureGenerator = importManager
				.getProcedureGenerator(operation);

		this.generateWrapperProcedure.setSelection(this.procedureGenerator
				.doGenerateWrapperProcedure());

		if (this.selectedRequestOperationText != null) {
			this.selectedRequestOperationText.setText(this.procedureGenerator
					.getOperation().getName());
		}
		if (this.selectedResponseOperationText != null) {
			this.selectedResponseOperationText.setText(this.procedureGenerator
					.getOperation().getName());
		}

		this.requestProcedureNameText.setText(this.procedureGenerator
				.getRequestProcedureName());
		this.responseProcedureNameText.setText(this.procedureGenerator
				.getResponseProcedureName());

		// Now update the two column info panels
		this.requestElementsInfoPanel.setProcedureInfo(this.procedureGenerator
				.getRequestInfo());
		this.responseElementsInfoPanel.setProcedureInfo(this.procedureGenerator
				.getResponseInfo());

		updateSqlText(BOTH);
		updateSchemaTree(BOTH);
	}

	public void notifyColumnDataChanged() {
		this.requestElementsInfoPanel.refresh();
		this.responseElementsInfoPanel.refresh();
		updateSqlText(BOTH);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 * @since 4.2
	 */
	public void createControl(Composite theParent) {
		final int COLUMNS = 1;
		Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE,
				GridData.FILL_BOTH);
		GridLayout layout = new GridLayout(COLUMNS, false);
		pnlMain.setLayout(layout);
		setControl(pnlMain);

		SashForm splitter = new SashForm(pnlMain, SWT.HORIZONTAL);
		GridData gid = new GridData();
		gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
		gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
		splitter.setLayoutData(gid);

		createOperationsListPanel(splitter);

		createTabbedDetailsPanel(splitter);

		splitter.setWeights(new int[] { 30, 70 });

		restoreState();
	}

	private void createOperationsListPanel(Composite parent) {
		Composite panel = WidgetFactory.createPanel(parent);

		GridLayout layout = new GridLayout(1, false);
		panel.setLayout(layout);

		// --------------------------
		// Group for checkbox tree
		// --------------------------
		Group operationsGroup = WidgetFactory.createGroup(panel,
				Messages.Operations, GridData.FILL_BOTH, 1, 1);

		// ----------------------------
		// TreeViewer
		// ----------------------------
		this.treeViewer = WidgetFactory.createTreeViewer(operationsGroup,
				SWT.SINGLE, GridData.FILL_BOTH);

		this.tree = this.treeViewer.getTree();

		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		OperationsListProvider provider = new OperationsListProvider();
		this.treeViewer.setContentProvider(provider);
		this.treeViewer.setLabelProvider(provider);

		this.treeViewer.setInput(null);

		this.treeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						updateForTreeSelection();
					}
				});

		// --------------------------
		// Group for checkbox tree
		// --------------------------
		Group optionsGroup = WidgetFactory.createGroup(panel, Messages.Options,
				GridData.FILL_BOTH, 1, 2);

		this.generateWrapperProcedure = WidgetFactory.createCheckBox(
				optionsGroup, Messages.GenerateWrapperProcedure);
		this.generateWrapperProcedure
				.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						procedureGenerator
								.setGenerateWrapperProcedure(generateWrapperProcedure
										.getSelection());
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

	}

	private void updateForTreeSelection() {
		TreeSelection selection = (TreeSelection) this.treeViewer
				.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Operation operation = (Operation) selection.getFirstElement();
			notifyOperationChanged(operation);
		}
	}

	private void createTabbedDetailsPanel(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		createRequestTab(tabFolder);
		createResponseTab(tabFolder);
	}

	private void createRequestTab(TabFolder tabFolder) {
		Composite panel = WidgetFactory.createPanel(tabFolder);
		this.requestTab = new TabItem(tabFolder, SWT.NONE);
		this.requestTab.setControl(panel);
		this.requestTab.setText(Messages.Request);

		panel.setLayout(new GridLayout(2, false));

		Label selectedOperationLabel = new Label(panel, SWT.NONE);
		selectedOperationLabel.setText(Messages.SelectedOperation);

		selectedRequestOperationText = new Text(panel, SWT.BORDER | SWT.SINGLE);
		selectedRequestOperationText.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		selectedRequestOperationText.setForeground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_DARK_BLUE));
		selectedRequestOperationText.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		selectedRequestOperationText.setEditable(false);

		Label procedureNameLabel = new Label(panel, SWT.NONE);
		procedureNameLabel.setText(Messages.GeneratedProcedureName);

		requestProcedureNameText = new Text(panel, SWT.BORDER | SWT.SINGLE);
		requestProcedureNameText.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		requestProcedureNameText.setForeground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_DARK_BLUE));
		requestProcedureNameText.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		requestProcedureNameText.setEditable(false);

		createRequestSchemaContentsGroup(panel);
		createRequestElementsInfoGroup(panel);
		createRequestSqlGroup(panel);
	}

	private void createRequestSchemaContentsGroup(Composite parent) {
		Group schemaContentsGroup = WidgetFactory.createGroup(parent,
				"Schema Contents", SWT.NONE, 2, 4); //$NON-NLS-1$
		schemaContentsGroup.setLayout(new GridLayout(4, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 140;
		schemaContentsGroup.setLayoutData(gd);

		this.requestXmlTreeViewer = new TreeViewer(schemaContentsGroup);
		semanticAdapterFactory = new XSDSemanticItemProviderAdapterFactory();
		this.requestXmlTreeViewer
				.setContentProvider(new AdapterFactoryContentProvider(
						semanticAdapterFactory));
		this.requestXmlTreeViewer
				.setLabelProvider(new AdapterFactoryLabelProvider(
						semanticAdapterFactory));
		this.requestXmlTreeViewer.setAutoExpandLevel(2);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		this.requestXmlTreeViewer.getControl().setLayoutData(data);
		this.requestXmlTreeViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				// filter built-ins if needed:
				if (!(parentElement instanceof EObject)
						&& element instanceof XSDSimpleTypeDefinition) {
					// parent is not an EObject, and kid is a STD; need to
					// filter out built-ins.
					XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition) element;
					return std.getSchema() == xsdSchema;
				} // endif

				return true;
			}
		});
		this.requestXmlTreeViewer.setInput(null);

		// Add a Context Menu
		final MenuManager columnMenuManager = new MenuManager();
		this.requestXmlTreeViewer.getControl().setMenu(
				columnMenuManager.createContextMenu(parent));
		this.requestXmlTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					/**
					 * {@inheritDoc}
					 * 
					 * @see oblafond@redhat.comrg.eclipse.jface.viewers.
					 *      ISelectionChangedListener
					 *      #selectionChanged(org.eclipse
					 *      .jface.viewers.SelectionChangedEvent)
					 */
					@Override
					public void selectionChanged(
							final SelectionChangedEvent event) {
						columnMenuManager.removeAll();
						IStructuredSelection sel = (IStructuredSelection) requestXmlTreeViewer
								.getSelection();
						if (sel.size() == 1 && sel.getFirstElement() instanceof XSDParticleImpl) {
							requestAddElementButton.setEnabled(true);
							columnMenuManager.add(requestCreateElementAction);
							columnMenuManager.add(requestSetRootPathAction);
						} else {
							requestAddElementButton.setEnabled(false);
						}

					}
				});

		this.requestXmlTreeViewer.addTreeListener(new ITreeViewerListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) requestXmlTreeViewer
						.getSelection();
				requestAddElementButton.setEnabled(sel.size() == 1);
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) requestXmlTreeViewer
						.getSelection();
				requestAddElementButton.setEnabled(sel.size() == 1);
			}
		});

		this.requestCreateElementAction = new Action(Messages.AddAsNewElement) {
			@Override
			public void run() {
				createRequestColumn();
			}
		};

		this.requestSetRootPathAction = new Action(Messages.SetAsRootPath) {
			@Override
			public void run() {
				setRequestRootPath();
			}
		};

		requestAddElementButton = new Button(schemaContentsGroup, SWT.PUSH);
		requestAddElementButton.setText(Messages.AddSelectionAsNewElement);
		gd = new GridData();
		gd.horizontalSpan = 1;
		requestAddElementButton.setLayoutData(gd);
		requestAddElementButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) requestXmlTreeViewer
						.getSelection();
				Object obj = sel.getFirstElement();
				// TODO:
				if (obj instanceof XSDParticleImpl) {
					createRequestColumn();
				} else {
					String newName = "column_" + (getProcedureGenerator().getRequestInfo().getColumnInfoList().length + 1); //$NON-NLS-1$
					getProcedureGenerator().getRequestInfo().addColumn(newName, false,
					null, null, null);
				}
				 notifyColumnDataChanged();
			}

		});
		requestAddElementButton.setEnabled(false);
	}

	private void createRequestElementsInfoGroup(Composite parent) {
		requestElementsInfoPanel = new ElementsInfoPanel(parent, SWT.NONE,
				REQUEST, this);
	}

	private void createRequestSqlGroup(Composite parent) {
		Group group = WidgetFactory.createGroup(parent,
				Messages.GeneratedSQLStatement, SWT.NONE, 2);
		group.setLayout(new GridLayout(1, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		ColorManager colorManager = new ColorManager();
		int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP
				| SWT.FULL_SELECTION;

		requestSqlTextViewer = new SqlTextViewer(group, new VerticalRuler(0),
				styles, colorManager);
		requestSqlDocument = new Document();
		requestSqlTextViewer.setInput(requestSqlDocument);
		requestSqlTextViewer.setEditable(false);
		requestSqlTextViewer.getTextWidget().setBackground(
				Display.getCurrent().getSystemColor(
						SWT.COLOR_WIDGET_LIGHT_SHADOW));
		requestSqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
		requestSqlTextViewer.getControl().setLayoutData(
				new GridData(GridData.FILL_BOTH));
	}

	private void createResponseTab(TabFolder tabFolder) {
		Composite panel = WidgetFactory.createPanel(tabFolder);
		this.responseTab = new TabItem(tabFolder, SWT.NONE);
		this.responseTab.setControl(panel);
		this.responseTab.setText(Messages.Response);

		panel.setLayout(new GridLayout(2, false));

		Label selectedOperationLabel = new Label(panel, SWT.NONE);
		selectedOperationLabel.setText(Messages.SelectedOperation);

		selectedResponseOperationText = new Text(panel, SWT.BORDER | SWT.SINGLE);
		selectedResponseOperationText.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		selectedResponseOperationText.setForeground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_DARK_BLUE));
		selectedResponseOperationText.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		selectedResponseOperationText.setEditable(false);

		Label procedureNameLabel = new Label(panel, SWT.NONE);
		procedureNameLabel.setText(Messages.GeneratedProcedureName);

		responseProcedureNameText = new Text(panel, SWT.BORDER | SWT.SINGLE);
		responseProcedureNameText.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		responseProcedureNameText.setForeground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_DARK_BLUE));
		responseProcedureNameText.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		responseProcedureNameText.setEditable(false);

		createResponseSchemaContentsGroup(panel);
		createResponseColumnInfoGroup(panel);
		createResponseSqlGroup(panel);
	}

	private void createResponseSchemaContentsGroup(Composite parent) {
		Group schemaContentsGroup = WidgetFactory.createGroup(parent,
				"Schema Contents", SWT.NONE, 2, 4); //$NON-NLS-1$
		schemaContentsGroup.setLayout(new GridLayout(4, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 140;
		gd.horizontalSpan = 2;
		schemaContentsGroup.setLayoutData(gd);

		responseXmlTreeViewer = new TreeViewer(schemaContentsGroup);
		semanticAdapterFactory = new XSDSemanticItemProviderAdapterFactory();
		this.responseXmlTreeViewer
				.setContentProvider(new AdapterFactoryContentProvider(
						semanticAdapterFactory));
		this.responseXmlTreeViewer
				.setLabelProvider(new AdapterFactoryLabelProvider(
						semanticAdapterFactory));
		this.responseXmlTreeViewer.setAutoExpandLevel(2);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		this.responseXmlTreeViewer.getControl().setLayoutData(data);
		this.responseXmlTreeViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				// filter built-ins if needed:
				if (!(parentElement instanceof EObject)
						&& element instanceof XSDSimpleTypeDefinition) {
					// parent is not an EObject, and kid is a STD; need to
					// filter out built-ins.
					XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition) element;
					return std.getSchema() == xsdSchema;
				} // endif

				return true;
			}
		});
		this.responseXmlTreeViewer.setInput(null);

		// Add a Context Menu
		final MenuManager columnMenuManager = new MenuManager();
		this.responseXmlTreeViewer.getControl().setMenu(
				columnMenuManager.createContextMenu(parent));
		this.responseXmlTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					/**
					 * {@inheritDoc}
					 * 
					 * @see oblafond@redhat.comrg.eclipse.jface.viewers.
					 *      ISelectionChangedListener
					 *      #selectionChanged(org.eclipse
					 *      .jface.viewers.SelectionChangedEvent)
					 */
					@Override
					public void selectionChanged(
							final SelectionChangedEvent event) {
						columnMenuManager.removeAll();
						IStructuredSelection sel = (IStructuredSelection) responseXmlTreeViewer
								.getSelection();
						if (sel.size() == 1 && sel.getFirstElement() instanceof XSDParticleImpl) {
							responseAddElementButton.setEnabled(true);
							columnMenuManager.add(responseCreateElementAction);
							columnMenuManager.add(requestSetRootPathAction);
						} else {
							responseAddElementButton.setEnabled(false);
						}

					}
				});

		this.responseXmlTreeViewer.addTreeListener(new ITreeViewerListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) responseXmlTreeViewer
						.getSelection();
				responseAddElementButton.setEnabled(sel.size() == 1);
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) responseXmlTreeViewer
						.getSelection();
				responseAddElementButton.setEnabled(sel.size() == 1);
			}
		});

		this.responseCreateElementAction = new Action(Messages.AddAsNewElement) {
			@Override
			public void run() {
				createResponseColumn();
			}
		};

		this.responseSetRootPathAction = new Action(Messages.SetAsRootPath) {
			@Override
			public void run() {
				setRequestRootPath();
			}
		};

		responseAddElementButton = new Button(schemaContentsGroup, SWT.PUSH);
		responseAddElementButton.setText(Messages.AddSelectionAsNewElement);
		gd = new GridData();
		gd.horizontalSpan = 1;
		responseAddElementButton.setLayoutData(gd);
		responseAddElementButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) responseXmlTreeViewer
						.getSelection();
				Object obj = sel.getFirstElement();
				// TODO:
				if (obj instanceof XSDParticleImpl) {
					createResponseColumn();
				} else {
					String newName = "column_" + (getProcedureGenerator().getRequestInfo().getColumnInfoList().length + 1); //$NON-NLS-1$
					getProcedureGenerator().getRequestInfo().addColumn(newName, false,
					null, null, null);
				}
				 notifyColumnDataChanged();
			}

		});
		responseAddElementButton.setEnabled(false);
	}

	private void createResponseColumnInfoGroup(Composite parent) {
		responseElementsInfoPanel = new ColumnsInfoPanel(parent, SWT.NONE,
				RESPONSE, this);
	}

	private void createResponseSqlGroup(Composite parent) {
		Group group = WidgetFactory.createGroup(parent,
				Messages.GeneratedSQLStatement, SWT.NONE, 2);
		group.setLayout(new GridLayout(1, false));
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

	void updateSqlText(int type) {
		if (this.procedureGenerator != null) {
			if (type == REQUEST) {
				requestSqlTextViewer.getDocument().set(
						this.procedureGenerator.getRequestInfo().getSqlString(
								new Properties()));
			} else if (type == RESPONSE) {
				responseSqlTextViewer.getDocument().set(
						this.procedureGenerator.getResponseInfo().getSqlString(
								new Properties()));
			} else if (type == BOTH) {
				requestSqlTextViewer.getDocument().set(
						this.procedureGenerator.getRequestInfo().getSqlString(
								new Properties()));
				responseSqlTextViewer.getDocument().set(
						this.procedureGenerator.getResponseInfo().getSqlString(
								new Properties()));
			}
		}
	}

	void updateSchemaTree(int type) {
		if (type == REQUEST) {
			requestXmlTreeViewer.setInput(getSchemaForSelectedOperation(type));
		} else if (type == RESPONSE) {
			responseXmlTreeViewer.setInput(getSchemaForSelectedOperation(type));
		} else {
			requestXmlTreeViewer.setInput(getSchemaForSelectedOperation(REQUEST));
			responseXmlTreeViewer.setInput(getSchemaForSelectedOperation(RESPONSE));
		}
	}

	/**
	 * @return
	 */
	public XSDTypeDefinition getSchemaForSelectedOperation(final int type) {

		Model wsdlModel = null;
		XSDTypeDefinition elementDeclaration = null;

		try {
			wsdlModel = importManager.getWSDLModel();
		} catch (ModelGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XSDSchema[] schemas = wsdlModel.getSchemas();

		Operation selectedOperation = procedureGenerator.getOperation();
		String partElementName = null;

		if (type == REQUEST) {
			partElementName = selectedOperation.getInputMessage().getParts()[0].getTypeName();
			if (partElementName == null){
				partElementName = selectedOperation.getInputMessage().getParts()[0].getElementName();
			}
		}else{
			partElementName = selectedOperation.getOutputMessage().getParts()[0].getTypeName();
			if (partElementName == null){
				partElementName = selectedOperation.getOutputMessage().getParts()[0].getElementName();
			}
		}

		for (XSDSchema schema : schemas) {
			xsdSchema = schema;
			EList<XSDTypeDefinition> types = schema.getTypeDefinitions();
			for (XSDTypeDefinition xsdType : types) {
				String elementName = xsdType.getName();
				if (xsdType.getName().equals(partElementName)) {
					elementDeclaration = xsdType;
					break;
				}
			}
			
			if (elementDeclaration == null) {
				
			EList<XSDElementDeclaration> elements = schema
					.getElementDeclarations();
			for (XSDElementDeclaration element : elements) {
				String elementName = element.getName();
				if (element.getName().equals(partElementName)) {
					elementDeclaration = element.getTypeDefinition();
					break;
				}
			}
			}
			
		}

		return elementDeclaration;
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
	 * Restores dialog size and position of the last time wizard ran.
	 * 
	 * @since 4.2
	 */
	private void restoreState() {
		IDialogSettings settings = getDialogSettings();

		if (settings != null) {
			Shell shell = getContainer().getShell();

			if (shell != null) {
				try {
					int x = settings.getInt(DIALOG_X);
					int y = settings.getInt(DIALOG_Y);
					int width = settings.getInt(DIALOG_WIDTH);
					int height = settings.getInt(DIALOG_HEIGHT);
					shell.setBounds(x, y, width, height);
				} catch (NumberFormatException theException) {
					// getInt(String) throws exception if not found.
					// just means no settings exist yet.
				}
			}
		}
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
		// TODO:

		WizardUtil.setPageComplete(this);

		getContainer().updateButtons();
	}

	@Override
	public void setVisible(boolean isVisible) {
		if (isVisible) {
			this.treeViewer
					.setInput(this.importManager.getSelectedOperations());

			TreeItem firstItem = this.treeViewer.getTree().getItem(0);
			if (firstItem != null) {
				this.treeViewer.getTree().select(firstItem);

				updateForTreeSelection();
			}

			setPageStatus();
		}
		super.setVisible(isVisible);
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
		return "<name>";
	}

	Object getNodeParent(Object element) {
		return null;
	}

	private void createRequestColumn() {
		IStructuredSelection sel = (IStructuredSelection) requestXmlTreeViewer
				.getSelection();
		Object obj = sel.getFirstElement();
		if (obj instanceof XSDParticleImpl) {
		    String name = ((XSDElementDeclarationImpl)((XSDParticleImpl) obj).getContent()).getName();
		    String ns = ((XSDElementDeclarationImpl)((XSDParticleImpl) obj).getContent()).getTargetNamespace();
			this.procedureGenerator.getRequestInfo().addColumn(name, false, "String", null, ns);
			notifyColumnDataChanged();
		}
	}

	private void setRequestRootPath() {
		// TODO:
	}

	private void createResponseColumn() {
		IStructuredSelection sel = (IStructuredSelection) responseXmlTreeViewer
				.getSelection();
		Object obj = sel.getFirstElement();
		if (obj instanceof XSDParticleImpl) {
		    String name = ((XSDElementDeclarationImpl)((XSDParticleImpl) obj).getContent()).getName();
		    String ns = ((XSDElementDeclarationImpl)((XSDParticleImpl) obj).getContent()).getTargetNamespace();
			this.procedureGenerator.getResponseInfo().addColumn(name, false, "String", null, ns);
			notifyColumnDataChanged();
		}
	}

	private void setResponseRootPath() {
		// TODO:
	}

	class OperationsListProvider extends LabelProvider implements
			ITreeContentProvider {
		private final Image OPERATION_ICON_IMG = ModelGeneratorWsdlUiUtil
				.getImage(Images.OPERATION_ICON);

		public void dispose() {
		}

		public Object[] getChildren(final Object node) {
			if (node instanceof ArrayList) {
				ArrayList theList = ((ArrayList) node);

				return theList.toArray();
			}
			return CoreStringUtil.Constants.EMPTY_STRING_ARRAY;
		}

		public Object[] getElements(final Object inputElement) {
			return getChildren(inputElement);
		}

		public Object getParent(final Object node) {
			return null;
		}

		public boolean hasChildren(final Object node) {
			return false;
		}

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
