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
	String TEIID_ODATA_CONNECTION_PROFILE_ID = "org.teiid.designer.datatools.profiles.ws.ODataConnectionProfile";  //$NON-NLS-1$

    
	String TEIID_CATEGORY = "org.teiid.designer.import.category"; //$NON-NLS-1$
    

    /*
     * Teiid Data Source property key.
     * 
     * Currently only EndPoint is the only property provided by Data Tools connection profile that matches up.
     */
     String DS_ENDPOINT = "wsdlURI"; //$NON-NLS-1$
     String DS_SECURITY_TYPE = SECURITY_TYPE_ID;
     /**
     * For OData
     * @since 8.2
     */
     String DS_REQUEST_TIMEOUT = "RequestTimeout"; //$NON-NLS-1$
     String DS_AUTH_USER_NAME = "AuthUserName"; //$NON-NLS-1$
     String DS_AUTH_PASSWORD = "AuthPassword"; //$NON-NLS-1$
     String DS_WS_SECURITY_CONFIG_URL = "WsSecurityConfigURL"; //$NON-NLS-1$
     String DS_WS_SECURITY_CONFIG_NAME = "WsSecurityConfigName"; //$NON-NLS-1$

     String SOAP_SERVICE_MODE = "DefaultServiceMode";  //$NON-NLS-1$
     String SOAP_BINDING = "DefaultBinding";  //$NON-NLS-1$

    /*
     * The Web Services Data Source object contains the following properties
     * 
     * connectionClass=org.my.custom.driver.Class
     * soapEndPoint=http://my.soap.endpoint.url
     * driverClassPath=org.my.first.jar;org.my.second.jar;
     * 
     * The only property that matches up with the teiid-connector-ws.jar definition is the soapEndPoint
     * 
     */
     String SOAP_ENDPOINT_KEY = "soapEndPoint"; //$NON-NLS-1$
     String CONNECTION_CLASS_KEY = "connectionClass"; //$NON-NLS-1$
     String DRIVER_CLASS_PATH_KEY = "driverClassPath"; //$NON-NLS-1$
     
     /**
      * Denotes the property key for the end point name in a WSDL. The end point
      * name is selected in the UI wizards and the URI and binding are properties
      * extracted from the WSDL using this name.
      */
     String END_POINT_NAME_PROP_ID = "EndPointName"; //$NON-NLS-1$
     
     /**
      * Denotes the property key for the end point address value(s) in a WSDL. In a connection
      * profile, only 1 end point is selected from the selection available in a WSDL.
      */
     String END_POINT_URI_PROP_ID = "EndPoint"; //$NON-NLS-1$
     
     /**
      * Denotes the property key for the WSDL URI. This is different to the WSDL endpoint
      * key in that a number of endpoints can appear in a WSDL accessible from a URI.
      * 
      * Also used incorrectly in 7.7.1 for the endpoint property key. 
      */
     String WSDL_URI_PROP_ID = "wsdlURI"; //$NON-NLS-1$
     
  	 String IS_PROXY_KEY = "isProxy"; //$NON-NLS-1$
 	 String PROXY_SERVER_HOST_KEY = "proxyServerHost"; //$NON-NLS-1$
 	 String PROXY_SERVER_PORT_KEY = "proxyServerPort"; //$NON-NLS-1$
     
     /**
      * Properties used for REST WS Request header info
      */
     String AUTHORIZATION_KEY = "Authorization"; //$NON-NLS-1$
     String ACCEPT_PROPERTY_KEY = "Accept"; //$NON-NLS-1$
     String ACCEPT_DEFAULT_VALUE = "application/xml"; //$NON-NLS-1$
     String CONTENT_TYPE_PROPERTY_KEY = "Content-Type"; //$NON-NLS-1$
     String CONTENT_TYPE_DEFAULT_VALUE = "application/xml"; //$NON-NLS-1$
     String CONTENT_TYPE_JSON_VALUE = "application/json"; //$NON-NLS-1$
     
     
     /**
      * Properties used for REST parameter info
      */
     String URI = "URI"; //$NON-NLS-1$
     String QUERY_STRING = "Query"; //$NON-NLS-1$
     String PARAMETER_MAP = "Parameter map"; //$NON-NLS-1$
     
     /**
      * Properties used for response content type
      */
     String RESPONSE_TYPE_PROPERTY_KEY = "responseType"; //$NON-NLS-1$
     String XML = "XML"; //$NON-NLS-1$
     String JSON = "JSON"; //$NON-NLS-1$
}
