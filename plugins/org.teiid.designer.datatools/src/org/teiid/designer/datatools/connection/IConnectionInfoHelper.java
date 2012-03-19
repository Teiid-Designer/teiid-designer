/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.connection;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * Provides generic capabilities to insert and extract <code>IConnectionProfile</code> information into a
 * <code>ModelResource</code>.
 */
public interface IConnectionInfoHelper {

    public static final String PROFILE_NAME_KEY = "connectionProfileName"; //$NON-NLS-1$
    public static final String PROFILE_PROVIDER_ID_KEY = "connectionProfileProviderId"; //$NON-NLS-1$
    public static final String PROFILE_DESCRIPTION_KEY = "connectionProfileDescription"; //$NON-NLS-1$
    public static final String PROFILE_ID_KEY = "connectionProfileInstanceID"; //$NON-NLS-1$
    public static final String CONNECTION_PROFILE_NAMESPACE = "connectionProfile:"; //$NON-NLS-1$
    public static final String CONNECTION_NAMESPACE = "connection:"; //$NON-NLS-1$
    public static final String TRANSLATOR_NAMESPACE = "translator:"; //$NON-NLS-1$
    public static final String TRANSLATOR_NAME_KEY = "name"; //$NON-NLS-1$
    public static final String TRANSLATOR_TYPE_KEY = "type"; //$NON-NLS-1$
    public static final String CATEGORY_ID_KEY = "connectionProfileCategory"; //$NON-NLS-1$

    public abstract Properties getProperties( IConnectionProfile connectionProfile );

    public abstract boolean hasConnectionInfo( ModelResource modelResource );

    public abstract IConnectionProfile getConnectionProfile( ModelResource modelResource );

    public abstract ConnectionProfile createConnectionProfile( String name,
                                                               String description,
                                                               String id,
                                                               Properties props );

    public abstract Properties getCommonProfileProperties( IConnectionProfile profile );

    public abstract Properties getConnectionProperties( ModelResource modelResource ) throws ModelWorkspaceException;
    
    public abstract Properties getTranslatorProperties( ModelResource modelResource );

    public abstract String getTranslatorName( ModelResource modelResource );

    /**
     * @param modelResource
     * @return
     * @throws ModelWorkspaceException
     */
    Properties getProfileProperties( ModelResource modelResource ) throws ModelWorkspaceException;

    public abstract String generateUniqueConnectionJndiName( String name,
                                                             IPath path,
                                                             String uuid );

    public abstract String generateUniqueConnectionJndiName( ModelResource modelResource,
                                                             String uuid );

    public abstract String findMatchingDataSourceTypeName( Properties properties );

    public abstract String findMatchingDataSourceTypeName( ModelResource modelResource ) throws ModelWorkspaceException;

    public abstract String findMatchingDataSourceTypeName( Collection<String> matchableStrings,
                                                           Set<String> dataSourceTypeNames );

}
