package org.teiid.designer.datatools.profiles.flatfile;

import java.io.File;
import java.util.Properties;
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
public class FlatFileConnectionInfoProvider  extends ConnectionInfoHelper implements IConnectionInfoProvider {

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
	public Properties getTeiidRelatedProperties(
			IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties baseProps = connectionProfile.getBaseProperties();
        
        if( baseProps.getProperty(IFlatFileProfileConstants.HOME_URL) != null ) {
        	connectionProps.put(IFlatFileProfileConstants.TEIID_PARENT_DIRECTORY_KEY, baseProps.get(IFlatFileProfileConstants.HOME_URL));
        } else if( baseProps.getProperty(IFlatFileProfileConstants.HOME_KEY) != null ) {
        	connectionProps.put(IFlatFileProfileConstants.TEIID_PARENT_DIRECTORY_KEY, baseProps.get(IFlatFileProfileConstants.HOME_KEY));
        } else if( baseProps.getProperty(IFlatFileProfileConstants.ODA_URI_KEY) != null ) {
        	String uri = baseProps.getProperty(IFlatFileProfileConstants.ODA_URI_KEY);
        	// Get Parent Folder path using file path
        	String parentPath = getFileParentDir(uri);
        	if(parentPath!=null) {
            	connectionProps.put(IFlatFileProfileConstants.TEIID_PARENT_DIRECTORY_KEY, parentPath);
        	}
        }

        connectionProps.setProperty(FILE_CLASSNAME, FILE_CONNECTION_FACTORY);
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
		} else {
			result = props.getProperty(IFlatFileProfileConstants.ODA_URI_KEY);
			if(null != result) {
	        	// Get Parent Folder path using file path
	        	String parentPath = getFileParentDir(result);
	        	if(parentPath!=null) {
					connectionProps.setProperty(CONNECTION_NAMESPACE
							+ IFlatFileProfileConstants.HOME_URL, parentPath);
	        	}
			}
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

	private String getFileParentDir(String filePath) {
		String dirPath = null;
		if(filePath != null) {
			File aFile = new File(filePath);
			if(aFile.exists() && aFile.isFile()) {
				File dirFile = aFile.getParentFile();
				if(dirFile!=null && dirFile.exists() && dirFile.isDirectory()) {
					dirPath = dirFile.getAbsolutePath();
				}
			}
		}
		return dirPath;
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
