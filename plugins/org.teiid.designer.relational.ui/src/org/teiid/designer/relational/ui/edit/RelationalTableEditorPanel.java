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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.swt.layout.GridLayout;
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
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	private List<String> MULTIPLICITY_LIST;

	private RelationalTable table;
	
	TabFolder tabFolder;
	TabItem generalPropertiesTab;
	TabItem columnsTab;
	TabItem primaryKeyTab;
	TabItem uniqueConstraintTab;
	TabItem foreignKeysTab;
	TabItem nativeQueryTab;
	TabItem	indexesTab;
	
	// table property widgets
	Button materializedCB, supportsUpdateCB, isSystemTableCB, includePrimaryKeyCB, includeUniqueConstraintCB;
	Button findTableReferenceButton;
	Label materializedTableLabel;
	Text helpText, modelNameText, nameText, nameInSourceText, 
		cardinalityText, materializedTableText, 
		primaryKeyNameText, uniqueConstraintNameText,
		primaryKeyNISText, uniqueConstraintNISText,
		nativeQueryHelpText,
		fkHelpText,
		ucHelpText,
		pkHelpText,
		indexesHelpText;
	StyledTextEditor descriptionTextEditor;
	StyledTextEditor nativeQueryTextEditor;
	
	// column widgets
	Button addColumnButton, deleteColumnButton, upColumnButton, downColumnButton;
	Button changePkColumnsButton, changeUcColumnsButton, addFKButton, editFKButton, deleteFKButton;
	Button addIndexButton, deleteIndexButton, editIndexButton;
	TableViewer columnsViewer;
	TableViewer pkColumnsViewer, ucColumnsViewer, fkViewer;
	TableViewer indexesViewer;
	
	boolean synchronizing = false;
	boolean processingChecks = false;
	
	boolean finishedStartup = false;

	/**
	 * @param parent the parent panel
	 * @param table the table object
	 * @param modelFile the model file
	 * @param statusListener the status listener
	 */
	public RelationalTableEditorPanel(Composite parent, RelationalTable table, IFile modelFile, IDialogStatusListener statusListener) {
		super(parent, table, modelFile, statusListener);
		this.table = table;
		
		MULTIPLICITY_LIST = new ArrayList<String>();
		for( String str : MULTIPLICITY.AS_ARRAY ) {
			MULTIPLICITY_LIST.add(str);
		}
		
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
		
		createGeneralPropertiesTab(tabFolder);
		createColumnsTab(tabFolder);
		createPrimaryKeyTab(tabFolder);
		createUniqueConstraintTab(tabFolder);
		createForeignKeysTab(tabFolder);
		createIndexesTab(tabFolder);
		createNativeQueryTab(tabFolder);
		
		finishedStartup = true;
		
	}
	
	Composite createNameGroup(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
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
        if( getModelFile() != null ) {
        	modelNameText.setText(getModelFile().getName());
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
    			
    			table.setName(value);
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
    			
    			table.setNameInSource(value);
    			handleInfoChanged();
    		}
        });
        
        return thePanel;
	}
	
	
	void createGeneralPropertiesTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.PROPERTIES));
        this.generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	
	void createColumnsTab(TabFolder folderParent) {
        Composite thePanel = createColumnTableGroup(folderParent);

        this.columnsTab = new TabItem(folderParent, SWT.NONE);
        this.columnsTab.setControl(thePanel);
        this.columnsTab.setText(Messages.columnsLabel);
        this.columnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	
	void createPrimaryKeyTab(TabFolder folderParent) {
        Composite thePanel = createPrimaryKeyPanel(folderParent);
        
        this.primaryKeyTab = new TabItem(folderParent, SWT.NONE);
        this.primaryKeyTab.setControl(thePanel);
        this.primaryKeyTab.setText(Messages.primaryKeyLabel);
        this.primaryKeyTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PK, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	
	void createUniqueConstraintTab(TabFolder folderParent) {
        Composite thePanel = createUniqueConstraintPanel(folderParent);
        
        this.uniqueConstraintTab = new TabItem(folderParent, SWT.NONE);
        this.uniqueConstraintTab.setControl(thePanel);
        this.uniqueConstraintTab.setText(Messages.uniqueConstraintLabel);
        this.uniqueConstraintTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.UC, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	
	void createForeignKeysTab(TabFolder folderParent) {
        Composite thePanel = createForeignKeysPanel(folderParent);

        this.foreignKeysTab = new TabItem(folderParent, SWT.NONE);
        this.foreignKeysTab.setControl(thePanel);
        this.foreignKeysTab.setText(Messages.foreignKeysLabel);
        this.foreignKeysTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.FK, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	void createIndexesTab(TabFolder folderParent) {
        Composite thePanel = createIndexesPanel(folderParent);
        
        this.indexesTab = new TabItem(folderParent, SWT.NONE);
        this.indexesTab.setControl(thePanel);
        this.indexesTab.setText(Messages.indexesLabel);
        this.indexesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.INDEX, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	void createNativeQueryTab(TabFolder folderParent) {
        Composite thePanel = createNativeQueryPanel(folderParent);
        
        this.nativeQueryTab = new TabItem(folderParent, SWT.NONE);
        this.nativeQueryTab.setControl(thePanel);
        this.nativeQueryTab.setText(Messages.nativeQueryLabel);
        //this.nativeQueryTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PK, ModelType.PHYSICAL, Status.OK_STATUS));

	}
	
	@Override
	protected void synchronizeUI() {
		if( synchronizing ) {
			return;
		}
		if( this.table == null ) {
			this.table = (RelationalTable)getRelationalReference();
		}
		synchronizing = true;
		
		if( table.getName() != null ) {
			if( WidgetUtil.widgetValueChanged(this.nameText, table.getName()) ) {
				this.nameText.setText(table.getName());
			}
		} else {
			if( WidgetUtil.widgetValueChanged(this.nameText, EMPTY_STRING) ) {
				this.nameText.setText(EMPTY_STRING);
			}
		}
		
    	this.helpText.setText(RelationalObjectEditorFactory.getHelpText(table));
    	
		if( table.getDescription() != null ) {
			if( !StringUtilities.equals(this.descriptionTextEditor.getText(), table.getDescription()) ) {
				this.descriptionTextEditor.setText(table.getDescription());
			}
		} else {
			this.descriptionTextEditor.setText(EMPTY_STRING);
		}
		
		if( table.getNameInSource() != null ) {
			if( WidgetUtil.widgetValueChanged(this.nameInSourceText, table.getNameInSource()) ) {
				this.nameInSourceText.setText(table.getNameInSource());
			}
		} else {
			if( WidgetUtil.widgetValueChanged(this.nameInSourceText, EMPTY_STRING) ) {
				this.nameInSourceText.setText(EMPTY_STRING);
			}
		}
		
		if( WidgetUtil.widgetValueChanged(this.cardinalityText, this.table.getCardinality()) ) {
			this.cardinalityText.setText(Integer.toString(this.table.getCardinality()));
		}
		
		boolean isMaterialized = this.table.isMaterialized();	
		if( WidgetUtil.widgetValueChanged(materializedCB, isMaterialized)) {
			this.materializedCB.setSelection(isMaterialized);
		}
		this.materializedTableText.setEnabled(isMaterialized);
		this.findTableReferenceButton.setEnabled(isMaterialized);
		
		if( WidgetUtil.widgetValueChanged(materializedCB, this.table.getSupportsUpdate())) {
			this.supportsUpdateCB.setSelection(this.table.getSupportsUpdate());
		}
		
		if( WidgetUtil.widgetValueChanged(isSystemTableCB, this.table.isSystem())) {
			this.isSystemTableCB.setSelection(this.table.isSystem());
		}
		generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, table.getModelType(), Status.OK_STATUS));
		
    	this.columnsViewer.getTable().removeAll();
    	IStatus maxStatus = Status.OK_STATUS;
        for( RelationalColumn row : table.getColumns() ) {
        	if( row.getStatus().getSeverity() > maxStatus.getSeverity() ) {
        		maxStatus = row.getStatus();
        	}
        	this.columnsViewer.add(row);
        }
        columnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, table.getModelType(), maxStatus));
        
        maxStatus = Status.OK_STATUS;
        this.fkViewer.getTable().removeAll();
        for( RelationalForeignKey row : this.table.getForeignKeys()) {
        	if( row.getStatus().getSeverity() > maxStatus.getSeverity() ) {
        		maxStatus = row.getStatus();
        	}
        	this.fkViewer.add(row);
        }
        foreignKeysTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.FK, table.getModelType(), maxStatus));
        
        if( this.table.getPrimaryKey() == null ) {
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
        	if( this.table.getPrimaryKey().getName() != null && WidgetUtil.widgetValueChanged(primaryKeyNameText, this.table.getPrimaryKey().getName())) {
        		this.primaryKeyNameText.setText(this.table.getPrimaryKey().getName());
        	}
        	this.primaryKeyNISText.setEnabled(true);
        	if( this.table.getPrimaryKey().getNameInSource() != null && WidgetUtil.widgetValueChanged(primaryKeyNISText, this.table.getPrimaryKey().getNameInSource())) {
        		this.primaryKeyNISText.setText(this.table.getPrimaryKey().getNameInSource());
        	}
        	this.pkColumnsViewer.getTable().removeAll();
        	if( !this.table.getPrimaryKey().getColumns().isEmpty() ) {
        		for( RelationalColumn column : this.table.getPrimaryKey().getColumns() ) {
        			this.pkColumnsViewer.add(column);
        		}
        	}
        	this.changePkColumnsButton.setEnabled(true);
        	primaryKeyTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PK, table.getModelType(), this.table.getPrimaryKey().getStatus()));
        }
        
        if( this.table.getUniqueContraint() == null ) {
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
        	if( this.table.getUniqueContraint().getName() != null && WidgetUtil.widgetValueChanged(uniqueConstraintNameText, this.table.getUniqueContraint().getName())) {
        		this.uniqueConstraintNameText.setText(this.table.getUniqueContraint().getName());
        	}
        	this.uniqueConstraintNISText.setEnabled(true);
        	if( this.table.getUniqueContraint().getNameInSource() != null && WidgetUtil.widgetValueChanged(uniqueConstraintNISText, this.table.getUniqueContraint().getNameInSource())) {
        		this.uniqueConstraintNISText.setText(this.table.getUniqueContraint().getName());
        	}		if( table.getDescription() != null ) {
    			if( !StringUtilities.equals(this.descriptionTextEditor.getText(), table.getDescription()) ) {
    				this.descriptionTextEditor.setText(table.getDescription());
    			}
    		} else {
    			this.descriptionTextEditor.setText(EMPTY_STRING);
    		}
    		
        	this.ucColumnsViewer.getTable().removeAll();
        	if( !this.table.getUniqueContraint().getColumns().isEmpty() ) {
        		for( RelationalColumn column : this.table.getUniqueContraint().getColumns() ) {
        			this.ucColumnsViewer.add(column);
        		}
        	}
        	this.changeUcColumnsButton.setEnabled(true);
        	uniqueConstraintTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.UC, table.getModelType(), this.table.getUniqueContraint().getStatus()));
        }
        
        maxStatus = Status.OK_STATUS;
        this.indexesViewer.getTable().removeAll();
        for( RelationalIndex row : this.table.getIndexes()) {
        	if( row.getStatus().getSeverity() > maxStatus.getSeverity() ) {
        		maxStatus = row.getStatus();
        	}
        	this.indexesViewer.add(row);
        }
        indexesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.INDEX, table.getModelType(), maxStatus));
        
		if( table.getNativeQuery() != null ) {
			if( !StringUtilities.equals(this.nativeQueryTextEditor.getText(), table.getNativeQuery()) ) {
				this.nativeQueryTextEditor.setText(table.getNativeQuery());
			}
		} else {
			this.nativeQueryTextEditor.setText(EMPTY_STRING);
		}
		
        
        if( table.getModelType() == ModelType.PHYSICAL ) {
        	this.materializedCB.setVisible(false);
        	this.materializedTableLabel.setVisible(false);
        	this.materializedTableText.setVisible(false);
        	this.findTableReferenceButton.setVisible(false);
        }
        
        if( finishedStartup ) {
	        setNativeQueryEnablement( !(table instanceof RelationalView) );
	        setKeyTabsEnablement(!(table instanceof RelationalView));
        }

		synchronizing = false;
	}
	
	private void setNativeQueryEnablement(boolean enable) {
		this.nativeQueryTab.getControl().setEnabled(enable);
		if( enable ) {
			this.nativeQueryHelpText.setText(Messages.nativeQueryHelpText);
		} else {
			this.nativeQueryHelpText.setText(Messages.nativeQueryNotSupportedForViews);
		}
		this.nativeQueryHelpText.setEnabled(enable);
		this.nativeQueryTextEditor.getTextWidget().setEnabled(enable);
		if( enable ) {
			nativeQueryTextEditor.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		} else {
			nativeQueryTextEditor.getTextWidget().setBackground(nativeQueryTextEditor.getTextWidget().getParent().getBackground());
		}
	}
	
	private void setKeyTabsEnablement(boolean enable) {
		this.foreignKeysTab.getControl().setEnabled(enable);
		this.primaryKeyTab.getControl().setEnabled(enable);
		this.uniqueConstraintTab.getControl().setEnabled(enable);
		addFKButton.setEnabled(enable);
		editFKButton.setEnabled(enable);
		deleteFKButton.setEnabled(enable);
		includePrimaryKeyCB.setEnabled(enable);
		includeUniqueConstraintCB.setEnabled(enable);
		addIndexButton.setEnabled(enable);
		editIndexButton.setEnabled(enable);
		deleteIndexButton.setEnabled(enable);
		
		if( enable ) {
			fkHelpText.setText(EMPTY_STRING);
			pkHelpText.setText(EMPTY_STRING);
			ucHelpText.setText(EMPTY_STRING);
			indexesHelpText.setText(EMPTY_STRING);
		} else {
	    	fkHelpText.setText(Messages.foreignKeysNotSupportedForViews);
	    	pkHelpText.setText(Messages.primaryKeysNotSupportedForViews);
	    	ucHelpText.setText(Messages.uniqueConstraintsNotSupportedForViews);
	    	indexesHelpText.setText(Messages.indexesNotSupportedForViews);
		}
	}
	
	@SuppressWarnings("unused")
	Composite createPropertiesPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
		thePanel.setLayout(new GridLayout(3, false));
		GridData panelGD = new GridData(GridData.FILL_BOTH);
//		panelGD.heightHint = 300;
    	thePanel.setLayoutData(panelGD);
    	
        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.cardinalityLabel);
        
        this.cardinalityText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.cardinalityText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.cardinalityText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.cardinalityText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			if( !synchronizing ) {
	    			if( !cardinalityText.getText().isEmpty()) {
	            		try {	
	        				int value = Integer.parseInt(cardinalityText.getText());
	        				table.setCardinality(value);
	        				handleInfoChanged();
	        			} catch (NumberFormatException ex) {
	        				MessageDialog.openError(
	        						tabFolder.getShell(), 
	        						Messages.cardinalityErrorTitle, 
	        						Messages.cardinalityMustBeAnInteger);
	        				return;
	        			}
	            	}
    			}
    		}
        });
        addSpacerLabels(thePanel, 1);
        
        this.supportsUpdateCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        this.supportsUpdateCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        this.supportsUpdateCB.setText(Messages.supportsUpdateLabel);
        this.supportsUpdateCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	table.setSupportsUpdate(supportsUpdateCB.getSelection());
                handleInfoChanged();
            }
        });
        
        addSpacerLabels(thePanel, 2);
        
        this.isSystemTableCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        this.isSystemTableCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        this.isSystemTableCB.setText(Messages.systemTableLabel);
        this.isSystemTableCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	table.setSystem(isSystemTableCB.getSelection());
                handleInfoChanged();
            }
        });
        
        addSpacerLabels(thePanel, 2);
        
        
        DESCRIPTION_GROUP: {
            final Group descGroup = WidgetFactory.createGroup(thePanel, UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION), GridData.FILL_BOTH, 3);
            descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
            final GridData descGridData = new GridData(GridData.FILL_BOTH);
            descGridData.horizontalSpan = 1;
            descGridData.heightHint = 100;
            descGridData.minimumHeight = 30;
            descGridData.grabExcessVerticalSpace = true;
            descriptionTextEditor.setLayoutData(descGridData);
            descriptionTextEditor.setText(""); //$NON-NLS-1$
            descriptionTextEditor.getTextWidget().addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					table.setDescription(descriptionTextEditor.getText());
				}
			});
        }
        
        MATERIALIZED_GROUP : {
	        this.materializedCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
	        this.materializedCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	        this.materializedCB.setText(Messages.materializedLabel);
	        this.materializedCB.addSelectionListener(new SelectionAdapter() {
	            /**            		
	             * {@inheritDoc}
	             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	             */
	            @Override
	            public void widgetSelected( SelectionEvent e ) {
	            	table.setMaterialized(materializedCB.getSelection());
	            	if( !materializedCB.getSelection() ) {
	            		table.setMaterializedTable(null);
	            	}
	                handleInfoChanged();
	            }
	        });
	        addSpacerLabels(thePanel, 2);

	        materializedTableLabel = new Label(thePanel, SWT.NONE | SWT.RIGHT);
	        materializedTableLabel.setText(Messages.tableReferenceLabel);
	        materializedTableLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	        
	        this.materializedTableText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
	        this.materializedTableText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	        this.materializedTableText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        
	        this.findTableReferenceButton = new Button(thePanel, SWT.PUSH);
	        this.findTableReferenceButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS));
	        this.findTableReferenceButton.setLayoutData(new GridData());
	        this.findTableReferenceButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					
				}
				
			});
        }
   
        return thePanel;
	}
	
	/*
	 * Simple panel containing name, name in source values as well as a list of primary key columns from this table
	 */
	Composite createPrimaryKeyPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		thePanel.setLayout(new GridLayout(2, false));
    	thePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
		new Label(parent, SWT.NONE);
		{
	    	pkHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
	    	pkHelpText.setBackground(parent.getBackground());
	    	pkHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	    	pkHelpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    	((GridData)pkHelpText.getLayoutData()).horizontalSpan = 2;
	    	((GridData)pkHelpText.getLayoutData()).heightHint = 20;
	    	((GridData)pkHelpText.getLayoutData()).widthHint = 360;
		}
    	
        this.includePrimaryKeyCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridData theGridData = new GridData();
        theGridData.horizontalSpan = 2;
        this.includePrimaryKeyCB.setLayoutData(theGridData);
        this.includePrimaryKeyCB.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.INCLUDE));
        this.includePrimaryKeyCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	if( includePrimaryKeyCB.getSelection() ) {
            		if( table.getPrimaryKey() == null ) {
            			RelationalPrimaryKey key = new RelationalPrimaryKey();
            			if( primaryKeyNameText.getText() != null ) {
            				key.setName(primaryKeyNameText.getText());
            			}
            			table.setPrimaryKey(key);
            		}
            	} else {
            		table.setPrimaryKey(null);
            	}
                handleInfoChanged();
            }
        });
        
        Label label = new Label(thePanel, SWT.NONE | SWT.RIGHT);
        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
        label.setLayoutData(new GridData());
        
        this.primaryKeyNameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.primaryKeyNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.primaryKeyNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.primaryKeyNameText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = primaryKeyNameText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
        		if( table.getPrimaryKey() != null ) {
        			RelationalPrimaryKey key = table.getPrimaryKey();
        			key.setName(value);
        		}
        		handleInfoChanged();
    		}
        });
        
        label = new Label(thePanel, SWT.NONE | SWT.RIGHT);
        label.setText(Messages.nameInSourceLabel);
        label.setLayoutData(new GridData());
        
        this.primaryKeyNISText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.primaryKeyNISText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.primaryKeyNISText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.primaryKeyNISText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = primaryKeyNISText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
        		if( table.getPrimaryKey() != null ) {
        			RelationalPrimaryKey key = table.getPrimaryKey();
        			key.setNameInSource(value);
        		}
    			
        		handleInfoChanged();
    		}
        });
        
    	Composite leftToolbarPanel = new Composite(thePanel, SWT.NONE);
    	leftToolbarPanel.setLayout(new GridLayout());
	  	GridData ltpGD = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	  	leftToolbarPanel.setLayoutData(ltpGD);
	  	
	  	this.changePkColumnsButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.changePkColumnsButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.CHANGE_ELIPSIS));
    	this.changePkColumnsButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	this.changePkColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		SelectColumnsDialog dialog = new SelectColumnsDialog(tabFolder.getShell(), table, true);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		Collection<RelationalColumn> selectedColumns = dialog.getSelectedColumns();
	        		if( !selectedColumns.isEmpty() ) {
	        			table.getPrimaryKey().setColumns(selectedColumns);
	        		} else {
	        			table.getPrimaryKey().setColumns(Collections.EMPTY_LIST);
	        		}
	        	}
	        	handleInfoChanged();
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());
    	columnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    	
        this.pkColumnsViewer = new TableViewer(columnTable);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        this.pkColumnsViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.pkColumnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.columnNameLabel);
        //column.setEditingSupport(new ColumnNameEditingSupport(this.pkColumnsViewer));
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();
        
        if( this.table != null && this.table.getPrimaryKey() != null ) {
	        for( RelationalColumn row : this.table.getPrimaryKey().getColumns() ) {
	        	this.columnsViewer.add(row);
	        }
        }
        //LayoutDebugger.debugLayout(theGroup);
    	
    	return thePanel;
	}
	
	Composite createUniqueConstraintPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		thePanel.setLayout(new GridLayout(2, false));
    	thePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
		new Label(parent, SWT.NONE);
		{
	    	ucHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
	    	ucHelpText.setBackground(parent.getBackground());
	    	ucHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	    	ucHelpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    	((GridData)ucHelpText.getLayoutData()).horizontalSpan = 2;
	    	((GridData)ucHelpText.getLayoutData()).heightHint = 20;
	    	((GridData)ucHelpText.getLayoutData()).widthHint = 360;
		}
    	
        this.includeUniqueConstraintCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridData theGridData = new GridData();
        theGridData.horizontalSpan = 2;
        this.includeUniqueConstraintCB.setLayoutData(theGridData);
        this.includeUniqueConstraintCB.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.INCLUDE));
        this.includeUniqueConstraintCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	if( includeUniqueConstraintCB.getSelection() ) {
            		if( table.getUniqueContraint() == null ) {
            			RelationalUniqueConstraint key = new RelationalUniqueConstraint();
            			if( uniqueConstraintNameText.getText() != null ) {
            				key.setName(uniqueConstraintNameText.getText());
            			}
            			table.setUniqueConstraint(key);
            		}
            	} else {
            		table.setUniqueConstraint(null);
            	}
                handleInfoChanged();
            }
        });
        
        Label label = new Label(thePanel, SWT.NONE | SWT.RIGHT);
        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
        label.setLayoutData(new GridData());
        
        this.uniqueConstraintNameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.uniqueConstraintNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.uniqueConstraintNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.uniqueConstraintNameText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = uniqueConstraintNameText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
        		if( table.getUniqueContraint() != null ) {
        			RelationalUniqueConstraint key = table.getUniqueContraint();
        			key.setName(value);
        		}
    			
        		handleInfoChanged();
    		}
        });
        
        label = new Label(thePanel, SWT.NONE | SWT.RIGHT);
        label.setText(Messages.nameInSourceLabel);
        label.setLayoutData(new GridData());
        
        this.uniqueConstraintNISText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.uniqueConstraintNISText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.uniqueConstraintNISText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.uniqueConstraintNISText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = uniqueConstraintNISText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
        		if( table.getUniqueContraint() != null ) {
        			RelationalUniqueConstraint key = table.getUniqueContraint();
        			key.setNameInSource(value);
        		}
    			
        		handleInfoChanged();
    		}
        });
        
    	Composite leftToolbarPanel = new Composite(thePanel, SWT.NONE);
    	leftToolbarPanel.setLayout(new GridLayout());
	  	GridData ltpGD = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	  	//ltpGD.heightHint=150;
	  	leftToolbarPanel.setLayoutData(ltpGD);
	  	
    	this.changeUcColumnsButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.changeUcColumnsButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.CHANGE_ELIPSIS));
    	this.changeUcColumnsButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	this.changeUcColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		SelectColumnsDialog dialog = new SelectColumnsDialog(tabFolder.getShell(), table, false);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		Collection<RelationalColumn> selectedColumns = dialog.getSelectedColumns();
	        		if( !selectedColumns.isEmpty() ) {
	        			table.getUniqueContraint().setColumns(selectedColumns);
	        		} else {
	        			table.getUniqueContraint().setColumns(Collections.EMPTY_LIST);
	        		}
	        	}
	        	handleInfoChanged();
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());
    	columnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    	
        this.ucColumnsViewer = new TableViewer(columnTable);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        this.ucColumnsViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.ucColumnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.columnNameLabel);
        //column.setEditingSupport(new ColumnNameEditingSupport(this.ucColumnsViewer));
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();
        
        if( this.table != null && this.table.getUniqueContraint() != null ) {
	        for( RelationalColumn row : this.table.getUniqueContraint().getColumns() ) {
	        	this.ucColumnsViewer.add(row);
	        }
        }
        
        //LayoutDebugger.debugLayout(theGroup);
    	
    	return thePanel;
	}
	
	Composite createForeignKeysPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		thePanel.setLayout(new GridLayout(2, false));
    	thePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
		new Label(parent, SWT.NONE);
		{
	    	fkHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
	    	fkHelpText.setBackground(parent.getBackground());
	    	fkHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	    	fkHelpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    	((GridData)fkHelpText.getLayoutData()).horizontalSpan = 2;
	    	((GridData)fkHelpText.getLayoutData()).heightHint = 20;
	    	((GridData)fkHelpText.getLayoutData()).widthHint = 360;
		}
    	
    	
    	// Create 1 panels
    	// Top is just a Table of current FK with Add/Edit/Delete buttons
    	
    	// Bottom panel is the "Edit
        
    	Composite leftToolbarPanel = new Composite(thePanel, SWT.NONE);
    	leftToolbarPanel.setLayout(new GridLayout());
	  	GridData ltpGD = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	  	//ltpGD.heightHint=150;
	  	leftToolbarPanel.setLayoutData(ltpGD);
	  	
    	this.addFKButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.addFKButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	this.addFKButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	this.addFKButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalForeignKey newFK = new RelationalForeignKey();
				
				EditForeignKeyDialog dialog = new EditForeignKeyDialog(tabFolder.getShell(), getModelFile(), table, newFK, false);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		table.addForeignKey(newFK);
	        	}
	        	handleInfoChanged();
			}
    		
		});
    	
    	this.editFKButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.editFKButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.EDIT_ELIPSIS));
    	this.editFKButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					
					EditForeignKeyDialog dialog = new EditForeignKeyDialog(tabFolder.getShell(), getModelFile(), table, fk, true);
		        	
		        	int result = dialog.open();
		        	if( result == Window.OK) {
		        		// TODO:  inject info from tempFK if dialog isn't cancelled
		        	}
		        	handleInfoChanged();
				}
			}
    		
		});
    	
    	this.deleteFKButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.deleteFKButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	this.deleteFKButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					table.removeForeignKey(fk);
					deleteFKButton.setEnabled(false);
					handleInfoChanged();
				}
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());
    	columnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    	
        this.fkViewer = new TableViewer(columnTable);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        this.fkViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.fkViewer, SWT.LEFT);
        column.getColumn().setText(Messages.fkNameLabel);
        //column.setEditingSupport(new ColumnNameEditingSupport(this.ucColumnsViewer));
        column.setLabelProvider(new FKDataLabelProvider(0));
        column.getColumn().pack();
        
        if( this.table != null) {
	        for( RelationalForeignKey row : this.table.getForeignKeys()) {
	        	this.fkViewer.add(row);
	        }
        }
        
        return thePanel;
	}
	
	Composite createIndexesPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		thePanel.setLayout(new GridLayout(2, false));
    	thePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
		new Label(parent, SWT.NONE);
		{
			indexesHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
			indexesHelpText.setBackground(parent.getBackground());
			indexesHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
			indexesHelpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    	((GridData)indexesHelpText.getLayoutData()).horizontalSpan = 2;
	    	((GridData)indexesHelpText.getLayoutData()).heightHint = 20;
	    	((GridData)indexesHelpText.getLayoutData()).widthHint = 360;
		}
        
    	Composite leftToolbarPanel = new Composite(thePanel, SWT.NONE);
    	leftToolbarPanel.setLayout(new GridLayout());
	  	GridData ltpGD = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	  	//ltpGD.heightHint=150;
	  	leftToolbarPanel.setLayoutData(ltpGD);
	  	
    	this.addIndexButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.addIndexButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	this.addIndexButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	this.addIndexButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalIndex newIndex = new RelationalIndex();
				
				EditIndexDialog dialog = new EditIndexDialog(tabFolder.getShell(), table, newIndex, false);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		table.addIndex(newIndex);
	        	}
	        	handleInfoChanged();
			}
    		
		});
    	
    	this.editIndexButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.editIndexButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.EDIT_ELIPSIS));
    	this.editIndexButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					
					EditIndexDialog dialog = new EditIndexDialog(tabFolder.getShell(), table, index, true);
		        	
		        	int result = dialog.open();
		        	if( result == Window.OK) {
		        		//
		        	}
		        	handleInfoChanged();
				}
			}
    		
		});
    	
    	this.deleteIndexButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.deleteIndexButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	this.deleteIndexButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					table.removeIndex(index);
					deleteIndexButton.setEnabled(false);
					handleInfoChanged();
				}
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());
    	columnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    	
        this.indexesViewer = new TableViewer(columnTable);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        this.indexesViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.indexesViewer, SWT.LEFT);
        column.getColumn().setText(Messages.indexLabel);
        //column.setEditingSupport(new ColumnNameEditingSupport(this.ucColumnsViewer));
        column.setLabelProvider(new IndexDataLabelProvider(0));
        column.getColumn().pack();
        
        if( this.table != null && this.table.getIndexes() != null ) {
	        for( RelationalIndex row : this.table.getIndexes() ) {
	        	this.indexesViewer.add(row);
	        }
        }
        
        //LayoutDebugger.debugLayout(theGroup);
    	
    	return thePanel;
	}
	
	/*
	 * Simple panel containing name, name in source values as well as a list of primary key columns from this table
	 */
	@SuppressWarnings("unused")
	Composite createNativeQueryPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
		thePanel.setLayout(new GridLayout(2, false));
		GridData panelGD = new GridData(GridData.FILL_BOTH);
		//panelGD.heightHint = 300;
    	thePanel.setLayoutData(panelGD);
    	
		new Label(parent, SWT.NONE);
		{
	    	nativeQueryHelpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
	    	nativeQueryHelpText.setBackground(parent.getBackground());//Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
	    	nativeQueryHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	    	nativeQueryHelpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    	((GridData)nativeQueryHelpText.getLayoutData()).horizontalSpan = 1;
	    	((GridData)nativeQueryHelpText.getLayoutData()).heightHint = 50;
	    	((GridData)nativeQueryHelpText.getLayoutData()).widthHint = 360;
	    	nativeQueryHelpText.setText(Messages.nativeQueryHelpText);
		}
    	
        NATIVE_QUERY_GROUP: {
            final Group descGroup = WidgetFactory.createGroup(thePanel, Messages.sqlLabel, GridData.FILL_BOTH, 3);
            nativeQueryTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
            final GridData theGridData = new GridData(GridData.FILL_BOTH);
            theGridData.horizontalSpan = 1;
            theGridData.grabExcessVerticalSpace = true;
            nativeQueryTextEditor.setLayoutData(theGridData);
            nativeQueryTextEditor.setText(""); //$NON-NLS-1$
            nativeQueryTextEditor.getTextWidget().addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					table.setNativeQuery(nativeQueryTextEditor.getText());
					handleInfoChanged();
				}
			});
        }
    	
    	return thePanel;
	}
	
	private void addSpacerLabels(Composite parent, int numSpacers) {
		for( int i=0; i<numSpacers; i++ ) {
			new Label(parent, SWT.NONE);
		}
	}
	
	Composite createColumnTableGroup(Composite parent) {
		  	
	  	Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
	  	thePanel.setLayout(new GridLayout(1, false));
	  	GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
	  	groupGD.heightHint=300;
	  	thePanel.setLayoutData(groupGD);
	  	
	  	Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 4);
	  	buttonPanel.setLayout(new GridLayout(4, false));
	  	GridData panelGD = new GridData();
	  	buttonPanel.setLayoutData(panelGD);
	  	
    	addColumnButton = new Button(buttonPanel, SWT.PUSH);
    	addColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	addColumnButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	addColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		table.createColumn();
				handleInfoChanged();
			}
    		
		});
    	
    	deleteColumnButton = new Button(buttonPanel, SWT.PUSH);
    	deleteColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	deleteColumnButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					table.removeColumn(column);
					deleteColumnButton.setEnabled(false);
					handleInfoChanged();
				}
			}
    		
		});
    	
    	upColumnButton = new Button(buttonPanel, SWT.PUSH);
    	upColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.MOVE_UP));
    	upColumnButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					table.moveColumnUp(info);
					handleInfoChanged();
					columnsViewer.getTable().select(selectedIndex-1);
					downColumnButton.setEnabled(table.canMoveColumnDown(info));
					upColumnButton.setEnabled(table.canMoveColumnUp(info));
					
				}
			}
    		
		});
    	
    	downColumnButton = new Button(buttonPanel, SWT.PUSH);
    	downColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.MOVE_DOWN));
    	downColumnButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					table.moveColumnDown(info);
					handleInfoChanged();
					columnsViewer.getTable().select(selectedIndex+1);
					downColumnButton.setEnabled(table.canMoveColumnDown(info));
					upColumnButton.setEnabled(table.canMoveColumnUp(info));
					
				}
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());
    	columnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    	
        this.columnsViewer = new TableViewer(columnTable);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        this.columnsViewer.getControl().setLayoutData(data);
        
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
        
    	
        if( this.table != null ) {
	        for( RelationalColumn row : this.table.getColumns() ) {
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
						upColumnButton.setEnabled(table.canMoveColumnUp(columnInfo));
						downColumnButton.setEnabled(table.canMoveColumnDown(columnInfo));
					}
					
				}
				
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
		this.table.validate();
		
		setCanFinish(this.table.nameIsValid());
		
		IStatus currentStatus = this.table.getStatus();
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
            GridLayout gridLayout = new GridLayout();
            composite.setLayout(gridLayout);
            gridLayout.numColumns = 1;
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.grabExcessHorizontalSpace = true;
            gridData.widthHint = 500;
            composite.setLayoutData(gridData);
            
        	Group columnsGroup = WidgetFactory.createGroup(composite, Messages.selectColumnsTitle, SWT.NONE, 1, 2);
        	columnsGroup.setLayout(new GridLayout(2, false));
        	GridData gd = new GridData(GridData.FILL_BOTH);
        	gd.heightHint = 280;
        	gd.widthHint = 500;
        	columnsGroup.setLayoutData(gd);
        	
    		Table table = new Table(columnsGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
    		table.setHeaderVisible(false);
    		table.setLinesVisible(true);
    		table.setLayout(new TableLayout());
    		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    		this.columnDataViewer = new TableViewer(table);
    		gd = new GridData(GridData.FILL_BOTH);
    		gd.heightHint = 160;
    		gd.horizontalSpan = 2;
    		this.columnDataViewer.getControl().setLayoutData(gd);
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
