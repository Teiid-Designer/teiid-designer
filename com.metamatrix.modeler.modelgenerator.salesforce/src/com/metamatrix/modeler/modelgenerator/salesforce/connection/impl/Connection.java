/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.connection.impl;

import java.net.URL;
import javax.xml.rpc.Stub;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.SimpleChain;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.handlers.SimpleSessionHandler;
import org.apache.axis.transport.http.CommonsHTTPSender;
import org.apache.axis.transport.http.HTTPTransport;
import com.metamatrix.modeler.modelgenerator.salesforce.connection.SalesforceConnection;
import com.sforce.soap.partner.CallOptions;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.SessionHeader;
import com.sforce.soap.partner.SforceServiceLocator;
import com.sforce.soap.partner.SoapBindingStub;

public class Connection implements SalesforceConnection {

    public SoapBindingStub binding;

    String getUserName() throws Exception {
        try {
            return binding.getUserInfo().getUserName();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.salesforce.connection.SalesforceConnection#getBinding()
     */
    public SoapBindingStub getBinding() {
        return binding;
    }

    public void login( String username,
                       String password,
                       URL connectionURL ) throws Exception {
        LoginResult loginResult = null;
        binding = null;
        SforceServiceLocator locator = new SforceServiceLocator();
        EngineConfiguration myConfig = getStaticConnectionConfig();
        locator.setEngineConfiguration(myConfig);
        locator.setEngine(new AxisClient(myConfig));
        try {
            if (null == connectionURL) {
                binding = (SoapBindingStub)locator.getSoap();
            } else {
                binding = (SoapBindingStub)locator.getSoap(connectionURL);
            }
            CallOptions co = new CallOptions();
            co.setClient("RedHat/MetaMatrix/"); //$NON-NLS-1$
            binding.setHeader("SforceService", "CallOptions", co); //$NON-NLS-1$ //$NON-NLS-2$
            loginResult = binding.login(username, password);
        } catch (Exception ex) {
            throw new Exception(ex);
        }

        // Reset the SOAP endpoint to the returned server URL
        binding._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, loginResult.getServerUrl());

        // Create a new session header object
        // add the session ID returned from the login
        SessionHeader sh = new SessionHeader();
        sh.setSessionId(loginResult.getSessionId());
        // Set the session header for subsequent call authentication
        binding.setHeader(new SforceServiceLocator().getServiceName().getNamespaceURI(), Messages.getString("Connection.0"), sh); //$NON-NLS-1$

        try {
            binding.getUserInfo();
        } catch (Exception ex) {
            System.out.println(Messages.getString("Connection.unexpected.error") + ex.getMessage()); //$NON-NLS-1$
            throw new Exception(ex);
        }
    }

    // Replace the non-static Axis connection with the static HTTP Commons connection.
    private EngineConfiguration getStaticConnectionConfig() {
        SimpleProvider clientConfig = new SimpleProvider();
        Handler sessionHandler = new SimpleSessionHandler();
        SimpleChain reqHandler = new SimpleChain();
        SimpleChain respHandler = new SimpleChain();
        reqHandler.addHandler(sessionHandler);
        respHandler.addHandler(sessionHandler);
        Handler pivot = new CommonsHTTPSender();
        Handler transport = new SimpleTargetedChain(reqHandler, pivot, respHandler);
        clientConfig.deployTransport(HTTPTransport.DEFAULT_TRANSPORT_NAME, transport);
        return clientConfig;
    }
}
