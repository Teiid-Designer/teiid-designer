/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.ui.internal.viewsupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * UiBusyIndicator is a copy of BusyIndicator that runs on the Display thread.
 */
public abstract class UiBusyIndicator {

    static int nextBusyId = 1;
    static final String BUSYID_NAME = "UI BusyIndicator"; //$NON-NLS-1$

    /**
     * Runs the given <code>Runnable</code> on the Display thread while providing
     * busy feedback using this busy indicator.
     * 
     * @param display the display on which the busy feedback should be
     *        displayed.  If the display is null, the Display for the current
     *        thread will be used.  If there is no Display for the current thread,
     *        the runnable code will be executed and no busy feedback will be displayed.
     * @param runnable the runnable for which busy feedback is to be shown.
     *        Must not be null.
     * 
    * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the runnable is null</li>
     * </ul>
     */
    public static void showWhile(Display display, final Runnable runnable) {

        // ensure runnable is not null
        if (runnable == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        // ensure we can get a valid Display object
        if (display == null) {
            display = Display.getCurrent();
            if (display == null) {
                runnable.run();
                return;
            }
        }
        
        // ensure this method is called only on the display's event dispatch thread
        if ( display.getThread() != Thread.currentThread() ) {
            final Display d = display;
            display.syncExec(new Runnable() {
                public void run() {
                    showWhile(d, runnable);
                }
            });
            return;
        }
    
        Integer busyId = new Integer(nextBusyId);
        nextBusyId++;
        Cursor cursor = new Cursor(display, SWT.CURSOR_WAIT);
    
        
        Shell[] shells = display.getShells();
        for (int i = 0; i < shells.length; i++) {
            Integer id = (Integer)shells[i].getData(BUSYID_NAME);
            if (id == null) {
                shells[i].setCursor(cursor);
                shells[i].setData(BUSYID_NAME, busyId);
            }
        }
        
        try {
            if ( display.getThread() == Thread.currentThread() ) {
                runnable.run();
            } else {
                display.syncExec(runnable);
            }
        } finally {
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
    }


}
