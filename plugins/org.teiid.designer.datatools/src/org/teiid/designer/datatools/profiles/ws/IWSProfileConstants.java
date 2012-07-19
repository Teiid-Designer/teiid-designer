/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.ws;

import org.teiid.designer.ui.common.ICredentialsCommon;

/**
 * @since 8.0
 */
public interface IWSProfileConstants extends ICredentialsCommon {

	String TEIID_WS_CONNECTION_PROFILE_ID = "org.teiid.designer.datatools.profiles.ws.WSConnectionProfile";  //$NON-NLS-1$

    String TEIID_CATEGORY = "org.teiid.designer.import.category"; //$NON-NLS-1$
    
    String URL_PROP_ID = "EndPoint"; //$NON-NLS-1$
    String WSDL_URI_PROP_ID = "wsdlURI"; //$NON-NLS-1$
}
