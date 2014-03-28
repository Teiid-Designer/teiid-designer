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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
import org.teiid.designer.relational.ui.editor.EditRelationalObjectDialogModel;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.SelectFromEObjectListDialog;

/**
 *
 */
public class RelationalIndexEditorPanel  extends RelationalEditorPanel implements RelationalConstants {
    private TabItem generalPropertiesTab;
    private TabItem referencedColumnsTab;
	
	// table property widgets
	private Button autoUpdateCB, nullableCB, uniqueCB;
	private Text filterConditionText;
	private Text tableReferenceText;
	private Button browseForTableButton;

	private TableViewer columnsViewer;

	/**
	 * @param parent the parent panel
	 * @param dialogModel dialog model
	 * @param statusListener the dialog status listener
	 */
	public RelationalIndexEditorPanel(Composite parent, EditRelationalObjectDialogModel dialogModel, IDialogStatusListener statusListener) {
		super(parent, dialogModel, statusListener);

		synchronizeUI();
	}

	@Override
	protected RelationalIndex getRelationalReference() {
	    return (RelationalIndex) super.getRelationalReference();
	}

	@Override
	protected void createPanel(Composite parent) {
		createNameGroup(parent);

		TabFolder tabFolder = createTabFolder(parent);
		createReferencedColumnsTab(tabFolder);
		createPropertiestab(tabFolder);
		createDescriptionTab(tabFolder);
		
	}

	private void createPropertiestab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.PROPERTIES));
        this.generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.INDEX, ModelType.PHYSICAL, Status.OK_STATUS));
	}

	private void createReferencedColumnsTab(TabFolder folderParent) {
        Composite thePanel = createReferencedColumnsPanel(folderParent);

        this.referencedColumnsTab = new TabItem(folderParent, SWT.NONE);
        this.referencedColumnsTab.setControl(thePanel);
        this.referencedColumnsTab.setText(Messages.referencedColumnsLabel);
        this.referencedColumnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	@Override
	protected void synchronizeExtendedUI() {
		if( getRelationalReference().getFilterCondition() != null ) {
			if( WidgetUtil.widgetValueChanged(this.filterConditionText, getRelationalReference().getFilterCondition()) ) {
				this.filterConditionText.setText(getRelationalReference().getFilterCondition());
			}
		} else {
			if( WidgetUtil.widgetValueChanged(this.filterConditionText, EMPTY_STRING) ) {
				this.filterConditionText.setText(EMPTY_STRING);
			}
		}
		
		if( WidgetUtil.widgetValueChanged(autoUpdateCB, getRelationalReference().isAutoUpdate())) {
			this.autoUpdateCB.setSelection(getRelationalReference().isAutoUpdate());
		}
		
		if( WidgetUtil.widgetValueChanged(uniqueCB, getRelationalReference().isUnique())) {
			this.uniqueCB.setSelection(getRelationalReference().isUnique());
		}
		
		if( WidgetUtil.widgetValueChanged(nullableCB, getRelationalReference().isNullable())) {
			this.nullableCB.setSelection(getRelationalReference().isNullable());
		}
		
		generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.INDEX, getRelationalReference().getModelType(), getRelationalReference().getStatus()));
	}

	private Composite createPropertiesPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		GridLayoutFactory.fillDefaults().numColumns(3).margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().applyTo(thePanel);
        
        this.autoUpdateCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.autoUpdateCB);
        this.autoUpdateCB.setText(Messages.autoUpdateLabel);
        this.autoUpdateCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	getRelationalReference().setAutoUpdate(autoUpdateCB.getSelection());
                handleInfoChanged();
            }
        });

        this.nullableCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.nullableCB);
        this.nullableCB.setText(Messages.nullableLabel);
        this.nullableCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	getRelationalReference().setNullable(nullableCB.getSelection());
                handleInfoChanged();
            }
        });

        this.uniqueCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.uniqueCB);
        this.uniqueCB.setText(Messages.uniqueLabel);
        this.uniqueCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	getRelationalReference().setUnique(uniqueCB.getSelection());
                handleInfoChanged();
            }
        });

        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.filterConditionLabel);
        
        this.filterConditionText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.filterConditionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(this.filterConditionText);
        this.filterConditionText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = filterConditionText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
    			
    			getRelationalReference().setFilterCondition(value);
    			handleInfoChanged();
    		}
        });
        return thePanel;
	}
	
	private Composite createReferencedColumnsPanel(Composite parent) {
	  	Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
	  	GridLayoutFactory.fillDefaults().numColumns(3).margins(10, 10).applyTo(thePanel);
	  	GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.tableReferenceLabel);
        GridDataFactory.fillDefaults().applyTo(label);

        this.tableReferenceText = new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.tableReferenceText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.tableReferenceText);
        this.tableReferenceText.setEditable(false);
        this.tableReferenceText.setBackground(label.getBackground());
        
        this.browseForTableButton = WidgetFactory.createButton(thePanel, UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS));
        this.browseForTableButton.setToolTipText(Messages.browseModelToSelectTableForIndexTooltipText);
        this.browseForTableButton.setEnabled(true);
        GridDataFactory.fillDefaults().hint(30, SWT.DEFAULT).applyTo(this.browseForTableButton);
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
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).hint(SWT.DEFAULT, 150).applyTo(this.columnsViewer.getControl());
        
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
        
    	
        if( getRelationalReference() != null ) {
    		for( RelationalColumn col : getRelationalReference().getColumns() ) {
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
						getRelationalReference().getColumns().clear();
			        	for( TableItem item : columnsViewer.getTable().getItems() ) {
			        		
			        		if( item.getChecked() ) {
			        			getRelationalReference().addColumn((RelationalColumn)item.getData());
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
	
	@Override
	protected void validate() {
		getRelationalReference().validate();
		
		setCanFinish(getRelationalReference().nameIsValid());
		
		IStatus currentStatus = getRelationalReference().getStatus();
		if( currentStatus.isOK() ) {
			setStatus(Status.OK_STATUS);
		} else {
			setStatus(currentStatus);
		}
		
	}

	private void handleBrowseWorkspaceForTablePressed() {
		ModelResource mr = ModelUtilities.getModelResourceForIFile(getModelFile(), true);
		
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
            getRelationalReference().setRelationalTable(relTable);
            getRelationalReference().setUsesExistingTable(true);
            
    		columnsViewer.setInput(relTable);
            
            handleInfoChanged();
        }

	}
	
	/**
	 * @param tableList the list of tables
	 * @return the dialog
	 */
	private SelectFromEObjectListDialog createTableSelectionDialog(List<EObject> tableList) {
		String title = Messages.tableSelectionTitle;
		String message = Messages.selectExistingTableForIndexInitialMessage;
		
        SelectFromEObjectListDialog dialog = 
                new SelectFromEObjectListDialog(
                		getShell(), 
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
