/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetEvent;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetMonitor;

public class ComboFacetSet extends AbstractFacetSet {
    //
    // Instance variables:
    //
    // private final String[] optionIDs;
    private final String[] optionNames;
    private final String defaultID;
    private Map idToName = new HashMap();
    private Map nameToID = new HashMap();
    private Combo combo;
    private MySelectionListener selList;

    //
    // Constructors:
    //
    public ComboFacetSet( String id,
                          String name,
                          String[] option_ids,
                          String[] option_names,
                          String defaultID ) {
        super(id, name, true, false);
        // optionIDs = option_ids;
        optionNames = option_names;
        this.defaultID = defaultID;
        for (int i = 0; i < option_names.length; i++) {
            String oname = option_names[i];
            String oid = option_ids[i];
            nameToID.put(oname, oid);
            idToName.put(oid, oname);
        } // endfor
    }

    //
    // Implementation of Abstract methods:
    //
    @Override
    protected void addMainControl( Composite parent,
                                   FormToolkit ftk,
                                   ComponentSetMonitor mon ) {
        init();

        combo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
        ftk.adapt(combo);
        combo.setItems(optionNames);
        setMainValue(defaultID);
        combo.addSelectionListener(selList);

        // wire up listener:
        selList.mon = mon;
    }

    @Override
    protected void setMainValue( Object value ) {
        String newVal = (String)idToName.get(value);

        if (newVal != null) {
            combo.setText(newVal);
        } else {
            // set to the default:
            newVal = (String)idToName.get(defaultID);
            if (newVal != null) {
                combo.setText(defaultID);
            } else {
                // give up, just clear out:
                combo.setText(""); //$NON-NLS-1$
            } // endif
        } // endif
    }

    // protected Object getDefaultMainValue() {
    // return defaultID;
    // }

    private void init() {
        if (selList == null) {
            selList = new MySelectionListener();
        } // endif
    }

    Object getValue() {
        return nameToID.get(combo.getText());
    }

    //
    // Inner classes:
    //
    class MySelectionListener extends SelectionAdapter {
        public ComponentSetMonitor mon;

        @Override
        public void widgetSelected( SelectionEvent e ) {
            // now, fire the event if needed:
            if (mon != null) {
                mon.update(new ComponentSetEvent(ComboFacetSet.this, false, getValue()));
            } // endif
        }
    }
}
