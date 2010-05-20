/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jface.viewers.TreeViewer;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import com.metamatrix.query.internal.ui.builder.util.ICriteriaStrategy;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.AbstractSetCriteria;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 * SetCriteriaEditorModel
 */
public class SetCriteriaEditorModel extends AbstractPredicateCriteriaTypeEditorModel
    implements ILanguageObjectEditorModelListener {
    private final static String PREFIX = I18nUtil.getPropertyPrefix(SetCriteriaEditorModel.class);
    private final static String[] OPERATORS = new String[] {Util.getString(PREFIX + "in") //$NON-NLS-1$
    };
    private final static SetCriteria EMPTY_SET_CRITERIA = new SetCriteria();
    private final static SubquerySetCriteria EMPTY_SUBQUERY_SET_CRITERIA = new SubquerySetCriteria();
    public final static String EXPRESSION = "SET CRITERIA EXPRESSION"; //$NON-NLS-1$
    public final static String SUBTYPE_CHANGED = "SET CRITERIA SUBTYPE CHANGED"; //$NON-NLS-1$
    public final static String VALUES = "SET CRITERIA VALUES"; //$NON-NLS-1$
    public final static String COMMAND = "SET CRITERIA SUBQUERY COMMAND"; //$NON-NLS-1$

    public final static int LIST = 1;
    public final static int SUBQUERY = 2;
    private final static int INITIAL_TYPE = LIST;

    private CriteriaExpressionEditorModel expModel;
    private Collection values = new ArrayList();
    private Command subqueryCommand = EMPTY_SUBQUERY_SET_CRITERIA.getCommand();
    private int curType = INITIAL_TYPE; // LIST or SUBQUERY
    private ICriteriaStrategy criteriaStrategy = null;
    private Object curSubquerySelection = null;

    public SetCriteriaEditorModel( CriteriaExpressionEditorModel model ) {
        super(AbstractSetCriteria.class);
        this.expModel = model;
        this.expModel.addModelListener(this);
    }

    public SetCriteriaEditorModel() {
        this(new CriteriaExpressionEditorModel());
    }

    public void setViewer( TreeViewer viewer ) {
        criteriaStrategy = ElementViewerFactory.getCriteriaStrategy(viewer);
    }

    public String getInvalidSelectionMessage() {
        String msg = criteriaStrategy.getInvalidMessage(curSubquerySelection);
        return msg;
    }

    public void setCurType( int type ) {
        if (this.curType != type) {
            this.curType = type;
            fireModelChanged(SUBTYPE_CHANGED);
        }
    }

    public int getCurType() {
        return this.curType;
    }

    @Override
    public LanguageObject getLanguageObject() {
        if (curType == LIST) {
            SetCriteria setCriteria = new SetCriteria();
            setCriteria.setExpression(expModel.getExpression());
            setCriteria.setValues(values);
            return setCriteria;
        }
        // must be SUBQUERY
        SubquerySetCriteria subquerySetCriteria = new SubquerySetCriteria();
        subquerySetCriteria.setExpression(expModel.getExpression());
        subquerySetCriteria.setCommand(subqueryCommand);
        return subquerySetCriteria;
    }

    @Override
    public void setLanguageObject( LanguageObject obj ) {
        super.setLanguageObject(obj);
        if (obj == null) {
            clear();
        } else {
            if (obj instanceof SetCriteria) {
                setCurType(LIST);
                SetCriteria curSetCriteria = (SetCriteria)obj;
                setExpression(curSetCriteria.getExpression());
                setValues(curSetCriteria.getValues());
            } else { // must be SUBQUERY
                setCurType(SUBQUERY);
                SubquerySetCriteria curSubquerySetCriteria = (SubquerySetCriteria)obj;
                setExpression(curSubquerySetCriteria.getExpression());
                setCommand(curSubquerySetCriteria.getCommand());
            }
        }
    }

    @Override
    public void clear() {
        notifyListeners = false;

        setCurType(INITIAL_TYPE);
        if (curType == LIST) {
            setExpression(EMPTY_SET_CRITERIA.getExpression());
            setValues(EMPTY_SET_CRITERIA.getValues());
        } else {
            setCommand(EMPTY_SUBQUERY_SET_CRITERIA.getCommand());
        }

        notifyListeners = true;
        super.clear();
    }

    @Override
    public void save() {
        super.save();
        expModel.save();
    }

    public void setExpression( Expression exp ) {
        boolean same;
        Expression oldExp = expModel.getExpression();
        if (exp == null) {
            same = (oldExp == null);
        } else {
            same = exp.equals(oldExp);
        }
        if (!same) {
            // Note-- do not fire event because model will fire its own event
            expModel.setLanguageObject(exp);
        }
    }

    public Expression getExpression() {
        return expModel.getExpression();
    }

    @Override
    public void setLeftExpression( Expression exp ) {
        setExpression(exp);
    }

    @Override
    public Expression getLeftExpression() {
        return getExpression();
    }

    @Override
    public void setRightExpression( Expression exp ) {
        // Unused abstract method
    }

    @Override
    public Expression getRightExpression() {
        // Unused abstract method
        return null;
    }

    public void setValues( Collection vals ) {
        Collection ourValues = vals;
        if (ourValues == null) {
            ourValues = new ArrayList();
        }
        boolean same = sameValues(ourValues);
        // We will set to a copy of the collection
        Iterator it = ourValues.iterator();
        this.values = new ArrayList();
        while (it.hasNext()) {
            this.values.add(it.next());
        }
        if (!same) {
            fireModelChanged(VALUES);
        }
    }

    public void addValue( LanguageObject newValue ) {
        if (!this.values.contains(newValue)) {
            this.values.add(newValue);
            fireModelChanged(VALUES);
        }
    }

    public void replaceValue( LanguageObject oldValue,
                              LanguageObject newValue ) {
        this.values.remove(oldValue);
        this.values.add(newValue);
        fireModelChanged(VALUES);
    }

    public void setCommand( Command cmd ) {
        boolean same;
        if (subqueryCommand == null) {
            // We will always set to changed for null
            same = false;
        } else {
            same = subqueryCommand.equals(cmd);
        }
        this.subqueryCommand = cmd;
        if (!same) {
            fireModelChanged(COMMAND);
        }
    }

    public Command getCommand() {
        return subqueryCommand;
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

    private boolean sameValues( Collection newValues ) {
        boolean same = false;
        if (values.size() == newValues.size()) {
            Map foundMap = new HashMap();
            Iterator it = values.iterator();
            boolean continuing = true;
            while (it.hasNext() && continuing) {
                Object value = it.next();
                if (foundMap.get(value) != null) {
                    continuing = false;
                } else {
                    if (newValues.contains(value)) {
                        foundMap.put(value, new Boolean(true));
                    } else {
                        continuing = false;
                    }
                }
            }
            same = continuing;
        }
        return same;
    }

    @Override
    public boolean isComplete() {
        boolean complete;
        if (curType == LIST) {
            complete = (expModel.isComplete() && (values.size() > 0));
        } else {
            complete = (expModel.isComplete() && subqueryIsComplete());
        }
        return complete;
    }

    public void setSubquerySelection( Object selection ) {
        curSubquerySelection = selection;
        if (curSubquerySelection != null) {
            Command cmd = criteriaStrategy.getCommand(selection);
            setCommand(cmd);
        } else {
            setCommand(EMPTY_SUBQUERY_SET_CRITERIA.getCommand());
        }
    }

    public Object getSubquerySelection() {
        return curSubquerySelection;
    }

    private boolean subqueryIsComplete() {
        boolean complete = false;
        if (subqueryCommand != null) {
            complete = (!subqueryCommand.equals(EMPTY_SUBQUERY_SET_CRITERIA.getCommand()));
        }
        return complete;
    }

    public CriteriaExpressionEditorModel getExpressionModel() {
        return expModel;
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
