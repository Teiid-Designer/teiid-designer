/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.jbossds;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;


/**
 * Connection Provider for the JBoss DataSource Connection Profile
 * @since 8.3
 */
public class JBossDsConnectionInfoProvider  extends ConnectionInfoHelper implements IConnectionInfoProvider {

    @Override
	public String getDataSourcePasswordPropertyKey() {
		return null;
	}

	@Override
	public String getDataSourceType() {
	    return "JBossDs"; //$NON-NLS-1$
	}

	@Override
	public String getPasswordPropertyKey() {
		return "bogus_xxxx"; //$NON-NLS-1$
	}

	@Override
	public Properties getTeiidRelatedProperties(IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties props = connectionProfile.getBaseProperties();
        
        String jndi = props.getProperty(IJBossDsProfileConstants.JNDI_PROP_ID);
        if (null != jndi) {
            connectionProps.setProperty(IJBossDsProfileConstants.JNDI_PROP_ID, jndi);
        }

        return connectionProps;
	}

	@Override
	public void setConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
		Properties connectionProps = getCommonProfileProperties(connectionProfile);

		Properties props = connectionProfile.getBaseProperties();

		String result = props.getProperty(IJBossDsProfileConstants.JNDI_PROP_ID);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + IJBossDsProfileConstants.JNDI_PROP_ID, result);
		}

		result = props.getProperty(IJBossDsProfileConstants.TRANSLATOR_PROP_ID);
		if (null != result) {
			connectionProps.setProperty(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, result);
		}
		
		getHelper().removeProperties(modelResource,	CONNECTION_PROFILE_NAMESPACE);
		getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
		getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);


		getHelper().setProperties(modelResource, connectionProps);
	}

	@Override
	public Properties getCommonProfileProperties(IConnectionProfile profile) {
		return super.getCommonProfileProperties(profile);
	}

	@Override
	public String getTranslatorName(ModelResource modelResource) {
		String translatorName = null;
		Properties translatorProps = null;
		try {
			translatorProps = getHelper().getProperties(modelResource, TRANSLATOR_NAMESPACE);
		} catch (ModelWorkspaceException ex) {
		}
		if(translatorProps!=null && !translatorProps.isEmpty()) {
			translatorName = translatorProps.getProperty(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY);
		}
		
		if(translatorName!=null && !translatorName.isEmpty()) return translatorName;
		
		return "unknown"; //$NON-NLS-1$
	}

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return false;
	}
}
