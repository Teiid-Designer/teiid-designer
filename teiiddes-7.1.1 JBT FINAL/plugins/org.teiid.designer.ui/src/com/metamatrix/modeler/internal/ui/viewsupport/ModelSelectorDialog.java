/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
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
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * @since 5.0
 */
public class ModelSelectorDialog extends ModelWorkspaceDialog implements UiConstants {

    private Label lbModelName;
    private Text txtModelName;
    private IContainer newModelParent;
    private IPath targetFilePath;
    private ModelResource modelResource;
    private SelectionAdapter checkBoxSelectionAdapter;
    private String newModelName;
    private boolean newModel = false;

    private ModelSelectorInfo newModelInfo;

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelSelectorDialog.class);
    private final static String FILE_EXT = ".xmi"; //$NON-NLS-1$
    private final static int MODEL_NAME_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .25);

    private final static String MODEL_CREATE_ERROR_NO_NAME = getString("noName.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_INVALID_NAME = getString("invalidName.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_ALREADY_EXISTS = getString("alreadyExists.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_SAME_NAME_AS = getString("sameNameAs.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_IS_VALID = getString("isValid.message"); //$NON-NLS-1$
    private final static String MODEL_NAME_LABEL = getString("newModelName.text"); //$NON-NLS-1$

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     Object obj1,
                                     Object obj2 ) {
        return Util.getString(I18N_PREFIX + id, obj1, obj2);
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     */
    public ModelSelectorDialog( Shell parent,
                                ModelSelectorInfo modelInfo ) {
        this(parent, modelInfo, new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for the tree
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public ModelSelectorDialog( Shell parent,
                                ModelSelectorInfo modelInfo,
                                ILabelProvider labelProvider,
                                ITreeContentProvider contentProvider ) {
        super(parent, modelInfo.getTitle(), labelProvider, contentProvider);
        this.newModelInfo = modelInfo;
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite composite = (Composite)super.createDialogArea(parent);

        // add code here to include new relationshipmodel panel:
        createModelCreationComposite(composite);

        // is the tree ready here??
        getTreeViewer().addSelectionChangedListener(this);

        return composite;
    }

    /**
     * Create the controls for creating a new relationships Model.
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

        // Enter Model name label - col 1 Model Name Label
        lbModelName = new Label(nameComposite, SWT.NONE);
        lbModelName.setText(MODEL_NAME_LABEL);
        lbModelName.getAlignment();

        // Model name entry field - col 2
        txtModelName = WidgetFactory.createTextField(nameComposite, GridData.HORIZONTAL_ALIGN_FILL);
        GridData modelNameTextGridData = new GridData();
        modelNameTextGridData.widthHint = MODEL_NAME_TEXT_WIDTH;
        txtModelName.setLayoutData(modelNameTextGridData);

        // listener for typing changes
        txtModelName.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                handleSetModelName();
            }
        });

        // This initializes the status label
        handleSetModelName();

        setCreateControlsEnabled(false);

        return nameComposite;
    }

    /**
     * handler for Create Relationships Model Button pressed
     */
    void createModel() {

        modelResource = constructModel(newModelParent, newModelName);

        // Save Relationship Model
        try {
            if (modelResource != null) {
                modelResource.save(null, false);
                newModel = true;
            }
        } catch (ModelWorkspaceException mwe) {
            Util.log(mwe);
        }
    }

    @Override
    public Object[] getResult() {

        // if they created a new relational model, return it
        if (modelResource != null) {
            IFile theFile = null;

            try {
                theFile = (IFile)modelResource.getUnderlyingResource();
            } catch (ModelWorkspaceException theException) {
                ModelerCore.Util.log(theException);
            }
            if (theFile != null) {
                return new Object[] {theFile};
            }
        }
        // if they selected an existing relational model, return it
        return super.getResult();
    }

    public boolean isNewModel() {
        return this.newModel;
    }

    private SelectionAdapter getOKSelectionListener() {

        // establish the selection adapter for the OK button
        if (checkBoxSelectionAdapter == null) {
            checkBoxSelectionAdapter = new SelectionAdapter() {

                @Override
                public void widgetSelected( final SelectionEvent event ) {

                    // create the new Model
                    boolean requiredStart = ModelerCore.startTxn(false, false, "Get Primary Metamodel URI", this); //$NON-NLS-1$
                    boolean succeeded = false;
                    try {
                        createModel();
                        succeeded = true;
                    } finally {
                        // If we start txn, commit it
                        if (requiredStart) {
                            if (succeeded) {
                                ModelerCore.commitTxn();
                            } else {
                                ModelerCore.rollbackTxn();
                            }
                        }
                    }

                }
            };
        }
        return checkBoxSelectionAdapter;
    }

    private void updateDialogMessage( String sMessage,
                                      boolean bIsError ) {
        int iStatusCode = IStatus.OK;

        if (bIsError) {
            iStatusCode = IStatus.ERROR;
        }

        IStatus status = new StatusInfo(PLUGIN_ID, iStatusCode, sMessage);

        updateStatus(status);
    }

    /**
     * handler when the relationships Model name field changes. This updates the status label under the modelName entry field
     */
    void handleSetModelName() {

        // get the text from the Text field
        String nameText = txtModelName.getText();

        // validate it
        if (isValidModelName(nameText)) {
            newModelName = nameText;

            if (getOkButton() != null) {
                getOkButton().addSelectionListener(getOKSelectionListener());

                getOkButton().setEnabled(true);
            }
            String message = getModelNameStatus(nameText);
            updateDialogMessage(message, false);
        } else {
            if (getOkButton() != null) {
                getOkButton().removeSelectionListener(getOKSelectionListener());

                getOkButton().setEnabled(false);
            }

            // if the name has zero length, reapply the 'create' message
            if (nameText.trim().length() == 0) {
                if (newModelParent != null) {
                    String msg = getString("modelCreateInstruction.message", //$NON-NLS-1$
                                           newModelInfo.getModelTypeDisplayName(),
                                           newModelParent.getName());
                    updateDialogMessage(msg, true);
                }
            } else {
                // use the message from the character validation
                String message = getModelNameStatus(nameText);
                updateDialogMessage(message, true);
            }
        }
    }

    /**
     * Create a Model with the supplied name, in the desired project
     * 
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    public ModelResource constructModel( IResource targetRes,
                                         String sModelName ) {

        String sFileName = getFileName(sModelName);
        IPath relativeModelPath = targetRes.getProjectRelativePath().append(sFileName);
        final IFile modelFile = targetRes.getProject().getFile(relativeModelPath);
        final ModelResource resrc = ModelerCore.create(modelFile);
        try {
            resrc.getModelAnnotation().setPrimaryMetamodelUri(newModelInfo.getModelURI());
            resrc.getModelAnnotation().setModelType(newModelInfo.getModelType());
        } catch (ModelWorkspaceException mwe) {
            mwe.printStackTrace();
        }
        return resrc;
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
        if (targetFilePath != null && targetFilePath.equals(modelFullPath)) {
            return false;
        }

        // success
        return true;
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

        if (targetFilePath != null && targetFilePath.equals(modelFullPath)) {
            return MODEL_CREATE_ERROR_SAME_NAME_AS;
        }

        // success
        return MODEL_CREATE_ERROR_IS_VALID;
    }

    protected void registerControls() {
        getTreeViewer().addSelectionChangedListener(this);
    }

    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        super.selectionChanged(event);

        // set default: null the selected project var
        newModelParent = null;

        // set default: disable the 'create' controls
        setCreateControlsEnabled(false);

        IStructuredSelection sel = (IStructuredSelection)getTreeViewer().getSelection();

        Object oSelection = sel.getFirstElement();

        if (isValidModelResource(oSelection)) {
            // null the selected project var
            newModelParent = null;

            // if user selects a RelationshipModel, disable the 'create' controls
            setCreateControlsEnabled(false);
            String msg = getString("useExistingModelInstruction.message", //$NON-NLS-1$
                                   newModelInfo.getModelTypeDisplayName(),
                                   ((IResource)oSelection).getName());

            updateDialogMessage(msg, false);
        } else if (isContainer(oSelection)) {
            // capture the selected project
            newModelParent = (IContainer)oSelection;

            setCreateControlsEnabled(true);

            // this will validate the model name
            handleSetModelName();
        }
    }

    private void setCreateControlsEnabled( boolean b ) {
        txtModelName.setEnabled(b);
        lbModelName.setEnabled(b);
    }

    private boolean isValidModelResource( Object oSelection ) {
        boolean bResult = false;
        if (oSelection instanceof IResource) {
            try {

                IResource resource = (IResource)oSelection;

                if (ModelUtilities.isModelFile(resource)) {
                    ModelResource mr = ModelUtil.getModelResource((IFile)resource, true);
                    if (mr.getModelType() != null) {
                        if (mr.getModelType().equals(newModelInfo.getModelType())) {
                            if (mr.getPrimaryMetamodelDescriptor() != null) {
                                if (mr.getPrimaryMetamodelDescriptor().getNamespaceURI().equals(newModelInfo.getModelURI())) {
                                    bResult = true;
                                }
                            }
                        }
                    }
                }
            } catch (ModelWorkspaceException mwe) {
                ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());
            }
        }
        return bResult;
    }

    private boolean isContainer( Object oSelection ) {
        return (oSelection instanceof IContainer);
    }

    /*
     * (non-Javadoc)
     * 
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
}
