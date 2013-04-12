/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.teiidimporter.ui.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.teiidimporter.ui.Activator;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.wizard.ITeiidImportServer;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * DataSourcePropertiesPanel
 * This panel shows the properties for the selected DataSource
 *
 * @since 8.1
 */
public final class DataSourcePropertiesPanel extends Composite implements UiConstants, DataSourcePanelListener {

    private static int GROUP_HEIGHT_100 = 100;
    private String dataSourceOrDriverName;
    private TableViewer propertiesViewer;
    private ITeiidImportServer teiidImportServer;
    private DataSourceManager dataSourceMgr;
    private List<PropertyItem> propertyItemList = new ArrayList<PropertyItem>();
    private boolean isCreateNew = false;
    private boolean isReadOnly = false;

    private List<DataSourcePropertiesPanelListener> listeners = new ArrayList<DataSourcePropertiesPanelListener>();
    private Button resetButton;
    
    /**
     * DataSourcePropertiesPanel constructor
     * @param parent the parent composite
     * @param teiidImportServer the TeiidServer
     * @param isReadOnly whether the panel is readonly
     * @param isCreateNew 'true' if creating a new source, 'false' if not
     * @param initialSelection the initialSelection to display
     */
    public DataSourcePropertiesPanel( Composite parent, ITeiidImportServer teiidImportServer, boolean isReadOnly, boolean isCreateNew, String initialSelection ) {
        super(parent, SWT.NONE);
        this.teiidImportServer = teiidImportServer;
        this.isCreateNew = isCreateNew;
        this.isReadOnly = isReadOnly;
        this.dataSourceOrDriverName = initialSelection;
        this.dataSourceMgr = new DataSourceManager(this.teiidImportServer);
        
        Composite outerPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 2);
        GridLayout bpGL = new GridLayout(2, false);
        bpGL.marginHeight = 1;
        outerPanel.setLayout(bpGL);        
        
        if(!isReadOnly) {
            createButtonsPanel(outerPanel);
        }

        createTablePanel(outerPanel,isReadOnly);
    }
    
    /*
     * Create the buttons panel containing the reset button
     * @param parent the parent composite
     */
    private void createButtonsPanel(Composite parent) {
        Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        panel.setLayout(new GridLayout(1, false));
        GridData gData = new GridData(SWT.CENTER, SWT.BEGINNING, false, false);
        gData.heightHint=GROUP_HEIGHT_100;
        panel.setLayoutData(gData);
        
        resetButton = new Button(panel, SWT.PUSH);
        resetButton.setText(Messages.dataSourcePropertiesPanel_resetButton); 
        resetButton.setToolTipText(Messages.dataSourcePropertiesPanel_resetTooltip);
        resetButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        resetButton.setEnabled(false);
        resetButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleResetProperty();
            }
            
        });        
    }

    /*
     * Create the properties table panel 
     * @param parent the parent composite
     * @param isReadOnly 'true' if not editable
     */
    private void createTablePanel(Composite parent, boolean isReadOnly) {
        Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);

        // Create Table Viewer
        int tableStyle = SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION;
        this.propertiesViewer = new TableViewer(panel, tableStyle);
        
        // Create 'Required' label below table 
        Label reqdLabel = new Label(panel,SWT.NONE);
        reqdLabel.setText(Messages.dataSourcePropertiesPanel_requiredLabel);

        ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);
        this.propertiesViewer.setContentProvider(new IStructuredContentProvider() {
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
                return getProperties();
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

        Table table = this.propertiesViewer.getTable();
        table.setLayout(new TableLayout());

        final GridData gridData = new GridData(GridData.FILL_BOTH); 
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        table.setLayoutData(gridData);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
        column.getColumn().setText(Messages.dataSourcePropertiesPanel_nameColText);
        column.setLabelProvider(new PropertyLabelProvider(true));
        column.getColumn().pack();

        column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
        column.getColumn().setText(Messages.dataSourcePropertiesPanel_valueColText);
        column.setLabelProvider(new PropertyLabelProvider(false));
        // Add editing support if its not readonly
        if(!isReadOnly) {
            column.setEditingSupport(new DataSourcePropertyEditingSupport(this.propertiesViewer,this));
        }
        column.getColumn().pack();

        this.propertiesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handlePropertySelected(event);
            }
        });

        this.propertiesViewer.setInput(this);
        updatePropertiesList();
        this.propertiesViewer.refresh();
        packTable();
    }
    
    /**
     * Add a listener to this panel
     * @param listener the listener 
     */
    public void addListener( DataSourcePropertiesPanelListener listener ) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener
     * @param listener the listener
     */
    public void removeListener( DataSourcePropertiesPanelListener listener ) {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    /**
     * Fire property changed to the listeners
     */
    public void firePropertyChanged() {
        for (DataSourcePropertiesPanelListener listener : this.listeners) {
            listener.propertyChanged( );
        }
    }
    
    /**
     * Handler for reset property action
     */
    private void handleResetProperty() {
        assert (!this.propertiesViewer.getSelection().isEmpty());
        PropertyItem prop = getSelectedProperty();
        prop.reset();
        this.propertiesViewer.refresh(prop);
        this.resetButton.setEnabled(false);
    }
    
    /**
     * Set the DataSource or Driver name to display the its properties.
     * If 'isCreateNew' is set - it is assumed the driver name is being supplied, otherwise it is assumed
     * to be a dataSource name.
     * @param sourceOrDriverName the dataSource or driver name
     */
    public void setDataSourceOrDriverName(String sourceOrDriverName) {
        this.dataSourceOrDriverName=sourceOrDriverName;
        updatePropertiesList();
        this.propertiesViewer.refresh();
        packTable();
    }
    
    /*
     * pack the table
     */
    private void packTable() {
        TableColumn[] cols = this.propertiesViewer.getTable().getColumns();
        for(int i=0; i<cols.length; i++) {
            cols[i].pack();
        }
    }

    /*
     * get the PropertyItem Array
     */
    private Object[] getProperties() {
        return this.propertyItemList.toArray();
    }
    
    /**
     * Get the deployment Properties for the currently selected Data Source.  The property will be used if
     * 1) it is required
     * 2) it is not required, and the value is different than the default
     * @return the Properties for the current selection
     */
    public Properties getDataSourceProperties() {
        Properties resultProperties = new Properties();
        for(PropertyItem propObj : this.propertyItemList) {
            String propName = propObj.getName();
            String propValue = propObj.getValue();
            String defaultValue = propObj.getDefaultValue();
            boolean isModifiable = propObj.isModifiable();
            boolean isRequired = propObj.isRequired();
            if(isRequired || (isModifiable && !valuesSame(propValue,defaultValue))) {
                resultProperties.setProperty(propName,propValue);
            }
        }
        return resultProperties;
    }
    
    private boolean valuesSame(String value1, String value2) {
        if(CoreStringUtil.isEmpty(value1) && CoreStringUtil.isEmpty(value2)) {
            return true;
        }
        if(CoreStringUtil.isEmpty(value1) && !CoreStringUtil.isEmpty(value2)) {
            return false;
        }
        if(CoreStringUtil.isEmpty(value2) && !CoreStringUtil.isEmpty(value1)) {
            return false;
        }
        if(!value1.equalsIgnoreCase(value2)) {
            return false;
        }
        return true;
    }
    
    /**
     * Get the state of DataSource property changes
     * @return the 'true' if any properties have changed, 'false' if not
     */
    public boolean hasPropertyChanges() {
        boolean hasChange = false;
        for(PropertyItem propItem : this.propertyItemList) {
            if(propItem.hasChanged()) {
                hasChange=true;
                break;
            }
        }
        return hasChange;
    }
    
    /*
     * Update the PropertyItem list for the currently selected DataSource name - from the TeiidServer
     */
    private void updatePropertiesList( ) {
        if(this.dataSourceOrDriverName!=null) {
            if(this.isCreateNew) {
                this.propertyItemList =  sortPropertyItems(this.dataSourceMgr.getDriverPropertyItems(this.dataSourceOrDriverName));
            } else {
                this.propertyItemList =  sortPropertyItems(this.dataSourceMgr.getDataSourcePropertyItems(this.dataSourceOrDriverName));
            }
        }
    }
    
    /*
     * Custom sorting for the Properties list.  Always put Connection url first, then username and password (if present).
     * Then all required properties, then all others
     * @param propItems the supplied list of PropertyItems
     * @return the sorted list of PropertyItems
     */
    private List<PropertyItem> sortPropertyItems(List<PropertyItem> propItems) {
        CoreArgCheck.isNotNull(propItems);
        
        List<PropertyItem> resultList = new ArrayList<PropertyItem>(propItems.size());
        
        // Create a list of the display names
        List<String> propDisplayNames = new ArrayList<String>(propItems.size());
        for(PropertyItem item: propItems) {
            propDisplayNames.add(item.getDisplayName());
        }
        
        // Sort the list in alpha order
        Collections.sort(propDisplayNames);
        
        // Connection url is first, if present
        if(propDisplayNames.contains(PropertyItem.CONNECTION_URL_DISPLAYNAME)) {
            PropertyItem item = getPropertyItem(PropertyItem.CONNECTION_URL_DISPLAYNAME,propItems);
            if(item!=null) resultList.add(item);
            propDisplayNames.remove(PropertyItem.CONNECTION_URL_DISPLAYNAME);
        }
        
        // Username is second, if present
        if(propDisplayNames.contains(PropertyItem.USERNAME_PROP_DISPLAYNAME)) {
            PropertyItem item = getPropertyItem(PropertyItem.USERNAME_PROP_DISPLAYNAME,propItems);
            if(item!=null) resultList.add(item);
            propDisplayNames.remove(PropertyItem.USERNAME_PROP_DISPLAYNAME);
        }
        
        // Password is third, if present
        if(propDisplayNames.contains(PropertyItem.PASSWORD_PROP_DISPLAYNAME)) {
            PropertyItem item = getPropertyItem(PropertyItem.PASSWORD_PROP_DISPLAYNAME,propItems);
            if(item!=null) resultList.add(item);
            propDisplayNames.remove(PropertyItem.PASSWORD_PROP_DISPLAYNAME);
        }
        
        // Get the names of all the required props
        List<String> requiredDisplayNames = new ArrayList<String>(propDisplayNames.size());
        for(String propName: propDisplayNames) {
            PropertyItem item = getPropertyItem(propName,propItems);
            if(item.isRequired()) {
                requiredDisplayNames.add(item.getDisplayName());
            }
        }
        // Put required Props in list
        for(String propName: requiredDisplayNames) {
            PropertyItem item = getPropertyItem(propName,propItems);
            if(item!=null) resultList.add(item);
        }
        // remove required from the overall list
        propDisplayNames.removeAll(requiredDisplayNames);

        // Now put the remaining property items in the result list
        for(String propName: propDisplayNames) {
            PropertyItem item = getPropertyItem(propName,propItems);
            resultList.add(item);
        }
        
        return resultList;
    }
    
    /*
     * Get the PropertyItem from the supplied list with the provided display name
     * @param the display name
     * @param the list of PropertyItems
     * @return the matching PropertyItem, null if not match
     */
    private PropertyItem getPropertyItem(String displayName, List<PropertyItem> propItems) {
        CoreArgCheck.isNotNull(propItems);
        CoreArgCheck.isNotNull(displayName);
        
        PropertyItem resultItem = null;
        for(PropertyItem item: propItems) {
            if(item.getDisplayName().equalsIgnoreCase(displayName)) {
                resultItem = item;
                break;
            }
        }
        return resultItem;
    }
    
    /*
     * Get the currently selected PropertyItem from the table, null if no selection
     * @return the selected PropertyItem
     */
    private PropertyItem getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)this.propertiesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (PropertyItem)selection.getFirstElement();
    }


    /*
     * Handler for selection changed events
     */
    void handlePropertySelected( SelectionChangedEvent event ) {
        if(isReadOnly) return;
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();

        if (selection.isEmpty()) {
            if (this.resetButton.isEnabled()) {
                this.resetButton.setEnabled(false);
            }
        } else {
            PropertyItem prop = (PropertyItem)selection.getFirstElement();
            boolean enable = prop.hasChanged();
            if (this.resetButton.isEnabled() != enable) {
                this.resetButton.setEnabled(enable);
            }
        }
    }

    /*
     * Handler for ResetAction
     */
    void handleRestorePropertyDefaultValue() {
        assert (!this.propertiesViewer.getSelection().isEmpty());

        PropertyItem prop = getSelectedProperty();
        prop.setValue(null);
        this.propertiesViewer.refresh(prop);
        this.resetButton.setEnabled(false);
    }
    
    /**
     * Get the current panel Status
     * @return the current Status
     */
    public IStatus getStatus() {
        for(PropertyItem propObj : this.propertyItemList) {
            if(!propObj.hasValidValue()) {
                return new Status(IStatus.ERROR, PLUGIN_ID, Messages.dataSourcePropertiesPanel_invalidPropertyMsg);        
            }
        }

        return new Status(IStatus.OK, PLUGIN_ID, Messages.dataSourcePropertiesPanelOk);        
    }
    
    /**
     * Public access to refresh the contents of this panel based on external changes to the translator override
     * properties
     */
    public void refresh() {
        this.propertiesViewer.setInput(this);
        this.propertiesViewer.refresh();
    }

    /**
     * Label Provider for the Property panel
     */
    class PropertyLabelProvider extends ColumnLabelProvider {

        private final boolean nameColumn;

        public PropertyLabelProvider( boolean nameColumn ) {
            this.nameColumn = nameColumn;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            PropertyItem property = (PropertyItem)element;
            
            Image image = null;
            Image changedImage = Activator.getDefault().getImage(UiConstants.ImageIds.RESET_PROPERTY);
            Image errorImage = Activator.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

            if (!this.nameColumn) {
                // determine if propery has changed
                boolean hasChanged = property.hasChanged();
                if(hasChanged) {
                    image = changedImage;
                }
                
                // determine if valid property
                boolean hasValidValue = property.hasValidValue();
                
                if (!hasValidValue) {
                    image = errorImage;
                } else {
                    image = null;
                }
            }

            return image;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            PropertyItem property = (PropertyItem)element;

            if (this.nameColumn) {
                if(property.isRequired()) {
                    return "* "+property.getDisplayName();  //$NON-NLS-1$
                }
                return property.getDisplayName();
            } else {
                String value = property.getValue();
                if((property.isMasked() || property.getDisplayName().equalsIgnoreCase(PropertyItem.PASSWORD_PROP_DISPLAYNAME)) && !CoreStringUtil.isEmpty(value)) {
                    return "*****"; //$NON-NLS-1$
                }
                return value;
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
            PropertyItem propObj = (PropertyItem)element;
            if(this.nameColumn) {
                return propObj.getDisplayName();
            }
            
            if(propObj.hasValidValue()) {
                return NLS.bind(Messages.dataSourcePropertiesPanel_validPropertyTooltip, propObj.getDisplayName());
            }
            
            return NLS.bind(Messages.dataSourcePropertiesPanel_invalidPropertyTooltip, propObj.getDisplayName());
        }
    }

    /**
     * SelectionEvent is received from DataSourcePanel
     */
    @Override
    public void selectionChanged(String dataSourceOrDriverName) {
        setDataSourceOrDriverName(dataSourceOrDriverName);
    }

}
