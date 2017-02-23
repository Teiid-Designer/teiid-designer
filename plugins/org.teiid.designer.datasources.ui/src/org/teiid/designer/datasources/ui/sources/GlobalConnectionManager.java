package org.teiid.designer.datasources.ui.sources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.panels.DataSourceItem;
import org.teiid.designer.datasources.ui.panels.DataSourceManager;
import org.teiid.designer.datasources.ui.wizard.TeiidDataSourceManager;
import org.teiid.designer.runtime.spi.ITeiidDataSource;

public class GlobalConnectionManager {
	public static final String PROFILES_NAME = "Profiles";
	public static final String SERVER_CONNECTIONS__NAME = "Server Connections";
	
	private DataSourceManager dataSourceManager;
	TeiidDataSourceManager importManager  = new TeiidDataSourceManager();
    private List<DataSourceItem> dataSourceObjList = new ArrayList<DataSourceItem>();
	
	RootConnectionNode[] rootNodes;
	
	public GlobalConnectionManager() {
		super();
		this.dataSourceManager = new DataSourceManager(this.importManager);
		refreshDataSourceList();
	}
	
	public TeiidDataSourceManager getImportManager() {
		return this.importManager;
	}
	
	public DataSourceManager getDataSourceManager() {
		return this.dataSourceManager;
	}
	
	public boolean serverAvailable() {
		return this.importManager.isValidImportServer();
	}

	public RootConnectionNode[] getRootNodes() {
		if( rootNodes == null ) {
			Collection<RootConnectionNode> nodes = new ArrayList<RootConnectionNode>(2);
			nodes.add(new RootConnectionNode(PROFILES_NAME, RootConnectionNode.PROFILE));
			nodes.add(new RootConnectionNode(SERVER_CONNECTIONS__NAME, RootConnectionNode.DATASOURCE));
			rootNodes = nodes.toArray(new RootConnectionNode[2]);
		}
		
		return rootNodes;
	}
	
    /**
     * Get DataSourceItem array
     * @return the array of DataSourceItem
     */
	public Object[] getDataSources() {
        return this.dataSourceObjList.toArray();
    }
	
    /*
     * Refresh the current data source list by requesting from the TeiidServer
     */
    public void refreshDataSourceList( ) {
        this.dataSourceObjList.clear();
        
        Collection<ITeiidDataSource> dataSources;
        try {
            dataSources = importManager.getDataSources();
        } catch (Exception ex) {
            dataSources = new ArrayList<ITeiidDataSource>();
            UiConstants.UTIL.log(ex);
        }
        
        // get className - driverMap for RA sources
        for(ITeiidDataSource dataSource: dataSources) {
            DataSourceItem dsObj = new DataSourceItem();
            // ------------------------
            // Set PropertyItem fields
            // ------------------------
            // Name
            String name = dataSource.getName();
            dsObj.setName(name);

            String sourceJndiName = dataSource.getPropertyValue("jndi-name");  //$NON-NLS-1$
            if(sourceJndiName!=null) {
            	dsObj.setJndiName(sourceJndiName);
            // The jndi property may not be present if the source was just created.  Use the default name.
            } else {
            	dsObj.setJndiName("java:/"+name); //$NON-NLS-1$
            }

             Map<String,String> classNameDriverNameMap = dataSourceManager.getClassNameDriverNameMap(dataSources);
        
       // Driver name
            String dsDriver = dataSourceManager.getDataSourceDriver(name, classNameDriverNameMap);
            dsObj.setDriver(dsDriver);
            // ------------------------
            // Add PropertyItem to List
            // ------------------------
            this.dataSourceObjList.add(dsObj);
        }
    }
}
