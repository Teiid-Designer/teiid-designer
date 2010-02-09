/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

    public WsdlDiagnostic( final String msg ) {
        this(msg, "", 0, 0); //$NON-NLS-1$
    }

    public WsdlDiagnostic( final String msg, // NO_UCD
                           final String location ) { // NO_UCD
        this(msg, location, 0, 0);
    }

    public WsdlDiagnostic( final String msg, // NO_UCD
                           final int line ) {
        this(msg, "", line, 0); //$NON-NLS-1$
    }

    public WsdlDiagnostic( final String msg, // NO_UCD
                           final String location,
                           final int line ) {
        this(msg, location, line, 0);
    }

    public WsdlDiagnostic( final String msg,
                           final String location,
                           final int line,
                           final int column ) {
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
