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
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.ui.common.table.ComboBoxEditingSupport;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;

/**
 *
 */
public class RelationalProcedureEditorPanel extends RelationalEditorPanel implements RelationalConstants {
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	

	private RelationalProcedure procedure;
	
	TabFolder tabFolder;
	TabItem generalPropertiesTab;
	TabItem parametersTab;
	TabItem descriptionTab;
	
	// table property widgets
	Button isFunctionCB, nonPreparedCB, deterministicCB, returnsNullCB, variableArgsCB, aggregateCB,
		allowsDistinctCB, allowsOrderByCB, analyticCB, decomposableCB, useDistinctRowsCB;
	Text modelNameText, nameText, nameInSourceText;
	StyledTextEditor descriptionTextEditor;
	
	// parameter widgets
	Button addParameterButton, deleteParameterButton, upParameterButton, downParameterButton;
	Combo updateCountCombo;
	TableViewer parametersViewer;
	
	boolean synchronizing = false;
	boolean processingChecks = false;

	/**
	 * @param parent the parent panel
	 * @param procedure the relational procedure BO
	 * @param modelFile the model file
	 * @param statusListener the dialog status listener
	 */
	public RelationalProcedureEditorPanel(Composite parent, RelationalProcedure procedure, IFile modelFile, IDialogStatusListener statusListener) {
		super(parent, procedure, modelFile, statusListener);
		this.procedure = procedure;
		
		
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
	    	helpText.setText(Messages.createRelationalProcedureHelpText);
		}
		createNameGroup(parent);
		
		tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		createGeneralPropertiesTab(tabFolder);
		createParametersTab(tabFolder);
		createDescriptionTab(tabFolder);
		
	}
	
	
	void createGeneralPropertiesTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(Messages.propertiesLabel);
        this.generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PROCEDURE, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	void createDescriptionTab(TabFolder folderParent) {
        Composite thePanel = createDescriptionPanel(folderParent);

        this.descriptionTab = new TabItem(folderParent, SWT.NONE);
        this.descriptionTab.setControl(thePanel);
        this.descriptionTab.setText(Messages.descriptionLabel);
	}
	
	
	void createParametersTab(TabFolder folderParent) {
        Composite thePanel = createParameterTableGroup(folderParent);

        this.parametersTab = new TabItem(folderParent, SWT.NONE);
        this.parametersTab.setControl(thePanel);
        this.parametersTab.setText(Messages.parametersLabel);
        this.parametersTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PARAMETER, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	@Override
	protected void synchronizeUI() {
		if( synchronizing ) {
			return;
		}
		if( this.procedure == null ) {
			this.procedure = (RelationalProcedure)getRelationalReference();
		}
		synchronizing = true;
		
		if( procedure.getName() != null ) {
			if( WidgetUtil.widgetValueChanged(this.nameText, procedure.getName()) ) {
				this.nameText.setText(procedure.getName());
			}
		} else {
			if( WidgetUtil.widgetValueChanged(this.nameText, EMPTY_STRING) ) {
				this.nameText.setText(EMPTY_STRING);
			}
		}
		
		if( procedure.getNameInSource() != null ) {
			if( WidgetUtil.widgetValueChanged(this.nameInSourceText, procedure.getNameInSource()) ) {
				this.nameInSourceText.setText(procedure.getNameInSource());
			}
		} else {
			if( WidgetUtil.widgetValueChanged(this.nameInSourceText, EMPTY_STRING) ) {
				this.nameInSourceText.setText(EMPTY_STRING);
			}
		}
		
		generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, procedure.getModelType(), procedure.getStatus()));
		
    	this.parametersViewer.getTable().removeAll();
    	IStatus maxStatus = Status.OK_STATUS;
        for( RelationalParameter row : procedure.getParameters() ) {
        	if( row.getStatus().getSeverity() > maxStatus.getSeverity() ) {
        		maxStatus = row.getStatus();
        	}
        	this.parametersViewer.add(row);
        }
        parametersTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PARAMETER, procedure.getModelType(), maxStatus));

		synchronizing = false;
	}
	
	private void addSpacerLabels(Composite parent, int numSpacers) {
		for( int i=0; i<numSpacers; i++ ) {
			new Label(parent, SWT.NONE);
		}
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
        if( this.modelFile != null ) {
        	modelNameText.setText(this.modelFile.getName());
        }
        
        label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.nameLabel);
        
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
    			
    			procedure.setName(value);
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
    			
    			procedure.setNameInSource(value);
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
            final Group descGroup = WidgetFactory.createGroup(thePanel, Messages.descriptionLabel, GridData.FILL_BOTH, 3);
            descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
            final GridData descGridData = new GridData(GridData.FILL_BOTH);
            descGridData.horizontalSpan = 1;
//            descGridData.heightHint = 80;
//            descGridData.minimumHeight = 30;
            descGridData.grabExcessVerticalSpace = true;
            descriptionTextEditor.setLayoutData(descGridData);
            descriptionTextEditor.setText(""); //$NON-NLS-1$
            descriptionTextEditor.getTextWidget().addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					procedure.setDescription(descriptionTextEditor.getText());
				}
			});
        }
    	
    	return thePanel;
	}

	@SuppressWarnings("unused")
	Composite createPropertiesPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2);
		thePanel.setLayout(new GridLayout(2, false));
		GridData panelGD = new GridData(GridData.FILL_BOTH);
		panelGD.heightHint = 300;
    	thePanel.setLayoutData(panelGD);
        
        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.updateCountLabel);
        
        this.updateCountCombo = new Combo(thePanel, SWT.DROP_DOWN | SWT.READ_ONLY);
        this.updateCountCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        for (String val : UPDATE_COUNT.AS_ARRAY) {
        	updateCountCombo.add(val);
        }
        
        this.updateCountCombo.setText(UPDATE_COUNT.AUTO);
        
        this.nonPreparedCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        this.nonPreparedCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        this.nonPreparedCB.setText(Messages.nonPreparedLabel);
        this.nonPreparedCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	procedure.setNonPrepared(nonPreparedCB.getSelection());
                handleInfoChanged();
            }
        });
        addSpacerLabels(thePanel, 1);
        

    	
        FUNCTION_GROUP: {
        	final Group functionGroup = WidgetFactory.createGroup(thePanel, Messages.functionPropertiesLabel, GridData.FILL_HORIZONTAL, 2, 3);
        	
            this.isFunctionCB = new Button(functionGroup, SWT.CHECK | SWT.RIGHT);
            this.isFunctionCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            this.isFunctionCB.setText(Messages.isFunctionLabel);
            this.isFunctionCB.addSelectionListener(new SelectionAdapter() {
                /**            		
                 * {@inheritDoc}
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                	procedure.setFunction(isFunctionCB.getSelection());
                    handleInfoChanged();
                }
            });
            addSpacerLabels(functionGroup, 2);

            this.deterministicCB = new Button(functionGroup, SWT.CHECK | SWT.RIGHT);
            this.deterministicCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            this.deterministicCB.setText(Messages.deterministicLabel);
            this.deterministicCB.addSelectionListener(new SelectionAdapter() {
                /**            		
                 * {@inheritDoc}
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                	procedure.setDeterministic(deterministicCB.getSelection());
                    handleInfoChanged();
                }
            });
            //addSpacerLabels(functionGroup, 1);

            this.returnsNullCB = new Button(functionGroup, SWT.CHECK | SWT.RIGHT);
            this.returnsNullCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            this.returnsNullCB.setText(Messages.returnsNullOnNullLabel);
            this.returnsNullCB.addSelectionListener(new SelectionAdapter() {
                /**            		
                 * {@inheritDoc}
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                	procedure.setReturnsNullOnNull(returnsNullCB.getSelection());
                    handleInfoChanged();
                }
            });
            //addSpacerLabels(functionGroup, 1);

            this.variableArgsCB = new Button(functionGroup, SWT.CHECK | SWT.RIGHT);
            this.variableArgsCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            this.variableArgsCB.setText(Messages.variableArgumentsLabel);
            this.variableArgsCB.addSelectionListener(new SelectionAdapter() {
                /**            		
                 * {@inheritDoc}
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                	procedure.setVariableArguments(variableArgsCB.getSelection());
                    handleInfoChanged();
                }
            });
            //addSpacerLabels(functionGroup, 1);


            
            AGGREGATE_GROUP: {
            	final Group aggregateGroup = WidgetFactory.createGroup(functionGroup, Messages.aggregatePropertiesLabel, GridData.FILL_HORIZONTAL, 3, 3);
            	
                this.aggregateCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                this.aggregateCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                this.aggregateCB.setText(Messages.aggregateLabel);
                this.aggregateCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( SelectionEvent e ) {
                    	procedure.setAggregate(aggregateCB.getSelection());
                        handleInfoChanged();
                    }
                });
                addSpacerLabels(aggregateGroup, 2);
            	
                this.allowsDistinctCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                this.allowsDistinctCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                this.allowsDistinctCB.setText(Messages.allowsDistinctLabel);
                this.allowsDistinctCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( SelectionEvent e ) {
                    	procedure.setAllowsDistinct(allowsDistinctCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.allowsOrderByCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                this.allowsOrderByCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                this.allowsOrderByCB.setText(Messages.allowsOrderByLabel);
                this.allowsOrderByCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( SelectionEvent e ) {
                    	procedure.setAllowsOrderBy(allowsOrderByCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.analyticCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                this.analyticCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                this.analyticCB.setText(Messages.analyticLabel);
                this.analyticCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( SelectionEvent e ) {
                    	procedure.setAnalytic(analyticCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.decomposableCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                this.decomposableCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                this.decomposableCB.setText(Messages.decomposableLabel);
                this.decomposableCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( SelectionEvent e ) {
                    	procedure.setDecomposable(decomposableCB.getSelection());
                        handleInfoChanged();
                    }
                });

                this.useDistinctRowsCB = new Button(aggregateGroup, SWT.CHECK | SWT.RIGHT);
                this.useDistinctRowsCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                this.useDistinctRowsCB.setText(Messages.usesDistinctRowsLabel);
                this.useDistinctRowsCB.addSelectionListener(new SelectionAdapter() {
                    /**            		
                     * {@inheritDoc}
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( SelectionEvent e ) {
                    	procedure.setUseDistinctRows(useDistinctRowsCB.getSelection());
                        handleInfoChanged();
                    }
                });
            }

        }
        
        setUiState();

        addSpacerLabels(thePanel, 2);
        

		
        return thePanel;
	}
	
	Composite createParameterTableGroup(Composite parent) {
	  	
	  	Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
	  	thePanel.setLayout(new GridLayout(1, false));
	  	GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
	  	groupGD.heightHint=300;
	  	thePanel.setLayoutData(groupGD);
	  	
	  	Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 4);
	  	buttonPanel.setLayout(new GridLayout(4, false));
	  	GridData panelGD = new GridData();
	  	buttonPanel.setLayoutData(panelGD);
	  	
    	addParameterButton = new Button(buttonPanel, SWT.PUSH);
    	addParameterButton.setText(Messages.addLabel);
    	addParameterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	addParameterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		procedure.createParameter();
				handleInfoChanged();
			}
    		
		});
    	
    	deleteParameterButton = new Button(buttonPanel, SWT.PUSH);
    	deleteParameterButton.setText(Messages.deleteLabel);
    	deleteParameterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					procedure.removeParameter(parameter);
					deleteParameterButton.setEnabled(false);
					handleInfoChanged();
				}
			}
    		
		});
    	
    	upParameterButton = new Button(buttonPanel, SWT.PUSH);
    	upParameterButton.setText(Messages.moveUpLabel);
    	upParameterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					procedure.moveParameterUp(info);
					handleInfoChanged();
					parametersViewer.getTable().select(selectedIndex-1);
					downParameterButton.setEnabled(procedure.canMoveParameterDown(info));
					upParameterButton.setEnabled(procedure.canMoveParameterUp(info));
					
				}
			}
    		
		});
    	
    	downParameterButton = new Button(buttonPanel, SWT.PUSH);
    	downParameterButton.setText(Messages.moveDownLabel);
    	downParameterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
					procedure.moveParameterDown(info);
					handleInfoChanged();
					parametersViewer.getTable().select(selectedIndex+1);
					downParameterButton.setEnabled(procedure.canMoveParameterDown(info));
					upParameterButton.setEnabled(procedure.canMoveParameterUp(info));
					
				}
			}
    		
		});
    	
    	Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    	columnTable.setHeaderVisible(true);
    	columnTable.setLinesVisible(true);
    	columnTable.setLayout(new TableLayout());
    	columnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    	
        this.parametersViewer = new TableViewer(columnTable);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        this.parametersViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.parametersViewer, SWT.LEFT);
        column.getColumn().setText(Messages.columnNameLabel + "          "); //$NON-NLS-1$
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
        
    	
        if( this.procedure != null ) {
	        for( RelationalParameter row : this.procedure.getParameters() ) {
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
						upParameterButton.setEnabled(procedure.canMoveParameterUp(columnInfo));
						downParameterButton.setEnabled(procedure.canMoveParameterDown(columnInfo));
					}
					
				}
				
			}
		});
        
        return thePanel;
    }
	
	void setUiState() {
        boolean functionState = this.isFunctionCB.getSelection();
        this.deterministicCB.setEnabled(functionState);
        this.returnsNullCB.setEnabled(functionState);
        this.variableArgsCB.setEnabled(functionState);
        this.aggregateCB.setEnabled(functionState);
        
        boolean aggregateState = functionState;
        if( aggregateState ) {
        	aggregateState = aggregateCB.getSelection();
        }
    	this.allowsDistinctCB.setEnabled(aggregateState);
        this.allowsOrderByCB.setEnabled(aggregateState);
        this.analyticCB.setEnabled(aggregateState);
        this.decomposableCB.setEnabled(aggregateState);
        this.useDistinctRowsCB.setEnabled(aggregateState);
	}
	
	void handleInfoChanged() {
		if( synchronizing ) {
			return;
		}
		validate();
		
		synchronizeUI();
		
		setUiState();
	}
	
	@Override
	protected void validate() {
		this.procedure.validate();
		
		IStatus currentStatus = this.procedure.getStatus();
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
				return UiPlugin.getDefault().getImage(UiConstants.Images.PARAMETER_ICON);
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
        	return ((RelationalParameter)element).getDatatype();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            ((RelationalParameter)element).setDatatype(newValue);
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
}
