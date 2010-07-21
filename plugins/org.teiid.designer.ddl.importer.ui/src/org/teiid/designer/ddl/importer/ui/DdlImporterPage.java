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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ExpandAdapter;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.modeshape.common.util.IoUtil;
import org.teiid.core.I18n;
import org.teiid.core.exception.EmptyArgumentException;
import org.teiid.designer.ddl.importer.DdlImporter;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.IPersistentWizardPage;

class DdlImporterPage extends WizardPage implements IPersistentWizardPage {

    private static final int PANEL_GRID_SPAN = 3;
    private static final String INITIAL_DIALOG_FOLDER_SETTING = "initialDialogFolder"; //$NON-NLS-1$
    private static final String HISTORY_SETTING = "history"; //$NON-NLS-1$
    private static final int MAX_HISTORY = 5;
    private static final List<ModelType> MODEL_TYPES = Arrays.asList(ModelType.PHYSICAL_LITERAL, ModelType.VIRTUAL_LITERAL);
    private static final String DDL_FILE_CONTENTS_SHOWN_SETTING = "ddlFileContentsShown"; //$NON-NLS-1$

    private final DdlImporter importer;

    private Combo ddlFileCombo;
    private Text modelFolderFld;
    private Text modelNameFld;
    private Combo modelTypeCombo;
    private ExpandBar ddlFileContentsExpanderBar;
    private ExpandItem ddlFileContentsExpander;
    private Text ddlFileContentsBox;

    private String initDlgFolderName;
    final IProject[] projects;
    private boolean generateModelName = true;
    private int ddlFileContentsBoxY;

    DdlImporterPage( final DdlImporter importer,
                     final IProject[] projects,
                     final IStructuredSelection selection ) {
        super(DdlImporterPage.class.getSimpleName(), DdlImporterUiI18n.PAGE_TITLE, null);
        this.importer = importer;
        this.projects = projects;
        final Set<IContainer> selectedContainers = new HashSet<IContainer>();
        for (final Iterator<?> iter = selection.iterator(); iter.hasNext();) {
            final Object resource = iter.next();
            if (resource instanceof IContainer) selectedContainers.add((IContainer)resource);
            else selectedContainers.add(((IResource)resource).getParent());
            if (selectedContainers.size() > 1) return;
        }
        if (selectedContainers.size() == 0) {
            if (projects.length == 1) importer.setModelFolder(projects[0]);
        } else importer.setModelFolder(selectedContainers.iterator().next());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        final IFile model = importer.model();
        return model != null && model.exists();
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

        final Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1, PANEL_GRID_SPAN);
        setControl(panel);

        WidgetFactory.createLabel(panel, DdlImporterUiI18n.DDL_FILE_LABEL);
        ddlFileCombo = WidgetFactory.createCombo(panel, SWT.NONE, GridData.FILL_HORIZONTAL, settings.getArray(HISTORY_SETTING));
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
        Button browseButton = WidgetFactory.createButton(panel, DdlImporterUiI18n.BROWSE_BUTTON);
        browseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                selectDdlFile();
            }
        });

        WidgetFactory.createLabel(panel, DdlImporterUiI18n.MODEL_FOLDER_LABEL);
        modelFolderFld = WidgetFactory.createTextField(panel);
        final IContainer modelFolder = importer.modelFolder();
        if (modelFolder != null) modelFolderFld.setText(modelFolder.getFullPath().toString());
        modelFolderFld.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( final ModifyEvent event ) {
                validate();
            }
        });
        browseButton = WidgetFactory.createButton(panel, DdlImporterUiI18n.BROWSE_BUTTON);
        if (projects.length == 0) browseButton.setEnabled(false);
        else browseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                selectModelFolder();
            }
        });

        WidgetFactory.createLabel(panel, DdlImporterUiI18n.MODEL_NAME_LABEL);
        modelNameFld = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL);
        modelNameFld.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( final ModifyEvent event ) {
                modelNameModified();
            }
        });
        WidgetFactory.createLabel(panel); // Filler

        WidgetFactory.createLabel(panel, DdlImporterUiI18n.MODEL_TYPE_LABEL);
        modelTypeCombo = WidgetFactory.createCombo(panel, SWT.READ_ONLY, 0, MODEL_TYPES, new LabelProvider() {

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
        modelTypeCombo.select(modelTypeCombo.indexOf(ModelType.PHYSICAL_LITERAL.getDisplayName()));
        WidgetFactory.createLabel(panel); // Filler

        ddlFileContentsExpanderBar = new ExpandBar(panel, SWT.NONE);
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = PANEL_GRID_SPAN;
        ddlFileContentsExpanderBar.setLayoutData(gridData);
        ddlFileContentsExpanderBar.addExpandListener(new ExpandAdapter() {

            @Override
            public void itemExpanded( final ExpandEvent event ) {
                sizeDdlFileContents();
            }
        });
        ddlFileContentsExpanderBar.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized( final ControlEvent event ) {
                panelResized();
            }
        });
        ddlFileContentsExpander = new ExpandItem(ddlFileContentsExpanderBar, SWT.NONE);
        ddlFileContentsExpander.setText(DdlImporterUiI18n.DDL_FILE_CONTENTS_TITLE);
        ddlFileContentsBox = WidgetFactory.createTextBox(ddlFileContentsExpanderBar);
        ddlFileContentsExpander.setControl(ddlFileContentsBox);
        ddlFileContentsBox.setEditable(false);
        ddlFileContentsExpander.setExpanded(settings.getBoolean(DDL_FILE_CONTENTS_SHOWN_SETTING));

        validate();
    }

    void ddlFileModified() {
        validate();
        final String ddlFileName = importer.ddlFileName();
        if (ddlFileName == null) {
            if (generateModelName) {
                modelNameFld.setText(StringUtilities.EMPTY_STRING);
                generateModelName = true;
            }
            ddlFileContentsBox.setText(StringUtilities.EMPTY_STRING);
        } else {
            if (generateModelName) {
                modelNameFld.setText(Path.fromOSString(ddlFileName).removeFileExtension().lastSegment());
                generateModelName = true;
            }
            try {
                ddlFileContentsBox.setText(IoUtil.read(new File(ddlFileName)));
            } catch (final IOException error) {
                throw CoreModelerPlugin.toRuntimeException(error);
            }
        }
        ddlFileContentsBox.setTopIndex(0);
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
    }

    void panelResized() {
        if (ddlFileContentsExpander.getExpanded()) sizeDdlFileContents();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.IPersistentWizardPage#saveSettings()
     */
    @Override
    public void saveSettings() {
        final IDialogSettings settings = getDialogSettings();
        if (initDlgFolderName != null) settings.put(INITIAL_DIALOG_FOLDER_SETTING, initDlgFolderName);
        final String file = importer.ddlFileName();
        if (!file.isEmpty()) {
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
        settings.put(DDL_FILE_CONTENTS_SHOWN_SETTING, ddlFileContentsExpander.getExpanded());
    }

    void selectDdlFile() {
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
        dlg.setFilterExtensions(new String[] {"*.ddl; sql", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
        dlg.setFilterNames(new String[] {"DDL Files", "All Files"}); //$NON-NLS-1$ //$NON-NLS-2$
        final String fileName = dlg.open();
        if (fileName != null) {
            ddlFileCombo.setText(fileName);
            tabFromDdlFileCombo();
        }
        initDlgFolderName = dlg.getFilterPath();
    }

    void selectModelFolder() {
        final ITreeContentProvider contentProvider = new ITreeContentProvider() {

            @Override
            public void dispose() {
            }

            @Override
            public Object[] getChildren( final Object element ) {
                final List<IResource> children = new ArrayList<IResource>();
                try {
                    for (final IResource resource : ((IContainer)element).members())
                        if (resource instanceof IContainer) children.add(resource);
                } catch (final CoreException error) {
                    throw CoreModelerPlugin.toRuntimeException(error);
                }
                return children.toArray();
            }

            @Override
            public Object[] getElements( final Object inputElement ) {
                return projects;
            }

            @Override
            public Object getParent( final Object element ) {
                return ((IContainer)element).getParent();
            }

            @Override
            public boolean hasChildren( final Object element ) {
                try {
                    for (final IResource resource : ((IContainer)element).members())
                        if (resource instanceof IContainer) return true;
                } catch (final CoreException error) {
                    throw CoreModelerPlugin.toRuntimeException(error);
                }
                return false;
            }

            @Override
            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
            }
        };
        final ElementTreeSelectionDialog dlg = new ElementTreeSelectionDialog(getShell(), new ModelExplorerLabelProvider(),
                                                                              contentProvider);
        dlg.setTitle(DdlImporterUiI18n.MODEL_FOLDER_SELECTION_DIALOG_TITLE);
        dlg.setMessage(DdlImporterUiI18n.MODEL_FOLDER_SELECTION_DIALOG_MSG);
        dlg.setAllowMultiple(false);
        dlg.setValidator(new ISelectionStatusValidator() {

            @Override
            public IStatus validate( final Object[] selection ) {
                if (selection.length == 0) return new Status(IStatus.ERROR, DdlImporterUiPlugin.ID, null);
                return new Status(IStatus.OK, DdlImporterUiPlugin.ID, null);
            }
        });
        final IContainer modelFolder = importer.modelFolder();
        if (modelFolder != null) dlg.setInitialSelection(modelFolder);
        dlg.setInput(projects);
        if (dlg.open() != Window.OK) return;
        modelFolderFld.setText(((IContainer)dlg.getFirstResult()).getFullPath().toString());
        modelNameFld.setFocus();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardPage#setErrorMessage(java.lang.String)
     */
    @Override
    public void setErrorMessage( final String message ) {
        super.setErrorMessage(message);
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
        if (ddlFileContentsExpander.getExpanded()) sizeDdlFileContents();
        if (importer.modelFolder() != null) ddlFileCombo.setFocus();
    }

    //
    // void showDdlFileContents() {
    // if (ddlFileContentsBox.getText().isEmpty() && importer.ddlFileName() != null) readDdlFile();
    // sizeDdlFileContents();
    // }

    void sizeDdlFileContents() {
        if (ddlFileContentsBoxY == 0) ddlFileContentsBoxY = ddlFileContentsBox.getLocation().y;
        ddlFileContentsExpander.setHeight(ddlFileContentsExpanderBar.getSize().y - ddlFileContentsBoxY);
    }

    void tabFromDdlFileCombo() {
        if (importer.ddlFileName() != null) if (importer.modelFolder() == null) modelFolderFld.setFocus();
        else modelNameFld.setFocus();
    }

    void validate() {
        final String ddlFileName = ddlFileCombo.getText();
        try {
            importer.setDdlFileName(ddlFileName);
        } catch (final EmptyArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.DDL_FILE_MSG);
            return;
        } catch (final IllegalArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.DDL_FILE_MSG + ' ' + error.getMessage());
            return;
        }
        ddlFileCombo.setToolTipText(ddlFileName);
        try {
            importer.setModelFolder(modelFolderFld.getText());
        } catch (final EmptyArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.MODEL_FOLDER_MSG);
            return;
        } catch (final IllegalArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.MODEL_FOLDER_MSG + ' ' + error.getMessage());
            return;
        }
        final String modelName = modelNameFld.getText().trim();
        try {
            importer.setModel(modelName);
        } catch (final EmptyArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.MODEL_MSG);
            return;
        } catch (final IllegalArgumentException error) {
            setErrorMessage(DdlImporterUiI18n.MODEL_MSG + ' ' + error.getMessage());
            return;
        }
        if (importer.model().exists()) {
            ModelResource model;
            try {
                model = ModelUtil.getModelResource(importer.model(), false);
                final ModelType type = ModelUtil.isPhysical(model.getEmfResource()) ? ModelType.PHYSICAL_LITERAL : ModelType.VIRTUAL_LITERAL;
                modelTypeCombo.select(modelTypeCombo.indexOf(type.getDisplayName()));
                modelTypeCombo.setEnabled(false);
            } catch (final Exception error) {
                error.printStackTrace();
                WidgetUtil.showError(error);
                return;
            }
        } else modelTypeCombo.setEnabled(true);
        setErrorMessage(null);
        String msg = I18n.format(DdlImporterUiI18n.PAGE_DESCRIPTION,
                                 importer.model().exists() ? DdlImporterUiI18n.UPDATE_MSG_PART : DdlImporterUiI18n.CREATE_MSG_PART,
                                 importer.getModelType().getDisplayName().toLowerCase(),
                                 modelName,
                                 importer.ddlFileName());
        final IContainer modelFolder = importer.modelFolder();
        if (!modelFolder.exists()) msg = msg
                                         + (modelFolder.getProject().exists() ? DdlImporterUiI18n.PATH_MSG_PART : DdlImporterUiI18n.PROJECT_MSG_PART)
                                         + I18n.format(DdlImporterUiI18n.PROJECT_MSG_PART, modelFolder.getFullPath());
        setDescription(msg);
    }
}
