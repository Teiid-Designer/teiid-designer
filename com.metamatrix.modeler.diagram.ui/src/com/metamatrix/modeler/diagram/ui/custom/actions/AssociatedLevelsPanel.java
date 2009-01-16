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

package com.metamatrix.modeler.diagram.ui.custom.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * AliasEntryPanel
 */
public class AssociatedLevelsPanel extends Composite {

    private static final int LABEL_GRID_STYLE   = GridData.HORIZONTAL_ALIGN_BEGINNING; 
    private static final String TEXT_1          = DiagramUiConstants.Util.getString("AssociatedLevelsPanel.text_1"); //$NON-NLS-1$
    private static final String TEXT_2          = DiagramUiConstants.Util.getString("AssociatedLevelsPanel.text_2"); //$NON-NLS-1$
    private static final String TEXT_3          = DiagramUiConstants.Util.getString("AssociatedLevelsPanel.text_3"); //$NON-NLS-1$
    private static final String DEFAULT_VALUE   = DiagramUiConstants.Util.getString("AssociatedLevelsPanel.defaultValue"); //$NON-NLS-1$

    private Text levelsText;
    //============================================================
    // Constructors
    //============================================================
    /**
     * Constructor.
     * 
     * @param parent    Parent of this control
     */
    public AssociatedLevelsPanel(Composite parent) {
        super(parent, SWT.NONE);
        init();
    }
    
    //============================================================
    // Instance methods
    //============================================================
    
    /**
     * Initialize the panel.
     */
    private void init( ) {
        //------------------------------        
        // Set layout for the Composite
        //------------------------------        
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        this.setLayoutData(gridData);
        
        WidgetFactory.createLabel(this,LABEL_GRID_STYLE,1,TEXT_1);

        Composite levelsPanel = new Composite(this, SWT.NONE);
        GridLayout levelsLayout = new GridLayout();
        levelsPanel.setLayout(levelsLayout);
        levelsLayout.numColumns = 1;

        levelsText = WidgetFactory.createTextField(levelsPanel, GridData.FILL_HORIZONTAL);
        levelsText.setTextLimit(50);
        levelsText.setText(DEFAULT_VALUE);

        GridData levelsTextGridData = new GridData();
        levelsTextGridData.widthHint = 30;
        levelsText.setLayoutData(levelsTextGridData);
        levelsText.selectAll();
        Label textLabel_2 = new Label(levelsPanel, SWT.NONE);
        textLabel_2.setText(TEXT_2); 
        Label textLabel_3 = new Label(levelsPanel, SWT.NONE);
        textLabel_3.setText(TEXT_3); 
    }
    
    public int getLevels() {
        return Integer.parseInt(levelsText.getText()); 
    }

}
