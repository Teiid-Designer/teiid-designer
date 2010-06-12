/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.runtime.connection.ModelConnectionMapper;
import org.teiid.designer.vdb.connections.SourceHandler;
import org.teiid.designer.vdb.connections.VdbSourceConnection;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * 
 */
public class VdbSouceConnectionHandler implements SourceHandler {

	@Override
	public VdbSourceConnection ensureVdbSourceConnection (
			String sourceModelname, Properties properties) throws Exception {
    	CoreArgCheck.isNotNull(properties, "properties"); //$NON-NLS-1$
    	
        ModelConnectionMapper mapper = new ModelConnectionMapper(sourceModelname, properties);
        
        VdbSourceConnection vdbSourceConnection = null;
        
        try {
			vdbSourceConnection = mapper.getVdbSourceConnection();
		} catch (ModelWorkspaceException e) {
			// TODO Auto-generated catch block
			DqpUiPlugin.UTIL.log(IStatus.ERROR, e, 
					DqpUiConstants.UTIL.getString("VdbSourceConnectionHandler.Error_could_not_find_source_connection_info_for_{0}_model", sourceModelname)); //$NON-NLS-1$
		}
        
		// TODO: vdbSourceConnection may be NULL, so query the user for translator name & jndi name
		
        return vdbSourceConnection;
	}
	
	
    
    
}
