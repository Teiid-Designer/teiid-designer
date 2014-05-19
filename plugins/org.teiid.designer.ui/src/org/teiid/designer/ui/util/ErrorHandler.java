/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.statushandlers.StatusManager;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.ui.UiConstants;

/**
 *
 */
public class ErrorHandler implements StringConstants {

    private ErrorHandler() {}

    private static Display getDisplay() {
        return (Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent());
    }

    /**
     * @param operation The operation to be executed in the SWT thread.
     * @param asynchronous True if the operation should be run asynchronously, meaning the calling thread will not be blocked.
     */
    private static void runInSwtThread( final Runnable operation, final boolean asynchronous ) {
        Display display = getDisplay();
        if (Thread.currentThread() != display.getThread()) {
            if (asynchronous) {
                display.asyncExec(operation);
            } else {
                display.syncExec(operation);
            }
        } else {
            operation.run();
        }
    }

    /**
     * Convert the given {@link Throwable} with message to a {@link CoreException}
     *
     * @param message message to use for the {@link CoreException}. If null then a default message is added.
     * @param throwable
     * @return {@link CoreException} wrapping given {@link Throwable}
     */
    public static CoreException toCoreException(String message, Throwable throwable) {
        if (throwable instanceof CoreException)
            return (CoreException) throwable;

        if (message == null)
            message = UiConstants.Util.getStringOrKey(
                                                      ErrorHandler.class.getSimpleName() + DOT + "operationExceptionThrown"); //$NON-NLS-1$ 

        MultiStatus status = new MultiStatus(UiConstants.PLUGIN_ID, IStatus.ERROR, message, null);
        do {
            status.add(new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, " * " + throwable.getLocalizedMessage())); //$NON-NLS-1$
            throwable = throwable.getCause();
        } while (throwable != null);

        return new CoreException(status);
    }

    /**
     * Convert the given {@link Throwable} to a {@link CoreException} with a default failure message
     *
     * @param throwable
     * @return {@link CoreException} wrapping given {@link Throwable}
     */
    public static CoreException toCoreException(Throwable throwable) {
        return toCoreException(null, throwable);
    }

    /**
     * Display the given core exception
     *
     * @param coreException
     */
    public static void toExceptionDialog(final CoreException coreException) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                StatusManager.getManager().handle(coreException.getStatus(), StatusManager.SHOW);
                UiConstants.Util.log(coreException);
            }
        };

        runInSwtThread(runnable, true);
    }

    /**
     * Display the given throwable in an error dialog
     *
     * @param message readable message to display as the main dialog message
     * @param throwable exception that needs to be displayed
     */
    public static void toExceptionDialog(String message, Throwable throwable) {
        toExceptionDialog(toCoreException(message, throwable));
    }

    /**
     * Display the given throwable with a standard "this operation failed" message
     *
     * @param throwable
     */
    public static void toExceptionDialog(Throwable throwable) {
        toExceptionDialog(toCoreException(null, throwable));
    }
}
