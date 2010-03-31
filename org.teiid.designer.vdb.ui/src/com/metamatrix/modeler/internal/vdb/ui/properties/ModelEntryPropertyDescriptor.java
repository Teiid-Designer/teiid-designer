/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/** 
 * @since 4.2
 */
public class ModelEntryPropertyDescriptor extends TextPropertyDescriptor {

    private String category;
    
    /** 
     * @param id
     * @param displayName
     * @since 4.2
     */
    public ModelEntryPropertyDescriptor(Object id,
                                            String displayName) {
        super(id, displayName);
    }

    /** 
     * @param id
     * @param displayName
     * @since 4.2
     */
    public ModelEntryPropertyDescriptor(Object id,
                                            String displayName, String category) {
        super(id, displayName);
        this.category = category;
    }

    /** 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    public CellEditor createPropertyEditor(Composite parent) {
        return null;
    }

    /** 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getCategory()
     * @since 4.2
     */
    @Override
    public String getCategory() {
        return category;
    }
}
