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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.roles.ui.wizard.PermissionTreeProvider;
import org.teiid.designer.roles.ui.wizard.dialogs.AbstractAddOrEditTitleDialog;
import org.teiid.designer.ui.common.table.CheckBoxEditingSupport;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
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

//	TableViewer tableViewer;
    TableViewerBuilder tableBuilder;
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
		
		{ // Message/description Text
			Composite thePanel = WidgetFactory.createPanel(getPrimaryPanel(), SWT.NONE, 1, 1);
			GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(thePanel);
			
			Text helpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
			helpText.setBackground(thePanel.getBackground());
			helpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
			helpText.setText(Messages.rowBasedSecurityHelpText);

		}
		
		{
		    tableBuilder = new TableViewerBuilder(getPrimaryPanel(), SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

	        ColumnViewerToolTipSupport.enableFor(tableBuilder.getTableViewer());
	        tableBuilder.setContentProvider(new IStructuredContentProvider() {
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
	        tableBuilder.setComparator(new ViewerComparator() {
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

	        // create columns
	        TableViewerColumn column = tableBuilder.createColumn(SWT.LEFT, 40, 100, true);
	        column.getColumn().setText(Messages.name + getSpaces(70));
	        column.setLabelProvider(new PermissionLabelProvider(0));
	        
	        column = tableBuilder.createColumn(SWT.LEFT, 30, 100, true);
	        column.getColumn().setText(Messages.constraint);
	        column.getColumn().setToolTipText(Messages.constraintButtonTooltip);
	        column.setLabelProvider(new PermissionLabelProvider(1));
	        column.setEditingSupport(new ContraintEditingSupport(tableBuilder.getTableViewer()));

	        column = tableBuilder.createColumn(SWT.LEFT, 30, 100, true);
	        column.getColumn().setText(Messages.condition);
	        column.setLabelProvider(new PermissionLabelProvider(2));
	        column.setEditingSupport(new ConditionEditingSupport(tableBuilder.getTableViewer()));

	        tableBuilder.addSelectionChangedListener(new ISelectionChangedListener() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	             */
	            @Override
	            public void selectionChanged( SelectionChangedEvent event ) {
	                boolean enable = !tableBuilder.getSelection().isEmpty();
	                editButton.setEnabled(enable);
	                removeButton.setEnabled(enable);
	            }
	        });
	        
	        tableBuilder.addDoubleClickListener(new IDoubleClickListener() {
				
				@Override
				public void doubleClick(DoubleClickEvent event) {
					handleEdit();
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
        
        getWizard().getTreeProvider().handlePermissionChanged(selection);
        
        // update UI
        getWizard().refreshAllTabs();
    }
    
    private Permission getSelectedPermission() {
        IStructuredSelection selection = (IStructuredSelection)this.tableBuilder.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (Permission)selection.getFirstElement();
    }
    
    @Override
    public void refresh() {
    	this.tableBuilder.getTable().removeAll();
    	
        for( Permission perm : getWizard().getTreeProvider().getPermissionsWithRowBasedSecurity() ) {
        	this.tableBuilder.add(perm);
        }
        
        if( this.tableBuilder.getSelection().isEmpty() ) {
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
					tableBuilder.getTableViewer().refresh(element);
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
    	
        private String targetTableOrView;
        private Text targetTableOrViewText;
        
        private StyledTextEditor textEditor;
        private String conditionString;
        private boolean constraint = true;
        private Button constraintButton;
        private boolean isEdit;

        /**
         * @param parentShell the parent shell (may be <code>null</code>)
         * @param title
         * @param message 
         * @param permission 
         * @param okEnabled 
         */
        public RowBasedSecurityDialog( Shell parentShell, String title, String message, Permission permission, boolean okEnabled ) {
            super(parentShell, title, message, okEnabled);

            if( permission != null && permission.getCondition() != null ) {
            	this.conditionString = permission.getCondition();
            	isEdit = true;
            	this.constraint = permission.isConstraint();
            	this.targetTableOrView = permission.getTargetName();
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
    	        
    	        Label theLabel = WidgetFactory.createLabel(innerPanel, Messages.target);
    	        GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.CENTER).applyTo(theLabel);

    	        this.targetTableOrViewText = WidgetFactory.createTextField(innerPanel, GridData.FILL_HORIZONTAL, 1, StringConstants.EMPTY_STRING);
    	        if( isEdit ) {
    	        	this.targetTableOrViewText.setText(this.targetTableOrView);
    	        	this.targetTableOrViewText.setEditable(false);
    	        }
    	        
    	        this.targetTableOrViewText.addModifyListener(new ModifyListener() {
    	            @Override
    	            public void modifyText( ModifyEvent e ) {
    	            	handleInputChanged();
    	            }
    	        });
    	        this.targetTableOrViewText.setEditable(false);
    	        this.targetTableOrViewText.setBackground(innerPanel.getBackground());
    	        
    	        Button button = new Button(innerPanel, SWT.PUSH);
    	        button.setText(Messages.dotDotDot);
    	        button.setToolTipText(Messages.browseVdbForTarget);
    	        button.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// Open dialog to display models tree so user can select a column object
						handleBrowseForTableOrView();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
    	        button.setEnabled(!isEdit);

    	        this.constraintButton = new Button(innerPanel, SWT.CHECK);
    	        this.constraintButton.setText(Messages.constraint_with_tooltip);
    	        GridData gd = new GridData();
    	        gd.horizontalSpan = 3;
    	        this.constraintButton.setLayoutData(gd);
    	        if( isEdit ) {
    	        	this.constraintButton.setSelection(this.constraint);
    	        } else {
    	        	this.constraintButton.setSelection(true);
    	        }
    	        this.constraintButton.setToolTipText(Messages.constraintButtonTooltip);
    	        
    	        this.constraintButton.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// Open dialog to display models tree so user can select a column object
						constraint = constraintButton.getSelection();
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
            return this.targetTableOrView;
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
        
        private void handleBrowseForTableOrView() {
        	SelectTableOrViewDialog dialog = new SelectTableOrViewDialog(getShell());
        	
            if (dialog.open() == Window.OK) {
            	targetTableOrView = dialog.getTargetName();
            	if( targetTableOrView != null ) {
            		targetTableOrViewText.setText(targetTableOrView);
            	}
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
            targetTableOrView = targetTableOrViewText.getText();
            if( targetTableOrView == null || targetTableOrView.trim().isEmpty() ) {
            	enable = false;
        		setErrorMessage(Messages.targetIsUndefined);
        		getButton(IDialogConstants.OK_ID).setEnabled(enable);
        		return;
            }
        	
            if( conditionString == null || conditionString.trim().isEmpty() ) {
            	enable = false;
        		setErrorMessage(Messages.conditionIsUndefined);
        		getButton(IDialogConstants.OK_ID).setEnabled(enable);
        		return;
            }
        	
        	getButton(IDialogConstants.OK_ID).setEnabled(enable);
        }
    }
    
    class SelectTableOrViewDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

        private Text nameText;
        private String name;
        private MessageLabel statusMessageLabel;

        public SelectTableOrViewDialog( Shell parent ) {
            super(parent, getPermissionTreeProvider(), getPermissionTreeProvider());
            setTitle(Messages.targetSelection);
            setMessage(Messages.selectTargetForCondition);
            setInput(getWizard().getTempContainer());
            setAllowMultiple(false);
        }

        @Override
        protected Control createDialogArea( Composite parent ) {
            Composite panel = new Composite(parent, SWT.NONE);
            panel.setLayout(new GridLayout());
            GridData panelData = new GridData(GridData.FILL_BOTH);
            panel.setLayoutData(panelData);

            Group selectedGroup = WidgetFactory.createGroup(panel, Messages.selectedTableViewOrProcedure, GridData.FILL_HORIZONTAL,1, 2);

            this.nameText = WidgetFactory.createTextField(selectedGroup, GridData.FILL_HORIZONTAL, Messages.undefined);
            GridData data = new GridData(GridData.FILL_HORIZONTAL);
            data.heightHint = convertHeightInCharsToPixels(1);
            data.verticalAlignment=GridData.CENTER;
            this.nameText.setLayoutData(data);
            this.nameText.setEditable(false);
            this.nameText.setBackground(panel.getBackground());
            this.nameText.setText(Messages.undefined);

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
                this.nameText.setText(Messages.undefined);
                this.name = null;
                updateOnSelection(null);
                return;
            }

            Object firstElement = selection.getFirstElement();

            if( isValidSelection(firstElement) ) {
                EObject target = (EObject)selection.getFirstElement();
                name = getFullTargetName(target);
                this.nameText.setText(name);
            } else {
                this.nameText.setText(Messages.undefined);
                this.name = null;
            }

            updateOnSelection(firstElement);
        }
        
        private String getFullTargetName(EObject target) {
        	String targetName = getResourceName(target.eResource()) + '/' + ModelerCore.getModelEditor().getModelRelativePath(target);

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
        
        private boolean isValidSelection(Object obj ) {
        	return (obj instanceof org.teiid.designer.metamodels.relational.Table || 
        			obj instanceof org.teiid.designer.metamodels.relational.View ||
        			obj instanceof org.teiid.designer.metamodels.relational.Procedure);
        }

        private void updateOnSelection( Object selectedObject ) {
            IStatus status = new Status(IStatus.INFO,
            		RolesUiPlugin.PLUGIN_ID, Messages.rowBasedSecurityOkMessage);
            if (selectedObject != null) {
                if (!isValidSelection(selectedObject)) {
                    status = new Status(IStatus.ERROR,
                    		RolesUiPlugin.PLUGIN_ID, Messages.invalidSelectionRowBasedSecurityMessage);
                    getOkButton().setEnabled(false);
                } else {
                    getOkButton().setEnabled(true);
                }
            } else {
                status = new Status(IStatus.ERROR,
                		RolesUiPlugin.PLUGIN_ID, Messages.noTargetSelected);
                getOkButton().setEnabled(false);
            }

            this.statusMessageLabel.setErrorStatus(status);
        }

        public String getTargetName() {
            return this.name;
        }

    }
}
