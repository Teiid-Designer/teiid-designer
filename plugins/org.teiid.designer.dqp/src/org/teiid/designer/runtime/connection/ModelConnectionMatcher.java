package org.teiid.designer.runtime.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.TeiidDataSource;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

public class ModelConnectionMatcher {
	private ExecutionAdmin admin;
	private ConnectionInfoHelper connectionHelper;

	public ModelConnectionMatcher(ExecutionAdmin admin) {
		super();
		CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$
		this.admin = admin;
		this.connectionHelper = new ConnectionInfoHelper();
	}

	public Collection<TeiidDataSource> findTeiidDataSources() throws Exception {
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
	    		if( this.admin.dataSourceExists(jndiName) ) {
	    			// Create a data source
	    			ModelResource mr = ModelUtil.getModelResource(modelFile, true);
			    	String dsTypeName = this.connectionHelper.findMatchingDataSourceTypeName(mr);
	    			dataSources.add(new TeiidDataSource(modelFile.getProjectRelativePath().lastSegment(), jndiName, dsTypeName));
	    		}
	        }
		}
		
		return dataSources;
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
