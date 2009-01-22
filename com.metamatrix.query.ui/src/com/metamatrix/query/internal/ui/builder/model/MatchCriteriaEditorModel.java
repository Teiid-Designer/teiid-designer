/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.util.BuilderUtils;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.MatchCriteria;
import com.metamatrix.query.sql.symbol.Expression;

/**
 * MatchCriteriaEditorModel
 */
public class MatchCriteriaEditorModel extends AbstractPredicateCriteriaTypeEditorModel 
		implements ILanguageObjectEditorModelListener {
	private final static String PREFIX = I18nUtil.getPropertyPrefix(
			MatchCriteriaEditorModel.class);
	private final static String[] OPERATORS = new String[] {
				Util.getString(PREFIX + "like") //$NON-NLS-1$
			};
	private final static MatchCriteria EMPTY_MATCH_CRITERIA = new MatchCriteria();
	
	public final static String LEFT_EXPRESSION = 
			"MATCH CRITERIA LEFT EXPRESSION"; //$NON-NLS-1$
	public final static String RIGHT_EXPRESSION = 
			"MATCH CRITERIA RIGHT EXPRESSION"; //$NON-NLS-1$
	public final static String ESCAPE_CHAR =
			"MATCH CRITERIA ESCAPE CHAR"; //$NON-NLS-1$
			
	private CriteriaExpressionEditorModel leftExpModel;
	private CriteriaExpressionEditorModel rightExpModel;
	private char escapeChar;
		
	public MatchCriteriaEditorModel(CriteriaExpressionEditorModel left, 
			CriteriaExpressionEditorModel right) {
		super(MatchCriteria.class);
		this.leftExpModel = left;
		this.rightExpModel = right;
		this.leftExpModel.addModelListener(this);
		this.rightExpModel.addModelListener(this);
	}
	
	public MatchCriteriaEditorModel() {
		this(new CriteriaExpressionEditorModel(), new CriteriaExpressionEditorModel());
	}
	
	@Override
    public LanguageObject getLanguageObject() {
		MatchCriteria matchCriteria = new MatchCriteria();
		matchCriteria.setLeftExpression(leftExpModel.getExpression());
		matchCriteria.setRightExpression(rightExpModel.getExpression());
		matchCriteria.setEscapeChar(escapeChar);
		return matchCriteria;
	}
	
	public void setEscapeChar(char escChar) {
		boolean same = (this.escapeChar == escChar);
		if (!same) {
			this.escapeChar = escChar;
			if (BuilderUtils.isDebugLogging() || BuilderUtils.isEventLogging()) {
				Util.print(this, "setEscapeChar(), firing model changed event of type ESCAPE_CHAR, new char is " + this.escapeChar); //$NON-NLS-1$
			}
			fireModelChanged(ESCAPE_CHAR);
		}
	}
	
	public char getEscapeChar() {
		return this.escapeChar;
	}
		
	@Override
    public void setLanguageObject(LanguageObject obj) {
		super.setLanguageObject(obj);
		MatchCriteria curMatchCriteria;
		if (obj == null) {
			clear();
		} else {
			curMatchCriteria = (MatchCriteria)obj;
			Expression newLeftExpression = curMatchCriteria.getLeftExpression();
			setLeftExpression(newLeftExpression);
			Expression newRightExpression = curMatchCriteria.getRightExpression();
			setRightExpression(newRightExpression);
			char newEscapeChar = curMatchCriteria.getEscapeChar();
			setEscapeChar(newEscapeChar);
		}
	}
	
	@Override
    public void clear() {
        notifyListeners = false;
        
		setLeftExpression(EMPTY_MATCH_CRITERIA.getLeftExpression());
		setRightExpression(EMPTY_MATCH_CRITERIA.getRightExpression());
		setEscapeChar(EMPTY_MATCH_CRITERIA.getEscapeChar());
        
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
    public Expression getLeftExpression() {
		return leftExpModel.getExpression();
	}
	
	@Override
    public void setLeftExpression(Expression exp) {
		boolean same;
		Expression oldExp = leftExpModel.getExpression();
		if (exp == null) {
			same = (oldExp == null);
		} else {
			same = exp.equals(oldExp);
		}
		if (!same) {
			//Note-- do not fire event because model will fire its own event
			leftExpModel.setLanguageObject(exp);
		}
	}
	
	@Override
    public Expression getRightExpression() {
		return rightExpModel.getExpression();
	}
	
	@Override
    public void setRightExpression(Expression exp) {
		boolean same;
		Expression oldExp = rightExpModel.getExpression();
		if (exp == null) {
			same = (oldExp == null);
		} else {
			same = exp.equals(oldExp);
		}
		if (!same) {
			//Note-- do not fire event because model will fire its own event
			rightExpModel.setLanguageObject(exp);
		}
	}
	
	public void modelChanged(LanguageObjectEditorModelEvent theEvent) {
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
			if (BuilderUtils.isDebugLogging() || BuilderUtils.isEventLogging()) {
				Util.print(this, "modelChanged(), firing event of type " + eventType); //$NON-NLS-1$
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
    public void setCurrentOperator(String op) {
		//Interface method that is unused.
	}
	
	@Override
    public boolean hasChanged() {
		boolean changed = super.hasChanged();
		if (BuilderUtils.isDebugLogging() || BuilderUtils.isTraceLogging()) {
			Util.print(this, "hasChanged() returning " + changed); //$NON-NLS-1$
		}
		return changed;
	}
}
