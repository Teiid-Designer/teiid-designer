/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;


/** 
 * @since 4.2
 */
public class ConnectorBindingsTreeProvider implements DqpUiConstants,
                                                      ITreeContentProvider,
                                                      ILabelProvider {

//    private ConnectorBindingsManager bindingsManager;
    private ConfigurationManager configManager;
    
    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 4.2
     */
    public Object[] getChildren(Object parentElement) {
        if ( parentElement instanceof ComponentType ) {
            Object id = ((ComponentType) parentElement).getID();
            return this.configManager.getBindingsForType(id).toArray();
        } else if ( parentElement instanceof ComponentTypeID ) {
            return this.configManager.getBindingsForType(parentElement).toArray();
        }
        return new Object[0];
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
        if ( element instanceof String ) {
            return (String) element;
        }
        return UTIL.getString(I18nUtil.getPropertyPrefix(ConnectorBindingsTreeProvider.class),
                              new Object[] {element.toString(), element.getClass().getName()});
    }
    
    /**
     * Indicates if at least one binding is loaded in the configuration. 
     * @return <code>true</code> if configuration contains at least one binding; <code>false</code>.
     * @since 4.3
     */
    public boolean containsBindings() {
        boolean result = false;
        
        if (this.configManager != null) {
            Object[] types = getElements(this.configManager);
            
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
        if ( inputElement instanceof ConfigurationManager) {
            configManager = (ConfigurationManager) inputElement;
            return configManager.getConnectorTypeIds().toArray();
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

}
