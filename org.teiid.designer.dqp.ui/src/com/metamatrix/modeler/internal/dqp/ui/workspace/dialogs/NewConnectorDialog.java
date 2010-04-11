/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs;

import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;

/**
 * @since 5.0
 */
public class NewConnectorDialog extends ExtendedTitleAreaDialog implements IChangeListener {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(NewConnectorDialog.class);
    private static final int WIDTH = 610;
    private static final int HEIGHT = 500;

    private final ExecutionAdmin admin;
    private Button btnOk;
    private NewConnectorPanel pnlBindings;
    private ConnectorType type;

    /**
     * @param parentShell the parent shell
     * @param admin the server execution admin (never <code>null</code>)
     * @param type the initial type selected by the user (can be <code>null</code>)
     */
    public NewConnectorDialog( Shell parentShell,
                               ExecutionAdmin admin,
                               ConnectorType type ) {
        super(parentShell, DqpUiPlugin.getDefault());
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$
        this.admin = admin;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
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

        this.pnlBindings = new NewConnectorPanel(mainComposite, this.admin, this.type);
        this.pnlBindings.addChangeListener(this);
        this.pnlBindings.setFocus();
        this.pnlBindings.setReadonly(false);

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

    /**
     * @return the name of the new connector (never <code>null</code>)
     * @since 7.0
     */
    public String getConnectorName() {
        return this.pnlBindings.getConnectorName();
    }

    /**
     * @return the properties of the new connector (never <code>null</code>)
     * @since 7.0
     */
    public Properties getConnectorProperties() {
        return this.pnlBindings.getConnectorProperties();
    }

    /**
     * @return the connector type of the new connector (never <code>null</code>)
     * @since 7.0
     */
    public ConnectorType getConnectorType() {
        return this.pnlBindings.getConnectorType();
    }

    /**
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 4.3
     */
    public void stateChanged( IChangeNotifier theSource ) {
        updateState();
    }

    void updateState() {
        if (this.btnOk != null) {
            IStatus status = this.pnlBindings.getStatus();
            setMessage(status.getMessage(), status.getSeverity());
            this.btnOk.setEnabled(status.getSeverity() == IStatus.OK);
        }
    }

}
