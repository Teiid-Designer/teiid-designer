/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;

/**
 * A ModelDiagrams represents the set of diagrams available in the model.
 */
public interface ModelDiagrams extends ModelWorkspaceItem {
    /**
     * Create a new diagram and add it to this resource, specifying whether the new diagram is to 
     * be persisted in this resource or whether it is a transient diagram that will be lost when this
     * resource is closed. 
     * @param target the "target" for the diagram; may not be null
     * @param persistent true if the diagram is to be persisted in this resource, or false otherwise
     * @return the new Diagram object
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    Diagram createNewDiagram( EObject target, boolean persistent ) throws ModelWorkspaceException;

    /**
     * Get the diagram objects associated with the supplied target model object.
     * Diagrams are created using the {@link #createNewDiagram(EObject, boolean)} method.
     * @param target the target object; may be null, meaning find all diagrams that have no target
     * @return the {@link Diagram} instances that are associated with the target object;
     * never null, but possibly empty
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getDiagrams( EObject target ) throws ModelWorkspaceException;

    /**
     * Get all the diagram objects known by this resource.
     * Diagrams are created using the {@link #createNewDiagram(EObject, boolean)} method.
     * @return the {@link Diagram} instances for this resource; never null, but possibly empty
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getDiagrams() throws ModelWorkspaceException;

    /**
     * Determine whether the supplied diagram is considered persistent.
     * @param diagram the diagram; may not be null
     * @return true if the diagram is persisted in this resource, or false otherwise
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    boolean isPersistent( Diagram diagram ) throws ModelWorkspaceException;

    /**
     * Define whether the supplied diagram is considered persistent.  This method has no effect if
     * the diagram's persistence already matches <code>persistent</code>.
     * @param diagram the diagram; may not be null
     * @param persistent true if the diagram is to be persisted in this resource, or false otherwise
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    void setPersistent( Diagram diagram, boolean persistent ) throws ModelWorkspaceException;

    /**
     * Remove the specified diagram from this resource.  This method works for persistent or transient
     * diagrams; persistent diagrams can always just be removed from the 
     * @param diagram the diagram to be deleted; may not be null
     * @return true if the diagram was deleted from this resource, or false if it was not
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    boolean delete( Diagram diagram ) throws ModelWorkspaceException;


}
