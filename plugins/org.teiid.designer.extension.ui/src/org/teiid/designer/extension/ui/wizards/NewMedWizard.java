/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.ui.Messages;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * @since 7.6
 */
public final class NewMedWizard extends AbstractWizard
    implements INewWizard, InternalUiConstants.Widgets, CoreStringUtil.Constants {


    private static final int COLUMN_COUNT = 3;

    private static final StringNameValidator nameValidator = new StringNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                                                     StringNameValidator.DEFAULT_MAXIMUM_LENGTH);

    private String medName; // name of MED to create
    private IContainer folderLocation; // location to create the MED

    private WizardPage pg;
    private Text nameText, folderText;
    private Button btnFolderBrowse;
    private ISelectionStatusValidator projectValidator = new ModelProjectSelectionStatusValidator();

    /**
     * @since 7.6
     */
    public NewMedWizard() {
        super(UiPlugin.getDefault(), Messages.newMedWizardTitle, null);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 7.6
     */
    @Override
    public boolean finish() {
        // append MED file extension if needed
        if (!medName.endsWith(ModelerCore.MED_FILE_EXTENSION)) {
            medName += ModelerCore.MED_FILE_EXTENSION;
        }

        // create MED resource
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            @SuppressWarnings("unchecked")
			public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    // Target File
                    final IFile medFile = NewMedWizard.this.folderLocation.getFile(new Path(NewMedWizard.this.medName));

                    ModelExtensionDefinitionParser medParser = new ModelExtensionDefinitionParser();
                    medFile.create(medParser.getModelExtensionDefinitionTemplate(), false, monitor);

                    NewMedWizard.this.folderLocation.refreshLocal(IResource.DEPTH_INFINITE, monitor);

                    // open editor
                    IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
                    IDE.openEditor(page, medFile);

                    // ModelExtensionDefinitionEditor medEditor = getMedEditor(medFile);
                    
                } catch (final Exception err) {
                    throw new InvocationTargetException(err);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            new ProgressMonitorDialog(getShell()).run(false, true, op);
            return true;
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                err = ((InvocationTargetException)err).getTargetException();
            }
            // VdbUiConstants.Util.log(err);
            WidgetUtil.showError(Messages.newMedWizardCreateFileErrorMsg);
            return false;
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 7.6
     */
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {

        if (selection != null && !selection.isEmpty()) {
            this.folderLocation = ModelUtil.getContainer(selection.getFirstElement());
        }

        if (folderLocation != null && !folderInModelProject()) {
            // Create empty page
            this.pg = new WizardPage(NewMedWizard.class.getSimpleName(), Messages.newMedWizardPageTitle, null) {
                public void createControl( final Composite parent ) {
                    setControl(createEmptyPageControl(parent));
                }
            };
            this.pg.setMessage(Messages.newMedWizardNotModelProjMsg, IMessageProvider.ERROR);
        } else {

            // Create and add page
            this.pg = new WizardPage(NewMedWizard.class.getSimpleName(), Messages.newMedWizardPageTitle, null) {
                public void createControl( final Composite parent ) {
                    setControl(createPageControl(parent));
                }
            };
            this.pg.setMessage(Messages.newMedWizardInitialMsg);

            // If current selection not null, set folder to selection if a folder, or to containing folder if not
            if (this.folderLocation != null) {
                if (!projectValidator.validate(new Object[] {this.folderLocation}).isOK()) {
                    this.folderLocation = null;
                }
            } else { // folder == null
                this.pg.setMessage(Messages.newMedWizardSelectFolderMsg, IMessageProvider.ERROR);
            }
        }

        this.pg.setPageComplete(false);
        addPage(pg);
    }

    private boolean folderInModelProject() {
        boolean result = false;

        if (this.folderLocation != null) {
            IProject project = this.folderLocation.getProject();
            try {
                if (project != null && project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) != null) {
                    result = true;
                }
            } catch (CoreException ex) {
                // VdbUiConstants.Util.log(ex);
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 7.6
     */
    @Override
    public boolean canFinish() {
        return super.canFinish();
    }

    Composite createEmptyPageControl( final Composite parent ) {
        return new Composite(parent, SWT.NONE);
    }

    /**
     * @param parent
     * @return composite the page
     * @since 7.6
     */
    @SuppressWarnings("unchecked")
	Composite createPageControl( final Composite parent ) {
        // Create page
        final Composite pg = new Composite(parent, SWT.NONE);
        pg.setLayout(new GridLayout(COLUMN_COUNT, false));

        // -----------------------------------------------------
        // Folder Name - target location to put the new .mxd
        // -----------------------------------------------------
        // Folder Label
        WidgetFactory.createLabel(pg, Messages.newMedWizardFolderLabel);
        // Folder Text widget
        final String name = (this.folderLocation == null ? null : this.folderLocation.getFullPath().makeRelative().toString());
        this.folderText = WidgetFactory.createTextField(pg, GridData.FILL_HORIZONTAL, 1, name, SWT.READ_ONLY);
        this.folderText.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                folderModified();
            }
        });
        // Folder Browse Button
        btnFolderBrowse = WidgetFactory.createButton(pg, BROWSE_BUTTON);
        btnFolderBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                browseFolderButtonSelected();
            }
        });

        // -----------------------------------------------------
        // MED Name
        // -----------------------------------------------------
        // MED Name Label
        WidgetFactory.createLabel(pg, Messages.newMedWizardNameLabel);
        // MED Name text widget
        this.nameText = WidgetFactory.createTextField(pg, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.nameText.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                nameModified();
            }
        });

        // set focus to browse button if no folder selected. otherwise set focus to text field
        if (folderLocation == null) {
            btnFolderBrowse.setFocus();
        } else {
            nameText.setFocus();
        }
        
        return pg;
    }

    /**
     * @since 7.6
     */
    private void browseFolderButtonSelected() {
        this.folderLocation = WidgetUtil.showFolderSelectionDialog(this.folderLocation,
                                                                   new ModelingResourceFilter(),
                                                                   projectValidator);

        if (folderLocation != null) {
            this.folderText.setText(folderLocation.getFullPath().makeRelative().toString());

            if (CoreStringUtil.isEmpty(nameText.getText())) {
                nameText.setFocus();
            }
        }

        validatePage();
    }

    /**
     * @since 7.6
     */
    void folderModified() {
        validatePage();
    }

    /**
     * @since 7.6
     */
    void nameModified() {
        validatePage();
    }

    /**
     * Validation logic for the page
     * 
     * @since 7.6
     */
    private void validatePage() {
        final IContainer folder;
        try {
            folder = WizardUtil.validateFileAndFolder(this.nameText,
                                                      this.folderText,
                                                      this.pg,
                                                      ModelerCore.MED_FILE_EXTENSION,
                                                      false);
            if (this.pg.getMessageType() == IMessageProvider.ERROR) {
                // WizardUtil.validateFileAndFolder can set error message and message type so no need to do further
                // validation if an error was already found
                return;
            }

            IStatus status = projectValidator.validate(new Object[] {folder});
            String proposedName = this.nameText.getText();

            if (!status.isOK()) {
                // only update the message if the vFolder is non-null;
                // if WizardUtil returned null, it already set the status
                // this corrects the case where the wrong message shows for
                // a bad filename.
                if (folder != null) {
                    this.pg.setErrorMessage(status.getMessage());
                    this.pg.setPageComplete(false);
                } // endif
            } else if (!nameValidator.isValidName(proposedName)) {
                this.pg.setErrorMessage(Messages.newMedWizardMedNameErrorMsg);
                this.pg.setPageComplete(false);
            } else {
                this.pg.setErrorMessage(null);
                this.pg.setPageComplete(true);
            }

            if (this.pg.isPageComplete()) {
                this.medName = proposedName;
                this.folderLocation = folder;
            }
        } catch (final CoreException err) {
            // VdbUiConstants.Util.log(err);
            WizardUtil.setPageComplete(this.pg, err.getLocalizedMessage(), IMessageProvider.ERROR);
        }
    }

    // /**
    // * Finds the visible MED Editor for the supplied MED If an editor is NOT open for this med, then null is returned.
    // *
    // * @param vdb
    // * @return the MedEditor
    // */
    // public ModelExtensionDefinitionEditor getMedEditor( final IFile med ) {
    // final IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();
    //
    // if (window != null) {
    // final IWorkbenchPage page = window.getActivePage();
    //
    // if (page != null) {
    // ModelExtensionDefinitionEditor editor = findEditorPart(page, med);
    // if (editor != null) {
    // return editor;
    // }
    // }
    // }
    // return null;
    // }
    //
    // private ModelExtensionDefinitionEditor findEditorPart( final IWorkbenchPage page,
    // IFile vdbFile ) {
    // // look through the open editors and see if there is one available for
    // // this model file.
    // final IEditorReference[] editors = page.getEditorReferences();
    // for (int i = 0; i < editors.length; ++i) {
    //
    // final IEditorPart editor = editors[i].getEditor(false);
    // if (editor instanceof ModelExtensionDefinitionEditor) {
    // final ModelExtensionDefinitionEditor medEditor = (ModelExtensionDefinitionEditor)editor;
    // // final IPath editorVdbPath = medEditor.getVdb().getName();
    // // if (vdbFile.getFullPath().equals(editorVdbPath))
    // // return medEditor;
    //
    // }
    // }
    //
    // return null;
    // }
}
