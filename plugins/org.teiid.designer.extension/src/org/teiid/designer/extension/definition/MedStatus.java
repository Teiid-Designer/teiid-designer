/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.definition;

import java.util.List;

/**
 * A MED validation status.
 */
public interface MedStatus extends Comparable<MedStatus> {

    /**
     * The status severity.
     */
    enum Severity {

        /**
         * An error severity.
         */
        ERROR,

        /**
         * An informational severity.
         */
        INFO,

        /**
         * A satisfactory severity.
         */
        OK,

        /**
         * A warning severity.
         */
        WARNING;

    }

    /**
     * @return the contained statuses (can be <code>null</code> or empty)
     */
    List<MedStatus> getChildren();

    /**
     * @return the message pertaining to the worse validation severity (never <code>null</code>)
     */
    String getMessage();

    /**
     * @return the status severity (never <code>null</code>)
     */
    Severity getSeverity();

    /**
     * @return <code>true</code> if the validation status has an {@link Severity#ERROR error} severity
     */
    boolean isError();

    /**
     * @return <code>true</code> if the validation status has an {@link Severity#INFO information} severity
     */
    boolean isInfo();

    /**
     * @return <code>true</code> if the validation status has an {@link Severity#OK OK} severity
     */
    boolean isOk();

    /**
     * @return <code>true</code> if status can contain more than one status
     */
    boolean isMulti();

    /**
     * @return <code>true</code> if the validation status has a {@link Severity#WARNING warning} severity
     */
    boolean isWarning();

}
