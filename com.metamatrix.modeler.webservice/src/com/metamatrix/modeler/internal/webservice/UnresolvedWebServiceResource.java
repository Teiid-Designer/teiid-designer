/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import java.io.File;
import java.io.InputStream;


/** 
 * @since 4.2
 */
public class UnresolvedWebServiceResource extends AbstractWebServiceResource {

    /** 
     * 
     * @since 4.2
     */
    public UnresolvedWebServiceResource( final String namespace ) {
        super(namespace,null);
    }

    /** 
     * @see com.metamatrix.modeler.internal.webservice.AbstractWebServiceResource#exists()
     * @since 4.2
     */
    @Override
    protected boolean exists() {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.internal.webservice.AbstractWebServiceResource#doGetFile()
     * @since 4.2
     */
    @Override
    protected File doGetFile() {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.internal.webservice.AbstractWebServiceResource#doGetRawInputStream()
     * @since 4.2
     */
    @Override
    protected InputStream doGetRawInputStream() throws Exception {
        return null;
    }

}
