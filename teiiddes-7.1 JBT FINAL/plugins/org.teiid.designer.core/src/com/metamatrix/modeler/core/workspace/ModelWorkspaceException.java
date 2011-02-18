/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.core.workspace.ModelStatusImpl;

/**
 * A checked exception representing a failure in the ModelWorkspace.
 * Model workspace exceptions contain a modeling-specific status object describing the
 * cause of the exception.
 * <p>
 * This class is not intended to be subclassed by clients. Instances of this
 * class are automatically created by the ModelWorkspace when problems arise, so
 * there is generally no need for clients to create instances.
 * </p>
 *
 * @see ModelStatus
 * @see ModelStatusConstants
 */
public class ModelWorkspaceException extends ModelerCoreException {

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public ModelWorkspaceException() {
        super();
    }
    
    /**
     * Creates a modeling exception that wrappers the given <code>Throwable</code>.
     * The exception contains a modeling-specific status object with severity
     * <code>IStatus.ERROR</code> and the given status code.
     *
     * @param exception the <code>Throwable</code>
     * @param code one of the modeling-specific status codes declared in
     *   <code>ModelStatusConstants</code>
     * @see ModelStatusConstants
     * @see org.eclipse.core.runtime.IStatus#ERROR
     */
    public ModelWorkspaceException(Throwable e, int code) {
        this(new com.metamatrix.modeler.internal.core.workspace.ModelStatusImpl(code, e)); 
    }
    /**
     * Creates a modeling exception for the given <code>CoreException</code>.
     * Equivalent to 
     * <code>ModelWorkspaceException(exception,ModelStatusConstants.CORE_EXCEPTION</code>.
     *
     * @param exception the <code>CoreException</code>
     */
    public ModelWorkspaceException(final CoreException exception) {
        super(exception);
    }

    /**
     * Creates a modeling exception for the given modeling-specific status object.
     *
     * @param status the modeling-specific status object
     */
    public ModelWorkspaceException(final ModelStatus status) {
        super(status);
    }

    /**
     * Constructor for ModelerCoreException.
     * @param message
     */
    public ModelWorkspaceException(String message) {
        this(new ModelStatusImpl(0, message));
    }

    /**
     * Constructor for ModelerCoreException.
     * @param e
     */
    public ModelWorkspaceException(Throwable e) {
        this(new ModelStatusImpl(0, e));
    }

    /**
     * Constructor for ModelerCoreException.
     * @param e
     * @param message
     */
    public ModelWorkspaceException(Throwable e, String message) {
        this(new ModelStatusImpl(0, e, message));
    }

    /**
     * Returns the modeling status object for this exception.
     * Equivalent to <code>(ModelStatus) getStatus()</code>.
     *
     * @return a status object
     */
    public ModelStatus getModelStatus() {
        IStatus status = this.getStatus();
        if (status instanceof ModelStatus) {
            return (ModelStatus)status;
        }
        // A regular IStatus is created only in the case of a CoreException.
        // See bug 13492 Should handle JavaModelExceptions that contains CoreException more gracefully
        final Throwable nestedException = this.getException();  
        if ( nestedException instanceof CoreException ) {
            return new ModelStatusImpl((CoreException)nestedException);
        }
        // should never really get here, as the only nested exceptions of a 
        // MetaMatrixCoreException must be CoreException
        return null;
    }
    /**
     * Returns whether this exception indicates that a modeling element does not
     * exist. Such exceptions have a status with a code of
     * <code>ModelStatusConstants.ELEMENT_DOES_NOT_EXIST</code>.
     * This is a convenience method.
     *
     * @return <code>true</code> if this exception indicates that a modeling
     *   element does not exist
     * @see ModelStatus#isDoesNotExist
     * @see ModelStatusConstants#ELEMENT_DOES_NOT_EXIST
     */
    public boolean isDoesNotExist() {
        ModelStatus modelStatus = getModelStatus();
        return modelStatus != null && modelStatus.isDoesNotExist();
    }

    /**
     * Subclasses may override this method, which is used by {@link #toString()} to obtain the "type"
     * for the exception class.
     * @return the type; defaults to "Model Workspace Exception"
     */
    @Override
    protected String getToStringType() {
        return "Model Workspace Exception"; //$NON-NLS-1$
    }
}
