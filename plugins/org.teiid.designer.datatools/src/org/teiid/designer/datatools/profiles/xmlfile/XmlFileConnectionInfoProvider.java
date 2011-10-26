/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.xmlfile;

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

public class XmlFileConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider {

	@Override
	public String getDataSourcePasswordPropertyKey() {
		// Flat file connection profile doesn't use password, but need one to prevent NPE
		return null;
	}

	@Override
	public String getDataSourceType() {
		return DataSourceConnectionConstants.DataSource.FILE;
	}

	@Override
	public String getPasswordPropertyKey() {
		// Flat file connection profile doesn't use password, but need one to prevent NPE
		return "bogus_xxxx"; //$NON-NLS-1$
	}

	@Override
	public Properties getTeiidRelatedProperties(
			IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties props = connectionProfile.getBaseProperties();
        
        String fileUrl = props.getProperty(XmlFileProfileConstants.FILELIST);
        if( fileUrl != null ) {
			IPath fullPath = new Path(fileUrl);
			String directoryUrl = fullPath.removeLastSegments(1).toString();
        	connectionProps.setProperty(XmlFileProfileConstants.TEIID_PARENT_DIRECTORY_KEY, directoryUrl);
        }

        return connectionProps;
	}

	@Override
	public void setConnectionInfo(ModelResource modelResource,
			IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
		Properties connectionProps = getCommonProfileProperties(connectionProfile);

		Properties props = connectionProfile.getBaseProperties();

		String result = props.getProperty(XmlFileProfileConstants.FILELIST_PROPERTY_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE
					+ XmlFileProfileConstants.FILELIST, result);
		}

		result = props.getProperty(XmlFileProfileConstants.SCHEMAFILELIST_PROPERTY_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE
					+ XmlFileProfileConstants.SCHEMAFILELIST, result);
		}

		result = props.getProperty(XmlFileProfileConstants.ENCODINGLIST_PROPERTY_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE
					+ XmlFileProfileConstants.ENCODINGLIST, result);
		}

		getHelper().removeProperties(modelResource,
				CONNECTION_PROFILE_NAMESPACE);
		getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
		getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

		connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, "file"); //$NON-NLS-1$
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
		
		String result = flatFileProps.getProperty(XmlFileProfileConstants.FILELIST);
		if( result != null ) {
			baseProps.put(XmlFileProfileConstants.FILELIST_PROPERTY_KEY, result);
		}
		result = flatFileProps.getProperty(XmlFileProfileConstants.SCHEMAFILELIST);
		if( result != null ) {
			baseProps.put(XmlFileProfileConstants.SCHEMAFILELIST_PROPERTY_KEY, result);
		}
		result = flatFileProps.getProperty(XmlFileProfileConstants.ENCODINGLIST);
		if( result != null ) {
			baseProps.put(XmlFileProfileConstants.ENCODINGLIST_PROPERTY_KEY, result);
		}

		return profile;
	}

	@Override
	public Properties getConnectionProperties(ModelResource modelResource)
			throws ModelWorkspaceException {
		Properties modelProps = super.getConnectionProperties(modelResource);
		
		Properties connProps = new Properties();
		// Search for "HOME" value
		
		String fileUrl = modelProps.getProperty(XmlFileProfileConstants.FILELIST);
		if( fileUrl != null ) {
			IPath fullPath = new Path(fileUrl);
			String fileName = fullPath.lastSegment().toString();
			String directoryUrl = fullPath.removeLastSegments(1).toString();
			connProps.put(XmlFileProfileConstants.TEIID_PARENT_DIRECTORY_KEY, directoryUrl);
			connProps.put(XmlFileProfileConstants.XML_FILE_NAME, fileName);
		}
		
		return connProps;
	}

	@Override
	public String getTranslatorName(ModelResource modelResource) {
		return "file"; //$NON-NLS-1$
	}

}
