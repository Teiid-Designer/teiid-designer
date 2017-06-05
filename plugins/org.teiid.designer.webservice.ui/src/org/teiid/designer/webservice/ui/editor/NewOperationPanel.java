package org.teiid.designer.webservice.ui.editor;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.xsd.XSDElementDeclaration;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.eventsupport.IDialogStatusListener;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.SelectFromEObjectListDialog;
import org.teiid.designer.webservice.WebServicePlugin;
import org.teiid.designer.webservice.ui.WebServiceUiPlugin;
import org.teiid.designer.webservice.ui.util.WebServiceUiUtil;

public class NewOperationPanel extends Composite implements StringConstants {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(NewOperationPanel.class);

    protected IStatus currentStatus;

    protected IDialogStatusListener statusListener;

    boolean canFinish;

    Interface intFace;
    WebServiceOperation operation;
    Text modelNameText, nameText, inputMsgText, outputMsgText;
    StyledTextEditor descriptionTextEditor;
	TabItem generalPropertiesTab;
	
	private Button browseInputContentElementButton;
	private Text selectedInputContentText;
	private Button browseOutputContentElementButton;
	private Text selectedOutputContentText;
	private Button browseOutputXmlDocumentButton;
	private Text selectedOutputXmlDocumentText;
	
	private Button includeInputMessageButton;
	private Button includeOutputMessageButton;
    
    ModelResource modelResource;
    
    private static String getString( final String id ) {
        return WebServiceUiPlugin.UTIL.getString(I18N_PREFIX + id);
    }


	/**
	 * @param parent the parent panel
	 * @param dialogModel model containing reference object
	 * @param statusListener the dialog status listener
	 */
	public NewOperationPanel(Composite parent, int style, Interface intFace, WebServiceOperation operation, IDialogStatusListener statusListener) {
		super(parent, style);
		
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).hint(500, 400).applyTo(this);
		
		this.intFace = intFace;
		this.operation = operation;
		this.statusListener = statusListener;
		
		initialize();

		this.nameText.setFocus();
	}
	
	private void initialize() {
		
		// set the model resource
		modelResource = ModelUtilities.getModelResource(intFace);
		createContents();
	}

	private void createContents() {
		createNameGroup();
		

		TabFolder tabFolder = createTabFolder(this);
		
		createGeneralPropertiesTab(tabFolder);
		
		createDescriptionTab(tabFolder);
	}
	

    protected Composite createNameGroup() {
        Composite thePanel = WidgetFactory.createPanel(this, SWT.NONE, 1, 2, 2);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false);
        GridDataFactory.fillDefaults().grab(true, false).minSize(SWT.DEFAULT, 120).applyTo(thePanel);

        Label label = new Label(thePanel, SWT.NONE);
        label.setText(getString("model"));  //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).applyTo(label);

        this.modelNameText = new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.modelNameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        this.modelNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.modelNameText);
        modelNameText.setText(modelResource.getItemName());

        label = new Label(thePanel, SWT.NONE);
        label.setText(getString("operationName"));  //$NON-NLS-1$

        this.nameText = new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        if( this.operation.getName() != null ) {
        	this.nameText.setText(this.operation.getName());
        }
        this.nameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.nameText);
        this.nameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent event) {
                String value = nameText.getText();
                if (value == null) {
                    value = EMPTY_STRING;
                }

                operation.setName(value);
                handleInfoChanged();
            }
        });

        return thePanel;
    }

    protected void handleInfoChanged() {

        validate();
    }

    protected TabFolder createTabFolder(Composite parent) {
        TabFolder tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(tabFolder);
        return tabFolder;
    }

    protected Composite createDescriptionPanel(Composite parent) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        final Group descGroup = WidgetFactory.createGroup(thePanel, UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION), GridData.FILL_BOTH, 3);
        descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 40).minSize(SWT.DEFAULT, 30).applyTo(descriptionTextEditor.getTextWidget());
        descriptionTextEditor.setText(""); //$NON-NLS-1$
        descriptionTextEditor.getTextWidget().addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
            	operation.setDescription(descriptionTextEditor.getText());
            }
        });

        return thePanel;
    }

    private void createDescriptionTab(TabFolder folderParent) {
        Composite thePanel = createDescriptionPanel(folderParent);

        TabItem descriptionTab = new TabItem(folderParent, SWT.NONE);
        descriptionTab.setControl(thePanel);
        descriptionTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION));
    }
    

	private void createGeneralPropertiesTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.PROPERTIES));
	}
	
	private Composite createPropertiesPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);
		
		{ // Input Message
			Composite inputMessagePanel = WidgetFactory.createGroup(thePanel, getString("inputMessageGroup"), GridData.FILL_BOTH); //$NON-NLS-1$
	        GridDataFactory.fillDefaults().grab(true, false).applyTo(inputMessagePanel);
	        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).margins(3, 3).applyTo(inputMessagePanel);
	        
	        this.includeInputMessageButton = new Button(inputMessagePanel, SWT.CHECK);
	        this.includeInputMessageButton.setText("Include Input Message");
	        this.includeInputMessageButton.setSelection(operation.isIncludeInputMessage());
	        GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(this.includeInputMessageButton);
	        
	        this.includeInputMessageButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean include = includeInputMessageButton.getSelection();
					inputMsgText.setEnabled(include);
					selectedInputContentText.setEnabled(include);
					browseInputContentElementButton.setEnabled(include);
					operation.setIncludeInputMessage(include);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
	        
	        Label label = new Label(inputMessagePanel, SWT.NONE);
	        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
	        
	        this.inputMsgText =  new Text(inputMessagePanel, SWT.BORDER | SWT.SINGLE);
	        if( this.operation.getInputMessageName() != null ) {
	        	this.inputMsgText.setText(this.operation.getInputMessageName());
	        }
	        this.inputMsgText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	        GridDataFactory.fillDefaults().grab(true, false).span(2,1).applyTo(this.inputMsgText);
	        this.inputMsgText.addModifyListener(new ModifyListener() {
	    		@Override
				public void modifyText( final ModifyEvent event ) {
	    			operation.setInputMessageName(inputMsgText.getText());
	    			handleInfoChanged();
	    		}
	        });
	        
	        // TODO:  Content Via Element Selection (EObject Reference)
	        
			Label contentViaLabel = new Label(inputMessagePanel, SWT.NONE);
			contentViaLabel.setText(getString("schemaContentViaElementLabel"));  //$NON-NLS-1$
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	
			// textfield for named type
			this.selectedInputContentText = WidgetFactory.createTextField(inputMessagePanel, GridData.FILL_HORIZONTAL);
			this.selectedInputContentText.setToolTipText(getString("inputElementTooltip")); //$NON-NLS-1$
			this.selectedInputContentText.setEditable(false);
			this.selectedInputContentText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			this.selectedInputContentText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	
			// browse type button
			this.browseInputContentElementButton = WidgetFactory.createButton(inputMessagePanel, UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS)); 
			this.browseInputContentElementButton.setToolTipText(getString("browseForInputElementTooltip")); //$NON-NLS-1$
			this.browseInputContentElementButton.setEnabled(true);
			this.browseInputContentElementButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
			this.browseInputContentElementButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleBrowseWorkspaceInputContentViaElement();
				}
			});
		}
		
		{ // Output Message
			Composite outputMessagePanel = WidgetFactory.createGroup(thePanel,  getString("outputMessageGroup"), GridData.FILL_BOTH); //$NON-NLS-1$
	        GridDataFactory.fillDefaults().grab(true, false).applyTo(outputMessagePanel);
	        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).margins(3, 3).applyTo(outputMessagePanel);
	        
	        this.includeOutputMessageButton = new Button(outputMessagePanel, SWT.CHECK);
	        this.includeOutputMessageButton.setText("Include Output Message");
	        this.includeOutputMessageButton.setSelection(operation.isIncludeOutputMessage());
	        GridDataFactory.fillDefaults().grab(true, false).span(3,1).applyTo(this.includeOutputMessageButton);
	        
	        this.includeOutputMessageButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean include = includeOutputMessageButton.getSelection();
					outputMsgText.setEnabled(include);
					selectedOutputContentText.setEnabled(include);
					browseOutputContentElementButton.setEnabled(include);
					selectedOutputXmlDocumentText.setEnabled(include);
					browseOutputXmlDocumentButton.setEnabled(include);
					operation.setIncludeOutputMessage(include);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
	        
	        Label label = new Label(outputMessagePanel, SWT.NONE);
	        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
	        
	        this.outputMsgText =  new Text(outputMessagePanel, SWT.BORDER | SWT.SINGLE);
	        if( this.operation.getOutputMessageName() != null ) {
	        	this.outputMsgText.setText(this.operation.getOutputMessageName());
	        }
	        this.outputMsgText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	        GridDataFactory.fillDefaults().grab(true, false).span(2,1).applyTo(this.outputMsgText);
	        this.outputMsgText.addModifyListener(new ModifyListener() {
	    		@Override
				public void modifyText( final ModifyEvent event ) {
	    			operation.setOutputMessageName(outputMsgText.getText());
	    			handleInfoChanged();
	    		}
	        });
	        
	        // TODO:  Content Via Element Selection (EObject Reference)
			Label contentViaLabel = new Label(outputMessagePanel, SWT.NONE);
			contentViaLabel.setText("Schema Content Via Element");
			contentViaLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	
			// textfield for named type
			this.selectedOutputContentText = WidgetFactory.createTextField(outputMessagePanel, GridData.FILL_HORIZONTAL);
			this.selectedOutputContentText.setToolTipText(getString("outputElementTooltip")); //$NON-NLS-1$
			this.selectedOutputContentText.setEditable(false);
			this.selectedOutputContentText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			this.selectedOutputContentText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	
			// browse type button
			this.browseOutputContentElementButton = WidgetFactory.createButton(outputMessagePanel,  UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS)); 
			this.browseOutputContentElementButton.setToolTipText(getString("browseForOutputElementTooltip")); //$NON-NLS-1$
			this.browseOutputContentElementButton.setEnabled(true);
			this.browseOutputContentElementButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
			this.browseOutputContentElementButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleBrowseWorkspaceOutputContentViaElement();
				}
			});
	        
	        // TODO:  XML Document Selection (EObject Reference)
			Label xmlDocumentLabel = new Label(outputMessagePanel, SWT.NONE); //$NON-NLS-1$
			xmlDocumentLabel.setText(getString("outputXMLDocumentLabel")); //$NON-NLS-1$
			xmlDocumentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	
			// textfield for named type
			this.selectedOutputXmlDocumentText = WidgetFactory.createTextField(outputMessagePanel, GridData.FILL_HORIZONTAL);
			this.selectedOutputXmlDocumentText.setToolTipText(getString("outputXmlDocumentTooltip")); //$NON-NLS-1$
			this.selectedOutputXmlDocumentText.setEditable(false);
			this.selectedOutputXmlDocumentText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			this.selectedOutputXmlDocumentText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	
			// browse type button
			this.browseOutputXmlDocumentButton = WidgetFactory.createButton(outputMessagePanel,  UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS)); 
			this.browseOutputXmlDocumentButton.setToolTipText(getString("browseForXmlDocumentTooltip")); //$NON-NLS-1$
			this.browseOutputXmlDocumentButton.setEnabled(true);
			this.browseOutputXmlDocumentButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
			this.browseOutputXmlDocumentButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleBrowseWorkspaceOutputXmlDocument();
				}
			});
		}
		
		return thePanel;
	}

	private void handleBrowseWorkspaceInputContentViaElement() {
		Collection<EObject> elements = WebServiceUiUtil.getGlobalElementDeclarations(intFace);
		
		SelectFromEObjectListDialog sdDialog = createElementSelectionDialog(elements);
		
		sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] selections = sdDialog.getResult();
            // should be single selection
            EObject element = (EObject)selections[0];
            String name = ModelerCore.getModelEditor().getName(element);
            
            this.selectedInputContentText.setText(name);
            this.operation.setInputContentViaElement((XSDElementDeclaration)element);

            handleInfoChanged();
        }
		
	}
	
	private void handleBrowseWorkspaceOutputContentViaElement() {
		Collection<EObject> elements = WebServiceUiUtil.getGlobalElementDeclarations(intFace);
		
		SelectFromEObjectListDialog sdDialog = createElementSelectionDialog(elements);
		
		sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] selections = sdDialog.getResult();
            // should be single selection
            EObject element = (EObject)selections[0];
            String name = ModelerCore.getModelEditor().getName(element);
            
            this.selectedOutputContentText.setText(name);
            this.operation.setOutputContentViaElement((XSDElementDeclaration)element);

            handleInfoChanged();
        }
		
	}
	

	private void handleBrowseWorkspaceOutputXmlDocument() {
		Collection<EObject> elements = WebServiceUiUtil.getXmlDocumentsForProject(intFace);
		
		SelectFromEObjectListDialog sdDialog = createXmlDocumentSelectionDialog(elements);
		
		sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] selections = sdDialog.getResult();
            // should be single selection
            EObject element = (EObject)selections[0];
            String name = ModelerCore.getModelEditor().getName(element);
            
            this.selectedOutputXmlDocumentText.setText(name);
            this.operation.setXmlDocument((XmlDocument)element);

            handleInfoChanged();
        }
		
	}
	
	/**
	 * @param tableList the list of tables
	 * @return the dialog
	 */
	private SelectFromEObjectListDialog createElementSelectionDialog(Collection<EObject> elementList) {
		String title = "Schema Element Selection";
		String message = "Select schema element for content via element reference in operation message";
		
        SelectFromEObjectListDialog dialog =  new SelectFromEObjectListDialog(
                		getShell(), elementList, false, title,  message,
                        ModelUtilities.getModelObjectLabelProvider());

        dialog.setValidator(new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				if (selection == null || selection.length == 0
						|| selection[0] == null
						|| (!(selection[0] instanceof XSDElementDeclaration)) ) {
					return new StatusInfo(WebServicePlugin.PLUGIN_ID, IStatus.ERROR, "No Element Selected");
				}
				return new StatusInfo(WebServicePlugin.PLUGIN_ID);
			}
		});


		return dialog;
	}
	
	/**
	 * @param tableList the list of tables
	 * @return the dialog
	 */
	private SelectFromEObjectListDialog createXmlDocumentSelectionDialog(Collection<EObject> elementList) {
		String title = getString("selectXmlDocument.title");
		String message = getString("selectXmlDocumentForOutputMessage.message");
		
        SelectFromEObjectListDialog dialog =  new SelectFromEObjectListDialog(
                		getShell(), elementList, false, title,  message,
                        ModelUtilities.getModelObjectLabelProvider());

        dialog.setValidator(new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				if (selection == null || selection.length == 0
						|| selection[0] == null
						|| (!(selection[0] instanceof XmlDocument)) ) {
					return new StatusInfo(WebServicePlugin.PLUGIN_ID, IStatus.ERROR, "No XML Document Selected");
				}
				return new StatusInfo(WebServicePlugin.PLUGIN_ID);
			}
		});


		return dialog;
	}

	protected void validate() {
		this.operation.validate();
		currentStatus = this.operation.getStatus();
		
		setCanFinish(this.currentStatus.getSeverity() != IStatus.ERROR);
		
		statusListener.notifyStatusChanged(currentStatus);
	}

	/**
	 * @param value if dialog can finish or not
	 */
	protected final void setCanFinish(boolean value) {
		this.canFinish = value;
	}
	

	/**
	 * @return if dialog can finish
	 */
	public boolean canFinish() {
		return this.canFinish;
	}
}