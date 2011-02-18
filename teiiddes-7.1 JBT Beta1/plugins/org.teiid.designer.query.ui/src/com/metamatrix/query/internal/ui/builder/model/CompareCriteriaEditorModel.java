/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import com.metamatrix.query.internal.ui.sqleditor.component.DisplayNodeConstants;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.AbstractCompareCriteria;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 * CompareCriteriaEditorModel
 */
public class CompareCriteriaEditorModel extends AbstractPredicateCriteriaTypeEditorModel
    implements ILanguageObjectEditorModelListener {
    public final static String LEFT_EXPRESSION = "COMPARE CRITERIA LEFT EXPRESSION"; //$NON-NLS-1$
    public final static String RIGHT_EXPRESSION = "COMPARE CRITERIA RIGHT EXPRESSION"; //$NON-NLS-1$
    public final static String OPERATOR = "OPERATOR"; //$NON-NLS-1$
    private final static CompareCriteria EMPTY_COMPARE_CRITERIA = new CompareCriteria();

    public final static String EQ = DisplayNodeConstants.EQUALS;
    public final static String NE = DisplayNodeConstants.NE;
    public final static String LT = DisplayNodeConstants.LT;
    public final static String GT = DisplayNodeConstants.GT;
    public final static String LE = DisplayNodeConstants.LE;
    public final static String GE = DisplayNodeConstants.GE;

    private final static String[] OPERATORS = new String[] {EQ, NE, LT, GT, LE, GE};

    private CriteriaExpressionEditorModel leftExpModel;
    private CriteriaExpressionEditorModel rightExpModel;
    private int curOperatorInt;
    private String curOperatorStr;

    public CompareCriteriaEditorModel( CriteriaExpressionEditorModel left,
                                       CriteriaExpressionEditorModel right ) {
        super(CompareCriteria.class);
        this.leftExpModel = left;
        this.rightExpModel = right;
        this.leftExpModel.addModelListener(this);
        this.rightExpModel.addModelListener(this);
        this.curOperatorInt = EMPTY_COMPARE_CRITERIA.getOperator();
        this.curOperatorStr = this.operatorAsString(this.curOperatorInt);

        // initialize expressions
        setLeftExpression(leftExpModel.getExpression());
        setRightExpression(rightExpModel.getExpression());
    }

    public CompareCriteriaEditorModel() {
        this(new CriteriaExpressionEditorModel(), new CriteriaExpressionEditorModel());
    }

    /* (non-Javadoc)
     * @see org.teiid.query.internal.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent)
     */
    public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
        String type = theEvent.getType();

        if (type.equals(CompositeLanguageObjectEditorModel.MODEL_CHANGE)) {
            // if a model change occurs, just fire event to listeners of this model so
            // that they can query the state of this model
            fireModelChanged(type);
        } else {
            fireModelChanged(LanguageObjectEditorModelEvent.STATE_CHANGE);
        }
    }

    @Override
    public LanguageObject getLanguageObject() {
        CompareCriteria compareCriteria = new CompareCriteria();
        compareCriteria.setLeftExpression(leftExpModel.getExpression());
        compareCriteria.setRightExpression(rightExpModel.getExpression());
        compareCriteria.setOperator(curOperatorInt);

        return compareCriteria;
    }

    @Override
    public void setLanguageObject( LanguageObject obj ) {

        super.setLanguageObject(obj);
        CompareCriteria curCompareCriteria;
        if (obj == null) {
            clear();
        } else {
            curCompareCriteria = (CompareCriteria)obj;
            setLeftExpression(curCompareCriteria.getLeftExpression());
            setRightExpression(curCompareCriteria.getRightExpression());
            setOperator(curCompareCriteria.getOperator());
        }
    }

    @Override
    public void clear() {
        notifyListeners = false;

        setLeftExpression(EMPTY_COMPARE_CRITERIA.getLeftExpression());
        setRightExpression(EMPTY_COMPARE_CRITERIA.getRightExpression());
        setOperator(EMPTY_COMPARE_CRITERIA.getOperator());

        notifyListeners = true;
        super.clear();
    }

    @Override
    public void setLeftExpression( Expression exp ) {
        leftExpModel.setLanguageObject(exp);
        fireModelChanged(LEFT_EXPRESSION);
    }

    @Override
    public void setRightExpression( Expression exp ) {
        rightExpModel.setLanguageObject(exp);
        fireModelChanged(RIGHT_EXPRESSION);
    }

    public void setOperator( int op ) {
        if (op != curOperatorInt) {
            curOperatorInt = op;
            curOperatorStr = operatorAsString(curOperatorInt);
            fireModelChanged(OPERATOR);
        }
    }

    @Override
    public void setCurrentOperator( String opStr ) {
        int opInt = operatorAsInt(opStr);
        setOperator(opInt);
    }

    private int operatorAsInt( String op ) {
        int opInt = -1;
        if (op != null) {
            if (op.equals(EQ)) {
                opInt = AbstractCompareCriteria.EQ;
            } else if (op.equals(NE)) {
                opInt = AbstractCompareCriteria.NE;
            } else if (op.equals(LT)) {
                opInt = AbstractCompareCriteria.LT;
            } else if (op.equals(GT)) {
                opInt = AbstractCompareCriteria.GT;
            } else if (op.equals(LE)) {
                opInt = AbstractCompareCriteria.LE;
            } else if (op.equals(GE)) {
                opInt = AbstractCompareCriteria.GE;
            }
        }
        return opInt;
    }

    private String operatorAsString( int op ) {
        String str = ""; //$NON-NLS-1$
        switch (op) {
            case AbstractCompareCriteria.EQ:
                str = EQ;
                break;
            case AbstractCompareCriteria.NE:
                str = NE;
                break;
            case AbstractCompareCriteria.LT:
                str = LT;
                break;
            case AbstractCompareCriteria.GT:
                str = GT;
                break;
            case AbstractCompareCriteria.LE:
                str = LE;
                break;
            case AbstractCompareCriteria.GE:
                str = GE;
                break;
        }
        return str;
    }

    @Override
    public void save() {
        super.save();
        leftExpModel.save();
        rightExpModel.save();
    }

    @Override
    public boolean isComplete() {
        boolean leftModelComplete = leftExpModel.isComplete();
        boolean curOperatorOK = (curOperatorInt != -1);
        boolean rightModelComplete = rightExpModel.isComplete();
        boolean complete = (leftModelComplete && curOperatorOK && rightModelComplete);
        return complete;
    }

    @Override
    public Expression getLeftExpression() {
        return leftExpModel.getExpression();
    }

    public CriteriaExpressionEditorModel getLeftExpressionModel() {
        return leftExpModel;
    }

    @Override
    public Expression getRightExpression() {
        return rightExpModel.getExpression();
    }

    public CriteriaExpressionEditorModel getRightExpressionModel() {
        return rightExpModel;
    }

    @Override
    public String[] getOperators() {
        return OPERATORS;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.criteria.AbstractPredicateCriteriaTypeEditorModel#getCurrentOperator()
     */
    @Override
    public String getCurrentOperator() {
        return curOperatorStr;
    }
}
