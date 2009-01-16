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

package com.metamatrix.modeler.internal.xml.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.xsd.XSDComponent;

import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlComment;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlFragmentUse;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.internal.mapping.factory.DefaultMappableTree;
import com.metamatrix.modeler.internal.mapping.factory.FragmentMappingAdapter;
import com.metamatrix.modeler.xml.ModelerXmlPlugin;
import com.metamatrix.modeler.xml.PluginConstants;

/**
 * XmlMappableTree
 */
public class XmlMappableTree extends DefaultMappableTree implements PluginConstants.PreferenceKeys {

    private static EObject stringDatatype;

    private FragmentMappingAdapter fragmentAdapter;

    /**
     * Construct an instance of XmlMappableTree.
     * @param treeRoot
     */
    public XmlMappableTree(EObject treeRoot) {
        super(treeRoot);
        fragmentAdapter = new FragmentMappingAdapter(treeRoot);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#getChildren(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Collection getChildren(EObject node) {
        Collection children = super.getChildren(node);
        ArrayList result = new ArrayList(children.size()); 
        int attributeIndex = 0;
        for ( Iterator iter = children.iterator() ; iter.hasNext() ; ) {
            Object child = iter.next();
            if ( child instanceof XmlFragmentUse ) {
                EObject childToAdd = (EObject) child;
                XmlFragment fragment = (XmlFragment) fragmentAdapter.getFragment(childToAdd);
                if ( fragment != null ) {
                    EObject root = fragment.getRoot();
                    if ( root != null ) {
                        childToAdd = root;
                    }
                }
                result.add(child);
            } else if ( child instanceof XmlNamespace || child instanceof XmlComment || child instanceof ProcessingInstruction ) {
                // skip it
            } else {
                if ( child instanceof XmlAttribute ) {
                    // add attributes to the beginning of the list
                    result.add(attributeIndex++, child);
                } else {
                    result.add(child);
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#getExternalRoots(boolean)
     */
    @Override
    public List getExternalRoots(boolean recurseFragments) {
        return super.getExternalRoots(recurseFragments);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#getParent(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public EObject getParent(EObject node) {
        // even if we are inside a fragment, the 
        return super.getParent(node);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#isExternal(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean isExternal(EObject node) {
        // if this node's XmlFragment is not the same as super.getTreeRoot, then the node is external.
        return ! getTreeRoot().equals(getFragmentRoot(node));
    }
    
    /**
     * Obtain the real XmlFragmentRoot for the specified node
     * @param node
     * @return
     */
    public EObject getFragmentRoot(EObject node) {
        XmlFragment result = null;
        while ( node != null ) {
            if ( node instanceof XmlFragment ) {
                result = (XmlFragment) node;
                break;
            }
            node = node.eContainer();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public EObject getDatatype(EObject node) {

        boolean useXsdType = true;
        Preferences prefs = ModelerXmlPlugin.getDefault().getPluginPreferences();
        if ( prefs.contains(MAPPING_TYPE_FROM_XSD) ) {
            useXsdType = prefs.getBoolean(MAPPING_TYPE_FROM_XSD);
        }

        if ( ! useXsdType ) {            
            return getStringDatatype();
        }

        EObject result = null;

        // get the schema node
        XSDComponent schemaNode = null;
        if ( node instanceof XmlElement ) {
            schemaNode = ((XmlElement) node).getXsdComponent();
        } else if ( node instanceof XmlAttribute ) {
            schemaNode = ((XmlAttribute) node).getXsdComponent();
        }

        if (schemaNode != null ) {
            // get the schema node's type definition
            result = XsdUtil.getSimpleType(schemaNode);
        }
        
        if ( result == null ) {
            // default type is String
            result = getStringDatatype();
        }
        
        return result;
    }
    
    /**
     * Static method to find the String datatype - only need to look it up once.
     * @return
     */
    private static EObject getStringDatatype() {
        if ( stringDatatype == null ) {
            try {
                stringDatatype = ModelerCore.getWorkspaceDatatypeManager().getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING);
            } catch (ModelerCoreException e) {
                PluginConstants.Util.log(e);
            }
        }
        return stringDatatype;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#areEquivalent(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean areEquivalent(EObject objA, EObject objB) {
//        if ( objA instanceof XmlDocumentNode && objB instanceof XmlDocumentNode ) {
//            return ((XmlDocumentNode) objA).getXsdComponent().equals(((XmlDocumentNode) objB).getXsdComponent());
//        }
//        return super.areEquivalent(objA, objB);
        
        // per defect 12301, all mappings will be unique
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#getUniqueName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getUniqueName(EObject node) {
        if ( node instanceof XmlDocumentNode ) {
            String name = ((XmlDocumentNode) node).getName();
            EObject parent = node.eContainer();
            while ( parent != null && parent instanceof XmlContainerNode ) {
                parent = parent.eContainer();
            }
            if ( parent != null && parent instanceof XmlDocumentNode ) {
                return name + '_' + ((XmlDocumentNode) parent).getName();
            }
        }
        return super.getUniqueName(node);
    }

    /** 
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#isVisibleNode(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isVisibleNode(EObject node) {
        // hide all XmlNamespace objects
        return ! ( node instanceof XmlNamespace );
    }
}
