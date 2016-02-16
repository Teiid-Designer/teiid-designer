/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.core.designer.I18n;
import org.teiid.core.designer.exception.EmptyArgumentException;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.ddl.importer.TeiidDDLConstants;
import org.teiid.designer.ddl.importer.node.teiid.TeiidDdlImporter;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.wizard.IPersistentWizardPage;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.util.ErrorHandler;

public class TeiidDdlImporterPage  extends WizardPage implements IPersistentWizardPage {

	private static final String TEIID_DIALECT = "TEIID"; //$NON-NLS-1$
	
    private static final int PANEL_GRID_SPAN = 3;
    private static final String INITIAL_DIALOG_FOLDER_SETTING = "initialDialogFolder"; //$NON-NLS-1$
    private static final String HISTORY_SETTING = "history"; //$NON-NLS-1$
    private static final int MAX_HISTORY = 5;
    private static final List<ModelType> MODEL_TYPES = Arrays.asList(ModelType.PHYSICAL_LITERAL, ModelType.VIRTUAL_LITERAL);
    private static final String OPT_TO_CREATE_MODEL_ENTITIES_FOR_UNSUPPORTED_DDL_SETTING = "optToCreateModelEntitiesForUnsupportedDdl"; //$NON-NLS-1$
    private static final String OPT_TO_SET_MODEL_ENTITY_DESCRIPTION_SETTING = "optToSetModelEntityDescription"; //$NON-NLS-1$

    private final DdlImporter importer;
    final IProject[] projects;
    IFile selectedFile;
    private StringNameValidator nameValidator = new StringNameValidator();

    private Combo ddlFileCombo;
    private Text modelFolderFld;
    private Text modelNameFld;
    private Combo modelTypeCombo;
    private Button generateDefaultSQLCheckBox;
    private Button optToSetModelEntityDescriptionCheckBox;
    private Button optToCreateModelEntitiesForUnsupportedDdlCheckBox;
    Button filterRedundantUCsCB;
    private Text ddlFileContentsBox;
    
    private TabItem modelDefinitionTab;
    private TabItem optionsTab;
    private TabItem ddlTab;

    private String initDlgFolderName;
    private boolean generateModelName = true;
    Properties options;

    /**
     * DdlImporterPage constructor
     * @param importer the DdlImporter
     * @param projects current workspace projects
     * @param selection current selection
     * @param options 
     */
    TeiidDdlImporterPage( final DdlImporter importer,
                     final IProject[] projects,
                     final IStructuredSelection selection,
                     final Properties options) {
        super(DdlImporterPage.class.getSimpleName(), DdlImporterUiI18n.PAGE_TITLE, null);
        this.importer = importer;
        this.projects = projects;
        this.options = options;
        final Set<IContainer> selectedContainers = new HashSet<IContainer>();
        for (final Iterator<?> iter = selection.iterator(); iter.hasNext();) {
            final Object resource = iter.next();
            if (resource instanceof IContainer) selectedContainers.add((IContainer)resource);
            else if( resource instanceof IResource ) {
            	selectedContainers.add(((IResource)resource).getParent());
            }
            if (selectedContainers.size() > 1) return;
        }
        if (selectedContainers.size() == 0) {
            if (projects.length == 1) importer.setModelFolder(projects[0]);
        } else if (selectedContainers.size() == 1) importer.setModelFolder(selectedContainers.iterator().next());
        if (selection.size() == 1) {
            final Object obj = selection.getFirstElement();
            if (obj instanceof IFile) selectedFile = (IFile)obj;
        }
        
        importer.setSpecifiedParser(TEIID_DIALECT);
    }

    void chooseDdlFile() {
        final FileDialog dlg = new FileDialog(getShell());
        final String ddlFileName = ddlFileCombo.getText().trim();
        final File file = new File(ddlFileName);
        if (file.exists() && file.isDirectory()) initDlgFolderName = ddlFileName;
        else {
            int ndx = ddlFileName.lastIndexOf(File.separatorChar);
            if (ndx < 0) ndx = ddlFileName.lastIndexOf('/');
            if (file.exists()) dlg.setFileName(ddlFileName.substring(ndx + 1));
            if (ndx >= 0) {
                final String folderName = ddlFileName.substring(0, ndx + 1);
                final File folder = new File(folderName);
                if (folder.exists() && folder.isDirectory()) initDlgFolderName = folderName;
            }
            if (initDlgFolderName == null) initDlgFolderName = System.getProperty("user.home"); //$NON-NLS-1$
        }
        dlg.setFilterPath(initDlgFolderName);
        dlg.setFilterExtensions(new String[] {"*.ddl; *.sql", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
        dlg.setFilterNames(new String[] {"DDL Files", "All Files"}); //$NON-NLS-1$ //$NON-NLS-2$
        final String fileName = dlg.open();
        if (fileName != null) {
            ddlFileCombo.setText(fileName);
            tabFromDdlFileCombo();
        }
        initDlgFolderName = dlg.getFilterPath();
    }

    void chooseDdlFileFromWorkspace() {
        final ChooseFileDialog dlg = new ChooseFileDialog(DdlImporterUiI18n.CHOOSE_DDL_FILE_DIALOG_TITLE,
                                                          DdlImporterUiI18n.CHOOSE_DDL_FILE_DIALOG_MSG,
                                                          new ChooseFileDialogContentProvider() {

                                                              @Override
                                                              boolean validFile( final IFile file ) {
                                                                  String ext = file.getFileExtension();
                                                                  if (ext == null) return false;
                                                                  ext = ext.toLowerCase();
                                                                  return "ddl".equals(ext) || "sql".equals(ext); //$NON-NLS-1$ //$NON-NLS-2$
                                                              }
                                                          });
        final IResource choice = showChooseDialog(dlg);
        if (choice == null) return;
        
        // Convert the IFile object to a File object
        final File realFile = choice.getLocation().toFile();
        ddlFileCombo.setText(realFile.toString());
        tabFromDdlFileCombo();
    }

    void chooseModel() {
        final ChooseFileDialog dlg = new ChooseFileDialog(DdlImporterUiI18n.CHOOSE_MODEL_FILE_DIALOG_TITLE,
                                                          DdlImporterUiI18n.CHOOSE_MODEL_FILE_DIALOG_MSG,
                                                          new ChooseFileDialogContentProvider() {

                                                              @Override
                                                              boolean validFile( final IFile file ) {
                                                                  return relationalModel(file);
                                                              }
                                                          });
        if (importer.modelFile() != null) dlg.setInitialSelection(importer.modelFile());
        final IResource choice = showChooseDialog(dlg);
        if (choice == null) return;
        
        // Existing IReource is returned
        
        // Set the file text field
        this.modelNameFld.setText(choice.getFullPath().removeFileExtension().lastSegment().toString());
        
        
        // Set the file location text field
        IPath folder = choice.getFullPath().removeLastSegments(1);
        this.modelFolderFld.setText(folder.toString());
        
        
        //ddlFileCombo.setText(choice.removeFileExtension().lastSegment());
    }

    void chooseModelFolder() {
        final ChooseDialog dlg = new ChooseDialog(DdlImporterUiI18n.CHOOSE_MODEL_FOLDER_DIALOG_TITLE,
                                                  DdlImporterUiI18n.CHOOSE_MODEL_FOLDER_DIALOG_MSG,
                                                  new ChooseDialogContentProvider() {

                                                      @Override
                                                      public final IResource[] getChildren( final IContainer container ) {
                                                          final List<IResource> children = new ArrayList<IResource>();
                                                          try {
                                                              for (final IResource resource : container.members())
                                                                  if (resource instanceof IContainer) children.add(resource);
                                                          } catch (final CoreException error) {
                                                              ErrorHandler.toExceptionDialog(error);
                                                          }
                                                          return children.toArray(new IResource[children.size()]);
                                                      }

                                                      @Override
                                                      public final boolean hasChildren( final IContainer container ) {
                                                          try {
                                                              for (final IResource resource : container.members())
                                                                  if (resource instanceof IContainer) return true;
                                                          } catch (final CoreException error) {
                                                              ErrorHandler.toExceptionDialog(error);
                                                          }
                                                          return false;
                                                      }
                                                  });
        if (importer.modelFolder() != null) dlg.setInitialSelection(importer.modelFolder());
        final IResource choice = showChooseDialog(dlg);
        if (choice == null) return;
        modelFolderFld.setText(choice.getFullPath().toString());
        modelNameFld.setFocus();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( final Composite parent ) {
        final IDialogSettings settings = getDialogSettings();
        initDlgFolderName = settings.get(INITIAL_DIALOG_FOLDER_SETTING);

        final Composite hostPanel = new Composite(parent, SWT.NONE);
        hostPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        hostPanel.setLayout(new GridLayout(1, false));

        // Create page            
        DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel, SWT.H_SCROLL | SWT.V_SCROLL);
    	scrolledComposite.setExpandHorizontal(true);
    	scrolledComposite.setExpandVertical(true);
        GridLayoutFactory.fillDefaults().equalWidth(false).applyTo(scrolledComposite);
        GridDataFactory.fillDefaults().grab(true,  false);

        final Composite panel = scrolledComposite.getPanel(); //new Composite(scrolledComposite, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        panel.setLayout(new GridLayout(3, false));

        setControl(panel);


        Composite ddlFileGroup = WidgetFactory.createGroup(panel, DdlImporterUiI18n.FILE_GROUP_LABEL, GridData.FILL_HORIZONTAL, 3, 3);
        // ----------------------------------------
        // DDL File controls 
        // ----------------------------------------
        WidgetFactory.createLabel(ddlFileGroup, GridData.VERTICAL_ALIGN_CENTER, DdlImporterUiI18n.DDL_FILE_LABEL);
        String[] ddlFileHistory = settings.getArray(HISTORY_SETTING);
        List<String> historyList = null;
        if(ddlFileHistory!=null) {
        	historyList = Arrays.asList(ddlFileHistory);
        } else {
        	historyList = Collections.emptyList();
        }
        ddlFileCombo = WidgetFactory.createCombo(ddlFileGroup, SWT.NONE, GridData.FILL_HORIZONTAL, historyList, new LabelProvider(), false) ;
        ddlFileCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( final ModifyEvent event ) {
                ddlFileModified();
            }
        });
        ddlFileCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                ddlFileSelected();
            }
        });
        final Composite buttonPanel = WidgetFactory.createPanel(ddlFileGroup, SWT.NO_TRIM);
        Button button = WidgetFactory.createButton(buttonPanel,
                                                   DdlImporterUiI18n.CHOOSE_FROM_FILE_SYSTEM_BUTTON,
                                                   GridData.HORIZONTAL_ALIGN_FILL);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                chooseDdlFile();
            }
        });
        button = WidgetFactory.createButton(buttonPanel,
                                            DdlImporterUiI18n.CHOOSE_FROM_WORKSPACE_BUTTON,
                                            GridData.HORIZONTAL_ALIGN_FILL);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                chooseDdlFileFromWorkspace();
            }
        });   
        
		TabFolder tabFolder = createTabFolder(panel);
		
		createModelDefinitionTab(tabFolder);
		createNameOptionsTab(tabFolder);
		createDdlTab(tabFolder);
		
        scrolledComposite.sizeScrolledPanel();
        
        setControl(hostPanel);

        if (selectedFile != null) {
            if (relationalModel(selectedFile)) {
                modelNameFld.setText(selectedFile.getFullPath().removeFileExtension().lastSegment());
                generateModelName = false;
            } else {
                String ext = selectedFile.getFileExtension();
                if (ext != null) {
                    ext = ext.toLowerCase();
                    if ("ddl".equals(ext) || "sql".equals(ext)) ddlFileCombo.setText(selectedFile.getLocation().toString()); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        } else validate();
    }
    
    private TabFolder createTabFolder(Composite parent) {
        TabFolder tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER | SWT.NO_SCROLL);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(tabFolder);
        return tabFolder;
    }
    
	private void createModelDefinitionTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createModelDefinitionPanel(folderParent);

        this.modelDefinitionTab = new TabItem(folderParent, SWT.NONE);
        this.modelDefinitionTab.setControl(thePanel);
        this.modelDefinitionTab.setText(DdlImporterUiI18n.MODEL_GROUP_LABEL);
	}
	
	private void createNameOptionsTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createOptionsPanel(folderParent);

        this.optionsTab = new TabItem(folderParent, SWT.NONE);
        this.optionsTab.setControl(thePanel);
        this.optionsTab.setText(DdlImporterUiI18n.OPTIONS_GROUP_LABEL);
	}
	
	private void createDdlTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createDdlPanel(folderParent);

        this.ddlTab = new TabItem(folderParent, SWT.NONE);
        this.ddlTab.setControl(thePanel);
        this.ddlTab.setText(DdlImporterUiI18n.DDL_FILE_CONTENTS_TITLE);
	}
	
    /*
     * 
     * Create the model definition panel
     */
    private Composite createModelDefinitionPanel( Composite parent ) {
        // Create Group for Model Info
        final Composite mainPanel = WidgetFactory.createPanel(parent, GridData.HORIZONTAL_ALIGN_FILL, 1, 3);
        GridLayoutFactory.fillDefaults().numColumns(3).margins(10, 10).applyTo(mainPanel);
        
        if( mainPanel.getHorizontalBar() != null ) {
        	mainPanel.getHorizontalBar().setVisible(false);
        }
        
        // ----------------------------------------
        // Model Folder controls
        // ----------------------------------------
        WidgetFactory.createLabel(mainPanel, GridData.VERTICAL_ALIGN_CENTER, DdlImporterUiI18n.MODEL_FOLDER_LABEL);
        modelFolderFld = WidgetFactory.createTextField(mainPanel, GridData.FILL_HORIZONTAL);
        final IContainer modelFolder = importer.modelFolder();
        if (modelFolder != null) modelFolderFld.setText(modelFolder.getFullPath().toString());
        modelFolderFld.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( final ModifyEvent event ) {
                validate();
            }
        });
        Button button = WidgetFactory.createButton(mainPanel, DdlImporterUiI18n.CHOOSE_BUTTON);
        if (projects.length == 0) button.setEnabled(false);
        else button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                chooseModelFolder();
            }
        });

        // ----------------------------------------
        // Model Name controls
        // ----------------------------------------
        WidgetFactory.createLabel(mainPanel, GridData.VERTICAL_ALIGN_CENTER, DdlImporterUiI18n.MODEL_NAME_LABEL);
        modelNameFld = WidgetFactory.createTextField(mainPanel);
        modelNameFld.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( final ModifyEvent event ) {
                modelNameModified();
            }
        });
        button = WidgetFactory.createButton(mainPanel, DdlImporterUiI18n.CHOOSE_BUTTON);
        if (projects.length == 0) button.setEnabled(false);
        else button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                chooseModel();
            }
        });

        // ----------------------------------------
        // Model Type controls
        // ----------------------------------------
        WidgetFactory.createLabel(mainPanel, GridData.VERTICAL_ALIGN_CENTER, DdlImporterUiI18n.MODEL_TYPE_LABEL);
        final Composite modelTypePanel = WidgetFactory.createPanel(mainPanel, SWT.NONE, GridData.HORIZONTAL_ALIGN_FILL, 2, 2);
        modelTypeCombo = WidgetFactory.createCombo(modelTypePanel, SWT.READ_ONLY, GridData.HORIZONTAL_ALIGN_FILL, MODEL_TYPES,   new LabelProvider() {

            @Override
            public String getText( final Object element ) {
                return ((ModelType)element).getDisplayName();
            }
        });
        modelTypeCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( final ModifyEvent event ) {
                modelTypeModified();
            }
        });
        modelTypeCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                validate();
            }
        });
        // 'Auto-select' checkbox is checked initially
        generateDefaultSQLCheckBox = WidgetFactory.createCheckBox(modelTypePanel,
        		DdlImporterUiI18n.GENERATE_DEFAULT_SQL_CHECKBOX,
        		0,
        		1,
        		false);
        generateDefaultSQLCheckBox.addSelectionListener(new SelectionAdapter() {

        	@Override
        	public void widgetSelected( final SelectionEvent event ) {
        		generateDefaultSQLChanged();
        	}
        });
        generateDefaultSQLCheckBox.setEnabled(false);
        modelTypeCombo.select(modelTypeCombo.indexOf(ModelType.PHYSICAL_LITERAL.getDisplayName()));
        
        return mainPanel;
    }

    void ddlFileModified() {
        validate();
        final String ddlFileName = importer.ddlFileName();
        if (ddlFileName == null) {
            if (generateModelName) {
                modelNameFld.setText(StringConstants.EMPTY_STRING);
                generateModelName = true;
            }
            ddlFileContentsBox.setText(StringConstants.EMPTY_STRING);
        } else {
            if (generateModelName) {
                modelNameFld.setText(Path.fromOSString(ddlFileName).removeFileExtension().lastSegment());
                generateModelName = true;
            }
            try {
                File ddlFile = new File(ddlFileName);
                ddlFileContentsBox.setText(FileUtil.readSafe(ddlFile));
            } catch (final IOException error) {
                ErrorHandler.toExceptionDialog(error);
            }
        }
        ddlFileContentsBox.setTopIndex(0);
    }
    
    /*
     * 
     * Create the Options panel
     */
    private Composite createOptionsPanel( Composite parent ) {
        final IDialogSettings settings = getDialogSettings();
        // Create Group for Model Info
        final Composite mainPanel = WidgetFactory.createPanel(parent, GridData.HORIZONTAL_ALIGN_FILL, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(mainPanel);
        
        if( mainPanel.getHorizontalBar() != null ) {
        	mainPanel.getHorizontalBar().setVisible(false);
        }

        // ----------------------------------------
        // Option checkboxes
        // ----------------------------------------
        optToSetModelEntityDescriptionCheckBox = WidgetFactory.createCheckBox(mainPanel,
                                                                              DdlImporterUiI18n.OPT_TO_SET_MODEL_ENTITY_DESCRIPTION_LABEL,
                                                                              0,
                                                                              PANEL_GRID_SPAN,
                                                                              settings.getBoolean(OPT_TO_SET_MODEL_ENTITY_DESCRIPTION_SETTING));
        optToSetModelEntityDescriptionCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                optToSetModelEntityDescriptionModified();
            }
        });
        
        // make sure importer has restored setting
        optToSetModelEntityDescriptionModified();

        optToCreateModelEntitiesForUnsupportedDdlCheckBox = WidgetFactory.createCheckBox(mainPanel,
                                                                                         DdlImporterUiI18n.OPT_TO_CREATE_MODEL_ENTITIES_FOR_UNSUPPORTED_DDL_LABEL,
                                                                                         0,
                                                                                         PANEL_GRID_SPAN,
                                                                                         settings.getBoolean(OPT_TO_CREATE_MODEL_ENTITIES_FOR_UNSUPPORTED_DDL_SETTING));
        optToCreateModelEntitiesForUnsupportedDdlCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                optToCreateModelEntitiesForUnsupportedDdlModified();
            }
        });
        
        // make sure importer has restored setting
        optToCreateModelEntitiesForUnsupportedDdlModified();
        
        // CheckBox for Connection Profile - defaults to checked
        filterRedundantUCsCB = WidgetFactory.createCheckBox(mainPanel, DdlImporterUiI18n.FILTER_UCS_FOR_DEFINED_PKS_LABEL, 0, PANEL_GRID_SPAN, true);
        filterRedundantUCsCB.setSelection(true);
        filterRedundantUCsCB.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected( final SelectionEvent event ) {
	            options.put(TeiidDDLConstants.DDL_IMPORT_FILTER_CONSTRAINTS, Boolean.toString(filterRedundantUCsCB.getSelection()));
	        }
	    });
        
        

        return mainPanel;
    }
    
    /*
     * 
     * Create the DDL panel
     */
    private Composite createDdlPanel( Composite parent ) {
        // Create Group for Model Info
        final Composite mainPanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(mainPanel);
        
        ddlFileContentsBox = WidgetFactory.createTextBox(mainPanel);
        ddlFileContentsBox.setEditable(false);
        
        return mainPanel;
    }

    void ddlFileSelected() {
        ddlFileModified();
        tabFromDdlFileCombo();
    }

    void modelNameModified() {
        generateModelName = false;
        validate();
    }

    void modelTypeModified() {
        importer.setModelType(MODEL_TYPES.get(modelTypeCombo.indexOf(modelTypeCombo.getText())));
        if( modelTypeCombo.getText().equals(ModelType.VIRTUAL_LITERAL.getDisplayName())) {
        	this.generateDefaultSQLCheckBox.setEnabled(true);
        } else {
        	this.generateDefaultSQLCheckBox.setEnabled(false);
        }
    }
    
    void generateDefaultSQLChanged() {
    	importer.setGenerateDefaultSQL(this.generateDefaultSQLCheckBox.getSelection());
    }

    void optToCreateModelEntitiesForUnsupportedDdlModified() {
        importer.setOptToCreateModelEntitiesForUnsupportedDdl(optToCreateModelEntitiesForUnsupportedDdlCheckBox.getSelection());
    }

    void optToSetModelEntityDescriptionModified() {
        importer.setOptToSetModelEntityDescription(optToSetModelEntityDescriptionCheckBox.getSelection());
    }

//    void panelResized() {
//        if (ddlFileContentsExpander.getExpanded()) sizeDdlFileContents();
//    }

    boolean relationalModel( final IFile file ) {
        if (file == null) return false;
        try {
            final ModelResource model = ModelerCore.getModelEditor().findModelResource(file);
            if (model == null) return false;
            if (ModelUtil.isModelFile(model.getEmfResource())
                && RelationalPackage.eNS_URI.equals(ModelUtil.getXmiHeader(model.getResource()).getPrimaryMetamodelURI())) return true;
        } catch (final CoreException error) {
            error.printStackTrace();
            WidgetUtil.showError(error);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.wizard.IPersistentWizardPage#saveSettings()
     */
    @Override
    public void saveSettings() {
        final IDialogSettings settings = getDialogSettings();
        if (initDlgFolderName != null) settings.put(INITIAL_DIALOG_FOLDER_SETTING, initDlgFolderName);
        final String file = importer.ddlFileName();
        if (file!=null && !file.isEmpty()) {
            String[] history = ddlFileCombo.getItems();
            boolean exists = false;
            for (final String oldFile : history)
                if (oldFile.equals(file)) {
                    exists = true;
                    break;
                }
            if (!exists) {
                if (history.length == MAX_HISTORY) {
                    System.arraycopy(history, 0, history, 1, MAX_HISTORY - 1);
                } else {
                    final String[] newHistory = new String[history.length + 1];
                    System.arraycopy(history, 0, newHistory, 1, history.length);
                    history = newHistory;
                }
                history[0] = file;
                ddlFileCombo.setItems(history);
                settings.put(HISTORY_SETTING, history);
            }
        }
        settings.put(OPT_TO_SET_MODEL_ENTITY_DESCRIPTION_SETTING, optToSetModelEntityDescriptionCheckBox.getSelection());
        settings.put(OPT_TO_CREATE_MODEL_ENTITIES_FOR_UNSUPPORTED_DDL_SETTING,
                     optToCreateModelEntitiesForUnsupportedDdlCheckBox.getSelection());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardPage#setErrorMessage(java.lang.String)
     */
    @Override
    public void setErrorMessage( final String message ) {
        if (message == null || getErrorMessage() == null) super.setErrorMessage(message);
        setPageComplete(message == null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( final boolean visible ) {
        super.setVisible(visible);
        if (importer.modelFolder() != null) ddlFileCombo.setFocus();
    }

    private IResource showChooseDialog( final ElementTreeSelectionDialog dialog ) {
        return dialog.open() == Window.OK ? ((IResource)dialog.getFirstResult()) : null;
    }

    void tabFromDdlFileCombo() {
        if (importer.ddlFileName() != null) if (importer.modelFolder() == null) modelFolderFld.setFocus();
        else modelNameFld.setFocus();
    }

    void validate() {
        setErrorMessage(null);
        final String ddlFileName = ddlFileCombo.getText();
        try {
            importer.setDdlFileName(ddlFileName);
        } catch (final EmptyArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.DDL_FILE_MSG);
        } catch (final IllegalArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.DDL_FILE_MSG + ' ' + error.getMessage());
        }
        ddlFileCombo.setToolTipText(ddlFileName);
        
        try {
            importer.setModelFolder(modelFolderFld.getText());
        } catch (final EmptyArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.MODEL_FOLDER_MSG);
        } catch (final IllegalArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.MODEL_FOLDER_MSG + ' ' + error.getMessage());
        }
        final String modelName = modelNameFld.getText().trim();
        if (!CoreStringUtil.isEmpty(modelName)) {
            String validationMsg = nameValidator.checkValidName(modelName);
            if (!CoreStringUtil.isEmpty(validationMsg)) {
            	setErrorMessage(DdlImporterUiI18n.MODEL_MSG+'\n'+validationMsg);
            }
        }
        
        try {
            importer.setModelName(modelName);
        } catch (final EmptyArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.MODEL_MSG);
        } catch (final IllegalArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.MODEL_MSG + ' ' + error.getMessage());
        }
        if (importer.modelFile() != null && importer.modelFile().exists()) {
            ModelResource model;
            try {
                model = ModelerCore.getModelEditor().findModelResource(importer.modelFile());
                final ModelType type = ModelUtil.isPhysical(model.getEmfResource()) ? ModelType.PHYSICAL_LITERAL : ModelType.VIRTUAL_LITERAL;
                modelTypeCombo.select(modelTypeCombo.indexOf(type.getDisplayName()));
                modelTypeCombo.setEnabled(false);
            } catch (final Exception error) {
                error.printStackTrace();
                WidgetUtil.showError(error);
                return;
            }
        } else modelTypeCombo.setEnabled(true);
        if (getErrorMessage() == null) {
            String msg = I18n.format(DdlImporterUiI18n.PAGE_DESCRIPTION,
                                     importer.modelFile().exists() ? DdlImporterUiI18n.UPDATE_MSG_PART : DdlImporterUiI18n.CREATE_MSG_PART,
                                     importer.modelType().getDisplayName().toLowerCase(),
                                     modelName,
                                     importer.ddlFileName());
            final IContainer modelFolder = importer.modelFolder();
            if (!modelFolder.exists()) msg = msg
                                             + (modelFolder.getProject().exists() ? DdlImporterUiI18n.PATH_MSG_PART : DdlImporterUiI18n.PROJECT_MSG_PART)
                                             + I18n.format(DdlImporterUiI18n.PROJECT_MSG_PART, modelFolder.getFullPath());
            setDescription(msg);
        }
    }

    private class ChooseDialog extends ElementTreeSelectionDialog {

        ChooseDialog( final String title,
                      final String message,
                      final ChooseDialogContentProvider contentProvider ) {
            super(TeiidDdlImporterPage.this.getShell(), new ModelExplorerLabelProvider(), contentProvider);
            setTitle(title);
            setMessage(message);
            setAllowMultiple(false);
            setInput(projects);
        }
    }

    abstract class ChooseDialogContentProvider implements ITreeContentProvider {

        @Override
        public final void dispose() {
        }

        abstract IResource[] getChildren( final IContainer container );

        @Override
        public final Object[] getChildren( final Object parent ) {
            return getChildren((IContainer)parent);
        }

        @Override
        public Object[] getElements( final Object inputElement ) {
            return projects;
        }

        @Override
        public final Object getParent( final Object element ) {
            return ((IResource)element).getParent();
        }

        abstract boolean hasChildren( final IContainer container );

        @Override
        public boolean hasChildren( final Object element ) {
            if (element instanceof IFile) return false;
            return hasChildren((IContainer)element);
        }

        @Override
        public void inputChanged( final Viewer viewer,
                                  final Object oldInput,
                                  final Object newInput ) {
        }
    }

    private class ChooseFileDialog extends ChooseDialog {

        ChooseFileDialog( final String title,
                          final String message,
                          final ChooseDialogContentProvider contentProvider ) {
            super(title, message, contentProvider);
            setValidator(new ISelectionStatusValidator() {

                @Override
                public IStatus validate( final Object[] selection ) {
                    if (selection.length == 1 && selection[0] instanceof IFile) return new Status(IStatus.OK,
                                                                                                  DdlImporterUiPlugin.ID, null);
                    return new Status(IStatus.ERROR, DdlImporterUiPlugin.ID, null);
                }
            });
        }
    }

    abstract class ChooseFileDialogContentProvider extends ChooseDialogContentProvider {

        private IProject[] projects;

        @Override
        public final IResource[] getChildren( final IContainer container ) {
            final List<IResource> children = new ArrayList<IResource>();
            try {
                for (final IResource resource : container.members())
                    if ((resource instanceof IContainer && hasChildren((IContainer)resource))
                        || (resource instanceof IFile && validFile((IFile)resource))) children.add(resource);
            } catch (final CoreException error) {
                ErrorHandler.toExceptionDialog(error);
            }
            return children.toArray(new IResource[children.size()]);
        }

        @Override
        public Object[] getElements( final Object inputElement ) {
            if (projects == null) {
                final List<IProject> projects = new ArrayList<IProject>();
                for (final IProject project : TeiidDdlImporterPage.this.projects)
                    if (hasChildren(project)) projects.add(project);
                this.projects = projects.toArray(new IProject[projects.size()]);
            }
            return projects;
        }

        @Override
        public final boolean hasChildren( final IContainer container ) {
            try {
                for (final IResource resource : container.members())
                    if (resource instanceof IContainer) {
                        if (hasChildren(resource)) return true;
                    } else if (validFile((IFile)resource)) return true;
            } catch (final CoreException error) {
                ErrorHandler.toExceptionDialog(error);
            }
            return false;
        }

        abstract boolean validFile( IFile resource );
    }
}
