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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import org.teiid.core.types.DataTypeManager;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalForeignKey;
import org.teiid.designer.relational.model.RelationalPrimaryKey;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalUniqueConstraint;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.UniqueConstraint;
import com.metamatrix.metamodels.relational.util.RelationalUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.relational.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.table.ComboBoxEditingSupport;
import com.metamatrix.ui.text.StyledTextEditor;

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
	
	// table property widgets
	Button materializedCB, supportsUpdateCB, isSystemTableCB, includePrimaryKeyCB, includeUniqueConstraintCB;
	Button findTableReferenceButton;
	Label materializedTableLabel;
	Text modelNameText, nameText, nameInSourceText, 
		cardinalityText, materializedTableText, 
		primaryKeyNameText, uniqueConstraintNameText,
		primaryKeyNISText, uniqueConstraintNISText;
	StyledTextEditor descriptionTextEditor;
	
	// column widgets
	Button addColumnButton, deleteColumnButton, upColumnButton, downColumnButton;
	Button changePkColumnsButton, changeUcColumnsButton, addFKButton, editFKButton, deleteFKButton;
	TableViewer columnsViewer;
	TableViewer pkColumnsViewer, ucColumnsViewer, fkViewer;
	
	boolean synchronizing = false;
	boolean processingChecks = false;

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
	    	Text helpText = new Text(parent, SWT.WRAP | SWT.READ_ONLY);
	    	helpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
	    	helpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	    	helpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    	((GridData)helpText.getLayoutData()).horizontalSpan = 1;
	    	((GridData)helpText.getLayoutData()).heightHint = 40;
	    	((GridData)helpText.getLayoutData()).widthHint = 360;
	    	helpText.setText(Messages.createRelationalTableHelpText);
		}
		tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createGeneralPropertiesTab(tabFolder);
		createColumnsTab(tabFolder);
		createPrimaryKeyTab(tabFolder);
		createUniqueConstraintTab(tabFolder);
		createForeignKeysTab(tabFolder);
		
	}
	
	
	void createGeneralPropertiesTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createTablePanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(Messages.propertiesLabel);
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
		
		if( table.getNameInSource() != null ) {
			if( WidgetUtil.widgetValueChanged(this.nameText, table.getNameInSource()) ) {
				this.nameInSourceText.setText(table.getNameInSource());
			}
		} else {
			if( WidgetUtil.widgetValueChanged(this.nameText, EMPTY_STRING) ) {
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
		generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, table.getModelType(), table.getStatus()));
		
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
        
        if( table.getModelType() == ModelType.PHYSICAL ) {
        	this.materializedCB.setVisible(false);
        	this.materializedTableLabel.setVisible(false);
        	this.materializedTableText.setVisible(false);
        	this.findTableReferenceButton.setVisible(false);
        }

		synchronizing = false;
	}
	
	@SuppressWarnings("unused")
	Composite createTablePanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
		thePanel.setLayout(new GridLayout(3, false));
		GridData panelGD = new GridData(GridData.FILL_BOTH);
		panelGD.heightHint = 300;
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
        addSpacerLabels(thePanel, 1);
        
        label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.nameLabel);
        
        this.nameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.nameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.nameText.addModifyListener(new ModifyListener() {
    		public void modifyText( final ModifyEvent event ) {
    			String value = nameText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
    			
    			table.setName(value);
    			handleInfoChanged();
    		}
        });
        addSpacerLabels(thePanel, 1);
        
        
        label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.nameInSourceLabel);
        
        this.nameInSourceText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.nameInSourceText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.nameInSourceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.nameInSourceText.addModifyListener(new ModifyListener() {
    		public void modifyText( final ModifyEvent event ) {
    			String value = nameInSourceText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
    			
    			table.setNameInSource(value);
    			handleInfoChanged();
    		}
        });
        addSpacerLabels(thePanel, 1);
        
        label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.cardinalityLabel);
        
        this.cardinalityText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.cardinalityText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.cardinalityText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.cardinalityText.addModifyListener(new ModifyListener() {
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
        
        DESCRIPTION_GROUP: {
            final Group descGroup = WidgetFactory.createGroup(thePanel, Messages.descriptionLabel, GridData.FILL_HORIZONTAL, 3);
            descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
            final GridData descGridData = new GridData(GridData.FILL_BOTH);
            descGridData.horizontalSpan = 1;
            descGridData.heightHint = 80;
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
        
        {
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
	        this.findTableReferenceButton.setText(Messages.elipsisLabel);
	        this.findTableReferenceButton.setLayoutData(new GridData());
	        this.findTableReferenceButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					
				}
				
			});
        }


        addSpacerLabels(thePanel, 3);
        addSpacerLabels(thePanel, 3);
        
        return thePanel;
	}
	
	/*
	 * Simple panel containing name, name in source values as well as a list of primary key columns from this table
	 */
	Composite createPrimaryKeyPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		thePanel.setLayout(new GridLayout(2, false));
    	thePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
        this.includePrimaryKeyCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridData theGridData = new GridData();
        theGridData.horizontalSpan = 2;
        this.includePrimaryKeyCB.setLayoutData(theGridData);
        this.includePrimaryKeyCB.setText(Messages.includeLabel);
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
        label.setText(Messages.nameLabel);
        label.setLayoutData(new GridData());
        
        this.primaryKeyNameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.primaryKeyNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.primaryKeyNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.primaryKeyNameText.addModifyListener(new ModifyListener() {
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
    	this.changePkColumnsButton.setText(Messages.changeLabel);
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
    	
        this.includeUniqueConstraintCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridData theGridData = new GridData();
        theGridData.horizontalSpan = 2;
        this.includeUniqueConstraintCB.setLayoutData(theGridData);
        this.includeUniqueConstraintCB.setText(Messages.includeLabel);
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
        label.setText(Messages.nameLabel);
        label.setLayoutData(new GridData());
        
        this.uniqueConstraintNameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.uniqueConstraintNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.uniqueConstraintNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.uniqueConstraintNameText.addModifyListener(new ModifyListener() {
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
    	this.changeUcColumnsButton.setText(Messages.changeLabel);
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
    	
    	// Create 1 panels
    	// Top is just a Table of current FK with Add/Edit/Delete buttons
    	
    	// Bottom panel is the "Edit
        
    	Composite leftToolbarPanel = new Composite(thePanel, SWT.NONE);
    	leftToolbarPanel.setLayout(new GridLayout());
	  	GridData ltpGD = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	  	//ltpGD.heightHint=150;
	  	leftToolbarPanel.setLayoutData(ltpGD);
	  	
    	this.addFKButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.addFKButton.setText(Messages.addLabel);
    	this.addFKButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	this.addFKButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalForeignKey newFK = new RelationalForeignKey();
				
				EditForeignKeyDialog dialog = new EditForeignKeyDialog(tabFolder.getShell(), modelFile, table, newFK, false);
	        	
	        	int result = dialog.open();
	        	if( result == Window.OK) {
	        		table.addForeignKey(newFK);
	        	}
	        	handleInfoChanged();
			}
    		
		});
    	
    	this.editFKButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.editFKButton.setText(Messages.editLabel);
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
					
					EditForeignKeyDialog dialog = new EditForeignKeyDialog(tabFolder.getShell(), modelFile, table, fk, true);
		        	
		        	int result = dialog.open();
		        	if( result == Window.OK) {
		        		// TODO:  inject info from tempFK if dialog isn't cancelled
		        	}
		        	handleInfoChanged();
				}
			}
    		
		});
    	
    	this.deleteFKButton = new Button(leftToolbarPanel, SWT.PUSH);
    	this.deleteFKButton.setText(Messages.deleteLabel);
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
    	addColumnButton.setText(Messages.addLabel);
    	addColumnButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	addColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		table.createColumn();
				handleInfoChanged();
			}
    		
		});
    	
    	deleteColumnButton = new Button(buttonPanel, SWT.PUSH);
    	deleteColumnButton.setText(Messages.deleteLabel);
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
    	upColumnButton.setText(Messages.moveUpLabel);
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
    	downColumnButton.setText(Messages.moveDownLabel);
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
						if(element instanceof RelationalColumn) {
							return ((RelationalColumn)element).getName();
						}
					}
					case 1: {
						if(element instanceof RelationalColumn) {
							return ((RelationalColumn)element).getDatatype();
						}
					}
					case 2: {
						if(element instanceof RelationalColumn) {
							return Integer.toString(((RelationalColumn)element).getLength());
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
    
    class ColumnNameEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer
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
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
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
		 * @param viewer
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
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
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
         * @param viewer
         */
        public DatatypeEditingSupport( ColumnViewer viewer ) {
            super(viewer);
    		Set<String> unsortedDatatypes = DataTypeManager.getAllDataTypeNames();
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
         * ParsedDataRowDialog constructor.
         * 
         * @param parent   parent of this dialog
         * @param fileInfo the flat file business object
         * @param stringToParse the data string to parse
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
    
    /**
		try {
            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
            String message = UiConstants.Util.getString("EditTransformationHelper.dialogMessage", modelResource.getItemName()); //$NON-NLS-1$
            SelectFromEObjectListDialog dialog = 
                new SelectFromEObjectListDialog(
                         shell, 
                         getAllTransformationTargets(modelResource), 
                         false, 
                         DIALOG_TITLE, 
                         message,
                         new MyLabelProvider());

            dialog.open();

            if (dialog.getReturnCode() == Window.OK) {
                // now select the object
                Object[] results = dialog.getResult();
                selectedTarget = (EObject)results[0];
            }
        } catch (ModelWorkspaceException theException) {
            UiConstants.Util.log(IStatus.ERROR, theException.getMessage());
        }
     * 
     */
    
    class EditForeignKeyDialog extends TitleAreaDialog {
    	private final String CREATE_TITLE = Messages.createForeignKeyTitle;
    	private final String EDIT_TITLE = Messages.editForeignKeyTitle;

        //=============================================================
        // Instance variables
        //=============================================================
    	RelationalForeignKey originalFK;
    	RelationalForeignKey editedFK;
    	RelationalTable theTable;
    	IFile theModelFile;
        
        String selectedTableName;
        String selectedKeyOrConstraint;

        TableViewer keyViewer;
        TableViewer theColumnDataViewer;
        Combo uniqueKeyMultiCombo;
        Combo foreignKeyMultiCombo;
        
        Set<RelationalColumn> selectedColumns = new HashSet<RelationalColumn>();
        
        boolean isEdit;
        
        boolean creatingContents = false;
            
        //=============================================================
        // Constructors
        //=============================================================
        /**
         * ParsedDataRowDialog constructor.
         * 
         * @param parent   parent of this dialog
         * @param fileInfo the flat file business object
         * @param stringToParse the data string to parse
         */
        public EditForeignKeyDialog(Shell parent, IFile theModelFile, RelationalTable theTable, RelationalForeignKey foreignKey, boolean isEdit) {
            super(parent);
            this.theModelFile = theModelFile;
            this.theTable = theTable;
            this.isEdit = isEdit;
            boolean reallyIsEdit = isEdit;
            this.originalFK = foreignKey;
            if( reallyIsEdit ) {
            	this.editedFK = this.originalFK.clone();
            }
            if( !reallyIsEdit ) {
            	this.editedFK = foreignKey;
            }
        }
        
        @Override
        protected void configureShell( Shell shell ) {
            super.configureShell(shell);
            if( isEdit ) {
            	shell.setText(EDIT_TITLE);
            } else {
            	shell.setText(CREATE_TITLE);
            }
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
        	creatingContents = true;
            if( isEdit ) {
            	setTitle(EDIT_TITLE);
            } else {
            	setTitle(CREATE_TITLE);
            }
        	
            Composite dialogComposite = (Composite)super.createDialogArea(parent);
            
            Composite composite = WidgetFactory.createPanel(dialogComposite);
            //------------------------------        
            // Set layout for the Composite
            //------------------------------        
            GridLayout gridLayout = new GridLayout();
            composite.setLayout(gridLayout);
            gridLayout.numColumns = 2;
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.grabExcessHorizontalSpace = true;
            gridData.widthHint = 500;
            composite.setLayoutData(gridData);
            
            Label label = new Label(composite, SWT.NONE | SWT.RIGHT);
            label.setText(Messages.nameLabel);
            label.setLayoutData(new GridData());
            
            final Text fkNameText =  new Text(composite, SWT.BORDER | SWT.SINGLE);
            fkNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            fkNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            fkNameText.addModifyListener(new ModifyListener() {
        		public void modifyText( final ModifyEvent event ) {
        			String value = fkNameText.getText();
        			if( value == null ) {
        				value = EMPTY_STRING;
        			}
        			editedFK.setName(value);
        			validate();
        		}
            });
            
            label = new Label(composite, SWT.NONE | SWT.RIGHT);
            label.setText(Messages.nameInSourceLabel);
            label.setLayoutData(new GridData());
            
            final Text fkNISText =  new Text(composite, SWT.BORDER | SWT.SINGLE);
            fkNISText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            fkNISText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            fkNISText.addModifyListener(new ModifyListener() {
        		public void modifyText( final ModifyEvent event ) {
        			String value = fkNISText.getText();
        			if( value == null ) {
        				value = EMPTY_STRING;
        			}
        			editedFK.setNameInSource(value);
        			validate();
        		}
            });
            
            WidgetFactory.createLabel(composite, Messages.foreignKeyMultiplicity);

            ILabelProvider multipicityLP = new LabelProvider() {

                @Override
                public String getText( final Object source ) {
                    return (String)source;
                }

                @Override
                public Image getImage( final Object source ) {
                    return null;
                }
            };
            this.foreignKeyMultiCombo = WidgetFactory.createCombo(composite,
                                                             SWT.READ_ONLY,
                                                             GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER,
                                                             Collections.EMPTY_LIST,
                                                             editedFK.getForeignKeyMultiplicity(),
                                                             multipicityLP,
                                                             true);
            this.foreignKeyMultiCombo.setItems(RelationalConstants.MULTIPLICITY.AS_ARRAY);
            
            this.foreignKeyMultiCombo.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText( final ModifyEvent event ) {
                	if( foreignKeyMultiCombo.getSelectionIndex() > -1 ) {
	                	editedFK.setForeignKeyMultiplicity(foreignKeyMultiCombo.getItem(foreignKeyMultiCombo.getSelectionIndex()));
	                    validate();
                	}
                }
            });
            WidgetUtil.setComboItems(this.foreignKeyMultiCombo, MULTIPLICITY_LIST, multipicityLP, true);
            
            WidgetFactory.createLabel(composite, Messages.uniqueKeyMultiplicity);

            this.uniqueKeyMultiCombo = WidgetFactory.createCombo(composite,
                                                             SWT.READ_ONLY,
                                                             GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER,
                                                             Collections.EMPTY_LIST,
                                                             editedFK.getPrimaryKeyMultiplicity(),
                                                             multipicityLP,
                                                             true);
            this.uniqueKeyMultiCombo.setItems(RelationalConstants.MULTIPLICITY.AS_ARRAY);
            this.uniqueKeyMultiCombo.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText( final ModifyEvent event ) {
                	if( uniqueKeyMultiCombo.getSelectionIndex() > -1 ) {
	                	editedFK.setPrimaryKeyMultiplicity(uniqueKeyMultiCombo.getItem(uniqueKeyMultiCombo.getSelectionIndex()));
	                    validate();
                	}
                }
            });
            WidgetUtil.setComboItems(this.uniqueKeyMultiCombo, MULTIPLICITY_LIST, multipicityLP, true);
            
        	Group keysGroup = WidgetFactory.createGroup(dialogComposite, Messages.selectPrimaryKeyOrUniqueConstraint, SWT.NONE, 2, 2);
        	keysGroup.setLayout(new GridLayout(2, false));
        	GridData gd = new GridData(GridData.FILL_BOTH);
        	gd.heightHint = 140;
        	gd.widthHint = 500;
        	keysGroup.setLayoutData(gd);
        	
    		Table table = new Table(keysGroup, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
    		table.setHeaderVisible(false);
    		table.setLinesVisible(true);
    		table.setLayout(new TableLayout());
    		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    		this.keyViewer = new TableViewer(table);
    		gd = new GridData(GridData.FILL_BOTH);
    		gd.heightHint = 160;
    		gd.horizontalSpan = 2;
    		this.keyViewer.getControl().setLayoutData(gd);
    		this.keyViewer.setContentProvider(new ITreeContentProvider() {
				
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
					if( inputElement instanceof Collection ) {
						return ((Collection)inputElement).toArray(new Object[0]);
					}
					return new Object[0];
				}
				
				@Override
				public Object[] getChildren(Object parentElement) {
					return new Object[0];
				}
			});
			
    		this.keyViewer.setLabelProvider(new ILabelProvider() {
				
				@Override
				public void removeListener(ILabelProviderListener listener) {
					// NO OP
				}
				
				@Override
				public boolean isLabelProperty(Object element, String property) {
					// NO OP
					return false;
				}
				
				@Override
				public void dispose() {
					// NO OP	
				}
				
				@Override
				public void addListener(ILabelProviderListener listener) {
					// NO OP
				}
				
				@Override
				public String getText(Object element) {
					String name = EMPTY_STRING;
        			if( element instanceof UniqueConstraint ) {
        				name += ((UniqueConstraint)element).getTable().getName();
        				name += ": " + ((UniqueConstraint)element).getName(); //$NON-NLS-1$
        			} else {
        				name += ((PrimaryKey)element).getTable().getName();
        				name += ": " + ((PrimaryKey)element).getName(); //$NON-NLS-1$
        			}
					return name;
				}
				
				@Override
				public Image getImage(Object element) {
        			if( element instanceof UniqueConstraint ) {
        				return UiPlugin.getDefault().getImage(UiConstants.Images.UC_ICON);
        			} else if( element instanceof PrimaryKey ){
        				return UiPlugin.getDefault().getImage(UiConstants.Images.PK_ICON);
        			} 
					return null;
				}
			});
    		this.keyViewer.getTable().addSelectionListener(
    				new SelectionListener() {

    					public void widgetSelected(SelectionEvent e) {
    						if( processingChecks ) {
    							return;
    						}
    						processingChecks = true;
    						if (e.detail == SWT.CHECK) {
    							
    							TableItem tableItem = (TableItem) e.item;
    							boolean wasChecked = tableItem.getChecked();
    							

								if( wasChecked ) {
									for( TableItem item : keyViewer.getTable().getItems()) {
										if( item != tableItem ) {
											item.setChecked(false);
										}
									}
								}
    						}
		        			String tblName = EMPTY_STRING;
		        			String keyName = EMPTY_STRING;
		        			boolean foundCheckedItem = false;;
    						for( TableItem item : keyViewer.getTable().getItems()) {
	    		        		if( item.getChecked() ) {
	    		        			foundCheckedItem = true;
	    		        			EObject selectedKey = (EObject)item.getData();
	    		        			if( selectedKey instanceof UniqueConstraint ) {
	    		        				tblName = ((UniqueConstraint)selectedKey).getTable().getName();
	    		        				keyName = ((UniqueConstraint)selectedKey).getName();
	    		        			} else {
	    		        				tblName = ((PrimaryKey)selectedKey).getTable().getName();
	    		        				keyName = ((PrimaryKey)selectedKey).getName();
	    		        			}
	    		        			editedFK.setUniqueKeyName(keyName);
	    		        			editedFK.setUniqueKeyTableName(tblName);
	    		        		}
    						}
    						if( !foundCheckedItem ) {
    							editedFK.setUniqueKeyName(keyName);
    		        			editedFK.setUniqueKeyTableName(tblName);
    						}
    						
    						processingChecks = false;

    						validate();
    					}

    					public void widgetDefaultSelected(SelectionEvent e) {
    					}
    				});
    		
    		
    		ModelResource mr = ModelUtilities.getModelResource(this.theModelFile);
    		List keys = new ArrayList();
    		if( mr != null ) {
    			try {
					keys.addAll(RelationalUtil.findUniqueKeys(mr.getEmfResource()));
				} catch (ModelWorkspaceException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
    		}
    		this.keyViewer.setInput(keys);
    		
        	Group theColumnsGroup = WidgetFactory.createGroup(dialogComposite, Messages.selectColumnReferencesToFK, SWT.NONE, 1, 1);
        	theColumnsGroup.setLayout(new GridLayout(1, false));
        	gd = new GridData(GridData.FILL_BOTH);
        	gd.heightHint = 120;
        	gd.widthHint = 500;
        	theColumnsGroup.setLayoutData(gd);
        	
    		Table tableWidget = new Table(theColumnsGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
    		table.setHeaderVisible(false);
    		table.setLinesVisible(true);
    		table.setLayout(new TableLayout());
    		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    		theColumnDataViewer = new TableViewer(tableWidget);
    		gd = new GridData(GridData.FILL_BOTH);
    		gd.heightHint = 160;
    		gd.horizontalSpan = 2;
    		theColumnDataViewer.getControl().setLayoutData(gd);
    		theColumnDataViewer.setContentProvider(new ITreeContentProvider() {
				
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
    		
    		this.theColumnDataViewer.getTable().addSelectionListener(
    				new SelectionListener() {

    					public void widgetSelected(SelectionEvent e) {
    						editedFK.getColumns().clear();
    			        	for( TableItem item : theColumnDataViewer.getTable().getItems() ) {
    			        		
    			        		if( item.getChecked() ) {
    			        			editedFK.addColumn((RelationalColumn)item.getData());
    			        		}
    			        	}
    						validate();
    					}

    					public void widgetDefaultSelected(SelectionEvent e) {
    					}
    				});
			
    		theColumnDataViewer.setLabelProvider(new ColumnDataLabelProvider(0));
    		
    		theColumnDataViewer.setInput(this.theTable);
    		
			for( RelationalColumn col : this.editedFK.getColumns() ) {
				for( TableItem item : theColumnDataViewer.getTable().getItems() ) {
	        		if( item.getData() == col ) {
	        			item.setChecked(true);
	        		}
	        	}
			}
    		
			// Set the initial value in the this.keyViewer
			if( this.editedFK.getUniqueKeyName() != null && this.editedFK.getUniqueKeyTableName() != null ) {
				String name = EMPTY_STRING;
    				name += this.editedFK.getUniqueKeyTableName();
    				name += ": " + this.editedFK.getUniqueKeyName(); //$NON-NLS-1$
    				int index = 0;
    				int selectedIndex = -1;
    				for( TableItem item : this.keyViewer.getTable().getItems() ) {
    					if( item.getData() instanceof EObject ) {
    						EObject value = (EObject)item.getData();
    						String keyName = EMPTY_STRING;
    	        			if( value instanceof UniqueConstraint ) {
    	        				keyName += ((UniqueConstraint)value).getTable().getName();
    	        				keyName += ": " + ((UniqueConstraint)value).getName(); //$NON-NLS-1$
    	        			} else {
    	        				keyName += ((PrimaryKey)value).getTable().getName();
    	        				keyName += ": " + ((PrimaryKey)value).getName(); //$NON-NLS-1$
    	        			}
    						
    						if( keyName.equalsIgnoreCase(name) ) {
    							selectedIndex = index;
    							item.setChecked(true);
    						}
    					}
    					if( selectedIndex > -1 ) {
    						break;
    					}
    					index++;
    				}
    				if( selectedIndex > -1 ) {
    					this.keyViewer.getTable().select(selectedIndex);
    				}
			}
            
            setMessage(Messages.newForeignKeyMessage);
            if( editedFK.getName() != null ) {
            	fkNameText.setText(editedFK.getName());
            }
            if( editedFK.getNameInSource() != null ) {
            	fkNISText.setText(editedFK.getNameInSource());
            }
            if( editedFK.getForeignKeyMultiplicity() != null ) {
            	WidgetUtil.setComboText(this.foreignKeyMultiCombo, this.editedFK.getForeignKeyMultiplicity(), multipicityLP);
            }
            if( editedFK.getPrimaryKeyMultiplicity() != null ) {
            	WidgetUtil.setComboText(this.uniqueKeyMultiCombo, this.editedFK.getPrimaryKeyMultiplicity(), multipicityLP);
            }
            creatingContents = false;
            
            return composite;
        }
        
        private void validate() {
        	if( creatingContents ) return;
        	
        	editedFK.validate();
        	
        	boolean enable = true;
        	setMessage(Messages.newForeignKeyMessage);
        	// ONLY DISABLE if NAME == null
        	if( editedFK.getName() == null || editedFK.getName().trim().length() == 0 ) {
        		enable = false;
        		setErrorMessage(editedFK.getStatus().getMessage());
        	} else {
        		if( editedFK.getStatus().getSeverity() < IStatus.ERROR ) {
        			setErrorMessage(null);
        		} else if(editedFK.getStatus().getSeverity() == IStatus.WARNING) {
        			setMessage(editedFK.getStatus().getMessage(), IMessageProvider.WARNING);
        		} else if(editedFK.getStatus().getSeverity() == IStatus.ERROR) {
        			setErrorMessage(editedFK.getStatus().getMessage());
        		}
        	}
        	
        	getButton(IDialogConstants.OK_ID).setEnabled(enable);
        }
        
        @Override
        public void create() {
            super.create();
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        
        @Override
        protected void okPressed() {
//        	for( TableItem item : keyViewer.getTable().getItems() ) {
//        		if( item.getChecked() ) {
//        			EObject selectedKey = (EObject)item.getData();
//        			if( selectedKey instanceof UniqueConstraint ) {
//        				this.selectedTableName = ((UniqueConstraint)selectedKey).getTable().getName();
//        				this.selectedKeyOrConstraint = ((UniqueConstraint)selectedKey).getName();
//        			} else {
//        				this.selectedTableName = ((PrimaryKey)selectedKey).getTable().getName();
//        				this.selectedKeyOrConstraint = ((PrimaryKey)selectedKey).getName();
//        			}
//        			this.editedFK.setUniqueKeyName(this.selectedKeyOrConstraint);
//        			this.editedFK.setUniqueKeyTableName(this.selectedTableName);
//        		}
//        	}
//        	
//          	for( TableItem item : theColumnDataViewer.getTable().getItems() ) {
//        		if( item.getChecked() ) {
//        			editedFK.addColumn((RelationalColumn)item.getData());
//        		}
//        	}
//          	if( this.foreignKeyMultiCombo.getText() != null ) {
//          		editedFK.setForeignKeyMultiplicity(this.foreignKeyMultiCombo.getText());
//          	}
//          	if( this.uniqueKeyMultiCombo.getText() != null ) {
//          		editedFK.setPrimaryKeyMultiplicity(this.uniqueKeyMultiCombo.getText());
//          	}
          	if( isEdit ) {
          		this.originalFK.inject(editedFK);
          	}
        	
            super.okPressed();
        }
        
        public String getTableName() {
        	return this.selectedTableName;
        }
        
        public String getKeyOrConstraintName() {
        	return this.selectedKeyOrConstraint;
        }

    }
}
