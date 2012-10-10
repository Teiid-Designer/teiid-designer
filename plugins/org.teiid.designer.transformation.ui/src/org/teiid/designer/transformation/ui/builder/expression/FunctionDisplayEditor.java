/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.expression;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectEditor;
import org.teiid.designer.transformation.ui.builder.ExpressionBuilder;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import org.teiid.query.ui.UiPlugin;
import org.teiid.query.ui.builder.model.FunctionDisplayEditorModel;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener;
import org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent;
import org.teiid.query.ui.builder.util.BuilderUtils;


/**
 * FunctionDisplayEditor
 *
 * @since 8.0
 */
public class FunctionDisplayEditor extends AbstractLanguageObjectEditor {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(FunctionDisplayEditor.class);

    private static final int TEXT_AREA_WIDTH_HINT = 150;
    private static final int TEXT_AREA_HEIGHT_HINT = 150;

    private ViewController controller;

    private FunctionDisplayEditorModel model;

    private Composite pnlContent;
    private Button editButton;
    private Text functionText;

    /**
     * Constructs a <code>FunctionDisplayEditor</code> using the given model.
     * 
     * @param theParent the parent container
     * @param theModel the editor's model
     * @throws IllegalArgumentException if any of the parameters are <code>null</code>
     */
    public FunctionDisplayEditor( Composite theParent,
                                  FunctionDisplayEditorModel theModel ) {
        super(theParent, Function.class, theModel);
        controller = new ViewController();
        model = theModel;
        model.addModelListener(controller);

        displayFunction();
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.ILanguageObjectEditor#acceptFocus()
     */
    @Override
    public void acceptFocus() {
        editButton.setFocus();
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectEditor#createUi(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createUi( Composite theParent ) {
        pnlContent = new Composite(theParent, SWT.NONE);
        GridLayout layout = new GridLayout();
        pnlContent.setLayout(layout);
        layout.numColumns = 2;
        GridData pnlData = new GridData(GridData.FILL_BOTH);
        pnlContent.setLayoutData(pnlData);
        editButton = new Button(pnlContent, SWT.PUSH);
        editButton.setText(Util.getString(PREFIX + "edit")); //$NON-NLS-1$
        editButton.setToolTipText(Util.getString(PREFIX + "editToolTipText")); //$NON-NLS-1$
        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                editButtonPressed();
            }
        });
        functionText = new Text(pnlContent, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
        GridData functionTextData = new GridData(GridData.FILL_BOTH);
        functionTextData.widthHint = TEXT_AREA_WIDTH_HINT;
        functionTextData.heightHint = TEXT_AREA_HEIGHT_HINT;
        functionText.setText(BuilderUtils.UNDEFINED);
        functionText.setLayoutData(functionTextData);
    }

    /**
     * Displays the appropriate UI for the current model state.
     */
    void displayFunction() {
        Function function = model.getFunction();
        functionText.setText(SQLStringVisitor.getSQLString(function));
    }

    void editButtonPressed() {
        Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        ExpressionBuilder dialog = new ExpressionBuilder(shell, true);
        dialog.create();
        dialog.setLanguageObject(model.getLanguageObject());
        dialog.open();
        if (dialog.getReturnCode() == Window.OK) {
            LanguageObject modifiedLanguageObject = dialog.getLanguageObject();
            // Inform model that language object has been changed. Model will fire
            // event to listeners, including us.
            model.setLanguageObject(modifiedLanguageObject);
        }
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.ILanguageObjectEditor#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(PREFIX + "title"); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectEditor#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        return Util.getString(PREFIX + "tip"); //$NON-NLS-1$
    }

    @Override
    public boolean isComplete() {
        // Being inserted as aid in debugging. BWP 09/05/03
        boolean complete = super.isComplete();
        return complete;
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.ILanguageObjectEditor#setLanguageObject(org.teiid.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLanguageObject ) {
        if (theLanguageObject == null) {
            clear();
        } else {
            if (!(theLanguageObject instanceof Function)) {
                CoreArgCheck.isTrue((theLanguageObject instanceof Function),
                                    Util.getString(PREFIX + "invalidLanguageObject", //$NON-NLS-1$
                                                   new Object[] {theLanguageObject.getClass().getName()}));
            }

            model.setLanguageObject(theLanguageObject);
        }
    }

    /**
     * The <code>ViewController</code> class is a view controller for the <code>FunctionDisplayEditor</code>.
     */
    class ViewController implements ILanguageObjectEditorModelListener {

        /**
         * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        @Override
		public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            displayFunction();
        }

    }
}
