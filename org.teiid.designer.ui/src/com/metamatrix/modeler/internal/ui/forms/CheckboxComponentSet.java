/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class CheckboxComponentSet extends SimpleComponentSet {
    //
    // Class constants:
    //
    private static final Boolean DEFAULT_SETTING = Boolean.FALSE;

    //
    // Instance constants:
    //
    private final String labelText;
    private final String[] buttonIDs;
    private final String[] buttonTitles;
    private final int orientation;

    //
    // Instance variables:
    //
    private MySelectionListener selList;
    private Object lastSetValue;
    private Button[] btns;
    private Map idToBtn;
    Set selectedIDs;
    private Set returnSet;

    //
    // Constructors:
    //
    public CheckboxComponentSet( String id,
                                 String text ) {
        super(id, null);
        labelText = text;
        buttonIDs = null;
        buttonTitles = null;
        orientation = 0; // not used in single mode
    }

    public CheckboxComponentSet( String id,
                                 String groupTitle,
                                 String[] buttonIDs,
                                 String[] buttonTitles,
                                 int orientation ) {
        super(id, groupTitle);
        this.orientation = orientation;
        labelText = null;
        this.buttonIDs = buttonIDs;
        this.buttonTitles = buttonTitles;
    }

    //
    // Implementation of Abstract methods:
    //
    @Override
    protected void addControls( Composite parent,
                                FormToolkit ftk ) {
        // init:
        init();

        // set up button(s):
        if (buttonIDs != null) {
            // multiple buttons:
            btns = new Button[buttonIDs.length];
            idToBtn = new HashMap(buttonIDs.length);
            selectedIDs = new HashSet(buttonIDs.length);
            returnSet = Collections.unmodifiableSet(selectedIDs);

            Composite grp = ftk.createComposite(parent);
            grp.setLayout(new FillLayout(orientation));

            for (int i = 0; i < buttonIDs.length; i++) {
                String id = buttonIDs[i];
                String text = buttonTitles[i];
                btns[i] = ftk.createButton(grp, text, SWT.CHECK);
                btns[i].setData(id);
                btns[i].addSelectionListener(selList);
                idToBtn.put(id, btns[i]);
            } // endfor -- buttons
        } else {

            // just a single button:
            btns = new Button[] {ftk.createButton(parent, labelText, SWT.CHECK)};
            // TableWrapData twd = new TableWrapData();
            // twd.colspan = 2;
            // btns[0].setLayoutData(twd);
            btns[0].addSelectionListener(selList);
        } // endif -- number of buttons
    }

    private void init() {
        if (selList == null) {
            selList = new MySelectionListener();
        } // endif
    }

    @Override
    protected void addMonitor( ComponentSetMonitor monitor ) {
        init();
        selList.mon = monitor;
    }

    @Override
    protected void removeMonitor( ComponentSetMonitor monitor ) {
        init();
        selList.mon = null;
    }

    public boolean isUserSet() {
        return !(FormUtil.safeEquals(getValue(), lastSetValue));
    }

    public void setValue( Object o ) {
        if (o != null) {
            lastSetValue = o;
        } else {
            o = DEFAULT_SETTING;
        } // endif

        if (o instanceof Boolean) {
            // just change first one:
            Boolean b = (Boolean)o;
            btns[0].setSelection(b.booleanValue());
        } else if (o instanceof Collection) {
            // collection of IDs, so I need to select those present
            Collection toSelect = (Collection)o;
            Iterator itor = toSelect.iterator();
            while (itor.hasNext()) {
                String id = (String)itor.next();
                Button b = (Button)idToBtn.get(id);
                b.setSelection(true);
            } // endwhile

            // ... and deselect the others:
            Set toDeselect = new HashSet(idToBtn.keySet());
            toDeselect.removeAll(toSelect);
            itor = toDeselect.iterator();
            while (itor.hasNext()) {
                String id = (String)itor.next();
                Button b = (Button)idToBtn.get(id);
                b.setSelection(false);
            } // endwhile
        } // endif -- was Boolean or Collection
    }

    public Object getValue() {
        Object rv;
        if (buttonIDs != null) {
            // this is a multi-check, so use the set:
            rv = returnSet;
        } else {
            // this is just a single, use a Boolean
            rv = new Boolean(btns[0].getSelection());
        } // endif

        return rv;
    }

    public void reset() {
        setValue(lastSetValue);
    }

    //
    // Overrides:
    //
    @Override
    public void setEditible( boolean enabled ) {
        super.setEditible(enabled);
        if (btns != null && btns.length > 1) {
            // need to setEnabled on all buttons to update their gui:
            for (int i = 0; i < btns.length; i++) {
                btns[i].setEnabled(enabled);
            } // endfor
        } // endif
    }

    class MySelectionListener extends SelectionAdapter {
        public ComponentSetMonitor mon;

        @Override
        public void widgetSelected( SelectionEvent e ) {
            Object id = e.widget.getData();
            if (id != null) {
                // we must be a multi-set; update the value collection:
                if (((Button)e.widget).getSelection()) {
                    // we are selected:
                    selectedIDs.add(id);
                } else {
                    // we are not selected:
                    selectedIDs.remove(id);
                } // endif
            } // endif

            // now, fire the event if needed:
            if (mon != null) {
                mon.update(new ComponentSetEvent(CheckboxComponentSet.this, false, getValue()));
            } // endif
        }
    }
}
