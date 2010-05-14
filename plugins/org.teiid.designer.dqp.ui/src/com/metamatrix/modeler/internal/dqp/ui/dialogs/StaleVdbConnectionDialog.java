/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;

/**
 * The <code>StaleVdbConnectionDialog</code> provides the user the opportunity to choose between closing the stale connection and
 * reconnecting, just closing the connection, or leaving the stale connection open. The dialog does not actually work with
 * connections; it just obtains the user's choice.
 * 
 * @since 5.0
 */
public class StaleVdbConnectionDialog extends MessageDialog implements DqpUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(StaleVdbConnectionDialog.class);

    /**
     * Option indicating the user wants to close the stale connection and reconnect.
     * 
     * @since 5.0
     * @see org.eclipse.jface.window.Window#open()
     */
    public static final int RECONNECT = 10;

    /**
     * Option indicating the user wants to close the stale connection (no reconnect).
     * 
     * @since 5.0
     * @see org.eclipse.jface.window.Window#open()
     */
    public static final int CLOSE = 11;

    /**
     * Option indicating the user wants to keep the stale connection open.
     * 
     * @since 5.0
     * @see org.eclipse.jface.window.Window#open()
     */
    public static final int KEEP_OPEN = CANCEL;

    /**
     * Flag indicating if the reconnect option should be available.
     * 
     * @since 5.0.1
     */
    private boolean allowReconnect = true;

    /**
     * Flag indicating if the close connection option should be available.
     * 
     * @since 5.0.1
     */
    private boolean allowClose = true;

    private int option = RECONNECT;

    /**
     * Indicates if the VDB whose connection is open is in a valid execution state.
     * 
     * @since 5.0.1
     */
    private boolean validVdb;

    /**
     * The name of the VDB whose connection is stale.
     * 
     * @since 5.0.1
     */
    private String vdbName;

    /**
     * Constructs a dialog. The VDB whose connection is stale must be in a valid execution state.
     * 
     * @param theShell the shell for the dialog
     * @param theVdbName the name of the VDB with the stale connection
     */
    public StaleVdbConnectionDialog( Shell theShell,
                                     String theMessage ) {
        this(theShell, null, true, theMessage);
    }

    /**
     * Constructs a dialog.
     * 
     * @param theShell the shell for the dialog
     * @param theVdbName the name of the VDB with the stale connection
     * @param theValidVdbFlag the flag indicating if the VDB in it's current state is valid
     */
    public StaleVdbConnectionDialog( Shell theShell,
                                     String theVdbName,
                                     boolean theValidVdbFlag,
                                     String theMessage ) {
        super(theShell, UTIL.getStringOrKey(PREFIX + "title"), //$NON-NLS-1$
              null, // use default window icon
              theMessage, WARNING, new String[] {IDialogConstants.OK_LABEL}, 0);

        this.validVdb = theValidVdbFlag;
        this.vdbName = theVdbName;
    }

    /**
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
    @Override
    protected Control createCustomArea( Composite theParent ) {
        Group group = new Group(theParent, SWT.NONE);
        group.setText(UTIL.getStringOrKey(PREFIX + "group")); //$NON-NLS-1$
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button btn = null;

        if (this.validVdb && this.allowReconnect) {
            btn = new Button(group, SWT.RADIO);
            btn.setText(UTIL.getStringOrKey(PREFIX + "btnReconnect")); //$NON-NLS-1$
            btn.setToolTipText(UTIL.getStringOrKey(PREFIX + "btnReconnect.tip")); //$NON-NLS-1$
            btn.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent theEvent ) {
                    if (((Button)theEvent.widget).getSelection()) {
                        setOption(RECONNECT);
                    }
                }
            });
            btn.setSelection(true);
        } else {
            if (!this.validVdb) {
                this.messageLabel.setText(UTIL.getString(PREFIX + "invalidStateMsg", this.vdbName)); //$NON-NLS-1$
            }

            if (this.allowClose) {
                this.option = CLOSE;
            } else {
                this.option = KEEP_OPEN;
            }
        }

        if (this.allowClose) {
            btn = new Button(group, SWT.RADIO);
            btn.setText(UTIL.getStringOrKey(PREFIX + "btnClose")); //$NON-NLS-1$
            btn.setToolTipText(UTIL.getStringOrKey(PREFIX + "btnClose.tip")); //$NON-NLS-1$
            btn.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent theEvent ) {
                    if (((Button)theEvent.widget).getSelection()) {
                        setOption(CLOSE);
                    }
                }
            });
            btn.setSelection(!this.validVdb);
        }

        btn = new Button(group, SWT.RADIO);
        btn.setText(UTIL.getStringOrKey(PREFIX + "btnKeepOpen")); //$NON-NLS-1$
        btn.setToolTipText(UTIL.getStringOrKey(PREFIX + "btnKeepOpen.tip")); //$NON-NLS-1$
        btn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                if (((Button)theEvent.widget).getSelection()) {
                    setOption(KEEP_OPEN);
                }
            }
        });

        return group;
    }

    /**
     * Indicates if the option chosen equals {@link #CLOSE}.
     * 
     * @return <code>true</code> if user wants to close the connection; <code>false</code> otherwise.
     * @since 5.0
     */
    public boolean closeConnection() {
        return ((getReturnCode() == OK) && (this.option == CLOSE));
    }

    /**
     * The option chosen by the user.
     * 
     * @return the option
     * @since 5.0
     * @see #RECONNECT
     * @see #CLOSE
     * @see #KEEP_OPEN
     */
    public int getOption() {
        return this.option;
    }

    /**
     * Indicates if the option chosen equals {@link #KEEP_OPEN}.
     * 
     * @return <code>true</code> if user wants to keep the connection open; <code>false</code> otherwise.
     * @since 5.0
     */
    public boolean keepConnectionOpen() {
        return ((getReturnCode() != OK) || (this.option == KEEP_OPEN));
    }

    /**
     * Indicates if the option chosen equals {@link #RECONNECT}.
     * 
     * @return <code>true</code> if user wants to close the connection and reconnect; <code>false</code> otherwise.
     * @since 5.0
     */
    public boolean reconnect() {
        return ((getReturnCode() == OK) && (this.option == RECONNECT));
    }

    void setOption( int theOption ) {
        this.option = theOption;
    }

    /**
     * Sets the visible options on the dialog.
     * 
     * @param theAllowReconnectOptionFlag the flag indicating if the reconnect option should be shown
     * @param theAllowCloseOptionFlag the flag indicating if the close connection option should be shown
     * @since 5.0.1
     */
    public void setOptions( boolean theAllowReconnectOptionFlag,
                            boolean theAllowCloseOptionFlag ) {
        this.allowReconnect = theAllowReconnectOptionFlag;
        this.allowClose = theAllowCloseOptionFlag;
    }
}
