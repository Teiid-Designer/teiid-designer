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

package com.metamatrix.query.internal.ui.builder.expression;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectEditor;
import com.metamatrix.query.internal.ui.builder.model.ConstantEditorModel;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener;
import com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent;
import com.metamatrix.query.internal.ui.builder.util.BuilderUtils;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.CalendarWidget;

/**
 * ConstantEditor
 */
public final class ConstantEditor extends AbstractLanguageObjectEditor {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ConstantEditor.class);

    private Control currentControl;

    private ViewController controller;

    ConstantEditorModel model;

    private StackLayout stackLayout;

    String validChars; // valid characters for selected type

    private Combo cbxType;

    private CalendarWidget dateWidget;

    private Label lblNull;

    private Label lblType;

    private Composite pnlBoolean;

    private Composite pnlContent;

    private Composite pnlDate;

    private Composite pnlNull;

    private Composite pnlText;

    private Group pnlValues;

    private Button rdbFalse;

    private Button rdbTrue;

    private Text txfValue;

    /**
     * Constructs a <code>ConstantEditor</code> using the given model.
     * 
     * @param theParent the parent container
     * @param theModel the editor's model
     * @throws IllegalArgumentException if any of the parameters are <code>null</code>
     */
    public ConstantEditor( Composite theParent,
                           ConstantEditorModel theModel ) {
        super(theParent, Constant.class, theModel);

        controller = new ViewController();
        model = theModel;
        model.addModelListener(controller);

        // start the controller
        controller.initialize();
    }

    /**
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#acceptFocus()
     */
    @Override
    public void acceptFocus() {
        if (isConversionType()) {
            cbxType.setFocus();
        } else if (currentControl == pnlNull) {
            cbxType.setFocus();
        } else if (currentControl == pnlBoolean) {
            if (rdbTrue.getSelection()) {
                rdbTrue.setFocus();
            } else {
                rdbFalse.setFocus();
            }
        } else if (currentControl == pnlText) {
            txfValue.setFocus();
        } else if (currentControl == pnlDate) {
            dateWidget.setFocus();
        }
    }

    private void constructBooleanWidgets() {
        if (rdbTrue == null) {
            rdbTrue = new Button(pnlBoolean, SWT.RADIO);
            rdbTrue.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.CENTER));
            rdbTrue.setText(Util.getString(PREFIX + "rdbTrue")); //$NON-NLS-1$
            rdbTrue.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent theEvent ) {
                    handleTrueSelected();
                }
            });
            rdbTrue.setSelection(true);

            rdbFalse = new Button(pnlBoolean, SWT.RADIO);
            rdbFalse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.CENTER));
            rdbFalse.setText(Util.getString(PREFIX + "rdbFalse")); //$NON-NLS-1$
            rdbFalse.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent theEvent ) {
                    handleFalseSelected();
                }
            });

            pnlBoolean.pack();
        }
    }

    private void constructDateWidgets() {
        if (dateWidget == null) {
            dateWidget = new CalendarWidget(pnlDate, SWT.NONE, false);
            dateWidget.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleDateChanged();
                }
            });
            dateWidget.setLayoutData(new GridData());

            pnlDate.pack();
        }
    }

    private void constructNullWidgets() {
        if (lblNull == null) {
            lblNull = new Label(pnlNull, SWT.BORDER);
            lblNull.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            lblNull.setText(Util.getString(PREFIX + "txfNull")); //$NON-NLS-1$

            pnlNull.pack();
        }
    }

    private void constructTextWidgets() {
        if (txfValue == null) {
            txfValue = new Text(pnlText, SWT.BORDER);
            txfValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            txfValue.addModifyListener(new ModifyListener() {
                public void modifyText( ModifyEvent theEvent ) {
                    handleTextChange();
                }
            });

            txfValue.addVerifyListener(new VerifyListener() {
                public void verifyText( VerifyEvent theEvent ) {
                    String text = theEvent.text;

                    if ((text != null) && (text.length() > 0)) {
                        for (int size = text.length(), i = 0; i < size; i++) {
                            if ((validChars != null) && (validChars.indexOf(text.charAt(i)) == -1)) {
                                theEvent.doit = false;
                                break;
                            } else if (!model.isValidValue(text)) {
                                theEvent.doit = false;
                                break;
                            }
                        }
                    }
                }
            });

            txfValue.addTraverseListener(new TraverseListener() {
                public void keyTraversed( TraverseEvent theEvent ) {
                    // stops the enter key from putting in invisible characters
                    if (theEvent.detail == SWT.TRAVERSE_RETURN) {
                        theEvent.detail = SWT.TRAVERSE_NONE;
                        theEvent.doit = true;
                    }
                }
            });

            pnlText.pack();
        }
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectEditor#createUi(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createUi( Composite theParent ) {
        pnlContent = new Composite(theParent, SWT.NONE);
        pnlContent.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        pnlContent.setLayout(layout);

        //
        // pnlContent contents
        //
        lblType = new Label(pnlContent, SWT.NONE);
        lblType.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        lblType.setText(Util.getString(PREFIX + "lblType")); //$NON-NLS-1$

        cbxType = new Combo(pnlContent, SWT.BORDER | SWT.READ_ONLY);
        cbxType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbxType.setItems(BuilderUtils.ALL_TYPES);
        cbxType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleTypeSelected();
            }
        });

        pnlValues = new Group(pnlContent, SWT.NONE);
        pnlValues.setText(Util.getString(PREFIX + "pnlValues")); //$NON-NLS-1$
        stackLayout = new StackLayout();
        pnlValues.setLayout(stackLayout);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        pnlValues.setLayoutData(gd);

        //
        // pnlValues contents
        //

        pnlText = new Composite(pnlValues, SWT.NONE);
        pnlText.setLayout(new GridLayout());
        pnlText.setLayoutData(new GridData(GridData.FILL_BOTH));

        // contents of pnlText created in constructTextWidgets()

        pnlNull = new Composite(pnlValues, SWT.NONE);
        pnlNull.setLayout(new GridLayout());
        pnlNull.setLayoutData(new GridData(GridData.FILL_BOTH));

        // contents of pnlNull created in constructNullWidgets()

        pnlBoolean = new Composite(pnlValues, SWT.NONE);
        pnlBoolean.setLayout(new GridLayout());
        pnlBoolean.setLayoutData(new GridData(GridData.FILL_BOTH));

        // contents of pnlBoolean created in constructBooleanWidgets()

        pnlDate = new Composite(pnlValues, SWT.NONE);
        pnlDate.setLayout(new GridLayout());
        pnlDate.setLayoutData(new GridData(GridData.FILL_BOTH));

        // contents of pnlDate created in constructDateWidgets()
    }

    void displayBooleanTypeUi() {
        normalizeTypes();
        boolean value = model.getBoolean();

        constructBooleanWidgets();

        if (rdbTrue.getSelection() != value) {
            if (model.getBoolean()) {
                WidgetUtil.selectRadioButton(rdbTrue);
            } else {
                WidgetUtil.selectRadioButton(rdbFalse);
            }
        }

        showControl(pnlBoolean);
    }

    void displayConversionTypeUi() {
        normalizeTypes();

        showValueLabel(false);
    }

    void displayDateTypeUi() {
        normalizeTypes();
        constructDateWidgets();

        dateWidget.setValue(model.getDate());
        dateWidget.showCalendar(true);
        dateWidget.showTime(false);
        pnlDate.pack();
        showControl(pnlDate);
    }

    void displayNullTypeUi() {
        normalizeTypes();
        constructNullWidgets();

        showControl(pnlNull);
    }

    void displayTextTypeUi() {
        normalizeTypes();
        String value = model.getText();

        if (value == null) {
            value = ""; //$NON-NLS-1$
        }

        constructTextWidgets();

        if (!txfValue.getText().equals(value)) {
            txfValue.setText(value); // causes caret to be placed at the beginning
            txfValue.setSelection(value.length()); // place caret at end
        }

        showControl(pnlText);
    }

    void displayTimeTypeUi() {
        normalizeTypes();
        constructDateWidgets();

        dateWidget.setValue(model.getTime());
        dateWidget.showCalendar(false);
        dateWidget.showTime(true);
        pnlDate.pack();
        showControl(pnlDate);
    }

    void displayTimestampTypeUi() {
        normalizeTypes();
        constructDateWidgets();

        dateWidget.setValue(model.getTimestamp());
        dateWidget.showCalendar(true);
        dateWidget.showTime(true);
        pnlDate.pack();
        showControl(pnlDate);
    }

    void displayTypeUi() {
        normalizeTypes();
        constructTextWidgets();

        String modelType = model.getType();

        txfValue.setTextLimit(BuilderUtils.getTextLimit(modelType));
        cbxType.setText(modelType);
    }

    /**
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(PREFIX + "title"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectEditor#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        return Util.getString(PREFIX + "tip"); //$NON-NLS-1$
    }

    void handleDateChanged() {
        if (dateWidget.isDateWidget()) {
            model.setDate(dateWidget.getDate());
        } else if (dateWidget.isTimeWidget()) {
            model.setTime(dateWidget.getTime());
        } else if (dateWidget.isTimestampWidget()) {
            model.setTimestamp(dateWidget.getTimestamp());
        }
    }

    void handleFalseSelected() {
        // only care about selection, not deselection
        if (rdbFalse.getSelection()) {
            model.setBoolean(false);
        }
    }

    void handleTextChange() {
        // the text entered is set into the model.
        // this causes the view controller to be modified of a text change which sets the text field's text
        model.setText(txfValue.getText());
    }

    void handleTrueSelected() {
        // only care about selection, not deselection
        if (rdbTrue.getSelection()) {
            model.setBoolean(true);
        }
    }

    void handleTypeSelected() {
        int index = cbxType.getSelectionIndex();
        model.setType(cbxType.getItem(index));
    }

    public boolean isConversionType() {
        return model.isConversionType();
    }

    /**
     * The set of available types are different when the constant is a conversion type and when it is not. This method makes sure
     * the types are correct.
     */
    private void normalizeTypes() {
        // processTypeChange = false;
        String selection = cbxType.getText(); // save current selection
        String[] invalidTypes = BuilderUtils.INVALID_CONVERSION_ARG_TYPES;
        boolean conversionType = isConversionType();

        for (int i = 0; i < invalidTypes.length; i++) {
            if (conversionType) {
                if (cbxType.indexOf(invalidTypes[i]) != -1) {
                    cbxType.remove(invalidTypes[i]);
                } else {
                    // assume if one type is not there then all of them are not there
                    break;
                }
            } else {
                if (cbxType.indexOf(invalidTypes[i]) == -1) {
                    cbxType.add(invalidTypes[i]);
                } else {
                    // assume if one type is there then all of them are there
                    break;
                }
            }
        }

        cbxType.setText(selection); // set back to saved selection
        // processTypeChange = true;
    }

    /**
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#setLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLanguageObject ) {
        if (theLanguageObject == null) {
            clear();
        } else {
            Assertion.assertTrue((theLanguageObject instanceof Constant),
                                 Util.getString(PREFIX + "invalidLanguageObject", //$NON-NLS-1$
                                                new Object[] {theLanguageObject.getClass().getName()}));

            model.setLanguageObject(theLanguageObject);
        }
    }

    private void showControl( Control theControl ) {
        showValueLabel(true);

        if (currentControl != theControl) {
            currentControl = theControl;
            stackLayout.topControl = currentControl;
            pnlValues.layout();
        }

        acceptFocus();
    }

    /** The value widgets are shown when not displaying a conversion type. */
    private void showValueLabel( boolean theShowFlag ) {
        pnlValues.setVisible(theShowFlag);
    }

    /**
     * The <code>ViewController</code> class is a view controller for the <code>ConstantEditor</code>.
     */
    class ViewController implements ILanguageObjectEditorModelListener {

        public void initialize() {
            // set first selection
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    modelChanged(new LanguageObjectEditorModelEvent(model, ConstantEditorModel.TYPE));
                }
            });
        }

        /**
         * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            String type = theEvent.getType();

            if (type.equals(LanguageObjectEditorModelEvent.SAVED)) {
                modelChanged(new LanguageObjectEditorModelEvent(model, ConstantEditorModel.TYPE));
            } else if (type.equals(ConstantEditorModel.TYPE)) {
                displayTypeUi();

                if (model.isConversionType()) {
                    displayConversionTypeUi();
                } else if (model.isText()) {
                    modelChanged(new LanguageObjectEditorModelEvent(model, ConstantEditorModel.TEXT));
                } else if (model.isBoolean()) {
                    modelChanged(new LanguageObjectEditorModelEvent(model, ConstantEditorModel.BOOLEAN));
                } else if (model.isNull()) {
                    modelChanged(new LanguageObjectEditorModelEvent(model, ConstantEditorModel.NULL));
                } else if (model.isDate()) {
                    modelChanged(new LanguageObjectEditorModelEvent(model, ConstantEditorModel.DATE));
                } else if (model.isTime()) {
                    modelChanged(new LanguageObjectEditorModelEvent(model, ConstantEditorModel.TIME));
                } else if (model.isTimestamp()) {
                    modelChanged(new LanguageObjectEditorModelEvent(model, ConstantEditorModel.TIMESTAMP));
                }
            } else if (type.equals(ConstantEditorModel.TEXT)) {
                displayTextTypeUi();
            } else if (type.equals(ConstantEditorModel.BOOLEAN)) {
                displayBooleanTypeUi();
            } else if (type.equals(ConstantEditorModel.NULL)) {
                displayNullTypeUi();
            } else if (type.equals(ConstantEditorModel.DATE)) {
                displayDateTypeUi();
            } else if (type.equals(ConstantEditorModel.TIME)) {
                displayTimeTypeUi();
            } else if (type.equals(ConstantEditorModel.TIMESTAMP)) {
                displayTimestampTypeUi();
            }
        }

    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectEditor#getLanguageObject()
     */
    @Override
    public LanguageObject getLanguageObject() {
        return super.getLanguageObject();
    }

}
