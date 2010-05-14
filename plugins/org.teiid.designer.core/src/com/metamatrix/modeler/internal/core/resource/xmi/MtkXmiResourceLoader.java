/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource.xmi;

import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.xml.sax.helpers.DefaultHandler;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;


/**
 * @author Lance Phillips
 *
 * @since 3.1
 */
public class MtkXmiResourceLoader extends XMILoadImpl {
    private final Container container;
    /**
     * Constructor for MtkXMIResourceLoader.
     * @param resource
     * @param is
     * @param options
     * @param helper
     */
    public MtkXmiResourceLoader(XMLHelper helper, Container container) {
        super(helper);
        if (container == null) {
            final String msg = ModelerCore.Util.getString("MtkXmiResourceLoader.The_Container_may_not_be_null"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        this.container = container;
    }

    @Override
    protected DefaultHandler makeDefaultHandler()
    {
        return new MtkXmiHandler((XMIResource)resource, helper, this.container, options);
    }



}
