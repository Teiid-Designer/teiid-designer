/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.vdb.runtime;

import java.util.Map;
import org.teiid.core.TeiidRuntimeException;

/**
 */
public final class URIResource {

    private static Map resources = null;

    static {
        try {
            URIResourceReader reader = new URIResourceReader();
            resources = reader.getURIResources();

        } catch (Exception e) {
            // TODO: I18n stuff
            String msg = "URIResource.Resource_not_found"; //, new Object[] {URIResourceReader.RESOURCE_NAME}); //$NON-NLS-1$
            throw new TeiidRuntimeException(e, msg);
        }
    }

    public boolean isXMLDocument( String uri ) {
        URIModelResource r = getURIModelResource(uri);
        if (r != null) {
            return r.isXMLDocType();
        }
        return false;
    }

    public String getAuthorizationLevel( String uri ) {
        URIModelResource r = getURIModelResource(uri);
        if (r != null) {
            return r.getAuthLevel();
        }
        return URIModelResource.AUTH_LEVEL.ALL;
    }

    public boolean isPhysicalBindingAllowed( String uri ) {
        URIModelResource r = getURIModelResource(uri);
        if (r != null) {
            return r.isPhysicalBindingAllowed();
        }
        return false;
    }

    private URIModelResource getURIModelResource( String uri ) {
        if (resources.containsKey(uri)) {
            return (URIModelResource)resources.get(uri);
        }
        return null;

    }
}
