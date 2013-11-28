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
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.wizard.ITeiidImportServer;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * CreateDataSourcePanel
 * Panel used for creating, deleting or editing DataSources using the supplied TeiidServer
 *
 * @since 8.1
 */
public final class CreateDataSourcePanel extends Composite implements UiConstants, Listener, 
                                                                      DataSourcePanelListener, DataSourcePropertiesPanelListener {

    private ITeiidImportServer teiidImportServer;
    private DataSourceManager dataSourceManager;
    private StringNameValidator nameValidator;
    private Text dataSourceNameText;
    private String dataSourceName;
    private DataSourceDriversPanel driversPanel;
    private DataSourcePropertiesPanel propertiesPanel;
    private IStatus panelStatus;
    private List<CreateDataSourcePanelListener> listeners = new ArrayList<CreateDataSourcePanelListener>();
    private List<String> existingSourceNames = new ArrayList<String>();
    private String editDSName;
    private boolean isCreateNew=false;
    
    /**
     * CreateDataSourcePanel constructor
     * @param parent the parent composite
     * @param teiidImportServer the TeiidServer to communicate with
     * @param editDSName the DataSource name to edit, null if creating a new source
     */
    public CreateDataSourcePanel( Composite parent, ITeiidImportServer teiidImportServer, String editDSName ) {
        super(parent, SWT.NONE);
        this.teiidImportServer = teiidImportServer;
        this.dataSourceManager = new DataSourceManager(teiidImportServer);
        this.nameValidator = new StringNameValidator(new char[] {'_','-'});
        
        this.editDSName = editDSName;
        if(this.editDSName!=null) {
            this.dataSourceName=editDSName;
            isCreateNew = false;
        } else {
            isCreateNew = true;
        }
        setLayout(new GridLayout());
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        initExistingDataSourceNames(teiidImportServer);
        
        createDataSourceNamePanel(this);
        
        // for creation of new source, allow driver selection
        if(isCreateNew) {
            createDataSourceDriversPanel(this,5);
        }
        
        createDataSourcePropertiesGroup(this);
        
        // Properties panel and this panel listen for drivers selection
        if(isCreateNew) {
            this.driversPanel.addListener(this.propertiesPanel);
            this.driversPanel.addListener(this);
        }
        
        // This panel listens for properties changes
        this.propertiesPanel.addListener(this);
    }
    
    /*
     * Initialize the list of existing data sources
     * @param teiidServer the TeiidServer
     */
    private void initExistingDataSourceNames(ITeiidImportServer teiidImportServer) {
        existingSourceNames.clear();
        try {
            Collection<ITeiidDataSource> sources = teiidImportServer.getDataSources();
            for(ITeiidDataSource source : sources) {
                existingSourceNames.add(source.getName());
            }
        } catch (Exception ex) {
            UTIL.log(ex);
        }
    }
    
    /*
     * Data Source Name Panel
     * @param parent the parent Composite
     */
    private void createDataSourceNamePanel(Composite parent) {
        // -------------------------------------
        // DataSource Name
        // -------------------------------------
        Composite namePanel = new Composite(parent,SWT.NONE);
        namePanel.setLayout(new GridLayout(2, false));
        namePanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label dsNameLabel = new Label(namePanel,SWT.NONE);
        dsNameLabel.setText(Messages.createDataSourcePanel_name);
        
        dataSourceNameText = new Text(namePanel, SWT.BORDER | SWT.SINGLE);
        dataSourceNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if(this.editDSName!=null) {
            dataSourceNameText.setText(editDSName);
            dataSourceNameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
            dataSourceNameText.setEditable(false);
        }
        dataSourceNameText.addListener(SWT.Modify, this);
        
        // For edit of existing source, just show the driver name
        if(!this.isCreateNew) {
            Label dsDriverLabel = new Label(namePanel,SWT.NONE);
            dsDriverLabel.setText(Messages.createDataSourcePanel_driver);
            
            Text dataSourceDriverText = new Text(namePanel, SWT.BORDER | SWT.SINGLE);
            dataSourceDriverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            dataSourceDriverText.setText(getDataSourceDriverName());
            dataSourceDriverText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
            dataSourceDriverText.setEditable(false);
        }
    }
    
    /*
     * DataSource Drivers (Templates) Panel
     * @param parent the parent composite
     * @param visibleTableRows the number of table rows to show
     */
    private void createDataSourceDriversPanel(Composite parent, int visibleTableRows) {
        Group dsDriversGroup = WidgetFactory.createGroup(parent, Messages.createDataSourcePanel_driversGroupTxt, SWT.NONE); 
        dsDriversGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        this.driversPanel = new DataSourceDriversPanel(dsDriversGroup,visibleTableRows,teiidImportServer);
        this.driversPanel.setLayout(new GridLayout(1, false));
        final GridData gData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gData.horizontalSpan = 1;
        this.driversPanel.setLayoutData(gData);
    }

    /*
     * DataSource Properties Group
     * @parent the parent composite
     */
    private void createDataSourcePropertiesGroup(Composite parent) {
        Group dsPropertiesGroup = WidgetFactory.createGroup(parent, Messages.createDataSourcePanel_dataSourcePropertiesGroupTxt, SWT.NONE); 
        GridData propertiesGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        propertiesGridData.heightHint=250;
        propertiesGridData.minimumHeight=250;
        dsPropertiesGroup.setLayoutData(propertiesGridData);

        this.propertiesPanel = new DataSourcePropertiesPanel(dsPropertiesGroup,teiidImportServer,false, isCreateNew, this.editDSName);
        this.propertiesPanel.setLayout(new GridLayout(1, false));
        final GridData gData = new GridData(GridData.BEGINNING);
        gData.horizontalSpan = 1;
        this.propertiesPanel.setLayoutData(gData);
    }
        
    @Override
    public void handleEvent( Event event ) {
        this.dataSourceName = dataSourceNameText.getText();
        updateStatus();
        fireStateChanged();
    }
    
    /**
     * Add listener to the panel
     * @param listener the listener
     */
    public void addListener( CreateDataSourcePanelListener listener ) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    /**
     * Remove listener from the panel
     * @param listener the listener
     */
    public void removeListener( CreateDataSourcePanelListener listener ) {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    /**
     * Fire state change to the listeners
     */
    private void fireStateChanged( ) {
        for (CreateDataSourcePanelListener listener : this.listeners) {
            listener.stateChanged();
        }
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.panels.DataSourcePanelListener#selectionChanged(java.lang.String)
     */
    @Override
    public void selectionChanged(String selectedDriverName) {
        this.propertiesPanel.setDataSourceOrDriverName(selectedDriverName);
        updateStatus();
        fireStateChanged();
    }
    
    /**
     * Get the current panel Status
     * @return the panel Status
     */
    public IStatus getStatus() {
        return panelStatus;
    }
    
    /**
     * Update the Panel Status
     */
    private void updateStatus() {
        
        // Validate the Name
        panelStatus = validateName();
        
        if(panelStatus.isOK()) {
            // Validate the templateSelection
            if(this.isCreateNew) {
                panelStatus = this.driversPanel.getStatus();
            }
            
            // Validate the properties
            if(panelStatus.isOK()) {
                panelStatus = this.propertiesPanel.getStatus();
            }
        }        
    }
    
    /*
     * Validate the DataSource name
     * @return the name status
     */
    private IStatus validateName() {
        String dsName = this.dataSourceNameText.getText();
        
        // Check if null or empty
        if(dsName==null || dsName.isEmpty()) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Messages.createDataSourcePanelErrorNameEmpty);
        }
        
        // For new Source, cannot duplicate name
        if(isCreateNew && existingSourceNames.contains(dsName)) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Messages.createDataSourcePanelErrorNameExists);
        }
        
        // Check for invalid chars
        if(!this.nameValidator.isValidName(dsName)) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Messages.errorNameInvalid);
        }
        
        return new Status(IStatus.OK, PLUGIN_ID, Messages.createDataSourcePanelOk);        
    }

    /**
     * Get the current DataSource name
     * @return the datasource name
     */
    public String getDataSourceName() {
        return this.dataSourceName;
    }
    
    /**
     * Get the current DataSource Driver name
     * @return the datasource driver name
     */
    public String getDataSourceDriverName() {
        if(isCreateNew) {
            return this.driversPanel.getSelectedDriverName();
        }
        return this.dataSourceManager.getDataSourceDriver(this.editDSName);
    }
    
    /**
     * Get the current DataSource Properties
     * @return the datasource properties
     */
    public Properties getDataSourceProperties() {
        return this.propertiesPanel.getDataSourceProperties();
    }
    
    /**
     * Get the state of DataSource property changes
     * @return the 'true' if any properties have changed, 'false' if not
     */
    public boolean hasPropertyChanges() {
        return this.propertiesPanel.hasPropertyChanges();
    }
    
    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.panels.DataSourcePropertiesPanelListener#propertyChanged()
     */
    @Override
    public void propertyChanged() {
        updateStatus();
        fireStateChanged();
    }
        
}
