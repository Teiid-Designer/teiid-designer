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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * A SimpleFacet is a linkedComponentSet that handles a label and a single control.
 * 
 * @author PForhan
 */
public abstract class SimpleComponentSet implements LinkedComponentSet {
    //
    // Class Constants:
    //
    protected static final Control[] EMPTY_CONTROL_ARRAY = new Control[] {};

    //
    // Instance variables:
    //
    private final String labelText;
    private final String myid;
    private ComponentSetMonitor monitor;
    private Label lbl;
    private Control[] ctrls;
    private ComponentCategory cat;

    //
    // Constructors:
    //
    public SimpleComponentSet( String id,
                               String labelName ) {
        myid = id;
        labelText = labelName;
    }

    //
    // Abstract methods:
    //
    protected abstract void addControls( Composite parent,
                                         FormToolkit ftk );

    protected abstract void addMonitor( ComponentSetMonitor monitor );

    protected abstract void removeMonitor( ComponentSetMonitor monitor );

    //
    // Implementation of LinkedComponentSet methods:
    //
    public String getID() {
        return myid;
    }

    public ComponentCategory getCategory() {
        return cat;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setCategory( ComponentCategory category ) {
        cat = category;
    }

    public int getControlCount() {
        if (labelText == null) {
            // no label:
            return 1;
        } // endif

        // label and control:
        return 2;
    }

    public void addFormControls( Composite parent,
                                 FormToolkit ftk,
                                 int totalColumns ) {
        // capture current state of panel:
        Control[] beforeKids = parent.getChildren();

        // add label if desired:
        if (labelText != null) {
            lbl = ftk.createLabel(parent, labelText);
            TableWrapData twd = new TableWrapData(TableWrapData.RIGHT, TableWrapData.TOP);
            lbl.setLayoutData(twd);
        } // endif

        // add specific children:
        addControls(parent, ftk);

        // capture all added children:
        ctrls = getChildDifference(parent.getChildren(), beforeKids);

        int myColumns = 0;
        for (int i = 0; i < ctrls.length; i++) {
            TableWrapData twd = (TableWrapData)ctrls[i].getLayoutData();

            if (twd != null) {
                myColumns += twd.colspan;
            } else {
                myColumns++;
            } // endif
        } // endfor

        if (myColumns < totalColumns) {
            // we need to add an extra something to absorb the rest of the columns:
            Label comp = ftk.createLabel(parent, null);
            TableWrapData twd = new TableWrapData();
            twd.colspan = totalColumns - myColumns;
            comp.setLayoutData(twd);
            // } else if (myColumns > totalColumns) {
            //            throw new IllegalStateException("Internal: SimpleComponentSet: Too many columns!"); //$NON-NLS-1$
        } // endif

        // this will set the component values, if available:
        reset();
    }

    public static Control[] getChildDifference( Control[] after,
                                                Control[] before ) {
        List big = new ArrayList(after.length);
        big.addAll(Arrays.asList(after));
        List small = Arrays.asList(before);
        big.removeAll(small);
        return (Control[])big.toArray(EMPTY_CONTROL_ARRAY);
    }

    public void setEditible( boolean enabled ) {
        if (lbl != null) {
            lbl.setEnabled(enabled);
        } // endif
        if (ctrls != null) {
            for (int i = 0; i < ctrls.length; i++) {
                ctrls[i].setEnabled(enabled);
            } // endfor
        } // endif
    }

    //
    // Events:
    //
    public void setMonitor( ComponentSetMonitor csl ) {
        if (monitor != null && monitor != csl) {
            // we are unsetting:
            removeMonitor(monitor);
        } // endif
        monitor = csl;
        if (monitor != null) {
            addMonitor(monitor);
        } // endif
    }

    //
    // Overrides:
    //
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public LinkedComponentSet cloneSet() {
        try {
            return (LinkedComponentSet)clone();
        } catch (CloneNotSupportedException e) {
            // should never occur, since LCS extends Cloneable
            return null;
        } // endif
    }
}
