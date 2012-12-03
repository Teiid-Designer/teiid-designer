/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.model;

import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.ILanguageObject;

/**
 * IsNullCriteriaEditorModel
 *
 * @since 8.0
 */
public class IsNullCriteriaEditorModel extends AbstractPredicateCriteriaTypeEditorModel
    implements ILanguageObjectEditorModelListener {
    public final static String EXPRESSION = "EXPRESSION"; //$NON-NLS-1$
    private final static String PREFIX = I18nUtil.getPropertyPrefix(IsNullCriteriaEditorModel.class);
    private final static String[] OPERATORS = new String[] {Util.getString(PREFIX + "isNull") //$NON-NLS-1$
    };
    private final IIsNullCriteria emptyIsNullCriteria;

    private CriteriaExpressionEditorModel expEditorModel;

    public IsNullCriteriaEditorModel( CriteriaExpressionEditorModel eem ) {
        super(IIsNullCriteria.class);
        this.expEditorModel = eem;
        this.expEditorModel.addModelListener(this);
        
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        emptyIsNullCriteria = factory.createIsNullCriteria();
    }

    public IsNullCriteriaEditorModel() {
        this(new CriteriaExpressionEditorModel());
    }

    @Override
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
    public void setLanguageObject( ILanguageObject obj ) {
        super.setLanguageObject(obj);
        IIsNullCriteria curIsNullCriteria;
        if (obj == null) {
            clear();
        } else {
            curIsNullCriteria = (IIsNullCriteria)obj;
            expEditorModel.setLanguageObject(curIsNullCriteria.getExpression());
        }
    }

    @Override
    public void clear() {
        notifyListeners = false;
        expEditorModel.setLanguageObject(emptyIsNullCriteria.getExpression());
        notifyListeners = true;

        super.clear();
    }

    @Override
    public void save() {
        super.save();
        expEditorModel.save();
    }

    @Override
    public ILanguageObject getLanguageObject() {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        
        IIsNullCriteria isNullCriteria = factory.createIsNullCriteria();
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

    public IExpression getExpression() {
        IExpression exp = expEditorModel.getExpression();
        return exp;
    }

    public void setExpression( IExpression exp ) {
        boolean same;
        IExpression oldExp = expEditorModel.getExpression();
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
    public IExpression getLeftExpression() {
        return getExpression();
    }

    @Override
    public void setLeftExpression( IExpression exp ) {
        setExpression(exp);
    }

    @Override
    public IExpression getRightExpression() {
        // Unused abstract method
        return null;
    }

    @Override
    public void setRightExpression( IExpression exp ) {
        // Unused abstract method
    }
}
