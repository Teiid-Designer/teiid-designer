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
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.IMatchCriteria;

/**
 * MatchCriteriaEditorModel
 *
 * @since 8.0
 */
public class MatchCriteriaEditorModel extends AbstractPredicateCriteriaTypeEditorModel
    implements ILanguageObjectEditorModelListener {
    private final static String PREFIX = I18nUtil.getPropertyPrefix(MatchCriteriaEditorModel.class);
    private final static String[] OPERATORS = new String[] {Util.getString(PREFIX + "like") //$NON-NLS-1$
    };
    
    public final static String LEFT_EXPRESSION = "MATCH CRITERIA LEFT EXPRESSION"; //$NON-NLS-1$
    public final static String RIGHT_EXPRESSION = "MATCH CRITERIA RIGHT EXPRESSION"; //$NON-NLS-1$
    public final static String ESCAPE_CHAR = "MATCH CRITERIA ESCAPE CHAR"; //$NON-NLS-1$

    private final IMatchCriteria emptyMatchCriteria;
    
    private CriteriaExpressionEditorModel leftExpModel;
    private CriteriaExpressionEditorModel rightExpModel;
    private char escapeChar;

    public MatchCriteriaEditorModel( CriteriaExpressionEditorModel left,
                                     CriteriaExpressionEditorModel right ) {
        super(IMatchCriteria.class);
        this.leftExpModel = left;
        this.rightExpModel = right;
        this.leftExpModel.addModelListener(this);
        this.rightExpModel.addModelListener(this);
        
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        emptyMatchCriteria = factory.createMatchCriteria();
    }

    public MatchCriteriaEditorModel() {
        this(new CriteriaExpressionEditorModel(), new CriteriaExpressionEditorModel());
    }

    @Override
    public ILanguageObject getLanguageObject() {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        IMatchCriteria matchCriteria = factory.createMatchCriteria();
        
        matchCriteria.setLeftExpression(leftExpModel.getExpression());
        matchCriteria.setRightExpression(rightExpModel.getExpression());
        matchCriteria.setEscapeChar(escapeChar);
        return matchCriteria;
    }

    public void setEscapeChar( char escChar ) {
        boolean same = (this.escapeChar == escChar);
        if (!same) {
            this.escapeChar = escChar;
            fireModelChanged(ESCAPE_CHAR);
        }
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    @Override
    public void setLanguageObject( ILanguageObject obj ) {
        super.setLanguageObject(obj);
        IMatchCriteria curMatchCriteria;
        if (obj == null) {
            clear();
        } else {
            curMatchCriteria = (IMatchCriteria)obj;
            IExpression newLeftExpression = curMatchCriteria.getLeftExpression();
            setLeftExpression(newLeftExpression);
            IExpression newRightExpression = curMatchCriteria.getRightExpression();
            setRightExpression(newRightExpression);
            char newEscapeChar = curMatchCriteria.getEscapeChar();
            setEscapeChar(newEscapeChar);
        }
    }

    @Override
    public void clear() {
        notifyListeners = false;

        setLeftExpression(emptyMatchCriteria.getLeftExpression());
        setRightExpression(emptyMatchCriteria.getRightExpression());
        setEscapeChar(emptyMatchCriteria.getEscapeChar());

        notifyListeners = true;
        super.clear();
    }

    @Override
    public void save() {
        super.save();
        leftExpModel.save();
        rightExpModel.save();
    }

    @Override
    public IExpression getLeftExpression() {
        return leftExpModel.getExpression();
    }

    @Override
    public void setLeftExpression( IExpression exp ) {
        boolean same;
        IExpression oldExp = leftExpModel.getExpression();
        if (exp == null) {
            same = (oldExp == null);
        } else {
            same = exp.equals(oldExp);
        }
        if (!same) {
            // Note-- do not fire event because model will fire its own event
            leftExpModel.setLanguageObject(exp);
        }
    }

    @Override
    public IExpression getRightExpression() {
        return rightExpModel.getExpression();
    }

    @Override
    public void setRightExpression( IExpression exp ) {
        boolean same;
        IExpression oldExp = rightExpModel.getExpression();
        if (exp == null) {
            same = (oldExp == null);
        } else {
            same = exp.equals(oldExp);
        }
        if (!same) {
            // Note-- do not fire event because model will fire its own event
            rightExpModel.setLanguageObject(exp);
        }
    }

    @Override
	public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
        String type = theEvent.getType();
        String eventType;
        if (type.equals(CompositeLanguageObjectEditorModel.MODEL_CHANGE)) {
            eventType = type;
        } else {
            Object source = theEvent.getSource();
            if (source == leftExpModel) {
                eventType = LEFT_EXPRESSION;
            } else {
                eventType = RIGHT_EXPRESSION;
            }
        }
        fireModelChanged(eventType);
    }

    @Override
    public boolean isComplete() {
        boolean complete = (leftExpModel.isComplete() && rightExpModel.isComplete());
        return complete;
    }

    public CriteriaExpressionEditorModel getLeftExpressionModel() {
        return leftExpModel;
    }

    public CriteriaExpressionEditorModel getRightExpressionModel() {
        return rightExpModel;
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
        // Interface method that is unused.
    }
}
