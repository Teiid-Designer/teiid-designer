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

package com.metamatrix.ui.internal.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.metamatrix.core.util.I18nUtil;

/**
 * IntegerSpinner
 */
public class IntegerSpinner extends Spinner {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(IntegerSpinner.class);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>IntegerSpinner</code> using the given parameters and sets the initial value to 
     * the min value.
     * @param theParent the parent containter
     * @param theMin the minimum value allowed
     * @param theMax the maximum value allowed
    */
    public IntegerSpinner(Composite theParent,
                          int theMin, 
                          int theMax) {
        super(theParent);

        if (theMin <= theMax) {
            configureIntegerSpinner(theMin, theMax);
            handleUpSelected();
        } else {
            throw new IllegalStateException(Util.getString(PREFIX + "invalidMinMaxValues", //$NON-NLS-1$
                                                           new Object[] {new Integer(theMin),
                                                                         new Integer(theMax)}));
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private void configureIntegerSpinner(int theMin,
                                         int theMax) {
        List values = new ArrayList();
        for (int i = theMin; i <= theMax; ++i ) {
            values.add(new Integer(i));
        }
        setPossibleValues(values);
    }
    
    /**
     * Gets the current value as an integer.
     * @return the current integer value
     * @throws IllegalStateException if spinner was not constructed with a min and max int values
     */
    public int getIntegerValue() {
        return ((Integer)getValue()).intValue();
    }
    
    public void setValue(int theValue) {
        setValue(new Integer(theValue));
    }
    
}
