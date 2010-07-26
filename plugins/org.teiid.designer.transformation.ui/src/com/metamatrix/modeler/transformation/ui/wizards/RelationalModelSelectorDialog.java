/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.WrappingLabel;

public class RelationalModelSelectorDialog extends ModelWorkspaceDialog implements UiConstants {
    private Label lblRelationalModelName;
    private Text txtRelationalModelName;
    private IContainer newModelParent;
    private IPath targetRelationalFilePath;
    private ModelResource mrRelationalModel;
    private EObject selectedEObject;
    private SelectionAdapter saCreateCbxAdapter;
    private String newRelationalModelName;
    boolean createNewModel = false;

    private final static String FILE_EXT = ".xmi"; //$NON-NLS-1$
    private final static int MODEL_NAME_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .25);

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RelationalModelSelectorDialog.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_NO_NAME = getString("noName.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_INVALID_NAME = getString("invalidName.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_ALREADY_EXISTS = getString("alreadyExists.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_SAME_NAME_AS_RELATIONAL = getString("sameNameAsModel.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_IS_VALID = getString("isValid.message"); //$NON-NLS-1$
    private static String MODEL_CREATE_INSTRUCTION = getString("modelCreateInstruction.message"); //$NON-NLS-1$  
    private static String EXISTING_MODEL_FOLDER_SELECTED = getString("existingModelFolderLocationSelected.message"); //$NON-NLS-1$
    private final static String NEW_MODEL_LABEL = getString("modelName.text"); //$NON-NLS-1$
    private final static String SELECTED_MODEL_NOT_RELATIONAL_MODEL = getString("selModelNotRelational.message"); //$NON-NLS-1$
    private final static String SELECTED_RESOURCE_NOT_RELATIONAL_MODEL = getString("selResrcNotRelational.message"); //$NON-NLS-1$
    private static final String DESCR_LABEL_TEXT = getString("descriptionLabel.text"); //$NON-NLS-1$
    private static final String XML_EXTENSION_PROJECT = "XMLExtensionsProject"; //$NON-NLS-1$

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     */
    public RelationalModelSelectorDialog( Shell parent ) {
        this(parent, new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for the tree
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public RelationalModelSelectorDialog( Shell parent,
                                          ILabelProvider labelProvider,
                                          ITreeContentProvider contentProvider ) {
        super(parent, TITLE, labelProvider, contentProvider);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        WrappingLabel descriptionLabel = WidgetFactory.createWrappingLabel(parent, SWT.NONE);
        GridData descriptionLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
        descriptionLabelGridData.horizontalIndent = 20;
        descriptionLabel.setLayoutData(descriptionLabelGridData);

        descriptionLabel.setText(DESCR_LABEL_TEXT);

        Composite composite = (Composite)super.createDialogArea(parent);

        // add code here to include new relationalmodel panel:
        createModelCreationComposite(composite);

        return composite;
    }

    /**
     * Create the controls for creating a new relational Model.
     * 
     * @param parent the parent composite
     * @return the created composite
     */
    private Composite createModelCreationComposite( Composite parent ) {

        // Set up Composite
        Composite nameComposite = new Composite(parent, SWT.NONE);
        GridLayout nameCompositeLayout = new GridLayout();
        nameComposite.setLayout(nameCompositeLayout);
        nameCompositeLayout.numColumns = 3;
        nameCompositeLayout.marginWidth = 0;
        GridData nameCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        nameCompositeGridData.horizontalIndent = 20;
        nameComposite.setLayoutData(nameCompositeGridData);

        // Enter Model name label - col 1 RELATIONAL_LABEL
        lblRelationalModelName = new Label(nameComposite, SWT.NONE);
        lblRelationalModelName.setText(NEW_MODEL_LABEL);
        lblRelationalModelName.getAlignment();

        // Model name entry field - col 2
        txtRelationalModelName = WidgetFactory.createTextField(nameComposite, GridData.HORIZONTAL_ALIGN_FILL);
        GridData modelNameTextGridData = new GridData();
        modelNameTextGridData.widthHint = MODEL_NAME_TEXT_WIDTH;
        txtRelationalModelName.setLayoutData(modelNameTextGridData);

        // listener for typing changes
        txtRelationalModelName.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                handleSetRelationalModelName();
            }
        });

        // This initializes the status label
        handleSetRelationalModelName();

        setCreateControlsEnabled(false);

        return nameComposite;
    }

    /**
     * handler for Create Relational Model Button pressed
     */
    void createRelationalModel() {

        mrRelationalModel = constructRelationalModel(newModelParent, newRelationalModelName);

        // Save Relational Model
        try {
            if (mrRelationalModel != null) {
                mrRelationalModel.save(null, false);

            }
        } catch (ModelWorkspaceException mwe) {
            Util.log(mwe);
        }
    }

    @Override
    public Object[] getResult() {

        // if they created a new relational model, return it
        if (mrRelationalModel != null) {
            return new Object[] {mrRelationalModel};
        } else if (selectedEObject != null) {
            return new Object[] {selectedEObject};
        }
        // if they selected an existing relational model, return it
        return super.getResult();
    }

    private SelectionAdapter getOKSelectionListener() {

        // establish the selection adapter for the OK button
        if (saCreateCbxAdapter == null) {
            saCreateCbxAdapter = new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {

                    if (createNewModel) {
                        // create the new Relational Model
                        createRelationalModel();
                    }
                }
            };
        }
        return saCreateCbxAdapter;
    }

    private void updateDialogMessage( String sMessage,
                                      boolean bIsError ) {
        int iStatusCode = IStatus.OK;

        if (bIsError) {
            iStatusCode = IStatus.ERROR;
        }

        IStatus status = new StatusInfo(UiConstants.PLUGIN_ID, iStatusCode, sMessage);

        updateStatus(status);
    }

    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        super.selectionChanged(event);

        // set default: null the selected project var
        newModelParent = null;

        IStructuredSelection sel = (IStructuredSelection)getTreeViewer().getSelection();

        Object oSelection = sel.getFirstElement();

        // assume we are creating a new model
        createNewModel = true;

        // Selection is a Relational Model
        if (isRelationalModelResource(oSelection)) {
            // null the selected project var
            newModelParent = null;
            selectedEObject = null;
            createNewModel = false;
            String instruction = Util.getString("RelationalModelSelectorDialog.useExistingModelInstruction.message", //$NON-NLS-1$ 
                                                ((IResource)oSelection).getName());

            updateDialogMessage(instruction, false);
            setCreateControlsEnabled(false);
            enableOKButton();
            // Selection is a Model, but not a Relational Model
        } else if (isModelResource(oSelection)) {
            newModelParent = null;
            selectedEObject = null;
            setCreateControlsEnabled(false);
            disableOKButton();
            updateDialogMessage(SELECTED_MODEL_NOT_RELATIONAL_MODEL, true);
            // Selection is a Container
        } else if (isContainer(oSelection)) {
            // capture the selected project
            newModelParent = (IContainer)oSelection;
            selectedEObject = null;
            setCreateControlsEnabled(true);

            // this will validate the model name
            handleSetRelationalModelName();
        } else if (isPackageInRelationalModel(oSelection)) {
            selectedEObject = (EObject)oSelection;
            newModelParent = null;
            createNewModel = false;
            setCreateControlsEnabled(false);
            enableOKButton();

            updateDialogMessage(EXISTING_MODEL_FOLDER_SELECTED, false);
            // Resource, not a Relational Model
        } else if (oSelection instanceof IResource) {
            newModelParent = null;
            selectedEObject = null;
            setCreateControlsEnabled(false);
            disableOKButton();

            updateDialogMessage(SELECTED_RESOURCE_NOT_RELATIONAL_MODEL, true);
        }
    }

    /**
     * handler when the relational Model name field changes. This updates the status label under the modelName entry field
     */
    void handleSetRelationalModelName() {

        // get the text from the Text field
        String nameText = txtRelationalModelName.getText();

        // validate it
        if (isValidModelName(nameText)) {
            newRelationalModelName = nameText;

            enableOKButton();
            String message = getModelNameStatus(nameText);
            updateDialogMessage(message, false);
        } else {
            disableOKButton();

            // if the name has zero length, reapply the 'create' message
            if (nameText.trim().length() == 0) {
                if (newModelParent != null) {
                    MODEL_CREATE_INSTRUCTION = Util.getString("RelationalModelSelectorDialog.modelCreateInstruction.message", //$NON-NLS-1$ 
                                                              newModelParent.getName());
                    updateDialogMessage(MODEL_CREATE_INSTRUCTION, true);
                }
            } else {
                // use the message from the character validation
                String message = getModelNameStatus(nameText);
                updateDialogMessage(message, true);
            }
        }
    }

    /**
     * Create a Relational Model with the supplied name, in the desired project
     * 
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    public ModelResource constructRelationalModel( IResource targetRes,
                                                   String sModelName ) {

        String sFileName = getFileName(sModelName);
        IPath relativeModelPath = targetRes.getProjectRelativePath().append(sFileName);
        final IFile modelFile = targetRes.getProject().getFile(relativeModelPath);
        final ModelResource resrc = ModelerCore.create(modelFile);
        try {
            resrc.getModelAnnotation().setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
            resrc.getModelAnnotation().setModelType(ModelType.PHYSICAL_LITERAL);
        } catch (ModelWorkspaceException mwe) {
            mwe.printStackTrace();
        }
        return resrc;
    }

    /**
     * get a string status indicator for the modelName status label, given the modelName
     * 
     * @param modelName the model name to test
     * @return the status of the supplied model name
     */
    private String getModelNameStatus( String sModelName ) {
        // Check for null or zero-length
        if (sModelName == null || sModelName.length() == 0) {
            return MODEL_CREATE_ERROR_NO_NAME;
            // Check for valid model name
        }
        String fileNameMessage = ModelUtilities.validateModelName(sModelName, FILE_EXT);
        if (fileNameMessage != null) {
            return MODEL_CREATE_ERROR_INVALID_NAME;
        }
        // Check if already exists
        String sFileName = getFileName(sModelName);
        IPath modelFullPath = null;
        IPath modelRelativePath = null;

        if (newModelParent != null) {
            modelFullPath = newModelParent.getFullPath().append(sFileName);
            modelRelativePath = newModelParent.getProjectRelativePath().append(sFileName);
        }

        if (newModelParent != null && newModelParent.getProject().exists(modelRelativePath)) {
            return MODEL_CREATE_ERROR_ALREADY_EXISTS;
        }

        if (targetRelationalFilePath != null && targetRelationalFilePath.equals(modelFullPath)) {
            return MODEL_CREATE_ERROR_SAME_NAME_AS_RELATIONAL;
        }

        // success
        return MODEL_CREATE_ERROR_IS_VALID;
    }

    /**
     * test whether the supplied modelName is valid
     * 
     * @param modelName the model name to test
     * @return 'true' if the name is valid, 'false' if not.
     */
    private boolean isValidModelName( String sModelName ) {

        // Check for null or zero-length
        if (sModelName == null || sModelName.length() == 0) {
            return false;
        }
        // Check for valid model name
        String fileNameMessage = ModelUtilities.validateModelName(sModelName, FILE_EXT);
        if (fileNameMessage != null) {
            return false;
        }

        // Check if already exists
        String sFileName = getFileName(sModelName);
        IPath modelFullPath = null;
        IPath modelRelativePath = null;
        if (newModelParent != null) {
            modelFullPath = newModelParent.getFullPath().append(sFileName);
            modelRelativePath = newModelParent.getProjectRelativePath().append(sFileName);
        }

        if (newModelParent != null && newModelParent.getProject().exists(modelRelativePath)) {
            return false;
        }

        // Check if it is the same path as the relational model being generated
        if (targetRelationalFilePath != null && targetRelationalFilePath.equals(modelFullPath)) {
            return false;
        }

        // success
        return true;
    }

    private void setCreateControlsEnabled( boolean b ) {
        txtRelationalModelName.setEnabled(b);
        lblRelationalModelName.setEnabled(b);
    }

    private void disableOKButton() {
        if (getOkButton() != null) {
            getOkButton().removeSelectionListener(getOKSelectionListener());
            getOkButton().setEnabled(false);
        }
    }

    private void enableOKButton() {
        if (getOkButton() != null) {
            getOkButton().addSelectionListener(getOKSelectionListener());
            getOkButton().setEnabled(true);
        }
    }

    private boolean isRelationalModelResource( Object oSelection ) {
        boolean bResult = false;
        if (oSelection instanceof IResource) {
            try {

                IResource resource = (IResource)oSelection;

                if (ModelUtilities.isModelFile(resource)) {
                    ModelResource mr = ModelUtilities.getModelResource((IFile)resource, true);

                    if (mr.getPrimaryMetamodelDescriptor() != null) {
                        if (mr.getPrimaryMetamodelDescriptor().getNamespaceURI().equals(RelationalPackage.eNS_URI) &&
                        	mr.getModelType().equals(ModelType.PHYSICAL_LITERAL)) {
                            bResult = true;
                        }
                    }
                }
            } catch (ModelWorkspaceException mwe) {
                ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());
            }
        }
        return bResult;
    }

    private boolean isModelResource( Object oSelection ) {
        boolean bResult = false;
        if (oSelection instanceof IResource) {
            try {

                IResource resource = (IResource)oSelection;

                if (ModelUtilities.isModelFile(resource)) {
                    ModelResource mr = ModelUtilities.getModelResource((IFile)resource, true);
                    if (mr.getPrimaryMetamodelDescriptor() != null) {
                        bResult = true;
                    }
                }
            } catch (ModelWorkspaceException mwe) {
                ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());
            }
        }
        return bResult;
    }

    private boolean isContainer( Object oSelection ) {
        return (oSelection instanceof IContainer || oSelection instanceof IFolder);
    }

    private boolean isPackageInRelationalModel( Object oSelection ) {
        if (oSelection instanceof EObject) {
            if (oSelection instanceof Schema) return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected TreeViewer createTreeViewer( Composite parent ) {
        TreeViewer result = super.createTreeViewer(parent);

        // add a filter to remove closed projects
        result.addFilter(new ViewerFilter() {
            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                boolean result = true;

                if (element instanceof IProject) {
                    IProject project = (IProject)element;

                    if (!project.isOpen()) {
                        result = false;
                    } else {
                        try {
                            if (!project.hasNature(ModelerCore.NATURE_ID)) {
                                result = false;
                            } else {
                                String name = project.getName();
                                if (name != null && name.equals(XML_EXTENSION_PROJECT)) {
                                    return false;
                                }
                            }
                        } catch (CoreException theException) {
                            ModelerCore.Util.log(theException);
                            result = false;
                        }
                    }
                }

                return result;
            }
        });

        result.expandToLevel(2);
        return result;
    }

    /**
     * get the full file name, given a modelName string
     * 
     * @param modelName the model name
     * @return the full model name, including extension
     */
    private String getFileName( String sModelName ) {
        String sResult = sModelName.trim();

        if (!sResult.endsWith(FILE_EXT)) {
            sResult += FILE_EXT;
        }

        return sResult;
    }

    @Override
    public boolean close() {
        getTreeViewer().removeSelectionChangedListener(this);
        return super.close();
    }
}