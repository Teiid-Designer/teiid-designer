/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.datatools.connectivity.ICategory;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.datasources.ui.Messages;
import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.panels.DataSourceItem;
import org.teiid.designer.datasources.ui.panels.DataSourceManager;
import org.teiid.designer.datasources.ui.wizard.TeiidDataSourceManager;
import org.teiid.designer.runtime.spi.ITeiidDataSource;

public class GlobalConnectionManager implements UiConstants {
	
	private DataSourceManager dataSourceManager;
	TeiidDataSourceManager importManager  = new TeiidDataSourceManager();
    private List<DataSourceItem> dataSourceObjList = new ArrayList<DataSourceItem>();
	
	RootConnectionNode[] rootNodes;
	TeiidConnectionFolder[] teiidFolders;
	
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
			nodes.add(new RootConnectionNode(Messages.LocalProfiles, RootConnectionNode.PROFILE));
			if(ModelerCore.getTeiidServerManager() != null && 
			   ModelerCore.getTeiidServerManager().isStarted() &&
			   ModelerCore.getTeiidServerManager().getDefaultServer() != null &&
			   ModelerCore.getTeiidServerManager().getDefaultServer().isConnected()
					) {
				nodes.add(new RootConnectionNode(Messages.Deployed, RootConnectionNode.DATASOURCE));
			} else {
				nodes.add(new RootConnectionNode(Messages.DeployedNotConnected, RootConnectionNode.DATASOURCE));
			}
			rootNodes = nodes.toArray(new RootConnectionNode[2]);
		}
		
		return rootNodes;
	}
	
	public boolean isServerAvailable() {
		return ModelerCore.getTeiidServerManager() != null && 
				   ModelerCore.getTeiidServerManager().isStarted() &&
				   ModelerCore.getTeiidServerManager().getDefaultServer() != null &&
				   ModelerCore.getTeiidServerManager().getDefaultServer().isConnected();
	}
	
	public TeiidConnectionFolder[] getTeiidFolders(ICategory profileCategory) {
		if( teiidFolders == null ) {
			Collection<TeiidConnectionFolder> nodes = new ArrayList<TeiidConnectionFolder>(10);
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_FLAT_FILE_URL_LABEL, TeiidProfileIDs.FLAT_FILE_URL, profileCategory));
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_JBOSS_JNDI_LABEL, TeiidProfileIDs.JBOSS_JNDI, profileCategory));
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_JDG_LABEL, TeiidProfileIDs.JDG, profileCategory));
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_LDAP_LABEL, TeiidProfileIDs.LDAP, profileCategory));
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_SALESFORCE_LABEL, TeiidProfileIDs.SALESFORCE, profileCategory));
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_WS_ODATA_LABEL, TeiidProfileIDs.WS_ODATA, profileCategory));
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_WS_REST_LABEL, TeiidProfileIDs.WS_REST, profileCategory));
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_WS_SOAP_LABEL, TeiidProfileIDs.WS_SOAP, profileCategory));
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_XML_FILE_LOCAL_LABEL, TeiidProfileIDs.XML_FILE_LOCAL, profileCategory));
			nodes.add(new TeiidConnectionFolder(TEIID_PROFILE_XML_FILE_URL_LABEL, TeiidProfileIDs.XML_FILE_URL, profileCategory));
			teiidFolders = nodes.toArray(new TeiidConnectionFolder[10]);
		}
		
		return teiidFolders;
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
    	rootNodes = null;
    	
    	getRootNodes();
    	
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
