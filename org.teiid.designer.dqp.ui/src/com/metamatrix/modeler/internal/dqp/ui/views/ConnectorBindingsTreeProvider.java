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
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * @since 4.2
 */
public class ConnectorBindingsTreeProvider implements DqpUiConstants, ITreeContentProvider, ILabelProvider {

    // private ConnectorBindingsManager bindingsManager;
    private ExecutionAdmin admin;

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 4.2
     */
    public Object[] getChildren( Object parentElement ) {
        if (parentElement instanceof ConnectorType) {
            try {
                return this.admin.getConnectors((ConnectorType)parentElement).toArray();
            } catch (Exception e) {
                UTIL.log(e);
                return new Object[0];
            }
        }

        return new Object[0];
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     * @since 4.2
     */
    public Object getParent( Object element ) {
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @since 4.2
     */
    public boolean hasChildren( Object element ) {
        return getChildren(element).length > 0;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.2
     */
    public Image getImage( Object element ) {
        if (element instanceof Connector) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_BINDING_ICON);
        }
        if (element instanceof ConnectorType) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_TYPE_ICON);
        }

        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.2
     */
    public String getText( Object element ) {
        if (element instanceof Connector) {
            return ((Connector)element).getName();
        }
        if (element instanceof ConnectorType) {
            return ((ConnectorType)element).getName();
        }
        if (element instanceof String) {
            return (String)element;
        }
        return UTIL.getString(I18nUtil.getPropertyPrefix(ConnectorBindingsTreeProvider.class), new Object[] {element.toString(),
            element.getClass().getName()});
    }

    /**
     * Indicates if at least one binding is loaded in the configuration.
     * 
     * @return <code>true</code> if configuration contains at least one binding; <code>false</code>.
     * @since 4.3
     */
    public boolean containsBindings() {
        boolean result = false;

        if (this.admin != null) {
            Object[] types = getElements(this.admin);

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
    public Object[] getElements( Object inputElement ) {
        if (inputElement instanceof ExecutionAdmin) {
            this.admin = (ExecutionAdmin)inputElement;
            return this.admin.getConnectorTypeIds().toArray();
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
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     * @since 4.2
     */
    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void addListener( ILabelProviderListener listener ) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    public boolean isLabelProperty( Object element,
                                    String property ) {
        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void removeListener( ILabelProviderListener listener ) {
    }

}
