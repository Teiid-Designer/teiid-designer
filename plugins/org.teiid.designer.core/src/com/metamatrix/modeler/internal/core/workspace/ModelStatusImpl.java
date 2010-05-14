/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelStatus;
import com.metamatrix.modeler.core.workspace.ModelStatusConstants;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;

/**
 * ModelStatus
 */
public class ModelStatusImpl extends Status implements ModelStatus, ModelStatusConstants, IResourceStatus {

    private static final String DEFAULT_STATUS_NAME = "ModelStatus"; //$NON-NLS-1$

    /**
     * The elements related to the failure, or <code>null</code> if no elements are involved.
     */
    protected ModelWorkspaceItem[] fElements = new ModelWorkspaceItem[0];
    /**
     * The path related to the failure, or <code>null</code> if no path is involved.
     */
    protected IPath fPath;
    /**
     * The <code>String</code> related to the failure, or <code>null</code> if no <code>String</code> is involved.
     */
    protected String fString;
    /**
     * Empty children
     */
    protected final static IStatus[] fgEmptyChildren = new IStatus[] {};
    protected IStatus[] fChildren = fgEmptyChildren;

    /**
     * Singleton OK object
     */
    public static final ModelStatus VERIFIED_OK = new ModelStatusImpl(OK, OK, ModelerCore.Util.getString("status.OK")); //$NON-NLS-1$

    /**
     * Constructs a model status with no corresponding elements.
     */
    public ModelStatusImpl() {
        // no code for an multi-status
        super(ERROR, ModelerCore.PLUGIN_ID, 0, DEFAULT_STATUS_NAME, null);
    }

    /**
     * Constructs a model status with no corresponding elements.
     */
    public ModelStatusImpl( int code ) {
        super(ERROR, ModelerCore.PLUGIN_ID, code, DEFAULT_STATUS_NAME, null);
        fElements = ModelWorkspaceItemInfo.fgEmptyChildren;
    }

    /**
     * Constructs a model status with the given corresponding elements.
     */
    public ModelStatusImpl( int code,
                            ModelWorkspaceItem[] elements ) {
        super(ERROR, ModelerCore.PLUGIN_ID, code, DEFAULT_STATUS_NAME, null);
        fElements = elements;
        fPath = null;
    }

    /**
     * Constructs a model status with no corresponding elements.
     */
    public ModelStatusImpl( int code,
                            String string ) {
        this(ERROR, code, string);
    }

    /**
     * Constructs a model status with no corresponding elements.
     */
    public ModelStatusImpl( int severity,
                            int code,
                            String string ) {
        super(severity, ModelerCore.PLUGIN_ID, code, DEFAULT_STATUS_NAME, null);
        fElements = ModelWorkspaceItemInfo.fgEmptyChildren;
        fPath = null;
        fString = string;
    }

    /**
     * Constructs a model status with no corresponding elements.
     */
    public ModelStatusImpl( int code,
                            Throwable throwable ) {
        super(ERROR, ModelerCore.PLUGIN_ID, code, DEFAULT_STATUS_NAME, throwable);
        fElements = ModelWorkspaceItemInfo.fgEmptyChildren;
    }

    /**
     * Constructs a model status with no corresponding elements.
     */
    public ModelStatusImpl( int code,
                            Throwable throwable,
                            String msg ) {
        super(ERROR, ModelerCore.PLUGIN_ID, code, DEFAULT_STATUS_NAME, throwable);
        fElements = ModelWorkspaceItemInfo.fgEmptyChildren;
        fString = msg;
    }

    /**
     * Constructs a model status with no corresponding elements.
     */
    public ModelStatusImpl( int code,
                            IPath path ) {
        super(ERROR, ModelerCore.PLUGIN_ID, code, DEFAULT_STATUS_NAME, null);
        fElements = ModelWorkspaceItemInfo.fgEmptyChildren;
        fPath = path;
    }

    /**
     * Constructs a model status with the given corresponding element.
     */
    public ModelStatusImpl( int code,
                            ModelWorkspaceItem element ) {
        this(code, new ModelWorkspaceItem[] {element});
    }

    /**
     * Constructs a model status with the given corresponding element and string
     */
    public ModelStatusImpl( int code,
                            ModelWorkspaceItem element,
                            String string ) {
        this(code, new ModelWorkspaceItem[] {element});
        fString = string;
    }

    /**
     * Constructs a model status with the given corresponding element and path
     */
    public ModelStatusImpl( int code,
                            ModelWorkspaceItem element,
                            IPath path ) {
        this(code, new ModelWorkspaceItem[] {element});
        fPath = path;
    }

    /**
     * Constructs a model status with no corresponding elements.
     */
    public ModelStatusImpl( CoreException coreException ) {
        super(ERROR, ModelerCore.PLUGIN_ID, CORE_EXCEPTION, DEFAULT_STATUS_NAME, coreException);
        fElements = ModelWorkspaceItemInfo.fgEmptyChildren;
    }

    protected int getBits() {
        int severity = 1 << (getCode() % 100 / 33);
        int category = 1 << ((getCode() / 100) + 3);
        return severity | category;
    }

    /**
     * @see IStatus
     */
    @Override
    public IStatus[] getChildren() {
        return fChildren;
    }

    /**
     * @see IJavaModelStatus
     */
    public ModelWorkspaceItem[] getModelWorkspaceItems() {
        return fElements;
    }

    /**
     * Returns the message that is relevant to the code of this status.
     */
    @Override
    public String getMessage() {
        Throwable exception = getException();
        if (exception == null) {
            switch (getCode()) {
                case CORE_EXCEPTION:
                    return ModelerCore.Util.getString("status.coreException"); //$NON-NLS-1$

                case ITEM_DOES_NOT_EXIST:
                    return ModelerCore.Util.getString("element.doesNotExist", ((ModelWorkspaceItemImpl)fElements[0]).toStringWithAncestors()); //$NON-NLS-1$

                case INDEX_OUT_OF_BOUNDS:
                    return ModelerCore.Util.getString("status.indexOutOfBounds"); //$NON-NLS-1$

                case INVALID_CONTENTS:
                    return ModelerCore.Util.getString("status.invalidContents"); //$NON-NLS-1$

                case INVALID_DESTINATION:
                    return ModelerCore.Util.getString("status.invalidDestination", ((ModelWorkspaceItemImpl)fElements[0]).toStringWithAncestors()); //$NON-NLS-1$

                case INVALID_ITEM_TYPES:
                    StringBuffer buff = new StringBuffer(ModelerCore.Util.getString("operation.notSupported")); //$NON-NLS-1$
                    for (int i = 0; i < fElements.length; i++) {
                        if (i > 0) {
                            buff.append(", "); //$NON-NLS-1$
                        }
                        buff.append(((ModelWorkspaceItemImpl)fElements[i]).toStringWithAncestors());
                    }
                    return buff.toString();

                case INVALID_NAME:
                    return ModelerCore.Util.getString("status.invalidName", fString); //$NON-NLS-1$

                case INVALID_PATH:
                    if (fString != null) {
                        return fString;
                    }
                    return ModelerCore.Util.getString("status.invalidPath", getPath() == null ? "null" : getPath().toString()); //$NON-NLS-1$ //$NON-NLS-2$

                case INVALID_PROJECT:
                    return ModelerCore.Util.getString("status.invalidProject", fString); //$NON-NLS-1$

                case INVALID_RESOURCE:
                    return ModelerCore.Util.getString("status.invalidResource", fString); //$NON-NLS-1$

                case INVALID_RESOURCE_TYPE:
                    return ModelerCore.Util.getString("status.invalidResourceType", fString); //$NON-NLS-1$

                case INVALID_SIBLING:
                    if (fString != null) {
                        return ModelerCore.Util.getString("status.invalidSibling", fString); //$NON-NLS-1$
                    }
                    return ModelerCore.Util.getString("status.invalidSibling", ((ModelWorkspaceItemImpl)fElements[0]).toStringWithAncestors()); //$NON-NLS-1$

                case IO_EXCEPTION:
                    return ModelerCore.Util.getString("status.IOException"); //$NON-NLS-1$

                case NAME_COLLISION:
                    if (fString != null) {
                        return fString;
                    }
                    return ModelerCore.Util.getString("status.nameCollision", ""); //$NON-NLS-1$ //$NON-NLS-2$
                case NO_ITEMS_TO_PROCESS:
                    return ModelerCore.Util.getString("operation.needElements"); //$NON-NLS-1$

                case NULL_NAME:
                    return ModelerCore.Util.getString("operation.needName"); //$NON-NLS-1$

                case NULL_PATH:
                    return ModelerCore.Util.getString("operation.needPath"); //$NON-NLS-1$

                case NULL_STRING:
                    return ModelerCore.Util.getString("operation.needString"); //$NON-NLS-1$

                case PATH_OUTSIDE_PROJECT:
                    return ModelerCore.Util.getString("operation.pathOutsideProject", fString, ((ModelWorkspaceItemImpl)fElements[0]).toStringWithAncestors()); //$NON-NLS-1$

                case READ_ONLY:
                    ModelWorkspaceItem element = fElements[0];
                    String name = element.getItemName();
                    return ModelerCore.Util.getString("status.readOnly", name); //$NON-NLS-1$

                case RELATIVE_PATH:
                    return ModelerCore.Util.getString("operation.needAbsolutePath", getPath().toString()); //$NON-NLS-1$

                case TARGET_EXCEPTION:
                    return ModelerCore.Util.getString("status.targetException"); //$NON-NLS-1$

                case UPDATE_CONFLICT:
                    return ModelerCore.Util.getString("status.updateConflict"); //$NON-NLS-1$

                case NO_LOCAL_CONTENTS:
                    return ModelerCore.Util.getString("status.noLocalContents", getPath().toString()); //$NON-NLS-1$

            }
            if (fString != null) {
                return fString;
            }
            return ""; // //$NON-NLS-1$
        }
        String message = exception.getMessage();
        if (message != null) {
            return message;
        }
        return exception.toString();
    }

    /**
     * @see IJavaModelStatus#getPath()
     */
    public IPath getPath() {
        return fPath;
    }

    /**
     * @see IStatus#getSeverity()
     */
    @Override
    public int getSeverity() {
        if (fChildren == fgEmptyChildren) return super.getSeverity();
        int severity = -1;
        for (int i = 0, max = fChildren.length; i < max; i++) {
            int childrenSeverity = fChildren[i].getSeverity();
            if (childrenSeverity > severity) {
                severity = childrenSeverity;
            }
        }
        return severity;
    }

    /**
     * @see IJavaModelStatus#isDoesNotExist()
     */
    public boolean isDoesNotExist() {
        return getCode() == ITEM_DOES_NOT_EXIST;
    }

    /**
     * @see IStatus#isMultiStatus()
     */
    @Override
    public boolean isMultiStatus() {
        return fChildren != fgEmptyChildren;
    }

    /**
     * @see IStatus#isOK()
     */
    @Override
    public boolean isOK() {
        return getCode() == OK;
    }

    /**
     * @see IStatus#matches(int)
     */
    @Override
    public boolean matches( int mask ) {
        if (!isMultiStatus()) {
            return matches(this, mask);
        }
        for (int i = 0, max = fChildren.length; i < max; i++) {
            if (matches((ModelStatusImpl)fChildren[i], mask)) return true;
        }
        return false;
    }

    /**
     * Helper for matches(int).
     */
    protected boolean matches( ModelStatusImpl status,
                               int mask ) {
        int severityMask = mask & 0x7;
        int categoryMask = mask & ~0x7;
        int bits = status.getBits();
        return ((severityMask == 0) || (bits & severityMask) != 0) && ((categoryMask == 0) || (bits & categoryMask) != 0);
    }

    /**
     * Adds the given status to this multi-status.
     * 
     * @param status the new child status
     */
    public void add( IStatus status ) {
        CoreArgCheck.isNotNull(status);
        IStatus[] result = new IStatus[fChildren.length + 1];
        System.arraycopy(fChildren, 0, result, 0, fChildren.length);
        result[result.length - 1] = status;
        fChildren = result;
        int newSev = status.getSeverity();
        if (newSev > getSeverity()) {
            setSeverity(newSev);
        }
    }

    /**
     * Adds all of the children of the given status to this multi-status. Does nothing if the given status has no children (which
     * includes the case where it is not a multi-status).
     * 
     * @param status the status whose children are to be added to this one
     */
    public void addAll( IStatus status ) {
        CoreArgCheck.isNotNull(status);
        IStatus[] statuses = status.getChildren();
        for (int i = 0; i < statuses.length; i++) {
            add(statuses[i]);
        }
    }

    /**
     * Creates and returns a new <code>IJavaModelStatus</code> that is a a multi-status status.
     * 
     * @see IStatus#isMultiStatus()
     */
    public static ModelStatus newMultiStatus( ModelStatus[] children ) {
        ModelStatusImpl result = new ModelStatusImpl();
        result.fChildren = children;
        return result;
    }

    /**
     * Returns a printable representation of this exception for debugging purposes.
     */
    @Override
    public String toString() {
        if (this == VERIFIED_OK) {
            return "ModelStatusImpl[OK]"; //$NON-NLS-1$
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("Model Status ["); //$NON-NLS-1$
        buffer.append(getMessage());
        buffer.append("]"); //$NON-NLS-1$
        return buffer.toString();
    }
}
