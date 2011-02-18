/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.print;

/**
 * Printable
 */
public class Printable implements IPrintable {

    private Object oPrintable;

    /**
     * Construct an instance of Printable.
     * 
     */
    public Printable( Object oPrintable ) {
        super();
        this.oPrintable = oPrintable;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.print.IPrintable#getObject()
     */
    public Object getObject() {
        return oPrintable;
    }

}
