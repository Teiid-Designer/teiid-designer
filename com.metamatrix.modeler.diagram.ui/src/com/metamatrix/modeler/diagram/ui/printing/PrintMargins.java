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
