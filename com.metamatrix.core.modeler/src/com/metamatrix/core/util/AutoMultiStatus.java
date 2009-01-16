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
