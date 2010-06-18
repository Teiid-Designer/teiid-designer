package org.teiid.designer.runtime.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.TeiidDataSource;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

public class ModelConnectionMatcher {
	private ExecutionAdmin admin;
	private DqpConnectionInfoHelper connectionHelper;
	private ConnectionInfoHelper profileHelper;

	public ModelConnectionMatcher(ExecutionAdmin admin) {
		super();
		CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$
		this.admin = admin;
		this.connectionHelper = new DqpConnectionInfoHelper();
		this.profileHelper = new ConnectionInfoHelper();
	}

	public Collection<TeiidDataSource> findTeiidDataSources(ExecutionAdmin admin) throws Exception {
		Collection<TeiidDataSource> dataSources = new ArrayList<TeiidDataSource>();
		
		// Get All Workspace Physical Models
		Collection fileResources = WorkspaceResourceFinderUtil.getAllWorkspaceResources();
		if( fileResources != null ) {
	        final Iterator itor = fileResources.iterator();
	        while (itor.hasNext()) {
	            final IFile modelFile = (IFile)itor.next();
		    	String name = modelFile.getFullPath().removeFileExtension().lastSegment();
	    		String jndiName = this.connectionHelper.generateUniqueConnectionJndiName(name, modelFile.getFullPath(), DqpPlugin.workspaceUuid().toString());
	    		// generate JNDI Name, see if it exists in admin
	    		TeiidDataSource tds = createTeiidDataSource(modelFile, jndiName, admin);
	    		
	    		if( tds != null ) {
			    	dataSources.add(tds);
	    		}
	        }
		}
		
		return dataSources;
	}
	
	public TeiidDataSource createTeiidDataSource(IFile modelFile, String jndiName, ExecutionAdmin admin) throws Exception {
		if( this.admin.dataSourceExists(jndiName) ) {
			// Create a data source
			ModelResource mr = ModelUtil.getModelResource(modelFile, true);
	    	String dsTypeName = this.connectionHelper.findMatchingDataSourceTypeName(mr);
	    	if( dsTypeName != null ) {
	    		IConnectionProfile cp = this.profileHelper.getConnectionProfile(mr);
	    		TeiidDataSource tds = new TeiidDataSource(modelFile.getProjectRelativePath().removeFileExtension().lastSegment(), jndiName, dsTypeName, admin);
	    		if( cp != null ) {
	    			tds.setProfileName(cp.getName());
	    		}

	    		return tds;
	    	}
		}
		return null;
	}
	
	public TeiidDataSource findTeiidDataSource(String jndiName, ExecutionAdmin admin) throws Exception {
		
		// Get All Workspace Physical Models
		Collection fileResources = WorkspaceResourceFinderUtil.getAllWorkspaceResources();
		if( fileResources != null ) {
	        final Iterator itor = fileResources.iterator();
	        while (itor.hasNext()) {
	            final IFile modelFile = (IFile)itor.next();
		    	String name = modelFile.getFullPath().removeFileExtension().lastSegment();
	    		String localJndiName = this.connectionHelper.generateUniqueConnectionJndiName(name, modelFile.getFullPath(), DqpPlugin.workspaceUuid().toString());

	    		if( jndiName.equalsIgnoreCase(localJndiName)) {
		    		TeiidDataSource tds = createTeiidDataSource(modelFile, jndiName, admin);
		    		
		    		if( tds != null ) {
				    	return tds;
		    		}
	    		}
	        }
		}
		
		return null;
	}
	
	public Collection<String> findStaleDataSources() throws Exception {
		Collection<String> staleDataSourceNames = new ArrayList<String>();
		
		// Get All Workspace Physical Models
		Collection fileResources = WorkspaceResourceFinderUtil.getAllWorkspaceResources();
		if( fileResources != null ) {
	        final Iterator itor = fileResources.iterator();
	        while (itor.hasNext()) {
	            final IFile modelFile = (IFile)itor.next();
		    	String name = modelFile.getFullPath().removeFileExtension().lastSegment();
	    		String jndiName = this.connectionHelper.generateUniqueConnectionJndiName(name, modelFile.getFullPath(), DqpPlugin.workspaceUuid().toString());
	    		if( !this.admin.dataSourceExists(jndiName) ) {
	    			staleDataSourceNames.add(jndiName);
	    		}
	        }
		}
		
		return staleDataSourceNames;
	}

}
