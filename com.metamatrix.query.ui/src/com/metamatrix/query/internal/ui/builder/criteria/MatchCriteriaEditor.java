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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener;
import com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent;
import com.metamatrix.query.internal.ui.builder.model.MatchCriteriaEditorModel;
import com.metamatrix.query.internal.ui.builder.util.BuilderUtils;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.MatchCriteria;
import com.metamatrix.query.sql.symbol.Expression;

/**
 * MatchCriteriaEditor
 */
public class MatchCriteriaEditor extends AbstractPredicateCriteriaTypeEditor {
    private final static String PREFIX = I18nUtil.getPropertyPrefix(MatchCriteriaEditor.class);
    private final static int ESC_CHAR_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .01);

    private MatchCriteria matchCriteria;
    private CriteriaExpressionEditor leftEditor;
    private CriteriaExpressionEditor rightEditor;
    private Composite rightComponent;
    private Control leftComponent;
    private ViewController viewController;
    MatchCriteriaEditorModel theModel;
    private Text escCharText;

    public MatchCriteriaEditor( Composite parent,
                                MatchCriteriaEditorModel model ) {
        super(parent, MatchCriteria.class, model);
        this.theModel = model;
        viewController = new ViewController();
        model.addModelListener(viewController);
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
        leftEditor = new CriteriaExpressionEditor(parent, theModel.getLeftExpressionModel());
        leftComponent = leftEditor.getUi();
        return leftComponent;
    }

    public Control createRightComponent( Composite parent ) {
        rightComponent = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        rightComponent.setLayout(layout);
        rightEditor = new CriteriaExpressionEditor(rightComponent, theModel.getRightExpressionModel());
        GridData rightEditorGridData = new GridData(GridData.FILL_BOTH);
        rightEditor.getUi().setLayoutData(rightEditorGridData);
        Composite escCharPanel = new Composite(rightComponent, SWT.NONE);
        GridLayout escLayout = new GridLayout();
        escCharPanel.setLayout(escLayout);
        escLayout.numColumns = 2;
        Label escCharLabel = new Label(escCharPanel, SWT.NONE);
        escCharLabel.setText(Util.getString(PREFIX + "escapeCharacter")); //$NON-NLS-1$
        escCharText = new Text(escCharPanel, SWT.SINGLE | SWT.BORDER);
        String escCharToolTipText = Util.getString(PREFIX + "escCharToolTipText"); //$NON-NLS-1$
        escCharText.setToolTipText(escCharToolTipText);
        escCharText.setTextLimit(1);
        escCharText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent ev ) {
                escapeCharacterModified();
            }
        });
        GridData escCharGridData = new GridData();
        escCharGridData.widthHint = ESC_CHAR_TEXT_WIDTH;
        escCharText.setLayoutData(escCharGridData);
        return rightComponent;
    }

    public Expression getLeftExpression() {
        Expression leftExpression = null;
        if (matchCriteria != null) {
            leftExpression = matchCriteria.getLeftExpression();
        }
        return leftExpression;
    }

    public Expression getRightExpression() {
        Expression rightExpression = null;
        if (matchCriteria != null) {
            rightExpression = matchCriteria.getRightExpression();
        }
        return rightExpression;
    }

    @Override
    public void setLanguageObject( LanguageObject obj ) {
        ArgCheck.isInstanceOf(MatchCriteria.class, obj);
        matchCriteria = (MatchCriteria)obj;
        if (leftEditor != null) {
            leftEditor.setLanguageObject(getLeftExpression());
        }
        if (rightEditor != null) {
            rightEditor.setLanguageObject(getRightExpression());
        }
    }

    public String[] getOperators() {
        return theModel.getOperators();
    }

    @Override
    public void acceptFocus() {
        leftEditor.acceptFocus();
    }

    public void setOperator( String op ) {
        theModel.setCurrentOperator(op);
    }

    public String getCurrentOperator() {
        return theModel.getCurrentOperator();
    }

    void escapeCharacterModified() {
        String textStr = escCharText.getText();
        if ((textStr != null) && (textStr.length() > 0)) {
            char newChar = escCharText.getText().charAt(0);
            theModel.setEscapeChar(newChar);
        } else {
            if (BuilderUtils.isDebugLogging()) {
                Util.print(this, "escapeCharacterModified(), calling model setEscapseChar()"); //$NON-NLS-1$
            }
            theModel.setEscapeChar(MatchCriteria.NULL_ESCAPE_CHAR);
        }
    }

    void displayEscapeChar() {
        char newChar = theModel.getEscapeChar();
        if (BuilderUtils.isDebugLogging()) {
            Util.print(this, "displayEscapeChar(), char is " + newChar); //$NON-NLS-1$
        }
        String textStr = escCharText.getText();
        char oldChar;
        if ((textStr != null) && (textStr.length() > 0)) {
            oldChar = escCharText.getText().charAt(0);
        } else {
            oldChar = MatchCriteria.NULL_ESCAPE_CHAR;
        }
        if (oldChar != newChar) {
            if (newChar != MatchCriteria.NULL_ESCAPE_CHAR) {
                escCharText.setText(new String(new char[] {newChar}));
            } else {
                escCharText.setText(""); //$NON-NLS-1$
            }
        }
    }

    void displayLeftExpression() {
    }

    void displayRightExpression() {
    }

    void displayLanguageObjectChange() {
        displayLeftExpression();
        displayRightExpression();
        displayEscapeChar();
    }

    @Override
    public boolean hasChanged() {
        boolean changed = super.hasChanged();
        if (BuilderUtils.isDebugLogging() || BuilderUtils.isTraceLogging()) {
            Util.print(this, "hasChanged(), returning " + changed); //$NON-NLS-1$
        }
        return changed;
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
            if (type.equals(MatchCriteriaEditorModel.LEFT_EXPRESSION)) {
                displayLeftExpression();
            } else if (type.equals(MatchCriteriaEditorModel.RIGHT_EXPRESSION)) {
                displayRightExpression();
            } else if (type.equals(MatchCriteriaEditorModel.ESCAPE_CHAR)) {
                displayEscapeChar();
            } else if (type.equals(LanguageObjectEditorModelEvent.SAVED)) {
                displayLanguageObjectChange();
            }
        }
    }
}
