/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.query.internal.ui.builder.criteria;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener;
import com.metamatrix.query.internal.ui.builder.model.IsNullCriteriaEditorModel;
import com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent;
import com.metamatrix.query.internal.ui.builder.util.BuilderUtils;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.IsNullCriteria;
import com.metamatrix.query.sql.symbol.Expression;

public class IsNullCriteriaEditor extends AbstractPredicateCriteriaTypeEditor {
    private final static String PREFIX = I18nUtil.getPropertyPrefix(IsNullCriteriaEditor.class);

    private CriteriaExpressionEditor editor;
    private Control component;
    private ViewController viewController;
    IsNullCriteriaEditorModel theModel;

    public IsNullCriteriaEditor( Composite parent,
                                 IsNullCriteriaEditorModel model ) {
        super(parent, IsNullCriteria.class, model);
        this.theModel = model;
        viewController = new ViewController();
        theModel.addModelListener(viewController);
        viewController.initialize();
    }

    @Override
    public String getToolTipText() {
        String tip = Util.getString(PREFIX + "toolTipText"); //$NON-NLS-1$
        return tip;
    }

    @Override
    public String getTitle() {
        String title = Util.getString(PREFIX + "title"); //$NON-NLS-1$
        return title;
    }

    public Control createLeftComponent( Composite parent ) {
        editor = new CriteriaExpressionEditor(parent, theModel.getExpressionModel());
        component = editor.getUi();
        return component;
    }

    public Control createRightComponent( Composite parent ) {
        // Unused
        return null;
    }

    public Expression getLeftExpression() {
        Expression leftExpression = theModel.getLeftExpression();
        return leftExpression;
    }

    public Expression getRightExpression() {
        // Unused
        return null;
    }

    @Override
    public void setLanguageObject( LanguageObject obj ) {
        Util.traceEntered(this, "setLanguageObject:value=" + obj); //$NON-NLS-1$

        ArgCheck.isInstanceOf(IsNullCriteria.class, obj);
        theModel.setLanguageObject(obj);

        Util.traceExited(this, "setLanguageObject"); //$NON-NLS-1$
    }

    public String[] getOperators() {
        return theModel.getOperators();
    }

    @Override
    public void acceptFocus() {
        editor.acceptFocus();
    }

    public void setOperator( String op ) {
        theModel.setCurrentOperator(op);
    }

    public String getCurrentOperator() {
        return theModel.getCurrentOperator();
    }

    void displayExpression() {
        LanguageObject langObj = theModel.getLeftExpression();
        editor.setLanguageObject(langObj);
    }

    void displayLanguageObjectChange() {
        displayExpression();
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
                public void run() {
                    modelChanged(new LanguageObjectEditorModelEvent(theModel, LanguageObjectEditorModelEvent.SAVED));
                }
            });
        }

        /**
         * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            String type = theEvent.getType();
            if (type.equals(IsNullCriteriaEditorModel.EXPRESSION)) {
                displayExpression();
            } else if (type.equals(LanguageObjectEditorModelEvent.SAVED)) {
                displayLanguageObjectChange();
            }
        }
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
