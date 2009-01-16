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

package com.metamatrix.modeler.internal.dqp.ui.config;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;
import com.metamatrix.vdb.internal.runtime.model.BasicVDBModelDefn;


/** 
 * @since 4.2
 */
public class VdbDefinitionLabelProvider implements DqpUiConstants,
                                                   ITableLabelProvider {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbDefinitionLabelProvider.class);
    
    //===========================================
    // Static Utility Methods

    /**<p>
     * </p>
     * @since 4.2
     */
    private static String getString(final String id) {
        return UTIL.getString(I18N_PREFIX + id);
    }

    
    
    private static final Image MODEL_ICON = DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SOURCE_MODEL_ICON);
    private static final Image CONNECTOR_ICON = DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_ICON);

    
    private InternalVdbEditingContext vdbContext;
    private VdbContextEditor vdbContextEditor;
    
    public VdbDefinitionLabelProvider(InternalVdbEditingContext theContext) {
        this.vdbContext = theContext;
        this.vdbContextEditor = null;
    }
    public VdbDefinitionLabelProvider(VdbContextEditor theContext) {
        this.vdbContext = null;
        this.vdbContextEditor = theContext;
    }

    /** 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     * @since 4.2
     */
    public Image getColumnImage(Object element,
                                int columnIndex) {
        return (columnIndex == 0 ? MODEL_ICON : CONNECTOR_ICON);
    }

    /** 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     * @since 4.2
     */
    public String getColumnText(Object element,
                                int columnIndex) {
        switch ( columnIndex ) {
            case 0:
                if ( element instanceof BasicVDBModelDefn ) {
                    String result = ((BasicVDBModelDefn) element).getName();
                    return result;
                }
                return element.toString();
            case 1:
                if ( element instanceof BasicVDBModelDefn ) {
                    ConnectorBinding binding = null;
                    if (this.vdbContext != null) {
                        binding = DqpPlugin.getInstance().getVdbDefnHelper(this.vdbContext).getFirstConnectorBinding((BasicVDBModelDefn) element);
                    } else if (this.vdbContextEditor != null) {
                        binding = DqpPlugin.getInstance().getVdbDefnHelper(this.vdbContextEditor).getFirstConnectorBinding((BasicVDBModelDefn) element);
                    }
                    return getText(binding);
                }
                return element.toString();
        }

        return ""; //$NON-NLS-1$
    }

    public String getText(ConnectorBinding binding) { 
        if ( binding == null ) {
            if (this.vdbContext != null && this.vdbContext.isReadOnly()) {
                return getString("none.ReadOnly"); //$NON-NLS-1$
            } else if (this.vdbContextEditor != null && this.vdbContextEditor.isReadOnly()) {
                return getString("none.ReadOnly"); //$NON-NLS-1$
            }
            return getString("none.Modifiable"); //$NON-NLS-1$                            
        }
        return binding.getName() + " - " + binding.getComponentTypeID().getName(); //$NON-NLS-1$
    }
    
    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void addListener(ILabelProviderListener listener) {
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     * @since 4.2
     */
    public void dispose() {
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    public boolean isLabelProperty(Object element,
                                   String property) {
        return false;
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void removeListener(ILabelProviderListener listener) {
    }

}
