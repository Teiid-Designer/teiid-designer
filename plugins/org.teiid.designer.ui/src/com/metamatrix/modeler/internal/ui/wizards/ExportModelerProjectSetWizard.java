/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.teiid.core.util.FileUtils;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceFilter;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceView;
import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public final class ExportModelerProjectSetWizard extends AbstractWizard
    implements FileUtils.Constants, IExportWizard, InternalUiConstants.Widgets, PluginConstants.Images, CoreStringUtil.Constants,
    UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExportModelerProjectSetWizard.class);

    private static final ImageDescriptor IMAGE = UiPlugin.getDefault().getImageDescriptor(EXPORT_PROJECT_ICON);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private static final String FILE_DIALOG_TITLE = getString("fileDialogTitle"); //$NON-NLS-1$
    private static final String PROJECTS_GROUP = getString("projectsGroup"); //$NON-NLS-1$
    private static final String FILE_GROUP = getString("fileGroup"); //$NON-NLS-1$
    private static final String FILE_LABEL = getString("fileLabel"); //$NON-NLS-1$
    private static final String FILE_BUTTON = getString("browse_3"); //$NON-NLS-1$
    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String NO_SELECTIONS_MESSAGE = getString("noSelectionsMessage"); //$NON-NLS-1$
    private static final String NESTED_PROJECT_MESSAGE = getString("nestedProjectMessage"); //$NON-NLS-1$
    private static final String NESTED_PROJECT_ERROR = getString("nestedProjectError"); //$NON-NLS-1$
    private static final String NO_FILE_MESSAGE = getString("noFileMessage"); //$NON-NLS-1$
    private static final String INVALID_FILE_MESSAGE = getString("invalidFileMessage"); //$NON-NLS-1$
    private static final String FILE_IMPORT_TAG = ".zip";//$NON-NLS-1$
    private static final String OPTIONS_GROUP_LABEL = getString("optionsGroup.label"); //$NON-NLS-1$
    private static final String CLEAR_CONNECTION_INFO_LABEL = getString("clearConnectionInfo.label"); //$NON-NLS-1$
    private final static String STORE_ZIP_FILE_NAMES_ID = getString("storeZipFileNamesId");//$NON-NLS-1$

    private static boolean exportLicensed = false;

    private boolean projectsSelected = false;

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private ModelWorkspaceSelections selections;
    private File targetZipFileName;
    private IStructuredSelection selection;

    private WizardPage page1;
    private CheckboxTreeViewer viewer;
    private Combo zipFileCombo;
    private Button clearConnectionInfoCheckBox;

    /**
     * @since 4.0
     */
    public ExportModelerProjectSetWizard() {
        super(UiPlugin.getDefault(), TITLE, IMAGE);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        if (exportLicensed) {
            super.createPageControls(pageContainer);
        }
        this.page1.setMessage(INITIAL_MESSAGE);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {

        List selectedProjects = getSelectedProjects();

        if (this.targetZipFileName.exists() && !WidgetUtil.confirmOverwrite(this.targetZipFileName)) {
            return false;
        }
        // about to invoke the operation so save our state
        saveWidgetValues();

        if (selectedProjects.size() > 0) 
        	return executeExportOperation(
        		new ModelerProjectZipOperation(
        				selectedProjects,
                        targetZipFileName.getAbsolutePath(),
                        this.clearConnectionInfoCheckBox.getSelection()));

        return true;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
        this.selection = selection;
        this.selections = new ModelWorkspaceSelections();
        this.page1 = new AbstractWizardPage(ExportModelerProjectSetWizard.class.getSimpleName(), PAGE_TITLE) {

            public void createControl( final Composite parent ) {
                setControl(createPageControl(parent));
            }
        };

        addPage(page1);
    }

    Composite createEmptyPageControl( final Composite parent ) {
        return new Composite(parent, SWT.NONE);
    }

    /**
     * @since 4.0
     */
    Composite createPageControl( final Composite parent ) {

        // Create page
        final Composite pg = new Composite(parent, SWT.NONE);
        pg.setLayout(new GridLayout());
        // Add widgets to page
        Group group = WidgetFactory.createGroup(pg, PROJECTS_GROUP, GridData.FILL_BOTH);
        // Add contents to view form

        this.viewer = (CheckboxTreeViewer)WidgetFactory.createTreeViewer(group, SWT.CHECK | SWT.MULTI);
        viewer.addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged( CheckStateChangedEvent event ) {
                validatePage();
            }
        });

        final Tree tree = this.viewer.getTree();
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        final ModelWorkspaceView view = new ModelWorkspaceView();
        view.setRestrictedToModelWorkspaceItemsOnly(true);
        view.getModelWorkspaceFilters().add(new ModelWorkspaceFilter() {

            public boolean select( final Object parent,
                                   final Object node ) {
                if (node instanceof ModelProject && ((ModelProject)node).isOpen()) {
                    try {
                        if (!((ModelProject)node).getProject().hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID)) {
                            return true;
                        }
                    } catch (CoreException e) {
                        Util.log(e);
                    }
                }
                return false;
            }
        });
        this.selections.setModelWorkspaceView(view);

        final ITreeContentProvider treeContentProvider = new ITreeContentProvider() {

            public void dispose() {
            }

            public Object[] getChildren( final Object node ) {
                return EMPTY_STRING_ARRAY;
            }

            public Object[] getElements( final Object inputElement ) {
                try {
                    return view.getChildren(ModelerCore.getModelWorkspace());
                } catch (final ModelWorkspaceException err) {
                    Util.log(err);
                    return EMPTY_STRING_ARRAY;
                }
            }

            public Object getParent( final Object node ) {
                return null;
            }

            public boolean hasChildren( final Object node ) {
                return false;
            }

            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
            }
        };
        this.viewer.setContentProvider(treeContentProvider);
        this.viewer.setLabelProvider(new LabelProvider() {

            final WorkbenchLabelProvider workbenchProvider = new WorkbenchLabelProvider();

            @Override
            public Image getImage( final Object node ) {
                if (node instanceof EObject) {
                    return ModelUtilities.getEMFLabelProvider().getImage(node);
                }
                return workbenchProvider.getImage(((ModelWorkspaceItem)node).getResource());
            }

            @Override
            public String getText( final Object node ) {
                if (node instanceof EObject) {
                    return ModelUtilities.getEMFLabelProvider().getText(node);
                }
                return workbenchProvider.getText(((ModelWorkspaceItem)node).getResource());
            }
        });

        this.viewer.setInput(this);
        
        Group optionsGroup = WidgetFactory.createGroup(pg, OPTIONS_GROUP_LABEL, GridData.FILL_HORIZONTAL);
        // Add contents to view form
        
        clearConnectionInfoCheckBox = new Button(optionsGroup,SWT.CHECK);
        clearConnectionInfoCheckBox.setText(CLEAR_CONNECTION_INFO_LABEL);

        final IDialogSettings settings = getDialogSettings();

        group = WidgetFactory.createGroup(pg, FILE_GROUP, GridData.FILL_HORIZONTAL, 1, 3);
        {
            WidgetFactory.createLabel(group, FILE_LABEL);
            this.zipFileCombo = WidgetFactory.createCombo(group,
                                                          SWT.NONE,
                                                          GridData.FILL_HORIZONTAL,
                                                          settings.getArray(FILE_LABEL));
            this.zipFileCombo.addModifyListener(new ModifyListener() {

                public void modifyText( final ModifyEvent event ) {
                    handleSourceFileChanged();
                }
            });
            WidgetFactory.createButton(group, FILE_BUTTON).addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    handleBrowseButtonSelected();
                }
            });
        }
        // Initialize widgets
        if (this.selection != null) {
            final Iterator iter = this.selection.iterator();
            for (@SuppressWarnings("unused")
			int ndx = 0; iter.hasNext(); ++ndx) {
                final Object obj = iter.next();
                final IPath path = (obj instanceof IResource ? ((IResource)obj).getFullPath() : view.getPath(obj));
                if (path != null) {
                    try {
                        Object proj = view.findObject(path);
                        if (proj != null) viewer.setChecked(proj, true);
                    } catch (final ModelWorkspaceException err) {
                        Util.log(err);
                        WidgetUtil.showError(err);
                    }
                }
            }
        }

        // Initialize widgets w/ last selections made by user
        restoreWidgetValues();
        this.page1.setMessage(INITIAL_MESSAGE);
        this.getShell().getDisplay().asyncExec(new Runnable() {

            public void run() {
                validatePage();
            }
        });
        return pg;
    }

    /**
     * @since 4.0
     */
    void handleBrowseButtonSelected() {
        // Display file dialog for user to choose libraries
        final FileDialog dlg = new FileDialog(getShell(), SWT.SAVE | SWT.SINGLE);
        dlg.setText(FILE_DIALOG_TITLE);

        final String someName = dlg.open();
        if (someName != null) {
            this.zipFileCombo.setText(someName);
        }
        validatePage();
    }

    /**
     * @since 4.0
     */
    void handleSourceFileChanged() {
        String someName = this.zipFileCombo.getText();
        if (someName != null && someName.length() > 0) {
            final char lastChr = someName.charAt(someName.length() - 1);
            if (someName.indexOf(FILE_EXTENSION_SEPARATOR) < 0 && lastChr != ':' && lastChr != '\\' && lastChr != '/') {
                someName += FILE_IMPORT_TAG;
            }
            this.targetZipFileName = new File(someName);
        } else {
            this.targetZipFileName = null;
        }
        validatePage();
    }

    /**
     * @since 4.0
     */
    void validatePage() {
        if (viewer.getCheckedElements().length == 0) {
            WizardUtil.setPageComplete(this.page1, NO_SELECTIONS_MESSAGE, IMessageProvider.ERROR);
            projectsSelected = false;
            return;
        }

        projectsSelected = true;
        List projects = getSelectedProjects();
        try {
            for (int i = 0; i < projects.size(); i++) {
                if (DotProjectUtils.getDotProjectCount((IContainer)projects.get(i), true, true) > 1) {
                    WizardUtil.setPageComplete(this.page1, NESTED_PROJECT_MESSAGE, IMessageProvider.ERROR);
                    return;
                }
            }
        } catch (Exception ce) {
            ce.printStackTrace();
            WizardUtil.setPageComplete(this.page1, NESTED_PROJECT_ERROR, IMessageProvider.ERROR);
            return;
        }

        if (this.targetZipFileName == null) {
            WizardUtil.setPageComplete(this.page1, NO_FILE_MESSAGE, IMessageProvider.ERROR);
            return;
        }

        if (this.targetZipFileName.isDirectory() || !FileUtils.isFilenameValid(targetZipFileName.getName())) {
            WizardUtil.setPageComplete(this.page1, INVALID_FILE_MESSAGE, IMessageProvider.ERROR);
            return;
        }

        if (this.targetZipFileName.exists()) {
            WizardUtil.setPageComplete(this.page1,
                                       WidgetUtil.getFileExistsMessage(this.targetZipFileName),
                                       IMessageProvider.WARNING);
            return;
        }

        WizardUtil.setPageComplete(this.page1);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.2
     */
    @Override
    public boolean canFinish() {
        if (projectsSelected && page1.isPageComplete()) return true;

        return false;
    }

    private List getSelectedProjects() {

        Object[] checkedProjects = viewer.getCheckedElements();

        final ArrayList objs = new ArrayList(checkedProjects.length);

        for (int i = 0; i < checkedProjects.length; i++) {
            ModelProject proj = (ModelProject)checkedProjects[i];
            objs.add(proj.getResource());
        }

        if (objs.isEmpty()) return Collections.EMPTY_LIST;

        return objs;
    }

    /**
     * Export the passed resource and recursively export all of its child resources (iff it's a container). Answer a boolean
     * indicating success.
     * 
     * @return boolean
     */
    protected boolean executeExportOperation( ModelerProjectZipOperation op ) {
        op.setCreateLeadupStructure(true);
        op.setUseCompression(true);

        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            String message = e.getTargetException().getMessage();
            if (message == null || message.length() == 0) {
                message = getString("exportProblemsMessage"); //$NON-NLS-1$
            }
            ErrorDialog.openError(getContainer().getShell(), getString("exportProblemsTitle"), //$NON-NLS-1$
                                  message,
                                  op.getStatus());
            Util.log(e.getTargetException());
            return false;
        }

        IStatus status = op.getStatus();
        if (!status.isOK()) {
            ErrorDialog.openError(getContainer().getShell(), getString("exportProblemsTitle"), //$NON-NLS-1$
                                  null,
                                  status);
            Util.log(status);
            return false;
        }

        return true;
    }

    /**
     * Sets the source name of the import to be the supplied path. Adds the name of the path to the list of items in the source
     * combo and selects it.
     * 
     * @param path the path to be added
     */
    protected void setZipFileName( String path ) {

        if (path.length() > 0) {

            String[] currentItems = this.zipFileCombo.getItems();
            int selectionIndex = -1;
            for (int i = 0; i < currentItems.length; i++) {
                if (currentItems[i].equals(path)) selectionIndex = i;
            }
            if (selectionIndex < 0) {
                int oldLength = currentItems.length;
                String[] newItems = new String[oldLength + 1];
                System.arraycopy(currentItems, 0, newItems, 0, oldLength);
                newItems[oldLength] = path;
                this.zipFileCombo.setItems(newItems);
                selectionIndex = oldLength;
            }
            this.zipFileCombo.select(selectionIndex);

        }
    }

    /**
     * Use the dialog store to restore widget values to the values that they held last time this wizard was used to completion
     */
    protected void restoreWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        WidgetUtil.removeMissingResources(settings, STORE_ZIP_FILE_NAMES_ID);
        if (settings != null) {
            String[] sourceNames = settings.getArray(STORE_ZIP_FILE_NAMES_ID);
            if (sourceNames == null) return; // ie.- no values stored, so stop

            // set filenames history
            for (int i = 0; i < sourceNames.length; i++)
                zipFileCombo.add(sourceNames[i]);

            zipFileCombo.select(0);
        }
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that they will persist into the next invocation of
     * this wizard page
     */
    protected void saveWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            // update source names history
            String[] sourceNames = settings.getArray(STORE_ZIP_FILE_NAMES_ID);
            if (sourceNames == null) sourceNames = new String[0];

            sourceNames = getLastTwentyComboNames(sourceNames);
            settings.put(STORE_ZIP_FILE_NAMES_ID, sourceNames);

        }
    }

    private String[] getLastTwentyComboNames( String[] currentArray ) {
        int nValues = currentArray.length;
        // Let's walk through the list and remove duplicates
        Collection nonDuplicates = new ArrayList(nValues);
        String currentZipName = zipFileCombo.getText();

        if (currentZipName == null) return currentArray;

        for (int i = 0; i < nValues; i++) {
            if (!currentArray[i].equals(currentZipName)) nonDuplicates.add(currentArray[i]);
        }
        Object[] newArray = nonDuplicates.toArray();
        int newArrayLength = newArray.length + 1;

        String[] newStrings = new String[newArrayLength];

        newStrings[0] = currentZipName;

        for (int i = 0; i < newArray.length; i++) {
            if (i > 18) break;
            newStrings[i + 1] = (String)newArray[i];
        }

        return newStrings;
    }
}
