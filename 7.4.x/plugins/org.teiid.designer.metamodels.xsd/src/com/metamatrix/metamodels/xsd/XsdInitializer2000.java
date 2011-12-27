/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd;

import org.eclipse.xsd.util.XSDConstants;

/**
 * XSDInitializer1999
 */
public class XsdInitializer2000 extends AbstractXsdInitializer {

    /**
     * Construct an instance of XSDInitializer1999.
     * 
     */
    public XsdInitializer2000() {
        super();
    }
    
    /**
     * @see com.metamatrix.metamodels.xsd.AbstractXsdInitializer#getXsdNamespace()
     */
    @Override
    protected String getXsdNamespace() {
        return XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10;
    }


}
