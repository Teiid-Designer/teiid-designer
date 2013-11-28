/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.modelgenerator.wsdl.model.Fault;
import org.teiid.designer.modelgenerator.wsdl.model.Model;
import org.teiid.designer.modelgenerator.wsdl.model.ModelGenerationException;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;
import org.teiid.designer.modelgenerator.wsdl.model.WSDLElement;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.util.ModelGeneratorWsdlUiUtil;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.WSDLImportWizardManager;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.WsdlDefinitionPage;
import org.teiid.designer.query.proc.wsdl.model.IPort;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.common.widget.Label;


/**
 * UI panel for selection of a wsdl's operations
 * 
 * @since 8.0
 */
public class WsdlOperationsPanel implements FileUtils.Constants, CoreStringUtil.Constants,
	ModelGeneratorWsdlUiConstants, ModelGeneratorWsdlUiConstants.Images {
	
	Text defaultBindingText;
	Combo defaultServiceModeCombo;
	
	/** The checkbox table viewer */
	TableViewer operationsViewer;
	TableViewerColumn operationNameColumn;

	/** Buttons for tree selection */
	Button selectAllButton;
	Button deselectAllButton;

	/** Text area for display of selection details */
	Text detailsTextBox;

	Composite parentComposite;

	/** The import manager. */
	WSDLImportWizardManager importManager;

	WsdlDefinitionPage wsdlPage;

	/** The WSDL model representation */
	Model wsdlModel = null;

	private IStatus panelStatus;

	/**
	 * Create a new instance
	 * 
	 * @param parent
	 * @param wsdlPage
	 * @param theImportManager
	 */
	public WsdlOperationsPanel(Composite parent, WsdlDefinitionPage wsdlPage,
		WSDLImportWizardManager theImportManager) {
		super();
		this.parentComposite = parent;
		this.wsdlPage = wsdlPage;
		this.importManager = theImportManager;
		this.importManager.setSelectedOperations(new ArrayList());

		Composite portPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_HORIZONTAL);
		GridLayout layout = new GridLayout(4, false);
		portPanel.setLayout(layout);
				
		Label label = WidgetFactory.createLabel(portPanel, Messages.DefaultBinding);
		GridData gd = new GridData();
		gd.verticalAlignment=SWT.CENTER;
		label.setLayoutData(gd);
		label.setToolTipText(Messages.DefaultBinding_tooltip);
		
		defaultBindingText = new Text(portPanel, SWT.BORDER | SWT.SINGLE);
		defaultBindingText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		defaultBindingText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		gd = new GridData();
		gd.verticalAlignment=SWT.CENTER;
		gd.widthHint = 200;
		defaultBindingText.setLayoutData(gd);
		defaultBindingText.setToolTipText(Messages.DefaultBinding_tooltip);
		
		label = WidgetFactory.createLabel(portPanel, Messages.DefaultServiceMode);
		gd = new GridData();
		gd.verticalAlignment=SWT.CENTER;
		label.setLayoutData(gd);
		label.setToolTipText(Messages.DefaultServiceMode_tooltip);
		
		this.defaultServiceModeCombo = WidgetFactory.createCombo(portPanel, SWT.READ_ONLY, 
			GridData.FILL_HORIZONTAL, WSDLImportWizardManager.SERVICE_MODES, true);
		this.defaultServiceModeCombo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan=1;
		this.defaultServiceModeCombo.setToolTipText(Messages.DefaultServiceMode_tooltip);
		this.defaultServiceModeCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Need to sync the worker with the current profile
				handleDefaultServiceModeSelected();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		this.defaultServiceModeCombo.select(1);
		
		createPanel(parent);
	}


	void updateTreeSelectionDetails() {
		TableItem[] selections = this.operationsViewer.getTable().getSelection();
		if (selections != null && selections.length > 0) {
			if( selections.length == 1 ) {
    			TableItem selectedItem = selections[0];
    			updateSelectionDetailsArea(selectedItem.getData());
			} else {
				this.detailsTextBox.setText(Messages.MultipleOperationsSelected_msg);
			}
		} else {
			this.detailsTextBox.setText(Messages.NoOperationsSelected_msg);
		}
	}

	/**
	 * Create the panel in the given parent
	 * 
	 * @param theParent 
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 * @since 4.2
	 */
	public void createPanel(Composite theParent) {
		final int COLUMNS = 1;
		
		Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
		GridLayout layout = new GridLayout(COLUMNS, false);
		pnlMain.setLayout(layout);

		createCheckboxTableComposite(pnlMain, Messages.WsdlOperationsPage_checkboxTreeGroup_title);
	}

	/**
	 * create the checkbox tree Composite
	 * 
	 * @param parent
	 *            the parent composite
	 * @param title
	 *            the group title
	 */
	private void createCheckboxTableComposite(Composite parent, String title) {

		// --------------------------
		// Group for checkbox table
		// --------------------------
		Group group = WidgetFactory.createGroup(parent, title, GridData.FILL_BOTH, 1, 2);

		// ----------------------------
		// Select/DeSelect Buttons
		// ----------------------------
		Composite buttonComposite = WidgetFactory.createPanel(group, SWT.NONE, GridData.FILL_VERTICAL);
		GridLayout layout = new GridLayout(1, false);
		buttonComposite.setLayout(layout);
		this.selectAllButton = WidgetFactory.createButton(buttonComposite,
			Messages.WsdlOperationsPage_selectAllButton_text, GridData.FILL_HORIZONTAL);
		this.selectAllButton.setToolTipText(Messages.WsdlOperationsPage_selectAllButton_tipText);
		this.deselectAllButton = WidgetFactory.createButton(buttonComposite,
			Messages.WsdlOperationsPage_deselectAllButton_text, GridData.FILL_HORIZONTAL);
		this.deselectAllButton.setToolTipText(Messages.WsdlOperationsPage_deselectAllButton_tipText);

		this.selectAllButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAllNodesSelected(true);
				updateImportManager();
				setPageStatus();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		this.deselectAllButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAllNodesSelected(false);
				updateImportManager();
				setPageStatus();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		
		// ----------------------------
		// TableViewer
		// ----------------------------
		Table table = new Table(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK );
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayout(new TableLayout());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		table.setLayoutData(gd);
		
		this.operationsViewer = new TableViewer(table);


		this.operationsViewer.setContentProvider(new CheckboxTreeContentProvider());
		this.operationsViewer.setLabelProvider(new CheckboxTreeLabelProvider());

		// Check events can occur separate from selection events.
		// In this case move the selected node.
		// Also trigger selection of node in model.
		this.operationsViewer.getTable().addSelectionListener(
			new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					updateTreeSelectionDetails();
					if (e.detail == SWT.CHECK) {
						updateImportManager();
						setPageStatus();
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

		// create columns
		operationNameColumn = new TableViewerColumn(this.operationsViewer, SWT.LEFT);
		operationNameColumn.getColumn().setText(Messages.Operation);
		operationNameColumn.setLabelProvider(new OperationsColumnLabelProvider());
		operationNameColumn.getColumn().pack();
		
		
		CLabel theLabel = new CLabel(group, SWT.NONE);
		theLabel.setText(Messages.WsdlOperationsPage_detailsTextbox_title);
		GridData gridData = new GridData(SWT.NONE);
		gridData.horizontalSpan = 1;
		theLabel.setLayoutData(gridData);
		this.detailsTextBox = new Text(group, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL); //WidgetFactory.createTextBox(detailsComposite, SWT.NONE, GridData.FILL_BOTH);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 70;
		gridData.horizontalSpan = 2;
		this.detailsTextBox.setLayoutData(gridData);
		detailsTextBox.setEditable(false);
	}

	/**
	 * update method for selection details area
	 */
	private void updateSelectionDetailsArea(Object selectedObject) {
		StringBuffer sb = new StringBuffer();
		if (selectedObject instanceof Operation) {
			addOperationDetails((Operation) selectedObject, sb);
		}
		this.detailsTextBox.setText(sb.toString());
	}

	private void addOperationDetails(Operation operation, StringBuffer sb) {
		sb.append("Operation: " + operation.getName() + '\n'); //$NON-NLS-1$
		sb.append("\tbinding: \t\t" + operation.getBinding().getName() + '\n'); //$NON-NLS-1$
		sb.append("\tport: \t\t\t" + operation.getBinding().getPort().getName() + '\n'); //$NON-NLS-1$
		sb.append("\tservice: \t\t\t" + operation.getBinding().getPort().getService().getName() + '\n'); //$NON-NLS-1$
		sb.append("\tid: \t\t\t\t" + operation.getId() + '\n'); //$NON-NLS-1$
		sb.append("\tinput message: \t" + operation.getInputMessage().getName() + '\n'); //$NON-NLS-1$
		String outputMsg = "<none>"; //$NON-NLS-1$
		if (operation.getOutputMessage() != null) {
			outputMsg = operation.getOutputMessage().getName();
		}
		sb.append("\toutput message: \t" + outputMsg + '\n'); //$NON-NLS-1$
		Fault[] faults = operation.getFaults();
		sb.append("\tfault names: \t\t"); //$NON-NLS-1$
		if (faults == null || faults.length == 0) {
			sb.append("none"); //$NON-NLS-1$
		} else {
			for (int i = 0; i < faults.length; i++) {
				Fault theFault = faults[i];
				sb.append(theFault.getName());
				if (i != faults.length - 1) {
					sb.append(", "); //$NON-NLS-1$
				}
			}
		}
		// --------------------------------------------------------
		// Add Problem Messages
		// --------------------------------------------------------
		if (!operation.canModel()) {
			String[] errorMsgs = operation.getProblemMessages();
			for (int i = 0; i < errorMsgs.length; i++) {
				sb.append('\n');
				sb.append(errorMsgs[i]);
			}
		}
	}
	
	private void updateImportManager() {
		this.importManager.setSelectedOperations(getSelectedOperations());
	}
	
	private List<Operation> getSelectedOperations() {
		List<Operation> ops = new ArrayList<Operation>();
		for( TableItem item : operationsViewer.getTable().getItems()) {
			if( item.getChecked() ) {
				ops.add((Operation)item.getData());
			}
		}
		
		return ops;
	}
	
	/**
	 * Populate the UI based on the wsdl in the connection profile
	 */
	public void notifyWsdlChanged() {
        /*
         * Depending on the size of the WSDL selected in the connection profile,
         * this can take a little while so indicate the user should wait.
         */
        UiBusyIndicator.showWhile(parentComposite.getDisplay(), new Runnable() {
            @Override
            public void run() {

                panelStatus = Status.OK_STATUS;
                try {
                    wsdlModel = importManager.getWSDLModel();
                } catch (ModelGenerationException e) {
                    wsdlModel = null;
                    Status exStatus = new Status(IStatus.ERROR, PLUGIN_ID, 0,
                                                 Messages.WsdlOperationsPage_dialog_wsdlParseError_msg, e);
                    Shell shell = parentComposite.getShell();
                    ErrorDialog.openError(shell, null, Messages.WsdlOperationsPage_dialog_wsdlParseError_title, exStatus);
                    panelStatus = exStatus;
                    operationsViewer.getTable().clearAll();
                    operationsViewer.setInput(new Object());
                }

                // Set the default binding label
                Properties properties = importManager.getConnectionProfile().getBaseProperties();
                String binding = properties.getProperty(IWSProfileConstants.SOAP_BINDING);
                if (binding == null)
                    binding = IPort.SOAP11;

                WidgetUtil.setText(defaultBindingText, binding);

                // Populate the operations table
                String portName = properties.getProperty(IWSProfileConstants.END_POINT_NAME_PROP_ID);
                Operation[] operations = new Operation[0];
                if (wsdlModel != null && portName != null) {
                    operations = wsdlModel.getModelableOperations(portName);
                }
                operationsViewer.setInput(new OperationsContainer(operations));
                operationsViewer.refresh(true);

                importManager.setSelectedOperations(new ArrayList());
                setAllNodesSelected(true);
                updateImportManager();
            }
        });
    }
	
	private void handleDefaultServiceModeSelected() {
		this.importManager.setTranslatorDefaultServiceMode(this.defaultServiceModeCombo.getText());
	}

	private void setPageStatus() {
		if (panelStatus.isOK()) {
			panelStatus = Status.OK_STATUS;
		}
		this.wsdlPage.setPageStatus();
	}

	/**
	 * @return the status of the panel
	 */
	public IStatus getStatus() {
		return panelStatus;
	}

	private void setAllNodesSelected(boolean bSelected) {
		if( bSelected ) {
			for( TableItem item : operationsViewer.getTable().getItems()) {
				item.setChecked(true);
			}
		} else {
			for( TableItem item : operationsViewer.getTable().getItems()) {
				item.setChecked(false);
			}
		}
		
		this.operationsViewer.refresh();
	}

	class CheckboxTreeLabelProvider extends LabelProvider {
		private final Image OPERATION_ICON_IMG = ModelGeneratorWsdlUiUtil.getImage(OPERATION_ICON);

		final WorkbenchLabelProvider workbenchProvider = new WorkbenchLabelProvider();

		@Override
		public Image getImage(final Object node) {
			if (node instanceof Operation) {
				return OPERATION_ICON_IMG;
			}
			return null;
		}

		@Override
		public String getText(final Object node) {
			if (node instanceof Model) {
				return "theModel"; //$NON-NLS-1$
			} else if (node instanceof WSDLElement) {
				return ((WSDLElement) node).getName();
			}
			return "unknownElement"; //$NON-NLS-1$
		}
	}

	class CheckboxTreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getChildren(final Object node) {
			if (wsdlModel != null) {
				if( node instanceof OperationsContainer ) {
					return ((OperationsContainer)node).getOperations();
				}
			}
			return EMPTY_STRING_ARRAY;
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			if( inputElement instanceof OperationsContainer ) {
				return ((OperationsContainer)inputElement).getOperations();
			}
			return new Object[0];
		}

		@Override
		public Object getParent(final Object node) {
			return null;
		}

		@Override
		public boolean hasChildren(final Object node) {
			if (wsdlModel != null) {
				if (node instanceof OperationsContainer) {
					return ((OperationsContainer)node).getOperations().length > 0;
				}
			}
			return false;
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
	}
	
	class OperationsColumnLabelProvider extends ColumnLabelProvider {
		private final Image PORT_ICON_IMG = ModelGeneratorWsdlUiUtil.getImage(PORT_ICON);
		
		public OperationsColumnLabelProvider() {
			super();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			// Element should be a "File"
			if( element instanceof Operation) {
				Operation op = ((Operation) element);
				StringBuilder sb = new StringBuilder(op.getName());
				//sb.append(" < Binding : ").append(op.getBinding().getName()).append(" < Service : ").append(op.getBinding().getPort().getService().getName());
				return sb.toString();
			}
			return EMPTY_STRING;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			return ""; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			return PORT_ICON_IMG;
		}

	}
	
	class OperationsContainer {
		Operation[] operations;
		public OperationsContainer(Operation[] ops) {
			super();
			operations = ops;
		}
		
		public Operation[] getOperations() {
			return this.operations;
		}
	}
}