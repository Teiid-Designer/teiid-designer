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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.DatatoolsPlugin;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;


/**
 * @since 8.0
 */
public class XmlFileConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider {

    public final static String FILE_CLASSNAME = "class-name"; //$NON-NLS-1$
    public final static String FILE_CONNECTION_FACTORY = "org.teiid.resource.adapter.file.FileManagedConnectionFactory"; //$NON-NLS-1$

    @Override
	public String getDataSourcePasswordPropertyKey() {
		// Flat file connection profile doesn't use password, but need one to prevent NPE
		return null;
	}

	@Override
	public String getDataSourceType() {
	    return ModelerCore.getTeiidDataTypeManagerService().getDataSourceType(DataSourceTypes.FILE);
	}

	@Override
	public String getPasswordPropertyKey() {
		// Flat file connection profile doesn't use password, but need one to prevent NPE
		return "bogus_xxxx"; //$NON-NLS-1$
	}

	@Override
	public Properties getTeiidRelatedProperties(IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties props = connectionProfile.getBaseProperties();
        
        String fileUrl = props.getProperty(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID);
        if( fileUrl != null ) {
			IPath fullPath = new Path(fileUrl);
			String directoryUrl = fullPath.removeLastSegments(1).toString();
        	connectionProps.setProperty(IXmlProfileConstants.TEIID_PARENT_DIRECTORY_KEY, directoryUrl);
        }
        connectionProps.setProperty(FILE_CLASSNAME, FILE_CONNECTION_FACTORY);

        return connectionProps;
	}

	@Override
	public void setConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
		Properties connectionProps = getCommonProfileProperties(connectionProfile);

		Properties props = connectionProfile.getBaseProperties();

		String result = props.getProperty(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID, result);
		}


		getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
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
		
		Properties connProps = new Properties();
		// Search for "URL" value
		
		String url = (String)modelProps.get(IXmlProfileConstants.LOCAL_FILE_PATH_PROP_ID);
		if( url != null ) {
			connProps.put(IXmlProfileConstants.TEIID_PARENT_DIRECTORY_KEY, url);
			connProps.put(FILE_CLASSNAME, FILE_CONNECTION_FACTORY);
		}
		
		return connProps;
	}

	@Override
	public String getTranslatorName(ModelResource modelResource) {
		return "file"; //$NON-NLS-1$
	}

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return false;
	}
}
