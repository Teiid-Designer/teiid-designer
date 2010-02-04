/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

//import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationContextInfo;
import com.metamatrix.modeler.relationship.NavigationLink;
import com.metamatrix.modeler.relationship.NavigationNode;

/**
 * NavigationContextImpl
 */
public class NavigationContextImpl implements NavigationContext {

    private final NavigationContextInfo info;
    private final NavigationNode focusNode;
    private final List nonFocusNodes;
    private final List readOnlyNonFocusNodes;
    private final List links;
//    private final List readOnlyLinks;
    private Map nodesMap = new HashMap(); //Key = Node, Value = List of Links
    private Map linksMap = new HashMap(); //Key = Link, Value = List of Nodes

    /**
     * Construct an instance of NavigationContextImpl.
     * 
     */
    public NavigationContextImpl( final NavigationNode focusNode, final NavigationContextInfo info  ) {
        Assertion.isNotNull(focusNode);
        Assertion.isNotNull(info);
        this.info = info;
        this.focusNode = focusNode;
        this.nonFocusNodes = new LinkedList();
        this.readOnlyNonFocusNodes = Collections.unmodifiableList(this.nonFocusNodes);
        this.links = new LinkedList();
//        this.readOnlyLinks = Collections.unmodifiableList(this.links);
        
    }
    
    public NavigationContextInfo getInfo() {
        return this.info;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getAllNodes()
     */
    public List getAllNodes() {       
        return nonFocusNodes;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#addNodeAndLink()
     */
    public void addNodeAndLink(NavigationNode node, NavigationLink link) {        
        
        if (!nonFocusNodes.contains(node)) //Check for node
        {
            nonFocusNodes.add(node); //Add node
        }
        
        if(!links.contains(link)) //Check for link
        {
            links.add(link); //Add link
        }
        
        if (linksMap.containsKey(node)) //Check map for node
       {
           Object linkList = linksMap.get(node);        
           ((List)linkList).add(link);            
           nodesMap.put(node, linkList);
       }else{ //Node not there yet
                       
           List linkList = new LinkedList();           
           linkList.add(link);           
           nodesMap.put(node, linkList);
       }
        
        if (linksMap.containsKey(link)) //This is a link to multiple nodes
        {
            Object nodeList = linksMap.get(link);            
            ((List)nodeList).add(node);            
            linksMap.put(link, nodeList);
        }else{ //Just add the link/node List pair            
            List nodeList = new LinkedList();
            nodeList.add(node);
            linksMap.put(link, nodeList);
        }
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
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNavigationLinks(com.metamatrix.modeler.relationship.NavigationNode)
     */
    public List getNavigationLinks(NavigationNode nonFocusNode) {
        return (List)this.nodesMap.get(nonFocusNode);
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNavigationLinks()
     */
    public List getNavigationLinks() {
        return this.links;
    }
    
    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNavigationLinks(com.metamatrix.modeler.relationship.NavigationNode)
     */
    public List getNodes(NavigationLink link) {
        return (List)this.linksMap.get(link);
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getLabel(com.metamatrix.modeler.relationship.NavigationLink)
     */
    public String getLabel(NavigationLink link) {
        return ((NavigationLink)links.get(links.indexOf(link))).getLabel();
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getLabelForEnd(com.metamatrix.modeler.relationship.NavigationLink, org.eclipse.emf.ecore.EObject)
     */
    public String getLabelForEnd(NavigationLink link, NavigationNode nodeForEnd) {
        String role = ""; //$NON-NLS-1$ 
        if (nodeForEnd.equals(getFocusNode()))
        {
            role = ((NavigationLinkImpl)links.get(links.indexOf(link))).getFocusRole();
        }else{
            ((NavigationLinkImpl)links.get(links.indexOf(link))).getNonFocusRole();
        }        
        //TODO Check to make sure link and/or node are valid for this context.
        return role;
    }
    
    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getNonFocusNodeRole(com.metamatrix.modeler.relationship.NavigationLink)
     */
    public String getNonFocusNodeRole(NavigationLink link) {
        //TODO Check to make sure link and/or node are valid for this context.
        return ((NavigationLinkImpl)links.get(links.indexOf(link))).getNonFocusRole();        
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getTooltip(com.metamatrix.modeler.relationship.NavigationLink)
     */
    public String getTooltip(NavigationLink link) {
        return ((NavigationLinkImpl)links.get(links.indexOf(link))).getToolTip();
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getTooltip(com.metamatrix.modeler.relationship.NavigationNode)
     */
    public String getTooltip(NavigationNode node) {
        return ((NavigationNodeImpl)nonFocusNodes.get(nonFocusNodes.indexOf(node))).getToolTip();
    }

}
