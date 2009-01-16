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

package com.metamatrix.modeler.internal.dqp.ui.workspace;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.internal.workspace.SourceModelInfo;
import com.metamatrix.modeler.dqp.internal.workspace.WorkspaceConfigurationManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;



/** 
 * Class provides content and label information for ConnectorBindings and ModelInfos in ConnectorsView
 * @since 5.0
 */
public class ConnectorsViewTreeProvider implements ITreeContentProvider, ILabelProvider {

    private WorkspaceConfigurationManager workspaceConfig;
    
    private boolean showModelMappings = false;
    
    private boolean showTypes = false;
    
    /** 
     * 
     * @since 5.0
     */
    public ConnectorsViewTreeProvider() {
        this(true);
    }
    
    /** 
     * 
     * @since 5.0
     */
    public ConnectorsViewTreeProvider(boolean showModelMappings) {
        super();
        this.showModelMappings = showModelMappings;
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 4.2
     */
    public Object[] getChildren(Object parentElement) {

        if( parentElement instanceof ConnectorBinding ) {
            ConnectorBinding binding = (ConnectorBinding)parentElement;
            Collection modelInfos = Collections.EMPTY_LIST; 
            
            if( showModelMappings && workspaceConfig != null ) {
                modelInfos = workspaceConfig.getModelsForBinding(binding.getName());
            }
            
            if(modelInfos.isEmpty()) {
                return new Object[0];
            }
            
            return modelInfos.toArray();
        } else if( parentElement instanceof SourceModelInfo ) {
            return new Object[0];
        } else if ( parentElement instanceof ComponentType ) {
            Object id = ((ComponentType) parentElement).getID();
            return workspaceConfig.getConfigurationManager().getBindingsForType(id).toArray();
        } else if ( parentElement instanceof ComponentTypeID ) {
            return workspaceConfig.getConfigurationManager().getBindingsForType(parentElement).toArray();
        }
        
        return this.workspaceConfig.getConnectorBindings().toArray();
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     * @since 4.2
     */
    public Object getParent(Object element) {
        return null;
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @since 4.2
     */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /** 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.2
     */
    public Image getImage(Object element) {
        if ( element instanceof ConnectorBinding ) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_BINDING_ICON);
        }
        if ( element instanceof ComponentType ) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_TYPE_ICON);
        }
        if ( element instanceof ComponentTypeID ) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_TYPE_ICON);
        }
        
        if( element instanceof SourceModelInfo ) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SOURCE_CONNECTOR_BINDING_ICON);
        }
        return null;
    }

    /** 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.2
     */
    public String getText(Object element) {
        if ( element instanceof ConnectorBinding ) {
            return ((ConnectorBinding) element).getFullName();
        }
        if ( element instanceof ComponentType ) {
            return ((ComponentType) element).getFullName();
        }
        if ( element instanceof ComponentTypeID ) {
            return element.toString();
        }
        if ( element instanceof SourceModelInfo ) {
            SourceModelInfo mInfo = (SourceModelInfo)element;
            return mInfo.getName();
        }
        if ( element instanceof String ) {
            return (String) element;
        }
        return DqpUiConstants.UTIL.getString(I18nUtil.getPropertyPrefix(ConnectorsViewTreeProvider.class),
                              new Object[] {element.toString(), element.getClass().getName()});
    }
    
    /**
     * Indicates if at least one binding is loaded in the configuration. 
     * @return <code>true</code> if configuration contains at least one binding; <code>false</code>.
     * @since 4.3
     */
    public boolean containsBindings() {
        boolean result = false;
        
        if (this.workspaceConfig != null) {
            Object[] types = getElements(this.workspaceConfig);
            
            if ((types != null) && (types.length != 0)) {
                for (int i = 0; i < types.length; ++i) {
                    if (hasChildren(types[i])) {
                        result = true;
                        break;
                    }
                }
            }
        }
        
        return result;
    }
    /** 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    public Object[] getElements(Object inputElement) {
        if ( inputElement instanceof WorkspaceConfigurationManager) {
            workspaceConfig = (WorkspaceConfigurationManager) inputElement;
            if( showTypes ) {
                // Show Types as roots
                return workspaceConfig.getConfigurationManager().getConnectorTypeIds().toArray();
            }
            
            // Only show connector bindings as roots
            return workspaceConfig.getConnectorBindings().toArray();
        }

        return new Object[0];
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     * @since 4.2
     */
    public void dispose() {
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void inputChanged(Viewer viewer,
                             Object oldInput,
                             Object newInput) {
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void addListener(ILabelProviderListener listener) {
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
    
    public void setShowTypes(boolean value) {
        this.showTypes = value;
    }

}
