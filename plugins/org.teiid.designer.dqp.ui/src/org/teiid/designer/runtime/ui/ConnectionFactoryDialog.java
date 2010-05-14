/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.runtime.ConnectorTemplate;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;

/**
 *
 */
abstract class ConnectionFactoryDialog extends ExtendedTitleAreaDialog implements IChangeListener {

    private final ExecutionAdmin admin;
    private Button btnOk;
    private ConnectionFactoryPanel pnlConnectionFactory;
    private ConnectorType type;

    /**
     * @param shell the parent shell (can be <code>null</code>)
     * @param admin the server execution admin (never <code>null</code>)
     * @param type the initial type selected by the user (can be <code>null</code>)
     */
    public ConnectionFactoryDialog( Shell shell,
                                    ExecutionAdmin admin,
                                    ConnectorType type ) {
        super(shell, DqpUiPlugin.getDefault());
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$
        this.admin = admin;
        this.type = type;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     */
    @Override
    protected Button createButton( Composite theParent,
                                   int theId,
                                   String theLabel,
                                   boolean theDefaultButton ) {
        Button btn = super.createButton(theParent, theId, theLabel, theDefaultButton);

        // disable OK button initially
        if (theId == IDialogConstants.OK_ID) {
            this.btnOk = btn;
            this.btnOk.setEnabled(false);
            this.btnOk.setToolTipText(IDialogConstants.OK_LABEL);
            updateState();
        }

        return btn;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected final Control createDialogArea( Composite parent ) {
        Composite panel = (Composite)super.createDialogArea(parent);
        panel.setLayout(new GridLayout());
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        this.pnlConnectionFactory = createConnectionFactoryPanel(panel);
        getShell().setText(getName());
        setTitle(getTitle());
        return panel;
    }

    /**
     * @param parent the parent panel
     * @return the panel where the connection factory is being created or edited
     */
    protected abstract ConnectionFactoryPanel createConnectionFactoryPanel( Composite parent );

    /**
     * @return the new or modified connector (can be <code>null</code> if called before OK button is enabled)
     */
    public ConnectorTemplate getConnector() {
        return this.pnlConnectionFactory.getConnector();
    }

    /**
     * @return the execution admin (never <code>null</code>)
     */
    protected ExecutionAdmin getAdmin() {
        return this.admin;
    }

    /**
     * @return the initial connector type (can be <code>null</code> when creating a new connector)
     */
    protected ConnectorType getConnectorType() {
        return this.type;
    }

    /**
     * @return the title of the window
     */
    protected abstract String getName();

    /**
     * @return the status indicating the dialog is complete and valid
     */
    protected abstract IStatus getOkStatus();

    /**
     * @return the dialog title
     */
    protected abstract String getTitle();

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     */
    @Override
    public void stateChanged( IChangeNotifier theSource ) {
        updateState();
    }

    /**
     * Updates the dialog message area with icon and message based on current status. Also updates OK button enablement.
     */
    protected void updateState() {
        if (this.btnOk != null) {
            IStatus status = this.pnlConnectionFactory.getStatus();

            if (status.isOK()) {
                status = getOkStatus();
            }

            setMessage(status.getMessage(), UiUtil.getDialogMessageType(status));

            if (this.btnOk.getEnabled() != (status.getSeverity() == IStatus.OK)) {
                this.btnOk.setEnabled(!this.btnOk.getEnabled());
            }
        }
    }

}
