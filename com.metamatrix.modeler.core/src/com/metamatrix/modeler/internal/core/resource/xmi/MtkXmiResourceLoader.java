/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
