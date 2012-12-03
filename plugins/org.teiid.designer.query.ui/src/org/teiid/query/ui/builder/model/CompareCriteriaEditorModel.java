/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.model;

import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.query.ui.sqleditor.component.DisplayNodeConstants;

/**
 * CompareCriteriaEditorModel
 *
 * @since 8.0
 */
public class CompareCriteriaEditorModel extends AbstractPredicateCriteriaTypeEditorModel
    implements ILanguageObjectEditorModelListener {
    public final static String LEFT_EXPRESSION = "COMPARE CRITERIA LEFT EXPRESSION"; //$NON-NLS-1$
    public final static String RIGHT_EXPRESSION = "COMPARE CRITERIA RIGHT EXPRESSION"; //$NON-NLS-1$
    public final static String OPERATOR = "OPERATOR"; //$NON-NLS-1$
    
    public final static String EQ = DisplayNodeConstants.EQUALS;
    public final static String NE = DisplayNodeConstants.NE;
    public final static String LT = DisplayNodeConstants.LT;
    public final static String GT = DisplayNodeConstants.GT;
    public final static String LE = DisplayNodeConstants.LE;
    public final static String GE = DisplayNodeConstants.GE;

    private final static String[] OPERATORS = new String[] {EQ, NE, LT, GT, LE, GE};

    private final ICompareCriteria emptyCompareCriteria;
    
    private CriteriaExpressionEditorModel leftExpModel;
    private CriteriaExpressionEditorModel rightExpModel;
    private int curOperatorInt;
    private String curOperatorStr;

    public CompareCriteriaEditorModel( CriteriaExpressionEditorModel left,
                                       CriteriaExpressionEditorModel right ) {
        super(ICompareCriteria.class);
        this.leftExpModel = left;
        this.rightExpModel = right;
        this.leftExpModel.addModelListener(this);
        this.rightExpModel.addModelListener(this);
        
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        emptyCompareCriteria = factory.createCompareCriteria();
        
        this.curOperatorInt = emptyCompareCriteria.getOperator();
        this.curOperatorStr = this.operatorAsString(this.curOperatorInt);

        // initialize expressions
        setLeftExpression(leftExpModel.getExpression());
        setRightExpression(rightExpModel.getExpression());
    }

    public CompareCriteriaEditorModel() {
        this(new CriteriaExpressionEditorModel(), new CriteriaExpressionEditorModel());
    }

    /* (non-Javadoc)
     * @see org.teiid.query.internal.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent)
     */
    @Override
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
    public ILanguageObject getLanguageObject() {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        
        ICompareCriteria compareCriteria = factory.createCompareCriteria();
        compareCriteria.setLeftExpression(leftExpModel.getExpression());
        compareCriteria.setRightExpression(rightExpModel.getExpression());
        compareCriteria.setOperator(curOperatorInt);

        return compareCriteria;
    }

    @Override
    public void setLanguageObject( ILanguageObject obj ) {

        super.setLanguageObject(obj);
        ICompareCriteria curCompareCriteria;
        if (obj == null) {
            clear();
        } else {
            curCompareCriteria = (ICompareCriteria)obj;
            setLeftExpression(curCompareCriteria.getLeftExpression());
            setRightExpression(curCompareCriteria.getRightExpression());
            setOperator(curCompareCriteria.getOperator());
        }
    }

    @Override
    public void clear() {
        notifyListeners = false;

        setLeftExpression(emptyCompareCriteria.getLeftExpression());
        setRightExpression(emptyCompareCriteria.getRightExpression());
        setOperator(emptyCompareCriteria.getOperator());

        notifyListeners = true;
        super.clear();
    }

    @Override
    public void setLeftExpression( IExpression exp ) {
        leftExpModel.setLanguageObject(exp);
        fireModelChanged(LEFT_EXPRESSION);
    }

    @Override
    public void setRightExpression( IExpression exp ) {
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
                opInt = ICompareCriteria.EQ;
            } else if (op.equals(NE)) {
                opInt = ICompareCriteria.NE;
            } else if (op.equals(LT)) {
                opInt = ICompareCriteria.LT;
            } else if (op.equals(GT)) {
                opInt = ICompareCriteria.GT;
            } else if (op.equals(LE)) {
                opInt = ICompareCriteria.LE;
            } else if (op.equals(GE)) {
                opInt = ICompareCriteria.GE;
            }
        }
        return opInt;
    }

    private String operatorAsString( int op ) {
        String str = ""; //$NON-NLS-1$
        switch (op) {
            case ICompareCriteria.EQ:
                str = EQ;
                break;
            case ICompareCriteria.NE:
                str = NE;
                break;
            case ICompareCriteria.LT:
                str = LT;
                break;
            case ICompareCriteria.GT:
                str = GT;
                break;
            case ICompareCriteria.LE:
                str = LE;
                break;
            case ICompareCriteria.GE:
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
    public IExpression getLeftExpression() {
        return leftExpModel.getExpression();
    }

    public CriteriaExpressionEditorModel getLeftExpressionModel() {
        return leftExpModel;
    }

    @Override
    public IExpression getRightExpression() {
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
     * @see org.teiid.query.ui.builder.criteria.AbstractPredicateCriteriaTypeEditorModel#getCurrentOperator()
     */
    @Override
    public String getCurrentOperator() {
        return curOperatorStr;
    }
}
