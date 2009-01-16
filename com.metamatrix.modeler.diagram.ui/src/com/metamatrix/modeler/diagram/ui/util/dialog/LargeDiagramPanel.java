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

package com.metamatrix.modeler.diagram.ui.util.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * AliasEntryPanel
 */
public class LargeDiagramPanel extends Composite {

	private static final int LABEL_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING;
	private static final String WARNING_TEXT = DiagramUiConstants.Util.getString("LargeDiagramPanel.warning"); //$NON-NLS-1$
	private static final String LEADING_TEXT1 = DiagramUiConstants.Util.getString("LargeDiagramPanel.leadingText1"); //$NON-NLS-1$
	private static final String LEADING_TEXT2 = DiagramUiConstants.Util.getString("LargeDiagramPanel.leadingText2"); //$NON-NLS-1$
	private static final String OBJECTS_TEXT = " " + DiagramUiConstants.Util.getString("LargeDiagramPanel.objectsText"); //$NON-NLS-1$ //$NON-NLS-2$
	private static final String OPTION1_TEXT = " " + DiagramUiConstants.Util.getString("LargeDiagramPanel.option1"); //$NON-NLS-1$ //$NON-NLS-2$

	private String nCompString;
	private String compLimitString;
	//============================================================
	// Constructors
	//============================================================
	/**
	 * Constructor.
	 * 
	 * @param parent    Parent of this control
	 */
	public LargeDiagramPanel(Composite parent, String nComps, String compLimit) {
		super(parent, SWT.NONE);
		this.nCompString = LEADING_TEXT1 + nComps + OBJECTS_TEXT; 
		this.compLimitString = LEADING_TEXT2+ compLimit + OBJECTS_TEXT; 
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
        
		WidgetFactory.createLabel(this,LABEL_GRID_STYLE,1,WARNING_TEXT);

		Composite thePanel = new Composite(this, SWT.NONE);
		GridLayout theLayout = new GridLayout();
		thePanel.setLayout(theLayout);
		theLayout.numColumns = 1;
        
		Label compLabel = new Label(thePanel, SWT.NONE);
		compLabel.setText(nCompString);
		Label limitLabel = new Label(thePanel, SWT.NONE);
		limitLabel.setText(compLimitString); 
		
		Label option1Label = new Label(thePanel, SWT.NONE);
		option1Label.setText(OPTION1_TEXT); 
        
//		Label aliasLabel = new Label(aliasPanel, SWT.NONE);
//		aliasLabel.setText(ALIAS_LABEL_TEXT+ " "); //$NON-NLS-1$
//		aliasText = WidgetFactory.createTextField(aliasPanel);
//		aliasText.setTextLimit(50);
//		GridData aliasTextGridData = new GridData();
//		aliasText.setLayoutData(aliasTextGridData);
	}

}
