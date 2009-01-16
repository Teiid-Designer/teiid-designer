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

package com.metamatrix.modeler.internal.mapping.factory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;

import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import com.metamatrix.modeler.mapping.factory.IChoiceFactory;
import com.metamatrix.modeler.mapping.factory.IMappableTree;

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
     * @see com.metamatrix.modeler.mapping.factory.MappableTree#getTreeRoot()
     */
    public EObject getTreeRoot() {
        return treeRoot;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.MappableTree#getParent(org.eclipse.emf.ecore.EObject)
     */
    public EObject getParent(EObject node) {
        Object parent = getModelContentProvider().getParent(node);
        if ( parent instanceof EObject ) {
            return (EObject) parent; 
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.MappableTree#getChildren(org.eclipse.emf.ecore.EObject)
     */
    public Collection getChildren(EObject node) {
        return getModelContentProvider().getChildren(node);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#isAncestorOf(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#isParentOf(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public boolean isParentOf(EObject parent, EObject child) {
        return getParent(child).equals(parent);
    }


    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#getExternalRoots(boolean)
     */
    public List getExternalRoots(boolean recurseFragments) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#isExternal(org.eclipse.emf.ecore.EObject)
     */
    public boolean isExternal(EObject node) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#getDatatype(org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#isChoiceNode(org.eclipse.emf.ecore.EObject)
     */
    public boolean isChoiceNode(EObject node) {
        IChoiceFactory factory = ChoiceFactoryManager.getChoiceFactory(node);
        if ( factory != null && factory.supports(node) ) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#isSiblingOf(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject, boolean)
     */
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
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#areEquivalent(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public boolean areEquivalent(EObject objA, EObject objB) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IMappableTree#getUniqueName(org.eclipse.emf.ecore.EObject)
     */
    public String getUniqueName(EObject node) {
        ModelEditor editor = ModelerCore.getModelEditor();
        String name = editor.getName(node);
        return name + '_' + editor.getName(node.eContainer());
    }

}
