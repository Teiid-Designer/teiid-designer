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
package com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;

/**
 * @since 5.0
 */
public class NewConnectorBindingDialog extends ExtendedTitleAreaDialog implements IChangeListener {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(NewConnectorBindingDialog.class);
    private static final int WIDTH = 610;
    private static final int HEIGHT = 500;

    private Button btnOk;

    private NewConnectorBindingPanel pnlBindings;

    private ComponentTypeID initialConnectorType;

    private Collection changeListenerList = new ArrayList(2);

    public NewConnectorBindingDialog( Shell theParentShell ) {
        super(theParentShell, DqpUiPlugin.getDefault());
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite mainComposite = (Composite)super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        mainComposite.setLayout(gridLayout);
        gridLayout.numColumns = 1;

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = WIDTH;
        gd.heightHint = HEIGHT;
        mainComposite.setLayoutData(gd);

        this.pnlBindings = new NewConnectorBindingPanel(mainComposite);
        this.pnlBindings.addChangeListener(this);
        this.pnlBindings.setSaveOnChange(true);
        this.pnlBindings.setFocus();
        if (initialConnectorType != null) {
            pnlBindings.setConnectorType(initialConnectorType);
        }
        getShell().setText(DqpUiConstants.UTIL.getString(I18N_PREFIX + "name")); //$NON-NLS-1$
        setTitle(DqpUiConstants.UTIL.getString(I18N_PREFIX + "title")); //$NON-NLS-1$

        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                updateState();
            }
        });

        return mainComposite;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     * @since 4.3
     */
    @Override
    protected Button createButton( Composite theParent,
                                   int theId,
                                   String theLabel,
                                   boolean theDefaultButton ) {
        Button btn = super.createButton(theParent, theId, theLabel, theDefaultButton);

        if (theId == IDialogConstants.OK_ID) {
            this.btnOk = btn;
            this.btnOk.setEnabled(false);
            this.btnOk.setToolTipText(DqpUiConstants.UTIL.getString(I18N_PREFIX + "ok")); //$NON-NLS-1$
        }

        return btn;
    }

    public void addIChangeListener( IChangeListener listener ) {
        if (!changeListenerList.contains(listener)) {
            changeListenerList.add(listener);
        }
    }

    public void removeIChangeListener( IChangeListener listener ) {
        changeListenerList.remove(listener);
    }

    /**
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 4.3
     */
    public void stateChanged( IChangeNotifier theSource ) {
        for (Iterator iter = changeListenerList.iterator(); iter.hasNext();) {
            ((IChangeListener)iter.next()).stateChanged(theSource);
        }
        updateState();
    }

    void updateState() {
        if (this.btnOk != null) {
            IStatus status = this.pnlBindings.getStatus();
            setMessage(status.getMessage(), status.getSeverity());

            this.btnOk.setEnabled(status.getSeverity() == IStatus.OK);
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 4.3
     */
    @Override
    protected void okPressed() {
        this.pnlBindings.save();
        this.pnlBindings.internalDispose();
        super.okPressed();
    }

    public ConnectorBinding getNewConnectorBinding() {
        return this.pnlBindings.getConnectorBinding();
    }

    public void setConnectorType( ComponentTypeID type ) {
        initialConnectorType = type;
    }
}
