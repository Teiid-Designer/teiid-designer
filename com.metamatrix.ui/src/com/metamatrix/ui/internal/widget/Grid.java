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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * A {@link Panel} that enforces the use of the default {@link GridData} and {@link GridLayout}, allowing for convenience methods
 * for working with these constructs.
 * 
 * @since 5.0.1
 */
public class Grid extends Panel {

    // ===========================================================================================================================
    // Constructors

    /**
     * @param parent
     * @since 5.0.1
     */
    public Grid(Composite parent) {
        super(parent);
    }

    /**
     * @param parent
     * @param style
     * @since 5.0.1
     */
    public Grid(Composite parent,
                int style) {
        super(parent, style);
    }

    /**
     * @param parent
     * @param style
     * @param columnCount
     * @since 5.0.1
     */
    public Grid(Composite parent,
                int style,
                int columnCount) {
        super(parent, style, columnCount);
    }

    // ===========================================================================================================================
    // Methods

    /**
     * @since 5.0.1
     */
    public GridData getGridData() {
        return (GridData)getLayoutData();
    }

    /**
     * @since 5.0.1
     */
    public GridLayout getGridLayout() {
        return (GridLayout)getLayout();
    }

    /**
     * @see org.eclipse.swt.widgets.Composite#setLayout(org.eclipse.swt.widgets.Layout)
     * @since 5.0.1
     */
    @Override
    public void setLayout(Layout layout) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.eclipse.swt.widgets.Control#setLayoutData(java.lang.Object)
     * @since 5.0.1
     */
    @Override
    public void setLayoutData(Object layoutData) {
        throw new UnsupportedOperationException();
    }
}
