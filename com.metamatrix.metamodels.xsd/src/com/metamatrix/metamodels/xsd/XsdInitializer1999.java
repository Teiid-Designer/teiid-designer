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

package com.metamatrix.metamodels.xsd;

import org.eclipse.xsd.util.XSDConstants;

/**
 * XSDInitializer1999
 */
public class XsdInitializer1999 extends AbstractXsdInitializer {

    /**
     * Construct an instance of XSDInitializer1999.
     * 
     */
    public XsdInitializer1999() {
        super();
    }
    
    /**
     * @see com.metamatrix.metamodels.xsd.AbstractXsdInitializer#getXsdNamespace()
     */
    @Override
    protected String getXsdNamespace() {
        return XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999;
    }


}
