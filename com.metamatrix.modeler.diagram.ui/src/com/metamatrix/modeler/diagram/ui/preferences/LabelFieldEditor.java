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

package com.metamatrix.modeler.diagram.ui.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A field editor for displaying labels not associated with other widgets.
 */
class LabelFieldEditor extends FieldEditor {

    private Label label;

    // All labels can use the same preference name since they don't
    // store any preference.
    public LabelFieldEditor(String value, Composite parent) {
        super("label", value, parent); //$NON-NLS-1$
    }

    // Adjusts the field editor to be displayed correctly
    // for the given number of columns.
    @Override
    protected void adjustForNumColumns(int numColumns) {
        ((GridData) label.getLayoutData()).horizontalSpan = numColumns;
    }

    // Fills the field editor's controls into the given parent.
    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        label = getLabelControl(parent);
        
        GridData gridData = new GridData();
        gridData.horizontalSpan = numColumns;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessVerticalSpace = false;
        
        label.setLayoutData(gridData);
    }

    // Returns the number of controls in the field editor.
    @Override
    public int getNumberOfControls() {
        return 1;
    }

    // Labels do not persist any preferences, so these methods are empty.
    @Override
    protected void doLoad() {
    }
    @Override
    protected void doLoadDefault() {
    }
    @Override
    protected void doStore() {
    }
}
