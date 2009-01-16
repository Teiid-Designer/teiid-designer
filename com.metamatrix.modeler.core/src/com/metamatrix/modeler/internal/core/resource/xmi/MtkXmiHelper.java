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
