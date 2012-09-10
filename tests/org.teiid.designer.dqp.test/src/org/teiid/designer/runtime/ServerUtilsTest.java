/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import java.net.MalformedURLException;

import org.junit.Test;
import org.teiid.designer.runtime.TeiidServerUtils;

/**
 * 
 */
public class ServerUtilsTest {

    @Test
    public void shouldBeValidUrl() throws MalformedURLException {
        TeiidServerUtils.validateServerUrl("mm://hostA:12345");
    }

    @Test
    public void shouldBeValidSecureUrl() throws MalformedURLException {
        TeiidServerUtils.validateServerUrl("mms://hostA:12345");
    }

    @Test( expected = MalformedURLException.class )
    public void shouldNotBeValidUrlWithInvalidProtocol() throws MalformedURLException {
        TeiidServerUtils.validateServerUrl("m://hostA:12345");
    }

    @Test
    public void shouldBeValidUrlWithInvalidHostName_1() throws MalformedURLException {
        TeiidServerUtils.validateServerUrl("mm://host@A:12345");
    }

    @Test
    public void shouldBeValidUrlWithInvalidHostName_2() throws MalformedURLException {
        TeiidServerUtils.validateServerUrl("mm://host[A:12345");
    }

    @Test
    public void shouldBeValidUrlWithInvalidHostName_3() throws MalformedURLException {
        TeiidServerUtils.validateServerUrl("mm://host*A:12345");
    }

    @Test( expected = MalformedURLException.class )
    public void shouldNotBeValidUrlWithInvalidPortNumber() throws MalformedURLException {
        TeiidServerUtils.validateServerUrl("mm://hostA:1234a");
    }

    @Test( expected = MalformedURLException.class )
    public void shouldNotBeValidUrlWithTooLargePortNumber() throws MalformedURLException {
        TeiidServerUtils.validateServerUrl("mm://hostA:1234567");
    }
}
