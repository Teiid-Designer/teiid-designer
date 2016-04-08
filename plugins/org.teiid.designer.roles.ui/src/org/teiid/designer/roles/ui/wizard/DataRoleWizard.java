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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.roles.Crud.Type;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.roles.ui.wizard.panels.AllowedLanguagesPanel;
import org.teiid.designer.roles.ui.wizard.panels.ColumnMaskingPanel;
import org.teiid.designer.roles.ui.wizard.panels.CrudPanel;
import org.teiid.designer.roles.ui.wizard.panels.RowBasedSecurityPanel;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.InternalUiConstants.Widgets;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.ui.common.widget.IListPanelController;
import org.teiid.designer.ui.common.widget.ListPanel;
import org.teiid.designer.ui.common.widget.ListPanelAdapter;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.vdb.AllowedLanguages;

/**
 * @since 8.0
 */
public class DataRoleWizard extends AbstractWizard {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(DataRoleWizard.class);

    private static final String TITLE = Messages.dataRoleWizardTitle;
    private static final String EDIT_TITLE = Messages.editDataRoleWizardEditTitle;

    private static final ImageDescriptor IMAGE = RolesUiPlugin.getInstance().getImageDescriptor("icons/full/wizban/dataPolicyWizard.png"); //$NON-NLS-1$

    private static final String GENERATED_DEFAULT_NAME = "Data Role"; //getString("undefinedName"); //$NON-NLS-1$
    private static final String SYS_ADMIN_TABLE_TARGET = "sysadmin"; //$NON-NLS-1$
    private static final String SYS_TABLE_TARGET = "sys"; //$NON-NLS-1$

    private static String getString( final String id ) {
        return RolesUiPlugin.UTIL.getString(I18N_PREFIX + id);
    }

    private DataRole existingDataRole;
    private DataRole editedDataRole;

    private Container tempContainer;

    private boolean isEdit = false;
    private WizardPage wizardPage;
    
    private CTabItem descTabItem;
    private CTabItem permissionsTabItem;
    private CTabItem roleNamesTabItem;
    private CTabItem optionsTabItem;
    
    private Text dataRoleNameText;
    private StyledTextEditor descriptionTextEditor;
    
    private Button allowSystemTablesCheckBox;
    private Button readSysCB;
    private Button executeSysCB;
    private Button anyAuthenticatedCheckBox;
    private Button allowCreateTempTablesCheckBox;
    private Button grantAllCheckBox;
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
    private boolean allowSystemRead;
    private boolean allowSystemExecute;
    
    private boolean anyAuthentication;
    private boolean allowCreateTempTables;
    private boolean grantAll;

    String roleNameTextEntry;
    
    private AllowedLanguages allowedLanguages;
    private Set<String> otherDataRoleNames;
    
    private boolean disableGrantAll = false;

    /**
     * @since 4.0
     */
    public DataRoleWizard(Container tempContainer, DataRole existingDataRole, AllowedLanguages allowedLanguages, Set<String> otherDataRoleNames) {
        super(RolesUiPlugin.getInstance(), TITLE, IMAGE);
        this.tempContainer = tempContainer;
        this.allowedLanguages = allowedLanguages;
        this.otherDataRoleNames = otherDataRoleNames;
        this.existingDataRole = existingDataRole;
        
        if (existingDataRole == null) {
            this.dataRoleName = StringUtilities.getUniqueName(GENERATED_DEFAULT_NAME, otherDataRoleNames, true, true, 1000);
            this.isEdit = false;
            this.allowSystemTables = true;
            this.anyAuthentication = false;
            this.allowCreateTempTables = false;
            this.grantAll = false;
            this.mappedRoleNames = new HashSet<String>();
            this.editedDataRole = new DataRole(this.dataRoleName);
        } else {
            this.editedDataRole = this.existingDataRole.clone();
            this.dataRoleName = existingDataRole.getName();
            this.description = existingDataRole.getDescription();
            this.allowCreateTempTables = existingDataRole.isAllowCreateTempTables();
            this.anyAuthentication = existingDataRole.isAnyAuthenticated();
            this.grantAll = existingDataRole.isGrantAll();
            this.allowSystemTables = existingDataRole.getPermission(SYS_ADMIN_TABLE_TARGET) != null;
            this.isEdit = true;
            this.setWindowTitle(EDIT_TITLE);
            this.mappedRoleNames = new HashSet<String>(existingDataRole.getRoleNames());
        }
        
        disableGrantAll = ModelerCore.getTeiidServerManager().getDefaultServer() == null || ModelerCore.getTeiidServerVersion().isLessThan(Version.TEIID_8_7);
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

        this.dataRoleNameText = WidgetFactory.createTextField(mainPanel, GridData.FILL_HORIZONTAL, 1, this.dataRoleName);

        this.dataRoleNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                dataRoleName = dataRoleNameText.getText();
                validateInputs();
            }
        });
        
        new Label(mainPanel, SWT.NONE);
        
        
        CTabFolder mainTabFolder = WidgetFactory.createTabFolder(mainPanel, SWT.NONE);
        mainTabFolder.setLayout(new GridLayout(1, false));
        mainTabFolder.setBorderVisible(true);
        final GridData folderGD = new GridData(GridData.FILL_BOTH);
        folderGD.horizontalSpan = 2;
        mainTabFolder.setLayoutData(folderGD);

        createPermissionsTab(mainTabFolder);
        
        createOptionsTab(mainTabFolder);
        
        createRoleNamesTab(mainTabFolder);
        
        createDesciptionTab(mainTabFolder);
        
        mainTabFolder.setSelection(this.permissionsTabItem);

        // ===========>>>> If we're in edit mode, load the UI objects with the info from the input dataRole
        if (isEdit) {
            loadExistingPermissions();
        }

        return mainPanel;
    }
    
    void createDesciptionTab(CTabFolder mainTabFolder) {
    	descTabItem = new CTabItem(mainTabFolder, SWT.NONE);
    	descTabItem.setText(Messages.desciption);
    	
    	Composite descPanel = new Composite(mainTabFolder, SWT.NONE);
    	GridLayoutFactory.fillDefaults().applyTo(descPanel);

        // ===========>>>> Create Description Group
        descriptionTextEditor = new StyledTextEditor(descPanel, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 60).minSize(SWT.DEFAULT, 30).applyTo(descriptionTextEditor.getTextWidget());

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
 
        descTabItem.setControl(descPanel);
    }
    
    void createPermissionsTab(CTabFolder mainTabFolder) {
    	permissionsTabItem = new CTabItem(mainTabFolder, SWT.NONE);
    	permissionsTabItem.setText(Messages.permissions);
        // ===========>>>> Create Relational Models Tree Viewer/Editor
    	
        Composite permissionsPanel = WidgetFactory.createPanel(mainTabFolder, GridData.FILL_BOTH, 2, 2);

        final GridData modelsGridData = new GridData(GridData.FILL_BOTH);
        modelsGridData.horizontalSpan = 2;
        modelsGridData.heightHint = 220;
        modelsGridData.minimumHeight = 220;
        modelsGridData.grabExcessVerticalSpace = true;
        modelsGridData.horizontalIndent = 5;
        modelsGridData.verticalIndent = 5;
        permissionsPanel.setLayoutData(modelsGridData);
        
        Label infoLabel = new Label(permissionsPanel, SWT.NONE);
        infoLabel.setText(Messages.permissionsTabHelpText);
        infoLabel.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        GridData gd_1 = new GridData();
        gd_1.horizontalSpan = 2;
        infoLabel.setLayoutData(gd_1);
        new Label(permissionsPanel, SWT.NONE);
        
        CTabFolder tabFolder = WidgetFactory.createTabFolder(permissionsPanel);
        tabFolder.setBorderVisible(true);
        
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
		
		permissionsTabItem.setControl(permissionsPanel);
    }
    
    void createRoleNamesTab(CTabFolder mainTabFolder) {
    	roleNamesTabItem = new CTabItem(mainTabFolder, SWT.NONE);
    	roleNamesTabItem.setText(Messages.mappedRoleNames);
    	roleNamesTabItem.setToolTipText(Messages.mappedRoleNamesTabDescription);
        Composite roleNamespanel = WidgetFactory.createPanel(mainTabFolder, 0, GridData.FILL_HORIZONTAL, 2);
		anyAuthenticatedCheckBox = WidgetFactory.createCheckBox(roleNamespanel,
						getString("anyAuthenticatedCheckbox.label"), 0, 1, anyAuthentication); //$NON-NLS-1$
		anyAuthenticatedCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				anyAuthentication = anyAuthenticatedCheckBox.getSelection();
				if (mappedRolesPanel != null) {
					mappedRolesPanel.setEnabled(!anyAuthentication);
				}
			}
		});
    	
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
        mappedRolesPanel = new ListPanel(roleNamespanel, " ", ctrlr, SWT.H_SCROLL | SWT.V_SCROLL, 2); //$NON-NLS-1$

        final GridData rolesGridData = new GridData(GridData.FILL_BOTH);
        rolesGridData.horizontalSpan = 2;
        rolesGridData.heightHint = 80;
        rolesGridData.minimumHeight = 80;
        rolesGridData.grabExcessVerticalSpace = true;
        mappedRolesPanel.setLayoutData(rolesGridData);
        mappedRolesPanel.getButton(Widgets.EDIT_BUTTON).setEnabled(false);
        mappedRolesPanel.getButton(Widgets.REMOVE_BUTTON).setEnabled(false);

        mappedRolesPanel.setEnabled(!anyAuthentication);
        roleNamesTabItem.setControl(roleNamespanel);
    }
    
    void createOptionsTab(CTabFolder mainTabFolder) {
    	optionsTabItem = new CTabItem(mainTabFolder, SWT.NONE);
    	optionsTabItem.setText(Messages.options);
    	optionsTabItem.setToolTipText(Messages.optionsTabDescription);
    	final Composite miscOptionsGroup = WidgetFactory.createPanel(mainTabFolder, GridData.FILL_HORIZONTAL, 2, 3);
        allowCreateTempTablesCheckBox = WidgetFactory.createCheckBox(miscOptionsGroup,
                                                                     getString("allowCreateTempTablesCheckBox.label"), GridData.FILL_HORIZONTAL, 1, anyAuthentication); //$NON-NLS-1$
        allowCreateTempTablesCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                allowCreateTempTables = allowCreateTempTablesCheckBox.getSelection();
            }
        });
        
        grantAllCheckBox = WidgetFactory.createCheckBox( miscOptionsGroup, getString("grantAllCheckBox.label"), GridData.FILL_HORIZONTAL, 1, grantAll); //$NON-NLS-1$
   	 	if( disableGrantAll ) {
   	   	 	grantAllCheckBox.setEnabled(false);
   	   	 	grantAllCheckBox.setText(getString("grantAllCheckBox.label") + StringConstants.SPACE + getString("grantAllCheckBoxDisabled.tooltip") );	//$NON-NLS-1$ //$NON-NLS-2$
    	} else {
	        grantAllCheckBox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					grantAll = grantAllCheckBox.getSelection();
				}
			});
    	}

        
        final Group sysTablesGroup = WidgetFactory.createGroup(miscOptionsGroup,
                getString("systemTablesAccess.label"), GridData.FILL_HORIZONTAL, 2, 2); //$NON-NLS-1$

		// ===========>>>>
		allowSystemTablesCheckBox = WidgetFactory.createCheckBox(sysTablesGroup,
						getString("systemTablesCheckbox.label"), 0, 2, allowSystemTables); //$NON-NLS-1$
		allowSystemTablesCheckBox.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						allowSystemTables = allowSystemTablesCheckBox
								.getSelection();
						readSysCB.setEnabled(allowSystemTables);
						executeSysCB.setEnabled(allowSystemTables);
						// If turned ON (checked) then auto-select READ
						if (allowSystemTables) {
							readSysCB.setSelection(true);
							allowSystemRead = true;
						}
					}
				});

		final Group sysTablesPermissions = WidgetFactory.createGroup(sysTablesGroup, StringConstants.EMPTY_STRING,
				GridData.FILL_HORIZONTAL, 2, 1);

		readSysCB = WidgetFactory.createCheckBox(sysTablesPermissions, Messages.read.toUpperCase(), 0, 1, false);
		readSysCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				allowSystemRead = readSysCB.getSelection();
				if (!allowSystemRead && !allowSystemExecute) {
					allowSystemTables = false;
					allowSystemTablesCheckBox.setSelection(false);
					allowSystemTables = false;
				} else {
					allowSystemTablesCheckBox.setSelection(true);
					allowSystemTables = true;
				}
			}
		});
		executeSysCB = WidgetFactory.createCheckBox(sysTablesPermissions, Messages.execute.toUpperCase(), 0, 1, false);
		executeSysCB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				allowSystemExecute = executeSysCB.getSelection();
				if (!allowSystemRead && !allowSystemExecute) {
					allowSystemTables = false;
					allowSystemTablesCheckBox.setSelection(false);
					allowSystemTables = false;
				} else {
					allowSystemTablesCheckBox.setSelection(true);
					allowSystemTables = true;
				}
			}
		});
        
        optionsTabItem.setControl(miscOptionsGroup);
    }
    
    public void refreshAllTabs() {
    	this.crudPanel.refresh();
    	this.rowBasedSecurityPanel.refresh();
    	this.columnMaskingPanel.refresh();
    }

    private void loadExistingPermissions() {
        this.dataRoleName = this.existingDataRole.getName();
        this.anyAuthentication = this.existingDataRole.isAnyAuthenticated();
        this.allowCreateTempTables = this.existingDataRole.isAllowCreateTempTables();
        this.grantAll = this.existingDataRole.isGrantAll();
        this.dataRoleNameText.setText(this.existingDataRole.getName());
        this.mappedRolesPanel.addItems(this.existingDataRole.getRoleNames().toArray());
        this.descriptionTextEditor.setText(this.existingDataRole.getDescription());
        
        
        this.treeProvider.loadPermissions(existingDataRole.getPermissions());
        
        boolean foundSystemPermission = false;
        
        for (Permission perm : existingDataRole.getPermissions()) {
            if (perm.getTargetName().equalsIgnoreCase(SYS_ADMIN_TABLE_TARGET) ||
    				perm.getTargetName().equalsIgnoreCase(SYS_TABLE_TARGET)) { // This is for backward compatability
                allowSystemTables = true;
                allowSystemTablesCheckBox.setSelection(allowSystemTables);
                
                foundSystemPermission = true;

                allowSystemRead = perm.getCRUDValue(Type.READ).booleanValue();
                readSysCB.setSelection(allowSystemRead);
                allowSystemExecute = perm.getCRUDValue(Type.EXECUTE).booleanValue();
                executeSysCB.setSelection(allowSystemExecute);
                
                readSysCB.setEnabled(allowSystemTables);
                executeSysCB.setEnabled(allowSystemTables);
            }
        }
        
        if( !foundSystemPermission ) {
        	// Need to set system check-box to unchecked
        	// read and execute check boxes to unchecked and disabled
        	allowSystemTables = false;
        	allowSystemTablesCheckBox.setSelection(false);
            readSysCB.setEnabled(allowSystemTables);
            executeSysCB.setEnabled(allowSystemTables);
        }

        this.anyAuthenticatedCheckBox.setSelection(this.anyAuthentication);
        this.allowCreateTempTablesCheckBox.setSelection(this.allowCreateTempTables);
        this.mappedRolesPanel.setEnabled(!anyAuthentication);
        if( !disableGrantAll ) {
        	this.grantAllCheckBox.setEnabled(true);
        }

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
    	editedDataRole.setName(this.dataRoleName);
    	editedDataRole.setAnyAuthenticated(this.anyAuthentication);
    	editedDataRole.setAllowCreateTempTables(this.allowCreateTempTables);
    	editedDataRole.setGrantAll(this.grantAll);
    	editedDataRole.setDescription(this.description);
    	editedDataRole.setPermissions(this.treeProvider.getPermissions());
    	
    	Permission systemPerm = editedDataRole.getPermission(SYS_ADMIN_TABLE_TARGET);
        if (allowSystemTables ) {
        	if( systemPerm == null ) {
        		editedDataRole.addPermission(new Permission(SYS_ADMIN_TABLE_TARGET,
        				false, allowSystemRead, false, 
        				false, allowSystemExecute, false));
        	}
        } else {
        	if( systemPerm != null ) {
        		editedDataRole.removePermission(systemPerm);
        	}
        }
        if (!this.anyAuthentication && !mappedRoleNames.isEmpty()) {
        	editedDataRole.setRoleNames(mappedRoleNames);
        } else {
        	editedDataRole.getRoleNames().clear();
        }
        
        return this.editedDataRole;
    }
    
    public DataRolesModelTreeProvider getTreeProvider() {
    	return this.treeProvider;
    }
    
    public Container getTempContainer() {
    	return this.tempContainer;
    }
    
    public AllowedLanguages getAllowedLanguages() {
    	return allowedLanguages;
    }

    public void validateInputs() {
        // Check that name != null
        if (this.dataRoleName == null || this.dataRoleName.length() == 0) {
            wizardPage.setErrorMessage(getString("nullNameMessage")); //$NON-NLS-1$
            wizardPage.setPageComplete(false);
            return;
        }

        // Check if data role already exists
        if( otherDataRoleNames.contains(this.dataRoleName)) {
            wizardPage.setErrorMessage(NLS.bind(Messages.dataRoleExists_0_Message, this.dataRoleName));
            wizardPage.setPageComplete(false);
            return;
        }
        
        wizardPage.setErrorMessage(null);
        wizardPage.setMessage(getString("okToFinishMessage")); //$NON-NLS-1$
        wizardPage.setPageComplete(true);

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
