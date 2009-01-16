/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.relationship;

import java.util.List;

/**
 * The NavigationContext represents a single point of view showing a model object and the other model
 * objects to which can be directly navigated using relationships.  The point of view for all information
 * is always from the context of the 'focused object', and this includes labels and other information.
 */
public interface NavigationContext {
    
    /**
     * Obtain the navigation info object for this context.
     * @return the info object for this context; never null
     */
    public NavigationContextInfo getInfo();
    
    /**
     * Add a {@link NavigationLink link} and a {@link NavigationNode nonfocus node} 
     * then there are multiple links that bind the two objects.
     * @param node (a non-focus nodes); may not be null
     * @param link (the link to the non-focus nodes); may not be null
     * @throws IllegalArgumentException if the supplied non-focus node is null or if it is actually the
     * focus node.
     */
    public void addNodeAndLink(NavigationNode node, NavigationLink link);
       
    /**
     * Obtain all of the nodes that are in this context.
     * @return the List of {@link NavigationNode objects} that are in this navigation context;
     * never null, and should always have at least one object (the focus node)
     */
    public List getAllNodes();
    
    /**
     * Obtain the {@link NavigationNode object} that is the focus of this navigation context.
     * @return the focus node; never null
     */
    public NavigationNode getFocusNode();
    
    /**
     * Get the {@link NavigationNode objects} that are directly navigable from the {@link #getFocusNode() focus node}
     * via this context.
     * @return the List of {@link EObject model objects}; never null, but possibly empty
     */
    public List getNonFocusNodes();
    
    /**
     * Get the {@link NavigationLink links} between all of the {@link #getAllNodes() nodes} in this
     * navigation context.  There will be a separate link between the {@link #getFocusNode() focus node}
     * and each {@link #getNonFocusNodes() non-focus node}.  However, if a link is a 
     * {@link MultiNavigationLink}, then there are multiple relationships that bind the two linked objects.
     * @return
     */
    public List getNavigationLinks();
    
    /**
      * Get a list of {@link NavigationNode node}s for a specific {@link NavigationLink link}.
      * @param link for one or more nodes; may not be null
      * @return the {@link List} of {@link NavigationNode node}s for the given {@link NavigationLink link} 
      */   
    public List getNodes(NavigationLink link);
    
    /**
     * Get the label for the supplied link.
     * @param link the NavigationLink; may not be null
     * @return the label, if there is one; may be null or zero-length if there is no label
     */
    public String getLabel( final NavigationLink link );
    
    /**
     * Get the label for one end of the supplied link.
     * @param link the NavigationLink; may not be null
     * @param nodeForEnd the end for which the role is to be found; may not be null
     * @return the label for the end nearest the supplied end; may be null or zero-length if there is no label
     */
    public String getLabelForEnd( final NavigationLink link, final NavigationNode nodeForEnd);
    
    /**
     * Get the role for the non focus node of the supplied link.
     * @param link the NavigationLink; may not be null     
     * @return the role for the non focus node(s) for the supplied link. There
     * may be multiple non focus nodes for this link, but the role will be the same; may be null or zero-length if there is no role name
     */
    public String getNonFocusNodeRole( final NavigationLink link );
    
    /**
     * Get the tooltip for the supplied NavigationLink.
     * @param link the NavigationLink; may not be null
     * @return the tool tip text; may be null or zero-length if there is no tooltip
     */
     public String getTooltip( final NavigationLink link );
    
    /**
     * Get the tooltip for the supplied {@link NavigationNode}.
     * @param node the node; may not be null
     * @return the tool tip text; may be null or zero-length if there is no tooltip
     */
    public String getTooltip( final NavigationNode node );

}
