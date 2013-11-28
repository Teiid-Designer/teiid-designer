/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.teiidimporter.ui.wizard;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.panels.DataSourcePanel;
import org.teiid.designer.teiidimporter.ui.panels.DataSourcePanelListener;
import org.teiid.designer.teiidimporter.ui.panels.DataSourcePropertiesPanel;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;


/**
 * SelectDataSourcePage
 * Page 1 of the TeiidImportWizard - for selection of the DataSource to use for the import
 * 
 * @since 8.1
 */
public class SelectDataSourcePage extends AbstractWizardPage
    implements UiConstants, DataSourcePanelListener {

    private static final String EMPTY_STR = ""; //$NON-NLS-1$
    private static final String SERVER_PREFIX = "Server: "; //$NON-NLS-1$
    
    // Source types that cannot be imported with this wizard
    private static final List<String> DISALLOWED_SOURCES;
    static {
    	DISALLOWED_SOURCES = new ArrayList<String>();
    	DISALLOWED_SOURCES.add("ldap");  //$NON-NLS-1$
    	DISALLOWED_SOURCES.add("mongodb"); //$NON-NLS-1$
    }

    private TeiidImportManager importManager;

    private DataSourcePropertiesPanel propertiesPanel;
    private DataSourcePanel dataSourcePanel;
   
    /**
     * SelectDataSourceConstructor
     * @param importManager the TeiidImportManager for the wizard
     */
    public SelectDataSourcePage( TeiidImportManager importManager ) {
        super(SelectDataSourcePage.class.getSimpleName(), Messages.selectDataSourcePage_title); 
        this.importManager = importManager;        
    }

    @Override
    public void createControl( Composite theParent ) {
        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(1, false));

        setControl(pnl);

        // Must have a running 8+ server to use this wizard.
        if(!importManager.isValidImportServer()) {
            setErrorMessage(Messages.selectDataSourcePage_InvalidServerMsg); 
            setPageComplete(false);
            return;
        }
        
        Label serverNameLabel = new Label(pnl,SWT.NONE);
        String serverString;
        try {
            serverString = importManager.getDisplayName();
        } catch (Exception ex) {
            serverString = "Unknown"; //$NON-NLS-1$
        }
        serverNameLabel.setText(SERVER_PREFIX+serverString);
                
        // Create Panel containing Connection name and translator combo
        createDataSourcesGroup(pnl);
        
        // Group for selection of the Connections
        createDataSourceDetailsGroup(pnl);
                
        this.dataSourcePanel.addListener(this.propertiesPanel);
        this.dataSourcePanel.addListener(this);
        
        // Validate the page
        validatePage();
    }

    /*
     * Panel for selection of the Connection Type
     * @param parent the parent Composite
     */
    private void createDataSourcesGroup(Composite parent) {
        Group dataSourcesGroup = WidgetFactory.createGroup(parent, Messages.selectDataSourcePage_dataSourceGroupText, SWT.NONE); 
        dataSourcesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        int visibleRows = 5;
        this.dataSourcePanel = new DataSourcePanel(dataSourcesGroup, visibleRows, importManager);
        this.dataSourcePanel.setLayout(new GridLayout(1, false));
        this.dataSourcePanel.setLayoutData(new GridData(SWT.BEGINNING,SWT.BEGINNING,true,true));
    }
    
    /*
     * Connection Templates Definition Group
     * @parent the parent composite
     */
    private void createDataSourceDetailsGroup(Composite parent) {
        Group dsPropertiesGroup = WidgetFactory.createGroup(parent, Messages.selectDataSourcePage_dataSourcePropertiesGroupText, SWT.NONE); 
        dsPropertiesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.propertiesPanel = new DataSourcePropertiesPanel(dsPropertiesGroup,importManager,true,false,null);
        this.propertiesPanel.setLayout(new GridLayout(1, false));
        final GridData gData = new GridData(GridData.BEGINNING);
        gData.horizontalSpan = 1;
        this.propertiesPanel.setLayoutData(gData);
    }
    
    private String getSelectedDataSourceName() {
        return this.importManager.getDataSourceName();
    }
    
    private String getSelectedDataSourceDriverName() {
        return this.importManager.getDataSourceDriverName();
    }
    
    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            getControl().setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }
    
    /*
     * Page Validation
     * @return 'true' if the page is valid, 'false' if not
     */
    private boolean validatePage() {
        // Ensure DataSource name is set
        String selectedDataSource = getSelectedDataSourceName();
        if(selectedDataSource==null || selectedDataSource.trim().isEmpty()) {
            setThisPageComplete(Messages.selectDataSourcePage_NoSourceSelectedMsg, ERROR);
            return false;
        }
         
        // The importer cannot be used for some types - the DDL cannot be generated.  Manual modeling is required.
        String selectedDataSourceDriverName = getSelectedDataSourceDriverName();
        if(!CoreStringUtil.isEmpty(selectedDataSourceDriverName)) {
        	String driverNameLC = selectedDataSourceDriverName.toLowerCase();
        	if(DISALLOWED_SOURCES.contains(driverNameLC)) {  
                setThisPageComplete( NLS.bind(Messages.selectDataSourcePage_CannotImportSourceTypeMsg, driverNameLC) , ERROR);
                return false;        		
        	}
        }
        setThisPageComplete(EMPTY_STR, NONE);
        return true;
    }
        
    private void setThisPageComplete( String message, int severity) {
        WizardUtil.setPageComplete(this, message, severity);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.panels.DataSourcePanelListener#selectionChanged(java.lang.String)
     */
    @Override
    public void selectionChanged(String selectedSourceName) {
        importManager.setDataSourceName(this.dataSourcePanel.getSelectedDataSourceName());
        importManager.setDataSourceDriverName(this.dataSourcePanel.getSelectedDataSourceDriver());
        importManager.setDataSourceProperties(this.propertiesPanel.getDataSourceProperties());
        
        // Selection changed - validate the page
        validatePage();
    }
    
}
