/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.relationship.NavigationContext;
import org.teiid.designer.relationship.NavigationContextInfo;
import org.teiid.designer.relationship.NavigationLink;
import org.teiid.designer.relationship.NavigationNode;


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
     */
    public FakeNavigationContextImpl( final NavigationNode focusNode,
                                      final NavigationContextInfo info ) {
        CoreArgCheck.isNotNull(focusNode);
        CoreArgCheck.isNotNull(info);
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
     * @see org.teiid.designer.relationship.NavigationContext#getNodes(org.teiid.designer.NavigationContext)
     */
    public List getNodes( NavigationLink link ) {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getNodes(org.teiid.designer.NavigationContext)
     */
    public List getAllNodes() {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getFocusNode()
     */
    public NavigationNode getFocusNode() {
        return this.focusNode;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getNonFocusNodes()
     */
    public List getNonFocusNodes() {
        return this.readOnlyNonFocusNodes;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getNavigationLinks()
     */
    public List getNavigationLinks() {
        return this.readOnlyLinks;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getNavigationLink(org.eclipse.emf.ecore.EObject)
     */
    public NavigationLink getNavigationLink( EObject nonFocusNode ) {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getLabel(org.teiid.designer.relationship.NavigationLink)
     */
    public String getLabel( NavigationLink link ) {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getLabelForEnd(org.teiid.designer.relationship.NavigationLink,
     *      org.eclipse.emf.ecore.EObject)
     */
    public String getLabelForEnd( NavigationLink link,
                                  EObject nodeForEnd ) {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getTooltip(org.teiid.designer.relationship.NavigationLink)
     */
    public String getTooltip( NavigationLink link ) {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getTooltip(org.eclipse.emf.ecore.EObject)
     */
    public String getTooltip( EObject node ) {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#addNodeAndLink(org.teiid.designer.relationship.NavigationNode,
     *      org.teiid.designer.relationship.NavigationLink)
     */
    public void addNodeAndLink( NavigationNode node,
                                NavigationLink link ) {
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getNavigationLink(org.teiid.designer.relationship.NavigationNode)
     */
    public NavigationLink getNavigationLink( NavigationNode nonFocusNode ) {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getLabelForEnd(org.teiid.designer.relationship.NavigationLink,
     *      org.teiid.designer.relationship.NavigationNode)
     */
    public String getLabelForEnd( NavigationLink link,
                                  NavigationNode nodeForEnd ) {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getNonFocusNodeRole(org.teiid.designer.relationship.NavigationLink)
     */
    public String getNonFocusNodeRole( NavigationLink link ) {
        return null;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContext#getTooltip(org.teiid.designer.relationship.NavigationNode)
     */
    public String getTooltip( NavigationNode node ) {
        return null;
    }

}
