/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.builder;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.transformation.ui.builder.expression.ExpressionEditor;
import com.metamatrix.query.internal.ui.builder.model.ExpressionEditorModel;

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
