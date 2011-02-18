/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import com.metamatrix.core.util.I18nUtil;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 * IsNullCriteriaEditorModel
 */
public class IsNullCriteriaEditorModel extends AbstractPredicateCriteriaTypeEditorModel
    implements ILanguageObjectEditorModelListener {
    public final static String EXPRESSION = "EXPRESSION"; //$NON-NLS-1$
    private final static String PREFIX = I18nUtil.getPropertyPrefix(IsNullCriteriaEditorModel.class);
    private final static String[] OPERATORS = new String[] {Util.getString(PREFIX + "isNull") //$NON-NLS-1$
    };
    private final static IsNullCriteria EMPTY_IS_NULL_CRITERIA = new IsNullCriteria();

    private CriteriaExpressionEditorModel expEditorModel;

    public IsNullCriteriaEditorModel( CriteriaExpressionEditorModel eem ) {
        super(IsNullCriteria.class);
        this.expEditorModel = eem;
        this.expEditorModel.addModelListener(this);
    }

    public IsNullCriteriaEditorModel() {
        this(new CriteriaExpressionEditorModel());
    }

    public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
        String type = theEvent.getType();
        String eventType;
        if (type.equals(CompositeLanguageObjectEditorModel.MODEL_CHANGE)) {
            eventType = type;
        } else {
            eventType = EXPRESSION;
        }
        fireModelChanged(eventType);
    }

    @Override
    public void setLanguageObject( LanguageObject obj ) {
        super.setLanguageObject(obj);
        IsNullCriteria curIsNullCriteria;
        if (obj == null) {
            clear();
        } else {
            curIsNullCriteria = (IsNullCriteria)obj;
            expEditorModel.setLanguageObject(curIsNullCriteria.getExpression());
        }
    }

    @Override
    public void clear() {
        notifyListeners = false;
        expEditorModel.setLanguageObject(EMPTY_IS_NULL_CRITERIA.getExpression());
        notifyListeners = true;

        super.clear();
    }

    @Override
    public void save() {
        super.save();
        expEditorModel.save();
    }

    @Override
    public LanguageObject getLanguageObject() {
        IsNullCriteria isNullCriteria = new IsNullCriteria();
        isNullCriteria.setExpression(expEditorModel.getExpression());
        return isNullCriteria;
    }

    @Override
    public boolean isComplete() {
        boolean complete = expEditorModel.isComplete();
        return complete;
    }

    public CriteriaExpressionEditorModel getExpressionModel() {
        return expEditorModel;
    }

    @Override
    public String[] getOperators() {
        return OPERATORS;
    }

    @Override
    public String getCurrentOperator() {
        return OPERATORS[0];
    }

    @Override
    public void setCurrentOperator( String op ) {
        // Interface method that is unused
    }

    public Expression getExpression() {
        Expression exp = expEditorModel.getExpression();
        return exp;
    }

    public void setExpression( Expression exp ) {
        boolean same;
        Expression oldExp = expEditorModel.getExpression();
        if (exp == null) {
            same = (oldExp == null);
        } else {
            same = exp.equals(oldExp);
        }
        if (!same) {
            // Note-- do not fire event because model will fire its own event
            expEditorModel.setLanguageObject(exp);
        }
    }

    @Override
    public Expression getLeftExpression() {
        return getExpression();
    }

    @Override
    public void setLeftExpression( Expression exp ) {
        setExpression(exp);
    }

    @Override
    public Expression getRightExpression() {
        // Unused abstract method
        return null;
    }

    @Override
    public void setRightExpression( Expression exp ) {
        // Unused abstract method
    }
}
