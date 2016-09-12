/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.edit;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalProcedureResultSet;
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
import org.teiid.designer.ui.properties.extension.VdbFileDialogUtil;

/**
 *
 */
public class RelationalProcedureEditorPanel extends RelationalEditorPanel implements RelationalConstants {
	private TabItem generalPropertiesTab;
	private TabItem parametersTab;
	private TabItem resultSetTab;
	private TabItem nativeQueryTab;
	
	// table property widgets
	private Button nonPreparedCB, deterministicCB, returnsNullCB, variableArgsCB, aggregateCB,
		allowsDistinctCB, allowsOrderByCB, analyticCB, decomposableCB, useDistinctRowsCB, includeResultSetCB;
	private Text resultSetNameText, nativeQueryHelpText;

	// parameter widgets
	private Button addParameterButton, editParameterButton, deleteParameterButton, upParameterButton, downParameterButton;
	private Button addColumnButton, editColumnButton, deleteColumnButton, upColumnButton, downColumnButton;
	private Combo updateCountCombo;
	private TableViewerBuilder parametersViewer;
	private TableViewerBuilder columnsViewer;
	private StyledTextEditor nativeQueryTextEditor;
	private Text javaClassText, javaMethodText, functionCategoryText, udfJarPathText;
	private Button udfJarPathBrowse;
	
	boolean validationPerformed = false;

	/**
	 * @param parent the parent panel
	 * @param dialogModel dialog model
	 * @param statusListener the dialog status listener
	 */
	public RelationalProcedureEditorPanel(Composite parent, RelationalDialogModel dialogModel, IDialogStatusListener statusListener) {
		super(parent, dialogModel, statusListener);

		synchronizeUI();
	}

	@Override
	protected RelationalProcedure getRelationalReference() {
	    return (RelationalProcedure) super.getRelationalReference();
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
		if( !this.getRelationalReference().isSourceFunction() && !this.getRelationalReference().isFunction() ) {
			createResultSetTab(tabFolder);
		}
		createDescriptionTab(tabFolder);
		if( !this.getRelationalReference().isFunction() ) {
			createNativeQueryTab(tabFolder);
		}
		
	}
	
	
	private void createGeneralPropertiesTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.PROPERTIES));
        this.generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PROCEDURE, ModelType.PHYSICAL, Status.OK_STATUS));
	}

	private void createParametersTab(TabFolder folderParent) {
        Composite thePanel = createParameterTableGroup(folderParent);

        this.parametersTab = new TabItem(folderParent, SWT.NONE);
        this.parametersTab.setControl(thePanel);
        this.parametersTab.setText(Messages.parametersLabel);
        this.parametersTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PARAMETER, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	private void createResultSetTab(TabFolder folderParent) {
        Composite thePanel = createResultSetPanel(folderParent);

        this.resultSetTab = new TabItem(folderParent, SWT.NONE);
        this.resultSetTab.setControl(thePanel);
        this.resultSetTab.setText(Messages.resultSetLabel);
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
		generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, getRelationalReference().getModelType(), Status.OK_STATUS));
		
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
        if( !this.getRelationalReference().isSourceFunction() && !this.getRelationalReference().isFunction()) {
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
	        	this.resultSetTab.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.TABLE_ICON));
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
	}

	private Composite createResultSetPanel(Composite parent) {
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);
    	
        this.includeResultSetCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.includeResultSetCB);
        this.includeResultSetCB.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.INCLUDE));
        this.includeResultSetCB.setToolTipText(Messages.includeResultSetTooltip);
        this.includeResultSetCB.setSelection(getRelationalReference().getResultSet() != null);
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
                setColumnButtonsState();
            }
        });
        
        Composite namePanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 2, 2);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(namePanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(namePanel);

        Label label = new Label(namePanel, SWT.NONE | SWT.RIGHT);
        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
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

        Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 5);
        GridLayoutFactory.fillDefaults().numColumns(5).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(buttonPanel);
	  	
    	addColumnButton = new Button(buttonPanel, SWT.PUSH);
    	addColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	GridDataFactory.fillDefaults().applyTo(addColumnButton);
    	addColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		getRelationalReference().getResultSet().createColumn();
				handleInfoChanged();
				setColumnButtonsState();
			}
    		
		});
    	this.addColumnButton.setEnabled(false);
    	
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
					getRelationalReference().getResultSet().removeColumn(column);
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
					getRelationalReference().getResultSet().moveColumnUp(info);
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
					getRelationalReference().getResultSet().moveColumnDown(info);
					handleInfoChanged();
					columnsViewer.getTable().select(selectedIndex+1);
				}
				setColumnButtonsState();
			}
    		
		});

        this.columnsViewer = new TableViewerBuilder(thePanel, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
       // GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 100).applyTo(this.columnsViewer.getTableComposite());

        // create columns
        TableViewerColumn column = columnsViewer.createColumn(SWT.LEFT, 30, 30, true);
        column.getColumn().setText(Messages.columnNameLabel + "          "); //$NON-NLS-1$
        column.setLabelProvider(new ColumnDataLabelProvider(0));


        column = columnsViewer.createColumn(SWT.LEFT, 30, 30, true);
        column.getColumn().setText(Messages.dataTypeLabel + "          "); //$NON-NLS-1$
        column.setLabelProvider(new ColumnDataLabelProvider(1));

        
        column = columnsViewer.createColumn(SWT.LEFT, 30, 30, true);
        column.getColumn().setText(Messages.lengthLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(2));

        
    	
        if( this.getRelationalReference().getResultSet() != null ) {
	        for( RelationalColumn row : this.getRelationalReference().getResultSet().getColumns() ) {
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
			upColumnButton.setEnabled(getRelationalReference().getResultSet().canMoveColumnUp(columnInfo));
			downColumnButton.setEnabled(getRelationalReference().getResultSet().canMoveColumnDown(columnInfo));
		} else {
			upColumnButton.setEnabled(false);
			downColumnButton.setEnabled(false);
		}
	}

    private Composite createPropertiesPanel(Composite parent) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().applyTo(thePanel);
        Label label = null;
        
        boolean isFunction = this.getRelationalReference().isFunction() || this.getRelationalReference().isSourceFunction();
        if( !isFunction ) {
	        label = new Label(thePanel, SWT.NONE);
	        label.setText(Messages.updateCountLabel);
	
	        this.updateCountCombo = new Combo(thePanel, SWT.DROP_DOWN | SWT.READ_ONLY);
	        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.updateCountCombo);
	        for (String val : UPDATE_COUNT.AS_ARRAY) {
	            updateCountCombo.add(val);
	        }
	
	        this.updateCountCombo.setText(UPDATE_COUNT.AUTO);
	        
	        this.updateCountCombo.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					getRelationalReference().setUpdateCount(updateCountCombo.getText());
					
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
	        
	        this.nonPreparedCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
	        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.nonPreparedCB);
	        this.nonPreparedCB.setText(Messages.nonPreparedLabel);
	        this.nonPreparedCB.addSelectionListener(new SelectionAdapter() {
	            /**            		
	             * {@inheritDoc}
	             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	             */
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	                getRelationalReference().setNonPrepared(nonPreparedCB.getSelection());
	                handleInfoChanged();
	            }
	        });
        }

        if (this.getRelationalReference().isFunction() || this.getRelationalReference().isSourceFunction()) {
            final Group functionGroup = WidgetFactory.createGroup(thePanel,
                                                                  Messages.functionPropertiesLabel,
                                                                  GridData.FILL_HORIZONTAL,
                                                                  2,
                                                                  3);

            // Add java class and method fields
            if( !this.getRelationalReference().isSourceFunction() ) {
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
	                    String selectedFile = VdbFileDialogUtil.selectFile(udfJarPathBrowse.getShell(),
	                                                                            getModelFile().getProject(),
	                                                                            VdbFolders.UDF);
	                    getRelationalReference().setUdfJarPath(selectedFile);
	                    handleInfoChanged();
	                }
	            });
            }

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

        setUiState();
        return thePanel;
    }
	
	private Composite createParameterTableGroup(Composite parent) {
	  	
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 5);
        GridLayoutFactory.fillDefaults().numColumns(5).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);
	  	
    	addParameterButton = new Button(buttonPanel, SWT.PUSH);
    	addParameterButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	GridDataFactory.fillDefaults().applyTo(addParameterButton);
    	addParameterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		getRelationalReference().createParameter();
				handleInfoChanged();
				setParameterButtonsState();
			}
    		
		});
    	
    	editParameterButton = new Button(buttonPanel, SWT.PUSH);
    	editParameterButton.setText(Messages.Edit);
    	GridDataFactory.fillDefaults().applyTo(editParameterButton);
    	editParameterButton.setEnabled(false);
    	editParameterButton.addSelectionListener(new SelectionAdapter() {

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
					EditParameterDialog dialog = new EditParameterDialog(getShell(), parameter);
					dialog.open();
					handleInfoChanged();
				}
				setParameterButtonsState();
			}
    		
		});
    	
    	deleteParameterButton = new Button(buttonPanel, SWT.PUSH);
    	deleteParameterButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
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
					handleInfoChanged();
				}
				setParameterButtonsState();
			}
    		
		});
    	
    	upParameterButton = new Button(buttonPanel, SWT.PUSH);
    	upParameterButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.MOVE_UP));
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
				}
				setParameterButtonsState();
			}
    		
		});
    	
    	downParameterButton = new Button(buttonPanel, SWT.PUSH);
    	downParameterButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.MOVE_DOWN));
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
				}
				setParameterButtonsState();
			}
    		
		});
    	
    	this.parametersViewer = new TableViewerBuilder(thePanel, (SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER));

        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 150).applyTo(this.parametersViewer.getTableComposite());
        
        // create columns
        TableViewerColumn column = parametersViewer.createColumn(SWT.LEFT, 30, 30, true);
        column.getColumn().setText(Messages.parameterNameLabel + "        "); //$NON-NLS-1$
        column.setLabelProvider(new ParameterDataLabelProvider(0));


        column = parametersViewer.createColumn(SWT.LEFT, 20, 30, true);
        column.getColumn().setText(Messages.dataTypeLabel + "          "); //$NON-NLS-1$
        column.setLabelProvider(new ParameterDataLabelProvider(1));

        column = parametersViewer.createColumn(SWT.LEFT, 20, 30, true);
        column.getColumn().setText(Messages.lengthLabel);
        column.setLabelProvider(new ParameterDataLabelProvider(2));

        
        column = parametersViewer.createColumn(SWT.LEFT, 30, 30, true);
        column.getColumn().setText(Messages.directionLabel);
        column.setLabelProvider(new ParameterDataLabelProvider(3));
    	
        if( getRelationalReference() != null ) {
	        for( RelationalParameter row : this.getRelationalReference().getParameters() ) {
	        	this.parametersViewer.add(row);
	        }
        }
        
        this.parametersViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setParameterButtonsState();
			}
		});
        
        this.parametersViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				Object[] objs = sel.toArray();
				if( objs.length == 1 && objs[0] instanceof RelationalParameter) {
					EditParameterDialog dialog = new EditParameterDialog(getShell(), (RelationalParameter)objs[0]);
					dialog.open();
					handleInfoChanged();
				}
			}
		});
        
        return thePanel;
    }
	
	private void setParameterButtonsState() {
		IStructuredSelection selection = (IStructuredSelection)this.parametersViewer.getSelection();
		boolean enable = selection != null && !selection.isEmpty();
		deleteParameterButton.setEnabled(enable);
		editParameterButton.setEnabled(enable);
		if( enable ) {
			Object[] objs = selection.toArray();
			RelationalParameter parameterInfo = (RelationalParameter)objs[0];
			upParameterButton.setEnabled(getRelationalReference().canMoveParameterUp(parameterInfo));
			downParameterButton.setEnabled(getRelationalReference().canMoveParameterDown(parameterInfo));
		} else {
			upParameterButton.setEnabled(false);
			downParameterButton.setEnabled(false);
		}
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
		nativeQueryTextEditor.getTextWidget().addModifyListener(new ModifyListener() {
				
		    @Override
		    public void modifyText(ModifyEvent e) {
		        getRelationalReference().setNativeQuery(nativeQueryTextEditor.getText());
		        handleInfoChanged();
		    }
		});

    	return thePanel;
	}
	
	private void setUiState() {
		if( getRelationalReference().isFunction()) {
	        boolean functionState = true;
	        
	        /*
	         * Some of these will not be created if the function
	         * is a source function
	         */
	        if (this.deterministicCB != null)
	            this.deterministicCB.setEnabled(functionState);

	        if (this.returnsNullCB != null)
	            this.returnsNullCB.setEnabled(functionState);

	        if (this.variableArgsCB != null)
	            this.variableArgsCB.setEnabled(functionState);

            if (this.aggregateCB != null) {
                this.aggregateCB.setEnabled(functionState);

                boolean aggregateState = functionState;
                if (aggregateState) {
                    aggregateState = aggregateCB.getSelection();
                }

                if (this.allowsDistinctCB != null)
                    this.allowsDistinctCB.setEnabled(aggregateState);

                if (this.allowsOrderByCB != null)
                    this.allowsOrderByCB.setEnabled(aggregateState);

                if (this.analyticCB != null)
                    this.analyticCB.setEnabled(aggregateState);

                if (this.decomposableCB != null)
                    this.decomposableCB.setEnabled(aggregateState);

                if (this.useDistinctRowsCB != null)
                    this.useDistinctRowsCB.setEnabled(aggregateState);
            }
		} else {
	        if( this.addColumnButton != null && this.includeResultSetCB != null ) {
	        	if( getRelationalReference().isNativeQueryProcedure() ) {
		        	this.addColumnButton.setEnabled(false);
		        	this.deleteColumnButton.setEnabled(false);
		        	this.upColumnButton.setEnabled(false);
		        	this.downColumnButton.setEnabled(false);
		        	includeResultSetCB.setEnabled(false);
		        } else {
					boolean enable = this.includeResultSetCB.getSelection();
					this.addColumnButton.setEnabled(enable);
					if( !enable ) {
						this.deleteColumnButton.setEnabled(false);
						this.downColumnButton.setEnabled(false);
						this.upColumnButton.setEnabled(false);
					}
		        }
			}
		}
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
			if( currentStatus.getSeverity() == IStatus.ERROR ) {
				setCanFinish(false);
			}
			setStatus(currentStatus);

		}
		validationPerformed = true;
	}
	
	@Override
	public boolean canFinish() {
		// check if procedure name is not-null
		if( !validationPerformed && this.getRelationalReference().getName() != null ) {
			return true;
		}
		
		return super.canFinish();
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
				return UiPlugin.getDefault().getImage(UiConstants.Images.PARAMETER_ICON);
			}
			return null;
		}
		
		
	}
}
