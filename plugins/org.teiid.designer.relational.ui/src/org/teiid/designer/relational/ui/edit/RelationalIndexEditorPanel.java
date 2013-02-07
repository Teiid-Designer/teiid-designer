/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalModelFactory;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.SelectFromEObjectListDialog;

/**
 *
 */
public class RelationalIndexEditorPanel  extends RelationalEditorPanel implements RelationalConstants {
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	

	private RelationalIndex index;
	
	TabFolder tabFolder;
	TabItem generalPropertiesTab;
	TabItem referencedColumnsTab;
	TabItem descriptionTab;
	
	// table property widgets
	Button autoUpdateCB, nullableCB, uniqueCB;
	Text helpText, modelNameText, nameText, nameInSourceText, filterConditionText;
	Text tableReferenceText;
	Button browseForTableButton;
	StyledTextEditor descriptionTextEditor;
	
	// parameter widgets
	Button addColumnButton, deleteColumnButton;

	TableViewer columnsViewer;
	
	boolean synchronizing = false;
	boolean processingChecks = false;

	/**
	 * @param parent the parent panel
	 * @param procedure the relational procedure BO
	 * @param modelFile the model file
	 * @param statusListener the dialog status listener
	 */
	public RelationalIndexEditorPanel(Composite parent, RelationalIndex procedure, IFile modelFile, IDialogStatusListener statusListener) {
		super(parent, procedure, modelFile, statusListener);
		this.index = procedure;
		
		
		synchronizeUI();
		
        this.nameText.setFocus();
	}
	
	@Override
	protected void createPanel(Composite parent) {
		// Spacer label
		new Label(parent, SWT.NONE);
		{
	    	helpText = new Text(parent, SWT.WRAP | SWT.READ_ONLY);
	    	helpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
	    	helpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	    	helpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    	((GridData)helpText.getLayoutData()).horizontalSpan = 1;
	    	((GridData)helpText.getLayoutData()).heightHint = 20;
	    	((GridData)helpText.getLayoutData()).widthHint = 360;
		}
		createNameGroup(parent);
		
		tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		createReferencedColumnsTab(tabFolder);
		createPropertiestab(tabFolder);
		createDescriptionTab(tabFolder);
		
	}
	
	
	void createPropertiestab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.PROPERTIES));
        this.generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.INDEX, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	void createDescriptionTab(TabFolder folderParent) {
        Composite thePanel = createDescriptionPanel(folderParent);

        this.descriptionTab = new TabItem(folderParent, SWT.NONE);
        this.descriptionTab.setControl(thePanel);
        this.descriptionTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION));
	}
	
	
	void createReferencedColumnsTab(TabFolder folderParent) {
        Composite thePanel = createReferencedColumnsPanel(folderParent);

        this.referencedColumnsTab = new TabItem(folderParent, SWT.NONE);
        this.referencedColumnsTab.setControl(thePanel);
        this.referencedColumnsTab.setText(Messages.referencedColumnsLabel);
        this.referencedColumnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	@Override
	protected void synchronizeUI() {
		if( synchronizing ) {
			return;
		}
		if( this.index == null ) {
			this.index = (RelationalIndex)getRelationalReference();
		}
		synchronizing = true;
		
    	helpText.setText(RelationalObjectEditorFactory.getHelpText(index));
		
		if( index.getName() != null ) {
			if( WidgetUtil.widgetValueChanged(this.nameText, index.getName()) ) {
				this.nameText.setText(index.getName());
			}
		} else {
			if( WidgetUtil.widgetValueChanged(this.nameText, EMPTY_STRING) ) {
				this.nameText.setText(EMPTY_STRING);
			}
		}
		
		if( index.getNameInSource() != null ) {
			if( WidgetUtil.widgetValueChanged(this.nameInSourceText, index.getNameInSource()) ) {
				this.nameInSourceText.setText(index.getNameInSource());
			}
		} else {
			if( WidgetUtil.widgetValueChanged(this.nameInSourceText, EMPTY_STRING) ) {
				this.nameInSourceText.setText(EMPTY_STRING);
			}
		}
		
		if( index.getFilterCondition() != null ) {
			if( WidgetUtil.widgetValueChanged(this.filterConditionText, index.getFilterCondition()) ) {
				this.filterConditionText.setText(index.getFilterCondition());
			}
		} else {
			if( WidgetUtil.widgetValueChanged(this.filterConditionText, EMPTY_STRING) ) {
				this.filterConditionText.setText(EMPTY_STRING);
			}
		}
		
		if( WidgetUtil.widgetValueChanged(autoUpdateCB, this.index.isAutoUpdate())) {
			this.autoUpdateCB.setSelection(this.index.isAutoUpdate());
		}
		
		if( WidgetUtil.widgetValueChanged(uniqueCB, this.index.isUnique())) {
			this.uniqueCB.setSelection(this.index.isUnique());
		}
		
		if( WidgetUtil.widgetValueChanged(nullableCB, this.index.isNullable())) {
			this.nullableCB.setSelection(this.index.isNullable());
		}
		
		generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.INDEX, index.getModelType(), index.getStatus()));

		synchronizing = false;
	}
	
	private void addSpacerLabels(Composite parent, int numSpacers) {
		for( int i=0; i<numSpacers; i++ ) {
			new Label(parent, SWT.NONE);
		}
	}
	
	Composite createNameGroup(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		thePanel.setLayout(new GridLayout(2, false));
		GridData panelGD = new GridData(GridData.FILL_BOTH);
		//panelGD.heightHint = 300;
    	thePanel.setLayoutData(panelGD);
    	
        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.modelFileLabel);
        
        this.modelNameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.modelNameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        this.modelNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.modelNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if( this.modelFile != null ) {
        	modelNameText.setText(this.modelFile.getName());
        }
        
        label = new Label(thePanel, SWT.NONE);
        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
        
        this.nameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.nameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.nameText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = nameText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
    			
    			index.setName(value);
    			handleInfoChanged();
    		}
        });
        
        label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.nameInSourceLabel);
        
        this.nameInSourceText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.nameInSourceText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.nameInSourceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.nameInSourceText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = nameInSourceText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
    			
    			index.setNameInSource(value);
    			handleInfoChanged();
    		}
        });
        
        return thePanel;
	}
	
	
	@SuppressWarnings("unused")
	Composite createDescriptionPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
		thePanel.setLayout(new GridLayout(2, false));
		GridData panelGD = new GridData(GridData.FILL_BOTH);
		//panelGD.heightHint = 300;
    	thePanel.setLayoutData(panelGD);
    	
        DESCRIPTION_GROUP: {
            final Group descGroup = WidgetFactory.createGroup(thePanel, UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION), GridData.FILL_BOTH, 3);
            descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
            final GridData descGridData = new GridData(GridData.FILL_BOTH);
            descGridData.horizontalSpan = 1;
            descGridData.heightHint = 200;
//            descGridData.minimumHeight = 30;
            descGridData.grabExcessVerticalSpace = true;
            descriptionTextEditor.setLayoutData(descGridData);
            descriptionTextEditor.setText(""); //$NON-NLS-1$
            descriptionTextEditor.getTextWidget().addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					index.setDescription(descriptionTextEditor.getText());
				}
			});
        }
    	
    	return thePanel;
	}

	Composite createPropertiesPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		thePanel.setLayout(new GridLayout(2, false));
		GridData panelGD = new GridData(GridData.FILL_BOTH);
		panelGD.heightHint = 300;
    	thePanel.setLayoutData(panelGD);
        
        this.autoUpdateCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        this.autoUpdateCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        this.autoUpdateCB.setText(Messages.autoUpdateLabel);
        this.autoUpdateCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	index.setAutoUpdate(autoUpdateCB.getSelection());
                handleInfoChanged();
            }
        });
        addSpacerLabels(thePanel, 1);
        
        this.nullableCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        this.nullableCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        this.nullableCB.setText(Messages.nullableLabel);
        this.nullableCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	index.setNullable(nullableCB.getSelection());
                handleInfoChanged();
            }
        });
        addSpacerLabels(thePanel, 1);

        
        this.uniqueCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        this.uniqueCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        this.uniqueCB.setText(Messages.uniqueLabel);
        this.uniqueCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	index.setUnique(uniqueCB.getSelection());
                handleInfoChanged();
            }
        });
        addSpacerLabels(thePanel, 1);

        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.filterConditionLabel);
        
        this.filterConditionText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.filterConditionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.filterConditionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.filterConditionText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = filterConditionText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
    			
    			index.setFilterCondition(value);
    			handleInfoChanged();
    		}
        });
        return thePanel;
	}
	
	Composite createReferencedColumnsPanel(Composite parent) {
	  	
	  	Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
	  	thePanel.setLayout(new GridLayout(3, false));
	  	GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
	  	groupGD.heightHint=300;
	  	thePanel.setLayoutData(groupGD);
	  	
	  	
        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.tableReferenceLabel);
        this.tableReferenceText = new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.tableReferenceText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.tableReferenceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.tableReferenceText.setEditable(false);
        this.tableReferenceText.setBackground(label.getBackground());
        
        this.browseForTableButton = WidgetFactory.createButton(thePanel, UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS));
        this.browseForTableButton.setToolTipText(Messages.browseModelToSelectTableForIndexTooltipText);
        this.browseForTableButton.setEnabled(true);
        this.browseForTableButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
        this.browseForTableButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected( SelectionEvent theEvent ) {
        		handleBrowseWorkspaceForTablePressed();
        	}
        });

    	Table columnTable = new Table(thePanel, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());
    	
        this.columnsViewer = new TableViewer(columnTable);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 200;
        data.horizontalSpan = 3;
        this.columnsViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.columnNameLabel + "          "); //$NON-NLS-1$
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();
        
        this.columnsViewer.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// NO OP
			}
			
			@Override
			public void dispose() {
				// NO OP
			}
			
			@Override
			public boolean hasChildren(Object element) {
				return true;
			}
			
			@Override
			public Object getParent(Object element) {
				return null;
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				if( inputElement instanceof RelationalTable ) {
					return ((RelationalTable)inputElement).getColumns().toArray(new Object[0]);
				}
				
				return new Object[0];
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				return new Object[0];
			}
		});
        
    	
        if( this.index != null ) {
    		for( RelationalColumn col : this.index.getColumns() ) {
    			for( TableItem item : columnsViewer.getTable().getItems() ) {
            		if( item.getData() == col ) {
            			item.setChecked(true);
            		}
            	}
    		}
        }
        
        this.columnsViewer.getTable().addSelectionListener(
				new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						index.getColumns().clear();
			        	for( TableItem item : columnsViewer.getTable().getItems() ) {
			        		
			        		if( item.getChecked() ) {
			        			index.addColumn((RelationalColumn)item.getData());
			        		}
			        	}
						validate();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
        
        return thePanel;
	}
	
	void handleInfoChanged() {
		if( synchronizing ) {
			return;
		}
		validate();
		
		synchronizeUI();
	}
	
	@Override
	protected void validate() {
		this.index.validate();
		
		setCanFinish(this.index.nameIsValid());
		
		IStatus currentStatus = this.index.getStatus();
		if( currentStatus.isOK() ) {
			setStatus(Status.OK_STATUS);
		} else {
			setStatus(currentStatus);
		}
		
	}

	private void handleBrowseWorkspaceForTablePressed() {
		ModelResource mr = ModelUtilities.getModelResourceForIFile(this.modelFile, true);
		
		List<EObject> childList = new ArrayList<EObject>(); 
		
		try {
			childList = mr.getEObjects();
		} catch (ModelWorkspaceException ex) {
			ex.printStackTrace();
		}
		
		List<EObject> tablesOnlyList = new ArrayList<EObject>();
		for( EObject child : childList) {
			if( child instanceof org.teiid.designer.metamodels.relational.Table ) {
				tablesOnlyList.add(child);
			}
		}
		
		SelectFromEObjectListDialog sdDialog = createTableSelectionDialog(tablesOnlyList);
		
		sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] selections = sdDialog.getResult();
            // should be single selection
            EObject tableObject = (EObject)selections[0];
            // TODO:
            // Create RelationalTable object from EObject and get "columns" and populate the columns viewer
            RelationalTable relTable = (RelationalTable)RelationalModelFactory.INSTANCE.getRelationalObject(tableObject);
            this.tableReferenceText.setText(relTable.getName());
            this.index.setRelationalTable(relTable);
            this.index.setUsesExistingTable(true);
            
    		columnsViewer.setInput(relTable);
            
            handleInfoChanged();
        }

	}
	
	/**
	 * @param tableList the list of tables
	 * @return the dialog
	 */
	public SelectFromEObjectListDialog createTableSelectionDialog(List<EObject> tableList) {
		String title = Messages.tableSelectionTitle;
		String message = Messages.selectExistingTableForIndexInitialMessage;
		
        SelectFromEObjectListDialog dialog = 
                new SelectFromEObjectListDialog(
                		tabFolder.getShell(), 
                		tableList, 
                         false, 
                         title, 
                         message,
                         ModelUtilities.getModelObjectLabelProvider());

        dialog.setValidator(new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				if (selection == null || selection.length == 0
						|| selection[0] == null
						|| (!(selection[0] instanceof org.teiid.designer.metamodels.relational.Table)) ) {
					return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR,Messages.noTableSelectedMessage);
				}
				return new StatusInfo(UiConstants.PLUGIN_ID);
			}
		});


		return dialog;
	}
	

	class ColumnDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public ColumnDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof RelationalColumn ) {
				switch (this.columnNumber) {
					case 0: {
						return ((RelationalColumn)element).getName();
					}
				}
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
			switch (this.columnNumber) {
			case 0: {
				return "Tooltip 1"; //getString("columnNameColumnTooltip"); //$NON-NLS-1$
			}
		}
		return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if( this.columnNumber == 0 ) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
			}
			return null;
		}
		
		
	}
    
 
}
