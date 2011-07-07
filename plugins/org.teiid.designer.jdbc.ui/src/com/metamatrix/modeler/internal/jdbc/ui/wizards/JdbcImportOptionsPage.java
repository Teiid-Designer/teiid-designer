/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.wizards;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.NewFolderDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.teiid.core.util.FileUtils;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.jdbc.CaseConversion;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.SourceNames;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.dialog.FolderSelectionDialog;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.IPersistentWizardPage;

/**
 * @since 4.0
 */
public class JdbcImportOptionsPage extends WizardPage implements
                                                     InternalUiConstants.Widgets,
                                                     IPersistentWizardPage,
                                                     UiConstants {

    // ===========================================================================================================================
    // Constants

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcImportOptionsPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final int COLUMN_COUNT = 3;

    private static final String INITIAL_MESSAGE_ID = "initialMessage"; //$NON-NLS-1$
    private static final String INITIAL_MESSAGE_HIDDEN_PROJECT_ID = "initialMessageHiddenProject"; //$NON-NLS-1$
    private static final String NO_NODEL_TO_UPDATE_MESSAGE_ID = "noModelToUpdateMessage"; //$NON-NLS-1$

    private static final String NAME_LABEL = getString("nameLabel"); //$NON-NLS-1$
    private static final String FOLDER_LABEL = getString("folderLabel"); //$NON-NLS-1$
    private static final String UPDATE_CHECKBOX = getString("updateCheckBox"); //$NON-NLS-1$
    private static final String MODEL_GROUP = getString("modelGroup"); //$NON-NLS-1$
    private static final String CASE_OPTIONS_GROUP = getString("caseOptionsGroup"); //$NON-NLS-1$
    private static final String CHANGE_CASE_GROUP = getString("changeCaseGroup"); //$NON-NLS-1$
    private static final String FULLY_QUALIFIED_CHECKBOX = getString("fullyQualifiedNamesCheckBox"); //$NON-NLS-1$
    private static final String FULLY_QUALIFIED_CHECKBOX_TOOLTIP = getString("fullyQualifiedNamesCheckBox.tooltip"); //$NON-NLS-1$
    private static final String INCLUDE_CATALOG_CHECKBOX = getString("includeCatalogCheckBox"); //$NON-NLS-1$
    private static final String INCLUDE_CATALOG_CHECKBOX_TOOLTIP = getString("includeCatalogCheckBox.tooltip"); //$NON-NLS-1$
    private static final String MODEL_OBJECT_NAMES_GROUP = getString("modelObjectNamesGroup"); //$NON-NLS-1$
    private static final String SOURCE_OBJECT_NAMES_GROUP = getString("sourceObjectNamesGroup"); //$NON-NLS-1$
    private static final String MODIFY_CASE_CHECKBOX = getString("modifyCaseCheckBox"); //$NON-NLS-1$
    private static final String MAKE_ALL_UPPER_RADIO = getString("makeAllUpperRadioButton"); //$NON-NLS-1$
    private static final String MAKE_ALL_LOWER_RADIO = getString("makeAllLowerRadioButton"); //$NON-NLS-1$


    private static final String FILE_EXISTS_MESSAGE = getString("fileExistsMessage", UPDATE_CHECKBOX); //$NON-NLS-1$
    private static final String NOT_MODEL_PROJECT_MESSAGE = getString("notModelProjectMessage"); //$NON-NLS-1$
    private static final String NOT_RELATIONAL_MODEL_MESSAGE = getString("notRelationalModelMessage"); //$NON-NLS-1$
    private static final String READ_ONLY_MODEL_MESSAGE = getString("readOnlyModelMessage"); //$NON-NLS-1$
    private static final String VIRTUAL_MODEL_MESSAGE = getString("virtualModelMessage"); //$NON-NLS-1$

    // ===========================================================================================================================
    // Static Methods

    /**
     * @since 4.0
     */
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.0
     */
    private static String getString(final String id,
                                    final Object parameter) {
        return Util.getString(I18N_PREFIX + id, parameter);
    }

    // ===========================================================================================================================
    // Variables

    private JdbcDatabase db;
    private JdbcImportSettings importSettings;
    private Group changeCaseGroup;
    private Text nameText, folderText;
    private Button updateCheckBox, fullyQualifiedNamesCheckBox, includeCatalogCheckBox, modifyCaseCheckBox,
    	uppercaseButton, lowercaseButton;
    private boolean initd;
    private IContainer folder;
    private boolean usesHiddenProject = false;
    private IFile selectedModel;

    // ===========================================================================================================================
    // Constructors

    /**
     * @param pageName
     * @since 4.0
     */
    protected JdbcImportOptionsPage() {
        super(JdbcImportOptionsPage.class.getSimpleName(), TITLE, null);
    }

    // ===========================================================================================================================
    // Implemented Methods

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    public void createControl(final Composite parent) {
        usesHiddenProject = ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric();

        // Create page
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        mainPanel.setLayout(new GridLayout(1, false));
        setControl(mainPanel);
        
        // Create Group for Model Info
        final Group modelGroup = WidgetFactory.createGroup(mainPanel,
        		MODEL_GROUP,
        GridData.HORIZONTAL_ALIGN_FILL,
        1, COLUMN_COUNT);
        
        // Add widgets to page
        WidgetFactory.createLabel(modelGroup, NAME_LABEL);
        this.nameText = WidgetFactory.createTextField(modelGroup, GridData.FILL_HORIZONTAL);
        this.nameText.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent event) {
                nameModified();
            }
        });

        // add browse button to allow selecting a model in the workspaced to update
        Button btn = WidgetFactory.createButton(modelGroup, BROWSE_BUTTON);
        btn.setToolTipText(getString("browseModelButton.tip")); //$NON-NLS-1$
        btn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                browseModelSelected();
            }
        });

        CLabel folderLabel = WidgetFactory.createLabel(modelGroup, FOLDER_LABEL);
        final IContainer folder = ((JdbcImportWizard)getWizard()).getFolder();
        final String name = (folder == null ? null : folder.getFullPath().makeRelative().toString());
        this.folderText = WidgetFactory.createTextField(modelGroup, GridData.FILL_HORIZONTAL, name);

        // If hidden project is used for the current project, don't show the folder fields
        if (usesHiddenProject) {
            this.folderText.setEditable(false);
            this.folderText.setVisible(false);
            folderLabel.setVisible(false);
        }
        this.folderText.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent event) {
                folderModified();
            }
        });

        // If hidden project is used for the current project, don't show the browse button to change the location
        if (!usesHiddenProject) {
            WidgetFactory.createButton(modelGroup, BROWSE_BUTTON).addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    browseButtonSelected();
                }
            });
        }

        this.updateCheckBox = WidgetFactory.createCheckBox(modelGroup, UPDATE_CHECKBOX, 0, COLUMN_COUNT);
        this.updateCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                updateCheckBoxSelected();
            }
        });
        
        WidgetFactory.createLabel(mainPanel, "  "); //$NON-NLS-1$
        
        this.includeCatalogCheckBox = WidgetFactory.createCheckBox(mainPanel, INCLUDE_CATALOG_CHECKBOX, 0, 1);
        this.includeCatalogCheckBox.setToolTipText(INCLUDE_CATALOG_CHECKBOX_TOOLTIP);
        this.includeCatalogCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	includeCatalogCheckBox();
            }
        });
        this.includeCatalogCheckBox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        
        WidgetFactory.createLabel(mainPanel, "  "); //$NON-NLS-1$
        
        Group caseOptionsGroup = WidgetFactory.createGroup(mainPanel,
                CASE_OPTIONS_GROUP,
                GridData.HORIZONTAL_ALIGN_FILL,
                1, 2);
        
        this.fullyQualifiedNamesCheckBox = WidgetFactory.createCheckBox(caseOptionsGroup, FULLY_QUALIFIED_CHECKBOX, 0, 2);
        this.fullyQualifiedNamesCheckBox.setToolTipText(FULLY_QUALIFIED_CHECKBOX_TOOLTIP);
        this.fullyQualifiedNamesCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	fullyQualifiedNamesCheckBoxSelected();
            }
        });
        this.fullyQualifiedNamesCheckBox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        ((GridData) this.fullyQualifiedNamesCheckBox.getLayoutData()).horizontalSpan = 2;
        
        this.modifyCaseCheckBox = WidgetFactory.createCheckBox(caseOptionsGroup, MODIFY_CASE_CHECKBOX, 0, 2);
        this.modifyCaseCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	modifyCaseSelected();
            }
        });
        this.modifyCaseCheckBox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        ((GridData) this.modifyCaseCheckBox.getLayoutData()).horizontalSpan = 2;
        
        // Indent the change case group
        Label spacer = new Label(caseOptionsGroup, SWT.NONE);
        spacer.setText("      "); //$NON-NLS-1$
        
        changeCaseGroup = WidgetFactory.createGroup(caseOptionsGroup,
                CHANGE_CASE_GROUP,
                GridData.HORIZONTAL_ALIGN_FILL,
                1, 1);
        
        this.uppercaseButton = WidgetFactory.createRadioButton(changeCaseGroup, MAKE_ALL_UPPER_RADIO);
        this.uppercaseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                uppercaseButtonSelected();
            }
        });
        this.lowercaseButton = WidgetFactory.createRadioButton(changeCaseGroup, MAKE_ALL_LOWER_RADIO);
        this.lowercaseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                lowercaseButtonSelected();
            }
        });
        
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.IPersistentWizardPage#saveSettings()
     * @since 4.0
     */
    public void saveSettings() {
        final IDialogSettings dlgSettings = getDialogSettings();
        final JdbcImportWizard wizard = (JdbcImportWizard)getWizard();
        // Information must be obtained from wizard, not local variables, since this method may be called w/o this page every
        // being set visible, via the user having pre-selected a destination folder and clicking on the "Finish" earlier in the
        // wizard.
        final JdbcImportSettings importSettings = wizard.getSource().getImportSettings();
        try {
            final DatabaseMetaData metadata = wizard.getDatabase().getDatabaseMetaData();
            dlgSettings.put(metadata.getCatalogTerm(), importSettings.isCreateCatalogsInModel());
            dlgSettings.put(metadata.getSchemaTerm(), importSettings.isCreateSchemasInModel());
        } catch (final Exception err) {
            Util.log(err);
            WidgetUtil.showError(err);
        }
        dlgSettings.put(MODEL_OBJECT_NAMES_GROUP, importSettings.getConvertCaseInModel().getName());
        dlgSettings.put(SOURCE_OBJECT_NAMES_GROUP, importSettings.getGenerateSourceNamesInModel().getName());
    }

    public boolean updateSelected() {
        if (updateCheckBox != null) {
            return updateCheckBox.getSelection();
        }

        return false;
    }

    // ===========================================================================================================================
    // Overridden Methods

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     * @since 4.0
     */
    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            // Wrap in transaction so it doesn't result in Significant Undoable
            boolean started = ModelerCore.startTxn(false, false, "Initializing Optioin Settings", this); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                initializeInTransaction();
                succeeded = true;
            } finally {
                if (started) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
        super.setVisible(visible);
    }

    void initializeInTransaction() {
        final JdbcImportWizard wizard = (JdbcImportWizard)getWizard();
        this.db = wizard.getDatabase();
        final String name = wizard.getModelName();
        this.nameText.setText(name);
        try {
            // Save object selections from previous page
            final JdbcSource src = wizard.getSource();
            JdbcPlugin.recordJdbcDatabaseSelections(src, this.db);
            // Initialize widgets
            this.importSettings = src.getImportSettings();
            this.importSettings.setCreateCatalogsInModel(false);
            this.importSettings.setCreateSchemasInModel(false);
            
            if (!this.initd) {
                setInitd(true);

                final DatabaseMetaData metadata = this.db.getDatabaseMetaData();
                
                // Check if supports catalogs....
                boolean supportsCatalogs = false;
                ResultSet resultSet = metadata.getCatalogs();
                while( resultSet.next() ) {
                    final String catalogName = resultSet.getString(1);
                    if( catalogName.length() > 0 ) {
                        supportsCatalogs = true;
                    }
                }
                
                if( supportsCatalogs ) {
                	includeCatalogCheckBox.setSelection(supportsCatalogs);
                	includeCatalogCheckBox.setEnabled(true);
                	this.importSettings.setCreateCatalogsInModel(true);
                } else {
                	includeCatalogCheckBox.setSelection(false);
                	includeCatalogCheckBox.setEnabled(false);
                	this.importSettings.setCreateCatalogsInModel(false);
                }
                
                fullyQualifiedNamesCheckBox.setSelection(true);
                this.importSettings.setGenerateSourceNamesInModel(SourceNames.FULLY_QUALIFIED_LITERAL);
                
	            switch (this.importSettings.getConvertCaseInModel().getValue()) {
	            	// Set state of modify check-box and toggle buttons
	                case CaseConversion.NONE: {
	                    this.modifyCaseCheckBox.setSelection(false);
	                    this.uppercaseButton.setSelection(true);
	                    this.changeCaseGroup.setEnabled(false);
	                    this.uppercaseButton.setEnabled(false);
	            		this.lowercaseButton.setEnabled(false);
	                    break;
	                }
	                case CaseConversion.TO_UPPERCASE: {
	                	this.uppercaseButton.setEnabled(true);
	            		this.lowercaseButton.setEnabled(true);
	                    this.uppercaseButton.setSelection(true);
	                    this.modifyCaseCheckBox.setSelection(true);
	                    this.changeCaseGroup.setEnabled(true);
	                    break;
	                }
	                case CaseConversion.TO_LOWERCASE: {
	                	this.uppercaseButton.setEnabled(true);
	            		this.lowercaseButton.setEnabled(true);
	                    this.lowercaseButton.setSelection(true);
	                    this.modifyCaseCheckBox.setSelection(true);
	                    this.changeCaseGroup.setEnabled(true);
	                    break;
	                }
	            }
            }
        } catch (final Exception err) {
            JdbcUiUtil.showAccessError(err);
        }
        validatePage(true);
        if (isPageComplete()) {
            if (usesHiddenProject) {
                setMessage(getString(INITIAL_MESSAGE_HIDDEN_PROJECT_ID, name));
            } else {
                setMessage(getString(INITIAL_MESSAGE_ID, name));
            }
        }
    }

    // ===========================================================================================================================
    // Model Methods

    /**
     * @return The JDBC import settings for the JDBC source being imported.
     */
    protected JdbcImportSettings getImportSettings() {
        return this.importSettings;
    }

    // ===========================================================================================================================
    // Controller Methods

    void browseModelSelected() {
        MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(RelationalPackage.eNS_URI);
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("dialog.modelChooser.title"), //$NON-NLS-1$
                                                                           getString("dialog.modelChooser.msg"), //$NON-NLS-1$
                                                                           false,
                                                                           ((JdbcImportWizard)getWizard()).getFolder(),
                                                                           new ModelWorkspaceViewerFilter(true),
                                                                           new ModelResourceSelectionValidator(descriptor, false),
                                                           				   new ModelExplorerLabelProvider());

        if ((resources != null) && (resources.length > 0)) {
            IFile model = (IFile)resources[0];
            this.selectedModel = model;
            IContainer folder = model.getParent();

            this.nameText.setText(model.getName());
            this.folderText.setText((folder == null) ? "" //$NON-NLS-1$
                            : folder.getFullPath().makeRelative().toString());
            this.updateCheckBox.setSelection(true);
            updateCheckBoxSelected(); // to get handler activated
        } else {
        	this.selectedModel = null;
        }
    }

    /**
     * @since 4.0
     */
    void browseButtonSelectedOLD() {
        final ViewerFilter filter = new ViewerFilter() {

            @Override
            public boolean select(final Viewer viewer,
                                  final Object parent,
                                  final Object element) {
                try {
                    return (((IContainer)element).getProject().getNature(ModelerCore.NATURE_ID) != null);
                } catch (final CoreException err) {
                    Util.log(err);
                    return false;
                }
            }
        };
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(((JdbcImportWizard)getWizard()).getFolder(),
                                                                       new ModelingResourceFilter(filter),
                                                                       new ModelProjectSelectionStatusValidator());
        if (folder != null) {
            this.folderText.setText(folder.getFullPath().makeRelative().toString());
            validatePage(false);
        }
    }

    /**
     * @since 4.0
     */
    void browseButtonSelected() {

        IContainer folder = getFolder();

        if (folder != null) {
            this.folderText.setText(folder.getFullPath().makeRelative().toString());
            validatePage(false);
        }
    }

    private IContainer getFolder() {

        // create the filter
        final ViewerFilter filter = getFilter();

        // create the dialog
        FolderSelectionDialog dlg = new FolderSelectionDialog(Display.getCurrent().getActiveShell(),
                                                              new WorkbenchLabelProvider(), new WorkbenchContentProvider());

        dlg.setInitialSelection(((JdbcImportWizard)getWizard()).getFolder());
        dlg.addFilter(new ModelingResourceFilter(filter));
        dlg.setValidator(new ModelProjectSelectionStatusValidator());
        dlg.setAllowMultiple(false);
        dlg.setInput(ResourcesPlugin.getWorkspace().getRoot());

        // display the dialog
        Object[] objs = new Object[1];
        if (dlg.open() == Window.OK) {
            objs = dlg.getResult();
        }

        return (objs.length == 0 ? null : (IContainer)objs[0]);
    }

    private ViewerFilter getFilter() {
        // create the filter
        final ViewerFilter filter = new ViewerFilter() {

            @Override
            public boolean select(final Viewer viewer,
                                  final Object parent,
                                  final Object element) {

                boolean result = false;

                if (element instanceof IResource) {
                    // If the project is closed, dont show
                    boolean projectOpen = ((IResource)element).getProject().isOpen();
                    if (projectOpen) {
                        // Show projects
                        if (element instanceof IProject) {
                            result = true;
                            // Show folders
                        } else if (element instanceof IFolder) {
                            result = true;
                        }
                    }
                }
                return result;
            }
        };
        return filter;
    }

    /**
     * @since 4.0
     */
    void folderModified() {
        validatePage(false);
    }

    /**
     * @since 4.0
     */
    void lowercaseButtonSelected() {
        if (this.lowercaseButton.getSelection()) {
            boolean requiredStart = ModelerCore.startTxn(false,false,"Set Lower Case Option",this); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                this.importSettings.setConvertCaseInModel(CaseConversion.TO_LOWERCASE_LITERAL);
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * @since 4.0
     */
    void nameModified() {
        validatePage(false);
    }

    /**
     * @since 4.0
     */
    protected void newFolderButtonSelected(final TreeViewer viewer,
                                           final IContainer folder) {
        final NewFolderDialog dlg = new NewFolderDialog(getShell(), folder);
        if (dlg.open() == Window.OK) {
            viewer.refresh(folder);
            final Object newFolder = dlg.getResult()[0];
            viewer.setSelection(new StructuredSelection(newFolder), true);
        }
    }

    /**
     * @since 4.0
     */
    void modifyCaseSelected() {
        
        boolean requiredStart = ModelerCore.startTxn(false,false,"Set None Option",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	if( !this.modifyCaseCheckBox.getSelection()) {
        		this.importSettings.setConvertCaseInModel(CaseConversion.NONE_LITERAL);
        		this.changeCaseGroup.setEnabled(false);
        		this.uppercaseButton.setEnabled(false);
        		this.lowercaseButton.setEnabled(false);
        	} else {
        		this.changeCaseGroup.setEnabled(true);
        		this.uppercaseButton.setEnabled(true);
        		this.lowercaseButton.setEnabled(true);
        		if( uppercaseButton.getSelection() ) {
        			this.importSettings.setConvertCaseInModel(CaseConversion.TO_UPPERCASE_LITERAL);
        		} else {
        			this.importSettings.setConvertCaseInModel(CaseConversion.TO_LOWERCASE_LITERAL);
        		}
        	}
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * @since 4.0
     */
    void fullyQualifiedNamesCheckBoxSelected() {
        boolean requiredStart = ModelerCore.startTxn(false,false,"Fully Qualified Names Selected",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	SourceNames value = SourceNames.UNQUALIFIED_LITERAL;
        	if( this.fullyQualifiedNamesCheckBox.getSelection() ) {
        		value = SourceNames.FULLY_QUALIFIED_LITERAL;
        	}
            this.importSettings.setGenerateSourceNamesInModel(value);
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
    
    
    /**
     * @since 4.0
     */
    void includeCatalogCheckBox() {
        boolean requiredStart = ModelerCore.startTxn(false,false,"Include Catalogs Selected",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	this.importSettings.setCreateCatalogsInModel(this.includeCatalogCheckBox.getSelection());
        	succeeded = true;
        } finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * @since 4.0
     */
    void updateCheckBoxSelected() {
    	final ModelResource model;
        /*
         * jhTODO: when checkbox is true, disable finish, enable next
         */
        if (this.updateCheckBox.getSelection()) {
            try {
                model = getSelectedModelResource();
                if (model != null && !model.isReadOnly()) {
                    if (model.getModelType().getValue() != ModelType.VIRTUAL) {
                        this.nameText.setText(model.getItemName());
                        // Return here (skipping call to validatePage) since previous line that sets the model name will end
                        // up calling validatePage anyway.
                        return;
                    }
                }
            } catch (final ModelWorkspaceException err) {
                Util.log(err);
                WidgetUtil.showError(err.getLocalizedMessage());
            }
        }
        validatePage(false);
    }

    /**
     * Get the selected ModelResource.  This method will check the selectedModel field - if it is
     * not null it will be returned.  Otherwise, the original selection is returned.
     * @return the ModelResource selection
     */
    private ModelResource getSelectedModelResource() throws ModelWorkspaceException {
    	ModelResource model = null;
    	if(this.selectedModel!=null) {
    		model = ModelUtil.getModel(this.selectedModel);
    	} else {
            final IStructuredSelection selection = UiUtil.getStructuredSelection();
            if(selection.size() == 1) {
            	model = ModelUtil.getModel(selection.getFirstElement());
            }
    	}
    	return model;
    }

    /**
     * @since 4.0
     */
    void uppercaseButtonSelected() {
        if (this.uppercaseButton.getSelection()) {
            boolean requiredStart = ModelerCore.startTxn(false,false,"Set Uppercase Option",this); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                this.importSettings.setConvertCaseInModel(CaseConversion.TO_UPPERCASE_LITERAL);
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    // ===========================================================================================================================
    // Utility Methods

    /**
     * @since 4.0
     */
    private void validatePage(boolean firstTime) {
        final boolean updating = this.updateCheckBox.getSelection();
        try {
            // making 'folder' an instance var so that canFlipToNextPage() can use it w/o recreating it
            folder = WizardUtil.validateFileAndFolder(this.nameText,
                                                      this.folderText,
                                                      this,
                                                      ModelerCore.MODEL_FILE_EXTENSION,
                                                      updating ? NONE : ERROR);
            // Check if folder's project is a model project
            if (folder != null && folder.getProject().getNature(ModelerCore.NATURE_ID) == null) {
                WizardUtil.setPageComplete(this, NOT_MODEL_PROJECT_MESSAGE, ERROR);
                folder = null;
            }
            // Check if model name is valid
            String name = this.nameText.getText();
            String problem = ModelUtilities.validateModelName(name, ModelerCore.MODEL_FILE_EXTENSION);
            if (problem != null) {
                WizardUtil.setPageComplete(this, INVALID_FILE_MESSAGE + '\n' + problem, ERROR);
                folder = null;
            }

            if (isPageComplete()) {
                name = FileUtils.toFileNameWithExtension(name, ModelerCore.MODEL_FILE_EXTENSION);
                final IFile file = folder.getFile(new Path(name));
                ModelResource model = null;
                if (file.exists()) {
                    try {
                        model = ModelerCore.getModelEditor().findModelResource(file);
                        if (model.isReadOnly()) {
                            WizardUtil.setPageComplete(this, READ_ONLY_MODEL_MESSAGE, ERROR);
                            return;
                        }
                        if (!RelationalPackage.eNS_URI.equals(model.getPrimaryMetamodelDescriptor().getNamespaceURI())) {
                            WizardUtil.setPageComplete(this, NOT_RELATIONAL_MODEL_MESSAGE, ERROR);
                            return;
                        }
                        if (model.getModelType().getValue() == ModelType.VIRTUAL) {
                            WizardUtil.setPageComplete(this, VIRTUAL_MODEL_MESSAGE, ERROR);
                            return;
                        }
                    } catch (final ModelWorkspaceException err) {
                        Util.log(err);
                        WidgetUtil.showError(err.getLocalizedMessage());
                    }
                } else if (updating) {
                    WizardUtil.setPageComplete(this,
                                               getString(NO_NODEL_TO_UPDATE_MESSAGE_ID, file.getFullPath().makeRelative()),
                                               ERROR);
                    return;
                }
                final JdbcImportWizard wizard = (JdbcImportWizard)getWizard();
                wizard.setModelName(name);
                wizard.setFolder(folder);
                if (model != wizard.getUpdatedModel()) {
                    List newlySelectedNodes = new ArrayList();
                    //collect newly selected nodes
                    collectNewlySelectedNodes(db.getChildren(), newlySelectedNodes, getOriginalImportSettings(model));
                    //reset the model to be updated. this reset the import setting
                    wizard.setUpdatedModel(model);
                    // Removed previously excluded nodes that have now been selected for import
                    removeExcludedNodes(newlySelectedNodes);
                }
                getContainer().updateButtons();
            } else if (folder != null) {
            	// During initialization (firstTime) auto-set the update-check-box for the user
            	if( firstTime ) {
            		this.updateCheckBox.setSelection(true);
            		updateCheckBoxSelected();
            	} else {
            		WizardUtil.setPageComplete(this, getMessage() + '\n' + FILE_EXISTS_MESSAGE, getMessageType());
            	}
            }
        } catch (final CoreException err) {
            Util.log(err);
            WizardUtil.setPageComplete(this, err.getLocalizedMessage(), ERROR);
        }
    }

    private void collectNewlySelectedNodes(JdbcNode[] children, List newlySelectedNodes, JdbcImportSettings settings) throws CoreException{
        if(children == null) {
            return;
        }

        if (settings != null) {
            for (int ndx = children.length; --ndx >= 0;) {
                final JdbcNode child = children[ndx];

                if(child.getSelectionMode() == JdbcNode.SELECTED) {
                    for (final Iterator objIter = settings.getExcludedObjectPaths().iterator(); objIter.hasNext();) {
                        final IPath path = new Path((String)objIter.next());
                        if (child.equals(((JdbcImportWizard)getWizard()).findNode(path, this.db))) {
                            newlySelectedNodes.add(child);
                            break;
                        }
                    }
                }
                if(child.getSelectionMode() == JdbcNode.SELECTED || child.getSelectionMode() == JdbcNode.PARTIALLY_SELECTED) {
                    collectNewlySelectedNodes(child.getChildren(), newlySelectedNodes, settings);
                }
            }
        }
    }

    private JdbcImportSettings getOriginalImportSettings(ModelResource model) throws CoreException{
        if(model != null) {
            for (Iterator modelIter = model.getAllRootEObjects().iterator(); modelIter.hasNext();) {
                final Object obj = modelIter.next();
                if (obj instanceof JdbcSource) {
                    return ((JdbcSource)obj).getImportSettings();
                }
            }
        }

        return((JdbcImportWizard)getWizard()).getSource().getImportSettings();
    }

    private void removeExcludedNodes(List nodes) throws CoreException{
        if(nodes.isEmpty()) {
            return;
        }

        final JdbcImportSettings settings = ((JdbcImportWizard)getWizard()).getSource().getImportSettings();
        if (settings != null) {
            Iterator iter = nodes.iterator();
            while(iter.hasNext()){
                final JdbcNode node = (JdbcNode)iter.next();
                node.setSelected(true);
                for (final Iterator objIter = settings.getExcludedObjectPaths().iterator(); objIter.hasNext();) {
                    final IPath path = new Path((String)objIter.next());
                    if (node.equals(((JdbcImportWizard)getWizard()).findNode(path, this.db))) {
                        objIter.remove();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean canFlipToNextPage() {

        /*
         * I have added a new, final page to this wizard that follows this page and shows the DifferenceReport in the case in
         * which the user is using this wizard to update a previously created model from the JDBC imported metadata. In the
         * non-update case it is important that this page NOT enable the Next button. I am overriding this WizardPage method to
         * accomplish that. return ( updating && isPageComplete() && folder != null && fileExists )? true : false;
         */
        final boolean updating = this.updateCheckBox.getSelection();

        IFile file = null;

        if (folder != null) {
            String name = this.nameText.getText();
            name = FileUtils.toFileNameWithExtension(name, ModelerCore.MODEL_FILE_EXTENSION);
            file = folder.getFile(new Path(name));
        }

        return (folder != null && file != null && updating && isPageComplete() && file.exists());
    }

    /**
     * @param initd
     *            The initd to set.
     * @since 4.3
     */
    public void setInitd(boolean initd) {
        this.initd = initd;
    }
}
