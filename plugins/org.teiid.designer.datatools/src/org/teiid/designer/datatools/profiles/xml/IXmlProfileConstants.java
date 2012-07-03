/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.xml;

import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;

import com.metamatrix.ui.ICredentialsCommon;

public interface IXmlProfileConstants extends ICredentialsCommon {
	String FILE_URL_CONNECTION_PROFILE_ID = "org.teiid.designer.datatools.profiles.xml.fileurl"; //$NON-NLS-1$
	String LOCAL_FILE_CONNECTION_PROFILE_ID = "org.teiid.designer.datatools.profiles.xml.localfile"; //$NON-NLS-1$
	
    String TEIID_CATEGORY = "org.teiid.designer.import.category"; //$NON-NLS-1$
    String URL_PROP_ID = "URL"; //$NON-NLS-1$
    String LOCAL_FILE_PATH_PROP_ID = "LocalFilePath"; //$NON-NLS-1$
    String TEIID_PARENT_DIRECTORY_KEY = "ParentDirectory"; //$NON-NLS-1$
    
    String WS_ENDPOINT_KEY = IWSProfileConstants.URL_PROP_ID;

}
