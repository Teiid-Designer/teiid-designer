/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * StructuralCopyTreeContentProvider
 */
public class StructuralCopyTreeContentProvider implements ITreeContentProvider {
	////////////////////////////////////////////////////////////////////////////////////////
	// STATIC VARIABLES
	////////////////////////////////////////////////////////////////////////////////////////
	public static StructuralCopyTreeContentProvider instance = null;
	
	////////////////////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	////////////////////////////////////////////////////////////////////////////////////////
	public static StructuralCopyTreeContentProvider getInstance() {
		if (instance == null) {
			instance = new StructuralCopyTreeContentProvider();
		}
		return instance;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////
	private StructuralCopyTreeContentProvider() {
		super();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	// INSTANCE METHODS
	////////////////////////////////////////////////////////////////////////////////////////
	public Object[] getChildren(Object node) {
		Object[] result;
		if (node instanceof ModelResource) {
			result =  getElements(node);
		} else {
			EObject parent = (EObject)node;
			EList children = parent.eContents();
			result = children.toArray();
		}
		return result;
	}
	
	public boolean hasChildren(Object node) {
		EObject parent = (EObject)node;
		EList children = parent.eContents();
		return (children.size() > 0);
	}
	
	public Object[] getElements(Object node) {
		ModelResource parent = (ModelResource)node;
		java.util.List elements = null;
		try {
			elements = parent.getEObjects();
		} catch (Exception ex) {
			//bwpTODO-- do what if exception occurs?			
		}
		if (elements == null) {
			elements = new ArrayList(0);
		}
		Object[] array = elements.toArray();
		return array;
	}
	
	public Object getParent(Object node) {
		EObject child = (EObject)node;
		EObject parent = child.eContainer();
		return parent;
	}
	
	public void dispose() {
	}
	
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
