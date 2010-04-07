/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.actions.VdbExecutor;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;

/**
 * @since 4.2
 */
public class ConnectorBindingsDialog extends ExtendedTitleAreaDialog implements DqpUiConstants, IChangeListener {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ConnectorBindingsDialog.class);

    private Button btnOk;

    private final File vdbFile;

    private final Vdb vdb;

    private ConnectorBindingsPanel pnlBindings;

    private final VdbExecutor validator;

    private final Collection<IChangeListener> changeListenerList = new ArrayList<IChangeListener>(2);

    public ConnectorBindingsDialog( final Shell theParentShell,
                                    final File theVdbFile,
                                    final Vdb vdb,
                                    final VdbExecutionValidator theValidator ) {
        super(theParentShell, DqpUiPlugin.getDefault());

        Assertion.isNotNull(theVdbFile);
        Assertion.isNotNull(vdb);

        this.vdbFile = theVdbFile;
        this.vdb = vdb;
        this.validator = new VdbExecutor(this.vdb, theValidator);
    }

    public void addIChangeListener( final IChangeListener listener ) {
        if (!changeListenerList.contains(listener)) changeListenerList.add(listener);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     * @since 4.3
     */
    @Override
    protected Button createButton( final Composite theParent,
                                   final int theId,
                                   final String theLabel,
                                   final boolean theDefaultButton ) {
        final Button btn = super.createButton(theParent, theId, theLabel, theDefaultButton);

        if (theId == IDialogConstants.OK_ID) {
            this.btnOk = btn;
            this.btnOk.setEnabled(false);
            this.btnOk.setToolTipText(UTIL.getStringOrKey(I18N_PREFIX + "btnOk.tip")); //$NON-NLS-1$
        }

        return btn;
    }

    @Override
    protected Control createDialogArea( final Composite parent ) {
        final Composite composite = (Composite)super.createDialogArea(parent);
        this.pnlBindings = new ConnectorBindingsPanel(parent, this.vdbFile, this.vdb);
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

    public VDBDefn getVdbDefn() {
        return (this.pnlBindings == null) ? null : this.pnlBindings.getVdbDefn();
    }

    public boolean hasVdbDefnChanges() {
        return (this.pnlBindings == null) ? false : this.pnlBindings.hasVdbDefnChanges();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 4.3
     */
    @Override
    protected void okPressed() {
        if (hasVdbDefnChanges()) this.pnlBindings.save();

        super.okPressed();
    }

    public void removeIChangeListener( final IChangeListener listener ) {
        changeListenerList.remove(listener);
    }

    public void saveVdbDefn() {
        if (this.pnlBindings != null) this.pnlBindings.save();
    }

    /**
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 4.3
     */
    public void stateChanged( final IChangeNotifier theSource ) {
        for (final IChangeListener iChangeListener : changeListenerList)
            iChangeListener.stateChanged(theSource);
        updateState();
    }

    void updateState() {
        final IStatus status = this.validator.canExecute();
        final String message = status.getMessage();
        setMessage(message, UiUtil.getDialogMessageType(status));
        this.btnOk.setEnabled(status.getSeverity() != IStatus.ERROR);
    }
}
