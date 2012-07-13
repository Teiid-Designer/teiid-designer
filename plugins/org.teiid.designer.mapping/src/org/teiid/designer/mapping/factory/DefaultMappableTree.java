/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.factory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect;


/**
 * DefaultMappableTree
 */
public class DefaultMappableTree implements IMappableTree {

    private EObject treeRoot;
    private AdapterFactoryItemDelegator emfContentProvider;

    /**
     * Construct an instance of DefaultMappableTree.
     */
    public DefaultMappableTree(EObject treeRoot) {
        this.treeRoot = treeRoot;
    }
    
    public AdapterFactoryItemDelegator getModelContentProvider() {
        if ( emfContentProvider == null ) {
            AdapterFactory factory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
            emfContentProvider = new AdapterFactoryItemDelegator(factory);
        }
        return emfContentProvider;
    }


    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.MappableTree#getTreeRoot()
     */
    @Override
	public EObject getTreeRoot() {
        return treeRoot;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.MappableTree#getParent(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getParent(EObject node) {
        Object parent = getModelContentProvider().getParent(node);
        if ( parent instanceof EObject ) {
            return (EObject) parent; 
        }
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.MappableTree#getChildren(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getChildren(EObject node) {
        return getModelContentProvider().getChildren(node);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#isAncestorOf(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isAncestorOf(EObject ancestor, EObject descendent) {
        EObject parent = getParent(descendent);
        while ( parent != null ) {
            if ( parent.equals(ancestor) ) {
                return true;
            }
            parent = getParent(parent);
        }
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#isParentOf(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isParentOf(EObject parent, EObject child) {
        return getParent(child).equals(parent);
    }


    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#getExternalRoots(boolean)
     */
    @Override
	public List getExternalRoots(boolean recurseFragments) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#isExternal(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isExternal(EObject node) {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getDatatype(EObject node) {
        EObject result = null;
        MetamodelAspect aspect = AspectManager.getSqlAspect(node);
        if ( aspect instanceof SqlColumnAspect ) {
            result = ((SqlColumnAspect) aspect).getDatatype(node);
        } else if ( aspect instanceof SqlProcedureParameterAspect ) {
            result = ((SqlProcedureParameterAspect) aspect).getDatatype(node);
        }

        return result;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#isChoiceNode(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isChoiceNode(EObject node) {
        IChoiceFactory factory = ChoiceFactoryManager.getChoiceFactory(node);
        if ( factory != null && factory.supports(node) ) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#isSiblingOf(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
	public boolean isSiblingOf(EObject instance, EObject possibleSibling, boolean higherOrderOnly) {
        boolean result = false;
        result = instance.eContainer().equals(possibleSibling.eContainer());
        if ( result && higherOrderOnly ) {
            List siblingList = instance.eContainer().eContents();
            int a = siblingList.indexOf(instance);
            int b = siblingList.indexOf(possibleSibling);
            result = b < a;
        }
        return result;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#areEquivalent(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean areEquivalent(EObject objA, EObject objB) {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IMappableTree#getUniqueName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getUniqueName(EObject node) {
        ModelEditor editor = ModelerCore.getModelEditor();
        String name = editor.getName(node);
        return name + '_' + editor.getName(node.eContainer());
    }

}
