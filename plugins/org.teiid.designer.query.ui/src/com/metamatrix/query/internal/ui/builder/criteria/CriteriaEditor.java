/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.criteria;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.model.CompositeLanguageObjectEditorModel;
import com.metamatrix.query.internal.ui.builder.model.CriteriaEditorModel;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener;
import com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.ui.builder.AbstractCompositeLanguageObjectEditor;
import com.metamatrix.query.ui.builder.ILanguageObjectEditor;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * CriteriaEditor
 */
public class CriteriaEditor extends AbstractCompositeLanguageObjectEditor {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(CriteriaEditor.class);

    private ViewController controller;

    private IPredicateCriteriaTypeEditor defaultEditor;

    private Map leftControls;

    private StackLayout leftStack;

    CriteriaEditorModel model;

    private Map rightControls;

    private StackLayout rightStack;

    private Map typeEditorMap;

    private Combo cbxOperator;

    private Composite leftEditorContent;
    private Composite rightEditorContent;

    private Composite pnlLeft;

    private Composite pnlRight;

    public CriteriaEditor( Composite theParent,
                           CriteriaEditorModel theModel ) {
        super(theParent, Criteria.class, theModel);

        model = theModel;
        controller = new ViewController();
        model.addModelListener(controller);

        // fill combo with operators from model
        cbxOperator.setItems(model.getOperators());

        // start the controller
        controller.initialize();
    }

    /**
     * @see com.metamatrix.query.ui.builder.AbstractCompositeLanguageObjectEditor#createEditors(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected List createEditors( Composite theParent ) {
        typeEditorMap = new HashMap();

        leftControls = new HashMap();
        leftStack = new StackLayout();

        rightControls = new HashMap();
        rightStack = new StackLayout();

        final SashForm editorSplitter = WidgetFactory.createSplitter(theParent, SWT.HORIZONTAL);

        leftEditorContent = WidgetFactory.createPanel(editorSplitter, SWT.NONE, GridData.FILL_BOTH, 1, 1);

        pnlLeft = new Composite(leftEditorContent, SWT.NONE);
        pnlLeft.setLayoutData(new GridData(GridData.FILL_BOTH));
        pnlLeft.setLayout(leftStack);

        rightEditorContent = WidgetFactory.createPanel(editorSplitter, SWT.NONE, GridData.FILL_BOTH, 1, 2);

        cbxOperator = WidgetFactory.createCombo(rightEditorContent, SWT.BORDER | SWT.READ_ONLY);
        cbxOperator.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleOperatorSelected();
            }
        });

        pnlRight = new Composite(rightEditorContent, SWT.NONE);
        pnlRight.setLayoutData(new GridData(GridData.FILL_BOTH));
        pnlRight.setLayout(rightStack);

        CriteriaEditorModel model = (CriteriaEditorModel)getModel();

        IPredicateCriteriaTypeEditor[] criteriaEditors = new IPredicateCriteriaTypeEditor[] {
            new CompareCriteriaEditor(theParent, model.getCompareCriteriaEditorModel()),
            new IsNullCriteriaEditor(theParent, model.getIsNullCriteriaEditorModel()),
            new MatchCriteriaEditor(theParent, model.getMatchCriteriaEditorModel()),
            new SetCriteriaEditor(theParent, model.getSetCriteriaEditorModel())};

        for (int i = 0; i < criteriaEditors.length; i++) {
            Class criteriaType = criteriaEditors[i].getEditorType();
            typeEditorMap.put(criteriaType, criteriaEditors[i]);

            // create left UI and cache it
            Control leftControl = criteriaEditors[i].createLeftComponent(pnlLeft);

            if (leftControl == null) {
                leftControl = new Composite(pnlLeft, SWT.NONE);
            }

            leftControls.put(criteriaType, leftControl);

            // create right UI
            Control rightControl = criteriaEditors[i].createRightComponent(pnlRight);

            if (rightControl == null) {
                rightControl = new Composite(pnlRight, SWT.NONE);
            }

            rightControls.put(criteriaType, rightControl);

            if (i == 0) {
                defaultEditor = criteriaEditors[i];
            }
        }

        return Arrays.asList(criteriaEditors);
    }

    void displayModelChangeUi() {
        Class criteriaType = model.getCurrentModel().getModelType();

        // change left panel if necessary
        Control control = (Control)leftControls.get(criteriaType);

        if (control != leftStack.topControl) {
            leftStack.topControl = control;
            pnlLeft.layout();
        }

        // change right panel if necessary
        control = (Control)rightControls.get(criteriaType);

        if (control != rightStack.topControl) {
            rightStack.topControl = control;
            pnlRight.layout();
        }

        setCurrentEditor((ILanguageObjectEditor)typeEditorMap.get(criteriaType));
    }

    void displayOperatorUi() {

        String operator = model.getOperator();
        cbxOperator.setText(operator);
    }

    /**
     * @see com.metamatrix.query.ui.builder.AbstractCompositeLanguageObjectEditor#getDefaultEditor()
     */
    @Override
    protected ILanguageObjectEditor getDefaultEditor() {
        return defaultEditor;
    }

    /**
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(PREFIX + "title"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        return Util.getString(PREFIX + "tip"); //$NON-NLS-1$
    }

    void handleOperatorSelected() {
        model.setOperator(cbxOperator.getText());
        cbxOperator.setToolTipText(getCurrentEditor().getToolTipText());
    }

    @Override
    public void setEnabled( boolean theEnableFlag ) {
        super.setEnabled(theEnableFlag);

        if (theEnableFlag) {
            WidgetUtil.enable(leftEditorContent);
            WidgetUtil.enable(rightEditorContent);
        } else {
            WidgetUtil.disable(leftEditorContent);
            WidgetUtil.disable(rightEditorContent);
        }
    }

    /**
     * The <code>ViewController</code> class is a view controller for the <code>ConstantEditor</code>.
     */
    class ViewController implements ILanguageObjectEditorModelListener {

        public void initialize() {
            // get first operator and criteria editor to show
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    modelChanged(new LanguageObjectEditorModelEvent(model, CompositeLanguageObjectEditorModel.MODEL_CHANGE));
                }
            });
        }

        /**
         * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            String type = theEvent.getType();

            if (type.equals(LanguageObjectEditorModelEvent.SAVED)) {
                modelChanged(new LanguageObjectEditorModelEvent(model, CompositeLanguageObjectEditorModel.MODEL_CHANGE));
            } else if (type.equals(CriteriaEditorModel.OPERATOR)) {
                displayOperatorUi();
            } else if (type.equals(CompositeLanguageObjectEditorModel.MODEL_CHANGE)) {
                displayModelChangeUi();
                displayOperatorUi();
            }
        }

    }

}
