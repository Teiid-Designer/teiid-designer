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

package com.metamatrix.modeler.internal.ui.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class FormTextObjectEditor {

    private static final String LINK_VALUE = "val"; //$NON-NLS-1$
    private static final String LINK_CHANGE = "chg"; //$NON-NLS-1$
    private static final String HTML_LINK_CHANGE_BEGIN = " <a nowrap=\"true\" href=\"" + LINK_CHANGE + "\">"; //$NON-NLS-1$ //$NON-NLS-2$
    public static final String HTML_LINK_VAL_BEGIN = "<a nowrap=\"true\" href=\"" + LINK_VALUE + "\">"; //$NON-NLS-1$ //$NON-NLS-2$
    public static final String HTML_LINK_END = "</a>"; //$NON-NLS-1$

    private ScrolledForm toReflow;
    private FormText text;
    private boolean editible;
    private Object value;
    private final String addText;
    private final String changeText;
    private final boolean valueClickable;
    private IHyperlinkListener myListener;

    public FormTextObjectEditor( String addText,
                                 String changeText,
                                 boolean valueClickable ) {
        this.addText = addText;
        this.changeText = changeText;
        this.valueClickable = valueClickable;
        myListener = new MyHyperlinkListener();
    }

    public FormText getFormText() {
        return text;
    }

    public void setEditible( boolean editible ) {
        if (editible != this.editible) {
            // state has changed:
            this.editible = editible;
            updateText();
        } // endif
    }

    public void setValue( Object newValue ) {
        if (value != newValue) {
            value = newValue;
            updateText();
        } // endif
    }

    public Object getValue() {
        return value;
    }

    /** access for non-form composites */
    public void addControl( Composite parent,
                            int style ) {
        text = new FormText(parent, style);
        text.addHyperlinkListener(myListener);

        updateText();
    }

    public Control addControl( ScrolledForm toReflow,
                               Composite parent,
                               FormToolkit ftk ) {
        this.toReflow = toReflow;
        text = ftk.createFormText(parent, false);
        text.addHyperlinkListener(myListener);

        updateText();

        return text;
    }

    /** Update the visual display. */
    public void updateText() {
        if (text != null && !text.isDisposed()) {
            String html = getHTMLText(value, editible);
            text.setText(html, true, false);
            if (toReflow != null) {
                toReflow.reflow(true);
            } // endif
        } // endif
    }

    /**
     * Action to perform on the clicking of add or change. Default implementation does nothing.
     */
    protected Object changeValue( Object startingValue ) {
        return startingValue;
    }

    /**
     * Action to perform on the clicking of the value. Default implementation does nothing.
     */
    protected void valueClicked( Object value ) {
    }

    private String getHTMLText( Object value,
                                boolean editible ) {
        StringBuffer sb = new StringBuffer(FormUtil.HTML_BEGIN);
        if (value != null) {
            // display value:
            if (valueClickable) sb.append(HTML_LINK_VAL_BEGIN);
            sb.append(getDisplayString(value));
            if (valueClickable) sb.append(HTML_LINK_END);
            if (editible) {
                sb.append(HTML_LINK_CHANGE_BEGIN);
                sb.append(changeText);
                sb.append(HTML_LINK_END);
            } // endif
        } else {
            // no value; prompt to add (if desired):
            if (editible && addText != null) {
                sb.append(HTML_LINK_CHANGE_BEGIN);
                sb.append(addText);
                sb.append(HTML_LINK_END);
            } // endif
        } // endif
        sb.append(FormUtil.HTML_END);

        return sb.toString();
    }

    /**
     * Get the user-visible version of value. Value must not be null.
     * 
     * @param value must not be null
     * @return
     */
    protected String getDisplayString( Object value ) {
        return value.toString();
    }

    class MyHyperlinkListener implements IHyperlinkListener {
        public void linkEntered( HyperlinkEvent e ) {
            // do nothing for now
        }

        public void linkExited( HyperlinkEvent e ) {
            // do nothing for now
        }

        public void linkActivated( HyperlinkEvent e ) {
            Object link = e.getHref();
            if (LINK_CHANGE.equals(link)) {
                setValue(changeValue(getValue()));
            } else if (LINK_VALUE.equals(link)) {
                valueClicked(getValue());
            } // endif
        }

    }
}
