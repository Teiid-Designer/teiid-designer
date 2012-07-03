/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.soap.provider;

import javax.xml.transform.Source;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

@ServiceMode(value = javax.xml.ws.Service.Mode.PAYLOAD)
@WebServiceProvider(targetNamespace = "${targetNamespace}", portName = "${portName}", serviceName = "${serviceName}", wsdlLocation = "WEB-INF/wsdl/${wsdlFileName}")
public class ${className} extends TeiidWSProvider implements
		javax.xml.ws.Provider<Source> {
	
	public javax.xml.transform.Source invoke(Source request) {
	
		return super.invoke(request);
	}
}
