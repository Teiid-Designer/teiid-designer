/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.printing;


public class PrintMargins {

    public double top;
    public double right;
    public double bottom;
    public double left;
    
    public PrintMargins() {
        
    }
    
    public void setTop( Double dMargin ) {
        top = dMargin.doubleValue();            
    }

    public void setRight( Double dMargin ) {
        right = dMargin.doubleValue();            
    }

    public void setBottom( Double dMargin ) {
        bottom = dMargin.doubleValue();            
    }

    public void setLeft( Double dMargin ) {
        left = dMargin.doubleValue();            
    }
    
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(super.toString());
        result.append("\n ----- PrintMargin -----"); //$NON-NLS-1$
        result.append("\n    TOP    =  " + top); //$NON-NLS-1$
        result.append("\n    RIGHT  =  " + right); //$NON-NLS-1$
        result.append("\n    BOTTOM =  " + bottom); //$NON-NLS-1$
        result.append("\n    LEFT   =  " + left + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
        return result.toString();
    }

}
