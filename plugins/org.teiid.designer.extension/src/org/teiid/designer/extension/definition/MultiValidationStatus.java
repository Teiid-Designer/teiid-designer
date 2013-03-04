/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.teiid.core.designer.util.CoreArgCheck;

/**
 * A MED status that has multiple statuses.
 */
public class MultiValidationStatus implements MedStatus {

    /**
     * @param status the status used to create the multi-status from (cannot be <code>null</code>)
     * @return the mulit-status (never <code>null</code>)
     */
    public static final MultiValidationStatus create(final MedStatus status) {
        CoreArgCheck.isNotNull(status);
        final MultiValidationStatus multiStatus = new MultiValidationStatus();
        multiStatus.add(status);
        return multiStatus;
    }

    private MedStatus status;
    private final List<MedStatus> children;

    /**
     * Don't allow public construction.
     */
    private MultiValidationStatus() {
        this.children = new ArrayList<MedStatus>();
    }

    /**
     * The children of multi-statuses being added are added as children.
     * 
     * @param childStatus the status being added (cannot be <code>null</code>)
     */
    public void add(final MedStatus childStatus) {
        CoreArgCheck.isNotNull(childStatus);

        if (childStatus.isMulti()) {
            for (final MedStatus kid : childStatus.getChildren()) {
                add(kid);
            }
        } else {
            if (childStatus.isOk()) {
                if (this.status == null) {
                    this.status = childStatus;
                }
            } else {
                this.children.add(childStatus);

                if ((this.status == null) || (this.status.compareTo(childStatus) > 0)) {
                    this.status = childStatus;
                }
            }
        }
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(MedStatus that) {
        return this.status.compareTo(that);
    }

    /**
     * @see org.teiid.designer.extension.definition.ValidationStatus#getChildren()
     */
    @Override
    public List<MedStatus> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#getMessage()
     */
    @Override
    public String getMessage() {
        return this.status.getMessage();
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#getSeverity()
     */
    @Override
    public Severity getSeverity() {
        return this.status.getSeverity();
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#isError()
     */
    @Override
    public boolean isError() {
        return this.status.isError();
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#isInfo()
     */
    @Override
    public boolean isInfo() {
        return this.status.isInfo();
    }

    /**
     * @see org.teiid.designer.extension.definition.ValidationStatus#isMulti()
     */
    @Override
    public boolean isMulti() {
        return true;
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#isOk()
     */
    @Override
    public boolean isOk() {
        return this.status.isOk();
    }

    /**
     * @see org.teiid.designer.extension.definition.MedStatus#isWarning()
     */
    @Override
    public boolean isWarning() {
        return this.status.isWarning();
    }

}
