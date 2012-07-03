/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.ui.wizards.NewModelWizardInput;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.table.CtrlClickListener;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The page will only accept file
 * name without the extension OR with the extension that matches the expected one (xml).
 */

public class NewModelWizardMetamodelPage extends WizardPage
    implements InternalUiConstants.Widgets, UiConstants, IModelerProductContexts {

    protected final static String CHOOSE_A_METAMODEL = UiConstants.Util.getString("NewModelWizard.chooseAMetamodel"); //$NON-NLS-1$
    protected final static String CHOOSE_A_MODEL_TYPE = UiConstants.Util.getString("NewModelWizard.chooseAModelType"); //$NON-NLS-1$
    protected final static String CONTRIBUTOR_LABEL = UiConstants.Util.getString("NewModelWizard.contributors"); //$NON-NLS-1$
    protected final static String DEFAULT_EXTENSION = UiConstants.Util.getString("ModelUtilities.modelFileExtension"); //$NON-NLS-1$

    // jh Defect 21886: These strings must agree with the corresponding metamodel names
    // I put them in the i18n because they'll need to be internationalized.
    protected final static String DEFAULT_CLASS = ModelUtil.MODEL_CLASS_RELATIONAL;
    protected final static String DEFAULT_TYPE = UiConstants.Util.getString("NewModelWizardMetamodelPage.defaultType"); //$NON-NLS-1$

    final static List ORDERED_METAMODELS_LIST;

    static {
        /*
         * jh Defect 21885
         * This array controls the order of the metamodels in the wizard's class combobox.
         * Add to or adjust as needed.
         */

        String[] ORDERED_METAMODELS = new String[] {ModelUtil.MODEL_CLASS_RELATIONAL, ModelUtil.MODEL_CLASS_XML, ModelUtil.MODEL_CLASS_XML_SCHEMA, 
        		ModelUtil.MODEL_CLASS_WEB_SERVICE, ModelUtil.MODEL_CLASS_FUNCTION, ModelUtil.MODEL_CLASS_MODEL_EXTENSION };

        ORDERED_METAMODELS_LIST = Arrays.asList(ORDERED_METAMODELS);
    }

    private static final int STATUS_OK = 0;
    private static final int STATUS_NO_LOCATION = 1;
    private static final int STATUS_NO_FILENAME = 2;
    private static final int STATUS_FILE_EXISTS = 3;
    private static final int STATUS_NO_METAMODEL = 4;
    private static final int STATUS_NO_TYPE = 5;
    private static final int STATUS_BAD_FILENAME = 6;
    private static final int STATUS_CLOSED_PROJECT = 7;
    private static final int STATUS_NO_PROJECT_NATURE = 8;
    private static final int STATUS_DEPRECATED_EXTENSION_METAMODEL = 9;

    /** HashMap of key=String metamodel name, value = MetamodelDescriptor */
    protected HashMap descriptorMap = new HashMap();
    /** HashMap of key=String model type display name, value = ModelType */
    protected HashMap modelTypeMap = new HashMap();

    private Text containerText;
    private Text fileText;
    private IPath filePath;
    private ISelection selection;
    private String panelMessage = null;
    private Combo modelTypesCombo;
    private Combo metamodelCombo;
    private TableViewer contributorTable;
    private CtrlClickListener tableClickListener;
    private int currentStatus = STATUS_OK;
    private String fileNameMessage = null;
    private String fileExtension = DEFAULT_EXTENSION;

    private String initialMetamodelClass;
    private ModelType initialModelType;
    private String initialBuilderType;
    private String initialFileName;
    private boolean metamodelWasFoundAndSelected = false;
    private boolean builderAutoSelected = false;
    
    private Properties designerProperties;

    /**
     * Constructor for NewModelWizardSpecifyModelPage
     * 
     * @param pageName
     */
    public NewModelWizardMetamodelPage( ISelection selection, Properties properties) {
        super("specifyModelPage"); //$NON-NLS-1$
        setTitle(Util.getString("NewModelWizard.title")); //$NON-NLS-1$
        setDescription(Util.getString("NewModelWizard.specifyModelDesc")); //$NON-NLS-1$
        this.selection = selection;
        this.designerProperties = properties;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl( Composite parent ) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        Composite topComposite = new Composite(container, SWT.NULL);
        GridData topCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        topComposite.setLayoutData(topCompositeGridData);
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 3;
        topComposite.setLayout(topLayout);
        GridData gd = null;

        if (!ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            Label locationLabel = new Label(topComposite, SWT.NULL);
            locationLabel.setText(Util.getString("NewModelWizard.location")); //$NON-NLS-1$

            containerText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
            gd = new GridData(GridData.FILL_HORIZONTAL);
            containerText.setLayoutData(gd);
            containerText.addModifyListener(new ModifyListener() {
                public void modifyText( ModifyEvent e ) {
                    updateStatusMessage();
                }
            });
            containerText.setEditable(false);

            Button browseButton = new Button(topComposite, SWT.PUSH);
            GridData buttonGridData = new GridData();
            // buttonGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
            browseButton.setLayoutData(buttonGridData);
            browseButton.setText(Util.getString("NewModelWizard.browse")); //$NON-NLS-1$
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleBrowse();
                }
            });
        }

        Label fileLabel = new Label(topComposite, SWT.NULL);
        fileLabel.setText(Util.getString("NewModelWizard.fileName")); //$NON-NLS-1$

        fileText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);
        fileText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                updateStatusMessage();
            }
        });

        // Label placeholderLabel1 =
        new Label(topComposite, SWT.NULL);

        Label modelLabel = new Label(topComposite, SWT.NULL);
        modelLabel.setText(Util.getString("NewModelWizard.metamodel")); //$NON-NLS-1$

        String[] virtualModelComboChoices = getMetamodelChoices();
        metamodelCombo = new Combo(topComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
        metamodelCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        metamodelCombo.setItems(virtualModelComboChoices);
        metamodelCombo.select((virtualModelComboChoices.length == 2) ? 1 : 0);
        // call the selection listener handler method and put on the UI queue since the listeners
        // don't get events during construction
        metamodelCombo.getDisplay().asyncExec(new Runnable() {
            public void run() {
                metamodelComboSelected();
            }
        });

        metamodelCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                metamodelComboSelected();
            }
        });
        // Label placeholderLabel2 =
        new Label(topComposite, SWT.NULL);

        Label modelTypesLabel = new Label(topComposite, SWT.NULL);
        modelTypesLabel.setText(Util.getString("NewModelWizard.modelType")); //$NON-NLS-1$
        modelTypesCombo = new Combo(topComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
        modelTypesCombo.add(CHOOSE_A_MODEL_TYPE);
        modelTypesCombo.select(0);
        modelTypesCombo.setEnabled(false);
        modelTypesCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        modelTypesCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                modelTypeSelected();
            }
        });

        // Add the contributor subpanel

        Group contributorGroup = new Group(container, SWT.SHADOW_ETCHED_IN);
        GridLayout groupLayout = new GridLayout();
        groupLayout.numColumns = 1;
        contributorGroup.setLayout(groupLayout);
        contributorGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label contributorLabel = new Label(contributorGroup, SWT.NULL);
        contributorLabel.setText(CONTRIBUTOR_LABEL);

        contributorTable = new TableViewer(contributorGroup, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
        contributorTable.getTable().setLayout(new TableLayout());
        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        gd.grabExcessVerticalSpace = true;
        contributorTable.getTable().setLayoutData(gd);
        contributorTable.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                handleTableSelection();
            }
        });
        contributorTable.setLabelProvider(new ContributorTableLabelProvider());
        tableClickListener = new CtrlClickListener(contributorTable);

        initialize();
        updateStatusMessage();
        setControl(container);

        fileText.setFocus();

        // set initial state of the metamodel and model type combos
        container.getDisplay().asyncExec(new Runnable() {
            public void run() {
                setInitialComboStates();
            }
        });
    }

    void metamodelComboSelected() {
        if (this.metamodelCombo.isDisposed()) {
            return;
        }

        if (metamodelCombo.getSelectionIndex() == 0) {
            modelTypesCombo.setEnabled(false);
        } else {
            String type = getMetamodelType();
            MetamodelDescriptor md = (MetamodelDescriptor)descriptorMap.get(type);
            List /*<ModelType>*/typeList = getModelTypeList(md);
            populateModelTypesCombo(typeList);
            fileExtension = md.getFileExtension();
        }
        updateContributorTable();
        updateStatusMessage();
    }

    private void populateModelTypesCombo( List /*<ModelType>*/types ) {
        modelTypesCombo.removeAll();
        modelTypesCombo.add(CHOOSE_A_MODEL_TYPE);
        Iterator it = types.iterator();
        while (it.hasNext()) {
            ModelType modelType = (ModelType)it.next();
            String modelTypeName = modelType.getDisplayName();
            modelTypeMap.put(modelTypeName, modelType);
            modelTypesCombo.add(modelTypeName);
        }
        if (types.size() == 1) {
            modelTypesCombo.select(1);
        } else {
            modelTypesCombo.select(0);
        }
        modelTypesCombo.setEnabled(true);
        modelTypeSelected();
    }

    void modelTypeSelected() {
        updateContributorTable();
        updateStatusMessage();
    }

    public ModelType getSelectedModelType() {
        int selectionIndex = modelTypesCombo.getSelectionIndex();
        ModelType modelType = null;
        if (selectionIndex >= 0) {
            String modelTypeName = modelTypesCombo.getItem(selectionIndex);
            modelType = (ModelType)modelTypeMap.get(modelTypeName);
        }
        return modelType;
    }

    private void updateContributorTable() {
        // clear out the current selection
        if (!builderAutoSelected) {
            contributorTable.setSelection(new StructuredSelection());

            // clear out the table
            Object[] elements = NewModelWizard.getContributorList().toArray();
            contributorTable.remove(elements);
            if (modelTypesCombo.getSelectionIndex() > 0) {
                ModelType selectedModelType = getSelectedModelType();
                boolean isVirtual = ((selectedModelType != null) && selectedModelType.equals(ModelType.VIRTUAL_LITERAL));
                List contributorList = NewModelWizard.getModelBuilders(getMetamodelDescriptor(), isVirtual);
                contributorTable.add(contributorList.toArray());
            }
        }
    }

    void handleTableSelection() {
        updateStatusMessage();
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */
    private void initialize() {
        if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection)selection;
            if (ssel.size() > 1) return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer container;
                if (obj instanceof IContainer) container = (IContainer)obj;
                else container = ((IResource)obj).getParent();
                if (containerText != null) containerText.setText(container.getFullPath().makeRelative().toString());
            }
        }
        if (initialFileName != null) {
            fileText.setText(initialFileName);
        } else {
            fileText.setText(Util.getString("NewModelWizard.defaultFileName")); //$NON-NLS-1$
        }
        updateStatusMessage();
    }

    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     */
    void handleBrowse() {
        final IContainer folder = WidgetUtil.showFolderSelectionDialog((IContainer)getTargetContainer(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && containerText != null) {
            containerText.setText(folder.getFullPath().makeRelative().toString());
            if( this.designerProperties != null ) {
            	DesignerPropertiesUtil.setProjectName(this.designerProperties, folder.getProject().getName());
            }
            IPath folderPath = folder.getProjectRelativePath();
            
            if( folderPath.segmentCount() == 1 && folderPath.toString().equals("sources") ) { //$NON-NLS-1$
            	DesignerPropertiesUtil.setSourcesFolderName(this.designerProperties, "sources"); //$NON-NLS-1$
            } else if( folderPath.segmentCount() == 1 && folderPath.toString().equals("views") ) { //$NON-NLS-1$
                DesignerPropertiesUtil.setViewsFolderName(this.designerProperties, "views"); //$NON-NLS-1$
            }
        }

        updateStatusMessage();
    }

    /**
     * Ensures that controls are set and sends the panel status message.
     * 
     * @return true if the status is okay, false if there is an error.
     */
    void updateStatusMessage() {
        checkStatus();
        switch (currentStatus) {

            case (STATUS_NO_LOCATION):
                updateStatus(Util.getString("NewModelWizard.locationMustBeSpecified")); //$NON-NLS-1$
                break;

            case (STATUS_CLOSED_PROJECT):
                updateStatus(Util.getString("NewModelWizard.projectClosed", getTargetProject().getName())); //$NON-NLS-1$
                break;

            case (STATUS_NO_PROJECT_NATURE):
                updateStatus(Util.getString("NewModelWizard.notModelProject", getTargetProject().getName())); //$NON-NLS-1$
                break;

            case (STATUS_NO_FILENAME):
                updateStatus(Util.getString("NewModelWizard.fileNameMustBeSpecified")); //$NON-NLS-1$
                break;

            case (STATUS_BAD_FILENAME):
                updateStatus(Util.getString("NewModelWizard.illegalFileName") + ' ' + fileNameMessage); //$NON-NLS-1$
                break;

            case (STATUS_FILE_EXISTS):
                final String fileName = getFileName();
                final String container = getContainerName();
                filePath = new Path(container).append(fileName);
                updateStatus(Util.getString("NewModelWizard.fileAlreadyExistsMessage", filePath.toOSString())); //$NON-NLS-1$
                break;

            case (STATUS_NO_METAMODEL):
                updateStatus(Util.getString("NewModelWizard.mustSelectMetamodel")); //$NON-NLS-1$
                break;

            case (STATUS_NO_TYPE):
                updateStatus(Util.getString("NewModelWizard.mustSelectModelType")); //$NON-NLS-1$
                break;
            
            case (STATUS_DEPRECATED_EXTENSION_METAMODEL):
            	updateStatus(panelMessage);
            	setMessage(Util.getString("NewModelWizard.extensionMetamodelDeprecated"), IStatus.WARNING); //$NON-NLS-1$
            	break;

            case (STATUS_OK):
            default:
                updateStatus(panelMessage);
                break;
        }
    }

    private boolean checkStatus() {
        String container = getContainerName();
        if (CoreStringUtil.isEmpty(container)) {
            currentStatus = STATUS_NO_LOCATION;
            return false;
        }
        IProject project = getTargetProject();
        if (project == null) {
            currentStatus = STATUS_NO_LOCATION;
            return false;
        } else if (!project.isOpen()) {
            currentStatus = STATUS_CLOSED_PROJECT;
            return false;
        } else {
            try {
                if (project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) == null) {
                    currentStatus = STATUS_NO_PROJECT_NATURE;
                    return false;
                }
            } catch (CoreException ex) {
                currentStatus = STATUS_NO_PROJECT_NATURE;
                return false;
            }
        }

        String fileText = getFileText();
        if (fileText.length() == 0) {
            currentStatus = STATUS_NO_FILENAME;
            return false;
        }
        fileNameMessage = ModelUtilities.validateModelName(fileText, fileExtension);
        if (fileNameMessage != null) {
            currentStatus = STATUS_BAD_FILENAME;
            return false;
        }
        String fileName = getFileName();
        filePath = new Path(container).append(fileName);
        if (ResourcesPlugin.getWorkspace().getRoot().exists(filePath)) {
            currentStatus = STATUS_FILE_EXISTS;
            return false;
        }
        if (metamodelCombo.getSelectionIndex() < 1) {
            currentStatus = STATUS_NO_METAMODEL;
            return false;
        }
        if (modelTypesCombo.getSelectionIndex() < 1) {
            currentStatus = STATUS_NO_TYPE;
            return false;
        }
        
        if( metamodelCombo.getSelectionIndex() == 6) {
        	// WARN USER OF EXTENSION MODEL DEPRECATION
        	currentStatus = STATUS_DEPRECATED_EXTENSION_METAMODEL;
        	return true;
        }
        currentStatus = STATUS_OK;
        return true;
    }

    private void updateStatus( String message ) {
        setErrorMessage(message);
        boolean complete = ((message == null) && (getSelectedBuilder() == null));
        setPageComplete(complete);
    }

    /**
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     * @since 5.0
     */
    @Override
    public void setVisible( boolean theVisibleFlag ) {
        super.setVisible(theVisibleFlag);

        if (isCurrentPage() && theVisibleFlag) {
            updateStatusMessage();
        }
    }

    private String getHiddenProjectPath() {
        String result = null;
        IProject hiddenProj = ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject(false);

        if (hiddenProj != null) {
            result = hiddenProj.getFullPath().makeRelative().toString();
        }

        return result;
    }

    public String getContainerName() {
        String result = null;

        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            result = getHiddenProjectPath();
        } else {
            result = containerText.getText().trim();
        }

        return result;
    }

    public String getFileName() {
        String result = fileText.getText().trim();
        if (!result.endsWith(fileExtension)) {
            result += fileExtension;
        }
        return result;
    }

    public String getFileText() {
        return fileText.getText().trim();
    }

    public IPath getFilePath() {
        return this.filePath;
    }

    public boolean isVirtualSelected() {
        boolean isVirtual = false;
        if (metamodelCombo.getSelectionIndex() > 0) {
            ModelType selectedModelType = getSelectedModelType();
            isVirtual = ((selectedModelType != null) && selectedModelType.equals(ModelType.VIRTUAL_LITERAL));
        }
        return isVirtual;
    }

    public IResource getTargetContainer() {
        IResource result = null;
        String containerName = getContainerName();

        if (!CoreStringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(containerName));

            if (resource.exists()) {
                result = resource;
            }
        }

        return result;
    }

    public IProject getTargetProject() {
        IProject result = null;
        String containerName = getContainerName();

        if (!CoreStringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(containerName));

            if (resource.exists()) {
                result = resource.getProject();
            }
        }

        return result;
    }

    /**
     * Get the metamodel type. Possible returns values are entirely data-driven by the metadata, so no constants exist, just
     * returning a String.
     * 
     * @return the metamodel type
     */
    public String getMetamodelType() {
        String metamodelType = metamodelCombo.getText();
        if (metamodelType.equals(CHOOSE_A_METAMODEL)) {
            metamodelType = null;
        }
        return metamodelType;
    }

    /**
     * Get the MetamodelDescriptor chosen by the user.
     * 
     * @return the metamodel descriptor
     */
    public MetamodelDescriptor getMetamodelDescriptor() {
        MetamodelDescriptor result = null;
        String metamodelName = getMetamodelType();
        if (metamodelName != null) {
            result = (MetamodelDescriptor)descriptorMap.get(metamodelName);
        }
        return result;
    }

    /**
     * Return the NewModelWizardDescriptor selected by the user, or null if one is not selected.
     * 
     * @return
     */
    public NewModelWizardDescriptor getSelectedBuilder() {
        ISelection selection = contributorTable.getSelection();
        NewModelWizardDescriptor builder = (NewModelWizardDescriptor)SelectionUtilities.getSelectedObject(selection);
        return builder;
    }

    protected String[] getMetamodelChoices() {
        // this method is called during contruction
        Collection /*<MetamodelDescriptor>*/mmdescs = Arrays.asList(ModelerCore.getMetamodelRegistry().getMetamodelDescriptors());
        List /*<String>*/choicesList = new ArrayList(mmdescs.size());

        Iterator it = mmdescs.iterator();
        while (it.hasNext()) {
            MetamodelDescriptor mmd = (MetamodelDescriptor)it.next();

            if (UiPlugin.getDefault().isProductContextValueSupported(Metamodel.URI, mmd.getNamespaceURI())
                && mmd.supportsNewModel()) {
                List typeList = new ArrayList(Arrays.asList(mmd.getAllowableModelTypes()));
                typeList.remove(ModelType.METAMODEL_LITERAL);

                if (!typeList.isEmpty()) {
                    choicesList.add(mmd.getDisplayName());
                    descriptorMap.put(mmd.getDisplayName(), mmd);
                }
            }
        }

        TreeSet choiceSet = new TreeSet(choicesList);

        String[] choices = new String[choiceSet.size() + 1];
        choices[0] = CHOOSE_A_METAMODEL;

        int ix = 0; // we'll pre-increment to start after the 'choose' entry

        // jh Defect 21886: Set the order of metamodes in the combobox.
        // To do this we process the metamodel names in the order we wish
        // them to appear in the comobobox.
        Iterator itMetamodels = ORDERED_METAMODELS_LIST.iterator();

        while (itMetamodels.hasNext()) {
            String sMetamodelName = (String)itMetamodels.next();
            if (choiceSet.contains(sMetamodelName)) {

                choices[++ix] = sMetamodelName;
                choiceSet.remove(sMetamodelName);
            }
        }

        // -- Now add any that remain
        it = choiceSet.iterator();
        while (it.hasNext()) {
            String sEntryName = (String)it.next();

            choices[++ix] = sEntryName;
        }

        return choices;
    }

    protected List getModelTypeList( MetamodelDescriptor md ) {
        List typeList = new ArrayList(Arrays.asList(md.getAllowableModelTypes()));

        return typeList;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        return (currentStatus == STATUS_OK && (this.getSelectedBuilder() != null) && (!CoreStringUtil.isEmpty(getContainerName())) && (getFileName().length() > 0));
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     */
    @Override
    public void dispose() {
        if (tableClickListener != null) {
            tableClickListener.dispose();
        }
        super.dispose();
    }

    void setInitialComboStates() {

        /*
         * jh Defect 21885: if metamodel class not set, default to 'Relational'
         *                  This default will be used in Enterprise, while the
         *                  class and type will normally be set in Dimension. 
         */

        String sMetamodelClass = DEFAULT_CLASS;

        // if we have been supplied with a particular initial class, use that one
        if (initialMetamodelClass != null) {
            sMetamodelClass = initialMetamodelClass;
        }

        // walk through the metamodel classes and select the matching type
        String[] classItems = metamodelCombo.getItems();
        for (int i = 0; i < classItems.length; i++) {
            if (classItems[i].equalsIgnoreCase(sMetamodelClass)) {
                metamodelCombo.select(i);
                metamodelComboSelected();
                metamodelWasFoundAndSelected = true;
                break;
            }
        }

        // jh Defect 21885: if metamodel type not set, default to 'View Source'
        String sMetamodelType = DEFAULT_TYPE;

        // if we have been supplied with a particular initial type, use that one
        if (initialModelType != null && initialModelType.getDisplayName() != null) {
            sMetamodelType = initialModelType.getDisplayName();
        }

        boolean foundType = false;
        if (metamodelWasFoundAndSelected && sMetamodelType != null) {

            // walk through the types and select the matching type
            String[] typeItems = modelTypesCombo.getItems();
            for (int i = 0; i < typeItems.length; i++) {
                if (typeItems[i].equalsIgnoreCase(sMetamodelType)) {
                    modelTypesCombo.select(i);
                    modelTypeSelected();
                    foundType = true;
                    break;
                }
            }
        }

        if (initialBuilderType != null && metamodelWasFoundAndSelected && foundType) {

            TableItem[] items = contributorTable.getTable().getItems();
            String label = null;
            for (int i = 0; i < items.length; i++) {
                label = items[i].getText();
                if (label != null && label.equalsIgnoreCase(initialBuilderType)) {
                    builderAutoSelected = true;
                    contributorTable.setSelection(new StructuredSelection(items[i].getData()));
                    handleTableSelection();
                    break;
                }
            }
        }

        if (initialMetamodelClass != null) {
            boolean hiddenProject = ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric();
            String msgId = (hiddenProject ? "NewModelWizard.hiddenProjectSpecifyMsg" //$NON-NLS-1$
            : "NewModelWizard.specifyOnlylModelNameDesc"); //$NON-NLS-1$
            setDescription(Util.getStringOrKey(msgId));
            setTitle(Util.getString("NewModelWizard.singleModelTypeTitle", initialMetamodelClass)); //$NON-NLS-1$
        }
        
    	// Check designer properties
    	if( this.designerProperties != null ) {
        	IContainer container = DesignerPropertiesUtil.getProject(designerProperties);
        	if( initialModelType != null && initialModelType.getLiteral().equalsIgnoreCase(ModelType.PHYSICAL_LITERAL.toString())) {
        		IContainer srcsFolder = DesignerPropertiesUtil.getSourcesFolder(this.designerProperties);
        		if( srcsFolder != null ) {
        			container = srcsFolder;
        		}
        	} else if( initialModelType != null && initialModelType.getLiteral().equalsIgnoreCase(ModelType.PHYSICAL_LITERAL.toString())) {
        		IContainer viewsFolder = DesignerPropertiesUtil.getViewsFolder(this.designerProperties);
        		if( viewsFolder != null ) {
        			container = viewsFolder;
        		}
        	}
        	if( container != null ) {
        		if (containerText != null) {
        			containerText.setText(container.getFullPath().makeRelative().toString());
        		}
        	}
    	}
    }

    public void setNewModelInput( NewModelWizardInput newModelInput ) {
        initialMetamodelClass = newModelInput.getMetamodelClass();
        initialModelType = newModelInput.getModelType();
        initialBuilderType = newModelInput.getBuilderType();
        if (newModelInput.getModelName() != null) {
            initialFileName = newModelInput.getModelName();
        }
    }
}

class ContributorTableLabelProvider extends LabelProvider {

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( Object element ) {
        return ((NewModelWizardDescriptor)element).getIcon();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( Object element ) {
        return element.toString();
    }
}
