/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class MultiComponentSet extends SimpleComponentSet {
    //
    // Instance variables:
    //
    private final LinkedComponentSet[] sets;
    private final int orientation;
    private Composite holder;

    //
    // Constructors: 
    //
    public MultiComponentSet( String id,
                              LinkedComponentSet[] sets,
                              int orientation ) {
        super(id, null);
        this.sets = sets;
        this.orientation = orientation;
    }

    //
    // Methods:
    //
    @Override
    protected void addControls( Composite parent,
                                FormToolkit ftk ) {
        int totalColumns;
        if (orientation == SWT.VERTICAL) {
            totalColumns = 1; // starting value

            for (int i = 0; i < sets.length; i++) {
                int controlCount = sets[i].getControlCount();

                if (controlCount > totalColumns) {
                    totalColumns = controlCount;
                } // endif
            } // endfor

        } else {
            // Horizontal orientation:
            totalColumns = 0;

            for (int i = 0; i < sets.length; i++) {
                totalColumns += sets[i].getControlCount();
            } // endfor -- sets
        } // endif -- orientation

        holder = ftk.createComposite(parent);
        if (orientation == SWT.VERTICAL) {
            // vertical layout, use fill just to make everything show up:
            holder.setLayout(new FillLayout(SWT.VERTICAL));

        } else {
            // horizontal layout, use tablewrap:
            TableWrapLayout twl = new TableWrapLayout();
            twl.numColumns = totalColumns;
            twl.bottomMargin = 0;
            twl.leftMargin = 0;
            twl.rightMargin = 0;
            twl.topMargin = 0;
            holder.setLayout(twl);
        } // endif
        holder.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, 2));

        for (int i = 0; i < sets.length; i++) {
            if (orientation == SWT.VERTICAL) {
                // Vertical Orientation:
                // behave "normally":
                sets[i].addFormControls(holder, ftk, totalColumns);

            } else {
                // Horizontal orientation:
                // use exactly the right width so that the component
                //  doesn't try to add a placeholder:
                sets[i].addFormControls(holder, ftk, sets[i].getControlCount());
                

            } // endif -- orientation
        } // endfor

        if (orientation == SWT.VERTICAL) {
            // remove children layout data:
            Control[] controls = holder.getChildren();
            for (int i = 0; i < controls.length; i++) {
                Control c = controls[i];
                c.setLayoutData(null);
            } // endfor
        } // endif
    }

    @Override
    protected void addMonitor(ComponentSetMonitor monitor) {
        for (int i = 0; i < sets.length; i++) {
            sets[i].setMonitor(monitor);
        } // endfor
    }

    @Override
    protected void removeMonitor(ComponentSetMonitor monitor) {
        for (int i = 0; i < sets.length; i++) {
            sets[i].setMonitor(null);
        } // endfor
    }

    public boolean isUserSet() {
        for (int i = 0; i < sets.length; i++) {
            if (sets[i].isUserSet()) return true;
        } // endfor
        
        return false;
    }

    public void setValue(Object o) {
        if (o != null && o.getClass().isArray()) {
            // we have an array:
            Object[] array = (Object[]) o;
            for (int i = 0; i < sets.length; i++) {
                sets[i].setValue(array[i]);
            } // endfor

        } else {
            // o null or not an array, just set the same value into everything:
            for (int i = 0; i < sets.length; i++) {
                sets[i].setValue(o);
            } // endfor
        } // endif
    }

    public void reset() {
        for (int i = 0; i < sets.length; i++) {
            sets[i].reset();
        } // endfor
    }

    @Override
    public void setEditible(boolean enabled) {
        super.setEditible(enabled);
        
        if (holder != null) {
            // stay enabled:
            holder.setEnabled(true);
        } // endif

        for (int i = 0; i < sets.length; i++) {
            sets[i].setEditible(enabled);
        } // endfor
    }

    @Override
    public int getControlCount() {
        return 2;
    }

    @Override
    public Object clone() {
        // need to deep clone my kids, and the sets field is final, so create a new one:
        LinkedComponentSet[] lcss = new LinkedComponentSet[sets.length];

        for (int i = 0; i < sets.length; i++) {
            lcss[i] = sets[i].cloneSet();
        } // endfor

        MultiComponentSet mcs = new MultiComponentSet(getID(), lcss, orientation);
        return mcs;
    }
}
