/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationContextInfo;
import com.metamatrix.modeler.relationship.NavigationLink;
import com.metamatrix.modeler.relationship.NavigationNode;

/**
 * NavigationContextImpl
 */
public class FakeNavigationContextImpl implements NavigationContext {

    private final NavigationContextInfo info;
    private final NavigationNode focusNode;
    private final List nonFocusNodes;
    private final List readOnlyNonFocusNodes;
    private final List links;
    private final List readOnlyLinks;

    /**
     * Construct an instance of NavigationContextImpl.
     * 
     */
    public FakeNavigationContextImpl( final NavigationNode focusNode, final NavigationContextInfo info  ) {
        Assertion.isNotNull(focusNode);
        Assertion.isNotNull(info);
        this.info = info;
        this.focusNode = focusNode;
        this.nonFocusNodes = new LinkedList();
        this.readOnlyNonFocusNodes = Collections.unmodifiableList(this.nonFocusNodes);
        this.links = new LinkedList();
        this.readOnlyLinks = Collections.unmodifiableList(this.links);
    }
    
    public NavigationContextInfo getInfo() {
        return this.info;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNodes(com.metamatrix.modeler.NavigationContext)
     */
    public List getNodes(NavigationLink link) {
        return null;
    }


    /**
    * @see com.metamatrix.modeler.relationship.NavigationContext#getNodes(com.metamatrix.modeler.NavigationContext)
    */
    public List getAllNodes() {
       return null;
    }
    
    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getFocusNode()
     */
    public NavigationNode getFocusNode() {
        return this.focusNode;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNonFocusNodes()
     */
    public List getNonFocusNodes() {
        return this.readOnlyNonFocusNodes;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNavigationLinks()
     */
    public List getNavigationLinks() {
        return this.readOnlyLinks;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNavigationLink(org.eclipse.emf.ecore.EObject)
     */
    public NavigationLink getNavigationLink(EObject nonFocusNode) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getLabel(com.metamatrix.modeler.relationship.NavigationLink)
     */
    public String getLabel(NavigationLink link) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getLabelForEnd(com.metamatrix.modeler.relationship.NavigationLink, org.eclipse.emf.ecore.EObject)
     */
    public String getLabelForEnd(NavigationLink link, EObject nodeForEnd) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getTooltip(com.metamatrix.modeler.relationship.NavigationLink)
     */
    public String getTooltip(NavigationLink link) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getTooltip(org.eclipse.emf.ecore.EObject)
     */
    public String getTooltip(EObject node) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#addNodeAndLink(com.metamatrix.modeler.relationship.NavigationNode, com.metamatrix.modeler.relationship.NavigationLink)
     */
    public void addNodeAndLink(NavigationNode node, NavigationLink link) {
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNavigationLink(com.metamatrix.modeler.relationship.NavigationNode)
     */
    public NavigationLink getNavigationLink(NavigationNode nonFocusNode) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getLabelForEnd(com.metamatrix.modeler.relationship.NavigationLink, com.metamatrix.modeler.relationship.NavigationNode)
     */
    public String getLabelForEnd(NavigationLink link, NavigationNode nodeForEnd) {
        return null;
    }


    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNonFocusNodeRole(com.metamatrix.modeler.relationship.NavigationLink)
     */
    public String getNonFocusNodeRole(NavigationLink link) {
        return null;
    }
    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getTooltip(com.metamatrix.modeler.relationship.NavigationNode)
     */
    public String getTooltip(NavigationNode node) {
        return null;
    }

}
