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

package com.metamatrix.metamodels.wsdl.io;

import org.eclipse.emf.ecore.resource.Resource.Diagnostic;


/** 
 * @since 4.2
 */
public class WsdlDiagnostic implements Diagnostic {
    
    private final String message;
    private final String location;
    private final int line;
    private final int column;
    
    public WsdlDiagnostic(final String msg) {
        this(msg,"",0,0); //$NON-NLS-1$
    }

    public WsdlDiagnostic(final String msg, final String location) {
        this(msg,location,0,0);
    }

    public WsdlDiagnostic(final String msg, final int line) {
        this(msg,"",line,0); //$NON-NLS-1$
    }

    public WsdlDiagnostic(final String msg, final String location, final int line) {
        this(msg,location,line,0);
    }

    public WsdlDiagnostic(final String msg, final String location, final int line, final int column ) {
        this.message = msg;
        this.location = location;
        this.line = line;
        this.column = column;
    }

    /** 
     * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic#getMessage()
     * @since 4.2
     */
    public String getMessage() {
        return this.message;
    }

    /** 
     * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic#getLocation()
     * @since 4.2
     */
    public String getLocation() {
        return this.location;
    }

    /** 
     * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic#getLine()
     * @since 4.2
     */
    public int getLine() {
        return this.line;
    }

    /** 
     * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic#getColumn()
     * @since 4.2
     */
    public int getColumn() {
        return this.column;
    }

}
