/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal.impl;

import org.eclipse.core.runtime.IStatus;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.rose.internal.IMessage;
import com.metamatrix.rose.internal.IRoseConstants;

/**
 * @since 4.1
 */
public final class Message implements
                          IMessage,
                          IRoseConstants {
    //============================================================================================================================
    // Variables

    private int type;

    private String text;

    private Object obj;

    private Throwable err;

    private Object src;

    //============================================================================================================================
    // Constructors

    /**
     * @param type
     * @since 4.1
     */
    public Message(final int type) {
        this(null, type, null, null, null);
    }

    /**
     * @param text
     * @since 4.1
     */
    public Message(final String text) {
        this(null, UNSPECIFIED, text, null, null);
    }

    /**
     * @param error
     * @since 4.1
     */
    public Message(final Throwable error) {
        this(null, IStatus.ERROR, null, error, null);
    }

    /**
     * @param object
     * @since 4.1
     */
    public Message(final Object object) {
        this(null, UNSPECIFIED, null, null, object);
    }

    /**
     * @param source
     * @param type
     * @since 4.1
     */
    public Message(final Object source,
                   final int type) {
        this(source, type, null, null, null);
    }

    /**
     * @param source
     * @param text
     * @since 4.1
     */
    public Message(final Object source,
                   final String text) {
        this(source, UNSPECIFIED, text, null, null);
    }

    /**
     * @param source
     * @param error
     * @since 4.1
     */
    public Message(final Object source,
                   final Throwable error) {
        this(source, IStatus.ERROR, null, error, null);
    }

    /**
     * @param source
     * @param object
     * @since 4.1
     */
    public Message(final Object source,
                   final Object object) {
        this(source, UNSPECIFIED, null, null, object);
    }

    /**
     * @param type
     * @param text
     * @since 4.1
     */
    public Message(final int type,
                   final String text) {
        this(null, type, text, null, null);
    }

    /**
     * @param type
     * @param error
     * @since 4.1
     */
    public Message(final int type,
                   final Throwable error) {
        this(null, type, null, error, null);
    }

    /**
     * @param type
     * @param object
     * @since 4.1
     */
    public Message(final int type,
                   final Object object) {
        this(null, type, null, null, object);
    }

    /**
     * @param text
     * @param error
     * @since 4.1
     */
    public Message(final String text,
                   final Throwable error) {
        this(null, IStatus.ERROR, text, error, null);
    }

    /**
     * @param text
     * @param object
     * @since 4.1
     */
    public Message(final String text,
                   final Object object) {
        this(null, UNSPECIFIED, text, null, object);
    }

    /**
     * @param error
     * @param object
     * @since 4.1
     */
    public Message(final Throwable error,
                   final Object object) {
        this(null, IStatus.ERROR, null, error, object);
    }

    /**
     * @param source
     * @param type
     * @param text
     * @since 4.1
     */
    public Message(final Object source,
                   final int type,
                   final String text) {
        this(source, type, text, null, null);
    }

    /**
     * @param source
     * @param type
     * @param error
     * @since 4.1
     */
    public Message(final Object source,
                   final int type,
                   final Throwable error) {
        this(source, type, null, error, null);
    }

    /**
     * @param source
     * @param type
     * @param object
     * @since 4.1
     */
    public Message(final Object source,
                   final int type,
                   final Object object) {
        this(source, type, null, null, object);
    }

    /**
     * @param source
     * @param text
     * @param error
     * @since 4.1
     */
    public Message(final Object source,
                   final String text,
                   final Throwable error) {
        this(source, IStatus.ERROR, text, error, null);
    }

    /**
     * @param source
     * @param text
     * @param object
     * @since 4.1
     */
    public Message(final Object source,
                   final String text,
                   final Object object) {
        this(source, UNSPECIFIED, text, null, object);
    }

    /**
     * @param source
     * @param error
     * @param object
     * @since 4.1
     */
    public Message(final Object source,
                   final Throwable error,
                   final Object object) {
        this(source, IStatus.ERROR, null, error, object);
    }

    /**
     * @param type
     * @param text
     * @param error
     * @since 4.1
     */
    public Message(final int type,
                   final String text,
                   final Throwable error) {
        this(null, type, text, error, null);
    }

    /**
     * @param type
     * @param text
     * @param object
     * @since 4.1
     */
    public Message(final int type,
                   final String text,
                   final Object object) {
        this(null, type, text, null, object);
    }

    /**
     * @param type
     * @param error
     * @param object
     * @since 4.1
     */
    public Message(final int type,
                   final Throwable error,
                   final Object object) {
        this(null, type, null, error, object);
    }

    /**
     * @param text
     * @param error
     * @param object
     * @since 4.1
     */
    public Message(final String text,
                   final Throwable error,
                   final Object object) {
        this(null, IStatus.ERROR, text, error, object);
    }

    /**
     * @param source
     * @param type
     * @param text
     * @param error
     * @since 4.1
     */
    public Message(final Object source,
                   final int type,
                   final String text,
                   final Throwable error) {
        this(source, type, text, error, null);
    }

    /**
     * @param source
     * @param type
     * @param text
     * @param object
     * @since 4.1
     */
    public Message(final Object source,
                   final int type,
                   final String text,
                   final Object object) {
        this(source, type, text, null, object);
    }

    /**
     * @param source
     * @param type
     * @param error
     * @param object
     * @since 4.1
     */
    public Message(final Object source,
                   final int type,
                   final Throwable error,
                   final Object object) {
        this(source, type, null, error, object);
    }

    /**
     * @param source
     * @param text
     * @param error
     * @param object
     * @since 4.1
     */
    public Message(final Object source,
                   final String text,
                   final Throwable error,
                   final Object object) {
        this(source, IStatus.ERROR, text, error, object);
    }

    /**
     * @param type
     * @param text
     * @param error
     * @param object
     * @since 4.1
     */
    public Message(final int type,
                   final String text,
                   final Throwable error,
                   final Object object) {
        this(null, type, text, error, object);
    }

    /**
     * @param source
     * @param type
     * @param text
     * @param error
     * @param object
     * @since 4.1
     */
    public Message(final Object source,
                   final int type,
                   final String text,
                   final Throwable error,
                   final Object object) {
        this.src = source;
        this.type = type;
        if (text == null && error != null) {
            this.text = error.getLocalizedMessage();
            if (this.text == null) {
                this.text = CoreStringUtil.computeDisplayableForm(error.getClass().getSimpleName());
            }
        } else {
            this.text = text;
        }
        this.err = error;
        this.obj = object;
    }

    //============================================================================================================================
    // Implemented Methods

    /**
     * @see com.metamatrix.rose.internal.IMessage#getException()
     * @since 4.1
     */
    public Throwable getException() {
        return this.err;
    }

    /**
     * @see com.metamatrix.rose.internal.IMessage#getObject()
     * @since 4.1
     */
    public Object getObject() {
        return this.obj;
    }

    /**
     * @see com.metamatrix.rose.internal.IMessage#getSource()
     * @since 4.1
     */
    public Object getSource() {
        return this.src;
    }

    /**
     * @see com.metamatrix.rose.internal.IMessage#getText()
     * @since 4.1
     */
    public String getText() {
        return this.text;
    }

    /**
     * @see com.metamatrix.rose.internal.IMessage#getType()
     * @since 4.1
     */
    public int getType() {
        return this.type;
    }

    //============================================================================================================================
    // Overridden Methods

    /**
     * @see java.lang.Object#toString()
     * @since 4.1
     */
    @Override
    public String toString() {
        return getText();
    }
}
