/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.factory;

import static org.teiid.designer.xml.PluginConstants.PLUGIN_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDComponent;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.mapping.factory.DefaultMappableTree;
import org.teiid.designer.mapping.factory.FragmentMappingAdapter;
import org.teiid.designer.metamodels.xml.ProcessingInstruction;
import org.teiid.designer.metamodels.xml.XmlAttribute;
import org.teiid.designer.metamodels.xml.XmlComment;
import org.teiid.designer.metamodels.xml.XmlContainerNode;
import org.teiid.designer.metamodels.xml.XmlDocumentNode;
import org.teiid.designer.metamodels.xml.XmlElement;
import org.teiid.designer.metamodels.xml.XmlFragment;
import org.teiid.designer.metamodels.xml.XmlFragmentUse;
import org.teiid.designer.metamodels.xml.XmlNamespace;
import org.teiid.designer.metamodels.xsd.XsdUtil;
import org.teiid.designer.xml.PluginConstants;


/**
 * XmlMappableTree
 *
 * @since 8.0
 */
public class XmlMappableTree extends DefaultMappableTree implements PluginConstants.PreferenceKeys {

    private static EObject stringDatatype;

    private FragmentMappingAdapter fragmentAdapter;

    /**
     * Construct an instance of XmlMappableTree.
     * 
     * @param treeRoot
     */
    public XmlMappableTree( EObject treeRoot ) {
        super(treeRoot);
        fragmentAdapter = new FragmentMappingAdapter(treeRoot);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#getChildren(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Collection getChildren( EObject node ) {
        Collection children = super.getChildren(node);
        ArrayList result = new ArrayList(children.size());
        int attributeIndex = 0;
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            Object child = iter.next();
            if (child instanceof XmlFragmentUse) {
                EObject childToAdd = (EObject)child;
                XmlFragment fragment = (XmlFragment)fragmentAdapter.getFragment(childToAdd);
                if (fragment != null) {
                    EObject root = fragment.getRoot();
                    if (root != null) {
                        childToAdd = root;
                    }
                }
                result.add(child);
            } else if (child instanceof XmlNamespace || child instanceof XmlComment || child instanceof ProcessingInstruction) {
                // skip it
            } else {
                if (child instanceof XmlAttribute) {
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
     * @See org.teiid.designer.mapping.factory.IMappableTree#getExternalRoots(boolean)
     */
    @Override
    public List getExternalRoots( boolean recurseFragments ) {
        return super.getExternalRoots(recurseFragments);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#getParent(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public EObject getParent( EObject node ) {
        // even if we are inside a fragment, the
        return super.getParent(node);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#isExternal(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean isExternal( EObject node ) {
        // if this node's XmlFragment is not the same as super.getTreeRoot, then the node is external.
        return !getTreeRoot().equals(getFragmentRoot(node));
    }

    /**
     * Obtain the real XmlFragmentRoot for the specified node
     * 
     * @param node
     * @return
     */
    public EObject getFragmentRoot( EObject node ) {
        XmlFragment result = null;
        while (node != null) {
            if (node instanceof XmlFragment) {
                result = (XmlFragment)node;
                break;
            }
            node = node.eContainer();
        }
        return result;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public EObject getDatatype( EObject node ) {

        boolean useXsdType = true;
        IEclipsePreferences prefs = new InstanceScope().getNode(PLUGIN_ID);

        if (prefs.get(MAPPING_TYPE_FROM_XSD, null) == null) {
            // then use default value if available
            IEclipsePreferences defaultPrefs = new DefaultScope().getNode(PLUGIN_ID);
            useXsdType = defaultPrefs.getBoolean(MAPPING_TYPE_FROM_XSD, useXsdType);
        } else {
            useXsdType = prefs.getBoolean(MAPPING_TYPE_FROM_XSD, useXsdType);
        }

        if (!useXsdType) {
            return getStringDatatype();
        }

        EObject result = null;

        // get the schema node
        XSDComponent schemaNode = null;
        if (node instanceof XmlElement) {
            schemaNode = ((XmlElement)node).getXsdComponent();
        } else if (node instanceof XmlAttribute) {
            schemaNode = ((XmlAttribute)node).getXsdComponent();
        }

        if (schemaNode != null) {
            // get the schema node's type definition
            result = XsdUtil.getSimpleType(schemaNode);
        }

        if (result == null) {
            // default type is String
            result = getStringDatatype();
        }

        return result;
    }

    /**
     * Static method to find the String datatype - only need to look it up once.
     * 
     * @return
     */
    private static EObject getStringDatatype() {
        if (stringDatatype == null) {
            try {
                stringDatatype = ModelerCore.getWorkspaceDatatypeManager().getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING);
            } catch (ModelerCoreException e) {
                PluginConstants.Util.log(e);
            }
        }
        return stringDatatype;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#areEquivalent(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean areEquivalent( EObject objA,
                                  EObject objB ) {
        // if ( objA instanceof XmlDocumentNode && objB instanceof XmlDocumentNode ) {
        // return ((XmlDocumentNode) objA).getXsdComponent().equals(((XmlDocumentNode) objB).getXsdComponent());
        // }
        // return super.areEquivalent(objA, objB);

        // per defect 12301, all mappings will be unique
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#getUniqueName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getUniqueName( EObject node ) {
        if (node instanceof XmlDocumentNode) {
            String name = ((XmlDocumentNode)node).getName();
            EObject parent = node.eContainer();
            while (parent != null && parent instanceof XmlContainerNode) {
                parent = parent.eContainer();
            }
            if (parent != null && parent instanceof XmlDocumentNode) {
                return name + '_' + ((XmlDocumentNode)parent).getName();
            }
        }
        return super.getUniqueName(node);
    }

    /**
     * @see org.teiid.designer.mapping.factory.IMappableTree#isVisibleNode(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isVisibleNode( EObject node ) { // NO_UCD
        // hide all XmlNamespace objects
        return !(node instanceof XmlNamespace);
    }
}
