/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.teiidadmin;

import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;

/**
 * 
 */
public interface ITeiidAdminProfileConstants {

    String PASSWORD_PROP_ID = IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID; //$NON-NLS-1$
    String USERNAME_PROP_ID = IJDBCDriverDefinitionConstants.USERNAME_PROP_ID; //$NON-NLS-1$
    String URL_PROP_ID = IJDBCDriverDefinitionConstants.URL_PROP_ID; //$NON-NLS-1$
    String TEIID_ADMIN_CATEGORY = "org.teiid.designer.admin.category";
}
