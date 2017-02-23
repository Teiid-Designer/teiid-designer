/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.panels;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.teiid.designer.datasources.ui.Messages;
import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.wizard.ITeiidImportServer;
import org.teiid.designer.ui.common.util.LayoutDebugger;
import org.teiid.designer.ui.common.util.WidgetUtil;


/**
 * DataSourceDriversPanel
 * This panel displays the current data source drivers (templates) that are available on the TeiidServer.
 * It allows the user to add new dataSource drivers by choosing a jar or rar from the file system.
 *
 * @since 8.1
 */
public final class DataSourceDriversPanel extends Composite implements UiConstants {

    private static final String BLANK = " ";  //$NON-NLS-1$
    
    private String selectedDriver;
    private final TableViewer driversViewer;
    private ITeiidImportServer teiidImportServer;
    private List<String> driverList = new ArrayList<String>();
    private int visibleTableRows = 4;
    
    private List<DataSourcePanelListener> listeners = new ArrayList<DataSourcePanelListener>();

    /**
     * DataSourceDriversPanel Constructor
     * @param parent the parent composite
     * @param visibleTableRows the number of rows of the table to show
     * @param teiidImportServer the TeiidServer to communication with
     */
    public DataSourceDriversPanel( Composite parent, int visibleTableRows, ITeiidImportServer teiidImportServer ) {
        super(parent, SWT.NONE);
        this.teiidImportServer = teiidImportServer;
        this.visibleTableRows = visibleTableRows;
        setLayout(new GridLayout());
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

//        Composite outerPanel = new Composite(parent, SWT.NONE);
        this.setLayout(new GridLayout(1, false));
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        createAddHyperlink(this);
        
        this.driversViewer = new TableViewer(this, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
        
        ColumnViewerToolTipSupport.enableFor(this.driversViewer);
        this.driversViewer.setContentProvider(new IStructuredContentProvider() {
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
                return getTemplates();
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
        // sort the table rows by display name
        this.driversViewer.setComparator(new ViewerComparator() {
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
                String name1 = (String)e1;
                String name2 = (String)e2;

                return super.compare(viewer, name1, name2);
            }
        });

        Table table = this.driversViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * this.visibleTableRows;
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.driversViewer, SWT.LEFT);
        column.getColumn().setText(Messages.dataSourceDriversPanel_colText);
        column.setLabelProvider(new DriverLabelProvider());


        this.driversViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleDriverSelected(event);
            }
        });

        refreshDriverList();
        this.driversViewer.setInput(this);
        packTable();
        //setTableSelection(null);
    }
    
    /**
     * Create a hyperlink for Adding a new Driver from the file system
     * @param parent the parent composite
     */
    private void createAddHyperlink(Composite parent) {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        Hyperlink addDriverLink = toolkit.createHyperlink(parent, Messages.dataSourceDriversPanelAddHyperlinkTxt, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(addDriverLink);
        addDriverLink.setBackground(parent.getBackground());
        addDriverLink.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                handleAddDriver();
            }
        });
     }
        
    
    /**
     * Set the selection in the Template table to the specified template
     * @param templateName
     */
    private void setTableSelection(String templateName) {
        selectedDriver = templateName;
        int selIndex = 0;
        TableItem[] items = this.driversViewer.getTable().getItems();
        if(templateName==null && items.length>0) {
            this.driversViewer.getTable().select(selIndex);
        } else {
            for(int i=0; i<items.length; i++) {
                String itemName = items[i].getText();
                if(itemName!=null && itemName.trim().equals(templateName)) {
                    selIndex = i;
                    break;
                }
            }
            this.driversViewer.getTable().select(selIndex);
        }
    }

    /**
     * @param listener the panel listener to add
     */
    public void addListener( DataSourcePanelListener listener ) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    /**
     * @param listener the panel listener to remove
     */
    public void removeListener( DataSourcePanelListener listener ) {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    /**
     * Fire a selection changed event to the listeners
     * @param selectedSourceName
     */
    private void fireSelectionChanged(String selectedSourceName) {
        for (DataSourcePanelListener listener : this.listeners) {
            listener.selectionChanged(selectedSourceName);
        }
    }

    /**
     * Handler for Add Driver hyperlink pressed
     */
    private void handleAddDriver() {
        File theFile = chooseFileFromFileSystem(getShell());
        if(theFile==null) return;
        
        String fileShortName = theFile.getName();
        
        // Check if there is already a DataSource driver with this name
        boolean isDuplicate = isDuplicateDriverName(fileShortName);
        if(isDuplicate) {
            MessageDialog.openError(getShell(), Messages.dataSourceDriversPanelAddDialogErrorTitle, Messages.dataSourceDriversPanelAddDialogErrorDuplicateNameMsg);
            return;
        }
        
        // Deploy the selected file
        deployJarOrRar(theFile);

        // Give a 2 sec pause for the file to deploy
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }   
        
        // Refresh the table and select the just-deployed driver
        refreshDriverList();
        this.driversViewer.refresh();
        packTable();
        setTableSelection(fileShortName);
        fireSelectionChanged(fileShortName);
    }
    
    /**
     * Deploy the specified driver file
     * @param deployFile
     * @return 'true' if deployment was successful, 'false' if not.
     */
    private boolean deployJarOrRar(final File deployFile) {
        try {
            new ProgressMonitorDialog(getShell()).run(false, false, new IRunnableWithProgress() {

                @Override
                public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                    monitor.beginTask(Messages.TeiidImportWizard_DeployDriverMsg, 100);
                    monitor.worked(50);
                    try {
                        teiidImportServer.deployDriver(deployFile);
                    } catch (Exception err) {
                        throw new InvocationTargetException(err);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (final InterruptedException error) {
            return false;
        } catch (Throwable error) {
            if (error instanceof InvocationTargetException) {
                error = ((InvocationTargetException)error).getTargetException();
            }
            error.printStackTrace();
            WidgetUtil.showError(error);
            return false;
        }
        return true;
    }
    
    /*
     * Show dialog to select desired type of file from the FileSystem
     * @param shell the shell
     * @return the selected file
     */
    private File chooseFileFromFileSystem(Shell shell) {
        File fileResult = null;

        final FileDialog dlg = new FileDialog(shell);
        dlg.setFilterExtensions(new String[] {"*.jar","*.rar","*.*"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        dlg.setFilterNames(new String[] {"jar","rar","all files"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

        String fileFullName = dlg.open();
        if(fileFullName!=null) {
            fileResult = new File(fileFullName);
        } 
        return fileResult;
    }
    
    /*
     * Determine if the supplied driver name is already contained in the deployment list
     * @param driverName the name of the template
     * @return 'true' if supplied name is a duplicate, 'false' if not.
     */
    private boolean isDuplicateDriverName(String driverName) {
        if(this.driverList.contains(driverName)) {
            return true;
        }
        return false;
    }
    
    /*
     * Pack the table
     */
    private void packTable() {
        TableColumn[] cols = this.driversViewer.getTable().getColumns();
        for(int i=0; i<cols.length; i++) {
            cols[i].pack();
        }
    }
    
    /*
     * Get the templates array
     * @return the array of template names
     */
    private Object[] getTemplates() {
        return this.driverList.toArray();
    }
    
    /**
     * Refresh the driver list from the server
     */
    private void refreshDriverList( ) {
        this.driverList.clear();
        
        Set<String> names;
        try {
            names = teiidImportServer.getDataSourceTemplateNames();
        } catch (Exception ex) {
            names = Collections.EMPTY_SET;
            UTIL.log(ex);
        }
        this.driverList.addAll(names);
    }
    
    /**
     * Get the selected Driver name
     * @return the name of the selected driver
     */
    public String getSelectedDriverName() {
        return selectedDriver;
    }
    
    /** 
     * Handle template selection changed.
     * @param event
     */
    void handleDriverSelected( SelectionChangedEvent event ) {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();

        // Set the selected template value
        if (!selection.isEmpty()) {
            selectedDriver = (String)selection.getFirstElement();
        } else {
            selectedDriver = null;
        }
        
        // Fire selection changed event to listeners
        fireSelectionChanged(selectedDriver);
    }

    /**
     * Get the current panel status
     * @return the panel status
     */
    public IStatus getStatus() {
        String selectedDriver = this.getSelectedDriverName();
        if(selectedDriver==null || selectedDriver.isEmpty()) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Messages.dataSourceDriversPanelErrorNoSelection);
        }
        
        return new Status(IStatus.OK, PLUGIN_ID, Messages.dataSourceDriversPanelOk);        
    }

    /**
     * Public access to refresh the contents of this panel based on external changes to the translator override
     * properties
     */
    public void refresh() {
        this.driversViewer.setInput(this);
        this.driversViewer.refresh();
    }

    /**
     * Label provider for the Drivers Table
     */
    class DriverLabelProvider extends ColumnLabelProvider {

        public DriverLabelProvider( ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            Image image = null;
            return image;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            String driverName = (String)element;
            if(driverName.equals(TranslatorHelper.TEIID_FILE_DRIVER)) {
                driverName = TranslatorHelper.TEIID_FILE_DRIVER_DISPLAYNAME;
            } else if(driverName.equals(TranslatorHelper.TEIID_GOOGLE_DRIVER)) {
                driverName = TranslatorHelper.TEIID_GOOGLE_DRIVER_DISPLAYNAME;
            } else if(driverName.equals(TranslatorHelper.TEIID_INFINISPAN_DRIVER)) {
                driverName = TranslatorHelper.TEIID_INFINISPAN_DRIVER_DISPLAYNAME;
            } else if(driverName.equals(TranslatorHelper.TEIID_LDAP_DRIVER)) {
                driverName = TranslatorHelper.TEIID_LDAP_DRIVER_DISPLAYNAME;
            } else if(driverName.equals(TranslatorHelper.TEIID_SALESORCE_DRIVER)) {
                driverName = TranslatorHelper.TEIID_SALESORCE_DRIVER_DISPLAYNAME;
            } else if(driverName.equals(TranslatorHelper.TEIID_WEBSERVICE_DRIVER)) {
                driverName = TranslatorHelper.TEIID_WEBSERVICE_DRIVER_DISPLAYNAME;
            }
            return driverName;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
            return Messages.dataSourceDriversPanelItemTooltip+BLANK+((String)element);
        }
    }

}
