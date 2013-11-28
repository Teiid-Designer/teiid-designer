/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalForeignKey;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalPrimaryKey;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalUniqueConstraint;
import org.teiid.designer.relational.model.RelationalView;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.table.ComboBoxEditingSupport;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;


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
	private Button materializedCB, supportsUpdateCB, isSystemTableCB, includePrimaryKeyCB, includeUniqueConstraintCB;
	private Button findTableReferenceButton;
	private Label materializedTableLabel;
	private Text cardinalityText, materializedTableText,
		primaryKeyNameText, uniqueConstraintNameText,
		primaryKeyNISText, uniqueConstraintNISText,
		nativeQueryHelpText;
	private StyledTextEditor nativeQueryTextEditor;
	
	// column widgets
	private Button addColumnButton, deleteColumnButton, upColumnButton, downColumnButton;
	private Button changePkColumnsButton, changeUcColumnsButton, addFKButton, editFKButton, deleteFKButton;
	private Button addIndexButton, deleteIndexButton, editIndexButton;
	private TableViewer columnsViewer;
	private TableViewer pkColumnsViewer, ucColumnsViewer, fkViewer;
	private TableViewer indexesViewer;

	private boolean finishedStartup = false;

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
		
		synchronizeUI();
	}

	@Override
	protected RelationalTable getRelationalReference() {
	    return (RelationalTable) super.getRelationalReference();
	}

	private boolean isPhysicalModel() {
	    return getRelationalReference().getModelType() == ModelType.PHYSICAL;
	}

	private boolean isRelationalView() {
        return getRelationalReference() instanceof RelationalView;
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
		
		finishedStartup = true;
		
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
	    if (isRelationalView())
	        return;

        Composite thePanel = createPrimaryKeyPanel(folderParent);
        
        this.primaryKeyTab = new TabItem(folderParent, SWT.NONE);
        this.primaryKeyTab.setControl(thePanel);
        this.primaryKeyTab.setText(Messages.primaryKeyLabel);
        this.primaryKeyTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PK, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	
	private void createUniqueConstraintTab(TabFolder folderParent) {
	    if (isRelationalView())
	        return;

        Composite thePanel = createUniqueConstraintPanel(folderParent);
        
        this.uniqueConstraintTab = new TabItem(folderParent, SWT.NONE);
        this.uniqueConstraintTab.setControl(thePanel);
        this.uniqueConstraintTab.setText(Messages.uniqueConstraintLabel);
        this.uniqueConstraintTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.UC, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	
	private void createForeignKeysTab(TabFolder folderParent) {
	    if (isRelationalView())
	        return;

        Composite thePanel = createForeignKeysPanel(folderParent);

        this.foreignKeysTab = new TabItem(folderParent, SWT.NONE);
        this.foreignKeysTab.setControl(thePanel);
        this.foreignKeysTab.setText(Messages.foreignKeysLabel);
        this.foreignKeysTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.FK, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	private void createIndexesTab(TabFolder folderParent) {
	    if (isRelationalView())
	        return;

        Composite thePanel = createIndexesPanel(folderParent);
        
        this.indexesTab = new TabItem(folderParent, SWT.NONE);
        this.indexesTab.setControl(thePanel);
        this.indexesTab.setText(Messages.indexesLabel);
        this.indexesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.INDEX, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	private void createNativeQueryTab(TabFolder folderParent) {
	    if (isRelationalView())
	        return;

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

        if( finishedStartup ) {
	        setNativeQueryEnablement(! isRelationalView() );
	        setKeyTabsEnablement(! isRelationalView());
        }
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

        if( getRelationalReference().getUniqueContraint() == null ) {
        	if( WidgetUtil.widgetValueChanged(includeUniqueConstraintCB, false)) {
        		this.includeUniqueConstraintCB.setSelection(false);
        	}
        	this.uniqueConstraintNameText.setEnabled(false);
        	if( WidgetUtil.widgetValueChanged(uniqueConstraintNameText, EMPTY_STRING)) {
        		this.uniqueConstraintNameText.setText(EMPTY_STRING);
        	}
        	this.uniqueConstraintNISText.setEnabled(false);
        	if( WidgetUtil.widgetValueChanged(uniqueConstraintNISText, EMPTY_STRING)) {
        		this.uniqueConstraintNISText.setText(EMPTY_STRING);
        	}
        	this.changeUcColumnsButton.setEnabled(false);
        	this.ucColumnsViewer.getTable().removeAll();
        	this.ucColumnsViewer.getTable().setEnabled(false);
        } else {
        	this.ucColumnsViewer.getTable().setEnabled(true);
        	if( WidgetUtil.widgetValueChanged(includeUniqueConstraintCB, true)) {
        		this.includeUniqueConstraintCB.setSelection(true);
        	}
        	this.uniqueConstraintNameText.setEnabled(true);
        	if( getRelationalReference().getUniqueContraint().getName() != null && WidgetUtil.widgetValueChanged(uniqueConstraintNameText, getRelationalReference().getUniqueContraint().getName())) {
        		this.uniqueConstraintNameText.setText(getRelationalReference().getUniqueContraint().getName());
        	}
        	this.uniqueConstraintNISText.setEnabled(true);
        	if( getRelationalReference().getUniqueContraint().getNameInSource() != null && WidgetUtil.widgetValueChanged(uniqueConstraintNISText, getRelationalReference().getUniqueContraint().getNameInSource())) {
        		this.uniqueConstraintNISText.setText(getRelationalReference().getUniqueContraint().getName());
        	}

        	this.ucColumnsViewer.getTable().removeAll();
        	if( !getRelationalReference().getUniqueContraint().getColumns().isEmpty() ) {
        		for( RelationalColumn column : getRelationalReference().getUniqueContraint().getColumns() ) {
        			this.ucColumnsViewer.add(column);
        		}
        	}
        	this.changeUcColumnsButton.setEnabled(true);
        	uniqueConstraintTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.UC, getRelationalReference().getModelType(), getRelationalReference().getUniqueContraint().getStatus()));
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
//		addFKButton.setEnabled(enable);
//		editFKButton.setEnabled(enable);
//		deleteFKButton.setEnabled(enable);
//		includePrimaryKeyCB.setEnabled(enable);
//		includeUniqueConstraintCB.setEnabled(enable);
//		addIndexButton.setEnabled(enable);
//		editIndexButton.setEnabled(enable);
//		deleteIndexButton.setEnabled(enable);
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
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(checkButtonPanel);

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

        if (isPhysicalModel()) {
            this.materializedCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
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

            this.findTableReferenceButton = new Button(materializedPanel, SWT.PUSH);
            this.findTableReferenceButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS));
            GridDataFactory.fillDefaults().hint(30, SWT.DEFAULT).applyTo(this.findTableReferenceButton);
            this.findTableReferenceButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {

                }
            });
        }

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

        if(isRelationalView()) {
            Text pkHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
            pkHelpText.setBackground(parent.getBackground());
            pkHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(250, 100).span(2, 1).applyTo(pkHelpText);
            pkHelpText.setText(Messages.primaryKeysNotSupportedForViews);
        }

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
	        			getRelationalReference().getPrimaryKey().setColumns(Collections.EMPTY_LIST);
	        		}
	        	}
	        	handleInfoChanged();
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());

        this.pkColumnsViewer = new TableViewer(columnTable);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 100).applyTo(this.pkColumnsViewer.getControl());        
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.pkColumnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.columnNameLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();
        
        if( getRelationalReference() != null && getRelationalReference().getPrimaryKey() != null ) {
	        for( RelationalColumn row : getRelationalReference().getPrimaryKey().getColumns() ) {
	        	this.columnsViewer.add(row);
	        }
        }
        //LayoutDebugger.debugLayout(theGroup);
    	
    	return thePanel;
	}
	
	private Composite createUniqueConstraintPanel(Composite parent) {
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        if(isRelationalView()) {
            Text ucHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
            ucHelpText.setBackground(parent.getBackground());
            ucHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(250, 100).span(2, 1).applyTo(ucHelpText);
            ucHelpText.setText(Messages.uniqueConstraintsNotSupportedForViews);
        }

        this.includeUniqueConstraintCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).span(2, 1).applyTo(this.includeUniqueConstraintCB);
        this.includeUniqueConstraintCB.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.INCLUDE));
        this.includeUniqueConstraintCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	if( includeUniqueConstraintCB.getSelection() ) {
            		if( getRelationalReference().getUniqueContraint() == null ) {
            			RelationalUniqueConstraint key = new RelationalUniqueConstraint();
            			if( uniqueConstraintNameText.getText() != null ) {
            				key.setName(uniqueConstraintNameText.getText());
            			}
            			getRelationalReference().setUniqueConstraint(key);
            		}
            	} else {
            		getRelationalReference().setUniqueConstraint(null);
            	}
                handleInfoChanged();
            }
        });
        
        Label label = new Label(thePanel, SWT.NONE | SWT.RIGHT);
        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
        GridDataFactory.fillDefaults().applyTo(label);
        
        this.uniqueConstraintNameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.uniqueConstraintNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().applyTo(this.uniqueConstraintNameText);
        this.uniqueConstraintNameText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = uniqueConstraintNameText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
        		if( getRelationalReference().getUniqueContraint() != null ) {
        			RelationalUniqueConstraint key = getRelationalReference().getUniqueContraint();
        			key.setName(value);
        		}
    			
        		handleInfoChanged();
    		}
        });
        
        label = new Label(thePanel, SWT.NONE | SWT.RIGHT);
        label.setText(Messages.nameInSourceLabel);
        GridDataFactory.fillDefaults().applyTo(label);
        
        this.uniqueConstraintNISText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.uniqueConstraintNISText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().applyTo(this.uniqueConstraintNISText);
        this.uniqueConstraintNISText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = uniqueConstraintNISText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
        		if( getRelationalReference().getUniqueContraint() != null ) {
        			RelationalUniqueConstraint key = getRelationalReference().getUniqueContraint();
        			key.setNameInSource(value);
        		}
    			
        		handleInfoChanged();
    		}
        });
        
    	Composite buttonPanel = new Composite(thePanel, SWT.NONE);
    	GridLayoutFactory.fillDefaults().applyTo(buttonPanel);
    	GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).applyTo(buttonPanel);

    	this.changeUcColumnsButton = new Button(buttonPanel, SWT.PUSH);
    	this.changeUcColumnsButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.CHANGE_ELIPSIS));
    	GridDataFactory.fillDefaults().applyTo(this.changeUcColumnsButton);
    	this.changeUcColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		SelectColumnsDialog dialog = new SelectColumnsDialog(getShell(), getRelationalReference(), false);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		Collection<RelationalColumn> selectedColumns = dialog.getSelectedColumns();
	        		if( !selectedColumns.isEmpty() ) {
	        			getRelationalReference().getUniqueContraint().setColumns(selectedColumns);
	        		} else {
	        			getRelationalReference().getUniqueContraint().setColumns(Collections.EMPTY_LIST);
	        		}
	        	}
	        	handleInfoChanged();
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());

        this.ucColumnsViewer = new TableViewer(columnTable);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 100).applyTo(this.ucColumnsViewer.getControl());        
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.ucColumnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.columnNameLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();
        
        if( getRelationalReference() != null && getRelationalReference().getUniqueContraint() != null ) {
	        for( RelationalColumn row : getRelationalReference().getUniqueContraint().getColumns() ) {
	        	this.ucColumnsViewer.add(row);
	        }
        }

    	return thePanel;
	}
	
	private Composite createForeignKeysPanel(Composite parent) {
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        if(isRelationalView()) {
            Text fkHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
            fkHelpText.setBackground(parent.getBackground());
            fkHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(250, 100).span(2, 1).applyTo(fkHelpText);
            fkHelpText.setText(Messages.foreignKeysNotSupportedForViews);
        }

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
			}
    		
		});
    	
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
					deleteFKButton.setEnabled(false);
					handleInfoChanged();
				}
			}
    		
		});

    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());

        this.fkViewer = new TableViewer(columnTable);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 200).applyTo(this.fkViewer.getControl());

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.fkViewer, SWT.LEFT);
        column.getColumn().setText(Messages.fkNameLabel);
        column.setLabelProvider(new FKDataLabelProvider(0));
        column.getColumn().pack();
        
        if( getRelationalReference() != null) {
	        for( RelationalForeignKey row : getRelationalReference().getForeignKeys()) {
	        	this.fkViewer.add(row);
	        }
        }
        
        return thePanel;
	}
	
	private Composite createIndexesPanel(Composite parent) {
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        if(isRelationalView()) {
            Text indexesHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
            indexesHelpText.setBackground(parent.getBackground());
            indexesHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(250, 100).span(2, 1).applyTo(indexesHelpText);
            indexesHelpText.setText(Messages.indexesNotSupportedForViews);
        }

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
			}
    		
		});
    	
    	this.deleteIndexButton = new Button(buttonPanel, SWT.PUSH);
    	this.deleteIndexButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	GridDataFactory.fillDefaults().applyTo(this.deleteIndexButton);
    	this.deleteIndexButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalIndex index = null;
				
				IStructuredSelection selection = (IStructuredSelection)fkViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalIndex ) {
						index =  (RelationalIndex) obj;
						break;
					}
				}
				if( index != null ) {
					getRelationalReference().removeIndex(index);
					deleteIndexButton.setEnabled(false);
					handleInfoChanged();
				}
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());
    	
        this.indexesViewer = new TableViewer(columnTable);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 200).applyTo(this.indexesViewer.getControl());

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.indexesViewer, SWT.LEFT);
        column.getColumn().setText(Messages.indexLabel);
        column.setLabelProvider(new IndexDataLabelProvider(0));
        column.getColumn().pack();
        
        if( getRelationalReference() != null && getRelationalReference().getIndexes() != null ) {
	        for( RelationalIndex row : getRelationalReference().getIndexes() ) {
	        	this.indexesViewer.add(row);
	        }
        }

    	return thePanel;
	}
	
	/*
	 * Simple panel containing name, name in source values as well as a list of primary key columns from this table
	 */
	private Composite createNativeQueryPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

		nativeQueryHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
		nativeQueryHelpText.setBackground(parent.getBackground());
		nativeQueryHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(250, 100).applyTo(nativeQueryHelpText);
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

        Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 4);
        GridLayoutFactory.fillDefaults().numColumns(4).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

    	addColumnButton = new Button(buttonPanel, SWT.PUSH);
    	addColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	GridDataFactory.fillDefaults().applyTo(addColumnButton);
    	addColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		getRelationalReference().createColumn();
				handleInfoChanged();
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
					deleteColumnButton.setEnabled(false);
					handleInfoChanged();
				}
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
					downColumnButton.setEnabled(getRelationalReference().canMoveColumnDown(info));
					upColumnButton.setEnabled(getRelationalReference().canMoveColumnUp(info));
					
				}
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
					downColumnButton.setEnabled(getRelationalReference().canMoveColumnDown(info));
					upColumnButton.setEnabled(getRelationalReference().canMoveColumnUp(info));
					
				}
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());

        this.columnsViewer = new TableViewer(columnTable);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 200).applyTo(columnsViewer.getControl());

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.columnNameLabel + "          "); //$NON-NLS-1$
        column.setEditingSupport(new ColumnNameEditingSupport(this.columnsViewer));
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.dataTypeLabel + "          "); //$NON-NLS-1$
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        column.setEditingSupport(new DatatypeEditingSupport(this.columnsViewer));
        column.getColumn().pack();
        
        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.lengthLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(2));
        column.setEditingSupport(new ColumnWidthEditingSupport(this.columnsViewer));
        column.getColumn().pack();
        
    	
        if( getRelationalReference() != null ) {
	        for( RelationalColumn row : getRelationalReference().getColumns() ) {
	        	this.columnsViewer.add(row);
	        }
        }
        
        this.columnsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				
				if( sel.isEmpty()) {
					deleteColumnButton.setEnabled(false);
					upColumnButton.setEnabled(false);
					downColumnButton.setEnabled(false);
				} else {
					boolean enable = true;
					Object[] objs = sel.toArray();
					RelationalColumn columnInfo = null;
					for( Object obj : objs) {
						if(  !(obj instanceof RelationalColumn)) {
							enable = false;
							break;
						} else {
							columnInfo = (RelationalColumn)obj;
						}
					} 
					if( objs.length == 0 ) {
						enable = false;
					}
					deleteColumnButton.setEnabled(enable);
					if( enable ) {
						upColumnButton.setEnabled(getRelationalReference().canMoveColumnUp(columnInfo));
						downColumnButton.setEnabled(getRelationalReference().canMoveColumnDown(columnInfo));
					}
					
				}
				
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
    
    class ColumnNameEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer the column viewer
		 */
		public ColumnNameEditingSupport(ColumnViewer viewer) {
			super(viewer);
			this.editor = new TextCellEditor((Composite) viewer.getControl());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override
		protected Object getValue(Object element) {
			if( element instanceof RelationalColumn ) {
				return ((RelationalColumn)element).getName();
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			if( element instanceof RelationalColumn ) {
				String oldValue = ((RelationalColumn)element).getName();
				String newValue = (String)value;
				if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
					((RelationalColumn)element).setName(newValue);
					columnsViewer.refresh(element);
					handleInfoChanged();
				}
			}
		}

	}
    
    class ColumnWidthEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer the column viewer
		 */
		public ColumnWidthEditingSupport(ColumnViewer viewer) {
			super(viewer);
			this.editor = new TextCellEditor((Composite) viewer.getControl());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override
		protected Object getValue(Object element) {
			if( element instanceof RelationalColumn ) {
				return Integer.toString(((RelationalColumn)element).getLength());
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			if( element instanceof RelationalColumn ) {
				int oldValue = ((RelationalColumn)element).getLength();
				int newValue = oldValue;
				try {
					newValue = Integer.parseInt((String)value);
				} catch (NumberFormatException ex) {
					return;
				}
				if( newValue != oldValue ) {
					((RelationalColumn)element).setLength(newValue);
					columnsViewer.refresh(element);
				}
			}
		}

	}
    
    class DatatypeEditingSupport extends ComboBoxEditingSupport {
    	
    	private String[] datatypes;
        /**
         * @param viewer the column viewer
         */
        public DatatypeEditingSupport( ColumnViewer viewer ) {
            super(viewer);
            IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
    		Set<String> unsortedDatatypes = service.getAllDataTypeNames();
    		Collection<String> dTypes = new ArrayList<String>();
    		
    		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
    		Arrays.sort(sortedStrings);
    		for( String dType : sortedStrings ) {
    			dTypes.add(dType);
    		}
    		
    		datatypes = dTypes.toArray(new String[dTypes.size()]);
    		
        }


        @Override
        protected String getElementValue( Object element ) {
        	return ((RelationalColumn)element).getDatatype();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            ((RelationalColumn)element).setDatatype(newValue);
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
//					case 1: {
//						if(element instanceof RelationalForeignKey) {
//							return ((RelationalForeignKey)element).getDatatype();
//						}
//					}
//					case 2: {
//						if(element instanceof RelationalForeignKey) {
//							return Integer.toString(((RelationalForeignKey)element).getLength());
//						}
//					}
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
        
        private boolean isPrimaryKeyColumns = false;
            
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
            this.isPrimaryKeyColumns = isPrimaryKeyColumns;
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
    		
    		if( isPrimaryKeyColumns )  {
    			for( RelationalColumn col : this.theTable.getPrimaryKey().getColumns() ) {
    				for( TableItem item : columnDataViewer.getTable().getItems() ) {
    	        		if( item.getData() == col ) {
    	        			item.setChecked(true);
    	        		}
    	        	}
    			}
    		} else {
    			for( RelationalColumn col : this.theTable.getUniqueContraint().getColumns() ) {
    				for( TableItem item : columnDataViewer.getTable().getItems() ) {
    	        		if( item.getData() == col ) {
    	        			item.setChecked(true);
    	        		}
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
				return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
			}
			return null;
		}
		
		
	}
}
