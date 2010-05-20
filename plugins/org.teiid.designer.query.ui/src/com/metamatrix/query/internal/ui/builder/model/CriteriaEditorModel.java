/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.teiid.core.util.Assertion;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.symbol.Expression;

/**
 * CriteriaEditorModel
 */
public class CriteriaEditorModel extends CompositeLanguageObjectEditorModel {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(CriteriaEditorModel.class);

    // event types
    public static final String OPERATOR = "OPERATOR"; //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private CompareCriteriaEditorModel compareModel;

    private IsNullCriteriaEditorModel isNullModel;

    private MatchCriteriaEditorModel matchModel;

    private SetCriteriaEditorModel setModel;

    private Class criteriaType;

    private Map typeModelMap;

    private Map typeOperatorMap;

    /** The default operator. Not set until a model is added. */
    private String defaultOperator;

    /** The current operator. */
    private String operator;

    /** The collection of all valid operators. */
    private String[] operators;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public CriteriaEditorModel() {
        this(new CompareCriteriaEditorModel(), new IsNullCriteriaEditorModel(), new MatchCriteriaEditorModel(),
             new SetCriteriaEditorModel());
    }

    public CriteriaEditorModel( CompareCriteriaEditorModel theCompareCriteriaModel,
                                IsNullCriteriaEditorModel theIsNullCriteriaModel,
                                MatchCriteriaEditorModel theMatchCriteriaModel,
                                SetCriteriaEditorModel theSetCriteriaModel ) {
        super(Criteria.class);

        typeModelMap = new HashMap();
        typeOperatorMap = new HashMap();

        compareModel = theCompareCriteriaModel;
        addModel(compareModel);

        isNullModel = theIsNullCriteriaModel;
        addModel(isNullModel);

        matchModel = theMatchCriteriaModel;
        addModel(matchModel);

        setModel = theSetCriteriaModel;
        addModel(setModel);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.CompositeLanguageObjectEditorModel#addModel(com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel)
     */
    @Override
    public void addModel( ILanguageObjectEditorModel theModel ) {
        super.addModel(theModel);

        // cache operators and type for each model
        AbstractPredicateCriteriaTypeEditorModel model = (AbstractPredicateCriteriaTypeEditorModel)theModel;
        typeModelMap.put(model.getModelType(), model);
        addOperators(model);
    }

    private void addOperators( AbstractPredicateCriteriaTypeEditorModel theModel ) {
        String[] modelOperators = theModel.getOperators();
        typeOperatorMap.put(theModel.getModelType(), modelOperators);

        if (operators == null) {
            operators = new String[0];
        }

        String[] temp = new String[operators.length + modelOperators.length];

        // modify array to include new operators
        System.arraycopy(operators, 0, temp, 0, operators.length);
        System.arraycopy(modelOperators, 0, temp, operators.length, modelOperators.length);

        operators = temp;

        // if default operator and/or operator not set set them
        if (defaultOperator == null) {
            defaultOperator = modelOperators[0];
            setOperator(defaultOperator);
        }
    }

    public CompareCriteriaEditorModel getCompareCriteriaEditorModel() {
        return compareModel;
    }

    public Criteria getCriteria() {
        return (Criteria)getLanguageObject();
    }

    public Class getCriteriaType() {
        return criteriaType;
    }

    private Class getCriteriaType( String theOperator ) {
        Class result = null;
        Iterator itr = typeOperatorMap.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry)itr.next();
            String[] operators = (String[])entry.getValue();

            if (Arrays.asList(operators).contains(theOperator)) {
                result = (Class)entry.getKey();
                break;
            }
        }

        return result;
    }

    private AbstractPredicateCriteriaTypeEditorModel getModel( String theOperator ) {
        Class type = getCriteriaType(theOperator);
        return (AbstractPredicateCriteriaTypeEditorModel)typeModelMap.get(type);
    }

    public IsNullCriteriaEditorModel getIsNullCriteriaEditorModel() {
        return isNullModel;
    }

    public MatchCriteriaEditorModel getMatchCriteriaEditorModel() {
        return matchModel;
    }

    public String getOperator() {
        AbstractPredicateCriteriaTypeEditorModel model = (AbstractPredicateCriteriaTypeEditorModel)getCurrentModel();
        operator = model.getCurrentOperator();

        return operator;
    }

    public String[] getOperators() {
        return operators;
    }

    public SetCriteriaEditorModel getSetCriteriaEditorModel() {
        return setModel;
    }

    private boolean isValidOperator( String theOperator ) {
        CoreArgCheck.isNotNull(theOperator); // should never fail
        Assertion.isNotEqual(0, theOperator.length()); // should never fail

        boolean result = false;
        Iterator itr = typeOperatorMap.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry)itr.next();
            String[] operators = (String[])entry.getValue();

            if (Arrays.asList(operators).contains(theOperator)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.CompositeLanguageObjectEditorModel#setCurrentModel(com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel)
     */
    @Override
    public void setCurrentModel( ILanguageObjectEditorModel theModel ) {
        // AbstractPredicateCriteriaTypeEditorModel currentModel = (AbstractPredicateCriteriaTypeEditorModel)getCurrentModel();
        // Expression leftExpression = currentModel.getLeftExpression();
        // Expression rightExpression = currentModel.getRightExpression();

        super.setCurrentModel(theModel);

        // if (currentModel != theModel) {
        // theModel.setLeftExpression(leftExpression);
        // theModel.setRightExpression(rightExpression);
        // }
    }

    public void setOperator( String theOperator ) {
        if (isValidOperator(theOperator)) {
            if ((getOperator() == null) || !operator.equals(theOperator)) {
                operator = theOperator;

                // need to set current model type if necessary
                AbstractPredicateCriteriaTypeEditorModel newModel = getModel(theOperator);
                AbstractPredicateCriteriaTypeEditorModel currentModel = (AbstractPredicateCriteriaTypeEditorModel)getCurrentModel();

                if (currentModel != newModel) {
                    // save expressions from model being swapped out
                    Expression leftExpression = currentModel.getLeftExpression();
                    Expression rightExpression = currentModel.getRightExpression();

                    setCurrentModel(newModel); // newModel is now the current model

                    // copy old models expressions
                    newModel.setLeftExpression(leftExpression);
                    newModel.setRightExpression(rightExpression);
                }

                newModel.setCurrentOperator(theOperator);
            }
        } else {
            CoreArgCheck.isTrue(false, Util.getString(PREFIX + "invalidOperator", //$NON-NLS-1$
                                                      new Object[] {theOperator}));
        }
    }

}
