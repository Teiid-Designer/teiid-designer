/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionHeader;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.UiConstants;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * AvailableModelExtensionDefinitionsDialog is a dialog that is used to add model extension definitions to a model.
 */
public class AvailableModelExtensionDefinitionsDialog extends SelectionStatusDialog {

    private ModelResource modelResource;
    private ModelExtensionRegistry registry;
    private List<ModelExtensionDefinition> availableMedsList;
    private List<ModelExtensionDefinition> selectedMedsList;
    private TableViewer tableViewer;

    /**
     * Construct an instance of AvailableModelExtensionDefinitionsDialog.
     */
    public AvailableModelExtensionDefinitionsDialog( Shell shell,
                                                     ModelResource resource,
                                                     List<ModelExtensionDefinitionHeader> currentModelHeaders ) {
        super(shell);
        this.setTitle(Messages.availableMedsDialogTitle);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        setStatusLineAboveButtons(true);

        this.registry = (Platform.isRunning() ? ExtensionPlugin.getInstance().getRegistry() : null);
        this.modelResource = resource;
        this.availableMedsList = getAddableMEDs(this.modelResource, currentModelHeaders);
        this.selectedMedsList = new ArrayList();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);

        GridData compositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        composite.setLayoutData(compositeGridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);

        // -----------------------------------------------
        // Message Label
        // -----------------------------------------------
        Label messageLabel = new Label(composite, SWT.NULL);
        messageLabel.setText(Messages.availableMedsDialogMsg);

        // -----------------------------------------------
        // Table
        // -----------------------------------------------
        Group tableGroup = WidgetFactory.createGroup(composite, Messages.availableMedsDialogTableLabel);
        tableGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        this.tableViewer = createTableViewer(tableGroup);

        return composite;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.dialogs.SelectionStatusDialog#create()
     */
    @Override
    public void create() {
        super.create();
        updateDialogMessage(Messages.availableMedsDialogNotDoneMsg, true);
    }

    public List<ModelExtensionDefinition> getSelectedModelExtensionDefinitions() {
        return this.selectedMedsList;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
     */
    @Override
    protected void computeResult() {
        setResult(((IStructuredSelection)this.tableViewer.getSelection()).toList());
    }

    /*
     * Update the dialog status message
     */
    private void updateDialogMessage( String sMessage,
                                      boolean bIsError ) {
        int iStatusCode = IStatus.OK;

        if (bIsError) {
            iStatusCode = IStatus.ERROR;
        }

        IStatus status = new StatusInfo(UiConstants.PLUGIN_ID, iStatusCode, sMessage);

        updateStatus(status);
    }

    /**
     * Get the list of registered ModelExtensionDefinitions that can be added to this ModelResource. If the model already contains
     * a MED it will not be included.
     * 
     * @param modelResource the ModelResource
     * @param currentModelHeaders the Med Headers already applied to the model resource
     * @return the list of valid ModelExtensionDefinitions for the supplied ModelResource
     */
    private List<ModelExtensionDefinition> getAddableMEDs( ModelResource modelResource,
                                                           List<ModelExtensionDefinitionHeader> currentModelHeaders ) {
        List<ModelExtensionDefinition> resultList = new ArrayList<ModelExtensionDefinition>();
        IFile file = (IFile)modelResource.getResource();
        Collection<ModelExtensionDefinition> allDefinitions = this.registry.getAllDefinitions();
        for (ModelExtensionDefinition med : allDefinitions) {
            ModelExtensionAssistant assistant = med.getModelExtensionAssistant();
            if (assistant.supportsMedOperation(ExtensionConstants.MedOperations.ADD_MED_TO_MODEL, file) &&
            !headerListContains(currentModelHeaders, med.getNamespacePrefix(), med.getVersion())) {
                resultList.add(med);
            }
        }
        return resultList;
    }

    /**
     * Determine if the supplied namespacePrefix and version are already contained in the supplied list of ModelExtensionDefinitionHeaders
     * @param currentModelHeaders the list of ModelExtensionDefinitionHeader
     * @param namespacePrefix the namespace prefix
     * @param version the version
     * @return 'true' if there is a ModelExtensionDefinitionHeader in the supplied list with the supplied namespace/version
     */
    private boolean headerListContains( List<ModelExtensionDefinitionHeader> currentModelHeaders,
                                        String namespacePrefix,
                                        int version ) {
        boolean contains = false;
        for (ModelExtensionDefinitionHeader header : currentModelHeaders) {
            if (namespacePrefix.equals(header.getNamespacePrefix()) && version == header.getVersion()) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * Create a TableViewer for the available ModelExtensionDefinitions
     */
    private TableViewer createTableViewer( Composite composite ) {
        TableViewer viewer = new TableViewer(composite, (SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
        ColumnViewerToolTipSupport.enableFor(viewer);

        // configure table
        Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * 5;

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
                                Object med1,
                                Object med2 ) {
                assert med1 instanceof ModelExtensionDefinition;
                assert med2 instanceof ModelExtensionDefinition;

                return super.compare(viewer,
                                     ((ModelExtensionDefinition)med1).getNamespacePrefix(),
                                     ((ModelExtensionDefinition)med1).getNamespacePrefix());
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
                return getAvailableMeds().toArray();
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
        viewer.setInput(this.availableMedsList);
        WidgetUtil.pack(viewer);

        return viewer;
    }

    void handleMedSelectionChanged() {
        this.selectedMedsList.clear();
        // Update the selected Meds List
        int[] selectedMedIndices = this.tableViewer.getTable().getSelectionIndices();
        if (selectedMedIndices.length > 0) {
            for (int i = 0; i < selectedMedIndices.length; i++) {
                this.selectedMedsList.add(this.availableMedsList.get(selectedMedIndices[i]));
            }
            updateDialogMessage(Messages.availableMedsDialogDoneMsg, false);
        } else {
            updateDialogMessage(Messages.availableMedsDialogNotDoneMsg, true);
        }
    }

    private void createColumns( final TableViewer viewer,
                                final Table table ) {
        // NOTE: create in the order in ColumnIndexes
        TableViewerColumn column = new TableViewerColumn(viewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.NAMESPACE_PREFIX, ColumnHeaders.NAMESPACE_PREFIX, true);

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
        String VERSION = Messages.versionColumnText;
        String DESCRIPTION = Messages.descriptionColumnText;
    }

    interface ColumnIndexes {
        int NAMESPACE_PREFIX = 0;
        int VERSION = 1;
        int DESCRIPTION = 2;
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
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            assert element instanceof ModelExtensionDefinition;
            ModelExtensionDefinition med = (ModelExtensionDefinition)element;

            if (this.columnIndex == ColumnIndexes.NAMESPACE_PREFIX) {
                return med.getNamespacePrefix();
            }

            if (this.columnIndex == ColumnIndexes.VERSION) {
                return Integer.toString(med.getVersion());
            }

            if (this.columnIndex == ColumnIndexes.DESCRIPTION) {
                return med.getDescription();
            }

            // shouldn't happen
            assert false : "Unknown column index of " + this.columnIndex; //$NON-NLS-1$
            return null;
        }

    }

    /**
     * Get the current ModelExtensionDefinitionHeader list
     */
    List<ModelExtensionDefinition> getAvailableMeds() {
        return this.availableMedsList;
    }

}
