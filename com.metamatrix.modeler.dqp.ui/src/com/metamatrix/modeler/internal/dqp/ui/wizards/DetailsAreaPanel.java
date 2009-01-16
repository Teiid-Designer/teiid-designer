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

package com.metamatrix.modeler.internal.dqp.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * The <code>DetailsAreaPanel</code> is where the selected connector type, connector binding, or connector jar import information
 * is displayed.
 *
 * @since 5.5.3
 */
public class DetailsAreaPanel extends Composite {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private final Text textArea;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param parent the panel's container
     * @since 5.5.3
     */
    public DetailsAreaPanel( Composite parent ) {
        super(parent, SWT.NONE);
        setLayout(new GridLayout());
        setLayoutData(new GridData(GridData.FILL_BOTH));

        Group group = WidgetFactory.createGroup(this, I18n.Details, GridData.FILL_BOTH);
        this.textArea = WidgetFactory.createTextBox(group, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP, GridData.FILL_BOTH);
        this.textArea.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        ((GridData)this.textArea.getLayoutData()).heightHint = 70; // make sure text area gets some size
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * @param status the status whose message will be displayed or <code>null</code> to clear
     * @since 5.5.3
     */
    public void setStatus( IStatus status ) {
        String msg = null;

        if (status == null) {
            msg = StringUtil.Constants.EMPTY_STRING;
        } else {
            StringBuffer temp = new StringBuffer();

            if (status.isMultiStatus()) {
                for (IStatus jarStatus : ((MultiStatus)status).getChildren()) {
                    temp.append(jarStatus).append(StringUtil.Constants.NEW_LINE_CHAR);
                }
            } else {
                temp.append(status.getMessage());
            }

            msg = temp.toString();
        }

        this.textArea.setText(msg);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);
        getParent().layout();
    }
}
