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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * A Composite that uses, by default, {@link GridData} (for use within a parent composite that uses a {@link GridLayout}) that
 * specifies to fill the containing cell vertically and horizontally, and a {@link GridLayout}.
 * 
 * @since 5.0.1
 */
public class Panel extends Composite {

    // ===========================================================================================================================
    // Constructors

    /**
     * @param parent
     * @since 5.0.1
     */
    public Panel(Composite parent) {
        this(parent, SWT.NONE, 1);
    }

    /**
     * @param parent
     * @param style
     * @since 5.0.1
     */
    public Panel(Composite parent,
                 int style) {
        this(parent, style, 1);
    }

    /**
     * @param parent
     * @param style
     * @param columnCount
     * @since 5.0.1
     */
    public Panel(Composite parent,
                 int style,
                 int columnCount) {
        super(parent, style);
        constructPanel(columnCount);
    }

    // ===========================================================================================================================
    // Methods

    /**
     * Called by constructors.
     * 
     * @param columns
     * @since 5.0.1
     */
    protected void constructPanel(int columnCount) {
        // Create & initialize grid layout
        GridLayout layout = new GridLayout(columnCount, false);
        if (WidgetUtil.hasStyle(this, SWT.NO_TRIM)) {
            layout.marginWidth = layout.marginHeight = 0;
        }
        super.setLayout(layout);
        // Create & set grid data
        super.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    }
}
