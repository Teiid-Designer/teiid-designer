/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.viewsupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * UiBusyIndicator is a copy of {@link org.eclipse.swt.custom.BusyIndicator}
 * that runs on the Display thread.
 * 
 * The original BusyIndicator will not correctly display the busy cursor since
 * it assumes the runnable being executed is a shell for another worker thread
 * that uses Display.syncExec. Please refer to this <a
 * href="http://www.eclipse.org/swt/snippets/#busyindicator">snippet</a>.
 * 
 * To make this simpler, the whole thread logic has been inserted into this
 * class so that clients can simply create a runnable with their work inside it.
 */
public abstract class UiBusyIndicator {

    /**
     * Encloses the done flag in an object, avoiding the need to synchronize
     * (potential deadlocks) it and have multiple threads potentially changing
     * the same done flag. Each execution of showWhile is responsible for
     * setting the cursor back to its correct state and should always exit the
     * Display.sleep() loop at the foot of the showWhile method.
     */
    private static class RunnableStatus {
        private boolean done = false;

        /**
         * @param done
         *            Sets done to the specified value.
         */
        public void done() {
            this.done = true;
        }

        /**
         * @return done
         */
        public boolean isDone() {
            return done;
        }
    }

    private static int nextBusyId = 1;
    private static final String BUSYID_NAME = "UI BusyIndicator"; //$NON-NLS-1$

    /**
     * Runs the given <code>Runnable</code> on the Display thread while
     * providing busy feedback using this busy indicator.
     * 
     * This method is synchronized to make it thread safe, ie. should two
     * threads call it in parallel then the second will have to wait for the
     * first to finish thereby ensuring that both runnables have completed
     * gracefully.
     * 
     * @param display
     *            the display on which the busy feedback should be displayed. If
     *            the display is null, the Display for the current thread will
     *            be used. If there is no Display for the current thread, the
     *            runnable code will be executed and no busy feedback will be
     *            displayed.
     * @param runnable
     *            the runnable for which busy feedback is to be shown. Must not
     *            be null.
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the runnable is null</li>
     *                </ul>
     */
    public static synchronized void showWhile(Display display,
            final Runnable runnable) {
        // ensure runnable is not null
        if (runnable == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);

        // ensure we can get a valid Display object
        if (display == null) {
            display = Display.getCurrent() != null ? Display.getCurrent()
                    : Display.getDefault();
            if (display == null) {
                // This just should not happen ...
                runnable.run();
                return;
            }
        }

        /*
         * ensure this method is called only on the display's event dispatch
         * thread
         */
        if (display.getThread() != Thread.currentThread()) {
            final Display d = display;
            display.syncExec(new Runnable() {
                @Override
                public void run() {
                    showWhileInternal(d, runnable);
                }
            });
            return;
        }
        else {
            showWhileInternal(display, runnable);
        }
    }

    /**
     * Internal method that change the cursor to WAITING, executes the runnable
     * and changes the cursor back again. Unlike
     * {@link org.eclipse.swt.custom.BusyIndicator}, the
     * {@link #executeRunnable(Display, Runnable, RunnableStatus)} will allow
     * the WAITING icon to actually display before executing the runnable.
     * 
     * @param display
     * @param runnable
     */
    private static void showWhileInternal(Display display,
            final Runnable runnable) {

        if (PlatformUI.getWorkbench().isStarting()) {
            /*
             * Need to avoid calling executeRunnable on startup since it creates
             * a new thread and uses display.syncExec. Since this thread is
             * non-priveleged, the syncExec gets blocked until startup has
             * completed. However, if showWhile() was called from a privileged
             * startup thread that requires this to complete, a deadlock can
             * result. Thus, we avoid this by simply running the runnable.
             */
            runnable.run();
            return;
        }

        Integer busyId = new Integer(nextBusyId);
        nextBusyId++;

        Shell[] shells = display.getShells();
        for (int i = 0; i < shells.length; i++) {
            Integer id = (Integer) shells[i].getData(BUSYID_NAME);
            if (id == null) {
                shells[i].setCursor(display.getSystemCursor(SWT.CURSOR_WAIT));
                shells[i].setData(BUSYID_NAME, busyId);
            }
        }

        try {
            executeRunnable(display, runnable, new RunnableStatus());
        }
        finally {
            shells = display.getShells();
            for (int i = 0; i < shells.length; i++) {
                Integer id = (Integer) shells[i].getData(BUSYID_NAME);
                if (busyId.equals(id)) {
                    shells[i].setCursor(null);
                    shells[i].setData(BUSYID_NAME, null);
                }
            }
        }
    }

    /**
     * Invokes the given {@link Runnable} on its own thread but calls the
     * {@link Runnable#run()} method using a {@link Display#syncExec(Runnable)}.
     * 
     * This ensures that the runnable's work is performed on the Display thread
     * but that the Display is also given a chance to update itself both before
     * and after the work, allowing the cursor icon to properly change to its
     * WAITING version.
     * 
     * This method should NOT be allowed to execute while eclipse is starting up
     * since the display.syncExec in its child thread can cause deadlocks and
     * eclipse will hang. This is guarded against in
     * {@link #showWhileInternal(Display, Runnable)}.
     * 
     * @param runnable
     */
    private static void executeRunnable(final Display display,
            final Runnable runnable, final RunnableStatus runnableStatus) {

        String threadName = UiBusyIndicator.class.getName()
                + " runnable parent thread"; //$NON-NLS-1$

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                if (display.isDisposed())
                    return;

                display.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runnable.run();
                        }
                        finally {
                            /*
                             * The runnable may throw an exception, which is up
                             * to the developer of the runnable to solve. We
                             * need to simply ensure that done is always true
                             * and the display is awakened.
                             */
                            runnableStatus.done();
                            display.wake();
                        }
                    }
                });
            }
        }, threadName);

        thread.setDaemon(true);
        thread.start();

        while (!runnableStatus.isDone() && !shellsDisposed(display)) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    /**
     * Determines if even one of the {@link Shell}s on the {@link Display} are
     * disposed.
     * 
     * @param display
     * @return
     */
    private static boolean shellsDisposed(Display display) {
        Shell[] shells = display.getShells();
        for (Shell shell : shells) {
            if (shell.isDisposed())
                return true;
        }

        return false;
    }
}

