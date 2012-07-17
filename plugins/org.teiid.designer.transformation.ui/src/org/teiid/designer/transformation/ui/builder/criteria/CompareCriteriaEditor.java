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

import org.teiid.core.util.CoreArgCheck;
import org.teiid.core.util.I18nUtil;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.ui.builder.model.CompareCriteriaEditorModel;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener;
import org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent;

/**
 * CompareCriteriaEditor
 */
public class CompareCriteriaEditor extends AbstractPredicateCriteriaTypeEditor {

    private final static String PREFIX = I18nUtil.getPropertyPrefix(CompareCriteriaEditor.class);

    private CriteriaExpressionEditor leftEditor;
    private CriteriaExpressionEditor rightEditor;
    private Control rightComponent;
    private Control leftComponent;
    private ViewController viewController;
    CompareCriteriaEditorModel theModel;

    public CompareCriteriaEditor( Composite parent,
                                  CompareCriteriaEditorModel model ) {
        super(parent, CompareCriteria.class, model);
        this.theModel = model;
        viewController = new ViewController();
        model.addModelListener(viewController);
        viewController.initialize();
    }

    @Override
    public String getToolTipText() {
        String toolTip = ""; //$NON-NLS-1$
        String curOperator = theModel.getCurrentOperator();
        if (curOperator.equals(CompareCriteriaEditorModel.EQ)) {
            toolTip = Util.getString(PREFIX + "equalsToolTipText"); //$NON-NLS-1$
        } else if (curOperator.equals(CompareCriteriaEditorModel.NE)) {
            toolTip = Util.getString(PREFIX + "notEqualToolTipText"); //$NON-NLS-1$
        } else if (curOperator.equals(CompareCriteriaEditorModel.GT)) {
            toolTip = Util.getString(PREFIX + "greaterThanToolTipText"); //$NON-NLS-1$
        } else if (curOperator.equals(CompareCriteriaEditorModel.LT)) {
            toolTip = Util.getString(PREFIX + "lessThanToolTipText"); //$NON-NLS-1$
        } else if (curOperator.equals(CompareCriteriaEditorModel.GE)) {
            toolTip = Util.getString(PREFIX + "greaterEqualToolTipText"); //$NON-NLS-1$
        } else if (curOperator.equals(CompareCriteriaEditorModel.LE)) {
            toolTip = Util.getString(PREFIX + "lessEqualToolTipText"); //$NON-NLS-1$
        }
        return toolTip;
    }

    @Override
    public String getTitle() {
        String title = Util.getString(PREFIX + "title"); //$NON-NLS-1$
        return title;
    }

    @Override
	public Control createLeftComponent( Composite parent ) {
        leftEditor = new CriteriaExpressionEditor(parent, theModel.getLeftExpressionModel());
        leftComponent = leftEditor.getUi();
        return leftComponent;
    }

    @Override
	public Control createRightComponent( Composite parent ) {
        rightEditor = new CriteriaExpressionEditor(parent, theModel.getRightExpressionModel());
        rightComponent = rightEditor.getUi();
        return rightComponent;
    }

    @Override
	public Expression getLeftExpression() {
        Expression leftExpression = theModel.getLeftExpression();
        return leftExpression;
    }

    @Override
	public Expression getRightExpression() {
        Expression rightExpression = theModel.getRightExpression();
        return rightExpression;
    }

    @Override
    public void setLanguageObject( LanguageObject obj ) {
        CoreArgCheck.isInstanceOf(CompareCriteria.class, obj);
        theModel.setLanguageObject(obj);
    }

    @Override
	public String[] getOperators() {
        return theModel.getOperators();
    }

    @Override
    public void acceptFocus() {
        leftEditor.acceptFocus();
    }

    @Override
	public void setOperator( String op ) {
        theModel.setCurrentOperator(op);
    }

    @Override
	public String getCurrentOperator() {
        return theModel.getCurrentOperator();
    }

    void displayLanguageObjectChange() {
        displayLeftExpression();
        displayRightExpression();
    }

    void displayLeftExpression() {
        leftEditor.setLanguageObject(theModel.getLeftExpression());
    }

    void displayRightExpression() {
        rightEditor.setLanguageObject(theModel.getRightExpression());
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
            if (type.equals(CompareCriteriaEditorModel.LEFT_EXPRESSION)) {
                displayLeftExpression();
            } else if (type.equals(CompareCriteriaEditorModel.RIGHT_EXPRESSION)) {
                displayRightExpression();
            } else if (type.equals(LanguageObjectEditorModelEvent.SAVED)) {
                displayLanguageObjectChange();
            }
        }
    }
}
