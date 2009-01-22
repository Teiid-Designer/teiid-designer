/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

/**
 * ModelStatus
 */
public interface ModelStatus extends IStatus {

    /**
     * Returns any Java elements associated with the failure (see specification
     * of the status code), or an empty array if no elements are related to this
     * particular status code.
     *
     * @return the list of Java element culprits
     * @see ModelStatusConstants
     */
    ModelWorkspaceItem[] getModelWorkspaceItems();

    /**
     * Returns the path associated with the failure (see specification
     * of the status code), or <code>null</code> if the failure is not 
     * one of <code>DEVICE_PATH</code>, <code>INVALID_PATH</code>, 
     * <code>PATH_OUTSIDE_PROJECT</code>, or <code>RELATIVE_PATH</code>.
     *
     * @return the path that caused the failure, or <code>null</code> if none
     * @see ModelStatusConstants#DEVICE_PATH
     * @see ModelStatusConstants#INVALID_PATH
     * @see ModelStatusConstants#PATH_OUTSIDE_PROJECT
     * @see ModelStatusConstants#RELATIVE_PATH
     */
    IPath getPath();

    /**
     * Returns whether this status indicates that a model item does not exist.
     * This convenience method is equivalent to
     * <code>getCode() == ModelStatusConstants.ITEM_DOES_NOT_EXIST</code>.
     *
     * @return <code>true</code> if the status code indicates that a Java model
     *   element does not exist
     * @see ModelStatusConstants#ITEM_DOES_NOT_EXIST
     */
    boolean isDoesNotExist();

}
