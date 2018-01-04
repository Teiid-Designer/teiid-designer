/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.teiidimporter.ui.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.util.JndiUtil;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.wizard.CopyDataSourceDialog;
import org.teiid.designer.teiidimporter.ui.wizard.CreateDataSourceDialog;
import org.teiid.designer.teiidimporter.ui.wizard.ITeiidImportServer;
import org.teiid.designer.teiidimporter.ui.wizard.TeiidImportManager;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.LayoutDebugger;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * DataSourcePanel
 * Panel which displays the current DataSources on the TeiidServer.  Includes toolbar which
 * allows the user to add and remove datasources.
 *
 * @since 8.1
 */
public final class DataSourcePanel extends Composite implements UiConstants {

    private final TableViewerBuilder dataSourcesViewer;
    private ITeiidImportServer teiidImportServer;
    private DataSourceManager dataSourceManager;
    private List<DataSourceItem> dataSourceObjList = new ArrayList<DataSourceItem>();
    private int visibleTableRows = 4;
    private final int GROUP_HEIGHT_160 = 150;
    
    private List<DataSourcePanelListener> listeners = new ArrayList<DataSourcePanelListener>();

    private Button newSourceButton;
    private Button deleteSourceButton;
    private Button editSourceButton;
    private Button copySourceButton;
    private Button refreshButton;
    
    /**
     * DataSourcePanel constructor
     * @param parent the parent composite
     * @param visibleTableRows the number of table rows to show
     * @param teiidImportServer the TeiidServer
     */
    public DataSourcePanel( Composite parent, int visibleTableRows, ITeiidImportServer teiidImportServer ) {
        super(parent, SWT.NONE);
        this.teiidImportServer = teiidImportServer;
        this.visibleTableRows = visibleTableRows;
        this.dataSourceManager = new DataSourceManager(this.teiidImportServer);
        
        setLayout(new GridLayout());
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(this);

        GridDataFactory.fillDefaults().grab(true,  true).applyTo(this);
        
        // Create the Buttons panel
        createButtonsPanel(this);
        
        this.dataSourcesViewer = new TableViewerBuilder(this, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
        ColumnViewerToolTipSupport.enableFor(this.dataSourcesViewer.getTableViewer());
        this.dataSourcesViewer.setContentProvider(new IStructuredContentProvider() {
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
                return getDataSources();
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

        // Sorts the table rows by DataSource name
        this.dataSourcesViewer.setComparator(new ViewerComparator() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public int compare( Viewer viewer,
                                Object e1,
                                Object e2 ) {
                DataSourceItem ds1 = (DataSourceItem)e1;
                DataSourceItem ds2 = (DataSourceItem)e2;

                return super.compare(viewer, ds1.getName(), ds2.getName());
            }
        });

        Table table = this.dataSourcesViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create columns
        TableViewerColumn column = dataSourcesViewer.createColumn(SWT.LEFT, 30, 30, true);
        column.getColumn().setText(Messages.dataSourcePanel_nameColText);
        column.setLabelProvider(new DataSourceLabelProvider(0));


        column = dataSourcesViewer.createColumn(SWT.LEFT, 30, 30, true);
        column.getColumn().setText(Messages.dataSourcePanel_jndiNameColText);
        column.setLabelProvider(new DataSourceLabelProvider(1));


        column = dataSourcesViewer.createColumn(SWT.LEFT, 30, 30, true);
        column.getColumn().setText(Messages.dataSourcePanel_typeColText);
        column.setLabelProvider(new DataSourceLabelProvider(2));


        this.dataSourcesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleDataSourceSelected(event);
            }
        });

        refreshDataSourceList();
        this.dataSourcesViewer.setInput(this);
        packTable();
    }
    
    /**
     * Create the buttons panel containing the new, delete and edit buttons
     * @param parent the parent composite
     */
    private void createButtonsPanel(Composite parent) {
        Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        panel.setLayout(new GridLayout(1, false));
        GridData groupGD = new GridData();
        //groupGD.heightHint= GROUP_HEIGHT_160;
        panel.setLayoutData(groupGD);
        
        newSourceButton = new Button(panel, SWT.PUSH);
        newSourceButton.setText(Messages.dataSourcePanel_newButtonText);
        newSourceButton.setToolTipText(Messages.dataSourcePanel_newButtonTooltip);
        newSourceButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        newSourceButton.setEnabled(true);
        newSourceButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleCreateSource();
            }
            
        });
        
        deleteSourceButton = new Button(panel, SWT.PUSH);
        deleteSourceButton.setText(Messages.dataSourcePanel_deleteButtonText);
        deleteSourceButton.setToolTipText(Messages.dataSourcePanel_deleteButtonTooltip);
        deleteSourceButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        deleteSourceButton.setEnabled(false);
        deleteSourceButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleDeleteSource();
            }
            
        });
        
        editSourceButton = new Button(panel, SWT.PUSH);
        editSourceButton.setText(Messages.dataSourcePanel_editButtonText);
        editSourceButton.setToolTipText(Messages.dataSourcePanel_editButtonTooltip);
        editSourceButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        editSourceButton.setEnabled(false);
        editSourceButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleEditSource();
            }
            
        });
        
        copySourceButton = new Button(panel, SWT.PUSH);
        copySourceButton.setText(Messages.dataSourcePanel_copyButtonText);
        copySourceButton.setToolTipText(Messages.dataSourcePanel_copyButtonTooltip);
        copySourceButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        copySourceButton.setEnabled(false);
        copySourceButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleCopySource();
            }
            
        });
        
        refreshButton = new Button(panel, SWT.PUSH);
        refreshButton.setText(Messages.dataSourcePanel_refreshButtonText);
        refreshButton.setToolTipText(Messages.dataSourcePanel_refreshButtonTooltip);
        refreshButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        refreshButton.setEnabled(true);
        refreshButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	refreshDataSourceList();
                refresh();
            }
            
        });
    }

    /**
     * Set the selection in the DataSource table to the specified DataSource name
     * @param dsName the DataSource name
     */
    public void setTableSelection(String dsName) {
        int selIndex = 0;
        TableItem[] items = this.dataSourcesViewer.getTable().getItems();
        if(dsName==null && items.length>0) {
            this.dataSourcesViewer.getTable().select(selIndex);
        } else {
            for(int i=0; i<items.length; i++) {
                String itemName = items[i].getText();
                if(itemName!=null && itemName.trim().equals(dsName)) {
                    selIndex = i;
                    break;
                }
            }
            this.dataSourcesViewer.getTable().select(selIndex);
        }
    }

    /*
     * Handler for creating a new Data Source
     */
    private void handleCreateSource() {
        // Show dialog for creating the DataSource
        CreateDataSourceDialog dialog = new CreateDataSourceDialog(getShell(), teiidImportServer, null);
        
        dialog.open();
        
        
        // If Dialog was OKd, create the DataSource
        if (dialog.getReturnCode() == Window.OK) {
            final String dsName = dialog.getDataSourceName();
            final String dsDriver = dialog.getDataSourceDriverName();
            final Properties dsProperties = dialog.getDataSourceProperties();
            IStatus createStatus = this.dataSourceManager.createDataSource(dsName, dsDriver, dsProperties);
            
            // If create failed, show Error Dialog
            if(!createStatus.isOK()) {
                ErrorDialog.openError(Display.getCurrent().getActiveShell(),Messages.dataSourcePanel_createErrorTitle, createStatus.getMessage(), createStatus); 
            }
            
            // Refresh the table and select the just-deployed template
            refreshDataSourceList();
            this.dataSourcesViewer.refresh();
            packTable();
            setTableSelection(dsName);
            fireSelectionChanged(getSelectedDataSourceName());
        }
    }
    
    /* 
     * Handler for deleting the dataSource
     */
    private void handleDeleteSource() {
        // Confirm Deletion
        if(MessageDialog.openQuestion(getShell(), Messages.dataSourcePanel_deleteSourceDialogTitle, 
                                      Messages.dataSourcePanel_deleteSourceDialogMsg)) {
            final String dsName = getSelectedDataSourceName();

            IStatus deleteStatus = this.dataSourceManager.deleteDataSource(dsName);
            
            // If create failed, show Error Dialog
            if(!deleteStatus.isOK()) {
                ErrorDialog.openError(Display.getCurrent().getActiveShell(),Messages.dataSourcePanel_deleteErrorTitle, deleteStatus.getMessage(), deleteStatus); 
            }

            // Refresh the table and select the just-deployed template
            refreshDataSourceList();
            this.dataSourcesViewer.refresh();
            packTable();
            setTableSelection(null);
            setButtonEnabledStates();
            fireSelectionChanged(getSelectedDataSourceName());
        }
    }
    
    /* 
     * Handler for editing the dataSource
     */
    private void handleEditSource() {
        String dataSourceName = getSelectedDataSourceName();
        // Show dialog for creating the DataSource
        CreateDataSourceDialog dialog = new CreateDataSourceDialog(getShell(), teiidImportServer, dataSourceName);

        dialog.open();
        
        // If Dialog was OKd, update the DataSource
        if (dialog.getReturnCode() == Window.OK) {
            // No need to update if no properties changed
            if(dialog.hasPropertyChanges()) {
                final String sourceName = dialog.getDataSourceName();
                final String dsDriver = dialog.getDataSourceDriverName();
                final Properties dsProps = dialog.getDataSourceProperties();

                IStatus deleteCreateStatus = this.dataSourceManager.deleteAndCreateDataSource(sourceName,sourceName,dsDriver,dsProps);

                // If create failed, show Error Dialog
                if(!deleteCreateStatus.isOK()) {
                    ErrorDialog.openError(Display.getCurrent().getActiveShell(),Messages.dataSourcePanel_editErrorTitle, deleteCreateStatus.getMessage(), deleteCreateStatus); 
                }

                // Refresh the table and select the just-deployed template
                refreshDataSourceList();
                this.dataSourcesViewer.refresh();
                packTable();
                setTableSelection(sourceName);
                setButtonEnabledStates();
                fireSelectionChanged(getSelectedDataSourceName());
            }
        }
    }
   
    /* 
     * Handler for making a copy of the dataSource
     */
    private void handleCopySource() {
        // Show dialog for copying the DataSource
        CopyDataSourceDialog dialog = new CopyDataSourceDialog(getShell(), teiidImportServer);

        dialog.open();
        
        // If Dialog was OKd, create the DataSource
        if (dialog.getReturnCode() == Window.OK) {
            final String dsToCopyName = getSelectedDataSourceName();
            final String dsToCopyDriver = getSelectedDataSourceDriver();
            String newDsName = dialog.getNewDataSourceName();
            
            IStatus copyStatus = this.dataSourceManager.copyDataSource(dsToCopyName, dsToCopyDriver, newDsName);
            
            // If create failed, show Error Dialog
            if(!copyStatus.isOK()) {
                ErrorDialog.openError(Display.getCurrent().getActiveShell(),Messages.dataSourcePanel_copyErrorTitle, copyStatus.getMessage(), copyStatus); 
            }

            // Refresh the table and select the just-deployed template
            refreshDataSourceList();
            this.dataSourcesViewer.refresh();
            packTable();
            setTableSelection(newDsName);
            setButtonEnabledStates();
            fireSelectionChanged(getSelectedDataSourceName());
        }
    }

    /*
     * pack the table
     */
    private void packTable() {
        TableColumn[] cols = this.dataSourcesViewer.getTable().getColumns();
        for(int i=0; i<cols.length; i++) {
            cols[i].pack();
        }
    }
    
    /**
     * Add a listener
     * @param listener the listener
     */
    public void addListener( DataSourcePanelListener listener ) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener
     * @param listener the listener
     */
    public void removeListener( DataSourcePanelListener listener ) {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    /**
     * Fire Selection changed to the listeners
     * @param selectedSourceName the name of the selected dataSource
     */
    public void fireSelectionChanged(String selectedSourceName) {
        for (DataSourcePanelListener listener : this.listeners) {
            listener.selectionChanged(selectedSourceName);
        }
    }

    /**
     * Get DataSourceItem array
     * @return the array of DataSourceItem
     */
    private Object[] getDataSources() {
        return this.dataSourceObjList.toArray();
    }
    
    /*
     * Refresh the current data source list by requesting from the TeiidServer
     */
    private void refreshDataSourceList( ) {
        this.dataSourceObjList.clear();
        
        Collection<ITeiidDataSource> dataSources;
        try {
            dataSources = teiidImportServer.getDataSources();
        } catch (Exception ex) {
            dataSources = new ArrayList<ITeiidDataSource>();
            UTIL.log(ex);
        }
 
        for(ITeiidDataSource dataSource: dataSources) {

            // ------------------------
            // Set PropertyItem fields
            // ------------------------
            // Name
            String dsName = dataSource.getName();
                        
            String sourceJndiName = dataSource.getPropertyValue("jndi-name");  //$NON-NLS-1$
            if( sourceJndiName == null ) {
            	sourceJndiName = JndiUtil.addJavaPrefix(dsName); //$NON-NLS-1$
            }

            // Driver name
            String dsDriver = dataSourceManager.getDriver(dataSource);
            
            DataSourceItem dsObj = new DataSourceItem(dsName, sourceJndiName, dsDriver);
            // ------------------------
            // Add PropertyItem to List
            // ------------------------
            this.dataSourceObjList.add(dsObj);
        }
    }
    
    /**
     * Get the currently selected DataSource Name
     * @return the selected dataSource name
     */
    public String getSelectedDataSourceName() {
        DataSourceItem selectedDS = getSelectedDataSource();
        return (selectedDS==null) ? null : selectedDS.getJndiName();
    }
    
    /**
     * Get the currently selected DataSource JNDI Name
     * @return the selected dataSource JNDI name
     */
    public String getSelectedDataSourceJndiName() {
        DataSourceItem selectedDS = getSelectedDataSource();
        return (selectedDS==null) ? null : selectedDS.getJndiName();
    }

    /**
     * Get the currently selected DataSource driver name
     * @return the selected dataSource driver
     */
    public String getSelectedDataSourceDriver() {
        DataSourceItem selectedDS = getSelectedDataSource();
        return (selectedDS==null) ? null : selectedDS.getDriver();
    }
    
    /*
     * Get the currently selected DataSourceItem, null if nothing is selected
     * @return the selected DataSourceItem
     */
    private DataSourceItem getSelectedDataSource() {
        IStructuredSelection selection = (IStructuredSelection)this.dataSourcesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }
        
        return (DataSourceItem)selection.getFirstElement();
    }


    /**
     * Handler for DataSource selection changed
     * @param event the selection event
     */
    void handleDataSourceSelected( SelectionChangedEvent event ) {
        setButtonEnabledStates();
        
        fireSelectionChanged(getSelectedDataSourceName());
    }
    
    /*
     * Set the button enabled states based on table selection
     */
    private void setButtonEnabledStates() {
        String selectedDS = getSelectedDataSourceName();
        
        if (CoreStringUtil.isEmpty(selectedDS)) {
            if (this.deleteSourceButton.isEnabled()) {
                this.deleteSourceButton.setEnabled(false);
            }
            if (this.editSourceButton.isEnabled()) {
                this.editSourceButton.setEnabled(false);
            }
            if (this.copySourceButton.isEnabled()) {
                this.copySourceButton.setEnabled(false);
            }
        } else {
            if (!this.deleteSourceButton.isEnabled()) {
                this.deleteSourceButton.setEnabled(true);
            }
            if (!this.editSourceButton.isEnabled()) {
                this.editSourceButton.setEnabled(true);
            }
            if (!this.copySourceButton.isEnabled()) {
                this.copySourceButton.setEnabled(true);
            }
        }
    }
    
    @Override
    public void setVisible(boolean visible) {
    	super.setVisible(visible);
    	// Make sure if the import manager
    	String existingName = ((TeiidImportManager)teiidImportServer).getDataSourceJndiName();
    	if( existingName != null ) {
    		setTableSelection(existingName);
    	}
    	}

    /**
     * Public access to refresh the contents of this panel based on external changes to the translator override
     * properties
     */
    public void refresh() {
        this.dataSourcesViewer.setInput(this);
        this.dataSourcesViewer.refresh();
    }

    /**
     * Label provider for DataSource Table
     */
    class DataSourceLabelProvider extends ColumnLabelProvider {

    	public int NAME_COL = 0;
    	public int JNDI_COL = 1;
    	public int DRIVER_COL = 2;
   	
        private int cType;

        public DataSourceLabelProvider( int colType ) {
            this.cType = colType;
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
            DataSourceItem dataSourceItem = (DataSourceItem)element;

            if (this.cType == NAME_COL) {
                return dataSourceItem.getName();
            } else if(this.cType == JNDI_COL) {
            	return dataSourceItem.getJndiName();
            } else {
                return dataSourceItem.getDriver();
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
            DataSourceItem dataSourceItem = (DataSourceItem)element;

            if (this.cType == NAME_COL) {
                return dataSourceItem.getName();
            } else if(this.cType == JNDI_COL) {
            	return dataSourceItem.getJndiName();
            } else {
                return Messages.dataSourcePanel_driverTooltipPrefix+dataSourceItem.getName();
            }
        }
    }
}
