/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.association;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;

/**
 * AssociationDescriptor
 */
public interface AssociationDescriptor {
    
    /**
     * Return true if the model information available through the
     * descriptor is sufficient to allow an association instance
     * to be created from this descriptor.
     * @return
     */
    boolean isComplete();
    
    /**
     * Return true the descriptor has already been processed and the object 
     * has been created and added to the Model.
     * @return
     */
    boolean creationComplete();
    
    /**
     * sets the descriptor value for creationComplete.
     * @return
     */
    void setCreationComplete(boolean complete);
    
    /**
     * Return a identifier unique to the specific type of association
     * descriptor.
     * @return
     */
    String getType();
    
    /**
     * Returns the image for the label of this descriptor.  The image
     * is owned by the underlying label provider and must not be disposed directly.
     * @return the image used to label the descriptor, or <code>null</code>
     *   if there is no image for this object
     */
    Object getImage();
    
    /**
     * Returns the text for the label of this descriptor.
     * @return the text string used to label the descriptor, or <code>null</code>
     *   if there is no text label for this object
     */
    String getText();
    
    /**
     * Return the {@link org.eclipse.core.runtime.IStatus} for the current status of
     * the descriptor.  If the descriptor is incomplete the status will indicate the 
     * error. If the descriptor is ambiguous the status will indicate a warning describing
     * the default behavior is the descriptor is used to create an instance without the 
     * ambiguity being resolved.
     * @return 
     */
    IStatus getStatus();
    
    /**
     * Returns whether this descriptor is considered ambiguous.  Ambiguities are
     * a result of over-determined set of objects defined for the descriptor such
     * that many variants of this association can be created.
     * @return <code>true</code> if ambiguous, <code>false</code> otherwise
     * @see #getChildren
     */
    boolean isAmbiguous();
    
    /**
     * Returns a list of possible sub-descriptors representing the many variants of
     * this association that can be created, or an empty list if this descriptor is 
     * not considered ambiguous.
     * @return an array of assocation descriptor objects
     * @see #isAmbiguous
     */
    AssociationDescriptor[] getChildren();
    
    /**
     * Returns the newly completed EObject
     * @return an a completely created association EObject
     */
    EObject getNewAssociation();

}
