/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.roles.Crud;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.internal.widget.IListPanelController;
import com.metamatrix.ui.internal.widget.ListPanel;
import com.metamatrix.ui.internal.widget.ListPanelAdapter;
import com.metamatrix.ui.internal.wizard.AbstractWizard;
import com.metamatrix.ui.text.StyledTextEditor;

public class NewDataRoleWizard extends AbstractWizard {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(NewDataRoleWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String EDIT_TITLE = getString("editTitle"); //$NON-NLS-1$

    private static final ImageDescriptor IMAGE = RolesUiPlugin.getInstance().getImageDescriptor("icons/full/wizban/dataPolicyWizard.png"); //$NON-NLS-1$
    
    private static final String DEFAULT_NAME = "Data Role 1"; //getString("undefinedName"); //$NON-NLS-1$
    private static final String SYS_TABLE_TARGET = "sys"; //$NON-NLS-1$
    private static final String PG_CATALOG_TARGET = "pg_catalog"; //$NON-NLS-1$

    private static String getString( final String id ) {
        return RolesUiPlugin.UTIL.getString(I18N_PREFIX + id);
    }
    
    private DataRole dataRole;
    private Container tempContainer;
    
    private boolean isEdit = false;
    private WizardPage wizardPage;
    private Text dataRoleNameText;
    private StyledTextEditor descriptionTextEditor;
    private TreeViewer treeViewer;
    private ListPanel mappedRolesPanel;
    private Button allowSystemTablesCheckBox;

    private DataRolesModelTreeProvider treeProvider;
    
    private String dataRoleName;
    private String description;
    private Set<String> mappedRoleNames;
    private boolean allowSystemTables;

    String roleNameTextEntry;
    
    private Map<Object, Permission> permissionsMap;
    
	/**
     * @since 4.0
     */
    public NewDataRoleWizard(Container tempContainer, DataRole existingDataRole) {
    	super(RolesUiPlugin.getInstance(), TITLE, IMAGE);
    	this.tempContainer = tempContainer;
    	if( existingDataRole == null ) {
    		this.dataRole = new DataRole(DEFAULT_NAME);
    		this.dataRoleName = DEFAULT_NAME;
    		this.isEdit = false;
    		this.allowSystemTables = true;
    	} else {
    		this.dataRole = existingDataRole;
    		this.isEdit = true;
    		this.setWindowTitle(EDIT_TITLE);
    	}
        this.mappedRoleNames = new HashSet<String>(dataRole.getRoleNames());
        this.permissionsMap = new HashMap<Object, Permission>();
    }

    /**
     * 
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
    	this.wizardPage = new WizardPage(NewDataRoleWizard.class.getSimpleName(), TITLE, null) {
            public void createControl( final Composite parent ) {
                setControl(createPageControl(parent));
            }
        };
        
        this.wizardPage.setPageComplete(false);
        if( isEdit ) {
        	this.wizardPage.setMessage(getString("initialEditMessage")); //$NON-NLS-1$
        	this.wizardPage.setTitle(EDIT_TITLE);
        } else {
        	this.wizardPage.setMessage(getString("initialCreateMessage")); //$NON-NLS-1$
        }
        addPage(wizardPage);
    	
    }
    
    /**
     * @param parent 
     * @return composite the page
     * @since 4.0
     */
	Composite createPageControl( final Composite parent ) {
    	// Tree Content Provider
    	
    	treeProvider = new DataRolesModelTreeProvider(this.permissionsMap);
    	
    	
        // ===========>>>> Create page composite
        final Composite pg = new Composite(parent, SWT.NONE);
        GridData pgGD = new GridData(GridData.FILL_BOTH);
        pg.setLayoutData(pgGD);
        pg.setLayout(new GridLayout(2, false));
        // Add widgets to page
        WidgetFactory.createLabel(pg, getString("nameLabel")); //$NON-NLS-1$
        
        this.dataRoleNameText = WidgetFactory.createTextField(pg, GridData.FILL_HORIZONTAL, 1, DEFAULT_NAME);
        
        this.dataRoleNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dataRoleName = dataRoleNameText.getText();
				validateInputs();
			}
		});
        
        // ===========>>>> Create Description Group 
        final Group descGroup = WidgetFactory.createGroup(pg, getString("desciptionGroupLabel"), GridData.FILL_HORIZONTAL, 2); //$NON-NLS-1$
        descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
        final GridData descGridData = new GridData(GridData.FILL_BOTH);
        descGridData.horizontalSpan = 1;
        descGridData.heightHint = 50;
        descGridData.minimumHeight = 30;
        descGridData.grabExcessVerticalSpace = true;
        descriptionTextEditor.setLayoutData(descGridData);
        descriptionTextEditor.setText(""); //$NON-NLS-1$
        descriptionTextEditor.getDocument().addDocumentListener(new IDocumentListener() {
			
			@Override
			public void documentChanged(DocumentEvent event) {
				description = descriptionTextEditor.getText();
			}
			
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				// NO OP
			}
		});

        // ===========>>>> Create Roles List Panel Editor 
        final IListPanelController ctrlr = new ListPanelAdapter() {
            @Override
            public Object[] addButtonSelected() {
                String title = getString("roleInputDialog.title"); //$NON-NLS-1$
                String label = getString("roleInputDialog.label"); //$NON-NLS-1$
                String textEntry = showTextEntryDialog(title, label, null);
                if( textEntry != null && textEntry.length() > 0 ) {
                	mappedRoleNames.add(textEntry);
                } else {
                	return Collections.EMPTY_LIST.toArray();
                }
                return new String[] {textEntry};
            }

            @Override
            public Object[] removeButtonSelected( IStructuredSelection selection ) {
                Object[] objArray = selection.toArray();
                for( Object obj : objArray ) {
                	mappedRoleNames.remove(obj);
                }
                return objArray;
            }
            
            @Override
            public Object editButtonSelected( IStructuredSelection selection ) {
                Object[] objArray = selection.toArray();
                String title = getString("roleInputDialog.title"); //$NON-NLS-1$
                String label = getString("roleInputDialog.label"); //$NON-NLS-1$
                String textEntry = showTextEntryDialog(title, label, (String)objArray[0]);
                if( textEntry != null && textEntry.length() > 0 ) {
                	mappedRoleNames.add(textEntry);
                }else {
                	return null;
                }
                
                return textEntry;
            }
            
        	@Override
            public void itemsSelected(final IStructuredSelection selection) {
        		Object[] objArray = selection.toArray();
        		boolean enableEdit = true;
        		if( objArray == null ) {
        			enableEdit = false;
        		} else if( objArray.length == 1 ) {
        			enableEdit = false;
        		} else {
        			enableEdit = true;
        		}
        		mappedRolesPanel.getButton(ListPanel.EDIT_BUTTON).setEnabled(enableEdit);
        	}
        };
        mappedRolesPanel = new ListPanel(pg, getString("dataRolesGroupLabel"), ctrlr, SWT.H_SCROLL | SWT.V_SCROLL, 2);  //$NON-NLS-1$

        final GridData rolesGridData = new GridData(GridData.FILL_BOTH);
        rolesGridData.horizontalSpan = 2;
        rolesGridData.heightHint = 120;
        rolesGridData.minimumHeight = 120;
        rolesGridData.grabExcessVerticalSpace = true;
        mappedRolesPanel.setLayoutData(rolesGridData);
        mappedRolesPanel.getButton(ListPanel.EDIT_BUTTON).setEnabled(false);
        mappedRolesPanel.getButton(ListPanel.REMOVE_BUTTON).setEnabled(false);
        
        // ===========>>>> Create Relational Models Tree Viewer/Editor
        Group group = WidgetFactory.createGroup(pg, getString("relationalModelsGroup"),  //$NON-NLS-1$
        		GridData.FILL_BOTH, 2, 2);
        
        final GridData modelsGridData = new GridData(GridData.FILL_BOTH);
        modelsGridData.horizontalSpan = 2;
        modelsGridData.heightHint = 220;
        modelsGridData.minimumHeight = 220;
        modelsGridData.grabExcessVerticalSpace = true;
        group.setLayoutData(modelsGridData);
        
        treeViewer = new TreeViewer(group,  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        Tree tree = treeViewer.getTree();

        final GridData gridData = new GridData(GridData.FILL_BOTH); //SWT.FILL, SWT.FILL, true, true);
        gridData.grabExcessHorizontalSpace = true;
        treeViewer.getControl().setLayoutData(gridData);
        
        tree.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// NO OP
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				Point pt = new Point(e.x, e.y);
				if( treeViewer.getCell(pt) != null && 
					treeViewer.getCell(pt).getViewerRow() != null &&
					treeViewer.getCell(pt).getViewerRow().getItem() != null ) {
					handleSelection((treeViewer.getCell(pt).getColumnIndex()), treeViewer.getCell(pt).getViewerRow().getItem().getData());
				}
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// NO OP
			}
		});

        treeViewer.setUseHashlookup(true);

        /*** Tree table specific code starts ***/

        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        TreeColumn treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText("columnLabel.model"); //$NON-NLS-1$

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(getString("columnLabel.create")); //$NON-NLS-1$
        treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(getString("columnLabel.read")); //$NON-NLS-1$
        treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(getString("columnLabel.update")); //$NON-NLS-1$
        treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(getString("columnLabel.delete")); //$NON-NLS-1$

        TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(60));
        layout.addColumnData(new ColumnWeightData(10));
        layout.addColumnData(new ColumnWeightData(10));
        layout.addColumnData(new ColumnWeightData(10));
        layout.addColumnData(new ColumnWeightData(10));

        tree.setLayout(layout); 

        treeViewer.setContentProvider(treeProvider);
        treeViewer.setLabelProvider(treeProvider);

        treeViewer.setInput(tempContainer);

        final Group sysTablesGroup = WidgetFactory.createGroup(pg,
                                                               getString("systemTablesAccess.label"), GridData.FILL_HORIZONTAL, 2, 2); //$NON-NLS-1$

        // ===========>>>>
        allowSystemTablesCheckBox = WidgetFactory.createCheckBox(sysTablesGroup,
                                                                 getString("systemTablesCheckbox.label"), 0, 2, allowSystemTables); //$NON-NLS-1$
        allowSystemTablesCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                allowSystemTables = allowSystemTablesCheckBox.getSelection();
            }
        });

        // ===========>>>> If we're in edit mode, load the UI objects with the info from the input dataRole
        if( isEdit )  {
        	loadExistingPermissions();
        }
        
        return pg;
    }
    
    private void loadExistingPermissions() {
    	this.dataRoleName = this.dataRole.getName();
    	this.dataRoleNameText.setText(this.dataRole.getName());
    	this.mappedRolesPanel.addItems(this.dataRole.getRoleNames().toArray());
    	this.descriptionTextEditor.setText(this.dataRole.getDescription());
    	
    	for( Permission perm : dataRole.getPermissions() ) {
    		Object obj = treeProvider.getPermissionTargetObject(perm);
    		if( obj != null ) {
    			if( obj instanceof Resource ) {
    				perm.setPrimary(true);
    			}
    			this.permissionsMap.put(obj, perm);
    		} else if (perm.getTargetName().equalsIgnoreCase(SYS_TABLE_TARGET)
                       || perm.getTargetName().equalsIgnoreCase(PG_CATALOG_TARGET)) {
                allowSystemTables = true;
                allowSystemTablesCheckBox.setSelection(allowSystemTables);
            }
    	}
    	
    	treeViewer.refresh();
    	
    	validateInputs();
    }
    

    /*
     * This method tells the Tree Provider that 
     */
    private void handleSelection(int column, Object rowData) {
    	if( column > 0 ) {
    		Crud.Type crudType = Crud.getCrudType(column);
    		this.treeProvider.togglePermission(rowData, crudType);
    	}
    	
    	treeViewer.refresh();
    	
    	validateInputs();
    }
    
	/**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        boolean canFinish = true;
        
        
        return canFinish;
    }
    
	@Override
	public boolean finish() {
		return true;
	}

    public DataRole getDataRole() {
    	if( dataRole != null ) {
	    	dataRole.setName(this.dataRoleName);
	    	dataRole.setDescription(this.description);
	    	dataRole.setPermissions(permissionsMap.values());
	    	if (allowSystemTables) {
                dataRole.addPermission(new Permission(SYS_TABLE_TARGET, false, true, false, false));
                dataRole.addPermission(new Permission(PG_CATALOG_TARGET, false, true, false, false));
            }
	    	if( !mappedRoleNames.isEmpty() ) {
	    		dataRole.setRoleNames(mappedRoleNames);
	    	}
    	}
		return dataRole;
	}

	public void setDataRole(DataRole dataRole) {
		this.dataRole = dataRole;
	}
	
	private void validateInputs() {
		// Check that name != null
		if( this.dataRoleName == null || this.dataRoleName.length() == 0 ) {
			wizardPage.setErrorMessage(getString("nullNameMessage")); //$NON-NLS-1$
			wizardPage.setPageComplete(false);
		} else {
			wizardPage.setErrorMessage(null);
			wizardPage.setMessage(getString("okToFinishMessage")); //$NON-NLS-1$
			wizardPage.setPageComplete(true);
		}
		
	}
	
    /**
     * Show a simple text entry dialog and get the result
     * 
     * @param title the title of the dialog
     * @param label the label text
     * @return the entered string data
     */
    String showTextEntryDialog( final String title,
                                final String label,
                                String initialText) {
        // Dialog for string entry
    	final String text = initialText;
        Shell shell = this.wizardPage.getShell();
        final Dialog dlg = new Dialog(shell, title) {
            @Override
            protected Control createDialogArea( final Composite parent ) {
                final Composite dlgPanel = (Composite)super.createDialogArea(parent);
                dlgPanel.setLayoutData(new GridData(400, 80));
                Group group = WidgetFactory.createGroup(dlgPanel, label,
                		GridData.FILL_BOTH, 1, 1);
                final Text nameText = WidgetFactory.createTextField(group, GridData.FILL_HORIZONTAL);
                if( text != null ) {
                	nameText.setText(text);
                }
                nameText.setSelection(0);
                nameText.addModifyListener(new ModifyListener() {
                    public void modifyText( final ModifyEvent event ) {
                        handleModifyText(nameText);
                    }
                });
                return dlgPanel;
            }

            @Override
            protected void createButtonsForButtonBar( final Composite parent ) {
                super.createButtonsForButtonBar(parent);
                getButton(IDialogConstants.OK_ID).setEnabled(false);
            }

            void handleModifyText( Text nameText ) {
                final String newName = nameText.getText();
                final boolean valid = (newName.length() > 0);
                getButton(IDialogConstants.OK_ID).setEnabled(valid);
                if (valid) {
                	roleNameTextEntry = nameText.getText();
                }
            }
        };
        if (dlg.open() == Window.OK) {
            return this.roleNameTextEntry;
        }
        return null;
    }

}
