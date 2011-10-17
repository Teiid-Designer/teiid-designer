/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.wizards;

import static org.teiid.designer.extension.ui.UiConstants.ImageIds.CHECK_MARK;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.core.extension.DefaultModelObjectExtensionAssistant;
import org.teiid.designer.core.extension.ModelExtensionUtils;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionHeader;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class CurrentModelExtensionDefnsPage extends WizardPage implements InternalUiConstants.Widgets {

    private final ModelExtensionRegistry registry;

    private static final int STATUS_OK = 0;
    private static final int STATUS_NO_LOCATION = 1;
    private static final int STATUS_NO_MODELNAME = 2;

    private ModelResource modelResource; // Current ModelResource selection
    private Text locationText, modelNameText; // Text widgets for ModelName and Location

    private TableViewer tableViewer;
    private MedHeadersEditManager editManager;

    private int selectedMedIndex = -1; // Index of the current Med Selection
    private Button addMedButton, removeMedButton, registerMedButton; // Buttons for adding/removing/registering MED

    protected int currentStatus = STATUS_OK; // Current status of wizard

    /**
     * Constructor for CurrentModelExtensionDefnsPage
     * 
     * @param rsrc the ModelResource initial selection
     */
    public CurrentModelExtensionDefnsPage( ModelResource rsrc ) {
        super("CurrentModelExtensionDefnsPage"); //$NON-NLS-1$
        setTitle(Messages.currentMedsPageTitle);
        setDescription(Messages.currentMedsPageTitle);

        this.registry = (Platform.isRunning() ? ExtensionPlugin.getInstance().getRegistry() : null);

        this.modelResource = rsrc;

        // Edit Manager maintains the Add/Remove State.
        this.editManager = new MedHeadersEditManager(getModelExtensionDefnHeaders(this.modelResource));
    }

    /**
     * Get the ModelExtensionDefinitionHeader list for the ModelResource
     * 
     * @param modelResource the ModelResource
     * @return the list of current ModelExtensionDefinitionHeaders for the supplied ModelResource
     */
    private List<ModelExtensionDefinitionHeader> getModelExtensionDefnHeaders( ModelResource modelResource ) {
        List<ModelExtensionDefinitionHeader> headers = new ArrayList<ModelExtensionDefinitionHeader>();

        try {
            // Get the namespaces which are currently persisted on the model
            Collection<String> supportedNamespaces = ModelExtensionUtils.getSupportedNamespaces(modelResource);

            // Get the associated Headers
            for (String namespace : supportedNamespaces) {
                ModelExtensionDefinitionHeader header = ModelExtensionUtils.getModelExtensionDefinitionHeader(modelResource,
                                                                                                              namespace);
                headers.add(header);
            }
        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        return headers;
    }

    /*
     * Get the current ModelExtensionDefinitionHeader list
     */
    private List<ModelExtensionDefinitionHeader> getCurrentHeaders() {
        return this.editManager.getCurrentHeaders();
    }

    /*
     * Get the list of ModelExtensionDefinitions to add.
     */
    public List<ModelExtensionDefinition> getModelExtensionDefnsToAdd() {
        return this.editManager.getModelExtensionDefnsToAdd();
    }

    /*
     * Get the list of Namespaces to remove.
     */
    public List<String> getNamespacesToRemove() {
        return this.editManager.getNamespacesToRemove();
    }

    /*
     * Get the selected ModelResource
     */
    public ModelResource getSelectedModelResource() {
        return this.modelResource;
    }

    /*
     * Get the ModelExtensionRegistry
     */
    private ModelExtensionRegistry getRegistry() {
        return this.registry;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl( Composite parent ) {
        GridLayout layout = new GridLayout();
        parent.setLayout(layout);
        // -----------------
        Composite topComposite = new Composite(parent, SWT.NULL);
        GridData topCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        topComposite.setLayoutData(topCompositeGridData);
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 3;
        topComposite.setLayout(topLayout);
        GridData gd = null;

        // -----------------------------------------------
        // Label and Text widgets for Model Location
        // -----------------------------------------------
        Label locationLabel = new Label(topComposite, SWT.NULL);
        locationLabel.setText(Messages.currentMedsPageModelLocationLabel);

        locationText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        locationText.setLayoutData(gd);
        locationText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                updateStatus();
            }
        });
        locationText.setEditable(false);

        // -----------------------------------------------
        // Label,Text and Browse Button for Model Name
        // -----------------------------------------------
        Label modelNameLabel = new Label(topComposite, SWT.NULL);
        modelNameLabel.setText(Messages.currentMedsPageModelNameLabel);

        modelNameText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        modelNameText.setLayoutData(gd);
        modelNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                updateStatus();
            }
        });
        modelNameText.setEditable(false);

        Button browseButton = new Button(topComposite, SWT.PUSH);
        GridData buttonGridData = new GridData();
        // buttonGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        browseButton.setLayoutData(buttonGridData);
        browseButton.setText(Messages.currentMedsPageBrowseButton);
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleBrowseModel();
            }
        });

        // -----------------------------------------------
        // Bottom Composite for Table and Buttons
        // -----------------------------------------------
        Composite bottomComposite = new Composite(parent, SWT.NULL);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout bottomLayout = new GridLayout();
        bottomLayout.numColumns = 2;
        bottomComposite.setLayout(bottomLayout);

        // Table and Buttons
        this.tableViewer = createTableViewer(bottomComposite);
        createTableButtonComposite(bottomComposite);

        setControl(parent);

        updateModelDisplay();

        updateStatus();
    }

    /**
     * Update the model name and location display components from the current modelResource selection
     */
    private void updateModelDisplay() {
        this.getControl().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (modelResource == null) {
                    return;
                }

                IContainer parentContainer = (IContainer)modelResource.getParent().getResource();
                if (parentContainer != null && parentContainer.getProject() != null) {
                    final IProject project = parentContainer.getProject();
                    locationText.setText(project.getName() + File.separator + parentContainer.getProjectRelativePath().toString());
                }

                modelNameText.setText(modelResource.getPath().removeFileExtension().lastSegment());
            }

        });
    }

    /**
     * Update the current status of the wizard
     */
    void updateStatus() {
        String location = getModelLocation();
        if (CoreStringUtil.isEmpty(location)) {
            setMessage(Messages.currentMedsPageNoModelLocationMsg, IMessageProvider.ERROR);
            currentStatus = STATUS_NO_LOCATION;
            setPageComplete(false);
            return;
        }

        String modelNameText = getModelName();
        if (modelNameText.length() == 0) {
            currentStatus = STATUS_NO_MODELNAME;
            setMessage(Messages.currentMedsPageNoModelNameMsg, IMessageProvider.ERROR);
            return;
        }

        setMessage(Messages.currentMedsPageDone, IMessageProvider.NONE);
        setPageComplete(true);
    }

    /**
     * handler for model browse button clicked
     */
    void handleBrowseModel() {
        // Open the selection dialog for the target relational model
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(Messages.currentMedsPageBrowseDialogTitle,
                                                                           Messages.currentMedsPageBrowseDialogMsg,
                                                                           false,
                                                                           null,
                                                                           new ModelingResourceFilter(this.extendableModelFilter),
                                                                           null,
                                                                           new ModelExplorerLabelProvider());

        if (resources != null && resources.length == 1 && resources[0] instanceof IFile) {
            IFile selectedFile = (IFile)resources[0];
            ModelResource selectedModel = null;
            locationText.setText(""); //$NON-NLS-1$
            modelNameText.setText(""); //$NON-NLS-1$
            boolean exceptionOccurred = false;
            try {
                selectedModel = ModelUtil.getModelResource(selectedFile, true);
            } catch (Exception ex) {
                ModelerCore.Util.log(ex);
                exceptionOccurred = true;
            }
            if (!exceptionOccurred) {
                this.modelResource = selectedModel;
                updateModelDisplay();
                this.editManager = new MedHeadersEditManager(getModelExtensionDefnHeaders(this.modelResource));
                this.tableViewer.refresh();
            } else {
                this.modelResource = null;
                this.editManager = new MedHeadersEditManager(Collections.EMPTY_LIST);
                this.tableViewer.refresh();
            }
        }
    }

    /** Filter for showing just open projects and their folders and extendable models */
    private ViewerFilter extendableModelFilter = new ViewerFilter() {
        @Override
        public boolean select( Viewer theViewer,
                               Object theParent,
                               Object theElement ) {
            boolean result = false;

            if (theElement instanceof IResource) {
                // If the project is closed, dont show
                boolean projectOpen = ((IResource)theElement).getProject().isOpen();
                if (projectOpen) {
                    // Show open projects and folder structure
                    if (theElement instanceof IContainer) {
                        result = true;
                    } else if (theElement instanceof IFile) {
                        try {
                            ModelResource modelResource = ModelUtil.getModelResource((IFile)theElement, false);
                            if (modelResource != null) {
                                // Check whether the model file is open and dirty
                                ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)theElement, false);
                                boolean isDirty = false;
                                if (editor != null && editor.isDirty()) {
                                    isDirty = true;
                                }
                                // Check whether model is not dirty and is extendable
                                if (!isDirty && isMetamodelExtendable(modelResource) && ModelUtilities.isPhysical(modelResource)) {
                                    result = true;
                                }

                            }
                        } catch (final ModelWorkspaceException theException) {
                            ModelerCore.Util.log(IStatus.ERROR, theException, theException.getMessage());
                        }
                    }
                }
            }

            return result;
        }
    };

    boolean isMetamodelExtendable( ModelResource modelResource ) {
        if (this.registry != null && modelResource != null) {
            String selectedModelURI = ModelIdentifier.getPrimaryMetamodelURI(modelResource);
            return registry.isExtendable(selectedModelURI);
        }
        return false;
    }

    /*
     * Get the ModelLocation string from the locationText widget
     */
    public String getModelLocation() {
        String result = locationText.getText().trim();
        return result;
    }

    /*
     * Get the ModelName string from the modelNameText widget
     */
    public String getModelName() {
        String result = modelNameText.getText().trim();
        return result;
    }

    /*
     * Create a TableViewer for the models current ModelExtensionDefinitions
     */
    private TableViewer createTableViewer( Composite composite ) {
        Group tableGroup = WidgetFactory.createGroup(composite, Messages.currentMedsPageTableLabel);
        tableGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        TableViewer viewer = new TableViewer(tableGroup, (SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER));
        ColumnViewerToolTipSupport.enableFor(viewer);

        // configure table
        Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create columns
        createColumns(viewer, table);

        viewer.setComparator(new ViewerComparator() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public int compare( Viewer viewer,
                                Object medHeader1,
                                Object medHeader2 ) {
                assert medHeader1 instanceof ModelExtensionDefinitionHeader;
                assert medHeader2 instanceof ModelExtensionDefinitionHeader;

                return super.compare(viewer,
                                     ((ModelExtensionDefinitionHeader)medHeader1).getNamespacePrefix(),
                                     ((ModelExtensionDefinitionHeader)medHeader2).getNamespacePrefix());
            }
        });

        viewer.setContentProvider(new IStructuredContentProvider() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( Object inputElement ) {
                return getCurrentHeaders().toArray();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public void inputChanged( Viewer viewer,
                                      Object oldInput,
                                      Object newInput ) {
                // nothing to do
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleMedSelectionChanged();
            }
        });

        // populate the view
        viewer.setInput(this.editManager.getCurrentHeaders());
        WidgetUtil.pack(viewer);

        return viewer;
    }

    private void handleMedSelectionChanged() {
        // Update the selected Med
        int[] selectedMeds = this.tableViewer.getTable().getSelectionIndices();
        if (selectedMeds.length > 0) {
            this.selectedMedIndex = selectedMeds[0];
        } else {
            this.selectedMedIndex = -1;
        }
        // Update the button states
        setButtonStates();
    }

    private void createColumns( final TableViewer viewer,
                                final Table table ) {
        // NOTE: create in the order in ColumnIndexes
        TableViewerColumn column = new TableViewerColumn(viewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.NAMESPACE_PREFIX, ColumnHeaders.NAMESPACE_PREFIX, true);

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        configureColumn(column, ColumnIndexes.REGISTERED, ColumnHeaders.REGISTERED, true);

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        configureColumn(column, ColumnIndexes.VERSION, ColumnHeaders.VERSION, true);

        final TableViewerColumn lastColumn = new TableViewerColumn(viewer, SWT.LEFT);
        configureColumn(lastColumn, ColumnIndexes.DESCRIPTION, ColumnHeaders.DESCRIPTION, true);
    }

    private void configureColumn( TableViewerColumn viewerColumn,
                                  int columnIndex,
                                  String headerText,
                                  boolean resizable ) {
        viewerColumn.setLabelProvider(new MedLabelProvider(columnIndex));

        TableColumn column = viewerColumn.getColumn();
        column.setText(headerText);
        column.setMoveable(false);
        column.setResizable(resizable);
    }

    interface ColumnHeaders {
        String NAMESPACE_PREFIX = Messages.namespacePrefixColumnText;
        String REGISTERED = Messages.registeredColumnText;
        String VERSION = Messages.versionColumnText;
        String DESCRIPTION = Messages.descriptionColumnText;
    }

    interface ColumnIndexes {
        int NAMESPACE_PREFIX = 0;
        int REGISTERED = 1;
        int VERSION = 2;
        int DESCRIPTION = 3;
    }

    class MedLabelProvider extends ColumnLabelProvider {

        private final int columnIndex;

        public MedLabelProvider( int columnIndex ) {
            this.columnIndex = columnIndex;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            if (this.columnIndex == ColumnIndexes.REGISTERED) {
                assert element instanceof ModelExtensionDefinitionHeader;
                ModelExtensionDefinitionHeader medHeader = (ModelExtensionDefinitionHeader)element;
                if (isRegistered(medHeader)) {
                    return Activator.getDefault().getImage(CHECK_MARK);
                }
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            assert element instanceof ModelExtensionDefinitionHeader;
            ModelExtensionDefinitionHeader header = (ModelExtensionDefinitionHeader)element;

            if (this.columnIndex == ColumnIndexes.NAMESPACE_PREFIX) {
                return header.getNamespacePrefix();
            }

            if (this.columnIndex == ColumnIndexes.REGISTERED) {
                return null;
            }

            if (this.columnIndex == ColumnIndexes.VERSION) {
                return Integer.toString(header.getVersion());
            }

            if (this.columnIndex == ColumnIndexes.DESCRIPTION) {
                return header.getDescription();
            }

            // shouldn't happen
            assert false : "Unknown column index of " + this.columnIndex; //$NON-NLS-1$
            return null;
        }

    }

    /*
     * Create the composite which contains the table button controls
     * @param composite the composite which includes the buttonComposite
     * @return the buttonComposite
     */
    private Composite createTableButtonComposite( Composite composite ) {
        Composite buttonComposite = new Composite(composite, SWT.NULL);
        GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 1;
        buttonComposite.setLayout(buttonLayout);

        // Add Med Button
        this.addMedButton = new Button(buttonComposite, SWT.PUSH);
        this.addMedButton.setText(Messages.currentMedsPageAddMedButton);
        this.addMedButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleAddMed();
            }
        });
        this.addMedButton.setEnabled(true);

        // Remove Med Button
        this.removeMedButton = new Button(buttonComposite, SWT.PUSH);
        this.removeMedButton.setText(Messages.currentMedsPageRemoveMedButton);
        this.removeMedButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleRemoveMed();
            }
        });
        this.removeMedButton.setEnabled(false);

        // Remove Med Button
        this.registerMedButton = new Button(buttonComposite, SWT.PUSH);
        this.registerMedButton.setText(Messages.currentMedsPageRegisterMedButton);
        this.registerMedButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleRegisterMed();
            }
        });
        this.registerMedButton.setEnabled(false);

        return buttonComposite;
    }

    /*
     * Set the Add/Remove MED buttons states based on the current table selection
     */
    private void setButtonStates() {
        // Remove Button - only enable if something is selected
        boolean removeEnabled = (this.selectedMedIndex >= 0) ? true : false;
        this.removeMedButton.setEnabled(removeEnabled);

        // Register Button - enabled if an unregistered MED is selected
        if (this.selectedMedIndex >= 0) {
            ModelExtensionDefinitionHeader medHeader = this.editManager.getCurrentHeaders().get(this.selectedMedIndex);
            if (!isRegistered(medHeader)) {
                this.registerMedButton.setEnabled(true);
            } else {
                this.registerMedButton.setEnabled(false);
            }
        } else {
            this.registerMedButton.setEnabled(false);
        }

        // Add Button - always enabled
        this.addMedButton.setEnabled(true);
    }

    /*
     * Determine if the header namespace is registered
     */
    private boolean isRegistered( ModelExtensionDefinitionHeader medHeader ) {
        boolean isRegistered = false;
        if (medHeader != null) {
            String namespacePrefix = medHeader.getNamespacePrefix();
            if (getRegistry().isNamespacePrefixRegistered(namespacePrefix)) {
                isRegistered = true;
            }
        }
        return isRegistered;
    }

    /*
     * handler for Add Med Button
     */
    void handleAddMed() {
        AvailableModelExtensionDefinitionsDialog dialog = new AvailableModelExtensionDefinitionsDialog(getShell(),
                                                                                                       this.modelResource,
                                                                                                       this.editManager.getCurrentHeaders());
        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            // Get the MEDs which were selected to be added from the dialog
            List<ModelExtensionDefinition> dialogMeds = dialog.getSelectedModelExtensionDefinitions();

            this.editManager.addModelExtensionDefinitions(dialogMeds);

            // Update the table
            this.tableViewer.refresh();
        }
    }

    /*
     * handler for Remove Med Button
     */
    void handleRemoveMed() {
        // Warn user that removal will remove all properties
        boolean confirmed = MessageDialog.openConfirm(getShell(),
                                                      Messages.currentMedsPageRemoveDialogTitle,
                                                      Messages.currentMedsPageRemoveDialogMsg);

        // If user confirms, proceed
        if (confirmed) {
            // put selected prefix in the remove list
            String namespacePrefix = this.editManager.getCurrentHeaders().get(this.selectedMedIndex).getNamespacePrefix();
            this.editManager.removeModelExtensionDefinition(namespacePrefix);
            this.tableViewer.refresh();
        }
    }

    /*
     * handler for Register Med Button.  This launches the NewMedWizard, providing the ModelExtensionDefinition which
     * needs to be saved.
     */
    void handleRegisterMed() {
        final IWorkbench workbench = UiPlugin.getDefault().getWorkbench();

        // New ModelExtensionDefinitions Wizard
        NewMedWizard newMedWizard = new NewMedWizard();
        IStructuredSelection structuredSelection = new StructuredSelection(modelResource.getResource());
        newMedWizard.init(workbench, structuredSelection);

        // Get the selected Med Header
        ModelExtensionDefinitionHeader medHeader = this.editManager.getCurrentHeaders().get(this.selectedMedIndex);

        // Create DefaultMedAssistant
        DefaultModelObjectExtensionAssistant defaultAssistant = new DefaultModelObjectExtensionAssistant(medHeader);
        // Get unregistered MED contents from the Model
        ModelExtensionDefinition unregisteredMed = null;
        try {
            unregisteredMed = defaultAssistant.getModelExtensionDefinition(modelResource);
        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        // File schemaFile = null;
        InputStream unregisteredMedStream = null;

        try {
            // schemaFile = ExtensionPlugin.getInstance().getMedSchema();
            ModelExtensionDefinitionWriter medWriter = new ModelExtensionDefinitionWriter();
            unregisteredMedStream = medWriter.write(unregisteredMed);
        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        newMedWizard.setMedInput(unregisteredMedStream);

        // Launch NewMedWizard, prompts for the location to save the med.
        final WizardDialog dialog = new WizardDialog(newMedWizard.getShell(), newMedWizard);
        // After OK, get the saved MED and register it
        if (dialog.open() == Window.OK) {
            IFile createdMed = newMedWizard.getCreatedMedFile();
            if (createdMed != null && createdMed.exists()) {
                try {
                    InputStream fileStream = createdMed.getContents();
                    getRegistry().addDefinition(fileStream, defaultAssistant);
                } catch (Exception e) {
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }
        }

        // Deselect the original med
        this.tableViewer.getTable().deselectAll();
        setButtonStates();

        // Reset manager to update states
        this.editManager = new MedHeadersEditManager(getModelExtensionDefnHeaders(this.modelResource));
        this.tableViewer.refresh();
    }

}
