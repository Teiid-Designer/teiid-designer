/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.relationship.ui.editor;

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
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * @since 4.2
 */
public class RelationshipModelSelectorDialog extends ModelWorkspaceDialog implements UiConstants {

    private Label lblRelationshipsModelName;
    private Text txtRelationshipsModelName;
    private IContainer newModelParent;
    private IPath targetRelationalFilePath;
    private ModelResource mrRelationalModel;
    private SelectionAdapter saCreateCbxAdapter;
    private String sNewRelationshipModelName;
    private final static String FILE_EXT = ".xmi"; //$NON-NLS-1$
    private final static int MODEL_NAME_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .25);

    private static final String TITLE = UiConstants.Util.getString("RelationshipModelSelectorDialog.title"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_NO_NAME = UiConstants.Util.getString("RelationshipModelSelectorDialog.noName.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_INVALID_NAME = UiConstants.Util.getString("RelationshipModelSelectorDialog.invalidName.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_ALREADY_EXISTS = UiConstants.Util.getString("RelationshipModelSelectorDialog.alreadyExists.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_SAME_NAME_AS_RELATIONAL = UiConstants.Util.getString("RelationshipModelSelectorDialog.sameNameAsRelational.message"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_IS_VALID = UiConstants.Util.getString("RelationshipModelSelectorDialog.isValid.message"); //$NON-NLS-1$
    private static String MODEL_CREATE_INSTRUCTION = UiConstants.Util.getString("RelationshipModelSelectorDialog.modelCreateInstruction.message"); //$NON-NLS-1$    
    private static String USE_EXISTING_MODEL_INSTRUCTION = UiConstants.Util.getString("RelationshipModelSelectorDialog.useExistingModelInstruction.message"); //$NON-NLS-1$
    private final static String RELATIONSHIP_LABEL = UiConstants.Util.getString("RelationshipModelSelectorDialog.relationshipsModelName.text"); //$NON-NLS-1$

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     */
    public RelationshipModelSelectorDialog( Shell parent ) {
        this(parent, new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for the tree
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public RelationshipModelSelectorDialog( Shell parent,
                                            ILabelProvider labelProvider,
                                            ITreeContentProvider contentProvider ) {
        super(parent, TITLE, labelProvider, contentProvider);
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

        // Enter Model name label - col 1 RELATIONSHIP_LABEL
        lblRelationshipsModelName = new Label(nameComposite, SWT.NONE);
        lblRelationshipsModelName.setText(RELATIONSHIP_LABEL);
        lblRelationshipsModelName.getAlignment();

        // Model name entry field - col 2
        txtRelationshipsModelName = WidgetFactory.createTextField(nameComposite, GridData.HORIZONTAL_ALIGN_FILL);
        GridData modelNameTextGridData = new GridData();
        modelNameTextGridData.widthHint = MODEL_NAME_TEXT_WIDTH;
        txtRelationshipsModelName.setLayoutData(modelNameTextGridData);

        // listener for typing changes
        txtRelationshipsModelName.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                handleSetRelationshipModelName();
            }
        });

        // This initializes the status label
        handleSetRelationshipModelName();

        setCreateControlsEnabled(false);

        return nameComposite;
    }

    /**
     * handler for Create Relationships Model Button pressed
     */
    void createRelationshipModel() {

        mrRelationalModel = constructRelationshipModel(newModelParent, sNewRelationshipModelName);

        // Save Relationship Model
        try {
            if (mrRelationalModel != null) {
                mrRelationalModel.save(null, false);

            }
        } catch (ModelWorkspaceException mwe) {
            UiConstants.Util.log(mwe);
        }
    }

    @Override
    public Object[] getResult() {

        // if they created a new relational model, return it
        if (mrRelationalModel != null) {
            return new Object[] {mrRelationalModel};
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

                    // create the new Relationship Model
                    createRelationshipModel();
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

        IStatus status = new StatusInfo(PLUGIN_ID, iStatusCode, sMessage);

        updateStatus(status);
    }

    /**
     * handler when the relationships Model name field changes. This updates the status label under the modelName entry field
     */
    void handleSetRelationshipModelName() {

        // get the text from the Text field
        String nameText = txtRelationshipsModelName.getText();

        // validate it
        if (isValidRelationshipsModelName(nameText)) {
            sNewRelationshipModelName = nameText;

            if (getOkButton() != null) {
                getOkButton().addSelectionListener(getOKSelectionListener());

                getOkButton().setEnabled(true);
            }
            String message = getRelationshipsModelNameStatus(nameText);
            updateDialogMessage(message, false);
        } else {
            if (getOkButton() != null) {
                getOkButton().removeSelectionListener(getOKSelectionListener());

                getOkButton().setEnabled(false);
            }

            // if the name has zero length, reapply the 'create' message
            if (nameText.trim().length() == 0) {
                if (newModelParent != null) {
                    MODEL_CREATE_INSTRUCTION = UiConstants.Util.getString("RelationshipModelSelectorDialog.modelCreateInstruction.message", //$NON-NLS-1$ 
                                                                          newModelParent.getName());
                    updateDialogMessage(MODEL_CREATE_INSTRUCTION, true);
                }
            } else {
                // use the message from the character validation
                String message = getRelationshipsModelNameStatus(nameText);
                updateDialogMessage(message, true);
            }
        }
    }

    /**
     * Create a Relationships Model with the supplied name, in the desired project
     * 
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    public ModelResource constructRelationshipModel( IResource targetRes,
                                                     String sModelName ) {

        String sFileName = getFileName(sModelName);
        IPath relativeModelPath = targetRes.getProjectRelativePath().append(sFileName);
        final IFile modelFile = targetRes.getProject().getFile(relativeModelPath);
        final ModelResource resrc = ModelerCore.create(modelFile);
        try {
            resrc.getModelAnnotation().setPrimaryMetamodelUri(RelationshipPackage.eNS_URI);
            resrc.getModelAnnotation().setModelType(ModelType.LOGICAL_LITERAL);
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
    private boolean isValidRelationshipsModelName( String sModelName ) {

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
    private String getRelationshipsModelNameStatus( String sModelName ) {
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

        if (isRelationshipModelResource(oSelection)) {
            // null the selected project var
            newModelParent = null;

            // if user selects a RelationshipModel, disable the 'create' controls
            setCreateControlsEnabled(false);
            USE_EXISTING_MODEL_INSTRUCTION = UiConstants.Util.getString("RelationshipModelSelectorDialog.useExistingModelInstruction.message", //$NON-NLS-1$ 
                                                                        ((IResource)oSelection).getName());

            updateDialogMessage(USE_EXISTING_MODEL_INSTRUCTION, false);
        } else if (isContainer(oSelection)) {
            // capture the selected project
            newModelParent = (IContainer)oSelection;

            setCreateControlsEnabled(true);

            // this will validate the model name
            handleSetRelationshipModelName();
        }
    }

    private void setCreateControlsEnabled( boolean b ) {
        txtRelationshipsModelName.setEnabled(b);
        lblRelationshipsModelName.setEnabled(b);
    }

    private boolean isRelationshipModelResource( Object oSelection ) {
        boolean bResult = false;
        if (oSelection instanceof IResource) {
            try {

                IResource resource = (IResource)oSelection;

                if (ModelUtilities.isModelFile(resource)) {
                    ModelResource mr = ModelUtilities.getModelResource((IFile)resource, true);

                    if (mr.getPrimaryMetamodelDescriptor() != null) {
                        if (mr.getPrimaryMetamodelDescriptor().getNamespaceURI().equals(RelationshipPackage.eNS_URI)) {
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

    private boolean isContainer( Object oSelection ) {
        return (oSelection instanceof IContainer);
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
