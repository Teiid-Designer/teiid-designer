/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship;

/**
 * NavigationContextBuilderImpl creates a NavigationContext for use by the Modeler. It uses Command objects to resolve related
 * objects and links.
 */
public interface NavigationContextBuilder {

    /**
     * Creates a NavigationContext to load in cache for use in Modeler Relationship navigation.
     * 
     * @param Context info object for the current focus node.
     * @return A new NavigationContext object containing the contextual focus node, non-focus nodes and their links.
     */
    public NavigationContext buildNavigationContext( NavigationContextInfo info ) throws NavigationContextBuilderException;

    /**
     * Method to gather all nodes and links for the current context.
     * 
     * @param uri string of the current focus node; cannot be null.
     * @param resolved EObject for the current focus node; cannot be null.
     * @return A new NavigationContext object containing the contextual focus node, non-focus nodes and their links.
     */
    public NavigationContext getAllNodes( String uri );
}
