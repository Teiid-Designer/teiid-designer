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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;


/** 
 * @since 4.2
 */
public class ModelReferencePropertySource implements
                                         IPropertySource, VdbUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelReferencePropertySource.class);
    
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    private static final String ARCHIVE_CATEGORY = getString("archiveCategory"); //$NON-NLS-1$
    private static final String INFO_CATEGORY = getString("infoCategory"); //$NON-NLS-1$
    
    private static final IPropertyDescriptor[] descriptorArray = new IPropertyDescriptor[] {
        new ModelReferencePropertyDescriptor(new Integer(0), getString("name"), INFO_CATEGORY), //$NON-NLS-1$
        new ModelReferencePropertyDescriptor(new Integer(1), getString("path"), INFO_CATEGORY), //$NON-NLS-1$
        new ModelReferencePropertyDescriptor(new Integer(2), getString("uri"), INFO_CATEGORY), //$NON-NLS-1$                                                                                       
        new ModelReferencePropertyDescriptor(new Integer(3), getString("isStale"), ARCHIVE_CATEGORY), //$NON-NLS-1$                                                                                       
        new ModelReferencePropertyDescriptor(new Integer(4), getString("modelType"), INFO_CATEGORY), //$NON-NLS-1$                                                                            
        new ModelReferencePropertyDescriptor(new Integer(5), getString("isVisible"), ARCHIVE_CATEGORY), //$NON-NLS-1$                                                                                        
        new ModelReferencePropertyDescriptor(new Integer(6), getString("primaryMetamodel"), INFO_CATEGORY), //$NON-NLS-1$                                                                                        
        new ModelReferencePropertyDescriptor(new Integer(7), getString("version"), INFO_CATEGORY), //$NON-NLS-1$                                                                                        
        new ModelReferencePropertyDescriptor(new Integer(8), getString("added"), ARCHIVE_CATEGORY), //$NON-NLS-1$                                                                                        
    };
    

    private ModelReference modelReference;
    private VdbEditingContext context;
    
    /** 
     * @since 4.2
     */
    public ModelReferencePropertySource(ModelReference modelReference, VdbEditingContext context) {
        this.modelReference = modelReference;
        this.context = context;
    }

    /** 
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     * @since 4.2
     */
    public Object getEditableValue() {
        return null;
    }

    /** 
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     * @since 4.2
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return descriptorArray;
    }

    /** 
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public Object getPropertyValue(Object id) {
        try {
	        int index = ((Integer) id).intValue();
	        switch (index) {
	            case 0:
	                return modelReference.getName();
	            case 1:
	                return modelReference.getModelLocation();
	            case 2:
	                return modelReference.getUri();
	            case 3:
	                // flip boolean from isStale to "Is Current"
	                return new Boolean(!context.isStale(modelReference)).toString();
	            case 4:
	                return modelReference.getModelType().getDisplayName();
	            case 5:
	                return new Boolean(modelReference.isVisible()).toString();
	            case 6:
	                String metamodel = modelReference.getPrimaryMetamodelUri();
	                if ( metamodel != null ) {
	                    return metamodel.substring(metamodel.lastIndexOf('/') + 1);
	                }
	                return null;
	            case 7:
	                return modelReference.getVersion();
	            case 8:
	                return modelReference.getTimeLastSynchronizedAsDate();
	            default:
	                return null;
	        }
        } catch (Exception e) {
            Util.log(e);
            return null;
        }
    }

    /** 
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     * @since 4.2
     */
    public boolean isPropertySet(Object id) {
        return false;
    }

    /** 
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public void resetPropertyValue(Object id) {
    }

    /** 
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void setPropertyValue(Object id,
                                 Object value) {
    }

}
