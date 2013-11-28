/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard.panels;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.roles.ui.wizard.PermissionTreeProvider;
import org.teiid.designer.roles.ui.wizard.dialogs.AbstractAddOrEditTitleDialog;
import org.teiid.designer.ui.common.table.CheckBoxEditingSupport;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.widget.MessageLabel;

/**
 *
 */
public class RowBasedSecurityPanel extends DataRolePanel {
    private static final char DELIM = CoreStringUtil.Constants.DOT_CHAR;
    private static final char B_SLASH = '/';
    
	TableViewer tableViewer;
	Button addButton;
	Button removeButton;
	Button editButton;
	
	PermissionTreeProvider permissionTreeProvider;
	
	/**
     * @param parent
     * @param wizard
     */
    public RowBasedSecurityPanel(Composite parent, DataRoleWizard wizard) {
    	super(parent, wizard);
    }
    

	/* (non-Javadoc)
	 * @see org.teiid.designer.roles.ui.wizard.panels.DataRolePanel#createControl()
	 */
	@Override
	void createControl() {
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(getPrimaryPanel());
		GridDataFactory.fillDefaults().applyTo(getPrimaryPanel());
		
		{
	        this.tableViewer = new TableViewer(getPrimaryPanel(), (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
	        ColumnViewerToolTipSupport.enableFor(this.tableViewer);
	        this.tableViewer.setContentProvider(new IStructuredContentProvider() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	             */
	            @Override
	            public void dispose() {
	                // nothing to do
	            }

	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	             */
	            @Override
	            public Object[] getElements( Object inputElement ) {
	            	List<Permission> permissions =  getWizard().getTreeProvider().getPermissionsWithRowBasedSecurity();

	                if (permissions.isEmpty()) {
	                    return new Object[0];
	                }
	                
	                return permissions.toArray(new Permission[0]);
	            }

	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	             *      java.lang.Object)
	             */
	            @Override
	            public void inputChanged( Viewer viewer,
	                                      Object oldInput,
	                                      Object newInput ) {
	                // nothing to do
	            }
	        });

	        // sort the table rows by display name
	        this.tableViewer.setComparator(new ViewerComparator() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	             *      java.lang.Object)
	             */
	            @Override
	            public int compare( Viewer viewer,
	                                Object e1,
	                                Object e2 ) {
	            	Permission perm1 = (Permission)e1;
	            	Permission perm2 = (Permission)e2;

	                return super.compare(viewer, perm1.getTargetName(), perm2.getTargetName());
	            }
	        });

	        Table table = this.tableViewer.getTable();
	        table.setHeaderVisible(true);
	        table.setLinesVisible(true);
	        table.setLayout(new TableLayout());
	        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	        ((GridData)table.getLayoutData()).horizontalSpan = 2;

	        // create columns
	        TableViewerColumn column = new TableViewerColumn(this.tableViewer, SWT.LEFT);
	        column.getColumn().setText(Messages.name + getSpaces(70));
	        column.setLabelProvider(new PermissionLabelProvider(0));
	        column.getColumn().pack();
	        
	        column = new TableViewerColumn(this.tableViewer, SWT.LEFT);
	        column.getColumn().setText(Messages.constraint);
	        column.setLabelProvider(new PermissionLabelProvider(1));
	        // TODO: add editing support
	        column.setEditingSupport(new ContraintEditingSupport(this.tableViewer));
	        column.getColumn().pack();
	        
	        column = new TableViewerColumn(this.tableViewer, SWT.LEFT);
	        column.getColumn().setText(Messages.condition);
	        column.setLabelProvider(new PermissionLabelProvider(2));
	        column.setEditingSupport(new ConditionEditingSupport(this.tableViewer));
	        column.getColumn().pack();


	        this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	             */
	            @Override
	            public void selectionChanged( SelectionChangedEvent event ) {
	                boolean enable = !tableViewer.getSelection().isEmpty();
	                editButton.setEnabled(enable);
	                removeButton.setEnabled(enable);
	            }
	        });
	        
		}
        
        Composite toolbarPanel = WidgetFactory.createPanel(getPrimaryPanel(), SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 3);
        
        this.addButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.addButton.setText(Messages.add);
        this.addButton.setToolTipText(Messages.addRowBasedSecurityTooltip);
        this.addButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAdd();
			} 

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        this.editButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.editButton.setText(Messages.edit);
        this.editButton.setToolTipText(Messages.editRowBasedSecurityTooltip);
        this.editButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEdit();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        this.editButton.setEnabled(false);
        
        this.removeButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.removeButton.setText(Messages.remove);
        this.removeButton.setToolTipText(Messages.removeRowBasedSecurityTooltip);
        this.removeButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRemove();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        this.removeButton.setEnabled(false);
		
	}
	
	class PermissionLabelProvider extends ColumnLabelProvider {

        private final int columnID;

        public PermissionLabelProvider( int columnID ) {
            this.columnID = columnID;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof Permission ) {
				if( columnID == 0 ) {
					return ((Permission)element).getTargetName();
				} else if( columnID == 2 ) {
					return ((Permission)element).getCondition();
				}  else if( columnID == 1 ) {
					return Boolean.toString(((Permission)element).isConstraint());
				}
			}
			return super.getText(element);
		}
	}
	
    void handleAdd() {
    	RowBasedSecurityDialog dialog = new RowBasedSecurityDialog(getPrimaryPanel().getShell(), 
    			Messages.addColumnRowBasedSecurityTitle, 
                Messages.addColumnRowBasedSecurityMessage,
                null, false);

        if (dialog.open() == Window.OK) {
            // update model
            String condition = dialog.getCondition();
            boolean constraint = dialog.getConstraintValue();
            String targetName = dialog.getTargetName();

            getWizard().getTreeProvider().setRowsBasedSecurity(targetName, condition, constraint);
            
            getWizard().refreshAllTabs();
        }
    }
    
    void handleEdit() {
    	Permission permission = getSelectedPermission();
    	
    	RowBasedSecurityDialog dialog = new RowBasedSecurityDialog(getPrimaryPanel().getShell(), 
    			Messages.editColumnRowBasedSecurityTitle, 
                Messages.editColumnRowBasedSecurityTitle,
                permission, true);

        if (dialog.open() == Window.OK) {
            // update model
            String condition = dialog.getCondition();
            boolean constraint = dialog.getConstraintValue();
            String targetName = dialog.getTargetName();

            getWizard().getTreeProvider().setRowsBasedSecurity(targetName, condition, constraint);
            
            getWizard().refreshAllTabs();
        }
    }
	
    void handleRemove() {
    	Permission selection = getSelectedPermission();
        assert (selection != null);

        // update model
        getWizard().getTreeProvider().removeRowBasedSecurity(selection);
        
        // update UI
        getWizard().refreshAllTabs();
    }
    
    private Permission getSelectedPermission() {
        IStructuredSelection selection = (IStructuredSelection)this.tableViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (Permission)selection.getFirstElement();
    }
    
    @Override
    public void refresh() {
    	this.tableViewer.getTable().removeAll();
    	
        for( Permission perm : getWizard().getTreeProvider().getPermissionsWithRowBasedSecurity() ) {
        	this.tableViewer.add(perm);
        }
        
        if( this.tableViewer.getSelection().isEmpty() ) {
        	this.editButton.setEnabled(false);
        	this.removeButton.setEnabled(false);
        }
    }
    
    class ContraintEditingSupport extends CheckBoxEditingSupport {

		public ContraintEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setElementValue(Object element, Object newValue) {
			if( element instanceof Permission && newValue instanceof Boolean) {
				Permission perm = (Permission)element;
				if(perm.isConstraint() ) {
					perm.setConstraint(false);
					handleInfoChanged();
				} else {
					perm.setConstraint(true);
					handleInfoChanged();
				}
			}
		}
    }
    
    class ConditionEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer the column viewer
		 */
		public ConditionEditingSupport(ColumnViewer viewer) {
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
			if( element instanceof Permission ) {
				return ((Permission)element).getCondition();
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
			if( element instanceof Permission ) {
				String oldValue = ((Permission)element).getCondition();
				String newValue = (String)value;
				if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
					((Permission)element).setCondition(newValue);
					tableViewer.refresh(element);
					handleInfoChanged();
				}
			}
		}

	}
    
    private void handleInfoChanged() {
    	refresh();
    }
    
    protected PermissionTreeProvider getPermissionTreeProvider() {
    	if( permissionTreeProvider == null ) {
    		permissionTreeProvider = new PermissionTreeProvider();
    	}
    	
    	return permissionTreeProvider;
    }
    
    /**
     * This inner class provides for selecting existing language to be allowed for the specified data role
     * The class contains a simple 
     */
    class RowBasedSecurityDialog extends AbstractAddOrEditTitleDialog {
    	
        private String targetColumn;
        private Text targetColumnText;
        
        private StyledTextEditor textEditor;
        private String conditionString;
        private boolean constraint = true;
        private Button constraintButton;
        private boolean isEdit;

        /**
         * @param parentShell the parent shell (may be <code>null</code>)
         * @param existingPropertyNames the existing property names (can be <code>null</code>)
         */
        public RowBasedSecurityDialog( Shell parentShell, String title, String message, Permission permission, boolean okEnabled ) {
            super(parentShell, title, message, okEnabled);

            if( permission != null && permission.getCondition() != null ) {
            	this.conditionString = permission.getCondition();
            	isEdit = true;
            	this.constraint = permission.isConstraint();
            	this.targetColumn = permission.getTargetName();
            }
        }

        
        /**
         * 
         * @param outerPanel
         */
        @Override
		public void createCustomArea( Composite outerPanel ) {
            
    		{
    	        final Composite innerPanel = new Composite(outerPanel, SWT.NONE);
    	        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(innerPanel);
    	        GridDataFactory.fillDefaults().grab(true, false).applyTo(innerPanel);        
    	        
    	        WidgetFactory.createLabel(innerPanel, Messages.targetColumn);

    	        this.targetColumnText = WidgetFactory.createTextField(innerPanel, GridData.FILL_HORIZONTAL, 1, StringUtilities.EMPTY_STRING);
    	        if( isEdit ) {
    	        	this.targetColumnText.setText(this.targetColumn);
    	        }
    	        
    	        this.targetColumnText.addModifyListener(new ModifyListener() {
    	            @Override
    	            public void modifyText( ModifyEvent e ) {
    	            	handleInputChanged();
    	            }
    	        });
    	        this.targetColumnText.setEditable(false);
    	        this.targetColumnText.setBackground(innerPanel.getBackground());
    	        
    	        Button button = new Button(innerPanel, SWT.PUSH);
    	        button.setText(Messages.dotDotDot);
    	        button.setToolTipText(Messages.browseVdbForTargetColumn);
    	        button.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// Open dialog to display models tree so user can select a column object
						handleBrowseForColumn();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
    	        
    	        Label label = WidgetFactory.createLabel(innerPanel, Messages.constraint);
    	        label.setToolTipText(Messages.constraintButtonTooltip);

    	        this.constraintButton = new Button(innerPanel, SWT.CHECK);
    	        this.constraintButton.setText(Messages.constraint);
    	        if( isEdit ) {
    	        	this.constraintButton.setSelection(this.constraint);
    	        } else {
    	        	this.constraintButton.setSelection(true);
    	        }
    	        
    	        this.constraintButton.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// Open dialog to display models tree so user can select a column object
						handleInputChanged();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
    	        
    	        final Group group = WidgetFactory.createGroup(outerPanel, Messages.condition, GridData.FILL_HORIZONTAL, 1);
    	        {
	    			textEditor = new StyledTextEditor(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
	    			GridDataFactory.fillDefaults().grab(true,  true).span(3,  1).applyTo(textEditor.getTextWidget());
	    			((GridData)textEditor.getTextWidget().getLayoutData()).heightHint = 50;
	    			
	    			if( isEdit ) {
	    				this.textEditor.setText(this.conditionString);
	    			} else {
	    				textEditor.setText(""); //$NON-NLS-1$
	    			}
	    	        textEditor.getDocument().addDocumentListener(new IDocumentListener() {
	
	    	            @Override
	    	            public void documentChanged( DocumentEvent event ) {
	    	            	handleInputChanged();
	    	            }
	
	    	            @Override
	    	            public void documentAboutToBeChanged( DocumentEvent event ) {
	    	                // NO OP
	    	            }
	    	        });
    	        }

    	        
    		}
        }
        
        /**
         * @return the new targetColumn value (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public String getTargetName() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return this.targetColumn;
        }

        /**
         * @return the new condition value (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public String getCondition() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return this.conditionString;
        }
        
        /**
         * @return the new language (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public boolean getConstraintValue() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return constraint;
        }
        
        private void handleBrowseForColumn() {
        	SelectColumnDialog dialog = new SelectColumnDialog(getShell());
        	
            if (dialog.open() == Window.OK) {
            	targetColumn = dialog.getColumnName();
            	targetColumnText.setText(targetColumn);
            	handleInputChanged();
            }
        }


        @Override
        protected void handleInputChanged() {
        	validate();
        }
        
        @Override
        protected void validate() {
        	boolean enable = true;
        	setErrorMessage(null);
        	setMessage(Messages.clickOkToFinish); //Messages.clickOKToFinish);
        	
            conditionString = textEditor.getText();
            targetColumn = targetColumnText.getText();
            if( targetColumn == null || targetColumn.trim().isEmpty() ) {
            	enable = false;
        		setErrorMessage(Messages.targetColumnIsUndefined);
        		return;
            }
        	
            if( conditionString == null || conditionString.trim().isEmpty() ) {
            	enable = false;
        		setErrorMessage(Messages.conditionIsUndefined);
            }
        	
        	getButton(IDialogConstants.OK_ID).setEnabled(enable);
        }
    }
    
    class SelectColumnDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

        private Text columnNameText;
        private String columnName;
        private MessageLabel statusMessageLabel;

        public SelectColumnDialog( Shell parent ) {
            super(parent, getPermissionTreeProvider(), getPermissionTreeProvider());
            setTitle(Messages.columnSelection);
            setMessage(Messages.selectColumnForCondition);
            setInput(getWizard().getTempContainer());
            setAllowMultiple(false);
        }

        @Override
        protected Control createDialogArea( Composite parent ) {
            Composite panel = new Composite(parent, SWT.NONE);
            panel.setLayout(new GridLayout());
            GridData panelData = new GridData(GridData.FILL_BOTH);
            panel.setLayoutData(panelData);

            Group selectedGroup = WidgetFactory.createGroup(panel, "Selected Column", GridData.FILL_HORIZONTAL,1, 2); //$NON-NLS-1$

            this.columnNameText = WidgetFactory.createTextField(selectedGroup, GridData.FILL_HORIZONTAL, Messages.undefined);
            GridData data = new GridData(GridData.FILL_HORIZONTAL);
            data.heightHint = convertHeightInCharsToPixels(1);
            this.columnNameText.setLayoutData(data);
            this.columnNameText.setEditable(false);
            this.columnNameText.setBackground(panel.getBackground());
            this.columnNameText.setText(Messages.undefined);

            super.createDialogArea(panel);

            this.statusMessageLabel = new MessageLabel(panel);
            GridData statusData = new GridData(GridData.FILL_HORIZONTAL);
            data.heightHint = convertHeightInCharsToPixels(1);
            this.statusMessageLabel.setLayoutData(statusData);
            this.statusMessageLabel.setEnabled(false);
            this.statusMessageLabel.setText(Messages.undefined);

            getTreeViewer().expandToLevel(2);

            return panel;
        }
        
        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected TreeViewer createTreeViewer( Composite parent ) {
            TreeViewer viewer = super.createTreeViewer(parent);
            viewer.addSelectionChangedListener(this);
            viewer.getTree().setEnabled(true);
            viewer.setSorter(new ViewerSorter());
            viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {
                @Override
                public boolean select( Viewer viewer,
                                       Object parentElement,
                                       Object element ) {
                    if (element instanceof EObject || element instanceof Resource) {
                        return true;
                    }

                    return false;
                }
            } });
            
            viewer.setContentProvider(getPermissionTreeProvider());
            viewer.setLabelProvider(getPermissionTreeProvider());

            viewer.setInput(getWizard().getTempContainer());

            return viewer;
        }

        @Override
        public void selectionChanged( SelectionChangedEvent event ) {
            TreeSelection selection = (TreeSelection)event.getSelection();
            if (selection.isEmpty()) {
                this.columnNameText.setText(Messages.undefined);
                this.columnName = null;
                updateOnSelection(null);
                return;
            }

            Object firstElement = selection.getFirstElement();

            if (!(firstElement instanceof Column)) {
                this.columnNameText.setText(Messages.undefined);
                this.columnName = null;
            } else {
                Column column = (Column)selection.getFirstElement();
                columnName = getFullColumnName(column);
                this.columnNameText.setText(column.getName());
            }

            updateOnSelection(firstElement);
        }
        
        private String getFullColumnName(Column column) {
        	String targetName = getResourceName(column.eResource()) + '/' + ModelerCore.getModelEditor().getModelRelativePath(column);

            targetName = targetName.replace(B_SLASH, DELIM);
            
            return targetName;
        }
        
        /*
         * Returns the file name only minus the xmi file extension
         */
        private String getResourceName( Resource res ) {

            if (res.getURI().path().endsWith(".xmi")) { //$NON-NLS-1$
                Path path = new Path(res.getURI().path());
                return path.removeFileExtension().lastSegment();
            }
            return res.getURI().path();
        }

        private void updateOnSelection( Object selectedObject ) {
            IStatus status = new Status(IStatus.INFO,
            		RolesUiPlugin.PLUGIN_ID,
                                        "Valid column selected. Click OK to finish."); //$NON-NLS-1$
            if (selectedObject != null) {
                if (!(selectedObject instanceof Column)) {
                    status = new Status(IStatus.ERROR,
                    		RolesUiPlugin.PLUGIN_ID,
                                        "Selected object is not a column"); //$NON-NLS-1$
                    getOkButton().setEnabled(false);
                } else {
                    getOkButton().setEnabled(true);
                }
            } else {
                status = new Status(IStatus.ERROR,
                		RolesUiPlugin.PLUGIN_ID,
                                    "No column selected"); //$NON-NLS-1$
                getOkButton().setEnabled(false);
            }

            this.statusMessageLabel.setErrorStatus(status);
        }

        /**
         * Returns the current TeiidTranslator
         * 
         * @return the TeiidTranslator. may return null
         */
        public String getColumnName() {
            return this.columnName;
        }

    }
}
