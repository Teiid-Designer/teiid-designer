/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalForeignKey;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalPrimaryKey;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalUniqueConstraint;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.eventsupport.IDialogStatusListener;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.ClosedProjectFilter;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceDialog;
import org.teiid.designer.ui.viewsupport.SingleProjectFilter;


/**
 * @since 8.0
 */
public class RelationalTableEditorPanel extends RelationalEditorPanel implements RelationalConstants {	
	private List<String> MULTIPLICITY_LIST;

	private TabItem generalPropertiesTab;
	private TabItem columnsTab;
	private TabItem primaryKeyTab;
	private TabItem uniqueConstraintTab;
	private TabItem foreignKeysTab;
	private TabItem nativeQueryTab;
	private TabItem	indexesTab;
	
	// table property widgets
	private Button materializedCB, supportsUpdateCB, isSystemTableCB, includePrimaryKeyCB;
	private Button findTableReferenceButton;
	private Label materializedTableLabel;
	private Text cardinalityText, materializedTableText,
		primaryKeyNameText,
		primaryKeyNISText,
		nativeQueryHelpText;
	private StyledTextEditor nativeQueryTextEditor;
	
	// column widgets
	private Button addColumnButton, editColumnButton, deleteColumnButton, upColumnButton, downColumnButton;
	private Button changePkColumnsButton, addFKButton, editFKButton, deleteFKButton;
	private Button addUCButton, editUCButton, deleteUCButton;
	private Button addIndexButton, deleteIndexButton, editIndexButton;
	private TableViewerBuilder columnsViewer;
	private TableViewerBuilder pkColumnsViewer, fkViewer;  //ucColumnsViewer,
	private TableViewerBuilder uniqueConstraintsViewer, indexesViewer;

	private boolean validationPerformed = false;
	
	private int HEIGHT_HINT_80 = 80;

	/**
	 * @param parent the parent panel
	 * @param dialogModel dialog model
	 * @param statusListener the status listener
	 */
	public RelationalTableEditorPanel(Composite parent, RelationalDialogModel dialogModel, IDialogStatusListener statusListener) {
		super(parent, dialogModel, statusListener);
		MULTIPLICITY_LIST = new ArrayList<String>();
		for( String str : MULTIPLICITY.AS_ARRAY ) {
			MULTIPLICITY_LIST.add(str);
		}
	}

	@Override
	protected RelationalTable getRelationalReference() {
	    return (RelationalTable) super.getRelationalReference();
	}

	@Override
	protected void createPanel(Composite parent) {
		createNameGroup(parent);

		TabFolder tabFolder = createTabFolder(parent);
		createGeneralPropertiesTab(tabFolder);
		createColumnsTab(tabFolder);
		createPrimaryKeyTab(tabFolder);
		createUniqueConstraintTab(tabFolder);
		createForeignKeysTab(tabFolder);
		createIndexesTab(tabFolder);
		createNativeQueryTab(tabFolder);
	}

	private void createGeneralPropertiesTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.PROPERTIES));
        this.generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	
	private void createColumnsTab(TabFolder folderParent) {
        Composite thePanel = createColumnTableGroup(folderParent);

        this.columnsTab = new TabItem(folderParent, SWT.NONE);
        this.columnsTab.setControl(thePanel);
        this.columnsTab.setText(Messages.columnsLabel);
        this.columnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	
	private void createPrimaryKeyTab(TabFolder folderParent) {
        Composite thePanel = createPrimaryKeyPanel(folderParent);
        
        this.primaryKeyTab = new TabItem(folderParent, SWT.NONE);
        this.primaryKeyTab.setControl(thePanel);
        this.primaryKeyTab.setText(Messages.primaryKeyLabel);
        this.primaryKeyTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PK, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	
	private void createUniqueConstraintTab(TabFolder folderParent) {
        Composite thePanel = createUniqueConstraintPanel(folderParent);
        
        this.uniqueConstraintTab = new TabItem(folderParent, SWT.NONE);
        this.uniqueConstraintTab.setControl(thePanel);
        this.uniqueConstraintTab.setText(Messages.uniqueConstraintLabel);
        this.uniqueConstraintTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.UC, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	
	private void createForeignKeysTab(TabFolder folderParent) {
        Composite thePanel = createForeignKeysPanel(folderParent);

        this.foreignKeysTab = new TabItem(folderParent, SWT.NONE);
        this.foreignKeysTab.setControl(thePanel);
        this.foreignKeysTab.setText(Messages.foreignKeysLabel);
        this.foreignKeysTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.FK, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	private void createIndexesTab(TabFolder folderParent) {
        Composite thePanel = createIndexesPanel(folderParent);
        
        this.indexesTab = new TabItem(folderParent, SWT.NONE);
        this.indexesTab.setControl(thePanel);
        this.indexesTab.setText(Messages.indexesLabel);
        this.indexesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.INDEX, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	private void createNativeQueryTab(TabFolder folderParent) {
        Composite thePanel = createNativeQueryPanel(folderParent);
        
        this.nativeQueryTab = new TabItem(folderParent, SWT.NONE);
        this.nativeQueryTab.setControl(thePanel);
        this.nativeQueryTab.setText(Messages.nativeQueryLabel);
        this.nativeQueryTab.setImage(RelationalUiUtil.getNativeSQLImage(Status.OK_STATUS));
	}
	
	@Override
	protected void synchronizeExtendedUI() {

	    synchronizePropertiesTab();
		synchronizeColumnsTab();
        synchronizePrimaryKeyTab();
        synchronizeForeignKeyTab();
        synchronizeUniqueConstraintTab();
        synchronizeIndexTab();
        synchronizeNativeQueryTab();

	    setNativeQueryEnablement(true );
	    setKeyTabsEnablement(true);
	}

	/**
     * Properties Tab
     */
    private void synchronizePropertiesTab() {
        if (generalPropertiesTab == null)
            return;

		if( WidgetUtil.widgetValueChanged(this.cardinalityText, getRelationalReference().getCardinality()) ) {
			this.cardinalityText.setText(Integer.toString(getRelationalReference().getCardinality()));
		}

		if (this.materializedCB != null) {
            boolean isMaterialized = getRelationalReference().isMaterialized();
            if (WidgetUtil.widgetValueChanged(materializedCB, isMaterialized)) {
                this.materializedCB.setSelection(isMaterialized);
            }
            this.materializedTableText.setEnabled(isMaterialized);
            this.findTableReferenceButton.setEnabled(isMaterialized);

            if (WidgetUtil.widgetValueChanged(materializedCB, getRelationalReference().getSupportsUpdate())) {
                this.supportsUpdateCB.setSelection(getRelationalReference().getSupportsUpdate());
            }
		}

		if( WidgetUtil.widgetValueChanged(isSystemTableCB, getRelationalReference().isSystem())) {
			this.isSystemTableCB.setSelection(getRelationalReference().isSystem());
		}
		generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, getRelationalReference().getModelType(), Status.OK_STATUS));
    }

    private void synchronizeColumnsTab() {
        /*
		 * Columns Tab
		 */
    	this.columnsViewer.getTable().removeAll();
    	IStatus maxStatus = Status.OK_STATUS;
        for( RelationalColumn row : getRelationalReference().getColumns() ) {
        	if( row.getStatus().getSeverity() > maxStatus.getSeverity() ) {
        		maxStatus = row.getStatus();
        	}
        	this.columnsViewer.add(row);
        }
        columnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, getRelationalReference().getModelType(), maxStatus));
    }

    /**
     * Foreign Keys Tab
     */
    private void synchronizeForeignKeyTab() {
        if (foreignKeysTab == null)
            return;

        IStatus maxStatus;

        maxStatus = Status.OK_STATUS;
        this.fkViewer.getTable().removeAll();
        for( RelationalForeignKey row : getRelationalReference().getForeignKeys()) {
        	if( row.getStatus().getSeverity() > maxStatus.getSeverity() ) {
        		maxStatus = row.getStatus();
        	}
        	this.fkViewer.add(row);
        }
        foreignKeysTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.FK, getRelationalReference().getModelType(), maxStatus));
    }

    /**
     * Primary Key Tab
     */
    private void synchronizePrimaryKeyTab() {
        if (primaryKeyTab == null)
            return;
        
        if( getRelationalReference().getPrimaryKey() == null ) {
        	if( WidgetUtil.widgetValueChanged(includePrimaryKeyCB, false)) {
        		this.includePrimaryKeyCB.setSelection(false);
        	}
        	this.primaryKeyNameText.setEnabled(false);
        	if( WidgetUtil.widgetValueChanged(primaryKeyNameText, EMPTY_STRING)) {
        		this.primaryKeyNameText.setText(EMPTY_STRING);
        	}
        	this.primaryKeyNISText.setEnabled(false);
        	if( WidgetUtil.widgetValueChanged(primaryKeyNISText, EMPTY_STRING)) {
        		this.primaryKeyNISText.setText(EMPTY_STRING);
        	}
        	this.changePkColumnsButton.setEnabled(false);
        	this.pkColumnsViewer.getTable().removeAll();
        	this.pkColumnsViewer.getTable().setEnabled(false);
        	this.primaryKeyTab.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.PK_ICON));
        } else {
        	this.pkColumnsViewer.getTable().setEnabled(true);
        	if( WidgetUtil.widgetValueChanged(includePrimaryKeyCB, true)) {
        		this.includePrimaryKeyCB.setSelection(true);
        	}
        	this.primaryKeyNameText.setEnabled(true);
        	if( getRelationalReference().getPrimaryKey().getName() != null && WidgetUtil.widgetValueChanged(primaryKeyNameText, getRelationalReference().getPrimaryKey().getName())) {
        		this.primaryKeyNameText.setText(getRelationalReference().getPrimaryKey().getName());
        	}
        	this.primaryKeyNISText.setEnabled(true);
        	if( getRelationalReference().getPrimaryKey().getNameInSource() != null && WidgetUtil.widgetValueChanged(primaryKeyNISText, getRelationalReference().getPrimaryKey().getNameInSource())) {
        		this.primaryKeyNISText.setText(getRelationalReference().getPrimaryKey().getNameInSource());
        	}
        	this.pkColumnsViewer.getTable().removeAll();
        	if( !getRelationalReference().getPrimaryKey().getColumns().isEmpty() ) {
        		for( RelationalColumn column : getRelationalReference().getPrimaryKey().getColumns() ) {
        			this.pkColumnsViewer.add(column);
        		}
        	}
        	this.changePkColumnsButton.setEnabled(true);
        	primaryKeyTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PK, getRelationalReference().getModelType(), getRelationalReference().getPrimaryKey().getStatus()));
        }
    }

    /**
     * Unique Constraint Tab
     */
    private void synchronizeUniqueConstraintTab() {
        if (uniqueConstraintTab == null)
            return;

        if( getRelationalReference().getUniqueConstraints().isEmpty() ) {
        	this.uniqueConstraintsViewer.getTable().removeAll();
        	this.uniqueConstraintsViewer.getTable().setEnabled(false);
        } else {
        	this.uniqueConstraintsViewer.getTable().setEnabled(true);

        	this.uniqueConstraintsViewer.getTable().removeAll();
        	if( !getRelationalReference().getUniqueConstraints().isEmpty() ) {
        		for( RelationalUniqueConstraint column : getRelationalReference().getUniqueConstraints() ) {
        			this.uniqueConstraintsViewer.add(column);
        		}
        	}

        	// Find highest severity status
        	IStatus ucStatus = Status.OK_STATUS;
        	for( RelationalUniqueConstraint constraint : getRelationalReference().getUniqueConstraints() ) {
        		if( constraint.getStatus().getSeverity() > ucStatus.getSeverity() ) {
        			ucStatus = constraint.getStatus();
        		}
        	}
        	uniqueConstraintTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.UC, getRelationalReference().getModelType(), ucStatus));
        }
    }

    /**
     * Index Tab
     */
    private void synchronizeIndexTab() {
        if (indexesTab == null)
            return;

        IStatus maxStatus;

        maxStatus = Status.OK_STATUS;
        this.indexesViewer.getTable().removeAll();
        for( RelationalIndex row : getRelationalReference().getIndexes()) {
        	if( row.getStatus().getSeverity() > maxStatus.getSeverity() ) {
        		maxStatus = row.getStatus();
        	}
        	this.indexesViewer.add(row);
        }
        indexesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.INDEX, getRelationalReference().getModelType(), maxStatus));
    }

    /**
     * Native Query Tab
     */
    private void synchronizeNativeQueryTab() {
        if (nativeQueryTab == null)
            return;

		if( getRelationalReference().getNativeQuery() != null ) {
			if( !StringUtilities.equals(this.nativeQueryTextEditor.getText(), getRelationalReference().getNativeQuery()) ) {
				this.nativeQueryTextEditor.setText(getRelationalReference().getNativeQuery());
			}
		} else {
			this.nativeQueryTextEditor.setText(EMPTY_STRING);
		}
    }

	private void setNativeQueryEnablement(boolean enable) {
//		if( enable ) {
//			this.nativeQueryHelpText.setText(Messages.nativeQueryHelpText);
//		} else {
//			this.nativeQueryHelpText.setText(Messages.nativeQueryNotSupportedForViews);
//		}
//		this.nativeQueryTextEditor.getTextWidget().setEnabled(enable);
//		if( enable ) {
//			nativeQueryTextEditor.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
//		} else {
//			nativeQueryTextEditor.getTextWidget().setBackground(nativeQueryTextEditor.getTextWidget().getParent().getBackground());
//		}
	}
	
	private void setKeyTabsEnablement(boolean enable) {
		addFKButton.setEnabled(enable);
		editFKButton.setEnabled(false);
		deleteFKButton.setEnabled(false);
		includePrimaryKeyCB.setEnabled(enable);
		addUCButton.setEnabled(enable);
		editUCButton.setEnabled(false);
		deleteUCButton.setEnabled(false);
		addIndexButton.setEnabled(enable);
		editIndexButton.setEnabled(false);
		deleteIndexButton.setEnabled(false);
		// Update buttons
		{
			IStructuredSelection selection = (IStructuredSelection)uniqueConstraintsViewer.getSelection();
			boolean value = ! selection.isEmpty();
			editUCButton.setEnabled(value);
			deleteUCButton.setEnabled(value);
		}
		{
			IStructuredSelection selection = (IStructuredSelection)fkViewer.getSelection();
			boolean value = ! selection.isEmpty();
			editFKButton.setEnabled(value);
			deleteFKButton.setEnabled(value);
		}
		{
			IStructuredSelection selection = (IStructuredSelection)indexesViewer.getSelection();
			boolean value = ! selection.isEmpty();
			editIndexButton.setEnabled(value);
			deleteIndexButton.setEnabled(value);
		}
	}

	private Composite createPropertiesPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

		Composite cardinalityPanel = new Composite(thePanel, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(cardinalityPanel);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(cardinalityPanel);

        Label label = new Label(cardinalityPanel, SWT.NONE);
        label.setText(Messages.cardinalityLabel);
        
        this.cardinalityText =  new Text(cardinalityPanel, SWT.BORDER | SWT.SINGLE);
        this.cardinalityText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.cardinalityText);
        this.cardinalityText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			if( isSynchronizing() )
    			    return;

    			if( !cardinalityText.getText().isEmpty()) {
    			    try {	
    			        int value = Integer.parseInt(cardinalityText.getText());
    			        getRelationalReference().setCardinality(value);
    			        handleInfoChanged();
    			    } catch (NumberFormatException ex) {
    			        MessageDialog.openError(
    			                                getShell(), 
    			                                Messages.cardinalityErrorTitle, 
    			                                Messages.cardinalityMustBeAnInteger);
	        				    return;
    			    }
    			}
    		}
        });

        Composite checkButtonPanel = new Composite(thePanel, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(checkButtonPanel);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(checkButtonPanel);

        this.supportsUpdateCB = new Button(checkButtonPanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.supportsUpdateCB);
        this.supportsUpdateCB.setText(Messages.supportsUpdateLabel);
        this.supportsUpdateCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	getRelationalReference().setSupportsUpdate(supportsUpdateCB.getSelection());
                handleInfoChanged();
            }
        });

        this.isSystemTableCB = new Button(checkButtonPanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.isSystemTableCB);
        this.isSystemTableCB.setText(Messages.systemTableLabel);
        this.isSystemTableCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	getRelationalReference().setSystem(isSystemTableCB.getSelection());
                handleInfoChanged();
            }
        });

        this.materializedCB = new Button(checkButtonPanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.materializedCB);
        this.materializedCB.setText(Messages.materializedLabel);
        this.materializedCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                getRelationalReference().setMaterialized(materializedCB.getSelection());
                if (!materializedCB.getSelection()) {
                    getRelationalReference().setMaterializedTable(null);
                }
                handleInfoChanged();
            }
        });

        Composite materializedPanel = new Composite(thePanel, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(materializedPanel);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(materializedPanel);

        materializedTableLabel = new Label(materializedPanel, SWT.NONE | SWT.RIGHT);
        materializedTableLabel.setText(Messages.tableReferenceLabel);
        GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(materializedTableLabel);

        this.materializedTableText = new Text(materializedPanel, SWT.BORDER | SWT.SINGLE);
        this.materializedTableText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.materializedTableText);
        this.materializedTableText.setEditable(false);

        this.findTableReferenceButton = new Button(materializedPanel, SWT.PUSH);
        this.findTableReferenceButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS));
        GridDataFactory.fillDefaults().hint(30, SWT.DEFAULT).applyTo(this.findTableReferenceButton);
        this.findTableReferenceButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	handleBrowseWorkspaceForMatTablePressed();
            }
        });

        createDescriptionPanel(thePanel);

        return thePanel;
	}
	
	/*
	 * Simple panel containing name, name in source values as well as a list of primary key columns from this table
	 */
	private Composite createPrimaryKeyPanel(Composite parent) {
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        this.includePrimaryKeyCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).span(2, 1).applyTo(this.includePrimaryKeyCB);
        this.includePrimaryKeyCB.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.INCLUDE));
        this.includePrimaryKeyCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	if( includePrimaryKeyCB.getSelection() ) {
            		if( getRelationalReference().getPrimaryKey() == null ) {
            			RelationalPrimaryKey key = new RelationalPrimaryKey();
            			if( primaryKeyNameText.getText() != null ) {
            				key.setName(primaryKeyNameText.getText());
            			}
            			getRelationalReference().setPrimaryKey(key);
            		}
            	} else {
            		getRelationalReference().setPrimaryKey(null);
            	}
                handleInfoChanged();
            }
        });
        
        Label label = new Label(thePanel, SWT.NONE | SWT.RIGHT);
        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
        GridDataFactory.fillDefaults().applyTo(label);
        
        this.primaryKeyNameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.primaryKeyNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().applyTo(this.primaryKeyNameText);
        this.primaryKeyNameText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = primaryKeyNameText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
        		if( getRelationalReference().getPrimaryKey() != null ) {
        			RelationalPrimaryKey key = getRelationalReference().getPrimaryKey();
        			key.setName(value);
        		}
        		handleInfoChanged();
    		}
        });
        
        label = new Label(thePanel, SWT.NONE | SWT.RIGHT);
        label.setText(Messages.nameInSourceLabel);
        GridDataFactory.fillDefaults().applyTo(label);
        
        this.primaryKeyNISText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.primaryKeyNISText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().applyTo(this.primaryKeyNISText);
        this.primaryKeyNISText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = primaryKeyNISText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
        		if( getRelationalReference().getPrimaryKey() != null ) {
        			RelationalPrimaryKey key = getRelationalReference().getPrimaryKey();
        			key.setNameInSource(value);
        		}
    			
        		handleInfoChanged();
    		}
        });
        
    	Composite buttonPanel = new Composite(thePanel, SWT.NONE);
    	GridLayoutFactory.fillDefaults().applyTo(buttonPanel);
    	GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).applyTo(buttonPanel);

	  	this.changePkColumnsButton = new Button(buttonPanel, SWT.PUSH);
    	this.changePkColumnsButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.CHANGE_ELIPSIS));
    	GridDataFactory.fillDefaults().applyTo(this.changePkColumnsButton);
    	this.changePkColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		SelectColumnsDialog dialog = new SelectColumnsDialog(getShell(), getRelationalReference(), true);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		Collection<RelationalColumn> selectedColumns = dialog.getSelectedColumns();
	        		if( !selectedColumns.isEmpty() ) {
	        			getRelationalReference().getPrimaryKey().setColumns(selectedColumns);
	        		} else {
	        			getRelationalReference().getPrimaryKey().setColumns(Collections.<RelationalColumn> emptyList());
	        		}
	        	}
	        	handleInfoChanged();
			}
    		
		});

        this.pkColumnsViewer = new TableViewerBuilder(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, HEIGHT_HINT_80).applyTo(this.pkColumnsViewer.getTableComposite());

        // create columns
        TableViewerColumn column = pkColumnsViewer.createColumn(SWT.LEFT, 100, 40, true);
        column.getColumn().setText(Messages.columnNameLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(0));

        if( getRelationalReference() != null && getRelationalReference().getPrimaryKey() != null ) {
	        for( RelationalColumn row : getRelationalReference().getPrimaryKey().getColumns() ) {
	        	this.columnsViewer.add(row);
	        }
        }

    	return thePanel;
	}
	
	private Composite createUniqueConstraintPanel(Composite parent) {
		// TODO: This panel needs to operate like the Foreign Key panel to allow multiple Unique Constraints
		// These constraints will behave just like Primary Keys so they won't need to referenced external columns/tables
		
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);
        
    	// Create 1 panels
    	// Top is just a Table of current FK with Add/Edit/Delete buttons
    	
    	// Bottom panel is the "Edit
        
        Composite buttonPanel = new Composite(thePanel, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

    	this.addUCButton = new Button(buttonPanel, SWT.PUSH);
    	this.addUCButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	GridDataFactory.fillDefaults().applyTo(this.addUCButton);
    	this.addUCButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalUniqueConstraint newUC = new RelationalUniqueConstraint();
				
				EditUniqueConstraintDialog dialog = new EditUniqueConstraintDialog(getShell(), getRelationalReference(), newUC, false);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		getRelationalReference().addUniqueConstraint(newUC);
	        	}
	        	handleInfoChanged();
	        	setUCButtonsState();
			}
    		
		});
    	
    	this.editUCButton = new Button(buttonPanel, SWT.PUSH);
    	this.editUCButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.EDIT_ELIPSIS));
    	GridDataFactory.fillDefaults().applyTo(this.editUCButton);
    	this.editUCButton.addSelectionListener(new SelectionAdapter() {

    		@Override
			public void widgetSelected(SelectionEvent e) {
    			RelationalUniqueConstraint uc = null;
				
				IStructuredSelection selection = (IStructuredSelection)uniqueConstraintsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalUniqueConstraint ) {
						uc =  (RelationalUniqueConstraint) obj;
						break;
					}
				}
				if( uc != null ) {
					
					EditUniqueConstraintDialog dialog = new EditUniqueConstraintDialog(getShell(), getRelationalReference(), uc, true);
		        	
		        	dialog.open();

		        	handleInfoChanged();
				}
				setUCButtonsState();
			}
    		
		});
    	this.editUCButton.setEnabled(false);
    	
    	this.deleteUCButton = new Button(buttonPanel, SWT.PUSH);
    	this.deleteUCButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	GridDataFactory.fillDefaults().applyTo(this.deleteUCButton);
    	this.deleteUCButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalUniqueConstraint uc= null;
				
				IStructuredSelection selection = (IStructuredSelection)uniqueConstraintsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalUniqueConstraint ) {
						uc =  (RelationalUniqueConstraint) obj;
						break;
					}
				}
				if( uc != null ) {
					getRelationalReference().removeUniqueConstraint(uc);
					handleInfoChanged();
				}
				setUCButtonsState();
			}
    		
		});
    	this.deleteUCButton.setEnabled(false);
    	
        this.uniqueConstraintsViewer = new TableViewerBuilder(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, HEIGHT_HINT_80).applyTo(this.uniqueConstraintsViewer.getTableComposite());

        // create columns
        TableViewerColumn column = uniqueConstraintsViewer.createColumn(SWT.LEFT, 100, 40, false);
        column.getColumn().setText(Messages.uniqueConstraintsLabel);
        column.setLabelProvider(new UniqueConstraintLabelProvider(0));

        if( getRelationalReference() != null && getRelationalReference().getUniqueConstraints() != null ) {
	        for( RelationalUniqueConstraint row : getRelationalReference().getUniqueConstraints() ) {
	        	this.uniqueConstraintsViewer.add(row);
	        }
        }
        
        this.uniqueConstraintsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setUCButtonsState();
			}
		});
        
        this.uniqueConstraintsViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				Object[] objs = sel.toArray();
				if( objs.length == 1 && objs[0] instanceof RelationalUniqueConstraint) {
					EditUniqueConstraintDialog dialog = new EditUniqueConstraintDialog(getShell(), getRelationalReference(), (RelationalUniqueConstraint)objs[0], true);
		        	int result = dialog.open();
		        	if( result == Window.OK) {
		        		//
		        	}
		        	handleInfoChanged();
				}
			}
		});

    	return thePanel;
	}
	
	private void setUCButtonsState() {
		IStructuredSelection selection = (IStructuredSelection)this.uniqueConstraintsViewer.getSelection();
		boolean enable = selection != null && !selection.isEmpty();
		deleteUCButton.setEnabled(enable);
		editUCButton.setEnabled(enable);
	}
	
	private Composite createForeignKeysPanel(Composite parent) {
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

    	// Create 1 panels
    	// Top is just a Table of current FK with Add/Edit/Delete buttons
    	
    	// Bottom panel is the "Edit
        
        Composite buttonPanel = new Composite(thePanel, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

    	this.addFKButton = new Button(buttonPanel, SWT.PUSH);
    	this.addFKButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	GridDataFactory.fillDefaults().applyTo(this.addFKButton);
    	this.addFKButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalForeignKey newFK = new RelationalForeignKey();
				
				EditForeignKeyDialog dialog = new EditForeignKeyDialog(getShell(), getModelFile(), getRelationalReference(), newFK, false);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		getRelationalReference().addForeignKey(newFK);
	        	}
	        	handleInfoChanged();
				setFKButtonsState();
			}
    		
		});
    	
    	this.editFKButton = new Button(buttonPanel, SWT.PUSH);
    	this.editFKButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.EDIT_ELIPSIS));
    	GridDataFactory.fillDefaults().applyTo(this.editFKButton);
    	this.editFKButton.addSelectionListener(new SelectionAdapter() {

    		@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalForeignKey fk = null;
				
				IStructuredSelection selection = (IStructuredSelection)fkViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalForeignKey ) {
						fk =  (RelationalForeignKey) obj;
						break;
					}
				}
				if( fk != null ) {
					
					EditForeignKeyDialog dialog = new EditForeignKeyDialog(getShell(), getModelFile(), getRelationalReference(), fk, true);
		        	
		        	int result = dialog.open();
		        	if( result == Window.OK) {
		        		// TODO:  inject info from tempFK if dialog isn't cancelled
		        	}
		        	handleInfoChanged();
				}
				setFKButtonsState();
			}
    		
		});
    	this.editFKButton.setEnabled(false);
    	
    	this.deleteFKButton = new Button(buttonPanel, SWT.PUSH);
    	this.deleteFKButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	GridDataFactory.fillDefaults().applyTo(this.deleteFKButton);
    	this.deleteFKButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalForeignKey fk = null;
				
				IStructuredSelection selection = (IStructuredSelection)fkViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalForeignKey ) {
						fk =  (RelationalForeignKey) obj;
						break;
					}
				}
				if( fk != null ) {
					getRelationalReference().removeForeignKey(fk);
					handleInfoChanged();
				}
				setFKButtonsState();
			}
    		
		});
    	this.deleteFKButton.setEnabled(false);

        this.fkViewer = new TableViewerBuilder(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, HEIGHT_HINT_80).applyTo(this.fkViewer.getTableComposite());

        // create columns
        TableViewerColumn column = fkViewer.createColumn(SWT.LEFT, 100, 40, false);
        column.getColumn().setText(Messages.fkNameLabel);
        column.setLabelProvider(new FKDataLabelProvider(0));

        if( getRelationalReference() != null) {
	        for( RelationalForeignKey row : getRelationalReference().getForeignKeys()) {
	        	this.fkViewer.add(row);
	        }
        }
        
        this.fkViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setFKButtonsState();
			}
		});
        
        this.fkViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				Object[] objs = sel.toArray();
				if( objs.length == 1 && objs[0] instanceof RelationalForeignKey) {
					EditForeignKeyDialog dialog = new EditForeignKeyDialog(getShell(), getModelFile(), getRelationalReference(), (RelationalForeignKey)objs[0], true);
		        	int result = dialog.open();
		        	if( result == Window.OK) {
		        		//
		        	}
		        	handleInfoChanged();
				}
			}
		});
        
        return thePanel;
	}
	
	private void setFKButtonsState() {
		IStructuredSelection selection = (IStructuredSelection)this.fkViewer.getSelection();
		boolean enable = selection != null && !selection.isEmpty();
		deleteFKButton.setEnabled(enable);
		editFKButton.setEnabled(enable);
	}
	
	private Composite createIndexesPanel(Composite parent) {
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        Composite buttonPanel = new Composite(thePanel, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

    	this.addIndexButton = new Button(buttonPanel, SWT.PUSH);
    	this.addIndexButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	GridDataFactory.fillDefaults().applyTo(this.addIndexButton);
    	this.addIndexButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalIndex newIndex = new RelationalIndex();
				
				EditIndexDialog dialog = new EditIndexDialog(getShell(), getRelationalReference(), newIndex, false);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		getRelationalReference().addIndex(newIndex);
	        	}
	        	handleInfoChanged();
	        	setIndexButtonsState();
			}
    		
		});
    	
    	this.editIndexButton = new Button(buttonPanel, SWT.PUSH);
    	this.editIndexButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.EDIT_ELIPSIS));
    	GridDataFactory.fillDefaults().applyTo(this.editIndexButton);
    	this.editIndexButton.addSelectionListener(new SelectionAdapter() {

    		@Override
			public void widgetSelected(SelectionEvent e) {
    			RelationalIndex index = null;
				
				IStructuredSelection selection = (IStructuredSelection)indexesViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalIndex ) {
						index =  (RelationalIndex) obj;
						break;
					}
				}
				if( index != null ) {
					
					EditIndexDialog dialog = new EditIndexDialog(getShell(), getRelationalReference(), index, true);
		        	
		        	int result = dialog.open();
		        	if( result == Window.OK) {
		        		//
		        	}
		        	handleInfoChanged();
				}
				setIndexButtonsState();
			}
    		
		});
    	this.editIndexButton.setEnabled(false);
    	
    	this.deleteIndexButton = new Button(buttonPanel, SWT.PUSH);
    	this.deleteIndexButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	GridDataFactory.fillDefaults().applyTo(this.deleteIndexButton);
    	this.deleteIndexButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalIndex index = null;
				
				IStructuredSelection selection = (IStructuredSelection)indexesViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalIndex ) {
						index =  (RelationalIndex) obj;
						break;
					}
				}
				if( index != null ) {
					getRelationalReference().removeIndex(index);
					handleInfoChanged();
				}
				setIndexButtonsState();
			}
    		
		});
    	this.deleteIndexButton.setEnabled(false);

        this.indexesViewer = new TableViewerBuilder(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, HEIGHT_HINT_80).applyTo(this.indexesViewer.getTableComposite());

        // create columns
        TableViewerColumn column = indexesViewer.createColumn(SWT.LEFT, 100, 40, false);
        column.getColumn().setText(Messages.indexLabel);
        column.setLabelProvider(new IndexDataLabelProvider(0));

        if( getRelationalReference() != null && getRelationalReference().getIndexes() != null ) {
	        for( RelationalIndex row : getRelationalReference().getIndexes() ) {
	        	this.indexesViewer.add(row);
	        }
        }
        
        this.indexesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setIndexButtonsState();
			}
		});
        
        this.indexesViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				Object[] objs = sel.toArray();
				if( objs.length == 1 && objs[0] instanceof RelationalIndex) {
					EditIndexDialog dialog = new EditIndexDialog(getShell(), getRelationalReference(), (RelationalIndex)objs[0], true);
		        	
		        	int result = dialog.open();
		        	if( result == Window.OK) {
		        		//
		        	}
		        	handleInfoChanged();
				}
				setIndexButtonsState();
			}
		});

    	return thePanel;
	}
	
	private void setIndexButtonsState() {
		IStructuredSelection selection = (IStructuredSelection)this.indexesViewer.getSelection();
		boolean enable = selection != null && !selection.isEmpty();
		deleteIndexButton.setEnabled(enable);
		editIndexButton.setEnabled(enable);
	}
	
	/*
	 * Simple panel containing name, name in source values as well as a list of primary key columns from this table
	 */
	private Composite createNativeQueryPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(thePanel);

		nativeQueryHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
		nativeQueryHelpText.setBackground(parent.getBackground());
		nativeQueryHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(250, 40).applyTo(nativeQueryHelpText);
		nativeQueryHelpText.setText(Messages.nativeQueryHelpText);

        final Group descGroup = WidgetFactory.createGroup(thePanel, Messages.sqlLabel, GridData.FILL_BOTH, 3);
        nativeQueryTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200).minSize(SWT.DEFAULT, 30).applyTo(nativeQueryTextEditor.getTextWidget());
        nativeQueryTextEditor.setText(""); //$NON-NLS-1$
        nativeQueryTextEditor.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        nativeQueryTextEditor.getTextWidget().addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getRelationalReference().setNativeQuery(nativeQueryTextEditor.getText());
                handleInfoChanged();
            }
        });

    	return thePanel;
	}

	private Composite createColumnTableGroup(Composite parent) {
		  	
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 5);
        GridLayoutFactory.fillDefaults().numColumns(5).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

    	addColumnButton = new Button(buttonPanel, SWT.PUSH);
    	addColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	GridDataFactory.fillDefaults().applyTo(addColumnButton);
    	addColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		getRelationalReference().createColumn();
				handleInfoChanged();
				setColumnButtonsState();
			}
    		
		});

    	editColumnButton = new Button(buttonPanel, SWT.PUSH);
    	editColumnButton.setText(Messages.Edit);
    	GridDataFactory.fillDefaults().applyTo(editColumnButton);
    	editColumnButton.setEnabled(false);
    	editColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalColumn column = null;
				
				IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalColumn ) {
						column =  (RelationalColumn) obj;
						break;
					}
				}
				if( column != null ) {
					EditColumnDialog dialog = new EditColumnDialog(getShell(), column);
					dialog.open();
					handleInfoChanged();
				}
				setColumnButtonsState();
			}
    		
		});
    	
    	deleteColumnButton = new Button(buttonPanel, SWT.PUSH);
    	deleteColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	GridDataFactory.fillDefaults().applyTo(deleteColumnButton);
    	deleteColumnButton.setEnabled(false);
    	deleteColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalColumn column = null;
				
				IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalColumn ) {
						column =  (RelationalColumn) obj;
						break;
					}
				}
				if( column != null ) {
					getRelationalReference().removeColumn(column);
					handleInfoChanged();
				}
				setColumnButtonsState();
			}
    		
		});
    	
    	upColumnButton = new Button(buttonPanel, SWT.PUSH);
    	upColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.MOVE_UP));
    	GridDataFactory.fillDefaults().applyTo(upColumnButton);
    	upColumnButton.setEnabled(false);
    	upColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalColumn info = null;
				
				IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalColumn ) {
						info =  (RelationalColumn) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = columnsViewer.getTable().getSelectionIndex();
					getRelationalReference().moveColumnUp(info);
					handleInfoChanged();
					columnsViewer.getTable().select(selectedIndex-1);
				}
				setColumnButtonsState();
			}
    		
		});
    	
    	downColumnButton = new Button(buttonPanel, SWT.PUSH);
    	downColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.MOVE_DOWN));
    	GridDataFactory.fillDefaults().applyTo(downColumnButton);
    	downColumnButton.setEnabled(false);
    	downColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalColumn info = null;
				
				IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalColumn ) {
						info =  (RelationalColumn) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = columnsViewer.getTable().getSelectionIndex();
					getRelationalReference().moveColumnDown(info);
					handleInfoChanged();
					columnsViewer.getTable().select(selectedIndex+1);
				}
				setColumnButtonsState();
			}
    		
		});
    	
    	this.columnsViewer = new TableViewerBuilder(thePanel, (SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER));  	

        // create columns
        TableViewerColumn column = this.columnsViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(Messages.columnNameLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(0));

        column = this.columnsViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(Messages.dataTypeLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        
        column = this.columnsViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(Messages.lengthLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(2));

        if( getRelationalReference() != null ) {
	        for( RelationalColumn row : getRelationalReference().getColumns() ) {
	        	this.columnsViewer.add(row);
	        }
        }
        
        this.columnsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {		
				setColumnButtonsState();
			}
		});
        
        this.columnsViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				Object[] objs = sel.toArray();
				if( objs.length == 1 && objs[0] instanceof RelationalColumn) {
					EditColumnDialog dialog = new EditColumnDialog(getShell(), (RelationalColumn)objs[0]);
					dialog.open();
					handleInfoChanged();
				}
				setColumnButtonsState();
			}
		});
        
        return thePanel;
    }
	
	private void setColumnButtonsState() {
		IStructuredSelection selection = (IStructuredSelection)this.columnsViewer.getSelection();
		boolean enable = selection != null && !selection.isEmpty();
		deleteColumnButton.setEnabled(enable);
		editColumnButton.setEnabled(enable);
		if( enable ) {
			Object[] objs = selection.toArray();
			RelationalColumn columnInfo = (RelationalColumn)objs[0];
			upColumnButton.setEnabled(getRelationalReference().canMoveColumnUp(columnInfo));
			downColumnButton.setEnabled(getRelationalReference().canMoveColumnDown(columnInfo));
		} else {
			upColumnButton.setEnabled(false);
			downColumnButton.setEnabled(false);
		}
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
		validationPerformed = true;
	}
	
	@Override
	public boolean canFinish() {
		// check if procedure name is not-null
		if( !validationPerformed && this.getRelationalReference().getName() != null ) {
			return true;
		} else if( validationPerformed) {
			return this.getRelationalReference().getName() != null;
		}
		
		return false;
	}
	
	private void handleBrowseWorkspaceForMatTablePressed() {
		ModelWorkspaceDialog sdDialog = createTableOrViewSelector();

		// add filters
		sdDialog.addFilter(new ClosedProjectFilter());
		Properties props = new Properties();
		DesignerPropertiesUtil.setProjectName(props, getModelFile().getProject().getName());
		sdDialog.addFilter(new SingleProjectFilter(props));
		
		sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] selections = sdDialog.getResult();
            // should be single selection
            EObject matTableOrView = (EObject)selections[0];
            String name = ModelerCore.getModelEditor().getName(matTableOrView);
            this.materializedTableText.setText(name);
            getRelationalReference().setMaterializedTable(name);
            ModelResource mr = ModelUtilities.getModelResource(matTableOrView);
            try {
				getRelationalReference().setMaterializedTableModelPath(mr.getCorrespondingResource().getFullPath().toString());
			} catch (ModelWorkspaceException e) {
				e.printStackTrace();
			}
            validate();
        }

	}
    
	private ModelWorkspaceDialog createTableOrViewSelector() {
		
		ModelWorkspaceDialog result = new ModelWorkspaceDialog(getShell(), null,
				new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());

		String title = "Select referenced table or view"; //$NON-NLS-1$
		String message = "Select referenced materialized table or view"; //$NON-NLS-1$
		result.setTitle(title);
		result.setMessage(message);
		result.setAllowMultiple(false);

		result.setInput(ModelerCore.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				if (selection == null || selection.length == 0
						|| selection[0] == null
						|| (!(selection[0] instanceof org.teiid.designer.metamodels.relational.Table) && !(selection[0] instanceof View)) ) {
					String msg = "Selection is not a table or view"; //$NON-NLS-1$
					return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR,msg);
				}
				return new StatusInfo(UiConstants.PLUGIN_ID);
			}
		});


		return result;
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
					case 1: {
						return ((RelationalColumn)element).getDatatype();
					}
					case 2: {
						return Integer.toString(((RelationalColumn)element).getLength());
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
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
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
    class FKDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public FKDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof RelationalForeignKey ) {
				switch (this.columnNumber) {
					case 0: {
						if(element instanceof RelationalForeignKey) {
							return ((RelationalForeignKey)element).getName();
						}
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
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
			}
		}
		return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if( this.columnNumber == 0 ) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.FK_ICON);
			}
			return null;
		}
		
		
	}
   
    class SelectColumnsDialog extends TitleAreaDialog {
    	private final String TITLE = Messages.selectColumnsTitle;
    	
        //=============================================================
        // Instance variables
        //=============================================================
        private RelationalTable theTable;

        TableViewer columnDataViewer;
        
        private Set<RelationalColumn> selectedColumns = new HashSet<RelationalColumn>();
        
        //=============================================================
        // Constructors
        //=============================================================
        /**
         * @param parent the parent shell
         * @param theTable the relational table
         * @param isPrimaryKeyColumns the primary key columns
         * 
         */
        public SelectColumnsDialog(Shell parent, RelationalTable theTable, boolean isPrimaryKeyColumns) {
            super(parent);
            this.theTable = theTable;
        }
        
        @Override
        protected void configureShell( Shell shell ) {
            super.configureShell(shell);
            shell.setText(TITLE);
        }
        
        /* (non-Javadoc)
        * @see org.eclipse.jface.window.Window#setShellStyle(int)
        */
        @Override
        protected void setShellStyle( int newShellStyle ) {
            super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);

        }
            
        //=============================================================
        // Instance methods
        //=============================================================

        @Override
        protected Control createDialogArea(Composite parent) {
        	setTitle(Messages.selectColumnsSubTitle);
        	
            Composite composite = (Composite)super.createDialogArea(parent);
            //------------------------------        
            // Set layout for the Composite
            //------------------------------
            GridLayoutFactory.fillDefaults().applyTo(composite);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

        	Group columnsGroup = WidgetFactory.createGroup(composite, Messages.selectColumnsTitle, SWT.NONE, 1, 2);
        	GridDataFactory.fillDefaults().grab(true, true).applyTo(columnsGroup);

    		Table table = new Table(columnsGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
    		table.setHeaderVisible(false);
    		table.setLinesVisible(true);
    		table.setLayout(new TableLayout());

    		this.columnDataViewer = new TableViewer(table);
    		GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 100).span(2, 1).applyTo(this.columnDataViewer.getControl());
    		this.columnDataViewer.setContentProvider(new ITreeContentProvider() {

				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void dispose() {
					// TODO Auto-generated method stub
				}
				
				@Override
				public boolean hasChildren(Object element) {
					return !theTable.getColumns().isEmpty();
				}
				
				@Override
				public Object getParent(Object element) {
					return null;
				}
				
				@Override
				public Object[] getElements(Object inputElement) {
					if( inputElement instanceof RelationalTable ) {
						return theTable.getColumns().toArray(new Object[0]);
					}
					return new Object[0];
				}
				
				@Override
				public Object[] getChildren(Object parentElement) {
					// TODO Auto-generated method stub
					return new Object[0];
				}
			});
			
    		this.columnDataViewer.setLabelProvider(new ColumnDataLabelProvider(0));
    		
    		this.columnDataViewer.setInput(this.theTable);
    		
			for( RelationalColumn col : this.theTable.getPrimaryKey().getColumns() ) {
				for( TableItem item : columnDataViewer.getTable().getItems() ) {
	        		if( item.getData() == col ) {
	        			item.setChecked(true);
	        		}
	        	}
			}
            
            setMessage(Messages.selectColumnsMessage);
            return composite;
        }
        
        @Override
        public void create() {
            super.create();
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        @Override
        protected void okPressed() {
        	for( TableItem item : columnDataViewer.getTable().getItems() ) {
        		if( item.getChecked() ) {
        			this.selectedColumns.add((RelationalColumn)item.getData());
        		}
        	}
            super.okPressed();
        }
        
        public Collection<RelationalColumn> getSelectedColumns() {
        	return selectedColumns;
        }

    }
    
    class IndexDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public IndexDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof RelationalIndex ) {
				switch (this.columnNumber) {
					case 0: {
						if(element instanceof RelationalIndex) {
							RelationalIndex index = (RelationalIndex)element;
							
							String value = index.getName();
							
							if(! index.getColumns().isEmpty() ) {
								int i=0;
								value = value + " : "; //$NON-NLS-1$
								for( RelationalColumn col : index.getColumns()) {
									value += col.getName();
									i++;
									if( i < index.getColumns().size()) {
										value += ", "; //$NON-NLS-1$
									}
								}
							}
							return value;
						}
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
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
			}
		}
		return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if( this.columnNumber == 0 ) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.INDEX_ICON);
			}
			return null;
		}
	}
    
    class UniqueConstraintLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public UniqueConstraintLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof RelationalUniqueConstraint ) {
				switch (this.columnNumber) {
					case 0: {
						if(element instanceof RelationalUniqueConstraint) {
							RelationalUniqueConstraint index = (RelationalUniqueConstraint)element;
							
							String value = index.getName();
							
							if(! index.getColumns().isEmpty() ) {
								int i=0;
								value = value + " : "; //$NON-NLS-1$
								for( RelationalColumn col : index.getColumns()) {
									value += col.getName();
									i++;
									if( i < index.getColumns().size()) {
										value += ", "; //$NON-NLS-1$
									}
								}
							}
							return value;
						}
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
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
			}
		}
		return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if( this.columnNumber == 0 ) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.UC_ICON);
			}
			return null;
		}
		
		
	}
}
