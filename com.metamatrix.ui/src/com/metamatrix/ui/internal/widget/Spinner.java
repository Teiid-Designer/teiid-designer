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
package com.metamatrix.ui.internal.widget;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * The <code>Spinner</code> class uses up/down arrows to scroll through a list of values. The list of values is set at
 * construction. Spinners by default wrap in both directions. Therefore, when going up once after the last value, the first value
 * is displayed (behaves same way in reverse direction).
 */
public class Spinner extends Composite implements UiConstants {

    /* ----- DESIGN NOTES -----
     * - the composite's vertical scrollbar is being used as the spinner buttons. holding the scrollbar
     *   buttons down (for longer than a click) causes continuous firing of selection events. this is good. 
     * - up and down arrow keys should increment and decrement spinner
     * - the text field is being sized to the biggest value but it is not checking to see if 
     *   that value is too big. there should be a maximum size.
     */

    private static final String PREFIX = I18nUtil.getPropertyPrefix(Spinner.class);

    // used to slow down the spinner when button being held down (150 was TOO SLOW)
    private static final int SLEEP = 50;

    Text txfValue;

    private int index = -1;

    List possibleValues;

    private int size = -1;

    /** Indicates if the spinner should wrap from last value to first value (and vice versa). */
    private boolean wrap = true;

    protected Spinner( Composite theParent ) {
        super(theParent, SWT.V_SCROLL);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        setLayout(layout);

        // construct text field
        txfValue = new Text(this, SWT.BORDER | SWT.READ_ONLY);

        // wire the vertical scrollbar to the up and down handlers
        getVerticalBar().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                if (theEvent.detail == SWT.ARROW_UP) {
                    handleUpSelected();
                } else if (theEvent.detail == SWT.ARROW_DOWN) {
                    handleDownSelected();
                }
            }
        });
    }

    /**
     * Construct an instance of Spinner using the given values.
     * 
     * @param theParent the parent container
     * @param thePossibleValues the list of values
     */
    public Spinner( Composite theParent,
                    List thePossibleValues ) {
        this(theParent);

        if ((thePossibleValues != null) && !thePossibleValues.isEmpty()) {
            setPossibleValues(thePossibleValues);
            handleUpSelected();
        } else {
            throw new IllegalStateException(Util.getString(PREFIX + "nullOrEmptyList")); //$NON-NLS-1$
        }
    }

    public void addSelectionListener( SelectionListener theListener ) {
        getVerticalBar().addSelectionListener(theListener);
    }

    /**
     * Obtains the current spinner value. The value will be one of the values from the list that the spinner was constructed with.
     * 
     * @return the current value
     */
    public Object getValue() {
        return possibleValues.get(index);
    }

    /**
     * Obtains the current spinner value as text.
     * 
     * @return the current value as text
     */
    public String getValueText() {
        return getValue().toString();
    }

    /**
     * Returns the List of possible values spinner was constructed with.
     * 
     * @return the list of values
     */

    public List getPossibleValues() {
        return possibleValues;
    }

    /**
     * Returns the lowest possible value spinner was constructed with.
     * 
     * @return the lowest value
     */

    public Object getLowerBound() {
        return possibleValues.get(0);
    }

    /**
     * Returns the highest possible value spinner was constructed with.
     * 
     * @return the highestt value
     */

    public Object getUpperBound() {
        return possibleValues.get(possibleValues.size() - 1);
    }

    protected void handleDownSelected() {
        --index;

        if (index < 0) {
            index = (wrap) ? size - 1 : 0;
        }

        updateSpinner(index);
    }

    protected void handleUpSelected() {
        ++index;
        int lastIndex = size - 1;

        if (index > lastIndex) {
            index = (wrap) ? 0 : lastIndex;
        }

        updateSpinner(index);
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setPossibleValues( List thePossibleValues ) {
        possibleValues = thePossibleValues;
        size = possibleValues.size();

        // find largest length value in order to size text field
        int length = 0;
        Object value = null;

        for (int i = 0; i < size; i++) {
            int valueLength = possibleValues.get(i).toString().length();

            if (valueLength > length) {
                length = valueLength;
                value = possibleValues.get(i);
            }
        }

        GridData gd = new GridData();
        gd.verticalSpan = 2;

        txfValue.setText(value.toString());
        gd.widthHint = txfValue.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        txfValue.setLayoutData(gd);

        // clear after setting size
        txfValue.setText(""); //$NON-NLS-1$

        // with new possible values, the old index may be outside the range;
        // fix it:
        if (index < 0 || index > possibleValues.size()) {
            index = 0;
            updateSpinner(index);
        }

    }

    public void setValue( Object theValue ) {
        int temp = possibleValues.indexOf(theValue);

        if (temp != -1) {
            index = temp;
            updateSpinner(index);
        }
    }

    public void setWrap( boolean theWrapFlag ) {
        wrap = theWrapFlag;
    }

    protected void updateSpinner( final int theValueIndex ) {
        // since this is called by the listener which uses another thread,
        // make sure in UI thread
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                txfValue.setText(possibleValues.get(theValueIndex).toString());
                txfValue.setSelection(txfValue.getCharCount());
                txfValue.update();

                // sleep to slow down the spinner
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException theEvent) {
                }
            }
        });
    }

    @Override
    public void setEnabled( boolean b ) {
        super.setEnabled(b);

        if (b) {
            txfValue.setBackground(UiUtil.getSystemColor(SWT.COLOR_WHITE));
        } else {
            txfValue.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        }
    }

}
