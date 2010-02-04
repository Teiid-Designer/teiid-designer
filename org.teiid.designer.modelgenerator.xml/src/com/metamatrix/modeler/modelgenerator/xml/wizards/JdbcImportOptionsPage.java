/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards;

import java.sql.DatabaseMetaData;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.NewFolderDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.CaseConversion;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.modelgenerator.xml.XMLExtensionsFilter;
import com.metamatrix.modeler.modelgenerator.xml.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.dialog.FolderSelectionDialog;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.IPersistentWizardPage;

/**
 * @since 4.0
 */
final class JdbcImportOptionsPage extends WizardPage implements
                                                    InternalUiConstants.Widgets,
                                                    IPersistentWizardPage,
                                                    UiConstants {

    //============================================================================================================================
    // Constants

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcImportOptionsPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final int COLUMN_COUNT = 3;

    private static final String INITIAL_MESSAGE_ID = "initialMessage"; //$NON-NLS-1$
    private static final String NO_NODEL_TO_UPDATE_MESSAGE_ID = "noModelToUpdateMessage"; //$NON-NLS-1$

    private static final String NAME_LABEL = getString("nameLabel"); //$NON-NLS-1$
    private static final String FOLDER_LABEL = getString("folderLabel"); //$NON-NLS-1$
    private static final String UPDATE_CHECKBOX = getString("updateCheckBox"); //$NON-NLS-1$
    private static final String MODEL_OBJECT_NAMES_GROUP = getString("modelObjectNamesGroup"); //$NON-NLS-1$
//    private static final String SOURCE_OBJECT_NAMES_GROUP = getString("sourceObjectNamesGroup"); //$NON-NLS-1$
    private static final String NONE_BUTTON = getString("noneButton"); //$NON-NLS-1$
    private static final String UPPERCASE_BUTTON = getString("uppercaseButton"); //$NON-NLS-1$
    private static final String LOWERCASE_BUTTON = getString("lowercaseButton"); //$NON-NLS-1$
//    private static final String EMPTY_BUTTON = getString("emptyButton"); //$NON-NLS-1$
//    private static final String UNQUALIFIED_BUTTON = getString("unqualifiedSourceNamesButton"); //$NON-NLS-1$
//    private static final String QUALIFIED_BUTTON = getString("qualifiedSourceNamesButton"); //$NON-NLS-1$


    

    private static final String MODEL_OBJECT_NAMES_DESCRIPTION = getString("modelObjectNamesDescription"); //$NON-NLS-1$
//    private static final String SOURCE_OBJECT_NAMES_DESCRIPTION = getString("sourceObjectNamesDescription"); //$NON-NLS-1$

    private static final String FILE_EXISTS_MESSAGE = getString("fileExistsMessage", UPDATE_CHECKBOX); //$NON-NLS-1$
    private static final String NOT_MODEL_PROJECT_MESSAGE = getString("notModelProjectMessage"); //$NON-NLS-1$
    private static final String NOT_RELATIONAL_MODEL_MESSAGE = getString("notRelationalModelMessage"); //$NON-NLS-1$
    private static final String READ_ONLY_MODEL_MESSAGE = getString("readOnlyModelMessage"); //$NON-NLS-1$
    private static final String VIRTUAL_MODEL_MESSAGE = getString("virtualModelMessage"); //$NON-NLS-1$


    
    //============================================================================================================================
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

    //============================================================================================================================
    // Variables

    private JdbcDatabase db;
    private JdbcImportSettings importSettings;
    private Text nameText, folderText;
//    private Button emptyButton, unqualifiedButton, qualifiedButton;
    private Button updateCheckBox, schemaCheckBox, noneButton, uppercaseButton, lowercaseButton;
    private boolean initd;
    private IContainer folder;

    //============================================================================================================================
    // Constructors

    /**
     * @param pageName
     * @since 4.0
     */
    JdbcImportOptionsPage() {
        super(JdbcImportOptionsPage.class.getSimpleName(), TITLE, null);
    }

    //============================================================================================================================
    // Implemented Methods

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    public void createControl(final Composite parent) {
        // Create page
        final Composite pg = new Composite(parent, SWT.NONE);
        pg.setLayout(new GridLayout(COLUMN_COUNT, false));
        setControl(pg);

        CLabel label = new CLabel(pg, SWT.NONE);
        label.setText(NAME_LABEL);
        final GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 1;
        label.setLayoutData(gridData);

        this.nameText = WidgetFactory.createTextField(pg, GridData.FILL_HORIZONTAL);
        this.nameText.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent event) {
                nameModified();
            }
        });
        
        // add browse button to allow selecting a model in the workspaced to update
        Button btn = WidgetFactory.createButton(pg, BROWSE_BUTTON);
        btn.setToolTipText(getString("browseModelButton.tip")); //$NON-NLS-1$
        btn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                browseModelSelected();
            }
        });

        CLabel label2 = new CLabel(pg, SWT.NONE);
        label2.setText(FOLDER_LABEL);
        final GridData gridData2 = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        gridData2.horizontalSpan = 1;
        label2.setLayoutData(gridData2);

        final IContainer folder = ((XsdAsRelationalImportWizard)getWizard()).getFolder();
        final String name = (folder == null ? null : folder.getFullPath().makeRelative().toString());
        this.folderText = WidgetFactory.createTextField(pg, GridData.FILL_HORIZONTAL, name);
        this.folderText.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent event) {
                folderModified();
            }
        });
        WidgetFactory.createButton(pg, BROWSE_BUTTON).addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                browseButtonSelected();
            }
        });

        this.updateCheckBox = WidgetFactory.createCheckBox(pg, UPDATE_CHECKBOX, 0, COLUMN_COUNT);
        this.updateCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                updateCheckBoxSelected();
            }
        });             
        /*
        final Group inclGroup = WidgetFactory.createGroup(pg, INCLUDE_GROUP, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT);
        {
            WidgetFactory.createLabel(inclGroup, GridData.FILL_HORIZONTAL, INCLUDE_DESCRIPTION, SWT.WRAP);
            this.catalogCheckBox = WidgetFactory.createCheckBox(inclGroup, GridData.FILL_HORIZONTAL);
            this.catalogCheckBox.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(final SelectionEvent event) {
                    catalogCheckBoxSelected();
                }
            });
            this.schemaCheckBox = WidgetFactory.createCheckBox(inclGroup, GridData.FILL_HORIZONTAL);
            this.schemaCheckBox.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(final SelectionEvent event) {
                    schemaCheckBoxSelected();
                }
            });
        }        
        */                                
        final Group modelObjNamesGroup = WidgetFactory.createGroup(pg,
                                                                   MODEL_OBJECT_NAMES_GROUP,
                                                                   GridData.HORIZONTAL_ALIGN_FILL,
                                                                   COLUMN_COUNT);
        {
            CLabel label3 = new CLabel(modelObjNamesGroup, SWT.WRAP);
            label3.setText(MODEL_OBJECT_NAMES_DESCRIPTION);
            final GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
            gridData3.horizontalSpan = 1;
            label3.setLayoutData(gridData3);

            this.noneButton = WidgetFactory.createRadioButton(modelObjNamesGroup, NONE_BUTTON);
            this.noneButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    noneButtonSelected();
                }
            });
            this.uppercaseButton = WidgetFactory.createRadioButton(modelObjNamesGroup, UPPERCASE_BUTTON);
            this.uppercaseButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    uppercaseButtonSelected();
                }
            });
            this.lowercaseButton = WidgetFactory.createRadioButton(modelObjNamesGroup, LOWERCASE_BUTTON);
            this.lowercaseButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    lowercaseButtonSelected();
                }
            });
        }
//        final Group srcObjNamesGroup = WidgetFactory.createGroup(pg,
//                                                                 SOURCE_OBJECT_NAMES_GROUP,
//                                                                 GridData.HORIZONTAL_ALIGN_FILL,
//                                                                 COLUMN_COUNT);
//        {
//            WidgetFactory.createLabel(srcObjNamesGroup, GridData.FILL_HORIZONTAL, SOURCE_OBJECT_NAMES_DESCRIPTION, SWT.WRAP);
//            this.emptyButton = WidgetFactory.createRadioButton(srcObjNamesGroup, EMPTY_BUTTON);
//            this.emptyButton.addSelectionListener(new SelectionAdapter() {
//
//                public void widgetSelected(final SelectionEvent event) {
//                    emptyButtonSelected();
//                }
//            });
//            this.unqualifiedButton = WidgetFactory.createRadioButton(srcObjNamesGroup, UNQUALIFIED_BUTTON);
//            this.unqualifiedButton.addSelectionListener(new SelectionAdapter() {
//
//                public void widgetSelected(final SelectionEvent event) {
//                    unqualifiedButtonSelected();
//                }
//            });
//            this.qualifiedButton = WidgetFactory.createRadioButton(srcObjNamesGroup, QUALIFIED_BUTTON);
//            this.qualifiedButton.addSelectionListener(new SelectionAdapter() {
//
//                public void widgetSelected(final SelectionEvent event) {
//                    qualifiedButtonSelected();
//                }
//            });
//        }
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.IPersistentWizardPage#saveSettings()
     * @since 4.0
     */
    public void saveSettings() {
        final IDialogSettings dlgSettings = getDialogSettings();
        final XsdAsRelationalImportWizard wizard = (XsdAsRelationalImportWizard)getWizard();
        // Information must be obtained from wizard, not local variables, since this method may be called w/o this page every
        // being set visible, via the user having pre-selected a destination folder and clicking on the "Finish" earlier in the
        // wizard.
        final JdbcImportSettings importSettings = wizard.getSource().getImportSettings();
        try {
            final DatabaseMetaData metadata = wizard.getDatabase().getDatabaseMetaData();
            //dlgSettings.put(metadata.getCatalogTerm(), importSettings.isCreateCatalogsInModel());           
            dlgSettings.put(metadata.getSchemaTerm(), importSettings.isCreateSchemasInModel());
        } catch (final Exception err) {
            Util.log(err);
            WidgetUtil.showError(err);
        }
        dlgSettings.put(MODEL_OBJECT_NAMES_GROUP, importSettings.getConvertCaseInModel().getName());
//        dlgSettings.put(SOURCE_OBJECT_NAMES_GROUP, importSettings.getGenerateSourceNamesInModel().getName());
    }
    
    public boolean updateSelected() {
        if ( updateCheckBox != null ) {
            return updateCheckBox.getSelection();
        }
        
        return false;
    }

    //============================================================================================================================
    // Overridden Methods

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     * @since 4.0
     */
    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            final XsdAsRelationalImportWizard wizard = (XsdAsRelationalImportWizard)getWizard();
            this.db = wizard.getDatabase();
            final String name = wizard.getModelName();
            this.nameText.setText(name);
            try {
                // Save object selections from previous page
                final JdbcSource src = wizard.getSource();
                JdbcPlugin.recordJdbcDatabaseSelections(src, this.db);
                // Initialize widgets
                this.importSettings = src.getImportSettings();
                if (!this.initd) {
                    this.initd = true;
                    final IDialogSettings dlgSettings = getDialogSettings();
                    final DatabaseMetaData metadata = this.db.getDatabaseMetaData();
                    final String catalogTerm = metadata.getCatalogTerm();
                    final String schemaTerm = metadata.getSchemaTerm();
                    if (!((XsdAsRelationalImportWizard)getWizard()).isUpdatedModel() && dlgSettings.get(catalogTerm) != null) {
                        //this.importSettings.setCreateCatalogsInModel(dlgSettings.getBoolean(catalogTerm));
                        this.importSettings.setCreateSchemasInModel(dlgSettings.getBoolean(schemaTerm));
                        this.importSettings.setConvertCaseInModel(CaseConversion.get(dlgSettings.get(MODEL_OBJECT_NAMES_GROUP)));
//                        this.importSettings.setGenerateSourceNamesInModel(SourceNames.get(dlgSettings.get(SOURCE_OBJECT_NAMES_GROUP)));
                    }
                    if(wizard.getStateManager().isUsingNoCatalog()) {
                    	importSettings.setCreateCatalogsInModel(false);
                    } else {
                    	importSettings.setCreateCatalogsInModel(true);
                    }
                    //final Composite parent = this.catalogCheckBox.getParent();
                    //initializeIncludeCheckBox(this.catalogCheckBox, catalogTerm, this.importSettings.isCreateCatalogsInModel());
                    //initializeIncludeCheckBox(this.schemaCheckBox, schemaTerm, this.importSettings.isCreateSchemasInModel());
                    //if (this.catalogCheckBox.isDisposed() && this.schemaCheckBox.isDisposed()) {
                    //    parent.dispose();
                    //    ((Composite)getControl()).layout();
                    //}
                    
                    //This is here because this is where it used to happen

                }    
                switch (this.importSettings.getConvertCaseInModel().getValue()) {
                    case CaseConversion.NONE:
                    {
                        this.noneButton.setSelection(true);
                        break;
                    }
                    case CaseConversion.TO_UPPERCASE:
                    {
                        this.uppercaseButton.setSelection(true);
                        break;
                    }
                    case CaseConversion.TO_LOWERCASE:
                    {
                        this.lowercaseButton.setSelection(true);
                        break;
                    }
                }
//                switch (this.importSettings.getGenerateSourceNamesInModel().getValue()) {
//                    case SourceNames.NONE:
//                    {
//                        this.emptyButton.setSelection(true);
//                        break;
//                    }
//                    case SourceNames.UNQUALIFIED:
//                    {
//                        this.unqualifiedButton.setSelection(true);
//                        break;
//                    }
//                    case SourceNames.FULLY_QUALIFIED:
//                    {
//                        this.qualifiedButton.setSelection(true);
//                        break;
//                    }
//                }
            } catch (final Exception err) {
                JdbcUiUtil.showAccessError(err);
            }
            validatePage();
            if (isPageComplete()) {
                setMessage(getString(INITIAL_MESSAGE_ID, name));
            }
        }
        super.setVisible(visible);
    }

    //============================================================================================================================
    // MVC Controller Methods
    
    void browseModelSelected() {
        MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(RelationalPackage.eNS_URI);
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("dialog.modelChooser.title"), //$NON-NLS-1$
                                                                           getString("dialog.modelChooser.msg"), //$NON-NLS-1$
                                                                           false,
                                                                           ((XsdAsRelationalImportWizard)getWizard()).getFolder(),
                                                                           new XMLExtensionsFilter(),
                                                                           new ModelResourceSelectionValidator(descriptor, false));

        if ((resources != null) && (resources.length > 0)) {
            IFile model = (IFile)resources[0];
            IContainer folder = model.getParent();

            this.nameText.setText(model.getName());
            this.folderText.setText((folder == null) ? "" //$NON-NLS-1$
                                                     : folder.getFullPath().makeRelative().toString());
            this.updateCheckBox.setSelection(true);
            updateCheckBoxSelected(); // to get handler activated
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
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(((XsdAsRelationalImportWizard)getWizard()).getFolder(),
                                                                       filter,
                                                                       new ModelProjectSelectionStatusValidator());
        if (folder != null) {
            this.folderText.setText(folder.getFullPath().makeRelative().toString());
            validatePage();
        }
    }


    /**
     * @since 4.0
     */
    void browseButtonSelected() {
        
        IContainer folder = getFolder();
        
        if (folder != null) {
            this.folderText.setText(folder.getFullPath().makeRelative().toString());
            validatePage();
        }
    }
    
    private IContainer getFolder() { 
        
        // create the filter  
        final ViewerFilter filter = getFilter();
        
        // create the dialog
        FolderSelectionDialog dlg = new FolderSelectionDialog( Display.getCurrent().getActiveShell(), 
                                                               new WorkbenchLabelProvider(), 
                                                               new WorkbenchContentProvider() );
        
        dlg.setInitialSelection( ((XsdAsRelationalImportWizard)getWizard()).getFolder() );
        dlg.addFilter( filter );
        dlg.setValidator( new ModelProjectSelectionStatusValidator() );
        dlg.setAllowMultiple( false );
        dlg.setInput( ResourcesPlugin.getWorkspace().getRoot() );

        // display the dialog
        Object[] objs = new Object[ 1 ];
        if ( dlg.open() == Window.OK ) {
            objs = dlg.getResult();
        }
        
        return ( objs.length == 0 ? null : (IContainer)objs[0] );
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
                    if(projectOpen) {
                        // Show projects
                        if(element instanceof IProject) {
                            result = true;
                        // Show folders
                        } else if (element instanceof IFolder ) {
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
//    void catalogCheckBoxSelected() {
//        this.importSettings.setCreateCatalogsInModel(this.catalogCheckBox.getSelection());
//    }

//    /**
//     * @since 4.0
//     */
//    void emptyButtonSelected() {
//        if (this.emptyButton.getSelection()) {
//            this.importSettings.setGenerateSourceNamesInModel(SourceNames.NONE_LITERAL);
//        }
//    }

    /**
     * @since 4.0
     */
    void folderModified() {
        validatePage();
    }

    /**
     * @since 4.0
     */
    void lowercaseButtonSelected() {
        if (this.lowercaseButton.getSelection()) {
            this.importSettings.setConvertCaseInModel(CaseConversion.TO_LOWERCASE_LITERAL);
        }
    }

    /**
     * @since 4.0
     */
    void nameModified() {
        validatePage();
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
    void noneButtonSelected() {
        if (this.noneButton.getSelection()) {
            this.importSettings.setConvertCaseInModel(CaseConversion.NONE_LITERAL);
        }
    }

//    /**
//     * @since 4.0
//     */
//    void qualifiedButtonSelected() {
//        if (this.qualifiedButton.getSelection()) {
//            this.importSettings.setGenerateSourceNamesInModel(SourceNames.FULLY_QUALIFIED_LITERAL);
//        }
//    }

    /**
     * @since 4.0
     */
    void schemaCheckBoxSelected() {
        this.importSettings.setCreateSchemasInModel(this.schemaCheckBox.getSelection());
    }

//    /**
//     * @since 4.0
//     */
//    void unqualifiedButtonSelected() {
//        if (this.unqualifiedButton.getSelection()) {
//            this.importSettings.setGenerateSourceNamesInModel(SourceNames.UNQUALIFIED_LITERAL);
//        }
//    }

    /**
     * @since 4.0
     */
    void updateCheckBoxSelected() {
        /*
         * jhTODO: when checkbox is true, disable finish, enable next
         */
        if (this.updateCheckBox.getSelection()) {
            final IStructuredSelection selection = UiUtil.getStructuredSelection();
            if (selection.size() == 1) {
                final ModelResource model;
                try {
                    model = ModelUtil.getModel(selection.getFirstElement());
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
        }
        validatePage();
    }

    /**
     * @since 4.0
     */
    void uppercaseButtonSelected() {
        if (this.uppercaseButton.getSelection()) {
            this.importSettings.setConvertCaseInModel(CaseConversion.TO_UPPERCASE_LITERAL);
        }
    }

    //============================================================================================================================
    // Utility Methods

    /**
     * @since 4.0
     */
//    private void initializeIncludeCheckBox(final Button checkBox,
//                                           final String term,
//                                           final boolean selected) {
//        if (term.length() == 0) {
//            checkBox.dispose();
//            ((Composite)getControl()).layout();
//        } else {
//            checkBox.setText(StringUtil.computeDisplayableForm(term));
//            checkBox.setSelection(selected);
//        }
//    }

    /**
     * @since 4.0
     */
    private void validatePage() {
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
                if (file.exists()) {
                    try {
                        final ModelResource model = ModelerCore.getModelEditor().findModelResource(file);
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
                final XsdAsRelationalImportWizard wizard = (XsdAsRelationalImportWizard)getWizard();
                wizard.setModelName(name);
                wizard.setFolder(folder);
                getContainer().updateButtons();
            } else if (folder != null) {
                WizardUtil.setPageComplete(this, getMessage() + '\n' + FILE_EXISTS_MESSAGE, getMessageType());
            }
        } catch (final CoreException err) {
            Util.log(err);
            WizardUtil.setPageComplete(this, err.getLocalizedMessage(), ERROR);
        }
    }
    
    
    @Override
    public boolean canFlipToNextPage() {

        /*
         * I have added a new, final page to this wizard that follows this
         * page and shows the DifferenceReport in the case in which the
         * user is using this wizard to update a previously created model from
         * the JDBC imported metadata.  In the non-update case it is important
         * that this page NOT enable the Next button.  I am overriding this 
         * WizardPage method to accomplish that. 
         * 
         * return ( updating && isPageComplete() && folder != null && fileExists )?  true : false;
         */
        final boolean updating = this.updateCheckBox.getSelection();

        IFile file = null;
        
        if ( folder != null  ) {
            String name = this.nameText.getText();
            name = FileUtils.toFileNameWithExtension(name, ModelerCore.MODEL_FILE_EXTENSION);
            file = folder.getFile(new Path(name));
        }
            
        return ( folder != null && file != null && updating && isPageComplete() && file.exists() );                
    }
}
