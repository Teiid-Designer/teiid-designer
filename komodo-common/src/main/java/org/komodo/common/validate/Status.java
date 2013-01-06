/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.validate;

import org.komodo.common.util.HashCode;
import org.komodo.common.util.Precondition;

/**
 * A validation status with a severity, code, and message.
 */
public class Status implements Comparable<Status> {

    /**
     * Represents the severity of the error code.
     */
    public enum Severity {

        /**
         * Indicates an error status.
         */
        ERROR,

        /**
         * Indicates an informational status.
         */
        INFO,

        /**
         * Indicates a normal status.
         */
        OK,

        /**
         * Indicates a warning status.
         */
        WARNNING
    }

    private final int code;
    private String message;
    private final Severity severity;

    /**
     * @param severity the status severity (cannot be <code>null</code>)
     * @param code a code identifying the error
     */
    public Status(final Severity severity,
                  final int code) {
        Precondition.notNull(severity, "severity"); //$NON-NLS-1$
        this.severity = severity;
        this.code = code;
    }

    /**
     * @param severity the status severity (cannot be <code>null</code>)
     * @param code a code identifying the error
     * @param message an error message (can be <code>null</code> or empty)
     */
    public Status(final Severity severity,
                  final int code,
                  final String message) {
        this(severity, code);
        this.message = message;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Status that) {
        if (this == that) {
            return 0;
        }

        if (this.severity == that.severity) {
            return (this.code - that.code);
        }

        if (this.severity == Severity.ERROR) {
            return -1;
        }
        
        if (this.severity == Severity.WARNNING) {
            return ((that.severity == Severity.ERROR) ? 1 : -1);
        }
        
        if (this.severity == Severity.INFO) {
            return ((that.severity == Severity.OK)) ? -1 : 1;
        }

        // severity is OK
        return 1;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        final Status that = (Status)obj;
        return ((this.code == that.code) && (this.severity == that.severity));
    }

    /**
     * @return the status code
     */
    public int getCode() {
        return this.code;
    }

    /**
     * @return the message (can be <code>null</code> or empty)
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return the severity (never <code>null</code>)
     */
    public Severity getSeverity() {
        return this.severity;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(this.code, this.severity);
    }

    /**
     * @return <code>true</code> if status severity is {@link Severity#ERROR}
     */
    public boolean isError() {
        return (this.severity == Severity.ERROR);
    }

    /**
     * @return <code>true</code> if status severity is {@link Severity#WARNNING}
     */
    public boolean isWarning() {
        return (this.severity == Severity.WARNNING);
    }

    /**
     * @return <code>true</code> if status severity is {@link Severity#INFO}
     */
    public boolean isInfo() {
        return (this.severity == Severity.INFO);
    }

    /**
     * @return <code>true</code> if status severity is {@link Severity#OK}
     */
    public boolean isOk() {
        return (this.severity == Severity.OK);
    }

    /**
     * @param newMessage the new message (can be <code>null</code> or empty)
     */
    public void setMessage(String newMessage) {
        this.message = newMessage;
    }
}
