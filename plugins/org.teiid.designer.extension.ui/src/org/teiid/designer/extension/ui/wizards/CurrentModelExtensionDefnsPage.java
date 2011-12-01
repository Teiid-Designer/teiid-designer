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
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.ModelExtensionAssistantAggregator;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionHeader;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class CurrentModelExtensionDefnsPage extends WizardPage implements InternalUiConstants.Widgets {

    private final ModelExtensionRegistry registry;

    private static final int STATUS_OK = 0;
    private static final int STATUS_NO_LOCATION = 1;
    private static final int STATUS_NO_MODELNAME = 2;

    ModelResource modelResource; // Current ModelResource selection
    Text locationText; // Text widgets for ModelName and Location

    Text modelNameText;

    private TableViewer tableViewer;
    private MedHeadersEditManager editManager;
    private Collection<ModelExtensionDefinition> modelMeds;

    private int selectedMedIndex = -1; // Index of the current Med Selection
    private Button addMedButton, removeMedButton, saveMedButton, updateMedButton; // Buttons for adding/removing/saving/updating MED

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
        ExtensionPlugin extPlugin = ExtensionPlugin.getInstance();

        try {
            // Get the namespaces which are currently persisted on the model
            ModelExtensionAssistantAggregator aggregator = extPlugin.getModelExtensionAssistantAggregator();
            Collection<String> supportedNamespaces = aggregator.getSupportedNamespacePrefixes(modelResource);
            this.modelMeds = new ArrayList<ModelExtensionDefinition>(supportedNamespaces.size());

            // Get the associated Headers
            for (String namespacePrefix : supportedNamespaces) {
                ModelObjectExtensionAssistant assistant = ExtensionPlugin.getInstance()
                                                                         .createDefaultModelObjectExtensionAssistant(namespacePrefix);
                ModelExtensionDefinition med = assistant.getModelExtensionDefinition(this.modelResource);
                this.modelMeds.add(med);
                headers.add(med.getHeader());
            }
        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        return headers;
    }

    /*
     * Get the current ModelExtensionDefinitionHeader list
     */
    List<ModelExtensionDefinitionHeader> getCurrentHeaders() {
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

    /**
     * @return the namespace prefixes whose MED should be replaced with the same MED from the registry (never <code>null</code>)
     */
    public List<String> getNamespacesToUpdate() {
        return this.editManager.getNamespacesToUpdate();
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
        locationText.setBackground(locationLabel.getBackground());
        locationText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                updateStatus();
            }
        });
        locationText.setEditable(false);

        // -----------------------------------------------
        // Label and Text widgets for Model Name
        // -----------------------------------------------
        Label modelNameLabel = new Label(topComposite, SWT.NULL);
        modelNameLabel.setText(Messages.currentMedsPageModelNameLabel);

        modelNameText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        modelNameText.setLayoutData(gd);
        modelNameText.setBackground(modelNameLabel.getBackground());
        modelNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                updateStatus();
            }
        });
        modelNameText.setEditable(false);

        // -----------------------------------------------
        // Bottom Composite for Table and Buttons
        // -----------------------------------------------
        Composite bottomComposite = new Composite(parent, SWT.NULL);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout bottomLayout = new GridLayout(2, false);
        bottomLayout.horizontalSpacing = 0;
        bottomComposite.setLayout(bottomLayout);

        // Table and Buttons
        this.tableViewer = createTableViewer(bottomComposite);
        createTableButtonComposite(bottomComposite);

        setControl(parent);

        updateModelDisplay();

        updateStatus();

        bottomComposite.setFocus();
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
     * Determine if the selection allows add or remove of any registered MED
     * 
     * @param theFile the selected IFile
     * @return 'true' if the selection is an extendable model
     * @since 7.6
     */
    boolean canExtend( IFile theFile ) {
        boolean result = false;

        // Get all the MEDs currently registered
        Collection<ModelExtensionDefinition> meds = this.registry.getAllDefinitions();

        // If any of the MEDs can be added or removed, action is valid
        for (ModelExtensionDefinition med : meds) {
            ModelExtensionAssistant assistant = med.getModelExtensionAssistant();
            if (assistant.supportsMedOperation(ExtensionConstants.MedOperations.ADD_MED_TO_MODEL, theFile)
                || assistant.supportsMedOperation(ExtensionConstants.MedOperations.DELETE_MED_FROM_MODEL, theFile)) {
                result = true;
                break;
            }
        }

        return result;
    }

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

    void handleMedSelectionChanged() {
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
        TableViewerColumn column = new TableViewerColumn(viewer, SWT.RIGHT);
        configureColumn(column, ColumnIndexes.REGISTERED, ColumnHeaders.REGISTERED, ColumnToolTips.REGISTERED, false);

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        configureColumn(column, ColumnIndexes.DIFFERENT, ColumnHeaders.DIFFERENT, ColumnToolTips.DIFFERENT, false);

        column = new TableViewerColumn(viewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.NAMESPACE_PREFIX, ColumnHeaders.NAMESPACE_PREFIX, ColumnToolTips.NAMESPACE_PREFIX,
                        true);

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        configureColumn(column, ColumnIndexes.VERSION, ColumnHeaders.VERSION, ColumnToolTips.VERSION, true);

        final TableViewerColumn lastColumn = new TableViewerColumn(viewer, SWT.LEFT);
        configureColumn(lastColumn, ColumnIndexes.DESCRIPTION, ColumnHeaders.DESCRIPTION, ColumnToolTips.DESCRIPTION, true);
    }

    private void configureColumn( TableViewerColumn viewerColumn,
                                  int columnIndex,
                                  String headerText,
                                  String headerToolTip,
                                  boolean resizable ) {
        viewerColumn.setLabelProvider(new MedLabelProvider(columnIndex));

        TableColumn column = viewerColumn.getColumn();
        column.setText(headerText);
        column.setToolTipText(headerToolTip);
        column.setMoveable(false);
        column.setResizable(resizable);
    }

    interface ColumnIndexes {
        int REGISTERED = 0;
        int DIFFERENT = 1;
        int NAMESPACE_PREFIX = 2;
        int VERSION = 3;
        int DESCRIPTION = 4;
    }

    interface ColumnHeaders {
        String REGISTERED = Messages.registeredColumnText;
        String DIFFERENT = Messages.medsDifferentColumnText;
        String NAMESPACE_PREFIX = Messages.namespacePrefixColumnText;
        String VERSION = Messages.versionColumnText;
        String DESCRIPTION = Messages.descriptionColumnText;
    }

    interface ColumnToolTips {
        String REGISTERED = Messages.registeredColumnToolTip;
        String DIFFERENT = Messages.medsDifferentColumnToolTip;
        String NAMESPACE_PREFIX = Messages.namespacePrefixColumnToolTip;
        String VERSION = Messages.versionColumnToolTip;
        String DESCRIPTION = Messages.descriptionColumnToolTip;
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
            assert element instanceof ModelExtensionDefinitionHeader;
            ModelExtensionDefinitionHeader medHeader = (ModelExtensionDefinitionHeader)element;

            if (this.columnIndex == ColumnIndexes.REGISTERED) {
                if (isRegistered(medHeader)) {
                    return Activator.getDefault().getImage(CHECK_MARK);
                }
            } else if (this.columnIndex == ColumnIndexes.DIFFERENT) {
                if (isDifferent(medHeader)) {
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

            // these columns just display an image
            if ((this.columnIndex == ColumnIndexes.REGISTERED) || (this.columnIndex == ColumnIndexes.DIFFERENT)) {
                return null;
            }

            if (this.columnIndex == ColumnIndexes.NAMESPACE_PREFIX) {
                return header.getNamespacePrefix();
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
        GridLayout layout = new GridLayout();
        buttonComposite.setLayout(layout);
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginTop = 5;
        buttonComposite.setLayout(layout);
        buttonComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));

        // Add Med Button
        this.addMedButton = new Button(buttonComposite, SWT.PUSH);
        this.addMedButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
        this.addMedButton.setText(Messages.currentMedsPageAddMedButton);
        this.addMedButton.setToolTipText(Messages.currentMedsPageAddMedTooltip);
        this.addMedButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleAddMed();
            }
        });
        this.addMedButton.setEnabled(true);

        // Remove Med Button
        this.removeMedButton = new Button(buttonComposite, SWT.PUSH);
        this.removeMedButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
        this.removeMedButton.setText(Messages.currentMedsPageRemoveMedButton);
        this.removeMedButton.setToolTipText(Messages.currentMedsPageRemoveMedTooltip);
        this.removeMedButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleRemoveMed();
            }
        });
        this.removeMedButton.setEnabled(false);

        // Save Med Button
        this.saveMedButton = new Button(buttonComposite, SWT.PUSH);
        this.saveMedButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
        this.saveMedButton.setText(Messages.currentMedsPageSaveMedButton);
        this.saveMedButton.setToolTipText(Messages.currentMedsPageSaveMedTooltip);
        this.saveMedButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleSaveMed();
            }
        });
        this.saveMedButton.setEnabled(false);

        // Update Med Button
        this.updateMedButton = new Button(buttonComposite, SWT.PUSH);
        this.updateMedButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
        this.updateMedButton.setText(Messages.currentMedsPageUpdateMedButton);
        this.updateMedButton.setToolTipText(Messages.currentMedsPageUpdateMedTooltip);
        this.updateMedButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleUpdateMed();
            }
        });
        this.updateMedButton.setEnabled(false);

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
            if (!isRegistered(medHeader) || isDifferent(medHeader)) {
                this.saveMedButton.setEnabled(true);
                this.updateMedButton.setEnabled(true);
            } else {
                this.saveMedButton.setEnabled(false);
                this.updateMedButton.setEnabled(false);
            }
        } else {
            this.saveMedButton.setEnabled(false);
        }

        // Add Button - always enabled
        this.addMedButton.setEnabled(true);
    }

    ModelExtensionDefinition getModelMed(String namespacePrefix) {
        if (this.modelMeds != null) {
            for (ModelExtensionDefinition med : this.modelMeds) {
                if (med.getNamespacePrefix().equals(namespacePrefix)) {
                    return med;
                }
            }
        }

        return null;
    }

    /**
     * @param medHeader the MED header being checked
     * @return <code>true</code> if this MED is different than the same MED in the registry
     */
    boolean isDifferent( ModelExtensionDefinitionHeader medHeader ) {
        if (isRegistered(medHeader)) {
            String namespacePrefix = medHeader.getNamespacePrefix();
            
            // if not currently stored in model then not different
            ModelExtensionDefinition modelMed = getModelMed(namespacePrefix);

            if (modelMed == null) {
                return false;
            }

            // since we know the namespace is registered we will always have a definition
            if (!getRegistry().getDefinition(namespacePrefix).equals(modelMed)) {
                // only different if user hasn't agreed to already update it
                return !this.editManager.getNamespacesToUpdate().contains(namespacePrefix);
            }
        }

        return false;
    }

    /*
     * Determine if the header namespace is registered
     */
    boolean isRegistered( ModelExtensionDefinitionHeader medHeader ) {
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
     * handler for Save Med Button.  This launches the NewMedWizard, providing the ModelExtensionDefinition which
     * needs to be saved.
     */
    void handleSaveMed() {
        NewMedWizard wizard = new NewMedWizard(Messages.copyMedWizardTitle);
        wizard.init(UiPlugin.getDefault().getCurrentWorkbenchWindow().getWorkbench(), null);
        // Set the selectedMed contents on the wizard
        ModelExtensionDefinitionWriter writer = new ModelExtensionDefinitionWriter();

        // Get the selected Med Header
        ModelExtensionDefinitionHeader medHeader = this.editManager.getCurrentHeaders().get(this.selectedMedIndex);

        // Create DefaultMedAssistant
        ModelObjectExtensionAssistant defaultAssistant = ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant();
        defaultAssistant.createModelExtensionDefinition(medHeader);

        // Get unregistered MED contents from the Model
        ModelExtensionDefinition unregisteredMed = null;
        try {
            unregisteredMed = defaultAssistant.getModelExtensionDefinition(modelResource);
        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        InputStream iStream = writer.writeAsStream(unregisteredMed);
        wizard.setMedInput(iStream);

        // Open wizard dialog
        WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
        wizardDialog.setBlockOnOpen(true);
        wizardDialog.open();

        // Deselect the original med
        this.tableViewer.getTable().deselectAll();
        setButtonStates();

        // Reset manager to update states
        this.editManager = new MedHeadersEditManager(getModelExtensionDefnHeaders(this.modelResource));
        this.tableViewer.refresh();
    }

    void handleUpdateMed() {
        // get confirmation from user
        boolean confirmed = MessageDialog.openConfirm(getShell(),
                                                      Messages.currentMedsPageUpdateMedDialogTitle,
                                                      Messages.currentMedsPageUpdateMedDialogMsg);

        if (confirmed) {
            String namespacePrefix = this.editManager.getCurrentHeaders().get(this.selectedMedIndex).getNamespacePrefix();
            this.editManager.updateModelExtensionDefinition(this.registry.getDefinition(namespacePrefix));
            this.tableViewer.refresh();
        }
    }
}
