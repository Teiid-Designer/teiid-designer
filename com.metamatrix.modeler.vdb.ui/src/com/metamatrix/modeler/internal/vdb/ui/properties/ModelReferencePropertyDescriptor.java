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

package com.metamatrix.modeler.internal.vdb.ui.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/** 
 * @since 4.2
 */
public class ModelReferencePropertyDescriptor extends TextPropertyDescriptor {

    private String category;
    
    /** 
     * @param id
     * @param displayName
     * @since 4.2
     */
    public ModelReferencePropertyDescriptor(Object id,
                                            String displayName) {
        super(id, displayName);
    }

    /** 
     * @param id
     * @param displayName
     * @since 4.2
     */
    public ModelReferencePropertyDescriptor(Object id,
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
