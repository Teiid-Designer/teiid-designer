/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship;

import org.eclipse.emf.common.util.URI;

/**
 * A NavigationLink represents the "association" or "relationship" between two {@link EObject model objects}.
 * However, "association" and "relationships" (in the general sense) often may relate a set of objects to a set
 * of objects, and therefore would be represented by multiple links.
 */
public interface NavigationLink extends NavigationObject {
    
    /**
     * Return the label for this link.
     * @return the label for the link; may be null if the link doesn't have a label
     */
    public String getLabel();

	/**
	 * Returns the URI to this link's {@link EObject model object}, if it has one.  A link may correspond to
     * exactly zero or one {@link EObject model object}, and will for generalized relationships
     * typically be reference an instance of {@link com.metamatrix.metamodels.relationship.Relationship}.
     * @return the URI to the model object, or null if this link does not correspond to any real model object.
	 */
	public URI getModelObjectUri();

    /**
     * Return the type for the link.
     * @return the type for the link; may be null if there is no type
     */
    public String getType();
}
