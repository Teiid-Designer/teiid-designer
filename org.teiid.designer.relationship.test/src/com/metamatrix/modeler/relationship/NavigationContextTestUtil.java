/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.relationship.FakeNavigationResolver;
import com.metamatrix.modeler.internal.relationship.NavigationContextImpl;
import com.metamatrix.modeler.internal.relationship.NavigationLinkImpl;
import com.metamatrix.modeler.internal.relationship.NavigationNodeImpl;


/**
 * NavigationContextUtil
 */
public class NavigationContextTestUtil {
    
    private static final String URI1_VALUE = "/myProject/folder A/model BCD#uuidOfObject"; //$NON-NLS-1$
    private static final String URI2_VALUE = "/myProject/folder B/model BCD#uuidOfObject"; //$NON-NLS-1$
    private static final String LINK_URI_VALUE = "/myProject/folder/links B/model BCD#uuidOfObject"; //$NON-NLS-1$
    private static final String URI_FRAGMENT = "A/model BCD#uuidOfObject"; //$NON-NLS-1$
    private static EObject eObject;
    private static URI uri;
    private static NavigationContextInfo info;
    private static NavigationContext context;
    private static NavigationNode node;
    
    private static NavigationLink link;
    private static FakeNavigationResolver resolver = new FakeNavigationResolver();
        
    private static NavigationNode focusNode;
    private static List links;
    private static List nodes;
    private static List nonFocusNodes;
    

    /**
     * Constructor for NavigationContextUtil.
     * @param name
     */
    public NavigationContextTestUtil() {    
    }

    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================
    
    /*
    * Create simple NavigationContext object
    */
    public NavigationContext createSimpleNavigationContext() throws Exception {
        links = new ArrayList();
        nodes = new ArrayList();
        nonFocusNodes = new ArrayList();
        eObject = resolver.resolve(URI1_VALUE);
        uri = resolver.getUri(eObject);
        node = new NavigationNodeImpl(uri, "Author", //$NON-NLS-1$
                                      eObject.eClass(), URI_FRAGMENT);
        focusNode=node;
        
        info = new NavigationContextInfo(eObject,uri.toString());
        context = new NavigationContextImpl(node,info);
        
        eObject = resolver.resolve(URI2_VALUE);
        uri = resolver.getUri(eObject);
        node = new NavigationNodeImpl(uri, "Book", //$NON-NLS-1$
                                      eObject.eClass(), URI_FRAGMENT);
        
        eObject = resolver.resolve(LINK_URI_VALUE);
        uri = resolver.getUri(eObject);
        
        link = new NavigationLinkImpl(uri,"Author of","Some Type1"); //$NON-NLS-1$  //$NON-NLS-2$
        links.add(link);
        
        context.addNodeAndLink(node,link);
                        
        return context;
    }
    
    /**
     * Create a NavigationContext object from the specified modelObject, using the children
     * of the object to build links.  (Added by Steve Jacobs for modeler testing -- will move soon)
     */
    public NavigationContext createChildNavigationContext(EObject modelObject) throws Exception {
        links = new ArrayList();
        nodes = new ArrayList();
        nonFocusNodes = new ArrayList();
        uri = ModelerCore.getModelEditor().getUri(modelObject);
        String path = ModelerCore.getModelEditor().getFullPathToParent(modelObject).toString();
        String name = ModelerCore.getModelEditor().getName(modelObject);
        node = new NavigationNodeImpl(uri, name, modelObject.eClass(), path); 
        focusNode=node;
        
        info = new NavigationContextInfo(modelObject,uri.toString());
        context = new NavigationContextImpl(node,info);
        
        List children = modelObject.eContents();
        Iterator iter = children.iterator();
        int i = 0;
        while ( iter.hasNext() ) {
            EObject child = (EObject) iter.next();
            uri = ModelerCore.getModelEditor().getUri(child);
            name = ModelerCore.getModelEditor().getName(child);
            path = ModelerCore.getModelEditor().getFullPathToParent(child).toString();
            node = new NavigationNodeImpl(uri, name, child.eClass(), path); 

            uri = ModelerCore.getModelEditor().getUri(child);
            link = new NavigationLinkImpl(uri, "Parent of", "Container"); //$NON-NLS-1$  //$NON-NLS-2$
            links.add(link);
            context.addNodeAndLink(node,link);
            ++i;
        }
        
        EObject parent = modelObject.eContainer();
        if ( parent != null ) {
            uri = ModelerCore.getModelEditor().getUri(parent);
            name = ModelerCore.getModelEditor().getName(parent);
            path = ModelerCore.getModelEditor().getFullPathToParent(parent).toString();
            node = new NavigationNodeImpl(uri, name, parent.eClass(), path); 

            uri = ModelerCore.getModelEditor().getUri(parent);
            link = new NavigationLinkImpl(uri, "Child of", "Container"); //$NON-NLS-1$  //$NON-NLS-2$
            links.add(link);
            context.addNodeAndLink(node,link);
        }
        
        return context;
    }
    
	/**
	 * @return
	 */
	public static NavigationNode getFocusNode()
	{
		return focusNode;
	}

	/**
	 * @return
	 */
	public static List getLinks()
	{
		return links;
	}

	/**
	 * @return
	 */
	public static List getNodes()
	{
		return nodes;
	}

	/**
	 * @return
	 */
	public static List getNonFocusNodes()
	{
		return nonFocusNodes;
	}

	/**
	 * @param node
	 */
	public static void setFocusNode(NavigationNode node)
	{
		focusNode = node;
	}

	/**
	 * @param list
	 */
	public static void setLinks(List list)
	{
		links = list;
	}

	/**
	 * @param list
	 */
	public static void setNodes(List list)
	{
		nodes = list;
	}

	/**
	 * @param list
	 */
	public static void setNonFocusNodes(List list)
	{
		nonFocusNodes = list;
	}

}
