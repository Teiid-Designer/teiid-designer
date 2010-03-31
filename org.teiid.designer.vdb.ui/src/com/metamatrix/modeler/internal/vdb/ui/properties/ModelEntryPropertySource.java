/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.teiid.designer.vdb.VdbModelEntry;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;

/**
 * @since 4.2
 */
public class ModelEntryPropertySource implements IPropertySource, VdbUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelEntryPropertySource.class);

    private static final String ARCHIVE_CATEGORY = getString("archiveCategory"); //$NON-NLS-1$

    private static final String INFO_CATEGORY = getString("infoCategory"); //$NON-NLS-1$
    private static final IPropertyDescriptor[] descriptorArray = new IPropertyDescriptor[] {
        new ModelEntryPropertyDescriptor(new Integer(0), getString("name"), INFO_CATEGORY), //$NON-NLS-1$
        new ModelEntryPropertyDescriptor(new Integer(1), getString("path"), INFO_CATEGORY), //$NON-NLS-1$
        new ModelEntryPropertyDescriptor(new Integer(2), getString("uri"), INFO_CATEGORY), //$NON-NLS-1$                                                                                       
        new ModelEntryPropertyDescriptor(new Integer(3), getString("isStale"), ARCHIVE_CATEGORY), //$NON-NLS-1$                                                                                       
        new ModelEntryPropertyDescriptor(new Integer(4), getString("modelType"), INFO_CATEGORY), //$NON-NLS-1$                                                                            
        new ModelEntryPropertyDescriptor(new Integer(5), getString("isVisible"), ARCHIVE_CATEGORY), //$NON-NLS-1$                                                                                        
        new ModelEntryPropertyDescriptor(new Integer(6), getString("primaryMetamodel"), INFO_CATEGORY), //$NON-NLS-1$                                                                                        
        new ModelEntryPropertyDescriptor(new Integer(7), getString("version"), INFO_CATEGORY), //$NON-NLS-1$                                                                                        
        new ModelEntryPropertyDescriptor(new Integer(8), getString("added"), ARCHIVE_CATEGORY), //$NON-NLS-1$                                                                                        
    };

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private final VdbModelEntry modelEntry;

    /**
     * @since 4.2
     */
    public ModelEntryPropertySource( final VdbModelEntry modelEntry ) {
        this.modelEntry = modelEntry;
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
    public Object getPropertyValue( final Object id ) {
        try {
            final int index = ((Integer)id).intValue();
            switch (index) {
                case 0:
                    return modelEntry.getName();
                case 1:
                    // flip boolean from isStale to "Is Current"
                    return modelEntry.getSyncState();
                case 2:
                    return modelEntry.getType();
                case 3:
                    return modelEntry.isVisible();
                default:
                    return null;
            }
        } catch (final Exception e) {
            Util.log(e);
            return null;
        }
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     * @since 4.2
     */
    public boolean isPropertySet( final Object id ) {
        return false;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public void resetPropertyValue( final Object id ) {
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void setPropertyValue( final Object id,
                                  final Object value ) {
    }

}
