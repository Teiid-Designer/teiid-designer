/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
public class EditConnectorBindingDialog extends ExtendedTitleAreaDialog implements IChangeListener {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(EditConnectorBindingDialog.class);
    private static final int WIDTH = 610;
    private static final int HEIGHT = 500;

    private Button btnOk;

    private EditConnectorBindingPanel pnlBindings;

    private ConnectorBinding connectorBinding;

    private Collection<IChangeListener> changeListenerList = new ArrayList<IChangeListener>(2);

    public EditConnectorBindingDialog( Shell theParentShell,
                                       ConnectorBinding binding ) {
        super(theParentShell, DqpUiPlugin.getDefault());
        this.connectorBinding = binding;
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

        this.pnlBindings = new EditConnectorBindingPanel(mainComposite, connectorBinding);
        this.pnlBindings.addChangeListener(this);
        this.pnlBindings.setSaveOnChange(true);
        this.pnlBindings.setFocus();

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
        for (Iterator<IChangeListener> iter = changeListenerList.iterator(); iter.hasNext();) {
            iter.next().stateChanged(theSource);
        }
        updateState();
    }

    void updateState() {
        IStatus status = this.pnlBindings.getStatus();
        setMessage(status.getMessage(), status.getSeverity());

        this.btnOk.setEnabled(status.getSeverity() == IStatus.OK);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 4.3
     */
    @Override
    protected void okPressed() {
        this.pnlBindings.save();

        super.okPressed();
    }
}
