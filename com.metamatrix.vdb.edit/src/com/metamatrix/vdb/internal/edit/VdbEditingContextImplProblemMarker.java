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

package com.metamatrix.vdb.internal.edit;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.core.util.StringUtil;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ProblemMarker;
import com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/** 
 * @since 4.2
 */
public class VdbEditingContextImplProblemMarker {
    
    private ProblemMarkerContainer marked;
    private int severity;
    private String msg;
    private Throwable throwable;
    
    /**
     *  
     * @param vdbEditingContextImpl
     * @param marked
     * @param severity
     * @param msg
     * @param t
     * @since 4.2
     */
    public VdbEditingContextImplProblemMarker(final ProblemMarkerContainer marked,
                                              final int severity,
                                              final String msg,
                                              final Throwable t) {
        
        this.marked     = marked;
        this.severity   = severity;
        this.msg        = msg;
        this.throwable  = t;
    }
    
    /**
     *  
     * 
     * @since 4.2
     */
    protected void markProblem() {
        final ProblemMarker marker = ManifestFactory.eINSTANCE.createProblemMarker();
        switch (severity) {
            case IStatus.ERROR:
                marker.setSeverity(Severity.ERROR_LITERAL);
                break;
            case IStatus.WARNING:
                marker.setSeverity(Severity.WARNING_LITERAL);
                break;
            case IStatus.INFO:
                marker.setSeverity(Severity.INFO_LITERAL);
                break;
            case IStatus.OK:
                marker.setSeverity(Severity.OK_LITERAL);
                break;
        }
        
        if (marked instanceof VirtualDatabase && ((VirtualDatabase)marked).getName() != null) {
            marker.setTarget(((VirtualDatabase)marked).getName());
        } else if (marked instanceof ModelReference && ((ModelReference)marked).getModelLocation() != null) {
            marker.setTarget(((ModelReference)marked).getModelLocation());
        }
        
        marker.setMessage(msg);
        if (throwable != null) {
            final String trace = StringUtil.getStackTrace(throwable);
            marker.setStackTrace(trace);
        }
        marker.setMarked(marked);
    }

}
