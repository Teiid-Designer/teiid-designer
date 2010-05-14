/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

/**
 * A factory that creates {@link ModelBuffer}s for {@link Openable openables}.
 * <p>
 * This interface may be implemented by clients.
 * </p>
 */
public interface ModelBufferFactory {

    /**
     * Creates a buffer for the given owner.
     * The new buffer will be initialized with the contents of the owner 
     * if and only if it was not already initialized by the factory (a buffer is uninitialized if 
     * its content is <code>null</code>).
     * 
     * @param owner the owner of the buffer
     * @throws ModelWorkspaceException if there was an error creating the ModelBuffer
     */
    ModelBuffer createBuffer(Openable owner) throws ModelWorkspaceException;
}
