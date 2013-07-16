/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.roles.ui.wizard.panels.AllowedLanguagesPanel;
import org.teiid.designer.roles.ui.wizard.panels.ColumnMaskingPanel;
import org.teiid.designer.roles.ui.wizard.panels.CrudPanel;
import org.teiid.designer.roles.ui.wizard.panels.RowBasedSecurityPanel;
import org.teiid.designer.ui.common.InternalUiConstants.Widgets;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.ui.common.widget.IListPanelController;
import org.teiid.designer.ui.common.widget.ListPanel;
import org.teiid.designer.ui.common.widget.ListPanelAdapter;
import org.teiid.designer.ui.common.wizard.AbstractWizard;

/**
 * @since 8.0
 */
public class DataRoleWizard extends AbstractWizard {
    private static final String I18N_PREFIX = "NewDataRoleWizard."; //$NON-NLS-1$

    private static final String TITLE = Messages.dataRoleWizardTitle;
    private static final String EDIT_TITLE = Messages.editDataRoleWizardEditTitle;

    private static final ImageDescriptor IMAGE = RolesUiPlugin.getInstance().getImageDescriptor("icons/full/wizban/dataPolicyWizard.png"); //$NON-NLS-1$

    private static final String DEFAULT_NAME = "Data Role 1"; //getString("undefinedName"); //$NON-NLS-1$
    private static final String SYS_ADMIN_TABLE_TARGET = "sysadmin"; //$NON-NLS-1$
    private static final String SYS_TABLE_TARGET = "sys"; //$NON-NLS-1$

    private static String getString( final String id ) {
        return RolesUiPlugin.UTIL.getString(I18N_PREFIX + id);
    }

    private DataRole existingDataRole;
//    private DataRole dataRole;
    private Container tempContainer;

    private boolean isEdit = false;
    private WizardPage wizardPage;
    private Text dataRoleNameText;
    private StyledTextEditor descriptionTextEditor;
    
    private Button allowSystemTablesCheckBox;
    private Button anyAuthenticatedCheckBox;
    private Button allowCreateTempTablesCheckBox;
    private ListPanel mappedRolesPanel;
    
    private CTabItem crudTabItem;
    private CrudPanel crudPanel;
    private CTabItem rowBasedCTabItem;
    private RowBasedSecurityPanel rowBasedSecurityPanel;
    private CTabItem columnsMaskingCTabItem;
    private ColumnMaskingPanel columnMaskingPanel;
    private CTabItem allowedLanguagesCTabItem;
    private AllowedLanguagesPanel allowLanguagesPanel;
    
    private DataRolesModelTreeProvider treeProvider;

    private String dataRoleName;
    private String description;
    private Set<String> mappedRoleNames;
    private boolean allowSystemTables;
    private boolean anyAuthentication;
    private boolean allowCreateTempTables;

    String roleNameTextEntry;
    
    private Set<String> allowedLanguages;

    /**
     * @since 4.0
     */
    public DataRoleWizard(Container tempContainer, DataRole existingDataRole, Set<String> allowedLanguages) {
        super(RolesUiPlugin.getInstance(), TITLE, IMAGE);
        this.tempContainer = tempContainer;
        this.allowedLanguages = allowedLanguages;
        this.existingDataRole = existingDataRole;
        
        if (existingDataRole == null) {
            this.dataRoleName = DEFAULT_NAME;
            this.isEdit = false;
            this.allowSystemTables = true;
            this.anyAuthentication = false;
            this.allowCreateTempTables = false;
            this.mappedRoleNames = new HashSet<String>();
        } else {
            this.dataRoleName = existingDataRole.getName();
            this.description = existingDataRole.getDescription();
            this.allowCreateTempTables = existingDataRole.allowCreateTempTables();
            this.anyAuthentication = existingDataRole.isAnyAuthenticated();
            this.allowSystemTables = existingDataRole.getPermissionsMap().get(SYS_ADMIN_TABLE_TARGET) != null;
            this.isEdit = true;
            this.setWindowTitle(EDIT_TITLE);
            this.mappedRoleNames = new HashSet<String>(existingDataRole.getRoleNames());
        }
        
    }

    /**
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        this.wizardPage = new WizardPage(DataRoleWizard.class.getSimpleName(), TITLE, null) {
            @Override
            public void createControl( final Composite parent ) {
                setControl(createPageControl(parent));
            }
        };

        this.wizardPage.setPageComplete(false);
        if (isEdit) {
            this.wizardPage.setMessage(Messages.initialNewDataRoleMessage);
            this.wizardPage.setTitle(EDIT_TITLE);
        } else {
            this.wizardPage.setMessage(Messages.initialEditDataRoleMessage);
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

        treeProvider = new DataRolesModelTreeProvider();

        // ===========>>>> Create page composite
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        GridData pgGD = new GridData(GridData.FILL_BOTH);
        mainPanel.setLayoutData(pgGD);
        mainPanel.setLayout(new GridLayout(2, false));
        // Add widgets to page
        WidgetFactory.createLabel(mainPanel, Messages.name);

        this.dataRoleNameText = WidgetFactory.createTextField(mainPanel, GridData.FILL_HORIZONTAL, 1, DEFAULT_NAME);

        this.dataRoleNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                dataRoleName = dataRoleNameText.getText();
                validateInputs();
            }
        });

        // ===========>>>> Create Description Group
        final Group descGroup = WidgetFactory.createGroup(mainPanel, Messages.desciption, GridData.FILL_HORIZONTAL, 2);
        descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        final GridData descGridData = new GridData(GridData.FILL_BOTH);
        descGridData.horizontalSpan = 1;
        descGridData.heightHint = 50;
        descGridData.minimumHeight = 30;
        descGridData.grabExcessVerticalSpace = true;
        descriptionTextEditor.setLayoutData(descGridData);
        if( this.description != null ) {
        	descriptionTextEditor.setText(this.description);
        } else {
        	descriptionTextEditor.setText(""); //$NON-NLS-1$
        }
        descriptionTextEditor.getDocument().addDocumentListener(new IDocumentListener() {

            @Override
            public void documentChanged( DocumentEvent event ) {
                description = descriptionTextEditor.getText();
                handleDescriptionChanged();
            }

            @Override
            public void documentAboutToBeChanged( DocumentEvent event ) {
                // NO OP
            }
        });

        allowCreateTempTablesCheckBox = WidgetFactory.createCheckBox(mainPanel,
                                                                     getString("allowCreateTempTablesCheckBox.label"), 0, 2, anyAuthentication); //$NON-NLS-1$
        allowCreateTempTablesCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                allowCreateTempTables = allowCreateTempTablesCheckBox.getSelection();
            }
        });

        anyAuthenticatedCheckBox = WidgetFactory.createCheckBox(mainPanel,
                                                                getString("anyAuthenticatedCheckbox.label"), 0, 2, anyAuthentication); //$NON-NLS-1$
        anyAuthenticatedCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                anyAuthentication = anyAuthenticatedCheckBox.getSelection();
                if (mappedRolesPanel != null) {
                	mappedRolesPanel.setEnabled(!anyAuthentication);
                }
            }
        });

        { // Role Names panel
	        // ===========>>>> Create Roles List Panel Editor
	        final IListPanelController ctrlr = new ListPanelAdapter() {
	            @Override
	            public Object[] addButtonSelected() {
	                String title = getString("roleInputDialog.title"); //$NON-NLS-1$
	                String label = getString("roleInputDialog.label"); //$NON-NLS-1$
	                String textEntry = showTextEntryDialog(title, label, null);
	                if (textEntry != null && textEntry.length() > 0) {
	                    mappedRoleNames.add(textEntry);
	                    handleMappedRoleNameChanged();
	                } else {
	                    return Collections.EMPTY_LIST.toArray();
	                }
	                return new String[] {textEntry};
	            }
	
	            @Override
	            public Object[] removeButtonSelected( IStructuredSelection selection ) {
	                Object[] objArray = selection.toArray();
	                for (Object obj : objArray) {
	                    mappedRoleNames.remove(obj);
	                }
	                handleMappedRoleNameChanged();
	                return objArray;
	            }
	
	            @Override
	            public Object editButtonSelected( IStructuredSelection selection ) {
	                Object[] objArray = selection.toArray();
	                String title = getString("roleInputDialog.title"); //$NON-NLS-1$
	                String label = getString("roleInputDialog.label"); //$NON-NLS-1$
	                String textEntry = showTextEntryDialog(title, label, (String)objArray[0]);
	                if (textEntry != null && textEntry.length() > 0) {
	                    mappedRoleNames.add(textEntry);
	                    handleMappedRoleNameChanged();
	                } else {
	                    return null;
	                }
	
	                return textEntry;
	            }
	
	            @Override
	            public void itemsSelected( final IStructuredSelection selection ) {
	                Object[] objArray = selection.toArray();
	                boolean enableEdit = true;
	                if (objArray == null) {
	                    enableEdit = false;
	                } else if (objArray.length == 1) {
	                    enableEdit = false;
	                } else {
	                    enableEdit = true;
	                }
	                mappedRolesPanel.getButton(Widgets.EDIT_BUTTON).setEnabled(enableEdit);
	            }
	        };
	        mappedRolesPanel = new ListPanel(mainPanel, getString("dataRolesGroupLabel"), ctrlr, SWT.H_SCROLL | SWT.V_SCROLL, 2); //$NON-NLS-1$
	
	        final GridData rolesGridData = new GridData(GridData.FILL_BOTH);
	        rolesGridData.horizontalSpan = 2;
	        rolesGridData.heightHint = 120;
	        rolesGridData.minimumHeight = 120;
	        rolesGridData.grabExcessVerticalSpace = true;
	        mappedRolesPanel.setLayoutData(rolesGridData);
	        mappedRolesPanel.getButton(Widgets.EDIT_BUTTON).setEnabled(false);
	        mappedRolesPanel.getButton(Widgets.REMOVE_BUTTON).setEnabled(false);
	
	        mappedRolesPanel.setEnabled(!anyAuthentication);
        }

        // ===========>>>> Create Relational Models Tree Viewer/Editor
        Group group = WidgetFactory.createGroup(mainPanel, Messages.permissions,
        		GridData.FILL_BOTH, 2, 2);

        final GridData modelsGridData = new GridData(GridData.FILL_BOTH);
        modelsGridData.horizontalSpan = 2;
        modelsGridData.heightHint = 220;
        modelsGridData.minimumHeight = 220;
        modelsGridData.grabExcessVerticalSpace = true;
        group.setLayoutData(modelsGridData);
        
        CTabFolder tabFolder = WidgetFactory.createTabFolder(group);
        
        { // Models Tab
        	crudTabItem = new CTabItem(tabFolder, SWT.NONE);
        	crudTabItem.setText(Messages.model);
	        
			crudPanel = new CrudPanel(tabFolder, this);

	        crudTabItem.setControl(crudPanel.getPrimaryPanel());
        }
		
		{ // Row-Level Security tab and panel
			rowBasedCTabItem = new CTabItem(tabFolder, SWT.NONE);
			rowBasedCTabItem.setText(Messages.conditions);
	        
	        rowBasedSecurityPanel = new RowBasedSecurityPanel(tabFolder, this);
	        
	        rowBasedCTabItem.setControl(rowBasedSecurityPanel.getPrimaryPanel());
		}
        
		{ // Column Masking tab and panel
			columnsMaskingCTabItem = new CTabItem(tabFolder, SWT.NONE);
			columnsMaskingCTabItem.setText(Messages.columnMasking);
			
	        columnMaskingPanel = new ColumnMaskingPanel(tabFolder, this);

	        columnsMaskingCTabItem.setControl(columnMaskingPanel.getPrimaryPanel());
		}
		
		{ // Allowed Languages tab and panel
			allowedLanguagesCTabItem = new CTabItem(tabFolder, SWT.NONE);
			allowedLanguagesCTabItem.setText(Messages.allowedLanguages);
			
	        allowLanguagesPanel = new AllowedLanguagesPanel(tabFolder, this);
			
	        allowedLanguagesCTabItem.setControl(allowLanguagesPanel.getPrimaryPanel());
		}
		
		tabFolder.setSelection(crudTabItem);

        final Group sysTablesGroup = WidgetFactory.createGroup(mainPanel,
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
        if (isEdit) {
            loadExistingPermissions();
        }

        return mainPanel;
    }
    
    public void refreshAllTabs() {
    	this.crudPanel.refresh();
    	this.rowBasedSecurityPanel.refresh();
    	this.columnMaskingPanel.refresh();
    }

    private void loadExistingPermissions() {
        this.dataRoleName = this.existingDataRole.getName();
        this.anyAuthentication = this.existingDataRole.isAnyAuthenticated();
        this.allowCreateTempTables = this.existingDataRole.allowCreateTempTables();
        this.dataRoleNameText.setText(this.existingDataRole.getName());
        this.mappedRolesPanel.addItems(this.existingDataRole.getRoleNames().toArray());
        this.descriptionTextEditor.setText(this.existingDataRole.getDescription());
        
        
        this.treeProvider.loadPermissions(existingDataRole.getPermissions());
        
        for (Permission perm : existingDataRole.getPermissions()) {
            if (perm.getTargetName().equalsIgnoreCase(SYS_ADMIN_TABLE_TARGET) ||
    				perm.getTargetName().equalsIgnoreCase(SYS_TABLE_TARGET)) { // This is for backward compatability
                allowSystemTables = true;
                allowSystemTablesCheckBox.setSelection(allowSystemTables);
            }
        }

        this.anyAuthenticatedCheckBox.setSelection(this.anyAuthentication);
        this.allowCreateTempTablesCheckBox.setSelection(this.allowCreateTempTables);
        this.mappedRolesPanel.setEnabled(!anyAuthentication);

        refreshAll();

        validateInputs();
    }
    
    private void refreshAll() {
        crudPanel.refresh();
        rowBasedSecurityPanel.refresh();
        columnMaskingPanel.refresh();
        allowLanguagesPanel.refresh();
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
    
    private void handleMappedRoleNameChanged() {
        if (!anyAuthentication && !mappedRoleNames.isEmpty()) {
            // DO NOTHING dataRole.setRoleNames(mappedRoleNames);
        } else {
            mappedRoleNames.clear();
        }
    }
    
    private void handleDescriptionChanged() {
    	// Do Nothing yet
    }

    
    public DataRole getFinalDataRole() {
    	if (existingDataRole == null) {
    		existingDataRole = new DataRole(this.dataRoleName);
    	}

    	existingDataRole.setName(this.dataRoleName);
    	existingDataRole.setAnyAuthenticated(this.anyAuthentication);
    	existingDataRole.setAllowCreateTempTables(this.allowCreateTempTables);
    	existingDataRole.setDescription(this.description);
    	existingDataRole.setPermissions(this.treeProvider.getPermissions());
    	
    	Permission systemPerm = existingDataRole.getPermissionsMap().get(SYS_ADMIN_TABLE_TARGET);
        if (allowSystemTables ) {
        	if( systemPerm == null ) {
        		existingDataRole.addPermission(new Permission(SYS_ADMIN_TABLE_TARGET, false, true, false, false, false, false));
        	}
        } else {
        	if( systemPerm != null ) {
        		existingDataRole.removePermission(systemPerm);
        	}
        }
        if (!this.anyAuthentication && !mappedRoleNames.isEmpty()) {
        	existingDataRole.setRoleNames(mappedRoleNames);
        } else {
        	existingDataRole.getRoleNames().clear();
        }
        
        return this.existingDataRole;
    }
    
    public DataRolesModelTreeProvider getTreeProvider() {
    	return this.treeProvider;
    }
    
    public Container getTempContainer() {
    	return this.tempContainer;
    }
    
    public Set<String> getAllowedLanguages() {
    	return allowedLanguages;
    }

    public void validateInputs() {
        // Check that name != null
        if (this.dataRoleName == null || this.dataRoleName.length() == 0) {
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
                                String initialText ) {
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
                if (text != null) {
                    nameText.setText(text);
                }
                nameText.setSelection(0);
                nameText.addModifyListener(new ModifyListener() {
                    @Override
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
