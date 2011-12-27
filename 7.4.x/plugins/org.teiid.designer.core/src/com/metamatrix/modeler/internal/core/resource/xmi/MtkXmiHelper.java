/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource.xmi;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;

/**
 * MtkXmiHelper
 */
public class MtkXmiHelper extends XMIHelperImpl {

    /**
     * Construct an instance of MtkXmiHelper.
     * 
     */
    public MtkXmiHelper() {
        super();
    }

    /**
     * Construct an instance of MtkXmiHelper.
     * @param resource
     */
    public MtkXmiHelper(XMLResource resource) {
        super(resource);
    }
    
    public EMap getPrefixesToURIs() {
        return prefixesToURIs;
    }

}
