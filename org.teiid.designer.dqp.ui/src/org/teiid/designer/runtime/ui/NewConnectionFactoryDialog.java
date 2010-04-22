/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.PLUGIN_ID;
import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.util.I18nUtil;

/**
 *
 */
public class NewConnectionFactoryDialog extends ConnectionFactoryDialog {

    /**
     * @param shell the parent shell (can be <code>null</code>)
     * @param admin the server execution admin (never <code>null</code>)
     * @param type the initial type selected by the user (can be <code>null</code>)
     */
    public NewConnectionFactoryDialog( Shell shell,
                                       ExecutionAdmin admin,
                                       ConnectorType type ) {
        super(shell, admin, type);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.ConnectionFactoryDialog#createConnectionFactoryPanel(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected ConnectionFactoryPanel createConnectionFactoryPanel( Composite panel ) {
        return new ConnectionFactoryPanel(panel, getAdmin(), getConnectorType(), this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.ConnectionFactoryDialog#getName()
     */
    @Override
    protected String getName() {
        return UTIL.getString(I18nUtil.getPropertyPrefix(NewConnectionFactoryDialog.class) + "name"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.ConnectionFactoryDialog#getOkStatus()
     */
    @Override
    protected IStatus getOkStatus() {
        String msg = UTIL.getString(I18nUtil.getPropertyPrefix(NewConnectionFactoryDialog.class) + "okMsg", //$NON-NLS-1$
                                    getConnector().getName());
        IStatus status = new Status(IStatus.OK, PLUGIN_ID, msg);
        return status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.ConnectionFactoryDialog#getTitle()
     */
    @Override
    protected String getTitle() {
        return UTIL.getString(I18nUtil.getPropertyPrefix(NewConnectionFactoryDialog.class) + "title"); //$NON-NLS-1$
    }

}
