/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.mapping.factory.MappingClassFactory;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelSelectorDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelSelectorInfo;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.internal.xml.factory.VirtualDocumentModelPopulator;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;

/**
 * @since 5.0
 */
public class CreateXmlViewFromXsdMainPage extends WizardDataTransferPage implements ModelerXmlUiConstants {

    private ModelSelectorInfo newModelInfo;
    private List xsdRoots;
    private ModelResource targetXmlStructureModel;

    // widgets
    private Text modelFolderNameField;
    private Button modelFolderBrowseButton;

    // A boolean to indicate if the user has typed anything
    private boolean initializing = false;

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(CreateXmlViewFromXsdMainPage.class);
    private static final String MODEL_SELECTOR_TITLE = getString("modelSelectorTitle"); //$NON-NLS-1$;
    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$;
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private static final String MODEL_NAME = getString("modelName"); //$NON-NLS-1$
    private static final String NEW_MODEL_NAME_LABEL = getString("newModelNameLabel"); //$NON-NLS-1$
    private static final String BROWSE_LABEL = getString("browseLabel"); //$NON-NLS-1$
    private static final String MODEL_LABEL = getString("modelLabel"); //$NON-NLS-1$
    private static final String NO_MODEL_MESSAGE = getString("noModelMessage"); //$NON-NLS-1$;
    private static final String ERROR_BUILDING_DOCUMENTS = getString("errorBuildingDocuments"); //$NON-NLS-1$;

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     Object obj ) {
        return Util.getString(I18N_PREFIX + id, obj);
    }

    /**
     * Creates an instance of this class
     * 
     * @param selection IStructuredSelection
     */
    public CreateXmlViewFromXsdMainPage( List xsdRoots ) {
        super(PAGE_TITLE);
        setTitle(PAGE_TITLE);
        this.xsdRoots = new ArrayList(xsdRoots);

        newModelInfo = new ModelSelectorInfo(MODEL_NAME, ModelType.VIRTUAL_LITERAL, XmlDocumentPackage.eNS_URI,
                                             NEW_MODEL_NAME_LABEL, MODEL_SELECTOR_TITLE);
    }

    /**
     * The <code>WizardResourceImportPage</code> implementation of this <code>WizardDataTransferPage</code> method returns
     * <code>true</code>. Subclasses may override this method.
     */
    @Override
    protected boolean allowNewContainerName() {
        return true;
    }

    /**
     * Handle all events and enablements for widgets in this dialog
     * 
     * @param event Event
     */
    public void handleEvent( Event event ) {
        if (!initializing) {
            boolean validate = false;

            if (event.widget == modelFolderBrowseButton) {
                handleModelFolderBrowseButtonPressed();
                validate = true;
            }

            if (validate) setCompletionStatus();

            updateWidgetEnablements();
        }
    }

    /**
     * Creates a new button with the given id.
     * <p>
     * The <code>Dialog</code> implementation of this framework method creates a standard push button, registers for selection
     * events including button presses and registers default buttons with its shell. The button id is stored as the buttons client
     * data. Note that the parent's layout is assumed to be a GridLayout and the number of columns in this layout is incremented.
     * Subclasses may override.
     * </p>
     * 
     * @param parent the parent composite
     * @param id the id of the button (see <code>IDialogConstants.*_ID</code> constants for standard dialog button ids)
     * @param label the label from the button
     * @param defaultButton <code>true</code> if the button is to be the default button, and <code>false</code> otherwise
     */
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        // increment the number of columns in the button bar
        ((GridLayout)parent.getLayout()).numColumns++;

        Button button = new Button(parent, SWT.PUSH);
        button.setFont(parent.getFont());

        GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
        button.setLayoutData(buttonData);

        button.setData(new Integer(id));
        button.setText(label);

        if (defaultButton) {
            Shell shell = parent.getShell();
            if (shell != null) {
                shell.setDefaultButton(button);
            }
            button.setFocus();
        }
        return button;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite parent ) {
        initializeDialogUnits(parent);

        // container specification group
        Composite containerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        containerGroup.setLayout(layout);
        containerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        containerGroup.setFont(parent.getFont());

        // container label
        Label resourcesLabel = new Label(containerGroup, SWT.NONE);
        resourcesLabel.setText(MODEL_LABEL);
        resourcesLabel.setFont(parent.getFont());

        // container name entry field
        modelFolderNameField = new Text(containerGroup, SWT.SINGLE | SWT.BORDER);
        modelFolderNameField.addListener(SWT.Modify, this);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        modelFolderNameField.setLayoutData(data);
        modelFolderNameField.setFont(parent.getFont());
        modelFolderNameField.setEditable(false);

        // container browse button
        modelFolderBrowseButton = new Button(containerGroup, SWT.PUSH);
        modelFolderBrowseButton.setText(BROWSE_LABEL);
        modelFolderBrowseButton.setLayoutData(new GridData());
        modelFolderBrowseButton.addListener(SWT.Selection, this);
        modelFolderBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(modelFolderBrowseButton);

        restoreWidgetValues();
        updateWidgetEnablements();

        setPageComplete(true);
        setMessage(INITIAL_MESSAGE);
        setControl(containerGroup);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * @see org.eclipse.jface.dialogs.DialogPage#setMessage(java.lang.String)
     * @since 4.2
     */
    @Override
    public void setMessage( String newMessage ) {
        super.setMessage(newMessage);
    }

    private boolean setCompletionStatus() {

        if (targetXmlStructureModel == null) {
            // Model is not defined
            setErrorMessage(NO_MODEL_MESSAGE);
            setPageComplete(false);
            return false;
        } else if (!targetXmlStructureModel.exists()) {
            // Model does not exist
            String msg = getString(I18N_PREFIX, modelFolderNameField.getText());
            setErrorMessage(msg);
            setPageComplete(false);
            return false;
        }

        setErrorMessage(null);
        setMessage(INITIAL_MESSAGE);
        setPageComplete(true);
        return true;
    }

    /**
     * The Finish button was pressed. Try to do the required work now and answer a boolean indicating success. If false is
     * returned then the wizard will not close.
     * 
     * @return boolean
     */
    public boolean finish() {

        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( IProgressMonitor theMonitor ) {
                try {
                    executeBuild(theMonitor);
                } finally {
                    theMonitor.done();
                }
            }
        };

        final boolean startedTxn = ModelerCore.startTxn(false, false, "Create XML View Model", this); //$NON-NLS-1$
        boolean success = false;
        try {
            new ProgressMonitorDialog(getShell()).run(true, true, op);
            success = true;
        } catch (InterruptedException e) {
            success = false;
            return false;
        } catch (InvocationTargetException e) {
            success = false;
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), ERROR_BUILDING_DOCUMENTS, realException.getMessage());
            return false;
        } finally {
            if (startedTxn) {
                if (success) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        saveWidgetValues();

        return true;
    }

    void executeBuild( IProgressMonitor theMonitor ) {
        if (xsdRoots != null && !xsdRoots.isEmpty()) {
            theMonitor.beginTask("Building View Model Documents:  ", 100); //$NON-NLS-1$;

            VirtualDocumentModelPopulator populator = new VirtualDocumentModelPopulator(xsdRoots);

            ModelResource mr = null;
            IContainer schemaModelContainer = null;
            try {
                mr = ModelUtil.getModel(xsdRoots.get(0));

                if (mr != null) {
                    IFile schemaModel = (IFile)mr.getUnderlyingResource();
                    if (schemaModel != null) {
                        schemaModelContainer = schemaModel.getParent();
                    }
                }
            } catch (ModelWorkspaceException theException) {
                Util.log(theException);
            }
            if (schemaModelContainer != null) {
                if (targetXmlStructureModel != null) {
                    populator.buildModel(targetXmlStructureModel,
                                         true,
                                         true,
                                         MappingClassFactory.getDefaultStrategy(),
                                         new SubProgressMonitor(theMonitor, 100));
                }
            }

        }
    }

    /**
     * Use the dialog store to restore widget values to the values that they held last time this wizard was used to completion
     */
    @Override
    protected void restoreWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
        }
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that they will persist into the next invocation of
     * this wizard page
     */
    @Override
    protected void saveWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
        }
    }

    /**
     * Opens a container selection dialog and displays the user's subsequent container resource selection in this page's container
     * name field.
     */
    protected void handleModelFolderBrowseButtonPressed() {

        // ==================================
        // launch Location chooser
        // ==================================

        ModelSelectorDialog mwdDialog = new ModelSelectorDialog(this.getShell(), newModelInfo);
        mwdDialog.addFilter(new ModelWorkspaceViewerFilter(true));
        mwdDialog.setValidator(new XmlViewModelSelectionValidator());
        mwdDialog.setAllowMultiple(false);
        mwdDialog.open();

        if (mwdDialog.getReturnCode() == Window.OK) {
            Object[] selectedObjects = mwdDialog.getResult();

            if (selectedObjects.length > 0 && selectedObjects[0] instanceof IFile) {
                IFile theFile = (IFile)selectedObjects[0];

                if (theFile != null) {
                    try {
                        targetXmlStructureModel = ModelUtil.getModelResource(theFile, false);
                    } catch (ModelWorkspaceException theException) {
                        Util.log(theException);
                    }
                }
                if (targetXmlStructureModel != null) {
                    modelFolderNameField.setText(targetXmlStructureModel.getPath().toOSString());
                }
            }
        }

    }

    /**
     * @return Returns the targetXmlStructureModel.
     * @since 5.0
     */
    public ModelResource getTargetXmlStructureModel() {
        return this.targetXmlStructureModel;
    }
}
