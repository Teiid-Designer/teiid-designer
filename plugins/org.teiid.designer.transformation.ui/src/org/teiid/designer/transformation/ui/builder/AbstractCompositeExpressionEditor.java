/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.transformation.ui.builder.util.CompositeEditorMessagePanel;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.ui.builder.model.CompositeLanguageObjectEditorModel;


/**
 * AbstractCompositeExpressionEditor As editors get added, the editor models are added to the composite editor's model.
 *
 * @since 8.0
 */
public abstract class AbstractCompositeExpressionEditor extends AbstractCompositeLanguageObjectEditor {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private List<Button> editorButtons;

    StackLayout stackLayout; // layout for the editors

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROLS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private Composite pnlButtons;

    Composite pnlEditors;

    private CompositeEditorMessagePanel pnlMessages;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    protected AbstractCompositeExpressionEditor( Composite theParent,
                                                 CompositeLanguageObjectEditorModel theModel ) {
        super(theParent, Expression.class, theModel);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private void createEditorButtons( List<ILanguageObjectEditor> theEditors ) {
        CoreArgCheck.isNotEmpty(theEditors);

        int numEditors = theEditors.size();
        editorButtons = new ArrayList<Button>(numEditors);
        GridLayout gdlayout = (GridLayout)pnlButtons.getLayout();
        gdlayout.numColumns = numEditors;

        for (int i = 0; i < numEditors; i++) {
            ILanguageObjectEditor editor = theEditors.get(i);

            String title = editor.getTitle();
            Button btn = new Button(pnlButtons, SWT.RADIO);
            btn.setText(title);
            btn.setToolTipText(editor.getToolTipText());
            btn.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent theEvent ) {
                    handleEditorSelected(theEvent);
                }
            });

            editorButtons.add(btn);

            // add to message panel
            pnlMessages.addEditor(btn, editor);
        }
    }

    /**
     * Subclasses must implement {@link #createExpressionEditors(Composite)} to create their UI.
     * 
     * @throws com.metamatrix.core.util.AssertionError if editor collection is <code>null</code> or empty
     * @see org.teiid.designer.transformation.ui.builder.AbstractCompositeLanguageObjectEditor#createEditors(org.eclipse.swt.widgets.Composite)
     */
    @Override
    final protected List createEditors( Composite theParent ) {

        pnlButtons = new Composite(theParent, SWT.NONE);
        pnlButtons.setLayout(new GridLayout());

        /*
         * [jh, 7/8/2005] This is truly wacky.  If I remove the following line,
         *    or change it to do 'horizontal align center', which is what I really want,
         *    the 3 buttons disappear again.  OK, Barry says radio buttons on the left
         *    are OK, sort of like Tabs being on the left.
         *    
         */
        pnlButtons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        pnlMessages = new CompositeEditorMessagePanel(theParent);

        ScrolledComposite scroller = new ScrolledComposite(theParent, SWT.H_SCROLL | SWT.V_SCROLL);
        scroller.setLayout(new GridLayout());
        scroller.setLayoutData(new GridData(GridData.FILL_BOTH));

        pnlEditors = new Composite(scroller, SWT.NONE);
        pnlEditors.setLayoutData(new GridData(GridData.FILL_BOTH));
        stackLayout = new StackLayout();
        pnlEditors.setLayout(stackLayout);

        scroller.setContent(pnlEditors);
        Point pt = scroller.getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setMinWidth(pt.x);
        scroller.setMinHeight(pt.y);

        final List editors = createExpressionEditors(pnlEditors);
        createEditorButtons(editors);

        // get first editor to show in stack layout
        Display.getDefault().asyncExec(new Runnable() {
            @Override
			public void run() {
                ILanguageObjectEditor editor = (ILanguageObjectEditor)editors.get(0);
                stackLayout.topControl = editor.getUi();
                WidgetUtil.selectRadioButton(getEditorButton(editor));
                pnlEditors.layout();
                getEditorForFocus().acceptFocus();
            }
        });

        return editors;
    }

    ILanguageObjectEditor getEditorForFocus() {
        return getCurrentEditor();
    }

    /**
     * Subclasses must return a non-empty list of <code>ILanguageObjectEditor</code>s whose editor type is
     * {@link org.teiid.query.sql.symbol.Expression}.
     * 
     * @param theParent the widget container of the editors
     * @return a collection of expression editors
     */
    protected abstract List createExpressionEditors( Composite theParent );

    /**
     * Gets the editor associated with the given button.
     * 
     * @param theButton the button whose editor is being requested
     */
    protected ILanguageObjectEditor getEditor( Button theButton ) {
        return (ILanguageObjectEditor)getEditors().get(editorButtons.indexOf(theButton));
    }

    /**
     * Gets the button associated with the given editor.
     * 
     * @param theEditor the editor whose button is being requested
     */
    protected Button getEditorButton( ILanguageObjectEditor theEditor ) {
        return editorButtons.get(getEditors().indexOf(theEditor));
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getTitle()
     */
    @Override
    public abstract String getTitle();

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getToolTipText()
     */
    @Override
    public abstract String getToolTipText();

    /**
     * Handler for when a different editor's button is selected.
     * 
     * @param theEvent the event being processed
     */
    void handleEditorSelected( SelectionEvent theEvent ) {
        Button btn = (Button)theEvent.getSource();

        // only process the selection (not deselection event)
        if (btn.getSelection()) {
            int index = editorButtons.indexOf(theEvent.getSource());
            ILanguageObjectEditor editor = (ILanguageObjectEditor)getEditors().get(index);
            selectEditor(editor);
        }
    }

    protected void selectEditor( ILanguageObjectEditor editor ) {
        stackLayout.topControl = editor.getUi();
        pnlEditors.layout();
        super.setCurrentEditor(editor); // call super since button is already selected
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.AbstractCompositeLanguageObjectEditor#setCurrentEditor(org.teiid.query.ui.builder.ILanguageObjectEditor)
     */
    @Override
    protected void setCurrentEditor( ILanguageObjectEditor theEditor ) {
        super.setCurrentEditor(theEditor);

        // select programmatically and fire selection event
        WidgetUtil.selectRadioButton(getEditorButton(theEditor));
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.AbstractCompositeLanguageObjectEditor#setLanguageObject(org.teiid.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLanguageObject ) {
        super.setLanguageObject(theLanguageObject);

        pnlMessages.setLanguageObject(theLanguageObject);
    }

    /**
     * Enables/disables the editor/button pair.
     * 
     * @param theEditor the editor whose enabled state is being modified
     * @param theEnableFlag the enabled state to set
     */
    protected void setEditorEnabled( ILanguageObjectEditor theEditor,
                                     boolean theEnableFlag ) {
        theEditor.setEnabled(theEnableFlag);
        getEditorButton(theEditor).setEnabled(theEnableFlag);
    }
}
