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

package com.metamatrix.modeler.internal.xsd.ui.editor;

public class InclusiveInteger implements Cloneable {
    public boolean isInclusive;
    public int     value;

    //
    // Constructors:
    //
    public InclusiveInteger() {}

    public InclusiveInteger(InclusiveInteger ii) {
        copyValuesOf(ii);
    }

    public InclusiveInteger(int value, boolean isInclusive) {
        this.value = value;
        this.isInclusive = isInclusive;
    }

    //
    // Data Methods:
    //
    public void copyValuesOf(InclusiveInteger ii) {
        if (ii != null) {
            this.value = ii.value;
            this.isInclusive = ii.isInclusive;
        } else {
            reset();
        } // endif
    }

    public void reset() {
        this.value = 0;
        this.isInclusive = false;
    }

    //
    // Utility methods:
    //
    public InclusiveInteger cloneValue() {
        try {
            return (InclusiveInteger) super.clone();
        } catch (CloneNotSupportedException e) {
            // should never happen, since we implement cloneable
            return new InclusiveInteger(this);
        } // endtry
    }
    
    //
    // Overrides:
    //
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;

        if (obj instanceof InclusiveInteger) {
            InclusiveInteger ii = (InclusiveInteger) obj;
            return value == ii.value 
                && isInclusive == ii.isInclusive;
        } // endif
        
        return false;
    }

    @Override
    public String toString() {
        return "InclusiveInteger: "+value+"; inclusive="+isInclusive;  //$NON-NLS-1$//$NON-NLS-2$
    }
}
