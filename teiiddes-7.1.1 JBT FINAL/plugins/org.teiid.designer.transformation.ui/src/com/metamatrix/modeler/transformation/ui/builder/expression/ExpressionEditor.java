/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.builder.expression;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.transformation.ui.builder.AbstractCompositeExpressionEditor;
import com.metamatrix.modeler.transformation.ui.builder.ILanguageObjectEditor;
import com.metamatrix.query.internal.ui.builder.model.ExpressionEditorModel;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * ExpressionEditor
 */
public class ExpressionEditor extends AbstractCompositeExpressionEditor {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ExpressionEditor.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    /////////////////////////////////////////////////////////////////////////////////////////////// 
    
    private ConstantEditor constantEditor;
    
    private ElementEditor elementEditor;
    
    private FunctionEditor functionEditor;
    
//    private boolean functionOnly;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ExpressionEditor(Composite theParent,
                            ExpressionEditorModel theModel) {
        super(theParent, theModel);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.AbstractCompositeExpressionEditor#createExpressionEditors(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected List createExpressionEditors(Composite theParent) {
        List editors = new ArrayList(3);
        ExpressionEditorModel model = (ExpressionEditorModel)getModel();
        
        elementEditor = new ElementEditor(theParent, model.getElementEditorModel());
        editors.add(elementEditor);

        constantEditor = new ConstantEditor(theParent, model.getConstantEditorModel());
        editors.add(constantEditor);

        functionEditor = new FunctionEditor(theParent, model.getFunctionEditorModel());
        editors.add(functionEditor);

        return editors;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.AbstractCompositeLanguageObjectEditor#getDefaultEditor()
     */
    @Override
    protected ILanguageObjectEditor getDefaultEditor() {
        return elementEditor;
    }


    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(PREFIX + "title"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        return Util.getString(PREFIX + "tip"); //$NON-NLS-1$
    }


    /**
     * Sets the editors (other than the function editor) to be enabled or disabled. In some cases,
     * only the function editor should be enabled.
     * @param theEnableFlag indicates if editors should be enabled or disabled
     */
    public void setFunctionOnly(boolean theEnableFlag) {
//    	functionOnly = theEnableFlag;
    	
    	setEditorEnabled(elementEditor, !theEnableFlag);
		setEditorEnabled(constantEditor, !theEnableFlag);

		if (theEnableFlag) {
			WidgetUtil.selectRadioButton(getEditorButton(functionEditor));
		}
	}
}
