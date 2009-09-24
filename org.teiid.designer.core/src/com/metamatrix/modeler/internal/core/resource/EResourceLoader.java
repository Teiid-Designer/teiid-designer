/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource;

import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @since 4.3
 */
public class EResourceLoader extends XMILoadImpl {

    // Store reference to the helper which stores the prefixesToNamespaceMap.  The
    // XMLLoad parent class will eventually null it's reference out so we need to
    // keep our own reference.
    private XMLHelper xmlHelper;

    /**
     * Constructor for EResourceLoader.
     */
    public EResourceLoader(final XMLHelper helper) {
        super(helper);
        this.xmlHelper = helper;
    }

    @Override
    protected DefaultHandler makeDefaultHandler() {
        return new EResourceXmiHandler((XMIResource)resource, helper, options);
    }

    protected XMLHelper getXMLHelper() {
        return this.xmlHelper;
    }

}
