/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.util.Collections;
import java.util.List;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.extension.Messages;

/**
 * A validation status that has a severity and a message.
 *
 * @since 8.0
 */
public class ValidationStatus implements MedStatus {

    private static final List<MedStatus> NO_CHILDREN = Collections.emptyList();

    /**
     * An OK validation status with a standard, localized message.
     */
    public static final ValidationStatus OK_STATUS = createOkMessage(Messages.okValidationMsg);

    /**
     * @param message the validation message (cannot be <code>null</code> or empty)
     * @return the error validation message (never <code>null</code>)
     */
    public static ValidationStatus createErrorMessage(final String message) {
        return new ValidationStatus(MedStatus.Severity.ERROR, message);
    }

    /**
     * @param message the validation message (cannot be <code>null</code> or empty)
     * @return the information validation message (never <code>null</code>)
     */
    public static ValidationStatus createInfoMessage(final String message) {
        return new ValidationStatus(Severity.INFO, message);
    }

    /**
     * @param message the validation message (cannot be <code>null</code> or empty)
     * @return the OK validation message (never <code>null</code>)
     */
    public static ValidationStatus createOkMessage(final String message) {
        return new ValidationStatus(Severity.OK, message);
    }

    /**
     * @param message the validation message (cannot be <code>null</code> or empty)
     * @return the warning validation message (never <code>null</code>)
     */
    public static ValidationStatus createWarningMessage(final String message) {
        return new ValidationStatus(Severity.WARNING, message);
    }

    private String message;
    private Severity severity;

    protected ValidationStatus(final Severity type,
                               final String message) {
        assert (type != null) : "severity is null"; //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(message, "message is empty"); //$NON-NLS-1$

        this.severity = type;
        this.message = message;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final MedStatus that) {
        if ((this == that) || (getSeverity() == that.getSeverity())) {
            return getMessage().compareTo(that.getMessage());
        }

        if (isError()) {
            if (that.isWarning()) {
                return -10;
            }

            if (that.isInfo()) {
                return -100;
            }

            return -1000; // ok
        }

        if (isWarning()) {
            if (that.isError()) {
                return 10;
            }

            if (that.isInfo()) {
                return -10;
            }

            return -100; // ok
        }

        if (isInfo()) {
            if (that.isError()) {
                return 100;
            }

            if (that.isWarning()) {
                return 10;
            }

            return -10; // ok
        }

        // OK
        if (that.isError()) {
            return 1000;
        }

        if (that.isWarning()) {
            return 100;
        }

        return 10; // info
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#getChildren()
     */
    @Override
    public List<MedStatus> getChildren() {
        return NO_CHILDREN;
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#getMessage()
     */
    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#getSeverity()
     */
    @Override
    public Severity getSeverity() {
        return this.severity;
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#isError()
     */
    @Override
    public boolean isError() {
        return (Severity.ERROR == this.severity);
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#isInfo()
     */
    @Override
    public boolean isInfo() {
        return (Severity.INFO == this.severity);
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#isMulti()
     */
    @Override
    public boolean isMulti() {
        return false;
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#isOk()
     */
    @Override
    public boolean isOk() {
        return (Severity.OK == this.severity);
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#isWarning()
     */
    @Override
    public boolean isWarning() {
        return (Severity.WARNING == this.severity);
    }

}
