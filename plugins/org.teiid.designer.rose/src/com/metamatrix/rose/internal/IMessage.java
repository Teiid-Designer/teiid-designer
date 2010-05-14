/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal;
/**
 * @since 4.1
 */
public interface IMessage {

    //============================================================================================================================
    // Constants
    /**
     * The value returned by {@link #getType()}when the type was not specified by the source.
     * 
     * @since 4.1
     */
    int UNSPECIFIED = -1;

    //============================================================================================================================
    // Property Methods
    /**
     * @return The exception associated with this message; may be null.
     * @since 4.1
     */
    Throwable getException();

    /**
     * @return An object providing further information about this message; may be null.
     * @since 4.1
     */
    Object getObject();

    /**
     * @return The object that created this message; may be null.
     * @since 4.1
     */
    Object getSource();

    /**
     * @return The message's text; may be null.
     * @since 4.1
     */
    String getText();

    /**
     * @return The message's type, or {@link #UNSPECIFIED}if not specified by the source. Possible values are defined by the
     *         source, but are usually one of the severities defined in
     *         {@link org.eclipse.core.runtime.IStatus#getSeverity() IStatus}.
     * @since 4.1
     */
    int getType();
}
