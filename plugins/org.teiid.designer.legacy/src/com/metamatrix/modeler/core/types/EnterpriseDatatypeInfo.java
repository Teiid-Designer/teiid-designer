/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.types;

import com.metamatrix.modeler.core.types.DatatypeConstants.BuiltInNames;



/**
 * @since 4.3
 */
public class EnterpriseDatatypeInfo {

    public static final String DEFAULT_RUNTIME_TYPE_VALUE = BuiltInNames.STRING;
    public static final Boolean DEFAULT_RUNTIME_TYPE_FIXED_VALUE = Boolean.FALSE;

    private String uuid = null;
    private String runtimeType = null;
    private Boolean runtimeTypeFixed = null;

    /**
     *
     * @since 4.3
     */
    public EnterpriseDatatypeInfo() {
        super();
    }

    /**
     * @param type
     * @param fixed
     * @param uuid
     * @since 4.3
     */
    public EnterpriseDatatypeInfo(String uuid, String type, Boolean fixed) {
        super();
        this.runtimeType = type;
        this.runtimeTypeFixed = fixed;
        this.uuid = uuid;
    }

    /**
     * @return Returns the runtimeType.
     * @since 4.3
     */
    public String getRuntimeType() {
        return this.runtimeType;
    }

    /**
     * @param runtimeType The runtimeType to set.
     * @since 4.3
     */
    public void setRuntimeType(String runtimeType) {
        this.runtimeType = runtimeType;
    }

    /**
     * @return Returns the runtimeTypeFixed.
     * @since 4.3
     */
    public Boolean getRuntimeTypeFixed() {
        return this.runtimeTypeFixed;
    }

    /**
     * @param runtimeTypeFixed The runtimeTypeFixed to set.
     * @since 4.3
     */
    public void setRuntimeTypeFixed(Boolean runtimeTypeFixed) {
        this.runtimeTypeFixed = runtimeTypeFixed;
    }

    /**
     * @return Returns the uuid.
     * @since 4.3
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * @param uuid The uuid to set.
     * @since 4.3
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isValid() {
        return (this.uuid != null) && (this.runtimeType != null) && (this.runtimeTypeFixed != null);
    }

    @Override
    public String toString() {
        return "Runtime type: " + this.runtimeType + " (Fixed: "  + this.runtimeTypeFixed + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if ((obj != null) && (obj instanceof EnterpriseDatatypeInfo)) {
            EnterpriseDatatypeInfo edtInfo = (EnterpriseDatatypeInfo) obj;

            if ( ((this.getUuid() == edtInfo.getUuid()) || (this.getUuid() != null && this.getUuid().equals(edtInfo.getUuid())))  &&
                  ((this.getRuntimeType() == edtInfo.getRuntimeType()) || (this.getRuntimeType() != null && this.getRuntimeType().equals(edtInfo.getRuntimeType()))) &&
                  ((this.getRuntimeTypeFixed() == edtInfo.getRuntimeTypeFixed()) || (this.getRuntimeTypeFixed() != null && this.getRuntimeTypeFixed().equals(edtInfo.getRuntimeTypeFixed())))) {
                isEqual = true;
            }
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        int result = 77;
        result = 27 * result + this.uuid.hashCode();
        result = 27 * result + this.runtimeType.hashCode();
        result = 27 * result + this.runtimeTypeFixed.hashCode();
        return result;
    }


}
