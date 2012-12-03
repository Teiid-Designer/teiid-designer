/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.criteria;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener;
import org.teiid.query.ui.builder.model.IsNullCriteriaEditorModel;
import org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent;

/**
 * @since 8.0
 */
public class IsNullCriteriaEditor extends AbstractPredicateCriteriaTypeEditor {
    private final static String PREFIX = I18nUtil.getPropertyPrefix(IsNullCriteriaEditor.class);

    private CriteriaExpressionEditor editor;
    private Control component;
    private ViewController viewController;
    IsNullCriteriaEditorModel theModel;

    public IsNullCriteriaEditor( Composite parent,
                                 IsNullCriteriaEditorModel model ) {
        super(parent, IIsNullCriteria.class, model);
        this.theModel = model;
        viewController = new ViewController();
        theModel.addModelListener(viewController);
        viewController.initialize();
    }

    @Override
    public String getToolTipText() {
        String tip = Util.getString(PREFIX + "toolTipText"); //$NON-NLS-1$
        return tip;
    }

    @Override
    public String getTitle() {
        String title = Util.getString(PREFIX + "title"); //$NON-NLS-1$
        return title;
    }

    @Override
	public Control createLeftComponent( Composite parent ) {
        editor = new CriteriaExpressionEditor(parent, theModel.getExpressionModel());
        component = editor.getUi();
        return component;
    }

    @Override
	public Control createRightComponent( Composite parent ) {
        // Unused
        return null;
    }

    @Override
	public IExpression getLeftExpression() {
        IExpression leftExpression = theModel.getLeftExpression();
        return leftExpression;
    }

    @Override
	public IExpression getRightExpression() {
        // Unused
        return null;
    }

    @Override
    public void setLanguageObject( ILanguageObject obj ) {
        CoreArgCheck.isInstanceOf(IIsNullCriteria.class, obj);
        theModel.setLanguageObject(obj);
    }

    @Override
	public String[] getOperators() {
        return theModel.getOperators();
    }

    @Override
    public void acceptFocus() {
        editor.acceptFocus();
    }

    @Override
	public void setOperator( String op ) {
        theModel.setCurrentOperator(op);
    }

    @Override
	public String getCurrentOperator() {
        return theModel.getCurrentOperator();
    }

    void displayExpression() {
        ILanguageObject langObj = theModel.getLeftExpression();
        editor.setLanguageObject(langObj);
    }

    void displayLanguageObjectChange() {
        displayExpression();
    }

    /**
     * The <code>ViewController</code> class is a view controller for the <code>FunctionEditor</code>.
     */
    private class ViewController implements ILanguageObjectEditorModelListener {
        public ViewController() {
            super();
        }

        public void initialize() {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
				public void run() {
                    modelChanged(new LanguageObjectEditorModelEvent(theModel, LanguageObjectEditorModelEvent.SAVED));
                }
            });
        }

        /**
         * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        @Override
		public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            String type = theEvent.getType();
            if (type.equals(IsNullCriteriaEditorModel.EXPRESSION)) {
                displayExpression();
            } else if (type.equals(LanguageObjectEditorModelEvent.SAVED)) {
                displayLanguageObjectChange();
            }
        }
    }
}
