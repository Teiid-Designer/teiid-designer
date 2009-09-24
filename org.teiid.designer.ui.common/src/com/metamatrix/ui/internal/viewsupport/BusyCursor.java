/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.viewsupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * BusyCursor are static methods to start and end the busy cursor over the current display.
 */
public abstract class BusyCursor {

    static int nextBusyId = 1;
    static final String BUSYID_NAME = "SWT BusyIndicator"; //$NON-NLS-1$

    /**
     * Starts the busy cursor over all shells in the current display.
     */
    public static void showBusy() {
        final Integer busyId = new Integer(nextBusyId);
        nextBusyId++;
    
        final Display display = Display.getCurrent();
        if (display != null) {
            
            display.syncExec(new Runnable() {
                public void run() {
                    Cursor cursor = new Cursor(display, SWT.CURSOR_WAIT);
                    Shell[] shells = display.getShells();
                    for (int i = 0; i < shells.length; i++) {
                        Integer id = (Integer)shells[i].getData(BUSYID_NAME);
                        if (id == null) {
                            shells[i].setCursor(cursor);
                            shells[i].setData(BUSYID_NAME, busyId);
                        }
                    }
                }
            });
        }
    }

    /**
     * Ends the busy cursor over all shells in the current display.
     */
    public static void endBusy() {
        final Integer busyId = new Integer(nextBusyId);

        final Display display = Display.getCurrent();
        if (display != null) {

            display.syncExec(new Runnable() {
                public void run() {

                    Cursor cursor = new Cursor(display, SWT.CURSOR_WAIT);
                    Shell[] shells = display.getShells();
        
                    shells = display.getShells();
                    for (int i = 0; i < shells.length; i++) {
                        Integer id = (Integer)shells[i].getData(BUSYID_NAME);
                        if (id == busyId) {
                            shells[i].setCursor(null);
                            shells[i].setData(BUSYID_NAME, null);
                        }
                    }
                    if (!cursor.isDisposed()) {
                        cursor.dispose();
                    }
                }
            });
            
        }
        
    }

}
