/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.util;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.builder.ILanguageObjectEditor;
import org.teiid.designer.transformation.ui.builder.expression.ConstantEditor;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModel;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener;
import org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent;
import org.teiid.query.ui.builder.util.BuilderUtils;


/**
 * CompositeEditorMessagePanel
 *
 * @since 8.0
 */
public class CompositeEditorMessagePanel extends Composite implements UiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(CompositeEditorMessagePanel.class);

    // /////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////

    /** Key:Button, Value:ILanguageObjectEditorModel */
    private Map buttonModelMap;

    /** Key:Button, Value:ILanguageObjectEditor */
    Map buttonEditorMap;

    /** The current editor. */
    ILanguageObjectEditor currentEditor;

    /** Key:ILanguageObjectEditorModel, Value:Button */
    Map modelButtonMap;

    /** Handles all listening. */
    private Listener listener;

    private Object langObj;

    // /////////////////////////////////////////////////////////////////////////
    // CONTROLS
    // /////////////////////////////////////////////////////////////////////////

    // private Text txa;
    private CLabel lblMsg;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    public CompositeEditorMessagePanel( Composite theParent ) {
        super(theParent, SWT.NONE);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout(layout);

        buttonModelMap = new HashMap();
        buttonEditorMap = new HashMap();
        modelButtonMap = new HashMap();
        listener = new Listener();

        lblMsg = new CLabel(this, SWT.SHADOW_IN | SWT.WRAP);
        lblMsg.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblMsg.setBackground(UiUtil.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        lblMsg.setForeground(UiUtil.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
    }

    // /////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////

    public void addEditor( Button theButton,
                           ILanguageObjectEditor theEditor ) {
        ILanguageObjectEditorModel model = theEditor.getModel();
        buttonModelMap.put(theButton, model);
        buttonEditorMap.put(theButton, theEditor);
        modelButtonMap.put(model, theButton);
        model.addModelListener(listener);
        theButton.addSelectionListener(listener);
    }

    public void clearMsg() {
        lblMsg.setText(""); //$NON-NLS-1$
    }

    private boolean isConstantEditor() {
        return (currentEditor == null) ? false : (currentEditor instanceof ConstantEditor);
    }

    private boolean isConstant() {
        return (langObj == null) ? false : (langObj instanceof Constant);
    }

    private boolean isElement() {
        return (langObj == null) ? false : (langObj instanceof ElementSymbol);
    }

    private boolean isFunction() {
        return (langObj == null) ? false : (langObj instanceof Function);
    }

    private boolean isReference() {
        return (langObj == null) ? false : (langObj instanceof Reference);
    }

    public void setLanguageObject( Object theLangObj ) {
        langObj = theLangObj;
        setResultMsg();
    }

    /** Constructs then sets the result message. */
    public void setResultMsg() {
        /*
             A = type unchanged,  value unchanged
             B = type unchanged, value changed
             C = type changed, value not selected
             D = type changed, value selected
             E = current value undef, create
         
                editor  | constant | element | function
            node        |          |         |
            -------------------------------------------
            undef       |    E     |    E    |    E
            constant    |   A,B    |   C,D   |   C,D
            element     |   C,D    |   A,B   |   C,D
            function    |   C,D    |   C,D   |   A,B
         */

        Object[] params = null;
        String id = null;

        String nodeType = BuilderUtils.UNDEFINED; // langObj not set yet

        if (isConstant()) {
            nodeType = Util.getString(PREFIX + "constantType"); //$NON-NLS-1$
        } else if (isElement()) {
            nodeType = Util.getString(PREFIX + "elementType"); //$NON-NLS-1$
        } else if (isFunction()) {
            nodeType = Util.getString(PREFIX + "functionType"); //$NON-NLS-1$
        } else if (isReference()) {
            nodeType = Util.getString(PREFIX + "referencyType"); //$NON-NLS-1$
        }

        if (currentEditor == null) {
            lblMsg.setText(""); //$NON-NLS-1$
        } else {
            String title = currentEditor.getTitle();

            if (!nodeType.equals(title)) {
                // msg C,D
                id = (currentEditor.hasChanged()) ? PREFIX + "changeTypeApply.msg" //$NON-NLS-1$
                : PREFIX + "changeType.msg"; //$NON-NLS-1$
                params = new Object[] {nodeType, title};
            } else if (currentEditor.hasChanged()) {
                // msg B
                if (isConstantEditor() && ((ConstantEditor)currentEditor).isConversionType()) {
                    id = PREFIX + "changeConstantTypeValue.msg"; //$NON-NLS-1$
                } else {
                    id = PREFIX + "changeValue.msg"; //$NON-NLS-1$
                }

                params = new Object[] {nodeType};
            } else if (!currentEditor.hasChanged()) {
                // msg A
                id = PREFIX + "unchanged.msg"; //$NON-NLS-1$
                params = new Object[] {nodeType};
            }

            lblMsg.setText(Util.getString(id, params));
        }

    }

    /** Handler for outer class. */
    class Listener extends SelectionAdapter implements ILanguageObjectEditorModelListener {

        @Override
        public void widgetSelected( SelectionEvent theEvent ) {
            Button btn = (Button)theEvent.getSource();

            // only process the selection (not deselection event)
            if (btn.getSelection()) {
                currentEditor = (ILanguageObjectEditor)buttonEditorMap.get(btn);
                setResultMsg();
            }
        }

        /* (non-Javadoc)
         * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        @Override
		public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            ILanguageObjectEditorModel model = (ILanguageObjectEditorModel)theEvent.getSource();
            Button btn = (Button)modelButtonMap.get(model);

            if (btn.getSelection()) {
                currentEditor = (ILanguageObjectEditor)buttonEditorMap.get(btn);
                setResultMsg();
            }
        }

    }

}
