/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.connection;

import java.util.Properties;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.ICategory;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.DatatoolsPlugin;

import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * Create instances of <code>IConnectionInfoProvider</code>.
 */
public class ConnectionInfoProviderFactory {

    private static final String EXTENSION = "org.teiid.designer.datatools.ConnectionInfoProvider"; //$NON-NLS-1$
    private static final String PROFILE_ATTRIBUTE = "profile"; //$NON-NLS-1$
    private static final String CATEGORY_ATTRIBUTE = "category"; //$NON-NLS-1$
    private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
    private IConnectionInfoHelper helper = new ConnectionInfoHelper();

    public IConnectionInfoProvider getProvider( IConnectionProfile profile ) {
        String instanceID = profile.getProviderId();
        IConnectionInfoProvider result = getProviderFromProfileID(instanceID);
        if (null == result) {
            IConnectionProfile parent = profile.getParentProfile();
            if (null != parent) {
                return getProvider(parent);
            }
        }
        if (null == result) {
            result = getProvider(profile.getCategory());
        }
        return result;
    }

    /**
     * @param profileID the id of a <code>IConnectionProfile</code>
     * @return the <code>IConnectionInfoProvider</code> for the <code>IConnectionProfile</code>. Can be null if there is no
     *         <code>IConnectionInfoProvider</code> registered for that profileID.
     */
    public IConnectionInfoProvider getProviderFromProfileID( String instanceID ) {
        IConnectionInfoProvider helper = null;
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION);

        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement element = extensions[i];
            if (instanceID.equals(element.getAttribute(PROFILE_ATTRIBUTE))) {
                try {
                    helper = (IConnectionInfoProvider)element.createExecutableExtension(CLASS_ATTRIBUTE);
                    break;
                } catch (Exception e) {
                	DatatoolsPlugin.Util.log(IStatus.ERROR, e.getMessage());
                }
            }
        }
        return helper;
    }

    private IConnectionInfoProvider getProvider( ICategory category ) {
        String instanceID = category.getId();
        IConnectionInfoProvider result = getProviderFromCategoryID(instanceID);
        if (null == result) {
            ICategory parent = category.getParent();
            if (null != parent) {
                return getProvider(parent);
            }
        }
        return result;
    }

    private IConnectionInfoProvider getProviderFromCategoryID( String categoryId ) {
        IConnectionInfoProvider helper = null;
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION);

        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement element = extensions[i];
            if (categoryId.equals(element.getAttribute(CATEGORY_ATTRIBUTE))) {
                try {
                    helper = (IConnectionInfoProvider)element.createExecutableExtension(CLASS_ATTRIBUTE);
                    break;
                } catch (Exception e) {
                	DatatoolsPlugin.Util.log(IStatus.ERROR, e.getMessage());
                }
            }
        }
        return helper;
    }

    /**
     * @param modeResource a <code>ModelResource</code> with connection information.
     * @return the <code>IConnectionInfoProvider</code> for the <code>IConnectionProfile</code>. Can be null if there is no
     *         <code>IConnectionInfoProvider</code> registered for that profileID.
     */
    public IConnectionInfoProvider getProvider( ModelResource modelResource ) throws Exception {
        IConnectionInfoProvider result = null;
        if (!helper.hasConnectionInfo(modelResource)) {
            throw new Exception(DatatoolsPlugin.Util.getString("ConnectionInfoProviderFactory.noConnectionInfoInModel", modelResource.getItemName())); //$NON-NLS-1$
        }

        IConnectionProfile profile = helper.getConnectionProfile(modelResource);
        if (null == profile) {
            throw new Exception(DatatoolsPlugin.Util.getString("ConnectionInfoProviderFactory.noConnectionProfileInModel", modelResource.getItemName())); //$NON-NLS-1$
        }
        result = getProvider(profile);

        if (null == result) {
            ICategory category = profile.getCategory();
            if (null == category) {
                throw new Exception(DatatoolsPlugin.Util.getString("ConnectionInfoProviderFactory.noCategoryForProfile", profile.getName())); //$NON-NLS-1$
            }
            result = getProvider(category);
        }
        return result;
    }

    public IConnectionInfoProvider getProvider( Properties props ) {
        IConnectionInfoProvider result = null;
        String providerID = props.getProperty(IConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE
                                              + IConnectionInfoHelper.PROFILE_PROVIDER_ID_KEY);
        result = getProviderFromProfileID(providerID);

        if (null == result) {
            String categoryID = props.getProperty(IConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE
                                                  + IConnectionInfoHelper.CATEGORY_ID_KEY);
            result = getProviderFromCategoryID(categoryID);
        }
        return result;
    }
}
