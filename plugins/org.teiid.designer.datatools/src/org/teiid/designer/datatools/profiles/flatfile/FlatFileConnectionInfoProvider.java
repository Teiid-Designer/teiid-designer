package org.teiid.designer.datatools.profiles.flatfile;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.DatatoolsPlugin;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionConstants;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

public class FlatFileConnectionInfoProvider  extends ConnectionInfoHelper implements IConnectionInfoProvider {

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
        
        Properties baseProps = connectionProfile.getBaseProperties();
        
        if( baseProps.getProperty(IFlatFileProfileConstants.HOME_URL) != null ) {
        	connectionProps.put(IFlatFileProfileConstants.TEIID_PARENT_DIRECTORY_KEY, baseProps.get(IFlatFileProfileConstants.HOME_URL));
        } else if( baseProps.getProperty(IFlatFileProfileConstants.HOME_KEY) != null ) {
        	connectionProps.put(IFlatFileProfileConstants.TEIID_PARENT_DIRECTORY_KEY, baseProps.get(IFlatFileProfileConstants.HOME_KEY));
        } 

        return connectionProps;
	}

	@Override
	public void setConnectionInfo(ModelResource modelResource,
			IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
		Properties connectionProps = getCommonProfileProperties(connectionProfile);

		Properties props = connectionProfile.getBaseProperties();

		String result = props.getProperty(IFlatFileProfileConstants.HOME_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE
					+ IFlatFileProfileConstants.HOME_URL, result);
		}

		result = props.getProperty(IFlatFileProfileConstants.DELIMTYPE_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE
					+ IFlatFileProfileConstants.DELIMETER, result);
		}

		result = props.getProperty(IFlatFileProfileConstants.CHARSET_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE
					+ IFlatFileProfileConstants.CHARSET, result);
		}
		
		result = props.getProperty(IFlatFileProfileConstants.INCLCOLUMNNAME_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE
					+ IFlatFileProfileConstants.FIRST_LINE_COLUMN_NAME, result);
		}
		
		result = props.getProperty(IFlatFileProfileConstants.INCLTYPELINE_KEY);
			if (null != result) {
				connectionProps.setProperty(CONNECTION_NAMESPACE
						+ IFlatFileProfileConstants.SECOND_LINE_DATATYPE, result);
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
		
		String result = flatFileProps.getProperty(IFlatFileProfileConstants.HOME_URL);
		if( result != null ) {
			baseProps.put(IFlatFileProfileConstants.HOME_KEY, result);
		}
		result = flatFileProps.getProperty(IFlatFileProfileConstants.CHARSET);
		if( result != null ) {
			baseProps.put(IFlatFileProfileConstants.CHARSET_KEY, result);
		}
		result = flatFileProps.getProperty(IFlatFileProfileConstants.DELIMETER);
		if( result != null ) {
			baseProps.put(IFlatFileProfileConstants.DELIMTYPE_KEY, result);
		}
		result = flatFileProps.getProperty(IFlatFileProfileConstants.FIRST_LINE_COLUMN_NAME);
		if( result != null ) {
			baseProps.put(IFlatFileProfileConstants.INCLCOLUMNNAME_KEY, result);
		}
		result = flatFileProps.getProperty(IFlatFileProfileConstants.SECOND_LINE_DATATYPE);
		if( result != null ) {
			baseProps.put(IFlatFileProfileConstants.INCLTYPELINE_KEY, result);
		}
		
		return profile;
	}

	@Override
	public Properties getConnectionProperties(ModelResource modelResource)
			throws ModelWorkspaceException {
		Properties modelProps = super.getConnectionProperties(modelResource);
		
		Properties connProps = new Properties();
		// Search for "HOME" value
		
		String home = modelProps.getProperty(IFlatFileProfileConstants.HOME_URL);
		if( home != null ) {
			connProps.put(IFlatFileProfileConstants.TEIID_PARENT_DIRECTORY_KEY, home);
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
