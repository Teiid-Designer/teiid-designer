/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.common.vdb.runtime;

/**
 */
public class BasicURIModelResource implements URIModelResource {

    private String uri = null;
    private String authlevel;
    private boolean isXMLDocType = false;
    private boolean isPhysicalBindingAllowed = false;

    protected BasicURIModelResource( String uri ) {
        this.uri = uri;

    }

    public String getURI() {
        return this.uri;
    }

    public String getAuthLevel() {
        return this.authlevel;
    }

    public boolean isXMLDocType() {
        return this.isXMLDocType;
    }

    public boolean isPhysicalBindingAllowed() {
        return this.isPhysicalBindingAllowed;
    }

    protected void setAuthLevel( String level ) {
        this.authlevel = level;
    }

    protected void setIsXMLDocType( boolean isDocType ) {
        this.isXMLDocType = isDocType;
    }

    protected void setIsPhysicalBindingAllowed( boolean allowsBinding ) {
        this.isPhysicalBindingAllowed = allowsBinding;
    }

}
