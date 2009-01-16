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
package com.metamatrix.modeler.internal.dqp.ui.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.actions.VdbExecutor;
import com.metamatrix.modeler.internal.dqp.ui.config.ConnectorBindingsPanel;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

/**
 * @since 4.2
 */
public class ConnectorBindingsDialog extends ExtendedTitleAreaDialog implements DqpUiConstants, IChangeListener {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ConnectorBindingsDialog.class);

    private Button btnOk;

    private File vdbFile;

    private InternalVdbEditingContext vdbContext;

    private ConnectorBindingsPanel pnlBindings;

    private VdbExecutor validator;

    private Collection<IChangeListener> changeListenerList = new ArrayList<IChangeListener>(2);

    public ConnectorBindingsDialog( Shell theParentShell,
                                    File theVdbFile,
                                    InternalVdbEditingContext theContext,
                                    VdbExecutionValidator theValidator ) {
        super(theParentShell, DqpUiPlugin.getDefault());

        Assertion.isNotNull(theVdbFile);
        Assertion.isNotNull(theContext);

        this.vdbFile = theVdbFile;
        this.vdbContext = theContext;
        this.validator = new VdbExecutor(this.vdbContext, theValidator);
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite composite = (Composite)super.createDialogArea(parent);
        this.pnlBindings = new ConnectorBindingsPanel(parent, this.vdbFile, this.vdbContext);
        this.pnlBindings.addChangeListener(this);
        this.pnlBindings.setSaveOnChange(true);
        this.pnlBindings.setFocus();

        getShell().setText(UTIL.getStringOrKey(I18N_PREFIX + "windowTitle")); //$NON-NLS-1$
        setTitle(UTIL.getStringOrKey(I18N_PREFIX + "title")); //$NON-NLS-1$
        setMessage(UTIL.getStringOrKey(I18N_PREFIX + "errorMsg"), IMessageProvider.ERROR); //$NON-NLS-1$

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                updateState();
            }
        });

        return composite;
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
            this.btnOk.setToolTipText(UTIL.getStringOrKey(I18N_PREFIX + "btnOk.tip")); //$NON-NLS-1$
        }

        return btn;
    }

    public VDBDefn getVdbDefn() {
        return (this.pnlBindings == null) ? null : this.pnlBindings.getVdbDefn();
    }

    public void saveVdbDefn() {
        if (this.pnlBindings != null) {
            this.pnlBindings.save();
        }
    }

    public boolean hasVdbDefnChanges() {
        return (this.pnlBindings == null) ? false : this.pnlBindings.hasVdbDefnChanges();
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
        for (Iterator<IChangeListener> iter = changeListenerList.iterator(); iter.hasNext();) {
            iter.next().stateChanged(theSource);
        }
        updateState();
    }

    void updateState() {
        IStatus status = this.validator.canExecute();
        String message = status.getMessage();
        setMessage(message, UiUtil.getDialogMessageType(status));
        this.btnOk.setEnabled(status.getSeverity() != IStatus.ERROR);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 4.3
     */
    @Override
    protected void okPressed() {
        if (hasVdbDefnChanges()) {
            this.pnlBindings.save();
        }

        super.okPressed();
    }
}
