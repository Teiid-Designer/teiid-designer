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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetEvent;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetMonitor;
import com.metamatrix.modeler.internal.ui.forms.DialogProvider;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;

public class TextFacetSet extends AbstractFacetSet {

    private final int myStyle;
    final DialogProvider provider;
    Text text;
    private MyModifyListener modList;

    public TextFacetSet( String id,
                         String labelName,
                         int style,
                         DialogProvider dialogProvider,
                         boolean needsDescription ) {
        super(id, labelName, true, needsDescription);
        myStyle = style;
        provider = dialogProvider;
    }

    @Override
    protected void addMainControl( Composite parent,
                                   FormToolkit ftk,
                                   ComponentSetMonitor mon ) {
        // init:
        modList = new MyModifyListener();

        // set up text:
        text = ftk.createText(parent, null, myStyle);
        TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
        text.setLayoutData(twd);

        if ((myStyle & SWT.MULTI) != 0) {
            // was multi, make height taller:
            twd.heightHint = 50;
        } // endif

        text.addModifyListener(modList);

        // set up launch button:
        Button b = ftk.createButton(parent, provider.getLaunchButtonText(), SWT.NONE);
        b.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                provider.showDialog(text.getShell(), text.getText());
                if (!provider.wasCancelled()) {
                    setMainValue(provider.getValue());
                } // endif
            }
        });
        twd = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP);
        b.setLayoutData(twd);

        // wire up listeners:
        modList.mon = mon;
    }

    @Override
    protected void setMainValue( Object value ) {
        if (value != null) {
            text.setText((String)value);
        } else {
            text.setText(""); //$NON-NLS-1$
        } // endif
    }

    /** We override super here to add another control */
    @Override
    public int getControlCount() {
        return super.getControlCount() + 1;
    }

    class MyModifyListener implements ModifyListener {
        public ComponentSetMonitor mon;
        private String lastVal;

        public void modifyText( ModifyEvent e ) {
            if (mon != null) {
                String newVal = text.getText();
                if (!FormUtil.safeEquals(newVal, lastVal)) {
                    mon.update(new ComponentSetEvent(TextFacetSet.this, false, newVal));
                    lastVal = newVal;
                } // endif
            } // endif
        }
    }
}
