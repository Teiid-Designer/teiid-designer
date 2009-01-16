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
package com.metamatrix.modeler.relationship.ui.navigation;

import java.util.Iterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * NavigationFocusComboBox
 */
public class NavigationFocusComboBox extends ControlContribution implements NavigationListener {

    private NavigationView view;
    Combo cbx;

    /**
     * Construct an instance of NavigationFocusComboBox.
     */
    public NavigationFocusComboBox( NavigationView view ) {
        super("myId"); //$NON-NLS-1$
        this.view = view;
        view.addNavigationListener(this);
    }

    /**
     * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createControl( Composite parent ) {
        cbx = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);

        cbx.setToolTipText(UiConstants.Util.getString("NavigationFocusComboBox.tooltip")); //$NON-NLS-1$
        cbx.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                comboBoxPressed();
            }
        });
        loadItems(view.getCurrentNavigationContext());
        cbx.setSize(300, 20);
        return cbx;
    }

    public Combo getControl() {
        return cbx;
    }

    public void loadItems( final NavigationContext context ) {
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                cbx.removeAll();
                if (context != null) {
                    NavigationNode node = context.getFocusNode();
                    cbx.add(node.getLabel());
                    cbx.setToolTipText(node.getPathInModel());
                    Iterator iter = context.getNonFocusNodes().iterator();
                    while (iter.hasNext()) {
                        node = (NavigationNode)iter.next();
                        cbx.add(node.getLabel());
                    }
                    cbx.select(0);
                }
            }
        });
    }

    void comboBoxPressed() {
        int index = cbx.getSelectionIndex();
        if (index != 0) {
            Object o = view.getCurrentNavigationContext().getNonFocusNodes().get(index - 1);
            if (o instanceof NavigationNode) {
                view.setCurrentObject((NavigationNode)o);
            } else if (o instanceof EObject) {
                view.setCurrentObject((EObject)o);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.relationship.ui.navigation.NavigationListener#navigationChanged(com.metamatrix.modeler.relationship.NavigationContext)
     */
    public void navigationChanged( NavigationContext newContext ) {
        loadItems(newContext);
    }

    public ControlContribution getLabel() {

        return new ControlContribution("myId") { //$NON-NLS-1$
            @Override
            protected Control createControl( Composite parent ) {
                String labelString = UiConstants.Util.getString("NavigationFocusComboBox.label"); //$NON-NLS-1$
                return WidgetFactory.createLabel(parent, GridData.HORIZONTAL_ALIGN_BEGINNING, labelString);
            }
        };

    }

}
