/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.connection.impl;

import java.net.URL;
import javax.xml.ws.BindingProvider;
import org.teiid.logging.LogConstants;
import org.teiid.logging.LogManager;
import com.metamatrix.modeler.modelgenerator.salesforce.connection.SalesforceConnection;
import com.sforce.soap.partner.CallOptions;
import com.sforce.soap.partner.InvalidIdFault;
import com.sforce.soap.partner.LoginFault;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.SessionHeader;
import com.sforce.soap.partner.SforceService;
import com.sforce.soap.partner.Soap;
import com.sforce.soap.partner.UnexpectedErrorFault;

public class Connection implements SalesforceConnection {

    private SforceService sfService;
    private Soap sfSoap;
    private SessionHeader sh;
    private CallOptions co;

    String getUserName() throws Exception {
        try {
            return sfSoap.getUserInfo().getUserName();
        } catch (UnexpectedErrorFault e) {
            throw new Exception(e);
        }
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.salesforce.connection.SalesforceConnection#getBinding()
     */
    @Override
    public Soap getBinding() {
        return sfSoap;
    }

    @Override
    public void login( String username,
                       String password,
                       URL url ) throws Exception {
        if (!isValid()) {
            LoginResult loginResult = null;
            sfSoap = null;
            sfService = null;
            co = new CallOptions();
            // This value identifies Teiid as a SF certified solution.
            // It was provided by SF and should not be changed.
            co.setClient("RedHat/MetaMatrix/"); //$NON-NLS-1$

            if (url == null) {
                throw new Exception("SalesForce URL is not specified, please provide a valid URL"); //$NON-NLS-1$
            }

            try {
                sfService = new SforceService();
                sh = new SessionHeader();

                // Session Id must be passed in soapHeader - add the handler
                sfService.setHandlerResolver(new SalesforceHandlerResolver(sh));

                sfSoap = sfService.getSoap();
                ((BindingProvider)sfSoap).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toExternalForm());
                loginResult = sfSoap.login(username, password);

                // Set the SessionId after login, for subsequent calls
                sh.setSessionId(loginResult.getSessionId());
            } catch (LoginFault e) {
                throw new Exception(e);
            } catch (InvalidIdFault e) {
                throw new Exception(e);
            } catch (com.sforce.soap.partner.UnexpectedErrorFault e) {
                throw new Exception(e);
            }
            LogManager.logTrace(LogConstants.CTX_CONNECTOR, "Login was successful for username " + username); //$NON-NLS-1$

            // Reset the SOAP endpoint to the returned server URL
            ((BindingProvider)sfSoap).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                                              loginResult.getServerUrl());
            // or maybe org.apache.cxf.message.Message.ENDPOINT_ADDRESS
            ((BindingProvider)sfSoap).getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
            // Set the timeout.
            // ((BindingProvider)sfSoap).getRequestContext().put(JAXWSProperties.CONNECT_TIMEOUT, timeout);

            // Test the connection.
            try {
                sfSoap.getUserInfo();
            } catch (com.sforce.soap.partner.UnexpectedErrorFault e) {
                throw new Exception(e);
            }
        }
    }

    public boolean isValid() {
        boolean result = true;
        if (sfSoap == null) {
            result = false;
        } else {
            try {
                sfSoap.getServerTimestamp();
            } catch (Throwable t) {
                LogManager.logTrace(LogConstants.CTX_CONNECTOR, "Caught Throwable in isAlive", t); //$NON-NLS-1$
                result = false;
            }
        }
        return result;
    }

}
