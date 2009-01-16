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

package com.metamatrix.query.internal.ui.builder;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.expression.ExpressionEditor;
import com.metamatrix.query.internal.ui.builder.model.ExpressionEditorModel;
import com.metamatrix.query.ui.builder.ILanguageObjectEditor;

/**
 * ExpressionBuilder
 */
public final class ExpressionBuilder extends AbstractLanguageObjectBuilder {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ExpressionBuilder.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROLS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private ExpressionEditor expressionEditor;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // VARIABLES
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private boolean functionOnly;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ExpressionBuilder(Shell theShell, boolean functionOnly) {
        super(theShell, Util.getString(PREFIX + "title")); //$NON-NLS-1$
        this.functionOnly = functionOnly;
        setSizeRelativeToScreen(50, 75);
        setCenterOnDisplay(true);
	}

	public ExpressionBuilder(Shell theShell) {
		this(theShell, false);
	}
	
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectBuilder#createEditorDetails(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected ILanguageObjectEditor createEditor(Composite theParent) {
        expressionEditor = new ExpressionEditor(theParent, new ExpressionEditorModel());
        
        // needs to be done after construction because buttons aren't created yet
        getButton(IDialogConstants.OK_ID).setToolTipText(Util.getString(PREFIX + "okButton.tip")); //$NON-NLS-1$
        getButton(IDialogConstants.CANCEL_ID).setToolTipText(Util.getString(PREFIX + "cancelButton.tip")); //$NON-NLS-1$

        return expressionEditor;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectBuilder#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(PREFIX + "title"); //$NON-NLS-1$
    }

	@Override
    protected void handleTreeSelection() {
		super.handleTreeSelection();
		if (functionOnly) {
			expressionEditor.setFunctionOnly(isTreeSelectionRoot());
		}
	}
}
