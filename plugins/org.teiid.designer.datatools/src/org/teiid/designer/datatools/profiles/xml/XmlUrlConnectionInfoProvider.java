/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.xml;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.DatatoolsPlugin;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionConstants;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

public class XmlUrlConnectionInfoProvider  extends ConnectionInfoHelper implements IConnectionInfoProvider {

	@Override
	public String getDataSourcePasswordPropertyKey() {
		return null;
	}

	@Override
	public String getDataSourceType() {
		return DataSourceConnectionConstants.DataSource.WS;
	}

	@Override
	public String getPasswordPropertyKey() {
		return "bogus_xxxx"; //$NON-NLS-1$
	}

	@Override
	public Properties getTeiidRelatedProperties(IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties props = connectionProfile.getBaseProperties();
        
        String fileUrl = props.getProperty(IXmlProfileConstants.URL_PROP_ID);
        if( fileUrl != null ) {
			IPath fullPath = new Path(fileUrl);
			String fileURL = fullPath.toString();
        	connectionProps.setProperty(IXmlProfileConstants.WS_ENDPOINT_KEY, fileURL);
        }

        return connectionProps;
	}

	@Override
	public void setConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
		Properties connectionProps = getCommonProfileProperties(connectionProfile);

		Properties props = connectionProfile.getBaseProperties();

		String result = props.getProperty(IXmlProfileConstants.URL_PROP_ID);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + IXmlProfileConstants.URL_PROP_ID, result);
		}


		getHelper().removeProperties(modelResource,
				CONNECTION_PROFILE_NAMESPACE);
		getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
		getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

		connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, "ws"); //$NON-NLS-1$
		getHelper().setProperties(modelResource, connectionProps);

	}

	@Override
	public Properties getCommonProfileProperties(IConnectionProfile profile) {
		return super.getCommonProfileProperties(profile);
	}

	@Override
	public IConnectionProfile getConnectionProfile(ModelResource modelResource) {
		IConnectionProfile profile = super.getConnectionProfile(modelResource);
		
		Properties baseProps = profile.getBaseProperties();
		Properties flatFileProps = new Properties();
		
		try {
			flatFileProps = getConnectionProperties(modelResource);
		} catch (ModelWorkspaceException e) {
			DatatoolsPlugin.Util.log(e);
		}
		
		String result = flatFileProps.getProperty(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID);
		if( result != null ) {
			baseProps.put(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID, result);
		}


		return profile;
	}

	@Override
	public Properties getConnectionProperties(ModelResource modelResource)
			throws ModelWorkspaceException {
		Properties modelProps = super.getConnectionProperties(modelResource);
		
		return modelProps;
		
//		Properties connProps = new Properties();
		// Search for "HOME" value
		
//		String fileUrl = modelProps.getProperty(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID);
//		if( fileUrl != null ) {
//			IPath fullPath = new Path(fileUrl);
//			String fileName = fullPath.lastSegment().toString();
//			String directoryUrl = fullPath.removeLastSegments(1).toString();
//			connProps.put(IXmlProfileConstants.TEIID_PARENT_DIRECTORY_KEY, directoryUrl);
//			connProps.put(IXmlProfileConstants.XML_FILE_NAME, fileName);
//		}
//		
//		return connProps;
	}

	@Override
	public String getTranslatorName(ModelResource modelResource) {
		return "ws"; //$NON-NLS-1$
	}

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return false;
	}
}
