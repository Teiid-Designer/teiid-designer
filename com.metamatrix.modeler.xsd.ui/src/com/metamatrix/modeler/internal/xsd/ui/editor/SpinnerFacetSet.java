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
package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetEvent;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetMonitor;

public class SpinnerFacetSet extends AbstractFacetSet {

    private static final InclusiveInteger DEFAULT_VALUE = new InclusiveInteger(0, true);

    Spinner sp;
    Button incl;
    private MyListener myList;
    InclusiveInteger val;
    private final boolean needsInclusive;
    private final int min;
    private final int max;

    public SpinnerFacetSet( String id,
                            String labelName,
                            boolean needsInclusiveBox,
                            int min,
                            int max ) {
        super(id, labelName, true, true);
        this.needsInclusive = needsInclusiveBox;
        this.min = min;
        this.max = max;
    }

    @Override
    protected void addMainControl( Composite parent,
                                   FormToolkit ftk,
                                   ComponentSetMonitor mon ) {
        // initialize:
        val = new InclusiveInteger(DEFAULT_VALUE);
        myList = new MyListener();

        // spinner:
        sp = new Spinner(parent, SWT.WRAP | SWT.BORDER);
        sp.setMinimum(min);
        sp.setMaximum(max);
        ftk.adapt(sp);
        // ftk.adapt(sp.getc)
        sp.addModifyListener(myList);
        // inclusive, if needed:
        if (needsInclusive) {
            incl = ftk.createButton(parent, GUIFacetHelper.getString("SpinnerFacetSet.facetbutton.inclusive"), SWT.CHECK); //$NON-NLS-1$
            incl.addSelectionListener(myList);
        } // endif

        // wire up listener:
        myList.mon = mon;
    }

    @Override
    protected void setMainValue( Object value ) {
        if (value == null || value == DEFAULT_VALUE) {
            val.copyValuesOf(DEFAULT_VALUE);
        } else if (value instanceof InclusiveInteger) {
            val.copyValuesOf((InclusiveInteger)value);
        } else if (value instanceof Integer) {
            Integer i = (Integer)value;
            val.value = i.intValue();
            val.isInclusive = false;
        } // endif

        if (GUIFacetHelper.isReady(sp)) sp.setSelection(val.value);

        if (needsInclusive && GUIFacetHelper.isReady(incl)) {
            incl.setSelection(val.isInclusive);
        } // endif
    }

    /** We override super here to add another control if inclusive was requested */
    @Override
    public int getControlCount() {
        int cc = super.getControlCount();

        if (needsInclusive) {
            cc++;
        } // endif

        return cc;
    }

    class MyListener implements ModifyListener, SelectionListener {
        public ComponentSetMonitor mon;

        public void modifyText( ModifyEvent e ) {
            if (mon != null) {
                sp.getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        if (!sp.isDisposed()) {
                            int newVal = sp.getSelection();
                            if (val.value != newVal) {
                                val.value = newVal;
                                fireUpdate();
                            } // endif
                        } // endif
                    }
                });
            } // endif
        }

        public void widgetSelected( SelectionEvent e ) {
            if (mon != null) {
                val.isInclusive = incl.getSelection();
                fireUpdate();
            } // endif
        }

        void fireUpdate() {
            // make clone of value:
            mon.update(new ComponentSetEvent(SpinnerFacetSet.this, false, val.cloneValue()));
        }

        public void widgetDefaultSelected( SelectionEvent e ) {
        } // do nothing
    }
}
