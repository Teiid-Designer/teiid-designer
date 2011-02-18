/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/** This class is a multiStatus that changes itself to be the same as its worst child.
 * 
 * @author PForhan
 *
 */
public class AutoMultiStatus extends MultiStatus
{
    public AutoMultiStatus() {
        this(OK_STATUS);
    }

    /** Initialize this MultiStatus with the fields from the specified status.
      * This status is <b>not</b> added to the children array.
      * @param initialStatus the initial settings for this status.
      */
    public AutoMultiStatus(IStatus initialStatus) {
        super(initialStatus.getPlugin(), initialStatus.getCode(), initialStatus.getMessage(), initialStatus.getException());
        setSeverity(initialStatus.getSeverity());
    }

    //
    // Overrides:
    //
    @Override
    public void add(IStatus status) {
        // capture severity info:
        int oldSev = getSeverity();
        int newSev = status.getSeverity();

        // let super do its work:
        super.add(status);

        // set fields if needed:
        if (newSev > oldSev) {
            // note: severity set by super...
            setPlugin(status.getPlugin());
            setCode(status.getCode());
            setMessage(status.getMessage());
            setException(status.getException());
        } // endif -- new sev greater
    }
}
