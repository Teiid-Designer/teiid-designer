/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jface.viewers.TreeViewer;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.query.ui.builder.util.ElementViewerFactory;
import org.teiid.query.ui.builder.util.ICriteriaStrategy;

/**
 * SetCriteriaEditorModel
 *
 * @since 8.0
 */
public class SetCriteriaEditorModel extends AbstractPredicateCriteriaTypeEditorModel
    implements ILanguageObjectEditorModelListener {
    private final static String PREFIX = I18nUtil.getPropertyPrefix(SetCriteriaEditorModel.class);
    private final static String[] OPERATORS = new String[] {Util.getString(PREFIX + "in") //$NON-NLS-1$
    };
    
    public final static String EXPRESSION = "SET CRITERIA EXPRESSION"; //$NON-NLS-1$
    public final static String SUBTYPE_CHANGED = "SET CRITERIA SUBTYPE CHANGED"; //$NON-NLS-1$
    public final static String VALUES = "SET CRITERIA VALUES"; //$NON-NLS-1$
    public final static String COMMAND = "SET CRITERIA SUBQUERY COMMAND"; //$NON-NLS-1$

    public final static int LIST = 1;
    public final static int SUBQUERY = 2;
    private final static int INITIAL_TYPE = LIST;

    private final ISetCriteria emptySetCriteria;
    private final ISubquerySetCriteria emptySubquerySetCriteria;
   
    private CriteriaExpressionEditorModel expModel;
    private Collection values = new ArrayList();
    private int curType = INITIAL_TYPE; // LIST or SUBQUERY
    private ICriteriaStrategy criteriaStrategy = null;
    private ICommand subqueryCommand;
    private Object curSubquerySelection = null;

    public SetCriteriaEditorModel( CriteriaExpressionEditorModel model ) {
        super(ISetCriteria.class);
        this.expModel = model;
        this.expModel.addModelListener(this);
        
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        emptySetCriteria = factory.createSetCriteria();
        emptySubquerySetCriteria = factory.createSubquerySetCriteria();
        subqueryCommand = emptySubquerySetCriteria.getCommand();
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
    public ILanguageObject getLanguageObject() {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        
        if (curType == LIST) {
            ISetCriteria setCriteria = factory.createSetCriteria();
            setCriteria.setExpression(expModel.getExpression());
            setCriteria.setValues(values);
            return setCriteria;
        }
        // must be SUBQUERY
        ISubquerySetCriteria subquerySetCriteria = factory.createSubquerySetCriteria();
        subquerySetCriteria.setExpression(expModel.getExpression());
        subquerySetCriteria.setCommand((IQueryCommand)subqueryCommand);
        return subquerySetCriteria;
    }

    @Override
    public void setLanguageObject( ILanguageObject obj ) {
        super.setLanguageObject(obj);
        if (obj == null) {
            clear();
        } else {
            if (obj instanceof ISetCriteria) {
                setCurType(LIST);
                ISetCriteria curSetCriteria = (ISetCriteria)obj;
                setExpression(curSetCriteria.getExpression());
                setValues(curSetCriteria.getValues());
            } else { // must be SUBQUERY
                setCurType(SUBQUERY);
                ISubquerySetCriteria curSubquerySetCriteria = (ISubquerySetCriteria)obj;
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
            setExpression(emptySetCriteria.getExpression());
            setValues(emptySetCriteria.getValues());
        } else {
            setCommand(emptySubquerySetCriteria.getCommand());
        }

        notifyListeners = true;
        super.clear();
    }

    @Override
    public void save() {
        super.save();
        expModel.save();
    }

    public void setExpression( IExpression exp ) {
        boolean same;
        IExpression oldExp = expModel.getExpression();
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

    public IExpression getExpression() {
        return expModel.getExpression();
    }

    @Override
    public void setLeftExpression( IExpression exp ) {
        setExpression(exp);
    }

    @Override
    public IExpression getLeftExpression() {
        return getExpression();
    }

    @Override
    public void setRightExpression( IExpression exp ) {
        // Unused abstract method
    }

    @Override
    public IExpression getRightExpression() {
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

    public void addValue( ILanguageObject newValue ) {
        if (!this.values.contains(newValue)) {
            this.values.add(newValue);
            fireModelChanged(VALUES);
        }
    }

    public void replaceValue( ILanguageObject oldValue,
                              ILanguageObject newValue ) {
        this.values.remove(oldValue);
        this.values.add(newValue);
        fireModelChanged(VALUES);
    }

    public void setCommand( ICommand cmd ) {
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

    public ICommand getCommand() {
        return subqueryCommand;
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
            ICommand cmd = criteriaStrategy.getCommand(selection);
            setCommand(cmd);
        } else {
            setCommand(emptySubquerySetCriteria.getCommand());
        }
    }

    public Object getSubquerySelection() {
        return curSubquerySelection;
    }

    private boolean subqueryIsComplete() {
        boolean complete = false;
        if (subqueryCommand != null) {
            complete = (!subqueryCommand.equals(emptySubquerySetCriteria.getCommand()));
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
