/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.connection;

import java.net.URL;

import com.sforce.soap.partner.SoapBindingStub;

public interface SalesforceConnection {

	public abstract SoapBindingStub getBinding();

	public void login(String username, String password, URL connectionURL) throws Exception;

}
