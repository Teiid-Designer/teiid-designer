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
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.util.RelationalUtil;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.RelationalConstants.MULTIPLICITY;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalForeignKey;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *
 */
public class EditForeignKeyDialog extends TitleAreaDialog {
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	private final String CREATE_TITLE = Messages.createForeignKeyTitle;
	private final String EDIT_TITLE = Messages.editForeignKeyTitle;
	
	private List<String> MULTIPLICITY_LIST;

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
    Button allowJoinButton;
    
    Set<RelationalColumn> selectedColumns = new HashSet<RelationalColumn>();
    
    boolean isEdit;
    
    boolean creatingContents = false;
	boolean processingChecks = false;
        
    //=============================================================
    // Constructors
    //=============================================================
    /**
     * ParsedDataRowDialog constructor.
     * 
     * @param parent  the parent of this dialog
     * @param theModelFile the model file
	 * @param theTable the relational table object
	 * @param foreignKey the FK being edited
	 * @param isEdit edit mode
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
        
		
		MULTIPLICITY_LIST = new ArrayList<String>();
		for( String str : MULTIPLICITY.AS_ARRAY ) {
			MULTIPLICITY_LIST.add(str);
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
        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));
        label.setLayoutData(new GridData());
        
        final Text fkNameText =  new Text(composite, SWT.BORDER | SWT.SINGLE);
        fkNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        fkNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fkNameText.addModifyListener(new ModifyListener() {
    		@Override
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
    		@Override
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
        
        this.allowJoinButton = new Button(composite, SWT.CHECK);
        this.allowJoinButton.setText(Messages.allowJoinLabel);
        this.allowJoinButton.setToolTipText(Messages.allowJoinTooltip);
        this.allowJoinButton.setSelection(editedFK.isAllowJoin());
        this.allowJoinButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				editedFK.setAllowJoin(allowJoinButton.getSelection());
				validate();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
        
    	Group keysGroup = WidgetFactory.createGroup(dialogComposite, Messages.selectPrimaryKeyOrUniqueConstraint, SWT.NONE, 2, 2);
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

					@Override
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
	        			boolean foundCheckedItem = false;
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

					@Override
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

					@Override
					public void widgetSelected(SelectionEvent e) {
						editedFK.getColumns().clear();
			        	for( TableItem item : theColumnDataViewer.getTable().getItems() ) {
			        		
			        		if( item.getChecked() ) {
			        			editedFK.addColumn((RelationalColumn)item.getData());
			        		}
			        	}
						validate();
					}

					@Override
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
      	if( isEdit ) {
      		this.originalFK.inject(editedFK);
      	}
    	
        super.okPressed();
    }
    
    /**
     * @return the table name
     */
    public String getTableName() {
    	return this.selectedTableName;
    }
    
    /**
     * @return the name of the key or constraint
     */
    public String getKeyOrConstraintName() {
    	return this.selectedKeyOrConstraint;
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
}
