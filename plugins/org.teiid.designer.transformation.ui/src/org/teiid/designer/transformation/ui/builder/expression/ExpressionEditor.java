/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.expression;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.transformation.ui.builder.AbstractCompositeExpressionEditor;
import org.teiid.designer.transformation.ui.builder.ILanguageObjectEditor;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.query.ui.builder.model.ExpressionEditorModel;


/**
 * ExpressionEditor
 *
 * @since 8.0
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
     * @see org.teiid.query.ui.builder.AbstractCompositeExpressionEditor#createExpressionEditors(org.eclipse.swt.widgets.Composite)
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
     * @see org.teiid.query.ui.builder.AbstractCompositeLanguageObjectEditor#getDefaultEditor()
     */
    @Override
    protected ILanguageObjectEditor getDefaultEditor() {
        return elementEditor;
    }


    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(PREFIX + "title"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getToolTipText()
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
