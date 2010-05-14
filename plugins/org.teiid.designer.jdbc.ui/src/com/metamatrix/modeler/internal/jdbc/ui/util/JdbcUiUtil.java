/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.internal.jdbc.relational.util.JdbcRelationalUtil;
import com.metamatrix.modeler.internal.jdbc.ui.InternalModelerJdbcUiPluginConstants;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcManager;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 4.0
 */
public final class JdbcUiUtil
    implements InternalModelerJdbcUiPluginConstants, InternalModelerJdbcUiPluginConstants.Widgets, InternalUiConstants.Widgets,
    CoreStringUtil.Constants {

    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcUiUtil.class);

    private static final String CONNECTION_FAILED_MESSAGE = getString("connectionFailedMessage"); //$NON-NLS-1$
    private static final String ERROR_ACCESSING_DATABASE_MESSAGE = getString("errorAccessingDatabaseMessage"); //$NON-NLS-1$
    private static final String ERROR_RELOADING_MESSAGE = getString("errorReloadingMessage"); //$NON-NLS-1$
    private static final String ERROR_SAVING_CHANGES_MESSAGE = getString("errorSavingChangesMessage"); //$NON-NLS-1$
    private static final String ERROR_STARTING_MANAGER_MESSAGE = getString("errorStartingManagerMessage"); //$NON-NLS-1$
    private static final String NONE_SPECIFIED_MESSAGE = getString("noneSpecifiedMessage"); //$NON-NLS-1$
    static final String SAVING_CHANGES_MESSAGE = getString("savingChangesMessage"); //$NON-NLS-1$

    static transient Connection connection;

    /**
     * @since 4.0
     */
    public static JdbcManager getJdbcManager() {
        try {
            return JdbcRelationalUtil.getJdbcManager();
        } catch (final Exception err) {
            showError(err, ERROR_STARTING_MANAGER_MESSAGE);
            return null;
        }
    }

    /**
     * @since 4.0
     */
    public static synchronized Connection connect( final JdbcSource database,
                                                   final String password ) {
        JdbcUiUtil.connection = null;
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                final Object[] params = new Object[] {""}; //$NON-NLS-1$
                if (database.getUrl() != null && database.getUrl().trim().length() != 0) {
                    params[0] = database.getUrl();
                }
                final String taskName = Util.getString(I18N_PREFIX + "connectingMessage", params); //$NON-NLS-1$
                monitor.setTaskName(taskName);
                try {
                    JdbcUiUtil.connection = getJdbcManager().createConnection(database,
                                                                              null/*database.getJdbcDriver()*/,
                                                                              password,
                                                                              monitor);
                } catch (final Exception err) {
                    throw new InvocationTargetException(err);
                } finally {
                    monitor.done();
                }
            }
        };
        final Shell shell = Display.getCurrent().getActiveShell();
        try {
            new ProgressMonitorDialog(shell).run(true, true, op);
        } catch (InterruptedException ie) {
            // do nothing (assuming it's a user-interruption)
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                err = ((InvocationTargetException)err).getTargetException();
            }
            showErrorCause(err, CONNECTION_FAILED_MESSAGE);
        }
        return JdbcUiUtil.connection;
    }

    /**
     * @since 4.0
     */
    public static void reload() {
        try {
            getJdbcManager().reload(null);
        } catch (final JdbcException err) {
            showError(err, ERROR_RELOADING_MESSAGE);
        }
    }

    /**
     * @since 4.0
     */
    public static boolean saveChanges() {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                monitor.setTaskName(SAVING_CHANGES_MESSAGE);
                try {
                    getJdbcManager().saveChanges(monitor);
                } catch (final IOException err) {
                    throw new InvocationTargetException(err);
                } finally {
                    monitor.done();
                }
            }
        };
        final Shell shell = Display.getCurrent().getActiveShell();
        try {
            new ProgressMonitorDialog(shell).run(false, false, op);
            return true;
        } catch (final Exception err) {
            showError(err, ERROR_SAVING_CHANGES_MESSAGE);
            return false;
        }
    }

    /**
     * Enables or disables the specified control, and the preceding control within the specified control's parent, according to
     * the specified enable flag.
     * 
     * @since 4.0
     */
    public static void setEnabled( final Control control,
                                   final boolean enable ) {
        control.setEnabled(enable);
        final Control[] ctrls = control.getParent().getChildren();
        for (int ndx = ctrls.length; --ndx >= 0;) {
            if (ctrls[ndx] == control) {
                ctrls[ndx - 1].setEnabled(enable);
                break;
            }
        }
    }

    /**
     * @since 4.0
     */
    public static void setText( final CLabel label,
                                final String text ) {
        if (text == null && label.isEnabled()) {
            setEnabled(label, false);
            label.setText(NONE_SPECIFIED_MESSAGE);
        } else {
            if (text != null && !label.isEnabled()) {
                setEnabled(label, true);
            }
            label.setText(text);
        }
    }

    /**
     * @since 4.0
     */
    public static void setText( final Text textFld,
                                final String text ) {
        if (text == null && textFld.isEnabled()) {
            setEnabled(textFld, false);
            textFld.setText(EMPTY_STRING);
        } else {
            if (text != null && !textFld.isEnabled()) {
                setEnabled(textFld, true);
            }
            textFld.setText(text);
        }
    }

    /**
     * @since 4.0
     */
    public static void setText( final Combo combo,
                                final String text ) {
        if (text == null && combo.isEnabled()) {
            setEnabled(combo, false);
            combo.setText(EMPTY_STRING);
        } else {
            if (text != null && !combo.isEnabled()) {
                setEnabled(combo, true);
            }
            combo.setText(text);
        }
    }

    /**
     * @since 4.0
     */
    public static void showAccessError( final Throwable error ) {
        showErrorCause(error, ERROR_ACCESSING_DATABASE_MESSAGE);
    }

    /**
     * @since 4.0
     */
    public static void showError( final Throwable error,
                                  final String message ) {
        Util.log(error);
        WidgetUtil.showError(message);
    }

    /**
     * @since 4.0
     */
    public static void showErrorCause( final Throwable error,
                                       final String message ) {
        Util.log(error);
        WidgetUtil.showCause(message, error);
    }

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * Prevents instantiation.
     * 
     * @since 4.0
     */
    private JdbcUiUtil() {
    }
}
