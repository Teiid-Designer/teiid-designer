/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import org.junit.Test;
import org.teiid.designer.runtime.ServerUtils;

/**
 * 
 */
public class ServerUtilsTest {

    @Test
    public void shouldBeValidUrl() {
        ServerUtils.validateServerUrl("mm://hostA:12345");
    }

    @Test
    public void shouldBeValidSecureUrl() {
        ServerUtils.validateServerUrl("mms://hostA:12345");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeValidUrlWithInvalidProtocol() {
        ServerUtils.validateServerUrl("m://hostA:12345");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeValidUrlWithInvalidHostName_1() {
        ServerUtils.validateServerUrl("mm://host@A:12345");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeValidUrlWithInvalidHostName_2() {
        ServerUtils.validateServerUrl("mm://host[A:12345");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeValidUrlWithInvalidHostName_3() {
        ServerUtils.validateServerUrl("mm://host*A:12345");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeValidUrlWithInvalidPortNumber() {
        ServerUtils.validateServerUrl("mm://hostA:1234a");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeValidUrlWithTooLargePortNumber() {
        ServerUtils.validateServerUrl("mm://hostA:1234567");
    }
}
