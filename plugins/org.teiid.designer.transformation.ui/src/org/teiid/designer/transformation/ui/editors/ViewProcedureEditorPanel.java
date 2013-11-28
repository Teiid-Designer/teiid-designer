/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalProcedureResultSet;
import org.teiid.designer.relational.ui.edit.IDialogStatusListener;
import org.teiid.designer.relational.ui.edit.RelationalEditorPanel;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;
import org.teiid.designer.transformation.model.RelationalViewProcedure;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlTextViewer;
import org.teiid.designer.transformation.ui.wizards.sqlbuilder.SQLTemplateDialog;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.graphics.ColorManager;
import org.teiid.designer.ui.common.table.ComboBoxEditingSupport;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.properties.extension.VdbFileDialogUtil;


/**
 * @since 8.0
 */
public class ViewProcedureEditorPanel extends RelationalEditorPanel implements RelationalConstants {
    private TabItem generalPropertiesTab;
    private TabItem sqlTab;
    private TabItem parametersTab;
    private TabItem resultSetTab;
	
	// table property widgets
    private Button nonPreparedCB, deterministicCB, returnsNullCB, variableArgsCB, aggregateCB,
		allowsDistinctCB, allowsOrderByCB, analyticCB, decomposableCB, useDistinctRowsCB, includeResultSetCB;
    private Text resultSetNameText;
	
	// parameter widgets
    private Button addParameterButton, deleteParameterButton, upParameterButton, downParameterButton;
    private Button addColumnButton, deleteColumnButton, upColumnButton, downColumnButton;
    private Combo updateCountCombo;
    private TableViewer parametersViewer;
    private TableViewer columnsViewer;
    // Table SQL Text Tab Controls
    private SqlTextViewer sqlTextViewer;
    private Document sqlDocument;
    
    private Text javaClassText, javaMethodText, functionCategoryText, udfJarPathText; 
    private Button udfJarPathBrowse;

	/**
	 * @param parent the parent panel
	 * @param dialogModel dialog model
	 * @param statusListener the dialog status listener
	 */
	public ViewProcedureEditorPanel(Composite parent, TransformationDialogModel dialogModel, IDialogStatusListener statusListener) {
		super(parent, dialogModel, statusListener);
		
		synchronizeUI();
	}

	@Override
	protected RelationalViewProcedure getRelationalReference() {
	    return (RelationalViewProcedure) super.getRelationalReference();
	}

	@Override
	protected void createPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
		GridLayoutFactory.fillDefaults().applyTo(thePanel);
		GridDataFactory.fillDefaults().applyTo(thePanel);

		createNameGroup(thePanel);

		TabFolder tabFolder = createTabFolder(parent);
		createGeneralPropertiesTab(tabFolder);
		createParametersTab(tabFolder);
		createSQLTab(tabFolder);
		createResultSetTab(tabFolder);
		createDescriptionTab(tabFolder);
		
	}
	
	
	private void createGeneralPropertiesTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(Messages.propertiesLabel);
        this.generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PROCEDURE, ModelType.VIRTUAL, Status.OK_STATUS));
	}

	private void createParametersTab(TabFolder folderParent) {
        Composite thePanel = createParameterTableGroup(folderParent);

        this.parametersTab = new TabItem(folderParent, SWT.NONE);
        this.parametersTab.setControl(thePanel);
        this.parametersTab.setText(Messages.parametersLabel);
        this.parametersTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PARAMETER, getRelationalReference().getModelType(), Status.OK_STATUS));
	}
	
    /*
     * Create the SQL Tab
     */
	private void createSQLTab( TabFolder folderParent ) {
        Composite thePanel = createSQLPanel(folderParent);

        this.sqlTab = new TabItem(folderParent, SWT.NONE);
        this.sqlTab.setControl(thePanel);
        this.sqlTab.setText(Messages.transformationSqlLabel);
        this.sqlTab.setImage(RelationalUiUtil.getNativeSQLImage(Status.OK_STATUS));
    }
	
	private void createResultSetTab(TabFolder folderParent) {
        Composite thePanel = createResultSetPanel(folderParent);

        this.resultSetTab = new TabItem(folderParent, SWT.NONE);
        this.resultSetTab.setControl(thePanel);
        this.resultSetTab.setText(Messages.resultSetLabel);
	}
	
	@Override
	protected void synchronizeExtendedUI() {
		generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PROCEDURE, getRelationalReference().getModelType(), Status.OK_STATUS));
		
    	this.parametersViewer.getTable().removeAll();
    	IStatus maxStatus = Status.OK_STATUS;
        for( RelationalParameter row : getRelationalReference().getParameters() ) {
        	if( row.getStatus().getSeverity() > maxStatus.getSeverity() ) {
        		maxStatus = row.getStatus();
        	}
        	this.parametersViewer.add(row);
        }
        parametersTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PARAMETER, getRelationalReference().getModelType(), maxStatus));
        
        // Result Set Tab
        if( this.getRelationalReference().getResultSet() == null ) {
        	if( WidgetUtil.widgetValueChanged(includeResultSetCB, false)) {
        		this.includeResultSetCB.setSelection(false);
        		this.addColumnButton.setEnabled(false);
        	}
        	
        	this.resultSetNameText.setEnabled(false);
        	if( WidgetUtil.widgetValueChanged(resultSetNameText, EMPTY_STRING)) {
        		this.resultSetNameText.setText(EMPTY_STRING);
        	}
        	
        	this.columnsViewer.getTable().removeAll();
        	this.columnsViewer.getTable().setEnabled(false);
        	this.resultSetTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, ModelType.VIRTUAL, Status.OK_STATUS));
        } else {
        	this.columnsViewer.getTable().setEnabled(true);
        	if( WidgetUtil.widgetValueChanged(includeResultSetCB, true)) {
        		this.includeResultSetCB.setSelection(true);
        	}
        	this.resultSetNameText.setEnabled(true);
        	if( this.getRelationalReference().getResultSet().getName() != null && WidgetUtil.widgetValueChanged(resultSetNameText, this.getRelationalReference().getResultSet().getName())) {
        		this.resultSetNameText.setText(this.getRelationalReference().getResultSet().getName());
        	}

        	this.columnsViewer.getTable().removeAll();
        	if( !this.getRelationalReference().getResultSet().getColumns().isEmpty() ) {
        		for( RelationalColumn column : this.getRelationalReference().getResultSet().getColumns() ) {
        			this.columnsViewer.add(column);
        		}
        	}
        	resultSetTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, getRelationalReference().getResultSet().getModelType(), this.getRelationalReference().getResultSet().getStatus()));
        }
        
        if( this.getRelationalReference().isFunction() ) {
        	// Assume UDF
        	if( this.getRelationalReference().getUdfJarPath() != null ) {
        		this.udfJarPathText.setText(this.getRelationalReference().getUdfJarPath());
        	}
        }
	}

	private Composite createResultSetPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        this.includeResultSetCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.includeResultSetCB);
        this.includeResultSetCB.setText(Messages.includeLabel);
        this.includeResultSetCB.setToolTipText(Messages.includeResultSetTooltip);
        this.includeResultSetCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	if( includeResultSetCB.getSelection() ) {
            		if( getRelationalReference().getResultSet() == null ) {
            			RelationalProcedureResultSet resultSet = new RelationalProcedureResultSet();
            			
            			if( resultSetNameText.getText() != null ) {
            				resultSet.setName(resultSetNameText.getText());
            			}
            			getRelationalReference().setResultSet(resultSet);
            		}
            	} else {
            		getRelationalReference().setResultSet(null);
            	}
                handleInfoChanged();
            }
        });
        
        Composite namePanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 2, 2);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(namePanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(namePanel);

        Label label = new Label(namePanel, SWT.NONE | SWT.RIGHT);
        label.setText(Messages.nameLabel);
        GridDataFactory.fillDefaults().applyTo(label);
        
        this.resultSetNameText =  new Text(namePanel, SWT.BORDER | SWT.SINGLE);
        this.resultSetNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).minSize(50, SWT.DEFAULT).applyTo(this.resultSetNameText);
        this.resultSetNameText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			String value = resultSetNameText.getText();
    			if( value == null ) {
    				value = EMPTY_STRING;
    			}
        		if( getRelationalReference().getResultSet() != null ) {
        			RelationalProcedureResultSet resultSet = getRelationalReference().getResultSet();
        			resultSet.setName(value);
        		}
        		handleInfoChanged();
    		}
        });

	  	Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 4);
	  	GridLayoutFactory.fillDefaults().numColumns(4).applyTo(buttonPanel);
	  	GridDataFactory.fillDefaults().span(2, 1).applyTo(buttonPanel);

    	addColumnButton = new Button(buttonPanel, SWT.PUSH);
    	addColumnButton.setText(Messages.addLabel);
    	GridDataFactory.fillDefaults().applyTo(addColumnButton);
    	addColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		getRelationalReference().getResultSet().createColumn();
				handleInfoChanged();
			}
    		
		});
    	this.addColumnButton.setEnabled(false);
    	
    	deleteColumnButton = new Button(buttonPanel, SWT.PUSH);
    	deleteColumnButton.setText(Messages.deleteLabel);
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
					getRelationalReference().getResultSet().removeColumn(column);
					deleteColumnButton.setEnabled(false);
					handleInfoChanged();
				}
			}
    		
		});
    	
    	upColumnButton = new Button(buttonPanel, SWT.PUSH);
    	upColumnButton.setText(Messages.moveUpLabel);
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
					getRelationalReference().getResultSet().moveColumnUp(info);
					handleInfoChanged();
					columnsViewer.getTable().select(selectedIndex-1);
					downColumnButton.setEnabled(getRelationalReference().getResultSet().canMoveColumnDown(info));
					upColumnButton.setEnabled(getRelationalReference().getResultSet().canMoveColumnUp(info));
					
				}
			}
    		
		});
    	
    	downColumnButton = new Button(buttonPanel, SWT.PUSH);
    	downColumnButton.setText(Messages.moveDownLabel);
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
					getRelationalReference().getResultSet().moveColumnDown(info);
					handleInfoChanged();
					columnsViewer.getTable().select(selectedIndex+1);
					downColumnButton.setEnabled(getRelationalReference().getResultSet().canMoveColumnDown(info));
					upColumnButton.setEnabled(getRelationalReference().getResultSet().canMoveColumnUp(info));
					
				}
			}
    		
		});

    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());

        this.columnsViewer = new TableViewer(columnTable);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 100).applyTo(this.columnsViewer.getControl());

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
        
    	
        if( this.getRelationalReference().getResultSet() != null ) {
	        for( RelationalColumn row : this.getRelationalReference().getResultSet().getColumns() ) {
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
						upColumnButton.setEnabled(getRelationalReference().getResultSet().canMoveColumnUp(columnInfo));
						downColumnButton.setEnabled(getRelationalReference().getResultSet().canMoveColumnDown(columnInfo));
					}
					
				}
				
			}
		});
    	
    	return thePanel;
	}

	private Composite createPropertiesPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(thePanel);
		GridDataFactory.fillDefaults().applyTo(thePanel);
        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.updateCountLabel);
        
        this.updateCountCombo = new Combo(thePanel, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.updateCountCombo);
        for (String val : UPDATE_COUNT.AS_ARRAY) {
        	updateCountCombo.add(val);
        }
        
        this.updateCountCombo.setText(UPDATE_COUNT.AUTO);
        
        this.nonPreparedCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.nonPreparedCB);
        this.nonPreparedCB.setText(Messages.nonPreparedLabel);
        this.nonPreparedCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	getRelationalReference().setNonPrepared(nonPreparedCB.getSelection());
                handleInfoChanged();
            }
        });

        if (this.getRelationalReference().isFunction()) {
            final Group functionGroup = WidgetFactory.createGroup(thePanel,
                                                                  Messages.functionPropertiesLabel,
                                                                  GridData.FILL_HORIZONTAL,
                                                                  2,
                                                                  3);

            if (!this.getRelationalReference().isSourceFunction()) {
                // Add java class and method fields
                label = new Label(functionGroup, SWT.NONE);
                label.setText(Messages.functionCategoryLabel);

                this.functionCategoryText = new Text(functionGroup, SWT.BORDER | SWT.SINGLE);
                this.functionCategoryText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
                GridDataFactory.fillDefaults().span(2, 1).applyTo(this.functionCategoryText);
                this.functionCategoryText.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(final ModifyEvent event) {
                        String value = functionCategoryText.getText();
                        if (value == null) {
                            value = EMPTY_STRING;
                        }

                        getRelationalReference().setFunctionCategory(value);
                        handleInfoChanged();
                    }
                });

                label = new Label(functionGroup, SWT.NONE);
                label.setText(Messages.javaClassLabel);

                this.javaClassText = new Text(functionGroup, SWT.BORDER | SWT.SINGLE);
                this.javaClassText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
                GridDataFactory.fillDefaults().span(2, 1).applyTo(this.javaClassText);
                this.javaClassText.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(final ModifyEvent event) {
                        String value = javaClassText.getText();
                        if (value == null) {
                            value = EMPTY_STRING;
                        }

                        getRelationalReference().setJavaClassName(value);
                        handleInfoChanged();
                    }
                });

                label = new Label(functionGroup, SWT.NONE);
                label.setText(Messages.javaMethodLabel);

                this.javaMethodText = new Text(functionGroup, SWT.BORDER | SWT.SINGLE);
                this.javaMethodText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
                GridDataFactory.fillDefaults().span(2, 1).applyTo(this.javaMethodText);
                this.javaMethodText.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(final ModifyEvent event) {
                        String value = javaMethodText.getText();
                        if (value == null) {
                            value = EMPTY_STRING;
                        }

                        getRelationalReference().setJavaMethodName(value);
                        handleInfoChanged();
                    }
                });

                label = new Label(functionGroup, SWT.NONE);
                label.setText(Messages.udfJarPathLabel);

                this.udfJarPathText = new Text(functionGroup, SWT.BORDER | SWT.SINGLE);
                this.udfJarPathText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
                GridDataFactory.fillDefaults().applyTo(this.udfJarPathText);
                this.udfJarPathText.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(final ModifyEvent event) {
                        String value = udfJarPathText.getText();
                        if (value == null) {
                            value = EMPTY_STRING;
                        }

                        getRelationalReference().setUdfJarPath(value);
                        if (!isSynchronizing()) {
                            handleInfoChanged();
                        }
                    }
                });

                this.udfJarPathBrowse = new Button(functionGroup, SWT.PUSH | SWT.RIGHT);
                this.udfJarPathBrowse.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS));
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.udfJarPathBrowse);
                this.udfJarPathBrowse.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        // Open dialog and get file
                        String selectedFile = VdbFileDialogUtil.selectUdfOrFile(udfJarPathBrowse.getShell(),
                                                                                getModelFile().getProject(),
                                                                                true);
                        getRelationalReference().setUdfJarPath(selectedFile);
                        handleInfoChanged();
                    }
                });

                Composite innerPanel = WidgetFactory.createPanel(functionGroup, SWT.NONE, GridData.FILL_HORIZONTAL, 3);
                GridLayoutFactory.fillDefaults().numColumns(3).applyTo(innerPanel);
                GridDataFactory.fillDefaults().span(3, 1).applyTo(innerPanel);

                this.deterministicCB = new Button(innerPanel, SWT.CHECK | SWT.RIGHT);
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.deterministicCB);
                this.deterministicCB.setText(Messages.deterministicLabel);
                this.deterministicCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getRelationalReference().setDeterministic(deterministicCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.returnsNullCB = new Button(innerPanel, SWT.CHECK | SWT.RIGHT);
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.returnsNullCB);
                this.returnsNullCB.setText(Messages.returnsNullOnNullLabel);
                this.returnsNullCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getRelationalReference().setReturnsNullOnNull(returnsNullCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.variableArgsCB = new Button(innerPanel, SWT.CHECK | SWT.RIGHT);
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.variableArgsCB);
                this.variableArgsCB.setText(Messages.variableArgumentsLabel);
                this.variableArgsCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getRelationalReference().setVariableArguments(variableArgsCB.getSelection());
                        handleInfoChanged();
                    }
                });

                final Group aggregateGroup = WidgetFactory.createGroup(functionGroup,
                                                                       Messages.aggregatePropertiesLabel,
                                                                       GridData.FILL_HORIZONTAL,
                                                                       2,
                                                                       3);

                this.aggregateCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.aggregateCB);
                this.aggregateCB.setText(Messages.aggregateLabel);
                this.aggregateCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getRelationalReference().setAggregate(aggregateCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.allowsDistinctCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.allowsDistinctCB);
                this.allowsDistinctCB.setText(Messages.allowsDistinctLabel);
                this.allowsDistinctCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getRelationalReference().setAllowsDistinct(allowsDistinctCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.allowsOrderByCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.allowsOrderByCB);
                this.allowsOrderByCB.setText(Messages.allowsOrderByLabel);
                this.allowsOrderByCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getRelationalReference().setAllowsOrderBy(allowsOrderByCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.analyticCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.analyticCB);
                this.analyticCB.setText(Messages.analyticLabel);
                this.analyticCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getRelationalReference().setAnalytic(analyticCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.decomposableCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.decomposableCB);
                this.decomposableCB.setText(Messages.decomposableLabel);
                this.decomposableCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getRelationalReference().setDecomposable(decomposableCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.useDistinctRowsCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.useDistinctRowsCB);
                this.useDistinctRowsCB.setText(Messages.usesDistinctRowsLabel);
                this.useDistinctRowsCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getRelationalReference().setUseDistinctRows(useDistinctRowsCB.getSelection());
                        handleInfoChanged();
                    }
                });
            }
        }
        
        setUiState();
        return thePanel;
	}
	
	private Composite createParameterTableGroup(Composite parent) {
	  	
	  	Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
	  	GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
	  	GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

	  	Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 4);
	  	GridLayoutFactory.fillDefaults().numColumns(4).applyTo(buttonPanel);
	  	GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

    	addParameterButton = new Button(buttonPanel, SWT.PUSH);
    	addParameterButton.setText(Messages.addLabel);
    	GridDataFactory.fillDefaults().applyTo(addParameterButton);
    	addParameterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		getRelationalReference().createParameter();
				handleInfoChanged();
			}
    		
		});
    	
    	deleteParameterButton = new Button(buttonPanel, SWT.PUSH);
    	deleteParameterButton.setText(Messages.deleteLabel);
    	GridDataFactory.fillDefaults().applyTo(deleteParameterButton);
    	deleteParameterButton.setEnabled(false);
    	deleteParameterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalParameter parameter = null;
				
				IStructuredSelection selection = (IStructuredSelection)parametersViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalParameter ) {
						parameter =  (RelationalParameter) obj;
						break;
					}
				}
				if( parameter != null ) {
					getRelationalReference().removeParameter(parameter);
					deleteParameterButton.setEnabled(false);
					handleInfoChanged();
				}
			}
    		
		});
    	
    	upParameterButton = new Button(buttonPanel, SWT.PUSH);
    	upParameterButton.setText(Messages.moveUpLabel);
    	GridDataFactory.fillDefaults().applyTo(upParameterButton);
    	upParameterButton.setEnabled(false);
    	upParameterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalParameter info = null;
				
				IStructuredSelection selection = (IStructuredSelection)parametersViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalParameter ) {
						info =  (RelationalParameter) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = parametersViewer.getTable().getSelectionIndex();
					getRelationalReference().moveParameterUp(info);
					handleInfoChanged();
					parametersViewer.getTable().select(selectedIndex-1);
					downParameterButton.setEnabled(getRelationalReference().canMoveParameterDown(info));
					upParameterButton.setEnabled(getRelationalReference().canMoveParameterUp(info));
					
				}
			}
    		
		});
    	
    	downParameterButton = new Button(buttonPanel, SWT.PUSH);
    	downParameterButton.setText(Messages.moveDownLabel);
    	GridDataFactory.fillDefaults().applyTo(downParameterButton);
    	downParameterButton.setEnabled(false);
    	downParameterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalParameter info = null;
				
				IStructuredSelection selection = (IStructuredSelection)parametersViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalParameter ) {
						info =  (RelationalParameter) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = parametersViewer.getTable().getSelectionIndex();
					getRelationalReference().moveParameterDown(info);
					handleInfoChanged();
					parametersViewer.getTable().select(selectedIndex+1);
					downParameterButton.setEnabled(getRelationalReference().canMoveParameterDown(info));
					upParameterButton.setEnabled(getRelationalReference().canMoveParameterUp(info));
					
				}
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());
    	
        this.parametersViewer = new TableViewer(columnTable);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 150).applyTo(this.parametersViewer.getControl());

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.parametersViewer, SWT.LEFT);
        column.getColumn().setText(Messages.parameterNameLabel + "        "); //$NON-NLS-1$
        column.setEditingSupport(new ParameterNameEditingSupport(this.parametersViewer));
        column.setLabelProvider(new ParameterDataLabelProvider(0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.parametersViewer, SWT.LEFT);
        column.getColumn().setText(Messages.dataTypeLabel + "          "); //$NON-NLS-1$
        column.setLabelProvider(new ParameterDataLabelProvider(1));
        column.setEditingSupport(new DatatypeEditingSupport(this.parametersViewer));
        column.getColumn().pack();
        
        column = new TableViewerColumn(this.parametersViewer, SWT.LEFT);
        column.getColumn().setText(Messages.lengthLabel);
        column.setLabelProvider(new ParameterDataLabelProvider(2));
        column.setEditingSupport(new ParameterWidthEditingSupport(this.parametersViewer));
        column.getColumn().pack();
        
        column = new TableViewerColumn(this.parametersViewer, SWT.LEFT);
        column.getColumn().setText(Messages.directionLabel);
        column.setLabelProvider(new ParameterDataLabelProvider(3));
        column.setEditingSupport(new DirectionEditingSupport(this.parametersViewer));
        column.getColumn().pack();
        
    	
        if( getRelationalReference() != null ) {
	        for( RelationalParameter row : this.getRelationalReference().getParameters() ) {
	        	this.parametersViewer.add(row);
	        }
        }
        
        this.parametersViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				
				if( sel.isEmpty()) {
					deleteParameterButton.setEnabled(false);
					upParameterButton.setEnabled(false);
					downParameterButton.setEnabled(false);
				} else {
					boolean enable = true;
					Object[] objs = sel.toArray();
					RelationalParameter columnInfo = null;
					for( Object obj : objs) {
						if(  !(obj instanceof RelationalParameter)) {
							enable = false;
							break;
						} else {
							columnInfo = (RelationalParameter)obj;
						}
					} 
					if( objs.length == 0 ) {
						enable = false;
					}
					deleteParameterButton.setEnabled(enable);
					if( enable ) {
						upParameterButton.setEnabled(getRelationalReference().canMoveParameterUp(columnInfo));
						downParameterButton.setEnabled(getRelationalReference().canMoveParameterDown(columnInfo));
					}
					
				}
				
			}
		});
        
        return thePanel;
    }
	
    /*
     * Create the SQL Display tab panel
     */
    private Composite createSQLPanel( Composite parent ) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        Button templateButton = new Button(thePanel, SWT.LEFT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(templateButton);
        templateButton.setText(Messages.selectSQLTemplateLabel);
        templateButton.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                SQLTemplateDialog templateDialog = new SQLTemplateDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(),
                                                                         SQLTemplateDialog.PROC_TEMPLATES);
                if (templateDialog.open() == Window.OK) {
                	String sql = templateDialog.getSQL();
                	getRelationalReference().setTransformationSQL(sql);
                    sqlDocument.set(sql);
                    handleInfoChanged();
                }
            }
        });

        createSqlGroup(thePanel);
        return thePanel;
    }
    
    /*
     * The SQL Display area portion of the SQL Tab
     */
    private void createSqlGroup( Composite parent ) {
        Group textTableOptionsGroup = WidgetFactory.createGroup(parent, Messages.sqlDefinitionLabel);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(textTableOptionsGroup);

        ColorManager colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION ;

        sqlTextViewer = new SqlTextViewer(textTableOptionsGroup, new VerticalRuler(0), styles, colorManager);
        sqlDocument = new Document();
        sqlTextViewer.setInput(sqlDocument);
        sqlTextViewer.getTextWidget().addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				getRelationalReference().setTransformationSQL(sqlTextViewer.getTextWidget().getText());
				handleInfoChanged();
				
			}
		});
        sqlTextViewer.setEditable(true);
        sqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(sqlTextViewer.getControl());
    }
	
	private void setUiState() {
		if( ! this.getRelationalReference().isFunction() ) {
			if( this.addColumnButton != null && this.includeResultSetCB != null ) {
				boolean enable = this.includeResultSetCB.getSelection();
				this.addColumnButton.setEnabled(enable);
				if( !enable ) {
					this.deleteColumnButton.setEnabled(false);
					this.downColumnButton.setEnabled(false);
					this.upColumnButton.setEnabled(false);
				}
			}
			
			return;
		}

        boolean functionState = true;
        this.deterministicCB.setEnabled(functionState);
        this.returnsNullCB.setEnabled(functionState);
        this.variableArgsCB.setEnabled(functionState);
        this.aggregateCB.setEnabled(functionState);

        boolean aggregateState = functionState;
        if (aggregateState) {
            aggregateState = aggregateCB.getSelection();
        }
        this.allowsDistinctCB.setEnabled(aggregateState);
        this.allowsOrderByCB.setEnabled(aggregateState);
        this.analyticCB.setEnabled(aggregateState);
        this.decomposableCB.setEnabled(aggregateState);
        this.useDistinctRowsCB.setEnabled(aggregateState);
        
	}

	@Override
    protected void handleInfoChanged() {
		super.handleInfoChanged();
		setUiState();
	}

	@Override
	protected void validate() {
		this.getRelationalReference().validate();
		
		setCanFinish(this.getRelationalReference().nameIsValid());	
		
		IStatus currentStatus = this.getRelationalReference().getStatus();
		if( currentStatus.isOK() ) {
			setStatus(Status.OK_STATUS);
		} else {
			setStatus(currentStatus);
		}
		
	}
	

	class ParameterDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public ParameterDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof RelationalParameter ) {
				switch (this.columnNumber) {
					case 0: {
						return ((RelationalParameter)element).getName();
					}
					case 1: {
						return ((RelationalParameter)element).getDatatype();
					}
					case 2: {
						return Integer.toString(((RelationalParameter)element).getLength());
					}
					case 3: {
						return ((RelationalParameter)element).getDirection();
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
				return RelationalUiUtil.getRelationalImage(TYPES.PARAMETER, ModelType.VIRTUAL, Status.OK_STATUS);
			}
			return null;
		}
		
		
	}
    
    class ParameterNameEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer the column viewer
		 */
		public ParameterNameEditingSupport(ColumnViewer viewer) {
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
			if( element instanceof RelationalParameter ) {
				return ((RelationalParameter)element).getName();
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
			if( element instanceof RelationalParameter ) {
				String oldValue = ((RelationalParameter)element).getName();
				String newValue = (String)value;
				if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
					((RelationalParameter)element).setName(newValue);
					parametersViewer.refresh(element);
					handleInfoChanged();
				}
			}
		}

	}
    
    class ParameterWidthEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer the column viewer
		 */
		public ParameterWidthEditingSupport(ColumnViewer viewer) {
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
			if( element instanceof RelationalParameter ) {
				return Integer.toString(((RelationalParameter)element).getLength());
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
			if( element instanceof RelationalParameter ) {
				int oldValue = ((RelationalParameter)element).getLength();
				int newValue = oldValue;
				try {
					newValue = Integer.parseInt((String)value);
				} catch (NumberFormatException ex) {
					return;
				}
				if( newValue != oldValue ) {
					((RelationalParameter)element).setLength(newValue);
					parametersViewer.refresh(element);
					handleInfoChanged();
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
        	if( element instanceof RelationalParameter ) {
        		return ((RelationalParameter)element).getDatatype();
        	} else if( element instanceof RelationalColumn ) {
        		return ((RelationalColumn)element).getDatatype();
        	}
        	
        	return EMPTY_STRING;
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            if( element instanceof RelationalParameter ) {
            	((RelationalParameter)element).setDatatype(newValue);
        	} else if( element instanceof RelationalColumn ) {
        		((RelationalColumn)element).setDatatype(newValue);
        	}
            handleInfoChanged();
        }
    }
    
    class DirectionEditingSupport extends ComboBoxEditingSupport {
        /**
         * @param viewer the column viewer
         */
        public DirectionEditingSupport( ColumnViewer viewer ) {
            super(viewer);
        }


        @Override
        protected String getElementValue( Object element ) {
        	return ((RelationalParameter)element).getDirection();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return DIRECTION.AS_ARRAY;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            ((RelationalParameter)element).setDirection(newValue);
            handleInfoChanged();
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
				return RelationalUiUtil.getRelationalImage(TYPES.COLUMN, ModelType.VIRTUAL, Status.OK_STATUS);
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
}
