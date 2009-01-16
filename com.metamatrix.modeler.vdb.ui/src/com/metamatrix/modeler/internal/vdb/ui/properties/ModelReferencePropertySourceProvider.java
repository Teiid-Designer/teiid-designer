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

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;

import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;


/** 
 * @since 4.2
 */
public class ModelReferencePropertySourceProvider implements
                                                 IPropertySourceProvider {

    private VdbEditingContext context;
    private PropertySheetPage propertySheetPage;
    
    /** 
     * @since 4.2
     */
    public ModelReferencePropertySourceProvider(VdbEditingContext context) {
        this.context = context;
    }

    /** 
     * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
     * @since 4.2
     */
    public IPropertySource getPropertySource(Object object) {
        if ( object instanceof ModelReference ) {
            return new ModelReferencePropertySource((ModelReference) object, context);
        }
        return null;
    }

    /**
     * Obtain a PropertySheetPage for displaying Model Objects.
     * @since 4.2
     */
    public PropertySheetPage getPropertySheetPage() {
        if (propertySheetPage == null) {
            propertySheetPage = new PropertySheetPage();
            propertySheetPage.setPropertySourceProvider(this);
        }

        return propertySheetPage;
    }

    
}
