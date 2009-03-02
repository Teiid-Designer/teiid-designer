/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
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
public class EditableSpinner extends Composite implements UiConstants {

    /* ----- DESIGN NOTES -----
     * - the composite's vertical scrollbar is being used as the spinner buttons. holding the scrollbar
     *   buttons down (for longer than a click) causes continuous firing of selection events. this is good. 
     * - up and down arrow keys should increment and decrement spinner
     * - the text field is being sized to the biggest value but it is not checking to see if 
     *   that value is too big. there should be a maximum size.
     */

    private static final String PREFIX = I18nUtil.getPropertyPrefix(EditableSpinner.class);

    // used to slow down the spinner when button being held down (150 was TOO slow)
    private static final int SLEEP = 50;

    Text txfValue;

    private int index = -1;

    List possibleValues;

    private int size = -1;

    /** Indicates if the spinner should wrap from last value to first value (and vice versa). */
    private boolean wrap = true;

    protected boolean bAllowListening = true;

    protected EditableSpinner( Composite theParent ) {
        super(theParent, SWT.V_SCROLL);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        setLayout(layout);

        // construct text field
        txfValue = new Text(this, SWT.BORDER /*| SWT.READ_ONLY */);

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

        // DO NOT wire the Text events here, since this constructor DOES NOT install possible vals.
        // We'll do it in the setPossibleValues method.
    }

    /**
     * Construct an instance of Spinner using the given values.
     * 
     * @param theParent the parent container
     * @param thePossibleValues the list of values
     */
    public EditableSpinner( Composite theParent,
                            List thePossibleValues ) {
        this(theParent);

        if ((thePossibleValues != null) && !thePossibleValues.isEmpty()) {
            setPossibleValues(thePossibleValues);
            handleUpSelected();
        } else {
            throw new IllegalStateException(Util.getString(PREFIX + "nullOrEmptyList")); //$NON-NLS-1$
        }

        // wire the Text at the end of this ctor, since it installs possible vals
        registerControls();
    }

    public void setEditable( boolean b ) {
        txfValue.setEditable(b);
    }

    public void addSelectionListener( SelectionListener theListener ) {
        getVerticalBar().addSelectionListener(theListener);
    }

    public void addModifyListener( ModifyListener theListener ) {
        txfValue.addModifyListener(theListener);
    }

    /**
     * Obtains the current spinner value. The value will be one of the values from the list that the spinner was constructed with.
     * 
     * @return the current value
     */
    public Object getValue() {
        if (index > -1 && index < size) {
            return possibleValues.get(index);
        }

        return possibleValues.get(0);
    }

    /**
     * Obtains the current spinner value as text.
     * 
     * @return the current value as text
     */
    public String getValueText() {
        return getValue().toString();
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

    protected void setPossibleValues( List thePossibleValues ) {

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

        // set tooltip based on values
        txfValue.setToolTipText(Util.getString(PREFIX + "text.tip", //$NON-NLS-1$
                                               new Object[] {new Integer(size), this.possibleValues.get(0),
                                                   this.possibleValues.get(size - 1)}));

        // wire the Text events at the end of this method
        registerControls();
    }

    public void setValue( Object theValue ) {
        Object currentText = txfValue.getText();

        if (!theValue.toString().equals(currentText)) {
            int temp = possibleValues.indexOf(theValue);

            if (temp != -1) {
                index = temp;
                updateSpinner(index);
            }
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
                // System.out.println("[EditableSpinner.updateSpinner] theValueIndex is: " + theValueIndex );

                String s = possibleValues.get(theValueIndex).toString();
                txfValue.setText(s);
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

    protected void handleVerifyText( VerifyEvent ve ) {
        if (!bAllowListening) {
            // System.out.println("[EditableSpinner.handleVerifyText] bAllowListening False, bailing out " );
            return;
        }

        // must turn OFF listening during this method because it calls setText
        // which will cause us to loop back to this method infinitely
        bAllowListening = false;

        String text = createPossibleNewText(txfValue.getText(), ve);
        if (!"".equals(text)) { //$NON-NLS-1$
            if (!verifyNewText(text)) {
                ve.doit = false;
            }
        }
        bAllowListening = true;
        return;
    }

    protected boolean verifyNewText( String newText ) {
        Integer val = null;
        try {
            val = new Integer(newText);
            if (!this.possibleValues.contains(val)) {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    protected void handleModifyText( ModifyEvent e ) {
        if (!bAllowListening) {
            // System.out.println("[EditableSpinner.handleModifyText] bAllowListening False, bailing out " );
            return;
        }

        // must turn OFF listening during this method because it calls setText
        // which will cause us to loop back to this method infinitely
        bAllowListening = false;

        String text = txfValue.getText();
        if (!"".equals(text)) {//$NON-NLS-1$ 
            try {
                Integer iNumber = new Integer(text);
                index = this.possibleValues.indexOf(iNumber);
                setValue(iNumber);
            } catch (NumberFormatException nfe) {
                // This should only occur with a string of "" due to the above validation.
                nfe.printStackTrace();
                index = 0;
                setValue(this.possibleValues.get(0));
            }
        }

        // System.out.println("[EditableSpinner.handleModifyText] About to set bAllowListening back to TRUE " );
        bAllowListening = true;
    }

    private void registerControls() {

        txfValue.addVerifyListener(new VerifyListener() {
            public void verifyText( VerifyEvent theEvent ) {

                handleVerifyText(theEvent);
            }
        });

        txfValue.addModifyListener(new ModifyListener() {

            public void modifyText( ModifyEvent theEvent ) {

                handleModifyText(theEvent);
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

        txfValue.addFocusListener(new FocusListener() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
                if (!verifyNewText(txfValue.getText())) {
                    txfValue.setText(possibleValues.get(0).toString());
                }
            }
        });

    }

    public String createPossibleNewText( String oldText,
                                         VerifyEvent ve ) {
        if (oldText.length() == ve.start) {
            return oldText + ve.text;
        }

        StringBuffer buffer = new StringBuffer();
        if (ve.start > 0) {
            buffer.append(oldText.substring(0, ve.start));
        }
        buffer.append(ve.text);
        if (ve.end < (oldText.length())) {
            buffer.append(oldText.substring(ve.end, oldText.length()));
        }
        return buffer.toString();
    }
}
