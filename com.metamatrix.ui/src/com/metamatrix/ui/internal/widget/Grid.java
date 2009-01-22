/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
