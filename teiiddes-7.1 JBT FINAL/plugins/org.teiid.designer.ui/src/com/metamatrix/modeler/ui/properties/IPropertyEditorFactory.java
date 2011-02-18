/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.properties;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * IPropertyEditorFactory is an interface for creating custom property cell editors.
 * See the extension point <code>propertyEditorFactory</code> for details.
 */
public interface IPropertyEditorFactory {

    /**
     * For determining if this Factory supports the specified EStructuralFeature
     * @param feature
     * @return true if this factory can create a CellEditor for the specified feature.
     */
    public boolean supportsStructuralFeature(EStructuralFeature feature);

    /**
     * Create a CellEditor for modifying the value of the specified IPropertyDescriptor
     * on the specified target object.
     * @param composite
     * @param itemPropertyDescriptor
     * @param propertyDescriptor
     * @param object
     * @return
     */
    public CellEditor createPropertyEditor(
        Composite composite,
        IItemPropertyDescriptor itemPropertyDescriptor,
        IPropertyDescriptor propertyDescriptor,
        Object object);

}
