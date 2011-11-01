/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.extension;

import org.teiid.designer.core.extension.AbstractMetaclassNameProvider;
import com.metamatrix.metamodels.webservice.WebServicePackage;

/**
 * 
 */
public class WebServiceExtendableClassnameProvider extends AbstractMetaclassNameProvider {

    public WebServiceExtendableClassnameProvider() {
        super(WebServicePackage.eNS_URI);
    }
}
